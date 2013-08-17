/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.tree;

import javax.swing.event.*;

/**
 * The interface that defines a suitable data model for a JTree. 
 * For further information on tree models,
 * including an example of a custom implementation,
 * see <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html">How to Use Trees</a>
 * in <em>The Java Tutorial.</em>
 * 
 * @version 1.17 02/06/02
 * @author Rob Davis
 * @author Ray Ryan
 */
public interface TreeModel
{

    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    public Object getRoot();


    /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public Object getChild(Object parent, int index);


    /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getChildCount(Object parent);


    /**
     * Returns true if <I>node</I> is a leaf.  It is possible for this method
     * to return false even if <I>node</I> has no children.  A directory in a
     * filesystem, for example, may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param   node    a node in the tree, obtained from this data source
     * @return  true if <I>node</I> is a leaf
     */
    public boolean isLeaf(Object node);

    /**
      * Messaged when the user has altered the value for the item identified
      * by <I>path</I> to <I>newValue</I>.  If <I>newValue</I> signifies
      * a truly new value the model should post a treeNodesChanged
      * event.
      *
      * @param path path to the node that the user has altered.
      * @param newValue the new value from the TreeCellEditor.
      */
    public void valueForPathChanged(TreePath path, Object newValue);

    /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child);

//
//  Change Events
//

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @see     #removeTreeModelListener
     * @param   l       the listener to add
     */
    void addTreeModelListener(TreeModelListener l);

    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     */  
    void removeTreeModelListener(TreeModelListener l);

}
