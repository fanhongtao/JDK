/*
 * @(#)file      SnmpModel.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.16
 * @(#)date      06/11/29
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.sun.jmx.snmp.internal;
/**
 * Interface that every SNMP model must implement in order to be integrated in the engine framework.
 * <p><b>This API is a Sun Microsystems internal API  and is subject 
 * to change without notice.</b></p>
 * @since 1.5
 */
public interface SnmpModel {
    
    /**
     * Returns the sub system that manages this model.
     * @return The sub system.
     */
    public SnmpSubSystem getSubSystem();
    /**
     * A human readable model name.
     * @return The model name.
     */
    public String getName();
}
