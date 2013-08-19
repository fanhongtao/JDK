/*
 * @(#)MethodDescriptor.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans;

import java.lang.reflect.*;

/**
 * A MethodDescriptor describes a particular method that a Java Bean
 * supports for external access from other components.
 */

public class MethodDescriptor extends FeatureDescriptor {

    /**
     * Constructs a <code>MethodDescriptor</code> from a
     * <code>Method</code>.
     *
     * @param method    The low-level method information.
     */
    public MethodDescriptor(Method method) {
	this.method = method;
	setName(method.getName());
    }


    /**
     * Constructs a <code>MethodDescriptor</code> from a
     * <code>Method</code> providing descriptive information for each
     * of the method's parameters.
     *
     * @param method    The low-level method information.
     * @param parameterDescriptors  Descriptive information for each of the
     *		 		method's parameters.
     */
    public MethodDescriptor(Method method, 
		ParameterDescriptor parameterDescriptors[]) {
	this.method = method;
	this.parameterDescriptors = parameterDescriptors;
	setName(method.getName());
    }

    /**
     * Gets the method that this MethodDescriptor encapsualtes. 
     *
     * @return The low-level description of the method
     */
    public Method getMethod() {
	return method;
    }


    /**
     * Gets the ParameterDescriptor for each of this MethodDescriptor's
     * method's parameters.
     *
     * @return The locale-independent names of the parameters.  May return
     *		a null array if the parameter names aren't known.
     */
    public ParameterDescriptor[] getParameterDescriptors() {
	return parameterDescriptors;
    }

    /*
     * Package-private constructor
     * Merge two method descriptors.  Where they conflict, give the
     * second argument (y) priority over the first argument (x).
     * @param x  The first (lower priority) MethodDescriptor
     * @param y  The second (higher priority) MethodDescriptor
     */

    MethodDescriptor(MethodDescriptor x, MethodDescriptor y) {
	super(x,y);
	method = x.method;
	parameterDescriptors = x.parameterDescriptors;
	if (y.parameterDescriptors != null) {
	    parameterDescriptors = y.parameterDescriptors;
	}
    }

    /*
     * Package-private dup constructor
     * This must isolate the new object from any changes to the old object.
     */
    MethodDescriptor(MethodDescriptor old) {
	super(old);
	method = old.method;	
	if (old.parameterDescriptors != null) {
	    int len = old.parameterDescriptors.length;
	    parameterDescriptors = new ParameterDescriptor[len];
	    for (int i = 0; i < len ; i++) {
	        parameterDescriptors[i] = new ParameterDescriptor(old.parameterDescriptors[i]);
	    }
	}
    }

    private Method method;
    private ParameterDescriptor parameterDescriptors[];
}
