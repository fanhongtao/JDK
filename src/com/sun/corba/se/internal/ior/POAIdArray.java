/*
 * @(#)POAIdArray.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)POAIdArray.java	1.4 03/01/23

package com.sun.corba.se.internal.ior ;

import java.util.Iterator ;
import org.omg.CORBA_2_3.portable.OutputStream ;

public class POAIdArray extends POAIdBase {
    private final String[] poaId ;

    public POAIdArray( String[] poaId )
    {
	this.poaId = poaId ;
    }

    public int getNumLevels()
    {
	return poaId.length ;
    }

    public Iterator iterator()
    {
	return new Iterator() {
	    int current = 0 ;

	    public boolean hasNext() {
		return current < poaId.length ;
	    }

	    public Object next() {
		return poaId[ current++ ] ;
	    }

	    public void remove()
	    {
		throw new UnsupportedOperationException() ;
	    }
	} ;
    }
}
