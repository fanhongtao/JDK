/*
 * @(#)ScrollPaneLayout.java	1.47 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;


import javax.swing.border.*;

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.Serializable;


/**
 * The layout manager used by JScrollPane.  JScrollPaneLayout is
 * responsible for nine components: a viewport, two scrollbars,
 * a row header, a column header, and four "corner" components.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JScrollPane
 * @see JViewport
 *
 * @version 1.47 02/02/00
 * @author Hans Muller
 */
public class ScrollPaneLayout
    implements LayoutManager, ScrollPaneConstants, Serializable
{

    /** 
     * The scrollpanes viewport child.  Default is an empty JViewport.
     * @see JScrollPane#setViewport
     */
    protected JViewport viewport;


    /**
     * The scrollpanes vertical scrollbar child.  Default is a JScrollBar.
     * @see JScrollPane#setVerticalScrollBar
     */
    protected JScrollBar vsb;


    /**
     * The scrollpanes horizontal scrollbar child.  Default is a JScrollBar.
     * @see JScrollPane#setHorizontalScrollBar
     */
    protected JScrollBar hsb;


    /** 
     * The row header child.  Default is null.
     * @see JScrollPane#setRowHeader
     */
    protected JViewport rowHead;


    /** 
     * The column header child.  Default is null.
     * @see JScrollPane#setColumnHeader
     */
    protected JViewport colHead;


    /**
     * The component to display in the lower left corner.  Default is null.
     * @see JScrollPane#setCorner
     */
    protected Component lowerLeft;


    /**
     * The component to display in the lower right corner.  Default is null.
     * @see JScrollPane#setCorner
     */
    protected Component lowerRight;


    /**
     * The component to display in the upper left corner.  Default is null.
     * @see JScrollPane#setCorner
     */
    protected Component upperLeft;


    /**
     * The component to display in the upper right corner.  Default is null.
     * @see JScrollPane#setCorner
     */
    protected Component upperRight;


    /** 
     * The display policy for the vertical scrollbar.
     * The default is JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED.  
     * <p>
     * This field is obsolete, please use the JScrollPane field instead.
     * 
     * @see JScrollPane#setVerticalScrollBarPolicy
     */
    protected int vsbPolicy = VERTICAL_SCROLLBAR_AS_NEEDED;


    /**
     * The display policy for the horizontal scrollbar.
     * The default is JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED.
     * <p>
     * This field is obsolete, please use the JScrollPane field instead.
     * 
     * @see JScrollPane#setHorizontalScrollBarPolicy
     */
    protected int hsbPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED;


    /**
     * This method must be called after setting a JScrollPanes
     * layout manager.  It initializes all of the internal fields that
     * are ordinarily set by addLayoutComponent().  For example:
     * <pre>
     * ScrollPaneLayout mySPLayout = new ScrollPanelLayout() {
     *     public void layoutContainer(Container p) {
     *         super.layoutContainer(p);
     *         // do some extra work here ...
     *     }
     * };
     * scrollpane.setLayout(mySPLayout):
     * mySPLayout.syncWithScrollPane(scrollpane);
     * </pre>
     */
    public void syncWithScrollPane(JScrollPane sp) {
	viewport = sp.getViewport();
	vsb = sp.getVerticalScrollBar();
	hsb = sp.getHorizontalScrollBar();
	rowHead = sp.getRowHeader();
	colHead = sp.getColumnHeader();
	lowerLeft = sp.getCorner(LOWER_LEFT_CORNER);
	lowerRight = sp.getCorner(LOWER_RIGHT_CORNER);
	upperLeft = sp.getCorner(UPPER_LEFT_CORNER);
	upperRight = sp.getCorner(UPPER_RIGHT_CORNER);
	vsbPolicy = sp.getVerticalScrollBarPolicy();
	hsbPolicy = sp.getHorizontalScrollBarPolicy();
    }


    /** 
     * The method used to remove an existing component.
     * Since there can be one and only one left corner, vertical scrollbar,
     * and so on. So only one of each subcomponent is allowed, the old one
     * is removed (if it exists) when a new one is added.
     * <p>
     * This method returns <code>newC</code>. If <code>oldC</code> is
     * not equal to <code>newC</code> and is non-<code>null</code>,
     * it will be removed from its parent.
     * 
     * @param oldC the <code>Component</code> to replace
     * @param newC the <code>Component</code> to add
     * @return the <code>newC</code>
     */
    protected Component addSingletonComponent(Component oldC, Component newC)
    {
	if ((oldC != null) && (oldC != newC)) {
	    oldC.getParent().remove(oldC);
	}
	return newC;
    }


    /**
     * Adds the specified component to the layout. The layout is
     * identified using one of:
     * <ul>
     * <li>JScrollPane.VIEWPORT
     * <li>JScrollPane.VERTICAL_SCROLLBAR
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR
     * <li>JScrollPane.ROW_HEADER
     * <li>JScrollPane.COLUMN_HEADER
     * <li>JScrollPane.LOWER_LEFT_CORNER
     * <li>JScrollPane.LOWER_RIGHT_CORNER
     * <li>JScrollPane.UPPER_LEFT_CORNER
     * <li>JScrollPane.UPPER_RIGHT_CORNER
     * </ul>
     *
     * @param s the component identifier
     * @param comp the the component to be added
     */
    public void addLayoutComponent(String s, Component c) 
    {
	if (s.equals(VIEWPORT)) {
	    viewport = (JViewport)addSingletonComponent(viewport, c);
	}
	else if (s.equals(VERTICAL_SCROLLBAR)) {
	    vsb = (JScrollBar)addSingletonComponent(vsb, c);
	}
	else if (s.equals(HORIZONTAL_SCROLLBAR)) {
	    hsb = (JScrollBar)addSingletonComponent(hsb, c);
	}
	else if (s.equals(ROW_HEADER)) {
	    rowHead = (JViewport)addSingletonComponent(rowHead, c);
	}
	else if (s.equals(COLUMN_HEADER)) {
	    colHead = (JViewport)addSingletonComponent(colHead, c);
	}
	else if (s.equals(LOWER_LEFT_CORNER)) {
	    lowerLeft = addSingletonComponent(lowerLeft, c);
	}
	else if (s.equals(LOWER_RIGHT_CORNER)) {
	    lowerRight = addSingletonComponent(lowerRight, c);
	}
	else if (s.equals(UPPER_LEFT_CORNER)) {
	    upperLeft = addSingletonComponent(upperLeft, c);
	}
	else if (s.equals(UPPER_RIGHT_CORNER)) {
	    upperRight = addSingletonComponent(upperRight, c);
	}
	else {
	    throw new IllegalArgumentException("invalid layout key " + s);
	}
    }


    /**
     * Removes the specified component from the layout.
     *   
     * @param c the component to remove
     */
    public void removeLayoutComponent(Component c) 
    {
	if (c == viewport) {
	    viewport = null;
	}
	else if (c == vsb) {
	    vsb = null;
	}
	else if (c == hsb) {
	    hsb = null;
	}
	else if (c == rowHead) {
	    rowHead = null;
	}
	else if (c == colHead) {
	    colHead = null;
	}
	else if (c == lowerLeft) {
	    lowerLeft = null;
	}
	else if (c == lowerRight) {
	    lowerRight = null;
	}
	else if (c == upperLeft) {
	    upperLeft = null;
	}
	else if (c == upperRight) {
	    upperRight = null;
	}
    }


    /**
     * Returns the vertical scrollbar-display policy.
     *
     * @return an int giving the display policy
     * @see #setVerticalScrollBarPolicy
     */
    public int getVerticalScrollBarPolicy() {
	return vsbPolicy;
    }


    /**
     * Sets the vertical scrollbar-display policy. The options
     * are:
     * <ul>
     * <li>JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     * <li>JScrollPane.VERTICAL_SCROLLBAR_NEVER
     * <li>JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     * </ul>
     * Note: Applications should use the JScrollPane version
     * of this method.  It only exists for backwards compatibility
     * with the Swing 1.0.2 (and earlier) versions of this class.
     *
     * @param x an int giving the display policy
     */
    public void setVerticalScrollBarPolicy(int x) {
	switch (x) {
	case VERTICAL_SCROLLBAR_AS_NEEDED:
	case VERTICAL_SCROLLBAR_NEVER:
	case VERTICAL_SCROLLBAR_ALWAYS:
	    	vsbPolicy = x;
		break;
	default:
	    throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
	}
    }


    /**
     * Returns the horizontal scrollbar-display policy.
     *
     * @return an int giving the display policy
     * @see #setHorizontalScrollBarPolicy
     */
    public int getHorizontalScrollBarPolicy() {
	return hsbPolicy;
    }

    /**
     * Sets the horizontal scrollbar-display policy.
     * The options are:<ul>
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
     * <li>JScrollPane.HOTRIZONTAL_SCROLLBAR_NEVER
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     * </ul>
     * Note: Applications should use the JScrollPane version
     * of this method.  It only exists for backwards compatibility
     * with the Swing 1.0.2 (and earlier) versions of this class.
     *
     * @param x an int giving the display policy
     */
    public void setHorizontalScrollBarPolicy(int x) {
	switch (x) {
	case HORIZONTAL_SCROLLBAR_AS_NEEDED:
	case HORIZONTAL_SCROLLBAR_NEVER:
	case HORIZONTAL_SCROLLBAR_ALWAYS:
	    	hsbPolicy = x;
		break;
	default:
	    throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
	}
    }


    /**
     * Returns the JViewport object that displays the scrollable contents.
     * @return the JViewport object that displays the scrollable contents
     * @see JScrollPane#getViewport
     */
    public JViewport getViewport() {
	return viewport;
    }


    /**
     * Returns the JScrollBar object that handles horizontal scrolling.
     * @return the JScrollBar object that handles horizontal scrolling
     * @see JScrollPane#getHorizontalScrollBar
     */
    public JScrollBar getHorizontalScrollBar() {
	return hsb;
    }

    /**
     * Returns the JScrollBar object that handles vertical scrolling.
     * @return the JScrollBar object that handles vertical scrolling
     * @see JScrollPane#getVerticalScrollBar
     */
    public JScrollBar getVerticalScrollBar() {
	return vsb;
    }


    /**
     * Returns the JViewport object that is the row header.
     * @return the JViewport object that is the row header
     * @see JScrollPane#getRowHeader
     */
    public JViewport getRowHeader() {
	return rowHead;
    }


    /**
     * Returns the JViewport object that is the column header.
     * @return the JViewport object that is the column header
     * @see JScrollPane#getColumnHeader
     */
    public JViewport getColumnHeader() {
	return colHead;
    }


    /**
     * Returns the Component at the specified corner.
     * @param key the String specifying the corner
     * @return the Component at the specified corner, as defined in
     *         {@link ScrollPaneConstants}
     * @see JScrollPane#getCorner
     */
    public Component getCorner(String key) {
	if (key.equals(LOWER_LEFT_CORNER)) {
	    return lowerLeft;
	}
	else if (key.equals(LOWER_RIGHT_CORNER)) {
	    return lowerRight;
	}
	else if (key.equals(UPPER_LEFT_CORNER)) {
	    return upperLeft;
	}
	else if (key.equals(UPPER_RIGHT_CORNER)) {
	    return upperRight;
	}
	else {
	    return null;
	}
    }


    /** 
     * The preferred size of a ScrollPane is the size of the insets,
     * plus the preferred size of the viewport, plus the preferred size of 
     * the visible headers, plus the preferred size of the scrollbars
     * that will appear given the current view and the current
     * scrollbar displayPolicies.  
     * 
     * @param parent the Container that will be laid out
     * @return a Dimension object specifying the preferred size of the 
     *         viewport and any scrollbars.
     * @see ViewportLayout
     * @see LayoutManager
     */
    public Dimension preferredLayoutSize(Container parent) 
    {
	/* Sync the (now obsolete) policy fields with the
	 * JScrollPane.
	 */
	JScrollPane scrollPane = (JScrollPane)parent;
	vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
	hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

	Insets insets = parent.getInsets();
	int prefWidth = insets.left + insets.right;
	int prefHeight = insets.top + insets.bottom;

	/* Note that viewport.getViewSize() is equivalent to 
	 * viewport.getView().getPreferredSize() modulo a null
	 * view or a view whose size was explicitly set.
	 */

	Dimension extentSize = null;
	Dimension viewSize = null;
	Component view = null;

	if (viewport !=  null) {
	    extentSize = viewport.getPreferredSize();
	    viewSize = viewport.getViewSize();
	    view = viewport.getView();
	}

	/* If there's a viewport add its preferredSize.
	 */

	if (extentSize != null) {
	    prefWidth += extentSize.width;
	    prefHeight += extentSize.height;
	}

	/* If there's a JScrollPane.viewportBorder, add its insets.
	 */

	Border viewportBorder = scrollPane.getViewportBorder();
	if (viewportBorder != null) {
	    Insets vpbInsets = viewportBorder.getBorderInsets(parent);
	    prefWidth += vpbInsets.left + vpbInsets.right;
	    prefHeight += vpbInsets.top + vpbInsets.bottom;
	}

	/* If a header exists and it's visible, factor its
	 * preferred size in.
	 */

	if ((rowHead != null) && rowHead.isVisible()) {
	    prefWidth += rowHead.getPreferredSize().width;
	}

	if ((colHead != null) && colHead.isVisible()) {
	    prefHeight += colHead.getPreferredSize().height;
	}

	/* If a scrollbar is going to appear, factor its preferred size in.
	 * If the scrollbars policy is AS_NEEDED, this can be a little
	 * tricky:
	 * 
	 * - If the view is a Scrollable then scrollableTracksViewportWidth
	 * and scrollableTracksViewportHeight can be used to effectively 
	 * disable scrolling (if they're true) in their respective dimensions.
	 * 
	 * - Assuming that a scrollbar hasn't been disabled by the 
	 * previous constraint, we need to decide if the scrollbar is going 
	 * to appear to correctly compute the JScrollPanes preferred size.
	 * To do this we compare the preferredSize of the viewport (the 
	 * extentSize) to the preferredSize of the view.  Although we're
	 * not responsible for laying out the view we'll assume that the 
	 * JViewport will always give it its preferredSize.
	 */

	if ((vsb != null) && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
	    if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
		prefWidth += vsb.getPreferredSize().width;
	    }
	    else if ((viewSize != null) && (extentSize != null)) {
		boolean canScroll = true;
		if (view instanceof Scrollable) {
		    canScroll = !((Scrollable)view).getScrollableTracksViewportHeight();
		}
		if (canScroll && (viewSize.height > extentSize.height)) {
		    prefWidth += vsb.getPreferredSize().width;
		}
	    }
	}

	if ((hsb != null) && (hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER)) {
	    if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
		prefHeight += hsb.getPreferredSize().height;
	    }
	    else if ((viewSize != null) && (extentSize != null)) {
		boolean canScroll = true;
		if (view instanceof Scrollable) {
		    canScroll = !((Scrollable)view).getScrollableTracksViewportWidth();
		}
		if (canScroll && (viewSize.width > extentSize.width)) {
		    prefHeight += hsb.getPreferredSize().height;
		}
	    }
	}

	return new Dimension(prefWidth, prefHeight);
    }


    /** 
     * The minimum size of a ScrollPane is the size of the insets 
     * plus minimum size of the viewport, plus the scrollpane's
     * viewportBorder insets, plus the minimum size 
     * of the visible headers, plus the minimum size of the 
     * scrollbars whose displayPolicy isn't NEVER.
     * 
     * @param parent the Container that will be laid out
     * @return a Dimension object specifying the minimum size
     */
    public Dimension minimumLayoutSize(Container parent) 
    {
	/* Sync the (now obsolete) policy fields with the
	 * JScrollPane.
	 */
	JScrollPane scrollPane = (JScrollPane)parent;
	vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
	hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

	Insets insets = parent.getInsets();
	int minWidth = insets.left + insets.right;
	int minHeight = insets.top + insets.bottom;
	
	/* If there's a viewport add its minimumSize.
	 */
	
	if (viewport != null) {
	    Dimension size = viewport.getMinimumSize();
	    minWidth += size.width;
	    minHeight += size.height;
	}

	/* If there's a JScrollPane.viewportBorder, add its insets.
	 */

	Border viewportBorder = scrollPane.getViewportBorder();
	if (viewportBorder != null) {
	    Insets vpbInsets = viewportBorder.getBorderInsets(parent);
	    minWidth += vpbInsets.left + vpbInsets.right;
	    minHeight += vpbInsets.top + vpbInsets.bottom;
	}

	/* If a header exists and it's visible, factor its
	 * minimum size in.
	 */

	if ((rowHead != null) && rowHead.isVisible()) {
	    Dimension size = rowHead.getMinimumSize();
	    minWidth += size.width;
	    minHeight = Math.max(minHeight, size.height);
	}

	if ((colHead != null) && colHead.isVisible()) {
	    Dimension size = colHead.getMinimumSize();
	    minWidth = Math.max(minWidth, size.width);
	    minHeight += size.height;
	}

	/* If a scrollbar might appear, factor its minimum
	 * size in.
	 */

	if ((vsb != null) && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
	    Dimension size = vsb.getMinimumSize();
	    minWidth += size.width;
	    minHeight = Math.max(minHeight, size.height);
	}

	if ((hsb != null) && (hsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
	    Dimension size = hsb.getMinimumSize();
	    minWidth = Math.max(minWidth, size.width);
	    minHeight += size.height;
	}

	return new Dimension(minWidth, minHeight);
    }


    /** 
     * Lay out the scrollpane. The positioning of components depends on
     * the following constraints:
     * <ul>
     * <li> The row header, if present and visible, gets its preferred
     * height and the viewports width.
     * 
     * <li> The column header, if present and visible, gets its preferred
     * width and the viewports height.
     * 
     * <li> If a vertical scrollbar is needed, i.e. if the viewports extent
     * height is smaller than its view height or if the displayPolicy
     * is ALWAYS, it's treated like the row header wrt it's dimensions and
     * it's made visible.
     * 
     * <li> If a horizontal scrollbar is needed it's treated like the
     * column header (and see the vertical scrollbar item).
     * 
     * <li> If the scrollpane has a non-null viewportBorder, then space
     * is allocated for that.
     * 
     * <li> The viewport gets the space available after accounting for
     * the previous constraints.
     * 
     * <li> The corner components, if provided, are aligned with the 
     * ends of the scrollbars and headers. If there's a vertical
     * scrollbar the right corners appear, if there's a horizontal
     * scrollbar the lower corners appear, a row header gets left
     * corners and a column header gets upper corners.
     * </ul>
     *
     * @param parent the Container to lay out
     */
    public void layoutContainer(Container parent) 
    {
	/* Sync the (now obsolete) policy fields with the
	 * JScrollPane.
	 */
	JScrollPane scrollPane = (JScrollPane)parent;
	vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
	hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

	Rectangle availR = scrollPane.getBounds();
	availR.x = availR.y = 0;

	Insets insets = parent.getInsets();
	availR.x = insets.left;
	availR.y = insets.top;
	availR.width -= insets.left + insets.right;
	availR.height -= insets.top + insets.bottom;

        /* Get the scrollPane's orientation.
         */
        boolean leftToRight = SwingUtilities.isLeftToRight(scrollPane);

	/* If there's a visible column header remove the space it 
	 * needs from the top of availR.  The column header is treated 
	 * as if it were fixed height, arbitrary width.
	 */

	Rectangle colHeadR = new Rectangle(0, availR.y, 0, 0);

	if ((colHead != null) && (colHead.isVisible())) {
	    int colHeadHeight = colHead.getPreferredSize().height;
	    colHeadR.height = colHeadHeight; 
	    availR.y += colHeadHeight;
	    availR.height -= colHeadHeight;
	}

	/* If there's a visible row header remove the space it needs
	 * from the left or right of availR.  The row header is treated 
	 * as if it were fixed width, arbitrary height.
	 */

	Rectangle rowHeadR = new Rectangle(0, 0, 0, 0);
	
	if ((rowHead != null) && (rowHead.isVisible())) {
	    int rowHeadWidth = rowHead.getPreferredSize().width;
	    rowHeadR.width = rowHeadWidth;
	    availR.width -= rowHeadWidth;
            if ( leftToRight ) {
                rowHeadR.x = availR.x;
                availR.x += rowHeadWidth;
            } else {
                rowHeadR.x = availR.x + availR.width;
            }
	}

	/* If there's a JScrollPane.viewportBorder, remove the
	 * space it occupies for availR.
	 */

	Border viewportBorder = scrollPane.getViewportBorder();
	Insets vpbInsets;
	if (viewportBorder != null) {
	    vpbInsets = viewportBorder.getBorderInsets(parent);
	    availR.x += vpbInsets.left;
	    availR.y += vpbInsets.top;
	    availR.width -= vpbInsets.left + vpbInsets.right;
	    availR.height -= vpbInsets.top + vpbInsets.bottom;
	}
	else {
	    vpbInsets = new Insets(0,0,0,0);
	}


	/* At this point availR is the space available for the viewport
	 * and scrollbars. rowHeadR is correct except for its height and y
         * and colHeadR is correct except for its width and x.  Once we're 
	 * through computing the dimensions  of these three parts we can 
	 * go back and set the dimensions of rowHeadR.height, rowHeadR.y,
         * colHeadR.width, colHeadR.x and the bounds for the corners.
	 * 
         * We'll decide about putting up scrollbars by comparing the 
         * viewport views preferred size with the viewports extent
	 * size (generally just its size).  Using the preferredSize is
	 * reasonable because layout proceeds top down - so we expect
	 * the viewport to be layed out next.  And we assume that the
	 * viewports layout manager will give the view it's preferred
	 * size.  One exception to this is when the view implements 
	 * Scrollable and Scrollable.getViewTracksViewport{Width,Height}
	 * methods return true.  If the view is tracking the viewports
	 * width we don't bother with a horizontal scrollbar, similarly
	 * if view.getViewTracksViewport(Height) is true we don't bother
	 * with a vertical scrollbar.
	 */

	Component view = (viewport != null) ? viewport.getView() : null;
	Dimension viewPrefSize =  
	    (view != null) ? view.getPreferredSize() 
                           : new Dimension(0,0);

	Dimension extentSize = 
	    (viewport != null) ? viewport.toViewCoordinates(availR.getSize()) 
	                       : new Dimension(0,0);

	boolean viewTracksViewportWidth = false;
	boolean viewTracksViewportHeight = false;
	Scrollable sv;
	if (view instanceof Scrollable) {
	    sv = (Scrollable)view;
	    viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
	    viewTracksViewportHeight = sv.getScrollableTracksViewportHeight();
	}
	else {
	    sv = null;
	}

	/* If there's a vertical scrollbar and we need one, allocate
	 * space for it (we'll make it visible later). A vertical 
	 * scrollbar is considered to be fixed width, arbitrary height.
	 */

	Rectangle vsbR = new Rectangle(0, availR.y - vpbInsets.top, 0, 0);

	boolean vsbNeeded;
	if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
	    vsbNeeded = true;
	}
	else if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
	    vsbNeeded = false;
	}
	else {  // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
	    vsbNeeded = !viewTracksViewportHeight && (viewPrefSize.height > extentSize.height);
	}


	if ((vsb != null) && vsbNeeded) {
	    adjustForVSB(true, availR, vsbR, vpbInsets, leftToRight);
	    extentSize = viewport.toViewCoordinates(availR.getSize());
	}
	
	/* If there's a horizontal scrollbar and we need one, allocate
	 * space for it (we'll make it visible later). A horizontal 
	 * scrollbar is considered to be fixed height, arbitrary width.
	 */

	Rectangle hsbR = new Rectangle(availR.x - vpbInsets.left, 0, 0, 0);
	boolean hsbNeeded;
	if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
	    hsbNeeded = true;
	}
	else if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
	    hsbNeeded = false;
	}
	else {  // hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED
	    hsbNeeded = !viewTracksViewportWidth && (viewPrefSize.width > extentSize.width);
	}

	if ((hsb != null) && hsbNeeded) {
	    adjustForHSB(true, availR, hsbR, vpbInsets);

	    /* If we added the horizontal scrollbar then we've implicitly 
	     * reduced  the vertical space available to the viewport. 
	     * As a consequence we may have to add the vertical scrollbar, 
	     * if that hasn't been done so already.  Ofcourse we
	     * don't bother with any of this if the vsbPolicy is NEVER.
	     */
	    if ((vsb != null) && !vsbNeeded &&
		(vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

		extentSize = viewport.toViewCoordinates(availR.getSize());
		vsbNeeded = viewPrefSize.height > extentSize.height;

		if (vsbNeeded) {
		    adjustForVSB(true, availR, vsbR, vpbInsets, leftToRight);
		}
	    }
	}

	/* Set the size of the viewport first, and then recheck the Scrollable
	 * methods. Some components base their return values for the Scrollable
	 * methods on the size of the Viewport, so that if we don't
	 * ask after resetting the bounds we may have gotten the wrong
	 * answer.
	 */
	
	if (viewport != null) {
	    viewport.setBounds(availR);

	    if (sv != null) {
		extentSize = viewport.toViewCoordinates(availR.getSize());

		boolean oldHSBNeeded = hsbNeeded;
		boolean oldVSBNeeded = vsbNeeded;
		viewTracksViewportWidth = sv.
		                          getScrollableTracksViewportWidth();
		viewTracksViewportHeight = sv.
		                          getScrollableTracksViewportHeight();
		if (vsb != null && vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
		    boolean newVSBNeeded = !viewTracksViewportHeight &&
		                     (viewPrefSize.height > extentSize.height);
		    if (newVSBNeeded != vsbNeeded) {
			vsbNeeded = newVSBNeeded;
			adjustForVSB(vsbNeeded, availR, vsbR, vpbInsets,
				     leftToRight);
			extentSize = viewport.toViewCoordinates
			                      (availR.getSize());
		    }
		}
		if (hsb != null && hsbPolicy ==HORIZONTAL_SCROLLBAR_AS_NEEDED){
		    boolean newHSBbNeeded = !viewTracksViewportWidth &&
			               (viewPrefSize.width > extentSize.width);
		    if (newHSBbNeeded != hsbNeeded) {
			hsbNeeded = newHSBbNeeded;
			adjustForHSB(hsbNeeded, availR, hsbR, vpbInsets);
			if ((vsb != null) && !vsbNeeded &&
			    (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

			    extentSize = viewport.toViewCoordinates
				         (availR.getSize());
			    vsbNeeded = viewPrefSize.height >
				        extentSize.height;

			    if (vsbNeeded) {
				adjustForVSB(true, availR, vsbR, vpbInsets,
					     leftToRight);
			    }
			}
		    }
		}
		if (oldHSBNeeded != hsbNeeded ||
		    oldVSBNeeded != vsbNeeded) {
		    viewport.setBounds(availR);
		    // You could argue that we should recheck the
		    // Scrollable methods again until they stop changing,
		    // but they might never stop changing, so we stop here
		    // and don't do any additional checks.
		}
	    }
	}

	/* We now have the final size of the viewport: availR.
	 * Now fixup the header and scrollbar widths/heights.
	 */
	vsbR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
	hsbR.width = availR.width + vpbInsets.left + vpbInsets.right;
	rowHeadR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
        rowHeadR.y = availR.y - vpbInsets.top;
	colHeadR.width = availR.width + vpbInsets.left + vpbInsets.right;
        colHeadR.x = availR.x - vpbInsets.left;

	/* Set the bounds of the remaining components.  The scrollbars
	 * are made invisible if they're not needed.
	 */
	
	if (rowHead != null) {
	    rowHead.setBounds(rowHeadR);
	}

	if (colHead != null) {
	    colHead.setBounds(colHeadR);
	}

	if (vsb != null) {
	    if (vsbNeeded) {
		vsb.setVisible(true);
		vsb.setBounds(vsbR);
	    }
	    else {
		vsb.setVisible(false);
	    }
	}

	if (hsb != null) {
	    if (hsbNeeded) {
		hsb.setVisible(true);
		hsb.setBounds(hsbR);
	    }
	    else {
		hsb.setVisible(false);
	    }
	}

	if (lowerLeft != null) {
	    lowerLeft.setBounds(leftToRight ? rowHeadR.x : vsbR.x,
                                hsbR.y,
                                leftToRight ? rowHeadR.width : vsbR.width,
                                hsbR.height);
	}

	if (lowerRight != null) {
	    lowerRight.setBounds(leftToRight ? vsbR.x : rowHeadR.x,
                                 hsbR.y,
                                 leftToRight ? vsbR.width : rowHeadR.width,
                                 hsbR.height);
	}

	if (upperLeft != null) {
	    upperLeft.setBounds(leftToRight ? rowHeadR.x : vsbR.x,
                                colHeadR.y,
                                leftToRight ? rowHeadR.width : vsbR.width,
                                colHeadR.height);
	}

	if (upperRight != null) {
	    upperRight.setBounds(leftToRight ? vsbR.x : rowHeadR.x,
                                 colHeadR.y,
                                 leftToRight ? vsbR.width : rowHeadR.width,
                                 colHeadR.height);
	}
    }

    /**
     * Adjusts the Rectangle <code>available</code> based on if
     * the vertical scrollbar is needed (<code>wantsVSB</code>).
     * The location of the vsb is updated in <code>vsbR</code>, and
     * the viewport border insets (<code>vpbInsets</code>) are used to offset
     * the vsb.
     */
    private void adjustForVSB(boolean wantsVSB, Rectangle available,
			      Rectangle vsbR, Insets vpbInsets, 
                              boolean leftToRight) {
	int vsbWidth = vsb.getPreferredSize().width;
	if (wantsVSB) {
	    available.width -= vsbWidth;
	    vsbR.width = vsbWidth;
            
            if( leftToRight ) {
                vsbR.x = available.x + available.width + vpbInsets.right;
            } else {
                vsbR.x = available.x - vpbInsets.left;
                available.x += vsbWidth;
            }
	}
	else {
	    available.width += vsbWidth;
	}
    }

    /**
     * Adjusts the Rectangle <code>available</code> based on if
     * the horizontal scrollbar is needed (<code>wantsHSB</code>).
     * The location of the hsb is updated in <code>hsbR</code>, and
     * the viewport border insets (<code>vpbInsets</code>) are used to offset
     * the hsb.
     */
    private void adjustForHSB(boolean wantsHSB, Rectangle available,
			      Rectangle hsbR, Insets vpbInsets) {
	int hsbHeight = hsb.getPreferredSize().height;
	if (wantsHSB) {
	    available.height -= hsbHeight;
	    hsbR.y = available.y + available.height + vpbInsets.bottom;
	    hsbR.height = hsbHeight;
	}
	else {
	    available.height += hsbHeight;
	}
    }

    

    /** 
     * Returns the bounds of the border around the specified scroll pane's 
     * viewport.
     *
     * @return the size and position of the viewport border
     * @deprecated As of JDK version Swing1.1
     *    replaced by <code>JScrollPane.getViewportBorderBounds()</code>.
     */
    public Rectangle getViewportBorderBounds(JScrollPane scrollpane) {
	return scrollpane.getViewportBorderBounds();
    }

    /**
     * The UI resource version of ScrollPaneLayout.
     */
    public static class UIResource extends ScrollPaneLayout implements javax.swing.plaf.UIResource {}
}
