/*
 * @(#)TreeModelListener.java	1.11 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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
 * TreeChangeListener defines the interface for an object that listens
 * to changes in a TreeModel.
 *
 * @version 1.11 04/22/99
 * @author Rob Davis
 * @author Ray Ryan
 */
public interface TreeModelListener extends EventListener {

    /**
     * <p>Invoked after a node (or a set of siblings) has changed in some
     * way. The node(s) have not changed locations in the tree or
     * altered their children arrays, but other attributes have
     * changed and may affect presentation. Example: the name of a
     * file has changed, but it is in the same location in the file
     * system.</p>
     * <p>To indicate the root has changed, childIndices and children
     * will be null. </p>
     * 
     * <p>e.path() returns the path the parent of the changed node(s).</p>
     * 
     * <p>e.childIndices() returns the index(es) of the changed node(s).</p>
     */
    void treeNodesChanged(TreeModelEvent e);

    /**
     * <p>Invoked after nodes have been inserted into the tree.</p>
     * 
     * <p>e.path() returns the parent of the new nodes
     * <p>e.childIndices() returns the indices of the new nodes in
     * ascending order.
     */
    void treeNodesInserted(TreeModelEvent e);

    /**
     * <p>Invoked after nodes have been removed from the tree.  Note that
     * if a subtree is removed from the tree, this method may only be
     * invoked once for the root of the removed subtree, not once for
     * each individual set of siblings removed.</p>
     *
     * <p>e.path() returns the former parent of the deleted nodes.</p>
     * 
     * <p>e.childIndices() returns the indices the nodes had before they were deleted in ascending order.</p>
     */
    void treeNodesRemoved(TreeModelEvent e);

    /**
     * <p>Invoked after the tree has drastically changed structure from a
     * given node down.  If the path returned by e.getPath() is of length
     * one and the first element does not identify the current root node
     * the first element should become the new root of the tree.<p>
     * 
     * <p>e.path() holds the path to the node.</p>
     * <p>e.childIndices() returns null.</p>
     */
    void treeStructureChanged(TreeModelEvent e);

}
