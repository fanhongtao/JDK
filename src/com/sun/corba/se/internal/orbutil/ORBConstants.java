/*
 * @(#)ORBConstants.java	1.52 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

import com.sun.corba.se.internal.util.SUNVMCID ;

public interface ORBConstants {
    public static final int SUN_FIRST_SERVICE_CONTEXT = 0x4e454f00 ;

    public static final int NUM_SUN_SERVICE_CONTEXTS = 15 ;

    // All Sun service contexts must be in the range SUN_FIRST_SERVICE_CONTEXT to
    // SUN_FIRST_SERVICE_CONTEXT + NUM_SUN_SERVICE_CONTEXTS - 1
    public static final int TAG_ORB_VERSION = SUN_FIRST_SERVICE_CONTEXT ;

    // All Sun policies are allocated using the SUNVMCID, which is also
    // used for minor codes.  This allows 12 bits of offset, so
    // the largest legal Sun policy is SUNVMCID.value + 4095.
    public static final int SERVANT_CACHING_POLICY = SUNVMCID.value + 0 ;

    // These are the subcontract IDs for various qualities of
    // service/implementation.
    // Transactional SCIDs have the first bit as 1.
    // Persistent SCIDs have the second bit as 1.
    // SCIDs less than FIRST_POA_SCID are JavaIDL SCIDs.
    public static final int FIRST_POA_SCID = 32;
    public static final int MAX_POA_SCID = 63;
    public static final int TransientSCID = FIRST_POA_SCID ;
    public static final int PersistentSCID = FIRST_POA_SCID | 0x2;
    public static final int SCTransientSCID = FIRST_POA_SCID + 4 ;
    public static final int SCPersistentSCID = (FIRST_POA_SCID + 4 ) | 0x2;
    public static final int TransientTransactionSCID = FIRST_POA_SCID | 0x1;
    public static final int PersistentTransactionSCID = FIRST_POA_SCID | 0x3;

    public static final int DefaultSCID = ORBConstants.TransientSCID;

    // Constants for ORB Classes supported by SUN's ORB

    // Transient Objects supported by IIOP ORB
    public static final String IIOP_ORB_NAME = "com.sun.corba.se.internal.iiop.ORB";

    // Transient Name Service uses NSORB
    public static final String NS_ORB_NAME = "com.sun.corba.se.internal.CosNaming.NSORB";

    // Persistent Objects supported by POA ORB
    public static final String POA_ORB_NAME = "com.sun.corba.se.internal.POA.POAORB";

    // Portable interceptors
    public static final String PI_ORB_NAME =
	"com.sun.corba.se.internal.Interceptors.PIORB";

    // Transactional Persistent Objects supported by Transactional POA ORB
    public static final String TRANS_POA_ORB_NAME = "com.sun.corba.se.internal.TransactionalPOA.TransactionalPOAORB";

    // Constants for Subcontract classes supported by SUN's ORB's

    // Constants for Subcontacts in POA ORB
    public static final String GenericPOAServer = "com.sun.corba.se.internal.POA.GenericPOAServerSC";
    public static final String GenericPOAClient = "com.sun.corba.se.internal.POA.GenericPOAClientSC";
    public static final String ServantCachePOAClient = "com.sun.corba.se.internal.POA.ServantCachePOAClientSC";

    // Constants for Subcontacts in Transactional POA ORB
    public static final String TransactionalPOAServer = "com.sun.corba.se.internal.TransactionalPOA.TransactionalServerSC";



    // Constants for ORB properties **************************************************************

    // All ORB properties must follow the following rules:
    // 1. Property names must start with either OMG_PREFIX or SUN_PREFIX.
    // 2. Property names must have unique suffixes after the last ".".
    // 3. Property names must have "ORB" as the first 3 letters in their suffix.
    // 4. proprietary property names should have a subsystem where appropriate after the prefix.

    // org.omg.CORBA properties must be defined by OMG standards
    // The well known org.omg.CORBA.ORBClass and org.omg.CORBA.ORBSingletonClass are not included here
    // since they occur in org.omg.CORBA.ORB.
    public static final String OMG_PREFIX = "org.omg.CORBA." ;

    public static final String INITIAL_HOST_PROPERTY		= OMG_PREFIX + "ORBInitialHost" ;
    public static final String INITIAL_PORT_PROPERTY		= OMG_PREFIX + "ORBInitialPort" ;
    public static final String INITIAL_SERVICES_PROPERTY	= OMG_PREFIX + "ORBInitialServices" ;
    public static final String DEFAULT_INIT_REF_PROPERTY	= OMG_PREFIX + "ORBDefaultInitRef" ;
    public static final String ORB_INIT_REF_PROPERTY	        = OMG_PREFIX + "ORBInitRef" ;

    // All of our proprietary properties must start with com.sun.CORBA
    public static final String SUN_PREFIX = "com.sun.CORBA." ;

    // general properties
    public static final String ALLOW_LOCAL_OPTIMIZATION		= SUN_PREFIX + "ORBAllowLocalOptimization" ;
    public static final String SERVER_PORT_PROPERTY		= SUN_PREFIX + "ORBServerPort" ;
    public static final String SERVER_HOST_PROPERTY		= SUN_PREFIX + "ORBServerHost" ;
    public static final String ORB_ID_PROPERTY			= SUN_PREFIX + "ORBid" ;
    public static final String DEBUG_PROPERTY			= SUN_PREFIX + "ORBDebug" ;

    // giop related properties - default settings in decimal form
    public static final String GIOP_VERSION                     = SUN_PREFIX + "giop.ORBGIOPVersion" ;
    public static final String GIOP_FRAGMENT_SIZE               = SUN_PREFIX + "giop.ORBFragmentSize" ;
    public static final String GIOP_BUFFER_SIZE                 = SUN_PREFIX + "giop.ORBBufferSize" ;
    public static final String GIOP_11_BUFFMGR                  = SUN_PREFIX + "giop.ORBGIOP11BuffMgr";
    public static final String GIOP_12_BUFFMGR                  = SUN_PREFIX + "giop.ORBGIOP12BuffMgr";
    public static final String GIOP_TARGET_ADDRESSING           = SUN_PREFIX + "giop.ORBTargetAddressing";    
    public static final int GIOP_DEFAULT_FRAGMENT_SIZE = 1024;
    public static final int GIOP_DEFAULT_BUFFER_SIZE = 1024;
    public static final int DEFAULT_GIOP_11_BUFFMGR = 0; //Growing
    public static final int DEFAULT_GIOP_12_BUFFMGR = 2; //Streaming
    public static final short ADDR_DISP_OBJKEY = 0; // object key used for target addressing
    public static final short ADDR_DISP_PROFILE = 1; // iop profile used for target addressing
    public static final short ADDR_DISP_IOR = 2; // ior used for target addressing
    public static final short ADDR_DISP_HANDLE_ALL = 3; // accept all target addressing dispositions (default)

    // CORBA formal 00-11-03 sections 15.4.2.2, 15.4.3.2, 15.4.6.2
    // state that the GIOP 1.2 RequestMessage, ReplyMessage, and
    // LocateReply message bodies must begin on 8 byte boundaries.
    public static final int GIOP_12_MSG_BODY_ALIGNMENT = 8;

    // The GIOP 1.2 fragments must be divisible by 8.  We generalize this
    // to GIOP 1.1 fragments, as well.
    public static final int GIOP_FRAGMENT_DIVISOR = 8;
    public static final int GIOP_FRAGMENT_MINIMUM_SIZE = 32;

    // connection management properties
    public static final String HIGH_WATER_MARK_PROPERTY		= SUN_PREFIX + "connection.ORBHighWaterMark" ;
    public static final String LOW_WATER_MARK_PROPERTY		= SUN_PREFIX + "connection.ORBLowWaterMark" ;
    public static final String NUMBER_TO_RECLAIM_PROPERTY	= SUN_PREFIX + "connection.ORBNumberToReclaim" ;
    public static final String SOCKET_FACTORY_CLASS_PROPERTY	= SUN_PREFIX + "connection.ORBSocketFactoryClass" ;
    public static final String LISTEN_SOCKET_PROPERTY	        = SUN_PREFIX + "connection.ORBListenSocket";

    // POA related policies
    public static final String PERSISTENT_SERVER_PORT_PROPERTY	= SUN_PREFIX + "POA.ORBPersistentServerPort" ;
    public static final String SERVER_ID_PROPERTY		= SUN_PREFIX + "POA.ORBServerId" ;
    public static final String BAD_SERVER_ID_HANDLER_CLASS_PROPERTY
	                                                        = SUN_PREFIX + "POA.ORBBadServerIdHandlerClass" ;
    public static final String ACTIVATED_PROPERTY		= SUN_PREFIX + "POA.ORBActivated" ;
    public static final String SERVER_NAME_PROPERTY		= SUN_PREFIX + "POA.ORBServerName" ;

    // Server Properties; e.g. when properties passed to ORB activated
    // servers

    public static final String SERVER_DEF_VERIFY_PROPERTY	= SUN_PREFIX + "activation.ORBServerVerify" ;

    // This one is an exception, but it may be externally visible
    public static final String SUN_LC_PREFIX = "com.sun.corba." ;

    // Necessary for package renaming to work correctly
    public static final String SUN_LC_VERSION_PREFIX = "com.sun.corba.se.";

    public static final String JTS_CLASS_PROPERTY		= SUN_LC_VERSION_PREFIX + "CosTransactions.ORBJTSClass" ;

    // Constants for ORB prefixes **************************************************************

    public static final String PI_ORB_INITIALIZER_CLASS_PREFIX   =
	"org.omg.PortableInterceptor.ORBInitializerClass.";

    // Constants for NameService properties ************************************

    public static final int DEFAULT_INITIAL_PORT                 = 900;


    // Constants for INS properties ********************************************

    // GIOP Version number for validation of INS URL format addresses
    public static final int MAJORNUMBER_SUPPORTED                 = 1;
    public static final int MINORNUMBERMAX                        = 2;

    // Subcontract's differentiation using the TRANSIENT and PERSISTENT
    // Name Service Property.
    public static final int TRANSIENT                             = 1;
    public static final int PERSISTENT                            = 2;

    // Constants for ORBD properties ****************************************************************

    // These properties are never passed on ORB init: they are only passed to ORBD.

    public static final String DB_DIR_PROPERTY			= SUN_PREFIX + "activation.DbDir" ;
    public static final String DB_PROPERTY			= SUN_PREFIX + "activation.db" ;
    public static final String ORBD_PORT_PROPERTY		= SUN_PREFIX + "activation.Port" ;
    public static final String SERVER_POLLING_TIME              = SUN_PREFIX + "activation.ServerPollingTime";
    public static final String SERVER_STARTUP_DELAY             = SUN_PREFIX + "activation.ServerStartupDelay";

    public static final int DEFAULT_ACTIVATION_PORT		= 1049 ;

    // If RI is starting the NameService then they would indicate that by
    // passing the RI flag. That would start a Persistent Port to listen to
    // INS request.
    public static final int RI_NAMESERVICE_PORT                 = 1050;

    public static final int DEFAULT_SERVER_POLLING_TIME         = 1000;

    public static final int DEFAULT_SERVER_STARTUP_DELAY        = 1000;



    // Constants for initial references *************************************************************

    public static final String TRANSIENT_NAME_SERVICE_NAME = "TNameService" ;
    public static final String PERSISTENT_NAME_SERVICE_NAME = "NameService" ;
    // A large Number to make sure that other ServerIds doesn't collide
    // with NameServer Persistent Server Id
    public static final int    NAME_SERVICE_SERVER_ID       = 100000;
    public static final String ROOT_POA_NAME		= "RootPOA" ;
    public static final String POA_CURRENT_NAME		= "POACurrent" ;
    public static final String SERVER_ACTIVATOR_NAME	= "ServerActivator" ;
    public static final String SERVER_LOCATOR_NAME	= "ServerLocator" ;
    public static final String SERVER_REPOSITORY_NAME	= "ServerRepository" ;
    public static final String INITIAL_NAME_SERVICE_NAME= "InitialNameService" ;
    public static final String TRANSACTION_CURRENT_NAME = "TransactionCurrent" ;
    public static final String DYN_ANY_FACTORY_NAME	= "DynAnyFactory" ;

    // New for Portable Interceptors
    public static final String PI_CURRENT_NAME		= "PICurrent" ;
    public static final String CODEC_FACTORY_NAME	= "CodecFactory" ;

    // Constants for ORBD DB ***********************************************************************

    public static final String DEFAULT_DB_DIR	    = "orb.db" ;
    public static final String DEFAULT_DB_NAME	    = "db" ;
    public static final String INITIAL_ORB_DB	    = "initial.db" ;
    public static final String SERVER_LOG_DIR	    = "logs" ;
    public static final String ORBID_DIR_BASE	    = "orbids" ;
    public static final String ORBID_DB_FILE_NAME   = "orbids.db" ;

    // Constants for ThreadPool ********************************************************************

    // Default value for when inactive threads in the pool can stop running (ms)
    public static final int DEFAULT_INACTIVITY_TIMEOUT = 120000;

    // Constants for minor code bases **************************************************************
    public static final int SUBSYSTEM_SIZE	= 200 ;

    // util/MinorCodes starts at SUNVMCID.value.  We do not include this here in order
    // to avoid creating another dependency of the pure ORB support code on our ORB.

    // GENERAL_BASE is used for orbutil/MinorCodes
    public static final int GENERAL_BASE		= SUNVMCID.value    + SUBSYSTEM_SIZE ;
    public static final int ACTIVATION_BASE		= GENERAL_BASE	    + SUBSYSTEM_SIZE ;
    public static final int COSNAMING_BASE		= ACTIVATION_BASE   + SUBSYSTEM_SIZE ;
    public static final int PORTABLE_INTERCEPTORS_BASE	= COSNAMING_BASE    + SUBSYSTEM_SIZE ;
    public static final int POA_BASE			= PORTABLE_INTERCEPTORS_BASE + SUBSYSTEM_SIZE ;
    public static final int IOR_BASE			= POA_BASE	    + SUBSYSTEM_SIZE ;
    public static final int UTIL_BASE			= IOR_BASE	    + SUBSYSTEM_SIZE ;

    // Code Set related *******************************************************

    // If we don't always send the code set context, there's a possibility
    // of failure when fragments of a smaller request are interleved with
    // those of a first request with other large service contexts.
    //
    public static final boolean DEFAULT_ALWAYS_SEND_CODESET_CTX = true;
    public static final String ALWAYS_SEND_CODESET_CTX_PROPERTY
        = SUN_PREFIX + "codeset.AlwaysSendCodeSetCtx";

    // Use byte order markers in streams when applicable?  This won't apply to
    // GIOP 1.1 due to limitations in the CDR encoding.
    public static final boolean DEFAULT_USE_BYTE_ORDER_MARKERS = true;
    public static final String USE_BOMS = SUN_PREFIX + "codeset.UseByteOrderMarkers";

    // Use byte order markers in encapsulations when applicable?
    public static final boolean DEFAULT_USE_BYTE_ORDER_MARKERS_IN_ENCAPS = false;
    public static final String USE_BOMS_IN_ENCAPS = SUN_PREFIX + "codeset.UseByteOrderMarkersInEncaps";

    // The CHAR_CODESETS and WCHAR_CODESETS allow the user to override the default
    // connection code sets.  The value should be a comma separated list of OSF
    // registry numbers.  The first number in the list will be the native code
    // set.
    //
    // Number can be specified as hex if preceded by 0x, otherwise they are
    // interpreted as decimal.
    //
    // Code sets that we accept currently (see core/OSFCodeSetRegistry):
    //
    // char/string:
    //
    // ISO8859-1 (Latin-1)     0x00010001
    // ISO646 (ASCII)          0x00010020
    // UTF-8                   0x05010001
    //
    // wchar/string:
    //
    // UTF-16                  0x00010109
    // UCS-2                   0x00010100
    // UTF-8                   0x05010001
    //
    // Note:  The ORB will let you assign any of the above values to
    // either of the following properties, but the above assignments
    // are the only ones that won't get you into trouble.
    public static final String CHAR_CODESETS = SUN_PREFIX + "codeset.charsets";
    public static final String WCHAR_CODESETS = SUN_PREFIX + "codeset.wcharsets";
}

