/*
 * @(#)AbstractAction.java	1.48 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.48 01/23/03
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

    /*
     * Private storage mechanism for Action key-value pairs.
     * In most cases this will be an array of alternating
     * key-value pairs.  As it grows larger it is scaled
     * up to a Hashtable.
     * <p>This is also used by InputMap and ActionMap, this does no
     * synchronization, if you need thread safety synchronize on another
     * object before calling this.
     */

    static class ArrayTable implements Cloneable {
	// Our field for storage
	private Object table = null;
	private static final int ARRAY_BOUNDARY = 8;

	/*
	 * Put the key-value pair into storage
	 */
	public void put(Object key, Object value){
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
	 * Gets the value for key
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
	 * Returns the number of pairs in storage
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
	 * Removes the key and its value
	 * Returns the value for the pair removed
	 */
	public Object remove(Object key){
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

	/**
	 * Removes all the mappings.
	 */
	public void clear() {
	    table = null;
	}

 	/* 
	 * Returns a clone of the <code>ArrayTable</code>.
	 */
	public Object clone() {
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

	/**
	 * Returns the keys of the table, or <code>null</code> if there 
	 * are currently no bindings.
         * @param keys  array of keys
         * @return an array of bindings
	 */
	public Object[] getKeys(Object[] keys) {
	    if (table == null) {
		return null;
	    }
	    if (isArray()) {
		Object[] array = (Object[])table;			
		if (keys == null) {
		    keys = new Object[array.length / 2];
		}
		for (int i = 0, index = 0 ;i < array.length-1 ; i+=2,
			 index++) {
		    keys[index] = array[i];
		}
	    } else {
		Hashtable tmp = (Hashtable)table;
		Enumeration enum = tmp.keys();
		int counter = tmp.size();
		if (keys == null) {
		    keys = new Object[counter];
		}
		while (counter > 0) {
		    keys[--counter] = enum.nextElement();
		}
	    }
	    return keys;
	}

	/*
	 * Returns true if the current storage mechanism is 
	 * an array of alternating key-value pairs.
	 */
	private boolean isArray(){
	    return (table instanceof Object[]);
	}

	/*
	 * Grows the storage from an array to a hashtable.
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
	 * Shrinks the storage from a hashtable to an array.
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


        /**
         * Writes the passed in ArrayTable to the passed in ObjectOutputStream.
         * The data is saved as an integer indicating how many key/value
         * pairs are being archived, followed by the the key/value pairs. If
         * <code>table</code> is null, 0 will be written to <code>s</code>.
         * <p>
         * This is a convenience method that ActionMap/InputMap and
         * AbstractAction use to avoid having the same code in each class.
         */
        static void writeArrayTable(ObjectOutputStream s, ArrayTable table) throws IOException {
            Object keys[];

            if (table == null || (keys = table.getKeys(null)) == null) {
                s.writeInt(0);
            }
            else {
                // Determine how many keys have Serializable values, when
                // done all non-null values in keys identify the Serializable
                // values.
                int validCount = 0;

                for (int counter = 0; counter < keys.length; counter++) {
                    if ((keys[counter] instanceof Serializable) &&
                        (table.get(keys[counter]) instanceof Serializable)) {
                        validCount++;
                    }
                    else {
                        keys[counter] = null;
                    }
                }
                // Write ou the Serializable key/value pairs.
                s.writeInt(validCount);
                if (validCount > 0) {
                    for (int counter = 0; counter < keys.length; counter++) {
                        if (keys[counter] != null) {
                            s.writeObject(keys[counter]);
                            s.writeObject(table.get(keys[counter]));
                            if (--validCount == 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
