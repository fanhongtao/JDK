/*
 * @(#)IdentifiableContainerBase.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/ContainerBase.java

package com.sun.corba.se.internal.ior;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import com.sun.corba.se.internal.ior.FreezableList ;
import com.sun.corba.se.internal.ior.TaggedComponent ;
import com.sun.corba.se.internal.ior.Identifiable ;

/**
 * @author 
 */
public class IdentifiableContainerBase extends FreezableList
{
    /** This class simply holds some static utility methods.
     * @return 
     * @exception 
     * @author 
     * @roseuid 3910984C0305
     */
    public IdentifiableContainerBase() 
    {
	super( new LinkedList() ) ;
    }
    
    /**
     * @param id
     * @return Iterator
     * @exception 
     * @author 
     * @roseuid 3911CA0D039D
     */
    public Iterator iteratorById( final int id) 
    {
	return new Iterator() {
	    Iterator iter = IdentifiableContainerBase.this.iterator() ;
	    Object current = advance() ;

	    private Object advance()
	    {
		while (iter.hasNext()) {
		    Identifiable ide = (Identifiable)(iter.next()) ;
		    if (ide.getId() == id)
			return ide ;
		}

		return null ;
	    }

	    public boolean hasNext() 
	    {
		return current != null ;
	    }

	    public Object next()
	    {
		Object result = current ;
		current = advance() ;
		return result ;
	    }

	    public void remove()
	    {
		iter.remove() ;
	    }
	} ;
    }
}
