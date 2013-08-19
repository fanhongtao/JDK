/*
 * @(#)LifespanPolicyImpl.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.POA;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

final class LifespanPolicyImpl
    extends org.omg.CORBA.LocalObject implements LifespanPolicy {

    public LifespanPolicyImpl(LifespanPolicyValue value) {
	this.value = value;
    }

    public LifespanPolicyValue value() {
	return value;
    }

    public int policy_type()
    {
	return LIFESPAN_POLICY_ID.value ;
    }

    public Policy copy() {
	return new LifespanPolicyImpl(value);
    }

    public void destroy() {
	value = null;
    }

    private LifespanPolicyValue value;

    public String toString()
    {
	return "LifespanPolicy[" +
	    ((value.value() == LifespanPolicyValue._TRANSIENT) ?
		"TRANSIENT" : "PERSISTENT" + "]") ;
    }
}
