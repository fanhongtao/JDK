/*
 * @(#)JButton.java	1.93 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.util.EventListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.plaf.*;
import javax.swing.event.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of a "push" button.
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/button.html">How to Use Buttons, Check Boxes, and Radio Buttons</a>
 * in <em>The Java Tutorial</em>
 * for information and examples of using buttons.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JButton"><code>JButton</code> key assignments</a>.
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
 * @beaninfo
 *   attribute: isContainer false
 * description: An implementation of a \"push\" button.
 *
 * @version 1.93 01/23/03
 * @author Jeff Dinkins
 */
public class JButton extends AbstractButton implements Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ButtonUI";

    private boolean defaultCapable = true;

    /**
     * Creates a button with no set text or icon.
     */
    public JButton() {
        this(null, null);
    }
    
    /**
     * Creates a button with an icon.
     *
     * @param icon  the Icon image to display on the button
     */
    public JButton(Icon icon) {
        this(null, icon);
    }
    
    /**
     * Creates a button with text.
     *
     * @param text  the text of the button
     */
    public JButton(String text) {
        this(text, null);
    }
    
    /**
     * Creates a button where properties are taken from the 
     * <code>Action</code> supplied.
     *
     * @param a the <code>Action</code> used to specify the new button
     *
     * @since 1.3
     */
    public JButton(Action a) {
        this();
	setAction(a);
    }

    /**
     * Creates a button with initial text and an icon.
     *
     * @param text  the text of the button
     * @param icon  the Icon image to display on the button
     */
    public JButton(String text, Icon icon) {
        // Create the model
        setModel(new DefaultButtonModel());

        // initialize
        init(text, icon);
    }

    /**
     * Resets the UI property to a value from the current look and
     * feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ButtonUI)UIManager.getUI(this));
    }
    

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return the string "ButtonUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Gets the value of the <code>defaultButton</code> property,
     * which if <code>true</code> means that this button is the current
     * default button for its <code>JRootPane</code>.
     * Most look and feels render the default button
     * differently, and may potentially provide bindings
     * to access the default button.
     *
     * @return the value of the <code>defaultButton</code> property
     * @see JRootPane#setDefaultButton
     * @see #isDefaultCapable
     * @beaninfo 
     *  description: Whether or not this button is the default button
     */
    public boolean isDefaultButton() {
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            return root.getDefaultButton() == this;
        }
        return false;
    }

    /**
     * Gets the value of the <code>defaultCapable</code> property.
     *
     * @return the value of the <code>defaultCapable</code> property
     * @see #setDefaultCapable
     * @see #isDefaultButton 
     * @see JRootPane#setDefaultButton
     */
    public boolean isDefaultCapable() {
        return defaultCapable;
    }

    /**
     * Sets the <code>defaultCapable</code> property,
     * which determines whether this button can be
     * made the default button for its root pane.
     * The default value of the <code>defaultCapable</code>
     * property is <code>true</code> unless otherwise
     * specified by the look and feel.
     *
     * @param defaultCapable <code>true</code> if this button will be
     *        capable of being the default button on the
     *        <code>RootPane</code>; otherwise <code>false</code>
     * @see #isDefaultCapable
     * @beaninfo 
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether or not this button can be the default button
     */
    public void setDefaultCapable(boolean defaultCapable) {
        boolean oldDefaultCapable = this.defaultCapable;
        this.defaultCapable = defaultCapable;
        firePropertyChange("defaultCapable", oldDefaultCapable, defaultCapable);
    }

    /**
     * Overrides <code>JComponent.removeNotify</code> to check if
     * this button is currently set as the default button on the
     * <code>RootPane</code>, and if so, sets the <code>RootPane</code>'s
     * default button to <code>null</code> to ensure the
     * <code>RootPane</code> doesn't hold onto an invalid button reference.
     */
    public void removeNotify() {
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null && root.getDefaultButton() == this) {
            root.setDefaultButton(null);
        }
        super.removeNotify();
    }

    /**
     * Factory method which sets the <code>AbstractButton</code>'s properties
     * according to values from the <code>Action</code> instance. 
     * The properties which get set may differ for <code>AbstractButton</code>
     * subclasses.  By default, the properties which get set are
     * <code>Text, Icon, Enabled, ToolTipText, ActionCommand</code>, and
     * <code>Mnemonic</code>.
     *
     * @param a the <code>Action</code> from which to get the
     *    properties, or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected void configurePropertiesFromAction(Action a) {
        // properties that we want to configure from Action. We handle
        // Action.NAME differently from AbstractButton, so we don't call
        // super.configurePropertiesFromAction(a) here.
        String[] types = { Action.MNEMONIC_KEY, Action.SHORT_DESCRIPTION,
                           Action.SMALL_ICON, Action.ACTION_COMMAND_KEY,
                           "enabled" };
        configurePropertiesFromAction(a, types);

	Boolean hide = (Boolean)getClientProperty("hideActionText");
	setText((( a != null && (hide == null || hide!=Boolean.TRUE))
		 ? (String)a.getValue(Action.NAME)
		 : null));
    }

    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }


    /**
     * Returns a string representation of this <code>JButton</code>.
     * This method is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JButton</code>
     */
    protected String paramString() {
	String defaultCapableString = (defaultCapable ? "true" : "false");
	
	return super.paramString() +
	    ",defaultCapable=" + defaultCapableString;
    }


/////////////////
// Accessibility support
////////////////

    /**
     * Gets the <code>AccessibleContext</code> associated with this
     * <code>JButton</code>. For <code>JButton</code>s,
     * the <code>AccessibleContext</code> takes the form of an 
     * <code>AccessibleJButton</code>. 
     * A new <code>AccessibleJButton</code> instance is created if necessary.
     *
     * @return an <code>AccessibleJButton</code> that serves as the 
     *         <code>AccessibleContext</code> of this <code>JButton</code>
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this Button.
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJButton();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JButton</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to button user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    protected class AccessibleJButton extends AccessibleAbstractButton {
    
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PUSH_BUTTON;
        }
    } // inner class AccessibleJButton
}
