/*
 * @(#)StateInvariantError.java	1.18 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

/**
 * This exception is to report the failure of state invarient 
 * assertion that was made.  This indicates an internal error
 * has occurred.
 * 
 * @author  Timothy Prinzing
 * @version 1.18 11/17/05
 */
class StateInvariantError extends Error
{
    /**
     * Creates a new StateInvariantFailure object.
     *
     * @param s		a string indicating the assertion that failed
     */
    public StateInvariantError(String s) {
	super(s);
    }

}
