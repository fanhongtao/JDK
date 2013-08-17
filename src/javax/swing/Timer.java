/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */



package javax.swing;



import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.event.EventListenerList;



/**
 * Causes an action to occur at a predefined rate.  For
 * example, an animation object can use a Timer as the trigger for drawing its
 * next frame.
 * For documentation and examples of using timers, see
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/timer.html">How to Use Timers</a>
 * in <em>The Java Tutorial.</em>
 *
 * <p>
 * Each Timer has a list of ActionListeners and a delay
 * (the time between <b>actionPerformed()</b> calls).  When
 * delay milliseconds have passed, a Timer sends the <b>actionPerformed()</b>
 * message to its listeners.  This cycle repeats until
 * <b>stop()</b> is called, or halts immediately if the Timer is configured
 * to send its message just once.<p>
 * Using a Timer involves first creating it, then starting it using
 * the <b>start()</b> method.
 *
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.33 02/06/02
 * @author Dave Moore
 */
public class Timer implements Serializable
{
    protected EventListenerList listenerList = new EventListenerList();

    boolean eventQueued = false;
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
     * Creates a Timer that will notify its listeners every
     * <i>delay</i> milliseconds.
     * @param delay The number of milliseconds between listener notification
     * @param listener  An initial listener
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
            if(eventQueued) {
                fireActionPerformed(new ActionEvent(Timer.this, 0, null));
                cancelEvent();
            }
        }

        Timer getTimer() {
            return Timer.this;
        }
    }


    /**
     * Adds an actionListener to the Timer
     */
    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }


    /**
     * Removes an ActionListener from the Timer.
     */
    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }


    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
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
     * Return an array of all the listeners of the given type that 
     * were added to this timer. 
     *
     * @returns all of the objects recieving <em>listenerType</em> notifications 
     *          from this timer
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
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
     * is posted to System.out whenever the timer goes off.
     *
     * @param flag  true to enable logging
     * @see #getLogTimers
     */
    public static void setLogTimers(boolean flag) {
        logTimers = flag;
    }


    /**
     * Returns true if logging is enabled.
     *
     * @return true if logging is enabled
     * @see #setLogTimers
     */
    public static boolean getLogTimers() {
        return logTimers;
    }


    /**
     * Sets the Timer's delay, the number of milliseconds between successive
     * <b>actionPerfomed()</b> messages to its listeners
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


    /** Returns the Timer's delay.
     * @see #setDelay
     */
    public int getDelay() {
        return delay;
    }


    /**
     * Sets the Timer's initial delay.  This will be used for the first
     * "ringing" of the Timer only.  Subsequent ringings will be spaced
     * using the delay property.
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
     * Returns the Timer's initial delay.
     * @see #setDelay
     */
    public int getInitialDelay() {
        return initialDelay;
    }


    /**
     * If <b>flag</b> is <b>false</b>, instructs the Timer to send
     * <b>actionPerformed()</b> to its listeners only once, and then stop.
     */
    public void setRepeats(boolean flag) {
        repeats = flag;
    }


    /**
     * Returns <b>true</b> if the Timer will send a <b>actionPerformed()</b>
     * message to its listeners multiple times.
     * @see #setRepeats
     */
    public boolean isRepeats() {
        return repeats;
    }


    /**
     * Sets whether the Timer coalesces multiple pending ActionEvent firings.
     * A busy application may not be able
     * to keep up with a Timer's message generation, causing multiple
     * <b>actionPerformed()</b> message sends to be queued.  When processed,
     * the application sends these messages one after the other, causing the
     * Timer's listeners to receive a sequence of <b>actionPerformed()</b>
     * messages with no delay between them. Coalescing avoids this situation
     * by reducing multiple pending messages to a single message send. Timers
     * coalesce their message sends by default.
     */
    public void setCoalesce(boolean flag) {
        coalesce = flag;
    }


    /**
     * Returns <b>true</b> if the Timer coalesces multiple pending
     * <b>performCommand()</b> messages.
     * @see #setCoalesce
     */
    public boolean isCoalesce() {
        return coalesce;
    }


    /**
     * Starts the Timer, causing it to send <b>actionPerformed()</b> messages
     * to its listeners.
     * @see #stop
     */
    public void start() {
        timerQueue().addTimer(this,
                              System.currentTimeMillis() + getInitialDelay());
    }


    /**
     * Returns <b>true</b> if the Timer is running.
     * @see #start
     */
    public boolean isRunning() {
        return timerQueue().containsTimer(this);
    }


    /**
     * Stops a Timer, causing it to stop sending <b>actionPerformed()</b>
     * messages to its Target.
     * @see #start
     */
    public void stop() {
        timerQueue().removeTimer(this);
        cancelEvent();
    }


    /**
     * Restarts a Timer, canceling any pending firings, and causing
     * it to fire with its initial dely.
     */
    public void restart() {
        stop();
        start();
    }


    synchronized void cancelEvent() {
        eventQueued = false;
    }


    synchronized void post() {
        if (eventQueued == false) {
            eventQueued = true;
            SwingUtilities.invokeLater(doPostEvent);
        }
    }
}
