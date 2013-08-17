/*
 * @(#)CheckboxGroup.java	1.17 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

/**
 * This class is used to create a multiple-exclusion scope for a set
 * of Checkbox buttons. For example, creating a set of Checkbox buttons
 * with the same CheckboxGroup object means that only one of those Checkbox
 * buttons will be allowed to be "on" at a time.
 *
 * @version 	1.17 01/27/97
 * @author 	Sami Shaio
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
     * Creates a new CheckboxGroup.
     */
    public CheckboxGroup() {
    }

    /**
     * Gets the current choice.
     */
    public Checkbox getSelectedCheckbox() {
	return getCurrent();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getSelectedCheckbox().
     */
    public Checkbox getCurrent() {
	return selectedCheckbox;
    }

    /**
     * Sets the current choice to the specified Checkbox.
     * If the Checkbox belongs to a different group, just return.
     * @param box the current Checkbox choice
     */
    public synchronized void setSelectedCheckbox(Checkbox box) {
    	setCurrent(box);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by setSelectedCheckbox(Checkbox).
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
     * Returns the String representation of this CheckboxGroup's values.
     * Convert to String.
     */
    public String toString() {
	return getClass().getName() + "[selectedCheckbox=" + selectedCheckbox + "]";
    }

}
