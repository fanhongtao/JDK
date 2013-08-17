/*
 * @(#)NoFramesView.java	1.2 98/08/26
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
package javax.swing.text.html;

import javax.swing.text.*;
import java.awt.*;

/**
 * This is the view associated with the html tag NOFRAMES.
 * This view has been written to ignore the contents of the
 * NOFRAMES tag.  The contents of the tag will only be visible
 * when the JTextComponent the view is contained in is editable.
 *
 * @author  Sunita Mani
 * @version 1.2 08/26/98
 */
class NoFramesView extends BlockView {

    /**
     * Creates a new view that represents an
     * html box.  This can be used for a number
     * of elements.  By default this view is not
     * visible.
     *
     * @param elem the element to create a view for
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public NoFramesView(Element elem, int axis) {
	super(elem, axis);
	visible = false;
    }


    /**
     * If this view is not visible, then it returns.
     * Otherwise it invokes the superclass.
     * 
     * @param g the rendering surface to use
     * @param allocation the allocated region to render into
     * @see isVisible
     * @see text.ParagraphView#paint
     */
    public void paint(Graphics g, Shape allocation) {
	Container host = getContainer();
	if (host != null &&
	    visible != ((JTextComponent)host).isEditable()) {
	    visible = ((JTextComponent)host).isEditable();
	}

	if (!isVisible()) {
	    return;
	}
	super.paint(g, allocation);
    }


    /**
     * Determines if the JTextComponent that the view
     * is contained in is editable. If so, then this
     * view and all its child views are visible.
     * Once this has been determined, the superclass
     * is invoked to continue processing.
     *
     * @param p the parent View.
     * @see BlockView#setParent 
     */
    public void setParent(View p) {
	if (p != null) {
	    Container host = p.getContainer();
	    if (host != null) {
		visible = ((JTextComponent)host).isEditable();
	    }
	}
	super.setParent(p);  
    }

    /**
     * Returns a true/false value that represents
     * whether the view is visible or not.
     */
    public boolean isVisible() {
	return visible;
    }


    /**
     * Do nothing if the view is not visible, otherwise
     * invoke the superclass to perform layout.
     */
    protected void layout(int width, int height) {
	if (!isVisible()) {
	    return;
	}
	super.layout(width, height);   
    }
        
    /**
     * Determines the preferred span for this view.  Returns
     * 0 if the view is not visible, otherwise it calls the
     * superclass method to get the preferred span.
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     * @see text.ParagraphView#getPreferredSpan
     */
    public float getPreferredSpan(int axis) {
       	if (!visible) {
	    return 0;
	}
	return super.getPreferredSpan(axis);
    }

    /**
     * Determines the minimum span for this view along an
     * axis.  Returns 0 if the view is not visible, otherwise 
     * it calls the superclass method to get the minimum span.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the minimum span the view can be rendered into.
     * @see text.ParagraphView#getMinimumSpan
     */
    public float getMinimumSpan(int axis) {
	if (!visible) {
	    return 0;
	}
	return super.getMinimumSpan(axis);
    }

    /**
     * Determines the maximum span for this view along an
     * axis.  Returns 0 if the view is not visible, otherwise
     * it calls the superclass method ot get the maximum span.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the maximum span the view can be rendered into.
     * @see text.ParagraphView#getMaximumSpan
     */
    public float getMaximumSpan(int axis) {
	if (!visible) {
	    return 0;
	}
	return super.getMaximumSpan(axis);
    }

    boolean visible;
}
