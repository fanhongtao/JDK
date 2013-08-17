/*
 * @(#)VetoableChangeSupport.java	1.14 98/07/01
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
 * This is a utility class that can be used by beans that support constrained
 * properties.  You can use an instance of this class as a member field
 * of your bean and delegate various work to it.
 */

public class VetoableChangeSupport implements java.io.Serializable {

    /**
     * @sourceBean  The bean to be given as the source for any events.
     */

    public VetoableChangeSupport(Object sourceBean) {
	source = sourceBean;
    }

    /**
     * Add a VetoableListener to the listener list.
     *
     * @param listener  The VetoableChangeListener to be added
     */

    public synchronized void addVetoableChangeListener(
					VetoableChangeListener listener) {
	if (listeners == null) {
	    listeners = new java.util.Vector();
	}
	listeners.addElement(listener);
    }

    /**
     * Remove a VetoableChangeListener from the listener list.
     *
     * @param listener  The VetoableChangeListener to be removed
     */
    public synchronized void removeVetoableChangeListener(
					VetoableChangeListener listener) {
	if (listeners == null) {
	    return;
	}
	listeners.removeElement(listener);
    }

    /**
     * Report a vetoable property update to any registered listeners.  If
     * anyone vetos the change, then fire a new event reverting everyone to 
     * the old value and then rethrow the PropertyVetoException.
     * <p>
     * No event is fired if old and new are equal and non-null.
     *
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @exception PropertyVetoException if the recipient wishes the property
     *              change to be rolled back.
     */
    public void fireVetoableChange(String propertyName, 
					Object oldValue, Object newValue)
					throws PropertyVetoException {

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

	try {
	    for (int i = 0; i < targets.size(); i++) {
	        VetoableChangeListener target = 
				(VetoableChangeListener)targets.elementAt(i);
	        target.vetoableChange(evt);
	    }
	} catch (PropertyVetoException veto) {
	    // Create an event to revert everyone to the old value.
       	    evt = new PropertyChangeEvent(source, propertyName, newValue, oldValue);
	    for (int i = 0; i < targets.size(); i++) {
		try {
	            VetoableChangeListener target =
				(VetoableChangeListener)targets.elementAt(i);
	            target.vetoableChange(evt);
		} catch (PropertyVetoException ex) {
		     // We just ignore exceptions that occur during reversions.
		}
	    }
	    // And now rethrow the PropertyVetoException.
	    throw veto;
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
	    for(int i = 0; i < listeners.size(); i++) {
	        VetoableChangeListener l = (VetoableChangeListener)v.elementAt(i);
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
	    addVetoableChangeListener((VetoableChangeListener)listenerOrNull);
        }
    }

    transient private java.util.Vector listeners;
    private Object source;
    private int vetoableChangeSupportSerializedDataVersion = 1;
}
