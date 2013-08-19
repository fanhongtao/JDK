/*
 * @(#)DynAnyFactoryImpl.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.DynamicAny;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TCKind;

import org.omg.DynamicAny.*;
import org.omg.DynamicAny.DynAnyFactoryPackage.*;

public class DynAnyFactoryImpl
    extends org.omg.CORBA.LocalObject
    implements org.omg.DynamicAny.DynAnyFactory
{
    //
    // Instance variables
    //

    private org.omg.CORBA.ORB orb;

    //
    // Constructors
    //

    private DynAnyFactoryImpl() {
        this.orb = null;
    }

    public DynAnyFactoryImpl(ORB orb) {
        this.orb = orb;
    }

    //
    // DynAnyFactory interface methods
    //

    // Returns the most derived DynAny type based on the Anys TypeCode.
    public org.omg.DynamicAny.DynAny create_dyn_any (org.omg.CORBA.Any any)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode
    {
        return DynAnyUtil.createMostDerivedDynAny(any, orb, true);
    }

    // Returns the most derived DynAny type based on the TypeCode.
    public org.omg.DynamicAny.DynAny create_dyn_any_from_type_code (org.omg.CORBA.TypeCode type)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode
    {
        return DynAnyUtil.createMostDerivedDynAny(type, orb);
    }

    // Needed for org.omg.CORBA.Object

    private String[] __ids = { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };

    public String[] _ids() {
        return __ids;
    }
}
