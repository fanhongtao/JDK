/*
 * @(#)MinorCodes.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.CosNaming;

import com.sun.corba.se.internal.orbutil.ORBConstants ;
/**
 * The Minor Codes for the transient and persistent name server.
 */ 
public final class MinorCodes {
    /**
     * The base number for all NamingContextImpl exception minor codes.
     */
    private static final int NAMING_CTX_BASE = ORBConstants.COSNAMING_BASE ;

    /**
     * The base number for all TransientNameService exception minor codes.
     */
    private static final int TRANS_NS_BASE = ORBConstants.COSNAMING_BASE + 50;
  
    /**
     * The base number for all TransientNamingContext exception minor codes.
     */
    private static final int TRANS_NC_BASE = ORBConstants.COSNAMING_BASE + 100;
  
    /**
     * The constructor is private since there should be no instances.
     */ 
    private MinorCodes() {}
  
    // CORBA::INITIALIZE

    /**
     * The transient name service caught a CORBA::SystemException while
     * initializing.
     */
    public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC_SYS = TRANS_NS_BASE+0;

    /**
     * The transient name service caught a Java exception while initializing.
     */
    public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC = TRANS_NS_BASE+1;


    // CORBA::INTERNAL
  
    /**
     * An AlreadyBound exception was thrown in a rebind operation, which
     * is unexpected.
     */
    public static final int NAMING_CTX_REBIND_ALREADY_BOUND = NAMING_CTX_BASE+0;

    /**
     * An AlreadyBound exception was thrown in a rebind_context operation, which
     * is unexpected.
     */
    public static final int NAMING_CTX_REBINDCTX_ALREADY_BOUND = NAMING_CTX_BASE+1;

    /**
     * The binding type passed to the internal binding implementation was
     * not BindingType::nobject or BindingType::ncontext.
     */
    public static final int NAMING_CTX_BAD_BINDINGTYPE = NAMING_CTX_BASE+2;

    /**
     * A object reference was bound as a context, but it could not be narrowed
     * to CosNaming::NamingContext.
     */
    public static final int NAMING_CTX_RESOLVE_CANNOT_NARROW_TO_CTX = NAMING_CTX_BASE+3;


    /**
     * The implementation of the bind operation encountered a previous
     * binding, which is unexpected.
     */
    public static final int TRANS_NC_BIND_ALREADY_BOUND = TRANS_NC_BASE+0;

    /**
     * The implementation of the list operation caught a Java exception
     * while creating the list iterator, which is unexpected.
     */
    public static final int TRANS_NC_LIST_GOT_EXC = TRANS_NC_BASE+1;

    /**
     * The implementation of the new_context operation caught a Java exception
     * while creating the new NamingContext servant, which is unexpected.
     */
    public static final int TRANS_NC_NEWCTX_GOT_EXC = TRANS_NC_BASE+2;

    /**
     * The implementation of the destroy operation caught a Java exception
     * while disconnecting from the ORB.
     */
    public static final int TRANS_NC_DESTROY_GOT_EXC = TRANS_NC_BASE+3;

    /**
     * The Object Reference passed as the argument in rebind( ) and bind( )
     * is invalid
     */
    public static final int OBJECT_IS_NULL = TRANS_NC_BASE+4;

    // For -ORBInitDef a stringified reference other than
    // corbaloc: corbaname: IOR: is given
    public static final int INS_BAD_SCHEME_NAME = TRANS_NC_BASE + 5;

    // For one of the -ORBInitDef The HostAddress given is not correct
    public static final int INS_BAD_ADDRESS = TRANS_NC_BASE + 6;

    // -ORBInitDef's stringified URL is malformed
    public static final int INS_BAD_SCHEME_SPECIFIC_PART = TRANS_NC_BASE + 7;

    // -ORBInitDef's stringified URL is malformed
    public static final int INS_OTHER = TRANS_NC_BASE + 8;
}
