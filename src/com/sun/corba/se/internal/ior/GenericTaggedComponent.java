/*
 * @(#)GenericTaggedComponent.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: G:/space/ws/ServerActivation/src/share/classes/com.sun.corba.se.internal.ior/GenericTaggedComponent.java

package com.sun.corba.se.internal.ior;

import org.omg.CORBA.ORB ;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.se.internal.core.GIOPVersion ;

/**
 * @author 
 */
public class GenericTaggedComponent extends GenericIdEncapsulation 
    implements TaggedComponent 
{
    public GenericTaggedComponent( int id, InputStream is ) 
    {
	super( id, is ) ;
    }

    public GenericTaggedComponent( int id, byte[] data ) 
    {
	super( id, data ) ;
    }
    
    /**
     * @return org.omg.IOP.TaggedComponent
     * @exception 
     * @author 
     * @roseuid 3980B6A50196
     */
    public org.omg.IOP.TaggedComponent getIOPComponent( ORB orb ) 
    {
	return new org.omg.IOP.TaggedComponent( getId(), 
	    getData() ) ;
    }
}
