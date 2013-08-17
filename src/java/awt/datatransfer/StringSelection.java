/*
 * @(#)StringSelection.java	1.5 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.datatransfer;

import java.io.*;

/**
 * A class which implements the capability required to transfer a
 * simple java String in plain text format.
 */
public class StringSelection implements Transferable, ClipboardOwner {

    final static int STRING = 0;
    final static int PLAIN_TEXT = 1;

    DataFlavor flavors[] = {DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};

    private String data;
						   
    /**
     * Creates a transferable object capable of transferring the
     * specified string in plain text format.
     */
    public StringSelection(String data) {
        this.data = data;
    }

    /**
     * Returns the array of flavors in which it can provide the data.
     */
    public synchronized DataFlavor[] getTransferDataFlavors() {
	return flavors;
    }

    /**
     * Returns whether the requested flavor is supported by this object.
     * @param flavor the requested flavor for the data
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
	return (flavor.equals(flavors[STRING]) || flavor.equals(flavors[PLAIN_TEXT]));
    }

    /**
     * If the data was requested in the "java.lang.String" flavor, return the
     * String representing the selection.
     *
     * @param flavor the requested flavor for the data
     * @exception UnsupportedFlavorException if the requested data flavor is
     *              not supported in the "<code>java.lang.String</code>" flavor.
     */
    public synchronized Object getTransferData(DataFlavor flavor) 
			throws UnsupportedFlavorException, IOException {
	if (flavor.equals(flavors[STRING])) {
	    return (Object)data;
	} else if (flavor.equals(flavors[PLAIN_TEXT])) {
	    return new StringReader(data);
	} else {
	    throw new UnsupportedFlavorException(flavor);
	}
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
	
