/*
 * @(#)ParagraphView.java	1.61 98/09/17
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
import java.util.Properties;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.SizeRequirements;
import javax.swing.SwingConstants;

/**
 * View of a simple line-wrapping paragraph that supports
 * multiple fonts, colors, components, icons, etc.  It is
 * basically a vertical box with a margin around it.  The 
 * contents of the box are a bunch of rows which are special 
 * horizontal boxes.  This view creates a collection of
 * views that represent the child elements of the paragraph 
 * element.  Each of these views are placed into a row 
 * directly if they will fit, otherwise the <code>breakView</code>
 * method is called to try and carve the view into pieces
 * that fit.
 *
 * @author  Timothy Prinzing
 * @author  Scott Violet
 * @version 1.61 09/17/98
 * @see     View
 */
public class ParagraphView extends BoxView implements TabExpander {

    /**
     * Constructs a ParagraphView for the given element.
     *
     * @param elem the element that this view is responsible for
     */
    public ParagraphView(Element elem) {
	super(elem, View.Y_AXIS);
	layoutSpan = -1;
	setPropertiesFromAttributes();
    }

    /**
     * Set the type of justification.
     */
    protected void setJustification(int j) {
	justification = j;
    }

    /**
     * Set the line spacing.
     *
     * @param ls the value in points
     */
    protected void setLineSpacing(float ls) {
	lineSpacing = ls;
    }

    /**
     * Set the indent on the first line
     *
     * @param ls the value in points
     */
    protected void setFirstLineIndent(float fi) {
	firstLineIndent = (int) fi;
    }

    protected void setPropertiesFromAttributes() {
	AttributeSet attr = getAttributes();
	if (attr != null) {
	    setParagraphInsets(attr);
	    setJustification(StyleConstants.getAlignment(attr));
	    lineSpacing = StyleConstants.getLineSpacing(attr);
	    firstLineIndent = (int)StyleConstants.getFirstLineIndent(attr);
	}
    }

    /**
     * The child views of the paragraph are rows which
     * have been used to arrange pieces of the Views that
     * represent the child elements.  This is the number 
     * of views that have been tiled in two dimensions,
     * and should be equivalent to the number of child elements
     * to the element this view is responsible for.
     */
    protected int getLayoutViewCount() {
	return layoutPool.size();
    }

    /**
     * The child views of the paragraph are rows which
     * have been used to arrange pieces of the Views that
     * represent the child elements.  This methods returns
     * the view responsible for the child element index
     * (prior to breaking).  These are the Views that were
     * produced from a factory (to represent the child
     * elements) and used for layout.
     */
    protected View getLayoutView(int index) {
	return (View) layoutPool.elementAt(index);
    }

    /**
     * Loads all of the children to initialize the view.
     * This is called by the <code>setParent</code> method.
     * This is reimplemented to not load any children directly
     * (as they are created in the process of formatting).
     * This does create views to represent the child elements,
     * but they are placed into a pool that is used in the 
     * process of formatting.
     *
     * @param f the view factory
     */
    protected void loadChildren(ViewFactory f) {
        layoutPool = new Vector();
        Element e = getElement();
        int n = e.getElementCount();
        for (int i = 0; i < n; i++) {
	    View v = f.create(e.getElement(i));
	    v.setParent(this);
            layoutPool.addElement(v);
        }
    }

    /**
     * Fetches the child view that represents the given position in
     * the model.  This is implemented to walk through the children
     * looking for a range that contains the given position.  In this
     * view the children do not have a one to one mapping with the
     * child elements (i.e. the children are actually rows that
     * represent a portion of the element this view represents).
     *
     * @param pos  the search position >= 0
     * @param a  the allocation to the box on entry, and the
     *   allocation of the view containing the position on exit
     * @returns  the view representing the given position, or 
     *   null if there isn't one
     */
    protected View getViewAtPosition(int pos, Rectangle a) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            int p0 = v.getStartOffset();
            int p1 = v.getEndOffset();
            if ((pos >= p0) && (pos < p1)) {
                // it's in this view.
		if (a != null) {
		    childAllocation(i, a);
		}
                return v;
            }
        }
	if (pos == getEndOffset()) {
	    // PENDING(bcb): This will probably want to choose the first
	    // if right to left.
	    View v = getView(n - 1);
	    if (a != null) {
		this.childAllocation(n - 1, a);
	    }
	    return v;
	}
        return null;
    }

    /**
     * Fetches the child view index representing the given position in
     * the model.
     *
     * @param pos the position >= 0
     * @returns  index of the view representing the given position, or 
     *   -1 if no view represents that position
     */
    protected int getViewIndexAtPosition(int pos) {
	// This is expensive, but are views are not necessarily layed
	// out in model order.
	if(pos < getStartOffset() || pos >= getEndOffset())
	    return -1;
	for(int counter = getViewCount() - 1; counter >= 0; counter--) {
	    View v = getView(counter);
	    if(pos >= v.getStartOffset() &&
	       pos < v.getEndOffset()) {
		return counter;
	    }
	}
	return -1;
    }

    /**
     * Lays out the children.  If the layout span has changed,
     * the rows are rebuilt.  The superclass functionality
     * is called after checking and possibly rebuilding the
     * rows.  If the height has changed, the 
     * <code>preferenceChanged</code> method is called
     * on the parent since the vertical preference is 
     * rigid.
     *
     * @param width  the width to lay out against >= 0.  This is
     *   the width inside of the inset area.
     * @param height the height to lay out against >= 0 (not used
     *   by paragraph, but used by the superclass).  This
     *   is the height inside of the inset area.
     */
    protected void layout(int width, int height) {
        if (layoutSpan != width) {
            int oldHeight = height;
            rebuildRows(width);
            int newHeight = (int) getPreferredSpan(Y_AXIS);
            if (oldHeight != newHeight) {
                View p = getParent();
                p.preferenceChanged(this, false, true);
            }
        }

        // do normal box layout
        super.layout(width, height);
    }

    /** 
     * Does a a full layout on this View.  This causes all of 
     * the rows (child views) to be rebuilt to match the given 
     * span of the given allocation.
     *
     * @param span  the length to layout against.
     */
    void rebuildRows(int span) {
        layoutSpan = span;
        int p0 = getStartOffset(); 
        int p1 = getEndOffset();
        removeAll();

        // Removing the rows may leave some views in the layout pool
        // disconnected from the view tree.  Rather than trying to 
        // figure out which views these are, we simply reparent all of 
        // the views in the pool.
        int n = layoutPool.size();
        for( int i=0; i<n; i++ ) {
            View v = (View)layoutPool.elementAt(i);
            v.setParent(this);
        }
        
	boolean firstRow = true;

        while(p0 < p1) {
            int old = p0;
            // PENDING(prinz) The old rows should be reused and
            // new ones created only if needed... and discarded
            // only if not needed.
            Row row = new Row(getElement());
	    if(firstRow) {
		// Give it at least 5 pixels.
		row.setInsets((short)0, (short)Math.min(span - 5,
							firstLineIndent),
			      (short)0, (short)0);
		firstRow = false;
	    }
            append(row);

            // layout the row to the current span
            layoutRow(row, p0);
            p0 = row.getEndOffset();
            if (p0 <= old) {
                throw new StateInvariantError("infinite loop in formatting");
            }
        }
    }

    /**
     * Creates a row of views that will fit within the 
     * current layout span.  The rows occupy the area
     * from the left inset to the right inset.
     * 
     * @param row the row to fill in with views.  This is assumed
     *   to be empty on entry.
     * @param pos  The current position in the children of
     *   this views element from which to start.  
     */
    void layoutRow(Row row, int pos) {
        int x = tabBase + getLeftInset();
        int spanLeft = layoutSpan;
        int end = getEndOffset();
	// Indentation.
	int preX = x;
	x += row.getLeftInset();
	spanLeft -= (x - preX);
	int availableSpan = spanLeft;
	preX = x;

        while (pos < end  && spanLeft > 0) {
            View v = createView(pos);
	    
            int chunkSpan;
            if (v instanceof TabableView) {
                chunkSpan = (int) ((TabableView)v).getTabbedSpan(x, this);
            } else {
                chunkSpan = (int) v.getPreferredSpan(View.X_AXIS);
            }
            spanLeft -= chunkSpan;
            x += chunkSpan;
            row.append(v);
            pos = v.getEndOffset();

	    // If a forced break is necessary, break
	    if (v.getBreakWeight(View.X_AXIS, pos, spanLeft) >= ForcedBreakWeight) {
		break;
	    }
        }
        if (spanLeft < 0) {
            // This row is too long and needs to be adjusted.
            adjustRow(row, availableSpan, preX);
        } else if (row.getViewCount() == 0) {
	    // Impossible spec... put in whatever is left.
            View v = createView(pos);
	    row.append(v);
	}
	// Adjust for line spacing
	if(lineSpacing > 1) {
	    int            height = (int)row.getPreferredSpan(View.Y_AXIS);
	    int            addition = (int)((float)height * lineSpacing) -
		                           height;

	    if(addition > 0)
		row.setInsets(row.getTopInset(), row.getLeftInset(),
			      (short)addition, row.getRightInset());
	}
    }

    /**
     * Adjusts the given row if possible to fit within the
     * layout span.  By default this will try to find the 
     * highest break weight possible nearest the end of
     * the row.  If a forced break is encountered, the
     * break will be positioned there.
     * 
     * @param r the row to adjust to the current layout
     *  span.
     * @param desiredSpan the current layout span >= 0
     * @param x the location r starts at.
     */
    protected void adjustRow(Row r, int desiredSpan, int x) {
        int n = r.getViewCount();
        int span = 0;
        int bestWeight = BadBreakWeight;
        int bestSpan = 0;
        int bestIndex = -1;
        int bestOffset = 0;
        View v;
        for (int i = 0; i < n; i++) {
            v = r.getView(i);
            int spanLeft = desiredSpan - span;

            int w = v.getBreakWeight(X_AXIS, x + span, spanLeft);
            if (w >= bestWeight) {
                bestWeight = w;
                bestIndex = i;
                bestSpan = span;
                if (w >= ForcedBreakWeight) {
                    // it's a forced break, so there is
                    // no point in searching further.
                    break;
                }
            }
            span += v.getPreferredSpan(X_AXIS);
        }
        if (bestIndex < 0) {
            // there is nothing that can be broken, leave
            // it in it's current state.
            return;
        }

        // Break the best candidate view, and patch up the row.
        int spanLeft = desiredSpan - bestSpan;
        v = r.getView(bestIndex);
        v = v.breakView(X_AXIS, v.getStartOffset(), x + bestSpan, spanLeft);
        View[] va = new View[1];
        va[0] = v;
        r.replace(bestIndex, n - bestIndex, va);

        // The views removed from the row now live in the layout pool with a
        // null parent.  These must be reparented.  Note: we could remember
        // what is being replaced and then reparent exactly those, but its
        // probably faster to just search the layout pool.
        int poolSize = layoutPool.size();
        for( int i=0; i<poolSize; i++ ) {
            v = (View)layoutPool.elementAt(i);
            if( v.getParent() == null )
                v.setParent(this);
        }
        
    }

    /**
     * Creates a unidirectional view that can be used to represent the
     * current chunk.  This can be either an entire view from the
     * layout pool, or a fragment there of.
     */
    View createView(int startOffset) {
        // Get the child view that contains the given starting position
        int childIndex = getElement().getElementIndex(startOffset);
        View v = (View) layoutPool.elementAt(childIndex);

        int endOffset = v.getEndOffset();
        
        // REMIND (bcb) handle case of not an abstract document.
        AbstractDocument d = (AbstractDocument)getDocument();
        
        if(d.getProperty(AbstractDocument.I18NProperty).equals(Boolean.TRUE)) {
            Element bidiRoot = d.getBidiRootElement();
            if( bidiRoot.getElementCount() > 1 ) {
                int bidiIndex = bidiRoot.getElementIndex( startOffset );
                Element bidiElem = bidiRoot.getElement( bidiIndex );
                endOffset = Math.min( bidiElem.getEndOffset(), endOffset );
            }
        }

        if (startOffset==v.getStartOffset() && endOffset==v.getEndOffset()) {
            // return the entire view
            return v;
        }

        // return a unidirectional fragment.
        v = v.createFragment(startOffset, endOffset);
        return v;
    }

    // --- TabExpander methods ------------------------------------------

    /**
     * Returns the next tab stop position given a reference position.
     * This view implements the tab coordinate system, and calls
     * <code>getTabbedSpan</code> on the logical children in the process 
     * of layout to determine the desired span of the children.  The
     * logical children can delegate their tab expansion upward to
     * the paragraph which knows how to expand tabs. 
     * <code>LabelView</code> is an example of a view that delegates
     * its tab expansion needs upward to the paragraph.
     * <p>
     * This is implemented to try and locate a <code>TabSet</code>
     * in the paragraph element's attribute set.  If one can be
     * found, its settings will be used, otherwise a default expansion
     * will be provided.  The base location for for tab expansion
     * is the left inset from the paragraphs most recent allocation
     * (which is what the layout of the children is based upon).
     *
     * @param x the X reference position
     * @param tabOffset the position within the text stream
     *   that the tab occurred at >= 0.
     * @return the trailing end of the tab expansion >= 0
     * @see TabSet
     * @see TabStop
     * @see LabelView
     */
    public float nextTabStop(float x, int tabOffset) {
	// If the text isn't left justified, offset by 10 pixels!
	if(justification != StyleConstants.ALIGN_LEFT)
            return x + 10.0f;
        x -= tabBase;
        TabSet tabs = getTabSet();
        if(tabs == null) {
            // a tab every 72 pixels.
            return (float)(tabBase + (((int)x / 72 + 1) * 72));
        }
        TabStop tab = tabs.getTabAfter(x + .01f);
        if(tab == null) {
            // no tab, do a default of 5 pixels.
            // Should this cause a wrapping of the line?
            return tabBase + x + 5.0f;
        }
        int alignment = tab.getAlignment();
        int offset;
        switch(alignment) {
        default:
        case TabStop.ALIGN_LEFT:
            // Simple case, left tab.
            return tabBase + tab.getPosition();
        case TabStop.ALIGN_BAR:
            // PENDING: what does this mean?
            return tabBase + tab.getPosition();
        case TabStop.ALIGN_RIGHT:
        case TabStop.ALIGN_CENTER:
            offset = findOffsetToCharactersInString(tabChars,
                                                    tabOffset + 1);
            break;
        case TabStop.ALIGN_DECIMAL:
            offset = findOffsetToCharactersInString(tabDecimalChars,
                                                    tabOffset + 1);
            break;
        }
        if (offset == -1) {
            offset = getEndOffset();
        }
        float charsSize = getPartialSize(tabOffset + 1, offset);
        switch(alignment) {
        case TabStop.ALIGN_RIGHT:
        case TabStop.ALIGN_DECIMAL:
            // right and decimal are treated the same way, the new
            // position will be the location of the tab less the
            // partialSize.
            return tabBase + Math.max(x, tab.getPosition() - charsSize);
        case TabStop.ALIGN_CENTER: 
            // Similar to right, but half the partialSize.
            return tabBase + Math.max(x, tab.getPosition() - charsSize / 2.0f);
        }
        // will never get here!
        return x;
    }

    /**
     * Gets the Tabset to be used in calculating tabs.
     *
     * @return the TabSet
     */
    protected TabSet getTabSet() {
	return StyleConstants.getTabSet(getElement().getAttributes());
    }

    /**
     * Returns the size used by the views between <code>startOffset</code>
     * and <code>endOffset</code>. This uses getPartialView to calculate the
     * size if the child view implements the TabableView interface. If a 
     * size is needed and a View does not implement the TabableView
     * interface, the preferredSpan will be used.
     *
     * @param startOffset the starting document offset >= 0
     * @param endOffset the ending document offset >= startOffset
     * @return the size >= 0
     */
    protected float getPartialSize(int startOffset, int endOffset) {
        float size = 0.0f;
        int viewIndex;
        int numViews = getViewCount();
        View view;
        int viewEnd;
        int tempEnd;

        // Have to search layoutPool!
        // PENDING: when ParagraphView supports breaking location
        // into layoutPool will have to change!
        viewIndex = getElement().getElementIndex(startOffset);
        numViews = layoutPool.size();
        while(startOffset < endOffset && viewIndex < numViews) {
            view = (View) layoutPool.elementAt(viewIndex++);
            viewEnd = view.getEndOffset();
            tempEnd = Math.min(endOffset, viewEnd);
            if(view instanceof TabableView)
                size += ((TabableView)view).getPartialSpan(startOffset, tempEnd);
            else if(startOffset == view.getStartOffset() &&
                    tempEnd == view.getEndOffset())
                size += view.getPreferredSpan(View.X_AXIS);
            else
                // PENDING: should we handle this better?
                return 0.0f;
            startOffset = viewEnd;
        }
        return size;
    }

    /**
     * Finds the next character in the document with a character in
     * <code>string</code>, starting at offset <code>start</code>. If
     * there are no characters found, -1 will be returned.
     *
     * @param string the string of characters
     * @param start where to start in the model >= 0
     * @return the document offset or -1
     */
    protected int findOffsetToCharactersInString(char[] string,
                                                 int start) {
        int stringLength = string.length;
        int end = getEndOffset();
        Segment seg = new Segment();
        try {
            getDocument().getText(start, end - start, seg);
        } catch (BadLocationException ble) {
            return -1;
        }
        for(int counter = seg.offset, maxCounter = seg.offset + seg.count;
            counter < maxCounter; counter++) {
            char currentChar = seg.array[counter];
            for(int subCounter = 0; subCounter < stringLength;
                subCounter++) {
                if(currentChar == string[subCounter])
                    return counter - seg.offset + start;
            }
        }
        // No match.
        return -1;
    }

    /**
     * @return where tabs are calculated from.
     */
    protected float getTabBase() {
	return (float)tabBase;
    }

    protected boolean flipEastAndWestAtEnds(int position,
					    Position.Bias bias) {
	Document doc = getDocument();
	if(doc instanceof AbstractDocument &&
	   !((AbstractDocument)doc).isLeftToRight(getStartOffset(),
						  getStartOffset() + 1)) {
	    return true;
	}
	return false;
    }

    // ---- View methods ----------------------------------------------------

    /**
     * Renders using the given rendering surface and area on that
     * surface.  This is implemented to delgate to the superclass
     * after stashing the base coordinate for tab calculations.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     * @see View#paint
     */
    public void paint(Graphics g, Shape a) {
        Rectangle alloc = a.getBounds();
        tabBase = alloc.x;
        super.paint(g, a);
    }

    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	if (r == null) {
	    r = new SizeRequirements();
	}
	float pref = 0;
	int n = layoutPool.size();
	for (int i = 0; i < n; i++) {
	    View v = (View) layoutPool.elementAt(i);
	    pref += v.getPreferredSpan(axis);
	}

	r.minimum = 0;
	r.preferred = (int) pref;
	r.maximum = Integer.MAX_VALUE;
	r.alignment = 0.5f;
	return r;
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the alignment to the
     * center of the first row along the y axis, and the default
     * along the x axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the desired alignment.  This should be a value
     *   between 0.0 and 1.0 inclusive, where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     */
    public float getAlignment(int axis) {
        switch (axis) {
        case Y_AXIS:
	    float a = 0.5f;
	    if (getViewCount() != 0) {
		int paragraphSpan = (int) getPreferredSpan(View.Y_AXIS);
		View v = getView(0);
		int rowSpan = (int) v.getPreferredSpan(View.Y_AXIS);
		a = (paragraphSpan != 0) ? ((float)(rowSpan / 2)) / paragraphSpan : 0;
	    }
            return a;
	case X_AXIS:
	    return 0.5f;
	default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Breaks this view on the given axis at the given length.<p>
     * ParagraphView instances are breakable along the Y_AXIS only, and only if
     * <code>len</code> is after the first line.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param len specifies where a potential break is desired
     *  along the given axis >= 0
     * @param a the current allocation of the view
     * @return the fragment of the view that represents the
     *  given span, if the view can be broken.  If the view
     *  doesn't support breaking behavior, the view itself is
     *  returned.
     * @see View#breakView
     */
    public View breakView(int axis, float len, Shape a) {
        if(axis == View.Y_AXIS) {
            if(a != null) {
                Rectangle alloc = a.getBounds();
                setSize(alloc.width, alloc.height);
            }
            // Determine what row to break on.

            // PENDING(prinz) add break support
            return this;
        }
        return this;
    }

    /**
     * Gets the break weight for a given location.
     * ParagraphView instances are breakable along the Y_AXIS only, and 
     * only if <code>len</code> is after the first row.  If the length
     * is less than one row, a value of BadBreakWeight is returned.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @param len specifies where a potential break is desired >= 0
     * @return a value indicating the attractiveness of breaking here
     * @see View#getBreakWeight
     */
    public int getBreakWeight(int axis, float len) {
        if(axis == View.Y_AXIS) {
            // PENDING(prinz) make this return a reasonable value
            // when paragraph breaking support is re-implemented.
            // If less than one row, bad weight value should be 
            // returned.
            //return GoodBreakWeight;
            return BadBreakWeight;
        }
        return BadBreakWeight;
    }

    /**
     * Gives notification that something was inserted into the document
     * in a location that this view is responsible for.
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#insertUpdate
     */
    public void insertUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
        // update the pool of logical children
        Element elem = getElement();
        DocumentEvent.ElementChange ec = changes.getChange(elem);
        if (ec != null) {
            // the structure of this element changed.
            updateLogicalChildren(ec, f);
        }

        // find and forward if there is anything there to 
        // forward to.  If children were removed then there was
        // a replacement of the removal range and there is no
        // need to forward.
	if (ec != null && ec.getChildrenAdded().length > 0) {
	    int index = ec.getIndex();
	    int pos = changes.getOffset();
	    if (index > 0) {
		Element child = elem.getElement(index - 1);
		if (child.getEndOffset() >= pos) {
		    View v = (View)layoutPool.elementAt(index - 1);
		    v.insertUpdate(changes, null, f);
		}
	    }
	    int endIndex = index + ec.getChildrenAdded().length;
	    if (endIndex < layoutPool.size()) {
		Element child = elem.getElement(endIndex);
		int start = child.getStartOffset();
		if (start >= pos && start <= (pos + changes.getLength())) {
		    View v = (View)layoutPool.elementAt(endIndex);
		    v.insertUpdate(changes, null, f);
		}
	    }
	}
        //REMIND(bcb) It is possible for an event have no added children,
        //a removed child and a change to an existing child.  To see, this
        //do the following.  Bring up Stylepad. Empty its contents. Select
        //a different font.  Type a line until it wraps then hit return.
        //Someone should code review this change.
        else/* if (ec == null || (ec.getChildrenRemoved().length == 0)) */{
            int pos = changes.getOffset();
            int index = elem.getElementIndex(pos);
            View v = (View) layoutPool.elementAt(index);
            v.insertUpdate(changes, null, f);
	    if (index > 0 && v.getStartOffset() == pos) {
		v = (View)layoutPool.elementAt(index - 1);
		v.insertUpdate(changes, null, f);
	    }
        }

        // force layout, should do something more intelligent about
        // incurring damage and triggering a new layout.  This is just
        // about as brute force as it can get.
        layoutSpan = Integer.MAX_VALUE;
        preferenceChanged(null, true, true);
        Rectangle alloc = getInsideAllocation(a);
        if (alloc != null) {
            layout((int) alloc.width, (int) alloc.height);
            Component host = getContainer();
            host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
        }
    }

    /**
     * Update the logical children to reflect changes made 
     * to the element this view is responsible.  This updates
     * the pool of views used for layout (ie. the views 
     * representing the child elements of the element this
     * view is responsible for).  This is called by the 
     * <code>insertUpdate, removeUpdate, and changeUpdate</code>
     * methods.
     */
    void updateLogicalChildren(DocumentEvent.ElementChange ec, ViewFactory f) {
        int index = ec.getIndex();
        Element[] removedElems = ec.getChildrenRemoved();
        for (int i = 0; i < removedElems.length; i++) {
	    View v = (View) layoutPool.elementAt(index);
	    v.setParent(null);
            layoutPool.removeElementAt(index);
        }
        Element[] addedElems = ec.getChildrenAdded();
        for (int i = 0; i < addedElems.length; i++) {
	    View v = f.create(addedElems[i]);
	    v.setParent(this);
            layoutPool.insertElementAt(v, index + i);
        }
    }

    /**
     * Gives notification that something was removed from the document
     * in a location that this view is responsible for.
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#removeUpdate
     */
    public void removeUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
        // update the pool of logical children
        Element elem = getElement();
        DocumentEvent.ElementChange ec = changes.getChange(elem);
        if (ec != null) {
            // the structure of this element changed.
            updateLogicalChildren(ec, f);
        }

        // find and forward if there is anything there to 
        // forward to.  If children were added then there was
        // a replacement of the removal range and there is no
        // need to forward.
        if (ec == null || (ec.getChildrenAdded().length == 0)) {
            int pos = changes.getOffset();
            int index = elem.getElementIndex(pos);
            View v = (View) layoutPool.elementAt(index);
            v.removeUpdate(changes, null, f);
        }

        // force layout, should do something more intelligent about
        // incurring damage and triggering a new layout.
        layoutSpan = Integer.MAX_VALUE;
        preferenceChanged(null, true, true);
        if (a != null) {
            Rectangle alloc = getInsideAllocation(a);
            layout((int) alloc.width, (int) alloc.height);
            Component host = getContainer();
            host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
        }
    }

    /**
     * Gives notification from the document that attributes were changed
     * in a location that this view is responsible for.
     *
     * @param changes the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#changedUpdate
     */
    public void changedUpdate(DocumentEvent changes, Shape a, ViewFactory f) {
        // update any property settings stored
	setPropertiesFromAttributes();

        // update the pool of logical children
        Element elem = getElement();
        DocumentEvent.ElementChange ec = changes.getChange(elem);
        if (ec != null) {
            // the structure of this element changed.
            updateLogicalChildren(ec, f);
        }

        // forward to the logical children
        int p0 = changes.getOffset();
        int p1 = p0 + changes.getLength();
        int index0 = elem.getElementIndex(p0);
        int index1 = elem.getElementIndex(p1 - 1);
	// Check for case where p0 == p1 and they fall on a boundry.
	if (p0 == p1 && index1 < index0 && index0 > 0) {
	    index0--;
	    index1 = index0 + 1;
	}
        for (int i = index0; i <= index1; i++) {
            View v = (View) layoutPool.elementAt(i);
            v.changedUpdate(changes, null, f);
        }

        // force layout, should do something more intelligent about
        // incurring damage and triggering a new layout.
        layoutSpan = Integer.MAX_VALUE;
        preferenceChanged(null, true, true);
        if (a != null) {
            Rectangle alloc = getInsideAllocation(a);
            layout((int) alloc.width, (int) alloc.height);
            Component host = getContainer();
            host.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
        }
    }

    /**
     * Overriden from CompositeView.
     */
    protected int getNextNorthSouthVisualPositionFrom(int pos, Position.Bias b,
						      Shape a, int direction,
						      Position.Bias[] biasRet)
	                                        throws BadLocationException {
	int vIndex;
	if(pos == -1) {
	    vIndex = (direction == SwingConstants.NORTH) ?
		     getViewCount() - 1 : 0;
	}
	else {
	    if(b == Position.Bias.Backward && pos > 0) {
		vIndex = getViewIndexAtPosition(pos - 1);
	    }
	    else {
		vIndex = getViewIndexAtPosition(pos);
	    }
	    if(direction == NORTH) {
		if(vIndex == 0) {
		    return -1;
		}
		vIndex--;
	    }
	    else if(++vIndex >= getViewCount()) {
		return -1;
	    }
	}
	// vIndex gives index of row to look in.
	JTextComponent text = (JTextComponent)getContainer();
	Caret c = text.getCaret();
	Point magicPoint;
	magicPoint = (c != null) ? c.getMagicCaretPosition() : null;
	int x;
	if(magicPoint == null) {
	    Shape posBounds = text.getUI().modelToView(text, pos, b);
	    if(posBounds == null) {
		x = 0;
	    }
	    else {
		x = posBounds.getBounds().x;
	    }
	}
	else {
	    x = magicPoint.x;
	}
	return getClosestPositionTo(pos, b, a, direction, biasRet, vIndex, x);
    }

    /**
     * Returns the closest model position to <code>x</code>.
     * <code>rowIndex</code> gives the index of the view that corresponds
     * that should be looked in.
     */
    // NOTE: This will not properly work if ParagraphView contains
    // other ParagraphViews. It won't raise, but this does not message
    // the children views with getNextVisualPositionFrom.
    protected int getClosestPositionTo(int pos, Position.Bias b, Shape a,
				       int direction, Position.Bias[] biasRet,
				       int rowIndex, int x)
	      throws BadLocationException {
	JTextComponent text = (JTextComponent)getContainer();
	Document doc = getDocument();
	AbstractDocument aDoc = (doc instanceof AbstractDocument) ?
	                        (AbstractDocument)doc : null;
	View row = getView(rowIndex);
	int lastPos = -1;
	// This could be made better to check backward positions too.
	biasRet[0] = Position.Bias.Forward;
	for(int vc = 0, numViews = row.getViewCount(); vc < numViews; vc++) {
	    View v = row.getView(vc);
	    int start = v.getStartOffset();
	    boolean ltr = (aDoc != null) ? aDoc.isLeftToRight
		           (start, start + 1) : true;
	    if(ltr) {
		lastPos = start;
		for(int end = v.getEndOffset(); lastPos < end; lastPos++) {
		    if(text.modelToView(lastPos).getBounds().x >= x) {
			return lastPos;
		    }
		}
		lastPos--;
	    }
	    else {
		for(lastPos = v.getEndOffset() - 1; lastPos >= start;
		    lastPos--) {
		    if(text.modelToView(lastPos).getBounds().x >= x) {
			return lastPos;
		    }
		}
		lastPos++;
	    }
	}
	if(lastPos == -1) {
	    return getStartOffset();
	}
	return lastPos;
    }

    
    // --- variables -----------------------------------------------

    private int justification;
    private float lineSpacing;
    /** Indentation for the first line, from the left inset. */
    protected int firstLineIndent;

    /**
     * Used by the TabExpander functionality to determine
     * where to base the tab calculations.  This is basically
     * the location of the left side of the paragraph.
     */
    private int tabBase;

    /**
     * Used by the layout process.  The span holds the
     * length that has been formatted to. 
     */
    private int layoutSpan;

    /**
     * These are the views that represent the child elements
     * of the element this view represents.  These are not
     * directly children of this view.  These are either 
     * placed into the rows directly or used for the purpose
     * of breaking into smaller chunks.
     */
    private Vector layoutPool;
    
    /** Used for searching for a tab. */
    static char[] tabChars;
    /** Used for searching for a tab or decimal character. */
    static char[] tabDecimalChars;

    static {
        tabChars = new char[1];
        tabChars[0] = '\t';
        tabDecimalChars = new char[2];
        tabDecimalChars[0] = '\t';
        tabDecimalChars[1] = '.';
    }

    /**
     * Internally created view that has the purpose of holding
     * the views that represent the children of the paragraph
     * that have been arranged in rows.
     */
    class Row extends BoxView {

        Row(Element elem) {
            super(elem, View.X_AXIS);
        }

        /**
         * This is reimplemented to do nothing since the
         * paragraph fills in the row with its needed
         * children.
         */
        protected void loadChildren(ViewFactory f) {
        }

	/**
	 * Fetches the attributes to use when rendering.  This view
	 * isn't directly responsible for an element so it returns
	 * the outer classes attributes.
	 */
        public AttributeSet getAttributes() {
	    return ParagraphView.this.getAttributes();
	}

	/**
	 * Determines the desired alignment for this view along an
	 * axis.  This is implemented to give a horizontal alignment
	 * appropriate for the kind of justification being done.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns the desired alignment >= 0.0f && <= 1.0f.  This should
	 *   be a value between 0.0 and 1.0 where 0 indicates alignment at the
	 *   origin and 1.0 indicates alignment to the full span
	 *   away from the origin.  An alignment of 0.5 would be the
	 *   center of the view.
	 * @exception IllegalArgumentException for an invalid axis
	 */
        protected void layout(int width, int height) {
	    Document doc = getDocument();
            if (doc.getProperty(AbstractDocument.I18NProperty).equals(Boolean.TRUE)) {
		int n = getViewCount();
		if (n > 1) {

		    // REMIND (bcb) handle case of not an abstract document.
		    AbstractDocument d = (AbstractDocument)getDocument();
		    Element bidiRoot 
			= ((AbstractDocument)getElement().getDocument()).getBidiRootElement();
		    byte[] levels = new byte[n];
		    View[] reorder = new View[n];
		    
		    for( int i=0; i<n; i++ ) {
			View v = getView(i);
			int bidiIndex =bidiRoot.getElementIndex(v.getStartOffset());
			Element bidiElem = bidiRoot.getElement( bidiIndex );
			levels[i] = (byte)StyleConstants.getBidiLevel(bidiElem.getAttributes());
			reorder[i] = v;
		    }
		    
		    Bidi.reorderVisually( levels, reorder );
		    replace(0, n, reorder);
		}
	    }
	    super.layout(width, height);
	}

        public float getAlignment(int axis) {
            if (axis == View.X_AXIS) {
                switch (justification) {
                case StyleConstants.ALIGN_LEFT:
                    return 0;
                case StyleConstants.ALIGN_RIGHT:
                    return 1;
                case StyleConstants.ALIGN_CENTER:
                case StyleConstants.ALIGN_JUSTIFIED:
                    return 0.5f;
                }
            }
            return super.getAlignment(axis);
        }

        /**
         * Provides a mapping from the document model coordinate space
         * to the coordinate space of the view mapped to it.  This is
         * implemented to let the superclass find the position along 
         * the major axis and the allocation of the row is used 
         * along the minor axis, so that even though the children 
         * are different heights they all get the same caret height.
         *
         * @param pos the position to convert
         * @param a the allocated region to render into
         * @return the bounding box of the given position
         * @exception BadLocationException  if the given position does not represent a
         *   valid location in the associated document
         * @see View#modelToView
         */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
            Rectangle r = a.getBounds();
	    View v = getViewAtPosition(pos, r);
	    if ((v != null) && (!v.getElement().isLeaf())) {
		// Don't adjust the height if the view represents a branch.
		return super.modelToView(pos, a, b);
	    }
	    r = a.getBounds();
            int height = r.height;
            int y = r.y;
            Shape loc = super.modelToView(pos, a, b);
            r = loc.getBounds();
            r.height = height;
            r.y = y;
            return r;
        }

        /**
         * Range represented by a row in the paragraph is only
         * a subset of the total range of the paragraph element.
         * @see View#getRange
         */
        public int getStartOffset() {
	    int offs = Integer.MAX_VALUE;
            int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		offs = Math.min(offs, v.getStartOffset());
	    }
            return offs;
        }

        public int getEndOffset() {
	    int offs = 0;
            int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		offs = Math.max(offs, v.getEndOffset());
	    }
            return offs;
        }

	/**
	 * Perform layout for the minor axis of the box (i.e. the
	 * axis orthoginal to the axis that it represents).  The results 
	 * of the layout should be placed in the given arrays which represent 
	 * the allocations to the children along the minor axis.
	 * <p>
	 * This is implemented to do a baseline layout of the children
	 * by calling BoxView.baselineLayout.
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
	    baselineLayout(targetSpan, axis, offsets, spans);
	}

        protected SizeRequirements calculateMinorAxisRequirements(int axis, 
								  SizeRequirements r) {
	    return baselineRequirements(axis, r);
	}

        /**
         * Fetches the child view that represents the given position in
         * the model.  This is implemented to walk through the children
         * looking for a range that contains the given position.
         * @param pos  The search position 
         * @param a  The allocation to the box on entry, and the
         *   allocation of the view containing the position on exit.
         * @returns  The view representing the given position, or 
         *   null if there isn't one.
         */
        protected View getViewAtPosition(int pos, Rectangle a) {
            int n = getViewCount();
            for (int i = 0; i < n; i++) {
                View v = getView(i);
                int p0 = v.getStartOffset();
                int p1 = v.getEndOffset();
                if ((pos >= p0) && (pos < p1)) {
                    // it's in this view.
		    if (a != null) {
			this.childAllocation(i, a);
		    }
                    return v;
                }
            }
	    if (pos == getEndOffset()) {
		// PENDING(bcb): This will probably want to choose the first
		// if right to left.
		View v = getView(n - 1);
		if (a != null) {
		    this.childAllocation(n - 1, a);
		}
		return v;
	    }
            return null;
        }

	/**
	 * Fetches the child view index representing the given position in
	 * the model.
	 *
	 * @param pos the position >= 0
	 * @returns  index of the view representing the given position, or 
	 *   -1 if no view represents that position
	 */
	protected int getViewIndexAtPosition(int pos) {
	    // This is expensive, but are views are not necessarily layed
	    // out in model order.
	    if(pos < getStartOffset() || pos >= getEndOffset())
		return -1;
	    for(int counter = getViewCount() - 1; counter >= 0; counter--) {
		View v = getView(counter);
		if(pos >= v.getStartOffset() &&
		   pos < v.getEndOffset()) {
		    return counter;
		}
	    }
	    return -1;
	}
    }

}
