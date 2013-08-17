/*
 * @(#)JCheckBox.java	1.46 98/09/01
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

import java.awt.*;
import java.awt.event.*;

import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of a CheckBox -- an item that can be selected or
 * deselected, and which displays its state to the user. In a group
 * of checkboxes, multiple checkboxes can be selected. 
 *
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/checkbox.html">How to Use CheckBoxes</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JCheckBox">JCheckBox</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JRadioButton
 *
 * @beaninfo
 *   attribute: isContainer false
 *
 * @version 1.46 09/01/98
 * @author Jeff Dinkins
 */
public class JCheckBox extends JToggleButton implements Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CheckBoxUI";


    /**
     * Creates an initially unselected checkbox button with no text, no icon.
     */
    public JCheckBox () {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected checkbox with an icon.
     *
     * @param icon  the Icon image to display
     */
    public JCheckBox(Icon icon) {
        this(null, icon, false);
    }
    
    /**
     * Creates a checkbox with an icon and specifies whether
     * or not it is initially selected.
     *
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the checkbox is selected
     */
    public JCheckBox(Icon icon, boolean selected) {
        this(null, icon, selected);
    }
    
    /**
     * Creates an initially unselected checkbox with text.
     *
     * @param text the text of the checkbox.
     */
    public JCheckBox (String text) {
        this(text, null, false);
    }

    /**
     * Creates a checkbox with text and specifies whether 
     * or not it is initially selected.
     *
     * @param text the text of the checkbox.
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the checkbox is selected
     */
    public JCheckBox (String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates an initially unselected checkbox with 
     * the specified text and icon.
     *
     * @param text the text of the checkbox.
     * @param icon  the Icon image to display
     */
    public JCheckBox(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a checkbox with text and icon,
     * and specifies whether or not it is initially selected.
     *
     * @param text the text of the checkbox.
     * @param icon  the Icon image to display
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the checkbox is selected
     */
    public JCheckBox (String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        setBorderPainted(false);
        setHorizontalAlignment(LEFT);
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
     * @return "CheckBoxUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * See JComponent.readObject() for information about serialization
     * in Swing.
     */
    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException 
    {
        s.defaultReadObject();
	if (getUIClassID().equals(uiClassID)) {
	    updateUI();
	}
    }


    /**
     * Returns a string representation of this JCheckBox. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JCheckBox.
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
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this CheckBox.
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJCheckBox();
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
    protected class AccessibleJCheckBox extends AccessibleJToggleButton {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }

    } // inner class AccessibleJCheckBox
}
  
