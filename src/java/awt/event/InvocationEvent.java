/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.awt.ActiveEvent;
import java.awt.AWTEvent;

/**
 * An event which executes the <code>run()</code> method on a <code>Runnable
 * </code> when dispatched by the AWT event dispatcher thread. This class can
 * be used as a reference implementation of <code>ActiveEvent</code> rather
 * than declaring a new class and defining <code>dispatch()</code>.<p>
 *
 * Instances of this class are placed on the <code>EventQueue</code> by calls
 * to <code>invokeLater</code> and <code>invokeAndWait</code>. Client code
 * can use this fact to write replacement functions for <code>invokeLater
 * </code> and <code>invokeAndWait</code> without writing special-case code
 * in any <code>AWTEventListener</code> objects.
 *
 * @author	Fred Ecks
 * @author	David Mendenhall
 * @version	1.10, 02/06/02
 *
 * @see		java.awt.ActiveEvent
 * @see		java.awt.EventQueue#invokeLater
 * @see		java.awt.EventQueue#invokeAndWait
 * @see		AWTEventListener
 *
 * @since 	1.2
 */
public class InvocationEvent extends AWTEvent implements ActiveEvent {

    /**
     * Marks the first integer id for the range of invocation event ids.
     */
    public static final int INVOCATION_FIRST = 1200;

    /**
     * The default id for all InvocationEvents.
     */
    public static final int INVOCATION_DEFAULT = INVOCATION_FIRST;

    /**
     * Marks the last integer id for the range of invocation event ids.
     */
    public static final int INVOCATION_LAST = INVOCATION_DEFAULT;

    /**
     * The Runnable whose run() method will be called.
     */
    protected Runnable runnable;

    /**
     * The (potentially null) Object whose notifyAll() method will be called
     * immediately after the Runnable.run() method returns.
     */
    protected Object notifier;

    /**
     * Set to true if dispatch() catches Exception and stores it in the
     * exception instance variable. If false, Exceptions are propagated up
     * to the EventDispatchThread's dispatch loop.
     */
    protected boolean catchExceptions;

    /**
     * The (potentially null) Exception thrown during execution of the
     * Runnable.run() method. This variable will also be null if a particular
     * instance does not catch exceptions.
     */
    private Exception exception = null;

    /**
     * Constructs an InvocationEvent with the specified source which will
     * execute the runnable's <code>run()</code> method when dispatched.
     *
     * @param source	the Object that originated the event
     * @param runnable	the Runnable whose run() method will be executed
     */
    public InvocationEvent(Object source, Runnable runnable) {
        this(source, runnable, null, false);
    }

    /**
     * Constructs an InvocationEvent with the specified source which will
     * execute the runnable's <code>run()</code> method when dispatched.  If
     * notifier is non-null, <code>notifyAll()</code> will be called on it
     * immediately after <code>run()</code> returns.
     *
     * @param source		the Object that originated the event
     * @param runnable		the Runnable whose run() method will be
     *                          executed
     * @param notifier		the Object whose notifyAll() method will be
     *                          called after Runnable.run() has returned
     * @param catchExceptions	specifies whether dispatch() should catch
     *                          Exception when executing the Runnable's run()
     *                          method, or should instead propagate those
     *                          Exceptions to the EventDispatchThread's
     *                          dispatch loop
     */
    public InvocationEvent(Object source, Runnable runnable, Object notifier,
                       boolean catchExceptions) {
	this(source, INVOCATION_DEFAULT, runnable, notifier, catchExceptions);
    }

    /**
     * Constructs an InvocationEvent with the specified source and ID which
     * will execute the runnable's <code>run()</code> method when dispatched.
     * If notifier is non-null, <code>notifyAll()</code> will be called on it 
     * immediately after <code>run()</code> returns.
     *
     * @param source		the Object that originated the event
     * @param id		the ID for the Event
     * @param runnable		the Runnable whose run() method will be
     *                          executed
     * @param notifier		the Object whose notifyAll() method will be
     *                          called after Runnable.run() has returned
     * @param catchExceptions	specifies whether dispatch() should catch
     *                          Exception when executing the Runnable's run()
     *                          method, or should instead propagate those
     *                          Exceptions to the EventDispatchThread's
     *                          dispatch loop
     */
    protected InvocationEvent(Object source, int id, Runnable runnable, 
	     	          Object notifier, boolean catchExceptions) {
        super(source, id);
	this.runnable = runnable;
	this.notifier = notifier;
	this.catchExceptions = catchExceptions;
    }

    /**
     * Executes the Runnable's <code>run()</code> method and notifies the
     * notifier (if any) when <code>run()</code> returns.
     */
    public void dispatch() {
	if (catchExceptions) {
	    try {
		runnable.run();
	    } 
	    catch (Exception e) {
		exception = e;
	    }
	}
	else {
	    runnable.run();
	}

	if (notifier != null) {
	    synchronized (notifier) {
		notifier.notifyAll();
	    }
	}
    }

    /**
     * Returns any Exception caught while executing the Runnable's <code>run()
     * </code> method.
     *
     * @return	A reference to the Exception if one was thrown; null if no
     *		Exception was thrown or if this InvocationEvent does not
     *		catch exceptions
     */
    public Exception getException() {
	return (catchExceptions) ? exception : null;
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * @return  A string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
	switch(id) {
            case INVOCATION_DEFAULT:
	        typeStr = "INVOCATION_DEFAULT";
		break;
            default:
	        typeStr = "unknown type";
	}
	return typeStr + ",runnable=" + runnable + ",notifier=" + notifier +
	    ",catchExceptions=" + catchExceptions;
    }
}
