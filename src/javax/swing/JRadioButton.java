/*
 * @(#)JRadioButton.java	1.72 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of a radio button -- an item that can be selected or
 * deselected, and which displays its state to the user.
 * Used with a {@link ButtonGroup} object to create a group of buttons
 * in which only one button at a time can be selected. (Create a ButtonGroup
 * object and use its <code>add</code> method to include the JRadioButton objects
 * in the group.)
 * <blockquote>
 * <strong>Note:</strong>
 * The ButtonGroup object is a logical grouping -- not a physical grouping.
 * Tocreate a button panel, you should still create a {@link JPanel} or similar
 * container-object and add a {@link javax.swing.border.Border} to it to set it off from surrounding
 * components.
 * </blockquote>
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/button.html">How to Use Buttons, Check Boxes, and Radio Buttons</a>
 * in <em>The Java Tutorial</em>
 * for further documentation.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JRadioButton"><code>JRadioButton</code> key assignments</a>.
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
 * description: A component which can display it's state as selected or deselected.
 *
 * @see ButtonGroup
 * @see JCheckBox
 * @version 1.72 01/23/03
 * @author Jeff Dinkins
 */
public class JRadioButton extends JToggleButton implements Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "RadioButtonUI";


    /**
     * Creates an initially unselected radio button
     * with no set text.
     */
    public JRadioButton () {
        this(null, null, false);
    }
     
    /**
     * Creates an initially unselected radio button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
    public JRadioButton(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a radiobutton where properties are taken from the 
     * Action supplied.
     *
     * @since 1.3
     */
    public JRadioButton(Action a) {
        this();
	setAction(a);
    }

    /**
     * Creates a radio button with the specified image
     * and selection state, but no text.
     *   
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JRadioButton(Icon icon, boolean selected) {
        this(null, icon, selected);
    }
    
    /**
     * Creates an unselected radio button with the specified text.
     * 
     * @param text  the string displayed on the radio button
     */
    public JRadioButton (String text) {
        this(text, null, false);
    }

    /**
     * Creates a radio button with the specified text
     * and selection state.
     * 
     * @param text  the string displayed on the radio button
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JRadioButton (String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates a radio button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text  the string displayed on the radio button 
     * @param icon  the image that the button should display
     */
    public JRadioButton(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a radio button that has the specified text, image,
     * and selection state.
     *
     * @param text  the string displayed on the radio button 
     * @param icon  the image that the button should display
     */
    public JRadioButton (String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        setBorderPainted(false);
        setHorizontalAlignment(LEADING);
    }


    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ButtonUI)UIManager.getUI(this));
    }
    

    /**
     * Returns the name of the L&F class
     * that renders this component.
     *
     * @return String "RadioButtonUI"
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
     * Factory method which sets the <code>ActionEvent</code> source's
     * properties according to values from the Action instance. The
     * properties which are set may differ for subclasses.
     * By default, the properties which get set are <code>Text, Mnemonic,
     * Enabled, ActionCommand</code>, and <code>ToolTipText</code>.
     *
     * @param a the Action from which to get the properties, or null
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected void configurePropertiesFromAction(Action a) {
        String[] types = { Action.MNEMONIC_KEY, Action.NAME,
                           Action.SHORT_DESCRIPTION,
                           Action.ACTION_COMMAND_KEY, "enabled" };
        configurePropertiesFromAction(a, types);
    }

    /**
     * Factory method which creates the PropertyChangeListener
     * used to update the ActionEvent source as properties change on
     * its Action instance.  Subclasses may override this in order 
     * to provide their own PropertyChangeListener if the set of
     * properties which should be kept up to date differs from the
     * default properties (Text, Icon, Enabled, ToolTipText).
     *
     * Note that PropertyChangeListeners should avoid holding
     * strong references to the ActionEvent source, as this may hinder
     * garbage collection of the ActionEvent source and all components
     * in its containment hierarchy.  
     *
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        return new AbstractActionPropertyChangeListener(this, a) {
	    public void propertyChange(PropertyChangeEvent e) {	    
		String propertyName = e.getPropertyName();
		AbstractButton button = (AbstractButton)getTarget();
		if (button == null) {   //WeakRef GC'ed in 1.2
		    Action action = (Action)e.getSource();
		    action.removePropertyChangeListener(this);
		} else {
		    if (propertyName.equals(Action.NAME)) {
			String text = (String) e.getNewValue();
			button.setText(text);
			button.repaint();
		    } else if (propertyName.equals(Action.SHORT_DESCRIPTION)) {
			String text = (String) e.getNewValue();
			button.setToolTipText(text);
		    } else if (propertyName.equals("enabled")) {
			Boolean enabledState = (Boolean) e.getNewValue();
			button.setEnabled(enabledState.booleanValue());
			button.repaint();
                    } else if (propertyName.equals(Action.ACTION_COMMAND_KEY)) {
                        button.setActionCommand((String)e.getNewValue());
		    } 
		}
	    }
	};
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
     * Returns a string representation of this JRadioButton. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JRadioButton.
     */
    protected String paramString() {
	return super.paramString();
    }


/////////////////
// Accessibility support
////////////////


    /**
     * Gets the AccessibleContext associated with this JRadioButton. 
     * For JRadioButtons, the AccessibleContext takes the form of an 
     * AccessibleJRadioButton. 
     * A new AccessibleJRadioButton instance is created if necessary.
     *
     * @return an AccessibleJRadioButton that serves as the 
     *         AccessibleContext of this JRadioButton
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this Button
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJRadioButton();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JRadioButton</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to radio button 
     * user-interface elements.
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
    protected class AccessibleJRadioButton extends AccessibleJToggleButton {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.RADIO_BUTTON;
        }

    } // inner class AccessibleJRadioButton
}
  
