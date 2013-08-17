/*
 * @(#)Clipboard.java	1.6 98/07/01
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

/**
 * A class which implements a mechanism to transfer data using 
 * cut/copy/paste operations.
 *
 * @version 	1.6, 07/01/98
 * @author	Amy Fowler
 */
public class Clipboard {

    String name;

    protected ClipboardOwner owner;
    protected Transferable contents;

    /**
     * Creates a clipboard object.
     */
    public Clipboard(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this clipboard object.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the current contents of the clipboard to the specified
     * transferable object and registers the specified clipboard owner
     * as the owner of the new contents.  If there is an existing owner 
     * registered, that owner is notified that it no longer holds ownership
     * of the clipboard contents.
     * @param content the transferable object representing the clipboard content
     * @param owner the object which owns the clipboard content
     */
    public synchronized void setContents(Transferable contents, ClipboardOwner owner) {
	if (this.owner != null && this.owner != owner) {
	    this.owner.lostOwnership(this, this.contents);
	}
	this.owner = owner;
	this.contents = contents;
    }

    /**
     * Returns a transferable object representing the current contents
     * of the clipboard.  If the clipboard currently has no contents,
     * it returns null.
     * @param requestor the object requesting the clip data
     * @return the current transferable object on the clipboard
     */
    public synchronized Transferable getContents(Object requestor) {
        return contents;
    }

}

    

    
