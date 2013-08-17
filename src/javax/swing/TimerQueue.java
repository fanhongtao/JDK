/*
 * @(#)TimerQueue.java	1.25 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


/**
 * Internal class to manage all Timers using one thread.
 *
 *
 * @version 1.25 08/26/98
 * @author Dave Moore
 */

package javax.swing;

import java.util.*;

/** Private class to manage a queue of Timers. The Timers are chained
  * together in a linked list sorted by the order in which they will expire.
  */
class TimerQueue implements Runnable {
    Timer firstTimer;
    boolean running;

    private static final Object sharedInstanceKey = new StringBuffer("TimerQueue.sharedInstanceKey");
    private static final Object expiredTimersKey = new StringBuffer("TimerQueue.expiredTimersKey");

    public TimerQueue() {
        super();

        // Now start the TimerQueue thread.
        start();
    }

    public static TimerQueue sharedInstance() {
        synchronized (TimerQueue.class) {
            TimerQueue sharedInst = (TimerQueue)
                SwingUtilities.appContextGet(sharedInstanceKey);
            if (sharedInst == null) {
                sharedInst = new TimerQueue();
                SwingUtilities.appContextPut(sharedInstanceKey, 
                                             sharedInst);
            }
            return sharedInst;
        }
    }

    synchronized void start() {
        if (running) {
            throw new RuntimeException("Can't start a TimerQueue that is already running");
        } else {
	    Thread timerThread = new Thread(this, "TimerQueue");

	    try {
		timerThread.setDaemon(true);
	    } catch (SecurityException e) {
	    }
	    timerThread.start();
            running = true;
        }
    }

    synchronized void stop() {
        running = false;
        notify();
    }

    synchronized void addTimer(Timer timer, long expirationTime) {
        Timer previousTimer, nextTimer;

        // If the Timer is already in the queue, then ignore the add.
        if (timer.running) {
            return;
	}

        previousTimer = null;
        nextTimer = firstTimer;

        // Insert the Timer into the linked list in the order they will
        // expire.  If two timers expire at the same time, put the newer entry
        // later so they expire in the order they came in.

        while (nextTimer != null) {
            if (nextTimer.expirationTime > expirationTime)
                break;

            previousTimer = nextTimer;
            nextTimer = nextTimer.nextTimer;
        }

        if (previousTimer == null)
            firstTimer = timer;
        else
            previousTimer.nextTimer = timer;

        timer.expirationTime = expirationTime;
        timer.nextTimer = nextTimer;
        timer.running = true;
        notify();
    }

    synchronized void removeTimer(Timer timer) {
        boolean found;
        Timer previousTimer, nextTimer;

        if (!timer.running)
            return;

        previousTimer = null;
        nextTimer = firstTimer;
        found = false;

        while (nextTimer != null) {
            if (nextTimer == timer) {
                found = true;
                break;
            }

            previousTimer = nextTimer;
            nextTimer = nextTimer.nextTimer;
        }

        if (!found)
            return;

        if (previousTimer == null)
            firstTimer = timer.nextTimer;
        else
            previousTimer.nextTimer = timer.nextTimer;

        timer.expirationTime = 0;
        timer.nextTimer = null;
        timer.running = false;
    }

    synchronized boolean containsTimer(Timer timer) {
        return timer.running;
    }

    // If there are a ton of timers, this method may never return.  It loops
    // checking to see if the head of the Timer list has expired.  If it has,
    // it posts the Timer and reschedules it if necessary.

    synchronized long postExpiredTimers() {
        long currentTime, timeToWait;
        Timer timer;

        // The timeToWait we return should never be negative and only be zero
        // when we have no Timers to wait for.

        do {
            timer = firstTimer;
            if (timer == null)
                return 0;

            currentTime = System.currentTimeMillis();
            timeToWait = timer.expirationTime - currentTime;

            if (timeToWait <= 0) {
		try {
		    timer.post();  // have timer post an event
		} catch (SecurityException e) {}

		// remove the timer from the queue
		removeTimer(timer);
		
		// This tries to keep the interval uniform at the cost of
		// drift.
		if (timer.isRepeats()) {
		    addTimer(timer, currentTime + timer.getDelay());
		}
	    }
	    
	    // Allow other threads to call addTimer() and removeTimer()
	    // even when we are posting Timers like mad.  Since the wait()
	    // releases the lock, be sure not to maintain any state
	    // between iterations of the loop.
	    
	    try {
		wait(1);
	    } catch (InterruptedException e) {
	    }
	} while (timeToWait <= 0);
	
	return timeToWait;
    }
    
    public synchronized void run() {
        long timeToWait;

	try {
	    while (running) {
		timeToWait = postExpiredTimers();
		try {
		    wait(timeToWait);
		}
		catch (InterruptedException e) { }
	    }
	}
	catch (ThreadDeath td) {
	    running = false;
	    // Mark all the timers we contain as not being queued.
	    Timer timer = firstTimer;
	    while(timer != null) {
		timer.eventQueued = false;
		timer = timer.nextTimer;
	    }
	    SystemEventQueueUtilities.restartTimerQueueThread();
	    throw td;
	}
    }

    public synchronized String toString() {
        StringBuffer buf;
        Timer nextTimer;

        buf = new StringBuffer();
        buf.append("TimerQueue (");

        nextTimer = firstTimer;
        while (nextTimer != null) {
            buf.append(nextTimer.toString());

            nextTimer = nextTimer.nextTimer;
            if (nextTimer != null)
                buf.append(", ");
        }

        buf.append(")");
        return buf.toString();
    }

}
