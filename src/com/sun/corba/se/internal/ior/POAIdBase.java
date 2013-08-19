/*
 * @(#)POAIdBase.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)POAIdBase.java	1.4 03/01/23

package com.sun.corba.se.internal.ior ;

import java.util.Iterator ;
import org.omg.CORBA_2_3.portable.OutputStream ;

abstract class POAIdBase implements POAId {
    public boolean equals( Object other ) 
    {
	if (!(other instanceof POAId)) 
	    return false ;

	POAId theOther = (POAId)other ;

	Iterator iter1 = iterator() ;
	Iterator iter2 = theOther.iterator() ;

	while (iter1.hasNext() && iter2.hasNext()) {
	    String str1 = (String)(iter1.next()) ;
	    String str2 = (String)(iter2.next()) ;

	    if (!str1.equals( str2 ))
		return false ;
	}

	return iter1.hasNext() == iter2.hasNext() ;
    }

    public String toString()
    {
	StringBuffer buff = new StringBuffer() ;
	buff.append( "POAID[" ) ;
	Iterator iter = iterator() ;
	boolean first = true ;
	while (iter.hasNext()) {
	    String str = (String)(iter.next()) ;

	    if (first) 
		first = false ;
	    else
		buff.append( "/" ) ;

	    buff.append( str ) ;
	}

	buff.append( "]" ) ;
	
	return buff.toString() ;
    }

    public void write( OutputStream os )
    {
	os.write_long( getNumLevels() ) ;
	Iterator iter = iterator() ;
	while (iter.hasNext()) {
	    String str = (String)(iter.next()) ;
	    os.write_string( str ) ;
	}
    }
}
