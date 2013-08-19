/*
 * @(#)IIOPInputStream_1_3.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.orbutil;

import java.io.*;
import java.util.Hashtable;

/**
 * Implements legacy behavior from before Ladybird to maintain
 * backwards compatibility.
 */
public class IIOPInputStream_1_3 extends com.sun.corba.se.internal.io.IIOPInputStream
{
    // The newer version in the io package correctly reads a wstring instead.
    // This concerns bug 4379597.
    protected String internalReadUTF(org.omg.CORBA.portable.InputStream stream) 
    {
        return stream.read_string();
    }

    /**
     * Before JDK 1.3.1_01, the PutField/GetField implementation
     * actually sent a Hashtable.
     */
    public ObjectInputStream.GetField readFields()
    	throws IOException, ClassNotFoundException, NotActiveException {
	Hashtable fields = (Hashtable)readObject();
	return new LegacyHookGetFields(fields);
    }

    public IIOPInputStream_1_3()
    	throws java.io.IOException {
        super();
    }
}
