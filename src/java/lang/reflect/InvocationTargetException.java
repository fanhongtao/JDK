/*
 * @(#)InvocationTargetException.java	1.4 98/07/01
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

package java.lang.reflect;

/**
 * InvocationTargetException is a checked exception that wraps
 * an exception thrown by an invoked method or constructor.
 *
 * @see Method
 * @see Constructor
 * 
 */
public
class InvocationTargetException extends Exception {

    private Throwable target;

    /*
     * 
     */
    protected InvocationTargetException() {
	super();
    }

    /**
     * Constructs a InvocationTargetException with a target exception.
     */
    public InvocationTargetException(Throwable target) {
	super();
	this.target = target;
    }

    /**
     * Constructs a InvocationTargetException with a target exception
     * and a detail message.
     */
    public InvocationTargetException(Throwable target, String s) {
	super(s);
	this.target = target;
    }

    /**
     * Get the thrown target exception.
     */
    public Throwable getTargetException() {
	return target;
    }

}
