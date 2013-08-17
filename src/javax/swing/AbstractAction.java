/*
 * @(#)AbstractAction.java	1.32 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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
import java.util.Enumeration;
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
 * @version 1.32 04/22/99
 * @author Georges Saab
 * @see Action
 */
public abstract class AbstractAction implements Action, Cloneable, Serializable 
{
    protected boolean enabled = true;
    private ArrayTable arrayTable = new ArrayTable();

    /**
     * Defines an Action object with a default description string
     * and default icon.
     */
    public AbstractAction() {
    }
    
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
	return arrayTable.get(key);
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
	/*
	  if (keyTable.containsKey(key))
	    oldValue = keyTable.get(key);
	// Remove the entry for key if newValue is null
	// else put in the newValue for key.
	if (newValue == null) {
		keyTable.remove(key);
	} else {
		keyTable.put(key,newValue);
	}
	*/
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
	newAction.arrayTable = (ArrayTable)arrayTable.clone();
	return newAction;
    }

    /*
     * Private storage mechanism for Action key-value pairs.
     * In most cases this will be an array of alternating
     * key-value pairs.  As it grows larger it is scaled
     * up to a Hashtable.
     */

    private class ArrayTable implements Cloneable, Serializable {
	// Our field for storage
	private Object table = null;
	private static final int ARRAY_BOUNDARY = 8;

	/*
	 * Put the key-value pair into storage
	 */
	public synchronized void put(Object key, Object value){
	    if (table==null) {
		table = new Object[] {key, value};
	    } else {
		int size = size();
		if (size < ARRAY_BOUNDARY) {	      // We are an array
		    if (containsKey(key)) {
			Object[] tmp = (Object[])table;
			for (int i = 0; i<tmp.length-1; i+=2) {
			    if (tmp[i].equals(key)) {
				tmp[i+1]=value;
				break;
			    }
			}
		    } else {
			Object[] array = (Object[])table;
			int i = array.length;
			Object[] tmp = new Object[i+2];
			System.arraycopy(array, 0, tmp, 0, i);
			
			tmp[i] = key;
			tmp[i+1] = value;		    
			table = tmp;
		    }
		} else {		     // We are a hashtable		    
		    if ((size==ARRAY_BOUNDARY) && isArray()) {   
			grow();
		    }
		    ((Hashtable)table).put(key, value);		    
		}	    
	    }
	}
	
	/*
	 * Get the value for key
	 */
	public Object get(Object key) {
	    Object value = null;
	    if (table !=null) {
		if (isArray()) {
		    Object[] array = (Object[])table;
		    for (int i = 0; i<array.length-1; i+=2) {
			if (array[i].equals(key)) {
			    value = array[i+1];
			    break;
			}
		    }
		} else {
		    value = ((Hashtable)table).get(key);
		}
	    }
	    return value;		
	}
    
	/*
	 * Return the number of pairs in storage
	 */
	public int size() {
	    int size;
	    if (table==null)
		return 0;
	    if (isArray()) {
		size = ((Object[])table).length/2;
	    } else {       
		size = ((Hashtable)table).size();
	    }	
	    return size;
	}
	
	/*
	 * Returns true if we have a value for the key
	 */
	public boolean containsKey(Object key) {
	    boolean contains = false;
	    if (table !=null) {
		if (isArray()) {
		    Object[] array = (Object[])table;
		    for (int i = 0; i<array.length-1; i+=2) {
			if (array[i].equals(key)) {
			    contains = true;
			    break;
			}
		    }
		} else {
		    contains = ((Hashtable)table).containsKey(key);
		}
	    }
	    return contains;		
	}
    
	/*
	 * Remove the key and its value
	 * Returns the value for the pair removed
	 */
	public synchronized Object remove(Object key){
	    Object value = null;
	    if (key==null) {
		return null;
	    }
	    if (table !=null) {
		if (isArray()){
		    // Is key on the list?
		    int index = -1;
		    Object[] array = (Object[])table;
		    for (int i = array.length-2; i>=0; i-=2) {
			if (array[i].equals(key)) {
			    index = i;
			    value = array[i+1];
			    break;
			}
		    }
		    
		    // If so,  remove it
		    if (index != -1) {
			Object[] tmp = new Object[array.length-2];
			// Copy the list up to index
			System.arraycopy(array, 0, tmp, 0, index);
			// Copy from two past the index, up to
			// the end of tmp (which is two elements
			// shorter than the old list)
			if (index < tmp.length)
			    System.arraycopy(array, index+2, tmp, index, 
					     tmp.length - index);
			// set the listener array to the new array or null
			table = (tmp.length == 0) ? null : tmp;
		    }
		} else {
		    value = ((Hashtable)table).remove(key);
		}
		if (size()==7 && !isArray()) {
		    shrink();
		}
	    }
	    return value;
	}

 	/* 
	 * Return a clone of the ArrayTable
	 */
	public synchronized Object clone() {
	    ArrayTable newArrayTable = new ArrayTable();
	    if (isArray()) {
		Object[] array = (Object[])table;			
		for (int i = 0 ;i < array.length-1 ; i+=2) {
		    newArrayTable.put(array[i], array[i+1]);
		}
	    } else {
		Hashtable tmp = (Hashtable)table;
		Enumeration keys = tmp.keys();
		while (keys.hasMoreElements()) {	    
		    Object o = keys.nextElement();
		    newArrayTable.put(o,tmp.get(o));
		}
	    }
	    return newArrayTable;
	}
    
	/*
	 * Return true if the current storage mechanism is 
	 * an array of alternating key-value pairs
	 */
	private boolean isArray(){
	    return (table instanceof Object[]);
	}

	/*
	 * Grow the storage from an array to a hashtable
	 */
	private void grow() {
	    Object[] array = (Object[])table;
	    Hashtable tmp = new Hashtable(array.length/2);
	    for (int i = 0; i<array.length; i+=2) {
		tmp.put(array[i], array[i+1]);
	    }
	    table = tmp;
	}
    
	/*
	 * Shrink the storage from a hashtable to an array
	 */
	private void shrink() {
	    Hashtable tmp = (Hashtable)table;
	    Object[] array = new Object[tmp.size()*2];
	    Enumeration keys = tmp.keys();
	    int j = 0;
	
	    while (keys.hasMoreElements()) {	    
		Object o = keys.nextElement();
		array[j] = o;
		array[j+1] = tmp.get(o);
		j+=2;
	    }
	    table = array;	
	}
    }
}
