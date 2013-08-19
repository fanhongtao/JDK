/*
 * @(#)ObjectId.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//Source file: J:/ws/serveractivation/src/share/classes/com.sun.corba.se.internal.ior/ObjectId.java

package com.sun.corba.se.internal.ior;

import java.util.Arrays ;

/**
 * @author 
 */
public final class ObjectId 
{
    private byte[] id;
    
    public boolean equals( Object obj )
    {
	if (!(obj instanceof ObjectId))
	    return false ;

	ObjectId other = (ObjectId)obj ;

	return Arrays.equals( this.id, other.id ) ;
    }

    public ObjectId( byte[] id ) 
    {
	this.id = id ;
    }

    public byte[] getId()
    {
	return id ;
    }
}
