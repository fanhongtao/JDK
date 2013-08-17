/*
 * @(#)DropTargetDropEvent.java	1.9 98/08/18
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
import java.awt.datatransfer.Transferable;

import java.awt.dnd.DropTargetEvent;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The DropTargetDropEvent is delivered via the DropTargetListener drop() 
 * method. 
 * </p>
 *
 * @version 1.9
 * @since JDK1.2
 *
 */

public class DropTargetDropEvent extends DropTargetEvent {

    /**
     * Constructs a DropTargetDropEvent.  By default, this constructor 
     * assumes that the target is not in the same virtual machine as 
     * the source; that is, {@link #isLocalTransfer()} will 
     * return <code>false</code>.
     *
     * @param dtc        The DropTargetContext for this operation
     * @param cursorLocn The location of the "Drag" Cursors hotspot in Component coordinates
     * @param dropAction The currently selected user drop action
     * @param srcActions The current set of actions supported by the source
     *
     * @throw NullPointerException if the dtc or cursorLocn are null
     * @throw IllegalArgumentException if the dropAction or srcActions are illegal values
     */

    public DropTargetDropEvent(DropTargetContext dtc, Point cursorLocn, int dropAction, int srcActions)  {
	super(dtc);

	if (cursorLocn == null) throw new NullPointerException("cursorLocn");

	if (dropAction != DnDConstants.ACTION_NONE &&
	    dropAction != DnDConstants.ACTION_COPY &&
	    dropAction != DnDConstants.ACTION_MOVE &&
	    dropAction != DnDConstants.ACTION_LINK
	) throw new IllegalArgumentException("dropAction = " + dropAction);

	if ((srcActions & ~(DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK)) != 0) throw new IllegalArgumentException("srcActions");

	location        = cursorLocn;
	actions         = srcActions;
	this.dropAction = dropAction;
    }

    /**
     * Construct a DropTargetEvent
     * @param dtc        The DropTargetContext for this operation
     * @param cursorLocn The location of the "Drag" Cursors hotspot in Component coordinates
     * @param dropAction The currently selected user drop action
     * @param srcActions The current set of actions supported by the source
     * @param isLocalTx  True iff the source is in the same JVM as the target
     */

    public DropTargetDropEvent(DropTargetContext dtc, Point cursorLocn, int dropAction, int srcActions, boolean isLocal)  {
	this(dtc, cursorLocn, dropAction, srcActions);

	isLocalTx = isLocal;
    }

    /**
     * @return the current cursor location in Component's coords.
     */

    public Point getLocation() {
	return location;
    }


    /**
     * @return current DataFlavors
     */

    public DataFlavor[] getCurrentDataFlavors() {
	return getDropTargetContext().getCurrentDataFlavors();
    }

    /**
     * @return the currently available DataFlavors as a java.util.List
     */

    public List getCurrentDataFlavorsAsList() {
	return getDropTargetContext().getCurrentDataFlavorsAsList();
    }

    /**
     * @return if the DataFlavor specified is available from the source
     */

    public boolean isDataFlavorSupported(DataFlavor df) {
 	return getDropTargetContext().isDataFlavorSupported(df);
    }

    /**
     * @return source actions
     */

    public int getSourceActions() { return actions; }

    /**
     * @return source actions
     */

    public int getDropAction() { return dropAction; }

    /**
     * @return the Transferable associated with the drop
     */

    public Transferable getTransferable() {
	return getDropTargetContext().getTransferable();
    }

    /**
     * accept the Drop, using the specified action.
     */

    public void acceptDrop(int dropAction) {
	getDropTargetContext().acceptDrop(dropAction);
    }

    /**
     * reject the Drop.
     */

    public void rejectDrop() {
	getDropTargetContext().rejectDrop();
    }

    /**
     * notify the DragSource that the drop transfer(s) are completed
     */

    public void dropComplete(boolean success) {
	getDropTargetContext().dropComplete(success);
    }
	
    /**
     * @return if the Source is in the same JVM
     */

    public boolean isLocalTransfer() {
	return isLocalTx;
    }

    /*
     * fields
     */

    static final private Point  zero     = new Point(0,0);

    private Point		location   = zero;
    private int			actions    = DnDConstants.ACTION_NONE;
    private int			dropAction = DnDConstants.ACTION_NONE;

    private boolean	        isLocalTx = false;
}
