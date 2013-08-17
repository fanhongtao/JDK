/*
 * @(#)SwingPropertyChangeSupport.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.event;

import java.beans.*;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * This subclass of java.beans.PropertyChangeSupport is identical
 * in functionality -- it sacrifices thread-safety (not a Swing
 * concern) for reduce memory consumption, which helps performance
 * (both big Swing concerns).  Most of the overridden methods are
 * only necessary because all of PropertyChangeSupport's instance
 * data is private, without accessor methods.
 *
 * @version 1.6 11/18/98
 * @author unattributed
 */

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {

    /**
     * Constructs a SwingPropertyChangeSupport object.
     *
     * @param sourceBean  The bean to be given as the source for any events.
     */
    public SwingPropertyChangeSupport(Object sourceBean) {
        super(sourceBean);
	source = sourceBean;
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
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
     * This removes a PropertyChangeListener that was registered
     * for all properties.
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
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */

    public synchronized void addPropertyChangeListener(
				String propertyName,
				PropertyChangeListener listener) {
	if (children == null) {
	    children = new java.util.Hashtable();
	}
	SwingPropertyChangeSupport child =
            (SwingPropertyChangeSupport)children.get(propertyName);
	if (child == null) {
	    child = new SwingPropertyChangeSupport(source);
	    children.put(propertyName, child);
	}
	child.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */

    public synchronized void removePropertyChangeListener(
				String propertyName,
				PropertyChangeListener listener) {
	if (children == null) {
	    return;
	}
	SwingPropertyChangeSupport child =
            (SwingPropertyChangeSupport)children.get(propertyName);
	if (child == null) {
	    return;
	}
	child.removePropertyChangeListener(listener);
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
	if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    return;
	}

	SwingPropertyChangeSupport child = null;
	synchronized (this) {
	    if (children != null && propertyName != null) {
		child = (SwingPropertyChangeSupport)children.get(propertyName);
	    }
	}

        if (listeners != null || child != null) {
            // Only create an event if there's an interested receiver.
            PropertyChangeEvent evt = new PropertyChangeEvent(
                source, propertyName, oldValue, newValue);

            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    PropertyChangeListener target =
                        (PropertyChangeListener)listeners.elementAt(i);
                    target.propertyChange(evt);
                }
            }

            if (child != null) {
                child.firePropertyChange(evt);
            }
        }
    }

    /**
     * Fire an existing PropertyChangeEvent to any registered listeners.
     * No event is fired if the given event's old and new values are
     * equal and non-null.
     * @param evt  The PropertyChangeEvent object.
     */
    public void firePropertyChange(PropertyChangeEvent evt) {
	Object oldValue = evt.getOldValue();
	Object newValue = evt.getNewValue();
        String propertyName = evt.getPropertyName();
	if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    return;
	}

	SwingPropertyChangeSupport child = null;
	synchronized (this) {
	    if (children != null && propertyName != null) {
		child = (SwingPropertyChangeSupport)children.get(propertyName);
	    }
	}

	if (listeners != null) {
	    int size = listeners.size();
	    for (int i = 0; i < size; i++) {
	        PropertyChangeListener target =
                    (PropertyChangeListener)listeners.elementAt(i);
	        target.propertyChange(evt);
	    }
	}
	if (child != null) {
	    child.firePropertyChange(evt);
	}
    }

    /**
     * Check if there are any listeners for a specific property.
     *
     * @param propertyName  the property name.
     * @return true if there are ore or more listeners for the given property
     */
    public synchronized boolean hasListeners(String propertyName) {
	if (listeners != null && !listeners.isEmpty()) {
	    // there is a generic listener
	    return true;
	}
	if (children != null) {
	    SwingPropertyChangeSupport child =
                (SwingPropertyChangeSupport)children.get(propertyName);
	    if (child != null) {
		// The child will always have a listeners Vector.
		return !child.listeners.isEmpty();
	    }
	}
	return false;
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
	    int size = v.size();
	    for (int i = 0; i < size; i++) {
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
        while (null != (listenerOrNull = s.readObject())) {
	  addPropertyChangeListener((PropertyChangeListener)listenerOrNull);
        }
    }

    // "listeners" lists all the generic listeners.
    transient private java.util.Vector listeners;
    // "children" contains SwingPropertyChangeSupports for individual properties
    private java.util.Hashtable children;
    private Object source;

    // Serialization version ID
    static final long serialVersionUID = 7162625831330845068L;
}
