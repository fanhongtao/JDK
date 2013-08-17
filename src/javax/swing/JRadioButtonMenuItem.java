/*
 * @(#)JRadioButtonMenuItem.java	1.31 98/08/28
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

import java.util.EventListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.plaf.*;
import javax.accessibility.*;

/**
 * An implementation of a RadioButtonMenuItem. A RadioButtonMenuItem is
 * a menu item that is part of a group of menu items in which only one
 * item in the group can be selected. The selected item displays its
 * selected state. Selecting it causes any other selected item to
 * switch to the unselected state.
 * <p>
 * Used with a {@link ButtonGroup} object to create a group of menu items
 * in which only one item at a time can be selected. (Create a ButtonGroup
 * object and use its <code>add</code> method to include the JRadioButtonMenuItem
 * objects in the group.)
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JRadioButtonMenuItem">JRadioButtonMenuItem</a> key assignments.
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
 * @version 1.31 08/28/98
 * @author Georges Saab
 * @author David Karlton
 * @see ButtonGroup
 */
public class JRadioButtonMenuItem extends JMenuItem implements Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "RadioButtonMenuItemUI";

    /**
     * Creates a JRadioButtonMenuItem with no set text or icon.
     */
    public JRadioButtonMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates a JRadioButtonMenuItem with an icon.
     *
     * @param icon the Icon to display on the RadioButtonMenuItem.
     */
    public JRadioButtonMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a JRadioButtonMenuItem with text.
     *
     * @param text the text of the RadioButtonMenuItem.
     */
    public JRadioButtonMenuItem(String text) {
        this(text, null, false);
    }
    
    /**
     * Creates a JRadioButtonMenuItem with the specified text
     * and Icon.
     *
     * @param text the text of the RadioButtonMenuItem
     * @param icon the icon to display on the RadioButtonMenuItem
     */
    public JRadioButtonMenuItem(String text, Icon icon) {
	this(text, icon, false);
    }

    /**
     * Creates a radiobutton menu item with the specified text 
     * and selection state.
     *
     * @param text the text of the CheckBoxMenuItem.
     * @param b the selected state of the checkboxmenuitem
     */
    public JRadioButtonMenuItem(String text, boolean b) {
        this(text);
        setSelected(b);
    }

    /**
     * Creates a radio button menu item with the specified image
     * and selection state, but no text.
     *   
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JRadioButtonMenuItem(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates a radio button menu item that has the specified 
     * text, image, and selection state.
     *
     * @param text  the string displayed on the radio button 
     * @param icon  the image that the button should display
     */
    public JRadioButtonMenuItem(String text, Icon icon, boolean selected) {
        setModel(new JToggleButton.ToggleButtonModel());
        init(text, icon);
        setBorderPainted(false);
        setFocusPainted(false);
        setHorizontalTextPosition(JButton.RIGHT);
        setHorizontalAlignment(JButton.LEFT);
        setSelected(selected);
        updateUI();
    }
    /**
     * Initialize the JRadioButtonMenuItem with the specified text
     * and Icon.
     *
     * @param text the text to display
     * @param icon the icon to display
     */
    protected void init(String text, Icon icon) {
        if(text != null) {
            setText(text);
            
            if(icon != null) {
                setVerticalTextPosition(BOTTOM);
            } 
        }
        
        if(icon != null) {
            setIcon(icon);
        }
        
        // Listen for Focus events
        addFocusListener(
            new FocusListener() {
            public void focusGained(FocusEvent event) {}
            public void focusLost(FocusEvent event) {
                // When focus is lost, repaint if 
                // we focus information is painted
                if(isFocusPainted()) {
                    repaint();
                }
            }
        }
        );
    }
    
    
    /**
     * Notification from the UIFactory that the L&F has changed. 
     * Called to replace the UI with the latest version from the 
     * UIFactory.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((MenuItemUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "RadioButtonMenuItemUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Override Component.requestFocus() to not grab focus.
     */
    public void requestFocus() {}


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
     * Returns a string representation of this JRadioButtonMenuItem.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JRadioButtonMenuItem.
     */
    protected String paramString() {
	return super.paramString();
    }

/////////////////                                                 
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJRadioButtonMenuItem();
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
    protected class AccessibleJRadioButtonMenuItem extends AccessibleJMenuItem {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.RADIO_BUTTON;
        }
    } // inner class AccessibleJRadioButtonMenuItem
}

