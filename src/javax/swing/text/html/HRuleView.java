/*
 * @(#)HRuleView.java	1.24 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text.html;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.text.*;
import java.util.Enumeration;
import java.lang.Integer;

/**
 * A view implementation to display an html horizontal
 * rule.
 *
 * @author  Timothy Prinzing
 * @author  Sara Swanson
 * @version 1.24 11/29/01
 */
class HRuleView extends View  {

    /**
     * Creates a new view that represents an <hr> element.
     *
     * @param elem the element to create a view for
     */
    public HRuleView(Element elem) {
	super(elem);
	AttributeSet attr = elem.getAttributes();

	if (attr != null) {
	    margin_left = 0;
	    margin_right = 0;
            alignment = StyleConstants.getAlignment(attr);
	    noshade = (String) attr.getAttribute("noshade");
	    String sizestr = (String)attr.getAttribute("size");
	    if (sizestr != null)
	        size = (Integer.valueOf(sizestr)).intValue();
	    String hrwidthstr = (String)attr.getAttribute("width");
	    if (hrwidthstr != null)
		hrwidth = (Integer.valueOf(hrwidthstr)).intValue();
	}

	bevel = BorderFactory.createLoweredBevelBorder();
    }

    // --- View methods ---------------------------------------------

    /**
     * Paints the view.
     *
     * @param g the graphics context
     * @param a the allocation region for the view
     * @see View#paint
     */
    public void paint(Graphics g, Shape a) {
	Rectangle alloc = a.getBounds();
	int x = 0;
	int y = alloc.y + SPACE_ABOVE;
	int width = alloc.width - (int)(margin_left + margin_right);
	if (hrwidth > 0)
		width = hrwidth;
	int height = alloc.height - (SPACE_ABOVE + SPACE_BELOW);
 	if (size > 0)
		height = size;

	// Align the rule horizontally.
        switch (alignment) {
        case StyleConstants.ALIGN_CENTER:
            x = alloc.x + (alloc.width / 2) - (width / 2);
	    break;
        case StyleConstants.ALIGN_RIGHT:
            x = alloc.x + alloc.width - hrwidth - (int)(margin_right);
	    break;
        case StyleConstants.ALIGN_LEFT:
        default:
            x = alloc.x + (int)margin_left;
	    break;
        }

	// Paint either a shaded rule or a solid line.
	if (noshade == HTML.NULL_ATTRIBUTE_VALUE)
	    g.fillRect(x, y, width, height);
	else
	    bevel.paintBorder(getContainer(), g, x, y, width, height);

    }


    /**
     * Calculates the desired shape of the rule... this is
     * basically the preferred size of the border.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @return the desired span
     * @see View#getPreferredSpan
     */
    public float getPreferredSpan(int axis) {
	Insets i = bevel.getBorderInsets(getContainer());
	switch (axis) {
	case View.X_AXIS:
	    return i.left + i.right;
	case View.Y_AXIS:
	    if (size > 0) {
	        return size + SPACE_ABOVE + SPACE_BELOW;
	    } else {
		if (noshade == HTML.NULL_ATTRIBUTE_VALUE) {
		    return 1 + SPACE_ABOVE + SPACE_BELOW;
		} else {
		    return i.top + i.bottom + SPACE_ABOVE + SPACE_BELOW;
		}
	    }
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Gets the resize weight for the axis.
     * The rule is: rigid vertically and flexible horizontally.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @return the weight
     */
    public int getResizeWeight(int axis) {
	if (axis == View.X_AXIS) {
		return 1;
	} else if (axis == View.Y_AXIS) {
		return 0;
	} else {
	    return 0;
	}
    }

    /**
     * Determines how attractive a break opportunity in 
     * this view is.  This is implemented to request a forced break.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param pos the potential location of the start of the 
     *   broken view >= 0.  This may be useful for calculating tab
     *   positions.
     * @param len specifies the relative length from <em>pos</em>
     *   where a potential break is desired >= 0.
     * @return the weight, which should be a value between
     *   ForcedBreakWeight and BadBreakWeight.
     */
    public int getBreakWeight(int axis, float pos, float len) {
	if (axis == X_AXIS) {
	    return ForcedBreakWeight;
	}
	return BadBreakWeight;
    }

    public View breakView(int axis, int offset, float pos, float len) {
	return null;
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does not
     * represent a valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	int p0 = getStartOffset();
	int p1 = getEndOffset();
	if ((pos >= p0) && (pos <= p1)) {
	    Rectangle r = a.getBounds();
	    if (pos == p1) {
		r.x += r.width;
	    }
	    r.width = 0;
	    return r;
	}
	return null;
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point of view
     * @see View#viewToModel
     */
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	Rectangle alloc = (Rectangle) a;
	if (x < alloc.x + (alloc.width / 2)) {
	    bias[0] = Position.Bias.Forward;
	    return getStartOffset();
	}
	bias[0] = Position.Bias.Backward;
	return getEndOffset();
    }

    // --- variables ------------------------------------------------

    private Border bevel;
    private float margin_left = 0;
    private float margin_right = 0;
    private int alignment = StyleConstants.ALIGN_LEFT;
    private String noshade = null;
    private int size = 0;
    private int hrwidth = 0;

    private static final int SPACE_ABOVE = 3;
    private static final int SPACE_BELOW = 3;
    
}

