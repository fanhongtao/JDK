/*
 * @(#)Operation.java	1.8 98/09/21
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

package java.rmi.server;

/**
 * An <code>Operation</code> contains a description of a Java method.
 * <code>Operation</code> objects were used in JDK1.1 version stubs and
 * skeletons. The <code>Operation</code> class is not needed for JDK1.2 style
 * stubs (stubs generated with <code>rmic -v1.2</code>); hence, this class
 * is deprecated.
 *
 * @version 1.8, 09/21/98
 * @since JDK1.1
 * @deprecated no replacement
 */
public class Operation {
    private String operation;
    
    /**
     * Creates a new Operation object.
     * @deprecated no replacement
     * @since JDK1.1
     */
    public Operation(String op) {
	operation = op;
    }
    
    /**
     * Returns the name of the method.
     * @deprecated no replacement
     * @since JDK1.1
     */
    public String getOperation() {
	return operation;
    }

    /**
     * Returns the string representation of the operation.
     * @deprecated no replacement
     * @since JDK1.1
     */
    public String toString() {
	return operation;
    }
}
