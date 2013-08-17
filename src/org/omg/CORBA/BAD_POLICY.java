/*
 * @(#)BAD_POLICY.java	1.7 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;
/**
 * BAD_POLICY is one of the PolicyErrorCodes which would be filled in
 * the PolicyError exception.
 *
 * @author rip-dev
 * @version 1.7 02/02/00
 */

public interface BAD_POLICY {
    /**
     * The Error code in PolicyError exception.
     */
    final short value = (short) (0L);
};
