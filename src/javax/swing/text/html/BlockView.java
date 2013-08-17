/*
 * @(#)BlockView.java	1.17 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

/**
 * A view implementation to display a block (as a box)
 * with CSS specifications.
 *
 * @author  Timothy Prinzing
 * @version 1.17 04/22/99
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
     * Calculate the requirements of the block along the major
     * axis (i.e. the axis along with it tiles).  This is implemented
     * to provide the superclass behavior and then adjust it if the 
     * CSS width or height attribute is specified and applicable to
     * the axis.
     */
    protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
	SizeRequirements rr = super.calculateMajorAxisRequirements(axis, r);
	adjustSizeForCSS(axis, rr);
	return rr;
    }

    /**
     * Calculate the requirements of the block along the minor
     * axis (i.e. the axis orthoginal to the axis along with it tiles).  
     * This is implemented
     * to provide the superclass behavior and then adjust it if the 
     * CSS width or height attribute is specified and applicable to
     * the axis.
     */
    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	SizeRequirements rr = super.calculateMinorAxisRequirements(axis, r);
	adjustSizeForCSS(axis, rr);
	return rr;
    }

    /**
     * Adjust the given requirements to the CSS width or height if
     * it is specified along the applicable axis.
     */
    /*protected*/ void adjustSizeForCSS(int axis, SizeRequirements r) {
	if (axis == X_AXIS) {
	    Object widthValue = attr.getAttribute(CSS.Attribute.WIDTH);
	    if (widthValue != null) {
		int width = (int) ((CSS.LengthValue)widthValue).getValue();
		r.minimum = r.preferred = width;
		r.maximum = Math.max(r.maximum, width);
	    }
	} else {
	    Object heightValue = attr.getAttribute(CSS.Attribute.HEIGHT);
	    if (heightValue != null) {
		int height = (int) ((CSS.LengthValue)heightValue).getValue();
		r.minimum = r.preferred = height;
		r.maximum = Math.max(r.maximum, height);
	    }
	}
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

    public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
	super.changedUpdate(changes, a, f);
	int pos = changes.getOffset();
	if (pos <= getStartOffset() && (pos + changes.getLength()) >=
	    getEndOffset()) {
	    setPropertiesFromAttributes();
	}
    }

    /**
     * Update any cached values that come from attributes.
     */
    protected void setPropertiesFromAttributes() {
	attr = getStyleSheet().getViewAttributes(this);
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
