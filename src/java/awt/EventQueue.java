/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.event.PaintEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.ActiveEvent;
import java.util.EmptyStackException;
import java.lang.reflect.InvocationTargetException;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.DebugHelper;

/**
 * EventQueue is a platform-independent class that queues events, both
 * from the underlying peer classes and from trusted application classes.
 * <p>
 * Some browsers partition applets in different code bases into separate
 * contexts, and establish walls between these contexts. In such a scenario,
 * there will be one EventQueue per context. Other browsers place all applets
 * into the same context, implying that there will be only a single, global
 * EventQueue for all applets. This behavior is implementation-dependent.
 * Consult your browser's documentation for more information.
 *
 * @author Thomas Ball
 * @author Fred Ecks
 * @author David Mendenhall
 *
 * @version 	1.72, 02/06/02
 * @since 	1.1
 */
public class EventQueue {
    private static final DebugHelper dbg = DebugHelper.create(EventQueue.class);

    // From Thread.java
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    private static final int LOW_PRIORITY = 0;
    private static final int NORM_PRIORITY = 1;
    private static final int HIGH_PRIORITY = 2;

    private static final int NUM_PRIORITIES = HIGH_PRIORITY + 1;

    /*
     * We maintain one Queue for each priority that the EventQueue supports.
     * That is, the EventQueue object is actually implemented as
     * NUM_PRIORITIES queues and all Events on a particular internal Queue
     * have identical priority. Events are pulled off the EventQueue starting
     * with the Queue of highest priority. We progress in decreasing order
     * across all Queues.
     */
    private Queue[] queues = new Queue[NUM_PRIORITIES];
    
    /*
     * The next EventQueue on the stack, or null if this EventQueue is
     * on the top of the stack.  If nextQueue is non-null, requests to post
     * an event are forwarded to nextQueue.
     */
    private EventQueue nextQueue;

    /*
     * The previous EventQueue on the stack, or null if this is the
     * "base" EventQueue.
     */
    private EventQueue previousQueue;

    private EventDispatchThread dispatchThread;

    /*
     * Debugging flag -- set true and recompile to enable checking.
     */
    private final static boolean debug = false;

    public EventQueue() {
        for (int i = 0; i < NUM_PRIORITIES; i++) {
	    queues[i] = new Queue();
	}
        String name = "AWT-EventQueue-" + nextThreadNum();
        dispatchThread = new EventDispatchThread(name, this);
        dispatchThread.setPriority(Thread.NORM_PRIORITY + 1);
        dispatchThread.start();
    }

    /**
     * Post a 1.1-style event to the EventQueue.  If there is an
     * existing event on the queue with the same ID and event source,
     * the source Component's coalesceEvents method will be called.
     *
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     */
    public void postEvent(AWTEvent theEvent) {
	Toolkit toolkit = Toolkit.getDefaultToolkit();
        if (toolkit instanceof SunToolkit) {
	    ((SunToolkit)toolkit).flushPendingEvents();
	}
        postEventPrivate(theEvent);
    }

    /**
     * Post a 1.1-style event to the EventQueue.  If there is an
     * existing event on the queue with the same ID and event source,
     * the source Component's coalesceEvents method will be called.
     *
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     */
    final void postEventPrivate(AWTEvent theEvent) {
        synchronized(this) {
            int id = theEvent.getID();
            if (nextQueue != null) {
                // Forward event to top of EventQueue stack.
                nextQueue.postEventPrivate(theEvent);
            } else if (theEvent instanceof PeerEvent &&
                       (((PeerEvent)theEvent).getFlags() & 
                                       PeerEvent.PRIORITY_EVENT) != 0) {
                postEvent(theEvent, HIGH_PRIORITY);
            } else if (id == PaintEvent.PAINT ||
                       id == PaintEvent.UPDATE) {
                postEvent(theEvent, LOW_PRIORITY);
            } else {
                postEvent(theEvent, NORM_PRIORITY);
            }
        }
    }

    /**
     * Posts the event to the internal Queue of specified priority,
     * coalescing as appropriate.
     */
    private void postEvent(AWTEvent theEvent, int priority) {
        EventQueueItem newItem = new EventQueueItem(theEvent);
	if (queues[priority].head == null) {
	    boolean shouldNotify = noEvents();

	    queues[priority].head = queues[priority].tail = newItem;

            // This component doesn't have any events of this type on the 
            // queue, so we have to initialize the RepaintArea with theEvent
	    if (theEvent.getID() == PaintEvent.PAINT ||
                theEvent.getID() == PaintEvent.UPDATE) {
                Object source = theEvent.getSource();
                ((Component)source).coalesceEvents(theEvent, theEvent);
	    }

	    if (shouldNotify) {
	        notifyAll();
	    }
	} else {
	    Object source = theEvent.getSource();
	    boolean isPeerEvent = theEvent instanceof PeerEvent;

	    // For Component source events, traverse the entire list,
	    // trying to coalesce events
	    if (source instanceof Component) {
	        EventQueueItem q = queues[priority].head;

		// fix bug 4301264, do not coalesce mouse move/drag events
		// across other types of mouse events.
		if (theEvent.id == Event.MOUSE_MOVE ||
		    theEvent.id == Event.MOUSE_DRAG) {
		    EventQueueItem qm;
		    for(qm = q; qm != null; qm = qm.next) {
			if ((qm.event instanceof MouseEvent) &&
			    qm.id != theEvent.id) {
				q = qm;
			}
		    }
		}

		for (;;) {
		    if (q.id == newItem.id && q.event.getSource() == source) {
		        AWTEvent coalescedEvent;
			coalescedEvent = ((Component)source).coalesceEvents(q.event, theEvent);
			if (isPeerEvent) {
			    if( coalescedEvent == null && q.event instanceof PeerEvent) {
				coalescedEvent = ((PeerEvent)q.event).coalesceEvents((PeerEvent)theEvent);
			    }
			}
			if (coalescedEvent != null) {
			    // Remove debugging statement because
			    // calling AWTEvent.toString here causes a
			    // deadlock.
			    //
			    // if (dbg.on) {
			    //     dbg.println("EventQueue coalesced event: " +
			    //                 coalescedEvent);
			    // }
			    q.event = coalescedEvent;
			    return;
			}
		    }
		    if (q.next != null) {
		        q = q.next;
		    } else {
		        break;
		    }
		}
	    }

            // The event was not coalesced or has non-Component source.
            // Insert it at the end of the appropriate Queue.
	    if (theEvent.getID() == PaintEvent.PAINT ||
                theEvent.getID() == PaintEvent.UPDATE) {
		// This component doesn't have any events of this type on the 
                // queue, so we have to initialize the RepaintArea with theEvent
	        ((Component)source).coalesceEvents(theEvent, theEvent);
	    }

	    queues[priority].tail.next = newItem;
	    queues[priority].tail = newItem;
	}
    }

    /**
     * @return whether an event is pending on any of the separate Queues
     */
    private boolean noEvents() {
        for (int i = 0; i < NUM_PRIORITIES; i++) {
	    if (queues[i].head != null) {
	        return false;
	    }
	}

	return true;
    }

    /**
     * Remove an event from the EventQueue and return it.  This method will
     * block until an event has been posted by another thread.
     * @return the next AWTEvent
     * @exception InterruptedException 
     *            if another thread has interrupted this thread.
     */
    public synchronized AWTEvent getNextEvent() throws InterruptedException {
        do {
	    for (int i = NUM_PRIORITIES - 1; i >= 0; i--) {
		if (queues[i].head != null) {
		    EventQueueItem eqi = queues[i].head;
		    queues[i].head = eqi.next;
		    if (eqi.next == null) {
			queues[i].tail = null;
		    }
		    return eqi.event;
		}
	    }
            wait();
        } while(true);
    }

    /**
     * Return the first event on the EventQueue without removing it.
     * @return the first event
     */
    public synchronized AWTEvent peekEvent() {
        for (int i = NUM_PRIORITIES - 1; i >= 0; i--) {
	    if (queues[i].head != null) {
	        return queues[i].head.event;
	    }
	}

	return null;
    }

    /**
     * Return the first event with the specified id, if any.
     * @param id the id of the type of event desired.
     * @return the first event of the specified id
     */
    public synchronized AWTEvent peekEvent(int id) {
        for (int i = NUM_PRIORITIES - 1; i >= 0; i--) {
	    EventQueueItem q = queues[i].head;
	    for (; q != null; q = q.next) {
	        if (q.id == id) {
		    return q.event;
		}
	    }
	}

        return null;
    }

    /**
     * Dispatch an event. The manner in which the event is
     * dispatched depends upon the type of the event and the
     * type of the event's source
     * object:
     * <p> </p>
     * <table border>
     * <tr>
     *     <th>Event Type</th>
     *     <th>Source Type</th> 
     *     <th>Dispatched To</th>
     * </tr>
     * <tr>
     *     <td>ActiveEvent</td>
     *     <td>Any</td>
     *     <td>event.dispatch()</td>
     * </tr>
     * <tr>
     *     <td>Other</td>
     *     <td>Component</td>
     *     <td>source.dispatchEvent(AWTEvent)</td>
     * </tr>
     * <tr>
     *     <td>Other</td>
     *     <td>MenuComponent</td>
     *     <td>source.dispatchEvent(AWTEvent)</td>
     * </tr>
     * <tr>
     *     <td>Other</td>
     *     <td>Other</td>
     *     <td>No action (ignored)</td>
     * </tr>
     * </table>
     * <p> </p>
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     */
    protected void dispatchEvent(AWTEvent event) {
        Object src = event.getSource();
        if (event instanceof ActiveEvent) {
            // This could become the sole method of dispatching in time.
            ((ActiveEvent)event).dispatch();
        } else if (src instanceof Component) {
            ((Component)src).dispatchEvent(event);
        } else if (src instanceof MenuComponent) {
            ((MenuComponent)src).dispatchEvent(event);
        } else {
            System.err.println("unable to dispatch event: " + event);
        }
    }

    /**
     * Replace the existing EventQueue with the specified one.
     * Any pending events are transferred to the new EventQueue
     * for processing by it.
     *
     * @param an EventQueue (or subclass thereof) instance to be used.
     * @see      java.awt.EventQueue#pop
     */
    public synchronized void push(EventQueue newEventQueue) {
	if (debug) {
	    System.out.println("EventQueue.push(" + newEventQueue + ")");
	}

        if (nextQueue != null) {
            nextQueue.push(newEventQueue);
            return;
        }

        synchronized (newEventQueue) {
	    // Transfer all events forward to new EventQueue.
	    while (peekEvent() != null) {
		try {
		    newEventQueue.postEventPrivate(getNextEvent());
		} catch (InterruptedException ie) {
		    if (debug) {
			System.err.println("interrupted push:");
			ie.printStackTrace(System.err);
		    }
		}
	    }

	    newEventQueue.previousQueue = this;
        }
	nextQueue = newEventQueue;
    }

    /**
     * Stop dispatching events using this EventQueue instance.
     * Any pending events are transferred to the previous
     * EventQueue for processing by it.  
     *
     * @exception if no previous push was made on this EventQueue.
     * @see      java.awt.EventQueue#push
     */
    protected void pop() throws EmptyStackException {
	if (debug) {
	    System.out.println("EventQueue.pop(" + this + ")");
	}

	// To prevent deadlock, we lock on the previous EventQueue before
	// this one.  This uses the same locking order as everything else
	// in EventQueue.java, so deadlock isn't possible.
	EventQueue prev = previousQueue;
	synchronized ((prev != null) ? prev : this) {
	  synchronized(this) {
            if (nextQueue != null) {
                nextQueue.pop();
                return;
            }
            if (previousQueue == null) {
                throw new EmptyStackException();
            }

	    // Transfer all events back to previous EventQueue.
	    previousQueue.nextQueue = null;
	    while (peekEvent() != null) {
		try {
		    previousQueue.postEventPrivate(getNextEvent());
		} catch (InterruptedException ie) {
		    if (debug) {
			System.err.println("interrupted pop:");
			ie.printStackTrace(System.err);
		    }
		}
	    }
	    previousQueue = null;
          }
        }

	dispatchThread.stopDispatching(); // Must be done outside synchronized
					  // block to avoid possible deadlock
    }

    /**
     * Returns true if the calling thread is the current AWT EventQueue's
     * dispatch thread.  Use this call the ensure that a given
     * task is being executed (or not being) on the current AWT
     * EventDispatchThread.
     *
     * @return true if running on the current AWT EventQueue's dispatch thread.
     */
    public static boolean isDispatchThread() {
	EventQueue eq = Toolkit.getEventQueue();
	EventQueue next = eq.nextQueue;
	while (next != null) {
	    eq = next;
	    next = eq.nextQueue;
	}
	return (Thread.currentThread() == eq.dispatchThread);
    }

    /*
     * Get the EventDispatchThread for this EventQueue.
     */
    final EventDispatchThread getDispatchThread() {
	return dispatchThread;
    }

    /*
     * Change the target of any pending KeyEvents because of a focus change.
     */
    final synchronized void changeKeyEventFocus(Object newSource) {
        for (int i = 0; i < NUM_PRIORITIES; i++) {
	    EventQueueItem q = queues[i].head;
	    for (; q != null; q = q.next) {
	        if (q.event instanceof KeyEvent) {
		    ((KeyEvent)q.event).setSource(newSource);
		}
	    }
	}
    }

    /*
     * Remove any pending events for the specified source object.
     * This method is normally called by the source's removeNotify method.
     */
    final void removeSourceEvents(Object source) {
	Toolkit toolkit = Toolkit.getDefaultToolkit();
        if (toolkit instanceof SunToolkit) {
	    ((SunToolkit)toolkit).flushPendingEvents();
	}

        synchronized (this) {
	    for (int i = 0; i < NUM_PRIORITIES; i++) {
	        EventQueueItem entry = queues[i].head;
		EventQueueItem prev = null;
		while (entry != null) {
		    if (entry.event.getSource() == source) {
		        if (prev == null) {
			    queues[i].head = entry.next;
			} else {
			    prev.next = entry.next;
			}
		    } else {
		        prev = entry;
		    }
		    entry = entry.next;
		}
		queues[i].tail = prev;
	    }
	}
    }

    /**
     * Causes <i>runnable</i> to have its run() method called in the dispatch
     * thread of the EventQueue.  This will happen after all pending events
     * are processed.
     *
     * @param runnable  the Runnable whose run() method should be executed
     *                  synchronously on the EventQueue
     * @see             #invokeAndWait
     * @since           1.2
     */
    public static void invokeLater(Runnable runnable) {
        Toolkit.getEventQueue().postEvent(
            new InvocationEvent(Toolkit.getDefaultToolkit(), runnable));
    }

    /**
     * Causes <i>runnable</i> to have its run() method called in the dispatch
     * thread of the EventQueue.  This will happen after all pending events
     * are processed.  The call blocks until this has happened.  This method
     * will throw an Error if called from the event dispatcher thread.
     *
     * @param runnable  the Runnable whose run() method should be executed
     *                  synchronously on the EventQueue
     * @exception       InterruptedException  if another thread has
     *                  interrupted this thread
     * @exception       InvocationTargetException  if an exception is thrown
     *                  when running <i>runnable</i>
     * @see             #invokeLater
     * @since           1.2
     */
    public static void invokeAndWait(Runnable runnable)
             throws InterruptedException, InvocationTargetException {

        if (EventQueue.isDispatchThread()) {
            throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
        }

	class AWTInvocationLock {}
        Object lock = new AWTInvocationLock();

        EventQueue queue = Toolkit.getEventQueue();
        InvocationEvent event = 
            new InvocationEvent(Toolkit.getDefaultToolkit(), runnable, lock,
				true);

        synchronized (lock) {
            Toolkit.getEventQueue().postEvent(event);
            lock.wait();
        }

        Exception eventException = event.getException();
        if (eventException != null) {
            throw new InvocationTargetException(eventException);
        }
    }
}

/**
 * The Queue object holds pointers to the beginning and end of one internal
 * queue. An EventQueue object is composed of multiple internal Queues, one
 * for each priority supported by the EventQueue. All Events on a particular
 * internal Queue have identical priority.
 */
class Queue {
    EventQueueItem head;
    EventQueueItem tail;
}

class EventQueueItem {
    AWTEvent event;
    int      id;
    EventQueueItem next;

    EventQueueItem(AWTEvent evt) {
        event = evt;
        id = evt.getID();
    }
}
