/*
 * @(#)Clipboard.java	1.15 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.datatransfer;

/**
 * A class which implements a mechanism to transfer data using 
 * cut/copy/paste operations.
 *
 * @see java.awt.Toolkit#getSystemClipboard
 *
 * @version 	1.15, 12/03/01
 * @author	Amy Fowler
 */
public class Clipboard {

    String name;

    protected ClipboardOwner owner;
    protected Transferable contents;

    /**
     * Creates a clipboard object.
     *
     * @see java.awt.Toolkit#getSystemClipboard
     */
    public Clipboard(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this clipboard object.
     *
     * @see java.awt.Toolkit#getSystemClipboard
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the current contents of the clipboard to the specified
     * transferable object and registers the specified clipboard owner
     * as the owner of the new contents.  If there is an existing owner 
     * registered, that owner is notified that it no longer holds ownership
     * of the clipboard contents.  The method throws 
     * <code>IllegalStateException</code> if the clipboard is currently 
     * unavailable.  For example, on some platforms, the system clipboard is 
     * unavailable while it is accessed by another application.
     *
     * @param content the transferable object representing the clipboard content
     * @param owner the object which owns the clipboard content
     * @throws IllegalStateException if the clipboard is currently unavailable
     * @see java.awt.Toolkit#getSystemClipboard
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
     * it returns <code>null</code>. The parameter Object requestor is
     * not currently used.  The method throws 
     * <code>IllegalStateException</code> if the clipboard is currently 
     * unavailable.  For example, on some platforms, the system clipboard is 
     * unavailable while it is accessed by another application.
     *
     * @param requestor the object requesting the clip data  (not used)
     * @return the current transferable object on the clipboard
     * @throws IllegalStateException if the clipboard is currently unavailable
     * @see java.awt.Toolkit#getSystemClipboard
     */
    public synchronized Transferable getContents(Object requestor) {
        return contents;
    }

}

    

    
