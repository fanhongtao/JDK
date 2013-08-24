/*
 * @(#)UNSUPPORTED_POLICY_VALUE.java	1.12 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * A <tt>PolicyErrorCode</tt> which would be filled if the value
 * requested for the <tt>Policy</tt> is of a
 * valid type and within the valid range for that type, but this valid value
 * is not currently supported.
 *
 * @author rip-dev
 * @version 1.12 11/17/05
 */
public interface UNSUPPORTED_POLICY_VALUE {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (4L);
};
