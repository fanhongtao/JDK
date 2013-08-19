/*
 * @(#)GIOPVersion.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

public class GIOPVersion {

    // Static fields

    public static final GIOPVersion V1_0 = new GIOPVersion((byte)1, (byte)0);
    public static final GIOPVersion V1_1 = new GIOPVersion((byte)1, (byte)1);
    public static final GIOPVersion V1_2 = new GIOPVersion((byte)1, (byte)2);

    public static final GIOPVersion DEFAULT_VERSION = V1_2;

    public static final int VERSION_1_0 = 0x0100;
    public static final int VERSION_1_1 = 0x0101;
    public static final int VERSION_1_2 = 0x0102;

    // Instance variables

    public byte major = (byte) 0;
    public byte minor = (byte) 0;

    // Constructor

    public GIOPVersion() {}

    public GIOPVersion(byte majorB, byte minorB) {
        this.major = majorB;
        this.minor = minorB;
    }

    public GIOPVersion(int major, int minor) {
        this.major = (byte)major;
        this.minor = (byte)minor;
    }

    // Accessor methods

    public byte getMajor() {
        return this.major;
    }

    public byte getMinor() {
        return this.minor;
    }

    // General methods

    public boolean equals(GIOPVersion gv){
        return gv.major == this.major && gv.minor == this.minor ;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof GIOPVersion))
            return equals((GIOPVersion)obj);
        else
            return false;
    }

    public boolean lessThan(GIOPVersion gv) {
        if (this.major < gv.major) {
            return true;
        } else if (this.major == gv.major) {
            if (this.minor < gv.minor) {
                return true;
            }
        }

        return false;
    }

    public int intValue()
    {
        return (major << 8 | minor);
    }

    public String toString()
    {
        return major + "." + minor;
    }

    public static GIOPVersion getInstance(byte major, byte minor)
    {
        switch(((major << 8) | minor)) {
            case VERSION_1_0:
                return GIOPVersion.V1_0;
            case VERSION_1_1:
                return GIOPVersion.V1_1;
            case VERSION_1_2:
                return GIOPVersion.V1_2;
             default:
                return new GIOPVersion(major, minor);
        }
    }

    public static GIOPVersion parseVersion(String s)
    {
        int dotIdx = s.indexOf('.');

        if (dotIdx < 1 || dotIdx == s.length() - 1)
            throw new NumberFormatException("GIOP major, minor, and decimal point required: " + s);

        int major = Integer.parseInt(s.substring(0, dotIdx));
        int minor = Integer.parseInt(s.substring(dotIdx + 1, s.length()));

        return GIOPVersion.getInstance((byte)major, (byte)minor);
    }

    /**
     * This computes the smallest among the iorGIOPVersion and orbGIOPVersion.
     *
     * @return the smallest(iorGIOPVersion, orbGIOPVersion)
     */
    public static GIOPVersion chooseRequestVersion(ORB orb, IOR ior) {

        GIOPVersion orbVersion = orb.getGIOPVersion();
        GIOPVersion iorVersion = ior.getGIOPVersion();

        // Check if the IOR is from a legacy Sun ORB.

        ORBVersionImpl targetOrbVersion = (ORBVersionImpl) ior.getORBVersion();
        if (!(targetOrbVersion.equals(ORBVersionImpl.FOREIGN)) &&
                targetOrbVersion.lessThan(ORBVersionImpl.NEWER)) {
            // we are dealing with a SUN legacy orb which emits 1.1 IORs,
            // in spite of being able to handle only GIOP 1.0 messages.
            return V1_0;
        }

        // Now the target has to be (FOREIGN | NEWER*)

        byte ior_major = iorVersion.getMajor();
        byte ior_minor = iorVersion.getMinor();

        byte orb_major = orbVersion.getMajor();
        byte orb_minor = orbVersion.getMinor();

        if (orb_major < ior_major) {
            return orbVersion;
        } else if (orb_major > ior_major) {
            return iorVersion;
        } else { // both major version are the same
            if (orb_minor <= ior_minor) {
                return orbVersion;
            } else {
                return iorVersion;
            }
        }
    }

    /* Could be useful for Messages?
    public GIOPVersion(Message m){
        this( m.getGIOPMajorVersion() , m.getGIOPMinorVersion());
    }
    public boolean hasSameGIOPVersion(Message m){
        return this.equals(m.getGIOPVersion());
    }
    */

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        this.major = istream.read_octet();
        this.minor = istream.read_octet();
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        ostream.write_octet(this.major);
        ostream.write_octet(this.minor);
    }
}
