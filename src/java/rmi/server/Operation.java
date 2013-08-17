/*
 * @(#)Operation.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * An <code>Operation</code> contains a description of a Java method.
 * <code>Operation</code> objects were used in JDK1.1 version stubs and
 * skeletons. The <code>Operation</code> class is not needed for JDK1.2 style
 * stubs (stubs generated with <code>rmic -v1.2</code>); hence, this class
 * is deprecated.
 *
 * @version 1.9, 11/29/01
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
