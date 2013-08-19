/*
 * @(#)MinorCodes.java	1.37 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.se.internal.util;

import com.sun.corba.se.internal.util.SUNVMCID ;
import org.omg.CORBA.OMGVMCID;

/** Class for minor codes used in the util and io packages.
* Note that all minor codes here must be based on either OMGVMCID,
* for standardized minor codes, or on SUNVMCID, for Sun specific minor
* codes.  Also, the Sun specific minor codes must be in the range
* [ SUNVMCID, SUNVMCID+199 ].
*/
public final class MinorCodes {
    //
    // BAD_PARAM
    //

    // This code is used in com.sun.rmi.util.Utility.throwNotSerializableForCorba().
    // The CORBA equivalent of a java.io.NotSerializableException.
    //
    // It's used in the io and util packages, as well as our
    // javax.rmi.CORBA.Util delegate.
    //
    // This value comes from Java to IDL ptc-00-01-06 1.4.8.
    //
    // Warning: Pre-Merlin Sun ORBs threw BAD_PARAM with
    // SUNVMCID.value + 1 for this case!
    public static final int NOT_SERIALIZABLE = OMGVMCID.value + 6;

    // This Minor code is used in loadStubAndUpdateCache method to 
    // report error when there is an exception in tie._this_object() method
    public static final int NO_POA = SUNVMCID.value + 2;

    //
    // NO_IMPLEMENT exception minor codes
    //

    // from FVDCodeBaseImpl
    public static final int MISSING_LOCAL_VALUE_IMPL = OMGVMCID.value + 1;
    public static final int INCOMPATIBLE_VALUE_IMPL = OMGVMCID.value + 2;

    //
    // DATA_CONVERSION exception minor codes
    //

    public static final int BAD_HEX_DIGIT = SUNVMCID.value + 1 ;
    
    //
    // MARSHAL exception minor codes
    //

    public static final int UNABLE_LOCATE_VALUE_FACTORY = OMGVMCID.value + 1;
    public static final int UNABLE_LOCATE_VALUE_HELPER = SUNVMCID.value + 2;
    public static final int INVALID_INDIRECTION = SUNVMCID.value + 3;

    //
    // UNKNOWN exception minor codes
    //
    public static final int UNKNOWN_SYSEX = SUNVMCID.value + 1 ;
}
