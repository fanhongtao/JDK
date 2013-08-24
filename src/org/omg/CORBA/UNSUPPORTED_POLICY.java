/*
 * @(#)UNSUPPORTED_POLICY.java	1.13 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * One of the <tt>PolicyErrorCode</tt>s which would be filled if 
 * the requested <tt>Policy</tt> is understood to be valid by the
 * ORB, but is not currently supported.
 *
 * @author rip-dev
 * @version 1.13 11/17/05
 */
public interface UNSUPPORTED_POLICY {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (1L);
};
