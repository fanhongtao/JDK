/*
 * @(#)BRView.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import javax.swing.text.*;

/**
 * Processes the &lt;BR&gt; tag.  In other words, forces a line break.
 *
 * @author Sunita Mani
 * @version 1.9 01/23/03
 */
class BRView extends InlineView {

    /**
     * Creates a new view that represents a &lt;BR&gt; element.
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

