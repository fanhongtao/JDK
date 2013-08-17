/*
 * @(#)JScrollPane.java	1.62 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.accessibility.*;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Color;
import java.awt.LayoutManager;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * A specialized container that manages a viewport, optional
 * vertical and horizontal scrollbars, and optional row and
 * column heading viewports.
 * <p>
 * <TABLE ALIGN="RIGHT" BORDER="0">
 *    <TR>
 *    <TD ALIGN="CENTER">
 *      <P ALIGN="CENTER"><IMG SRC="doc-files/JScrollPane-1.gif" WIDTH="256" HEIGHT="248" ALIGN="BOTTOM" BORDER="0">
 *    </TD>
 *    </TR>
 * </TABLE>
 * The JViewport provides a window, or &quot;viewport&quot; onto a data 
 * source -- for example, a text file. That data source is the 
 * &quot;scrollable client&quot; (aka data model) displayed by the 
 * JViewport view. A JScrollPane basically consists of JScrollBars, a JViewport, 
 * and the wiring between them, as shown in the diagram at right. 
 * <p>
 * In addition to the scroll bars and viewport, a JScrollPane can have a
 * column header and a row header. Each of these is a JViewport object that
 * you specify with <code>setRowHeaderView</code>, and <code>setColumnHeaderView</code>.
 * The column header viewport automatically scrolls left and right, tracking
 * the left-right scrolling of the main viewport. (It never scrolls vertically,
 * however.) The row header acts in a similar fashion.
 * <p>
 * By default, the corners are empty. You can put a component into a corner using 
 * <code>setCorner</code>, in case you there is some function or decoration you
 * would like to add to the scroll pane. The size of corner components is
 * entirely determined by the size of the headers and scroll bars that surround them.
 * <p>
 * To add a border around the main viewport, you can use <code>setViewportBorder</code>. 
 * (Of course, you can also add a border around the whole scroll pane using
 * <code>setBorder</code>.)
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JScrollPane">JScrollPane</a> 
 * key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see JScrollBar
 * @see JViewport
 * @see #setViewportView
 * @see #setRowHeaderView
 * @see #setColumnHeaderView
 * @see #setCorner
 * @see #setViewportBorder
 * 
 * @beaninfo
 *     attribute: isContainer true
 *     attribute: containerDelegate getViewport
 *   description: A specialized container that manages a viewport, optional scrollbars and headers
 *
 * @version 1.62 11/29/01
 * @author Hans Muller
 */
public class JScrollPane extends JComponent implements ScrollPaneConstants, Accessible
{
    private Border viewportBorder;

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ScrollPaneUI";

    /** 
     * The display policy for the vertical scrollbar.
     * The default is JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED.
     * @see #setVerticalScrollBarPolicy
     */
    protected int verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED;


    /**
     * The display policy for the horizontal scrollbar.
     * The default is JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED.
     * @see #setHorizontalScrollBarPolicy
     */
    protected int horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED;


    /** 
     * The scrollpanes viewport child.  Default is an empty JViewport.
     * @see #setViewport
     */
    protected JViewport viewport;


    /**
     * The scrollpane's vertical scrollbar child.  Default is a JScrollBar.
     * @see #setVerticalScrollBar
     */
    protected JScrollBar verticalScrollBar;


    /**
     * The scrollpane's horizontal scrollbar child.  Default is a JScrollBar.
     * @see #setHorizontalScrollBar
     */
    protected JScrollBar horizontalScrollBar;


    /** 
     * The row header child.  Default is null.
     * @see #setRowHeader
     */
    protected JViewport rowHeader;


    /** 
     * The column header child.  Default is null.
     * @see #setColumnHeader
     */
    protected JViewport columnHeader;


    /**
     * The component to display in the lower left corner.  Default is null.
     * @see #setCorner
     */
    protected Component lowerLeft;


    /**
     * The component to display in the lower right corner.  Default is null.
     * @see #setCorner
     */
    protected Component lowerRight;


    /**
     * The component to display in the upper left corner.  Default is null.
     * @see #setCorner
     */
    protected Component upperLeft;


    /**
     * The component to display in the upper right corner.  Default is null.
     * @see #setCorner
     */
    protected Component upperRight;


    /**
     * Create a JScrollPane that displays the view component in a viewport
     * whose view position can be controlled with a pair of scrollbars.
     * The scrollbar policies specify when the scrollbars are displayed, 
     * e.g. if <code>vsbPolicy</code> is VERTICAL_SCROLLBAR_AS_NEEDED</code>
     * then the vertical scrollbar only appears if the view doesn't fit
     * vertically. The available policies settings are listed at 
     * {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
     * 
     * @see #setViewportView
     * 
     * @param view the Component to display in the scrollpanes viewport
     * @param vsbPolicy an int that specifies the vertical scrollbar policy
     * @param hsbPolicy an int that specifies the horizontal scrollbar policy
     */
    public JScrollPane(Component view, int vsbPolicy, int hsbPolicy) 
    {
	setLayout(new ScrollPaneLayout.UIResource());
        setVerticalScrollBarPolicy(vsbPolicy);
        setHorizontalScrollBarPolicy(hsbPolicy);
	setViewport(createViewport());
	setVerticalScrollBar(createVerticalScrollBar());
	setHorizontalScrollBar(createHorizontalScrollBar());
	if (view != null) {
	    setViewportView(view);
	}
        updateUI();
    }


    /**
     * Create a JScrollPane that displays the contents of the specified
     * component, where both horizontal and vertical scrollbars appear
     * whenever the component's contents are larger than the view.
     * 
     * @see #setViewportView
     * @param view the Component to display in the scrollpanes viewport
     */
    public JScrollPane(Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * Create an empty (no viewport view) JScrollPane with specified 
     * scrollbar policies. The available policies settings are listed at 
     * {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
     * 
     * @see #setViewportView
     * 
     * @param vsbPolicy an int that specifies the vertical scrollbar policy
     * @param hsbPolicy an int that specifies the horizontal scrollbar policy
     */
    public JScrollPane(int vsbPolicy, int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }


    /**
     * Create an empty (no viewport view) JScrollPane where both horizontal and vertical 
     * scrollbars appear when needed.
     */
    public JScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * Returns the L&F object that renders this component.
     *
     * @return the ScrollPaneUI object that renders this component
     * @see #setUI
     */
    public ScrollPaneUI getUI() {
        return (ScrollPaneUI)ui;
    }


    /**
     * Sets the ScrollPaneUI object that provides the look and feel for
     * this component.
     *
     * @param ui the ScrollPaneUI L&F object
     * @see #getUI
     */
    public void setUI(ScrollPaneUI ui) {
        super.setUI(ui);
    }


    /**
     * To be called when the default look and feel changes.
     * Replaces the current ScrollPaneUI object with a version 
     * from the current default LookAndFeel.
     *
     * @see JComponent#updateUI
     * @see UIManager#getUI
     */
    public void updateUI() {
        setUI((ScrollPaneUI)UIManager.getUI(this));
    }


    /**
     * Returns the key used to look up the ScrollPaneUI class that provides
     * the look and feel for JScrollPane.
     * 
     * @return "ScrollPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * 
     * @beaninfo
     *    hidden: true
     */
    public String getUIClassID() {
        return uiClassID;
    }



    /** 
     * Sets the layout manager for this JScrollPane. This method overrides 
     * setLayout in java.awt.Container to ensure that only LayoutManagers which
     * are subclasses of ScrollPaneLayout can be used in a JScrollPane.
     * 
     * @param layout the specified layout manager
     * @exception ClassCastException if layout is not a ScrollPaneLayout
     * @see java.awt.Container#getLayout
     * @see java.awt.Container#setLayout
     * 
     * @beaninfo
     *    hidden: true
     */
    public void setLayout(LayoutManager layout) {
        if ((layout == null) || (layout instanceof ScrollPaneLayout)) {
            super.setLayout(layout);
        }
	else {
	    String s = "layout of JScrollPane must be a ScrollPaneLayout";
	    throw new ClassCastException(s);
	}
    }


    /**
     * Returns true if this component paints every pixel
     * in its range. (In other words, it does not have a transparent
     * background or foreground.)
     *
     * @return The value of the opaque property
     * @see JComponent#isOpaque
     */
    public boolean isOpaque() {
        JViewport viewport;
        Component view;
        if( (viewport = getViewport()) != null    && 
            ((view = viewport.getView()) != null) &&
            ((view instanceof JComponent) && ((JComponent)view).isOpaque())) {
            if(((JComponent)view).getWidth()  >= viewport.getWidth() && 
               ((JComponent)view).getHeight() >= viewport.getHeight())
                return true;
        }
        return false;
    }


    /** 
     * Calls to revalidate() on any descendant of this JScrollPane, e.g. 
     * the viewports view, will cause a request to be queued that
     * will validate the JScrollPane and all its descendants.
     * 
     * @return true
     * @see JComponent#revalidate
     * 
     * @beaninfo
     *    hidden: true
     */
    public boolean isValidateRoot() {
        return true;
    }


    /**
     * Returns the vertical scroll bar policy value.
     * @return the vertical scrollbar policy
     * @see #setVerticalScrollBarPolicy
     */
    public int getVerticalScrollBarPolicy() {
        return verticalScrollBarPolicy;
    }


    /**
     * Determines when the vertical scrollbar appears in the scrollpane. 
     * Legal values are:
     * <ul>
     * <li>JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     * <li>JScrollPane.VERTICAL_SCROLLBAR_NEVER
     * <li>JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     * </ul>
     *
     * @see #getVerticalScrollBarPolicy
     * 
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The scrollpane vertical scrollbar policy
     *        enum: VERTICAL_SCROLLBAR_AS_NEEDED JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     *              VERTICAL_SCROLLBAR_NEVER JScrollPane.VERTICAL_SCROLLBAR_NEVER
     *              VERTICAL_SCROLLBAR_ALWAYS JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     */
    public void setVerticalScrollBarPolicy(int policy) {
	switch (policy) {
	case VERTICAL_SCROLLBAR_AS_NEEDED:
	case VERTICAL_SCROLLBAR_NEVER:
	case VERTICAL_SCROLLBAR_ALWAYS:
		break;
	default:
	    throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
	}
	int old = verticalScrollBarPolicy;
	verticalScrollBarPolicy = policy;
	firePropertyChange("verticalScrollBarPolicy", old, policy);
    }


    /**
     * Returns the horizontal scroll bar policy value.
     * @return the horizontal scrollbar policy.
     * @see #setHorizontalScrollBarPolicy
     */
    public int getHorizontalScrollBarPolicy() {
	return horizontalScrollBarPolicy;
    }


    /**
     * Determines when the horizontal scrollbar appears in the scrollpane. 
     * The options are:<ul>
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     * </ul>
     * 
     * @see #getHorizontalScrollBarPolicy
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The scrollpane scrollbar policy
     *        enum: HORIZONTAL_SCROLLBAR_AS_NEEDED JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
     *              HORIZONTAL_SCROLLBAR_NEVER JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     *              HORIZONTAL_SCROLLBAR_ALWAYS JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     */
    public void setHorizontalScrollBarPolicy(int policy) {
	switch (policy) {
	case HORIZONTAL_SCROLLBAR_AS_NEEDED:
	case HORIZONTAL_SCROLLBAR_NEVER:
	case HORIZONTAL_SCROLLBAR_ALWAYS:
		break;
	default:
	    throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
	}
	int old = horizontalScrollBarPolicy;
	horizontalScrollBarPolicy = policy;
	firePropertyChange("horizontalScrollBarPolicy", old, policy);
    }


    /**
     * Returns the value of the viewportBorder property.
     *
     * @return the Border object that surrounds the viewport
     * @see #setViewportBorder
     */
    public Border getViewportBorder() {
        return viewportBorder;
    }


    /**
     * Add a border around the viewport.  Note that the border isn't
     * set on the viewport directly, JViewport doesn't support the
     * JComponent border property.  Similarly setting the JScrollPanes
     * viewport doesn't effect the viewportBorder property.
     * <p>
     * The default value of this property is computed by the look
     * and feel implementation.
     *
     * @see #getViewportBorder
     * @see #setViewport
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The border around the viewport.
     */
    public void setViewportBorder(Border viewportBorder) {
        Border oldValue = this.viewportBorder;
        this.viewportBorder = viewportBorder;
        firePropertyChange("viewportBorder", oldValue, viewportBorder);
    }


    /**
     * Returns the bounds of the viewport border.
     *
     * @return a Rectangle object specifying the viewport border
     */
    public Rectangle getViewportBorderBounds()
    {
	Rectangle borderR = new Rectangle(getSize());

	Insets insets = getInsets();
	borderR.x = insets.left;
	borderR.y = insets.top;
	borderR.width -= insets.left + insets.right;
	borderR.height -= insets.top + insets.bottom;


	/* If there's a visible column header remove the space it 
	 * needs from the top of borderR.  
	 */

	JViewport colHead = getColumnHeader();
	if ((colHead != null) && (colHead.isVisible())) {
	    int colHeadHeight = colHead.getHeight();
	    borderR.y += colHeadHeight;
	    borderR.height -= colHeadHeight;
	}

	/* If there's a visible row header remove the space it needs
	 * from the left of borderR.  
	 */

	JViewport rowHead = getRowHeader();
	if ((rowHead != null) && (rowHead.isVisible())) {
	    int rowHeadWidth = rowHead.getWidth();
	    borderR.x += rowHeadWidth;
	    borderR.width -= rowHeadWidth;
	}

	/* If there's a visible vertical scrollbar remove the space it needs
	 * from the width of borderR.  
	 */
	JScrollBar vsb = getVerticalScrollBar();
	if ((vsb != null) && (vsb.isVisible())) {
	    borderR.width -= vsb.getWidth();
	}

	/* If there's a visible horizontal scrollbar remove the space it needs
	 * from the height of borderR.  
	 */
	JScrollBar hsb = getHorizontalScrollBar();
	if ((hsb != null) && (hsb.isVisible())) {
	    borderR.height -= hsb.getHeight();
	}

	return borderR;
    }


    /**
     * By default JScrollPane creates scrollbars that are instances
     * of this class.  Scrollbar overrides the getUnitIncrement
     * and getBlockIncrement methods so that, if the viewports view is 
     * a Scrollable, the view is asked to compute these values. Unless
     * the unit/block increment have been explicitly set.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see Scrollable
     * @see JScrollPane#createVerticalScrollBar
     * @see JScrollPane#createHorizontalScrollBar
     */
    protected class ScrollBar extends JScrollBar implements UIResource
    {
	/** 
         * Set to true when the unit increment has been explicitly set.
	 * If this is false the viewports view is obtained and if it
	 * is an instance of Scrollable the unit increment from it is used.
	 */
	private boolean unitIncrementSet;
	/** 
         * Set to true when the block increment has been explicitly set.
	 * If this is false the viewports view is obtained and if it
	 * is an instance of Scrollable the block increment from it is used.
	 */
	private boolean blockIncrementSet;

        /**
         * Create a scrollbar with the specified orientation, where the options
         * are:<ul>
         * <li>JScrollPane.VERTICAL_SCROLLBAR
         * <li>JScrollPane.HORIZONTAL_SCROLLBAR
         * </ul>
         *
         * @param orientation  an int specifying the orientation
         */
        public ScrollBar(int orientation) {
            super(orientation);
        }

	/**
	 * Messages super to set the value, and resets the
	 * <code>unitIncrementSet</code> instance variable to true.
	 */
	public void setUnitIncrement(int unitIncrement) { 
	    unitIncrementSet = true;
	    super.setUnitIncrement(unitIncrement);
	}

        /**
         * If the viewports view is a Scrollable then ask the view
         * to compute the unit increment.  Otherwise return
         * super.getUnitIncrement().
         * 
         * @see Scrollable#getScrollableUnitIncrement
         */
        public int getUnitIncrement(int direction) {
            JViewport vp = getViewport();
            if (!unitIncrementSet && (vp != null) &&
		(vp.getView() instanceof Scrollable)) {
                Scrollable view = (Scrollable)(vp.getView());
                Rectangle vr = vp.getViewRect();
                return view.getScrollableUnitIncrement(vr, getOrientation(), direction);
            }
            else {
                return super.getUnitIncrement(direction);
            }
        }

	/**
	 * Messages super to set the value, and resets the
	 * <code>blockIncrementSet</code> instance variable to true.
	 */
	public void setBlockIncrement(int blockIncrement) { 
	    blockIncrementSet = true;
	    super.setBlockIncrement(blockIncrement);
	}

        /**
         * If the viewports view is a Scrollable then ask the
         * view to compute the block increment.  Otherwise
         * the blockIncrement equals the viewports width
         * or height.  If there's no viewport reuurn 
         * super.getBlockIncrement().
         * 
         * @see Scrollable#getScrollableBlockIncrement
         */
        public int getBlockIncrement(int direction) {
            JViewport vp = getViewport();
            if (blockIncrementSet || vp == null) {
                return super.getBlockIncrement(direction);
            }
            else if (vp.getView() instanceof Scrollable) {
                Scrollable view = (Scrollable)(vp.getView());
                Rectangle vr = vp.getViewRect();
                return view.getScrollableBlockIncrement(vr, getOrientation(), direction);
            }
            else if (getOrientation() == VERTICAL) {
                return vp.getExtentSize().height;
            }
            else {
                return vp.getExtentSize().width;
            }
        }

    }


    /**
     * Used by ScrollPaneUI implementations to create the horizontal
     * scrollbar.  Returns a JScrollPane.ScrollBar by default.  Subclasses
     * may override this method to force ScrollPaneUI implementations to
     * use a JScrollBar subclass.
     *
     * @return a JScrollBar with a horizontal orientation
     * @see JScrollBar
     */
    public JScrollBar createHorizontalScrollBar() {
        return new ScrollBar(JScrollBar.HORIZONTAL);
    }


    /**
     * Returns the horizontal scroll bar.
     * @return the scrollbar that controls the viewports horizontal view position
     * @see #setHorizontalScrollBar
     */
    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }

    
    /**
     * Add the scrollbar that controls the viewports horizontal view position
     * to the scrollpane.  This is usually unneccessary, JScrollPane creates
     * horizontal and vertical scrollbars by default.
     * 
     * @see #createHorizontalScrollBar
     * @see #getHorizontalScrollBar
     * 
     * @beaninfo
     *        expert: true
     *         bound: true
     *   description: The horizontal scrollbar.
     */
    public void setHorizontalScrollBar(JScrollBar horizontalScrollBar) {
	JScrollBar old = getHorizontalScrollBar();
	this.horizontalScrollBar = horizontalScrollBar;
	add(horizontalScrollBar, HORIZONTAL_SCROLLBAR);
	firePropertyChange("horizontalScrollBar", old, horizontalScrollBar);
    }


    /**
     * Used by ScrollPaneUI implementations to create the vertical
     * scrollbar.  Returns a JScrollPane.ScrollBar by default.  Subclasses
     * may override this method to force ScrollPaneUI implementations to
     * use a JScrollBar subclass.
     *
     * @return a JScrollBar with a vertical orientation
     * @see JScrollBar
     */
    public JScrollBar createVerticalScrollBar() {
        return new ScrollBar(JScrollBar.VERTICAL);
    }


    /**
     * Returns the vertical scroll bar.
     * @return the scrollbar that controls the viewports vertical view position
     * @see #setVerticalScrollBar
     */
    public JScrollBar getVerticalScrollBar() {
	return verticalScrollBar;
    }


    /**
     * Add the scrollbar that controls the viewports vertical view position
     * to the scrollpane.  This is usually unneccessary, JScrollPane creates
     * vertical and vertical scrollbars by default.
     * 
     * @see #createVerticalScrollBar
     * @see #getVerticalScrollBar
     * 
     * @beaninfo
     *        expert: true
     *         bound: true
     *   description: The vertical scrollbar.
     */
    public void setVerticalScrollBar(JScrollBar verticalScrollBar) {
	JScrollBar old = getVerticalScrollBar();
	this.verticalScrollBar = verticalScrollBar;
	add(verticalScrollBar, VERTICAL_SCROLLBAR);
	firePropertyChange("verticalScrollBar", old, verticalScrollBar);
    }


    /**
     * Returns a new JViewport by default.  Used to create the
     * viewport (as needed) in <code>setViewportView</code>,
     * <code>setRowHeaderView</code>, and <code>setColumnHeaderView</code>.
     * Subclasses may override this method to return a subclass of JViewport.
     *
     * @return a JViewport
     */
    protected JViewport createViewport() {
        return new JViewport();
    }


    /**
     * Returns the current JViewport.
     *
     * @see #setViewport
     * @return the JViewport currently in use
     */
    public JViewport getViewport() {
        return viewport;
    }

    
    /**
     * Remove the old viewport (if there is one), force the
     * viewPosition of the new viewport to be in the +x,+y quadrant,
     * sync up the row and column headers (if there are any) with the
     * new viewport, and finally sync the scrollbars and
     * headers with the new viewport.
     * <p>
     * Most applications will find it more convenient to use setViewportView
     * to add a viewport and a view to the scrollpane.
     * 
     * @see #createViewport
     * @see #getViewport
     * @see #setViewportView
     * 
     * @beaninfo
     *       expert: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The viewport child for this scrollpane
     * 
     */
    public void setViewport(JViewport viewport) {
	JViewport old = getViewport();
	this.viewport = viewport;
	if (viewport != null) {
	    add(viewport, VIEWPORT);
	}
	else if (old != null) {
	    remove(old);
	}
	firePropertyChange("viewport", old, viewport);

	if (accessibleContext != null) {
	    ((AccessibleJScrollPane)accessibleContext).resetViewPort();
	}

	revalidate();
	repaint();
    }


    /**
     * Creates a viewport if neccessary and then sets its view.  Applications
     * that don't provide the view directly to the JScrollPane constructor
     * should use this method to specify the scrollable child that's going
     * to be displayed in the scrollpane, e.g.:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * </pre>
     * Applications should not add children directly to the scrollpane.
     *
     * @param view the Component to add to the viewport
     * @see #setViewport
     * @see JViewport#setView
     */
    public void setViewportView(Component view) {
        if (getViewport() == null) {
            setViewport(createViewport());
        }
        getViewport().setView(view);
    }



    /**
     * Returns the row header.
     * @return the JViewport for the row header
     * @see #setRowHeader
     */
    public JViewport getRowHeader() {
        return rowHeader;
    }


    /**
     * If an old rowHeader exists, remove it.  If the new rowHeader
     * isn't null, sync the y coordinate of the its viewPosition with
     * the viewport (if there is one) and then add it to the ScrollPane.
     * <p>
     * Most applications will find it more convenient to use setRowHeaderView
     * to add a row header component and its viewport to the scrollpane.
     * 
     * @see #getRowHeader
     * @see #setRowHeaderView
     * 
     * @beaninfo
     *        bound: true
     *       expert: true
     *  description: The row header child for this scrollpane
     */
    public void setRowHeader(JViewport rowHeader) {
	JViewport old = getRowHeader();
	this.rowHeader = rowHeader;	
	if (rowHeader != null) {
	    add(rowHeader, ROW_HEADER);
	}
	else if (old != null) {
	    remove(old);
	}
	firePropertyChange("rowHeader", old, rowHeader);
    }


    /**
     * Creates a row-header viewport if neccessary, sets
     * its view and then adds the row-header viewport
     * to the scrollpane.  For example:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setRowHeaderView(myBigComponentsRowHeader);
     * </pre>
     *
     * @see #setRowHeader
     * @see JViewport#setView
     * @param view the Component to display as the row header
     */
    public void setRowHeaderView(Component view) {
        if (getRowHeader() == null) {
            setRowHeader(createViewport());
        }
        getRowHeader().setView(view);
    }



    /**
     * Returns the column header.
     * @return a JViewport object for the column header 
     * @see #setColumnHeader
     */
    public JViewport getColumnHeader() {
        return columnHeader;
    }


    /**
     * If an old columnHeader exists, remove it.  If the new columnHeader
     * isn't null, sync the x coordinate of the its viewPosition with
     * the viewport (if there is one) and then add it to the ScrollPane.
     * <p>
     * Most applications will find it more convenient to use setRowHeaderView
     * to add a row header component and its viewport to the scrollpane.
     * 
     * @see #getColumnHeader
     * @see #setColumnHeaderView
     * 
     * @beaninfo
     *        bound: true
     *  description: The column header child for this scrollpane
     *    attribute: visualUpdate true
     */
    public void setColumnHeader(JViewport columnHeader) {
	JViewport old = getColumnHeader();
	this.columnHeader = columnHeader;	
	if (columnHeader != null) {
	    add(columnHeader, COLUMN_HEADER);
	}
	else if (old != null) {
	    remove(columnHeader);
	}
	firePropertyChange("columnHeader", old, columnHeader);

	revalidate();
	repaint();
    }



    /**
     * Creates a column-header viewport if neccessary, sets
     * its view and then adds the column-header viewport
     * to the scrollpane.  For example:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setColumnHeaderView(myBigComponentsColumnHeader);
     * </pre>
     * 
     * @see #setColumnHeader
     * @see JViewport#setView
     * 
     * @param view the Component to display as the column header
     */
    public void setColumnHeaderView(Component view) {
        if (getColumnHeader() == null) {
            setColumnHeader(createViewport());
        }
        getColumnHeader().setView(view);
    }


    /**
     * Returns the component at the specified corner. The
     * <code>key</code> value specifying the corner is one of:
     * <ul>
     * <li>JScrollPane.LOWER_LEFT_CORNER
     * <li>JScrollPane.LOWER_RIGHT_CORNER
     * <li>JScrollPane.UPPER_LEFT_CORNER
     * <li>JScrollPane.UPPER_RIGHT_CORNER
     * </ul>
     *
     * @see #setCorner
     * @return the Component at the specified corner
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
     * Adds a child that will appear in one of the scroll panes
     * corners, if there's room.   For example with both scrollbars
     * showing (on the right and bottom edges of the scrollpane) 
     * the lower left corner component will be shown in the space
     * between ends of the two scrollbars. Legal values for 
     * the <b>key</b> are:
     * <ul>
     * <li>JScrollPane.LOWER_LEFT_CORNER
     * <li>JScrollPane.LOWER_RIGHT_CORNER
     * <li>JScrollPane.UPPER_LEFT_CORNER
     * <li>JScrollPane.UPPER_RIGHT_CORNER
     * </ul>
     * <p>
     * Although "corner" isn't doesn't match any beans property
     * signature, PropertyChange events are generated with the
     * property name set to the corner key.
     * 
     * @param key identifies which corner the component will appear in
     * @param corner any component
     */
    public void setCorner(String key, Component corner) 
    {
	Component old;
	if (key.equals(LOWER_LEFT_CORNER)) {
	    old = lowerLeft;
	    lowerLeft = corner;
	}
	else if (key.equals(LOWER_RIGHT_CORNER)) {
	    old = lowerRight;
	    lowerRight = corner;
	}
	else if (key.equals(UPPER_LEFT_CORNER)) {
	    old = upperLeft;
	    upperLeft = corner;
	}
	else if (key.equals(UPPER_RIGHT_CORNER)) {
	    old = upperRight;
	    upperRight = corner;
	}
	else {
	    throw new IllegalArgumentException("invalid corner key");
	}
	add(corner, key);
	firePropertyChange(key, old, corner);
    }


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JScrollPane. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JScrollPane.
     */
    protected String paramString() {
        String viewportBorderString = (viewportBorder != null ?
				       viewportBorder.toString() : "");
        String viewportString = (viewport != null ?
				 viewport.toString() : "");
        String verticalScrollBarPolicyString;
        if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_AS_NEEDED";
        } else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_NEVER) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_NEVER";
        } else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_ALWAYS";
        } else verticalScrollBarPolicyString = "";
        String horizontalScrollBarPolicyString;
        if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
        } else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_NEVER";
        } else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_ALWAYS";
        } else horizontalScrollBarPolicyString = "";
        String horizontalScrollBarString = (horizontalScrollBar != null ?
					    horizontalScrollBar.toString()
					    : "");
        String verticalScrollBarString = (verticalScrollBar != null ?
					  verticalScrollBar.toString() : "");
        String columnHeaderString = (columnHeader != null ?
				     columnHeader.toString() : "");
        String rowHeaderString = (rowHeader != null ?
				  rowHeader.toString() : "");
        String lowerLeftString = (lowerLeft != null ?
				  lowerLeft.toString() : "");
        String lowerRightString = (lowerRight != null ?
				  lowerRight.toString() : "");
        String upperLeftString = (upperLeft != null ?
				  upperLeft.toString() : "");
        String upperRightString = (upperRight != null ?
				  upperRight.toString() : "");

        return super.paramString() +
        ",columnHeader=" + columnHeaderString +
        ",horizontalScrollBar=" + horizontalScrollBarString +
        ",horizontalScrollBarPolicy=" + horizontalScrollBarPolicyString +
        ",lowerLeft=" + lowerLeftString +
        ",lowerRight=" + lowerRightString +
        ",rowHeader=" + rowHeaderString +
        ",upperLeft=" + upperLeftString +
        ",upperRight=" + upperRightString +
        ",verticalScrollBar=" + verticalScrollBarString +
        ",verticalScrollBarPolicy=" + verticalScrollBarPolicyString +
        ",viewport=" + viewportString +
        ",viewportBorder=" + viewportBorderString;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJScrollPane();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJScrollPane extends AccessibleJComponent 
    implements ChangeListener {

        protected JViewport viewPort = null;

        public void resetViewPort() {
            viewPort.removeChangeListener(this);
            viewPort = JScrollPane.this.getViewport();
            viewPort.addChangeListener(this);
        }

        /**
         * Constructor to set up listener on viewport.
         */
        public AccessibleJScrollPane() {
            super();
            if (viewPort == null) {
               viewPort = JScrollPane.this.getViewport();
            }
            viewPort.addChangeListener(this);
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_PANE;
        }

        /**
         * Supports the change listener interface and fires property change
         */
        public void stateChanged(ChangeEvent e) {
            AccessibleContext ac = ((Accessible)JScrollPane.this).getAccessibleContext();
            if (ac != null) {
                ac.firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, new Boolean(false), new Boolean(true));
            }
        }
    }
}

