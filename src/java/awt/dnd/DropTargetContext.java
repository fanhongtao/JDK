/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * A <code>DropTargetContext</code> is created 
 * whenever the logical cursor associated
 * with a Drag and Drop operation coincides with the visible geometry of
 * a <code>Component</code> associated with a <code>DropTarget</code>.
 * The <code>DropTargetContext</code> provides 
 * the mechanism for a potential receiver
 * of a drop operation to both provide the end user with the appropriate
 * drag under feedback, but also to effect the subsequent data transfer
 * if appropriate.
 *
 * @version 	1.29, 02/06/02
 * @since 1.2
 */

public class DropTargetContext {

    /**
     * Construct a <code>DropTargetContext</code> 
     * given a specified <code>DropTarget</code>.
     * <P>
     * @param dt the DropTarget to associate with
     */

    DropTargetContext(DropTarget dt) {
	super();

	dropTarget = dt;
    }

    /**
     * This method returns the <code>DropTarget</code> associated with this
     * <code>DropTargetContext</code>.
     * <P>
     * @return the <code>DropTarget</code> associated with this <code>DropTargetContext</code>
     */

    public DropTarget getDropTarget() { return dropTarget; }

    /**
     * This method returns the <code>Component</code> associated with
     * this <code>DropTargetContext</code>.
     * <P>
     * @return the Component associated with this Context
     */

    public Component getComponent() { return dropTarget.getComponent(); }

    /**
     * Called when associated with the <code>DropTargetContextPeer</code>.
     * <P>
     * @param dtcp the <code>DropTargetContextPeer</code>
     */

    public synchronized void addNotify(DropTargetContextPeer dtcp) {
	dropTargetContextPeer = dtcp;
    }

    /**
     * Called when disassociated with the <code>DropTargetContextPeer</code>.
     */

    public synchronized void removeNotify() {
	dropTargetContextPeer = null;
	transferable          = null;
    }

    /**
     * This method sets the current actions acceptable to 
     * this <code>DropTarget</code>.
     * <P>
     * @param actions an <code>int</code> representing the supported action(s)
     */

    protected void setTargetActions(int actions) {
	if (dropTargetContextPeer != null) dropTargetContextPeer.setTargetActions(actions);
    }

    /**
     * This method returns an <code>int</code> representing the 
     * current actions this <code>DropTarget</code> will accept.
     * <P>
     * @return the current actions acceptable to this <code>DropTarget</code>
     */

    protected int getTargetActions() {
	return ((dropTargetContextPeer != null)
			? dropTargetContextPeer.getTargetActions() 
			: dropTarget.getDefaultActions()
	);
    }

    /**
     * This method signals that the drop is completed and 
     * if it was successful or not.
     * <P>
     * @param success true for success, false if not
     * <P>
     * @throws InvalidDnDOperationException if a drop is not outstanding/extant
     */

    public void dropComplete(boolean success) throws InvalidDnDOperationException{
	if (dropTargetContextPeer != null)
	    dropTargetContextPeer.dropComplete(success);
    }

    /**
     * accept the Drag.
     * <P>
     * @param dragOperation the supported action(s)
     */

    protected void acceptDrag(int dragOperation) {
	if (dropTargetContextPeer != null) dropTargetContextPeer.acceptDrag(dragOperation);
    }

    /**
     * reject the Drag.
     */

    protected void rejectDrag() {
	if (dropTargetContextPeer != null) dropTargetContextPeer.rejectDrag();
    }

    /**
     * called to signal that the drop is acceptable
     * using the specified operation.
     * must be called during DropTargetListener.drop method invocation.
     * <P>
     * @param dropOperation the supported action(s)
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
     * get the available DataFlavors of the 
     * <code>Transferable</code> operand of this operation.
     * <P>
     * @return a <code>DataFlavor[]</code> containing the
     * supported <code>DataFlavor</code>s of the 
     * <code>Transferable</code> operand.
     */

    protected DataFlavor[] getCurrentDataFlavors() {
	return dropTargetContextPeer != null ? dropTargetContextPeer.getTransferDataFlavors() : new DataFlavor[0];
    }

    /** 
     * This method returns a the currently available DataFlavors 
     * of the <code>Transferable</code> operand
     * as a <code>java.util.List</code>.
     * <P>
     * @return the currently available 
     * DataFlavors as a <code>java.util.List</code>
     */

    protected List getCurrentDataFlavorsAsList() {
	return Arrays.asList(getCurrentDataFlavors());
    }

    /**
     * This method returns a <code>boolean</code> 
     * indicating if the given <code>DataFlavor</code> is
     * supported by this <code>DropTargetContext</code>.
     * <P>
     * @param df the <code>DataFlavor</code>
     * <P>
     * @return if the <code>DataFlavor</code> specified is supported
     */

    protected boolean isDataFlavorSupported(DataFlavor df) {
	return getCurrentDataFlavorsAsList().contains(df);
    }

    /**
     * get the Transferable (proxy) operand of this operation
     * <P>
     * @throws InvalidDnDOperationException if a drag is not outstanding/extant
     * <P>
     * @return the <code>Transferable</code>
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
     * Get the <code>DropTargetContextPeer</code>
     * <P>
     * @return the platform peer
     */

    DropTargetContextPeer getDropTargetContextPeer() { 
	return dropTargetContextPeer;
    }

    /**
     * subclasses may override this to supply their own Proxy Transferable
     * <P>
     * @param t the <code>Transferable</code>
     * @param local <code>boolean</code>
     * <P>
     * @return the <code>Transferable</code>
     */

    protected Transferable createTransferableProxy(Transferable t, boolean local) {
	return new TransferableProxy(t, local);
    }

/****************************************************************************/

  
    /**
     * The <code>TransferableProxy</code> is a 
     * nested helper class that
     * supports the <code>DropTargetContext</code> 
     * in managing the transfer of data.
     * In particular it provides automatic 
     * support for the de-serialization
     * of application/x-java-serialized-object 
     * <code>DatFlavor</code>s.
     */

    protected class TransferableProxy implements Transferable {

	/**
         * construct the proxy
         * <P>
         * @param t the <code>Transferable</code>
         * @param local is local?
         */

 	TransferableProxy(Transferable t, boolean local) {
	    transferable = t;
	    isLocal	 = local;
	}

	/** 
         * get the flavors
         * <P>
         * @return the <code>DataFlavor</code> array
         */

	public synchronized DataFlavor[] getTransferDataFlavors() {
	    return transferable.getTransferDataFlavors();
	}

	/**
         * check if a particular flavor is supported?
         * <P>
         * @param flavor the <code>DataFlavor</code>
         * <P>
         * @return a <code>boolean</code> indicating if
         * the specified <code>DataFlavor</code> is supported.
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
         * <P>
         * @param df the <code>DataFlavor</code>
         * <P>
         * @throws UnsupportedFlavorException if the requested <code>DataFlavor</code> is not supported.
         * @throws IOException if the data is no longer available in the requested flavor.
         * <P>
         * @return the Object
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


        /**
         * The "actual" <code>Transferable</code> 
         * that the Proxy is a Proxy for,
         * usually supplied from the underlying system.
         */
	protected Transferable	transferable;

       /**
        * A <code> boolean</code> indicating if 
        * the <code>DragSource</code> is in the 
        * same JVM as the <code>DropTarget</code>.
        */
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










