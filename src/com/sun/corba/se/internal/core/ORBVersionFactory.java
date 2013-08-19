/*
 * @(#)ORBVersionFactory.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core ;

import com.sun.corba.se.internal.core.ORBVersion ;
import com.sun.corba.se.internal.core.ORBVersionImpl ;
import org.omg.CORBA.portable.InputStream ;
import org.omg.CORBA.INTERNAL ;

public class ORBVersionFactory {
    private ORBVersionFactory() {} ;

    public static ORBVersion getORBVersion()
    {
	return ORBVersionImpl.NEWER ;
    }

    public static ORBVersion create( InputStream is ) 
    {
	byte value = is.read_octet() ;
	return byteToVersion( value ) ;
    }

    private static ORBVersion byteToVersion( byte value ) 
    {
	/* Throwing an exception here would cause Merlin to be incompatible
	* with future versions of the ORB, to the point that Merlin could
	* not even unmarshal objrefs from a newer version that uses 
	* extended versioning.  Therefore, we will simply treat all 
	* unknown versions as NEWER.
	if (value < 0)
	    throw new INTERNAL() ;
	*/

	switch (value) {
	    case ORBVersion.FOREIGN : return ORBVersionImpl.FOREIGN ;
	    case ORBVersion.OLD : return ORBVersionImpl.OLD ;
	    case ORBVersion.NEW : return ORBVersionImpl.NEW ;
            case ORBVersion.JDK1_3_1_01: return ORBVersionImpl.JDK1_3_1_01 ;
	    case ORBVersion.NEWER : return ORBVersionImpl.NEWER ;
	    default : return ORBVersionImpl.NEWER ;
	}
    }
}
