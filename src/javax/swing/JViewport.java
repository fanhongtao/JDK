/*
 * @(#)JViewport.java	1.53 98/08/28
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

package javax.swing;

import javax.swing.event.*;
import javax.swing.border.*;
import javax.accessibility.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.io.Serializable;


/**
 * The "viewport" or "porthole" through which you see the underlying
 * information. When you scroll, what moves is the viewport. Its like
 * peering through a camera's viewfinder. Moving the viewfinder upwards
 * brings new things into view at the top of the picture and loses
 * things that were at the bottom.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.53 08/28/98
 * @author Hans Muller
 * @author Philip Milne
 * @see JScrollPane
 */
public class JViewport extends JComponent implements Accessible
{
    /** True when the viewport dimensions have been determined. */
    protected boolean isViewSizeSet = false;

    /**
     * The last viewPosition that we've painted, so we know how
     * much of the backing store image is valid.
     */
    protected Point lastPaintPosition = null;

    /**
     * True when this viewport is maintaining an offscreen image of its
     * contents, so that some scrolling can take place using fast "bit-blit"
     * operations instead of by accessing the view object to construct the
     * display.
     */
    protected boolean backingStore = false;
    /** The view image used for a backing store. */
    transient protected Image backingStoreImage = null;

    /**
     * The scrollUnderway flag is used for components like JList.
     * When the downarrow key is pressed on a JList and the selected
     * cell is the last in the list, the scrollpane autoscrolls.
     * Here, the old selected cell needs repainting and so we need
     * a flag to make the viewport do the optimised painting
     * only when there is an explicit call to setViewPosition(Point).
     * When setBounds() is called through other routes,
     * the flag is off and the view repaints normally.
     * Another approach would be to remove this from the Viewport
     * class and have the JList manage this case by using
     * setBackingStoreEnabled().
     */
    protected boolean scrollUnderway = false;

    /*
     * Listener that's notified each time the view changes size.
     */
    private ComponentListener viewListener = null;

    /* Only one ChangeEvent is needed per JViewport instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    private transient ChangeEvent changeEvent = null;

    /** Create a JViewPort */
    public JViewport() {
        super();
        setLayout(createLayoutManager());
    }

    /**
     * Sets the Viewport's one lightweight child, which can be null.
     * (Since there is only one child which occupies the entire viewport,
     * the constraints and index arguments are ignored.)
     *
     * @param child       the Component ______________
     * @param constraints the Object ______________
     * @param index       the int ______________
     * @see #setView
     */
    protected void addImpl(Component child, Object constraints, int index) {
      setView(child);
    }


    /**
     * Removes the Viewport's one lightweight child.
     *
     * @see #setView
     */
    public void remove(Component child) {
        child.removeComponentListener(viewListener);
        super.remove(child);
    }


    /**
     * Overridden to scroll the view so that Rectangle within the
     * view becomes visible.
     *
     * @param contentRect the Rectangle to display
     */
    public void scrollRectToVisible(Rectangle contentRect) {
        Component view = getView();

        if (view == null) {
            return;
        } else {
            int     dx = 0, dy = 0;
            Rectangle bounds = getBounds();

            dx = positionAdjustment(bounds.width, contentRect.width, contentRect.x);
            dy = positionAdjustment(bounds.height, contentRect.height, contentRect.y);

            if (dx != 0 || dy != 0) {
                Point viewPosition = getViewPosition();
                setViewPosition(new Point(viewPosition.x - dx, viewPosition.y - dy));
                scrollUnderway = false;
            }
        }
    }

     /*  This method is used by the scrollToRect method to determine the
      *  proper direction and amount to move by. The integer variables are named
      *  width, but this method is applicable to height also. The code assumes that
      *  parentWidth/childWidth are positive and childAt can be negative.
      */
    private int positionAdjustment(int parentWidth, int childWidth, int childAt)    {

        //   +-----+
        //   | --- |     No Change
        //   +-----+
        if (childAt >= 0 && childWidth + childAt <= parentWidth)    {
            return 0;
        }

        //   +-----+
        //  ---------   No Change
        //   +-----+
        if (childAt <= 0 && childWidth + childAt >= parentWidth) {
            return 0;
        }

        //   +-----+          +-----+
        //   |   ----    ->   | ----|
        //   +-----+          +-----+
        if (childAt > 0 && childWidth <= parentWidth)    {
            return -childAt + parentWidth - childWidth;
        }

        //   +-----+             +-----+
        //   |  --------  ->     |--------
        //   +-----+             +-----+
        if (childAt >= 0 && childWidth >= parentWidth)   {
            return -childAt;
        }

        //   +-----+          +-----+
        // ----    |     ->   |---- |
        //   +-----+          +-----+
        if (childAt <= 0 && childWidth <= parentWidth)   {
            return -childAt;
        }

        //   +-----+             +-----+
        //-------- |      ->   --------|
        //   +-----+             +-----+
        if (childAt < 0 && childWidth >= parentWidth)    {
            return -childAt + parentWidth - childWidth;
        }

        return 0;
    }


    /**
     * The viewport "scrolls" it's child (called the "view") by the
     * normal parent/child clipping (typically the view is moved in
     * the opposite direction of the scroll).  A non-null border,
     * or non-zero insets, isn't supported, to prevent the geometry
     * of this component from becoming complex enough to inhibit
     * subclassing.  To create a JViewport with a border, add it to a
     * JPanel that has a border.
     *
     * @param border the Border to set
     */
    public final void setBorder(Border border) {
        if (border != null) {
            throw new IllegalArgumentException("JViewport.setBorder() not supported");
        }
    }


    /**
     * Returns the insets (border) dimensions as (0,0,0,0), since borders
     * are not supported on a JViewPort.
     *
     * @return new Insets(0, 0, 0, 0)
     * @see #setBorder
     */
    public final Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }

    /**
     * Returns an Insets object containing this JViewPort's inset
     * values.  The passed-in Insets object will be reinitialized, and
     * all existing values within this object are overwritten.
     *
     * @param insets the Insets object which can be reused.
     * @see #getInsets
     * @beaninfo
     *   expert: true
     */
    public final Insets getInsets(Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 0;
        return insets;
    }


    private Graphics getBackingStoreGraphics(Graphics g) {
        Graphics bsg = backingStoreImage.getGraphics();
        bsg.setColor(g.getColor());
        bsg.setFont(g.getFont());
        bsg.setClip(g.getClipBounds());
        return bsg;
    }


    private void paintViaBackingStore(Graphics g) {
        Graphics bsg = getBackingStoreGraphics(g);
        super.paint(bsg);
        g.drawImage(backingStoreImage, 0, 0, this);
    }

    /**
     * The JViewport overrides the default implementation of
     * this method (in JComponent) to return false. This ensures
     * that the drawing machinery will call the Viewport's paint()
     * implementation rather than messaging the JViewport's
     * children directly.
     *
     * @return false
     */
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }


    /**
     * Only used by the paint method below.
     */
    private Point getViewLocation() {
        Component view = getView();
        if (view != null) {
            return view.getLocation();
        }
        else {
            return new Point(0,0);
        }
    }

    /**
     * Depending on whether the backingStore is enabled,
     * either paint the image through the backing store or paint
     * just the recently exposed part, using the backing store
     * to "blit" the remainder.
     * <blockquote>
     * The term "blit" is the pronounced version of the PDP-10
     * BLT (BLock Transfer) instruction, which copied a block of
     * bits. (In case you were curious.)
     * </blockquote>
     *
     * @param g the Graphics context within which to paint
     */
    public void paint(Graphics g)
    {
        int width = getWidth();
        int height = getHeight();

        if ((width <= 0) || (height <= 0)) {
            return;
        }

        if (!backingStore) {
            super.paint(g);
            lastPaintPosition = getViewLocation();
            return;
        }

        // If the view is smaller than the viewport, we should set the
        // clip. Otherwise, as the bounds of the view vary, we will
        // blit garbage into the exposed areas.
        Rectangle viewBounds = getView().getBounds();
        g.clipRect(0, 0, viewBounds.width, viewBounds.height);

        if (backingStoreImage == null) {
            // Backing store is enabled but this is the first call to paint.
            // Create the backing store, paint it and then copy to g.
	    backingStoreImage = createImage(width, height);
            paintViaBackingStore(g);
        }
        else {
            if (!scrollUnderway || lastPaintPosition.equals(getViewLocation())) {
                // No scrolling happened: repaint required area via backing store.
                paintViaBackingStore(g);
            } else {
                // The image was scrolled. Manipulate the backing store and flush it to g.
                Point blitFrom = new Point();
                Point blitTo = new Point();
                Dimension blitSize = new Dimension();
                Rectangle blitPaint = new Rectangle();

                Point newLocation = getViewLocation();
                int dx = newLocation.x - lastPaintPosition.x;
                int dy = newLocation.y - lastPaintPosition.y;
                boolean canBlit = computeBlit(dx, dy, blitFrom, blitTo, blitSize, blitPaint);
                if (!canBlit) {
                    // The image was either moved diagonally or
                    // moved by more than the image size: paint normally.
                    paintViaBackingStore(g);
                } else {
                    int bdx = blitTo.x - blitFrom.x;
                    int bdy = blitTo.y - blitFrom.y;

                    // Move the relevant part of the backing store.
                    Graphics bsg = getBackingStoreGraphics(g);
                    bsg.copyArea(blitFrom.x, blitFrom.y, blitSize.width, blitSize.height, bdx, bdy);

                    // Paint the rest of the view; the part that has just been exposed.
                    Rectangle r = viewBounds.intersection(blitPaint);
                    bsg.setClip(r);
                    super.paint(bsg);

                    // Copy whole of the backing store to g.
                    g.drawImage(backingStoreImage, 0, 0, this);
                }
            }
        }
        lastPaintPosition = getViewLocation();
        scrollUnderway = false;
    }


    /**
     * Sets the bounds of this viewport.  If the viewports width
     * or height has changed, fire a StateChanged event.
     *
     * @param x left edge of the origin
     * @param y top edge of the origin
     * @param w width in pixels
     * @param h height in pixels
     *
     * @see JComponent#reshape(int, int, int, int)
     */
    public void reshape(int x, int y, int w, int h) {
	boolean sizeChanged = (getWidth() != w) || (getHeight() != h);
        if (sizeChanged) {
            backingStoreImage = null;
        }
        super.reshape(x, y, w, h);
	if (sizeChanged) {
	    fireStateChanged();
	}
    }


    /**
     * Returns true if this viewport is maintaining an offscreen
     * image of its contents.
     */
    public boolean isBackingStoreEnabled() {
        return backingStore;
    }


    /**
     * If true if this viewport will maintain an offscreen
     * image of its contents.  The image is used to reduce the cost
     * of small one dimensional changes to the viewPosition.
     * Rather than repainting the entire viewport we use
     * Graphics.copyArea() to effect some of the scroll.
     */
    public void setBackingStoreEnabled(boolean x) {
        backingStore = x;
    }


    /**
     * Returns the Viewport's one child or null.
     *
     * @see #setView
     */
    public Component getView() {
        return (getComponentCount() > 0) ? getComponent(0) : null;
    }

    /**
     * Sets the Viewport's one lightweight child (<code>view</code>),
     * which can be null.
     *
     * @see #getView
     */
    public void setView(Component view) {

        /* Remove the viewport's existing children, if any.
         * Note that removeAll() isn't used here because it
         * doesn't call remove() (which JViewport overrides).
         */
        int n = getComponentCount();
        for(int i = n - 1; i >= 0; i--) {
            remove(i);
        }

        isViewSizeSet = false;

        if (view != null) {
            super.addImpl(view, null, -1);
            viewListener = createViewListener();
            view.addComponentListener(viewListener);
        }
    }


    /**
     * If the view's size hasn't been explicitly set, return the
     * preferred size, otherwise return the view's current size.
     * If there is no view, return 0,0.
     *
     * @return a Dimension object specifying the size of the view
     */
    public Dimension getViewSize() {
        Component view = getView();

        if (view == null) {
            return new Dimension(0,0);
        }
        else if (isViewSizeSet) {
            return view.getSize();
        }
        else {
            return view.getPreferredSize();
        }
    }


    /**
     * Sets the view coordinates that appear in the upper left
     * hand corner of the viewport, and the size of the view.
     *
     * @param newSize a Dimension object specifying the size and
     *        location of the new view coordinates, or null if there
     *        is no view
     */
    public void setViewSize(Dimension newSize) {
        Component view = getView();
        if (view != null) {
            Dimension oldSize = view.getSize();
            if (!newSize.equals(oldSize)) {
		// scrollUnderway will be true if this is invoked as the
		// result of a validate and setViewPosition was previously
		// invoked.
		scrollUnderway = false;
                view.setSize(newSize);
                isViewSizeSet = true;
                fireStateChanged();
            }
        }
    }

    /**
     * Returns the view coordinates that appear in the upper left
     * hand corner of the viewport, 0,0 if there's no view.
     *
     * @return a Point object giving the upper left coordinates
     */
    public Point getViewPosition() {
        Component view = getView();
        if (view != null) {
            Point p = view.getLocation();
            p.x = -p.x;
            p.y = -p.y;
            return p;
        }
        else {
            return new Point(0,0);
        }
    }


    /**
     * Sets the view coordinates that appear in the upper left
     * hand corner of the viewport, does nothing if there's no view.
     *
     * @param p  a Point object giving the upper left coordinates
     */
    public void setViewPosition(Point p) 
    {
        Component view = getView();
        if (view == null) {
	    return;
	}
	
	int oldX, oldY, x = p.x, y = p.y;

	/* Force p to lie within the bounds of the view,
	 * and do the song and dance to avoid allocating 
	 * a Rectangle object if we don't have to.
	 */
	if (view instanceof JComponent) {
	    JComponent c = (JComponent)view;
	    oldX = c.getX();
	    oldY = c.getY();
	    x = Math.min(x, c.getWidth() - 1);
	    y = Math.min(y, c.getHeight() - 1);
	}
	else {
	    Rectangle r = view.getBounds();
	    oldX = r.x;
	    oldY = r.y;
	    x = Math.min(x, r.width - 1);
	    y = Math.min(y, r.height - 1);
	}
	x = Math.max(0, p.x);
	y = Math.max(0, p.y);

	/* The view scrolls in the opposite direction to mouse 
	 * movement.
	 */
	int newX = -x;
	int newY = -y;
	
	if ((oldX != newX) || (oldY != newY)) {
	    scrollUnderway = true;
	    view.setLocation(newX, newY); // This calls setBounds(), and then repaint().
	    fireStateChanged();
	}
    }


    /**
     * Return a rectangle whose origin is getViewPosition and size is
     * getExtentSize(). This is the visible part of the view, in view
     * coordinates.
     *
     * @return a Rectangle giving the visible part of the view using view coordinates.
     */
    public Rectangle getViewRect() {
        return new Rectangle(getViewPosition(), getExtentSize());
    }


    /**
     * Computes the parameters for a blit where the backing store image
     * currently contains oldLoc in the upper left hand corner
     * and we're scrolling to newLoc.  The parameters are modified
     * to return the values required for the blit.
     */
    protected boolean computeBlit(
        int dx,
        int dy,
        Point blitFrom,
        Point blitTo,
        Dimension blitSize,
        Rectangle blitPaint)
    {
        int dxAbs = Math.abs(dx);
        int dyAbs = Math.abs(dy);
        Dimension extentSize = getExtentSize();

        if ((dx == 0) && (dy != 0) && (dyAbs < extentSize.height)) {
            if (dy < 0) {
                blitFrom.y = -dy;
                blitTo.y = 0;
                blitPaint.y = extentSize.height + dy;
            }
            else {
                blitFrom.y = 0;
                blitTo.y = dy;
                blitPaint.y = 0;
            }

            blitPaint.x = blitFrom.x = blitTo.x = 0;

            blitSize.width = extentSize.width;
            blitSize.height = extentSize.height - dyAbs;

            blitPaint.width = extentSize.width;
            blitPaint.height = dyAbs;

            return true;
        }

        else if ((dy == 0) && (dx != 0) && (dxAbs < extentSize.width)) {
            if (dx < 0) {
                blitFrom.x = -dx;
                blitTo.x = 0;
                blitPaint.x = extentSize.width + dx;
            }
            else {
                blitFrom.x = 0;
                blitTo.x = dx;
                blitPaint.x = 0;
            }

            blitPaint.y = blitFrom.y = blitTo.y = 0;

            blitSize.width = extentSize.width - dxAbs;
            blitSize.height = extentSize.height;

            blitPaint.y = 0;
            blitPaint.width = dxAbs;
            blitPaint.height = extentSize.height;

            return true;
        }

        else {
            return false;
        }
    }


    /**
     * Returns the size of the visible part of the view in view coordinates.
     *
     * @return a Dimension object giving the size of the view
     */
    public Dimension getExtentSize() {
        return getSize();
    }


    /**
     * Convert a size in pixel coordinates to view coordinates.
     * Subclasses of viewport that support "logical coordinates"
     * will override this method.
     *
     * @param size  a Dimension object using pixel coordinates
     * @return a Dimension object converted to view coordinates
     */
    public Dimension toViewCoordinates(Dimension size) {
        return new Dimension(size);
    }

    /**
     * Convert a point in pixel coordinates to view coordinates.
     * Subclasses of viewport that support "logical coordinates"
     * will override this method.
     *
     * @param p  a Point object using pixel coordinates
     * @return a Point object converted to view coordinates
     */
    public Point toViewCoordinates(Point p) {
        return new Point(p);
    }


    /**
     * Set the size of the visible part of the view using view coordinates.
     *
     * @param newExtent  a Dimension object specifying the size of the view
     */
    public void setExtentSize(Dimension newExtent) {
        Dimension oldExtent = getExtentSize();
        if (!newExtent.equals(oldExtent)) {
            setSize(newExtent);
            fireStateChanged();
        }
    }

    /**
     * A listener for the view.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class ViewListener extends ComponentAdapter implements Serializable
    {
        public void componentResized(ComponentEvent e) {
            fireStateChanged();
        }
    }

    /**
     * Create a listener for the view.
     * @return a ViewListener
     */
    protected ViewListener createViewListener() {
        return new ViewListener();
    }


    /**
     * Subclassers can override this to install a different
     * layout manager (or null) in the constructor.  Returns
     * a new JViewportLayout object.
     *
     * @return a LayoutManager
     */
    protected LayoutManager createLayoutManager() {
        return new ViewportLayout();
    }


    /**
     * Add a ChangeListener to the list that's notified each time the view's
     * size, position, or the viewport's extent size has changed.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     * @see #setViewPosition
     * @see #setViewSize
     * @see #setExtentSize
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Remove a ChangeListener from the list that's notified each
     * time the views size, position, or the viewports extent size
     * has changed.
     *
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }


    /*
     * Notify all ChangeListeners when the views
     * size, position, or the viewports extent size has changed.
     *
     * @see #addChangeListener
     * @see #removeChangeListener
     * @see EventListenerList
     */
    protected void fireStateChanged()
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    /**
     * We always repaint in our parent coordinate system to make sure
     * only one paint is performed by the RepaintManager.
     *
     * @param     tm   maximum time in milliseconds before update
     * @param     x    the <i>x</i> coordinate (pixels over from left)
     * @param     y    the <i>y</i> coordinate (pixels down from top)
     * @param     width    the width
     * @param     height   the height
     * @see       java.awt.Component#update(java.awt.Graphics)
     */
    public void repaint(long tm, int x, int y, int w, int h) {
        Container parent = getParent();
        if(parent != null)
            parent.repaint(tm,x+getX(),y+getY(),w,h);
        else
            super.repaint(tm,x,y,w,h);
    }


    /**
     * Returns a string representation of this JViewport. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JViewport.
     */
    protected String paramString() {
        String isViewSizeSetString = (isViewSizeSet ?
				      "true" : "false");
        String lastPaintPositionString = (lastPaintPosition != null ?
					  lastPaintPosition.toString() : "");
        String backingStoreString = (backingStore ?
                                    "true" : "false");
        String backingStoreImageString = (backingStoreImage != null ?
					  backingStoreImage.toString() : "");
        String scrollUnderwayString = (scrollUnderway ?
				       "true" : "false");

        return super.paramString() +
        ",backingStore=" + backingStoreString +
        ",backingStoreImage=" + backingStoreImageString +
        ",isViewSizeSet=" + isViewSizeSetString +
        ",lastPaintPosition=" + lastPaintPositionString +
        ",scrollUnderway=" + scrollUnderwayString;
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
            accessibleContext = new AccessibleJViewport();
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
    protected class AccessibleJViewport extends AccessibleJComponent {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of
         * the object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.VIEWPORT;
        }
    } // inner class AccessibleJViewport
}
