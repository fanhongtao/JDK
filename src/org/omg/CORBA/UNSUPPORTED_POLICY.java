/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.8 02/06/02
 */
public interface UNSUPPORTED_POLICY {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (1L);
};
