/*
 * @(#)EventDispatchThread.java	1.26 98/08/11
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

/**
 * EventDispatchThread is a package-private AWT class which takes
 * events off the EventQueue and dispatches them to the appropriate
 * AWT components.
 *
 * @version 1.26 08/11/98
 * @author Tom Ball
 * @author Amy Fowler
 * @author Fred Ecks
 */
class EventDispatchThread extends Thread {
    private EventQueue theQueue;
    private boolean doDispatch = true;

    EventDispatchThread(String name, EventQueue queue) {
	super(name);
        theQueue = queue;
    }

    public void stopDispatching() {
	// Note: We stop dispatching via a flag rather than using
	// Thread.interrupt() because we can't guarantee that the wait()
	// we interrupt will be EventQueue.getNextEvent()'s.  -fredx 8-11-98

        doDispatch = false;

	// fix 4122683, 4128923
	// Post an empty event to ensure getNextEvent is unblocked
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
       while (doDispatch && !isInterrupted()) {
            try {
                AWTEvent event = theQueue.getNextEvent();
                theQueue.dispatchEvent(event);
            } catch (ThreadDeath death) {
                return;

	    } catch (InterruptedException interruptedException) {
		return; // AppContext.dispose() interrupts all
			// Threads in the AppContext

            } catch (Throwable e) {
                System.err.println(
                    "Exception occurred during event dispatching:");
                e.printStackTrace();
            }
        }
    }

    boolean isDispatching(EventQueue eq) {
	return theQueue.equals(eq);
    }

    EventQueue getEventQueue() { return theQueue; }
}
