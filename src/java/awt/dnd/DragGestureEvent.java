/*
 * @(#)DragGestureEvent.java	1.2 98/06/29
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
import java.awt.Cursor;

import java.awt.Image;
import java.awt.Point;

import java.awt.event.InputEvent;

import java.awt.datatransfer.Transferable;

import java.util.EventObject;

import java.util.List;
import java.util.Iterator;

/**
 * <p>
 * A DragGestureEvent is passed to a DragGestureListener via its gestureRecognized() method
 * when a particular DragGestureRecognizer detects a platform dependent Drag and
 * Drop action initiating gesture has occurred on the Component it is tracking.
 * </p>
 *
 * @version 1.2
 * @see java.awt.dnd.DragGestureRecognizer
 * @see java.awt.dnd.DragGestureListener
 * @see java.awt.dnd.DragSource
 *
 */

public class DragGestureEvent extends EventObject {

    /**
     * construct a DragGestureEvent
     *
     * @param dgr The DragSourceRecognizer firing this event
     * @param ori The origin of the drag
     * @param act The the user's preferred action
     * @param evs The List of events that comprise the gesture
     */

    public DragGestureEvent(DragGestureRecognizer dgr, int act, Point ori, List evs) {
	super(dgr);

	if ((component = dgr.getComponent()) == null)
	    throw new IllegalArgumentException("null component");
	if ((dragSource = dgr.getDragSource()) == null)
	    throw new IllegalArgumentException("null DragSource");

	if (evs == null || evs.isEmpty())
	    throw new IllegalArgumentException("null or empty list of events");

	if (act != DnDConstants.ACTION_COPY &&
	    act != DnDConstants.ACTION_MOVE &&
	    act != DnDConstants.ACTION_LINK)
	    throw new IllegalArgumentException("bad action");

	if (ori == null) throw new IllegalArgumentException("null origin");

	events     = evs;
	action     = act;
	origin     = ori;
    }

    /**
     * @return the source as a DragGestureRecognizer
     */

    public DragGestureRecognizer getSourceAsDragGestureRecognizer() {
	return (DragGestureRecognizer)getSource();
    }

    /**
     * @return the Component
     */

    public Component getComponent() { return component; }

    /**
     * @return the DragSource
     */

    public DragSource getDragSource() { return dragSource; }

    /**
     * @return the Point where the drag originated in Component coords.
     */

    public Point getDragOrigin() {
	return origin;
    }

    /**
     * @return an Iterator for the events comprising the gesture
     */

    public Iterator iterator() { return events.iterator(); }

    /**
     * @return an array of the events comprising the gesture
     */

    public Object[] toArray() { return events.toArray(); }

    /**
     * @return an array of the events comprising the gesture
     */

    public Object[] toArray(Object[] array) { return events.toArray(array); }

    /**
     * @return the action selected by the user
     */

    public int getDragAction() { return action; }

    /**
     * @returns the first "triggering" event in the sequence of the gesture
     */

    public InputEvent getTriggerEvent() {
	return getSourceAsDragGestureRecognizer().getTriggerEvent();
    }

    /**
     * start the drag
     *
     * @param dragCursor   The initial drag Cursor
     * @param transferable The source's Transferable
     * @param dsl	   The source's DragSourceListener
     */

    public void startDrag(Cursor dragCursor, Transferable transferable, DragSourceListener dsl) throws InvalidDnDOperationException {
	dragSource.startDrag(this, dragCursor, transferable, dsl);
    }

    /**
     * start the drag
     *
     * @param dragCursor   The initial drag Cursor
     * @param dragImage    The source's dragImage
     * @param imageOffset  The dragImage's offset
     * @param transferable The source's Transferable
     * @param dsl	   The source's DragSourceListener
     */

    public void startDrag(Cursor dragCursor, Image dragImage, Point imageOffset, Transferable transferable, DragSourceListener dsl) throws InvalidDnDOperationException {
	dragSource.startDrag(this,  dragCursor, dragImage, imageOffset, transferable, dsl);
    }

    /*
     * fields
     */

    private List       events;
    private DragSource dragSource;
    private Component  component;
    private Point      origin;
    private int	       action;
}
