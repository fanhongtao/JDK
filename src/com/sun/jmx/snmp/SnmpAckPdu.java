/*
 * @(#)file      SnmpAckPdu.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.15
 * @(#)date      10/07/17
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.sun.jmx.snmp;
/**
 * Interface to be implemented by PDUs that are acknowledged (eg:
 * request, bulk).
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @since 1.5
 */
public interface SnmpAckPdu {
    /**
     * Returns the PDU to use for the response.
     * @return The response PDU.
     */
    public SnmpPdu getResponsePdu();
}
