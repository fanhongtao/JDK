/*
 * @(#)MinorCodes.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.corba.se.internal.Interceptors;

import com.sun.corba.se.internal.orbutil.ORBConstants;
import org.omg.CORBA.OMGVMCID;

/** 
 * Minor codes for Portable Interceptors.  Each minor code is based either
 * off ORGVMCID or ORBConstants.PORTABLE_INTERCEPTORS_BASE.
 * <p>
 * For Sun-specific ids, see exception description in code for a description
 * of the minor code meaning.
 */

public final class MinorCodes {
    // BAD_CONTEXT exception minor codes
    // BAD_INV_ORDER exception minor codes
    public static final int
	INVALID_PI_CALL         = OMGVMCID.value + 14;
    public static final int
	SERVICE_CONTEXT_ADD_FAILED = OMGVMCID.value + 15;
    public static final int
	POLICY_FACTORY_REG_FAILED  = OMGVMCID.value + 16;
    public static final int
	RIR_INVALID_PRE_INIT     = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 1;

    // BAD_OPERATION exception minor codes
    // BAD_PARAM exception minor codes
    public static final int 
	INVALID_SERVICE_CONTEXT_ID = OMGVMCID.value + 26;
    public static final int 
	RIR_WITH_NULL_OBJECT    = OMGVMCID.value + 27;
    public static final int
	INVALID_COMPONENT_ID    = OMGVMCID.value + 28;
    public static final int
	INVALID_PROFILE_ID      = OMGVMCID.value + 29;

    // BAD_TYPECODE exception minor codes
    // BOUNDS exception minor codes
    // COMM_FAILURE exception minor codes
    public static final int
	IOEXCEPTION_DURING_CANCEL_REQUEST =
	     ORBConstants.PORTABLE_INTERCEPTORS_BASE + 1;

    // DATA_CONVERSION exception minor codes
    // IMP_LIMIT exception minor codes
    // INTF_REPOS exception minor codes
    // INTERNAL exception minor codes
    public static final int
	EXCEPTION_WAS_NULL      = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 1;
    public static final int
	OBJECT_HAS_NO_DELEGATE  = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 2;
    public static final int
	DELEGATE_NOT_CLIENTSUB  = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 3;
    public static final int
	OBJECT_NOT_OBJECTIMPL   = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 4;
    public static final int
	EXCEPTION_INVALID       = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 5;
    public static final int
	REPLY_STATUS_NOTINIT    = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 6;
    public static final int
	EXCEPTION_IN_ARGUMENTS  = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 7;
    public static final int
	EXCEPTION_IN_EXCEPTIONS = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 8;
    public static final int
	EXCEPTION_IN_CONTEXTS   = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 9;
    public static final int
	EXCEPTION_WAS_NULL_2    = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 10;
    public static final int
	SERVANT_INVALID         = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 11;
    public static final int
	CANT_POP_ONLY_PICURRENT = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 12;
    public static final int
	CANT_POP_ONLY_CURRENT_2 = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 13;
    public static final int
	PI_DSI_RESULT_IS_NULL   = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 14;
    public static final int
	PI_DII_RESULT_IS_NULL   = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 15;
    public static final int
	EXCEPTION_UNAVAILABLE   = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 16;
    public static final int
	CLIENT_INFO_STACK_NULL  = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 17;
    public static final int
	SERVER_INFO_STACK_NULL  = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 18;
    public static final int
	MARK_AND_RESET_FAILED   = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 19;

    // INV_FLAG exception minor codes
    // INV_IDENT exception minor codes
    // INV_OBJREF exception minor codes
    // INV_POLICY exception minor codes
    public static final int
	POLICY_UNKNOWN = OMGVMCID.value + 2;

    // MARSHAL exception minor codes
    // NO_MEMORY exception minor codes
    // FREE_MEM exception minor codes
    // NO_IMPLEMENT exception minor codes
    public static final int
	PI_ORB_NOT_POLICY_BASED = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 1;

    // NO_PERMISSION exception minor codes
    // NO_RESOURCES exception minor codes
    public static final int
	PI_OPERATION_NOT_SUPPORTED = OMGVMCID.value + 1;

    // NO_RESPONSE exception minor codes
    // OBJ_ADAPTER exception minor codes
    // INITIALIZE exception minor codes
    // PERSIST_STORE exception minor codes
    // TRANSIENT exception minor codes
    public static final int
	REQUEST_CANCELLED       = OMGVMCID.value + 3;

    // UNKNOWN exception minor codes
    public static final int
	UNKNOWN_USER_EXCEPTION  = OMGVMCID.value + 1;

    public static final int
	UNKNOWN_REQUEST_INVOKE  = ORBConstants.PORTABLE_INTERCEPTORS_BASE + 1;

    // OBJECT_NOT_EXIST  exception minor codes
} ;
