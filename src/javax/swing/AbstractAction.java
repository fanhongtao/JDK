/*
 * @(#)AbstractAction.java	1.25 98/08/28
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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Hashtable;
import java.io.Serializable;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This class provides default implementations for the JFC Action 
 * interface. Standard behaviors like the get and set methods for
 * Action object properties (icon, text, and enabled) are defined
 * here. The developer need only subclass this abstract class and
 * define the <code>actionPerformed</code> method. 
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.25 08/28/98
 * @author Georges Saab
 * @see Action
 */
public abstract class AbstractAction implements Action, Cloneable, Serializable 
{
    protected boolean enabled = true;
    // Will be replaced by a lighter weight storage mechanism soon!
    private Hashtable keyTable = new Hashtable(5);

    /**
     * Defines an Action object with a default description string
     * and default icon.
     */
    public AbstractAction() {}
    
    /**
     * Defines an Action object with the specified description string
     * and a default icon.
     */
    public AbstractAction(String name) {
	putValue(Action.NAME, name);
    }

    /**
     * Defines an Action object with the specified description string
     * and a the specified icon.
     */
    public AbstractAction(String name, Icon icon) {
	this(name);
	putValue(Action.SMALL_ICON, icon);
    }
    
    /** 
     * Gets the Object associated with the specified key.
     *
     * @return the Object stored with this key
     * @see Action#getValue
     */
    public Object getValue(String key) {
	return keyTable.get(key);
    }
    
    /** 
     * Sets the Value associated with the specified key.
     *
     * @param key  the String that identifies the stored object
     * @param newValue the Object to store using this key
     * @see Action#putValue 
     */
    public synchronized void putValue(String key, Object newValue) {
	Object oldValue = null;
	if (keyTable.containsKey(key))
	    oldValue = keyTable.get(key);
	if (newValue != null)
	    keyTable.put(key,newValue);
	firePropertyChange(key, oldValue, newValue);
    }

    /**
     * Returns true if the action is enabled.
     *
     * @return true if the action is enabled
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
    public synchronized void setEnabled(boolean newValue) {
	boolean oldValue = this.enabled;
	this.enabled = newValue;
	firePropertyChange("enabled", 
			   new Boolean(oldValue), new Boolean(newValue));
    }

    /*
     * If any PropertyChangeListeners have been registered, the
     * changeSupport field describes them.
     */
    protected SwingPropertyChangeSupport changeSupport;

    /**
     * Support for reporting bound property changes.  This method can be called
     * when a bound property has changed and it will send the appropriate
     * PropertyChangeEvent to any registered PropertyChangeListeners.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired in response to setting
     * a bound property, e.g. setFont, setBackground, or setForeground.
     * Note that if the current component is inheriting its foreground, 
     * background, or font from its container, then no event will be 
     * fired in response to a change in the inherited property.
     *
     * @param listener  The PropertyChangeListener to be added
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
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
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
     * Clone the abstract action. This gives the clone
     * its own copy of the key/value list,
     * which is not handled for you by Object.clone()
     **/

    protected Object clone() throws CloneNotSupportedException {
	AbstractAction newAction = (AbstractAction)super.clone();
	newAction.keyTable = (Hashtable)keyTable.clone();
	return newAction;
    }
}
