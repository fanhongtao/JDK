/*
 * @(#)ButtonGroup.java	1.22 98/08/28
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

import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;
 
/**
 * This class is used to create a multiple-exclusion scope for
 * a set of buttons. Creating a set of buttons with the
 * same ButtonGroup object means that turning "on" one of those buttons 
 * turns off all other buttons in the group. A ButtonGroup can be used with
 * sets of JButton, JRadioButton, or JRadioButtonMenuItem objects.
 * <p>
 * Initially, all buttons in the group are unselected. Once any button is
 * selected, one button is always selected in the group. There is no way
 * to turn a button programmatically to "off", in order to clear the button
 * group. To give the appearance of "none selected", add an invisible radio
 * button to the group and then programmatically select that button to 
 * turn off all the displayed radio buttons. For example, a normal button
 * with the label "none" could be wired to select the invisible radio button.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.22 08/28/98
 * @author Jeff Dinkins
 */
public class ButtonGroup implements Serializable {

    // the list of buttons participating in this group
    protected Vector buttons = new Vector();

    /**
     * The current choice.
     */
    ButtonModel selection = null;

    /**
     * Creates a new ButtonGroup.
     */
    public ButtonGroup() {}

    /**
     * Adds the button to the group.
     */ 
    public void add(AbstractButton b) {
        if(b == null) {
            return;
        }
        buttons.addElement(b);
        if(selection == null && b.isSelected()) {
            selection = b.getModel();
        }
        b.getModel().setGroup(this);
    }
 
    /**
     * Removes the button from the group.
     */ 
    public void remove(AbstractButton b) {
        if(b == null) {
            return;
        }
        buttons.removeElement(b);
        if(b.getModel() == selection) {
            selection = null;
        }
        b.getModel().setGroup(null);
    }

    /**
     * Return all the buttons that are participating in
     * this group.
     */
    public Enumeration getElements() {
        return buttons.elements();
    }

    /**
     * Return the selected button model.
     */
    public ButtonModel getSelection() {
        return selection;
    }

    /**
     * Sets the selected value for the button.
     */
    public void setSelected(ButtonModel m, boolean b) {
        if(b && m != selection) {
            ButtonModel oldSelection = selection;
            selection = m;
            if(oldSelection != null) {
                oldSelection.setSelected(false);
            }
        } 
    }

    /**
     * Returns the selected value for the button.
     */
    public boolean isSelected(ButtonModel m) {
        return (m == selection);
    }

}
