/*
 * @(#)ClipboardOwner.java	1.7 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.datatransfer;

/**
 * Defines the interface for classes that will provide data to
 * a clipboard.
 * 
 * @version 	1.7, 02/02/00
 * @author	Amy Fowler
 */

public interface ClipboardOwner {

    /**
     * Notifies this object that it is no longer the owner of
     * the contents of the clipboard.
     * @param clipboard the clipboard that is no longer owned
     * @param contents the contents which this owner had placed on the clipboard
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents);

}
