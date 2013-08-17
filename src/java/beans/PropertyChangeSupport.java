/*
 * @(#)PropertyChangeSupport.java	1.13 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * This is a utility class that can be used by beans that support bound
 * properties.  You can use an instance of this class as a member field
 * of your bean and delegate various work to it.
 */

public class PropertyChangeSupport implements java.io.Serializable {

    /**
     * @sourceBean  The bean to be given as the source for any events.
     */

    public PropertyChangeSupport(Object sourceBean) {
	source = sourceBean;
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     *
     * @param listener  The PropertyChangeListener to be added
     */

    public synchronized void addPropertyChangeListener(
				PropertyChangeListener listener) {
	if (listeners == null) {
	    listeners = new java.util.Vector();
	}
	listeners.addElement(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     *
     * @param listener  The PropertyChangeListener to be removed
     */

    public synchronized void removePropertyChangeListener(
				PropertyChangeListener listener) {
	if (listeners == null) {
	    return;
	}
	listeners.removeElement(listener);
    }

    /**
     * Report a bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     *
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public void firePropertyChange(String propertyName, 
					Object oldValue, Object newValue) {

	if (oldValue != null && oldValue.equals(newValue)) {
	    return;
	}

	java.util.Vector targets;
	synchronized (this) {
	    if (listeners == null) {
	    	return;
	    }
	    targets = (java.util.Vector) listeners.clone();
	}
        PropertyChangeEvent evt = new PropertyChangeEvent(source,
					    propertyName, oldValue, newValue);

	for (int i = 0; i < targets.size(); i++) {
	    PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
	    target.propertyChange(evt);
	}
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

	java.util.Vector v = null;
	synchronized (this) {
	    if (listeners != null) {
	        v = (java.util.Vector) listeners.clone();
            }
	}

	if (v != null) {
	    for(int i = 0; i < v.size(); i++) {
	        PropertyChangeListener l = (PropertyChangeListener)v.elementAt(i);
	        if (l instanceof Serializable) {
	            s.writeObject(l);
	        }
            }
        }
        s.writeObject(null);
    }


    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();
      
        Object listenerOrNull;
        while(null != (listenerOrNull = s.readObject())) {
	  addPropertyChangeListener((PropertyChangeListener)listenerOrNull);
        }
    }

    transient private java.util.Vector listeners;
    private Object source;
    private int propertyChangeSupportSerializedDataVersion = 1;
}




