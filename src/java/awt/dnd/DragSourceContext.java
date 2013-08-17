/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.event.InputEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;

import java.awt.dnd.peer.DragSourceContextPeer;

import java.io.IOException;

import java.util.TooManyListenersException;

/**
 * The DragSourceContext class is responsible for managing the initiator side
 * of the Drag and Drop protocol. In particular it is responsible for managing
 * event notifications to the DragSourceListener, and providing the
 * Transferable state to enable the data transfer.
 * <P>
 * An instance of this class is created as a result 
 * of a <code>DragSource's</code> startDrag() method being successfully 
 * invoked. This instance is responsible for tracking the state 
 * of the operation on behalf of the
 * <code>DragSource</code> and dispatching state changes to 
 * the <code>DragSourceListener</code>.
 * <P>
 * Note that the <code>DragSourceContext</code> itself 
 * implements the <code>DragSourceListener</code> 
 * interface. This is to allow the platform peer 
 * (the <code>DragSourceContextPeer</code> instance) 
 * created by the <code>DragSource</code> to notify 
 * the <code>DragSourceContext</code> of
 * changes in state in the ongiong operation. This allows the
 * <code>DragSourceContext</code> to interpose 
 * itself between the platform and the
 * <code>DragSourceListener</code> provided by 
 * the initiator of the operation.
 *
 * @version 1.43, 02/06/02
 * @since 1.2
 */

public class DragSourceContext implements DragSourceListener {

    // used by updateCurrentCursor

    /**
     * An <code>int</code> used by updateCurrentCursor() 
     * indicating that the <code>Cursor</code> should change
     * to the default (no drop) <code>Cursor</code>.
     */
    protected static final int DEFAULT = 0;

    /**
     * An <code>int</code> used by updateCurrentCursor()
     * indicating that the <code>Cursor</code> 
     * has entered a <code>DropTarget</code>. 
     */
    protected static final int ENTER   = 1;

    /**
     * An <code>int</code> used by updateCurrentCursor()
     * indicating that the <code>Cursor</code> is 
     * over a <code>DropTarget</code>. 
     */
    protected static final int OVER    = 2;

    /**
     * An <code>int</code> used by updateCurrentCursor()
     * indicating that the user operation has changed. 
     */ 

    protected static final int CHANGED = 3;
	
    /**
     * Called from <code>DragSource</code>, this constructor creates a new
     * <code>DragSourceContext</code> given the
     * <code>DragSourceContextPeer</code> for this Drag, the
     * <code>DragGestureEvent</code> that triggered the Drag, the initial
     * <code>Cursor</code> to use for the Drag, an (optional)
     * <code>Image</code> to display while the Drag is taking place, the offset
     * of the <code>Image</code> origin from the hotspot at the instant of the
     * triggering event, the <code>Transferable</code> subject data, and the 
     * <code>DragSourceListener</code> to use during the Drag and Drop
     * operation.
     *
     * @param dscp       the <code>DragSourceContextPeer</code> for this drag
     * @param trigger    the triggering event
     * @param dragCursor the initial <code>Cursor</code> 
     * @param dragImage  the <code>Image</code> to drag (or <code>null</code>)
     * @param offset     the offset of the image origin from the hotspot at the
     *                   instant of the triggering event
     * @param t          the <code>Transferable</code>
     * @param dsl        the <code>DragSourceListener</code>
     *
     * @throws IllegalArgumentException if trigger instance is incomplete
     * @throws NullPointerException if dscp, dsl, trigger, or t are null, or
     *         if dragImage is non-null and offset is null
     */
     public DragSourceContext(DragSourceContextPeer dscp, DragGestureEvent trigger, Cursor dragCursor, Image dragImage, Point offset, Transferable t, DragSourceListener dsl) {
	if ((peer = dscp) == null)
	    throw new NullPointerException("DragSourceContextPeer");

	if ((this.trigger = trigger) == null)
	    throw new NullPointerException("Trigger");

	if ((dragSource = trigger.getDragSource()) == null) 
	    throw new NullPointerException("DragSource");


	if ((component = trigger.getComponent()) == null)
	    throw new NullPointerException("Component");

	if ((actions = trigger.getSourceAsDragGestureRecognizer().getSourceActions()) == DnDConstants.ACTION_NONE)
	    throw new IllegalArgumentException("source actions");

	if ((currentDropAction = trigger.getDragAction()) == DnDConstants.ACTION_NONE)
	    throw new IllegalArgumentException("no drag action");

	if (t == null)
	    throw new NullPointerException("Transferable");

	if (dsl == null) 
	    throw new NullPointerException("DragSourceListener");

	if (image != null && offset == null)
	    throw new NullPointerException("offset");
	
	cursor	     = dragCursor;
	image        = dragImage;
	this.offset  = offset;
	transferable = t;
	listener     = dsl;

        if (dragCursor != null) {
            useCustomCursor = true;
        }

	updateCurrentCursor(currentDropAction, actions, DEFAULT);
    }

    /**
     * This method returns the <code>DragSource</code> 
     * that instantiated this <code>DragSourceContext</code>.
     * <P>
     * @return the <code>DragSource</code> that 
     * instantiated this <code>DragSourceContext</code>
     */

    public DragSource   getDragSource() { return dragSource; }

    /**
     * This method returns the <code>Component</code> associated with this 
     * <code>DragSourceContext</code>.
     * <P>
     * @return the <code>Component</code> that started the drag
     */

    public Component    getComponent() { return component; }

    /**
     * This method returns the <code>DragGestureEvent</code>
     * that initially  triggered the drag.
     * <P>
     * @return the Event that triggered the drag
     */

    public DragGestureEvent getTrigger() { return trigger; }

    /**
     * This method returns an <code>int</code> 
     * representing the current action(s) 
     * associated with this <code>DragSourceContext.</code>
     * <P>
     * @return the current actions
     */

    public int  getSourceActions() {
	return actions;
    }

    /**
     * This method sets the current drag <code>Cursor.</code>
     * <P>
     * @param c the <code>Cursor</code> to display.
     * Note that while <code>null</code> is not prohibited,
     * it is not an acceptable value for this parameter.
     */

    public void setCursor(Cursor c) {
        useCustomCursor = (c != null);
        setCursorImpl(c);
    }

    /**
     * This method returns the current drag <code>Cursor</code>. 
     * <P>
     * @return the current drag <code>Cursor</code>
     */

    public Cursor getCursor() { return cursor; }

    /**
     * Add a <code>DragSourceListener</code> to this
     * <code>DragSourceContext</code> if one has not already been added.
     * If a <code>DragSourceListener</code> already exists, 
     * this method throws a <code>TooManyListenersException</code>.
     * <P>
     * @param dsl the <code>DragSourceListener</code> to add.
     * Note that while <code>null</code> is not prohibited,
     * it is not acceptable as a parameter.
     * <P>
     * @throws <code>TooManyListenersException</code> if
     * a <code>DragSourceListener</code> has already been added
     */

    public synchronized void addDragSourceListener(DragSourceListener dsl) throws TooManyListenersException {
	if (dsl == null) return;

	if (equals(dsl)) throw new IllegalArgumentException("DragSourceContext may not be its own listener");

	if (listener != null)
	    throw new TooManyListenersException();
	else
	    listener = dsl;
    }

    /**
     * This method removes the specified <code>DragSourceListener</code>
     * from  this <code>DragSourceContext</code>.
     * <P>
     * @param dsl the <code>DragSourceListener</code> to remove.
     * Note that while <code>null</code> is not prohibited,
     * it is not acceptable as a parameter.
     */

    public synchronized void removeDragSourceListener(DragSourceListener dsl) {
	if (listener != null && listener.equals(dsl)) {
	    listener = null;
	} else
	    throw new IllegalArgumentException();
    }

    /**
     * This method notifies the peer that 
     * the Transferable's DataFlavors have changed.
     */

    public void transferablesFlavorsChanged() {
	if (peer != null) peer.transferablesFlavorsChanged();
    }

    /**
     * This method 
     * intercepts the <code>DragSourceDragEvent</code>
     * associated with dragEnter() from the peer.
     * <P>
     * Note: This method is called by the peer implementation, not the user.
     * <P>
     * @param dsde the intercepted <code>DragSourceDragEvent</code>
     */

    public synchronized void dragEnter(DragSourceDragEvent dsde) {
	if (listener != null) listener.dragEnter(dsde);

	updateCurrentCursor(dsde.getDropAction(), dsde.getTargetActions(), ENTER);
    }

    /**
     * This method 
     * intercepts the <code>DragSourceDragEvent</code>
     * associated with dragOver() from the peer.
     * <P>
     * Note: This method is called by the peer implementation, not the user.
     * <P>
     * @param dsde the intercepted <code>DragSourceDragEvent</code>
     */

    public synchronized void dragOver(DragSourceDragEvent dsde) {
	if (listener != null) listener.dragOver(dsde);

	updateCurrentCursor(dsde.getDropAction(), dsde.getTargetActions(), OVER);
    }

    /**
     * This method intercepts the <code>DragSourceEvent</code>
     * associated with dragExit() from the peer.
     * <P>
     * Note: This method is called by the peer implementation, not the user.
     * <P>
     * @param dse the intercepted <code>DragSourceEvent</code>
     */

    public synchronized void dragExit(DragSourceEvent dse) {
	if (listener != null) listener.dragExit(dse);

	updateCurrentCursor(currentDropAction, DnDConstants.ACTION_NONE, DEFAULT);
    }

    /**
     * This method intercepts the <code>DragSourceDragEvent</code> 
     * associated with dropActionChanged() from the peer.
     * <P>
     * Note: This method is called by the peer implementation, not the user.
     * <P>
     * @param dsde the intercepted <code>DragSourceDragEvent</code>
     */

    public synchronized void dropActionChanged(DragSourceDragEvent dsde) {
	currentDropAction = dsde.getDropAction();

	if (listener != null) listener.dropActionChanged(dsde);

	updateCurrentCursor(currentDropAction, dsde.getTargetActions(), CHANGED);
    }

    /**
     * This method intercepts the <code>DragSourceDropEvent</code> 
     * associated with dragDropEnd() from the peer.
     * <P>
     * Note: This method is called by the peer implementation, not the user.
     * The value of <code>null</code> is not acceptable as a parameter
     * to this method.
     * <P>
     * @param dsde the intercepted <code>DragSourceDropEvent</code>
     */

    public synchronized void dragDropEnd(DragSourceDropEvent dsde) {
	if (listener != null) listener.dragDropEnd(dsde);
    }


    /**
     * This method returns the <code>Transferable</code>
     * associated with this <code>DragSourceContext.</code>
     * <P>
     * @return the <code>Transferable</code>
     */
    public Transferable getTransferable() { return transferable; }
   
    /**
     * check the cursor for updates and implement defaults
     * <P>
     * @param dropOp the user's currently selected operation
     * @param targetAct the current target's supported actions
     * @param status the constant
     */

    protected void updateCurrentCursor(int dropOp, int targetAct, int status) {

	// if the cursor has been previously set then dont do any defaults
	// processing.

	if (useCustomCursor) {
	    return;
	}

	// do defaults processing

	Cursor c = null;

	switch (status) {
	    default:
		targetAct = DnDConstants.ACTION_NONE;
	    case ENTER:
	    case OVER:
	    case CHANGED:
		int    ra = dropOp & targetAct;

		if (ra == DnDConstants.ACTION_NONE) { // no drop possible
		    if ((dropOp & DnDConstants.ACTION_LINK) == DnDConstants.ACTION_LINK)
		        c = DragSource.DefaultLinkNoDrop;
		    else if ((dropOp & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE)
		        c = DragSource.DefaultMoveNoDrop;
		    else
		        c = DragSource.DefaultCopyNoDrop;
		} else { // drop possible
		    if ((ra & DnDConstants.ACTION_LINK) == DnDConstants.ACTION_LINK)
		        c = DragSource.DefaultLinkDrop;
		    else if ((ra & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE)
		        c = DragSource.DefaultMoveDrop;
		    else
		        c = DragSource.DefaultCopyDrop;
		}
	}

	setCursorImpl(c);
    }

    private void setCursorImpl(Cursor c) {
	if (cursor == null || !cursor.equals(c)) {
	    cursor      = c;
	    if (peer != null) peer.setCursor(cursor);
	}
    }

    /*
     * fields
     */

    private DragSource	          dragSource;
    private DragSourceContextPeer peer;

    private DragGestureEvent	  trigger;

    private Cursor		  cursor;

    private Component		  component;

    private int		    	  actions;

    private int		    	  currentDropAction;

    private Image		  image;
    private Point		  offset;

    private Transferable	  transferable;

    private DragSourceListener    listener;

    private boolean		  useCustomCursor = false;
}
