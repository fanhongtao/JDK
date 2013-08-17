/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.18 02/06/02
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
	    listeners = new EventListenerList();
	}
	listeners.add(PropertyChangeListener.class, listener);
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
	listeners.remove(PropertyChangeListener.class, listener);
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
	firePropertyChange(new PropertyChangeEvent(source, propertyName, 
						   oldValue, newValue));
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
	if (children != null) {
	    synchronized (this) {
		if (children != null && propertyName != null) {
		    child = (SwingPropertyChangeSupport)children.get(propertyName);
		}
	    }
	}

        if (listeners != null)  {
            Object[] listenerList = listeners.getListenerList();
            for (int i = 0; i <= listenerList.length-2; i += 2) {
                if (listenerList[i] == PropertyChangeListener.class)  {
                    ((PropertyChangeListener)listenerList[i+1]).propertyChange(evt);
                }
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
	if (listeners != null && listeners.getListenerCount(PropertyChangeListener.class) > 0) {
	    // there is a generic listener
	    return true;
	}
	if (children != null) {
	    SwingPropertyChangeSupport child =
                (SwingPropertyChangeSupport)children.get(propertyName);
	    if (child != null) {
		// The child will always have a listeners Vector.
		return child.hasListeners(propertyName);
	    }
	}
	return false;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        if (listeners != null)  {
            Object[] listenerList = listeners.getListenerList();
            for (int i = 0; i <= listenerList.length-2; i += 2) {
                if (listenerList[i] == PropertyChangeListener.class && 
                    (PropertyChangeListener)listenerList[i+1] instanceof Serializable)  {
                    s.writeObject((PropertyChangeListener)listenerList[i+1]);
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
    transient private EventListenerList listeners;
    // "children" contains SwingPropertyChangeSupports for individual properties
    private java.util.Hashtable children;
    private Object source;

    // Serialization version ID
    static final long serialVersionUID = 7162625831330845068L;
}
