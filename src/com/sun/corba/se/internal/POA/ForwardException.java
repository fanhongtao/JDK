/*
 * @(#)ForwardException.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * @(#)ForwardException.java	1.6 98/02/09
 * 
 * Copyright 1993-1997 Sun Microsystems, Inc. 901 San Antonio Road, 
 * Palo Alto, California, 94303, U.S.A.  All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * CopyrightVersion 1.2
 * 
 */

package com.sun.corba.se.internal.POA;

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
