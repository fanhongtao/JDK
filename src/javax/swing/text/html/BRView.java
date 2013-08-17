/*
 * @(#)BRView.java	1.4 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.text.html;

import javax.swing.text.*;

/**
 * Processes the <BR> tag i.e forces a line break.
 *
 * @author Sunita Mani
 * @version 1.4 08/26/98
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

