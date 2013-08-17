/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.undo;

import javax.swing.UIManager;
import java.io.Serializable;

/**
 * An abstract implementation of UndoableEdit, implementing simple
 * responses to all boolean methods in that interface. 
 *
 * @version 1.25 02/06/02
 * @author Ray Ryan
 */
public class AbstractUndoableEdit implements UndoableEdit, Serializable {

    /**
     * String returned by getUndoPresentationName()
     *
     * @see javax.swing.UIDefaults
     */
    protected static final String UndoName = "Undo";

    /**
     * String returned by getRedoPresentationName()
     *
     * @see javax.swing.UIDefaults
     */
    protected static final String RedoName = "Redo";

    /**
     * Defaults to true. Becomes false if this edit is undone, true
     * again if it is redone.  
     */
    boolean hasBeenDone;

    /**
     * True if this edit has not received die().
     */
    boolean alive;

    public AbstractUndoableEdit() {
	super();

	hasBeenDone = true;
	alive = true;
    }

    /**
     * Sets alive to false. Note that this is a one way operation:
     * dead edits cannot be resurrected.  Sending undo() or redo() to
     * a dead edit results in an exception being thrown.
     *
     * Typically an edit is killed when it is consolidated by another
     * edit's addEdit() or replaceEdit() method, or when it is
     * dequeued from an UndoManager
     */
    public void die() {
	alive = false;
    }

    /**
     * Throws CannotUndoException if canUndo() returns false. Sets
     * hasBeenDone to false. Subclasses should override to undo the
     * operation represented by this edit. Override should begin with
     * a call to super.
     *
     * @see	#canUndo
     */
    public void undo() throws CannotUndoException {
	if (!canUndo()) {
	    throw new CannotUndoException();
	}
	hasBeenDone = false;
    }

    /**
     * Returns true if this edit is alive and hasBeenDone is true.
     *
     * @see     #die
     * @see	#undo
     * @see	#redo
     */
    public boolean canUndo() {
	return alive && hasBeenDone;
    }

    /**
     * Throws CannotRedoException if canRedo() returns false. Sets
     * hasBeenDone to true. Subclasses should override to redo the
     * operation represented by this edit. Override should begin with
     * a call to super.
     *
     * @see	#canRedo
     */
    public void redo() throws CannotRedoException {
	if (!canRedo()) {
	    throw new CannotRedoException();
	}
	hasBeenDone = true;
    }

    /**
     * Returns true if this edit is alive and hasBeenDone is false.
     *
     * @see     #die
     * @see	#undo
     * @see	#redo
     */
    public boolean canRedo() {
	return alive && !hasBeenDone;
    }
	
    /**
     * This default implementation returns false. 
     *
     * @see UndoableEdit#addEdit
     */
    public boolean addEdit(UndoableEdit anEdit) {
	return false;
    }

    /**
     * This default implementation returns false. 
     *
     * @see UndoableEdit#replaceEdit
     */
    public boolean replaceEdit(UndoableEdit anEdit) {
	return false;
    }

    /**
     * This default implementation returns true. 
     *
     * @see UndoableEdit#isSignificant
     */
    public boolean isSignificant() {
	return true;
    }

    /**
     * This default implementation returns "". Used by
     * getUndoPresentationName() and getRedoPresentationName() to
     * construct the strings they return. Subclasses shoul override to
     * return an appropriate description of the operation this edit
     * represents.
     *
     * @see	#getUndoPresentationName
     * @see	#getRedoPresentationName
     */
    public String getPresentationName() {
	return "";
    }

    /**
     * If getPresentationName() returns "", returns
     * AbstractUndoableEdit.UndoName. Otherwise returns
     * AbstractUndoableEdit.UndoName followed by a space and
     * getPresentationName()
     *
     * @see #getPresentationName
     */
    public String getUndoPresentationName() {
	String name = getPresentationName();
	if (name != "") {
	    name = UIManager.getString("AbstractUndoableEdit.undoText") + " " +
                name;
	} else {
	    name = UIManager.getString("AbstractUndoableEdit.undoText");
	}

	return name;
    }

    /**
     * If getPresentationName() returns "", returns
     * AbstractUndoableEdit.RedoName. Otherwise returns
     * AbstractUndoableEdit.RedoName followed by a space and
     * getPresentationName()
     *
     * @see #getPresentationName
     */
    public String getRedoPresentationName() {
	String name = getPresentationName();
	if (name != "") {
	    name = UIManager.getString("AbstractUndoableEdit.redoText") + " " +
                name;
	} else {
	    name = UIManager.getString("AbstractUndoableEdit.redoText");
	}

	return name;
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
	    + " hasBeenDone: " + hasBeenDone
	    + " alive: " + alive;
    }
}

