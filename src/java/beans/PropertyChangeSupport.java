/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 *
 * This class is serializable.  When it is serialized it will save
 * (and restore) any listeners that are themselves serializable.  Any
 * non-serializable listeners will be skipped during serialization.
 *
 */

public class PropertyChangeSupport implements java.io.Serializable {

    /**
     * Constructs a <code>PropertyChangeSupport</code> object.
     *
     * @param sourceBean  The bean to be given as the source for any events.
     */

    public PropertyChangeSupport(Object sourceBean) {
	if (sourceBean == null) {
	    throw new NullPointerException();
	}
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
	PropertyChangeSupport child = (PropertyChangeSupport)children.get(propertyName);
	if (child == null) {
	    child = new PropertyChangeSupport(source);
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
	PropertyChangeSupport child = (PropertyChangeSupport)children.get(propertyName);
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

	java.util.Vector targets = null;
	PropertyChangeSupport child = null;
	synchronized (this) {
	    if (listeners != null) {
	        targets = (java.util.Vector) listeners.clone();
	    }
	    if (children != null && propertyName != null) {
		child = (PropertyChangeSupport)children.get(propertyName);
	    }
	}

        PropertyChangeEvent evt = new PropertyChangeEvent(source,
					    propertyName, oldValue, newValue);

	if (targets != null) {
	    for (int i = 0; i < targets.size(); i++) {
	        PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
	        target.propertyChange(evt);
	    }
	}

	if (child != null) {
	    child.firePropertyChange(evt);
	}	
    }

    /**
     * Report an int bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * firePropertyChange method that takes Object values.
     *
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public void firePropertyChange(String propertyName, 
					int oldValue, int newValue) {
	if (oldValue == newValue) {
	    return;
	}
	firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
    }


    /**
     * Report a boolean bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * firePropertyChange method that takes Object values.
     *
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public void firePropertyChange(String propertyName, 
					boolean oldValue, boolean newValue) {
	if (oldValue == newValue) {
	    return;
	}
	firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
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

	java.util.Vector targets = null;
	PropertyChangeSupport child = null;
	synchronized (this) {
	    if (listeners != null) {
	        targets = (java.util.Vector) listeners.clone();
	    }
	    if (children != null && propertyName != null) {
		child = (PropertyChangeSupport)children.get(propertyName);
	    }
	}

	if (targets != null) {
	    for (int i = 0; i < targets.size(); i++) {
	        PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
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
	    PropertyChangeSupport child = (PropertyChangeSupport)children.get(propertyName);
	    if (child != null && child.listeners != null) {
		return !child.listeners.isEmpty();
	    }
	}
	return false;
    }

    /**
     * @serialData Null terminated list of <code>PropertyChangeListeners</code>.
     * <p>
     * At serialization time we skip non-serializable listeners and
     * only serialize the serializable listeners.
     *
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

	java.util.Vector v = null;
	synchronized (this) {
	    if (listeners != null) {
	        v = (java.util.Vector) listeners.clone();
            }
	}

	if (v != null) {
	    for (int i = 0; i < v.size(); i++) {
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

    /**
     * "listeners" lists all the generic listeners.
     *
     *  This is transient - its state is written in the writeObject method.
     */
    transient private java.util.Vector listeners;

    /** 
     * Hashtable for managing listeners for specific properties.
     * Maps property names to PropertyChangeSupport objects.
     * @serial 
     * @since 1.2
     */
    private java.util.Hashtable children;

    /** 
     * The object to be provided as the "source" for any generated events.
     * @serial
     */
    private Object source;

    /**
     * Internal version number
     * @serial
     * @since
     */
    private int propertyChangeSupportSerializedDataVersion = 2;

    /**
     * Serialization version ID, so we're compatible with JDK 1.1
     */
    static final long serialVersionUID = 6401253773779951803L;
}
