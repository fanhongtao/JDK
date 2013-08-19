/*
 * @(#)ORBVersionImpl.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

import org.omg.CORBA.portable.OutputStream ;

public class ORBVersionImpl implements ORBVersion {
    private byte orbType ;

    private ORBVersionImpl( byte orbType )
    {
	this.orbType = orbType ;
    }

    public static final ORBVersion FOREIGN = new ORBVersionImpl(
	ORBVersion.FOREIGN ) ;

    public static final ORBVersion OLD = new ORBVersionImpl(
	ORBVersion.OLD ) ;

    public static final ORBVersion NEW = new ORBVersionImpl(
	ORBVersion.NEW ) ;

    public static final ORBVersion JDK1_3_1_01 = new ORBVersionImpl(
        ORBVersion.JDK1_3_1_01 ) ;

    public static final ORBVersion NEWER = new ORBVersionImpl(
	ORBVersion.NEWER ) ;


    public byte getORBType()
    {
	return orbType ;
    }

    public void write( OutputStream os )
    {
	os.write_octet( (byte)orbType ) ;
    }

    public String toString()
    {
	return "ORBVersionImpl[" + Byte.toString( orbType ) + "]" ;
    }

    public boolean equals( Object obj )
    {
	if (!(obj instanceof ORBVersion))
	    return false ;

	ORBVersion version = (ORBVersion)obj ;
	return version.getORBType() == orbType ;
    }

    public boolean lessThan(ORBVersion version) {
        return orbType < version.getORBType();
    }

    public int compareTo(Object obj) {
        // The Comparable interface says that this
        // method throws a ClassCastException if the
        // given object's type prevents it from being
        // compared.
        return getORBType() - ((ORBVersion)obj).getORBType();
    }
}
