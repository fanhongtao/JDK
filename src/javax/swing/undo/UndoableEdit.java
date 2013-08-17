/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.undo;

import javax.swing.event.*;

/**
 * An object representing an edit that has been done, and that can be
 * undone and redone
 *
 * @version 1.15, 02/06/02
 * @author Ray Ryan
 */

public interface UndoableEdit {
    /**
     * Undo the edit that was made.
     */
    public void undo() throws CannotUndoException;

    /**
     * True if it is still possible to undo this operation
     */
    public boolean canUndo();

    /**
     * Re-apply the edit, assuming that it has been undone.
     */
    public void redo() throws CannotRedoException;

    /**
     * True if it is still possible to redo this operation
     */
    public boolean canRedo();

    /**
     * May be sent to inform an edit that it should no longer be
     * used. This is a useful hook for cleaning up state no longer
     * needed once undoing or redoing is impossible--for example,
     * deleting file resources used by objects that can no longer be
     * undeleted. UndoManager calls this before it dequeues edits.
     *
     * Note that this is a one-way operation. There is no "undie"
     * method.
     *
     * @see CompoundEdit#die
     */
    public void die();

    /**
     * This UndoableEdit should absorb anEdit if it can. Return true
     * if anEdit has been incoporated, false if it has not.
     *
     * <p>Typically the receiver is already in the queue of a
     * UndoManager (or other UndoableEditListener), and is being
     * given a chance to incorporate anEdit rather than letting it be
     * added to the queue in turn.</p>
     *
     * <p>If true is returned, from now on anEdit must return false from
     * canUndo() and canRedo(), and must throw the appropriate
     * exception on undo() or redo().</p>
     */
    public boolean addEdit(UndoableEdit anEdit);

    /**
     * Return true if this UndoableEdit should replace anEdit. The
     * receiver should incorporate anEdit's state before returning true.
     *
     * <p>This message is the opposite of addEdit--anEdit has typically
     * already been queued in a UndoManager (or other
     * UndoableEditListener), and the receiver is being given a chance
     * to take its place.</p>
     *
     * <p>If true is returned, from now on anEdit must return false from
     * canUndo() and canRedo(), and must throw the appropriate
     * exception on undo() or redo().</p>
     */
    public boolean replaceEdit(UndoableEdit anEdit);

    /**
     * Return false if this edit is insignificant--for example one
     * that maintains the user's selection, but does not change any
     * model state. This status can be used by an UndoableEditListener
     * (like UndoManager) when deciding which UndoableEdits to present
     * to the user as Undo/Redo options, and which to perform as side
     * effects of undoing or redoing other events.
     */
    public boolean isSignificant();

    /**
     * Provide a localized, human readable description of this edit
     * suitable for use in, say, a change log.
     */
    public String getPresentationName();

    /**
     * Provide a localized, human readable description of the undoable
     * form of this edit, e.g. for use as an Undo menu item. Typically
     * derived from getDescription();
     */
    public String getUndoPresentationName();

    /**
     * Provide a localized, human readable description of the redoable
     * form of this edit, e.g. for use as a Redo menu item. Typically
     * derived from getPresentationName();
     */
    public String getRedoPresentationName();
}
