/*
 * @(#)file      SnmpPduBulkType.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.14
 * @(#)date      06/11/29
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.sun.jmx.snmp;
/**
 * Interface implemented by classes modelizing bulk pdu.
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @since 1.5
 */

public interface SnmpPduBulkType extends SnmpAckPdu {
    
    /**
     * The <CODE>max-repetitions</CODE> setter.
     * @param max Maximum repetition.
     */
    public void setMaxRepetitions(int max);
    
    /**
     * The <CODE>non-repeaters</CODE> setter.
     * @param nr Non repeaters.
     */
    public void setNonRepeaters(int nr);

    /**
     * The <CODE>max-repetitions</CODE> getter.
     * @return Maximum repetition.
     */
    public int getMaxRepetitions();
    
    /**
     * The <CODE>non-repeaters</CODE> getter.
     * @return Non repeaters.
     */
    public int getNonRepeaters();
}
