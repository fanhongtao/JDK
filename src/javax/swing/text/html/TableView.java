/*
 * @(#)TableView.java	1.7 98/08/26
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

import java.awt.*;
import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;

/**
 * HTML table view.  Extends the basic table support to
 * provide html tables.  This is primarily code to interpret
 * the html table attributes and css attributes and feed them
 * into the table formatting and rendering operations.
 *
 * @author  Timothy Prinzing
 * @version 1.7 08/26/98
 */
class TableView extends javax.swing.text.TableView {

    /**
     * Constructs a TableView for the given element.
     *
     * @param elem the element that this view is responsible for
     */
    public TableView(Element elem) {
	super(elem);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
    }

    /**
     * Establishes the parent view for this view.  This is
     * guaranteed to be called before any other methods if the
     * parent view is functioning properly.
     * <p> 
     * This is implemented
     * to forward to the superclass as well as call the
     * <a href="#setPropertiesFromAttributes">setPropertiesFromAttributes</a>
     * method to set the paragraph properties from the css
     * attributes.  The call is made at this time to ensure
     * the ability to resolve upward through the parents 
     * view attributes.
     *
     * @param parent the new parent, or null if the view is
     *  being removed from a parent it was previously added
     *  to
     */
    public void setParent(View parent) {
	super.setParent(parent);
	StyleSheet sheet = getStyleSheet();
	painter = sheet.getBoxPainter(attr);
	setPropertiesFromAttributes();
    }

    /**
     * Creates a new table cell.
     *
     * @param elem an element
     * @return the cell
     */
    protected TableCell createTableCell(Element elem) {
	return new CellView(elem);
    }

    /**
     * Creates a new table row.
     *
     * @param elem an element
     * @return the row
     */
    protected TableRow createTableRow(Element elem) {
	// PENDING(prinz) need to add support for some of the other
	// elements, but for now just ignore anything that is not
	// a TR.
	Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
	if (o == HTML.Tag.TR) {
	    return new RowView(elem);
	}
	return null;
    }

    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
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
    
    // --- View methods --------------------------------

    /**
     * Determines the maximum span for this view along an
     * axis.  For an html table this is basically the 
     * preferred size, as the table is not willing to
     * stretch beyond that.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the maximum span the view can be rendered into.
     * @see View#getPreferredSpan
     */
    public float getMaximumSpan(int axis) {
	return getPreferredSpan(axis);
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

    // --- variables ---------------------------------------

    private AttributeSet attr;
    private StyleSheet.BoxPainter painter;

    /**
     * An html row.  This adds storage of the appropriate
     * css attributes to the superclass behavior.
     */
    class RowView extends javax.swing.text.TableView.TableRow {

	/**
	 * Constructs a RowView for the given element.
	 *
	 * @param elem the element that this view is responsible for
	 */
        public RowView(Element elem) {
	    super(elem);
	    StyleSheet sheet = getStyleSheet();
	    attr = sheet.getViewAttributes(this);
	}

	/**
	 * Fetches the attributes to use when rendering.  This is
	 * implemented to multiplex the attributes specified in the
	 * model with a StyleSheet.
	 */
        public AttributeSet getAttributes() {
	    return attr;
	}

        protected StyleSheet getStyleSheet() {
	    HTMLDocument doc = (HTMLDocument) getDocument();
	    return doc.getStyleSheet();
	}

        private AttributeSet attr;
    }

    /**
     * A view of an html table cell.
     */
    class CellView extends javax.swing.text.TableView.TableCell {

	/**
	 * Constructs a TableCell for the given element.
	 *
	 * @param elem the element that this view is responsible for
	 */
        public CellView(Element elem) {
	    super(elem);
	    StyleSheet sheet = getStyleSheet();
	    attr = sheet.getViewAttributes(this);
	}

	/**
	 * Establishes the parent view for this view.  This is
	 * guaranteed to be called before any other methods if the
	 * parent view is functioning properly.
	 * <p> 
	 * This is implemented
	 * to forward to the superclass as well as call the
	 * <a href="#setPropertiesFromAttributes">setPropertiesFromAttributes</a>
	 * method to set the paragraph properties from the css
	 * attributes.  The call is made at this time to ensure
	 * the ability to resolve upward through the parents 
	 * view attributes.
	 *
	 * @param parent the new parent, or null if the view is
	 *  being removed from a parent it was previously added
	 *  to
	 */
        public void setParent(View parent) {
	    super.setParent(parent);
	    StyleSheet sheet = getStyleSheet();
	    painter = sheet.getBoxPainter(attr);
	    setPropertiesFromAttributes();
	}

	/**
	 * Gets the number of columns this cell spans (e.g. the
	 * grid width).  This is implemented return the number
	 * specified by the html <em>colspan</em> attribute.
         *
         * @return the number of columns
	 */
	public int getColumnCount() {
	    AttributeSet a = getElement().getAttributes();
	    String s = (String) a.getAttribute(HTML.Attribute.COLSPAN);
	    if (s != null) {
		try {
		    return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
		    return 1;
		}
	    }
	    return 1;
	}

	/**
	 * Gets the number of rows this cell spans (that is, the
	 * grid height).  This is implemented return the number
	 * specified by the html <em>rowspan</em> attribute.
         *
         * @return the number of rows
	 */
	public int getRowCount() {
	    AttributeSet a = getElement().getAttributes();
	    String s = (String) a.getAttribute(HTML.Attribute.ROWSPAN);
	    if (s != null) {
		try {
		    return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
		    return 1;
		}
	    }
	    return 1;
	}

	/**
	 * Perform layout for the major axis of the box (i.e. the
	 * axis that it represents).  The results of the layout should
	 * be placed in the given arrays which represent the allocations
	 * to the children along the major axis.  This is called by the
	 * superclass to recalculate the positions of the child views
	 * when the layout might have changed.
	 * <p>
	 * This is implemented to delegate to the superclass to
	 * tile the children.  If the target span is greater than
	 * was needed, the offsets are adjusted to align the children
	 * (i.e. position according to the html valign attribute).
	 *
	 * @param targetSpan the total span given to the view, which
	 *  whould be used to layout the children.
	 * @param axis the axis being layed out.
	 * @param offsets the offsets from the origin of the view for
	 *  each of the child views.  This is a return value and is
	 *  filled in by the implementation of this method.
	 * @param spans the span of each child view.  This is a return
	 *  value and is filled in by the implementation of this method.
	 * @returns the offset and span for each child view in the
	 *  offsets and spans parameters.
	 */
        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	    super.layoutMajorAxis(targetSpan, axis, offsets, spans);

	    // calculate usage
	    int used = 0;
	    int n = spans.length;
	    for (int i = 0; i < n; i++) {
		used += spans[i];
	    }

	    // calculate adjustments
	    int adjust = 0;
	    if (used < targetSpan) {
		// PENDING(prinz) change to use the css alignment.
		String valign = (String) getElement().getAttributes().getAttribute(
		    HTML.Attribute.VALIGN);
		if (valign == null) {
		    AttributeSet rowAttr = getElement().getParentElement().getAttributes();
		    valign = (String) rowAttr.getAttribute(HTML.Attribute.VALIGN);
		}
		if ((valign == null) || valign.equals("middle")) {
		    adjust = (targetSpan - used) / 2;
		} else if (valign.equals("bottom")) {
		    adjust = targetSpan - used;
		}
	    }

	    // make adjustments.
	    if (adjust != 0) {
		for (int i = 0; i < n; i++) {
		    offsets[i] += adjust;
		}
	    }
	}

	/**
	 * Calculate the requirements needed along the major axis.
	 * This is called by the superclass whenever the requirements 
	 * need to be updated (i.e. a preferenceChanged was messaged 
	 * through this view).  
	 * <p>
	 * This is implemented to delegate to the superclass, but
	 * indicate the maximum size is very large (i.e. the cell 
	 * is willing to expend to occupy the full height of the row).
	 * 
	 * @param axis the axis being layed out.
	 * @param r the requirements to fill in.  If null, a new one
	 *  should be allocated.
	 */
        protected SizeRequirements calculateMajorAxisRequirements(int axis, 
								  SizeRequirements r) {
	    SizeRequirements req = super.calculateMajorAxisRequirements(axis, r);
	    req.maximum = Integer.MAX_VALUE;
	    return req;
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

	/**
	 * Renders using the given rendering surface and area on that
	 * surface.  This is implemented to delegate to the superclass 
	 * after adjusting the allocation if needed because the 
	 * cell spans multiple grid points (eg. muliple columns
	 * and/or rows).
	 *
	 * @param g the rendering surface to use
	 * @param alloc the allocated region to render into
	 * @see View#paint
	 */
        public void paint(Graphics g, Shape alloc) {
	    Rectangle a = (Rectangle) alloc;
	    painter.paint(g, a.x, a.y, a.width, a.height, this);
	    super.paint(g, a);
	}

        private AttributeSet attr;
        private StyleSheet.BoxPainter painter;
    }

}
