/*
 * @(#)POAIdPOAView.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#)POAIdPOAView.java	1.5 03/01/23

package com.sun.corba.se.internal.ior ;

import java.util.Iterator ;
import java.util.Stack ;

import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.INTERNAL ;
import com.sun.corba.se.internal.ior.POAView ;
import com.sun.corba.se.internal.ior.POAIdBase ;

public class POAIdPOAView extends POAIdBase {
    private final POAView poa ;

    public POAIdPOAView( POAView poa )
    {
	if (poa == null)
	    throw new INTERNAL() ;

	this.poa = poa ;
    }

    public int getNumLevels()
    {
	return poa.getNumLevels() ;
    }

    public Iterator iterator()
    {
	final Stack stack = new Stack() ;
	POAView current = poa ;
	POAView root = null ;
	while ((root = current.getParent()) != null) {
	    stack.push( current.the_name() ) ;
	    current = root ;
	}

	return new Iterator() {
	    public boolean hasNext() {
		return !stack.empty() ;
	    }

	    public Object next() {
		return stack.pop() ;
	    }

	    public void remove()
	    {
		throw new UnsupportedOperationException() ;
	    }
	} ;
    }
}

