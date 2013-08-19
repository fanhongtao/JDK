/*
 * @(#)MinorCodes.java	1.45 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.corba.se.internal.orbutil ;

import com.sun.corba.se.internal.orbutil.ORBConstants ;
import org.omg.CORBA.OMGVMCID ;
import com.sun.corba.se.internal.util.SUNVMCID ;

/** Minor codes for CORBA system-exceptions. These codes are marshalled
 *  on the wire and allow the client to know the exact cause of the exception.
 *  The minor code numbers for POA/ORBId start with 1001 to distinguish
 *  them from JavaIDL minor codes which start from 1, and POA codes which start
 *  from 101. 
 */

public final class MinorCodes {
/////////////////////////////////////////////////////////////////
// BAD_OPERATION minor codes
/////////////////////////////////////////////////////////////////
    public static final int ADAPTER_ID_NOT_AVAILABLE = ORBConstants.GENERAL_BASE + 1 ;
    public static final int SERVER_ID_NOT_AVAILABLE = ORBConstants.GENERAL_BASE + 2 ;

/////////////////////////////////////////////////////////////////
// BAD_PARAM
/////////////////////////////////////////////////////////////////

    // The server tried to unmarshal wchar/wstring data from the client, but
    // the client didn't send the code set service context.
    // See CORBA formal 00-11-03 13.9.2.6.
    public static final int NO_CLIENT_WCHAR_CODESET_CTX = OMGVMCID.value + 23;

    public static final int UNABLE_REGISTER_VALUE_FACTORY = OMGVMCID.value + 1;

    // The next two minor codes must be duplicated here from the util
    // package because of the pure ORB build.  We want J2EE to use these
    // correct values even if running pure ORB.
    //
    // NOTE: Pre-Merlin Sun ORBs threw BAD_PARAM with a minor code
    // of SUNVMCID.value + 1 to mean a java.io.NotSerializableException.
    public static final int LEGACY_SUN_NOT_SERIALIZABLE = SUNVMCID.value + 1;

    // Correct value: Java to IDL ptc-00-01-06 1.4.8.
    public static final int NOT_SERIALIZABLE = OMGVMCID.value + 6;

    public static final int NULL_PARAM = ORBConstants.GENERAL_BASE + 1;

    // For some reason this is used in ORB.lookup_value_factory()
    // instead of UNABLE_LOCATE_VALUE_FACTORY, so we define it here.
    public static final int UNABLE_FIND_VALUE_FACTORY = ORBConstants.GENERAL_BASE + 2;
    // Added as part of issue 3015 to support AbstractInterfaceDef
    public static final int ABSTRACT_FROM_NON_ABSTRACT = ORBConstants.GENERAL_BASE + 3;
    /**
     * Thrown if there are errors while reading the org.omg.IOP.TaggedProfile
     * during ior construction.
     */
    public static final int INVALID_TAGGED_PROFILE = ORBConstants.GENERAL_BASE + 4;

/////////////////////////////////////////////////////////////////
// BAD_INV_ORDER exception minor codes
/////////////////////////////////////////////////////////////////
    public static final int DSIMETHOD_NOTCALLED = ORBConstants.GENERAL_BASE + 1;
    public static final int SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK = OMGVMCID.value + 3;
    public static final int BAD_OPERATION_AFTER_SHUTDOWN = OMGVMCID.value + 4;

/////////////////////////////////////////////////////////////////
// COMM_FAILURE minor codes
/////////////////////////////////////////////////////////////////
    public static final int CONNECT_FAILURE = ORBConstants.GENERAL_BASE + 1 ;
    public static final int CONN_CLOSE_REBIND = ORBConstants.GENERAL_BASE + 2 ;
    public static final int WRITE_ERROR_SEND = ORBConstants.GENERAL_BASE + 3 ;
    public static final int GET_PROPERTIES_ERROR = ORBConstants.GENERAL_BASE + 4 ;
    public static final int BOOTSTRAP_SERVER_NOT_AVAIL = ORBConstants.GENERAL_BASE + 5 ;
    public static final int INVOKE_ERROR = ORBConstants.GENERAL_BASE + 6 ;
    public static final int
	DEFAULT_CREATE_SERVER_SOCKET_GIVEN_NON_IIOP_CLEAR_TEST =
	ORBConstants.GENERAL_BASE + 7;
    public static final int CONN_ABORT = ORBConstants.GENERAL_BASE + 8;
    public static final int CONN_REBIND = ORBConstants.GENERAL_BASE + 9;

    // Received a MessageError message type, probably indicating header
    // corruption or GIOP version mismatch.
    public static final int RECV_MSG_ERROR = ORBConstants.GENERAL_BASE + 10;

/////////////////////////////////////////////////////////////////
// DATA_CONVERSION minor codes
/////////////////////////////////////////////////////////////////
    /**
     * A character didn't properly map to the transmission code set.
     * CORBA formal 00-11-03.
     */
    public static final int CHAR_NOT_IN_CODESET = OMGVMCID.value + 1;

    public static final int BAD_STRINGIFIED_IOR_LEN = ORBConstants.GENERAL_BASE + 1 ;
    public static final int BAD_STRINGIFIED_IOR = ORBConstants.GENERAL_BASE + 2 ;

    /** 
     * Unable to perform ORB resolve_initial_references operation
     * due to the host or the post being incorrect or unspecified.
     */
    public static final int BAD_MODIFIER = ORBConstants.GENERAL_BASE + 3;
    
    // No longer used!
    public static final int CODESET_INCOMPATIBLE = ORBConstants.GENERAL_BASE + 4;
   
    public static final int BAD_HEX_DIGIT = ORBConstants.GENERAL_BASE + 5 ;

    /**
     * An invalid unicode pair was detected during code set conversion.
     */
    public static final int BAD_UNICODE_PAIR = ORBConstants.GENERAL_BASE + 6;

    /**
     * Tried to convert bytes to a single Java char, but the bytes yielded more
     * than one Java char.  This could happen in the future when surrogate pairs
     * before more common.
     */
    public static final int BTC_RESULT_MORE_THAN_ONE_CHAR = ORBConstants.GENERAL_BASE + 7;

    /**
     * Client side sent a code set service contexts with values that we
     * don't support.  That probably means there was an error on the client
     * or we didn't correctly put code set information in our IOR.
     */
    public static final int BAD_CODESETS_FROM_CLIENT = ORBConstants.GENERAL_BASE + 8;

    /**
     * Thrown when a char to byte conversion for a CORBA char resulted in
     * more than one byte.
     */
    public static final int INVALID_SINGLE_CHAR_CTB = ORBConstants.GENERAL_BASE + 9;

    /**
     * Thrown when a character to byte conversion resulted in a number
     * of bytes not equal to two times the number of chars.  This is only
     * used in GIOP 1.1 since interoperability is limited to 2 byte
     * fixed width encodings.
     */
    public static final int BAD_GIOP_1_1_CTB = ORBConstants.GENERAL_BASE + 10;
    
/////////////////////////////////////////////////////////////////
// INV_OBJREF exception minor codes
/////////////////////////////////////////////////////////////////
    public static final int BAD_CORBALOC_STRING = ORBConstants.GENERAL_BASE + 1 ;
    public static final int NO_PROFILE_PRESENT = ORBConstants.GENERAL_BASE + 2 ;

    // The client tried to marshal wchar/wstring data, but the server
    // didn't include its code sets in the IOR.
    // See 00-11-03 13.9.2.6
    public static final int NO_SERVER_WCHAR_CODESET_CMP = OMGVMCID.value + 1;

/////////////////////////////////////////////////////////////////
// INITIALIZE exception minor codes
/////////////////////////////////////////////////////////////////
    public static final int CANNOT_CREATE_ORBID_DB  = ORBConstants.GENERAL_BASE + 1;
    public static final int CANNOT_READ_ORBID_DB    = ORBConstants.GENERAL_BASE + 2;
    public static final int CANNOT_WRITE_ORBID_DB   = ORBConstants.GENERAL_BASE + 3;
    public static final int 
	GET_SERVER_PORT_CALLED_BEFORE_ENDPOINTS_INITIALIZED
	= ORBConstants.GENERAL_BASE + 4;
	

/////////////////////////////////////////////////////////////////
// INTERNAL exception minor codes (also see util/MinorCodes)
/////////////////////////////////////////////////////////////////
    public static final int NON_EXISTENT_ORBID = ORBConstants.GENERAL_BASE + 1;
    public static final int NO_SERVER_SUBCONTRACT = ORBConstants.GENERAL_BASE + 2 ;
    public static final int SERVER_SC_TEMP_SIZE = ORBConstants.GENERAL_BASE + 3 ;	
    public static final int NO_CLIENT_SC_CLASS = ORBConstants.GENERAL_BASE + 4 ;	
    public static final int SERVER_SC_NO_IIOP_PROFILE = ORBConstants.GENERAL_BASE + 5 ;	
    public static final int GET_SYSTEM_EX_RETURNED_NULL = ORBConstants.GENERAL_BASE + 6;
    // This minor code is used when there is an attempt to put an Indirection
    // offset in the orbutil.cacheTable when there is an existing entry with
    // same key (Object). The new indirection offset is different from the one
    // present in the table.
    public static final int DUPLICATE_INDIRECTION_OFFSET = ORBConstants.GENERAL_BASE + 7;

    /** 
     * When unmarshalling, the repository id of the user exception
     * was found to be of incorrect length.
     */
    public static final int PEEKSTRING_FAILED = ORBConstants.GENERAL_BASE + 7 ;

    /** 
     * Unable to determine local hostname using the Java APIs 
     * InetAddress.getLocalHost().getHostName().
     */
    public static final int GET_LOCAL_HOST_FAILED = ORBConstants.GENERAL_BASE + 8 ;

    /** 
     * Unable to create the listener thread on the specific port.
     * Either the post is taken or there was an error creating the
     * daemon thread.
     */
    public static final int CREATE_LISTENER_FAILED = ORBConstants.GENERAL_BASE + 9 ;

    /**
     * Bad locate request status found in the IIOP locate reply.
     */
    public static final int BAD_LOCATE_REQUEST_STATUS = ORBConstants.GENERAL_BASE + 10 ;

    /** 
     * Error encountered while stringifying an object reference.
     */
    public static final int STRINGIFY_WRITE_ERROR = ORBConstants.GENERAL_BASE + 11 ;
    // from Message.java
    /** 
     * IIOP message with bad GIOP v1.0 message type found. 
     */
    public static final int BAD_GIOP_REQUEST_TYPE = ORBConstants.GENERAL_BASE + 12 ;
    // from Utility.java
    /** 
     * Error encountered while unmarshalling the user exception.
     */
    public static final int ERROR_UNMARSHALING_USEREXC = ORBConstants.GENERAL_BASE + 13 ;
    // from SubcontractRegistry.java
    /** 
     * The client, or a server subcontract being registered 
     * will overflow the suncontract registry.
     */
    public static final int SUBCONTRACTREGISTRY_ERROR = ORBConstants.GENERAL_BASE + 14 ;
    // from GenericCORBAClientSC.java, RequestImpl.java
    /** 
     * Error while processing a LocationForward.
     */
    public static final int LOCATIONFORWARD_ERROR = ORBConstants.GENERAL_BASE + 15 ;

    // There is another minor code with that name in com.sun.PortableServer.MinorCodes
    public static final int WRONG_CLIENTSC = ORBConstants.GENERAL_BASE + 16 ;

    public static final int BAD_SERVANT_READ_OBJECT = ORBConstants.GENERAL_BASE + 17 ;

    public static final int MULT_IIOP_PROF_NOT_SUPPORTED = ORBConstants.GENERAL_BASE + 18 ;

    // from Message.java
    /**
     * GIOP magic is corrupted.
     */
    public static final int GIOP_MAGIC_ERROR = ORBConstants.GENERAL_BASE + 20;
    // from Message.java
    
    /**
     * Invalid GIOP version.
     */
    public static final int GIOP_VERSION_ERROR = ORBConstants.GENERAL_BASE + 21;
    // from Message.java
    /**
     * Illegal reply status in GIOP reply message.
     */
    public static final int ILLEGAL_REPLY_STATUS = ORBConstants.GENERAL_BASE + 22;
    // from Message.java
    /**
     * Illegal reply status in GIOP reply message.
     */
    public static final int ILLEGAL_GIOP_MSG_TYPE = ORBConstants.GENERAL_BASE + 23;
    // from Message.java
    /**
     * Fragmentation is not allowed for a particular message type.
     */
    public static final int FRAGMENTATION_DISALLOWED = ORBConstants.GENERAL_BASE + 24;

    /** 
     * Bad status returned in the IIOP Reply message by the server.
     */
    public static final int BAD_REPLYSTATUS = ORBConstants.GENERAL_BASE + 25 ;

    /**
     * Character converter failed internally.
     */
    public static final int CTB_CONVERTER_FAILURE = ORBConstants.GENERAL_BASE + 26;
    public static final int BTC_CONVERTER_FAILURE = ORBConstants.GENERAL_BASE + 27;

    /**
     * Currently, we only support fixed width encodings even in
     * GIOP 1.2 due to code complexity when writing wchar arrays with
     * chunking and fragmentation.  (This is fine for now since we
     * only really support UTF-16 for wchar data.)
     */
    public static final int WCHAR_ARRAY_UNSUPPORTED_ENCODING = ORBConstants.GENERAL_BASE + 28;

    /**
     * Illegal target addressing disposition value.
     */
    public static final int ILLEGAL_TARGET_ADDRESS_DISPOSITION = ORBConstants.GENERAL_BASE + 29;    
    
    /**
     * No reply while attempting to get addressing disposition.
     */
    public static final int NULL_REPLY_IN_GET_ADDR_DISPOSITION = ORBConstants.GENERAL_BASE + 30;

    /**
     * Invalid GIOP target addressing preference.
     */
    public static final int ORB_TARGET_ADDR_PREFERENCE_IN_EXTRACT_OBJECTKEY_INVALID = ORBConstants.GENERAL_BASE + 31;

    public static final int INVALID_ISSTREAMED_TCKIND = ORBConstants.GENERAL_BASE + 32;

    /**
     * Found a JDK 1.3.1 patch level indicator at the end of the object key,
     * but it had a value less than the JDK 1.3.1_01 value of 1.
     */
    public static final int INVALID_JDK1_3_1_PATCH_LEVEL = ORBConstants.GENERAL_BASE + 33;

    /**
     * Error unmarshaling the data portion of a service context. 
     * Most likely due to the ServiceContextData class not being
     * able to initialize the proper ServiceContext.
     */
    public static final int SVCCTX_UNMARSHAL_ERROR = ORBConstants.GENERAL_BASE + 34;

/////////////////////////////////////////////////////////////////
// MARSHAL exception minor codes (also see util/MinorCodes)
/////////////////////////////////////////////////////////////////

    /**
     * When a non objectimpl given to object_to_string
     * formal/01-09-34 p 3-22.
     */
    public static final int NOT_AN_OBJECT_IMPL = OMGVMCID.value + 2;

    /**
     * The chunk ended but data was read past it without closing
     */
    public static final int CHUNK_OVERFLOW = ORBConstants.GENERAL_BASE + 1;

    /**
     * Thrown when a CDRInputStream with the grow strategy tries to
     * call underflow.
     */
    public static final int UNEXPECTED_EOF = ORBConstants.GENERAL_BASE + 2;

    /** 
     * Error occured while trying to read a marshalled object 
     * reference and converting into an in memory object reference.
     */
    public static final int READ_OBJECT_EXCEPTION = ORBConstants.GENERAL_BASE + 3 ;

   /** 
     * Character encountered while marshalling or unmarshalling 
     * that is not ISO Latin-1 (8859.1) compliant. It is not in
     * the range of 0 to 255.  Also used for DATA_CONVERSION exception
     *
     * No longer used!
     */
    public static final int CHARACTER_OUTOFRANGE = ORBConstants.GENERAL_BASE + 4 ;

    /**
     * An exception was thrown while doing the result()
     * operation on ServerRequest.
     */
    public static final int DSI_RESULT_EXCEPTION = ORBConstants.GENERAL_BASE + 5 ;

    /**
     * IIOPInputStream.grow was called.
     */
    public static final int IIOPINPUTSTREAM_GROW = ORBConstants.GENERAL_BASE + 6 ;

    /**
     * Thrown when underflow occurs in BufferManagerReadStream, but
     * the last fragment of that message was already received and
     * processed.
     */
    public static final int END_OF_STREAM = ORBConstants.GENERAL_BASE + 7;

    /**
     * Thrown when errors happen while extracting object key from the 
     * request headers.
     */
    public static final int INVALID_OBJECT_KEY = ORBConstants.GENERAL_BASE + 8;

    /**
     * Thrown when loading a class with a provided URL, and the URL is
     * invalid.
     */
    public static final int MALFORMED_URL = ORBConstants.GENERAL_BASE + 9;

    /**
     * Thrown when an Error comes up from calling readValue on a ValueHandler
     * in CDRInputStream.
     */
    public static final int VALUEHANDLER_READ_ERROR = ORBConstants.GENERAL_BASE + 10;

    /**
     * Thrown when an Exception comes up from calling readValue on a 
     * ValueHandler in CDRInputStream.
     */
    public static final int VALUEHANDLER_READ_EXCEPTION = ORBConstants.GENERAL_BASE + 11;

    /**
     * Thrown when a bad kind is given in isCustomType in CDRInputStream.
     */
    public static final int BAD_KIND = ORBConstants.GENERAL_BASE + 12;

    /**
     * Thrown when couldn't find a class in readClass in CDRInputStream.
     */
    public static final int CNFE_READ_CLASS = ORBConstants.GENERAL_BASE + 13;

    /**
     * Bad repository ID indirection.
     */
    public static final int BAD_REP_ID_INDIRECTION = ORBConstants.GENERAL_BASE + 14;

    /**
     * Bad codebase string indirection.
     */
    public static final int BAD_CODEBASE_INDIRECTION = ORBConstants.GENERAL_BASE + 15;

    /**
     * An unknown code set was specified by the client ORB as one of the
     * negotiated code sets.  This can only occur if there is a bug in
     * the client side's code set negotiation.
     */
    public static final int UNKNOWN_CODESET = ORBConstants.GENERAL_BASE + 16;

    /**
     * wchar/wstring data in GIOP 1.0
     *
     * As part of the resolution for issue 3681, a MARSHAL exception must
     * be thrown when wchar/wstring data is sent in GIOP 1.0.  The minor
     * codes will be standard, but aren't available, yet.
     */
    public static final int WCHAR_DATA_IN_GIOP_1_0 = ORBConstants.GENERAL_BASE + 17;

    /**
     * String or wstring with a negative length.  This is usually due to
     * other problems in the stream, probably custom marshalling.
     */
    public static final int NEGATIVE_STRING_LENGTH = ORBConstants.GENERAL_BASE + 18;

    /**
     * Someone called CDRInputStream read_value(expectedType) with a null
     * parameter and there was no repository ID information on the wire.
     */
    public static final int EXPECTED_TYPE_NULL_AND_NO_REP_ID
        = ORBConstants.GENERAL_BASE + 19;

    /**
     * Someone called CDRInputStream read_value() and there was no
     * repository ID information on the wire.
     */
    public static final int READ_VALUE_AND_NO_REP_ID
        = ORBConstants.GENERAL_BASE + 20;

    /**
     * Thrown in CDROutputStream_1_0 when an error occurs while
     * connecting a servant.
     */
    public static final int CONNECTING_SERVANT = ORBConstants.GENERAL_BASE + 21;

    /**
     * We received (or think we received) an end tag which is less
     * than the one we were expecting.  That means that the sender
     * must think there are more enclosing chunked valuetypes than
     * we do.
     */
    public static final int UNEXPECTED_ENCLOSING_VALUETYPE = ORBConstants.GENERAL_BASE + 22;

    /**
     * We read (or think we read) an end tag whose value was
     * [0, 0x7fffff00).  End tags are always negative.
     * There is a very obscure case where the read end tag
     * code might encounter a nested valuetype's value tag
     * if a custom marshaler leaves too much data on the wire,
     * so this isn't thrown if the long we read had a
     * value greater than or equal to 0x7fffff00 (the minimum
     * value tag).
     */
    public static final int POSITIVE_END_TAG = ORBConstants.GENERAL_BASE + 23;

    /**
     * The client thread tried to get its out call descriptor
     * from the table, but it was null.
     */
    public static final int NULL_OUT_CALL = ORBConstants.GENERAL_BASE + 24;

    /**
     * write_Object called with a org.omg.CORBA.LocalObject.
     * IDL to Java formal 01-06-06 1.21.4.2.  No standard minor code
     * given.
     */
    public static final int WRITE_LOCAL_OBJECT = ORBConstants.GENERAL_BASE + 25;

    /**
     * Attempted to insert something besides an ObjectImpl
     * into an Any via insert_Object.
     */
    public static final int BAD_INSERTOBJ_PARAM = ORBConstants.GENERAL_BASE + 26;

/////////////////////////////////////////////////////////////////
// NO_IMPLEMENT minor cores
/////////////////////////////////////////////////////////////////
    public static final int GENERIC_NO_IMPL = ORBConstants.GENERAL_BASE + 1 ;
    public static final int CONTEXT_NOT_IMPLEMENTED = ORBConstants.GENERAL_BASE + 2 ; 

    /** 
     * get_interface is not implemented on server.
     */
    public static final int GETINTERFACE_NOT_IMPLEMENTED = ORBConstants.GENERAL_BASE + 3 ;
    /** 
     * deferred sends are not implemented.
     */
    public static final int SEND_DEFERRED_NOTIMPLEMENTED = ORBConstants.GENERAL_BASE + 4 ;


/////////////////////////////////////////////////////////////////
// OBJ_ADAPTER minor codes
/////////////////////////////////////////////////////////////////
    /** 
     * There was no subcontract found that matches the one in 
     * the object key when dispatching the request on the server
     * side to the object adapter layer.
     */
    public static final int NO_SERVER_SC_IN_DISPATCH = ORBConstants.GENERAL_BASE + 1 ;
    /** 
     * There was no subcontract found that matches the one in 
     * the object key when dispatching the locate request on the server
     * side to the object adapter layer.
     */
    /** 
     * Error occured when trying to connect a servant to the ORB.
     */
    public static final int ORB_CONNECT_ERROR = ORBConstants.GENERAL_BASE + 2 ;

/////////////////////////////////////////////////////////////////
// OBJECT_NOT_EXIST
/////////////////////////////////////////////////////////////////
    // from IIOPConnection.java
    /** 
     * The locate request got the response indicating that the
     * object is not known to the locator.
     */
    public static final int LOCATE_UNKNOWN_OBJECT = ORBConstants.GENERAL_BASE + 1 ; 
    // from GenericServerSC.java
    /** 
     * The server id of the server that received the request does
     * not match the server id baked into the object key of the
     * object reference that was invoked upon.
     */
    public static final int BAD_SERVER_ID = ORBConstants.GENERAL_BASE + 2 ; 
  
    /** 
     * No skeleton was found on the server side that matches the
     * contents of the object key inside the object reference.
     */
    // There is another minor code with that name in com.sun.PortableServer.MinorCodes
    public static final int BAD_SKELETON = ORBConstants.GENERAL_BASE + 3 ; 

    public static final int SERVANT_NOT_FOUND = ORBConstants.GENERAL_BASE + 4 ; 


/////////////////////////////////////////////////////////////////
// TRANSIENT minor codes
/////////////////////////////////////////////////////////////////

    /** 
     * ptc/00-08-06 table 4-1 
     */
    public static final int REQUEST_CANCELED = OMGVMCID.value + 3 ;


/////////////////////////////////////////////////////////////////
// UNKNOWN minor codes
/////////////////////////////////////////////////////////////////
    /** 
     * Unknown user exception encountered while unmarshalling.
     */
    public static final int UNKNOWN_CORBA_EXC = ORBConstants.GENERAL_BASE + 1 ;
    /** 
     * Unknown user exception thrown by the server implementation.
     */
    public static final int RUNTIMEEXCEPTION = ORBConstants.GENERAL_BASE + 2 ;
    /** 
     * Unknown exception/error thrown by the ORB/application implementation.
     */
    public static final int UNKNOWN_SERVER_ERROR = ORBConstants.GENERAL_BASE + 3 ;
    /**
     * Error while marshaling SystemException after DSI-based invocation. 
     */
    public static final int UNKNOWN_DSI_SYSEX = ORBConstants.GENERAL_BASE + 4;

    /*
     * Error while unmarshalling SystemException
     */
    public static final int UNKNOWN_SYSEX = ORBConstants.GENERAL_BASE + 5;

}
