/*
 * @(#)BRView.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import javax.swing.text.*;

/**
 * Processes the <BR> tag i.e forces a line break.
 *
 * @author Sunita Mani
 * @version 1.5 11/29/01
 */
class BRView extends InlineView {

    /**
     * Creates a new view that represents an <br> element.
     *
     * @param elem the element to create a view for
     */
    public BRView(Element elem) {
	super(elem);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
    }

    /**
     * Forces a line break.
     *
     * @return View.ForcedBreakWeight
     */
    public int getBreakWeight(int axis, float pos, float len) {
	if (axis == X_AXIS) {
	    return ForcedBreakWeight;
	} else {
	    return super.getBreakWeight(axis, pos, len);
	}
    }
    AttributeSet attr;
}

