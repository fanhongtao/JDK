/*
 * @(#)EventQueue.java	1.46 98/09/16
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.ActiveEvent;
import java.util.EmptyStackException;
import java.lang.reflect.InvocationTargetException;
import sun.awt.MagicEvent;

/**
 * EventQueue is a platform-independent class that queues events, both
 * from the underlying peer classes and from trusted application classes.
 * There is only one EventQueue for each AppContext.
 *
 * @version 1.46 09/16/98
 * @author Thomas Ball
 * @author Fred Ecks
 */
public class EventQueue {

    // From Thread.java
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    /* The actual queue of events, implemented as a linked-list. */
    private EventQueueItem queue;
 
    /* The tail of the list. Maintained so that non-Component source events
       can be appended to the end of the list without O(n) traversal. */
    private EventQueueItem queueTail;

    /* The last element in the list which is a priority event. Will be null
       if the list is empty or there are no priority events in the queue.
       Priority events are MagicEvents with the PRIORITY_EVENT bit set
       (high priority). */
    private EventQueueItem lastPriorityItem;

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
        queue = queueTail = lastPriorityItem = null;
        String name = "AWT-EventQueue-" + nextThreadNum();
        dispatchThread = new EventDispatchThread(name, this);
        dispatchThread.setPriority(Thread.NORM_PRIORITY + 1);
        dispatchThread.start();
    }

    /**
     * Post a 1.1-style event to the EventQueue.  If there is an
     * existing event on the queue with the same ID and event source,
     * the source component's coalesceEvents method will be called.
     *
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     */
    public synchronized void postEvent(AWTEvent theEvent) {
        if (theEvent instanceof MagicEvent) {
            long flags = ((MagicEvent)theEvent).getFlags();
	    if ((flags & MagicEvent.PRIORITY_EVENT) != 0) {
                postEvent(theEvent, true);
		return;
	    }
	}
	
	postEvent(theEvent, false);
    }

    /*
     * Inserts an event in the queue, at the proper position. Handles
     * event coalescing.
     */
    private void postEvent(AWTEvent theEvent, boolean priorityEvent) {

        if (nextQueue != null) {
            // Forward event to top of EventQueue stack.
            nextQueue.postEvent(theEvent);
            return;
        }

        Object source = theEvent.getSource();
        EventQueueItem eqi = new EventQueueItem(theEvent);
        if (queue == null) {
            queue = queueTail = eqi;
            if (priorityEvent) {
                lastPriorityItem = eqi;
            }
            notifyAll();
        } else {
            // For Component source events, traverse the entire list,
            // trying to coalesce events
            if (source instanceof Component) {
                EventQueueItem q = queue;
                for (;;) {
                    if (q.id == eqi.id && q.source == source) {
                        AWTEvent coalescedEvent = 
                            ((Component)source).coalesceEvents(q.event, 
                                                               theEvent);
                        if (coalescedEvent != null) {
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
            // Insert it into the queue.
            if (priorityEvent && lastPriorityItem == null) {
	        // Post at front of queue. Set lastPriorityItem to front of
	        // queue.
                eqi.next = queue;
                queue = lastPriorityItem = eqi;
            } else if (priorityEvent && lastPriorityItem == queueTail) {
	        // Post at end of queue. Set lastPriorityItem to end of queue.
	        queueTail.next = eqi;
		queueTail = lastPriorityItem = eqi;
            } else if (priorityEvent) {
	        // Post after lastPriorityItem. Set lastPriorityItem to new
	        // item.
                eqi.next = lastPriorityItem.next;
                lastPriorityItem.next = eqi;
                lastPriorityItem = eqi;
            } else {
	        // Post at end of queue. lastPriorityItem unchanged.
                queueTail.next = eqi;
                queueTail = eqi;
            }
        }
    } // postEvent()

    /**
     * Remove an event from the queue and return it.  This method will
     * block until an event has been posted by another thread.
     * @return the next AWTEvent
     * @exception InterruptedException 
     *            if another thread has interrupted this thread.
     */
    public synchronized AWTEvent getNextEvent() throws InterruptedException {
        while (queue == null) {
            wait();
        }
        EventQueueItem eqi = queue;
        if (queue == lastPriorityItem) {
            lastPriorityItem = null;
        }
        queue = queue.next;
	if (queue == null) {
	    queueTail = null;
	}
        return eqi.event;
    }

    /**
     * Return the first event without removing it.
     * @return the first event
     */
    public synchronized AWTEvent peekEvent() {
        return (queue != null) ? queue.event : null;
    }

    /**
     * Return the first event with the specified id, if any.
     * @param id the id of the type of event desired.
     * @return the first event of the specified id
     */
    public synchronized AWTEvent peekEvent(int id) {
        EventQueueItem q = queue;
        for (; q != null; q = q.next) {
            if (q.id == id) {
                return q.event;
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
                boolean priority = (lastPriorityItem != null);
		try {
		    newEventQueue.postEvent(getNextEvent(), priority);
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
                boolean priority = (lastPriorityItem != null);
		try {
		    previousQueue.postEvent(getNextEvent(), priority);
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
        EventQueueItem q = queue;
        for (; q != null; q = q.next) {
            if (q.event instanceof KeyEvent) {
                q.event.setSource(newSource);
            }
        }
    }

    /*
     * Remove any pending events for the specified source object.
     * This method is normally called by the source's removeNotify method.
     */
    final synchronized void removeSourceEvents(Object source) {
        EventQueueItem entry = queue;
        EventQueueItem prev = null;
        while (entry != null) {
            if (entry.source == source) {
                if (entry == lastPriorityItem) {
                    lastPriorityItem = prev;
                }
                if (prev == null) {
                    queue = entry.next;
                } else {
                    prev.next = entry.next;
                }
            } else {
                prev = entry;
            }
            entry = entry.next;
        }
	queueTail = prev;
    }

    /*   
     * Remove any pending events of specified id for the specified source.
     */
    synchronized void removeSourceEvents(Object source, int id) {
        EventQueueItem entry = queue;
        EventQueueItem prev = null;
        while (entry != null) {
            if ((entry.event.getSource().equals(source)) && (entry.id == id)) {
                if (prev == null) {
                    queue = entry.next;
                } else {
                    prev.next = entry.next;
                }
            }    
            prev = entry;
            entry = entry.next;
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
     * @since           JDK1.2
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
     * @since           JDK1.2
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

class EventQueueItem {
    AWTEvent event;
    int      id;
    Object   source;
    EventQueueItem next;

    EventQueueItem(AWTEvent evt) {
        event = evt;
        id = evt.getID();
        source = evt.getSource();
    }
}
