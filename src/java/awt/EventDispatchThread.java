/*
 * @(#)EventDispatchThread.java	1.54 05/03/03
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.awt.DebugHelper;
import sun.awt.AWTAutoShutdown;
import sun.awt.SunToolkit;

import sun.awt.dnd.SunDragSourceContextPeer;

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
 * @version 1.54, 03/03/05
 * @since 1.1
 */
class EventDispatchThread extends Thread {
    private static final DebugHelper dbg = DebugHelper.create(EventDispatchThread.class);

    private EventQueue theQueue;
    private boolean doDispatch = true;
    private static final int ANY_EVENT = -1;

    EventDispatchThread(ThreadGroup group, String name, EventQueue queue) {
        super(group, name);
        theQueue = queue;
    }

    void stopDispatchingImpl(boolean wait) {
        // Note: We stop dispatching via a flag rather than using
        // Thread.interrupt() because we can't guarantee that the wait()
        // we interrupt will be EventQueue.getNextEvent()'s.  -fredx 8-11-98

        StopDispatchEvent stopEvent = new StopDispatchEvent();

        // wait for the dispatcher to complete
        if (Thread.currentThread() != this) {

            // fix 4122683, 4128923
            // Post an empty event to ensure getNextEvent is unblocked
            //
            // We have to use postEventPrivate instead of postEvent because
            // EventQueue.pop calls EventDispatchThread.stopDispatching.
            // Calling SunToolkit.flushPendingEvents in this case could
            // lead to deadlock.
            theQueue.postEventPrivate(stopEvent);
                
            if (wait) {
                try {
                    join();
                } catch(InterruptedException e) {
                }
            }
        } else {
            stopEvent.dispatch();
        }
        synchronized (theQueue) {
            if (theQueue.getDispatchThread() == this) {
                theQueue.detachDispatchThread();
            }
        }
    }

    public void stopDispatching() {
        stopDispatchingImpl(true);
    }

    public void stopDispatchingLater() {
        stopDispatchingImpl(false);
    }

    class StopDispatchEvent extends AWTEvent implements ActiveEvent {
        public StopDispatchEvent() {
            super(EventDispatchThread.this,0);
        }

        public void dispatch() {
            doDispatch = false;
        }
    }

    public void run() {
	try {
	    pumpEvents(new Conditional() {
		public boolean evaluate() {
		    return true;
		}
	    });	    
	} finally {
	    /*
	     * This synchronized block is to secure that the event dispatch 
	     * thread won't die in the middle of posting a new event to the
	     * associated event queue. It is important because we notify
	     * that the event dispatch thread is busy after posting a new event
	     * to its queue, so the EventQueue.dispatchThread reference must
	     * be valid at that point.
	     */
	    synchronized (theQueue) {
                if (theQueue.getDispatchThread() == this) {
                    theQueue.detachDispatchThread();
                }
                /*
                 * Event dispatch thread dies in case of an uncaught exception. 
                 * A new event dispatch thread for this queue will be started
                 * only if a new event is posted to it. In case if no more
                 * events are posted after this thread died all events that 
                 * currently are in the queue will never be dispatched.
                 */
                /*
                 * Fix for 4648733. Check both the associated java event
                 * queue and the PostEventQueue.
                 */
                if (theQueue.peekEvent() != null || 
                    !SunToolkit.isPostEventQueueEmpty()) { 
                    theQueue.initDispatchThread();
                }
		AWTAutoShutdown.getInstance().notifyThreadFree(this);
	    }
	}
    }

    void pumpEvents(Conditional cond) {
	pumpEvents(ANY_EVENT, cond);
    }

    void pumpEventsForHierarchy(Conditional cond, Component modalComponent) {
        pumpEventsForHierarchy(ANY_EVENT, cond, modalComponent);
    }

    void pumpEvents(int id, Conditional cond) {
        pumpEventsForHierarchy(id, cond, null);
    }

    void pumpEventsForHierarchy(int id, Conditional cond, Component modalComponent)
    {
        while (doDispatch && cond.evaluate()) {
            if (isInterrupted() || !pumpOneEventForHierarchy(id, modalComponent)) {
                doDispatch = false;
            }
        }
    }

    boolean checkMouseEventForModalJInternalFrame(MouseEvent me, Component modalComp) {
        // Check if the MouseEvent is targeted to the HW parent of the
        // LW component, if so, then return true. The job of distinguishing
        // between the LW components is done by the LW dispatcher.
        if (modalComp instanceof javax.swing.JInternalFrame) {
            Container c;
            synchronized (modalComp.getTreeLock()) {
                c = ((Container)modalComp).getHeavyweightContainer();
            }
            if (me.getSource() == c) 
                return true;
        }
        return false;
    }

    boolean pumpOneEventForHierarchy(int id, Component modalComponent) {
        try {
            AWTEvent event;
            boolean eventOK;
            do {
	        event = (id == ANY_EVENT)
		    ? theQueue.getNextEvent()
		    : theQueue.getNextEvent(id);

                eventOK = true;
                if (modalComponent != null) {
                    /*
                     * filter out MouseEvent and ActionEvent that's outside
                     * the modalComponent hierarchy.
                     * KeyEvent is handled by using enqueueKeyEvent
                     * in Dialog.show
                     */
                    int eventID = event.getID();
                    if (((eventID >= MouseEvent.MOUSE_FIRST &&
                            eventID <= MouseEvent.MOUSE_LAST) &&
                            !(checkMouseEventForModalJInternalFrame((MouseEvent)
                                event, modalComponent))) || 
                            (eventID >= ActionEvent.ACTION_FIRST &&
                            eventID <= ActionEvent.ACTION_LAST) ||
                            eventID == WindowEvent.WINDOW_CLOSING) {
                        Object o = event.getSource();
                        if (o instanceof sun.awt.ModalExclude) {
                            // Exclude this object from modality and
                            // continue to pump it's events.
                        } else if (o instanceof Component) {
                            Component c = (Component) o;
                            boolean modalExcluded = false;
                            if (modalComponent instanceof Container) {
                                while (c != modalComponent && c != null) {
                                    if ((c instanceof Window) &&
                                        (sun.awt.SunToolkit.isModalExcluded((Window)c))) {
                                            // Exclude this window and all its children from
                                            //  modality and continue to pump it's events.
                                        modalExcluded = true;
                                        break;
                                    }
                                    c = c.getParent();
                                }
                            }
                            if (!modalExcluded && (c != modalComponent)) {
                                eventOK = false;
                            }
                        }
                    }
                }
                eventOK = eventOK && SunDragSourceContextPeer.checkEvent(event);
                if (!eventOK) {
                    event.consume();
                }
            } while (eventOK == false);
                      
	    if ( dbg.on ) dbg.println("Dispatching: "+event);

            theQueue.dispatchEvent(event);
            return true;
        } catch (ThreadDeath death) {
            return false;

        } catch (InterruptedException interruptedException) {
            return false; // AppContext.dispose() interrupts all
                          // Threads in the AppContext

	    // Can get and throw only unchecked exceptions
        } catch (RuntimeException e) {
            processException(e, modalComponent != null);
        } catch (Error e) {
            processException(e, modalComponent != null);
        }
        return true;
    }

    private void processException(Throwable e, boolean isModal) {
        if (!handleException(e)) {
            // See bug ID 4499199.
            // If we are in a modal dialog, we cannot throw
            // an exception for the ThreadGroup to handle (as added
            // in RFE 4063022).  If we did, the message pump of
            // the modal dialog would be interrupted.
            // We instead choose to handle the exception ourselves.
            // It may be useful to add either a runtime flag or API
            // later if someone would like to instead dispose the
            // dialog and allow the thread group to handle it.
            if (isModal) {
                System.err.println(
                    "Exception occurred during event dispatching:");
                e.printStackTrace();
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else if (e instanceof Error) {
                throw (Error)e;
            }
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
     * @return  <tt>false</tt> if any of the above steps failed, otherwise
     *          <tt>true</tt>
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
