/*
 * @(#)CompositeView.java	1.37 98/09/11
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

import java.util.Vector;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.SwingConstants;

/**
 * A view of a text model that has a children
 * box.  If the box is vertical, it might be useful to represent
 * something like a collection of lines or paragraphs.  If the
 * box is horizontal, it might be used to represent unwrapped
 * lines.
 *
 * @author  Timothy Prinzing
 * @version 1.37 09/11/98
 */
public abstract class CompositeView extends View {

    /**
     * Constructs a CompositeView for the given element.
     *
     * @param elem  the element this view is responsible for
     */
    public CompositeView(Element elem) {
	super(elem);
	children = new View[1];
	nchildren = 0;

    }

    /**
     * Loads all of the children to initialize the view.
     * This is called by the <code>setParent</code> method.
     * Subclasses can reimplement this to initialize their
     * child views in a different manner.  The default
     * implementation creates a child view for each 
     * child element.
     *
     * @param f the view factory
     */
    protected void loadChildren(ViewFactory f) {
	Element e = getElement();
	int n = e.getElementCount();
	if (n > 0) {
	    View[] added = new View[n];
	    for (int i = 0; i < n; i++) {
		added[i] = f.create(e.getElement(i));
	    }
	    replace(0, 0, added);
	}
    }

    /**
     * Removes all of the children.
     */
    public void removeAll() {
	replace(0, nchildren, ZERO);
    }

    /**
     * Inserts a single child view.
     *
     * @param offs the offset of the view to insert before >= 0
     * @param v the view
     */
    public void insert(int offs, View v) {
	ONE[0] = v;
	replace(offs, 0, ONE);
    }

    /**
     * Appends a single child view.
     *
     * @param v the view
     */
    public void append(View v) {
	ONE[0] = v;
	replace(nchildren, 0, ONE);
    }

    /**
     * Invalidates the layout and resizes the cache of requests/allocations,
     * allowing for the replacement of child views.
     *
     * @param offset the starting offset into the child views to insert
     *   before >= 0
     * @param length the number of existing child views affected >= 0
     * @param views the child views to use as replacements
     */
    public void replace(int offset, int length, View[] views) {
	// update parent reference on removed views
	for (int i = offset; i < offset + length; i++) {
	    children[i].setParent(null);
	}
	
	// update the array
	int delta = views.length - length;
	int src = offset + length;
	int nmove = nchildren - src;
	int dest = src + delta;
	if ((nchildren + delta) >= children.length) {
	    // need to grow the array
	    int newLength = Math.max(2*children.length, nchildren + delta);
	    View[] newChildren = new View[newLength];
	    System.arraycopy(children, 0, newChildren, 0, offset);
	    System.arraycopy(views, 0, newChildren, offset, views.length);
	    System.arraycopy(children, src, newChildren, dest, nmove);
	    children = newChildren;
	} else {
	    // patch the existing array
	    System.arraycopy(children, src, children, dest, nmove);
	    System.arraycopy(views, 0, children, offset, views.length);
	}
	nchildren = nchildren + delta;

	// update parent reference on added views
	for (int i = 0; i < views.length; i++) {
	    views[i].setParent(this);
	}
    }

    // --- View methods ---------------------------------------------

    /**
     * Sets the parent of the view.
     * This is reimplemented to provide the superclass
     * behavior as well as calling the <code>loadChildren</code>
     * method.  The children should not be loaded in the 
     * constructor because the act of setting the parent
     * may cause them to try to search up the hierarchy
     * (to get the hosting Container for example).
     *
     * @param parent the parent of the view, null if none
     */
    public void setParent(View parent) {
	super.setParent(parent);
	if (parent != null) {
	    ViewFactory f = getViewFactory();
	    loadChildren(f);
	}
    }

    /** 
     * Returns the number of views in this view.
     *
     * @return the number of views >= 0
     * @see #getView
     */
    public int getViewCount() {
	return nchildren;
    }

    /** 
     * Gets the n-th view in this container.
     *
     * @param n the number of the view to get, >= 0 && < getViewCount()
     * @return the view
     */
    public View getView(int n) {
	return children[n];
    }

    /**
     * Fetches the allocation for the given child view. 
     * This enables finding out where various views
     * are located, without assuming the views store
     * their location.  
     *
     * @param index the index of the child, >= 0 && < getViewCount()
     * @param a  the allocation to this view.
     * @return the allocation to the child
     */
    public Shape getChildAllocation(int index, Shape a) {
	Rectangle alloc = a.getBounds();
	childAllocation(index, alloc);
	return alloc;
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does
     *   not represent a valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	boolean isBackward = (b == Position.Bias.Backward);
	int testPos = (isBackward) ? Math.max(0, pos - 1) : pos;
	if(isBackward && testPos < getStartOffset()) {
	    return null;
	}
	int vIndex = getViewIndexAtPosition(testPos);
	if ((vIndex != -1) && (vIndex < getViewCount())) {
	    View v = getView(vIndex);
	    if(v != null && testPos >= v.getStartOffset() &&
	       testPos < v.getEndOffset()) {
		Rectangle alloc = getInsideAllocation(a);
		childAllocation(vIndex, alloc);
		Shape retShape = v.modelToView(pos, alloc, b);
		if(retShape == null && v.getEndOffset() == pos) {
		    if(++vIndex < getViewCount()) {
			v = getView(vIndex);
			childAllocation(vIndex, alloc);
			retShape = v.modelToView(pos, alloc, b);
		    }
		}
		return retShape;
	    }
	}
	throw new BadLocationException("Position not represented by view",
				       pos);
    }

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
	if (p0 == getStartOffset() && p1 == getEndOffset()) {
	    return a;
	}
	Rectangle alloc = getInsideAllocation(a);
	Rectangle r0 = new Rectangle(alloc);
	View v0 = getViewAtPosition(p0, r0);
	Rectangle r1 = new Rectangle(alloc);
	View v1 = getViewAtPosition(p1, r1);
	if (v0 == v1) {
	    if (v0 == null) {
		return a;
	    }
	    // Range contained in one view
	    return v0.modelToView(p0, b0, p1, b1, r0);
	}
	// Straddles some views.
	int viewCount = getViewCount();
	int counter = 0;
	while (counter < viewCount) {
	    View v;
	    // Views may not be in same order as model.
	    // v0 or v1 may be null if there is a gap in the range this
	    // view contains.
	    if ((v = getView(counter)) == v0 || v == v1) {
		View endView;
		Rectangle retRect;
		Rectangle tempRect = new Rectangle();
		if (v == v0) {
		    retRect = v0.modelToView(p0, b0, v0.getEndOffset(),
					     Position.Bias.Backward, r0).
                              getBounds();
		    endView = v1;
		}
		else {
		    retRect = v1.modelToView(v1.getStartOffset(),
					     Position.Bias.Forward,
					     p1, b1, r1).getBounds();
		    endView = v0;
		}

		// Views entirely covered by range.
		while (++counter < viewCount &&
		       (v = getView(counter)) != endView) {
		    tempRect.setBounds(alloc);
		    childAllocation(counter, tempRect);
		    retRect.add(tempRect);
		}

		// End view.
		if (endView != null) {
		    Shape endShape;
		    if (endView == v1) {
			endShape = v1.modelToView(v1.getStartOffset(),
						  Position.Bias.Forward,
						  p1, b1, r1);
		    }
		    else {
			endShape = v0.modelToView(p0, b0, v0.getEndOffset(),
						  Position.Bias.Backward, r0);
		    }
		    if (endShape instanceof Rectangle) {
			retRect.add((Rectangle)endShape);
		    }
		    else {
			retRect.add(endShape.getBounds());
		    }
		}
		return retRect;
	    }
	    counter++;
	}
	throw new BadLocationException("Position not represented by view", p0);
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x   x coordinate of the view location to convert >= 0
     * @param y   y coordinate of the view location to convert >= 0
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point in the view >= 0
     * @see View#viewToModel
     */
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	Rectangle alloc = getInsideAllocation(a);
	if (isBefore((int) x, (int) y, alloc)) {
	    // point is before the range represented
	    int retValue = -1;

	    try {
		retValue = getNextVisualPositionFrom(-1, Position.Bias.Forward,
						     a, EAST, bias);
	    } catch (BadLocationException ble) { }
	    catch (IllegalArgumentException iae) { }
	    if(retValue == -1) {
		retValue = getStartOffset();
		bias[0] = Position.Bias.Forward;
	    }
	    return retValue;
	} else if (isAfter((int) x, (int) y, alloc)) {
	    // point is after the range represented.
	    int retValue = -1;
	    try {
		retValue = getNextVisualPositionFrom(-1, Position.Bias.Forward,
						     a, WEST, bias);
	    } catch (BadLocationException ble) { }
	    catch (IllegalArgumentException iae) { }

	    if(retValue == -1) {
		// NOTE: this could actually use end offset with backward.
		retValue = getEndOffset() - 1;
		bias[0] = Position.Bias.Forward;
	    }
	    return retValue;
	} else {
	    // locate the child and pass along the request
	    View v = getViewAtPoint((int) x, (int) y, alloc);
	    if (v != null) {
	      return v.viewToModel(x, y, alloc, bias);
	    }
	}
	return -1;
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
        Rectangle alloc = getInsideAllocation(a);

	switch (direction) {
	case NORTH:
	    return getNextNorthSouthVisualPositionFrom(pos, b, a, direction,
						       biasRet);
	case SOUTH:
	    return getNextNorthSouthVisualPositionFrom(pos, b, a, direction,
						       biasRet);
	case EAST:
	    return getNextEastWestVisualPositionFrom(pos, b, a, direction,
						     biasRet);
	case WEST:
	    return getNextEastWestVisualPositionFrom(pos, b, a, direction,
						     biasRet);
	default:
	    throw new IllegalArgumentException("Bad direction: " + direction);
	}
    }


    // --- local methods ----------------------------------------------------


    /**
     * Tests whether a point lies before the rectangle range.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param alloc the rectangle
     * @return true if the point is before the specified range
     */
    protected abstract boolean isBefore(int x, int y, Rectangle alloc);

    /**
     * Tests whether a point lies after the rectangle range.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param alloc the rectangle
     * @return true if the point is after the specified range
     */
    protected abstract boolean isAfter(int x, int y, Rectangle alloc);

    /**
     * Fetches the child view at the given point.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param alloc the parent's allocation on entry, which should
     *   be changed to the child's allocation on exit
     * @return the child view
     */
    protected abstract View getViewAtPoint(int x, int y, Rectangle alloc);

    /**
     * Returns the allocation for a given child.
     *
     * @param index the index of the child, >= 0 && < getViewCount()
     * @param a  the allocation to the interior of the box on entry, 
     *   and the allocation of the view containing the position on exit
     */
    protected abstract void childAllocation(int index, Rectangle a);

    /**
     * Fetches the child view that represents the given position in
     * the model.  This is implemented to fetch the view in the case
     * where there is a child view for each child element.
     *
     * @param pos the position >= 0
     * @param a  the allocation to the interior of the box on entry, 
     *   and the allocation of the view containing the position on exit
     * @returns  the view representing the given position, or 
     *   null if there isn't one
     */
    protected View getViewAtPosition(int pos, Rectangle a) {
	Element elem = getElement();
	int index = elem.getElementIndex(pos);
	Element child = elem.getElement(index);
	if ((child != null) && (index < getViewCount())) {
	    View v = getView(index);
	    if (v.getElement() == child) {
		if (a != null) {
		    childAllocation(index, a);
		}
		return v;
	    }
	}
	return null;
    }

    /**
     * Fetches the child view index representing the given position in
     * the model.  This is implemented to fetch the view in the case
     * where there is a child view for each child element.
     *
     * @param pos the position >= 0
     * @returns  index of the view representing the given position, or 
     *   -1 if no view represents that position
     */
    protected int getViewIndexAtPosition(int pos) {
	Element elem = getElement();
	return elem.getElementIndex(pos);
    }

    /**
     * Translates the allocation given to the view to the allocation used
     * for composing the interior.  This takes into account any 
     * margins that were specified.
     *
     * @param a The allocation given to the view.
     * @returns The allocation that represents the inside of the 
     *   view after the margins have all been removed.  If the
     *   given allocation was null, the return value is null.
     */
    protected Rectangle getInsideAllocation(Shape a) {
	if (a != null) {
	    Rectangle alloc = new Rectangle(a.getBounds());
	    alloc.x += left;
	    alloc.y += top;
	    alloc.width -= left + right;
	    alloc.height -= top + bottom;
	    return alloc;
	}
	return null;
    }

    /**
     * Sets the insets from the paragraph attributes specified in
     * the given attributes.
     *
     * @param attr the attributes
     */
    protected final void setParagraphInsets(AttributeSet attr) {
	// Since version 1.1 doesn't have scaling and assumes 
	// a pixel is equal to a point, we just cast the point
	// sizes to integers.
	top = (short) StyleConstants.getSpaceAbove(attr);
	left = (short) StyleConstants.getLeftIndent(attr);
	bottom = (short) StyleConstants.getSpaceBelow(attr);
	right = (short) StyleConstants.getRightIndent(attr);
    }

    /**
     * Sets the insets for the view.
     *
     * @param top the top inset >= 0
     * @param left the left inset >= 0
     * @param bottom the bottom inset >= 0
     * @param right the right inset >= 0
     */
    protected final void setInsets(short top, short left, short bottom, short right) {
	this.top = top;
	this.left = left;
	this.right = right;
	this.bottom = bottom;
    }

    /**
     * Gets the left inset.
     *
     * @return the inset >= 0
     */
    protected final short getLeftInset() {
	return left;
    }

    /**
     * Gets the right inset.
     *
     * @return the inset >= 0
     */
    protected final short getRightInset() {
	return right;
    }

    /**
     * Gets the top inset.
     *
     * @return the inset >= 0
     */
    protected final short getTopInset() {
	return top;
    }

    /**
     * Gets the bottom inset.
     *
     * @return the inset >= 0
     */
    protected final short getBottomInset() {
	return bottom;
    }

    /**
     * Returns the next visual position for the cursor, in either the
     * east or west direction.
     *
     * @return next position west of the passed in position.
     */
    // PENDING: This only checks the next element. If one of the children
    // Views returns -1, it should continue checking all the children.
    // PENDING(sky): This name sucks! Come up with a better one!
    protected int getNextNorthSouthVisualPositionFrom(int pos, Position.Bias b,
						      Shape a, int direction,
						      Position.Bias[] biasRet)
	                                        throws BadLocationException {
	if(getViewCount() == 0) {
	    // Nothing to do.
	    return pos;
	}

	boolean isNorth = (direction == NORTH);
	Rectangle alloc = getInsideAllocation(a);
	int retValue;
	if(pos == -1) {
	    View v = (isNorth) ? getView(getViewCount() - 1) : getView(0);
	    childAllocation(0, alloc);
	    retValue = v.getNextVisualPositionFrom(pos, b, alloc, direction,
						   biasRet);
	}
	else {
	    int vIndex;
	    if(b == Position.Bias.Backward && pos > 0) {
		vIndex = getViewIndexAtPosition(pos - 1);
	    }
	    else {
		vIndex = getViewIndexAtPosition(pos);
	    }
	    View v = getView(vIndex);
	    childAllocation(vIndex, alloc);
	    retValue = v.getNextVisualPositionFrom(pos, b, alloc, direction,
						   biasRet);
	    if(retValue == -1) {
		if((isNorth && --vIndex >= 0) ||
		   (!isNorth && ++vIndex < getViewCount())) {
		    v = getView(vIndex);
		    alloc = getInsideAllocation(a);
		    childAllocation(vIndex, alloc);
		    retValue = v.getNextVisualPositionFrom(-1, b, alloc,
							   direction, biasRet);
		}
	    }
	}
	return retValue;
    }

    /**
     * Returns the next visual position for the cursor, in either the
     * east or west direction.
     *
     * @return next position west of the passed in position.
     */
    // PENDING: This only checks the next element. If one of the children
    // Views returns -1, it should continue checking all the children.
    // PENDING(sky): This name sucks! Come up with a better one!
    protected int getNextEastWestVisualPositionFrom(int pos, Position.Bias b,
						    Shape a,
						    int direction,
						    Position.Bias[] biasRet)
	                                        throws BadLocationException {
	boolean isEast = (direction == EAST);
	Rectangle alloc = getInsideAllocation(a);
	int retValue;
	int increment = (isEast) ? 1 : -1;
	if(pos == -1) {
	    View v = (isEast) ? getView(0) : getView(getViewCount() - 1);
	    childAllocation(0, alloc);
	    retValue = v.getNextVisualPositionFrom(pos, b, alloc,
						   direction, biasRet);
	    if(retValue == -1 && isEast && getViewCount() > 1) {
		// Special case that should ONLY happen if first view
		// isn't valid (can happen when end position is put at
		// beginning of line.
		v = getView(1);
		alloc = getInsideAllocation(a);
		childAllocation(1, alloc);
		retValue = v.getNextVisualPositionFrom(-1, biasRet[0], alloc,
						       direction, biasRet);
	    }
	}
	else {
	    int vIndex;
	    if(b == Position.Bias.Backward) {
		vIndex = getViewIndexAtPosition(Math.max(getStartOffset(),
							 pos - 1));
	    }
	    else {
		vIndex = getViewIndexAtPosition(pos);
	    }
	    View v = getView(vIndex);
	    childAllocation(vIndex, alloc);
	    retValue = v.getNextVisualPositionFrom(pos, b, alloc,
						   direction, biasRet);
	    if(retValue == -1) {
		if(flipEastAndWestAtEnds(pos, b)) {
		    increment *= -1;
		}
		vIndex += increment;
		if(vIndex >= 0 && vIndex < getViewCount()) {
		    v = getView(vIndex);
		    alloc = getInsideAllocation(a);
		    childAllocation(vIndex, alloc);
		    retValue = v.getNextVisualPositionFrom
			(-1, b, alloc, direction, biasRet);
		    // If there is a bias change, it is a fake position
		    // and we should skip it. This is usually the result
		    // of two elements side be side flowing the same way.
		    if(retValue == pos && retValue != -1 && biasRet[0] != b) {
			alloc = getInsideAllocation(a);
			childAllocation(vIndex, alloc);
			retValue = v.getNextVisualPositionFrom
			    (retValue, biasRet[0], alloc, direction,
			     biasRet);
		    }
		}
	    }
	    else {
		if(flipEastAndWestAtEnds(pos, b)) {
		    increment *= -1;
		}
		vIndex += increment;
		if(biasRet[0] != b &&
		    ((increment == 1 && v.getEndOffset() == retValue) ||
		     (increment == -1 && v.getStartOffset() == retValue)) &&
		   vIndex >= 0 && vIndex < getViewCount()) {
		    // Reached the end of a view, make sure the next view
		    // is a different direction.
		    v = getView(vIndex);
		    alloc = getInsideAllocation(a);
		    childAllocation(vIndex, alloc);
		    Position.Bias originalBias = biasRet[0];
		    int nextPos = v.getNextVisualPositionFrom
			(-1, b, alloc, direction, biasRet);
		    if(biasRet[0] == b) {
			retValue = nextPos;
		    }
		    else {
			biasRet[0] = originalBias;
		    }
		}
	    }
	}
	return retValue;
    }

    /**
     * Subclasses may wish to subclass this and conditionally return
     * true based on the position. A return value of true indicates that
     * when a View returns -1 from getNextVisualPositionFrom the next
     * view for east should be the current index offset by -1, and for
     * west it means offset by 1. The normal direction (for left to
     * right text) is to offset east by 1 and west by -1.
     *
     * @return false
     */
    protected boolean flipEastAndWestAtEnds(int position,
					    Position.Bias bias) {
	return false;
    }


    // ---- member variables ---------------------------------------------

    private static View[] ONE = new View[1];
    private static View[] ZERO = new View[0];
    
    private View[] children;
    private int nchildren;
    private short left;
    private short right;
    private short top;
    private short bottom;
}
