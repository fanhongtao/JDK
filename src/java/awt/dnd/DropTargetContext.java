/*
 * @(#)DropTargetContext.java	1.13 98/05/02
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

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Component;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;

import java.awt.dnd.peer.DropTargetContextPeer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * A DropTargetContext is created whenever the logical cursor associated
 * with a Drag and Drop operation coincides with the visibel geometry of
 * a Component, with an associated DropTarget.
 * The DropTargetContext provides the mechanism for a potential receiver
 * of a Drop operation to both provide the end userr with the appropriate
 * drag under feedback, but also to effect the subsequent data transfer
 * if appropriate.
 * </p>
 *
 * @version 1.8
 * @since JDK1.2
 *
 */

public class DropTargetContext {

    /**
     * Construct a DropTargetContext
     */

    DropTargetContext(DropTarget dt) {
	super();

	dropTarget = dt;
    }

    /**
     * @return the DropTarget associated with this Context
     */

    public DropTarget getDropTarget() { return dropTarget; }

    /**
     * @return the Component associated with this Context
     */

    public Component getComponent() { return dropTarget.getComponent(); }

    /**
     * called when associated with the DropTargetContextPeer
     */

    public synchronized void addNotify(DropTargetContextPeer dtcp) {
	dropTargetContextPeer = dtcp;
    }

    /**
     * called when disassociated with the DropTargetContextPeer
     */

    public synchronized void removeNotify() {
	dropTargetContextPeer = null;
	transferable          = null;
    }

    /**
     * set the current actions acceptable to this DropTarget
     */

    protected void setTargetActions(int actions) {
	if (dropTargetContextPeer != null) dropTargetContextPeer.setTargetActions(actions);
    }

    /**
     * @return the current actions acceptable to this DropTarget
     */

    protected int getTargetActions() {
	return ((dropTargetContextPeer != null)
			? dropTargetContextPeer.getTargetActions() 
			: dropTarget.getDefaultActions()
	);
    }

    /**
     * signal that the drop is completed and if it was successful or not
     */

    public void dropComplete(boolean success) throws InvalidDnDOperationException{
	if (dropTargetContextPeer != null)
	    dropTargetContextPeer.dropComplete(success);
    }

    /**
     * accept the Drag
     */

    protected void acceptDrag(int dragOperation) {
	if (dropTargetContextPeer != null) dropTargetContextPeer.acceptDrag(dragOperation);
    }

    /**
     * reject the Drag
     */

    protected void rejectDrag() {
	if (dropTargetContextPeer != null) dropTargetContextPeer.rejectDrag();
    }

    /**
     * called to signal that the drop is acceptable using the specified operation.
     * must be called during DropTargetListener.drop method invocation.
     */

    protected void acceptDrop(int dropOperation) {
	if (dropTargetContextPeer != null) dropTargetContextPeer.acceptDrop(dropOperation);
    }

    /**
     * called to signal that the drop is unacceptable.
     * must be called during DropTargetListener.drop method invocation.
     */

    protected void rejectDrop() {
	if (dropTargetContextPeer != null) dropTargetContextPeer.rejectDrop();
    }

    /**
     * get the available DataFlavors of the Transferable operand of this operation
     */

    protected DataFlavor[] getCurrentDataFlavors() {
	return dropTargetContextPeer != null ? dropTargetContextPeer.getTransferDataFlavors() : new DataFlavor[0];
    }

    /**
     * @return the currently available DataFlavors as a java.util.List
     */

    protected List getCurrentDataFlavorsAsList() {
	return Arrays.asList(getCurrentDataFlavors());
    }

    /**
     * @return if the DataFlavor specified is supported by the source
     */

    protected boolean isDataFlavorSupported(DataFlavor df) {
	return getCurrentDataFlavorsAsList().contains(df);
    }

    /**
     * get the Transferable (proxy) operand of this operation
     */

    protected synchronized Transferable getTransferable() throws InvalidDnDOperationException {
	if (dropTargetContextPeer == null) {
	    throw new InvalidDnDOperationException();
	} else {
	    if (transferable == null) {
		transferable = createTransferableProxy(dropTargetContextPeer.getTransferable(), dropTargetContextPeer.isTransferableJVMLocal());
	    }

	    return transferable;
	}
    }

    /**
     * @return the platform peer
     */

    DropTargetContextPeer getDropTargetContextPeer() { 
	return dropTargetContextPeer;
    }

    /**
     * subclasses may override this to supply their own Proxy Transferable
     */

    protected Transferable createTransferableProxy(Transferable t, boolean local) {
	return new TransferableProxy(t, local);
    }

/****************************************************************************/

    /*
     * private nested class to provide Remote Transferable proxy ...
     */

    protected class TransferableProxy implements Transferable {

	/**
         * construct the proxy
         */

 	TransferableProxy(Transferable t, boolean local) {
	    transferable = t;
	    isLocal	 = local;
	}

	/** 
         * get the flavors
         */

	public synchronized DataFlavor[] getTransferDataFlavors() {
	    return transferable.getTransferDataFlavors();
	}

	/**
         * check if a particular flavor is supported?
         */

	public synchronized boolean isDataFlavorSupported(DataFlavor flavor) {
	    DataFlavor[] flavors = getTransferDataFlavors();
	
	    if (flavors != null && flavors.length != 0) {
		for (int i = 0; i < flavors.length; i++)
		    if (flavors[i].equals(flavor)) return true;
	    }

	    return false;
	}

	/**
         * get the transfer data
         */

	public synchronized Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
	    if (!isDataFlavorSupported(df)) 
		throw new UnsupportedFlavorException(df);
	    else {
		Object data;
		InputStream is = null;

		try {
		    data = transferable.getTransferData(df);
		} catch (Exception e) {
		    throw new IOException(e.getClass() + ":" + e.getMessage() + " caught while getting Data");
		}

		// if its am application/x-java-serialized-object then 
		// we should pass it as a serialized copy of the original

		if (isLocal && df.isFlavorSerializedObjectType()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			(new ObjectOutputStream(baos)).writeObject(data);
		
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			try {
			    data = (new ObjectInputStream(bais)).readObject();
			} catch (ClassNotFoundException cnfe) {
			    throw new IOException(cnfe.getMessage());
			}
		}
	
	        return data;
	    }
	}

	/*
	 * fields
	 */

	protected Transferable	transferable;
	protected boolean	isLocal;
    }

/****************************************************************************/

    /*
     * fields
     */

    private DropTarget	    	  dropTarget;

    private DropTargetContextPeer dropTargetContextPeer;

    private Transferable          transferable;
}
