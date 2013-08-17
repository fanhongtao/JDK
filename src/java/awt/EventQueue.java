/*
 * @(#)EventQueue.java	1.17 97/06/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;

/**
 * EventQueue is a platform-independent class that queues events, both
 * from the underlying peer classes and from trusted application classes.
 * There is only one EventQueue for the system.
 *
 * @version 1.17 06/23/97
 * @author Thomas Ball
 */
public class EventQueue {

    // From Thread.java
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    private EventQueueItem queue;

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
        postEvent(theEvent, false);
    }

    /**
     * Post a 1.1-style event at the HEAD of the EventQueue (I.e., this
     * event will be handled next).
     *
     * @param theEvent an instance of java.awt.AWTEvent, or a
     * subclass of it.
     */
    synchronized void postEventAtHead(AWTEvent theEvent) {
        postEvent(theEvent, true);
    }

    private synchronized void postEvent(AWTEvent theEvent, boolean atHead) {
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

	    // add it to the queue
	    if (atHead) {
	        eqi.next = queue;
		queue = eqi;
	    } else {
                q.next = eqi;
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
