/*
 * @(#)AbstractAction.java	1.51 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This class provides default implementations for the JFC <code>Action</code> 
 * interface. Standard behaviors like the get and set methods for
 * <code>Action</code> object properties (icon, text, and enabled) are defined
 * here. The developer need only subclass this abstract class and
 * define the <code>actionPerformed</code> method. 
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.51 12/19/03
 * @author Georges Saab
 * @see Action
 */
public abstract class AbstractAction implements Action, Cloneable, Serializable 
{
    /**
     * Specifies whether action is enabled; the default is true.
     */
    protected boolean enabled = true;


    /**
     * Contains the array of key bindings.
     */
    private transient ArrayTable arrayTable;
    
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public AbstractAction() {
    }
    
    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public AbstractAction(String name) {
	putValue(Action.NAME, name);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public AbstractAction(String name, Icon icon) {
	this(name);
	putValue(Action.SMALL_ICON, icon);
    }
    
    /** 
     * Gets the <code>Object</code> associated with the specified key.
     *
     * @param key a string containing the specified <code>key</code>
     * @return the binding <code>Object</code> stored with this key; if there
     *		are no keys, it will return <code>null</code>
     * @see Action#getValue
     */
    public Object getValue(String key) {
	if (arrayTable == null) {
	    return null;
	}
	return arrayTable.get(key);
    }
    
    /** 
     * Sets the <code>Value</code> associated with the specified key.
     *
     * @param key  the <code>String</code> that identifies the stored object
     * @param newValue the <code>Object</code> to store using this key
     * @see Action#putValue 
     */
    public void putValue(String key, Object newValue) {
	Object oldValue = null;
	if (arrayTable == null) {
	    arrayTable = new ArrayTable();
	}
	if (arrayTable.containsKey(key))
	    oldValue = arrayTable.get(key);
	// Remove the entry for key if newValue is null
	// else put in the newValue for key.
	if (newValue == null) {
	    arrayTable.remove(key);
	} else {
	    arrayTable.put(key,newValue);
	}
	firePropertyChange(key, oldValue, newValue);
    }

    /**
     * Returns true if the action is enabled.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Enables or disables the action.
     *
     * @param newValue  true to enable the action, false to
     *                  disable it
     * @see Action#setEnabled
     */
    public void setEnabled(boolean newValue) {
	boolean oldValue = this.enabled;

	if (oldValue != newValue) {
	    this.enabled = newValue;
	    firePropertyChange("enabled", 
			       Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
	}
    }


    /**
     * Returns an array of <code>Object</code>s which are keys for
     * which values have been set for this <code>AbstractAction</code>,
     * or <code>null</code> if no keys have values set.
     * @return an array of key objects, or <code>null</code> if no
     *			keys have values set
     * @since 1.3
     */
    public Object[] getKeys() {
	if (arrayTable == null) {
	    return null;
	}
	Object[] keys = new Object[arrayTable.size()];
	arrayTable.getKeys(keys);
	return keys;
    }

    /**
     * If any <code>PropertyChangeListeners</code> have been registered, the
     * <code>changeSupport</code> field describes them.
     */
    protected SwingPropertyChangeSupport changeSupport;

    /**
     * Supports reporting bound property changes.  This method can be called
     * when a bound property has changed and it will send the appropriate
     * <code>PropertyChangeEvent</code> to any registered 
     * <code>PropertyChangeListeners</code>.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null || 
	    (oldValue != null && newValue != null && oldValue.equals(newValue))) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to setting
     * a bound property, e.g. <code>setFont</code>, <code>setBackground</code>,
     * or <code>setForeground</code>.
     * Note that if the current component is inheriting its foreground, 
     * background, or font from its container, then no event will be 
     * fired in response to a change in the inherited property.
     *
     * @param listener  The <code>PropertyChangeListener</code> to be added
     *
     * @see Action#addPropertyChangeListener 
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
	    changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }


    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list.
     * This removes a <code>PropertyChangeListener</code> that was registered
     * for all properties.
     *
     * @param listener  the <code>PropertyChangeListener</code> to be removed
     *
     * @see Action#removePropertyChangeListener 
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
    }


    /**
     * Returns an array of all the <code>PropertyChangeListener</code>s added
     * to this AbstractAction with addPropertyChangeListener().
     *
     * @return all of the <code>PropertyChangeListener</code>s added or an empty
     *         array if no listeners have been added
     * @since 1.4
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return changeSupport.getPropertyChangeListeners();
    }


    /**
     * Clones the abstract action. This gives the clone
     * its own copy of the key/value list,
     * which is not handled for you by <code>Object.clone()</code>.
     **/

    protected Object clone() throws CloneNotSupportedException {
	AbstractAction newAction = (AbstractAction)super.clone();
	synchronized(this) {
	    if (arrayTable != null) {
		newAction.arrayTable = (ArrayTable)arrayTable.clone();
	    }
	}
	return newAction;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
	// Store the default fields
        s.defaultWriteObject();

        // And the keys
        ArrayTable.writeArrayTable(s, arrayTable);
    }

    private void readObject(ObjectInputStream s) throws ClassNotFoundException,
	IOException {
        s.defaultReadObject();
	for (int counter = s.readInt() - 1; counter >= 0; counter--) {
	    putValue((String)s.readObject(), s.readObject());
	}
    }
}
