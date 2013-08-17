/*
 * @(#)VetoableChangeListener.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans;

/**
 * A VetoableChange event gets fired whenever a bean changes a "constrained"
 * property.  You can register a VetoableChangeListener with a source bean
 * so as to be notified of any constrained property updates.
 */
public interface VetoableChangeListener extends java.util.EventListener {
    /**
     * This method gets called when a constrained property is changed.
     *
     * @param     evt a <code>PropertyChangeEvent</code> object describing the
     *   	      event source and the property that has changed.
     * @exception PropertyVetoException if the recipient wishes the property
     *              change to be rolled back.
     */
    void vetoableChange(PropertyChangeEvent evt)
				throws PropertyVetoException;
}
