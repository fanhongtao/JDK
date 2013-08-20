/*
 * @(#)file      SnmpBadSecurityLevelException.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.14
 * @(#)date      04/09/15
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.sun.jmx.snmp;
/**
 * This exception is thrown when an incorrect security level is handled.
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @since 1.5
 */
public class SnmpBadSecurityLevelException extends Exception {
    public SnmpBadSecurityLevelException(String msg) {
	super(msg);
    }
}
