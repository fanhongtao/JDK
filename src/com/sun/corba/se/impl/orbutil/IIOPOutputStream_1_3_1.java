/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)IIOPOutputStream_1_3_1.java	1.5 03/12/19
 */
package com.sun.corba.se.impl.orbutil;

import java.io.*;
import java.util.Hashtable;

/**
 * Implements legacy behavior from Ladybird to maintain
 * backwards compatibility.
 */
public class IIOPOutputStream_1_3_1 extends com.sun.corba.se.impl.io.IIOPOutputStream
{
    // We can't assume that the superclass's putFields
    // member will be non-private.  We must allow
    // the RI to run on JDK 1.3.1 FCS as well as
    // the JDK 1.3.1_01 patch.
    private ObjectOutputStream.PutField putFields_1_3_1;

    public IIOPOutputStream_1_3_1()
    	throws java.io.IOException {
        super();
    }

    /**
     * Before JDK 1.3.1_01, the PutField/GetField implementation
     * actually sent a Hashtable.
     */
    public ObjectOutputStream.PutField putFields()
	throws IOException {

	putFields_1_3_1 = new LegacyHookPutFields();
	return putFields_1_3_1;
    }

    public void writeFields()
	throws IOException {

	putFields_1_3_1.write(this);
    }
}

