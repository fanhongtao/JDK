/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.undo;

import java.util.*;

/**
 * A concrete subclass of AbstractUndoableEdit, used to assemble little
 * UndoableEdits into great big ones.
 *
 * @version 1.18 02/06/02
 * @author Ray Ryan
 */
public class CompoundEdit extends AbstractUndoableEdit {
    /**
     * True iff this edit has never received end()
     */
    boolean inProgress;

    /**
     * The collection of UndoableEdits undone/redone en masse by this
     * CompoundEdit
     */ 
    protected Vector edits;

    public CompoundEdit() {
	super();
	inProgress = true;
	edits = new Vector();
    }

    /**
     * Sends undo() to all contained UndoableEdits in the reverse of
     * the order in which they were added.
     */
    public void undo() throws CannotUndoException {
	super.undo();
	int i = edits.size();
	while (i-- > 0) {
	    UndoableEdit e = (UndoableEdit)edits.elementAt(i);
	    e.undo();
	}
    }

    /**
     * Sends redo() to all contained UndoableEdits in the order in
     * which they were added.
     */
    public void redo() throws CannotRedoException {
	super.redo();
	Enumeration cursor = edits.elements();
	while (cursor.hasMoreElements()) {
	    ((UndoableEdit)cursor.nextElement()).redo();
	}
    }

    /**
     * Returns the last UndoableEdit in edits, or null if edits is
     * empty
     */
    protected UndoableEdit lastEdit() {
	int count = edits.size();
	if (count > 0)
	    return (UndoableEdit)edits.elementAt(count-1);
	else
	    return null;
    }

    /**
     * Send die to each subedit, in the reverse of the order that they
     * were added
     */
    public void die() {
	int size = edits.size();
	for (int i = size-1; i >= 0; i--)
	{
	    UndoableEdit e = (UndoableEdit)edits.elementAt(i);
// 	    System.out.println("CompoundEdit(" + i + "): Discarding " +
// 			       e.getUndoPresentationName());
	    e.die();
	}
	super.die();
    }	

    /**
     * If this edit is inProgress, accepts anEdit and returns
     * true.
     * 
     *  <p>The last edit added to this CompoundEdit is given a
     * chance to addEdit(anEdit). If it refuses (returns false), anEdit is
     * given a chance to replaceEdit the last edit. If anEdit returns
     * false here, it is added to edits.</p>
     */ 
    public boolean addEdit(UndoableEdit anEdit) {
	if (!inProgress) {
	    return false;
	} else {
	    UndoableEdit last = lastEdit();

	    // If this is the first subedit received, just add it.
	    // Otherwise, give the last one a chance to absorb the new
	    // one.  If it won't, give the new one a chance to absorb
	    // the last one.

	    if (last == null) {
		edits.addElement(anEdit);
	    }
	    else if (!last.addEdit(anEdit)) {
		if (anEdit.replaceEdit(last)) {
		    edits.removeElementAt(edits.size()-1);
		}
		edits.addElement(anEdit);
	    }

	    return true;
	}
    }

    /**
     * Sets inProgress to false.
     * 
     * @see #canUndo
     * @see #canRedo
     */
    public void end() {
	inProgress = false;
    }

    /**
     * Returns false if isInProgress or if super does.
     * 
     * @see	#isInProgress
     */
    public boolean canUndo() {
	return !isInProgress() && super.canUndo();
    }

    /**
     * Returns false if isInProgress or if super does.
     * 
     * @see	#isInProgress
     */
    public boolean canRedo() {
	return !isInProgress() && super.canRedo();
    }

    /**
     * Returns true if this edit is in progress--that is, it has not
     * received end. This generally means that edits are still being
     * added to it.
     *
     * @see	#end
     */
    public boolean isInProgress() {
	return inProgress;
    }

    /**
     * Returns true if any of the UndoableEdits in edits do. Returns
     * false if they all return false.
     */
    public boolean  isSignificant() {
	Enumeration cursor = edits.elements();
	while (cursor.hasMoreElements()) {
	    if (((UndoableEdit)cursor.nextElement()).isSignificant()) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns getPresentationName from the last UndoableEdit added to
     * edits. If edits is empty, calls super.
     */
    public String getPresentationName() {
	UndoableEdit last = lastEdit();
	if (last != null) {
	    return last.getPresentationName();
	} else {
	    return super.getPresentationName();
	}
    }
        
    /**
     * Returns getUndoPresentationName from the last UndoableEdit
     * added to edits. If edits is empty, calls super.
     */
    public String getUndoPresentationName() {
	UndoableEdit last = lastEdit();
	if (last != null) {
	    return last.getUndoPresentationName();
	} else {
	    return super.getUndoPresentationName();
	}
    }
        
    /**
     * Returns getRedoPresentationName from the last UndoableEdit
     * added to edits. If edits is empty, calls super.
     */
    public String getRedoPresentationName() {
	UndoableEdit last = lastEdit();
	if (last != null) {
	    return last.getRedoPresentationName();
	} else {
	    return super.getRedoPresentationName();
	}
    }
        
    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString()
    {
	return super.toString()
	    + " inProgress: " + inProgress
	    + " edits: " + edits;
    }
}
