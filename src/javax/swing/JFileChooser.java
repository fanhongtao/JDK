/*
 * @(#)JFileChooser.java	1.49 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing;

import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.FileChooserUI;

import javax.accessibility.*;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.util.Vector;
import java.awt.Component;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.*;

/**
 * JFileChooser provides a simple mechanism for the user to chooser a file.
 *
 * The following pops up a file chooser in the users home directory that
 * only sees .jpg and .gif images:
 *    JFileChooser chooser = new JFileChooser();
 *    // Note: source for ExtensionFileFilter can be found in the SwingSet demo
 *    ExtensionFileFilter filter = new ExtensionFileFilter();
 *    filter.addExtension("jpg");
 *    filter.addExtension("gif");
 *    filter.setDescription("JPG & GIF Images");
 *    chooser.setFileFilter(filter);
 *    int returnVal = chooser.showOpenDialog(parent);
 *    if(returnVal == JFileChooser.APPROVE_OPTION) {
 *       System.out.println("You chose to open this file: " +
 *            chooser.getSelectedFile().getName());
 *    }
 *
 * @beaninfo
 *   attribute: isContainer false
 *
 * @version 1.49 04/22/99
 * @author Jeff Dinkins
 *
 */
public class JFileChooser extends JComponent implements Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "FileChooserUI";


    // ************************
    // ***** Dialog Types *****
    // ************************

    /**
     * Type value indicating that the FileChooser supports an "Open"
     * file operation.
     */
    public static final int OPEN_DIALOG = 0;

    /**
     * Type value indicating that the FileChooser supports a "Save"
     * file operation.
     */
    public static final int SAVE_DIALOG = 1;

    /**
     * Type value indicating that the FileChooser supports a developer
     * sepcified file operation.
     */
    public static final int CUSTOM_DIALOG = 2;


    // ********************************
    // ***** Dialog Return Values *****
    // ********************************

    /**
     * Return value if cancel is chosen.
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen.
     */
    public static final int APPROVE_OPTION = 0;

    /**
     * Return value if an error occured.
     */
    public static final int ERROR_OPTION = -1;


    // **********************************
    // ***** FileChooser properties *****
    // **********************************


    /** Instruction to display only files. */
    public static final int FILES_ONLY = 0;

    /** Instruction to display only directories. */
    public static final int DIRECTORIES_ONLY = 1;

    /** Instruction to display both files and directories. */
    public static final int FILES_AND_DIRECTORIES = 2;

    /** Instruction to cancel the current selection. */
    public static final String CANCEL_SELECTION = "CancelSelection";

    /** Instruction to approve the current selection (Same as pressing yes or ok.) */
    public static final String APPROVE_SELECTION = "ApproveSelection";

    /** Identifies change in the text on the approve (yes, ok) button. */
    public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";

    /**  Identifies change in the tooltip text for the approve (yes, ok) button . */
    public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";

    /**  Identifies change in the mnemonic for the approve (yes, ok) button . */
    public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";

    /** Identifies user's directory change. */
    public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";

    /** Identifes change in user's single-file selection. */
    public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";

    /** Identifes change in user's multiple-file selection. */
    public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";

    /** Enables multiple-file selections. */
    public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "fileFilterChanged";

    /** Says that a different object is being used to find available drives on the system. */
    public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";

    /** Says that a different object is being used to retrieve file information. */
    public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";

    /** Identifies a change in the display-hidden-files property. */
    public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";

    /** User changed the kind of files to display.*/
    public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";

    /** Identifies a change in the kind of selection (single, multiple, etc.). */
    public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";

    /** Says that a different accessory component is in use. (For example, to preview files.) */
    public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";

    /** Identifies whether a the AcceptAllFileFilter is used or not. */
    private static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "AcceptAllFileFilterUsedChanged";

    /** Identifies a change in the dialog title. */
    public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";

    /**
     * Identifies a change in the type of files displayed (files only,
     * directories only, or both files and directories. 
     */
    public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";

    /** 
     * Identifies a change in the list of predefined file filters
     * the user can choose from
     */
    public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";

    // ******************************
    // ***** instance variables *****
    // ******************************

    private String dialogTitle = null;
    private String approveButtonText = null;
    private String approveButtonToolTipText = null;
    private int approveButtonMnemonic = 0;

    private ActionListener actionListener = null;

    private Vector filters = new Vector(5);
    private JDialog dialog = null;
    private int dialogType = OPEN_DIALOG;
    private int returnValue = ERROR_OPTION;
    private JComponent accessory = null;

    private FileView fileView = null;
    private FileView uiFileView = null;

    private boolean useFileHiding = true;

    private int fileSelectionMode = FILES_ONLY;

    private boolean multiSelectionEnabled = false;

    private boolean useAcceptAllFileFilter = true;

    private FileFilter fileFilter = null;

    private FileSystemView fileSystemView = null;

    private File currentDirectory = null;
    private File selectedFile = null;
    private File[] selectedFiles;

    // *************************************
    // ***** JFileChooser Constructors *****
    // *************************************

    /**
     * Creates a JFileChooser pointing to the user's home directory.
     */
    public JFileChooser() {
	this((File) null, (FileSystemView) null);
    }
    
    /**
     * Creates a JFileChooser using the given path. Passing in a null
     * string causes the file chooser to point to the users home directory.
     *
     * @param path  a String giving the path to a file or directory
     */
    public JFileChooser(String currentDirectoryPath) {
	this(currentDirectoryPath, (FileSystemView) null);
    }

    /**
     * Creates a JFileChooser using the given File as the path. Passing
     * in a null file causes the file chooser to point to the users's
     * home directory.
     *
     * @param directory  a File object specifying the path to a file 
     *                   or directory
     */
    public JFileChooser(File currentDirectory) {
	this(currentDirectory, (FileSystemView) null);
    }

    /**
     * Creates a JFileChooser using the given FileSystemView
     */
    public JFileChooser(FileSystemView fsv) {
	this((File) null, fsv);
    }


    /**
     * Creates a JFileChooser using the given current directory and FileSystemView
     */
    public JFileChooser(File currentDirectory, FileSystemView fsv) {
	setup(fsv);
	setCurrentDirectory(currentDirectory);
    }

    /**
     * Creates a JFileChooser using the given current directory path and FileSystemView
     */
    public JFileChooser(String currentDirectoryPath, FileSystemView fsv) {
	setup(fsv);
	if(currentDirectoryPath == null) {
	    setCurrentDirectory(null);
        } else {
	    setCurrentDirectory(fileSystemView.createFileObject(currentDirectoryPath));
	}
    }

    /**
     * Perform common constructor initialization and setup
     */
    protected void setup(FileSystemView view) {
	if(view == null) {
	    view = FileSystemView.getFileSystemView();
        }
	setFileSystemView(view);
	updateUI(); 
	if(isAcceptAllFileFilterUsed()) {
	    setFileFilter(getAcceptAllFileFilter());
	}
    }


    // *****************************
    // ****** File Operations ******
    // *****************************

    /**
     * Returns the selected file. This can be set either by the
     * programmer via setFile() or by a user action, such as
     * either typing the filename int the UI or selecting the
     * file from a list in the UI.
     * 
     * @see #setSelectedFile
     * @return the selected file
     */
    public File getSelectedFile() {
	return selectedFile;
    }

    /**
     * Sets the selected file. If the file's parent directory is
     * not the current directory, changes the current directory
     * to be the file's parent directory.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     *
     * @see #getSelectedFile
     *
     * @param selectedFile the selected file 
     */
    public void setSelectedFile(File file) {
	// PENDING(jeff) - make sure that the file's path is
	// in the current directory. If not, change the current
	// directory to the file's path. 
	File oldValue = selectedFile;
	selectedFile = file;
	if(selectedFile != null) {
	    String parent = selectedFile.getParent();
	    if(parent != null) {
		File parentF = getFileSystemView().createFileObject(parent);
		if(!parentF.equals(getCurrentDirectory())) {
		    setCurrentDirectory(parentF);
		} 
	    }
	    ensureFileIsVisible(selectedFile);
	}
	firePropertyChange(SELECTED_FILE_CHANGED_PROPERTY, oldValue, selectedFile);
    }

    /**
     * Returns a list of selected files if the filechooser is
     * set to allow multi-selection.
     */
    public File[] getSelectedFiles() {
	if(selectedFiles == null) {
	    return new File[0];
	} else {
	    return (File[]) selectedFiles.clone();
	}
    }

    /**
     * Sets the list of selected files if the filechooser is
     * set to allow multi-selection.
     *
     * @beaninfo
     *       bound: true
     * description: the list of selected files if the chooser is in multi-selection mode
     */
    public void setSelectedFiles(File[] selectedFiles) {
	File[] oldValue = this.selectedFiles;
	this.selectedFiles = selectedFiles;
	firePropertyChange(SELECTED_FILES_CHANGED_PROPERTY, oldValue, this.selectedFiles);
    }

    /**
     * Returns the current directory. 
     *
     * @return the current directory
     * @see #setCurrentDirectory
     */
    public File getCurrentDirectory() {
	return currentDirectory;
    }

    /**
     * Sets the current directory. Passing in null sets the filechooser
     * to point to the users's home directory.
     *
     * If the file passed in as currentDirectory is not a directory, the
     * parent of the file will be used as the currentDirectory. If the
     * parent is not traversable, then it will walk up the parent tree
     * until it finds a traversable direcotry, or hits the root of the
     * file system.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: the directory that the FileChooser is showing files of
     *
     * @param currentDirectory the current directory to point to
     * @see #getCurrentDirectory
     */
    public void setCurrentDirectory(File dir) {
	File oldValue = currentDirectory;
	
	if(dir == null) {
	    currentDirectory = getFileSystemView().getHomeDirectory();
            firePropertyChange(DIRECTORY_CHANGED_PROPERTY, oldValue, currentDirectory);
            return;
	}
	
	
	if (currentDirectory != null) {
	    /* Verify the toString of object */
	    if (this.currentDirectory.equals(dir)) {
		return;
	    }
	}
	
	File prev = null;
	while(!isTraversable(dir) && prev != dir && !getFileSystemView().isRoot(dir)) {
	    prev = dir;
	    dir = getFileSystemView().getParentDirectory(dir);
	}
	currentDirectory = dir;

	firePropertyChange(DIRECTORY_CHANGED_PROPERTY, oldValue, currentDirectory);
    }

    /**
     * Changes the directory to be set to the parent of the
     * current directory. 
     *
     * @see #getCurrentDirectory
     */
    public void changeToParentDirectory() {
	File oldValue = getCurrentDirectory();
	setCurrentDirectory(getFileSystemView().getParentDirectory(oldValue));
    }

    /**
     * Tells the UI to rescan it's files list from the current directory.
     */
    public void rescanCurrentDirectory() {
        getUI().rescanCurrentDirectory(this);
    }

    /**
     * Make sure that the specified file is viewable, and
     * not hidden.
     *
     * @param f  a File object
     */
    public void ensureFileIsVisible(File f) {
        getUI().ensureFileIsVisible(this, f);
    }

    // **************************************
    // ***** FileChooser Dialog methods *****
    // **************************************

    /**
     * Pops up an "Open File" file chooser dialog. Note that the
     * text that appears in the approve button is determined by
     * the L&F.
     *
     *
     * @return   the return state of the filechooser on popdown:
     *             CANCEL_OPTION, APPROVE_OPTION
     */
    public int showOpenDialog(Component parent) {
	setDialogType(OPEN_DIALOG);
	return showDialog(parent, null);
    }

    /**
     * Pops up a "Save File" file chooser dialog. Note that the
     * text that appears in the approve button is determined by
     * the L&F.
     *
     * @return   the return state of the filechooser on popdown:
     *             CANCEL_OPTION, APPROVE_OPTION
     */
    public int showSaveDialog(Component parent) {
	setDialogType(SAVE_DIALOG);
	return showDialog(parent, null);
    }

    /**
     * Pops a custom file chooser dialog with a custom ApproveButton.
     *
     * e.g. filechooser.showDialog(parentWindow, "Run Application");
     * would pop up a filechooser with a "Run Application" button
     * (instead of the normal "Save" or "Open" button).
     *
     * Alternatively, the following code will do the same thing:
     *    JFileChooser chooser = new JFileChooser(null);
     *    chooser.setApproveButtonText("Run Application");
     *    chooser.showDialog(this, null);
     * 
     * PENDING(jeff) - the following method should be added to the api:
     *      showDialog(Component parent);
     *
     * @param   approveButtonText the text of the ApproveButton
     * @return  the return state of the filechooser on popdown:
     *             CANCEL_OPTION, APPROVE_OPTION
     */
    public int showDialog(Component parent, String approveButtonText) {
	if(approveButtonText != null) {
	    setApproveButtonText(approveButtonText);
	    setDialogType(CUSTOM_DIALOG);
	}

        Frame frame = parent instanceof Frame ? (Frame) parent
              : (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);

	String title = null;

	if(getDialogTitle() != null) {
	    title = dialogTitle;
	} else {
	    title = getUI().getDialogTitle(this);
	}

        dialog = new JDialog(frame, title, true);
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
 
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
 
        dialog.show();
	return returnValue;
    }

    // **************************
    // ***** Dialog Options *****
    // **************************

    /**
     * Returns the type of this dialog.
     *
     * @return   the type of dialog to be displayed:
     *           OPEN_DIALOG, SAVE_DIALOG, CUSTOM_DIALOG
     *
     * @see #setDialogType
     */
    public int getDialogType() {
	return dialogType;
    }

    /**
     * Sets the type of this dialog. Use OPEN_DIALOG when you want to
     * bring up a filechooser that the user can use to open a file. Likewise,
     * use SAVE_DIALOG for letting the user choose a file for saving.
     *
     * Use CUSTOM_DIALOG when you want to use the filechooser in a context
     * other than "Open" or "Save". For instance, you might want to bring up
     * a filechooser that allows the user to choose a file to execute. Note that
     * you normally would not need to set the FileChooser to use CUSTOM_DIALOG
     * since a call to setApproveButtonText does this for you.
     *
     * @param dialogType the type of dialog to be displayed:
     *                   OPEN_DIALOG, SAVE_DIALOG, CUSTOM_DIALOG
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The type (open, save, custom) of the FileChooser
     *        enum: 
     *              OPEN_DIALOG JFileChooser.OPEN_DIALOG
     *              SAVE_DIALOG JFileChooser.SAVE_DIALOG
     *              CUSTOM_DIALOG JFileChooser.CUSTOM_DIALOG
     *
     * @see #getDialogType
     * @see #setApproveButtonText
     */ 
    // PENDING(jeff) - fire button text change property
    public void setDialogType(int dialogType) {
	if(this.dialogType == dialogType) {
	    return;
	}
	if(!(dialogType == OPEN_DIALOG || dialogType == SAVE_DIALOG || dialogType == CUSTOM_DIALOG)) {
	    throw new IllegalArgumentException("Incorrect Dialog Type: " + dialogType);
	}
	int oldValue = this.dialogType;
	this.dialogType = dialogType;
	if(dialogType == OPEN_DIALOG || dialogType == SAVE_DIALOG) {
	    setApproveButtonText(null);
	}
	firePropertyChange(DIALOG_TYPE_CHANGED_PROPERTY, oldValue, dialogType);
    }

    /**
     * Sets the string that goes in the FileChooser window's title bar.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The title of the FileChooser dialog window
     *
     * @see #getDialogTitle
     *
     */
    public void setDialogTitle(String dialogTitle) {
	String oldValue = this.dialogTitle;
	this.dialogTitle = dialogTitle;
	if(dialog != null) {
	    dialog.setTitle(dialogTitle);
	}
	firePropertyChange(DIALOG_TITLE_CHANGED_PROPERTY, oldValue, dialogTitle);
    }

    /**
     * Gets the string that goes in the FileChooser's titlebar.
     *
     * @see #setDialogTitle
     */
    public String getDialogTitle() {
	return dialogTitle;
    }

    // ************************************
    // ***** FileChooser View Options *****
    // ************************************



    /**
     * Sets the tooltip text used in the ApproveButton.
     * If null, the UI object will determine the button's text.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The tooltip text for the ApproveButton
     *
     * @return the text used in the ApproveButton
     *
     * @see #setApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */ 
    public void setApproveButtonToolTipText(String toolTipText) {
	if(approveButtonToolTipText == toolTipText) {
	    return;
	}
	String oldValue = approveButtonToolTipText;
	approveButtonToolTipText = toolTipText;
	setDialogType(CUSTOM_DIALOG);
	firePropertyChange(APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY, oldValue, approveButtonToolTipText);
    }


    /**
     * Returns the tooltip text used in the ApproveButton.
     * If null, the UI object will determine the button's text.
     *
     * @return the text used in the ApproveButton
     *
     * @see #setApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */ 
    public String getApproveButtonToolTipText() {
	return approveButtonToolTipText;
    }

    /**
     * Returns the approve button's mnemonic.
     * @return an int value for the mnemonic key
     *
     * @see #setApproveButtonMnemonic
     */
    public int getApproveButtonMnemonic() {
	return approveButtonMnemonic;
    }

    /**
     * Sets the approve button's mnemonic using a numeric keycode.
     * @param an int value for the mnemonic key
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The mnemonic key accelerator for the ApproveButton
     *
     * @see #getApproveButtonMnemonic
     */
    public void setApproveButtonMnemonic(int mnemonic) {
	if(approveButtonMnemonic == mnemonic) {
	   return;
	}
	int oldValue = approveButtonMnemonic;
	approveButtonMnemonic = mnemonic;
	firePropertyChange(APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY, oldValue, approveButtonMnemonic);
    }

    /**
     * Sets the approve button's mnemonic using a character.
     * @param an char value for the mnemonic key
     *
     * @see #getApproveButtonMnemonic
     */
    public void setApproveButtonMnemonic(char mnemonic) {
        int vk = (int) mnemonic;
        if(vk >= 'a' && vk <='z') {
	    vk -= ('a' - 'A');
	}
        setApproveButtonMnemonic(vk);
    }


    /**
     * Sets the text used in the ApproveButton in the FileChooserUI.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The text that goes in the AprroveButton
     *
     * @param approveButtonText the text used in the ApproveButton
     *
     * @see #getApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */ 
    // PENDING(jeff) - have ui set this on dialog type change
    public void setApproveButtonText(String approveButtonText) {
	if(this.approveButtonText == approveButtonText) {
	    return;
	}
	String oldValue = this.approveButtonText;
	this.approveButtonText = approveButtonText;
	firePropertyChange(APPROVE_BUTTON_TEXT_CHANGED_PROPERTY, oldValue, approveButtonText);
    }

    /**
     * Returns the text used in the ApproveButton in the FileChooserUI.
     * If null, the UI object will determine the button's text.
     *
     * Typically, this would be "Open" or "Save".
     *
     * @return the text used in the ApproveButton
     *
     * @see #setApproveButtonText
     * @see #setDialogType
     * @see #showDialog
     */ 
    public String getApproveButtonText() {
	return approveButtonText;
    }

    /**
     * Gets the list of user choosable file filters
     *
     * @return a FileFilter array containing all the choosable
     *         file filters
     *
     * @ see #addChoosableFileFilter
     * @ see #removeChoosableFileFilter
     * @ see #resetChoosableFileFilter
     */ 
    public FileFilter[] getChoosableFileFilters() {
	FileFilter[] filterArray = new FileFilter[filters.size()];
	filters.copyInto(filterArray);
	return filterArray;
    }

    /**
     * Adds a filter to the list of user choosable file filters.
     * 
     * @param filter the FileFilter to add to the choosable file
     *               filter list
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Adds a filter to the list of user choosable file filters.
     *
     * @ see #getChoosableFileFilters
     * @ see #removeChoosableFileFilter
     * @ see #resetChoosableFileFilter
     */ 
    public void addChoosableFileFilter(FileFilter filter) {
	if(filter != null && !filters.contains(filter)) {
	    FileFilter[] oldValue = getChoosableFileFilters();
	    filters.addElement(filter);
	    firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
	} 
	setFileFilter(filter);
    }

    /**
     * Removes a filter from the list of user choosable file filters. Returns
     * true if the file filter was removed;
     *
     * @ see #addChoosableFileFilter
     * @ see #getChoosableFileFilters
     * @ see #resetChoosableFileFilter
     */ 
    public boolean removeChoosableFileFilter(FileFilter f) {
	if(filters.contains(f)) {
            if(getFileFilter() == f) {
		setFileFilter(null);
            }
	    FileFilter[] oldValue = getChoosableFileFilters();
	    filters.removeElement(f);
	    firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Resets the choosable file filter list to its starting state. Normally,
     * this removes all added file filters while leaving the AcceptAll file filter.
     *
     * @see #addChoosableFileFilter
     * @see #getChoosableFileFilters
     * @see #removeChoosableFileFilter
     */
    public void resetChoosableFileFilters() {
	FileFilter[] oldValue = getChoosableFileFilters();
	setFileFilter(null);
	filters.removeAllElements();
	if(isAcceptAllFileFilterUsed()) {
	   addChoosableFileFilter(getAcceptAllFileFilter());
	}
	firePropertyChange(CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY, oldValue, getChoosableFileFilters());
    }

    /**
     * Returns the AcceptAll file filter (e.g. (All Files *.*) on windows).
     */
    public FileFilter getAcceptAllFileFilter() {
	FileFilter filter = null;
	if(getUI() != null) {
	    filter = getUI().getAcceptAllFileFilter(this);
	}
	return filter;
    }

   /**
    * Returns whether the AcceptAll FileFilter is used.
    * @see setAcceptAllFileFilterUsed
    */
    private boolean isAcceptAllFileFilterUsed() {
	return useAcceptAllFileFilter;
    }

   /**
    * Determins if the AcceptAll FileFilter is used.
    * @see isAcceptAllFileFilterUsed
    *
    * PENDING(jeff) make this public in next major release
    * PENDING(jeff) fire property change event
    */
    private void setAcceptAllFileFilterUsed(boolean b) {
	boolean oldValue = useAcceptAllFileFilter;
	useAcceptAllFileFilter = b;
	if(!b) {
	    removeChoosableFileFilter(getAcceptAllFileFilter());
	} else {
	    removeChoosableFileFilter(getAcceptAllFileFilter());
	    addChoosableFileFilter(getAcceptAllFileFilter());
	}
	firePropertyChange(ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY, oldValue, useAcceptAllFileFilter);
    }

    /**
     * Return the accessory component.
     *
     * @return this JFileChooser's accessory component, or null
     * @see #setAccessory
     */
    public JComponent getAccessory() {
        return accessory;
    }

    /**
     * Sets the accessory component. An accessory is often used to show a preview
     * image of the selected file; however, it can be used for anything that
     * the programmer wishes - such as extra custom file chooser controls.
     *
     * Note: if there was a previous accessory, you should unregister
     * any listeners that the accessory might have registered with the
     * file chooser.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the accessory component on the FileChooser.
     */
    public void setAccessory(JComponent newAccessory) {
        JComponent oldValue = accessory;
        accessory = newAccessory;
	firePropertyChange(ACCESSORY_CHANGED_PROPERTY, oldValue, accessory);
    }
    
    /**
     * Sets the FileChooser to allow the user to just select files, just select
     * directories, or select both files and directories.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the types of files that the FileChooser can choose.
     *        enum: FILES_ONLY JFileChooser.FILES_ONLY
     *              DIRECTORIES_ONLY JFileChooser.DIRECTORIES_ONLY
     *              FILES_AND_DIRECTORIES JFileChooser.FILES_AND_DIRECTORIES
     *
     * @param dialogType the type of dialog to be displayed:
     *                   FILES_ONLY, DIRECTORIES_ONLY, FILES_AND_DIRECTORIES
     *
     * @see #getFileSelectionMode
     */
    public void setFileSelectionMode(int mode) {
	if(fileSelectionMode == mode) {
	    return;
	}
	int oldValue = fileSelectionMode;
	fileSelectionMode = mode;
	firePropertyChange(FILE_SELECTION_MODE_CHANGED_PROPERTY, oldValue, fileSelectionMode);
    }

    /**
     * Returns the current file-selection mode.
     *
     * @param an int indicating the type of dialog to be displayed:
     *                   FILES_ONLY, DIRECTORIES_ONLY, FILES_AND_DIRECTORIES
     * @see #setFileSelectionMode
     */
    public int getFileSelectionMode() {
	return fileSelectionMode;
    }

    /**
     * Convenience call that determines if files are selectable based on the current
     * file selection mode
     *
     * @see #setFileSelectionMode
     * @see #getFileSelectionMode
     */
    public boolean isFileSelectionEnabled() {
	return ((fileSelectionMode == FILES_ONLY) || (fileSelectionMode == FILES_AND_DIRECTORIES));
    }

    /**
     * Convenience call that determines if directories are selectable based on the current
     * file selection mode
     *
     * @see #setFileSelectionMode
     * @see #getFileSelectionMode
     */
    public boolean isDirectorySelectionEnabled() {
	return ((fileSelectionMode == DIRECTORIES_ONLY) || (fileSelectionMode == FILES_AND_DIRECTORIES));
    }

    /**
     * Sets the filechooser to allow multiple file selections.
     * NOTE: this functionality is not yet implemented in the current L&Fs.
     *
     * @beaninfo
     *       bound: true
     * description: Sets multiple file selection mode
     *
     * @see #isMultiSelectionEnabled
     */
    public void setMultiSelectionEnabled(boolean b) {
	if(multiSelectionEnabled == b) {
	    return;
	}
	boolean oldValue = multiSelectionEnabled;
	multiSelectionEnabled = b;
	firePropertyChange(MULTI_SELECTION_ENABLED_CHANGED_PROPERTY, oldValue, multiSelectionEnabled);
    }

    /**
     * Returns true if multiple files can be selected.
     * @return true if multiple files can be selected.
     * @see #setMultiSelectionEnabled
     */
    public boolean isMultiSelectionEnabled() {
	return multiSelectionEnabled;
    }

    
    /**
     * If true, hidden files are not shown in the filechooser
     *
     * @return the status of the file hiding property
     * @see #setFileHidingEnabled
     */
    public boolean isFileHidingEnabled() {
	return useFileHiding;
    }

    /**
     * Sets file hiding on or off. If true, hidden files are not shown
     * in the filechooser. The job of determining which files are
     * show is done by the FileView.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets file hiding on or off.
     *
     * @param b the boolean value that determines whether file hiding is
     *          turned on or not.
     * @see #isFileHidingEnabled
     */
    public void setFileHidingEnabled(boolean b) {
	boolean oldValue = useFileHiding;
	useFileHiding = b;
	firePropertyChange(FILE_HIDING_CHANGED_PROPERTY, oldValue, useFileHiding);
    }

    /**
     * Sets the current File Filter. The file filter is used by the
     * filechooser to filter out files from view from the user.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the File Filter used to filter out files of type.
     *
     * @param filter the new current file filter to use
     * @see #getFileFilter
     */
    public void setFileFilter(FileFilter filter) {
	FileFilter oldValue = fileFilter;
	fileFilter = filter;
	if(selectedFile != null && fileFilter != null && !filter.accept(selectedFile)) {
	    setSelectedFile(null);
	}
	firePropertyChange(FILE_FILTER_CHANGED_PROPERTY, oldValue, fileFilter);
    }
    

    /**
     * Returns the currently selected file filter.
     *
     * @return the current file filter.
     * @see #setFileFilter
     * @see #addChoosableFileFilter
     */
    public FileFilter getFileFilter() {
	return fileFilter;
    }

    /**
     * Sets the file view to used to retrieve UI information, such as
     * the icon that represents a file or the type description of a file.
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: Sets the File View used to get file type information
     *
     * @see #getFileView
     */
    public void setFileView(FileView fileView) {
	FileView oldValue = this.fileView;
	this.fileView = fileView;
	firePropertyChange(FILE_VIEW_CHANGED_PROPERTY, oldValue, fileView);
    }

    /**
     * Returns the current file view.
     *
     * @see #setFileView
     */
    public FileView getFileView() {
	return fileView;
    }
    
    // ******************************
    // *****FileView delegation *****
    // ******************************

    // NOTE: all of the following methods attempt to delegate
    // first to the client set fileView, and if null is returned
    // (or there is now client defined fileView) then calls the
    // UI's default fileView.
    
    /**
     * Returns the file name.
     * @see FileView#getName
     */
    public String getName(File f) {
	String filename = null;
	if(getFileView() != null) {
	    filename = getFileView().getName(f);
	}
	if(filename == null && uiFileView != null) {
	    filename = uiFileView.getName(f);
	}
	return filename;
    }

    /**
     * Returns the file description.
     * @see FileView#getDescription
     */
    public String getDescription(File f) {
	String description = null;
	if(getFileView() != null) {
	    description = getFileView().getDescription(f);
	}
	if(description == null && uiFileView != null) {
	    description = uiFileView.getDescription(f);
	}
	return description;
    }

    /**
     * Returns the file type.
     * @see FileView#getTypeDescription
     */
    public String getTypeDescription(File f) {
	String typeDescription = null;
	if(getFileView() != null) {
	    typeDescription = getFileView().getTypeDescription(f);
	}
	if(typeDescription == null && uiFileView != null) {
	    typeDescription = uiFileView.getTypeDescription(f);
	}
	return typeDescription;
    }

    /**
     * Returns the icon for this file or type of file, depending
     * on the system.
     * @see FileView#getIcon
     */
    public Icon getIcon(File f) {
	Icon icon = null;
	if(getFileView() != null) {
	    icon = getFileView().getIcon(f);
	}
	if(icon == null && uiFileView != null) {
	    icon = uiFileView.getIcon(f);
	}
	return icon;
    }

    /**
     * Returns true if the file (directory) can be visited.
     * Returns false if the directory cannot be traversed.
     * @see FileView#isTraversable
     */
    public boolean isTraversable(File f) {
	Boolean traversable = null;
	if(getFileView() != null) {
	    traversable = getFileView().isTraversable(f);
	}
	if(traversable == null && uiFileView != null) {
	    traversable = uiFileView.isTraversable(f);
	}
	if(traversable == null && f != null) {
	    if(f.isDirectory()) {
		traversable = Boolean.TRUE;
	    } else {
		traversable = Boolean.FALSE;
	    }
	} else if(traversable == null) {
	    return false;
	}
	return traversable.booleanValue();
    }

    /**
     * Returns true if the file should be displayed.
     * @see FileFilter#accept
     */
    public boolean accept(File f) {
	boolean shown = true;
	if(fileFilter != null) {
	    shown = fileFilter.accept(f);
	}
	return shown;
    }

    /**
     * Sets the file system view which the JFileChooser uses to
     * access and create file system resouces, such as finding
     * the floppy drive and getting a list of root drives.
     *
     * @beaninfo
     *      expert: true
     *       bound: true
     * description: Sets the FileSytemView used to get filesystem information
     *
     * @see FileSystemView
     */
    public void setFileSystemView(FileSystemView fsv) {
	FileSystemView oldValue = fileSystemView;
	fileSystemView = fsv;
	firePropertyChange(FILE_SYSTEM_VIEW_CHANGED_PROPERTY, oldValue, fileSystemView);
    }

    /**
     * Returns the file system view.
     * @return the FileSystemView object
     * @see #setFileSystemView
     */
    public FileSystemView getFileSystemView() {
	return fileSystemView;
    }

    // **************************
    // ***** Event Handling *****
    // **************************

    /**
     * Called by the UI when the user hits the approve
     * (AKA "Open" or "Save") button. This can also by
     * called by the programmer.
     */
    public void approveSelection() {
	returnValue = APPROVE_OPTION;
	if(dialog != null) {
	    dialog.setVisible(false);
	}
	fireActionPerformed(APPROVE_SELECTION);
    }

    /**
     * Called by the UI when the user hits the cancel button.
     * This can also be called by the programmer.
     */
    public void cancelSelection() {
	returnValue = CANCEL_OPTION;
	if(dialog != null) {
	    dialog.setVisible(false);
	}
	fireActionPerformed(CANCEL_SELECTION);
    }

    /**
     * adds an ActionListener to the button
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
 
    /**
     * removes an ActionListener from the button
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
 
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type. The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireActionPerformed(String command) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new ActionEvent(this,
                                        ActionEvent.ACTION_PERFORMED,
                                        command);
                }
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }
        }
    }

    // *********************************
    // ***** Pluggable L&F methods *****
    // *********************************

    /**
     * Notification from the UIFactory that the L&F
     * has changed.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
	FileChooserUI ui = ((FileChooserUI)UIManager.getUI(this));
        setUI(ui);

	uiFileView = getUI().getFileView(this);
	if(isAcceptAllFileFilterUsed()) {
	    addChoosableFileFilter(getAcceptAllFileFilter());
	}
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return "ButtonUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo
     *        expert: true
     *   description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Gets the UI object which implements the L&F for this component.
     *
     * @return the FileChooserUI object that implements the FileChooserUI L&F
     */
    public FileChooserUI getUI() {
        return (FileChooserUI) ui;
    }

    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JFileChooser. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JFileChooser.
     */
    protected String paramString() {
        String approveButtonTextString = (approveButtonText != null ?
					  approveButtonText: "");
        String dialogTitleString = (dialogTitle != null ?
				    dialogTitle: "");
        String dialogTypeString;
        if (dialogType == OPEN_DIALOG) {
            dialogTypeString = "OPEN_DIALOG";
        } else if (dialogType == SAVE_DIALOG) {
            dialogTypeString = "SAVE_DIALOG";
        } else if (dialogType == CUSTOM_DIALOG) {
            dialogTypeString = "CUSTOM_DIALOG";
        } else dialogTypeString = "";
        String returnValueString;
        if (returnValue == CANCEL_OPTION) {
            returnValueString = "CANCEL_OPTION";
        } else if (returnValue == APPROVE_OPTION) {
            returnValueString = "APPROVE_OPTION";
        } else if (returnValue == ERROR_OPTION) {
            returnValueString = "ERROR_OPTION";
        } else returnValueString = "";
        String useFileHidingString = (useFileHiding ?
                                    "true" : "false");
        String fileSelectionModeString;
        if (fileSelectionMode == FILES_ONLY) {
            fileSelectionModeString = "FILES_ONLY";
        } else if (fileSelectionMode == DIRECTORIES_ONLY) {
            fileSelectionModeString = "DIRECTORIES_ONLY";
        } else if (fileSelectionMode == FILES_AND_DIRECTORIES) {
            fileSelectionModeString = "FILES_AND_DIRECTORIES";
        } else fileSelectionModeString = "";
        String currentDirectoryString = (currentDirectory != null ?
					 currentDirectory.toString() : "");
        String selectedFileString = (selectedFile != null ?
				     selectedFile.toString() : "");

        return super.paramString() +
        ",approveButtonText=" + approveButtonTextString +
        ",currentDirectory=" + currentDirectoryString +
        ",dialogTitle=" + dialogTitleString +
        ",dialogType=" + dialogTypeString +
        ",fileSelectionMode=" + fileSelectionModeString +
        ",returnValue=" + returnValueString +
        ",selectedFile=" + selectedFileString +
        ",useFileHiding=" + useFileHidingString;
    }

/////////////////
// Accessibility support
////////////////

    protected AccessibleContext accessibleContext = null;

    /**
     * Get the AccessibleContext associated with this JFileChooser
     *
     * @return the AccessibleContext of this JFileChooser
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJFileChooser();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible context for this object.
     */
    protected class AccessibleJFileChooser extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FILE_CHOOSER;
        }

    } // inner class AccessibleJFileChooser

}
