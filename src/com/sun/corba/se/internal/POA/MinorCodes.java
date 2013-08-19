/*
 * @(#)MinorCodes.java	1.31 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.corba.se.internal.POA ;

import com.sun.corba.se.internal.orbutil.ORBConstants;
import org.omg.CORBA.OMGVMCID ;

/** Minor codes for CORBA system-exceptions. These codes are marshalled
 *  on the wire and allow the client to know the exact cause of the exception.
 *  The minor code numbers for POA/JavaIDLx start with 101 to distinguish
 *  them from JavaIDL minor codes which start from 1. 
 */

public final class MinorCodes {
    // BAD_CONTEXT exception minor codes
    // BAD_INV_ORDER exception minor codes
    public static final int SERVANT_MANAGER_ALREADY_SET = ORBConstants.POA_BASE + 1;
    // BAD_OPERATION exception minor codes
    public static final int SERVANT_ORB = ORBConstants.POA_BASE + 1;
    public static final int BAD_SERVANT = ORBConstants.POA_BASE + 2;

    // BAD_PARAM exception minor codes
    public static final int BAD_TRANSACTION_CONTEXT = ORBConstants.POA_BASE + 1;
    public static final int BAD_REPOSITORY_ID = ORBConstants.POA_BASE + 2;

    // BAD_TYPECODE exception minor codes
    // BOUNDS exception minor codes
    // COMM_FAILURE exception minor codes
    // DATA_CONVERSION exception minor codes
    // IMP_LIMIT exception minor codes
    // INTF_REPOS exception minor codes
    // INTERNAL exception minor codes
    public static final int INVOKESETUP = ORBConstants.POA_BASE + 1;
    public static final int BAD_LOCALREPLYSTATUS = ORBConstants.POA_BASE + 2;
    public static final int PERSISTENT_SERVERPORT_ERROR = ORBConstants.POA_BASE + 3;
    public static final int SERVANT_DISPATCH = ORBConstants.POA_BASE + 4;
    public static final int WRONG_CLIENTSC = ORBConstants.POA_BASE + 5;
    public static final int CANT_CLONE_TEMPLATE = ORBConstants.POA_BASE + 6;
    public static final int POACURRENT_UNBALANCED_STACK =
	ORBConstants.POA_BASE + 7;
    public static final int POACURRENT_NULL_FIELD = ORBConstants.POA_BASE + 8;
    public static final int POA_INTERNAL_GET_SERVANT_ERROR =
	ORBConstants.POA_BASE + 9;

    // INV_FLAG exception minor codes
    // INV_IDENT exception minor codes
    // INV_OBJREF exception minor codes
    // MARSHAL exception minor codes
    // NO_MEMORY exception minor codes
    // FREE_MEM exception minor codes
    // NO_IMPLEMENT exception minor codes
    // NO_PERMISSION exception minor codes
    // NO_RESOURCES exception minor codes
    // NO_RESPONSE exception minor codes
    // OBJ_ADAPTER exception minor codes
    public static final int POA_LOOKUP_ERROR = ORBConstants.POA_BASE + 1;
    public static final int POA_INACTIVE = ORBConstants.POA_BASE + 2;
    public static final int POA_NO_SERVANT_MANAGER = ORBConstants.POA_BASE + 3;
    public static final int POA_NO_DEFAULT_SERVANT = ORBConstants.POA_BASE + 4;
    public static final int POA_SERVANT_NOT_UNIQUE = ORBConstants.POA_BASE + 5;
    public static final int POA_WRONG_POLICY = ORBConstants.POA_BASE + 6;
    public static final int FINDPOA_ERROR = ORBConstants.POA_BASE + 7;
    public static final int ADAPTER_ACTIVATOR_EXCEPTION = OMGVMCID.value + 1 ;
    public static final int POA_SERVANT_ACTIVATOR_LOOKUP_FAILED = ORBConstants.POA_BASE + 9;
    public static final int POA_BAD_SERVANT_MANAGER = ORBConstants.POA_BASE + 10;
    public static final int POA_SERVANT_LOCATOR_LOOKUP_FAILED = ORBConstants.POA_BASE + 11;
    public static final int POA_UNKNOWN_POLICY = ORBConstants.POA_BASE + 12;
    public static final int POA_NOT_FOUND = ORBConstants.POA_BASE + 13;
    public static final int SERVANT_LOOKUP = ORBConstants.POA_BASE + 14;
    public static final int LOCAL_SERVANT_LOOKUP = ORBConstants.POA_BASE + 15;
    
    // INITIALIZE exception minor codes
    public static final int JTS_INIT_ERROR = ORBConstants.POA_BASE + 1;
    public static final int PERSISTENT_SERVERID_NOT_SET = ORBConstants.POA_BASE + 2;
    public static final int PERSISTENT_SERVERPORT_NOT_SET = ORBConstants.POA_BASE + 3;
    public static final int ORBD_ERROR = ORBConstants.POA_BASE + 4;
    public static final int BOOTSTRAP_ERROR = ORBConstants.POA_BASE + 5;

    // PERSIST_STORE exception minor codes
    // TRANSIENT exception minor codes
    public static final int POA_DISCARDING = ORBConstants.POA_BASE + 1;

    // UNKNOWN exception minor codes
    public static final int OTSHOOKEXCEPTION = ORBConstants.POA_BASE + 1;
    public static final int UNKNOWN_SERVER_EXCEPTION = ORBConstants.POA_BASE + 2;
    public static final int UNKNOWN_SERVERAPP_EXCEPTION = ORBConstants.POA_BASE + 3;
    public static final int UNKNOWN_LOCALINVOCATION_ERROR = ORBConstants.POA_BASE + 4;

    // OBJECT_NOT_EXIST  exception minor codes
    public static final int ADAPTER_ACTIVATOR_NONEXISTENT = ORBConstants.POA_BASE + 1;
    public static final int ADAPTER_ACTIVATOR_FAILED = ORBConstants.POA_BASE + 2;
    public static final int BAD_SKELETON = ORBConstants.POA_BASE + 3;
    public static final int NULL_SERVANT = ORBConstants.POA_BASE + 4;
    public static final int ADAPTER_DESTROYED = ORBConstants.POA_BASE + 5;
} ;
