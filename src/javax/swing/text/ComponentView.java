/*
 * @(#)ComponentView.java	1.36 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.awt.*;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.OverlayLayout;

/**
 * Component decorator that implements the view interface.  The
 * entire element is used to represent the component.  This acts
 * as a gateway from the display-only View implementations to
 * interactive lightweight components (ie it allows components
 * to be embedded into the View hierarchy).
 *
 * @author Timothy Prinzing
 * @version 1.36 11/29/01
 */
public class ComponentView extends View  {

    /**
     * Creates a new ComponentView object.
     *
     * @param elem the element to decorate
     */
    public ComponentView(Element elem) {
	super(elem);
    }

    /**
     * Create the component that is associated with
     * this view.  This will be called when it has
     * been determined that a new component is needed.
     * This would result from a call to setParent or
     * as a result of being notified that attributes
     * have changed.
     */
    protected Component createComponent() {
	AttributeSet attr = getElement().getAttributes();
	Component comp = StyleConstants.getComponent(attr);
	return comp;
    }

    /**
     * Fetch the component associated with the view.
     */
    public final Component getComponent() {
	return createdC;
    }

    // --- View methods ---------------------------------------------

    /**
     * Paints a component view.
     * The real paint behavior occurs naturally from the association
     * that the component has with its parent container (the same
     * container hosting this view), so this simply allows us to
     * position the component properly relative to the view.  Since
     * the coordinate system for the view is simply the parent
     * containers, positioning the child component is easy.
     *
     * @param g the graphics context
     * @param a the shape
     * @see View#paint
     */
    public void paint(Graphics g, Shape a) {
	if (c != null) {
	    c.setBounds(a.getBounds());
	    if (! c.isVisible()) {
		c.setVisible(true);
	    }
	}
    }

    /**
     * Determines the preferred span for this view along an
     * axis.  This is implemented to return the value
     * returned by Component.getPreferredSize along the
     * axis of interest.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis
     */
    public float getPreferredSpan(int axis) {
	if (c != null) {
	    Dimension size = c.getPreferredSize();
	    switch (axis) {
	    case View.X_AXIS:
		return size.width;
	    case View.Y_AXIS:
		return size.height;
	    default:
		throw new IllegalArgumentException("Invalid axis: " + axis);
	    }
	}
	return 0;
    }

    /**
     * Determines the minimum span for this view along an
     * axis.  This is implemented to return the value
     * returned by Component.getMinimumSize along the
     * axis of interest.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis
     */
    public float getMinimumSpan(int axis) {
	if (c != null) {
	    Dimension size = c.getMinimumSize();
	    switch (axis) {
	    case View.X_AXIS:
		return size.width;
	    case View.Y_AXIS:
		return size.height;
	    default:
		throw new IllegalArgumentException("Invalid axis: " + axis);
	    }
	}
	return 0;
    }

    /**
     * Determines the maximum span for this view along an
     * axis.  This is implemented to return the value
     * returned by Component.getMaximumSize along the
     * axis of interest.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis
     */
    public float getMaximumSpan(int axis) {
	if (c != null) {
	    Dimension size = c.getMaximumSize();
	    switch (axis) {
	    case View.X_AXIS:
		return size.width;
	    case View.Y_AXIS:
		return size.height;
	    default:
		throw new IllegalArgumentException("Invalid axis: " + axis);
	    }
	}
	return 0;
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the alignment of the
     * embedded component.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the desired alignment.  This should be a value
     *   between 0.0 and 1.0 where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     */
    public float getAlignment(int axis) {
	if (c != null) {
	    switch (axis) {
	    case View.X_AXIS:
		return c.getAlignmentX();
	    case View.Y_AXIS:
		return c.getAlignmentY();
	    }
	}
	return super.getAlignment(axis);
    }

    /**
     * Sets the size of the view.  This is implemented
     * to set the size of the component to match the
     * size of the view.
     * <p>
     * The changing of the component size will
     * touch the component lock, which is the one thing 
     * that is not safe from the View hierarchy.  Therefore,
     * this functionality is executed immediately if on the
     * event thread, or is queued on the event queue if
     * called from another thread (notification of change
     * from an asynchronous update).
     *
     * @param width the width >= 0
     * @param height the height >= 0
     */
    public void setSize(float width, float height) {
	if (c != null) {
	    if (SwingUtilities.isEventDispatchThread()) {
		c.setSize((int) width, (int) height);
	    } else {
		Runnable callSetComponentSize = 
		    new SafeResizer((int) width, (int) height);
		SwingUtilities.invokeLater(callSetComponentSize);
	    }
	}
    }

    class SafeResizer implements Runnable {
	
	SafeResizer(int width, int height) {
	    this.width = width;
	    this.height = height;
	}

        public void run() {
	    if (c != null) {
		c.setSize(width, height);
	    }
	}

	private int width;
	private int height;
    }

    /**
     * Sets the parent for a child view.
     * The parent calls this on the child to tell it who its
     * parent is, giving the view access to things like
     * the hosting Container.  The superclass behavior is
     * executed, followed by a call to createComponent if
     * a component has not yet been created and the embedded
     * components parent is set to the value returned by 
     * <code>getContainer</code>.
     * <p>
     * The changing of the component hierarhcy will
     * touch the component lock, which is the one thing 
     * that is not safe from the View hierarchy.  Therefore,
     * this functionality is executed immediately if on the
     * event thread, or is queued on the event queue if
     * called from another thread (notification of change
     * from an asynchronous update).
     *
     * @param p the parent
     */
    public void setParent(View p) {
	super.setParent(p);
        if (SwingUtilities.isEventDispatchThread()) {
	    setComponentParent();
        } else {
            Runnable callSetComponentParent = new Runnable() {
                public void run() {
		    Document doc = getDocument();
		    try {
			if (doc instanceof AbstractDocument) {
			    ((AbstractDocument)doc).readLock();
			}
			setComponentParent();
			Container host = getContainer();
			if (host != null) {
			    preferenceChanged(null, true, true);
			    host.repaint();
			}
		    } finally {
			if (doc instanceof AbstractDocument) {
			    ((AbstractDocument)doc).readUnlock();
			}
		    }			
                }
            };
            SwingUtilities.invokeLater(callSetComponentParent);
        }
    }

    /**
     * Set the parent of the embedded component 
     * with assurance that it is thread-safe.
     */
    void setComponentParent() {
	View p = getParent();
	if (p == null) {
	    if (c != null) {
		Container parent = c.getParent();
		if (parent != null) {
		    parent.remove(c);
		}
	    }
	} else {
	    if (c == null) {
		// try to build a component
		Component comp = createComponent();
		if (comp != null) {
		    createdC = comp;
		    c = new Invalidator(comp);
		    c.setVisible(false);
		}
	    }
	    if (c != null) {
		if (c.getParent() == null) {
		    Container parent = getContainer();
		    parent.add(c);
		}
	    }
	}
    }

    /**
     * Provides a mapping from the coordinate space of the model to
     * that of the view.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position is returned
     * @exception BadLocationException  if the given position does not
     *   represent a valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	int p0 = getStartOffset();
	int p1 = getEndOffset();
	if ((pos >= p0) && (pos <= p1)) {
	    Rectangle r = a.getBounds();
	    if (pos == p1) {
		r.x += r.width;
	    }
	    r.width = 0;
	    return r;
	}
	throw new BadLocationException(pos + " not in range " + p0 + "," + p1, pos);
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param a the allocated region to render into
     * @return the location within the model that best represents
     *    the given point in the view
     * @see View#viewToModel
     */
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	Rectangle alloc = (Rectangle) a;
	if (x < alloc.x + (alloc.width / 2)) {
	    bias[0] = Position.Bias.Forward;
	    return getStartOffset();
	}
	bias[0] = Position.Bias.Backward;
	return getEndOffset();
    }

    // --- member variables ------------------------------------------------

    private Component createdC;
    private Component c;

    /**
     * This class feeds the invalidate back to the
     * hosting View.  This is needed to get the View
     * hierarchy to consider giving the component
     * a different size (i.e. layout may have been
     * cached between the associated view and the
     * container hosting this component).
     */
    class Invalidator extends Container {

	Invalidator(Component child) {
	    setLayout(new OverlayLayout(this));
	    add(child);
	    min = child.getMinimumSize();
	    pref = child.getPreferredSize();
	    max = child.getMaximumSize();
	    yalign = child.getAlignmentY();
	    xalign = child.getAlignmentX();
	}

	/**
	 * The components invalid layout needs 
	 * to be propagated through the view hierarchy
	 * so the views (which position the component)
	 * can have their layout recomputed.
	 */
	public void invalidate() {
	    super.invalidate();
	    min = super.getMinimumSize();
	    pref = super.getPreferredSize();
	    max = super.getMaximumSize();
	    yalign = super.getAlignmentY();
	    xalign = super.getAlignmentX();
	    if (getParent() != null) {
		preferenceChanged(null, true, true);
	    }
	}

	/**
	 * Shows or hides this component depending on the value of parameter 
	 * <code>b</code>.
	 * @param <code>b</code>  If <code>true</code>, shows this component; 
	 * otherwise, hides this component.
	 * @see #isVisible
	 * @since JDK1.1
	 */
        public void setVisible(boolean b) {
	    super.setVisible(b);
	    Component child = this.getComponent(0);
	    child.setVisible(b);
	}

        public Dimension getMinimumSize() {
	    return min;
	}

        public Dimension getPreferredSize() {
	    return pref;
	}

        public Dimension getMaximumSize() {
	    return max;
	}

	public float getAlignmentX() {
	    return xalign;
	}

	public float getAlignmentY() {
	    return yalign;
	}

	Dimension min;
	Dimension pref;
	Dimension max;
	float yalign;
	float xalign;

    }

}

