/*
 * @(#)UNSUPPORTED_POLICY.java	1.14 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * One of the <tt>PolicyErrorCode</tt>s which would be filled if 
 * the requested <tt>Policy</tt> is understood to be valid by the
 * ORB, but is not currently supported.
 *
 * @author rip-dev
 * @version 1.14 03/23/10
 */
public interface UNSUPPORTED_POLICY {
    /**
     *  The Error code for PolicyError exception.
     */
    final short value = (short) (1L);
};
