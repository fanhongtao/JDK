/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import javax.swing.event.TreeModelEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Enumeration;

/**
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.11 02/06/02
 * @author Scott Violet
 */

public abstract class AbstractLayoutCache implements RowMapper {
    /** Object responsible for getting the size of a node. */
    protected NodeDimensions     nodeDimensions;

    /** Model providing information. */
    protected TreeModel          treeModel;

    /** Selection model. */
    protected TreeSelectionModel treeSelectionModel;

    /**
     * True if the root node is displayed, false if its children are
     * the highest visible nodes.
     */
    protected boolean            rootVisible;

    /**
      * Height to use for each row.  If this is <= 0 the renderer will be
      * used to determine the height for each row.
      */
    protected int                rowHeight;


    /**
     * Sets the renderer that is responsible for drawing nodes in the tree
     * and which is threfore responsible foc calculating the dimensions of
     * individual nodes.
     *
     * @param nd a NodeDimensions object
     */
    public void setNodeDimensions(NodeDimensions nd) {
	this.nodeDimensions = nd;
    }

    /**
     * Returns the object that renders nodes in the tree, and which is 
     * responsible for calculating the dimensions of individual nodes.
     *
     * @return the NodeDimensions object
     */
    public NodeDimensions getNodeDimensions() {
	return nodeDimensions;
    }

    /**
     * Sets the TreeModel that will provide the data.
     *
     * @param newModel the TreeModel that is to provide the data
     */
    public void setModel(TreeModel newModel) {
        treeModel = newModel;
    }

    /**
     * Returns the TreeModel that is providing the data.
     *
     * @return the TreeModel that is providing the data
     */
    public TreeModel getModel() {
        return treeModel;
    }

    /**
     * Determines whether or not the root node from
     * the TreeModel is visible.
     *
     * @param rootVisible true if the root node of the tree is to be displayed
     * @see #rootVisible
     * @beaninfo
     *        bound: true
     *  description: Whether or not the root node
     *               from the TreeModel is visible.
     */
    public void setRootVisible(boolean rootVisible) {
        this.rootVisible = rootVisible;
    }

    /**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     * @see #rootVisible
     */
    public boolean isRootVisible() {
        return rootVisible;
    }

    /**
     * Sets the height of each cell.  If the specified value
     * is less than or equal to zero the current cell renderer is
     * queried for each row's height.
     *
     * @param rowHeight the height of each cell, in pixels
     * @beaninfo
     *        bound: true
     *  description: The height of each cell.
     */
    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    /**
     * Returns the height of each row.  If the returned value is less than
     * or equal to 0 the height for each row is determined by the
     * renderer.
     *
     * @param the height of each cell, in pixels. Zero or negative if the
     *        height of each row is determined by the tree cell renderer
     */
    public int getRowHeight() {
        return rowHeight;
    }

    /**
     * Sets the TreeSelectionModel used to manage the selection to
     * new LSM.
     */
    public void setSelectionModel(TreeSelectionModel newLSM) {
	if(treeSelectionModel != null)
	    treeSelectionModel.setRowMapper(null);
	treeSelectionModel = newLSM;
	if(treeSelectionModel != null)
	    treeSelectionModel.setRowMapper(this);
    }

    /**
     * Returns the model used to maintain the selection.
     */
    public TreeSelectionModel getSelectionModel() {
	return treeSelectionModel;
    }

    /**
     * Returns the preferred height.
     */
    public int getPreferredHeight() {
	// Get the height
	int           rowCount = getRowCount();

	if(rowCount > 0) {
	    Rectangle     bounds = getBounds(getPathForRow(rowCount - 1),
					     null);

	    if(bounds != null)
		return bounds.y + bounds.height;
	}
	return 0;
    }

    /**
     * Returns the preferred width for the passed in region. If
     * <code>bounds</code> is null, the preferred width for all the nodes
     * will be returned (and this may be VERY expensive).
     */
    public int getPreferredWidth(Rectangle bounds) {
	int           rowCount = getRowCount();

	if(rowCount > 0) {
	    // Get the width
	    TreePath      firstPath;
	    int           endY;

	    if(bounds == null) {
		firstPath = getPathForRow(0);
		endY = Integer.MAX_VALUE;
	    }
	    else {
		firstPath = getPathClosestTo(bounds.x, bounds.y);
		endY = bounds.height + bounds.y;
	    }

	    Enumeration   paths = getVisiblePathsFrom(firstPath);

	    if(paths != null && paths.hasMoreElements()) {
		Rectangle   pBounds = getBounds((TreePath)paths.nextElement(),
						null);
		int         width;

		if(pBounds != null) {
		    width = pBounds.x + pBounds.width;
		    if (pBounds.y >= endY) {
			return width;
		    }
		}
		else
		    width = 0;
		while (pBounds != null && paths.hasMoreElements()) {
		    pBounds = getBounds((TreePath)paths.nextElement(),
					pBounds);
		    if (pBounds != null && pBounds.y < endY) {
			width = Math.max(width, pBounds.x + pBounds.width);
		    }
		    else {
			pBounds = null;
		    }
		}
		return width;
	    }
	}
	return 0;
    }

    //
    // Abstract methods that must be implemented to be concrete.
    //

    /**
      * Returns true if the value identified by row is currently expanded.
      */
    public abstract boolean isExpanded(TreePath path);

    /**
     * Returns a rectangle giving the bounds needed to draw path.
     *
     * @param path     a TreePath specifying a node
     * @param placeIn  a Rectangle object giving the available space
     * @return a Rectangle object specifying the space to be used
     */
    public abstract Rectangle getBounds(TreePath path, Rectangle placeIn);

    /**
      * Returns the path for passed in row.  If row is not visible
      * null is returned.
      */
    public abstract TreePath getPathForRow(int row);

    /**
      * Returns the row that the last item identified in path is visible
      * at.  Will return -1 if any of the elements in path are not
      * currently visible.
      */
    public abstract int getRowForPath(TreePath path);

    /**
      * Returns the path to the node that is closest to x,y.  If
      * there is nothing currently visible this will return null, otherwise
      * it'll always return a valid path.  If you need to test if the
      * returned object is exactly at x, y you should get the bounds for
      * the returned path and test x, y against that.
      */
    public abstract TreePath getPathClosestTo(int x, int y);

    /**
     * Returns an Enumerator that increments over the visible paths
     * starting at the passed in location. The ordering of the enumeration
     * is based on how the paths are displayed. The first element of the
     * returned enumeration will be path, unless it isn't visible, in
     * which case null will be returned.
     */
    public abstract Enumeration getVisiblePathsFrom(TreePath path);

    /**
     * Returns the number of visible children for row.
     */
    public abstract int getVisibleChildCount(TreePath path);

    /**
     * Marks the path <code>path</code> expanded state to
     * <code>isExpanded</code>.
     */
    public abstract void setExpandedState(TreePath path, boolean isExpanded);

    /**
     * Returns true if the path is expanded, and visible.
     */
    public abstract boolean getExpandedState(TreePath path);

    /**
     * Number of rows being displayed.
     */
    public abstract int getRowCount();

    /**
     * Informs the TreeState that it needs to recalculate all the sizes
     * it is referencing.
     */
    public abstract void invalidateSizes();

    /**
     * Instructs the LayoutCache that the bounds for <code>path</code>
     * are invalid, and need to be updated.
     */
    public abstract void invalidatePathBounds(TreePath path);

    //
    // TreeModelListener methods
    // AbstractTreeState does not directly become a TreeModelListener on
    // the model, it is up to some other object to forward these methods.
    //

    /**
     * <p>Invoked after a node (or a set of siblings) has changed in some
     * way. The node(s) have not changed locations in the tree or
     * altered their children arrays, but other attributes have
     * changed and may affect presentation. Example: the name of a
     * file has changed, but it is in the same location in the file
     * system.</p>
     *
     * <p>e.path() returns the path the parent of the changed node(s).</p>
     *
     * <p>e.childIndices() returns the index(es) of the changed node(s).</p>
     */
    public abstract void treeNodesChanged(TreeModelEvent e);

    /**
     * <p>Invoked after nodes have been inserted into the tree.</p>
     *
     * <p>e.path() returns the parent of the new nodes
     * <p>e.childIndices() returns the indices of the new nodes in
     * ascending order.
     */
    public abstract void treeNodesInserted(TreeModelEvent e);

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
    public abstract void treeNodesRemoved(TreeModelEvent e);

    /**
     * <p>Invoked after the tree has drastically changed structure from a
     * given node down.  If the path returned by e.getPath() is of length
     * one and the first element does not identify the current root node
     * the first element should become the new root of the tree.<p>
     *
     * <p>e.path() holds the path to the node.</p>
     * <p>e.childIndices() returns null.</p>
     */
    public abstract void treeStructureChanged(TreeModelEvent e);

    //
    // RowMapper
    //

    /**
     * Returns the rows that the TreePath instances in <code>path</code>
     * are being displayed at. The receiver should return an array of
     * the same length as that passed in, and if one of the TreePaths
     * in <code>path</code> is not valid its entry in the array should
     * be set to -1.
     */
    public int[] getRowsForPaths(TreePath[] paths) {
	if(paths == null)
	    return null;

	int               numPaths = paths.length;
	int[]             rows = new int[numPaths];

	for(int counter = 0; counter < numPaths; counter++)
	    rows[counter] = getRowForPath(paths[counter]);
	return rows;
    }

    //
    // Local methods that subclassers may wish to use that are primarly
    // convenience methods.
    //

    /**
     * Returns, by reference in size, the size needed to reprensent
     * value. If size is null, a newly created Dimension should be returned,
     * otherwise the value should be placed in size and returned. This will
     * return null if there is no renderer.
     */
    protected Rectangle getNodeDimensions(Object value, int row, int depth,
					  boolean expanded,
					  Rectangle placeIn) {
	NodeDimensions            nd = getNodeDimensions();

	if(nd != null) {
	    return nd.getNodeDimensions(value, row, depth, expanded, placeIn);
	}
	return null;
    }

    /**
      * Returns true if the height of each row is a fixed size.
      */
    protected boolean isFixedRowHeight() {
	return (rowHeight > 0);
    }


    /**
     * Used by AbstractLayoutCache to determing the size and x origin
     * of a particular node.
     */
    static public abstract class NodeDimensions {
	/**
	 * Returns, by reference in bounds, the size and x origin to
	 * place value at. The receiver is responsible for determing
	 * the Y location. If bounds is null, a newly created
	 * Rectangle should be returned, otherwise the value should be
	 * placed in bounds and returned.
	 */
	public abstract Rectangle getNodeDimensions(Object value, int row,
						    int depth,
						    boolean expanded,
						    Rectangle bounds);
    }
}
