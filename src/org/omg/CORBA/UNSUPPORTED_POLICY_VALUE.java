/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.7 02/06/02
 */
public interface UNSUPPORTED_POLICY_VALUE {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (4L);
};
