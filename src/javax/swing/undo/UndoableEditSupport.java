/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.undo;

import javax.swing.event.*;
import java.util.*;

/**
 * A support class used for managing UndoableEdit listeners.
 *
 * @author Ray Ryan
 * @version 1.12 02/06/02
 */
public class UndoableEditSupport {
    protected int updateLevel;
    protected CompoundEdit compoundEdit;
    protected Vector listeners;
    protected Object realSource;

    /**
     * Constructs an UndoableEditSupport object.
     */
    public UndoableEditSupport() {
	this(null);
    }

    /**
     * Constructs an UndoableEditSupport object.
     *
     * @param r  an Object 
     */
    public UndoableEditSupport(Object r) {
	realSource = r == null ? this : r;
	updateLevel = 0;
	compoundEdit = null;
	listeners = new Vector();
    }

    /**
     * Registers an UndoableEditListener. The listener is notified whenever
     * an edit occurs which can be undone.
     *
     * @param l  an UndoableEditListener object
     * @see #removeUndoableEditListener
     */
    public synchronized void addUndoableEditListener(UndoableEditListener l) {
	listeners.addElement(l);
    }

    /**
     * Removes an UndoableEditListener.
     *
     * @param l  an UndoableEditListener object
     * @see #addUndoableEditListener
     */
    public synchronized void removeUndoableEditListener(UndoableEditListener l)
    {
	listeners.removeElement(l);
    }

    /**
     * Called only from postEdit and endUpdate. Calls
     * undoableEditHappened in all listeners. No synchronization
     * is performed here, since the two calling methods are
     * synchonized.
     */
    protected void _postEdit(UndoableEdit e) {
	UndoableEditEvent ev = new UndoableEditEvent(realSource, e);
	Enumeration cursor = listeners.elements();
	while (cursor.hasMoreElements()) {
	    ((UndoableEditListener)cursor.nextElement()).
		undoableEditHappened(ev);	    
	}
    }
    
    /**
     * DEADLOCK WARNING: Calling this method may call undoableEditHappened
     * in all listeners.  It is unwise to call this method from one
     * of its listeners.
     */
    public synchronized void postEdit(UndoableEdit e) {
	if (updateLevel == 0) {
	    _postEdit(e);
	} else {
	    // PENDING(rjrjr) Throw an exception if this fails? 
	    compoundEdit.addEdit(e);
	}
    }

    /**
     * Returns the update level value.
     *
     * @return an int representing the update level
     */
    public int getUpdateLevel() {
	return updateLevel;
    }

    /**
     *
     */
    public synchronized void beginUpdate() {
	if (updateLevel == 0) {
	    compoundEdit = createCompoundEdit();
	}
	updateLevel++;
    }

    /**
     * Called only from beginUpdate. Exposed here for subclasses' use
     */
    protected CompoundEdit createCompoundEdit() {
	return new CompoundEdit();
    }

    /**
     * DEADLOCK WARNING: Calling this method may call undoableEditHappened
     * in all listeners.  It is unwise to call this method from one
     * of its listeners.
     */
    public synchronized void endUpdate() {
	updateLevel--;
	if (updateLevel == 0) {
	    compoundEdit.end();
	    _postEdit(compoundEdit);
	    compoundEdit = null;
	}
    }
    
    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
	return super.toString() +
	    " updateLevel: " + updateLevel +
	    " listeners: " + listeners +
	    " compoundEdit: " + compoundEdit;
    }
}


