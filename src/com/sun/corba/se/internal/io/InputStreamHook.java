/*
 * @(#)InputStreamHook.java	1.15 03/01/23
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

package com.sun.corba.se.internal.io;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

public abstract class InputStreamHook extends ObjectInputStream
{
    private class HookGetFields extends ObjectInputStream.GetField {
	private Map fields = null;

	HookGetFields(Map fields){
	    this.fields = fields;
	}

	/**
	 * Get the ObjectStreamClass that describes the fields in the stream.
         *
         * REVISIT!  This doesn't work since we have our own ObjectStreamClass.
	 */
	public java.io.ObjectStreamClass getObjectStreamClass() {
	    return null;
	}
		
	/**
	 * Return true if the named field is defaulted and has no value
	 * in this stream.
	 */
	public boolean defaulted(String name)
	    throws IOException, IllegalArgumentException  {
	    return (!fields.containsKey(name));
	}
		
	/**
	 * Get the value of the named boolean field from the persistent field.
	 */
	public boolean get(String name, boolean defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Boolean)fields.get(name)).booleanValue();
	}
		
	/**
	 * Get the value of the named char field from the persistent fields.
	 */
	public char get(String name, char defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Character)fields.get(name)).charValue();

	}
		
	/**
	 * Get the value of the named byte field from the persistent fields.
	 */
	public byte get(String name, byte defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Byte)fields.get(name)).byteValue();

	}
		
	/**
	 * Get the value of the named short field from the persistent fields.
	 */
	public short get(String name, short defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Short)fields.get(name)).shortValue();

	}
		
	/**
	 * Get the value of the named int field from the persistent fields.
	 */
	public int get(String name, int defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Integer)fields.get(name)).intValue();

	}
		
	/**
	 * Get the value of the named long field from the persistent fields.
	 */
	public long get(String name, long defvalue)
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Long)fields.get(name)).longValue();

	}
		
	/**
	 * Get the value of the named float field from the persistent fields.
	 */
	public float get(String name, float defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return ((Float)fields.get(name)).floatValue();

	}
		
	/**
	 * Get the value of the named double field from the persistent field.
	 */
	public double get(String name, double defvalue) 
	    throws IOException, IllegalArgumentException  {
	    if (defaulted(name))
		return defvalue;
	    else return ((Double)fields.get(name)).doubleValue();

	}
		
	/**
	 * Get the value of the named Object field from the persistent field.
	 */
	public Object get(String name, Object defvalue) 
	    throws IOException, IllegalArgumentException {
	    if (defaulted(name))
		return defvalue;
	    else return fields.get(name);

	}
		
	public String toString(){
	    return fields.toString();
	}
    }

    public InputStreamHook()
	throws IOException {
	super();
    }
    public void defaultReadObject()
	throws IOException, ClassNotFoundException, NotActiveException
    {
    	defaultReadObjectDelegate();
    }

    public abstract void defaultReadObjectDelegate();

    abstract void readFields(java.util.Map fieldToValueMap)
        throws java.io.InvalidClassException, java.io.StreamCorruptedException,
               ClassNotFoundException, java.io.IOException;

    public ObjectInputStream.GetField readFields()
    	throws IOException, ClassNotFoundException, NotActiveException {

        HashMap fieldValueMap = new HashMap();

        readFields(fieldValueMap);

	return new HookGetFields(fieldValueMap);
    }
}
