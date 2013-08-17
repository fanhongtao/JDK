/*
 * @(#)SeedGenerator.java	1.4 97/12/11
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

package java.security;

/**
 * This class exports a single static method (genSeed) that generates
 * a seed for a cryptographically strong random number generator.  The
 * goal is to provide a seed whose least significant byte is "truly random."
 * The seed is produced by creating a "sleeper thread" that sleeps for a
 * designated time period, and spinning on Thread.yield() while waiting
 * for the sleeper thread to terminate.  The parent thread keeps track of
 * the number of times that it yields, and returns this "spin count"
 * as a seed.  Remarkably, the low order bits of this seed seem to be
 * quite random.
 *
 * @version 1.4, 00/08/15
 * @author Josh Bloch
 */

class SeedGenerator {
    private static int sleepTime;
    static {
	setSleepTime();
    }

    private static final int TARGET_SPIN_COUNT = 55000;
    private static final int MIN_SPIN_COUNT = (6*TARGET_SPIN_COUNT)/10;
    private static final int MAX_SPIN_COUNT = 2*TARGET_SPIN_COUNT;
    private static final int MAX_SLEEP_TIME = 30000;    // 30 seconds
    private static final int MAX_ATTEMPTS = 5;

    /**
     * This method calculates a sleep time that results in ~55000 thread
     * yields.  Experimentally, this seems sufficient to generate one random
     * byte.  Note that this  method (which "performs an experiment")requires
     * approximately one second to execute.  It is called once when the class
     * is loaded, and again if the computed sleep time "stops doing its job."
     * (This will happen if the load on the machine changes drastically.)
     *
     * If the machine is heavily loaded, the calculated sleepTime may
     * turn out to be quite high (possibly hours).  If such a value is
     * generated, reset sleepTime to a more reasonble time, MAX_SLEEP_TIME;
     */
    private static void setSleepTime() {
	sleepTime = (1000*TARGET_SPIN_COUNT)/genSeed(1000);
	if (sleepTime > MAX_SLEEP_TIME)
	    sleepTime = MAX_SLEEP_TIME;

	Security.debug("Resetting sleep time for seed generation: "
		       + sleepTime + " ms.");
     }

    /**
     * genSeed() - The sole exported method from this class; generates a
     * random seed, an integer whose last byte is intended to be "truly
     * random".  (Higher order bits may have some randomness as well.)
     * This method is synchronized for two reasons: 1) it has the side effect
     * of maintaining sleepTime, which is a static variable, and 2) having
     * multiple threads generate seeds concurrently would likely result in
     * unnecessary (and inefficent) increases in sleepTime.
     */
    public synchronized static int genSeed() {
	int candidate = genSeed(sleepTime);

	/*
	 * If candidate is way too low, recalculate sleep time until it
	 * isn't.  This is necessary to thwart an attack where our adversary
	 * loads down the machine in order to reduce the quality of the
	 * seed generation.
	 *
	 * Only try this MAX_ATTEMPTS number of times so that we don't
	 * get into an infinite loop.
	 */
	int attempts = 0;
	while (candidate < MIN_SPIN_COUNT && attempts < MAX_ATTEMPTS) {
	    Security.debug("Candidate seed too low: "+ candidate +" ms.");
	    setSleepTime();
	    candidate = genSeed(sleepTime);
	    attempts++;
	}

	if (attempts > MAX_ATTEMPTS)
	    throw new SecurityException("unable to generate a quality seed");

	/*
	 * If candidate is way too high, recalculate sleep time, but DO use
	 * the candidate (which is of HIGHER quality than necessary).  This
	 * step merely reduces the cost to calculate subsequent seed bytes.
	 * It isn't necessarily a win, as the recalculation is time consuming,
	 * but it prevents seed calculation time from unnecessarily
	 * "ratcheting up" over time.
	 */
	if (candidate > MAX_SPIN_COUNT) {
	    Security.debug("Candidate seed too high: "+ candidate +" ms.");
	    setSleepTime();
	}

	return candidate;
    }

    /**
     * Generate a random seed by spinning on thread yield while waiting for
     * a child thread that sleeps for the designated period of time, in
     * milliseconds.  The returned seed is the number of times we yield
     * whilst waiting for the child.
     */
    private static int genSeed(int sleepTime) {
	int counter = 0;

	Thread sleeper = new Sleeper(sleepTime);
	sleeper.start();

	while (sleeper.isAlive()) {
	    counter++;
	    Thread.yield();
	}

	return counter;
    }
}

/**
 * This helper class exports a "sleeper thread" that sleeps for a designated
 * period (in milliseconds) and terminates.
 */
class Sleeper extends Thread {
    private int sleepTime;

    Sleeper(int sleepTime) {
	this.sleepTime = sleepTime;
    }

    final public void run() {
	try {
	    Thread.sleep(sleepTime);
	} catch (InterruptedException e) {
	}
    }
}
