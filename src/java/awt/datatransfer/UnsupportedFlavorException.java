/*
 * @(#)UnsupportedFlavorException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.datatransfer;

/**
 * Signals that the requested data is not supported in this flavor.
 * @see Transferable#getTransferData
 *
 * @version 	1.6, 09/21/98
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
