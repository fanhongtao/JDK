/*
 * @(#)UndoableEdit.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.undo;

import javax.swing.event.*;

/**
 * An object representing an edit that has been done, and that can be
 * undone and redone.
 *
 * @version 1.18, 01/23/03
 * @author Ray Ryan
 */

public interface UndoableEdit {
    /**
     * Undo the edit that was made.
     */
    public void undo() throws CannotUndoException;

    /**
     * True if it is still possible to undo this operation.
     */
    public boolean canUndo();

    /**
     * Re-apply the edit, assuming that it has been undone.
     */
    public void redo() throws CannotRedoException;

    /**
     * True if it is still possible to redo this operation.
     */
    public boolean canRedo();

    /**
     * May be sent to inform an edit that it should no longer be
     * used. This is a useful hook for cleaning up state no longer
     * needed once undoing or redoing is impossible--for example,
     * deleting file resources used by objects that can no longer be
     * undeleted. <code>UndoManager</code> calls this before it dequeues edits.
     *
     * Note that this is a one-way operation. There is no "un-die"
     * method.
     *
     * @see CompoundEdit#die
     */
    public void die();

    /**
     * This <code>UndoableEdit</code> should absorb <code>anEdit</code>
     * if it can. Returns true
     * if <code.anEdit</code> has been incorporated, false if it has not.
     *
     * <p>Typically the receiver is already in the queue of a
     * <code>UndoManager</code> (or other <code>UndoableEditListener</code>),
     * and is being given a chance to incorporate <code>anEdit</code>
     * rather than letting it be added to the queue in turn.</p>
     *
     * <p>If true is returned, from now on <code>anEdit</code> must return
     * false from <code>canUndo</code> and <code>canRedo</code>,
     * and must throw the appropriate exception on <code>undo</code> or
     * <code>redo</code>.</p>
     * @param anEdit the edit to be added
     */
    public boolean addEdit(UndoableEdit anEdit);

    /**
     * Returns true if this <code>UndoableEdit</code> should replace
     * <code>anEdit</code>. The receiver should incorporate
     * <code>anEdit</code>'s state before returning true.
     *
     * <p>This message is the opposite of addEdit--anEdit has typically
     * already been queued in a <code>UndoManager</code> (or other
     * UndoableEditListener), and the receiver is being given a chance
     * to take its place.</p>
     *
     * <p>If true is returned, from now on anEdit must return false from
     * canUndo() and canRedo(), and must throw the appropriate
     * exception on undo() or redo().</p>
     */
    public boolean replaceEdit(UndoableEdit anEdit);

    /**
     * Returns false if this edit is insignificant--for example one
     * that maintains the user's selection, but does not change any
     * model state. This status can be used by an 
     * <code>UndoableEditListener</code>
     * (like UndoManager) when deciding which UndoableEdits to present
     * to the user as Undo/Redo options, and which to perform as side
     * effects of undoing or redoing other events.
     */
    public boolean isSignificant();

    /**
     * Provides a localized, human readable description of this edit
     * suitable for use in, say, a change log.
     */
    public String getPresentationName();

    /**
     * Provides a localized, human readable description of the undoable
     * form of this edit, e.g. for use as an Undo menu item. Typically
     * derived from <code>getDescription</code>.
     */
    public String getUndoPresentationName();

    /**
     * Provides a localized, human readable description of the redoable
     * form of this edit, e.g. for use as a Redo menu item. Typically
     * derived from <code>getPresentationName</code>.
     */
    public String getRedoPresentationName();
}
