/*
 * @(#)BasicFileChooserUI.java	1.17 01/01/11
 *
 * Copyright 1998-2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Basic L&F implementation of a FileChooser.
 *
 * @version %i% %g%
 * @author Jeff Dinkins
 */
public class BasicFileChooserUI extends FileChooserUI {

    /* FileView icons */
    protected Icon directoryIcon = null;
    protected Icon fileIcon = null;
    protected Icon computerIcon = null;
    protected Icon hardDriveIcon = null;
    protected Icon floppyDriveIcon = null;

    protected Icon newFolderIcon = null;
    protected Icon upFolderIcon = null;
    protected Icon homeFolderIcon = null;
    protected Icon listViewIcon = null;
    protected Icon detailsViewIcon = null;

    protected int saveButtonMnemonic = 0;
    protected int openButtonMnemonic = 0;
    protected int cancelButtonMnemonic = 0;
    protected int updateButtonMnemonic = 0;
    protected int helpButtonMnemonic = 0;

    protected String saveButtonText = null;
    protected String openButtonText = null;
    protected String cancelButtonText = null;
    protected String updateButtonText = null;
    protected String helpButtonText = null;

    private String newFolderErrorSeparator = null;
    private String newFolderErrorText = null;
    private String fileDescriptionText = null;
    private String directoryDescriptionText = null;

    protected String saveButtonToolTipText = null;
    protected String openButtonToolTipText = null;
    protected String cancelButtonToolTipText = null;
    protected String updateButtonToolTipText = null;
    protected String helpButtonToolTipText = null;

    // Some of the more generic FileChooser functions
    private Action approveSelectionAction = new ApproveSelectionAction();
    private Action cancelSelectionAction = new CancelSelectionAction();
    private Action updateAction = new UpdateAction();
    private Action newFolderAction = new NewFolderAction();
    private Action goHomeAction = new GoHomeAction();
    private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();

    private JFileChooser filechooser = null;

    private PropertyChangeListener propertyChangeListener = null;
    private AncestorListener ancestorListener = null;

    private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();


    private BasicDirectoryModel model = null;

    private BasicFileView fileView = new BasicFileView();

    // The accessoryPanel is a container to place the JFileChooser accessory component
    private JPanel accessoryPanel = null;


    public BasicFileChooserUI(JFileChooser b) {
    }

    public void installUI(JComponent c) {
	accessoryPanel = new JPanel(new BorderLayout());
	filechooser = (JFileChooser) c;

	createModel();

	installDefaults(filechooser);
	installComponents(filechooser);
	installListeners(filechooser);
    }

    public void uninstallUI(JComponent c) {
	uninstallListeners((JFileChooser) filechooser);
	uninstallComponents((JFileChooser) filechooser);
	uninstallDefaults((JFileChooser) filechooser);

	if(accessoryPanel != null) {
	    accessoryPanel.removeAll();
	}

	accessoryPanel = null;
	getFileChooser().removeAll();
    }

    public void installComponents(JFileChooser fc) {
    }

    public void uninstallComponents(JFileChooser fc) {
    }

    protected void installListeners(JFileChooser fc) {
	propertyChangeListener = createPropertyChangeListener(fc);
	if(propertyChangeListener != null) {
	    fc.addPropertyChangeListener(propertyChangeListener);
	}
	fc.addPropertyChangeListener(model);

	ancestorListener = new AncestorListener() {
	    public void ancestorAdded(AncestorEvent e) {
		JButton approveButton = getApproveButton(getFileChooser());
		if(approveButton != null) {
		    approveButton.requestFocus();
		}
	    }
	    public void ancestorRemoved(AncestorEvent e) {
	    }
	    public void ancestorMoved(AncestorEvent e) {
	    }
	};
	fc.addAncestorListener(ancestorListener);
	

	AbstractAction escAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
		getFileChooser().cancelSelection();
            }
            public boolean isEnabled(){
                return getFileChooser().isEnabled();
            }
        };
        fc.registerKeyboardAction(escAction,
					 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),
					 JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    protected void uninstallListeners(JFileChooser fc) {
	if(propertyChangeListener != null) {
	    fc.removePropertyChangeListener(propertyChangeListener);
	}
	fc.removePropertyChangeListener(model);
	fc.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0));
	fc.removeAncestorListener(ancestorListener);
	ancestorListener = null;
    }


    protected void installDefaults(JFileChooser fc) {
	installIcons(fc);
	installStrings(fc);
    }

    protected void installIcons(JFileChooser fc) {
	directoryIcon    = UIManager.getIcon("FileView.directoryIcon");
	fileIcon         = UIManager.getIcon("FileView.fileIcon");
	computerIcon     = UIManager.getIcon("FileView.computerIcon");
	hardDriveIcon    = UIManager.getIcon("FileView.hardDriveIcon");
	floppyDriveIcon  = UIManager.getIcon("FileView.floppyDriveIcon");

	newFolderIcon    = UIManager.getIcon("FileChooser.newFolderIcon");
	upFolderIcon     = UIManager.getIcon("FileChooser.upFolderIcon");
	homeFolderIcon   = UIManager.getIcon("FileChooser.homeFolderIcon");
	detailsViewIcon  = UIManager.getIcon("FileChooser.detailsViewIcon");
	listViewIcon     = UIManager.getIcon("FileChooser.listViewIcon");
    }

    protected void installStrings(JFileChooser fc) {

	newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText");
	newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator");

	fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText");
	directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText");

	saveButtonText   = UIManager.getString("FileChooser.saveButtonText");
	openButtonText   = UIManager.getString("FileChooser.openButtonText");
	cancelButtonText = UIManager.getString("FileChooser.cancelButtonText");
	updateButtonText = UIManager.getString("FileChooser.updateButtonText");
	helpButtonText   = UIManager.getString("FileChooser.helpButtonText");

	saveButtonMnemonic   = UIManager.getInt("FileChooser.saveButtonMnemonic");
	openButtonMnemonic   = UIManager.getInt("FileChooser.openButtonMnemonic");
	cancelButtonMnemonic = UIManager.getInt("FileChooser.cancelButtonMnemonic");
	updateButtonMnemonic = UIManager.getInt("FileChooser.updateButtonMnemonic");
	helpButtonMnemonic   = UIManager.getInt("FileChooser.helpButtonMnemonic");

	saveButtonToolTipText   = UIManager.getString("FileChooser.saveButtonToolTipText");
	openButtonToolTipText   = UIManager.getString("FileChooser.openButtonToolTipText");
	cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText");
	updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText");
	helpButtonToolTipText   = UIManager.getString("FileChooser.helpButtonToolTipText");
    }

    protected void uninstallDefaults(JFileChooser fc) {
	uninstallIcons(fc);
	uninstallStrings(fc);
    }

    protected void uninstallIcons(JFileChooser fc) {
	directoryIcon    = null;
	fileIcon         = null;
	computerIcon     = null;
	hardDriveIcon    = null;
	floppyDriveIcon  = null;

	newFolderIcon    = null;
	upFolderIcon     = null;
	homeFolderIcon   = null;
	detailsViewIcon  = null;
	listViewIcon     = null;
    }

    protected void uninstallStrings(JFileChooser fc) {
	saveButtonText   = null;
	openButtonText   = null;
	cancelButtonText = null;
	updateButtonText = null;
	helpButtonText   = null;

	saveButtonToolTipText = null;
	openButtonToolTipText = null;
	cancelButtonToolTipText = null;
	updateButtonToolTipText = null;
	helpButtonToolTipText = null;
    }

    protected void createModel() {
	model = new BasicDirectoryModel(getFileChooser());
    }

    public BasicDirectoryModel getModel() {
	return model;
    }

    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
	return null;
    }

    public String getFileName() {
	return null;
    }

    public String getDirectoryName() {
	return null;
    }

    public void setFileName(String filename) {
    }

    public void setDirectoryName(String dirname) {
    }

    public void rescanCurrentDirectory(JFileChooser fc) {
    }

    public void ensureFileIsVisible(JFileChooser fc, File f) {
    }

    public JFileChooser getFileChooser() {
	return filechooser;
    }

    public JPanel getAccessoryPanel() {
	return accessoryPanel;
    }

    protected JButton getApproveButton(JFileChooser fc) {
	return null;
    }

    public String getApproveButtonToolTipText(JFileChooser fc) {
	String tooltipText = fc.getApproveButtonToolTipText();
	if(tooltipText != null) {
	    return tooltipText;
	}

	if(fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openButtonToolTipText;
	} else if(fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveButtonToolTipText;
	}
	return null;
    }

    public void clearIconCache() {
	fileView.clearIconCache();
    }


    // ********************************************
    // ************ Create Listeners **************
    // ********************************************

    public ListSelectionListener createListSelectionListener(JFileChooser fc) {
	return new SelectionListener();
    }

    protected class DoubleClickListener extends MouseAdapter {
	JList list;
	public  DoubleClickListener(JList list) {
	    this.list = list;
	}

	public void mouseClicked(MouseEvent e) {
	    if (e.getClickCount() == 2) {
		int index = list.locationToIndex(e.getPoint());
		if(index >= 0) {
		    File f = (File) list.getModel().getElementAt(index);
		    try {
		      // Strip trailing ".."
		      f = f.getCanonicalFile();
                    } catch (IOException ex) {
		      // That's ok, we'll use f as is
                    }
		    if(getFileChooser().isTraversable(f)) {
                        list.clearSelection();
                        getFileChooser().setCurrentDirectory(f);
                    } else {
		        getFileChooser().approveSelection();
		    }
		}
	    }
	}
    }

    protected MouseListener createDoubleClickListener(JFileChooser fc, JList list) {
	return new DoubleClickListener(list);
    }

    protected class SelectionListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e) {
	    if(!e.getValueIsAdjusting()) {
		JList list = (JList) e.getSource();
		File f = (File) list.getSelectedValue();
		if(f != null && ((getFileChooser().isDirectorySelectionEnabled()) ||
				 !getFileChooser().isTraversable(f))) {
		    getFileChooser().setSelectedFile(f);
		}
	    }
	}
    }


    // *******************************************************
    // ************ FileChooser UI PLAF methods **************
    // *******************************************************

    /**
     * Returns the default accept all file filter
     */
    public FileFilter getAcceptAllFileFilter(JFileChooser fc) {
	return acceptAllFileFilter;
    }


    public FileView getFileView(JFileChooser fc) {
	return fileView;
    }


    /**
     * Returns the title of this dialog
     */
    public String getDialogTitle(JFileChooser fc) {
	return getApproveButtonText(fc);
    }


    public int getApproveButtonMnemonic(JFileChooser fc) {
	if(getFileChooser().getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openButtonMnemonic;
	} else if(getFileChooser().getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveButtonMnemonic;
	} 

	int mnemonic = getFileChooser().getApproveButtonMnemonic();
	return mnemonic;
    }

    public String getApproveButtonText(JFileChooser fc) {
	String buttonText = getFileChooser().getApproveButtonText();
	if(buttonText != null) {
	    return buttonText;
	}

	if(getFileChooser().getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openButtonText;
	} else if(getFileChooser().getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveButtonText;
	}
	return null;
    }


    // *****************************
    // ***** Directory Actions *****
    // *****************************

    public Action getNewFolderAction() {
	return newFolderAction;
    }

    public Action getGoHomeAction() {
	return goHomeAction;
    }

    public Action getChangeToParentDirectoryAction() {
	return changeToParentDirectoryAction;
    }

    public Action getApproveSelectionAction() {
	return approveSelectionAction;
    }

    public Action getCancelSelectionAction() {
	return cancelSelectionAction;
    }

    public Action getUpdateAction() {
	return updateAction;
    }


    /**
     * Creates a new folder.
     */
    protected class NewFolderAction extends AbstractAction {
	protected NewFolderAction() {
	    super("New Folder");
	}
	public void actionPerformed(ActionEvent e) {
	    JFileChooser fc = getFileChooser();
	    File currentDirectory = fc.getCurrentDirectory();
	    File newFolder = null;
	    try {
		newFolder = fc.getFileSystemView().createNewFolder(currentDirectory);
	    } catch (IOException exc) {
		JOptionPane.showMessageDialog(
		    fc,
		    newFolderErrorText + newFolderErrorSeparator + exc,
		    newFolderErrorText, JOptionPane.ERROR_MESSAGE);
		return;
	    } 

	    fc.rescanCurrentDirectory();
	    fc.ensureFileIsVisible(newFolder);
	}
    }

    /**
     * Acts on the "home" key event or equivalent event.
     */
    protected class GoHomeAction extends AbstractAction {
	protected GoHomeAction() {
	    super("Go Home");
	}
	public void actionPerformed(ActionEvent e) {
	    getFileChooser().setCurrentDirectory(null);
	}
    }

    protected class ChangeToParentDirectoryAction extends AbstractAction {
	protected ChangeToParentDirectoryAction() {
	    super("Go Up");
	}
	public void actionPerformed(ActionEvent e) {
	    getFileChooser().changeToParentDirectory();
	}
    }

    /**
     * Responds to an Open or Save request
     */
    protected class ApproveSelectionAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    String filename = getFileName();
	    FileSystemView fs = getFileChooser().getFileSystemView();
	    File dir = getFileChooser().getCurrentDirectory();

	    if(filename != null) {
		// Remove whitespace from beginning and end of filename
		filename = filename.trim();
	    }
	    if(filename != null && !filename.equals("")) {
		// check for directory change action
		File selectedFile = fs.createFileObject(filename);
		if(!selectedFile.isAbsolute()) {
		   selectedFile = fs.createFileObject(dir, filename);
		}
		if(selectedFile.isDirectory() && getFileChooser().isTraversable(selectedFile)
		                              && !getFileChooser().isDirectorySelectionEnabled())
		{
		    getFileChooser().setCurrentDirectory(selectedFile);
		} else if ((!selectedFile.isDirectory() && getFileChooser().isFileSelectionEnabled()) ||
			   (selectedFile.isDirectory() && getFileChooser().isDirectorySelectionEnabled())) {
		    getFileChooser().setSelectedFile(selectedFile);
		    getFileChooser().approveSelection();
		}
		return;
	    }
	    getFileChooser().setSelectedFile(null);
	    getFileChooser().cancelSelection();
	}
    }


    /**
     * Responds to a cancel request.
     */
    protected class CancelSelectionAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    getFileChooser().cancelSelection();
	}
    }

    /**
     * Rescans the files in the current directory
     */
    protected class UpdateAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JFileChooser fc = getFileChooser();
	    fc.setCurrentDirectory(fc.getFileSystemView().createFileObject(getDirectoryName()));
	    fc.rescanCurrentDirectory();
	}
    }


    // *****************************************
    // ***** default AcceptAll file filter *****
    // *****************************************
    protected class AcceptAllFileFilter extends FileFilter {

	public AcceptAllFileFilter() {
	}

	public boolean accept(File f) {
	    return true;
	}

	public String getDescription() {
	    return UIManager.getString("FileChooser.acceptAllFileFilterText");
	}
    }


    // ***********************
    // * FileView operations *
    // ***********************
    protected class BasicFileView extends FileView {
	/* FileView type descriptions */
	// PENDING(jeff) - pass in the icon cache size
	protected Hashtable iconCache = new Hashtable();

	public BasicFileView() {
	}

	public void clearIconCache() {
	    iconCache = new Hashtable();
	}

	public String getName(File f) {
	    String fileName = null;
	    if(f != null) {
		fileName = f.getName();
		if (fileName.equals("")) {
		    fileName = f.getPath();
		}
	    }
	    return fileName;
	}


	public String getDescription(File f) {
	    return f.getName();
	}

	public String getTypeDescription(File f) {
	    if (f.isDirectory()) {
		return directoryDescriptionText;
	    } else {
		return fileDescriptionText;
	    }
	}

	public Icon getCachedIcon(File f) {
	    return (Icon) iconCache.get(f);
	}

	public void cacheIcon(File f, Icon i) {
	    if(f == null || i == null) {
		return;
	    }
	    iconCache.put(f, i);
	}

	public Icon getIcon(File f) {
	    Icon icon = getCachedIcon(f);
	    if(icon != null) {
		return icon;
	    }
	    if (f != null && f.isDirectory()) {
		if(getFileChooser().getFileSystemView().isRoot(f)) {
		    icon = hardDriveIcon;
		} else {
		    icon = directoryIcon;
		}
	    } else {
		icon = fileIcon;
	    }
	    cacheIcon(f, icon);
	    return icon;
	}

	public Boolean isTraversable(File f) {
	    if (f.isDirectory()) {
		return Boolean.TRUE;
	    } else {
		return Boolean.FALSE;
	    }
	}

	public Boolean isHidden(File f) {
	    String name = f.getName();
	    if(name != null && name.charAt(0) == '.') {
		return Boolean.TRUE;
	    } else {
		return Boolean.FALSE;
	    }
	}
    }

}
