/*
 * @(#)MetalFileChooserUI.java	1.17 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.plaf.metal;

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
 * Basic L&F implementation of a FileChooser.
 *
 * @version 1.17 08/28/98
 * @author Jeff Dinkins
 */
public class MetalFileChooserUI extends BasicFileChooserUI {
    // These are all private because we don't want to lock the internal
    // implementation down. If you really need to subtype MetalFileChooser,
    // copy the source and modify the copy.
    private JPanel centerPanel;

    private JComboBox directoryComboBox;
    private DirectoryComboBoxModel directoryComboBoxModel;
    private Action directoryComboBoxAction = new DirectoryComboBoxAction();

    private FilterComboBoxModel filterComboBoxModel;

    private JTextField filenameTextField;

    private JList list;

    private JButton approveButton;
    private JButton cancelButton;

    private JComboBox filterComboBox;

    private JPanel bodyPanel = null;

    private static final Dimension hstrut10 = new Dimension(10, 1);
    private static final Dimension hstrut25 = new Dimension(25, 1);
    private static final Dimension vstrut10 = new Dimension(1, 10);
    private static final Insets shrinkwrap = new Insets(0,0,0,0);

    private static int PREF_WIDTH = 500;
    private static int PREF_HEIGHT = 300;
    private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);

    private static int MIN_WIDTH = 400;
    private static int MIN_HEIGHT = 200;
    private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);

    private static int LIST_MIN_WIDTH = 400;
    private static int LIST_MIN_HEIGHT = 100;
    private static Dimension LIST_MIN_SIZE = new Dimension(LIST_MIN_WIDTH, LIST_MIN_HEIGHT);

    private int    lookInLabelMnemonic = 0;
    private String lookInLabelText = null;

    private int    fileNameLabelMnemonic = 0;
    private String fileNameLabelText = null;

    private int    filesOfTypeLabelMnemonic = 0;
    private String filesOfTypeLabelText = null;

    private String upFolderToolTipText = null;
    private String upFolderAccessibleName = null;

    private String homeFolderToolTipText = null;
    private String homeFolderAccessibleName = null;

    private String newFolderToolTipText = null;
    private String newFolderAccessibleName = null;

    private String listViewButtonToolTipText = null;
    private String listViewButtonAccessibleName = null;

    private String detailsViewButtonToolTipText = null;
    private String detailsViewButtonAccessibleName = null;

    //
    // ComponentUI Interface Implementation methods
    //
    public static ComponentUI createUI(JComponent c) {
        return new MetalFileChooserUI((JFileChooser) c);
    }

    public MetalFileChooserUI(JFileChooser filechooser) {
	super(filechooser);
    }


    public void installComponents(JFileChooser fc) {
	// set to a Y BoxLayout. The chooser will be layed out top to bottom.
	fc.setLayout(new BoxLayout(fc, BoxLayout.Y_AXIS));
	fc.add(Box.createRigidArea(vstrut10));

	// ********************************* //
	// **** Construct the top panel **** //
	// ********************************* //

	// Directory manipulation buttons
	JPanel topPanel = new JPanel();
	topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

	// Add the top panel to the fileChooser
	fc.add(topPanel);
	fc.add(Box.createRigidArea(vstrut10));

	// ComboBox Label

     	JLabel l = new JLabel(lookInLabelText);
     	l.setDisplayedMnemonic(lookInLabelMnemonic);
	l.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	l.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	topPanel.add(Box.createRigidArea(hstrut10));
	topPanel.add(l);
	topPanel.add(Box.createRigidArea(hstrut25));

	// CurrentDir ComboBox
	directoryComboBox = new JComboBox();
	directoryComboBox.putClientProperty( "JComboBox.lightweightKeyboardNavigation", "Lightweight" );
	l.setLabelFor(directoryComboBox);
	directoryComboBoxModel = createDirectoryComboBoxModel(fc);
	directoryComboBox.setModel(directoryComboBoxModel);
	directoryComboBox.addActionListener(directoryComboBoxAction);
	directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
	directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	directoryComboBox.setAlignmentY(JComponent.CENTER_ALIGNMENT);

	topPanel.add(directoryComboBox);
	topPanel.add(Box.createRigidArea(hstrut10));

	// Up Button
	JButton b = new JButton(upFolderIcon);
     	b.setToolTipText(upFolderToolTipText);
     	b.getAccessibleContext().setAccessibleName(upFolderAccessibleName);
	b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	b.setMargin(shrinkwrap);
	b.addActionListener(getChangeToParentDirectoryAction());
	topPanel.add(b);
	topPanel.add(Box.createRigidArea(hstrut10));

	// Home Button
	b = new JButton(homeFolderIcon);
     	b.setToolTipText(homeFolderToolTipText);
     	b.getAccessibleContext().setAccessibleName(homeFolderAccessibleName);
	b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	b.setMargin(shrinkwrap);
	b.addActionListener(getGoHomeAction());
	topPanel.add(b);
	topPanel.add(Box.createRigidArea(hstrut10));

	// New Directory Button
	b = new JButton(newFolderIcon);
     	b.setToolTipText(newFolderToolTipText);
     	b.getAccessibleContext().setAccessibleName(newFolderAccessibleName);
	b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	b.setMargin(shrinkwrap);
	b.addActionListener(getNewFolderAction());
	topPanel.add(b);
	topPanel.add(Box.createRigidArea(hstrut10));

	// List Button
	JToggleButton tb = new JToggleButton(listViewIcon);
     	tb.setToolTipText(listViewButtonToolTipText);
     	tb.getAccessibleContext().setAccessibleName(listViewButtonAccessibleName);
	tb.setEnabled(false);
	tb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	tb.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	tb.setMargin(shrinkwrap);
	topPanel.add(tb);

	// Details Button
	tb = new JToggleButton(detailsViewIcon);
     	tb.setToolTipText(detailsViewButtonToolTipText);
     	tb.getAccessibleContext().setAccessibleName(detailsViewButtonAccessibleName);
	tb.setSelected(true);
	tb.setEnabled(false);
	tb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	tb.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	tb.setMargin(shrinkwrap);
	topPanel.add(tb);
	topPanel.add(Box.createRigidArea(hstrut10));

	// ************************************** //
	// ******* Add the directory pane ******* //
	// ************************************** //
	centerPanel = new JPanel(new BorderLayout());
	JPanel p = createList(fc);
	p.setMinimumSize(LIST_MIN_SIZE);
	centerPanel.add(p, BorderLayout.CENTER);
	centerPanel.add(getAccessoryPanel(), BorderLayout.EAST);
	JComponent accessory = fc.getAccessory();
	if(accessory != null) {
	    getAccessoryPanel().add(accessory);
	}
	fc.add(centerPanel);

	// ********************************** //
	// **** Construct the bottom panel ** //
	// ********************************** //
	JPanel bottomPanel = new JPanel();
	bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
	bottomPanel.add(Box.createRigidArea(hstrut10));

	// Add the bottom panel to file chooser
	fc.add(Box.createRigidArea(vstrut10));
	fc.add(bottomPanel);
	fc.add(Box.createRigidArea(vstrut10));

	// labels
	JPanel labelPanel = new JPanel();
	labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

     	JLabel fnl = new JLabel(fileNameLabelText);
     	fnl.setDisplayedMnemonic(fileNameLabelMnemonic);
	labelPanel.add(fnl);

	labelPanel.add(Box.createRigidArea(vstrut10));

     	JLabel ftl = new JLabel(filesOfTypeLabelText);
     	ftl.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
	labelPanel.add(ftl);

	bottomPanel.add(labelPanel);
	bottomPanel.add(Box.createRigidArea(hstrut25));

	// file entry and filters
	JPanel fileAndFilterPanel = new JPanel();
	fileAndFilterPanel.setLayout(new BoxLayout(fileAndFilterPanel, BoxLayout.Y_AXIS));

	filenameTextField = new JTextField();
	fnl.setLabelFor(filenameTextField);
	filenameTextField.addActionListener(getApproveSelectionAction());
	File f = fc.getSelectedFile();
	if(f != null) {
	    setFileName(fc.getName(f));
	}

	fileAndFilterPanel.add(filenameTextField);

	fileAndFilterPanel.add(Box.createRigidArea(vstrut10));

	filterComboBoxModel = createFilterComboBoxModel();
	fc.addPropertyChangeListener(filterComboBoxModel);
	filterComboBox = new JComboBox(filterComboBoxModel);
	ftl.setLabelFor(filterComboBox);
	filterComboBox.setRenderer(createFilterComboBoxRenderer());
	fileAndFilterPanel.add(filterComboBox);

	bottomPanel.add(fileAndFilterPanel);
	bottomPanel.add(Box.createRigidArea(hstrut10));

	// buttons
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

	approveButton = new JButton(getApproveButtonText(fc)) {
	    public Dimension getMaximumSize() {
		return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
	    }
	};
	approveButton.setMnemonic(getApproveButtonMnemonic(fc));
	approveButton.addActionListener(getApproveSelectionAction());
	approveButton.setToolTipText(getApproveButtonToolTipText(fc));
	buttonPanel.add(approveButton);
	buttonPanel.add(Box.createRigidArea(vstrut10));

	cancelButton = new JButton(cancelButtonText) {
	    public Dimension getMaximumSize() {
		return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
	    }
	};
	cancelButton.setMnemonic(cancelButtonMnemonic);
	cancelButton.setToolTipText(cancelButtonToolTipText);
	cancelButton.addActionListener(getCancelSelectionAction());
	buttonPanel.add(cancelButton);

	bottomPanel.add(buttonPanel);
	bottomPanel.add(Box.createRigidArea(hstrut10));
    }

    protected void installStrings(JFileChooser fc) {
	super.installStrings(fc);

	lookInLabelMnemonic = UIManager.getInt("FileChooser.lookInLabelMnemonic"); 
	lookInLabelText = UIManager.getString("FileChooser.lookInLabelText");
	
	fileNameLabelMnemonic = UIManager.getInt("FileChooser.fileNameLabelMnemonic");  
	fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText"); 
	
	filesOfTypeLabelMnemonic = UIManager.getInt("FileChooser.filesOfTypeLabelMnemonic");  
	filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText"); 
	
	upFolderToolTipText =  UIManager.getString("FileChooser.upFolderToolTipText");
	upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName"); 
	
	homeFolderToolTipText =  UIManager.getString("FileChooser.homeFolderToolTipText");
	homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName"); 
	
	newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText");
	newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName"); 
	
	listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText"); 
	listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName"); 
	
	detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText"); 
	detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName"); 
    }

    protected JPanel createList(JFileChooser fc) {
	JPanel p = new JPanel(new BorderLayout());
	list = new JList();
	list.setCellRenderer(new FileRenderer());
	list.setModel(getModel());
	list.addListSelectionListener(createListSelectionListener(fc));
	list.addMouseListener(createDoubleClickListener(fc, list));
	list.addMouseListener(createSingleClickListener(fc, list));
	JScrollPane scrollpane = new JScrollPane(list);


	p.add(scrollpane, BorderLayout.CENTER);
	return p;
    }

    private MouseListener createSingleClickListener(JFileChooser fc, JList list) {
	return new SingleClickListener(list);
    }

    int lastIndex = -1;
    boolean editing = false;
    int editX = 20;
    int editWidth = 200;

    private void cancelEdit() {
	editing = false;
	if(editCell != null) {
	    list.remove(editCell);
	}
    }

    JTextField editCell = null;
    protected class SingleClickListener extends MouseAdapter {
	JList list;

	public  SingleClickListener(JList list) {
	    this.list = list;
	    editCell = new JTextField();
	    editCell.addActionListener(new EditActionListener());
	}

	public void mouseClicked(MouseEvent e) {
	    if (e.getClickCount() == 1) {
		int index = list.locationToIndex(e.getPoint());
		if(lastIndex == index && editing == false) {
		    editing = true;
		    Rectangle r = list.getCellBounds(index, index);
		    list.add(editCell);
		    File f = (File) list.getSelectedValue();
		    editCell.setText(getFileChooser().getName(f));
		    editCell.setBounds(editX + r.x, r.y, editWidth, r.height);
		    editCell.selectAll();
		} else {
		    lastIndex = index;
		    cancelEdit();
		}
	    } else {
		cancelEdit();
	    }
	    list.repaint();
	    
	}

    }

    class EditActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    JTextField tf = (JTextField) e.getSource();
	    File f = (File) list.getSelectedValue();
	    String newFileName = tf.getText();
	    newFileName = newFileName.trim();
	    if(!newFileName.equals(getFileChooser().getName(f))) {
		// rename
                File f2 = getFileChooser().getFileSystemView().createFileObject(
                        getFileChooser().getCurrentDirectory(), newFileName
                );
		if(f.renameTo(f2)) {
		    rescanCurrentDirectory(getFileChooser());
		} else {
		    // PENDING(jeff) - show a dialog indicating failure
		}
	    }
	    cancelEdit();
	    list.repaint();
	} 
    }

    protected class FileRenderer extends DefaultListCellRenderer  {

	public Component getListCellRendererComponent(JList list, Object value,
						      int index, boolean isSelected,
						      boolean cellHasFocus) {

	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    File file = (File) value;
	    String fileName = getFileChooser().getName(file);
	    setText(fileName);

	    Icon icon = getFileChooser().getIcon(file);
	    setIcon(icon);

	    if(isSelected) {
		// PENDING(jeff) - grab padding (4) below from defaults table.
		editX = icon.getIconWidth() + 4;
	    }

	    return this;
	}
    }

    public void uninstallUI(JComponent c) {
	// Remove listeners
	c.removePropertyChangeListener(filterComboBoxModel);
	cancelButton.removeActionListener(getCancelSelectionAction());
	approveButton.removeActionListener(getApproveSelectionAction());
	filenameTextField.removeActionListener(getApproveSelectionAction());

	super.uninstallUI(c);
    }

    public Dimension getPreferredSize(JComponent c) {
	return PREF_SIZE;
    }

    public Dimension getMinimumSize(JComponent c) {
	return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /*
     * Listen for filechooser property changes, such as
     * the selected file changing, or the type of the dialog changing.
     */
    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
	return new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		if(prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
		    cancelEdit();
		    File f = (File) e.getNewValue();
		    if(f != null) {
			setFileName(getFileChooser().getName(f));
			if(getModel().contains(f)) {
			    list.setSelectedIndex(getModel().indexOf(e.getNewValue()));
			    list.ensureIndexIsVisible(list.getSelectedIndex());
			}
		    }
		} else if(prop.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
		    cancelEdit();
		    clearIconCache();
		    list.clearSelection();
		    File currentDirectory = getFileChooser().getCurrentDirectory();
		    if(currentDirectory != null) {
			directoryComboBoxModel.addItem(currentDirectory);
			// Enable the newFolder action if the current directory
			// is writable.
			// PENDING(jeff) - broken - fix
			getNewFolderAction().setEnabled(currentDirectory.canWrite());
		    }
		} else if(prop.equals(JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY)) {
		    clearIconCache();
		    list.clearSelection();
		} else if(prop == JFileChooser.ACCESSORY_CHANGED_PROPERTY) {
		    if(getAccessoryPanel() != null) {
			if(e.getOldValue() != null) {
			    getAccessoryPanel().remove((JComponent) e.getOldValue());
			}
			JComponent accessory = (JComponent) e.getNewValue();
			if(accessory != null) {
			    getAccessoryPanel().add(accessory, BorderLayout.CENTER);
			}
		    }
		} else if(prop == JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY ||
			  prop == JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY) {
		    JFileChooser chooser = getFileChooser();
		    approveButton.setText(getApproveButtonText(chooser));
		    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
		    approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
		} else if(prop.equals(JFileChooser.APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY)) {
		    approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
		}
	    }
	};
    }

    public void ensureFileIsVisible(JFileChooser fc, File f) {
	if(getModel().contains(f)) {
	    list.ensureIndexIsVisible(getModel().indexOf(f));
	}
    }

    public void rescanCurrentDirectory(JFileChooser fc) {
	getModel().invalidateFileCache();
	getModel().validateFileCache();
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
	// PENDING(jeff) - get the name from the directory combobox
	return null;
    }

    public void setDirectoryName(String dirname) {
	// PENDING(jeff) - set the name in the directory combobox
    }

    protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser fc) {
	return new DirectoryComboBoxRenderer();
    }

    //
    // Renderer for DirectoryComboBox
    //
    class DirectoryComboBoxRenderer extends DefaultListCellRenderer  {
	IndentIcon ii = new IndentIcon();
	public Component getListCellRendererComponent(JList list, Object value,
						      int index, boolean isSelected,
						      boolean cellHasFocus) {

	    super.getListCellRendererComponent(list, value, index,
					       isSelected, cellHasFocus);
	    File directory = (File) value;
	    if(directory == null) {
		setText("");
		return this;
	    }

	    String fileName = getFileChooser().getName(directory);
            setText(fileName);

	    // Find the depth of the directory
	    int depth = 0;
	    if(index != -1) {
		File f = directory;
		while(f.getParent() != null) {
		    depth++;
                    f = getFileChooser().getFileSystemView().createFileObject(
                        f.getParent()
                    );
		}
	    }
	    
	    Icon icon = getFileChooser().getIcon(directory);

	    ii.icon = icon;
	    ii.depth = depth;
	    
	    setIcon(ii);

	    return this;
	}
    }

    final static int space = 10;
    class IndentIcon implements Icon {

	Icon icon = null;
	int depth = 0;

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    icon.paintIcon(c, g, x+depth*space, y);
	}

	public int getIconWidth() {
	    return icon.getIconWidth() + depth*space;
	}

	public int getIconHeight() {
	    return icon.getIconHeight();
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
	int topIndex = -1;
	int pathCount = 0;

	File selectedDirectory = null;

	public DirectoryComboBoxModel() {
	    super();

	    // Add root files to the model
	    File[] roots = getFileChooser().getFileSystemView().getRoots();
	    for(int i = 0; i < roots.length; i++) {
		directories.addElement(roots[i]);
	    }

	    // Add the current directory to the model, and make it the
	    // selectedDirectory
	    addItem(getFileChooser().getCurrentDirectory());
	}

	/**
	 * Removes the selected directory, and clears out the
	 * path file entries leading up to that directory.
	 */
	private void removeSelectedDirectory() {
	    if(topIndex >= 0 ) {
		for(int i = topIndex; i < topIndex + pathCount; i++) {
		    directories.removeElementAt(topIndex+1);
		}
	    }
	    topIndex = -1;
	    pathCount = 0;
	    selectedDirectory = null;
	    // dump();
	}

	/*
	private void dump() {
	    System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVV");
	    System.out.println("dumping directories");
	    for(int i = 0; i < directories.size(); i++) {
		File f = (File) directories.elementAt(i);
		System.out.println(f.getPath());
	    }
	    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^");
	}
	*/
	
	/**
	 * Adds the directory to the model and sets it to be selected,
	 * additionally clears out the previous selected directory and
	 * the paths leading up to it, if any.
	 */
	private void addItem(File directory) {

	    if(directory == null) {
		return;
	    }
	    if(selectedDirectory != null) {
		removeSelectedDirectory();
	    }

	    // Get the canonical (full) path. This has the side
	    // benefit of removing extraneous chars from the path,
	    // for example /foo/bar/ becomes /foo/bar
	    File canonical = null;
	    try {
                canonical = getFileChooser().getFileSystemView().createFileObject(
                    directory.getCanonicalPath()
                );
	    } catch (IOException e) {
	    }

	    // create File instances of each directory leading up to the top
	    File f = canonical;
	    Vector path = new Vector(10);
	    while(f.getParent() != null) {
		path.addElement(f);
		
		// Find the index of the top leveo of the passed
		// in directory
		if(directories.contains(f)) {
		    topIndex = directories.indexOf(f); 
		}
		
                f = getFileChooser().getFileSystemView().createFileObject(f.getParent());
	    }
	    pathCount = path.size();
	    
	    // if we didn't find the top index above, check
	    // the remaining parent
	    // PENDING(jeff) - if this fails, we need might
	    // need to scan all the other roots?
	    if(topIndex < 0) {
		if(directories.contains(f)) {
		    topIndex = directories.indexOf(f); 
		}
	    }

	    // insert all the path directories leading up to the
	    // selected directory.
	    for(int i = 0; i < path.size(); i++) {
		directories.insertElementAt(path.elementAt(i), topIndex+1);
	    }

	    setSelectedItem(canonical);

	    // dump();
	}

	public void setSelectedItem(Object selectedDirectory) {
	    this.selectedDirectory = (File) selectedDirectory;
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

	    FileFilter filter = (FileFilter) value;
	    if(filter != null) {
		setText(filter.getDescription());
	    }

	    return this;
	}
    }

    //
    // DataModel for Types Comboxbox
    //
    protected FilterComboBoxModel createFilterComboBoxModel() {
	return new FilterComboBoxModel();
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

    public void valueChanged(ListSelectionEvent e) {
	File f = getFileChooser().getSelectedFile();
	if (!e.getValueIsAdjusting() && f != null && !getFileChooser().isTraversable(f)) {
	    setFileName(getFileChooser().getName(f));
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
	    getFileChooser().setCurrentDirectory((File) directoryComboBox.getSelectedItem());
	}
    }

    protected JButton getApproveButton(JFileChooser fc) {
	return approveButton;
    }

}
