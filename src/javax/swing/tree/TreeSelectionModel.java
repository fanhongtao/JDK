/*
 * @(#)TreeSelectionModel.java	1.14 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import javax.swing.event.*;
import java.beans.PropertyChangeListener;

/**
  * This interface represents the current state of the selection for
  * the tree component.  It will keep track of the selected rows, but
  * in order to select by row you will need to go directly to the tree.
  * <p>resetRowSelection is called from any of the methods that update
  * the selected paths.
  *
  * @version 1.14 11/29/01
  * @author Scott Violet
  */

public interface TreeSelectionModel
{
    /** Selection can only contain one path at a time. */
    public static final int               SINGLE_TREE_SELECTION = 1;

    /** Selection can only be contiguous. This will only be enforced if
     * a RowMapper instance is provided. */
    public static final int               CONTIGUOUS_TREE_SELECTION = 2;

    /** Selection can contain any number of items that are not necessarily
     * contiguous. */
    public static final int               DISCONTIGUOUS_TREE_SELECTION = 4;

    /**
     * Sets the selection model, which must be one of SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
     */
    void setSelectionMode(int mode);

    /**
     * Returns the selection mode.
     */
    int getSelectionMode();

    /**
      * Sets the selection to path.  If this represents a change, then
      * the TreeSelectionListeners are notified.
      *
      * @param path new path to select
      */
    void setSelectionPath(TreePath path);

    /**
      * Sets the selection to the the paths.  If this represents a
      * change the TreeSelectionListeners are notified.
      *
      * @param paths new selection.
      */
    void setSelectionPaths(TreePath[] paths);

    /**
      * Adds path to the current selection.  If path is not currently
      * in the selection the TreeSelectionListeners are notified.
      *
      * @param path the new path to add to the current selection.
      */
    void addSelectionPath(TreePath path);

    /**
      * Adds paths to the current selection.  If any of the paths in
      * paths are not currently in the selection the TreeSelectionListeners
      * are notified.
      *
      * @param path the new path to add to the current selection.
      */
    void addSelectionPaths(TreePath[] paths);

    /**
      * Removes path from the selection.  If path is in the selection
      * The TreeSelectionListeners are notified.
      *
      * @param path the path to remove from the selection.
      */
    void removeSelectionPath(TreePath path);

    /**
      * Removes paths from the selection.  If any of the paths in paths
      * are in the selection the TreeSelectionListeners are notified.
      *
      * @param path the path to remove from the selection.
      */
    void removeSelectionPaths(TreePath[] paths);

    /**
      * Returns the first path in the selection.
      */
    TreePath getSelectionPath();

    /**
      * Returns the paths in the selection.
      */
    TreePath[] getSelectionPaths();

    /**
     * Returns the number of paths that are selected.
     */
    int getSelectionCount();

    /**
      * Returns true if the path, path, is in the current selection.
      */
    boolean isPathSelected(TreePath path);

    /**
      * Returns true if the selection is currently empty.
      */
    boolean isSelectionEmpty();

    /**
      * Empties the current selection.  If this represents a change in the
      * current selection, the selection listeners are notified.
      */
    void clearSelection();

    /**
     * Sets the RowMapper instance.  This instance is used to determine
     * what row corresponds to what path.
     */
    void setRowMapper(RowMapper newMapper);

    /**
     * Returns the RowMapper instance that is able to map a path to a
     * row.
     */
    RowMapper getRowMapper();

    /**
      * Returns all of the currently selected rows.
      */
    int[] getSelectionRows();

    /**
      * Gets the first selected row.
      */
    int getMinSelectionRow();

    /**
      * Gets the last selected row.
      */
    int getMaxSelectionRow();

    /**
      * Returns true if the row identitifed by row is selected.
      */
    boolean isRowSelected(int row);

    /**
     * Updates what rows are selected.  This can be externally called in
     * case the location of the paths change, but not the actual paths.
     * You do not normally need to call this.
     */
    void resetRowSelection();

    /**
     * Returns the lead selection index. That is the last index that was
     * added.
     */
    int getLeadSelectionRow();

    /**
     * Returns the last path that was added.
     */
    TreePath getLeadSelectionPath();

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired in response to an
     * explicit setFont, setBackground, or SetForeground on the
     * current component.  Note that if the current component is
     * inheriting its foreground, background, or font from its
     * container, then no event will be fired in response to a
     * change in the inherited property.
     *
     * @param listener  The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
      * Adds x to the list of listeners that are notified each time the
      * selection changes.
      *
      * @param x the new listener to be added.
      */
    void addTreeSelectionListener(TreeSelectionListener x);

    /**
      * Removes x from the list of listeners that are notified each time
      * the selection changes.
      *
      * @param x the listener to remove.
      */
    void removeTreeSelectionListener(TreeSelectionListener x);
}
