/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.rmi.server;

import java.io.*;

/**
 * Abstraction for creating identifiers that are unique with respect
 * to the the host on which it is generated.
 
 * @version 1.14, 02/06/02
 * @author  Ann Wollrath
 * @since   JDK1.1
 */
public final class UID implements java.io.Serializable {

    /**
     * @serial Integer that helps create a unique UID.
     */
    private int unique;

    /**
     * @serial Long used to record the time.  The <code>time</code>
     *         will be used to create a unique UID.
     */
    private long time;

    /**
     * @serial Short used to create a hash key for <code>this</code>
     *         UID.
     */
    private short count;

    /** indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 1086053664494604050L;

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
     * Creates a pure identifier that is unique with respect to the
     * host on which it is generated.  This UID is unique under the
     * following conditions: a) the machine takes more than one second
     * to reboot, and b) the machine's clock is never set backward.
     * In order to construct a UID that is globally unique, simply
     * pair a UID with an InetAddress.
     * @since JDK1.1
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
     * Creates a "well-known" ID.  There are 2^16 -1 such possible
     * well-known ids.  An id generated via this constructor will not
     * clash with any id generated via the default UID
     * constructor which will generates a genuinely unique identifier
     * with respect to this host.
     * @param num well known ID number
     * @since JDK1.1
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

    /**
     * Returns a hashcode for the <code>UID</code>.  Two <code>UID</code>s
     * will have the same hashcode if they are equal with respect to their
     * content.
     *
     * @return the hashcode
     * @see java.util.Hashtable
     * @since JDK1.1
     */
    public int hashCode() {
	return (int)time + (int)count;
    }

    /**
     * Compares two <code>UID</code>s for content equality.
     *
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     * @since JDK1.1
     */
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

    /**
     * Returns the string representation of the <code>UID</code>.
     * @since JDK1.1
     */
    public String toString() {
	return Integer.toString(unique,16) + ":" +
	    Long.toString(time,16) + ":" +
	    Integer.toString(count,16);
    }

    /**
     * Write the <code>UID</code> to output stream, <code>out</code>.
     *
     * @param out the output stream to which the <code>UID</code> is
     * written
     * @exception IOException if writing the <code>UID</code> to the
     * stream fails.
     * @since JDK1.1
     */
    public void write(DataOutput out) throws java.io.IOException
    {
	out.writeInt(unique);
	out.writeLong(time);
	out.writeShort(count);
    }

    /**
     * Reads the <code>UID</code> from the input stream.
     *
     * @param in the input stream
     * @return the <code>UID</code>
     * @exception IOException if uid could not be read
     * (due to stream failure or malformed <code>UID</code>)
     * @since JDK1.1
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
