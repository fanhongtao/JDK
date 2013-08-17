/*
 * @(#)Operation.java	1.5 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
