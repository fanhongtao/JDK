/*
 * @(#)Action.java	1.16 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * The JFC Action interface provides a useful extension to the ActionListner
 * interface in cases where the same functionality may be accessed by
 * several controls.
 * <p>
 * In addition to the <code>actionPerformed</code> method
 * defined by the ActionListener interface, this interface allows the
 * application to define, in a single place:
 * <ul>
 * <li>One or more text strings that describe the function. These strings
 *     can be used, for example, to display the flyover text for a button
 *     or to set the text in a menu item.
 * <li>One or more icons that depict the function. These icons can be used
 *     for the images in a menu control, or for composite-entries in a more
 *     sophisticated user-interface.
 * <li>The enabled/disabled state of the functionality. Instead of having
 *     to separately disable the menu-item and the toolbar-button, the
 *     application can disable the function that implements this interface.
 *     All components which are registered as listeners for the state-change
 *     then know to disable event-generation for that item and to modify the 
 *     display accordingly.
 * </ul>
 * Containers in the Swing set like menus and toolbars know how to add an
 * Action object, as well as other components, using a version of the 
 * <code>add</code> method. When an Action object is added to such a
 * container, the container:
 * <ol type="a">
 * <li>Creates a component that is appropriate for that container 
 *     (a toolbar creates a button component, for example).
 * <li>Gets the appropriate property(s) from the Action object to customize
 *     the component (for example, the icon image and flyover text).
 * <li>Checks the intial state of the Action object to determine if it is
 *     enabled or disabled, and renders the component in the appropriate
 *     fashion.
 * <li>Registers a listener with the Action object so that is notified of
 *     state changes. When the Action object changes from enabled to disabled,
 *     or back, the container makes the appropriate revisions to the
 *     event-generation mechanisms and renders the component accordingly.
 * </ol>
 * For example, both a menu item and a toolbar button could access a
 * <code>Cut</code> action object. The text associated with the object is 
 * specified as "Cut", and an image depicting a pair of scissors is specified 
 * as its icon. The <code>Cut</code> action-object can then be added to a
 * menu and to a toolbar. Each container does the appropriate things with the
 * object, and invokes its <code>actionPerformed</code> method when the
 * component associated with it is activated. The application can then disable 
 * or enable the application object without worrying about what user-interface
 * components are connected to it.
 * <p>
 * This interface can be added to an existing class or used to create an
 * adapter (typically, by subclassing AbstractAction). The Action object
 * can then be added to multiple action-aware containers and connected to
 * Action-capable components. The GUI controls can then be activated or
 * deactivated all at once by invoking the Action object's <code>setEnabled</code>
 * method.
 *
 * Note that Action implementations tend to be more expensive in terms of
 * storage than a typical ActionListener, which does not offer the benefits
 * of centralized control of functionality and broadcast of property changes.
 * For this reason, you should take care to only use Actions where their
 * benefits are desired, and use a simple ActionListener where they
 * are not necessary.
 *
 * @version 1.16 11/29/01
 * @author Georges Saab
 * @see AbstractAction
 */
public interface Action extends ActionListener {
    /**
     * Useful constants that can be used as the storage-retreival key 
     * when setting or getting one of this object's properties (text
     * or icon).
     */
    public static final String DEFAULT = "Default";
    /** 
     * The key used for storing the name for the action,
     * used for a menu or button.
     */
    public static final String NAME = "Name";
    /**
     * The key used for storing a short description for the action,
     * used for tooltip text.
     */
    public static final String SHORT_DESCRIPTION = "ShortDescription";
    /**
     * The key used for storing a longer description for the action,
     * could be used for context-sensitive help.
     */
    public static final String LONG_DESCRIPTION = "LongDescription";
    /**
     * The key used for storing a small icon for the action,
     * used for toolbar buttons.
     */
    public static final String SMALL_ICON = "SmallIcon";

    /**
     * Gets one of this object's properties
     * using the associated key.
     * @see #putValue
     */
    public Object getValue(String key);
    /**
     * Sets one of this object's properties
     * using the associated key. If the value has
     * changed, a PropertyChangeEvent is sent
     * to listeners.
     *
     * @param key    a String containing the key
     * @param value  an Object value
     */
    public void putValue(String key, Object value);

    /**
     * Tests the enabled state of the Action. When enabled,
     * any component associated with this object is active and
     * able to fire this object's <code>actionPerformed</code> method.
     * If the value has changed, a PropertyChangeEvent is sent
     * to listeners.
     *
     * @param  b true to enable this Action, false to disable it
     */
    public void setEnabled(boolean b);
    /**
     * Sets the enabled state of the Action. When enabled,
     * any component associated with this object is active and
     * able to fire this object's <code>actionPerformed</code> method.
     *
     * @return true if this Action is enabled
     */
    public boolean isEnabled();

    /**
     * Add a PropertyChange listener. Containers and attached
     * components use these methods to register interest in this Action
     * object. When its enabled state or other property changes,
     * the registered listeners are informed of the change.
     *
     * @param listener  a PropertyChangeListener object ...
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    /**
     * Remove a PropertyChange listener.
     *
     * @param listener  a PropertyChangeListener object ...
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

}
