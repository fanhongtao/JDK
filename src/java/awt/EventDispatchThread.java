/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.lang.reflect.Method;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.awt.DebugHelper;
import java.awt.event.InputEvent;


/**
 * EventDispatchThread is a package-private AWT class which takes
 * events off the EventQueue and dispatches them to the appropriate
 * AWT components.
 *
 * The Thread starts a "permanent" event pump with a call to
 * pumpEvents(Conditional) in its run() method. Event handlers can choose to
 * block this event pump at any time, but should start a new pump (<b>not</b>
 * a new EventDispatchThread) by again calling pumpEvents(Conditional). This
 * secondary event pump will exit automatically as soon as the Condtional
 * evaluate()s to false and an additional Event is pumped and dispatched.
 *
 * @author Tom Ball
 * @author Amy Fowler
 * @author Fred Ecks
 * @author David Mendenhall
 * 
 * @version 1.37, 02/06/02
 * @since 1.1
 */
class EventDispatchThread extends Thread {
    private static final DebugHelper dbg = DebugHelper.create(EventDispatchThread.class);

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
	//
        // We have to use postEventPrivate instead of postEvent because
        // EventQueue.pop calls EventDispatchThread.stopDispatching.
        // Calling SunToolkit.flushPendingEvents in this case could
        // lead to deadlock.
	theQueue.postEventPrivate(new EmptyEvent());

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
        pumpEvents(new Conditional() {
            public boolean evaluate() {
                return true;
            }
        });
    }

    void pumpEvents(Conditional cond) {
	pumpEventsForHierarchy(cond, null);
    }

    void pumpEventsForHierarchy(Conditional cond, Component modalComponent) {
        while (doDispatch && cond.evaluate()) {
            if (isInterrupted() || !pumpOneEventForHierarchy(modalComponent)) {
                doDispatch = false;
            }
        }
    }

    boolean pumpOneEventForHierarchy(Component modalComponent) {
        try {
            AWTEvent event = theQueue.getNextEvent();
	    if (modalComponent != null) {
		/*
		 * filter out InputEvent that's not belong to
		 * the specified modal component.
		 * this can be caused by the following case:
	         * a button, click once to open up a modal dialog
		 * but use click on it twice really fast. 
		 * before the modal dialog comes up, the second 
		 * mouse click already comes in.
		 * see also the comment in Dialog.show
		 */
		while  (event instanceof InputEvent) {
		    Component c = (Component)event.getSource();
		    // check if c's modalComponent's child
		    if (modalComponent instanceof Container)
		        while (c != modalComponent && c != null)
			    c = c.getParent();
		    if (c != modalComponent)
			event = theQueue.getNextEvent();
		    else
			break;
		}
 	    }		 
	    if ( dbg.on ) dbg.println("Dispatching: "+event);
            theQueue.dispatchEvent(event);
            return true;
        } catch (ThreadDeath death) {
            return false;

        } catch (InterruptedException interruptedException) {
            return false; // AppContext.dispose() interrupts all
                          // Threads in the AppContext

        } catch (Throwable e) {
	    if (!handleException(e)) {
		System.err.println(
                "Exception occurred during event dispatching:");
		e.printStackTrace();
	    }
            return true;
        }
    }

    private static final String handlerPropName = "sun.awt.exception.handler";
    private static String handlerClassName = null;
    private static String NO_HANDLER = new String();

    /**
     * Handles an exception thrown in the event-dispatch thread.
     *
     * <p> If the system property "sun.awt.exception.handler" is defined, then
     * when this method is invoked it will attempt to do the following:
     *
     * <ol>
     * <li> Load the class named by the value of that property, using the
     *      current thread's context class loader,
     * <li> Instantiate that class using its zero-argument constructor,
     * <li> Find the resulting handler object's <tt>public void handle</tt>
     *      method, which should take a single argument of type
     *      <tt>Throwable</tt>, and
     * <li> Invoke the handler's <tt>handle</tt> method, passing it the
     *      <tt>thrown</tt> argument that was passed to this method.
     * </ol>
     *
     * If any of the first three steps fail then this method will return
     * <tt>false</tt> and all following invocations of this method will return
     * <tt>false</tt> immediately.  An exception thrown by the handler object's
     * <tt>handle</tt> will be caught, and will cause this method to return
     * <tt>false</tt>.  If the handler's <tt>handle</tt> method is successfully
     * invoked, then this method will return <tt>true</tt>.  This method will
     * never throw any sort of exception.
     *
     * <p> <i>Note:</i> This method is a temporary hack to work around the
     * absence of a real API that provides the ability to replace the
     * event-dispatch thread.  The magic "sun.awt.exception.handler" property
     * <i>will be removed</i> in a future release.
     *
     * @param  thrown  The Throwable that was thrown in the event-dispatch
     *                 thread
     *
     * @returns  <tt>false</tt> if any of the above steps failed, otherwise
     *           <tt>true</tt>.
     */
    private boolean handleException(Throwable thrown) {

        try {

            if (handlerClassName == NO_HANDLER) {
                return false;   /* Already tried, and failed */
            }

            /* Look up the class name */
            if (handlerClassName == null) {
                handlerClassName = ((String) AccessController.doPrivileged(
                    new GetPropertyAction(handlerPropName)));
                if (handlerClassName == null) {
                    handlerClassName = NO_HANDLER; /* Do not try this again */
                    return false;
                }
            }

            /* Load the class, instantiate it, and find its handle method */
            Method m;
            Object h;
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Class c = Class.forName(handlerClassName, true, cl);
                m = c.getMethod("handle", new Class[] { Throwable.class });
                h = c.newInstance();
            } catch (Throwable x) {
                handlerClassName = NO_HANDLER; /* Do not try this again */
                return false;
            }

            /* Finally, invoke the handler */
            m.invoke(h, new Object[] { thrown });

        } catch (Throwable x) {
            return false;
        }

        return true;
    }

    boolean isDispatching(EventQueue eq) {
	return theQueue.equals(eq);
    }

    EventQueue getEventQueue() { return theQueue; }
}
