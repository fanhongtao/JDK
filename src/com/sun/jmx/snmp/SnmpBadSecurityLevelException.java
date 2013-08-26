/*
 * @(#)file      SnmpBadSecurityLevelException.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.16
 * @(#)date      10/07/17
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
