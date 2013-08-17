/*
 * @(#)ScrollPane.java	1.48 97/03/13
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.awt.peer.ScrollPanePeer;
import java.awt.event.*;
import java.io.Serializable;


/**
 * A container class which implements automatic horizontal and/or
 * vertical scrolling for a single child component.  The display
 * policy for the scrollbars can be set to:
 * <OL>
 * <LI>as needed: scrollbars created and shown only when needed by scrollpane
 * <LI>always: scrollbars created and always shown by the scrollpane
 * <LI>never: scrollbars never created or shown by the scrollpane
 * </OL>
 * <P>
 * The state of the positioning of the child component is represented
 * by two objects (one for each dimension) which implement the
 * Adjustable interface.  The API provides methods to access those
 * objects such that the attributes on the Adjustable object (such as unitIncrement,
 * value, etc.) can be manipulated.
 * <P>
 * Certain adjustable properties (minimum, maximum, and visibleAmount) 
 * are set internally by the scrollpane in accordance with the geometry 
 * of the scrollpane and its child and these should not be set by programs 
 * using the scrollpane.
 * <P>
 * If the scrollbar display policy is defined as "never", then the
 * scrollpane can still be programmatically scrolled using the 
 * setScrollPosition() method and the scrollpane will move and clip
 * the child's contents appropriately.  This policy is useful if the
 * program needs to create and manage its own adjustable controls.
 * <P>
 * The placement of the scrollbars is controlled by platform-specific
 * properties set by the user outside of the program.
 * <P>
 * The initial size of this container is set to 100x100, but can
 * be reset using setSize(). 
 * <P>
 * Insets are used to define any space used by scrollbars and any
 * borders created by the scroll pane. getInsets() can be used
 * to get the current value for the insets.  If the value of 
 * scrollbarsAlwaysVisible is false, then the value of the insets
 * will change dynamically depending on whether the scrollbars are
 * currently visible or not.   
 *
 * @version     1.48 03/13/97
 * @author      Tom Ball
 * @author      Amy Fowler
 * @author      Tim Prinzing
 */
public class ScrollPane extends Container {

    /**
     * Specifies that horizontal/vertical scrollbar should be shown 
     * only when the size of the child exceeds the size of the scrollpane
     * in the horizontal/vertical dimension.
     */
    public static final int SCROLLBARS_AS_NEEDED = 0;

    /**
     * Specifies that horizontal/vertical scrollbars should always be
     * shown regardless of the respective sizes of the scrollpane and child.
     */
    public static final int SCROLLBARS_ALWAYS = 1;

    /**
     * Specifies that horizontal/vertical scrollbars should never be shown
     * regardless of the respective sizes of the scrollpane and child.
     */
    public static final int SCROLLBARS_NEVER = 2;

    private int scrollbarDisplayPolicy;
    private ScrollPaneAdjustable vAdjustable;
    private ScrollPaneAdjustable hAdjustable;

    private static final String base = "scrollpane";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 7956609840827222915L;

    /**
     * Create a new scrollpane container with a scrollbar display policy of
     * "as needed".
     */
    public ScrollPane() {
	this(SCROLLBARS_AS_NEEDED);
    }

    /**
     * Create a new scrollpane container.
     * @param scrollbarDisplayPolicy policy for when scrollbars should be shown
     */
    public ScrollPane(int scrollbarDisplayPolicy) {
        this.name = base + nameCounter++;
	this.layoutMgr = null;
	this.width = 100;
	this.height = 100;
	switch (scrollbarDisplayPolicy) {
	  case SCROLLBARS_NEVER:
	  case SCROLLBARS_AS_NEEDED:
	  case SCROLLBARS_ALWAYS:
	    this.scrollbarDisplayPolicy = scrollbarDisplayPolicy;
	    break;
	  default:
	    throw new IllegalArgumentException("illegal scrollbar display policy");
	}

	vAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this), 
					       Adjustable.VERTICAL);
	hAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this), 
					       Adjustable.HORIZONTAL);
    }

    /** 
     * Adds the specified component to this scroll pane container.
     * If the scroll pane has an existing child component, that
     * component is removed and the new one is added.  
     * @param comp the component to be added 
     * @param constraints  not applicable
     * @param index position of child component (must be <= 0) 
     */
    protected final void addImpl(Component comp, Object constraints, int index) {
    	synchronized (Component.LOCK) {
	    if (getComponentCount() > 0) {
		remove(0);
	    }
	    if (index > 0) {
		throw new IllegalArgumentException("position greater than 0");
	    }
		
	    super.addImpl(comp, constraints, index);
	}
    }

    /**
     * Returns the display policy for the scrollbars.
     * @return the display policy for the scrollbars
     */
    public int getScrollbarDisplayPolicy() {
        return scrollbarDisplayPolicy;
    }

    /**
     * Returns the current size of the scroll pane's view port.
     * @return the size of the view port in pixels
     */
    public Dimension getViewportSize() {
	Insets i = getInsets();
	return new Dimension(width - i.right - i.left,
			     height - i.top - i.bottom);
    }

    /**
     * Returns the height that would be occupied by a horizontal
     * scrollbar, which is independent of whether it is currently
     * displayed by the scroll pane or not.
     * @return the height of a horizontal scrollbar in pixels
     */
    public int getHScrollbarHeight() {
	int h = 0;
	if (scrollbarDisplayPolicy != SCROLLBARS_NEVER) {
	    ScrollPanePeer peer = (ScrollPanePeer)this.peer;
	    if (peer != null) {
		h = peer.getHScrollbarHeight();
	    }
	}
	return h;
    }

    /**
     * Returns the width that would be occupied by a vertical
     * scrollbar, which is independent of whether it is currently
     * displayed by the scroll pane or not.
     * @return the width of a vertical scrollbar in pixels
     */
    public int getVScrollbarWidth() {
	int w = 0;
	if (scrollbarDisplayPolicy != SCROLLBARS_NEVER) {
	    ScrollPanePeer peer = (ScrollPanePeer)this.peer;
	    if (peer != null) {
		w = peer.getVScrollbarWidth();
	    }
	}
	return w;
    }

    /**
     * Returns the Adjustable object which represents the state of
     * the vertical scrollbar. 
     */
    public Adjustable getVAdjustable() {
        return vAdjustable;
    }

    /**
     * Returns the Adjustable object which represents the state of
     * the horizontal scrollbar.
     */
    public Adjustable getHAdjustable() {
        return hAdjustable;
    }

    /**
     * Scrolls to the specified position within the child component.
     * A call to this method is only valid if the scroll pane contains
     * a child.  Specifying a position outside of the legal scrolling bounds
     * of the child will scroll to the closest legal position.  
     * Legal bounds are defined to be the rectangle: 
     * x = 0, y = 0, width = (child width - view port width),
     * height = (child height - view port height).
     * This is a convenience method which interfaces with the Adjustable
     * objects which respresent the state of the scrollbars.
     * @param x the x position to scroll to
     * @param y the y position to scroll to
     */
    public void setScrollPosition(int x, int y) {
    	synchronized (Component.LOCK) {
	    if (ncomponents <= 0) {
		throw new NullPointerException("child is null");
	    }
	    hAdjustable.setValue(x);
	    vAdjustable.setValue(y);
	}
    }

   /**
     * Scrolls to the specified position within the child component.
     * A call to this method is only valid if the scroll pane contains
     * a child and the specified position is within legal scrolling bounds
     * of the child.  Legal bounds are defined to be the rectangle: 
     * x = 0, y = 0, width = (child width - view port width),
     * height = (child height - view port height).
     * This is a convenience method which interfaces with the Adjustable
     * objects which respresent the state of the scrollbars.
     * @param p the Point representing the position to scroll to
     */
    public void setScrollPosition(Point p) {
        setScrollPosition(p.x, p.y);
    }

    /**
     * Returns the current x,y position within the child which is displayed 
     * at the 0,0 location of the scrolled panel's view port.
     * This is a convenience method which interfaces with the adjustable
     * objects which respresent the state of the scrollbars.
     * @return the coordinate position for the current scroll position
     */
    public Point getScrollPosition() {
	if (ncomponents <= 0) {
	    throw new NullPointerException("child is null");
	}
	return new Point(hAdjustable.getValue(), vAdjustable.getValue()); 
    }

    /** 
     * Sets the layout manager for this container.  This method is
     * overridden to prevent the layout mgr from being set.
     * @param mgr the specified layout manager
     */
    public final void setLayout(LayoutManager mgr) {
	throw new AWTError("ScrollPane controls layout");
    }

    /**
     * Lays out this container by resizing its child to its preferred size.
     * If the new preferred size of the child causes the current scroll
     * position to be invalid, the scroll position is set to the closest
     * valid position.
     *
     * @see Component#validate
     */
    public void doLayout() {
	layout();
    }

    /**
     * Determine the size to allocate the child component.
     * If the viewport area is bigger than the childs 
     * preferred size then the child is allocated enough
     * to fill the viewport, otherwise the child is given
     * it's preferred size.
     */
    Dimension calculateChildSize() {
	Component c = getComponent(0);
	Dimension cs = new Dimension(c.getPreferredSize());
	Dimension size = getSize();
	int vbarWidth = getVScrollbarWidth(); 
	int hbarHeight = getHScrollbarHeight();
	Insets i = getInsets();
	boolean vbarOn = ((i.left + i.right) >= vbarWidth);
	boolean hbarOn = ((i.top + i.bottom) >= hbarHeight);
	int viewWidth = size.width - (i.left + i.right) - 
	    (vbarOn ? 0 : vbarWidth);
	int viewHeight = size.height - (i.top + i.bottom) - 
	    (hbarOn ? 0 : hbarHeight);
	boolean hbarNeeded = (cs.width > viewWidth);
	boolean vbarNeeded = (cs.height > viewHeight);
	
	if (cs.width < viewWidth) {
	    cs.width = viewWidth + (vbarNeeded ? 0 : vbarWidth);
	}
	if (cs.height < viewHeight) {
	    cs.height = viewHeight + (hbarNeeded ? 0 : hbarHeight);
	}

	return cs;
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by doLayout().
     */
    public void layout() {
	if (ncomponents > 0) {
	    Component c = getComponent(0);
	    Point p = c.getLocation();
	    Dimension cs = calculateChildSize();
	    Dimension vs = getViewportSize();	

	    // update the child
	    int xPos = (cs.width > vs.width ?
			   Math.min(-p.x, cs.width - vs.width) :
			   -p.x);
	    int yPos = (cs.height > vs.height ?
			   Math.min(-p.y, cs.height - vs.height) :
			   -p.y);
	    c.reshape(-xPos, -yPos, cs.width, cs.height);
	    ScrollPanePeer peer = (ScrollPanePeer)this.peer;
	    if (peer != null) {
	        peer.childResized(cs.width, cs.height);
	    }

	    // update adjustables... the viewport size may have changed
	    // with the scrollbars coming or going so the viewport size
	    // is updated before the adjustables.
	    vs = getViewportSize();
	    hAdjustable.setSpan(0, cs.width, vs.width);
	    vAdjustable.setSpan(0, cs.height, vs.height);
	}
    }

    /** 
     * Prints the component in this scroll pane.
     * @param g the specified Graphics window
     * @see Component#print
     * @see Component#printAll
     */
    public void printComponents(Graphics g) {
	if (ncomponents > 0) {
	    Component c = component[0];
	    Point p = c.getLocation();
	    Dimension vs = getViewportSize();

	    Graphics cg = g.create();
	    try {
	        cg.translate(p.x, p.y);
		cg.clipRect(-(p.x), -(p.y), vs.width, vs.height);
		c.printAll(cg);
	    } finally {
		cg.dispose();
	    }
	}
    }

    /**
     * Creates the scroll pane's peer. 
     */
    public void addNotify() {
	peer = getToolkit().createScrollPane(this);
	super.addNotify();
	
	if (getComponentCount() > 0) {
	    Component comp = getComponent(0);
	    if (comp.peer instanceof java.awt.peer.LightweightPeer) {
		synchronized(Component.LOCK) {
		    // The scrollpane won't work with a windowless child... it assumes
		    // it is moving a child window around so the windowless child is
		    // wrapped with a window.
		    remove(0);
		    Panel child = new Panel();
		    child.setLayout(new BorderLayout());
		    child.add(comp);
		    add(child);
		}
	    }
	}
    }

    public String paramString() {
	String sdpStr;
	switch (scrollbarDisplayPolicy) {
	    case SCROLLBARS_AS_NEEDED:
		sdpStr = "as-needed";
		break;
	    case SCROLLBARS_ALWAYS:
		sdpStr = "always";
		break;
	    case SCROLLBARS_NEVER:
		sdpStr = "never";
		break;
	    default:
		sdpStr = "invalid display policy";
	}
	Point p = ncomponents > 0? getScrollPosition() : new Point(0,0);
	Insets i = getInsets();		
	return super.paramString()+",ScrollPosition=("+p.x+","+p.y+")"+
	    ",Insets=("+i.top+","+i.left+","+i.bottom+","+i.right+")"+
	    ",ScrollbarDisplayPolicy="+sdpStr;
    }

    class PeerFixer implements AdjustmentListener, java.io.Serializable {

	PeerFixer(ScrollPane scroller) {
	    this.scroller = scroller;
	}

	/**
	 * Invoked when the value of the adjustable has changed.
	 */   
        public void adjustmentValueChanged(AdjustmentEvent e) {
	    Adjustable adj = e.getAdjustable();
	    int value = e.getValue();
	    ScrollPanePeer peer = (ScrollPanePeer) scroller.peer;
	    if (peer != null) {
		peer.setValue(adj, value);
	    }
	    
	    Component c = scroller.getComponent(0);
	    switch(adj.getOrientation()) {
	    case Adjustable.VERTICAL:
		c.move(c.getLocation().x, -(value));
		break;
	    case Adjustable.HORIZONTAL:
		c.move(-(value), c.getLocation().y);
		break;
	    default:
		throw new IllegalArgumentException("Illegal adjustable orientation");
	    }
	}

        private ScrollPane scroller;
    }
}


class ScrollPaneAdjustable implements Adjustable, java.io.Serializable {

    private ScrollPane sp;
    private int orientation;
    private int minimum;
    private int maximum;
    private int visibleAmount;
    private int unitIncrement = 1;
    private int blockIncrement = 1;
    private int value;
    private AdjustmentListener adjustmentListener;

    private static final String SCROLLPANE_ONLY = 
        "Can be set by scrollpane only";

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -3359745691033257079L;

    public ScrollPaneAdjustable(ScrollPane sp, AdjustmentListener l, int orientation) {
        this.sp = sp;
        this.orientation = orientation;
	addAdjustmentListener(l);
    }

    /**
     * This is called by the scrollpane itself to update the 
     * min,max,visible values.  The scrollpane is the only one 
     * that should be changing these since it is the source of
     * these values.
     */
    void setSpan(int min, int max, int visible) {
	// adjust the values to be reasonable
	minimum = min;
	maximum = Math.max(max, minimum + 1);
	visibleAmount = Math.min(visible, maximum - minimum);
	visibleAmount = Math.max(visibleAmount, 1);
        blockIncrement = Math.max((int)(visible * .90), 1);
	setValue(value);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setMinimum(int min) {
	throw new AWTError(SCROLLPANE_ONLY);
    }

    public int getMinimum() {
        return 0;
    }

    public void setMaximum(int max) {
	throw new AWTError(SCROLLPANE_ONLY);
    }   

    public int getMaximum() {
        return maximum;
    }

    public synchronized void setUnitIncrement(int u) {
	if (u != unitIncrement) {
	    unitIncrement = u;
	    if (sp.peer != null) {
		ScrollPanePeer peer = (ScrollPanePeer) sp.peer;
		peer.setUnitIncrement(this, u);
	    }
	}
    } 
  
    public int getUnitIncrement() {
        return unitIncrement;
    }

    public synchronized void setBlockIncrement(int b) {
        blockIncrement = b;
    }    

    public int getBlockIncrement() {
        return blockIncrement;
    }

    public void setVisibleAmount(int v) {
	throw new AWTError(SCROLLPANE_ONLY);
    }

    public int getVisibleAmount() {
        return visibleAmount;
    }

    public void setValue(int v) {
	// bounds check
	v = Math.max(v, minimum);
	v = Math.min(v, maximum - visibleAmount);

        if (v != value) {
	    value = v;
	    // Synchronously notify the listeners so that they are 
	    // guaranteed to be up-to-date with the Adjustable before
	    // it is mutated again.
	    AdjustmentEvent e = 
		new AdjustmentEvent(this, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
				    AdjustmentEvent.TRACK, value);
	    adjustmentListener.adjustmentValueChanged(e);
	}
    }

    public int getValue() {
        return value;
    }

    public synchronized void addAdjustmentListener(AdjustmentListener l) {
	adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
    }

    public synchronized void removeAdjustmentListener(AdjustmentListener l){
	adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
    }

    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }

    public String paramString() {
        return ((orientation==Adjustable.VERTICAL?"vertical,":"horizontal,")+
	      "[0.."+maximum+"],"+"val="+value+",vis="+visibleAmount+
                ",unit="+unitIncrement+",block="+blockIncrement);
    }

}

