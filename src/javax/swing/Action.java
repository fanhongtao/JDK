/*
 * @(#)Action.java	1.30 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * The <code>Action</code> interface provides a useful extension to the
 * <code>ActionListener</code>
 * interface in cases where the same functionality may be accessed by
 * several controls.
 * <p>
 * In addition to the <code>actionPerformed</code> method defined by the 
 * <code>ActionListener</code> interface, this interface allows the
 * application to define, in a single place:
 * <ul>
 * <li>One or more text strings that describe the function. These strings
 *     can be used, for example, to display the flyover text for a button
 *     or to set the text in a menu item.
 * <li>One or more icons that depict the function. These icons can be used
 *     for the images in a menu control, or for composite entries in a more
 *     sophisticated user interface.
 * <li>The enabled/disabled state of the functionality. Instead of having
 *     to separately disable the menu item and the toolbar button, the
 *     application can disable the function that implements this interface.
 *     All components which are registered as listeners for the state change
 *     then know to disable event generation for that item and to modify the 
 *     display accordingly.
 * </ul>
 * Certain containers, including menus and tool bars, know how to add an
 * <code>Action</code> object. When an <code>Action</code> object is added
 * to such a container, the container:
 * <ol type="a">
 * <li>Creates a component that is appropriate for that container 
 *     (a tool bar creates a button component, for example).
 * <li>Gets the appropriate property(s) from the <code>Action</code> object to 
 *     customize the component (for example, the icon image and flyover text).
 * <li>Checks the initial state of the <code>Action</code> object to determine 
 *     if it is enabled or disabled, and renders the component in the 
 *     appropriate fashion.
 * <li>Registers a listener with the <code>Action</code> object so that is 
 *     notified of state changes. When the <code>Action</code> object changes
 *     from enabled to disabled,
 *     or back, the container makes the appropriate revisions to the
 *     event-generation mechanisms and renders the component accordingly.
 * </ol>
 * For example, both a menu item and a toolbar button could access a
 * <code>Cut</code> action object. The text associated with the object is 
 * specified as "Cut", and an image depicting a pair of scissors is specified 
 * as its icon. The <code>Cut</code> action-object can then be added to a
 * menu and to a tool bar. Each container does the appropriate things with the
 * object, and invokes its <code>actionPerformed</code> method when the
 * component associated with it is activated. The application can then disable 
 * or enable the application object without worrying about what user-interface
 * components are connected to it.
 * <p>
 * This interface can be added to an existing class or used to create an
 * adapter (typically, by subclassing <code>AbstractAction</code>).
 * The <code>Action</code> object
 * can then be added to multiple <code>Action</code>-aware containers
 * and connected to <code>Action</code>-capable
 * components. The GUI controls can then be activated or
 * deactivated all at once by invoking the <code>Action</code> object's
 * <code>setEnabled</code> method.
 * <p>
 * Note that <code>Action</code> implementations tend to be more expensive
 * in terms of storage than a typical <code>ActionListener</code>,
 * which does not offer the benefits of centralized control of
 * functionality and broadcast of property changes.  For this reason,
 * you should take care to only use <code>Action</code>s where their benefits
 * are desired, and use simple <code>ActionListener</code>s elsewhere.
 *
 * @version 1.30 12/19/03
 * @author Georges Saab
 * @see AbstractAction
 */
public interface Action extends ActionListener {
    /**
     * Useful constants that can be used as the storage-retrieval key 
     * when setting or getting one of this object's properties (text
     * or icon).
     */
    /**
     * Not currently used.
     */
    public static final String DEFAULT = "Default";
    /** 
     * The key used for storing the <code>String</code> name
     * for the action, used for a menu or button.
     */
    public static final String NAME = "Name";
    /**
     * The key used for storing a short <code>String</code>
     * description for the action, used for tooltip text.
     */
    public static final String SHORT_DESCRIPTION = "ShortDescription";
    /**
     * The key used for storing a longer <code>String</code>
     * description for the action, could be used for context-sensitive help.
     */
    public static final String LONG_DESCRIPTION = "LongDescription";
    /**
     * The key used for storing a small <code>Icon</code>, such
     * as <code>ImageIcon</code>, for the action, used for toolbar buttons.  
     */
    public static final String SMALL_ICON = "SmallIcon";

    /**
     * The key used to determine the command <code>String</code> for the
     * <code>ActionEvent</code> that will be created when an
     * <code>Action</code> is going to be notified as the result of
     * residing in a <code>Keymap</code> associated with a
     * <code>JComponent</code>.
     */
    public static final String ACTION_COMMAND_KEY = "ActionCommandKey";

    /**
     * The key used for storing a <code>KeyStroke</code> to be used as the
     * accelerator for the action.
     *
     * @since 1.3
     */
    public static final String ACCELERATOR_KEY="AcceleratorKey";
    
    /**
     * The key used for storing a <code>KeyEvent</code> to be used as
     * the mnemonic for the action.
     *
     * @since 1.3
     */
    public static final String MNEMONIC_KEY="MnemonicKey";

    /**
     * Gets one of this object's properties
     * using the associated key.
     * @see #putValue
     */
    public Object getValue(String key);
    /**
     * Sets one of this object's properties
     * using the associated key. If the value has
     * changed, a <code>PropertyChangeEvent</code> is sent
     * to listeners.
     *
     * @param key    a <code>String</code> containing the key
     * @param value  an <code>Object</code> value
     */
    public void putValue(String key, Object value);

    /**
     * Sets the enabled state of the <code>Action</code>.  When enabled,
     * any component associated with this object is active and
     * able to fire this object's <code>actionPerformed</code> method.
     * If the value has changed, a <code>PropertyChangeEvent</code> is sent
     * to listeners.
     *
     * @param  b true to enable this <code>Action</code>, false to disable it
     */
    public void setEnabled(boolean b);
    /**
     * Returns the enabled state of the <code>Action</code>. When enabled,
     * any component associated with this object is active and
     * able to fire this object's <code>actionPerformed</code> method.
     *
     * @return true if this <code>Action</code> is enabled
     */
    public boolean isEnabled();

    /**
     * Adds a <code>PropertyChange</code> listener. Containers and attached
     * components use these methods to register interest in this 
     * <code>Action</code> object. When its enabled state or other property
     * changes, the registered listeners are informed of the change.
     *
     * @param listener  a <code>PropertyChangeListener</code> object
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    /**
     * Removes a <code>PropertyChange</code> listener.
     *
     * @param listener  a <code>PropertyChangeListener</code> object
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

}
