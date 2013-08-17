 /*
 * @(#)JCheckBoxMenuItem.java	1.38 98/08/28
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
 * A menu item that can be selected or deselected. If selected, the menu
 * item typically appears with a checkmark next to it. If unselected or
 * deselected, the menu item appears without a checkmark. Like a regular
 * menu item, a checkbox menu item can have either text or a graphic
 * icon associated with it, or both.
 * <p>
 * Either <code>isSelected</code>/<code>setSelected</code> or 
 * <code>getState</code>/<code>setState</code> can be used
 * to determine/specify the menu item's selection state. (The
 * Swing-standard methods are <code>isSelected</code> and
 * <code>setSelected</code>. These methods work for all menus and buttons.
 * The <code>getState</code> and <code>setState</code> methods exist for
 * compatibility with other component sets.)
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JCheckBoxMenuItem">JCheckBoxMenuItem</a> key assignments.
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
 * @version 1.38 08/28/98
 * @author Georges Saab
 * @author David Karlton
 */
public class JCheckBoxMenuItem extends JMenuItem implements SwingConstants,
        Accessible 
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CheckBoxMenuItemUI";

    /**
     * Creates an initially unselected checkboxMenuItem with no set text or icon.
     */
    public JCheckBoxMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected checkboxMenuItem with an icon.
     *
     * @param icon the icon of the CheckBoxMenuItem.
     */
    public JCheckBoxMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates an initially unselected checkboxMenuItem with text.
     *
     * @param text the text of the CheckBoxMenuItem
     */
    public JCheckBoxMenuItem(String text) {
        this(text, null, false);
    }
    
    /**
     * Creates an initially unselected checkboxMenuItem with the specified text and icon.
     *
     * @param text the text of the CheckBoxMenuItem
     * @param icon the icon of the CheckBoxMenuItem
     */
    public JCheckBoxMenuItem(String text, Icon icon) {
	this(text, icon, false);
    }

    /**
     * Creates a checkboxMenuItem with the specified text and selection state.
     *
     * @param text the text of the CheckBoxMenuItem.
     * @param b the selected state of the checkboxmenuitem
     */
    public JCheckBoxMenuItem(String text, boolean b) {
        this(text, null, b);
    }

    /**
     * Creates a checkboxMenuItem with the specified text, icon, and selection state.
     *
     * @param text the text of the CheckBoxMenuItem
     * @param icon the icon of the CheckBoxMenuItem
     * @param b the selected state of the checkboxmenuitem
     */
    public JCheckBoxMenuItem(String text, Icon icon, boolean b) {
        setModel(new JToggleButton.ToggleButtonModel());
        init(text, icon);
        setBorderPainted(false);
        setFocusPainted(false);
        setHorizontalTextPosition(RIGHT);
        setHorizontalAlignment(LEFT);
        setSelected(b);
        updateUI();
    }

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
     * Notification from the UIFactory that the L&F
     * has changed. 
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((MenuItemUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class
     * that renders this component.
     *
     * @return "CheckBoxMenuItemUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }
            
     /**
      * Returns the selected-state of the item. This method
      * exists for AWT compatibility only.  New code should
      * use isSelected() instead.
      *
      * @return true  if the item is selected
      */
    public boolean getState() {
        return isSelected();
    }
            
    /**
     * Sets the selected-state of the item. This method
     * exists for AWT compatibility only.  New code should
     * use setSelected() instead.
     *
     * @param b  a boolean value indicating the item's
     *           selected-state, where true=selected
     * @beaninfo
     * description: The selection state of the Checkbox menu item
     *      hidden: true
     */
    public synchronized void setState(boolean b) {
        setSelected(b);
    }
            
            
    /**
     * Returns an array (length 1) containing the checkbox menu item 
     * label or null if the checkbox is not selected.
     *
     * @return an array containing 1 Object -- the text of the menu item
     *         -- if the item is selected, otherwise null 
     */
    public synchronized Object[] getSelectedObjects() {
        if (isSelected() == false)
            return null;
        Object[] selectedObjects = new Object[1];
        selectedObjects[0] = getText();
        return selectedObjects;
    }

    /**
     * Override <code>JComponent.requestFocus()</code> to prevent grabbing the focus.
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
     * Returns a string representation of this JCheckBoxMenuItem. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JCheckBoxMenuItem.
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
            accessibleContext = new AccessibleJCheckBoxMenuItem();
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
    protected class AccessibleJCheckBoxMenuItem extends AccessibleJMenuItem {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }
    } // inner class AccessibleJCheckBoxMenuItem
}
