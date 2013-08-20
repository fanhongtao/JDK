/*
 * @(#)ImplicitActivationPolicyImpl.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

final class ImplicitActivationPolicyImpl
    extends org.omg.CORBA.LocalObject implements ImplicitActivationPolicy {

    public
	ImplicitActivationPolicyImpl(ImplicitActivationPolicyValue
				     value) {
	this.value = value;
    }

    public ImplicitActivationPolicyValue value() {
	return value;
    }

    public int policy_type()
    {
	return IMPLICIT_ACTIVATION_POLICY_ID.value ;
    }

    public Policy copy() {
	return new ImplicitActivationPolicyImpl(value);
    }

    public void destroy() {
	value = null;
    }

    private ImplicitActivationPolicyValue value;

    public String toString()
    {
	return "ImplicitActivationPolicy[" +
	    ((value.value() == ImplicitActivationPolicyValue._IMPLICIT_ACTIVATION) ?
		"IMPLICIT_ACTIVATION" : "NO_IMPLICIT_ACTIVATION" + "]") ;
    }
}
