/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
 * <a href="doc-files/Key-Index.html#JButton">JButton</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: An implementation of a \"push\" button.
 *
 * @version 1.85 02/06/02
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
     * Action supplied.
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
     * @param text  the text of the button.
     * @param icon  the Icon image to display on the button
     */
    public JButton(String text, Icon icon) {
        // Create the model
        setModel(new DefaultButtonModel());

        // initialize
        init(text, icon);
    }

    /**
     * Notification from the UIFactory that the L&F
     * has changed. 
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
     * @return "ButtonUI"
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
     * Returns whether or not this button is the default button
     * on the RootPane.
     *
     * @return "boolean"
     * @see JRootPane#setDefaultButton
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
     * Returns whether or not this button is capable of being
     * the default button on the RootPane.
     *
     * @return "boolean"
     * @see #setDefaultCapable
     * @see #isDefaultButton 
     * @see JRootPane#setDefaultButton
     */
    public boolean isDefaultCapable() {
        return defaultCapable;
    }

    /**
     * Sets whether or not this button is capable of being
     * the default button on the RootPane.
     *
     * @return "boolean"
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
     * RootPane, and if so, sets the RootPane's default button to null to
     * ensure the RootPane doesn't hold onto an invalid button reference.
     */
    public void removeNotify() {
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null && root.getDefaultButton() == this) {
            root.setDefaultButton(null);
        }
        super.removeNotify();
    }

    /**
     * Factory method which sets the AbstractButton's properties
     * according to values from the Action instance.  The properties 
     * which get set may differ for AbstractButton subclasses.
     * By default, the properties which get set are Text, Icon
     * Enabled, and ToolTipText.
     *
     * @param a the Action from which to get the properties, or null
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected void configurePropertiesFromAction(Action a) {
	Boolean hide = (Boolean)getClientProperty("hideActionText");
	setText((( a != null && (hide == null || hide!=Boolean.TRUE))
		 ? (String)a.getValue(Action.NAME)
		 : null));
	setIcon((a!=null?(Icon)a.getValue(Action.SMALL_ICON):null));
	setEnabled((a!=null?a.isEnabled():true));
 	setToolTipText((a!=null?(String)a.getValue(Action.SHORT_DESCRIPTION):null));	
    }

    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JButton. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JButton.
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
     * Gets the AccessibleContext associated with this JButton. 
     * For JButtons, the AccessibleContext takes the form of an 
     * AccessibleJButton. 
     * A new AccessibleJButton instance is created if necessary.
     *
     * @return an AccessibleJButton that serves as the 
     *         AccessibleContext of this JButton
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
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
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
