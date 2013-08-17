/*
 * @(#)UNSUPPORTED_POLICY_VALUE.java	1.6 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/**
 * UNSUPPORTED_POLICY_VALUE is one of the PolicyErrorCodes which would be filled in
 * the PolicyError exception.
 *
 * UNSUPPORTED_POLICY_VALUE - The value requested for the Policy is of a
 * valid type and within the valid range for that type, but this valid value
 * is not currently supported.
 *
 * @author rip-dev
 * @version 1.6 02/02/00
 */
public interface UNSUPPORTED_POLICY_VALUE {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (4L);
};
