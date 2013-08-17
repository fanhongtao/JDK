/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing; 

import javax.swing.event.*;
import java.util.EventObject;
import java.io.Serializable;

/**
 * @version 1.5 02/06/02 
 * 
 * A base class for <code>CellEditors</code>, providing default
 * implementations for the methods in the <code>CellEditor</code>
 * interface except <code>getCellEditorValue()</code>. 
 * Like the other abstract implementations in Swing, also manages a list 
 * of listeners. 
 *
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 * 
 * @author Philip Milne
 */

public abstract class AbstractCellEditor implements CellEditor, Serializable {

    protected EventListenerList listenerList = new EventListenerList();
    transient protected ChangeEvent changeEvent = null;

    // Force this to be implemented. 
    // public Object  getCellEditorValue()  

    /**
     * Returns true.
     * @param e  an event object
     * @return true
     */
    public boolean isCellEditable(EventObject e) { 
	return true; 
    } 

    /**
     * Returns true.
     * @param e  an event object
     * @return true
     */
    public boolean shouldSelectCell(EventObject anEvent) { 
	return true; 
    }
    
    /**
     * Calls <code>fireEditingStopped</code> and returns true.
     * @return true
     */
    public boolean stopCellEditing() { 
	fireEditingStopped(); 
	return true;
    }

    /**
     * Calls <code>fireEditingCanceled</code>.
     */
    public void  cancelCellEditing() { 
	fireEditingCanceled(); 
    }

    /**
     * Adds a <code>CellEditorListener</code> to the listener list.
     * @param l  the new listener to be added
     */
    public void addCellEditorListener(CellEditorListener l) {
	listenerList.add(CellEditorListener.class, l);
    }

    /**
     * Removes a <code>CellEditorListener</code> from the listener list.
     * @param l  the listener to be removed
     */
    public void removeCellEditorListener(CellEditorListener l) {
	listenerList.remove(CellEditorListener.class, l);
    }

    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
	    }	       
	}
    }

    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
	    }	       
	}
    }
}
