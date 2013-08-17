/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.io.PrintStream;
import java.util.Vector;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.SizeRequirements;

/**
 * A view that arranges its children into a box 
 * shape by tiling it's children along an axis.
 * The box is somewhat like that found in TeX where
 * there is alignment of the children, flexibility of the
 * children is considered, etc.  This is considered an
 * useful building block that might be useful to represent 
 * things like a collection of lines, paragraphs, lists,
 * columns, pages, etc.
 *
 * @author  Timothy Prinzing
 * @version 1.45 02/06/02
 */
public class BoxView extends CompositeView {

    /**
     * Constructs a BoxView.
     *
     * @param elem the element this view is responsible for
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public BoxView(Element elem, int axis) {
	super(elem);
	tempRect = new Rectangle();
	this.axis = axis;
	xOffsets = new int[0];
	xSpans = new int[0];
	xValid = false;
	xAllocValid = false;
	yOffsets = new int[0];
	ySpans = new int[0];
	yValid = false;
	yAllocValid = false;
    }

    /**
     * Fetch the axis property.
     *
     * @return the major axis of the box, either 
     *  View.X_AXIS or View.Y_AXIS.
     */
    public int getAxis() {
	return axis;
    }

    /**
     * Set the axis property.
     *
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public void setAxis(int axis) {
	this.axis = axis;
	preferenceChanged(null, true, true);
    }

    /**
     * Invalidate the layout along an axis.  This happens
     * automatically if the preferences have changed for
     * any of the child views.  In some cases the layout
     * may need to be recalculated when the preferences
     * have not changed.  The layout can be marked as
     * invalid by calling this method.  The layout will
     * be updated the next time the setSize method is called
     * on this view (typically in paint).
     *
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public void layoutChanged(int axis) {
 	if (axis == X_AXIS) {
 	    xAllocValid = false;
 	} else {
 	    yAllocValid = false;
 	}
    }

    /**
     * Paints a child.  By default
     * that is all it does, but a subclass can use this to paint 
     * things relative to the child.
     *
     * @param g the graphics context
     * @param alloc the allocated region to paint into
     * @param index the child index, >= 0 && < getViewCount()
     */
    protected void paintChild(Graphics g, Rectangle alloc, int index) {
	View child = getView(index);
	child.paint(g, alloc);
    }

    /**
     * Invalidates the layout and resizes the cache of 
     * requests/allocations.  The child allocations can still
     * be accessed for the old layout, but the new children
     * will have an offset and span of 0.
     *
     * @param index the starting index into the child views to insert
     *   the new views.  This should be a value >= 0 and <= getViewCount.
     * @param length the number of existing child views to remove.
     *   This should be a value >= 0 and <= (getViewCount() - offset).
     * @param views the child views to add.  This value can be null
     *   to indicate no children are being added (useful to remove).
     */
    public void replace(int index, int length, View[] elems) {
	super.replace(index, length, elems);

	// invalidate cache 
	int nInserted = (elems != null) ? elems.length : 0;
	xOffsets = updateLayoutArray(xOffsets, index, nInserted);
	xSpans = updateLayoutArray(xSpans, index, nInserted);
	xValid = false;
	xAllocValid = false;
	yOffsets = updateLayoutArray(yOffsets, index, nInserted);
	ySpans = updateLayoutArray(ySpans, index, nInserted);
	yValid = false;
	yAllocValid = false;
    }

    /**
     * Resize the given layout array to match the new number of
     * child views.  The current number of child views are used to
     * produce the new array.  The contents of the old array are
     * inserted into the new array at the appropriate places so that
     * the old layout information is transferred to the new array.
     */
    int[] updateLayoutArray(int[] oldArray, int offset, int nInserted) {
	int n = getViewCount();
	int[] newArray = new int[n];

	System.arraycopy(oldArray, 0, newArray, 0, offset);
	System.arraycopy(oldArray, offset, 
			 newArray, offset + nInserted, n - nInserted - offset);
	return newArray;
    }

    /**
     * Forward the given DocumentEvent to the child views
     * that need to be notified of the change to the model.
     * If a child changed it's requirements and the allocation
     * was valid prior to forwarding the portion of the box
     * from the starting child to the end of the box will
     * be repainted.
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
    protected void forwardUpdate(DocumentEvent.ElementChange ec, 
				     DocumentEvent e, Shape a, ViewFactory f) {
	boolean wasValid = isAllocationValid();
	super.forwardUpdate(ec, e, a, f);

	// determine if a repaint is needed
	if (wasValid && (! isAllocationValid())) {
	    // repaint is needed, if there is a hosting component and
	    // and an allocated shape.
	    Component c = getContainer();
	    if ((a != null) && (c != null)) {
		int pos = e.getOffset();
		int index = getViewIndexAtPosition(pos);
		Rectangle alloc = getInsideAllocation(a);
		if (axis == X_AXIS) {
		    alloc.x += xOffsets[index];
		    alloc.width -= xSpans[index];
		} else {
		    alloc.y += yOffsets[index];
		    alloc.height -= ySpans[index];
		}
		c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
	    }
	}
    }

    // --- View methods ---------------------------------------------

    /**
     * This is called by a child to indicated its 
     * preferred span has changed.  This is implemented to
     * throw away cached layout information so that new
     * calculations will be done the next time the children
     * need an allocation.
     *
     * @param child the child view
     * @param width true if the width preference should change
     * @param height true if the height preference should change
     */
    public void preferenceChanged(View child, boolean width, boolean height) {
	if (width) {
	    xValid = false;
	    xAllocValid = false;
	}
	if (height) {
	    yValid = false;
	    yAllocValid = false;
	}
	super.preferenceChanged(child, width, height);
    }

    /**
     * Gets the resize weight.  A value of 0 or less is not resizable.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @return the weight
     * @exception IllegalArgumentException for an invalid axis
     */
    public int getResizeWeight(int axis) {
	checkRequests();
        switch (axis) {
        case View.X_AXIS:
	    if ((xRequest.preferred != xRequest.minimum) &&
		(xRequest.preferred != xRequest.maximum)) {
		return 1;
	    }
	    return 0;
        case View.Y_AXIS:
	    if ((yRequest.preferred != yRequest.minimum) &&
		(yRequest.preferred != yRequest.maximum)) {
		return 1;
	    }
            return 0;
        default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    /**
     * Sets the size of the view.  If the size has changed, layout
     * is redone.  The size is the full size of the view including
     * the inset areas.
     *
     * @param width the width >= 0
     * @param height the height >= 0
     */
    public void setSize(float width, float height) {
	if (((int) width) != this.width) {
	    xAllocValid = false;
	}
	if (((int) height) != this.height) { 
	    yAllocValid = false;
	}
	if ((! xAllocValid) || (! yAllocValid)) {
	    this.width = (int) width;
	    this.height = (int) height;
	    layout(this.width - getLeftInset() - getRightInset(), 
		   this.height - getTopInset() - getBottomInset());
	}
    }

    /**
     * Renders using the given rendering surface and area 
     * on that surface.  Only the children that intersect
     * the clip bounds of the given Graphics will be
     * rendered.
     *
     * @param g the rendering surface to use
     * @param allocation the allocated region to render into
     * @see View#paint
     */
    public void paint(Graphics g, Shape allocation) {
	Rectangle alloc = (allocation instanceof Rectangle) ?
	                   (Rectangle)allocation : allocation.getBounds();
	setSize(alloc.width, alloc.height);
	int n = getViewCount();
	int x = alloc.x + getLeftInset();
	int y = alloc.y + getTopInset();
	Rectangle clip = g.getClipBounds();
	for (int i = 0; i < n; i++) {
	    tempRect.x = x + xOffsets[i];
	    tempRect.y = y + yOffsets[i];
	    tempRect.width = xSpans[i];
	    tempRect.height = ySpans[i];
	    if (tempRect.intersects(clip)) {
		paintChild(g, tempRect, i);
	    }
	}
    }

    /**
     * Fetches the allocation for the given child view. 
     * This enables finding out where various views
     * are located.  This is implemented to return null
     * if the layout is invalid, otherwise the
     * superclass behavior is executed.
     *
     * @param index the index of the child, >= 0 && < getViewCount()
     * @param a  the allocation to this view.
     * @return the allocation to the child
     */
    public Shape getChildAllocation(int index, Shape a) {
	if (a != null) {
	    Shape ca = super.getChildAllocation(index, a);
	    if ((ca != null) && (! isAllocationValid())) {
		// The child allocation may not have been set yet.
		Rectangle r = (ca instanceof Rectangle) ? 
		    (Rectangle) ca : ca.getBounds();
		if ((r.width == 0) && (r.height == 0)) {
		    return null;
		}
	    }
	    return ca;
	}
	return null;
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.  This makes
     * sure the allocation is valid before letting the superclass
     * do its thing.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does
     *  not represent a valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	if (! isAllocationValid()) {
	    Rectangle alloc = a.getBounds();
	    setSize(alloc.width, alloc.height);
	}
	return super.modelToView(pos, a, b);
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
	if (! isAllocationValid()) {
	    Rectangle alloc = a.getBounds();
	    setSize(alloc.width, alloc.height);
	}
	return super.viewToModel(x, y, a, bias);
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the total alignment
     * needed to position the children with the alignment points
     * lined up along the axis orthoginal to the axis that is
     * being tiled.  The axis being tiled will request to be
     * centered (i.e. 0.5f).
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the desired alignment >= 0.0f && <= 1.0f.  This should
     *   be a value between 0.0 and 1.0 where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     * @exception IllegalArgumentException for an invalid axis
     */
    public float getAlignment(int axis) {
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return xRequest.alignment;
	case View.Y_AXIS:
	    return yRequest.alignment;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the preferred span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis type
     */
    public float getPreferredSpan(int axis) {
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return ((float)xRequest.preferred) + getLeftInset() + getRightInset();
	case View.Y_AXIS:
	    return ((float)yRequest.preferred) + getTopInset() + getBottomInset();
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the minimum span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis type
     */
    public float getMinimumSpan(int axis) {
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return ((float)xRequest.minimum) + getLeftInset() + getRightInset();
	case View.Y_AXIS:
	    return ((float)yRequest.minimum) + getTopInset() + getBottomInset();
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the maximum span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis type
     */
    public float getMaximumSpan(int axis) {
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return ((float)xRequest.maximum) + getLeftInset() + getRightInset();
	case View.Y_AXIS:
	    return ((float)yRequest.maximum) + getTopInset() + getBottomInset();
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    // --- local methods ----------------------------------------------------

    /**
     * Are the allocations for the children still
     * valid?
     *
     * @return true if allocations still valid
     */
    protected boolean isAllocationValid() {
	return (xAllocValid && yAllocValid);
    }
   
    /**
     * Determines if a point falls before an allocated region.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param innerAlloc the allocated region.  This is the area
     *   inside of the insets.
     * @return true if the point lies before the region else false
     */
    protected boolean isBefore(int x, int y, Rectangle innerAlloc) {
	if (axis == View.X_AXIS) {
	    return (x < innerAlloc.x);
	} else {
	    return (y < innerAlloc.y);
	}
    }

    /**
     * Determines if a point falls after an allocated region.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param innerAlloc the allocated region.  This is the area
     *   inside of the insets.
     * @return true if the point lies after the region else false
     */
    protected boolean isAfter(int x, int y, Rectangle innerAlloc) {
	if (axis == View.X_AXIS) {
	    return (x > (innerAlloc.width + innerAlloc.x));
	} else {
	    return (y > (innerAlloc.height + innerAlloc.y));
	}
    }

    /**
     * Fetches the child view at the given point.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param alloc the parents inner allocation on entry, which should
     *   be changed to the childs allocation on exit.
     * @return the view
     */
    protected View getViewAtPoint(int x, int y, Rectangle alloc) {
	int n = getViewCount();
	if (axis == View.X_AXIS) {
	    if (x < (alloc.x + xOffsets[0])) {
		childAllocation(0, alloc);
		return getView(0);
	    }
	    for (int i = 0; i < n; i++) {
		if (x < (alloc.x + xOffsets[i])) {
		    childAllocation(i - 1, alloc);
		    return getView(i - 1);
		}
	    }
	    childAllocation(n - 1, alloc);
	    return getView(n - 1);
	} else {
	    if (y < (alloc.y + yOffsets[0])) {
		childAllocation(0, alloc);
		return getView(0);
	    }
	    for (int i = 0; i < n; i++) {
		if (y < (alloc.y + yOffsets[i])) {
		    childAllocation(i - 1, alloc);
		    return getView(i - 1);
		}
	    }
	    childAllocation(n - 1, alloc);
	    return getView(n - 1);
	}
    }

    /**
     * Allocates a region for a child view.  
     *
     * @param index the index of the child view to
     *   allocate, >= 0 && < getViewCount()
     * @param alloc the allocated region
     */
    protected void childAllocation(int index, Rectangle alloc) {
	alloc.x += xOffsets[index];
	alloc.y += yOffsets[index];
	alloc.width = xSpans[index];
	alloc.height = ySpans[index];
    }

    /**
     * Performs layout of the children.  The size is the
     * area inside of the insets.  This method calls
     * the methods 
     * <a href="#layoutMajorAxis">layoutMajorAxis</a> and
     * <a href="#layoutMinorAxis">layoutMinorAxis</a> as
     * needed.  To change how layout is done those methods
     * should be reimplemented.
     *
     * @param width the width >= 0
     * @param height the height >= 0
     */
    protected void layout(int width, int height) {
	checkRequests();

	if (axis == X_AXIS) {
	    if (! xAllocValid) {
		layoutMajorAxis(width, X_AXIS, xOffsets, xSpans);
	    }
	    if (! yAllocValid) {
		layoutMinorAxis(height, Y_AXIS, yOffsets, ySpans);
	    }
	} else {
	    if (! xAllocValid) {
		layoutMinorAxis(width, X_AXIS, xOffsets, xSpans);
	    }
	    if (! yAllocValid) {
		layoutMajorAxis(height, Y_AXIS, yOffsets, ySpans);
	    }
	}
	xAllocValid = true;
	yAllocValid = true;

	// flush changes to the children
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    v.setSize((float) xSpans[i], (float) ySpans[i]);
	}
    }

    /**
     * The current width of the box.  This is the width that
     * it was last allocated.
     */
    public int getWidth() {
	return width;
    }

    /**
     * The current height of the box.  This is the height that
     * it was last allocated.
     */
    public int getHeight() {
	return height;
    }

    /**
     * Perform layout for the major axis of the box (i.e. the
     * axis that it represents).  The results of the layout should
     * be placed in the given arrays which represent the allocations
     * to the children along the major axis.
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
	/*
	 * first pass, calculate the preferred sizes
	 * and the flexibility to adjust the sizes.
	 */
	long minimum = 0;
	long maximum = 0;
	long preferred = 0;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    spans[i] = (int) v.getPreferredSpan(axis);
	    preferred += spans[i];
	    minimum += v.getMinimumSpan(axis);
	    maximum += v.getMaximumSpan(axis);
	}

	/*
	 * Second pass, expand or contract by as much as possible to reach
	 * the target span.  
	 */

	// determine the adjustment to be made
	long desiredAdjustment = targetSpan - preferred;
	float adjustmentFactor = 0.0f;
	if (desiredAdjustment != 0) {
	    float maximumAdjustment = (desiredAdjustment > 0) ? 
		maximum - preferred : preferred - minimum;
            if (maximumAdjustment == 0.0f) {
                adjustmentFactor = 0.0f;
            }
            else {
                adjustmentFactor = desiredAdjustment / maximumAdjustment;
                adjustmentFactor = Math.min(adjustmentFactor, 1.0f);
                adjustmentFactor = Math.max(adjustmentFactor, -1.0f);
            }
	}

	// make the adjustments
	int totalOffset = 0;
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    offsets[i] = totalOffset;
	    int availableSpan = (adjustmentFactor > 0.0f) ? 
		(int) v.getMaximumSpan(axis) - spans[i] : 
		spans[i] - (int) v.getMinimumSpan(axis);
            float adjF = adjustmentFactor * availableSpan;
            if (adjF < 0) {
                adjF -= .5f;
            }
            else {
                adjF += .5f;
            }
	    int adj = (int)adjF;
	    spans[i] += adj;
	    totalOffset = (int) Math.min((long) totalOffset + (long) spans[i], Integer.MAX_VALUE);
	}
    }

    /**
     * Perform layout for the minor axis of the box (i.e. the
     * axis orthoginal to the axis that it represents).  The results 
     * of the layout should be placed in the given arrays which represent 
     * the allocations to the children along the minor axis.
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
    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    int min = (int) v.getMinimumSpan(axis);
	    int max = (int) v.getMaximumSpan(axis);
	    if (max < targetSpan) {
		// can't make the child this wide, align it
		float align = v.getAlignment(axis);
		offsets[i] = (int) ((targetSpan - max) * align);
		spans[i] = max;
	    } else {
		// make it the target width, or as small as it can get.
		offsets[i] = 0;
		spans[i] = Math.max(min, targetSpan);
	    }
	}
    }

    protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
	// calculate tiled request
	float min = 0;
	float pref = 0;
	float max = 0;

	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    min += v.getMinimumSpan(axis);
	    pref += v.getPreferredSpan(axis);
	    max += v.getMaximumSpan(axis);
	}

	if (r == null) {
	    r = new SizeRequirements();
	}
	r.alignment = 0.5f;
	r.minimum = (int) min;
	r.preferred = (int) pref;
	r.maximum = (int) max;
	return r;
    }

    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	int min = 0;
	long pref = 0;
	int max = Integer.MAX_VALUE;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    min = Math.max((int) v.getMinimumSpan(axis), min);
	    pref = Math.max((int) v.getPreferredSpan(axis), pref);
	    max = Math.max((int) v.getMaximumSpan(axis), max);
	}

	if (r == null) {
	    r = new SizeRequirements();
	    r.alignment = 0.5f;
	}
	r.preferred = (int) pref;
	r.minimum = min;
	r.maximum = max;
	return r;
    }

    /**
     * Checks the request cache and update if needed.
     */
    void checkRequests() {
	if (axis == X_AXIS) {
	    if (! xValid) {
		xRequest = calculateMajorAxisRequirements(X_AXIS, xRequest);
	    }
	    if (! yValid) {
		yRequest = calculateMinorAxisRequirements(Y_AXIS, yRequest);
	    }
	} else {
	    if (! xValid) {
		xRequest = calculateMinorAxisRequirements(X_AXIS, xRequest);
	    }
	    if (! yValid) {
		yRequest = calculateMajorAxisRequirements(Y_AXIS, yRequest);
	    }
	}
	yValid = true;
	xValid = true;
    }

    protected void baselineLayout(int targetSpan, int axis, int[] offsets, int[] spans) {
	int totalBelow = (int) (targetSpan * getAlignment(axis));
	int totalAbove = targetSpan - totalBelow;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    float align = v.getAlignment(axis);
	    int span = (int) v.getPreferredSpan(axis);
	    int below = (int) (span * align);
	    int above = span - below;
	    if (span > targetSpan) {
		// check compress
		if ((int) v.getMinimumSpan(axis) < span) {
		    below = totalBelow;
		    above = totalAbove;
		} else {
		    if ((v.getResizeWeight(axis) > 0) && (v.getMaximumSpan(axis) != span)) {
			throw new Error("should not happen: " + v.getClass());
		    }
		}
	    } else if (span < targetSpan) { 
		// check expand
		if ((int) v.getMaximumSpan(axis) > span) {
		    below = totalBelow;
		    above = totalAbove;
		}
	    }
/*
	    if (v.getResizeWeight(axis) > 0) {
		below = totalBelow;
		above = totalAbove;
	    }
	    */
	    offsets[i] = totalBelow - below;
	    spans[i] = below + above;
	}
    }

    protected SizeRequirements baselineRequirements(int axis, SizeRequirements r) {
	int totalAbove = 0;
	int totalBelow = 0;
	int resizeWeight = 0;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    int span = (int) v.getPreferredSpan(axis);
	    int below = (int) (v.getAlignment(axis) * span);
	    int above = span - below;
	    totalAbove = Math.max(above, totalAbove);
	    totalBelow = Math.max(below, totalBelow);
	    resizeWeight += v.getResizeWeight(axis);
	}

	if (r == null) {
	    r = new SizeRequirements();
	}
	r.preferred = totalAbove + totalBelow;
	if (resizeWeight != 0) {
	    r.maximum = Integer.MAX_VALUE;
	    r.minimum = 0;
	} else {
	    r.maximum = r.preferred;
	    r.minimum = r.preferred;
	}
	if (r.preferred > 0) {
	    r.alignment = (float) totalBelow / r.preferred;
	} else {
	    r.alignment = 0.5f;
	}
	return r;
    }

    /**
     * Fetch the offset of a particular childs current layout
     */
    protected int getOffset(int axis, int childIndex) {
	int[] offsets = (axis == X_AXIS) ? xOffsets : yOffsets;
	return offsets[childIndex];
    }

    /**
     * Fetch the span of a particular childs current layout
     */
    protected int getSpan(int axis, int childIndex) {
	int[] spans = (axis == X_AXIS) ? xSpans : ySpans;
	return spans[childIndex];
    }

    protected boolean flipEastAndWestAtEnds(int position,
					    Position.Bias bias) {
	if(axis == Y_AXIS) {
	    int testPos = (bias == Position.Bias.Backward) ?
		          Math.max(0, position - 1) : position;
	    int index = getViewIndexAtPosition(testPos);
	    if(index != -1) {
		View v = getView(index);
		if(v != null && v instanceof CompositeView) {
		    return ((CompositeView)v).flipEastAndWestAtEnds(position,
								    bias);
		}
	    }
	}
	return false;
    }

    // --- variables ------------------------------------------------

    int axis;
    int width;
    int height;

    /*
     * Request cache
     */
    boolean xValid;
    boolean yValid;
    SizeRequirements xRequest;
    SizeRequirements yRequest;

    /*
     * Allocation cache
     */
    boolean xAllocValid;
    int[] xOffsets;
    int[] xSpans;
    boolean yAllocValid;
    int[] yOffsets;
    int[] ySpans;

    /** used in paint. */
    Rectangle tempRect;
}
