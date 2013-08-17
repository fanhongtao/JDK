/*
 * @(#)UID.java	1.6 98/08/12
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.rmi.server;

import java.io.*;

/**
 * Abstraction for creating identifiers that are unique with respect
 * to the the host on which it is generated.
 */
public final class UID implements java.io.Serializable {

    private static final long serialVersionUID = 1086053664494604050L;

    private int unique;
    private long time;
    private short count;

    /**
     * In the absence of an actual pid, just do something somewhat
     * random.
     */
    private static int getHostUniqueNum() {
	return (new Object()).hashCode();
    }
    
    private static int hostUnique = getHostUniqueNum();
    private static long lastTime = System.currentTimeMillis();
    private static short lastCount = Short.MIN_VALUE;
    private static Object mutex = new Object();
    private static long  ONE_SECOND = 1000; // in milliseconds
    
    /**
     * Create a pure identifier that is unique with respect to the
     * host on which it is generated.  This UID is unique under the
     * following conditions: a) the machine takes more than one second
     * to reboot, and b) the machine's clock is never set backward.
     * In order to construct a UID that is globally unique, simply
     * pair a UID with an InetAddress.
     */
    public UID() {
	
	synchronized (mutex) {
	    if (lastCount == Short.MAX_VALUE) {
		boolean done = false;
		while (!done) {
		    time = System.currentTimeMillis();
		    if (time < lastTime+ONE_SECOND) {
			// pause for a second to wait for time to change
			try {
			    Thread.currentThread().sleep(ONE_SECOND);
			} catch (java.lang.InterruptedException e) {
			}	// ignore exception
			continue;
		    } else {
			lastTime = time;
			lastCount = Short.MIN_VALUE;
			done = true;
		    }
		}
	    } else {
		time = lastTime;
	    }
	    unique = hostUnique;
	    count = lastCount++;
	}
    }

    /**
     * Create a "well-known" ID.  There are 2^16 -1 such possible
     * well-known ids.  An id generated via this constructor will not
     * clash with any id generated via the default UID
     * constructor which will generates a genuinely unique identifier
     * with respect to this host.
     */
    public UID(short num) 
    {
	unique = 0;
	time = 0;
	count = num;
    }

    private UID(int unique, long time, short count) 
    {
	this.unique = unique;
	this.time = time;
	this.count = count;
    }

    public int hashCode() {
	return (int)time + (int)count;
    }

    public boolean equals(Object obj) {
	if ((obj != null) && (obj instanceof UID)) {
	    UID uid = (UID)obj;
	    return (unique == uid.unique &&
		    count == uid.count &&
		    time == uid.time);
	} else {
	    return false;
	}
    }
	
    public String toString() {
	return Integer.toString(unique,16) + ":" +
	    Long.toString(time,16) + ":" +
	    Integer.toString(count,16);
    }
    
    /**
     * Write uid to output stream.
     */
    public void write(DataOutput out) throws java.io.IOException
    {
	out.writeInt(unique);
	out.writeLong(time);
	out.writeShort(count);
    }
    
    /**
     * Get the uid from the input stream.
     * @param in the input stream
     * @exception IOException If uid could not be read
     * (due to stream failure or malformed uid)
     */
    public static UID read(DataInput in)
	throws java.io.IOException
    {
	int unique = in.readInt();
	long time = in.readLong();
	short count = in.readShort();
	return new UID(unique, time, count);
    }
}
