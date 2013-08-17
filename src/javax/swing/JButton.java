/*
 * @(#)JButton.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * <p>
 * To create a set of mutually exclusive buttons, create a {@link ButtonGroup} object and
 * use its <code>add</code> method to include the JButton objects in the group.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/button.html">How to Use Buttons</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
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
 *
 * @version 1.73 09/01/98
 * @author Jeff Dinkins
 * @see ButtonGroup
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
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
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
     * The class used to obtain the accessible role for this object.
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
