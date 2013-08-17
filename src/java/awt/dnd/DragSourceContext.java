/*
 * @(#)DragSourceContext.java	1.19 98/07/07
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
 * <p>
 * The DragSourceContext class is responsible for managing the initiator side
 * of the Drag and Drop protocol. In particular it is responsible for managing
 * event notifications to the DragSourceListener, and providing the
 * Transferable state to enable the data transfer.
 * </p>
 *
 * @version 1.19
 * @since JDK1.2
 *
 */

public class DragSourceContext implements DragSourceListener {

    // used by updateCurrentCursor

    protected static final int DEFAULT = 0;
    protected static final int ENTER   = 1;
    protected static final int OVER    = 2;
    protected static final int CHANGED = 3;
	
    /**
     * construct a DragSourceContext (called from DragSource)
     *
     * @param dscp	 The DragSourceContextPeer for this Drag
     * @param trigger	 The triggering event
     * @param dragCursor The initial Cursor
     * @param dragImage  The image to drag (or null)
     * @param offset	 The offset of the image origin from the hotspot
     *			 at the instant of the triggering event
     * @param t		 The Transferable
     * @param dsl	 The DragSourceListener
     *
     * @throw IllegalArgumentException
     * @throw NullPointerException
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

	updateCurrentCursor(currentDropAction, actions, DEFAULT);
    }

    /**
     * @return the DragSource that instantiated this DragSourceContext
     */

    public DragSource   getDragSource() { return dragSource; }

    /**
     * @return the Component that started the Drag
     */

    public Component    getComponent() { return component; }

    /**
     * @return the Event that triggered the Drag
     */

    public DragGestureEvent getTrigger() { return trigger; }

    /**
     * @return the current actions
     */

    public int  getSourceActions() {
	return actions;
    }

    /**
     * change the drag cursor
     */

    public void setCursor(Cursor c) {
	if (cursor == null || !cursor.equals(c)) {
	    cursorDirty = true;
	    cursor      = c;
	    if (peer != null) peer.setCursor(cursor);
	}
    }

    /**
     * @return the current drag cursor
     */

    public Cursor getCursor() { return cursor; }

    /**
     * change the DragSourceListener
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
     * change the DragSourceListener
     */

    public synchronized void removeDragSourceListener(DragSourceListener dsl) {
	if (listener != null && listener.equals(dsl)) {
	    listener = null;
	} else
	    throw new IllegalArgumentException();
    }

    /**
     * notify the peer that the Transferables DataFlavors have changed
     */

    public void transferablesFlavorsChanged() {
	if (peer != null) peer.transferablesFlavorsChanged();
    }

    /**
     * intercept the dragEnter event from the peer
     */

    public synchronized void dragEnter(DragSourceDragEvent dsde) {
	if (listener != null) listener.dragEnter(dsde);

	updateCurrentCursor(dsde.getDropAction(), dsde.getTargetActions(), ENTER);
    }

    /**
     * intercept the dragOver event from the peer
     */

    public synchronized void dragOver(DragSourceDragEvent dsde) {
	if (listener != null) listener.dragOver(dsde);

	updateCurrentCursor(dsde.getDropAction(), dsde.getTargetActions(), OVER);
    }

    /**
     * intercept the dragExit event from the peer
     */

    public synchronized void dragExit(DragSourceEvent dse) {
	if (listener != null) listener.dragExit(dse);

	updateCurrentCursor(currentDropAction, DnDConstants.ACTION_NONE, DEFAULT);
    }

    /**
     * intercept the dragGestureChanged event from the peer
     */

    public synchronized void dropActionChanged(DragSourceDragEvent dsde) {
	currentDropAction = dsde.getDropAction();

	if (listener != null) listener.dropActionChanged(dsde);

	updateCurrentCursor(currentDropAction, dsde.getTargetActions(), CHANGED);
    }

    /**
     * intercept the dragDropEnd event from the peer
     */

    public synchronized void dragDropEnd(DragSourceDropEvent dsde) {
	if (listener != null) listener.dragDropEnd(dsde);
    }

    public Transferable getTransferable() { return transferable; }
   
    /**
     * check the cursor for updates and implement defaults
     */

    protected void updateCurrentCursor(int dropOp, int targetAct, int status) {

	// if the cursor has been previously set then dont do any defaults
	// processing.

	if (cursorDirty && cursor != null) {
	    cursorDirty = false;
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

	setCursor(c);
	cursorDirty = false;
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

    private boolean		  cursorDirty = true;
}
