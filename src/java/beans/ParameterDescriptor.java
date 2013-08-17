/*
 * @(#)ParameterDescriptor.java	1.14 98/09/21
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

/**
 * The ParameterDescriptor class allows bean implementors to provide
 * additional information on each of their parameters, beyond the
 * low level type information provided by the java.lang.reflect.Method
 * class.
 * <p>
 * Currently all our state comes from the FeatureDescriptor base class.
 */

public class ParameterDescriptor extends FeatureDescriptor {

    /**
     * Public default constructor.
     */
    public ParameterDescriptor() {
    }

    /**
     * Package private dup constructor.
     * This must isolate the new object from any changes to the old object.
     */	
    ParameterDescriptor(ParameterDescriptor old) {
	super(old);
    }

}
