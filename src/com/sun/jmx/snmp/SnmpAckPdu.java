/*
 * @(#)file      SnmpAckPdu.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.13
 * @(#)date      04/09/15
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
