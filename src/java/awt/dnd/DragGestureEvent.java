/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * A <code>DragGestureEvent</code> is passed 
 * to <code>DragGestureListener</code>'s  
 * dragGestureRecognized() method
 * when a particular <code>DragGestureRecognizer</code> detects that a 
 * platform dependent drag initiating gesture has occurred 
 * on the <code>Component</code> that it is tracking.
 * 
 * @version 1.16
 * @see java.awt.dnd.DragGestureRecognizer
 * @see java.awt.dnd.DragGestureListener
 * @see java.awt.dnd.DragSource
 */

public class DragGestureEvent extends EventObject {

    /**
     * Construct a <code>DragGestureEvent</code> given the
     * <code>DragGestureRecognizer</code> firing this event, 
     * an <code>int</code> representing
     * the user's preferred action, a <code>Point</code> 
     * indicating the origin of the drag, and a <code>List</code> 
     * of events that comprise the gesture.
     * <P>
     * @param dgr The <code>DragGestureRecognizer</code> firing this event
     * @param act The the user's preferred action
     * @param ori The origin of the drag
     * @param evs The <code>List</code> of events that comprise the gesture
     * <P>
     * @throws <code>IllegalArgumentException</code> if 
     * input parameters are null
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
     * This method returns the source as a <code>DragGestureRecognizer</code>.
     * <P>
     * @return the source as a <code>DragGestureRecognizer</code>
     */

    public DragGestureRecognizer getSourceAsDragGestureRecognizer() {
	return (DragGestureRecognizer)getSource();
    }

    /**
     * This method returns the <code>Component</code> associated 
     * with this <code>DragGestureEvent</code>.
     * <P>
     * @return the Component
     */

    public Component getComponent() { return component; }

    /**
     * This method returns the <code>DragSource</code>.
     * <P>
     * @return the <code>DragSource</code>
     */

    public DragSource getDragSource() { return dragSource; }

    /**
     * This method returns a <code>Point</code> in the coordinates
     * of the <code>Component</code> over which the drag originated.
     * <P>
     * @return the Point where the drag originated in Component coords.
     */

    public Point getDragOrigin() {
	return origin;
    }

    /**
     * This method returns an <code>Iterator</code> for the events
     * comprising the gesture.
     * <P>
     * @return an Iterator for the events comprising the gesture
     */

    public Iterator iterator() { return events.iterator(); }

    /**
     * This method returns an <code>Object</code> array of the 
     * events comprising the drag gesture.
     * <P>
     * @return an array of the events comprising the gesture
     */

    public Object[] toArray() { return events.toArray(); }

    /**
     * This method returns an array of the 
     * events comprising the drag gesture.
     * <P>
     * @param array the array of <code>EventObject</code> sub(types)
     * <P>
     * @return an array of the events comprising the gesture
     */

    public Object[] toArray(Object[] array) { return events.toArray(array); }

    /**
     * This method returns an <code>int</code> representing the 
     * action selected by the user.
     * <P>
     * @return the action selected by the user
     */

    public int getDragAction() { return action; }

    /**
     * This method returns the initial event that triggered the gesture. 
     * <P>
     * @returns the first "triggering" event in the sequence of the gesture
     */

    public InputEvent getTriggerEvent() {
	return getSourceAsDragGestureRecognizer().getTriggerEvent();
    }

    /**
     * Start the drag given the initial <code>Cursor</code> to display, 
     * the <code>Transferable</code> object, 
     * and the <code>DragSourceListener</code> to use.
     * <P>
     * @param dragCursor   The initial drag Cursor
     * @param transferable The source's Transferable
     * @param dsl	   The source's DragSourceListener
     * <P>
     * @throws <code>InvalidDnDOperationException</code> if 
     * the Drag and Drop system is unable to
     * initiate a drag operation, or if the user 
     * attempts to start a drag while an existing
     * drag operation is still executing.
     */

    public void startDrag(Cursor dragCursor, Transferable transferable, DragSourceListener dsl) throws InvalidDnDOperationException {
	dragSource.startDrag(this, dragCursor, transferable, dsl);
    }

    /**
     * Start the drag given the initial <code>Cursor</code> to display,
     * a drag <code>Image</code>, the offset of 
     * the <code>Image</code>, 
     * the <code>Transferable</code> object, and 
     * the <code>DragSourceListener</code> to use.
     * <P>
     * @param dragCursor   The initial drag Cursor
     * @param dragImage    The source's dragImage
     * @param imageOffset  The dragImage's offset
     * @param transferable The source's Transferable
     * @param dsl	   The source's DragSourceListener
     * <P>
     * @throws <code>InvalidDnDOperationException</code> if 
     * the Drag and Drop system is unable to
     * initiate a drag operation, or if the user 
     * attempts to start a drag while an existing
     * drag operation is still executing.
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








