/*
 * @(#)file      SnmpCounter.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   4.9
 * @(#)date      06/11/29
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


package com.sun.jmx.snmp;



/**
 * Represents an SNMP counter.
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @version     4.9     11/17/05
 * @author      Sun Microsystems, Inc
 */

public class SnmpCounter extends SnmpUnsignedInt {

    // CONSTRUCTORS
    //-------------
    /**
     * Constructs a new <CODE>SnmpCounter</CODE> from the specified integer value.
     * @param v The initialization value.
     * @exception IllegalArgumentException The specified value is negative
     * or larger than {@link SnmpUnsignedInt#MAX_VALUE SnmpUnsignedInt.MAX_VALUE}. 
     */
    public SnmpCounter(int v) throws IllegalArgumentException {
	super(v) ;
    }

    /**
     * Constructs a new <CODE>SnmpCounter</CODE> from the specified <CODE>Integer</CODE> value.
     * @param v The initialization value.
     * @exception IllegalArgumentException The specified value is negative
     * or larger than {@link SnmpUnsignedInt#MAX_VALUE SnmpUnsignedInt.MAX_VALUE}. 
     */
    public SnmpCounter(Integer v) throws IllegalArgumentException {
	super(v) ;
    }

    /**
     * Constructs a new <CODE>SnmpCounter</CODE> from the specified long value.
     * @param v The initialization value.
     * @exception IllegalArgumentException The specified value is negative
     * or larger than {@link SnmpUnsignedInt#MAX_VALUE SnmpUnsignedInt.MAX_VALUE}. 
     */
    public SnmpCounter(long v) throws IllegalArgumentException {
	super(v) ;
    }

    /**
     * Constructs a new <CODE>SnmpCounter</CODE> from the specified <CODE>Long</CODE> value.
     * @param v The initialization value.
     * @exception IllegalArgumentException The specified value is negative
     * or larger than {@link SnmpUnsignedInt#MAX_VALUE SnmpUnsignedInt.MAX_VALUE}. 
     */
    public SnmpCounter(Long v) throws IllegalArgumentException {
	super(v) ;
    }

    // PUBLIC METHODS
    //---------------
    /**
     * Returns a textual description of the type object.
     * @return ASN.1 textual description.
     */
    final public String getTypeName() {
	return name ;
    }

    // VARIABLES
    //----------
    /**
     * Name of the type.
     */
    final static String name = "Counter32" ;
}
