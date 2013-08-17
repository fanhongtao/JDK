/*
 * @(#)FileDialog.java	1.32 98/07/01
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
package java.awt;

import java.awt.peer.FileDialogPeer;
import java.io.FilenameFilter;

/**
 * The <code>FileDialog</code> class displays a dialog window 
 * from which the user can select a file. 
 * <p>
 * Since it is a modal dialog, when the application calls 
 * its <code>show</code> method to display the dialog, 
 * it blocks the rest of the application until the user has 
 * chosen a file. 
 *
 * @see Window#show
 *
 * @version 	1.32, 07/01/98
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 * @since       JDK1.0
 */
public class FileDialog extends Dialog {
    
    /**
     * This constant value indicates that the purpose of the file  
     * dialog window is to locate a file from which to read. 
     * @since    JDK1.0
     */
    public static final int LOAD = 0;

    /**
     * This constant value indicates that the purpose of the file  
     * dialog window is to locate a file to which to write. 
     * @since    JDK1.0
     */
    public static final int SAVE = 1;

    int mode;
    String dir;
    String file;
    FilenameFilter filter;

    private static final String base = "filedlg";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 5035145889651310422L;

    /**
     * Creates a file dialog for loading a file.  The title of the
     * file dialog is initially empty.
     * @param parent the owner of the dialog
     * @since JDK1.1
     */
    public FileDialog(Frame parent) {
	this(parent, "", LOAD);
    }

    /**
     * Creates a file dialog window with the specified title for loading 
     * a file. The files shown are those in the current directory. 
     * @param     parent   the owner of the dialog.
     * @param     title    the title of the dialog.
     * @since     JDK1.0
     */
    public FileDialog(Frame parent, String title) {
	this(parent, title, LOAD);
    }

    /**
     * Creates a file dialog window with the specified title for loading 
     * or saving a file. 
     * <p>
     * If the value of <code>mode</code> is <code>LOAD</code>, then the 
     * file dialog is finding a file to read. If the value of 
     * <code>mode</code> is <code>SAVE</code>, the file dialog is finding 
     * a place to write a file. 
     * @param     parent   the owner of the dialog.
     * @param     title   the title of the dialog.
     * @param     mode   the mode of the dialog.
     * @see       java.awt.FileDialog#LOAD
     * @see       java.awt.FileDialog#SAVE
     * @since     JDK1.0
     */
    public FileDialog(Frame parent, String title, int mode) {
	super(parent, title, true);
	this.mode = mode;
	setLayout(null);
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the file dialog's peer.  The peer allows us to change the look
     * of the file dialog without changing its functionality.
     */
    public void addNotify() {
        synchronized(getTreeLock()) {
	    if (peer == null)
			peer = getToolkit().createFileDialog(this);
	    super.addNotify();
        }
    }

    /**
     * Indicates whether this file dialog box is for loading from a file 
     * or for saving to a file. 
     * @return   the mode of this file dialog window, either 
     *               <code>FileDialog.LOAD</code> or 
     *               <code>FileDialog.SAVE</code>.
     * @see      java.awt.FileDialog#LOAD
     * @see      java.awt.FileDialog#SAVE
     * @see      java.awt.FileDialog#setMode
     * @since    JDK1.0
     */
    public int getMode() {
	return mode;
    }

    /**
     * Sets the mode of the file dialog.
     * @param      mode  the mode for this file dialog, either 
     *                 <code>FileDialog.LOAD</code> or 
     *                 <code>FileDialog.SAVE</code>.
     * @see        java.awt.FileDialog#LOAD
     * @see        java.awt.FileDialog#SAVE
     * @see        java.awt.FileDialog#getMode
     * @exception  IllegalArgumentException if an illegal file 
     *                 dialog mode is used.
     * @since      JDK1.1
     */
    public void setMode(int mode) {
	switch (mode) {
	  case LOAD:
	  case SAVE:
	    this.mode = mode;
	    break;
	  default:
	    throw new IllegalArgumentException("illegal file dialog mode");
	}
    }

    /**
     * Gets the directory of this file dialog.
     * @return    the directory of this file dialog.
     * @see       java.awt.FileDialog#setDirectory
     * @since     JDK1.0
     */
    public String getDirectory() {
	return dir;
    }

    /**
     * Sets the directory of this file dialog window to be the  
     * specified directory. 
     * @param     dir   the specific directory.
     * @see       java.awt.FileDialog#getDirectory
     * @since     JDK1.0
     */
    public void setDirectory(String dir) {
	this.dir = dir;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setDirectory(dir);
	}
    }

    /**
     * Gets the selected file of this file dialog.
     * @return    the currently selected file of this file dialog window, 
     *                or <code>null</code> if none is selected.
     * @see       java.awt.FileDialog#setFile
     * @since     JDK1.0
     */
    public String getFile() {
	return file;
    }

    /**
     * Sets the selected file for this file dialog window to be the 
     * specified file. This file becomes the default file if it is set 
     * before the file dialog window is first shown. 
     * @param    file   the file being set.
     * @see      java.awt.FileDialog#getFile
     * @since    JDK1.0
     */
    public void setFile(String file) {
	this.file = file;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setFile(file);
	}
    }
	
    /**
     * Determines this file dialog's filename filter. A filename filter 
     * allows the user to specify which files appear in the file dialog 
     * window. 
     * @return    this file dialog's filename filter.
     * @see       java.io.FilenameFilter
     * @see       java.awt.FileDialog#setFilenameFilter
     * @since     JDK1.0
     */
    public FilenameFilter getFilenameFilter() {
	return filter;
    }

    /**
     * Sets the filename filter for this file dialog window to the 
     * specified filter. 
     * @param   filter   the specified filter.
     * @see     java.io.FilenameFilter
     * @see     java.awt.FileDialog#getFilenameFilter
     * @since   JDK1.0
     */
    public synchronized void setFilenameFilter(FilenameFilter filter) {
	this.filter = filter;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setFilenameFilter(filter);
	}
    }

    /**
     * Returns the parameter string representing the state of this file 
     * dialog window. This string is useful for debugging. 
     * @return  the parameter string of this file dialog window.
     * @since   JDK1.0
     */
    protected String paramString() {
	String str = super.paramString();
	if (dir != null) {
	    str += ",dir= " + dir;
	}
	return str + ((mode == LOAD) ? ",load" : ",save");
    }

    boolean postsOldMouseEvents() {
        return false;
    }
}
