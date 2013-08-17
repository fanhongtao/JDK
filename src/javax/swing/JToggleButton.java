/*
 * @(#)JToggleButton.java	1.37 98/09/04
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

import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * An implementation of a two-state button.  
 * The <code>JRadioButton</code> and <code>JCheckBox</code> classes
 * are subclasses of this class.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JToggleButton">JToggleButton</a> key assignments.
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
 * @see JRadioButton
 * @see JCheckBox
 * @version 1.37 09/04/98
 * @author Jeff Dinkins
 */
public class JToggleButton extends AbstractButton implements Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToggleButtonUI";

    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
    public JToggleButton () {
        this(null, null, false);
    }

    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
    public JToggleButton(Icon icon) {
        this(null, icon, false);
    }
    
    /**
     * Creates a toggle button with the specified image 
     * and selection state, but no text.
     *
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JToggleButton(Icon icon, boolean selected) {
        this(null, icon, selected);
    }
    
    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
    public JToggleButton (String text) {
        this(text, null, false);
    }

    /**
     * Creates a toggle button with the specified text
     * and selection state.
     *
     * @param text  the string displayed on the toggle button
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JToggleButton (String text, boolean selected) {
        this(text, null, selected);
    }

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
    public JToggleButton(String text, Icon icon) {
        this(text, icon, false);
    }

    /**
     * Creates a toggle button with the specified text, image, and
     * selection state.
     *
     * @param text the text of the toggle button.
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JToggleButton (String text, Icon icon, boolean selected) {
        // Create the model
        setModel(new ToggleButtonModel());

        model.setSelected(selected);

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
     * Returns a string that specifies the name of the l&f class
     * that renders this component.
     *
     * @return String "ToggleButtonUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *  description: A string that specifies the name of the L&F class
     */
    public String getUIClassID() {
        return uiClassID;
    }


    // *********************************************************************

    /**
     * The ToggleButton model
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class ToggleButtonModel extends DefaultButtonModel {

        /**
         * Creates a new ToggleButton Model
         */
        public ToggleButtonModel () {
        }

        /**
         * Checks if the button is selected.
         */
        public boolean isSelected() {
            if(group != null) {
                return group.isSelected(this);
            } else {
                return (stateMask & SELECTED) != 0;
            }
        }


        /**
         * Sets the selected state of the button.
         * @param b true selects the toggle button,
         *          false deselects the toggle button.
         */
        public void setSelected(boolean b) {
            // if (this.isSelected() == b) {
            // return;
            // }
                
            if(group != null) {
                // use the group model instead
                group.setSelected(this, b);
            } else {
                if (b) {
                    stateMask |= SELECTED;
                } else {
                    stateMask &= ~SELECTED;
                }
            }

            // Send ChangeEvent
            fireStateChanged();

            // Send ItemEvent
            fireItemStateChanged(
                    new ItemEvent(this,
                                  ItemEvent.ITEM_STATE_CHANGED,
                                  this,
                                  this.isSelected() ?  ItemEvent.SELECTED : ItemEvent.DESELECTED));
        
        }

        /**
         * Sets the pressed state of the toggle button.
         */ 
        public void setPressed(boolean b) {
            if ((isPressed() == b) || !isEnabled()) {
                return;
            }

            if (b == false && isArmed()) {
                setSelected(!this.isSelected());
            } 

            if (b) {
                stateMask |= PRESSED;
            } else {
                stateMask &= ~PRESSED;
            }

            fireStateChanged();

            if(!isPressed() && isArmed()) {
                fireActionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                    getActionCommand())
                    );
            }

        }
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
     * Returns a string representation of this JToggleButton. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JToggleButton.
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
     *  description: The AccessibleContext associated with this ToggleButton.
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJToggleButton();
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
    protected class AccessibleJToggleButton extends AccessibleAbstractButton 
	    implements ItemListener {

	public AccessibleJToggleButton() {
	    super();
	    JToggleButton.this.addItemListener(this);
	}

	/**
	 * Fire accessible property change events when the state of the
	 * toggle button changes.
	 */
        public void itemStateChanged(ItemEvent e) {
            JToggleButton tb = (JToggleButton) e.getSource();
            if (JToggleButton.this.accessibleContext != null) {
                if (tb.isSelected()) {
                    JToggleButton.this.accessibleContext.firePropertyChange(
                            AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            null, AccessibleState.CHECKED);
                } else {
                    JToggleButton.this.accessibleContext.firePropertyChange(
                            AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                            AccessibleState.CHECKED, null);
                }
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOGGLE_BUTTON;
        }
    } // inner class AccessibleJToggleButton
}
  
