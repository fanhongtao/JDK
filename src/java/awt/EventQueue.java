/*
 * @(#)EventQueue.java	1.23 98/12/10
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.util.EventListener;

import java.util.Vector;

/**
 * EventQueue is a platform-independent class that queues events, both
 * from the underlying peer classes and from trusted application classes.
 * There is only one EventQueue for the system.
 *
 * @version 1.23 12/10/98
 * @author Thomas Ball
 */
public class EventQueue {


    // From Thread.java
    private static int threadInitNumber;

    // fix for 4187686 Several class objects are used for synchronization
    private static Object classLock = new Object();

    private static int nextThreadNum() {
	// fix for 4187686 Several class objects are used for synchronization
	synchronized (classLock) {
	    return threadInitNumber++;
	}
    }

    private EventQueueItem queue;
    /* The multiplexed EventQueueListener list. */
    EventQueueListener eventQueueListener;

    public EventQueue() {
        queue = null;
        String name = "AWT-EventQueue-" + nextThreadNum();
        new EventDispatchThread(name, this).start();
    }

    /**
     * Post a 1.1-style event to the EventQueue.
     *
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     */
    public synchronized void postEvent(AWTEvent theEvent) {
        EventQueueItem eqi = new EventQueueItem(theEvent);
	if (queue == null) {
	    queue = eqi;
	    notifyAll();
	} else {
	    EventQueueItem q = queue;
	    for (;;) {
		if (q.id == eqi.id) {
		    switch (q.id) {
		      case Event.MOUSE_MOVE:
		      case Event.MOUSE_DRAG:
			  // New-style event id's never collide with 
			  // old-style id's, so if the id's are equal, 
			  // we can safely cast the queued event to be 
			  // an old-style one.
			  MouseEvent e = (MouseEvent)q.event;
			  if (e.getSource() == ((MouseEvent)theEvent).getSource() &&
			      e.getModifiers() == ((MouseEvent)theEvent).getModifiers()) {
			      q.event = eqi.event;// just replace old event
			      return;
			  }
			  break;

		      case PaintEvent.PAINT:
		      case PaintEvent.UPDATE:
			  PaintEvent pe = (PaintEvent)q.event;
			  if (pe.getSource() == theEvent.getSource()) {
			      Rectangle rect = pe.getUpdateRect();
			      Rectangle newRect = 
				  ((PaintEvent)theEvent).getUpdateRect();
			      if (!rect.equals(newRect)) {
				  pe.setUpdateRect(rect.union(newRect));
			      }
			      return;
			  }
			  break;

		    }
		}
		if (q.next != null) {
		    q = q.next;
		} else {
		    break;
		}
	    }
	    q.next = eqi;
	}
	notifyEventQueueListeners(theEvent);
    }

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
        queue = queue.next;
        return eqi.event;
    }

    /**
     * Return the first event without removing it.
     * @return the first event, which is either an instance of java.awt.Event
     * or java.awt.AWTEvent.
     */
    public synchronized AWTEvent peekEvent() {
        return (queue != null) ? queue.event : null;
    }

    /*
     * Return the first event of the specified type, if any.
     * @param id the id of the type of event desired.
     * @return the first event of the requested type, 
     * which is either an instance of java.awt.Event
     * or java.awt.AWTEvent.
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

    /* comment out until 1.2...
    /**
     * Dispatch an event to its source.
     *
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     *
     *    protected void dispatchEvent(AWTEvent event) {
     *        Object src = event.getSource();
     *        if (src instanceof Component) {
     *            ((Component)src).dispatchEvent(event);
     *        } else if (src instanceof MenuComponent) {
     *            ((MenuComponent)src).dispatchEvent(event);
     *        }
     *    }
     */

    /*
     * (Copied from new public 1.2 API)
     * Adds listener to event queue
     */
    synchronized void addEventQueueListener(EventQueueListener l) {
        eventQueueListener = EventQueueMulticaster.add(eventQueueListener, l);
    }

    /*
     * (Copied from new public 1.2 API)
     * Removes listener from event queue
     */
    synchronized void removeEventQueueListener(EventQueueListener l) {
        eventQueueListener = EventQueueMulticaster.remove(eventQueueListener, l);
    }


    /*
     * Change the target of any pending KeyEvents because of a focus change.
     */
    synchronized void changeKeyEventFocus(Object newSource) {
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
    synchronized void removeSourceEvents(Object source) {
        EventQueueItem entry = queue;
        EventQueueItem prev = null;
        while (entry != null) {
            if (entry.event.getSource().equals(source)) {
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

    /*
     * Remove any pending events of the given class and id
     */
    synchronized void removeEvents(Class evClass, int id) {
        EventQueueItem entry = queue;
        EventQueueItem prev = null;
        while (entry != null) {
            if (evClass.isInstance(entry.event) && entry.event.getID() == id) {
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

    static private class EventQueueMulticaster extends AWTEventMulticaster
        implements EventQueueListener {
        // Implementation cloned from AWTEventMulticaster.

        EventQueueMulticaster(EventListener a, EventListener b) {
            super(a, b);
        }

        static EventQueueListener add(EventQueueListener a,
                                      EventQueueListener b) {
            if (a == null)  return b;
            if (b == null)  return a;
            return new EventQueueMulticaster(a, b);
        }

        static EventQueueListener remove(EventQueueListener l,
                                         EventQueueListener oldl) {
            return (EventQueueListener) removeInternal(l, oldl);
        }

        // #4179773: must overload remove(EventListener) to call our add()
        // instead of the static addInternal() so we allocate an
        // EventQueueMulticaster instead of an AWTEventMulticaster.
        // Note: this method is called by AWTEventMulticaster.removeInternal(),
        // so its method signature must match AWTEventMulticaster.remove().
        protected EventListener remove(EventListener oldl) {
            if (oldl == a)  return b;
            if (oldl == b)  return a;
            EventQueueListener a2 = (EventQueueListener)removeInternal(a, oldl);
            EventQueueListener b2 = (EventQueueListener)removeInternal(b, oldl);
            if (a2 == a && b2 == b) {
                return this;	// it's not here
            }
            return add(a2, b2);
        }

        public void eventPosted(AWTEvent e) {
            ((EventQueueListener)a).eventPosted(e);
            ((EventQueueListener)b).eventPosted(e);
        }
    }

    /*
     * Based on new protected 1.2 API
     */
    private void notifyEventQueueListeners(AWTEvent theEvent) {
        if (eventQueueListener != null) {
            eventQueueListener.eventPosted(theEvent);
        }
    }
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
