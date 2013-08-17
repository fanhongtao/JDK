/*
 * @(#)BlockView.java	1.14 98/08/26
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
package javax.swing.text.html;

import java.util.Enumeration;
import java.awt.*;
import javax.swing.SizeRequirements;
import javax.swing.border.*;
import javax.swing.text.*;

/**
 * A view implementation to display a block (as a box)
 * with CSS specifications.
 *
 * @author  Timothy Prinzing
 * @version 1.14 08/26/98
 */
public class BlockView extends BoxView  {

    /**
     * Creates a new view that represents an
     * html box.  This can be used for a number
     * of elements.
     *
     * @param elem the element to create a view for
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public BlockView(Element elem, int axis) {
	super(elem, axis);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
	painter = sheet.getBoxPainter(attr);
	setPropertiesFromAttributes();
    }

    /**
     * Renders using the given rendering surface and area on that
     * surface.  This is implemented to delegate to the css box
     * painter to paint the border and background prior to the
     * interior.
     *
     * @param g the rendering surface to use
     * @param allocation the allocated region to render into
     * @see View#paint
     */
    public void paint(Graphics g, Shape allocation) {
	Rectangle a = (Rectangle) allocation;
	painter.paint(g, a.x, a.y, a.width, a.height, this);
	super.paint(g, a);
    }

    /**
     * Fetches the attributes to use when rendering.  This is
     * implemented to multiplex the attributes specified in the
     * model with a StyleSheet.
     */
    public AttributeSet getAttributes() {
	return attr;
    }

    /**
     * Gets the resize weight.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @return the weight
     * @exception IllegalArgumentException for an invalid axis
     */
    public int getResizeWeight(int axis) {
	switch (axis) {
	case View.X_AXIS:
	    return 1;
	case View.Y_AXIS:
	    return 0;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Gets the alignment.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @return the alignment
     */
    public float getAlignment(int axis) {
	switch (axis) {
	case View.X_AXIS:
	    return 0;
	case View.Y_AXIS:
	    float span = getPreferredSpan(View.Y_AXIS);
	    View v = getView(0);
	    float above = v.getPreferredSpan(View.Y_AXIS);
	    float a = (((int)span) != 0) ? (above * v.getAlignment(View.Y_AXIS)) / span: 0;
	    return a;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Update any cached values that come from attributes.
     */
    protected void setPropertiesFromAttributes() {
	if (attr != null) {
	    setInsets((short) painter.getInset(TOP, this),
		      (short) painter.getInset(LEFT, this),
		      (short) painter.getInset(BOTTOM, this),
		      (short) painter.getInset(RIGHT, this));
	}
    }

    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }

    private AttributeSet attr;
    private StyleSheet.BoxPainter painter;
}
