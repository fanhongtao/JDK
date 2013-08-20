/*
 * @(#)TreeExpansionListener.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.util.EventListener;

/**
  * The listener that's notified when a tree expands or collapses
  * a node.
  * For further documentation and examples see
  * <a
  href="http://java.sun.com/docs/books/tutorial/uiswing/events/treeexpansionlistener.html">How to Write a Tree Expansion Listener</a>,
  * a section in <em>The Java Tutorial.</em>
  *
  * @author Scott Violet
  */

public interface TreeExpansionListener extends EventListener
{
    /**
      * Called whenever an item in the tree has been expanded.
      */
    public void treeExpanded(TreeExpansionEvent event);

    /**
      * Called whenever an item in the tree has been collapsed.
      */
    public void treeCollapsed(TreeExpansionEvent event);
}
