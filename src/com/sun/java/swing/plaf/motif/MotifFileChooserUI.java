/*
 * @(#)MotifFileChooserUI.java	1.37 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Motif FileChooserUI.
 *
 * @version 1.37 01/23/03
 * @author Jeff Dinkins
 */
public class MotifFileChooserUI extends BasicFileChooserUI {

    private FilterComboBoxModel filterComboBoxModel;

    protected JList directoryList = null;
    protected JList fileList = null;

    protected JTextField pathField = null;
    protected JComboBox filterComboBox = null;
    protected JTextField filenameTextField = null;

    private static final Dimension hstrut10 = new Dimension(10, 1);
    private static final Dimension vstrut10 = new Dimension(1, 10);

    private static final Insets insets = new Insets(10, 10, 10, 10);

    private static Dimension prefListSize = new Dimension(75, 150);

    private static Dimension WITH_ACCELERATOR_PREF_SIZE = new Dimension(650, 450);
    private static Dimension PREF_SIZE = new Dimension(350, 450);
    private static Dimension MIN_SIZE = new Dimension(200, 300);

    private static Dimension PREF_ACC_SIZE = new Dimension(10, 10);
    private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);

    private static Dimension MAX_SIZE = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);

    private static final Insets buttonMargin = new Insets(3, 3, 3, 3);

    private JPanel directoryPanel = new JPanel();

    protected JButton approveButton;

    private String enterFileNameLabelText = null;
    private int enterFileNameLabelMnemonic = 0;

    private String filesLabelText = null;
    private int filesLabelMnemonic = 0;

    private String foldersLabelText = null;
    private int foldersLabelMnemonic = 0;

    private String pathLabelText = null;
    private int pathLabelMnemonic = 0;

    private String filterLabelText = null;
    private int filterLabelMnemonic = 0;

    public MotifFileChooserUI(JFileChooser filechooser) {
	super(filechooser);
    }

    public String getFileName() {
	if(filenameTextField != null) {
	    return filenameTextField.getText();
	} else {
	    return null;
	}
    }

    public void setFileName(String filename) {
	if(filenameTextField != null) {
	    filenameTextField.setText(filename);
	}
    }

    public String getDirectoryName() {
	return pathField.getText();
    }

    public void setDirectoryName(String dirname) {
	pathField.setText(dirname);
    }

    public void ensureFileIsVisible(JFileChooser fc, File f) {
	// PENDING(jeff)
    }

    public void rescanCurrentDirectory(JFileChooser fc) {
	// PENDING(jeff)
    }

    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
	return new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		if(prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
		    File f = (File) e.getNewValue();
		    if(f != null) {
			setFileName(getFileChooser().getName(f));
		    }
		} else if(prop.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
		    directoryList.clearSelection();
		    fileList.clearSelection();
		    File currentDirectory = getFileChooser().getCurrentDirectory();
		    if(currentDirectory != null) {
			try {
			    setDirectoryName(((File)e.getNewValue()).getCanonicalPath());
			} catch (IOException ioe) {
			    setDirectoryName(((File)e.getNewValue()).getAbsolutePath());
			}
		    }
		} else if(prop.equals(JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY)) {
		    directoryList.clearSelection();
		} else if(prop == JFileChooser.MULTI_SELECTION_ENABLED_CHANGED_PROPERTY) {
		    if(getFileChooser().isMultiSelectionEnabled()) {
			fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		    } else {
			fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fileList.clearSelection();
			getFileChooser().setSelectedFiles(null);
		    }
		} else if(prop == JFileChooser.ACCESSORY_CHANGED_PROPERTY) {
		    if(getAccessoryPanel() != null) {
			if(e.getOldValue() != null) {
			    getAccessoryPanel().remove((JComponent) e.getOldValue());
			}
			JComponent accessory = (JComponent) e.getNewValue();
			if(accessory != null) {
			    getAccessoryPanel().add(accessory, BorderLayout.CENTER);
			    getAccessoryPanel().setPreferredSize(PREF_ACC_SIZE);
			    getAccessoryPanel().setMaximumSize(MAX_SIZE);
			} else {
			    getAccessoryPanel().setPreferredSize(ZERO_ACC_SIZE);
			    getAccessoryPanel().setMaximumSize(ZERO_ACC_SIZE);
			}
		    }
		} else if (prop == JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY ||
			   prop == JFileChooser.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY ||
			   prop == JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY) {
		    approveButton.setText(getApproveButtonText(getFileChooser()));
		    approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
		} else if (prop.equals("componentOrientation")) {
		    ComponentOrientation o = (ComponentOrientation)e.getNewValue();
		    JFileChooser cc = (JFileChooser)e.getSource();
		    if (o != (ComponentOrientation)e.getOldValue()) {
			cc.applyComponentOrientation(o);
		    }
		}
	    }
	};
    }

    //
    // ComponentUI Interface Implementation methods
    //
    public static ComponentUI createUI(JComponent c) {
        return new MotifFileChooserUI((JFileChooser)c);
    }

    public void installUI(JComponent c) {
	super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
	getFileChooser().removeAll();
	super.uninstallUI(c);
    }

    public void installComponents(JFileChooser fc) {
	fc.setLayout(new BorderLayout(10, 10));
	fc.setAlignmentX(JComponent.CENTER_ALIGNMENT);

	JPanel interior = new JPanel() {
	    public Insets getInsets() {
		return insets;
	    }
	};
	align(interior);
	interior.setLayout(new BoxLayout(interior, BoxLayout.PAGE_AXIS));

	fc.add(interior, BorderLayout.CENTER);

	// PENDING(jeff) - I18N
	JLabel l = new JLabel(pathLabelText);
	l.setDisplayedMnemonic(pathLabelMnemonic);
	align(l);
	interior.add(l);

	File currentDirectory = fc.getCurrentDirectory();
	String curDirName = null;
	if(currentDirectory != null) {
	    curDirName = currentDirectory.getPath();
	}
	pathField = new JTextField(curDirName) {
	    public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		d.height = getPreferredSize().height;
		return d;
	    }
	};
	l.setLabelFor(pathField);
	align(pathField);

	// Change to folder on return
	pathField.addActionListener(getUpdateAction());
	interior.add(pathField);

	interior.add(Box.createRigidArea(vstrut10));


	// CENTER: left, right accessory
	JPanel centerPanel = new JPanel();
	centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.LINE_AXIS));
	align(centerPanel);

	// left panel - Filter & folderList
	JPanel leftPanel = new JPanel();
	leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
	align(leftPanel);

	// add the filter PENDING(jeff) - I18N
	l = new JLabel(filterLabelText);
	l.setDisplayedMnemonic(filterLabelMnemonic);
	align(l);
	leftPanel.add(l);

	filterComboBox = new JComboBox() {
	    public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		d.height = getPreferredSize().height;
		return d;
	    }
	};
        l.setLabelFor(filterComboBox);
	filterComboBoxModel = createFilterComboBoxModel();
	filterComboBox.setModel(filterComboBoxModel);
	filterComboBox.setRenderer(createFilterComboBoxRenderer());
	fc.addPropertyChangeListener(filterComboBoxModel);
	align(filterComboBox);
	leftPanel.add(filterComboBox);

	// leftPanel.add(Box.createRigidArea(vstrut10));

	// Add the Folder List PENDING(jeff) - I18N
	l = new JLabel(foldersLabelText);
	l.setDisplayedMnemonic(foldersLabelMnemonic);
	align(l);
	leftPanel.add(l);
	JScrollPane sp = createDirectoryList();
        sp.getVerticalScrollBar().setFocusable(false);
        sp.getHorizontalScrollBar().setFocusable(false);
	l.setLabelFor(sp.getViewport().getView());
	leftPanel.add(sp);


	// create files list
	JPanel rightPanel = new JPanel();
	align(rightPanel);
	rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));

	l = new JLabel(filesLabelText);
	l.setDisplayedMnemonic(filesLabelMnemonic);
	align(l);
	rightPanel.add(l);
	sp = createFilesList();
	l.setLabelFor(sp);
	rightPanel.add(sp);

	centerPanel.add(leftPanel);
	centerPanel.add(Box.createRigidArea(hstrut10));
	centerPanel.add(rightPanel);

	JComponent accessoryPanel = getAccessoryPanel();
	JComponent accessory = fc.getAccessory();
	if(accessoryPanel != null) {
	    if(accessory == null) {
		accessoryPanel.setPreferredSize(ZERO_ACC_SIZE);
		accessoryPanel.setMaximumSize(ZERO_ACC_SIZE);
	    } else {
		getAccessoryPanel().add(accessory, BorderLayout.CENTER);
		accessoryPanel.setPreferredSize(PREF_ACC_SIZE);
		accessoryPanel.setMaximumSize(MAX_SIZE);
	    }
	    align(accessoryPanel);
	    centerPanel.add(accessoryPanel);
	}
	interior.add(centerPanel);
	interior.add(Box.createRigidArea(vstrut10));

	// add the filename field PENDING(jeff) - I18N
	l = new JLabel(enterFileNameLabelText);
	l.setDisplayedMnemonic(enterFileNameLabelMnemonic);
	align(l);
	interior.add(l);

	filenameTextField = new JTextField() {
	    public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		d.height = getPreferredSize().height;
		return d;
	    }
	};
	l.setLabelFor(filenameTextField);
	filenameTextField.addActionListener(getApproveSelectionAction());
	align(filenameTextField);
	filenameTextField.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	interior.add(filenameTextField);

	JPanel bottomPanel = new JPanel(new BorderLayout(0, 4));
	bottomPanel.add(new JSeparator(), BorderLayout.NORTH);

	// Add buttons
	JPanel buttonPanel = new JPanel();
	align(buttonPanel);
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
	buttonPanel.add(Box.createGlue());

	approveButton = new JButton(getApproveButtonText(fc)) {
	    public Dimension getMaximumSize() {
		return new Dimension(MAX_SIZE.width, this.getPreferredSize().height);
	    }
	};
	approveButton.setMnemonic(getApproveButtonMnemonic(fc));
	approveButton.setToolTipText(getApproveButtonToolTipText(fc));
	align(approveButton);
	approveButton.setMargin(buttonMargin);
	approveButton.addActionListener(getApproveSelectionAction());
	buttonPanel.add(approveButton);
	buttonPanel.add(Box.createGlue());

	JButton updateButton = new JButton(updateButtonText) {
	    public Dimension getMaximumSize() {
		return new Dimension(MAX_SIZE.width, this.getPreferredSize().height);
	    }
	};
	updateButton.setMnemonic(updateButtonMnemonic);
	updateButton.setToolTipText(updateButtonToolTipText);
	align(updateButton);
	updateButton.setMargin(buttonMargin);
	updateButton.addActionListener(getUpdateAction());
	buttonPanel.add(updateButton);
	buttonPanel.add(Box.createGlue());

	JButton cancelButton = new JButton(cancelButtonText) {
	    public Dimension getMaximumSize() {
		return new Dimension(MAX_SIZE.width, this.getPreferredSize().height);
	    }
	};
	cancelButton.setMnemonic(cancelButtonMnemonic);
	cancelButton.setToolTipText(cancelButtonToolTipText);
	align(cancelButton);
	cancelButton.setMargin(buttonMargin);
	cancelButton.addActionListener(getCancelSelectionAction());
	buttonPanel.add(cancelButton);
	buttonPanel.add(Box.createGlue());

	JButton helpButton = new JButton(helpButtonText) {
	    public Dimension getMaximumSize() {
		return new Dimension(MAX_SIZE.width, this.getPreferredSize().height);
	    }
	};
	helpButton.setMnemonic(helpButtonMnemonic);
	helpButton.setToolTipText(helpButtonToolTipText);
	align(helpButton);
	helpButton.setMargin(buttonMargin);
	helpButton.setEnabled(false);
	buttonPanel.add(helpButton);
	buttonPanel.add(Box.createGlue());

	bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
	fc.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void uninstallComponents(JFileChooser fc) {
	fc.removeAll();
    }

    protected void installStrings(JFileChooser fc) {
        super.installStrings(fc);

        Locale l = fc.getLocale();

	enterFileNameLabelText = UIManager.getString("FileChooser.enterFileNameLabelText",l);
	enterFileNameLabelMnemonic = UIManager.getInt("FileChooser.enterFileNameLabelMnemonic"); 
	
	filesLabelText = UIManager.getString("FileChooser.filesLabelText",l);
	filesLabelMnemonic = UIManager.getInt("FileChooser.filesLabelMnemonic"); 
	
	foldersLabelText = UIManager.getString("FileChooser.foldersLabelText",l);
	foldersLabelMnemonic = UIManager.getInt("FileChooser.foldersLabelMnemonic"); 
	
	pathLabelText = UIManager.getString("FileChooser.pathLabelText",l);
	pathLabelMnemonic = UIManager.getInt("FileChooser.pathLabelMnemonic"); 
	
	filterLabelText = UIManager.getString("FileChooser.filterLabelText",l);
	filterLabelMnemonic = UIManager.getInt("FileChooser.filterLabelMnemonic");  
    }

    protected void installIcons(JFileChooser fc) {
	// Since motif doesn't have button icons, leave this empty
	// which overrides the supertype icon loading
    }

    protected void uninstallIcons(JFileChooser fc) {
	// Since motif doesn't have button icons, leave this empty
	// which overrides the supertype icon loading
    }

    protected JScrollPane createFilesList() {
	fileList = new JList();

	if(getFileChooser().isMultiSelectionEnabled()) {
	    fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} else {
	    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	fileList.setModel(new MotifFileListModel());
	fileList.setCellRenderer(new FileCellRenderer());
	fileList.addListSelectionListener(createListSelectionListener(getFileChooser()));
	fileList.addMouseListener(createDoubleClickListener(getFileChooser(), fileList));
	align(fileList);
	JScrollPane scrollpane = new JScrollPane(fileList);
	scrollpane.setPreferredSize(prefListSize);
	scrollpane.setMaximumSize(MAX_SIZE);
	align(scrollpane);
	return scrollpane;
    }

    protected JScrollPane createDirectoryList() {
	directoryList = new JList();
	align(directoryList);

	directoryList.setCellRenderer(new DirectoryCellRenderer());
	directoryList.setModel(new MotifDirectoryListModel());
	directoryList.addMouseListener(createDoubleClickListener(getFileChooser(), directoryList));
	directoryList.addListSelectionListener(createListSelectionListener(getFileChooser()));

	JScrollPane scrollpane = new JScrollPane(directoryList);
	scrollpane.setMaximumSize(MAX_SIZE);
	scrollpane.setPreferredSize(prefListSize);
	align(scrollpane);
	return scrollpane;
    }

    public Dimension getPreferredSize(JComponent c) {
	Dimension prefSize =
	    (getFileChooser().getAccessory() != null) ? WITH_ACCELERATOR_PREF_SIZE : PREF_SIZE;
	Dimension d = c.getLayout().preferredLayoutSize(c);
	if (d != null) {
	    return new Dimension(d.width < prefSize.width ? prefSize.width : d.width,
				 d.height < prefSize.height ? prefSize.height : d.height);
	} else {
	    return prefSize;
	}
    }

    public Dimension getMinimumSize(JComponent x)  {
	return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent x) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    protected void align(JComponent c) {
	c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	c.setAlignmentY(JComponent.TOP_ALIGNMENT);
    }

    protected class FileCellRenderer extends DefaultListCellRenderer  {
	public Component getListCellRendererComponent(JList list, Object value, int index,
						      boolean isSelected, boolean cellHasFocus) {

	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    setText(getFileChooser().getName((File) value));
	    return this;
	}
    }

    protected class DirectoryCellRenderer extends DefaultListCellRenderer  {
	public Component getListCellRendererComponent(JList list, Object value, int index,
						      boolean isSelected, boolean cellHasFocus) {

	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    setText(getFileChooser().getName((File) value));
	    return this;
	}
    }

    protected class MotifDirectoryListModel extends AbstractListModel implements ListDataListener {
	public MotifDirectoryListModel() {
	    getModel().addListDataListener(this);
	}

	public int getSize() {
	    return getModel().getDirectories().size();
	}

	public Object getElementAt(int index) {
	    return getModel().getDirectories().elementAt(index);
	}

	public void intervalAdded(ListDataEvent e) {
	}

	// PENDING(jeff) - implement
	public void intervalRemoved(ListDataEvent e) {
	}

	// PENDING(jeff) - this is inefficient - should sent out
	// incremental adjustment values instead of saying that the
	// whole list has changed.
	public void fireContentsChanged() {
	    fireContentsChanged(this, 0, getModel().getDirectories().size()-1);
	}

	// PENDING(jeff) - fire the correct interval changed - currently sending
	// out that everything has changed
	public void contentsChanged(ListDataEvent e) {
	    fireContentsChanged();
	}

    }

    protected class MotifFileListModel extends AbstractListModel implements ListDataListener {
	public MotifFileListModel() {
	    getModel().addListDataListener(this);
	}

	public int getSize() {
	    return getModel().getFiles().size();
	}

	public boolean contains(Object o) {
	    return getModel().getFiles().contains(o);
	}

	public int indexOf(Object o) {
	    return getModel().getFiles().indexOf(o);
	}

	public Object getElementAt(int index) {
	    return getModel().getFiles().elementAt(index);
	}

	public void intervalAdded(ListDataEvent e) {
	}

	// PENDING(jeff) - implement
	public void intervalRemoved(ListDataEvent e) {
	}

	// PENDING(jeff) - this is inefficient - should sent out
	// incremental adjustment values instead of saying that the
	// whole list has changed.
	public void fireContentsChanged() {
	    fireContentsChanged(this, 0, getModel().getFiles().size()-1);
	}

	// PENDING(jeff) - fire the interval changed
	public void contentsChanged(ListDataEvent e) {
	    fireContentsChanged();
	}

    }

    //
    // DataModel for Types Comboxbox
    //
    protected FilterComboBoxModel createFilterComboBoxModel() {
	return new FilterComboBoxModel();
    }

    //
    // Renderer for Types ComboBox
    //
    protected FilterComboBoxRenderer createFilterComboBoxRenderer() {
	return new FilterComboBoxRenderer();
    }


    /**
     * Render different type sizes and styles.
     */
    public class FilterComboBoxRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList list,
	    Object value, int index, boolean isSelected,
	    boolean cellHasFocus) {

	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    if (value != null && value instanceof FileFilter) {
		setText(((FileFilter)value).getDescription());
	    }

	    return this;
	}
    }

    /**
     * Data model for a type-face selection combo-box.
     */
    protected class FilterComboBoxModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener {
	protected FileFilter[] filters;
	protected FilterComboBoxModel() {
	    super();
	    filters = getFileChooser().getChoosableFileFilters();
	}

	public void propertyChange(PropertyChangeEvent e) {
	    String prop = e.getPropertyName();
	    if(prop == JFileChooser.CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY) {
		filters = (FileFilter[]) e.getNewValue();
		fireContentsChanged(this, -1, -1);
	    }
	}

	public void setSelectedItem(Object filter) {
	    if(filter != null) {
		getFileChooser().setFileFilter((FileFilter) filter);
		fireContentsChanged(this, -1, -1);
	    }
	}

	public Object getSelectedItem() {
	    // Ensure that the current filter is in the list.
	    // NOTE: we shouldnt' have to do this, since JFileChooser adds
	    // the filter to the choosable filters list when the filter
	    // is set. Lets be paranoid just in case someone overrides
	    // setFileFilter in JFileChooser.
	    FileFilter currentFilter = getFileChooser().getFileFilter();
	    boolean found = false;
	    if(currentFilter != null) {
		for(int i=0; i < filters.length; i++) {
		    if(filters[i] == currentFilter) {
			found = true;
		    }
		}
		if(found == false) {
		    getFileChooser().addChoosableFileFilter(currentFilter);
		}
	    }
	    return getFileChooser().getFileFilter();
	}

	public int getSize() {
	    if(filters != null) {
		return filters.length;
	    } else {
		return 0;
	    }
	}

	public Object getElementAt(int index) {
	    if(index > getSize() - 1) {
		// This shouldn't happen. Try to recover gracefully.
		return getFileChooser().getFileFilter();
	    }
	    if(filters != null) {
		return filters[index];
	    } else {
		return null;
	    }
	}
    }

    protected JButton getApproveButton(JFileChooser fc) {
	return approveButton;
    }

}
