/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.Point;

import java.awt.datatransfer.DataFlavor;

import java.awt.dnd.DropTargetEvent;

import java.util.Arrays;
import java.util.List;

/**
 * The <code>DropTargetDragEvent</code> is delivered to a 
 * <code>DropTargetListener</code> via its
 * dragEnter() and dragOver() methods.
 *
 * @version 	1.17, 02/06/02
 * @since 1.2
 */

public class DropTargetDragEvent extends DropTargetEvent {

    /**
     * Construct a <code>DropTargetDragEvent</code> given the
     * <code>DropTargetContext</code> for this operation,
     * the location of the "Drag" <code>Cursor</code>'s hotspot 
     * in the <code>Component</code>'s coordinates, the
     * currently selected user drop action, and current 
     * set of actions supported by the source.
     * <P>
     * @param dtc        The DropTargetContext for this operation
     * @param cursorLocn The location of the "Drag" Cursor's 
     * hotspot in Component coordinates
     * @param dropAction The currently selected user drop action
     * @param srcActions The current set of actions supported by the source
     * <P>
     * @throws NullPointerException if cursorLocn is null
     * @throws IllegalArgumentException if the dropAction or 
     * srcActions are illegal values, or if dtc is null
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
     * This method returns a <code>Point</code>
     * indicating the <code>Cursor</code>'s current
     * location within the <code>Component'</code>s
     * coordinates.
     * <P>
     * @return the current cursor location in 
     * <code>Component</code>'s coords.
     */

    public Point getLocation() {
	return location;
    }


    /**
     * This method returns the current <code>DataFlavor</code>s from the
     * <code>DropTargetContext</code>.
     * <P>
     * @return current DataFlavors from the DropTargetContext
     */

    public DataFlavor[] getCurrentDataFlavors() {
	return getDropTargetContext().getCurrentDataFlavors();
    }

    /**
     * This method returns the current <code>DataFlavor</code>s
     * as a <code>java.util.List</code>
     * <P>
     * @return a <code>java.util.List</code> of the Current <code>DataFlavor</code>s
     */

    public List getCurrentDataFlavorsAsList() {
	return getDropTargetContext().getCurrentDataFlavorsAsList();
    }

    /**
     * This method returns a <code>boolean</code> indicating
     * if the specified <code>DataFlavor</code> is supported.
     * <P>
     * @param df the <code>DataFlavor</code> to test
     * <P>
     * @return if a particular DataFlavor is supported
     */

    public boolean isDataFlavorSupported(DataFlavor df) {
	return getDropTargetContext().isDataFlavorSupported(df);
    }

    /**
     * This method returns an <code>int</code> representing
     * set of actions supported by the source.
     * <P>
     * @return source actions
     */

    public int getSourceActions() { return actions; }

    /**
     * This method returns an <code>int</code>
     * representing the currently selected drop action.
     * <P>
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
     * <P>
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





