/*
 * @(#)View.java	1.30 98/08/26
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
package javax.swing.text;

import java.awt.*;
import javax.swing.SwingConstants;
import javax.swing.event.*;

/**
 * A view of some portion of document model.  Provides
 * a mapping to model coordinates from view coordinates
 * and a mapping to view coordinates from model coordinates.
 * A view also provides rendering and layout services.
 *
 * @author  Timothy Prinzing
 * @version 1.30 08/26/98
 */
public abstract class View implements SwingConstants {

    /**
     * Creates a new View object.
     *
     * @param elem the element to represent
     */
    public View(Element elem) {
	this.elem = elem;
    }

    /**
     * Returns the parent of the view.
     *
     * @return the parent, null if none
     */
    public View getParent() {
	return parent;
    }

    /**
     *  Returns a boolean that indicates whether
     *  the view is visible or not.  By default
     *  all views are visible.
     *
     * @return boolean value.
     */
    public boolean isVisible() {
	return true;
    }

	
    /**
     * Determines the preferred span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @see View#getPreferredSpan
     */
    public abstract float getPreferredSpan(int axis);

    /**
     * Determines the minimum span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the minimum span the view can be rendered into.
     * @see View#getPreferredSpan
     */
    public float getMinimumSpan(int axis) {
	int w = getResizeWeight(axis);
	if (w == 0) {
	    // can't resize
	    return getPreferredSpan(axis);
	}
	return 0;
    }

    /**
     * Determines the maximum span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the maximum span the view can be rendered into.
     * @see View#getPreferredSpan
     */
    public float getMaximumSpan(int axis) {
	int w = getResizeWeight(axis);
	if (w == 0) {
	    // can't resize
	    return getPreferredSpan(axis);
	}
	return Integer.MAX_VALUE;
    }
	
    /**
     * Child views can call this on the parent to indicate that
     * the preference has changed and should be reconsidered
     * for layout.  By default this just propagates upward to 
     * the next parent.  The root view will call 
     * <code>revalidate</code> on the associated text component.
     *
     * @param child the child view
     * @param width true if the width preference has changed
     * @param height true if the height preference has changed
     * @see javax.swing.JComponent#revalidate
     */
    public void preferenceChanged(View child, boolean width, boolean height) {
	getParent().preferenceChanged(child, width, height);
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  By default this is simply centered.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns The desired alignment.  This should be a value
     *   >= 0.0 and <= 1.0 where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     */
    public float getAlignment(int axis) {
	return 0.5f;
    }

    /**
     * Renders using the given rendering surface and area on that
     * surface.  The view may need to do layout and create child
     * views to enable itself to render into the given allocation.
     *
     * @param g the rendering surface to use
     * @param allocation the allocated region to render into
     * @see View#paint
     */
    public abstract void paint(Graphics g, Shape allocation);

    /**
     * Establishes the parent view for this view.  This is
     * guaranteed to be called before any other methods if the
     * parent view is functioning properly.  This is also
     * the last method called, since it is called to indicate
     * the view has been removed from the hierarchy as 
     * well.  If this is reimplemented, 
     * <code>super.setParent()</code> should be called.
     *
     * @param parent the new parent, or null if the view is
     *  being removed from a parent it was previously added
     *  to
     */
    public void setParent(View parent) {
	this.parent = parent;
    }

    /** 
     * Returns the number of views in this view.  Since
     * the default is to not be a composite view this
     * returns 0.
     *
     * @return the number of views >= 0
     * @see View#getViewCount
     */
    public int getViewCount() {
	return 0;
    }

    /** 
     * Gets the nth child view.  Since there are no
     * children by default, this returns null.
     *
     * @param n the number of the view to get, >= 0 && < getViewCount()
     * @return the view
     */
    public View getView(int n) {
	return null;
    }

    /**
     * Fetches the allocation for the given child view. 
     * This enables finding out where various views
     * are located, without assuming the views store
     * their location.  This returns null since the
     * default is to not have any child views.
     *
     * @param index the index of the child, >= 0 && < getViewCount()
     * @param a  the allocation to this view.
     * @return the allocation to the child
     */
    public Shape getChildAllocation(int index, Shape a) {
	return null;
    }

    /**
     * Provides a way to determine the next visually represented model 
     * location that one might place a caret.  Some views may not be visible,
     * they might not be in the same order found in the model, or they just
     * might not allow access to some of the locations in the model.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @param direction the direction from the current position that can
     *  be thought of as the arrow keys typically found on a keyboard.
     *  This may be SwingConstants.WEST, SwingConstants.EAST, 
     *  SwingConstants.NORTH, or SwingConstants.SOUTH.  
     * @return the location within the model that best represents the next
     *  location visual position.
     * @exception BadLocationException
     * @exception IllegalArgumentException for an invalid direction
     */
    public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, 
					 int direction, Position.Bias[] biasRet) 
      throws BadLocationException {

	biasRet[0] = Position.Bias.Forward;
	switch (direction) {
	case NORTH:
	{
	    JTextComponent target = (JTextComponent) getContainer();
	    Rectangle r = target.modelToView(pos);
	    pos = Utilities.getPositionAbove(target, pos, r.x);
	}
	    break;
	case SOUTH:
	{
	    JTextComponent target = (JTextComponent) getContainer();
	    Rectangle r = target.modelToView(pos);
	    pos = Utilities.getPositionBelow(target, pos, r.x);
	}
	    break;
	case WEST:
	    if(pos == -1) {
		pos = getEndOffset() - 1;
	    }
	    else {
		pos -= 1;
	    }
	    break;
	case EAST:
	    if(pos == -1) {
		pos = getStartOffset();
	    }
	    else {
		pos += 1;
	    }
	    break;
	default:
	    throw new IllegalArgumentException("Bad direction: " + direction);
	}
	return pos;
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @param b the bias toward the previous character or the
     *  next character represented by the offset, in case the 
     *  position is a boundary of two views. 
     * @return the bounding box of the given position is returned
     * @exception BadLocationException  if the given position does
     *   not represent a valid location in the associated document
     * @exception IllegalArgumentException for an invalid bias argument
     * @see View#viewToModel
     */
    public abstract Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException;

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param p0 the position to convert >= 0
     * @param b0 the bias toward the previous character or the
     *  next character represented by p0, in case the 
     *  position is a boundary of two views. 
     * @param p1 the position to convert >= 0
     * @param b1 the bias toward the previous character or the
     *  next character represented by p1, in case the 
     *  position is a boundary of two views. 
     * @param a the allocated region to render into
     * @return the bounding box of the given position is returned
     * @exception BadLocationException  if the given position does
     *   not represent a valid location in the associated document
     * @exception IllegalArgumentException for an invalid bias argument
     * @see View#viewToModel
     */
    public Shape modelToView(int p0, Position.Bias b0, int p1, Position.Bias b1, Shape a) throws BadLocationException {
	Shape s0 = modelToView(p0, a, b0);
	Shape s1;
	if (p1 == getEndOffset()) {
	    try {
		s1 = modelToView(p1, a, b1);
	    } catch (BadLocationException ble) {
		s1 = null;
	    }
	    if (s1 == null) {
		// Assume extends left to right.
		Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a :
		                  a.getBounds();
		s1 = new Rectangle(alloc.x + alloc.width - 1, alloc.y,
				   1, alloc.height);
	    }
	}
	else {
	    s1 = modelToView(p1, a, b1);
	}
	Rectangle r0 = s0.getBounds();
	Rectangle r1 = (s1 instanceof Rectangle) ? (Rectangle) s1 :
	                                           s1.getBounds();
	if (r0.y != r1.y) {
	    // If it spans lines, force it to be the width of the view.
	    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a :
		              a.getBounds();
	    r0.x = alloc.x;
	    r0.width = alloc.width;
	}
	r0.add(r1);
	return r0;
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.  The biasReturn argument will be
     * filled in to indicate that the point given is closer to the next
     * character in the model or the previous character in the model.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point in the view >= 0.  The biasReturn argument will be
     * filled in to indicate that the point given is closer to the next
     * character in the model or the previous character in the model.
     */
    public abstract int viewToModel(float x, float y, Shape a, Position.Bias[] biasReturn);

    /**
     * Gives notification that something was inserted into the document 
     * in a location that this view is responsible for.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#insertUpdate
     */
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    }

    /**
     * Gives notification from the document that attributes were removed 
     * in a location that this view is responsible for.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#removeUpdate
     */
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    }

    /**
     * Gives notification from the document that attributes were changed
     * in a location that this view is responsible for.
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#changedUpdate
     */
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    }

    /**
     * Fetches the model associated with the view.
     *
     * @return the view model, null if none
     * @see View#getDocument
     */
    public Document getDocument() {
	return elem.getDocument();
    }

    /**
     * Fetches the portion of the model that this view is
     * responsible for.
     *
     * @return the starting offset into the model >= 0
     * @see View#getStartOffset
     */
    public int getStartOffset() {
	return elem.getStartOffset();
    }

    /**
     * Fetches the portion of the model that this view is
     * responsible for.
     *
     * @return the ending offset into the model >= 0
     * @see View#getEndOffset
     */
    public int getEndOffset() {
	return elem.getEndOffset();
    }

    /**
     * Fetches the structural portion of the subject that this
     * view is mapped to.  The view may not be responsible for the
     * entire portion of the element.
     *
     * @return the subject
     * @see View#getElement
     */
    public Element getElement() {
	return elem;
    }

    /**
     * Fetches the attributes to use when rendering.  By default
     * this simply returns the attributes of the associated element.
     * This method should be used rather than using the element
     * directly to obtain access to the attributes to allow
     * view-specific attributes to be mixed in or to allow the
     * view to have view-specific conversion of attributes by
     * subclasses.
     * Each view should document what attributes it recognizes
     * for the purpose of rendering or layout, and should always
     * access them through the AttributeSet returned by this method.
     */
    public AttributeSet getAttributes() {
	return elem.getAttributes();
    }

    /**
     * Tries to break this view on the given axis.  This is
     * called by views that try to do formatting of their
     * children.  For example, a view of a paragraph will
     * typically try to place its children into row and 
     * views representing chunks of text can sometimes be 
     * broken down into smaller pieces.
     * <p>
     * This is implemented to return the view itself, which
     * represents the default behavior on not being
     * breakable.  If the view does support breaking, the
     * starting offset of the view returned should be the
     * given offset, and the end offset should be less than
     * or equal to the end offset of the view being broken.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param offset the location in the document model
     *   that a broken fragment would occupy >= 0.  This
     *   would be the starting offset of the fragment
     *   returned.
     * @param pos the position along the axis that the
     *  broken view would occupy >= 0.  This may be useful for
     *  things like tab calculations.
     * @param len specifies the distance along the axis
     *  where a potential break is desired >= 0.  
     * @return the fragment of the view that represents the
     *  given span, if the view can be broken.  If the view
     *  doesn't support breaking behavior, the view itself is
     *  returned.
     * @see ParagraphView
     */
    public View breakView(int axis, int offset, float pos, float len) {
	return this;
    }

    /**
     * Create a view that represents a portion of the element.
     * This is potentially useful during formatting operations
     * for taking measurements of fragments of the view.  If 
     * the view doesn't support fragmenting (the default), it 
     * should return itself.  
     *
     * @param p0 the starting offset >= 0.  This should be a value
     *   greater or equal to the element starting offset and
     *   less than the element ending offset.
     * @param p1 the ending offset > p0.  This should be a value
     *   less than or equal to the elements end offset and
     *   greater than the elements starting offset.
     * @returns the view fragment, or itself if the view doesn't
     *   support breaking into fragments.
     * @see LabelView
     */
    public View createFragment(int p0, int p1) {
	return this;
    }

    /**
     * Determines how attractive a break opportunity in 
     * this view is.  This can be used for determining which
     * view is the most attractive to call <code>breakView</code>
     * on in the process of formatting.  A view that represents
     * text that has whitespace in it might be more attractive
     * than a view that has no whitespace, for example.  The
     * higher the weight, the more attractive the break.  A
     * value equal to or lower than <code>BadBreakWeight</code>
     * should not be considered for a break.  A value greater
     * than or equal to <code>ForcedBreakWeight</code> should
     * be broken.
     * <p>
     * This is implemented to provide the default behavior
     * of returning <code>BadBreakWeight</code> unless the length
     * is greater than the length of the view in which case the 
     * entire view represents the fragment.  Unless a view has
     * been written to support breaking behavior, it is not
     * attractive to try and break the view.  An example of
     * a view that does support breaking is <code>LabelView</code>.
     * An example of a view that uses break weight is 
     * <code>ParagraphView</code>.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param pos the potential location of the start of the 
     *   broken view >= 0.  This may be useful for calculating tab
     *   positions.
     * @param len specifies the relative length from <em>pos</em>
     *   where a potential break is desired >= 0.
     * @return the weight, which should be a value between
     *   ForcedBreakWeight and BadBreakWeight.
     * @see LabelView
     * @see ParagraphView
     * @see BadBreakWeight
     * @see GoodBreakWeight
     * @see ExcellentBreakWeight
     * @see ForcedBreakWeight
     */
    public int getBreakWeight(int axis, float pos, float len) {
	if (len > getPreferredSpan(axis)) {
	    return GoodBreakWeight;
	}
	return BadBreakWeight;
    }

    /**
     * Determines the resizability of the view along the
     * given axis.  A value of 0 or less is not resizable.
     *
     * @param axis View.X_AXIS or View.Y_AXIS
     * @return the weight
     */
    public int getResizeWeight(int axis) {
	return 0;
    }

    /**
     * Sets the size of the view.  This should cause 
     * layout of the view, if it has any layout duties.
     * The default is to do nothing.
     *
     * @param width the width >= 0
     * @param height the height >= 0
     */
    public void setSize(float width, float height) {
    }

    /**
     * Fetches the container hosting the view.  This is useful for
     * things like scheduling a repaint, finding out the host 
     * components font, etc.  The default implementation
     * of this is to forward the query to the parent view.
     *
     * @return the container, null if none
     */
    public Container getContainer() {
	View v = getParent();
	return (v != null) ? v.getContainer() : null;
    }

    /**
     * Fetches the ViewFactory implementation that is feeding
     * the view hierarchy.  Normally the views are given this
     * as an argument to updates from the model when they
     * are most likely to need the factory, but this
     * method serves to provide it at other times.
     *
     * @return the factory, null if none
     */
    public ViewFactory getViewFactory() {
	View v = getParent();
	return (v != null) ? v.getViewFactory() : null;
    }

    /**
     * The weight to indicate a view is a bad break
     * opportunity for the purpose of formatting.  This
     * value indicates that no attempt should be made to
     * break the view into fragments as the view has 
     * not been written to support fragmenting.
     * @see #getBreakWeight
     * @see GoodBreakWeight
     * @see ExcellentBreakWeight
     * @see ForcedBreakWeight
     */
    public static final int BadBreakWeight = 0;

    /**
     * The weight to indicate a view supports breaking,
     * but better opportunities probably exist.
     * 
     * @see #getBreakWeight
     * @see BadBreakWeight
     * @see GoodBreakWeight
     * @see ExcellentBreakWeight
     * @see ForcedBreakWeight
     */
    public static final int GoodBreakWeight = 1000;

    /**
     * The weight to indicate a view supports breaking,
     * and this represents a very attractive place to
     * break.
     *
     * @see #getBreakWeight
     * @see BadBreakWeight
     * @see GoodBreakWeight
     * @see ExcellentBreakWeight
     * @see ForcedBreakWeight
     */
    public static final int ExcellentBreakWeight = 2000;

    /**
     * The weight to indicate a view supports breaking,
     * and must be broken to be represented properly 
     * when placed in a view that formats it's children
     * by breaking them.
     *
     * @see #getBreakWeight
     * @see BadBreakWeight
     * @see GoodBreakWeight
     * @see ExcellentBreakWeight
     * @see ForcedBreakWeight
     */
    public static final int ForcedBreakWeight = 3000;

    /**
     * Axis for format/break operations.
     */
    public static final int X_AXIS = HORIZONTAL;

    /**
     * Axis for format/break operations.
     */
    public static final int Y_AXIS = VERTICAL;

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it. This is 
     * implemented to default the bias to Position.Bias.Forward
     * which was previously implied.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position is returned
     * @exception BadLocationException  if the given position does
     *   not represent a valid location in the associated document
     * @see View#modelToView
     * @deprecated
     */
    public Shape modelToView(int pos, Shape a) throws BadLocationException {
	return modelToView(pos, a, Position.Bias.Forward);
    }


    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point in the view >= 0
     * @see View#viewToModel
     * @deprecated
     */
    public int viewToModel(float x, float y, Shape a) {
	sharedBiasReturn[0] = Position.Bias.Forward;
	return viewToModel(x, y, a, sharedBiasReturn);
    }

    // static argument available for viewToModel calls since only
    // one thread at a time may call this method.
    static final Position.Bias[] sharedBiasReturn = new Position.Bias[1];

    private View parent;
    private Element elem;

};

