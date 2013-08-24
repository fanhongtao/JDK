/*
 * @(#)KeyAlreadyExistsException.java	3.19 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.management.openmbean;

/**
 * This runtime exception is thrown to indicate that the index of a row to be added to a <i>tabular data</i> instance
 * is already used to refer to another row in this <i>tabular data</i> instance.
 *
 * @version     3.19  05/11/17
 * @author      Sun Microsystems, Inc.
 *
 * @since 1.5
 * @since.unbundled JMX 1.1
 */
public class KeyAlreadyExistsException extends IllegalArgumentException {

    private static final long serialVersionUID = 1845183636745282866L;

    /**
     * A KeyAlreadyExistsException with no detail message.
     */
    public KeyAlreadyExistsException() {
	super();
    }

    /**
     * A KeyAlreadyExistsException with a detail message.
     *
     * @param msg the detail message.
     */
    public KeyAlreadyExistsException(String msg) {
	super(msg);
    }

}
