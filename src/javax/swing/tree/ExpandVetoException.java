/*
 * @(#)ExpandVetoException.java	1.7 00/02/02
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
 * See <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/events/treewillexpandlistener.html">How to Write a Tree-Will-Expand Listener</a>
 * in <em>The Java Tutorial</em>
 * for further information and examples.
 *
 * @version 1.7 02/02/00
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
