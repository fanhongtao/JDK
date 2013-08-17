/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.event.*;
import java.io.Serializable;
import java.util.EventListener;

/**
 * The Abstract definition for the data model the provides
 * a List with its contents.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.23 02/06/02
 * @author Hans Muller
 */
public abstract class AbstractListModel implements ListModel, Serializable
{
    protected EventListenerList listenerList = new EventListenerList();


    /**
     * Add a listener to the list that's notified each time a change
     * to the data model occurs.
     * @param l the ListDataListener
     */  
    public void addListDataListener(ListDataListener l) {
	listenerList.add(ListDataListener.class, l);
    }


    /**
     * Remove a listener from the list that's notified each time a 
     * change to the data model occurs.
     * @param l the ListDataListener
     */  
    public void removeListDataListener(ListDataListener l) {
	listenerList.remove(ListDataListener.class, l);
    }


    /**
     * AbstractListModel subclasses must call this method <b>after</b>
     * one or more elements of the list change.  The changed elements
     * are specified by a closed interval index0, index1, i.e. the
     * range that includes both index0 and index1.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source The ListModel that changed, typically "this".
     * @param index0 One end of the new interval.
     * @param index1 The other end of the new interval.
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).contentsChanged(e);
	    }	       
	}
    }


    /**
     * AbstractListModel subclasses must call this method <b>after</b>
     * one or more elements are added to the model.  The new elements
     * are specified by a closed interval index0, index1, i.e. the
     * range that includes both index0 and index1.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source The ListModel that changed, typically "this".
     * @param index0 One end of the new interval.
     * @param index1 The other end of the new interval.
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).intervalAdded(e);
	    }	       
	}
    }


    /**
     * AbstractListModel subclasses must call this method <b>after</b>
     * one or more elements are removed from the model.  The new elements
     * are specified by a closed interval index0, index1, i.e. the
     * range that includes both index0 and index1.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source The ListModel that changed, typically "this".
     * @param index0 One end of the new interval.
     * @param index1 The other end of the new interval.
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).intervalRemoved(e);
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
