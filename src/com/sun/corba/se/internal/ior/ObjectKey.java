/*
 * @(#)ObjectKey.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/ObjectKey.java

package com.sun.corba.se.internal.ior;

import com.sun.corba.se.internal.ior.Writeable ;
import com.sun.corba.se.internal.ior.ObjectKeyTemplate ;
import com.sun.corba.se.internal.ior.ObjectId ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.ORB ;
import com.sun.corba.se.internal.corba.EncapsOutputStream ;

/**
 * @author 
 */
public class ObjectKey implements Writeable 
{
    private ObjectKeyTemplate template;
    private ObjectId id;
    
    public boolean equals( Object obj )
    {
	if (obj == null)
	    return false ;

	if (!(obj instanceof ObjectKey))
	    return false ;

	ObjectKey other = (ObjectKey)obj ;

	return template.equals( other.template ) &&
	    id.equals( other.id ) ;
    }

    public ObjectKeyTemplate getTemplate() 
    {
	return template ;
    }

    public ObjectId getId()
    {
	return id ;
    }

    public ObjectKey( ObjectKeyTemplate template, ObjectId id ) 
    {
	this.template = template ;
	this.id = id ;
    }

    public void write( OutputStream os ) 
    {
	template.write( id, os ) ;
    }

    public byte[] getBytes( ORB orb ) 
    {
	EncapsOutputStream os = new EncapsOutputStream( orb ) ;
	write( os ) ;
	return os.toByteArray() ;
    }
}
