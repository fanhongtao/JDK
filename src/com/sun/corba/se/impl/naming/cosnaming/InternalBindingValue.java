/*
 * @(#)InternalBindingValue.java	1.24 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.NameComponent;

/**
 * Class InternalBindingKey acts as a container for two objects, namely
 * a org.omg.CosNaming::Binding and an CORBA object reference, which are the two
 * components associated with the binding.
 */
public class InternalBindingValue
{
    public Binding theBinding;
    public String strObjectRef;
    public org.omg.CORBA.Object theObjectRef;
  
    // Default constructor
    public InternalBindingValue() {}

    // Normal constructor
    public InternalBindingValue(Binding b, String o) {
	theBinding = b;
	strObjectRef = o;
    }
}
