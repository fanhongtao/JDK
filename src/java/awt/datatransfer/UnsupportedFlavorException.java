/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

/**
 * Signals that the requested data is not supported in this flavor.
 * @see Transferable#getTransferData
 *
 * @version 	1.13, 02/06/02
 * @author	Amy Fowler
 */
public class UnsupportedFlavorException extends Exception {

     /*
      * JDK 1.1 serialVersionUID 
      */

     private static final long serialVersionUID = 5383814944251665601L;

    /**
     * Constructs an UnsupportedFlavorException.
     * @param flavor the flavor object which caused the exception
     * @throws NullPointerException if flavor is <code>null</code>
     */  
    public UnsupportedFlavorException(DataFlavor flavor) {
	// JCK Test UnsupportedFlavorException0002: if 'flavor' is null, throw
	// NPE
	super(flavor.getHumanPresentableName());
    }
}
