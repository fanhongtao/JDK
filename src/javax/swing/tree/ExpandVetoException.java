/*
 * @(#)ExpandVetoException.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.10 12/19/03
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
