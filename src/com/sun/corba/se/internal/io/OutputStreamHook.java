/*
 * @(#)OutputStreamHook.java	1.11 03/01/23
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
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutput;
import java.util.Hashtable;

public abstract class OutputStreamHook extends ObjectOutputStream
{
    private HookPutFields putFields = null;

    /**
     * Since ObjectOutputStream.PutField methods specify no exceptions,
     * we are not checking for null parameters on put methods.
     */
    private class HookPutFields extends ObjectOutputStream.PutField
    {
	private Hashtable fields = new Hashtable();

	/**
	 * Put the value of the named boolean field into the persistent field.
	 */
	public void put(String name, boolean value){
	    fields.put(name, new Boolean(value));
	}
		
	/**
	 * Put the value of the named char field into the persistent fields.
	 */
	public void put(String name, char value){
	    fields.put(name, new Character(value));
	}
		
	/**
	 * Put the value of the named byte field into the persistent fields.
	 */
	public void put(String name, byte value){
	    fields.put(name, new Byte(value));
	}
		
	/**
	 * Put the value of the named short field into the persistent fields.
	 */
	public void put(String name, short value){
	    fields.put(name, new Short(value));
	}
		
	/**
	 * Put the value of the named int field into the persistent fields.
	 */
	public void put(String name, int value){
	    fields.put(name, new Integer(value));
	}
		
	/**
	 * Put the value of the named long field into the persistent fields.
	 */
	public void put(String name, long value){
	    fields.put(name, new Long(value));
	}
		
	/**
	 * Put the value of the named float field into the persistent fields.
	 *
	 */
	public void put(String name, float value){
	    fields.put(name, new Float(value));
	}
		
	/**
	 * Put the value of the named double field into the persistent field.
	 */
	public void put(String name, double value){
	    fields.put(name, new Double(value));
	}
		
	/**
	 * Put the value of the named Object field into the persistent field.
	 */
	public void put(String name, Object value){
	    fields.put(name, value);
	}
		
	/**
	 * Write the data and fields to the specified ObjectOutput stream.
	 */
	public void write(ObjectOutput out) throws IOException {
            OutputStreamHook hook = (OutputStreamHook)out;

            ObjectStreamField[] osfields = hook.getFieldsNoCopy();

            // Write the fields to the stream in the order
            // provided by the ObjectStreamClass.  (They should
            // be sorted appropriately already.)
            for (int i = 0; i < osfields.length; i++) {

                Object value = fields.get(osfields[i].getName());

                hook.writeField(osfields[i], value);
            }
	}
    }

    abstract void writeField(ObjectStreamField field, Object value) throws IOException;

    public OutputStreamHook()
	throws java.io.IOException {
	super();
		
    }

    public void defaultWriteObject() throws IOException {
	defaultWriteObjectDelegate();
    }

    public abstract void defaultWriteObjectDelegate();
	
    public ObjectOutputStream.PutField putFields()
	throws IOException {
	putFields = new HookPutFields();
	return putFields;
    }

    abstract ObjectStreamField[] getFieldsNoCopy();

    public void writeFields()
	throws IOException {

        putFields.write(this);
    }
}





