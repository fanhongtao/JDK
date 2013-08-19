/*
 * @(#)StateInvariantError.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

/**
 * This exception is to report the failure of state invarient 
 * assertion that was made.  This indicates an internal error
 * has occurred.
 * 
 * @author  Timothy Prinzing
 * @version 1.16 01/23/03
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
