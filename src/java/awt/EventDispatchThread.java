/*
 * @(#)EventDispatchThread.java	1.16 98/11/24  Amy Fowler
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

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.PaintEvent;
import java.awt.peer.ActiveEvent;

/**
 * EventDispatchThread is a package-private AWT class which takes
 * events off the EventQueue and dispatches them to the appropriate
 * AWT components.
 *
 * @version 1.16 11/24/98
 * @author Tom Ball
 * @author Amy Fowler
 */
class EventDispatchThread extends Thread {
    private EventQueue theQueue;
    private boolean doDispatch = true;

    EventDispatchThread(String name, EventQueue queue) {
	super(name);
        theQueue = queue;
    }

    public void stopDispatching() {
	doDispatch = false;
	// post an empty event to ensure getNextEvent
	// is unblocked - rkhan 4/14/98
	theQueue.postEvent(new EmptyEvent());
	// wait for the dispatcher to complete
	if (Thread.currentThread() != this) {
	    try {
		join();
	    } catch(InterruptedException e) {
	    }
	}
    }

    class EmptyEvent extends AWTEvent implements ActiveEvent {
	public EmptyEvent() {
	    super(EventDispatchThread.this,0);
	}

	public void dispatch() {}
    }

    public void run() {
       while (doDispatch) {
            try {
                AWTEvent event = theQueue.getNextEvent();
                if (false) {
                    // Not until 1.2...
                    // theQueue.dispatchEvent(event);
                } else {
                    // old code...
                    Object src = event.getSource();
                    if (event instanceof ActiveEvent) {
			// This could become the sole method of dispatching in time, and 
			// moved to the event queue's dispatchEvent() method.
			((ActiveEvent)event).dispatch();
		    } else if (src instanceof Component) {
                        ((Component)src).dispatchEvent(event);
                    } else if (src instanceof MenuComponent) {
                        ((MenuComponent)src).dispatchEvent(event);
		    }
                }
            } catch (ThreadDeath death) {
                return;

            } catch (Throwable e) {
                System.err.println(
                    "Exception occurred during event dispatching:");
                e.printStackTrace();
            }
        }
    }
}
