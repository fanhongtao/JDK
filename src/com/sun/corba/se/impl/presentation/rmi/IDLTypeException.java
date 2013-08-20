/*
 * @(#)IDLTypeException.java	1.2 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.presentation.rmi ;

/**
 * Checked exception containing information about an
 * an IDL type validation.
 */
public class IDLTypeException extends Exception {

    public IDLTypeException() {}

    public IDLTypeException(String message) {
        super(message);
    }

}
