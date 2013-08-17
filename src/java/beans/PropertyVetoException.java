/*
 * @(#)PropertyVetoException.java	1.7 98/07/01
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

package java.beans;

 
/**
 * A PropertyVetoException is thrown when a proposed change to a
 * property represents an unacceptable value.
 */

public
class PropertyVetoException extends Exception {

 
    /**
     * @param mess Descriptive message
     * @param evt A PropertyChangeEvent describing the vetoed change.
     */
    public PropertyVetoException(String mess, PropertyChangeEvent evt) {
        super(mess);
	this.evt = evt;	
    }

    public PropertyChangeEvent getPropertyChangeEvent() {
	return evt;
    }

    private PropertyChangeEvent evt;
}
