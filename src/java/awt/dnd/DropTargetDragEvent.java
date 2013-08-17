/*
 * @(#)DropTargetDragEvent.java	1.7 98/04/21
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

import java.awt.Point;

import java.awt.datatransfer.DataFlavor;

import java.awt.dnd.DropTargetEvent;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The DropTargetDragEvent is delivered to a DropTargetListener via its
 * dragEnter() and dragOver().
 * </p>
 *
 * @version 1.7
 * @since JDK1.2
 *
 */

public class DropTargetDragEvent extends DropTargetEvent {

    /**
     * construct an Event
     *
     * @param dtc        The DropTargetContext for this operation
     * @param cursorLocn The location of the "Drag" Cursors hotspot in Component coordinates
     * @param dropAction The currently selected user drop action
     * @param srcActions The current set of actions supported by the source
     *
     * @throw NullPointerException if the dtc or cursorLocn are null
     * @throw IllegalArgumentException if the dropAction or srcActions are illegal values
     */

    public DropTargetDragEvent(DropTargetContext dtc, Point cursorLocn, int dropAction, int srcActions)  {
	super(dtc);

	if (cursorLocn == null) throw new NullPointerException("cursorLocn");

	if (dropAction != DnDConstants.ACTION_NONE &&
	    dropAction != DnDConstants.ACTION_COPY &&
	    dropAction != DnDConstants.ACTION_MOVE &&
	    dropAction != DnDConstants.ACTION_LINK
	) throw new IllegalArgumentException("dropAction" + dropAction);

	if ((srcActions & ~(DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK)) != 0) throw new IllegalArgumentException("srcActions");

	location        = cursorLocn;
	actions         = srcActions;
	this.dropAction = dropAction;
    }

    /**
     * @return the current cursor location in Component's coords.
     */

    public Point getLocation() {
	return location;
    }


    /**
     * @return current DataFlavors from the DropTargetContext
     */

    public DataFlavor[] getCurrentDataFlavors() {
	return getDropTargetContext().getCurrentDataFlavors();
    }

    /**
     * @return a java.util.List of the Current DataFlavors
     */

    public List getCurrentDataFlavorsAsList() {
	return getDropTargetContext().getCurrentDataFlavorsAsList();
    }

    /**
     * @return if a particular DataFlavor is supported
     */

    public boolean isDataFlavorSupported(DataFlavor df) {
	return getDropTargetContext().isDataFlavorSupported(df);
    }

    /**
     * @return source actions
     */

    public int getSourceActions() { return actions; }

    /**
     * @return currently selected drop action
     */

    public int getDropAction() { return dropAction; }

    /**
     * Accept the drag
     *
     * This method should be called from a DropTargetListeners dragEnter(),
     * dragOver() and dragActionChanged() methods if the implementation
     * wishes to accept an operation from the srcActions other than the one
     * selected by the user as represented by the dropAction.
     *
     * @param dragOperation the operation accepted by the target
     */

    public void acceptDrag(int dragOperation) {
	getDropTargetContext().acceptDrag(dragOperation);
    }

    /**
     * Reject the drag as a result of examining either the dropAction or
     * the available DataFlavor types.
     */

    public void rejectDrag() {
	getDropTargetContext().rejectDrag();
    }

    /*
     * fields
     */

    private Point		location;
    private int			actions;
    private int			dropAction;
}
