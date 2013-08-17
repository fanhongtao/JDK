/*
 * @(#)UnsupportedFlavorException.java	1.7 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

/**
 * Signals that the requested data is not supported in this flavor.
 * @see Transferable#getTransferData
 *
 * @version 	1.7, 11/29/01
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
     */  
    public UnsupportedFlavorException(DataFlavor flavor) {
        super(flavor.getHumanPresentableName());
    }
}
