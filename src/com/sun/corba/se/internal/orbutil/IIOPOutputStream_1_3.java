/*
 * @(#)IIOPOutputStream_1_3.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.orbutil;

import java.io.*;

/**
 * Implements legacy behavior from before Ladybird to maintain
 * backwards compatibility.
 */
public class IIOPOutputStream_1_3 extends com.sun.corba.se.internal.io.IIOPOutputStream
{
    // We can't assume that the superclass's putFields
    // member will be non-private.  We must allow
    // the RI to run on JDK 1.3.1 FCS as well as
    // the JDK 1.3.1_01 patch.
    private ObjectOutputStream.PutField putFields_1_3;

    // The newer version in the io package correctly writes a wstring instead.
    // This concerns bug 4379597.
    protected void internalWriteUTF(org.omg.CORBA.portable.OutputStream stream, 
                                    String data)
    {
        stream.write_string(data);
    }

    public IIOPOutputStream_1_3()
	throws java.io.IOException {
	super();
    }

    /**
     * Before JDK 1.3.1_01, the PutField/GetField implementation
     * actually sent a Hashtable.
     */
    public ObjectOutputStream.PutField putFields()
	throws IOException {
	putFields_1_3 = new LegacyHookPutFields();
	return putFields_1_3;
    }

    public void writeFields()
	throws IOException {
	putFields_1_3.write(this);
    }
}
