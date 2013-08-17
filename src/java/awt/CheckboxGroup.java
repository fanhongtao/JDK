/*
 * @(#)CheckboxGroup.java	1.21 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.awt;

/**
 * The <code>CheckboxGroup</code> class is used to group together 
 * a set of <code>Checkbox</code> buttons. 
 * <p>
 * Exactly one check box button in a <code>CheckboxGroup</code> can 
 * be in the "on" state at any given time. Pushing any 
 * button sets its state to "on" and forces any other button that 
 * is in the "on" state into the "off" state. 
 * <p>
 * The following code example produces a new check box group,
 * with three check boxes: 
 * <p>
 * <hr><blockquote><pre>
 * setLayout(new GridLayout(3, 1));
 * CheckboxGroup cbg = new CheckboxGroup();
 * add(new Checkbox("one", cbg, true));
 * add(new Checkbox("two", cbg, false));
 * add(new Checkbox("three", cbg, false));
 * </pre></blockquote><hr>
 * <p>
 * This image depicts the check box group created by this example:
 * <p>
 * <img src="images-awt/CheckboxGroup-1.gif"
 * ALIGN=center HSPACE=10 VSPACE=7> 
 * <p>
 * @version 	1.21 07/01/98
 * @author 	Sami Shaio
 * @see         java.awt.Checkbox
 * @since       JDK1.0
 */
public class CheckboxGroup implements java.io.Serializable {
    /**
     * The current choice.
     */
    Checkbox selectedCheckbox = null;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 3729780091441768983L;

    /**
     * Creates a new instance of <code>CheckboxGroup</code>. 
     * @since     JDK1.0
     */
    public CheckboxGroup() {
    }

    /**
     * Gets the current choice from this check box group.
     * The current choice is the check box in this  
     * group that is currently in the "on" state, 
     * or <code>null</code> if all check boxes in the
     * group are off.
     * @return   the check box that is currently in the
     *                 "on" state, or <code>null</code>.
     * @see      java.awt.Checkbox
     * @see      java.awt.CheckboxGroup#setSelectedCheckbox
     * @since    JDK1.1
     */
    public Checkbox getSelectedCheckbox() {
	return getCurrent();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getSelectedCheckbox()</code>.
     */
    public Checkbox getCurrent() {
	return selectedCheckbox;
    }

    /**
     * Sets the currently selected check box in this group
     * to be the specified check box.
     * This method sets the state of that check box to "on" and 
     * sets all other check boxes in the group to be off.
     * <p>
     * If the check box argument is <code>null</code> or belongs to a 
     * different check box group, then this method does nothing. 
     * @param     box   the <code>Checkbox</code> to set as the
     *                      current selection.
     * @see      java.awt.Checkbox
     * @see      java.awt.CheckboxGroup#getSelectedCheckbox
     * @since    JDK1.1
     */
    public void setSelectedCheckbox(Checkbox box) {
    	setCurrent(box);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setSelectedCheckbox(Checkbox)</code>.
     */
    public synchronized void setCurrent(Checkbox box) {
	if (box != null && box.group != this) {
	    return;
	}
	Checkbox oldChoice = this.selectedCheckbox;
	this.selectedCheckbox = box;
	if ((oldChoice != null) && (oldChoice != box)) {
	    oldChoice.setState(false);
	}
	if (box != null && oldChoice != box && !box.getState()) {
	    box.setStateInternal(true);
	}
    }

    /**
     * Returns a string representation of this check box group,
     * including the value of its current selection.
     * @return    a string representation of this check box group.
     * @since     JDK1.0
     */
    public String toString() {
	return getClass().getName() + "[selectedCheckbox=" + selectedCheckbox + "]";
    }

}
