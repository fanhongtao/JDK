/*
 * @(#)TreeExpansionListener.java	1.5 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.event;

import java.util.EventListener;

/**
  * The listener that's notified when a tree expands or collapses
  * a node.
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
