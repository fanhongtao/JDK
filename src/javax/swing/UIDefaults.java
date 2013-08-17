/*
 * @(#)UIDefaults.java	1.28 98/08/28
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


import javax.swing.plaf.ComponentUI;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;

import java.util.Hashtable;
import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;
import java.lang.reflect.Method;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * A table of defaults for Swing components.  Applications can set/get
 * default values via the UIManager.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see UIManager
 * @version 1.28 08/28/98
 * @author Hans Muller
 */
public class UIDefaults extends Hashtable
{
    private static final Object PENDING = new String("Pending");

    private SwingPropertyChangeSupport changeSupport;


    /**
     * Create an empty defaults table.
     */
    public UIDefaults() {
        super();
    }


    /**
     * Create a defaults table initialized with the specified
     * key/value pairs.  For example:
     * <pre>
        Object[] uiDefaults = {
             "Font", new Font("Dialog", Font.BOLD, 12),
            "Color", Color.red,
             "five", new Integer(5)
        }
        UIDefaults myDefaults = new UIDefaults(uiDefaults);
     * </pre>
     */
    public UIDefaults(Object[] keyValueList) {
        super(keyValueList.length / 2);
        for(int i = 0; i < keyValueList.length; i += 2) {
            super.put(keyValueList[i], keyValueList[i + 1]);
        }
    }


    /**
     * Returns the value for key.  If the value is a
     * <code>UIDefaults.LazyValue</code> then the real
     * value is computed with <code>LazyValue.createValue()</code>,
     * the table entry is replaced, and the real value is returned.
     * If the value is an <code>UIDefaults.ActiveValue</code>
     * the table entry is not replaced - the value is computed
     * with ActiveValue.createValue() for each get() call.
     *
     * @see LazyValue
     * @see ActiveValue
     * @see java.util.Hashtable#get
     */
    public Object get(Object key)
    {
        /* Quickly handle the common case, without grabbing
         * a lock.
         */
        Object value = super.get(key);
        if ((value != PENDING) &&
            !(value instanceof ActiveValue) &&
            !(value instanceof LazyValue)) {
            return value;
        }

        /* If the LazyValue for key is being constructed by another
         * thread then wait and then return the new value, otherwise drop
         * the lock and construct the ActiveValue or the LazyValue.
         * We use the special value PENDING to mark LazyValues that
         * are being constructed.
         */
        synchronized(this) {
            value = super.get(key);
            if (value == PENDING) {
                do {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException e) {
                    }
                    value = super.get(key);
                }
                while(value == PENDING);
                return value;
            }
            else if (value instanceof LazyValue) {
                super.put(key, PENDING);
            }
            else if (!(value instanceof ActiveValue)) {
                return value;
            }
        }

        /* At this point we know that the value of key was
         * a LazyValue or an ActiveValue.
         */
        if (value instanceof LazyValue) {
            try {
                /* If an exception is thrown we'll just put the LazyValue
                 * back in the table.
                 */
                value = ((LazyValue)value).createValue(this);
            }
            finally {
                synchronized(this) {
                    if (value == null) {
                        super.remove(key);
                    }
                    else {
                        super.put(key, value);
                    }
                    this.notify();
                }
            }
        }
        else {
            value = ((ActiveValue)value).createValue(this);
        }

        return value;
    }


    /**
     * Set the value of <code>key</code> to <code>value</code>.
     * If <code>key</code> is a string and the new value isn't
     * equal to the old one, fire a PropertyChangeEvent.  If value
     * is null, the key is removed from the table.
     *
     * @param key    the unique Object who's value will be used to 
     *               retreive the data value associated with it
     * @param value  the new Object to store as data under that key
     * @return the previous Object value, or null
     * @see #putDefaults
     * @see java.util.Hashtable#put
     */
    public Object put(Object key, Object value) {
        Object oldValue = (value == null) ? super.remove(key) : super.put(key, value);
        if (key instanceof String) {
            firePropertyChange((String)key, oldValue, value);
        }
        return oldValue;
    }


    /**
     * Put all of the key/value pairs in the database and
     * unconditionally generate one PropertyChangeEvent.
     * The events oldValue and newValue will be null and its
     * propertyName will be "UIDefaults".
     *
     * @see #put
     * @see java.util.Hashtable#put
     */
    public void putDefaults(Object[] keyValueList) {
        for(int i = 0; i < keyValueList.length; i += 2) {
            Object value = keyValueList[i + 1];
            if (value == null) {
                super.remove(keyValueList[i]);
            }
            else {
                super.put(keyValueList[i], value);
            }
        }
        firePropertyChange("UIDefaults", null, null);
    }


    /**
     * If the value of <code>key</code> is a Font return it, otherwise
     * return null.
     */
    public Font getFont(Object key) {
        Object value = get(key);
        return (value instanceof Font) ? (Font)value : null;
    }

    /**
     * If the value of <code>key</code> is a Color return it, otherwise
     * return null.
     */
    public Color getColor(Object key) {
        Object value = get(key);
        return (value instanceof Color) ? (Color)value : null;
    }


    /**
     * If the value of <code>key</code> is an Icon return it, otherwise
     * return null.
     */
    public Icon getIcon(Object key) {
        Object value = get(key);
        return (value instanceof Icon) ? (Icon)value : null;
    }


    /**
     * If the value of <code>key</code> is a Border return it, otherwise
     * return null.
     */
    public Border getBorder(Object key) {
        Object value = get(key);
        return (value instanceof Border) ? (Border)value : null;
    }


    /**
     * If the value of <code>key</code> is a String return it, otherwise
     * return null.
     */
    public String getString(Object key) {
        Object value = get(key);
        return (value instanceof String) ? (String)value : null;
    }

    /**
     * If the value of <code>key</code> is a Integer return its
     * integer value, otherwise return 0.
     */
    public int getInt(Object key) {
        Object value = get(key);
        return (value instanceof Integer) ? ((Integer)value).intValue() : 0;
    }

    /**
     * If the value of <code>key</code> is a Insets return it, otherwise
     * return null.
     */
    public Insets getInsets(Object key) {
        Object value = get(key);
        return (value instanceof Insets) ? (Insets)value : null;
    }

    /**
     * If the value of <code>key</code> is a Dimension return it, otherwise
     * return null.
     */
    public Dimension getDimension(Object key) {
        Object value = get(key);
        return (value instanceof Dimension) ? (Dimension)value : null;
    }


    /**
     * The value of get(uidClassID) must be the String name of a
     * class that implements the corresponding ComponentUI
     * class.  If the class hasn't been loaded before, this method looks 
     * up the class with <code>uiClassLoader.loadClass()</code> if a non null
     * class loader is provided, <code>classForName()</code> otherwise.
     * <p>
     * If a mapping for uiClassID exists or if the specified
     * class can't be found, return null.
     * <p>
     * This method is used by <code>getUI</code>, it's usually
     * not neccessary to call it directly.
     *
     * @return The value of <code>Class.forName(get(uidClassID))</code>.
     * @see #getUI
     */
    public Class getUIClass(String uiClassID, ClassLoader uiClassLoader)
    {
        try {
            String className = (String)get(uiClassID);
            Class cls = (Class)get(className);
            if (cls == null) {
		if (uiClassLoader == null) {
		    cls = Class.forName(className);
		}
		else {
		    cls = uiClassLoader.loadClass(className);
		}
                if (cls != null) {
                    // Save lookup for future use, as forName is slow.
                    put(className, cls);
                }
            }
            return cls;
        } 
	catch (ClassNotFoundException e) {
            return null;
        } 
	catch (ClassCastException e) {
            return null;
        }
    }


    /**
     * Returns the L&F class that renders this component.
     *
     * @return the Class object returned by getUIClass(uiClassID, null)
     */
    public Class getUIClass(String uiClassID) {
	return getUIClass(uiClassID, null);
    }


    /**
     * If getUI() fails for any reason, it calls this method before
     * returning null.  Subclasses may choose to do more or
     * less here.
     *
     * @param msg Message string to print.
     * @see #getUI
     */
    protected void getUIError(String msg) {
        System.err.println("UIDefaults.getUI() failed: " + msg);
        try {
            throw new Error();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Create an ComponentUI implementation for the
     * specified component.  In other words create the look
     * and feel specific delegate object for <code>target</code>.
     * This is done in two steps:
     * <ul>
     * <li> Lookup the name of the ComponentUI implementation
     * class under the value returned by target.getUIClassID().
     * <li> Use the implementation classes static <code>createUI()</code>
     * method to construct a look and feel delegate.
     * </ul>
     */
    public ComponentUI getUI(JComponent target)
    {
	ClassLoader uiClassLoader = target.getClass().getClassLoader();
        Class uiClass = getUIClass(target.getUIClassID(), uiClassLoader);
        Object uiObject = null;

        if (uiClass == null) {
            getUIError("no ComponentUI class for: " + target);
        }
        else {
            try {
		Method m = (Method)get(uiClass);
		if (m == null) {
		    Class acClass = javax.swing.JComponent.class;
		    m = uiClass.getMethod("createUI", new Class[]{acClass});
		    put(uiClass, m);
		}
		uiObject = m.invoke(null, new Object[]{target});
            }
            catch (NoSuchMethodException e) {
                getUIError("static createUI() method not found in " + uiClass);
            }
            catch (Exception e) {
                getUIError("createUI() failed for " + target + " " + e);
            }
        }

        return (ComponentUI)uiObject;
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired whenever a default
     * is changed.
     *
     * @param listener  The PropertyChangeListener to be added
     * @see java.beans.PropertyChangeSupport
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
     * @see java.beans.PropertyChangeSupport
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }


    /**
     * Support for reporting bound property changes.  If oldValue and
     * newValue are not equal and the PropertyChangeEvent listener list
     * isn't empty, then fire a PropertyChange event to each listener.
     *
     * @param propertyName  The programmatic name of the property that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @see java.beans.PropertyChangeSupport
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }


    /**
     * This class enables one to store an entry in the defaults
     * table that isn't constructed until the first time it's
     * looked up with one of the <code>getXXX(key)</code> methods.
     * Lazy values are useful for defaults that are expensive
     * to construct or are seldom retrieved.  The first time
     * a LazyValue is retrieved its "real value" is computed
     * by calling <code>LazyValue.createValue()</code> and the real
     * value is used to replace the LazyValue in the UIDefaults
     * table.  Subsequent lookups for the same key return
     * the real value.  Here's an example of a LazyValue that
     * constructs a Border:
     * <pre>
     *  Object borderLazyValue = new UIDefaults.LazyValue() {
     *      public Object createValue(UIDefaults table) {
     *          return new BorderFactory.createLoweredBevelBorder();
     *      }
     *  };
     *
     *  uiDefaultsTable.put("MyBorder", borderLazyValue);
     * </pre>
     *
     * @see UIDefaults#get
     */
    public interface LazyValue {
        /**
         * Creates the actual value retrieved from the UIDefaults
         * table. When an object that implements this interface is
         * retrieved from the table, this method is used to create
         * the real value, which is then stored in the table and
         * returned to the calling method.
         *
         * @param table  a UIDefaults table
         * @return the created Object 
         */
        Object createValue(UIDefaults table);
    }


    /**
     * This class enables one to store an entry in the defaults
     * table that's constructed each time it's looked up with one of
     * the <code>getXXX(key)</code> methods. Here's an example of
     * an ActiveValue that constructs a DefaultListCellRenderer
     * <pre>
     *  Object cellRendererActiveValue = new UIDefaults.ActiveValue() {
     *      public Object createValue(UIDefaults table) {
     *          return new DefaultListCellRenderer();
     *      }
     *  };
     *
     *  uiDefaultsTable.put("MyRenderer", cellRendererActiveValue);
     * </pre>
     *
     * @see UIDefaults#get
     */
    public interface ActiveValue {
        /**
         * Creates the value retrieved from the UIDefaults table.
         * The object is created each time it is accessed.
         *
         * @param table  a UIDefaults table
         * @return the created Object 
         */
        Object createValue(UIDefaults table);
    }
}

