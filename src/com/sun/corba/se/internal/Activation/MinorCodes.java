/*
 * @(#)MinorCodes.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Activation;

import com.sun.corba.se.internal.orbutil.ORBConstants;

/** 
 * Minor codes for CORBA system-exceptions. These codes are marshalled
 * on the wire and allow the client to know the exact cause of the exception.
 * The minor code numbers for POA/JavaIDLx start with 101 to distinguish
 * them from JavaIDL minor codes which start from 1. 
 * 
 * @version     1.17, 03/06/20
 * @author      Rohit Garg
 * @since       JDK1.2
 */
public final class MinorCodes {
    // INITIALIZE exception minor codes
    public static final int CANNOT_READ_REPOSITORY_DB = ORBConstants.ACTIVATION_BASE +1;
    public static final int CANNOT_ADD_INITIAL_NAMING = ORBConstants.ACTIVATION_BASE +2;

    // INTERNAL
    public static final int CANNOT_WRITE_REPOSITORY_DB = ORBConstants.ACTIVATION_BASE +1;
    public static final int SERVER_NOT_EXPECTED_TO_REGISTER = ORBConstants.ACTIVATION_BASE +3;
    public static final int UNABLE_TO_START_PROCESS = ORBConstants.ACTIVATION_BASE +4;
    public static final int SERVER_IS_HELD_DOWN = ORBConstants.ACTIVATION_BASE +5;
    public static final int SERVER_NOT_RUNNING = ORBConstants.ACTIVATION_BASE +6;

    // 
}
