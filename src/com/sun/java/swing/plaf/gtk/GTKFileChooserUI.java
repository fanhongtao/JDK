/*
 * @(#)GTKFileChooserUI.java	1.10 03/05/05
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.table.*;

/**
 * GTK FileChooserUI.
 *
 * @version 1.36 08/21/02
 * @author Leif Samuelsson
 * @author Jeff Dinkins
 */
class GTKFileChooserUI extends SynthFileChooserUI {

    // The accessoryPanel is a container to place the JFileChooser accessory component
    private JPanel accessoryPanel = null;

    private String newFolderButtonText = null;
    private String deleteFileButtonText = null;
    private String renameFileButtonText = null;

    private String newFolderButtonToolTipText = null;
    private String deleteFileButtonToolTipText = null;
    private String renameFileButtonToolTipText = null;

    private int newFolderButtonMnemonic = 0;
    private int deleteFileButtonMnemonic = 0;
    private int renameFileButtonMnemonic = 0;



    // From Motif

    private JPanel rightPanel;
    private JList directoryList;
    private JList fileList;

    private JLabel pathField;
    private JTextField fileNameTextField;

    private static final Dimension hstrut10 = new Dimension(10, 1);
    private static final Dimension vstrut10 = new Dimension(1, 10);

    private static final Insets insets = new Insets(10, 10, 10, 10);

    private static Dimension prefListSize = new Dimension(75, 150);

    private static Dimension PREF_SIZE = new Dimension(435, 360);
    private static Dimension MIN_SIZE = new Dimension(200, 300);

    private static Dimension PREF_ACC_SIZE = new Dimension(10, 10);
    private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);

    private static Dimension MAX_SIZE = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);

    private static final Insets buttonMargin = new Insets(3, 3, 3, 3);

    private String filesLabelText = null;

    private String foldersLabelText = null;

    private String pathLabelText = null;
    private int pathLabelMnemonic = 0;

    private JComboBox directoryComboBox;
    private DirectoryComboBoxModel directoryComboBoxModel;
    private Action directoryComboBoxAction = new DirectoryComboBoxAction();
    private JPanel bottomButtonPanel;


    public String getFileName() {
	if (fileNameTextField != null) {
	    return fileNameTextField.getText();
	} else {
	    return null;
	}
    }

    public void setFileName(String fileName) {
	if (fileNameTextField != null) {
	    fileNameTextField.setText(fileName);
	}
    }

//     public String getDirectoryName() {
// 	return pathField.getText();
//     }

    public void setDirectoryName(String dirname) {
	pathField.setText(dirname);
    }

    public void ensureFileIsVisible(JFileChooser fc, File f) {
	// PENDING
    }

    public void rescanCurrentDirectory(JFileChooser fc) {
	// PENDING
    }

    protected JPanel getAccessoryPanel() {
	return accessoryPanel;
    }

    private void updateDefaultButton() {
	JFileChooser filechooser = getFileChooser();
	JRootPane root = SwingUtilities.getRootPane(filechooser);
	if (root == null) {
	    return;
	}

	if (filechooser.getControlButtonsAreShown()) {
	    if (root.getDefaultButton() == null) {
		root.setDefaultButton(getApproveButton(filechooser));
		getCancelButton(filechooser).setDefaultCapable(false);
	    }
	} else {
	    if (root.getDefaultButton() == getApproveButton(filechooser)) {
		root.setDefaultButton(null);
	    }
	}
    }

    protected void doSelectedFileChanged(PropertyChangeEvent e) {
	super.doSelectedFileChanged(e);
	File f = (File) e.getNewValue();
	if (f != null) {
	    setFileName(getFileChooser().getName(f));
	}
    }

    protected void doDirectoryChanged(PropertyChangeEvent e) {
	directoryList.clearSelection();
	fileList.clearSelection();

	File currentDirectory = getFileChooser().getCurrentDirectory();
	if (currentDirectory != null) {
	    try {
		setDirectoryName(((File)e.getNewValue()).getCanonicalPath());
	    } catch (IOException ioe) {
		setDirectoryName(((File)e.getNewValue()).getAbsolutePath());
	    }
	    directoryComboBoxModel.addItem(currentDirectory);
	}
	super.doDirectoryChanged(e);
    }

    protected void doAccessoryChanged(PropertyChangeEvent e) {
	if (getAccessoryPanel() != null) {
	    if (e.getOldValue() != null) {
		getAccessoryPanel().remove((JComponent)e.getOldValue());
	    }
	    JComponent accessory = (JComponent)e.getNewValue();
	    if (accessory != null) {
		getAccessoryPanel().add(accessory, BorderLayout.CENTER);
		getAccessoryPanel().setPreferredSize(accessory.getPreferredSize());
		getAccessoryPanel().setMaximumSize(MAX_SIZE);
	    } else {
		getAccessoryPanel().setPreferredSize(ZERO_ACC_SIZE);
		getAccessoryPanel().setMaximumSize(ZERO_ACC_SIZE);
	    }
	}
    }

    protected void doFileSelectionModeChanged(PropertyChangeEvent e) {
	directoryList.clearSelection();
	rightPanel.setVisible(((Integer)e.getNewValue()).intValue() != JFileChooser.DIRECTORIES_ONLY);

	super.doFileSelectionModeChanged(e);
    }

    protected void doMultiSelectionChanged(PropertyChangeEvent e) {
	if (getFileChooser().isMultiSelectionEnabled()) {
	    fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} else {
	    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    fileList.clearSelection();
	}

	super.doMultiSelectionChanged(e);
    }

    protected void doControlButtonsChanged(PropertyChangeEvent e) {
	super.doControlButtonsChanged(e);

	JFileChooser filechooser = getFileChooser();
	if (filechooser.getControlButtonsAreShown()) {
	    filechooser.add(bottomButtonPanel, BorderLayout.SOUTH);
	} else {
	    filechooser.remove(bottomButtonPanel);
	}
	updateDefaultButton();
    }

    protected void doAncestorChanged(PropertyChangeEvent e) {
	if (e.getOldValue() == null && e.getNewValue() != null) {
	    // Ancestor was added, set initial focus
	    fileNameTextField.selectAll();
	    fileNameTextField.requestFocus();
	    updateDefaultButton();
	}

	super.doAncestorChanged(e);
    }



    // ********************************************
    // ************ Create Listeners **************
    // ********************************************

    public ListSelectionListener createListSelectionListener(JFileChooser fc) {
	return new SelectionListener();
    }

    class DoubleClickListener extends MouseAdapter {
	JList list;
	public  DoubleClickListener(JList list) {
	    this.list = list;
	}

	public void mouseClicked(MouseEvent e) {
	    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
		int index = list.locationToIndex(e.getPoint());
		if (index >= 0) {
		    File f = (File) list.getModel().getElementAt(index);
		    try {
			// Strip trailing ".."
			f = f.getCanonicalFile();
		    } catch (IOException ex) {
			// That's ok, we'll use f as is
		    }
		    if (getFileChooser().isTraversable(f)) {
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
	    if (!e.getValueIsAdjusting()) {
		JFileChooser chooser = getFileChooser();
		JList list = (JList) e.getSource();

		if (chooser.isMultiSelectionEnabled()) {
		    File[] files = null;
		    Object[] objects = list.getSelectedValues();
		    if (objects != null) {
			if (objects.length == 1
			    && ((File)objects[0]).isDirectory()
			    && chooser.isTraversable(((File)objects[0]))
			    && (chooser.getFileSelectionMode() != chooser.DIRECTORIES_ONLY
				|| !chooser.getFileSystemView().isFileSystem(((File)objects[0])))) {
			    setDirectorySelected(true);
			    setDirectory(((File)objects[0]));
			} else {
			    ArrayList fList = new ArrayList(objects.length);
			    for (int i = 0; i < objects.length; i++) {
				File f = (File)objects[i];
				if ((chooser.isFileSelectionEnabled() && f.isFile())
				    || (chooser.isDirectorySelectionEnabled() && f.isDirectory())) {
				    fList.add(f);
				}
			    }
			    if (fList.size() > 0) {
				files = (File[])fList.toArray(new File[fList.size()]);
			    }
			    setDirectorySelected(false);
			}
		    }
		    chooser.setSelectedFiles(files);
		} else {
		    File file = (File)list.getSelectedValue();
		    if (file != null
			&& file.isDirectory()
			&& chooser.isTraversable(file)
			&& (chooser.getFileSelectionMode() != chooser.DIRECTORIES_ONLY
			    || !chooser.getFileSystemView().isFileSystem(file))) {

			setDirectorySelected(true);
			setDirectory(file);
		    } else {
			setDirectorySelected(false);
			if (file != null) {
			    chooser.setSelectedFile(file);
			}
		    }
		}
	    }
	}
    }


    //
    // ComponentUI Interface Implementation methods
    //
    public static ComponentUI createUI(JComponent c) {
        return new GTKFileChooserUI();
    }

    public void installUI(JComponent c) {
	accessoryPanel = new JPanel(new BorderLayout(10, 10));
	accessoryPanel.setName("GTKFileChooser.accessoryPanel");

	super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
	super.uninstallUI(c);

	if (accessoryPanel != null) {
	    accessoryPanel.removeAll();
	}
	accessoryPanel = null;
	getFileChooser().removeAll();
    }

    public void installComponents(JFileChooser fc) {
	super.installComponents(fc);

	boolean leftToRight = fc.getComponentOrientation().isLeftToRight();

	fc.setLayout(new BorderLayout());
	fc.setAlignmentX(JComponent.CENTER_ALIGNMENT);

	// Top row of buttons
	JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        topButtonPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
	topButtonPanel.setName("GTKFileChooser.topButtonPanel");

	JButton newFolderButton = new JButton(getNewFolderAction());
	newFolderButton.setName("GTKFileChooser.newFolderButton");
	newFolderButton.setMnemonic(newFolderButtonMnemonic);
	newFolderButton.setToolTipText(newFolderButtonToolTipText);
	newFolderButton.setText(newFolderButtonText);
	topButtonPanel.add(newFolderButton);

	JButton deleteFileButton = new JButton(deleteFileButtonText);
	deleteFileButton.setName("GTKFileChooser.deleteFileButton");
	deleteFileButton.setMnemonic(deleteFileButtonMnemonic);
	deleteFileButton.setToolTipText(deleteFileButtonToolTipText);
	deleteFileButton.setEnabled(false);
	topButtonPanel.add(deleteFileButton);

	JButton renameFileButton = new JButton(renameFileButtonText);
	renameFileButton.setName("GTKFileChooser.renameFileButton");
	renameFileButton.setMnemonic(renameFileButtonMnemonic);
	renameFileButton.setToolTipText(renameFileButtonToolTipText);
	topButtonPanel.add(renameFileButton);

	fc.add(topButtonPanel, BorderLayout.NORTH);


	JPanel interior = new JPanel();
        interior.setBorder(new EmptyBorder(0, 10, 10, 10));
	interior.setName("GTKFileChooser.interiorPanel");
	align(interior);
	interior.setLayout(new BoxLayout(interior, BoxLayout.PAGE_AXIS));

	fc.add(interior, BorderLayout.CENTER);

	JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
                                                         0, 0) {
            public void layoutContainer(Container target) {
                super.layoutContainer(target);
                JComboBox comboBox = directoryComboBox;
                if (comboBox.getWidth() > target.getWidth()) {
                    comboBox.setBounds(0, comboBox.getY(), target.getWidth(),
                                       comboBox.getHeight());
                }
            }
        });
        comboBoxPanel.setBorder(new EmptyBorder(0, 0, 4, 0));
	comboBoxPanel.setName("GTKFileChooser.directoryComboBoxPanel");
	// CurrentDir ComboBox
	directoryComboBoxModel = createDirectoryComboBoxModel(fc);
	directoryComboBox = new JComboBox(directoryComboBoxModel);
	directoryComboBox.setName("GTKFileChooser.directoryComboBox");
	directoryComboBox.putClientProperty( "JComboBox.lightweightKeyboardNavigation", "Lightweight" );
	directoryComboBox.addActionListener(directoryComboBoxAction);
	directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
	directoryComboBox.setMaximumRowCount(8);
        comboBoxPanel.add(directoryComboBox);
	interior.add(comboBoxPanel);


	// interior.add(Box.createRigidArea(vstrut10));


	// CENTER: left, right, accessory
	JPanel centerPanel = new JPanel(new BorderLayout());
	centerPanel.setName("GTKFileChooser.centerPanel");

	// SPLIT PANEL: left, right
	JSplitPane splitPanel = new JSplitPane();
	splitPanel.setName("GTKFileChooser.splitPanel");
	splitPanel.setDividerLocation((PREF_SIZE.width-8)/2);

	// left panel - Filter & directoryList
	JPanel leftPanel = new JPanel(new GridBagLayout());
	leftPanel.setName("GTKFileChooser.directoryListPanel");

	// Add the Directory List
	// Create a label that looks like button (should be a table header)
	TableCellRenderer headerRenderer = new JTableHeader().getDefaultRenderer();
	JComponent directoryListLabel =
	    (JComponent)headerRenderer.getTableCellRendererComponent(null, foldersLabelText,
								     false, false, 0, 0);
	directoryListLabel.setName("GTKFileChooser.directoryListLabel");
	leftPanel.add(directoryListLabel, new GridBagConstraints(
                          0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                          GridBagConstraints.HORIZONTAL,
                          SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS, 0, 0));
	leftPanel.add(createDirectoryList(), new GridBagConstraints(
                          0, 1, 1, 1, 1, 1, GridBagConstraints.EAST,
                          GridBagConstraints.BOTH,
                          SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS, 0, 0));


	// create files list
	rightPanel = new JPanel(new GridBagLayout());
	rightPanel.setName("GTKFileChooser.fileListPanel");

	headerRenderer = new JTableHeader().getDefaultRenderer();
	JComponent fileListLabel =
	    (JComponent)headerRenderer.getTableCellRendererComponent(null, filesLabelText,
								     false, false, 0, 0);
	fileListLabel.setName("GTKFileChooser.fileListLabel");
	rightPanel.add(fileListLabel, new GridBagConstraints(
                          0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                          GridBagConstraints.HORIZONTAL,
                          SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS,
                          0, 0));
	rightPanel.add(createFilesList(), new GridBagConstraints(
                          0, 1, 1, 1, 1, 1, GridBagConstraints.EAST,
                          GridBagConstraints.BOTH,
                          SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS, 0, 0));

	splitPanel.add(leftPanel,  leftToRight ? JSplitPane.LEFT : JSplitPane.RIGHT);
	splitPanel.add(rightPanel, leftToRight ? JSplitPane.RIGHT : JSplitPane.LEFT);
	centerPanel.add(splitPanel, BorderLayout.CENTER);

	JComponent accessoryPanel = getAccessoryPanel();
	JComponent accessory = fc.getAccessory();
	if (accessoryPanel != null) {
	    if (accessory == null) {
		accessoryPanel.setPreferredSize(ZERO_ACC_SIZE);
		accessoryPanel.setMaximumSize(ZERO_ACC_SIZE);
	    } else {
		getAccessoryPanel().add(accessory, BorderLayout.CENTER);
		accessoryPanel.setPreferredSize(accessory.getPreferredSize());
		accessoryPanel.setMaximumSize(MAX_SIZE);
	    }
	    align(accessoryPanel);
	    centerPanel.add(accessoryPanel, BorderLayout.AFTER_LINE_ENDS);
	}
	interior.add(centerPanel);
	interior.add(Box.createRigidArea(vstrut10));

	JPanel pathFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEADING,
                                                          0, 0));
        pathFieldPanel.setBorder(new EmptyBorder(0, 0, 4, 0));
	JLabel pathFieldLabel = new JLabel(pathLabelText);
	pathFieldLabel.setName("GTKFileChooser.pathFieldLabel");
	pathFieldLabel.setDisplayedMnemonic(pathLabelMnemonic);
	align(pathFieldLabel);
	pathFieldPanel.add(pathFieldLabel);

	File currentDirectory = fc.getCurrentDirectory();
	String curDirName = null;
	if (currentDirectory != null) {
	    curDirName = currentDirectory.getPath();
	}
	pathField = new JLabel(curDirName) {
	    public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		d.height = getPreferredSize().height;
		return d;
	    }
	};
	pathField.setName("GTKFileChooser.pathField");
	pathFieldLabel.setLabelFor(pathField);
	align(pathField);
	pathFieldPanel.add(pathField);
	interior.add(pathFieldPanel);

	// add the fileName field
	fileNameTextField = new JTextField() {
	    public boolean isManagingFocus() {
		return true;
	    }

	    public Dimension getMaximumSize() {
		Dimension d = super.getMaximumSize();
		d.height = getPreferredSize().height;
		return d;
	    }
	};
	fileNameTextField.setName("GTKFileChooser.fileNameTextField");
	fileNameTextField.getActionMap().put("fileNameCompletionAction", getFileNameCompletionAction());
	fileNameTextField.getInputMap().put(KeyStroke.getKeyStroke('\t'), "fileNameCompletionAction");
	interior.add(fileNameTextField);

	// Add buttons
	bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	bottomButtonPanel.setName("GTKFileChooser.bottomButtonPanel");
	align(bottomButtonPanel);

	JButton cancelButton = getCancelButton(fc);
	align(cancelButton);
	cancelButton.setMargin(buttonMargin);
	bottomButtonPanel.add(cancelButton);

	JButton approveButton = getApproveButton(fc);;
	align(approveButton);
	approveButton.setMargin(buttonMargin);
	bottomButtonPanel.add(approveButton);

	fc.add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    protected void installStrings(JFileChooser fc) {
	super.installStrings(fc);

        Locale l = fc.getLocale();

	newFolderButtonText = UIManager.getString("FileChooser.newFolderButtonText", l);
	deleteFileButtonText = UIManager.getString("FileChooser.deleteFileButtonText", l);
	renameFileButtonText = UIManager.getString("FileChooser.renameFileButtonText", l);

	newFolderButtonMnemonic = UIManager.getInt("FileChooser.newFolderButtonMnemonic", l);
	deleteFileButtonMnemonic = UIManager.getInt("FileChooser.deleteFileButtonMnemonic", l);
	renameFileButtonMnemonic = UIManager.getInt("FileChooser.renameFileButtonMnemonic", l);

	newFolderButtonToolTipText = UIManager.getString("FileChooser.newFolderButtonToolTipText", l);
	deleteFileButtonToolTipText = UIManager.getString("FileChooser.deleteFileButtonToolTipText", l);
	renameFileButtonToolTipText = UIManager.getString("FileChooser.renameFileButtonToolTipText", l);

	foldersLabelText = UIManager.getString("FileChooser.foldersLabelText",l);
	filesLabelText = UIManager.getString("FileChooser.filesLabelText",l);
	
	pathLabelText = UIManager.getString("FileChooser.pathLabelText",l);
	pathLabelMnemonic = UIManager.getInt("FileChooser.pathLabelMnemonic"); 
    }

    protected void uninstallStrings(JFileChooser fc) {
	super.uninstallStrings(fc);

	newFolderButtonText = null;
	deleteFileButtonText = null;
	renameFileButtonText = null;

	newFolderButtonToolTipText = null;
	deleteFileButtonToolTipText = null;
	renameFileButtonToolTipText = null;

	foldersLabelText = null;
	filesLabelText = null;
	
	pathLabelText = null;
    }

    protected JScrollPane createFilesList() {
	fileList = new JList();
	fileList.setName("GTKFileChooser.fileList");

	if (getFileChooser().isMultiSelectionEnabled()) {
	    fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} else {
	    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	fileList.setModel(new GTKFileListModel());
	fileList.setCellRenderer(new FileCellRenderer());
	fileList.addListSelectionListener(createListSelectionListener(getFileChooser()));
	fileList.addMouseListener(createDoubleClickListener(getFileChooser(), fileList));
	align(fileList);
	JScrollPane scrollpane = new JScrollPane(fileList);
	scrollpane.setName("GTKFileChooser.fileListScrollPane");
	scrollpane.setPreferredSize(prefListSize);
	scrollpane.setMaximumSize(MAX_SIZE);
	align(scrollpane);
	return scrollpane;
    }

    protected JScrollPane createDirectoryList() {
	directoryList = new JList();
	directoryList.setName("GTKFileChooser.directoryList");
	align(directoryList);

	directoryList.setCellRenderer(new DirectoryCellRenderer());
	directoryList.setModel(new GTKDirectoryListModel());
	directoryList.addMouseListener(createDoubleClickListener(getFileChooser(), directoryList));
	directoryList.addListSelectionListener(createListSelectionListener(getFileChooser()));

	JScrollPane scrollpane = new JScrollPane(directoryList);
	scrollpane.setName("GTKFileChooser.directoryListScrollPane");
	scrollpane.setMaximumSize(MAX_SIZE);
	scrollpane.setPreferredSize(prefListSize);
	align(scrollpane);
	return scrollpane;
    }

    protected class GTKDirectoryListModel extends AbstractListModel implements ListDataListener {
	public GTKDirectoryListModel() {
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

	// PENDING - implement
	public void intervalRemoved(ListDataEvent e) {
	}

	// PENDING - this is inefficient - should sent out
	// incremental adjustment values instead of saying that the
	// whole list has changed.
	public void fireContentsChanged() {
	    fireContentsChanged(this, 0, getModel().getDirectories().size()-1);
	}

	// PENDING - fire the correct interval changed - currently sending
	// out that everything has changed
	public void contentsChanged(ListDataEvent e) {
	    fireContentsChanged();
	}

    }

    protected class GTKFileListModel extends AbstractListModel implements ListDataListener {
	public GTKFileListModel() {
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

	// PENDING - implement
	public void intervalRemoved(ListDataEvent e) {
	}

	// PENDING - this is inefficient - should sent out
	// incremental adjustment values instead of saying that the
	// whole list has changed.
	public void fireContentsChanged() {
	    fireContentsChanged(this, 0, getModel().getFiles().size()-1);
	}

	// PENDING - fire the interval changed
	public void contentsChanged(ListDataEvent e) {
	    fireContentsChanged();
	}

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
	    setText(getFileChooser().getName((File) value) + "/");
	    return this;
	}
    }

    public Dimension getPreferredSize(JComponent c) {
	Dimension prefSize = new Dimension(PREF_SIZE);
	JComponent accessory = getFileChooser().getAccessory();
	if (accessory != null) {
	    prefSize.width += accessory.getPreferredSize().width + 20;
	}
	Dimension d = c.getLayout().preferredLayoutSize(c);
	if (d != null) {
	    return new Dimension(d.width < prefSize.width ? prefSize.width : d.width,
				 d.height < prefSize.height ? prefSize.height : d.height);
	} else {
	    return prefSize;
	}
    }

    public Dimension getMinimumSize(JComponent x)  {
	return new Dimension(MIN_SIZE);
    }

    public Dimension getMaximumSize(JComponent x) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    protected void align(JComponent c) {
	c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	c.setAlignmentY(JComponent.TOP_ALIGNMENT);
    }

    protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser fc) {
	return new DirectoryComboBoxRenderer();
    }

    //
    // Renderer for DirectoryComboBox
    //
    class DirectoryComboBoxRenderer extends SynthComboBoxUI.SynthComboBoxRenderer  {
	public Component getListCellRendererComponent(JList list, Object value,
						      int index, boolean isSelected,
						      boolean cellHasFocus) {

	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    if (value == null) {
		setText("");
		return this;
	    }
	    setText(((File)value).getAbsolutePath());

	    return this;
	}
    }


    //
    // DataModel for DirectoryComboxbox
    //
    protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser fc) {
	return new DirectoryComboBoxModel();
    }

    /**
     * Data model for a type-face selection combo-box.
     */
    protected class DirectoryComboBoxModel extends AbstractListModel implements ComboBoxModel {
	Vector directories = new Vector();
	File selectedDirectory = null;
	JFileChooser chooser = getFileChooser();
	FileSystemView fsv = chooser.getFileSystemView();

	public DirectoryComboBoxModel() {
	    // Add the current directory to the model, and make it the
	    // selectedDirectory
	    File dir = getFileChooser().getCurrentDirectory();
	    if (dir != null) {
		addItem(dir);
	    }
	}

	/**
	 * Adds the directory to the model and sets it to be selected,
	 * additionally clears out the previous selected directory and
	 * the paths leading up to it, if any.
	 */
	private void addItem(File directory) {

	    if (directory == null) {
		return;
	    }

            int oldSize = directories.size();
	    directories.clear();
            if (oldSize > 0) {
                fireIntervalRemoved(this, 0, oldSize);
            }

	    // Get the canonical (full) path. This has the side
	    // benefit of removing extraneous chars from the path,
	    // for example /foo/bar/ becomes /foo/bar
	    File canonical = null;
	    try {
		canonical = fsv.createFileObject(directory.getCanonicalPath());
	    } catch (IOException e) {
		// Maybe drive is not ready. Can't abort here.
		canonical = directory;
	    }

	    // create File instances of each directory leading up to the top
	    File f = canonical;
	    do {
		directories.add(f);
	    } while ((f = f.getParentFile()) != null);
            int newSize = directories.size();
            if (newSize > 0) {
                fireIntervalAdded(this, 0, newSize);
            }
	    setSelectedItem(canonical);
	}

	public void setSelectedItem(Object selectedDirectory) {
	    this.selectedDirectory = (File)selectedDirectory;
            fireContentsChanged(this, -1, -1);
	}

	public Object getSelectedItem() {
	    return selectedDirectory;
	}

	public int getSize() {
	    return directories.size();
	}

	public Object getElementAt(int index) {
	    return directories.elementAt(index);
	}
    }

    /**
     * Acts when DirectoryComboBox has changed the selected item.
     */
    protected class DirectoryComboBoxAction extends AbstractAction {
	protected DirectoryComboBoxAction() {
	    super("DirectoryComboBoxAction");
	}

	public void actionPerformed(ActionEvent e) {
	    File f = (File)directoryComboBox.getSelectedItem();
	    getFileChooser().setCurrentDirectory(f);
	}
    }

}
