/*
 * @(#)UNSUPPORTED_POLICY.java	1.7 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package org.omg.CORBA;

/**
 * UNSUPPORTED_POLICY is one of the PolicyErrorCodes which would be filled in
 * the PolicyError exception.
 *
 * UNSUPPORTED_POLICY - the requested Policy is understood to be valid by the
 * ORB, but is not currently supported.
 *
 * @author rip-dev
 * @version 1.7 02/02/00
 */
public interface UNSUPPORTED_POLICY {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (1L);
};
