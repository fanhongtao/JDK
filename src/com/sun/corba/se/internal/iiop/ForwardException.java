/*
 * @(#)ForwardException.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop;

import com.sun.corba.se.internal.core.IOR;

/**
 * Thrown to signal an OBJECT_FORWARD or LOCATION_FORWARD
 */

public class ForwardException extends Exception {
    private IOR forwardedIOR;

    public ForwardException(IOR ior) {
        super();
	forwardedIOR = ior;
    }

    public IOR getIOR()
    {
	return forwardedIOR;
    }
}
