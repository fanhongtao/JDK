/*
 * @(#)ButtonGroup.java	1.35 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;
 
/**
 * This class is used to create a multiple-exclusion scope for
 * a set of buttons. Creating a set of buttons with the
 * same <code>ButtonGroup</code> object means that
 * turning "on" one of those buttons 
 * turns off all other buttons in the group.
 * <p>
 * A <code>ButtonGroup</code> can be used with
 * any set of objects that inherit from <code>AbstractButton</code>.
 * Typically a button group contains instances of 
 * <code>JRadioButton</code>,
 * <code>JRadioButtonMenuItem</code>,
 * or <code>JToggleButton</code>.
 * It wouldn't make sense to put an instance of 
 * <code>JButton</code> or <code>JMenuItem</code>
 * in a button group
 * because <code>JButton</code> and <code>JMenuItem</code>
 * don't implement the selected state.
 * <p>
 * Initially, all buttons in the group are unselected. Once any button is
 * selected, one button is always selected in the group. There is no way
 * to turn a button programmatically to "off", in order to clear the button
 * group. To give the appearance of "none selected", add an invisible radio
 * button to the group and then programmatically select that button to 
 * turn off all the displayed radio buttons. For example, a normal button
 * with the label "none" could be wired to select the invisible radio button.
 * <p>
 * For examples and further information on using button groups see
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/button.html#radiobutton">How to Use Radio Buttons</a>,
 * a section in <em>The Java Tutorial</em>.
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
 * @version 1.35 01/23/03
 * @author Jeff Dinkins
 */
public class ButtonGroup implements Serializable {

    // the list of buttons participating in this group
    protected Vector buttons = new Vector();

    /**
     * The current selection.
     */
    ButtonModel selection = null;

    /**
     * Creates a new <code>ButtonGroup</code>.
     */
    public ButtonGroup() {}

    /**
     * Adds the button to the group.
     * @param b the button to be added
     */ 
    public void add(AbstractButton b) {
        if(b == null) {
            return;
        }
        buttons.addElement(b);

        if (b.isSelected()) {
            if (selection == null) {
                selection = b.getModel();
            } else {
                b.setSelected(false);
            }
        }

        b.getModel().setGroup(this);
    }
 
    /**
     * Removes the button from the group.
     * @param b the button to be removed
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
     * Returns all the buttons that are participating in
     * this group.
     * @return an <code>Enumeration</code> of the buttons in this group
     */
    public Enumeration getElements() {
        return buttons.elements();
    }

    /**
     * Returns the model of the selected button.
     * @return the selected button model
     */
    public ButtonModel getSelection() {
        return selection;
    }

    /**
     * Sets the selected value for the <code>ButtonModel</code>.
     * Only one button in the group may be selected at a time.
     * @param m the <code>ButtonModel</code>
     * @param b <code>true</code> if this button is to be
     *   selected, otherwise <code>false</code>
     */
    public void setSelected(ButtonModel m, boolean b) {
        if (b && m != null && m != selection) {
            ButtonModel oldSelection = selection;
            selection = m;
            if (oldSelection != null) {
                oldSelection.setSelected(false);
            }
            m.setSelected(true);
        } 
    }

    /**
     * Returns whether a <code>ButtonModel</code> is selected.
     * @return <code>true</code> if the button is selected,
     *   otherwise returns <code>false</code>
     */
    public boolean isSelected(ButtonModel m) {
        return (m == selection);
    }

    /**
     * Returns the number of buttons in the group.
     * @return the button count
     * @since 1.3
     */
    public int getButtonCount() {
	if (buttons == null) {
	    return 0;
	} else {
	    return buttons.size();
	}
    }

}
