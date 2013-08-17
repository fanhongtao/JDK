/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.event.*;
import java.io.Serializable;
import java.util.EventListener; 

/**
 * A generic implementation of SingleSelectionModel.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.26 02/06/02
 * @author Dave Moore
 */
public class DefaultSingleSelectionModel implements SingleSelectionModel, 
Serializable {
    /* Only one ModelChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    /** The collection of registered listeners */
    protected EventListenerList listenerList = new EventListenerList();

    private int index = -1;

    // implements javax.swing.SingleSelectionModel
    public int getSelectedIndex() {
        return index;
    }

    // implements javax.swing.SingleSelectionModel
    public void setSelectedIndex(int index) {
        if (this.index != index) {
            this.index = index;
	    fireStateChanged();
        }
    }

    // implements javax.swing.SingleSelectionModel
    public void clearSelection() {
        setSelectedIndex(-1);
    }

    // implements javax.swing.SingleSelectionModel
    public boolean isSelected() {
	boolean ret = false;
	if (getSelectedIndex() != -1) {
	    ret = true;
	}
	return ret;
    }

    /**
     * Adds a ChangeListener to the button.
     */
    public void addChangeListener(ChangeListener l) {
	listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from the button.
     */
    public void removeChangeListener(ChangeListener l) {
	listenerList.remove(ChangeListener.class, l);
    }
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireStateChanged() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==ChangeListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
	    }	       
	}
    }	

    /**
     * Return an array of all the listeners of the given type that 
     * were added to this model. 
     *
     * @returns all of the objects recieving <em>listenerType</em> notifications 
     *          from this model
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }
}









