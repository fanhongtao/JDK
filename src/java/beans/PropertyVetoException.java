/*
 * @(#)PropertyVetoException.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
