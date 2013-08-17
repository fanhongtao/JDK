/*
 * @(#)AbstractListModel.java	1.19 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing;

import javax.swing.event.*;
import java.io.Serializable;

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
 * @version 1.19 10/27/98
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
}
