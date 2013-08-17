/*
 * @(#)FileDialog.java	1.26 97/01/27 Arthur van Hoff
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.awt.peer.FileDialogPeer;
import java.io.FilenameFilter;

/**
 * The File Dialog class displays a file selection dialog. It is a
 * modal dialog and will block the calling thread when the show method
 * is called to display it, until the user has chosen a file.
 *
 * @see Window#show
 *
 * @version 	1.26, 01/27/97
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class FileDialog extends Dialog {
    
    /**
     * The file load variable.
     */
    public static final int LOAD = 0;

    /**
     * The file save variable.
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
     * file dialog is initialy empty.
     * @param parent the owner of the dialog
     */
    public FileDialog(Frame parent) {
	this(parent, "", LOAD);
    }

    /**
     * Creates a file dialog for loading a file.
     * @param parent the owner of the dialog
     * @param title the title of the Dialog
     */
    public FileDialog(Frame parent, String title) {
	this(parent, title, LOAD);
    }

    /**
     * Creates a file dialog with the specified title and mode.
     * @param parent the owner of the dialog
     * @param title the title of the Dialog
     * @param mode the mode of the Dialog
     */
    public FileDialog(Frame parent, String title, int mode) {
	super(parent, title, true);
	this.name = base + nameCounter++;
	this.mode = mode;
	setLayout(null);
    }

    /**
     * Creates the file dialog's peer.  The peer allows us to change the look
     * of the file dialog without changing its functionality.
     */
    public void addNotify() {
	peer = getToolkit().createFileDialog(this);
	super.addNotify();
    }

    /**
     * Gets the mode of the file dialog.  The mode determines whether
     * this file dialog will be used for loading a file (LOAD) or
     * saving a file (SAVE).
     */
    public int getMode() {
	return mode;
    }

    /**
     * Sets the mode of the file dialog.
     * @param mode  the mode (LOAD or SAVE) for this file dialog.
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
     * Gets the directory of the Dialog.
     */
    public String getDirectory() {
	return dir;
    }

    /**
     * Set the directory of the Dialog to the specified directory.
     * @param dir the specific directory
     */
    public synchronized void setDirectory(String dir) {
	this.dir = dir;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setDirectory(dir);
	}
    }

    /**
     * Gets the file of the Dialog.
     */
    public String getFile() {
	return file;
    }

    /**
     * Sets the file for this dialog to the specified file. This will 
     * become the default file if set before the dialog is shown.
     * @param file the file being set
     */
    public synchronized void setFile(String file) {
	this.file = file;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setFile(file);
	}
    }
	
    /**
     * Gets the filter.
     */
    public FilenameFilter getFilenameFilter() {
	return filter;
    }

    /**
     * Sets the filter for this dialog to the specified filter.
     * @param filter the specified filter
     */
    public synchronized void setFilenameFilter(FilenameFilter filter) {
	this.filter = filter;
	FileDialogPeer peer = (FileDialogPeer)this.peer;
	if (peer != null) {
	    peer.setFilenameFilter(filter);
	}
    }

    /**
     * Returns the parameter String of this file dialog.
     * Parameter String.
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
