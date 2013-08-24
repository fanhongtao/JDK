/*
 * @(#)UnsupportedFlavorException.java	1.16 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

/**
 * Signals that the requested data is not supported in this flavor.
 * @see Transferable#getTransferData
 *
 * @version 	1.16, 11/17/05
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
