/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.Point;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.awt.dnd.DropTargetEvent;

import java.util.Arrays;
import java.util.List;

/**
 * The <code>DropTargetDropEvent</code> is delivered 
 * via the <code>DropTargetListener</code> drop() method. 
 * 
 * @version 	1.22, 02/06/02
 * @since 1.2
 */

public class DropTargetDropEvent extends DropTargetEvent {

    /**
     * Construct a <code>DropTargetDropEvent</code> given
     * the <code>DropTargetContext</code> for this operation, 
     * the location of the drag <code>Cursor</code>'s
     * hotspot in the <code>Component</code>'s coordinates, 
     * the currently
     * selected user drop action, and the current set of
     * actions supported by the source.
     * By default, this constructor 
     * assumes that the target is not in the same virtual machine as 
     * the source; that is, {@link #isLocalTransfer()} will 
     * return <code>false</code>.
     * <P>
     * @param dtc        The <code>DropTargetContext</code> for this operation
     * @param cursorLocn The location of the "Drag" Cursor's 
     * hotspot in <code>Component</code> coordinates
     * @param dropAction The currently selected user drop action: COPY, MOVE, or LINK
     * constants found in DnDConstants.
     * @param srcActions The current set of actions supported by the source: some
     * combination of COPY, MOVE, or LINK as exposed by the <code>DragSource</code>.
     * <P>
     * @throws <code>NullPointerException</code> 
     * if cursorLocn is <code>null</code>
     * @throws <code>IllegalArgumentException</code> 
     * if the dropAction or srcActions are illegal values,
     * or if dtc is <code>null</code>.
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
     * Construct a <code>DropTargetEvent</code> given the 
     * <code>DropTargetContext</code> for this operation,
     * the location of the drag <code>Cursor</code>'s hotspot 
     * in the <code>Component</code>'s 
     * coordinates, the currently selected user drop action, 
     * the current set of actions supported by the source,
     * and a <code>boolean</code> indicating if the source is in the same JVM
     * as the target.
     * <P>
     * @param dtc        The DropTargetContext for this operation
     * @param cursorLocn The location of the "Drag" Cursor's 
     * hotspot in Component's coordinates
     * @param dropAction The currently selected user drop action: COPY, MOVE, or LINK
     * constants found in DnDConstants.
     * @param srcActions The current set of actions supported by the source: some
     * combination of COPY, MOVE, or LINK as exposed by the <code>DragSource</code>.
     * @param isLocalTx  True if the source is in the same JVM as the target
     */

    public DropTargetDropEvent(DropTargetContext dtc, Point cursorLocn, int dropAction, int srcActions, boolean isLocal)  {
	this(dtc, cursorLocn, dropAction, srcActions);

	isLocalTx = isLocal;
    }

    /**
     * This method returns a <code>Point</code>
     * indicating the <code>Cursor</code>'s current
     * location in the <code>Component</code>'s coordinates.
     * <P>
     * @return the current <code>Cursor</code> location in Component's coords.
     */

    public Point getLocation() {
	return location;
    }


    /**
     * This method returns the current DataFlavors.
     * <P>
     * @return current DataFlavors
     */

    public DataFlavor[] getCurrentDataFlavors() {
	return getDropTargetContext().getCurrentDataFlavors();
    }

    /**
     * This method returns the currently available
     * <code>DataFlavor</code>s as a <code>java.util.List</code>.
     * <P>
     * @return the currently available DataFlavors as a java.util.List
     */

    public List getCurrentDataFlavorsAsList() {
	return getDropTargetContext().getCurrentDataFlavorsAsList();
    }

    /**
     * This method returns a <code>boolean</code> indicating if the
     * specified <code>DataFlavor</code> is available
     * from the source.
     * <P>
     * @param df the <code>DataFlavor</code> to test
     * <P>
     * @return if the DataFlavor specified is available from the source
     */

    public boolean isDataFlavorSupported(DataFlavor df) {
 	return getDropTargetContext().isDataFlavorSupported(df);
    }

    /**
     * This method returns an <code>int</code> representing the 
     * action(s) supported by the source.
     * <P>
     * @return source actions
     */

    public int getSourceActions() { return actions; }

    /**
     * This method returns an <code>int</code>
     * representing the action(s) supported
     * by the source at the time of the drop.
     * <P>
     * @return source actions
     */

    public int getDropAction() { return dropAction; }

    /**
     * This method returns the <code>Transferable</code> object 
     * associated with the drop.
     * <P>
     * @return the <code>Transferable</code> associated with the drop
     */

    public Transferable getTransferable() {
	return getDropTargetContext().getTransferable();
    }

    /**
     * accept the drop, using the specified action.
     * <P>
     * @param dropAction the specified action
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
     * This method notifies the <code>DragSource</code> 
     * that the drop transfer(s) are completed.
     * <P>
     * @param success a <code>boolean</code> indicating that the drop transfer(s) are completed.
     */

    public void dropComplete(boolean success) {
	getDropTargetContext().dropComplete(success);
    }
	
    /**
     * This method returns an <code>int</code> indicating if
     * the source is in the same JVM as the target.
     * <P>
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






