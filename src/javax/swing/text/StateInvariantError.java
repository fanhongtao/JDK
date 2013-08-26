/*
 * @(#)StateInvariantError.java	1.19 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

/**
 * This exception is to report the failure of state invarient 
 * assertion that was made.  This indicates an internal error
 * has occurred.
 * 
 * @author  Timothy Prinzing
 * @version 1.19 03/23/10
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
