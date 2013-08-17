/*
 * @(#)StateInvariantError.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.text;

/**
 * This exception is to report the failure of state invarient 
 * assertion that was made.  This indicates an internal error
 * has occurred.
 * 
 * @author  Timothy Prinzing
 * @version 1.12 08/26/98
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
