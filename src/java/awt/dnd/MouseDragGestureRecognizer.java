/*
 * @(#)MouseDragGestureRecognizer.java	1.3 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.dnd;

import java.awt.Component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * <p>
 * This abstract subclass of DragGestureRecognizer defines a DragGestureRecognizer
 * for Mouse based gestures.
 *
 * Each platform will implement its own concrete subclass of this class,

 * available via the Toolkit.createDragGestureRecognizer() method, to encapsulate
 * the recognition of the platform dependent Mouse gesture(s) that initiate
 * a Drag and Drop operation.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.3
 *
 * @see java.awt.dnd.DragGestureListener
 * @see java.awt.dnd.DragGestureEvent
 * @see java.awt.dnd.DragSource
 */

public abstract class MouseDragGestureRecognizer extends DragGestureRecognizer implements MouseListener, MouseMotionListener {

    /**
     * construct a new MouseDragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     * @param c   The Component to observe
     * @param act The actions permitted for this Drag
     * @param dgl The DragGestureRecognizer to notify when a gesture is detected
     *
     */

    protected MouseDragGestureRecognizer(DragSource ds, Component c, int act, DragGestureListener dgl) {
	super(ds, c, act, dgl);
    }

    /**
     * construct a new MouseDragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     * @param c   The Component to observe
     * @param act The actions permitted for this Drag
     */

    protected MouseDragGestureRecognizer(DragSource ds, Component c, int act) {
	this(ds, c, act, null);
    }

    /**
     * construct a new MouseDragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     * @param c   The Component to observe
     */

    protected MouseDragGestureRecognizer(DragSource ds, Component c) {
	this(ds, c, DnDConstants.ACTION_NONE);
    }

    /**
     * construct a new MouseDragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     */

    protected MouseDragGestureRecognizer(DragSource ds) {
	this(ds, null);
    }

    /**
     * register this DragGestureRecognizer's Listeners with the Component
     */

    protected void registerListeners() {
	component.addMouseListener(this);
	component.addMouseMotionListener(this);
    }

    /**
     * unregister this DragGestureRecognizer's Listeners with the Component
     *
     * subclasses must override this method
     */


    protected void unregisterListeners() {
	component.removeMouseListener(this);
	component.removeMouseMotionListener(this);
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */

    public void mouseClicked(MouseEvent e) { }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */

    public void mousePressed(MouseEvent e) { }

    /**
     * Invoked when a mouse button has been released on a component.
     */

    public void mouseReleased(MouseEvent e) { }

    /**
     * Invoked when the mouse enters a component.
     */

    public void mouseEntered(MouseEvent e) { }

    /**
     * Invoked when the mouse exits a component.
     */

    public void mouseExited(MouseEvent e) { }

    /**
     * Invoked when a mouse button is pressed on a component.
     */

    public void mouseDragged(MouseEvent e) { }

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */

    public void mouseMoved(MouseEvent e) { }
}
