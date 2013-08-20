/*
 * @(#)file      SnmpUnknownSecModelException.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.13
 * @(#)date      04/09/15
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.sun.jmx.snmp;

import com.sun.jmx.snmp.SnmpUnknownModelException;

/**
 * This exception is thrown when an <CODE>SnmpSecuritySubSystem</CODE> doesn't know the passed ID.
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @since 1.5
 */
public class SnmpUnknownSecModelException extends SnmpUnknownModelException {
    /**
     * Constructor. 
     * @param msg The exception msg to display.
     */
    public SnmpUnknownSecModelException(String msg) {
	super(msg);
    }
}
