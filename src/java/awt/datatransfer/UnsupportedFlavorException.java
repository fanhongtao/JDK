/*
 * @(#)UnsupportedFlavorException.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

/**
 * Signals that the requested data is not supported in this flavor.
 * @see Transferable#getTransferData
 *
 * @version 	1.15, 12/19/03
 * @author	Amy Fowler
 */
public class UnsupportedFlavorException extends Exception {

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 5383814944251665601L;

    /**
     * Constructs an UnsupportedFlavorException.
     *
     * @param flavor the flavor object which caused the exception. May
     *        be <code>null</code>.
     */  
    public UnsupportedFlavorException(DataFlavor flavor) {
        super((flavor != null) ? flavor.getHumanPresentableName() : null);
    }
}
