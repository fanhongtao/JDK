/*
 * @(#)CompositeView.java	1.46 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.util.Vector;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.SwingConstants;

/**
 * A view that is composed of other views (has children).
 * As a container of children, the composite view needs
 * to provide:
 *
 * <dl>
 * <dt><b>services to manage the collection of children</b>
 * <dd>The following methods can be used to manage the
 * collection.
 *   <ul>
 *   <li><a href="#removeAll">removeAll</a>
 *   <li><a href="#insert">insert</a>
 *   <li><a href="#append">append</a>
 *   <li><a href="#replace">replace</a>
 *   <li><a href="#getViewCount">getViewCount</a>
 *   <li><a href="#getView">getView</a>
 *   <li><a href="#loadChildren">loadChildren</a>
 *   </ul>
 *
 * <dt><b>layout of the children</b>
 * <dd>This class does not implement a layout policy
 * as it is abstract.  A subclass will determine how
 * the children are laid out by implementing the
 * <a href="View#setSize">setSize</a> method to position
 * the children when the size has been changed.
 *
 * <dt><b>paint the children</b>
 * <dd>This class does not attempt to paint the
 * children.  Subclasses will want to use the 
 * layout information and call paint on the children
 * that are visible (intersect the clipping region)
 * with the Shape argument set to the location of the
 * child view.
 *
 * <dt><b>propagation of 
 * <a href="javax.swing.event.DocumentEvent">DocumentEvent</a> 
 * information to the appropriate children.</b>
 *
 * <dt>propagation of model/view translation to the
 * proper child.
 * </dl>
 *
 * @author  Timothy Prinzing
 * @version 1.46 11/29/01
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
	childAlloc = new Rectangle();
    }

    /**
     * Loads all of the children to initialize the view.
     * This is called by the <a href="#setParent">setParent</a> 
     * method.  Subclasses can reimplement this to initialize 
     * their child views in a different manner.  The default
     * implementation creates a child view for each 
     * child element.
     *
     * @param f the view factory
     * @see #setParent
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
     * Removes one of the children at the given position.
     */
    /*public*/ void remove(int i) {
	replace(i, 1, ZERO);
    }

    /**
     * Inserts a single child view.  This is a convenience 
     * call to replace.
     *
     * @param offs the offset of the view to insert before >= 0
     * @param v the view
     * @see #replace
     */
    public void insert(int offs, View v) {
	View[] one = new View[1];
	one[0] = v;
	replace(offs, 0, one);
    }

    /**
     * Appends a single child view.  This is a convenience 
     * call to replace.
     *
     * @param v the view
     * @see #replace
     */
    public void append(View v) {
	View[] one = new View[1];
	one[0] = v;
	replace(nchildren, 0, one);
    }

    /**
     * Replace child views.  If there are no views to remove
     * this acts as an insert.  If there are no views to
     * add this acts as a remove.  Views being removed will
     * have the parent set to null, and the internal reference
     * to them removed so that they can be garbage collected.
     *
     * @param index the starting index into the child views to insert
     *   the new views >= 0
     * @param length the number of existing child views to remove >= 0
     * @param views the child views to add
     */
    public void replace(int offset, int length, View[] views) {
	// update parent reference on removed views
	for (int i = offset; i < offset + length; i++) {
	    children[i].setParent(null);
	    children[i] = null;
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

    /**
     * Updates the child views in response to receiving notification
     * that the model changed, and there is change record for the 
     * element this view is responsible for.  This is implemented
     * to assume the child views are directly responsible for the
     * child elements of the element this view represents.  The
     * ViewFactory is used to create child views for each element
     * specified as added in the ElementChange, starting at the
     * index specified in the given ElementChange.  The number of
     * child views representing the removed elements specified are 
     * removed.
     * 
     * @param ec The change information for the element this view
     *  is responsible for.  This should not be null if this method
     *  gets called.
     * @param e the change information from the associated document
     * @param f the factory to use to build child views
     * @return whether or not the child views represent the
     *  child elements of the element this view is responsible
     *  for.  Some views create children that represent a portion 
     *  of the element they are responsible for, and should return
     *  false.  This information is used to determine if views 
     *  in the range of the added elements should be forwarded to
     *  or not.
     * @see #insertUpdate
     * @see #removeUpdate
     * @see #changedUpdate     
     */
    /*protected*/ boolean updateChildren(DocumentEvent.ElementChange ec, 
					 DocumentEvent e, ViewFactory f) {
	// the structure of this element changed.
	Element[] removedElems = ec.getChildrenRemoved();
	Element[] addedElems = ec.getChildrenAdded();
	View[] added = new View[addedElems.length];
	for (int i = 0; i < addedElems.length; i++) {
	    added[i] = f.create(addedElems[i]);
	}
	int index = ec.getIndex();
	replace(index, removedElems.length, added);
	return true;
    }

    /**
     * Forward the given DocumentEvent to the child views
     * that need to be notified of the change to the model.
     * If there were changes to the element this view is 
     * responsible for, that should be considered when 
     * forwarding (i.e. new child views should not get
     * notified).
     *
     * @param ec changes to the element this view is responsible
     *  for (may be null if there were no changes).
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see #insertUpdate
     * @see #removeUpdate
     * @see #changedUpdate     
     */
    /*protected*/ void forwardUpdate(DocumentEvent.ElementChange ec, 
				      DocumentEvent e, Shape a, ViewFactory f) {
	Element elem = getElement();
	int pos = e.getOffset();
	int index0 = getViewIndexAtPosition(pos);
	int index1 = index0;
	View v = (index0 >= 0) ? getView(index0) : null;
	if (v != null) {
	    if ((v.getStartOffset() == pos) && (pos > 0)) {
		// If v is at a boundry, forward the event to the previous
		// view too.
		index0 = Math.max(index0 - 1, 0);
	    }
	}
	if (e.getType() != DocumentEvent.EventType.REMOVE) {
	    index1 = getViewIndexAtPosition(pos + e.getLength());
	    if (index1 < 0) {
		index1 = getViewCount() - 1;
	    }
	}
	int hole0 = index1 + 1;
	int hole1 = hole0;
	Element[] addedElems = (ec != null) ? ec.getChildrenAdded() : null;
	if ((addedElems != null) && (addedElems.length > 0)) {
	    hole0 = ec.getIndex();
	    hole1 = hole0 + addedElems.length - 1;
	}

	// forward to any view not in the forwarding hole 
	// formed by added elements (i.e. they will be updated
	// by initialization.
	for (int i = index0; i <= index1; i++) {
	    if (! ((i >= hole0) && (i <= hole1))) {
		v = getView(i);
		if (v != null) {
		    Shape childAlloc = getChildAllocation(i, a);
		    forwardUpdateToView(v, e, childAlloc, f);
		}
	    }
	}
    }

    /**
     * Forward the DocumentEvent to the give child view.  This
     * simply messages the view with a call to insertUpdate, 
     * removeUpdate, or changedUpdate depending upon the type
     * of the event.  This is called by
     * <a href="#forwardUpdate">forwardUpdate</a> to forward 
     * the event to children that need it.
     *
     * @param v the child view to forward the event to.
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see #forwardUpdate
     */
    /*protected*/ void forwardUpdateToView(View v, DocumentEvent e, 
					   Shape a, ViewFactory f) {
	DocumentEvent.EventType type = e.getType();
	if (type == DocumentEvent.EventType.INSERT) {
	    v.insertUpdate(e, a, f);
	} else if (type == DocumentEvent.EventType.REMOVE) {
	    v.removeUpdate(e, a, f);
	} else {
	    v.changedUpdate(e, a, f);
	}
    }

    /**
     * Update the layout in response to receiving notification of
     * change from the model.  This is implemented to call preferenceChanged
     * to reschedule a new layout if the ElementChange record is not null.
     *
     * @param ec changes to the element this view is responsible
     *  for (may be null if there were no changes).
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see #insertUpdate
     * @see #removeUpdate
     * @see #changedUpdate     
     */
    /*protected*/ void updateLayout(DocumentEvent.ElementChange ec, 
				    DocumentEvent e, Shape a) {
	if ((ec != null) && (a != null)) {
	    // should damage more intelligently
	    preferenceChanged(null, true, true);
	    getContainer().repaint();
	}
    }

    // --- View methods ---------------------------------------------

    /**
     * Sets the parent of the view.
     * This is reimplemented to provide the superclass
     * behavior as well as calling the <code>loadChildren</code>
     * method if this view does not already have children.  
     * The children should not be loaded in the 
     * constructor because the act of setting the parent
     * may cause them to try to search up the hierarchy
     * (to get the hosting Container for example).
     * If this view has children (the view is being moved
     * from one place in the view hierarchy to another), 
     * the <code>loadChildren</code> method will not be called.
     *
     * @param parent the parent of the view, null if none
     */
    public void setParent(View parent) {
	super.setParent(parent);
	if ((parent != null) && (nchildren == 0)) {
	    ViewFactory f = getViewFactory();
	    loadChildren(f);
	}
    }

    /** 
     * Returns the number of child views of this view.
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
     * are located.
     *
     * @param index the index of the child, >= 0 && < getViewCount()
     * @param a  the allocation to this view.
     * @return the allocation to the child
     */
    public Shape getChildAllocation(int index, Shape a) {
	Rectangle alloc = getInsideAllocation(a);
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
		Shape retShape = v.modelToView(pos, getChildAllocation(vIndex, a), b);
		if(retShape == null && v.getEndOffset() == pos) {
		    if(++vIndex < getViewCount()) {
			v = getView(vIndex);
			retShape = v.modelToView(pos, getChildAllocation(vIndex, a), b);
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
	View v0 = getViewAtPosition((b0 == Position.Bias.Backward) ?
				    Math.max(0, p0 - 1) : p0, r0);
	Rectangle r1 = new Rectangle(alloc);
	View v1 = getViewAtPosition((b1 == Position.Bias.Backward) ?
				    Math.max(0, p1 - 1) : p1, r1);
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

    /**
     * Gives notification that something was inserted into 
     * the document in a location that this view is responsible for.  
     * To reduce the burden to subclasses, this functionality is
     * spread out into the following calls that subclasses can
     * reimplement:
     * <ol>
     * <li><a href="#updateChildren">updateChildren</a> is called
     * if there were any changes to the element this view is
     * responsible for.  If this view has child views that are
     * represent the child elements, then this method should do
     * whatever is necessary to make sure the child views correctly
     * represent the model.
     * <li><a href="#forwardUpdate">forwardUpdate</a> is called
     * to forward the DocumentEvent to the appropriate child views.
     * <li><a href="#updateLayout">updateLayout</a> is called to
     * give the view a chance to either repair it's layout, to reschedule
     * layout, or do nothing.
     * </ol>
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#insertUpdate
     */
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	Element elem = getElement();
	DocumentEvent.ElementChange ec = e.getChange(elem);
	if (ec != null) {
	    if (! updateChildren(ec, e, f)) {
		// don't consider the element changes they
		// are for a view further down.
		ec = null;
	    }
	}
	forwardUpdate(ec, e, a, f);
	updateLayout(ec, e, a);
    }

    /**
     * Gives notification that something was removed from the document
     * in a location that this view is responsible for.
     * To reduce the burden to subclasses, this functionality is
     * spread out into the following calls that subclasses can
     * reimplement:
     * <ol>
     * <li><a href="#updateChildren">updateChildren</a> is called
     * if there were any changes to the element this view is
     * responsible for.  If this view has child views that are
     * represent the child elements, then this method should do
     * whatever is necessary to make sure the child views correctly
     * represent the model.
     * <li><a href="#forwardUpdate">forwardUpdate</a> is called
     * to forward the DocumentEvent to the appropriate child views.
     * <li><a href="#updateLayout">updateLayout</a> is called to
     * give the view a chance to either repair it's layout, to reschedule
     * layout, or do nothing.
     * </ol>
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#removeUpdate
     */
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	Element elem = getElement();
	DocumentEvent.ElementChange ec = e.getChange(elem);
	if (ec != null) {
	    if (! updateChildren(ec, e, f)) {
		// don't consider the element changes they
		// are for a view further down.
		ec = null;
	    }
	}
	forwardUpdate(ec, e, a, f);
	updateLayout(ec, e, a);
    }

    /**
     * Gives notification from the document that attributes were changed
     * in a location that this view is responsible for.
     * To reduce the burden to subclasses, this functionality is
     * spread out into the following calls that subclasses can
     * reimplement:
     * <ol>
     * <li><a href="#updateChildren">updateChildren</a> is called
     * if there were any changes to the element this view is
     * responsible for.  If this view has child views that are
     * represent the child elements, then this method should do
     * whatever is necessary to make sure the child views correctly
     * represent the model.
     * <li><a href="#forwardUpdate">forwardUpdate</a> is called
     * to forward the DocumentEvent to the appropriate child views.
     * <li><a href="#updateLayout">updateLayout</a> is called to
     * give the view a chance to either repair it's layout, to reschedule
     * layout, or do nothing.
     * </ol>
     *
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#changedUpdate
     */
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	Element elem = getElement();
	DocumentEvent.ElementChange ec = e.getChange(elem);
	if (ec != null) {
	    if (! updateChildren(ec, e, f)) {
		// don't consider the element changes they
		// are for a view further down.
		ec = null;
	    }
	}
	forwardUpdate(ec, e, a, f);
	updateLayout(ec, e, a);
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
     *   and the allocation of the child view at the index on exit.
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
     * Translates the immutable allocation given to the view 
     * to a mutable allocation that represents the interior
     * allocation (i.e. the bounds of the given allocation
     * with the top, left, bottom, and right insets removed.
     * It is expected that the returned value would be further
     * mutated to represent an allocation to a child view. 
     * This is implemented to reuse an instance variable so
     * it avoids creating excessive Rectangles.  Typically
     * the result of calling this method would be fed to
     * the childAllocation method.
     *
     * @param a The allocation given to the view.
     * @returns The allocation that represents the inside of the 
     *   view after the margins have all been removed.  If the
     *   given allocation was null, the return value is null.
     */
    protected Rectangle getInsideAllocation(Shape a) {
	if (a != null) {
	    // get the bounds, hopefully without allocating
	    // a new rectangle.  The Shape argument should 
	    // not be modified... we copy it into the
	    // child allocation.
	    Rectangle alloc;
	    if (a instanceof Rectangle) {
		alloc = (Rectangle) a;
	    } else {
		alloc = a.getBounds();
	    }

	    childAlloc.setBounds(alloc);
	    childAlloc.x += left;
	    childAlloc.y += top;
	    childAlloc.width -= left + right;
	    childAlloc.height -= top + bottom;
	    return childAlloc;
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

    private static View[] ZERO = new View[0];
    
    private View[] children;
    private int nchildren;
    private short left;
    private short right;
    private short top;
    private short bottom;
    private Rectangle childAlloc;
}
