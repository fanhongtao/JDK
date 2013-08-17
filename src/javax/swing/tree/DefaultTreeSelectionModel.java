/*
 * @(#)DefaultTreeSelectionModel.java	1.26 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.BitSet;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.event.*;
import javax.swing.DefaultListSelectionModel;

/**
 * Implementation of TreeSelectionModel.  Listeners are notified whenever
 * the paths in the selection change, not the rows. In order
 * to be able to track row changes you may wish to become a listener 
 * for expansion events on the tree and test for changes from there.
 * <p>resetRowSelection is called from any of the methods that update
 * the selected paths. If you subclass and of these methods to
 * filter what is allowed to be selected, be sure and message
 * resetRowSelection if you do not message super.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.26 11/29/01
 * @author Scott Violet
 */
public class DefaultTreeSelectionModel extends Object implements Cloneable, Serializable, TreeSelectionModel
{
    /** Property name for selectionMode. */
    public static final String          SELECTION_MODE_PROPERTY = "selectionMode";

    /** Used to messaged registered listeners. */
    protected SwingPropertyChangeSupport     changeSupport;

    /** Paths that are currently selected.  Will be null if nothing is
      * currently selected. */
    protected TreePath[]                selection;

    /** Event listener list. */
    protected EventListenerList   listenerList = new EventListenerList();

    /** Provides a row for a given path. */
    transient protected RowMapper               rowMapper;

    /** Handles maintaining the list selection model. */
    protected DefaultListSelectionModel     listSelectionModel;

    /** Mode for the selection, will be either SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
     */
    protected int                           selectionMode;

    /** Last path that was added. */
    protected TreePath                      leadPath;
    /** Index of the lead path in selection. */
    protected int                           leadIndex;
    /** Lead row. */
    protected int                           leadRow;


    /**
     * Creates a new instance of DefaultTreeSelectionModel that is
     * empty, and having a selection mode of DISCONTIGUOUS_TREE_SELECTION.
     */
    public DefaultTreeSelectionModel() {
	listSelectionModel = new DefaultListSelectionModel();
	selectionMode = DISCONTIGUOUS_TREE_SELECTION;
	leadIndex = leadRow = -1;
    }

    /**
     * Sets the RowMapper instance.  This instance is used to determine
     * what row corresponds to what path.
     */
    public void setRowMapper(RowMapper newMapper) {
	rowMapper = newMapper;
	resetRowSelection();
    }

    /**
     * Returns the RowMapper instance that is able to map a path to a
     * row.
     */
    public RowMapper getRowMapper() {
	return rowMapper;
    }

    /**
     * Sets the selection model, which must be one of SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
     */
    public void setSelectionMode(int mode) {
	int            oldMode = selectionMode;

	selectionMode = mode;
	if(selectionMode != TreeSelectionModel.SINGLE_TREE_SELECTION &&
	   selectionMode != TreeSelectionModel.CONTIGUOUS_TREE_SELECTION &&
	   selectionMode != TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
	    selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
	if(oldMode != selectionMode && changeSupport != null)
	    changeSupport.firePropertyChange(SELECTION_MODE_PROPERTY,
					     new Integer(oldMode),
					     new Integer(selectionMode));
    }

    /**
     * Returns the selection mode.
     */
    public int getSelectionMode() {
	return selectionMode;
    }

    /**
      * Sets the selection to path.  If this represents a change, then
      * the TreeSelectionListeners are notified.
      *
      * @param path new path to select
      */
    public void setSelectionPath(TreePath path) {
	if(path == null)
	    setSelectionPaths(null);
	else {
	    TreePath[]          newPaths = new TreePath[1];

	    newPaths[0] = path;
	    setSelectionPaths(newPaths);
	}
    }

    /**
      * Sets the selection to the paths in paths.  If this represents a
      * change the TreeSelectionListeners are notified.  Potentially
      * paths will be held by the reciever, in other words don't change
      * any of the objects in the array once passed in.
      *
      * @param paths new selection.
      */
    public void setSelectionPaths(TreePath[] pPaths) {
	boolean        differs = false;
	int            newCount, newCounter, oldCount, oldCounter;
	TreePath[]     paths = pPaths;

	if(paths == null)
	    newCount = 0;
	else
	    newCount = paths.length;
	if(selection == null)
	    oldCount = 0;
	else
	    oldCount = selection.length;
	if((newCount + oldCount) != 0) {
	    if(selectionMode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
		/* If single selection and more than one path, only allow
		   first. */
		if(newCount > 1) {
		    paths = new TreePath[1];
		    paths[0] = pPaths[0];
		    newCount = 1;
		}
	    }
	    else if(selectionMode ==
		    TreeSelectionModel.CONTIGUOUS_TREE_SELECTION) {
		/* If contiguous selection and paths aren't contiguous,
		   only select the first path item. */
		if(newCount > 0 && !arePathsContiguous(paths)) {
		    paths = new TreePath[1];
		    paths[0] = pPaths[0];
		    newCount = 1;
		}
	    }

	    boolean          found;
	    int              validCount = 0;
	    TreePath         beginLeadPath = leadPath;
	    Vector           cPaths = new Vector(newCount + oldCount);

	    leadPath = null;
	    /* Find the paths that are new. */
	    for(newCounter = 0; newCounter < newCount; newCounter++) {
		found = false;
		if(paths[newCounter] != null) {
		    validCount++;
		    for(oldCounter = 0; oldCounter < oldCount; oldCounter++) {
			if(selection[oldCounter] != null &&
			   selection[oldCounter].equals(paths[newCounter])) {
			    selection[oldCounter] = null;
			    oldCounter = oldCount;
			    found = true;
			}
		    }
		    if(!found)
			cPaths.addElement(new PathPlaceHolder
					  (paths[newCounter], true));
		    if(leadPath == null)
			leadPath = paths[newCounter];
		}
	    }

	    /* Get the paths that were selected but no longer selected. */
	    for(oldCounter = 0; oldCounter < oldCount; oldCounter++)
		if(selection[oldCounter] != null)
		    cPaths.addElement(new PathPlaceHolder
				      (selection[oldCounter], false));

	    /* If the validCount isn't equal to newCount it means there
	       are some null in paths, remove them and set selection to
	       the new path. */
	    if(validCount == 0)
		selection = null;
	    else if (validCount != newCount) {
		selection = new TreePath[validCount];

		for(newCounter = 0, validCount = 0; newCounter < newCount;
		    newCounter++)
		    if(paths[newCounter] != null)
			selection[validCount++] = paths[newCounter];
	    }
	    else {
		selection = new TreePath[paths.length];
		System.arraycopy(paths, 0, selection, 0, paths.length);
	    }

	    if(selection != null)
		insureUniqueness();

	    updateLeadIndex();

	    resetRowSelection();
	    /* Notify of the change. */
	    if(cPaths.size() > 0)
		notifyPathChange(cPaths, beginLeadPath);
	}
    }

    /**
      * Adds path to the current selection.  If path is not currently
      * in the selection the TreeSelectionListeners are notified.
      *
      * @param path the new path to add to the current selection.
      */
    public void addSelectionPath(TreePath path) {
	if(path != null) {
	    TreePath[]            toAdd = new TreePath[1];

	    toAdd[0] = path;
	    addSelectionPaths(toAdd);
	}
    }

    /**
      * Adds paths to the current selection.  If any of the paths in 
      * paths are not currently in the selection the TreeSelectionListeners
      * are notified.
      *
      * @param path the new path to add to the current selection.
      */
    public void addSelectionPaths(TreePath[] paths) {
	int       newPathLength = ((paths == null) ? 0 : paths.length);

	if(newPathLength > 0) {
	    if(selectionMode == TreeSelectionModel.SINGLE_TREE_SELECTION &&
	       selection != null && selection.length > 0)
		setSelectionPaths(paths);
	    else if(selectionMode == TreeSelectionModel.
		    CONTIGUOUS_TREE_SELECTION && !canPathsBeAdded(paths)) {
		if(arePathsContiguous(paths))
		    setSelectionPaths(paths);
		else {
		    TreePath[]          newPaths = new TreePath[1];

		    newPaths[0] = paths[0];
		    setSelectionPaths(newPaths);
		}
	    }
	    else {
		boolean           didCopyPaths, inSelection;
		int               counter, validCount;
		int               oldCount, oldCounter;
		TreePath          beginLeadPath = leadPath;
		Vector            cPaths = null;

		if(selection == null)
		    oldCount = 0;
		else
		    oldCount = selection.length;
		didCopyPaths = false;
		leadPath = null;
		/* Determine the paths that aren't currently in the
		   selection. */
		for(counter = 0, validCount = 0; counter < paths.length;
		    counter++) {
		    if(paths[counter] != null) {
			inSelection = false;
			for (oldCounter = 0; oldCounter < oldCount;
			     oldCounter++) {
			    if(paths[counter].equals(selection[oldCounter])) {
				oldCounter = oldCount;
				if(!didCopyPaths) {
				    /* Copy the paths so that we can mess with
				       the array. */
				    TreePath[]      copiedPaths;

				    copiedPaths = new TreePath[paths.length];
				    System.arraycopy(paths, 0, copiedPaths,
						     0, paths.length);
				    paths = copiedPaths;
				    didCopyPaths = true;
				}
				paths[counter] = null;
				inSelection = true;
			    }
			}
			if(!inSelection) {
			    validCount++;
			    if(cPaths == null)
				cPaths = new Vector();
			    cPaths.addElement(new PathPlaceHolder
					      (paths[counter], true));
			}
			if(leadPath == null)
			    leadPath = paths[counter];
		    }
		}

		if(leadPath == null)
		    leadPath = beginLeadPath;

		if(validCount > 0) {
		    TreePath         newSelection[] = new TreePath[oldCount +
								  validCount];

		    /* And build the new selection. */
		    if(oldCount > 0)
			System.arraycopy(selection, 0, newSelection, 0,
					 oldCount);
		    if(validCount != paths.length) {
			/* Some of the paths in paths are already in
			   the selection. */
			int           validCounter;

			for(counter = validCounter = 0; counter < paths.length;
			    counter++) {
			    if (paths[counter] != null)
				newSelection[oldCount + validCounter++] =
				    paths[counter];
			}
		    }
		    else
			System.arraycopy(paths, 0, newSelection, oldCount,
					 validCount);

		    selection = newSelection;

		    insureUniqueness();

		    updateLeadIndex();

		    resetRowSelection();

		    notifyPathChange(cPaths, beginLeadPath);
		}
		else
		    leadPath = beginLeadPath;
	    }
	}
    }

    /**
      * Removes path from the selection.  If path is in the selection
      * The TreeSelectionListeners are notified.
      *
      * @param path the path to remove from the selection.
      */
    public void removeSelectionPath(TreePath path) {
	if(path != null) {
	    TreePath[]             rPath = new TreePath[1];

	    rPath[0] = path;
	    removeSelectionPaths(rPath);
	}
    }

    /**
      * Removes paths from the selection.  If any of the paths in paths
      * are in the selection the TreeSelectionListeners are notified.
      *
      * @param path the path to remove from the selection.
      */
    public void removeSelectionPaths(TreePath[] paths) {
	if (paths != null && selection != null && paths.length > 0) {
	    if(!canPathsBeRemoved(paths)) {
		/* Could probably do something more interesting here! */
		clearSelection();
	    }
	    else {
		int         oldCount, oldCounter;
		int         removeCount, removeCounter;
		int         toRemoveCount = 0;
		TreePath    beginLeadPath = leadPath;
		Vector      pathsToRemove = null;

		oldCount = selection.length;

		/* Find the paths that can be removed. */
		for (removeCounter = 0; removeCounter < paths.length;
		     removeCounter++) {
		    if(paths[removeCounter] != null) {
			if(leadPath != null &&
			   leadPath.equals(paths[removeCounter]))
			    leadPath = null;
			for(oldCounter = 0; oldCounter < oldCount;
			    oldCounter++) {
			    if(paths[removeCounter].equals
			       (selection[oldCounter])){
				selection[oldCounter] = null;
				oldCounter = oldCount;
				if(pathsToRemove == null)
				    pathsToRemove = new Vector(paths.length);
				if(!pathsToRemove.contains
				   (paths[removeCounter]))
				    pathsToRemove.addElement
					(new PathPlaceHolder
					 (paths[removeCounter], false));
			    }
			}
		    }
		}
		if(pathsToRemove != null) {
		    removeCount = pathsToRemove.size();
		    if(removeCount == selection.length)
			selection = null;
		    else {
			int                  validCount = 0;
			TreePath[]          newSelection;

			newSelection = new TreePath[selection.length -
						   removeCount];
			for(oldCounter = 0; oldCounter < oldCount; 
			    oldCounter++)
			    if(selection[oldCounter] != null)
				newSelection[validCount++] =
				    selection[oldCounter];
			selection = newSelection;
		    }

		    if(leadPath == null && selection != null)
			leadPath = selection[0];

		    updateLeadIndex();

		    resetRowSelection();

		    notifyPathChange(pathsToRemove, beginLeadPath);
		}
	    }
	}
    }

    /**
      * Returns the first path in the selection.
      */
    public TreePath getSelectionPath() {
	if(selection != null)
	    return selection[0];
	return null;
    }

    /**
      * Returns the paths in the selection.
      */
    public TreePath[] getSelectionPaths() {
	if(selection != null) {
	    int                 pathSize = selection.length;
	    TreePath[]         result = new TreePath[pathSize];

	    System.arraycopy(selection, 0, result, 0, pathSize);
	    return result;
	}
	return null;
    }

    /**
     * Returns the number of paths that are selected.
     */
    public int getSelectionCount() {
	return (selection == null) ? 0 : selection.length;
    }

    /**
      * Returns true if the path, path, is in the current selection.
      */
    public boolean isPathSelected(TreePath path) {
	if (selection != null) {
	    for (int counter = 0; counter < selection.length; counter++)
		if(selection[counter].equals(path))
		    return true;
	}
	return false;
    }

    /**
      * Returns true if the selection is currently empty.
      */
    public boolean isSelectionEmpty() {
	return (selection == null);
    }

    /**
      * Empties the current selection.  If this represents a change in the
      * current selection, the selection listeners are notified.
      */
    public void clearSelection() {
	if(selection != null) {
	    int                    selSize = selection.length;
	    boolean[]              newness = new boolean[selSize];

	    for(int counter = 0; counter < selSize; counter++)
		newness[counter] = false;

	    TreeSelectionEvent     event = new TreeSelectionEvent
		(this, selection, newness, leadPath, null);

	    leadPath = null;
	    leadIndex = leadRow = -1;
	    selection = null;
	    resetRowSelection();
	    fireValueChanged(event);
	}
    }

    /**
      * Adds x to the list of listeners that are notified each time the
      * selection changes.
      *
      * @param x the new listener to be added.
      */
    public void addTreeSelectionListener(TreeSelectionListener x) {
	listenerList.add(TreeSelectionListener.class, x);
    }

    /**
      * Removes x from the list of listeners that are notified each time
      * the selection changes.
      *
      * @param x the listener to remove.
      */
    public void removeTreeSelectionListener(TreeSelectionListener x) {
	listenerList.remove(TreeSelectionListener.class, x);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireValueChanged(TreeSelectionEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// TreeSelectionEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TreeSelectionListener.class) {
		// Lazily create the event:
		// if (e == null)
		// e = new ListSelectionEvent(this, firstIndex, lastIndex);
		((TreeSelectionListener)listeners[i+1]).valueChanged(e);
	    }	       
	}
    }

    /**
      * Returns all of the currently selected rows.
      */
    public int[] getSelectionRows() {
	// This is currently rather expensive.  Needs
	// to be better support from ListSelectionModel to speed this up.
	if(rowMapper != null && selection != null) {
	    return rowMapper.getRowsForPaths(selection);
	}
	return null;
    }

    /**
      * Gets the first selected row.
      */
    public int getMinSelectionRow() {
	return listSelectionModel.getMinSelectionIndex();
    }

    /**
      * Gets the last selected row.
      */
    public int getMaxSelectionRow() {
	return listSelectionModel.getMaxSelectionIndex();
    }

    /**
      * Returns true if the row identitifed by row is selected.
      */
    public boolean isRowSelected(int row) {
	return listSelectionModel.isSelectedIndex(row);
    }

    /**
     * Recalculates what rows are selected by asking the RowMapper for the
     * row for each path.
     */
    public void resetRowSelection() {
	listSelectionModel.clearSelection();
	if(selection != null && rowMapper != null) {
	    int               aRow;
	    int               validCount = 0;
	    int[]             rows = rowMapper.getRowsForPaths(selection);

	    for(int counter = 0, maxCounter = selection.length;
		counter < maxCounter; counter++) {
		aRow = rows[counter];
		if(aRow != -1)
		    listSelectionModel.addSelectionInterval(aRow, aRow);
	    }
	    if(leadIndex != -1)
		leadRow = rows[leadIndex];
	    insureRowContinuity();

	}
	else
	    leadRow = -1;
    }

    /**
     * Returns the lead selection index. That is the last index that was
     * added.
     */
    public int getLeadSelectionRow() {
	return leadRow;
    }

    /**
     * Returns the last path that was added.
     */
    public TreePath getLeadSelectionPath() {
	return leadPath;
    }

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
    public synchronized void addPropertyChangeListener(
                                PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     */

    public synchronized void removePropertyChangeListener(
                                PropertyChangeListener listener) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Useful for CONTIGUOUS_TREE_SELECTION. If the rows that are selected
     * are not contiguous then the selection is reset to be contiguous.
     * Or if the selection mode is single selection and more than one
     * this is selected the selection is reset.
     */
    protected void insureRowContinuity() {
	if(selectionMode == TreeSelectionModel.CONTIGUOUS_TREE_SELECTION &&
	   selection != null && rowMapper != null) {
	    DefaultListSelectionModel lModel = listSelectionModel;
	    int                       min = lModel.getMinSelectionIndex();

	    if(min != -1) {
		for(int counter = min,
			maxCounter = lModel.getMaxSelectionIndex();
		        counter <= maxCounter; counter++) {
		    if(!lModel.isSelectedIndex(counter)) {
			if(counter == min) {
			    clearSelection();
			}
			else {
			    TreePath[]   newSel = new TreePath[counter - min];

			    System.arraycopy(selection, 0, newSel,
					     0, counter - min);
			    setSelectionPaths(newSel);
			    break;
			}
		    }
		}
	    }
	}
	else if(selectionMode == TreeSelectionModel.SINGLE_TREE_SELECTION &&
		selection != null && selection.length > 1) {
	    setSelectionPath(selection[0]);
	}
    }

    /**
     * Returns true if the paths are contiguous.
     */
    protected boolean arePathsContiguous(TreePath[] paths) {
	if(rowMapper == null || paths.length < 2)
	    return true;
	else {
	    BitSet                             bitSet = new BitSet(32);
	    int                                anIndex, counter, min;
	    int                                pathCount = paths.length;
	    int                                validCount = 0;
	    TreePath[]                         tempPath = new TreePath[1];

	    tempPath[0] = paths[0];
	    min = rowMapper.getRowsForPaths(tempPath)[0];
	    for(counter = 0; counter < pathCount; counter++) {
		if(paths[counter] != null) {
		    tempPath[0] = paths[counter];
		    anIndex = rowMapper.getRowsForPaths(tempPath)[0];
		    if(anIndex == -1 || anIndex < (min - pathCount) ||
		       anIndex > (min + pathCount))
			return false;
		    if(anIndex < min)
			min = anIndex;
		    if(!bitSet.get(anIndex)) {
			bitSet.set(anIndex);
			validCount++;
		    }
		}
	    }
	    int          maxCounter = validCount + min;

	    for(counter = min; counter < maxCounter; counter++)
		if(!bitSet.get(counter))
		    return false;
	}
	return true;
    }

    /**
     * Returns true if the paths can be added without breaking the
     * continuity of the model.
     */
    protected boolean canPathsBeAdded(TreePath[] paths) {
	if(paths == null || paths.length == 0 || rowMapper == null ||
	   selection == null || selectionMode ==
	   TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
	    return true;
	else {
	    BitSet                       bitSet = new BitSet();
	    DefaultListSelectionModel    lModel = listSelectionModel;
	    int                          anIndex;
	    int                          counter;
	    int                          min = lModel.getMinSelectionIndex();
	    int	                         max = lModel.getMaxSelectionIndex();
	    TreePath[]                   tempPath = new TreePath[1];

	    if(min != -1) {
		for(counter = min; counter <= max; counter++) {
		    if(lModel.isSelectedIndex(min))
			bitSet.set(counter);
		}
	    }
	    else {
		tempPath[0] = paths[0];
		min = max = rowMapper.getRowsForPaths(tempPath)[0];
	    }
	    for(counter = paths.length - 1; counter >= 0; counter--) {
		if(paths[counter] != null) {
		    tempPath[0] = paths[counter];
		    anIndex = rowMapper.getRowsForPaths(tempPath)[0];
		    min = Math.min(anIndex, min);
		    max = Math.max(anIndex, max);
		    if(anIndex == -1)
			return false;
		    bitSet.set(anIndex);
		}
	    }
	    for(counter = min; counter <= max; counter++)
		if(!bitSet.get(counter))
		    return false;
	}
	return true;
    }

    /**
     * Returns true if the paths can be removed without breaking the
     * continuity of the model.
     * This is rather expensive.
     */
    protected boolean canPathsBeRemoved(TreePath[] paths) {
	if(rowMapper == null || selection == null ||
	   selectionMode == TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION)
	    return true;
	else {
	    boolean              found;
	    BitSet               bitSet = new BitSet();
	    int                  counter, rCounter;
	    int                  pathCount = paths.length;
	    int                  anIndex;
	    int                  min = -1;
	    int                  validCount = 0;
	    TreePath[]           tPaths = new TreePath[pathCount];
	    TreePath[]           tempPath = new TreePath[1];

	    /* Determine the rows for the removed entries. */
	    System.arraycopy(paths, 0, tPaths, 0, pathCount);
	    for(counter = selection.length - 1; counter >= 0; counter--) {
		found = false;
		for(rCounter = 0; rCounter < pathCount; rCounter++) {
		    if(tPaths[rCounter] != null && selection[counter].
		       equals(tPaths[rCounter])) {
			tPaths[rCounter] = null;
			found = true;
			break;
		    }
		}
		if(!found) {
		    tempPath[0] = selection[counter];
		    anIndex = rowMapper.getRowsForPaths(tempPath)[0];
		    if(anIndex != -1 && !bitSet.get(anIndex)) {
			validCount++;
			if(min == -1)
			    min = anIndex;
			else
			    min = Math.min(min, anIndex);
			bitSet.set(anIndex);
		    }
		}
	    }
	    /* Make sure they are contiguous. */
	    if(validCount > 1) {
		for(counter = min + validCount - 1; counter >= min;
		    counter--)
		    if(!bitSet.get(counter))
			return false;
	    }
	}
	return true;
    }

    /**
      * Notifies listeners of a change in path. changePaths should contain
      * instances of PathPlaceHolder.
      * 
      */
    protected void notifyPathChange(Vector changedPaths,
				    TreePath oldLeadSelection) {
	int                    cPathCount = changedPaths.size();
	boolean[]              newness = new boolean[cPathCount];
	TreePath[]            paths = new TreePath[cPathCount];
	PathPlaceHolder        placeholder;
	
	for(int counter = 0; counter < cPathCount; counter++) {
	    placeholder = (PathPlaceHolder)changedPaths.elementAt(counter);
	    newness[counter] = placeholder.isNew;
	    paths[counter] = placeholder.path;
	}
	
	TreeSelectionEvent     event = new TreeSelectionEvent
	                  (this, paths, newness, oldLeadSelection, leadPath);
	
	fireValueChanged(event);
    }

    /**
     * Updates the leadIndex instance variable.
     */
    protected void updateLeadIndex() {
	if(leadPath != null) {
	    if(selection == null) {
		leadPath = null;
		leadIndex = leadRow = -1;
	    }
	    else {
		leadRow = leadIndex = -1;
		for(int counter = selection.length - 1; counter >= 0;
		    counter--) {
		    if(selection[counter].equals(leadPath)) {
			leadIndex = counter;
			break;
		    }
		}
	    }
	}
    }

    /**
      * Insures that all the elements in path are unique.  This does not
      * check for a null selection!
      */
    protected void insureUniqueness() {
	int            compareCounter;
	int            dupCount = 0;
	int            indexCounter;

	for(compareCounter = 0; compareCounter < selection.length;
	    compareCounter++) {
	    if(selection[compareCounter] != null) {
		for(indexCounter = compareCounter + 1; indexCounter <
			selection.length; indexCounter++) {
		    if (selection[indexCounter] != null &&
			selection[compareCounter].equals(selection
							 [indexCounter])){
			dupCount++;
			selection[indexCounter] = null;
		    }
		}
	    }
	}
	if(dupCount > 0) {
	    /* Squash the duplicates. */
	    TreePath[]          newSelection = new TreePath[selection.length-
							     dupCount];

	    for (int counter = 0, validCounter = 0; counter < selection.length;
		    counter++)
		if(selection[counter] != null)
		    newSelection[validCounter++] = selection[counter];
	    selection = newSelection;
	}
    }


    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
	int                selCount = getSelectionCount();
	StringBuffer       retBuffer = new StringBuffer();
	int[]              rows;

	if(rowMapper != null)
	    rows = rowMapper.getRowsForPaths(selection);
	else
	    rows = null;
	retBuffer.append(getClass().getName() + " " + hashCode() + " [ ");
	for(int counter = 0; counter < selCount; counter++) {
	    if(rows != null)
		retBuffer.append(selection[counter].toString() + "@" +
				 Integer.toString(rows[counter])+ " ");
	    else
		retBuffer.append(selection[counter].toString() + " ");
	}
	retBuffer.append("]");
	return retBuffer.toString();
    }

    /**
     * Returns a clone of the reciever with the same selection.
     * selectionListeners, and PropertyListeners are not duplicated.
     *
     * @exception CloneNotSupportedException if the receiver does not
     *    both (a) implement the Cloneable interface and (b) define a
     *    <code>clone</code> method.
     */
    public Object clone() throws CloneNotSupportedException {
	DefaultTreeSelectionModel        clone = (DefaultTreeSelectionModel)
	                    super.clone();

	clone.changeSupport = null;
	if(selection != null) {
	    int              selLength = selection.length;

	    clone.selection = new TreePath[selLength];
	    System.arraycopy(selection, 0, clone.selection, 0, selLength);
	}
	clone.listenerList = new EventListenerList();
	clone.listSelectionModel = (DefaultListSelectionModel)
	    listSelectionModel.clone();
	return clone;
    }

    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
	Object[]             tValues;

	s.defaultWriteObject();
	// Save the rowMapper, if it implements Serializable
	if(rowMapper != null && rowMapper instanceof Serializable) {
	    tValues = new Object[2];
	    tValues[0] = "rowMapper";
	    tValues[1] = rowMapper;
	}
	else
	    tValues = new Object[0];
	s.writeObject(tValues);
    }


    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException {
	Object[]      tValues;

	s.defaultReadObject();

	tValues = (Object[])s.readObject();

	if(tValues.length > 0 && tValues[0].equals("rowMapper"))
	    rowMapper = (RowMapper)tValues[1];
    }
}

/**
 * Holds a path and whether or not it is new.
 */
class PathPlaceHolder {
    protected boolean             isNew;
    protected TreePath           path;

    PathPlaceHolder(TreePath path, boolean isNew) {
	this.path = path;
	this.isNew = isNew;
    }
}
