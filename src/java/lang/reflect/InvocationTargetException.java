/*
 * @(#)InvocationTargetException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
