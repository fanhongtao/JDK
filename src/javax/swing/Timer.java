/*
 * @(#)Timer.java	1.45 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */



package javax.swing;



import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.event.EventListenerList;



/**
 * Fires one or more action events after a specified delay.  
 * For example, an animation object can use a <code>Timer</code>
 * as the trigger for drawing its frames.
 *
 *<p>
 *
 * Setting up a timer
 * involves creating a <code>Timer</code> object,
 * registering one or more action listeners on it,
 * and starting the timer using
 * the <code>start</code> method.
 * For example, 
 * the following code creates and starts a timer
 * that fires an action event once per second
 * (as specified by the first argument to the <code>Timer</code> constructor).
 * The second argument to the <code>Timer</code> constructor
 * specifies a listener to receive the timer's action events.
 *
 *<pre>
 *  int delay = 1000; //milliseconds
 *  ActionListener taskPerformer = new ActionListener() {
 *      public void actionPerformed(ActionEvent evt) {
 *          <em>//...Perform a task...</em>
 *      }
 *  };
 *  new Timer(delay, taskPerformer).start();</pre>
 *
 * <p>
 * Each <code>Timer</code>
 * has one or more action listeners
 * and a <em>delay</em>
 * (the time between action events).  
 * When
 * <em>delay</em> milliseconds have passed, the <code>Timer</code>
 * fires an action event to its listeners.  
 * By default, this cycle repeats until
 * the <code>stop</code> method is called.
 * If you want the timer to fire only once,
 * invoke <code>setRepeats(false)</code> on the timer.
 * To make the delay before the first action event
 * different from the delay between events,
 * use the <code>setInitialDelay</code> method.
 *
 * <p>
 * Although all <code>Timer</code>s perform their waiting
 * using a single, shared thread 
 * (created by the first <code>Timer</code> object that executes),
 * the action event handlers for <code>Timer</code>s
 * execute on another thread -- the event-dispatching thread.
 * This means that the action handlers for <code>Timer</code>s
 * can safely perform operations on Swing components.
 * However, it also means that the handlers must execute quickly
 * to keep the GUI responsive.
 *
 * <p>
 * In v 1.3, another <code>Timer</code> class was added
 * to the Java platform: <code>java.util.Timer</code>.
 * Both it and <code>javax.swing.Timer</code>
 * provide the same basic functionality,
 * but <code>java.util.Timer</code> 
 * is more general and has more features.
 * The <code>javax.swing.Timer</code> has two features
 * that can make it a little easier to use with GUIs.
 * First, its event handling metaphor is familiar to GUI programmers
 * and can make dealing with the event-dispatching thread
 * a bit simpler.
 * Second, its
 * automatic thread sharing means that you don't have to 
 * take special steps to avoid spawning
 * too many threads.
 * Instead, your timer uses the same thread
 * used to make cursors blink,
 * tool tips appear, 
 * and so on.
 *
 * <p>
 * You can find further documentation
 * and several examples of using timers by visiting
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/timer.html"
 * target = "_top">How to Use Timers</a>,
 * a section in <em>The Java Tutorial.</em>
 * For more examples and help in choosing between
 * this <code>Timer</code> class and 
 * <code>java.util.Timer</code>, 
 * see 
 * <a href="http://java.sun.com/products/jfc/tsc/articles/timer/"
 * target="_top">Using Timers in Swing Applications</a>,
 * an article in <em>The Swing Connection.</em>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @see java.util.Timer <code>java.util.Timer</code>
 *
 *
 * @version 1.45 05/05/04
 * @author Dave Moore
 */
public class Timer implements Serializable
{
    protected EventListenerList listenerList = new EventListenerList();

    // The following field strives to maintain the following:
    //    If coalesce is true, only allow one Runnable to be queued on the
    //    EventQueue and be pending (ie in the process of notifying the
    //    ActionListener). If we didn't do this it would allow for a
    //    situation where the app is taking too long to process the
    //    actionPerformed, and thus we'ld end up queing a bunch of Runnables
    //    and the app would never return: not good. This of course implies
    //    you can get dropped events, but such is life.
    // notify is used to indicate if the ActionListener can be notified, when
    // the Runnable is processed if this is true it will notify the listeners.
    // notify is set to true when the Timer fires and the Runnable is queued.
    // It will be set to false after notifying the listeners (if coalesce is
    // true) or if the developer invokes stop.
    private boolean notify = false;

    int     initialDelay, delay;
    boolean repeats = true, coalesce = true;

    Runnable doPostEvent = null;

    private static boolean logTimers;


    // These fields are maintained by TimerQueue.
    // eventQueued can also be reset by the TimerQueue, but will only ever
    // happen in applet case when TimerQueues thread is destroyed.
    long    expirationTime;
    Timer   nextTimer;
    boolean running;


    /**
     * Creates a <code>Timer</code> that will notify its listeners every
     * <code>delay</code> milliseconds. If <code>delay</code> is less than
     * or equal to zero the timer will fire as soon as it
     * is started. If <code>listener</code> is not <code>null</code>,
     * it's registered as an action listener on the timer.
     *
     * @param delay the number of milliseconds between action events
     * @param listener  an initial listener; can be <code>null</code>
     *
     * @see #addActionListener
     * @see #setInitialDelay
     * @see #setRepeats
     */
    public Timer(int delay, ActionListener listener) {
        super();
        this.delay = delay;
        this.initialDelay = delay;

        doPostEvent = new DoPostEvent();

	if (listener != null) {
	    addActionListener(listener);
	}
    }


    /**
     * DoPostEvent is a runnable class that fires actionEvents to 
     * the listeners on the EventDispatchThread, via invokeLater.
     * @see #post
     */
    class DoPostEvent implements Runnable, Serializable
    {
        public void run() {
            if (logTimers) {
                System.out.println("Timer ringing: " + Timer.this);
            }
            if(notify) {
                fireActionPerformed(new ActionEvent(Timer.this, 0, null,
                                                    System.currentTimeMillis(),
                                                    0));
                if (coalesce) {
                    cancelEvent();
                }
            }
        }

        Timer getTimer() {
            return Timer.this;
        }
    }


    /**
     * Adds an action listener to the <code>Timer</code>.
     *
     * @param listener the listener to add
     * 
     * @see #Timer
     */
    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }


    /**
     * Removes the specified action listener from the <code>Timer</code>.
     *
     * @param listener the listener to remove
     */
    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }


    /**
     * Returns an array of all the action listeners registered
     * on this timer.
     *
     * @return all of the timer's <code>ActionListener</code>s or an empty
     *         array if no action listeners are currently registered
     *
     * @see #addActionListener
     * @see #removeActionListener
     *
     * @since 1.4
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[])listenerList.getListeners(
                ActionListener.class);
    }


    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  
     *
     * @param e the action event to fire
     * @see EventListenerList
     */
    protected void fireActionPerformed(ActionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i=listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s
     * upon this <code>Timer</code>.
     * <code><em>Foo</em>Listener</code>s
     * are registered using the <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a <code>Timer</code> 
     * instance <code>t</code>
     * for its action listeners
     * with the following code:
     *
     * <pre>ActionListener[] als = (ActionListener[])(t.getListeners(ActionListener.class));</pre>
     *
     * If no such listeners exist,
     * this method returns an empty array.
     *
     * @param listenerType  the type of listeners requested;
     *          this parameter should specify an interface
     *          that descends from <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s
     *          on this timer,
     *          or an empty array if no such 
     *		listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     *          specify a class or interface that implements 
     *		<code>java.util.EventListener</code>
     * 
     * @see #getActionListeners
     * @see #addActionListener
     * @see #removeActionListener
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) { 
	return listenerList.getListeners(listenerType); 
    }

    /**
     * Returns the timer queue.
     */
    TimerQueue timerQueue() {
        return TimerQueue.sharedInstance();
    }


    /**
     * Enables or disables the timer log. When enabled, a message
     * is posted to <code>System.out</code> whenever the timer goes off.
     *
     * @param flag  <code>true</code> to enable logging
     * @see #getLogTimers
     */
    public static void setLogTimers(boolean flag) {
        logTimers = flag;
    }


    /**
     * Returns <code>true</code> if logging is enabled.
     *
     * @return <code>true</code> if logging is enabled; otherwise, false
     * @see #setLogTimers
     */
    public static boolean getLogTimers() {
        return logTimers;
    }


    /**
     * Sets the <code>Timer</code>'s delay, the number of milliseconds
     * between successive action events.
     *
     * @param delay the delay in milliseconds
     * @see #setInitialDelay
     */
    public void setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Invalid delay: " + delay);
        }
        else {
            this.delay = delay;
        }
    }


    /**
     * Returns the delay, in milliseconds, 
     * between firings of action events.
     *
     * @see #setDelay
     * @see #getInitialDelay
     */
    public int getDelay() {
        return delay;
    }


    /**
     * Sets the <code>Timer</code>'s initial delay,
     * which by default is the same as the between-event delay.
     * This is used only for the first action event.
     * Subsequent action events are spaced
     * using the delay property.
     * 
     * @param initialDelay the delay, in milliseconds, 
     *                     between the invocation of the <code>start</code>
     *                     method and the first action event
     *                     fired by this timer
     *
     * @see #setDelay
     */
    public void setInitialDelay(int initialDelay) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: " +
                                               initialDelay);
        }
        else {
            this.initialDelay = initialDelay;
        }
    }


    /**
     * Returns the <code>Timer</code>'s initial delay.
     *
     * @see #setInitialDelay
     * @see #setDelay
     */
    public int getInitialDelay() {
        return initialDelay;
    }


    /**
     * If <code>flag</code> is <code>false</code>,
     * instructs the <code>Timer</code> to send only one
     * action event to its listeners.
     *
     * @param flag specify <code>false</code> to make the timer
     *             stop after sending its first action event
     */
    public void setRepeats(boolean flag) {
        repeats = flag;
    }


    /**
     * Returns <code>true</code> (the default)
     * if the <code>Timer</code> will send
     * an action event 
     * to its listeners multiple times.
     *
     * @see #setRepeats
     */
    public boolean isRepeats() {
        return repeats;
    }


    /**
     * Sets whether the <code>Timer</code> coalesces multiple pending
     * <code>ActionEvent</code> firings.
     * A busy application may not be able
     * to keep up with a <code>Timer</code>'s event generation,
     * causing multiple
     * action events to be queued.  When processed,
     * the application sends these events one after the other, causing the
     * <code>Timer</code>'s listeners to receive a sequence of
     * events with no delay between them. Coalescing avoids this situation
     * by reducing multiple pending events to a single event.
     * <code>Timer</code>s
     * coalesce events by default.
     *
     * @param flag specify <code>false</code> to turn off coalescing
     */
    public void setCoalesce(boolean flag) {
        boolean old = coalesce;
        coalesce = flag;
        if (!old && coalesce) {
            // We must do this as otherwise if the Timer once notified
            // in !coalese mode notify will be stuck to true and never
            // become false.
            cancelEvent();
        }
    }


    /**
     * Returns <code>true</code> if the <code>Timer</code> coalesces
     * multiple pending action events.
     *
     * @see #setCoalesce
     */
    public boolean isCoalesce() {
        return coalesce;
    }


    /**
     * Starts the <code>Timer</code>,
     * causing it to start sending action events
     * to its listeners.
     *
     * @see #stop
     */
    public void start() {
        timerQueue().addTimer(this,
                              System.currentTimeMillis() + getInitialDelay());
    }


    /**
     * Returns <code>true</code> if the <code>Timer</code> is running.
     *
     * @see #start
     */
    public boolean isRunning() {
        return timerQueue().containsTimer(this);
    }


    /**
     * Stops the <code>Timer</code>,
     * causing it to stop sending action events
     * to its listeners.
     *
     * @see #start
     */
    public void stop() {
        timerQueue().removeTimer(this);
        cancelEvent();
    }


    /**
     * Restarts the <code>Timer</code>,
     * canceling any pending firings and causing
     * it to fire with its initial delay.
     */
    public void restart() {
        stop();
        start();
    }


    /**
     * Resets the internal state to indicate this Timer shouldn't notify
     * any of its listeners. This does not stop a repeatable Timer from
     * firing again, use <code>stop</code> for that.
     */
    synchronized void cancelEvent() {
        notify = false;
    }


    synchronized void post() {
        if (notify == false || !coalesce) {
            notify = true;
            SwingUtilities.invokeLater(doPostEvent);
        }
    }
}
