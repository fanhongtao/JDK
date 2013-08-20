/*
 * @(#)ObjectKeyImpl.java	1.17 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.ior.ObjectId ;
import com.sun.corba.se.spi.ior.ObjectKey ;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate ;

import com.sun.corba.se.impl.encoding.EncapsOutputStream ;

/**
 * @author 
 */
public class ObjectKeyImpl implements ObjectKey 
{
    private ObjectKeyTemplate oktemp;
    private ObjectId id;
    
    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof ObjectKeyImpl))
	    return false ;

	ObjectKeyImpl other = (ObjectKeyImpl)obj ;

	return oktemp.equals( other.oktemp ) &&
	    id.equals( other.id ) ;
    }

    public int hashCode()
    {
	return oktemp.hashCode() ^ id.hashCode() ;
    }

    public ObjectKeyTemplate getTemplate() 
    {
	return oktemp ;
    }

    public ObjectId getId()
    {
	return id ;
    }

    public ObjectKeyImpl( ObjectKeyTemplate oktemp, ObjectId id ) 
    {
	this.oktemp = oktemp ;
	this.id = id ;
    }

    public void write( OutputStream os ) 
    {
	oktemp.write( id, os ) ;
    }

    public byte[] getBytes( org.omg.CORBA.ORB orb ) 
    {
	EncapsOutputStream os = new EncapsOutputStream( (ORB)orb ) ;
	write( os ) ;
	return os.toByteArray() ;
    }

    public CorbaServerRequestDispatcher getServerRequestDispatcher( ORB orb ) 
    {
	return oktemp.getServerRequestDispatcher( orb, id ) ;
    }
}
