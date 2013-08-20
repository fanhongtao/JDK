/*
 * @(#)TreeSelectionListener.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.util.EventListener;

/**
 * The listener that's notified when the selection in a TreeSelectionModel
 * changes.
 * For more information and examples see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/events/treeselectionlistener.html">How to Write a Tree Selection Listener</a>,
 * a section in <em>The Java Tutorial.</em>
 *
 * @see javax.swing.tree.TreeSelectionModel
 * @see javax.swing.JTree
 *
 * @version 1.14 12/19/03
 * @author Scott Violet
 */
public interface TreeSelectionListener extends EventListener
{
    /** 
      * Called whenever the value of the selection changes.
      * @param e the event that characterizes the change.
      */
    void valueChanged(TreeSelectionEvent e);
}
