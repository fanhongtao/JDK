/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

/**
 * Defines the interface for classes that will provide data to
 * a clipboard.
 * 
 * @version 	1.8, 02/06/02
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
