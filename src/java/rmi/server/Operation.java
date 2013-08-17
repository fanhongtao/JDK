/*
 * @(#)Operation.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * Operation holds a description of a Java method.
 */
public class Operation {
    private String operation;
    
    /**
     * Creates a new Operation object.
     */
    public Operation(String op) {
	operation = op;
    }
    
    /**
     * Returns the name of the method.
     */
    public String getOperation() {
	return operation;
    }
   
    public String toString() {
	return operation;
    }
}
