/*
 * @(#)SeedGenerator.java	1.10 99/02/09
 *
 * Copyright 1995-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.security;

/**
 * <P> This class generates seeds for the cryptographically strong random
 * number generator.
 * <P> The seed is produced by counting the number of times the VM
 * manages to loop in a given period. This number roughly
 * reflects the machine load at that point in time.
 * The samples are translated using a permutation (s-box)
 * and then XORed together. This process is non linear and
 * should prevent the samples from "averaging out". The s-box
 * was designed to have even statistical distribution; it's specific
 * values are not crucial for the security of the seed.
 * We also create a number of sleeper threads which add entropy
 * to the system by keeping the scheduler busy.
 * Twenty such samples should give us roughly 160 bits of randomness.
 * <P> These values are gathered in the background by a daemon thread
 * thus allowing the system to continue performing it's different
 * activites, which in turn add entropy to the random seed.
 * <p> The class also gathers miscellaneous system information, some
 * machine dependent, some not. This information is then hashed together
 * with the 20 seed bytes.
 *
 * @version 1.10, 99/02/09
 * @author Joshua Bloch
 * @author Gadi Guy
 */

import java.security.*;
import java.io.*;
import java.util.Properties;
import java.util.Enumeration;
import java.net.*;

class SeedGenerator implements Runnable {
    // Static instance is created at link time
    private static SeedGenerator myself = new SeedGenerator();

    // Queue is used to collect seed bytes
    private byte[] pool;
    private int start, end, count;

    // Thread group for our threads
    ThreadGroup seedGroup;

    /**
     * The constructor is only called once to construct the one
     * instance we actually use. It instantiates the message digest
     * and starts the thread going.
     */

    private SeedGenerator() {
	pool = new byte[20];
	start = end = 0;

	Thread t = null;
	MessageDigest digest;

	try {
    	    digest = MessageDigest.getInstance("SHA");
    	} catch (NoSuchAlgorithmException e) {
    	    throw new InternalError("internal error: SHA-1 not available.");
	}

	ThreadGroup parent, group = Thread.currentThread().getThreadGroup();
	while ((parent = group.getParent()) != null)
	    group = parent;
	seedGroup = new ThreadGroup(group, "SeedGenerator ThreadGroup");
	t = new Thread(seedGroup, this, "SeedGenerator Thread");
	t.setPriority(Thread.MIN_PRIORITY);
	t.setDaemon(true);
	t.start();
    }

    /**
     * This method does the actual work. It collects random bytes and
     * pushes them into the queue.
     */

    final public void run() {
	try {
	    while (true) {
		// Queue full? Wait till there's room.
		synchronized(this) {
		    while (count >= pool.length)
			wait();
		}

		int counter, quanta;
		byte v = 0;

		// Spin count must not be under 64000
		for (counter = quanta = 0; (counter < 64000) && (quanta < 6);
							quanta++) {

		    // Start some noisy threads
		    try {
			BogusThread bt = new BogusThread();
			Thread t = new Thread
			    (seedGroup, bt, "SeedGenerator Thread");
			t.start();
		    } catch (Exception e) {
			throw new InternalError("internal error: " +
			    "SeedGenerator thread creation error.");
		    }

		    // We wait 250milli quanta, so the minimum wait time
		    // cannot be under 250milli.
		    int latch = 0;
		    latch = 0;
		    long l = System.currentTimeMillis() + 250;
		    while (System.currentTimeMillis() < l) {
			synchronized(this){};
			latch++;
		    }

		    // Translate the value using the permutation, and xor
		    // it with previous values gathered.
		    v ^= rndTab[latch % 255];
		    counter += latch;
		}

		// Push it into the queue and notify anybody who might
		// be waiting for it.
		synchronized(this) {
		    pool[end] = v;
		    end++;
		    count++;
		    if (end >= pool.length)
			end = 0;

		    notifyAll();
		}
	    }
	} catch (Exception e) {
    	    throw new InternalError("internal error: " +
		"SeedGenerator thread generated an exception.");
	}
    }

    /**
    * Return a byte from the queue. Wait for it if it isn't ready.
    */

    static public byte getByte() {
	return myself._getByte();
    }

    private byte _getByte() {
	byte b = 0;

	try {
	    // Wait for it...
	    synchronized(this) {
		while (count <= 0)
		    wait();
	    }
	} catch (Exception e) {
	    if (count <= 0)
		throw new InternalError("internal error: " +
		    "SeedGenerator thread generated an exception.");
	}

	synchronized(this) {
	    // Get it from the queue
	    b = pool[start];
	    pool[start] = 0;
	    start++;
	    count--;
	    if (start == pool.length)
		start = 0;

	   // Notify the daemon thread, just in case it is
	   // waiting for us to make room in the queue.
	    notifyAll();
	}

	return b;
    }

    /**
     * Retrieve some system information, hashed.
     */

    static byte[] getSystemEntropy() {
	String s;
	String[] sa;
	byte b;
	Properties p;
	Enumeration e;
	File f;
	MessageDigest md;

	try {
    	    md = MessageDigest.getInstance("SHA");
    	} catch (NoSuchAlgorithmException nsae) {
    	    throw new InternalError("internal error: SHA-1 not available.");
	}

	// The current time in millis
	b =(byte)System.currentTimeMillis();
	md.update(b);

	try {
	    // System properties can change from machine to machine
	    p = System.getProperties();
	    e = p.propertyNames();
	    while (e.hasMoreElements()) {
		s =(String)e.nextElement();
		md.update(s.getBytes());
		md.update(p.getProperty(s).getBytes());
	    }

	    md.update(InetAddress.getLocalHost().toString().getBytes());

	    // The temporary dir
	    f = new File(p.getProperty("java.io.tmpdir"));
	    sa = f.list();
	    for(int i = 0; i < sa.length; i++)
		md.update(sa[i].getBytes());
	} catch (Exception ex) {
	    md.update((byte)ex.hashCode());
	}

	// get Runtime memory stats
	Runtime rt = Runtime.getRuntime();
	byte[] memBytes = longToByteArray(rt.totalMemory());
	md.update(memBytes, 0, memBytes.length);
	memBytes = longToByteArray(rt.freeMemory());
	md.update(memBytes, 0, memBytes.length);

	return md.digest();
    }

    /**
     * Helper function to convert a long into a byte array (least significant
     * byte first).
     */
    private static byte[] longToByteArray(long l) {
	byte[] retVal = new byte[8];
 
	for (int i=0; i<8; i++) {
	    retVal[i] = (byte) l;
	    l >>= 8;
	}
 
	return retVal;
    }

    /*
    // This method helps the test utility receive unprocessed seed bytes.
    public static int genTestSeed() {
	return myself.getByte();
    }
    */

    // The permutation was calculated by generating 64k of random
    // data and using it to mix the trivial permutation.
    // It should be evenly distributed. The specific values
    // are not crucial to the security of this class.
    private static byte[] rndTab = {
	56, 30, -107, -6, -86, 25, -83, 75, -12, -64,
	5, -128, 78, 21, 16, 32, 70, -81, 37, -51,
	-43, -46, -108, 87, 29, 17, -55, 22, -11, -111,
	-115, 84, -100, 108, -45, -15, -98, 72, -33, -28,
	31, -52, -37, -117, -97, -27, 93, -123, 47, 126,
	-80, -62, -93, -79, 61, -96, -65, -5, -47, -119,
	14, 89, 81, -118, -88, 20, 67, -126, -113, 60,
	-102, 55, 110, 28, 85, 121, 122, -58, 2, 45,
	43, 24, -9, 103, -13, 102, -68, -54, -101, -104,
	19, 13, -39, -26, -103, 62, 77, 51, 44, 111,
	73, 18, -127, -82, 4, -30, 11, -99, -74, 40,
	-89, 42, -76, -77, -94, -35, -69, 35, 120, 76,
	33, -73, -7, 82, -25, -10, 88, 125, -112, 58,
	83, 95, 6, 10, 98, -34, 80, 15, -91, 86,
	-19, 52, -17, 117, 49, -63, 118, -90, 36, -116,
	-40, -71, 97, -53, -109, -85, 109, -16, -3, 104,
	-95, 68, 54, 34, 26, 114, -1, 106, -121, 3,
	66, 0, 100, -84, 57, 107, 119, -42, 112, -61,
	1, 48, 38, 12, -56, -57, 39, -106, -72, 41,
	7, 71, -29, -59, -8, -38, 79, -31, 124, -124,
	8, 91, 116, 99, -4, 9, -36, -78, 63, -49,
	-67, -87, 59, 101, -32, 92, 94, 53, -41, 115,
	-66, -70, -122, 50, -50, -22, -20, -18, -21, 23,
	-2, -48, 96, 65, -105, 123, -14, -110, 69, -24,
	-120, -75, 74, 127, -60, 113, 90, -114, 105, 46,
	27, -125, -23, -44, 64
    };

    /**
     * This inner thread causes the thread scheduler to become 'noisy',
     * thus adding entropy to the system load.
     * At least one instance of this class is generated for every seed byte.
     */

    private class BogusThread implements Runnable {
	final public void run() {
	    try {
		for(int i = 0; i < 5; i++)
		   Thread.sleep(50);
		System.gc();
	    } catch (Exception e) {
	    }
	}
    }
}
