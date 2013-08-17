/*
 * @(#)MethodDescriptor.java	1.17 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

import java.lang.reflect.*;

/**
 * A MethodDescriptor describes a particular method that a Java Bean
 * supports for external access from other components.
 */

public class MethodDescriptor extends FeatureDescriptor {

    /**
     * @param method    The low-level method information.
     */
    public MethodDescriptor(Method method) {
	this.method = method;
	setName(method.getName());
    }


    /**
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
     * @return The low-level description of the method
     */
    public Method getMethod() {
	return method;
    }


    /**
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

    private Method method;
    private ParameterDescriptor parameterDescriptors[];
}
