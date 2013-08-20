/*
 * @(#)file      SnmpUnknownModelException.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.12
 * @(#)date      04/09/15
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.sun.jmx.snmp;
/**
 * This exception is thrown when a needed model is not present in the engine.
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @since 1.5
 */
public class SnmpUnknownModelException extends Exception {
    /**
     * Constructor. 
     * @param msg The exception msg to display.
     */
    public SnmpUnknownModelException(String msg) {
	super(msg);
    }
}
