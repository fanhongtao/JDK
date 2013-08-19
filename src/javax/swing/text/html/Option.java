/*
 * @(#)Option.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Value for the ListModel used to represent
 * &lt;option&gt; elements.  This is the object
 * installed as items of the DefaultComboBoxModel
 * used to represent the &lt;select&gt; element.
 *
 * @author  Timothy Prinzing
 * @version 1.9 01/23/03
 */
public class Option  {

    /**
     * Creates a new Option object.
     *
     * @param attr the attributes associated with the 
     *  option element.  The attributes are copied to
     *  ensure they won't change.
     */
    public Option(AttributeSet attr) {
	this.attr = attr.copyAttributes();
	selected = (attr.getAttribute(HTML.Attribute.SELECTED) != null);
    }

    /**
     * Sets the label to be used for the option.
     */
    public void setLabel(String label) {
	this.label = label;
    }

    /**
     * Fetch the label associated with the option.
     */
    public String getLabel() {
	return label;
    }

    /**
     * Fetch the attributes associated with this option.
     */
    public AttributeSet getAttributes() {
	return attr;
    }

    /**
     * String representation is the label.
     */
    public String toString() {
	return label;
    }

    /**
     * Sets the selected state.  
     */
    protected void setSelection(boolean state) {
	selected = state;
    }

    /**
     * Fetches the selection state associated with this option.
     */
    public boolean isSelected() {
	return selected;
    }

    /**
     * Convenience method to return the string associated
     * with the <code>value</code> attribute.  If the 
     * value has not been specified, the label will be
     * returned.
     */
    public String getValue() {
	String value = (String) attr.getAttribute(HTML.Attribute.VALUE);
	if (value == null) {
	    value = label;
	}
	return value;
    }

    private boolean selected;
    private String label;
    private AttributeSet attr;
}

