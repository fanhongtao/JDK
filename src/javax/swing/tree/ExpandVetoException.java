/*
 * @(#)ExpandVetoException.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.tree;

import javax.swing.event.TreeExpansionEvent;

/**
 * Exception used to stop and expand/collapse from happening.
 *
 * @version 1.4 08/26/98
 * @author Scott Violet
 */
public class ExpandVetoException extends Exception {
    /** The event that the exception was created for. */
    protected TreeExpansionEvent      event;

    /**
     * Constructs an ExpandVetoException object with no message.
     *
     * @param event  a TreeExpansionEvent object
     */

    public ExpandVetoException(TreeExpansionEvent event) {
	this(event, null);
    }

    /**
     * Constructs an ExpandVetoException object with the specified message.
     *
     * @param event    a TreeExpansionEvent object
     * @param message  a String containing the message
     */
    public ExpandVetoException(TreeExpansionEvent event, String message) {
	super(message);
	this.event = event;
    }
}
