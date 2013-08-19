/*
 * @(#)SynthFileChooserUI.java	1.7 03/04/09
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicDirectoryModel;

/**
 * Synth FileChooserUI.
 *
 * Note: This class is abstract. It does not actually create the file chooser GUI.
 *
 * @version 1.7, 04/09/03
 * @author Leif Samuelsson
 * @author Jeff Dinkins
 */
abstract class SynthFileChooserUI extends FileChooserUI implements SynthUI {
    private JFileChooser filechooser = null;

    private JButton approveButton, cancelButton;

    private SynthStyle style;
    private SynthFileView fileView = new SynthFileView();
    private BasicDirectoryModel model = null;
    private PropertyChangeListener propertyChangeListener = null;

    private String saveButtonText = null;
    private String openButtonText = null;
    private String cancelButtonText = null;

    private String openDialogTitleText = null;
    private String saveDialogTitleText = null;

    private String saveButtonToolTipText = null;
    private String openButtonToolTipText = null;
    private String cancelButtonToolTipText = null;

    private int saveButtonMnemonic = 0;
    private int openButtonMnemonic = 0;
    private int cancelButtonMnemonic = 0;

    // Some generic FileChooser functions
    private Action approveSelectionAction = new ApproveSelectionAction();
    private Action cancelSelectionAction = new CancelSelectionAction();
    private Action newFolderAction = new NewFolderAction();
    private Action goHomeAction = new GoHomeAction();
    private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();
    private Action fileNameCompletionAction = new FileNameCompletionAction();

    private String newFolderErrorSeparator = null;
    private String newFolderErrorText = null;
    private String fileDescriptionText = null;
    private String directoryDescriptionText = null;

    private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();

    private boolean directorySelected = false;
    private File directory = null;

    private FileFilter actualFileFilter = null;
    private GlobFilter globFilter = null;

    public JFileChooser getFileChooser() {
	return filechooser;
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, SynthLookAndFeel.getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        Region region = SynthLookAndFeel.getRegion(c);
        return SynthContext.getContext(SynthContext.class, c, region,
                                       style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    public FileView getFileView(JFileChooser fc) {
	return fileView;
    }

    public String getApproveButtonToolTipText(JFileChooser fc) {
	String tooltipText = fc.getApproveButtonToolTipText();
	if (tooltipText != null) {
	    return tooltipText;
	}

	if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openButtonToolTipText;
	} else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveButtonToolTipText;
	}
	return null;
    }

    public void installUI(JComponent c) {
	filechooser = (JFileChooser) c;

	createModel();

	installDefaults(filechooser);
	installComponents(filechooser);
	installListeners(filechooser);
	filechooser.applyComponentOrientation(filechooser.getComponentOrientation());
    }

    public void uninstallUI(JComponent c) {
	getFileChooser().removeAll();
	uninstallListeners((JFileChooser) filechooser);
	uninstallComponents((JFileChooser) filechooser);
	uninstallDefaults((JFileChooser) filechooser);
    }

    public void installComponents(JFileChooser fc) {
        SynthContext context = getContext(fc, ENABLED);

	cancelButton = new JButton(cancelButtonText);
	cancelButton.setName("SynthFileChooser.cancelButton");
	cancelButton.setIcon(context.getStyle().getIcon(context, "FileChooser.cancelIcon"));
	cancelButton.setMnemonic(cancelButtonMnemonic);
	cancelButton.setToolTipText(cancelButtonToolTipText);
	cancelButton.addActionListener(getCancelSelectionAction());

	approveButton = new JButton(getApproveButtonText(fc));
	approveButton.setName("SynthFileChooser.approveButton");
	approveButton.setIcon(context.getStyle().getIcon(context, "FileChooser.okIcon"));
	approveButton.setMnemonic(getApproveButtonMnemonic(fc));
	approveButton.setToolTipText(getApproveButtonToolTipText(fc));
	approveButton.addActionListener(getApproveSelectionAction());

    }

    public void uninstallComponents(JFileChooser fc) {
	fc.removeAll();
    }

    protected void installListeners(JFileChooser fc) {
	propertyChangeListener = createPropertyChangeListener(fc);
	if(propertyChangeListener != null) {
	    fc.addPropertyChangeListener(propertyChangeListener);
	}
	fc.addPropertyChangeListener(getModel());

	InputMap inputMap = getInputMap(JComponent.
					WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	SwingUtilities.replaceUIInputMap(fc, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
	ActionMap actionMap = getActionMap();
	SwingUtilities.replaceUIActionMap(fc, actionMap);

	getModel().addListDataListener(new ListDataListener() {
	    public void contentsChanged(ListDataEvent e) {
		// Update the selection after JList has been updated
		new DelayedSelectionUpdater();
	    }
	    public void intervalAdded(ListDataEvent e) {
		new DelayedSelectionUpdater();
	    }
	    public void intervalRemoved(ListDataEvent e) {
	    }
	});

    }

    private class DelayedSelectionUpdater implements Runnable {
	DelayedSelectionUpdater() {
	    SwingUtilities.invokeLater(this);
	}

	public void run() {
	    updateFileNameCompletion();
	}
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    return (InputMap)UIManager.get("FileChooser.ancestorInputMap");
	}
	return null;
    }

    ActionMap getActionMap() {
	return createActionMap();
    }

    ActionMap createActionMap() {
	AbstractAction escAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
		getFileChooser().cancelSelection();
            }
            public boolean isEnabled(){
                return getFileChooser().isEnabled();
            }
        };
	ActionMap map = new ActionMapUIResource();
        map.put("approveSelection", getApproveSelectionAction());
	map.put("cancelSelection", escAction);
        map.put("Go Up", getChangeToParentDirectoryAction());
        map.put("fileNameCompletion", getFileNameCompletionAction());
	return map;
    }


    protected void uninstallListeners(JFileChooser fc) {
	if(propertyChangeListener != null) {
	    fc.removePropertyChangeListener(propertyChangeListener);
	}
	fc.removePropertyChangeListener(getModel());
	SwingUtilities.replaceUIInputMap(fc, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	SwingUtilities.replaceUIActionMap(fc, null);
    }


    protected void installDefaults(JFileChooser fc) {
        fetchStyle(fc);
	installIcons(fc);
	installStrings(fc);
    }

    protected void uninstallDefaults(JFileChooser fc) {
	uninstallIcons(fc);
	uninstallStrings(fc);

        SynthContext context = getContext(filechooser, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void installIcons(JFileChooser fc) {
	// No icons are installed by default
    }

    protected void uninstallIcons(JFileChooser fc) {
	// No icons are installed by default
    }

    protected void installStrings(JFileChooser fc) {
        Locale l = fc.getLocale();
	newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText",l);
	newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator",l);

	fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText",l);
	directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText",l);

	saveButtonText   = UIManager.getString("FileChooser.saveButtonText",l);
	openButtonText   = UIManager.getString("FileChooser.openButtonText",l);
	saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText",l);
	openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText",l);
	cancelButtonText = UIManager.getString("FileChooser.cancelButtonText",l);

	saveButtonMnemonic   = UIManager.getInt("FileChooser.saveButtonMnemonic");
	openButtonMnemonic   = UIManager.getInt("FileChooser.openButtonMnemonic");
	cancelButtonMnemonic = UIManager.getInt("FileChooser.cancelButtonMnemonic");

	saveButtonToolTipText   = UIManager.getString("FileChooser.saveButtonToolTipText",l);
	openButtonToolTipText   = UIManager.getString("FileChooser.openButtonToolTipText",l);
	cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText",l);

    }

    protected void uninstallStrings(JFileChooser fc) {
	saveButtonText   = null;
	openButtonText   = null;
	cancelButtonText = null;

	saveButtonToolTipText = null;
	openButtonToolTipText = null;
	cancelButtonToolTipText = null;
    }

    protected void createModel() {
	model = new BasicDirectoryModel(getFileChooser());
    }

    public BasicDirectoryModel getModel() {
	return model;
    }

    /**
     * Property to remember the directory that is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param f the <code>File</code> object representing the directory that is
     *		currently selected
     * @since 1.4
     */
    protected void setDirectory(File f) {
	directory = f;
    }

    abstract public void setFileName(String fileName);
    abstract public String getFileName();

    protected void doSelectedFileChanged(PropertyChangeEvent e) {
    }

    protected void doDirectoryChanged(PropertyChangeEvent e) {
	File currentDirectory = getFileChooser().getCurrentDirectory();
	if (currentDirectory != null) {
	    getNewFolderAction().setEnabled(currentDirectory.canWrite());
	}
    }

    protected void doAccessoryChanged(PropertyChangeEvent e) {
    }

    protected void doFileSelectionModeChanged(PropertyChangeEvent e) {
    }

    protected void doMultiSelectionChanged(PropertyChangeEvent e) {
	if (!getFileChooser().isMultiSelectionEnabled()) {
	    getFileChooser().setSelectedFiles(null);
	}
    }

    protected void doControlButtonsChanged(PropertyChangeEvent e) {
	if (filechooser.getControlButtonsAreShown()) {
	    approveButton.setText(getApproveButtonText(getFileChooser()));
	    approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
	}
    }

    protected void doAncestorChanged(PropertyChangeEvent e) {
    }

    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
	return new SynthFCPropertyChangeListener();
    }

    protected class SynthFCPropertyChangeListener implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String prop = e.getPropertyName();
	    if (prop.equals(JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY)) {
		doFileSelectionModeChanged(e);
	    } else if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
		doSelectedFileChanged(e);
	    } else if (prop.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
		doDirectoryChanged(e);
	    } else if (prop == JFileChooser.MULTI_SELECTION_ENABLED_CHANGED_PROPERTY) {
		doMultiSelectionChanged(e);
	    } else if (prop == JFileChooser.ACCESSORY_CHANGED_PROPERTY) {
		doAccessoryChanged(e);
	    } else if (prop == JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY ||
		       prop == JFileChooser.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY ||
		       prop == JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY ||
		       prop == JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY) {
		doControlButtonsChanged(e);
	    } else if (prop.equals("componentOrientation")) {
		ComponentOrientation o = (ComponentOrientation)e.getNewValue();
		JFileChooser cc = (JFileChooser)e.getSource();
		if (o != (ComponentOrientation)e.getOldValue()) {
		    cc.applyComponentOrientation(o);
		}
	    } else if (prop.equals("ancestor")) {
		doAncestorChanged(e);
	    }
	}
    }


    /**
     * Property to remember whether a directory is currently selected in the UI.
     *
     * @return <code>true</code> iff a directory is currently selected.
     * @since 1.4
     */
    protected boolean isDirectorySelected() {
	return directorySelected;
    }

    /**
     * Property to remember whether a directory is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param b iff a directory is currently selected.
     * @since 1.4
     */
    protected void setDirectorySelected(boolean b) {
	directorySelected = b;
    }

    /**
     * Property to remember the directory that is currently selected in the UI.
     *
     * @return the value of the <code>directory</code> property
     * @see #setDirectory
     * @since 1.4
     */
    protected File getDirectory() {
	return directory;
    }


    /**
     * Responds to an Open or Save request
     */
    protected class ApproveSelectionAction extends AbstractAction {
	protected ApproveSelectionAction() {
	    super("approveSelection");
	}
	public void actionPerformed(ActionEvent e) {
	    if (isDirectorySelected()) {
		File dir = getDirectory();
		if (dir != null) {
		    try {
			// Strip trailing ".."
			dir = dir.getCanonicalFile();
		    } catch (IOException ex) {
			// Ok, use f as is
		    }
		    getFileChooser().setCurrentDirectory(dir);
		    return;
		}
	    }

	    JFileChooser chooser = getFileChooser();

	    String fileName = getFileName();
	    FileSystemView fs = chooser.getFileSystemView();
	    File dir = chooser.getCurrentDirectory();

	    if (fileName != null) {
		// Remove whitespace from beginning and end of filename
		fileName = fileName.trim();
	    }

	    if (fileName == null || fileName.equals("")) {
		// no file selected, multiple selection off, therefore cancel the approve action
		resetGlobFilter();
		return;
	    }

	    File selectedFile = null;
	    File[] selectedFiles = null;

	    if (fileName != null && !fileName.equals("")) {
		// Unix: Resolve '~' to user's home directory
		if (File.separatorChar == '/') {
		    if (fileName.startsWith("~/")) {
			fileName = System.getProperty("user.home") + fileName.substring(1);
		    } else if (fileName.equals("~")) {
			fileName = System.getProperty("user.home");
		    }
		}

		if (chooser.isMultiSelectionEnabled() && fileName.startsWith("\"")) {
		    ArrayList fList = new ArrayList();

		    fileName = fileName.substring(1);
		    if (fileName.endsWith("\"")) {
			fileName = fileName.substring(0, fileName.length()-1);
		    }
		    File[] children = null;
		    int childIndex = 0;
		    do {
			String str;
			int i = fileName.indexOf("\" \"");
			if (i > 0) {
			    str = fileName.substring(0, i);
			    fileName = fileName.substring(i+3);
			} else {
			    str = fileName;
			    fileName = "";
			}
			File file = fs.createFileObject(str);
			if (!file.isAbsolute()) {
			    if (children == null) {
				children = fs.getFiles(dir, false);
				Arrays.sort(children);
			    }
			    for (int k = 0; k < children.length; k++) {
				int l = (childIndex + k) % children.length;
				if (children[l].getName().equals(str)) {
				    file = children[l];
				    childIndex = l + 1;
				    break;
				}
			    }
			}
			fList.add(file);
		    } while (fileName.length() > 0);
		    if (fList.size() > 0) {
			selectedFiles = (File[])fList.toArray(new File[fList.size()]);
		    }
		    resetGlobFilter();
		} else {
		    selectedFile = fs.createFileObject(fileName);
		    if(!selectedFile.isAbsolute()) {
		       selectedFile = fs.getChild(dir, fileName);
		    }
		    // check for wildcard pattern
		    FileFilter currentFilter = chooser.getFileFilter();
		    if (!selectedFile.exists() && isGlobPattern(fileName)) {
			if (globFilter == null) {
			    globFilter = new GlobFilter();
			}
			globFilter.setPattern(fileName);
			if (!(currentFilter instanceof GlobFilter)) {
			    actualFileFilter = currentFilter;
			}
			chooser.setFileFilter(null);
			chooser.setFileFilter(globFilter);
			return;
		    }

		    resetGlobFilter();

		    // Check for directory change action
		    boolean isDir = (selectedFile != null && selectedFile.isDirectory());
		    boolean isTrav = (selectedFile != null && chooser.isTraversable(selectedFile));
		    boolean isDirSelEnabled = chooser.isDirectorySelectionEnabled();
		    boolean isFileSelEnabled = chooser.isFileSelectionEnabled();

		    if (isDir && isTrav && !isDirSelEnabled) {
			chooser.setCurrentDirectory(selectedFile);
			return;
		    } else if ((isDir || !isFileSelEnabled)
			       && (!isDir || !isDirSelEnabled)
			       && (!isDirSelEnabled || selectedFile.exists())) {
			selectedFile = null;
		    }
		}
	    }
	    if (selectedFiles != null || selectedFile != null) {
		if (selectedFiles != null) {
		    chooser.setSelectedFiles(selectedFiles);
		} else if (chooser.isMultiSelectionEnabled()) {
		    chooser.setSelectedFiles(new File[] { selectedFile });
		} else {
		    chooser.setSelectedFile(selectedFile);
		}
		chooser.approveSelection();
	    } else {
		if (chooser.isMultiSelectionEnabled()) { 
		    chooser.setSelectedFiles(null);
		} else {
		    chooser.setSelectedFile(null);
		}
		chooser.cancelSelection();
	    }
	}
    }

    /**
     * Responds to a File Name completion request (e.g. Tab)
     */
    protected class FileNameCompletionAction extends AbstractAction {
	protected FileNameCompletionAction() {
	    super("fileNameCompletion");
	}

	public void actionPerformed(ActionEvent e) {
	    JFileChooser chooser = getFileChooser();

	    String fileName = getFileName();

	    if (fileName != null) {
		// Remove whitespace from beginning and end of filename
		fileName = fileName.trim();
	    }

	    resetGlobFilter();

	    if (fileName == null || fileName.equals("") ||
		(chooser.isMultiSelectionEnabled() && fileName.startsWith("\""))) {
		return;
	    }

	    FileFilter currentFilter = chooser.getFileFilter();
	    if (globFilter == null) {
		globFilter = new GlobFilter();
	    }
	    globFilter.setPattern(fileName + "*");
	    if (!(currentFilter instanceof GlobFilter)) {
		actualFileFilter = currentFilter;
	    }
	    chooser.setFileFilter(null);
	    chooser.setFileFilter(globFilter);
	    fileNameCompletionString = fileName;
	}
    }

    private String fileNameCompletionString;

    private void updateFileNameCompletion() {
	if (fileNameCompletionString != null) {
	    if (fileNameCompletionString.equals(getFileName())) {
		File[] files = (File[])getModel().getFiles().toArray(new File[0]);
		String str = getCommonStartString(files);
		if (str != null && str.startsWith(fileNameCompletionString)) {
		    setFileName(str);
		}
		fileNameCompletionString = null;
	    }
	}
    }

    private String getCommonStartString(File[] files) {
	String str = null;
	String str2 = null;
	int i = 0;
    if (files.length == 0) {
        return null;
    }
	while (true) {
	    for (int f = 0; f < files.length; f++) {
		String name = files[f].getName();
		if (f == 0) {
		    if (name.length() == i) {
			return str;
		    }
		    str2 = name.substring(0, i+1);
		}
		if (!name.startsWith(str2)) {
		    return str;
		}
	    }
	    str = str2;
	    i++;
	}
    }

    private void resetGlobFilter() {
	if (actualFileFilter != null) {
	    JFileChooser chooser = getFileChooser();
	    FileFilter currentFilter = chooser.getFileFilter();
	    if (currentFilter != null && currentFilter.equals(globFilter)) {
		chooser.setFileFilter(actualFileFilter);
		chooser.removeChoosableFileFilter(globFilter);
	    }
	    actualFileFilter = null;
	}
    }

    private static boolean isGlobPattern(String fileName) {
	return ((File.separatorChar == '\\' && fileName.indexOf('*') >= 0)
		|| (File.separatorChar == '/' && (fileName.indexOf('*') >= 0
						  || fileName.indexOf('?') >= 0
						  || fileName.indexOf('[') >= 0)));
    }

    
    /* A file filter which accepts file patterns containing
     * the special wildcard '*' on windows, plus '?', and '[ ]' on Unix.
     */
    class GlobFilter extends FileFilter {
	Pattern pattern;
	String globPattern;

	public void setPattern(String globPattern) {
	    char[] gPat = globPattern.toCharArray();
	    char[] rPat = new char[gPat.length * 2];
	    boolean isWin32 = (File.separatorChar == '\\');
	    boolean inBrackets = false;
	    StringBuffer buf = new StringBuffer();
	    int j = 0;

	    this.globPattern = globPattern;

	    if (isWin32) {
		// On windows, a pattern ending with *.* is equal to ending with *
		int len = gPat.length;
		if (globPattern.endsWith("*.*")) {
		    len -= 2;
		}
		for (int i = 0; i < len; i++) {
		    if (gPat[i] == '*') {
			rPat[j++] = '.';
		    }
		    rPat[j++] = gPat[i];
		}
	    } else {
		for (int i = 0; i < gPat.length; i++) {
		    switch(gPat[i]) {
		      case '*':
			if (!inBrackets) {
			    rPat[j++] = '.';
			}
			rPat[j++] = '*';
			break;

		      case '?':
			rPat[j++] = inBrackets ? '?' : '.';
			break;

		      case '[':
			inBrackets = true;
			rPat[j++] = gPat[i];

			if (i < gPat.length - 1) {
			    switch (gPat[i+1]) {
			      case '!':
			      case '^':
				rPat[j++] = '^';
				i++;
				break;

			      case ']':
				rPat[j++] = gPat[++i];
				break;
			    }
			}
			break;

		      case ']':
			rPat[j++] = gPat[i];
			inBrackets = false;
			break;

		      case '\\':
			if (i == 0 && gPat.length > 1 && gPat[1] == '~') {
			    rPat[j++] = gPat[++i];
			} else {
			    rPat[j++] = '\\';
			    if (i < gPat.length - 1 && "*?[]".indexOf(gPat[i+1]) >= 0) {
				rPat[j++] = gPat[++i];
			    } else {
				rPat[j++] = '\\';
			    }
			}
			break;

		      default:
			//if ("+()|^$.{}<>".indexOf(gPat[i]) >= 0) {
			if (!Character.isLetterOrDigit(gPat[i])) {
			    rPat[j++] = '\\';
			}
			rPat[j++] = gPat[i];
			break;
		    }
		}
	    }
	    this.pattern = Pattern.compile(new String(rPat, 0, j), Pattern.CASE_INSENSITIVE);
	}

	public boolean accept(File f) {
	    if (f == null) {
		return false;
	    }
	    if (f.isDirectory()) {
		return true;
	    }
	    return pattern.matcher(f.getName()).matches();
	}

	public String getDescription() {
	    return globPattern;
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


    /**
     * Returns the title of this dialog
     */
    public String getDialogTitle(JFileChooser fc) {
	String dialogTitle = fc.getDialogTitle();
	if (dialogTitle != null) {
	    return dialogTitle;
	} else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openDialogTitleText;
	} else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveDialogTitleText;
	} else {
	    return getApproveButtonText(fc);
	}
    }


    public int getApproveButtonMnemonic(JFileChooser fc) {
	int mnemonic = fc.getApproveButtonMnemonic();
	if (mnemonic > 0) {
	    return mnemonic;
	} else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openButtonMnemonic;
	} else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveButtonMnemonic;
	} else {
	    return mnemonic;
	}
    }

    public String getApproveButtonText(JFileChooser fc) {
	String buttonText = fc.getApproveButtonText();
	if (buttonText != null) {
	    return buttonText;
	} else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
	    return openButtonText;
	} else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
	    return saveButtonText;
	} else {
	    return null;
	}
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

    /**
     * Acts on the "home" key event or equivalent event.
     */
    protected class GoHomeAction extends AbstractAction {
	protected GoHomeAction() {
	    super("Go Home");
	}
	public void actionPerformed(ActionEvent e) {
	    JFileChooser fc = getFileChooser();
	    fc.setCurrentDirectory(fc.getFileSystemView().getHomeDirectory());
	}
    }

    public Action getFileNameCompletionAction() {
	return fileNameCompletionAction;
    }

    protected class ChangeToParentDirectoryAction extends AbstractAction {
	protected ChangeToParentDirectoryAction() {
	    super("Go Up");
	}
	public void actionPerformed(ActionEvent e) {
	    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	    if (focusOwner == null || !(focusOwner instanceof javax.swing.text.JTextComponent)) {
		getFileChooser().changeToParentDirectory();
	    }
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
	    return UIManager.getString("FileChooser.acceptAllFileFilterText",
                                       getFileChooser().getLocale());
	}
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
		if (fc.isMultiSelectionEnabled()) {
		    fc.setSelectedFiles(new File[] { newFolder });
		} else {
		    fc.setSelectedFile(newFolder);
		}
	    } catch (IOException exc) {
		JOptionPane.showMessageDialog(
		    fc,
		    newFolderErrorText + newFolderErrorSeparator + exc,
		    newFolderErrorText, JOptionPane.ERROR_MESSAGE);
		return;
	    } 

	    fc.rescanCurrentDirectory();
	}
    }

    protected JButton getApproveButton(JFileChooser fc) {
	return approveButton;
    }

    protected JButton getCancelButton(JFileChooser fc) {
	return cancelButton;
    }



    // ***********************
    // * FileView operations *
    // ***********************
    protected class SynthFileView extends FileView {

	public String getName(File f) {
	    // Note: Returns display name rather than file name
	    String fileName = null;
	    if(f != null) {
		fileName = getFileChooser().getFileSystemView().getSystemDisplayName(f);
	    }
	    return fileName;
	}


	public String getDescription(File f) {
	    return f.getName();
	}

	public String getTypeDescription(File f) {
	    String type = getFileChooser().getFileSystemView().getSystemTypeDescription(f);
	    if (type == null) {
		if (f.isDirectory()) {
		    type = directoryDescriptionText;
		} else {
		    type = fileDescriptionText;
		}
	    }
	    return type;
	}

	public Icon getIcon(File f) {
	    return null;
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
