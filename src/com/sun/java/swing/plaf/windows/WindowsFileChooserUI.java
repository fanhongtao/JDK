/*
 * @(#)WindowsFileChooserUI.java	1.78 03/02/17
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

import sun.awt.shell.ShellFolder;

/**
 * Windows L&F implementation of a FileChooser.
 *
 * @version 1.78 02/17/03
 * @author Jeff Dinkins
 */
public class WindowsFileChooserUI extends BasicFileChooserUI {

    // The following are private because the implementation of the
    // Windows FileChooser L&F is not complete yet.

    private static final String[] OS_NAMES =
		new String[] { "Windows 3.1", "Windows 95", "Windows NT",
			       "Windows 98", "Windows 2000", "Windows Me", "Windows XP" };
    private static int WIN_31 = 0;
    private static int WIN_95 = 1;
    private static int WIN_NT = 2;
    private static int WIN_98 = 3;
    private static int WIN_2k = 4;
    private static int WIN_Me = 5;
    private static int WIN_XP = 6;
    private static String osName = System.getProperty("os.name");
    private static String osVersion = System.getProperty("os.version");
    private static final String OS_NAME = ((osName.equals(OS_NAMES[WIN_98]) && osVersion.startsWith("4.9"))
					   ? "Windows Me" : osName);
    private static final int OS_LEVEL = Arrays.asList(OS_NAMES).indexOf(OS_NAME);

    private JPanel centerPanel;

    private JLabel lookInLabel;
    private JComboBox directoryComboBox;
    private DirectoryComboBoxModel directoryComboBoxModel;
    private ActionListener directoryComboBoxAction = new DirectoryComboBoxAction();

    private FilterComboBoxModel filterComboBoxModel;

    private JTextField filenameTextField;
    private ShortCutPanel shortCutPanel;
    private JToggleButton listViewButton;
    private JToggleButton detailsViewButton;
    private JPanel listViewPanel;
    private JPanel detailsViewPanel;
    private JPanel currentViewPanel;
    private FocusListener editorFocusListener = new FocusAdapter() {
	public void focusLost(FocusEvent e) {
	    if (! e.isTemporary()) {
		applyEdit();
	    }
	}
    };
    private boolean smallIconsView = false;
    private Border listViewBorder;
    private boolean useShellFolder;

    private ListSelectionModel listSelectionModel;
    private JList list;
    private JTable detailsTable;

    private JButton approveButton;
    private JButton cancelButton;

    private JPanel buttonPanel;
    private JPanel bottomPanel;

    private JComboBox filterComboBox;

    private static final Dimension hstrut10 = new Dimension(10, 1);
    private static final Dimension hstrut25 = new Dimension(25, 1);

    private static final Dimension vstrut1  = new Dimension(1, 1);
    private static final Dimension vstrut4  = new Dimension(1, 4);
    private static final Dimension vstrut5  = new Dimension(1, 5);
    private static final Dimension vstrut6  = new Dimension(1, 6);
    private static final Dimension vstrut8  = new Dimension(1, 8);

    private static final Insets shrinkwrap = new Insets(0,0,0,0);

    // Preferred and Minimum sizes for the dialog box
    private static int PREF_WIDTH = 425;
    private static int PREF_HEIGHT = 245;
    private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);

    private static int MIN_WIDTH = 425;
    private static int MIN_HEIGHT = 245;
    private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);

    private static int LIST_PREF_WIDTH = 444;
    private static int LIST_PREF_HEIGHT = 138;
    private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);

    private static final int COLUMN_FILENAME = 0;
    private static final int COLUMN_FILESIZE = 1;
    private static final int COLUMN_FILETYPE = 2;
    private static final int COLUMN_FILEDATE = 3;
    private static final int COLUMN_FILEATTR = 4;
    private static final int COLUMN_COLCOUNT = 5;

    private int[] COLUMN_WIDTHS = { 150,  75,  130,  130,  40 };

    // Labels, mnemonics, and tooltips (oh my!)
    private int    lookInLabelMnemonic = 0;
    private String lookInLabelText = null;
    private String saveInLabelText = null;

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

    private String fileNameHeaderText = null;
    private String fileSizeHeaderText = null;
    private String fileTypeHeaderText = null;
    private String fileDateHeaderText = null;
    private String fileAttrHeaderText = null;

    private Action newFolderAction = new WindowsNewFolderAction();
    private File newFolderFile;
    private BasicFileView fileView = new WindowsFileView();

    //
    // ComponentUI Interface Implementation methods
    //
    public static ComponentUI createUI(JComponent c) {
        return new WindowsFileChooserUI((JFileChooser) c);
    }

    public WindowsFileChooserUI(JFileChooser filechooser) {
	super(filechooser);
    }

    public void installUI(JComponent c) {
	super.installUI(c);
    }

    public void uninstallComponents(JFileChooser fc) {
	fc.removeAll();
    }

    public void installComponents(JFileChooser fc) {
	FileSystemView fsv = fc.getFileSystemView();
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    listViewBorder = xp.getBorder("listview");
	} else {
	    listViewBorder = new BevelBorder(BevelBorder.LOWERED,
					     UIManager.getColor("ToolBar.highlight"),
					     UIManager.getColor("ToolBar.background"),
					     UIManager.getColor("ToolBar.darkShadow"),
					     UIManager.getColor("ToolBar.shadow"));
	}

	fc.setBorder(new EmptyBorder(4, 10, 10, 10));
	fc.setLayout(new BorderLayout(8, 8));

	// ********************************* //
	// **** Construct the top panel **** //
	// ********************************* //

	// Directory manipulation buttons
	JToolBar topPanel = new JToolBar();
	topPanel.setFloatable(false);
	if (OS_LEVEL >= WIN_2k) {
	    topPanel.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
	}

	// Add the top panel to the fileChooser
	fc.add(topPanel, BorderLayout.NORTH);

	// ComboBox Label
     	lookInLabel = new JLabel(lookInLabelText);
     	lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
	lookInLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	lookInLabel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	topPanel.add(Box.createRigidArea(new Dimension(14,0)));
	topPanel.add(lookInLabel);
	topPanel.add(Box.createRigidArea(new Dimension(29,0)));

	// CurrentDir ComboBox
	directoryComboBox = new JComboBox();
	directoryComboBox.putClientProperty( "JComboBox.lightweightKeyboardNavigation", "Lightweight" );
	lookInLabel.setLabelFor(directoryComboBox);
	directoryComboBoxModel = createDirectoryComboBoxModel(fc);
	directoryComboBox.setModel(directoryComboBoxModel);
	directoryComboBox.addActionListener(directoryComboBoxAction);
	directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
	directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	directoryComboBox.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	directoryComboBox.setMaximumRowCount(8);

	topPanel.add(directoryComboBox);
	topPanel.add(Box.createRigidArea(hstrut10));

	// Up Button
	JButton upFolderButton = new JButton(getChangeToParentDirectoryAction());
	upFolderButton.setText(null);
	upFolderButton.setIcon(upFolderIcon);
     	upFolderButton.setToolTipText(upFolderToolTipText);
     	upFolderButton.getAccessibleContext().setAccessibleName(upFolderAccessibleName);
	upFolderButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	upFolderButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	upFolderButton.setMargin(shrinkwrap);
	upFolderButton.setFocusPainted(false);
	topPanel.add(upFolderButton);
	if (OS_LEVEL < WIN_2k) {
	    topPanel.add(Box.createRigidArea(hstrut10));
	}

	JButton b;

	if (OS_LEVEL == WIN_98) {
	    // Desktop Button
	    File homeDir = fsv.getHomeDirectory();
	    String toolTipText = homeFolderToolTipText;
	    if (fsv.isRoot(homeDir)) {
		toolTipText = getFileView(fc).getName(homeDir); // Probably "Desktop".
	    }
	    b = new JButton(getFileView(fc).getIcon(homeDir));
	    b.setToolTipText(toolTipText);
	    b.getAccessibleContext().setAccessibleName(toolTipText);
	    b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	    b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	    b.setMargin(shrinkwrap);
	    b.setFocusPainted(false);
	    b.addActionListener(getGoHomeAction());
	    topPanel.add(b);
	    topPanel.add(Box.createRigidArea(hstrut10));
	}

	// New Directory Button
	b = new JButton(getNewFolderAction());
	b.setText(null);
	b.setIcon(newFolderIcon);
     	b.setToolTipText(newFolderToolTipText);
     	b.getAccessibleContext().setAccessibleName(newFolderAccessibleName);
	b.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	b.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	b.setMargin(shrinkwrap);
	b.setFocusPainted(false);
	topPanel.add(b);
	if (OS_LEVEL < WIN_2k) {
	    topPanel.add(Box.createRigidArea(hstrut10));
	}

	// View button group
	ButtonGroup viewButtonGroup = new ButtonGroup();

	class ViewButtonListener implements ActionListener {
	    JFileChooser fc;

	    ViewButtonListener(JFileChooser fc) {
		this.fc = fc;
	    }

	    public void actionPerformed(ActionEvent e) {
		JToggleButton b = (JToggleButton)e.getSource();
		JPanel oldViewPanel = currentViewPanel;

		if (b == detailsViewButton) {
		    if (detailsViewPanel == null) {
			detailsViewPanel = createDetailsView(fc);
			detailsViewPanel.setPreferredSize(LIST_PREF_SIZE);
		    }
		    currentViewPanel = detailsViewPanel;
		} else {
		    currentViewPanel = listViewPanel;
		}
		if (currentViewPanel != oldViewPanel) {
		    centerPanel.remove(oldViewPanel);
		    centerPanel.add(currentViewPanel, BorderLayout.CENTER);
		    centerPanel.revalidate();
		    centerPanel.repaint();
		}
	    }
	}

	ViewButtonListener viewButtonListener = new ViewButtonListener(fc);

	// List Button
	listViewButton = new JToggleButton(listViewIcon);
     	listViewButton.setToolTipText(listViewButtonToolTipText);
     	listViewButton.getAccessibleContext().setAccessibleName(listViewButtonAccessibleName);
	listViewButton.setFocusPainted(false);
	listViewButton.setSelected(true);
	listViewButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	listViewButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	listViewButton.setMargin(shrinkwrap);
	listViewButton.addActionListener(viewButtonListener);
	topPanel.add(listViewButton);
	viewButtonGroup.add(listViewButton);

	// Details Button
	detailsViewButton = new JToggleButton(detailsViewIcon);
     	detailsViewButton.setToolTipText(detailsViewButtonToolTipText);
     	detailsViewButton.getAccessibleContext().setAccessibleName(detailsViewButtonAccessibleName);
	detailsViewButton.setFocusPainted(false);
	detailsViewButton.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	detailsViewButton.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	detailsViewButton.setMargin(shrinkwrap);
	detailsViewButton.addActionListener(viewButtonListener);
	topPanel.add(detailsViewButton);
	viewButtonGroup.add(detailsViewButton);

	// Use ShellFolder class to populate shortcut panel and combobox
	// only if FileSystemView.getRoots() returns the desktop folder,
	// i.e. the normal Windows hierarchy.
	{
	    useShellFolder = false;
	    File[] roots = fsv.getRoots();
	    if (roots != null && roots.length == 1) {
		File[] cbFolders = (File[])ShellFolder.get("fileChooserComboBoxFolders");
		if (cbFolders != null && cbFolders.length > 0 && roots[0] == cbFolders[0]) {
		    useShellFolder = true;
		}
	    }
	}
	if (OS_LEVEL >= WIN_2k && useShellFolder) {
	    shortCutPanel = new ShortCutPanel();
	    fc.add(shortCutPanel, BorderLayout.BEFORE_LINE_BEGINS);
	}

	// ************************************** //
	// ******* Add the directory pane ******* //
	// ************************************** //
	centerPanel = new JPanel(new BorderLayout());
	listViewPanel = createList(fc);
	listSelectionModel = list.getSelectionModel();
	listViewPanel.setPreferredSize(LIST_PREF_SIZE);
	centerPanel.add(listViewPanel, BorderLayout.CENTER);
	currentViewPanel = listViewPanel;
	centerPanel.add(getAccessoryPanel(), BorderLayout.AFTER_LINE_ENDS);
	JComponent accessory = fc.getAccessory();
	if(accessory != null) {
	    getAccessoryPanel().add(accessory);
	}
	fc.add(centerPanel, BorderLayout.CENTER);

	// ********************************** //
	// **** Construct the bottom panel ** //
	// ********************************** //
	getBottomPanel().setLayout(new BoxLayout(getBottomPanel(), BoxLayout.LINE_AXIS));

	// Add the bottom panel to file chooser
	centerPanel.add(getBottomPanel(), BorderLayout.SOUTH);

	// labels
	JPanel labelPanel = new JPanel();
	labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
        labelPanel.add(Box.createRigidArea(vstrut4));

     	JLabel fnl = new JLabel(fileNameLabelText);
     	fnl.setDisplayedMnemonic(fileNameLabelMnemonic);
	fnl.setAlignmentY(0);
	labelPanel.add(fnl);

	labelPanel.add(Box.createRigidArea(new Dimension(1,12)));

     	JLabel ftl = new JLabel(filesOfTypeLabelText);
     	ftl.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
	labelPanel.add(ftl);

	getBottomPanel().add(labelPanel);
	getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));

	// file entry and filters
	JPanel fileAndFilterPanel = new JPanel();
        fileAndFilterPanel.add(Box.createRigidArea(vstrut8));
	fileAndFilterPanel.setLayout(new BoxLayout(fileAndFilterPanel, BoxLayout.Y_AXIS));


	filenameTextField = new JTextField(35) {
	    public Dimension getMaximumSize() {
		return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
	    }
	};

	fnl.setLabelFor(filenameTextField);
        filenameTextField.addFocusListener(
	    new FocusAdapter() {
		public void focusGained(FocusEvent e) {
		    if (!getFileChooser().isMultiSelectionEnabled()) {
			listSelectionModel.clearSelection();
		    }
		}
	    }
        );

	if (fc.isMultiSelectionEnabled()) {
	    setFileName(fileNameString(fc.getSelectedFiles()));
	} else {
	    setFileName(fileNameString(fc.getSelectedFile()));
	}

	fileAndFilterPanel.add(filenameTextField);
	fileAndFilterPanel.add(Box.createRigidArea(vstrut8));

	filterComboBoxModel = createFilterComboBoxModel();
	fc.addPropertyChangeListener(filterComboBoxModel);
	filterComboBox = new JComboBox(filterComboBoxModel);
	ftl.setLabelFor(filterComboBox);
	filterComboBox.setRenderer(createFilterComboBoxRenderer());
	fileAndFilterPanel.add(filterComboBox);

	getBottomPanel().add(fileAndFilterPanel);
	getBottomPanel().add(Box.createRigidArea(hstrut10));

	// buttons
	getButtonPanel().setLayout(new BoxLayout(getButtonPanel(), BoxLayout.Y_AXIS));

	approveButton = new JButton(getApproveButtonText(fc)) {
	    public Dimension getMaximumSize() {
		return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ?
		       approveButton.getPreferredSize() : cancelButton.getPreferredSize();
	    }
	}; 
	approveButton.setMnemonic(getApproveButtonMnemonic(fc));
	approveButton.addActionListener(getApproveSelectionAction());
	approveButton.setToolTipText(getApproveButtonToolTipText(fc));
	getButtonPanel().add(Box.createRigidArea(vstrut4));
	getButtonPanel().add(approveButton);
	getButtonPanel().add(Box.createRigidArea(vstrut6));

	cancelButton = new JButton(cancelButtonText) {
	    public Dimension getMaximumSize() {
		return approveButton.getPreferredSize().width > cancelButton.getPreferredSize().width ?
		       approveButton.getPreferredSize() : cancelButton.getPreferredSize();
	    }
	}; 
	cancelButton.setMnemonic(cancelButtonMnemonic);
	cancelButton.setToolTipText(cancelButtonToolTipText);
	cancelButton.addActionListener(getCancelSelectionAction());
	getButtonPanel().add(cancelButton);

	if(fc.getControlButtonsAreShown()) {
	    addControlButtons();
	}
    }

    protected JPanel getButtonPanel() {
	if(buttonPanel == null) {
	    buttonPanel = new JPanel();
	}
	return buttonPanel;
    }

    protected JPanel getBottomPanel() {
	if(bottomPanel == null) {
	    bottomPanel = new JPanel();
	}
	return bottomPanel;
    }

    protected void installStrings(JFileChooser fc) {
	super.installStrings(fc);

        Locale l = fc.getLocale();

	lookInLabelMnemonic = UIManager.getInt("FileChooser.lookInLabelMnemonic"); 
	lookInLabelText = UIManager.getString("FileChooser.lookInLabelText",l);
	saveInLabelText = UIManager.getString("FileChooser.saveInLabelText",l);
	
	fileNameLabelMnemonic = UIManager.getInt("FileChooser.fileNameLabelMnemonic");  
	fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText",l); 
	
	filesOfTypeLabelMnemonic = UIManager.getInt("FileChooser.filesOfTypeLabelMnemonic");  
	filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText",l); 
	
	upFolderToolTipText =  UIManager.getString("FileChooser.upFolderToolTipText",l);
	upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName",l); 
	
	homeFolderToolTipText =  UIManager.getString("FileChooser.homeFolderToolTipText",l);
	homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName",l); 
	
	newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText",l);
	newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName",l); 
	
	listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText",l); 
	listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName",l); 
	
	detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText",l); 
	detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName",l); 


	fileNameHeaderText = UIManager.getString("FileChooser.fileNameHeaderText",l);
	fileSizeHeaderText = UIManager.getString("FileChooser.fileSizeHeaderText",l);
	fileTypeHeaderText = UIManager.getString("FileChooser.fileTypeHeaderText",l);
	fileDateHeaderText = UIManager.getString("FileChooser.fileDateHeaderText",l);
	fileAttrHeaderText = UIManager.getString("FileChooser.fileAttrHeaderText",l);
    }

    protected void installListeners(JFileChooser fc) {
	super.installListeners(fc);
        ActionMap actionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(fc, actionMap);
    }

    protected ActionMap getActionMap() {
        return createActionMap();
    }

    protected ActionMap createActionMap() {
        AbstractAction escAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
		if (editFile != null) {
    		   cancelEdit();
		} else {
                   getFileChooser().cancelSelection();
		}
            }
            public boolean isEnabled(){
                return getFileChooser().isEnabled();
            }
        };
        ActionMap map = new ActionMapUIResource();
        map.put("approveSelection", getApproveSelectionAction());
        map.put("cancelSelection", escAction);
        map.put("Go Up", getChangeToParentDirectoryAction());
        return map;
    }

    class ShortCutPanel extends JToolBar implements ActionListener {
	JToggleButton[] buttons;
	File[] files;
	XPStyle xp = XPStyle.getXP();
	final Dimension buttonSize = new Dimension(83, (xp != null) ? 69 : 54);

	ShortCutPanel() {
	    super(JToolBar.VERTICAL);
	    setFloatable(false);
	    putClientProperty("JToolBar.isRollover", Boolean.TRUE);
	    if (xp != null) {
		putClientProperty("XPStyle.subClass", "placesbar");
		setBorder(new EmptyBorder(1, 1, 1, 1));
	    } else {
		setBorder(listViewBorder);
	    }
	    Color bgColor = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
	    setBackground(bgColor);
	    JFileChooser chooser = getFileChooser();
	    FileSystemView fsv = chooser.getFileSystemView();

	    files = (File[])ShellFolder.get("fileChooserShortcutPanelFolders");
	    buttons = new JToggleButton[files.length];
	    ButtonGroup buttonGroup = new ButtonGroup();
	    for (int i = 0; i < files.length; i++) {
		if (fsv.isFileSystemRoot(files[i])) {
		    // Create special File wrapper for drive path
		    files[i] = fsv.createFileObject(files[i].getAbsolutePath());
		}

		String folderName = fsv.getSystemDisplayName(files[i]);
		int index = folderName.lastIndexOf(File.separatorChar);
		if (index >= 0 && index < folderName.length() - 1) {
		    folderName = folderName.substring(index + 1);
		}
		Icon icon = null;
		if (files[i] instanceof ShellFolder) {
		    // We want a large icon, fsv only gives us a small.
		    ShellFolder sf = (ShellFolder)files[i];
		    icon = new ImageIcon(sf.getIcon(true), sf.getFolderType());
		} else {
		    icon = fsv.getSystemIcon(files[i]);
		}
		buttons[i] = new JToggleButton(folderName, icon);
		if (xp != null) {
		    buttons[i].setIconTextGap(2);
		    buttons[i].setMargin(new Insets(2, 2, 2, 2));
		    buttons[i].setText("<html><center>"+folderName+"</center></html>");
		} else {
		    Color fgColor = new Color(UIManager.getColor("List.selectionForeground").getRGB());
		    buttons[i].setBackground(bgColor);
		    buttons[i].setForeground(fgColor);
		}
		buttons[i].setHorizontalTextPosition(JToggleButton.CENTER);
		buttons[i].setVerticalTextPosition(JToggleButton.BOTTOM);
		buttons[i].setAlignmentX(JComponent.CENTER_ALIGNMENT);
		buttons[i].setPreferredSize(buttonSize);
		buttons[i].setMaximumSize(buttonSize);
		buttons[i].addActionListener(this);
		add(buttons[i]);
		if (i < files.length-1 && xp != null) {
		    add(Box.createRigidArea(vstrut1));
		}
		buttonGroup.add(buttons[i]);
	    }
	    doDirectoryChanged(chooser.getCurrentDirectory());
	}

	void doDirectoryChanged(File f) {
	    for (int i=0; i<buttons.length; i++) {
		buttons[i].setSelected(files[i].equals(f));
	    }
	}

	public void actionPerformed(ActionEvent e) {
	    JToggleButton b = (JToggleButton)e.getSource();
	    for (int i=0; i<buttons.length; i++) {
		if (b == buttons[i]) {
		    getFileChooser().setCurrentDirectory(files[i]);
		    break;
		}
	    }
	}

	public Dimension getPreferredSize() {
	    Dimension min  = super.getMinimumSize();
	    Dimension pref = super.getPreferredSize();
	    if (min.height > pref.height) {
		pref = new Dimension(pref.width, min.height);
	    }
	    return pref;
	}
    } // class ShortCutPanel


    private void updateListRowCount() {
	if (smallIconsView) {
	    list.setVisibleRowCount(getModel().getSize() / 3);
	} else {
	    list.setVisibleRowCount(-1);
	}
    }

    protected JPanel createList(JFileChooser fc) {
	JPanel p = new JPanel(new BorderLayout());
	final JFileChooser fileChooser = fc;
	list = new JList() {
	    public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
		ListModel model = getModel();
		int max = model.getSize();
		if (prefix == null || startIndex < 0 || startIndex >= max) {
		    throw new IllegalArgumentException();
		}
		// start search from the next element before/after the selected element
		boolean backwards = (bias == Position.Bias.Backward);
		for (int i = startIndex; backwards ? i >= 0 : i < max; i += (backwards ?  -1 : 1)) {
		    String filename = fileChooser.getName((File)model.getElementAt(i));
		    if (filename.regionMatches(true, 0, prefix, 0, prefix.length())) {
			return i;
		    }
		}
		return -1;
	    }
	};
	list.setCellRenderer(new FileRenderer());
	list.setLayoutOrientation(JList.VERTICAL_WRAP);

	updateListRowCount();

	getModel().addListDataListener(new ListDataListener() {
	    public void intervalAdded(ListDataEvent e) {
		updateListRowCount();
	    }
	    public void intervalRemoved(ListDataEvent e) {
		updateListRowCount();
	    }
	    public void contentsChanged(ListDataEvent e) {
		updateListRowCount();
	    }
	});

	if (fc.isMultiSelectionEnabled()) {
	    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} else {
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	list.setModel(getModel());
	list.addListSelectionListener(createListSelectionListener(fc));
	list.addMouseListener(createDoubleClickListener(fc, list));
	list.addMouseListener(createSingleClickListener(fc, list));
	getModel().addListDataListener(new ListDataListener() {
	    public void contentsChanged(ListDataEvent e) {
		// Update the selection after JList has been updated
		new DelayedSelectionUpdater();
	    }
	    public void intervalAdded(ListDataEvent e) {
		int i0 = e.getIndex0();
		int i1 = e.getIndex1();
		if (i0 == i1) {
		    File file = (File)getModel().getElementAt(i0);
		    if (file.equals(newFolderFile)) {
			new DelayedSelectionUpdater(file);
			newFolderFile = null;
		    }
		}
	    }
	    public void intervalRemoved(ListDataEvent e) {
	    }
	});

	JScrollPane scrollpane = new JScrollPane(list);
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    Color bg = xp.getColor("listview.fillcolor", null);
	    if (bg != null) {
		list.setBackground(bg);
	    }
	}
	if (listViewBorder != null) {
	    scrollpane.setBorder(listViewBorder);
	}
	p.add(scrollpane, BorderLayout.CENTER);
	return p;
    }

    class DetailsTableModel extends AbstractTableModel implements ListDataListener {
	String[] columnNames = {
	    fileNameHeaderText,
	    fileSizeHeaderText,
	    fileTypeHeaderText,
	    fileDateHeaderText,
	    fileAttrHeaderText
	};
	JFileChooser chooser;
	ListModel listModel;

	DetailsTableModel(JFileChooser fc) {
	    this.chooser = fc;
	    listModel = getModel();
	    listModel.addListDataListener(this);
	}

	public int getRowCount() {
	    return listModel.getSize();
	}

	public int getColumnCount() {
	    return COLUMN_COLCOUNT;
	}

	public String getColumnName(int column) {
	    return columnNames[column];
	}

	public Class getColumnClass(int column) {
	    switch (column) {
	      case COLUMN_FILENAME:
		  return File.class;
	      case COLUMN_FILEDATE:
		  return Date.class;
	      default:
		  return super.getColumnClass(column);
	    }
	}

	public Object getValueAt(int row, int col) {
	    // Note: It is very important to avoid getting info on drives, as
	    // this will trigger "No disk in A:" and similar dialogs.
	    //
	    // Use (f.exists() && !chooser.getFileSystemView().isFileSystemRoot(f)) to
	    // determine if it is safe to call methods directly on f.

	    File f = (File)listModel.getElementAt(row);
	    switch (col) {
	      case COLUMN_FILENAME:
		  return f;

	      case COLUMN_FILESIZE:
		  if (!f.exists() || f.isDirectory()) {
		      return null;
		  }
		  return (f.length() / 1024 + 1) + "KB";

	      case COLUMN_FILETYPE:
		  if (!f.exists()) {
		      return null;
		  }
		  return chooser.getFileSystemView().getSystemTypeDescription(f);

	      case COLUMN_FILEDATE:
		  if (!f.exists() || chooser.getFileSystemView().isFileSystemRoot(f)) {
		      return null;
		  }
		  long time = f.lastModified();
		  return (time == 0L) ? null : new Date(time);

	      case COLUMN_FILEATTR:
		  if (!f.exists() || chooser.getFileSystemView().isFileSystemRoot(f)) {
		      return null;
		  }
		  String attributes = "";
		  if (!f.canWrite()) {
		      attributes += "R";
		  }
		  if (f.isHidden()) {
		      attributes += "H";
		  }
		  return attributes;
	    }
	    return null;
	}

	public void setValueAt(Object value, int row, int col) {
	    if (col == COLUMN_FILENAME) {
		JFileChooser chooser = getFileChooser();
		File f = (File)getValueAt(row, col);
		String oldDisplayName = chooser.getName(f);
		String oldFileName = f.getName();
		String newDisplayName = ((String)value).trim();
		String newFileName;

		if (!newDisplayName.equals(oldDisplayName)) {
		    newFileName = newDisplayName;
		    //Check if extension is hidden from user
		    int i1 = oldFileName.length();
		    int i2 = oldDisplayName.length();
		    if (i1 > i2 && oldFileName.charAt(i2) == '.') {
			newFileName = newDisplayName + oldFileName.substring(i2);
		    }

		    // rename
		    FileSystemView fsv = chooser.getFileSystemView();
		    File f2 = fsv.createFileObject(f.getParentFile(), newFileName);
		    if (!f2.exists() && WindowsFileChooserUI.this.getModel().renameFile(f, f2)) {
			if (fsv.isParent(chooser.getCurrentDirectory(), f2)) {
			    if (chooser.isMultiSelectionEnabled()) {
				chooser.setSelectedFiles(new File[] { f2 });
			    } else {
				chooser.setSelectedFile(f2);
			    }
			} else {
			    //Could be because of delay in updating Desktop folder
			    //chooser.setSelectedFile(null);
			}
		    } else {
			// PENDING(jeff) - show a dialog indicating failure
		    }
		}
	    }
	}

	public boolean isCellEditable(int row, int column) {
	    return (column == COLUMN_FILENAME);
	}

	public void contentsChanged(ListDataEvent e) {
	    fireTableDataChanged();
	}
	public void intervalAdded(ListDataEvent e) {
	    fireTableDataChanged();
	}
	public void intervalRemoved(ListDataEvent e) {
	    fireTableDataChanged();
	}
    }

    class DetailsTableCellRenderer extends DefaultTableCellRenderer {
	JFileChooser chooser;
	DateFormat df;

	DetailsTableCellRenderer(JFileChooser chooser) {
	    this.chooser = chooser;
	    df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
						chooser.getLocale());
	}

	public void setBounds(int x, int y, int width, int height) { 
	    super.setBounds(x, y, Math.min(width, this.getPreferredSize().width+4), height); 
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			      boolean isSelected, boolean hasFocus, int row, int column) {

	    if (column == COLUMN_FILESIZE || column == COLUMN_FILEATTR) {
		setHorizontalAlignment(SwingConstants.TRAILING);
	    } else {
		setHorizontalAlignment(SwingConstants.LEADING);
	    }

	    if (column == COLUMN_FILENAME && table.isRowSelected(row) && table.isFocusOwner()) {
		super.setForeground(table.getSelectionForeground());
		super.setBackground(table.getSelectionBackground());
	    } else {
		super.setForeground(table.getForeground());
		super.setBackground(table.getBackground());
	    }
	    setFont(table.getFont());
	    setValue(value); 

	    return this;
	}

	public void setValue(Object value) { 
	    setIcon(null);
	    if (value instanceof File) {
		File file = (File)value;
		String fileName = chooser.getName(file);
		setText(fileName);
		Icon icon = chooser.getIcon(file);
		setIcon(icon);
	    } else if (value instanceof Date) {
		setText((value == null) ? "" : df.format((Date)value));
	    } else {
		super.setValue(value);
	    }
	}
    }

    protected JPanel createDetailsView(JFileChooser fc) {
	final JFileChooser chooser = fc;
	JPanel p = new JPanel(new BorderLayout());

	DetailsTableModel detailsTableModel = new DetailsTableModel(chooser);

	detailsTable = new JTable(detailsTableModel) {
	    // Handle Escape key events here
	    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && getCellEditor() == null) {
		    // We are not editing, forward to filechooser.
		    chooser.dispatchEvent(e);
		    return true;
		}

		return super.processKeyBinding(ks, e, condition, pressed);
	    }
	};

	detailsTable.setComponentOrientation(chooser.getComponentOrientation());
	detailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	detailsTable.setShowGrid(false);
	detailsTable.setSelectionModel(listSelectionModel); 
	detailsTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);

	Font font = detailsTable.getFont();
	detailsTable.setRowHeight(Math.max(font.getSize(), 19)+3);

	TableColumnModel columnModel = detailsTable.getColumnModel();
	TableColumn[] columns = new TableColumn[COLUMN_COLCOUNT];

	for (int i = 0; i < COLUMN_COLCOUNT; i++) {
	    columns[i] = columnModel.getColumn(i);
	    columns[i].setPreferredWidth(COLUMN_WIDTHS[i]);
	}

	TableCellRenderer cellRenderer = new DetailsTableCellRenderer(chooser);
	detailsTable.setDefaultRenderer(File.class, cellRenderer);
	detailsTable.setDefaultRenderer(Date.class, cellRenderer);
	detailsTable.setDefaultRenderer(Object.class, cellRenderer);

	// Install cell editor for editing file name
	final JTextField tf = new JTextField();
	tf.addFocusListener(editorFocusListener);
	columns[COLUMN_FILENAME].setCellEditor(new DefaultCellEditor(tf) {
	    public boolean isCellEditable(EventObject e) {
		if (e instanceof MouseEvent) { 
		    MouseEvent me = (MouseEvent)e;
		    int index = detailsTable.rowAtPoint(me.getPoint());
		    return (me.getClickCount() == 1 && detailsTable.isRowSelected(index));
		}
		return super.isCellEditable(e);
	    }

	    public Component getTableCellEditorComponent(JTable table, Object value,
							 boolean isSelected, int row, int column) {
		Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		if (value instanceof File) {
		    tf.setText(chooser.getName((File)value));
		    tf.requestFocus();
		    tf.selectAll();
		}		
		return comp;
	    }
	});

	JList fakeList = new JList(detailsTableModel.listModel) {
	    JTable table = detailsTable;

	    public int locationToIndex(Point location) {
		return table.rowAtPoint(location);
	    }

	    public Rectangle getCellBounds(int index0, int index1) {
		Rectangle r0 = table.getCellRect(index0, COLUMN_FILENAME, false);
		Rectangle r1 = table.getCellRect(index1, COLUMN_FILENAME, false);
		return r0.union(r1);
	    }
	    
	    public Object getSelectedValue() {
		return table.getValueAt(table.getSelectedRow(), COLUMN_FILENAME);
	    }

	    public Component add(Component comp) {
		if (comp instanceof JTextField) {
		    return table.add(comp);
		} else {
		    return super.add(comp);
		}
	    }

	    public void repaint() {
		if (table != null)
		    table.repaint();
	    }

	    public TransferHandler getTransferHandler() {
		if (table != null) {
		    return table.getTransferHandler();
		} else {
		    return super.getTransferHandler();
		}
	    }

	    public void setTransferHandler(TransferHandler newHandler) {
		if (table != null) {
		    table.setTransferHandler(newHandler);
		} else {
		    super.setTransferHandler(newHandler);
		}
	    }

	    public boolean getDragEnabled() {
		if (table != null) {
		    return table.getDragEnabled();
		} else {
		    return super.getDragEnabled();
		}
	    }

	    public void setDragEnabled(boolean b) {
		if (table != null) {
		    table.setDragEnabled(b);
		} else {
		    super.setDragEnabled(b);
		}
	    }
	};

	fakeList.setSelectionModel(listSelectionModel);
	detailsTable.addMouseListener(createDoubleClickListener(chooser, fakeList));
	//detailsTable.addMouseListener(createSingleClickListener(chooser, fakeList));

	JScrollPane scrollpane = new JScrollPane(detailsTable);
	scrollpane.setComponentOrientation(chooser.getComponentOrientation());
        LookAndFeel.installColors(scrollpane.getViewport(), "Table.background", "Table.foreground");

	scrollpane.addComponentListener(new ComponentAdapter() {
	    public void componentResized(ComponentEvent e) {
		JScrollPane sp = (JScrollPane)e.getComponent();
		fixNameColumnWidth(sp.getViewport().getSize().width);
		sp.removeComponentListener(this);
	    }
	});

	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    Color bg = xp.getColor("listview.fillcolor", null);
	    if (bg != null) {
		list.setBackground(bg);
	    }
	}
	if (listViewBorder != null) {
	    scrollpane.setBorder(listViewBorder);
	}
	p.add(scrollpane, BorderLayout.CENTER);
	return p;
    }

    private void fixNameColumnWidth(int viewWidth) {
	TableColumn nameCol = detailsTable.getColumnModel().getColumn(COLUMN_FILENAME);
	int tableWidth = detailsTable.getPreferredSize().width;

	if (tableWidth < viewWidth) {
	    nameCol.setPreferredWidth(nameCol.getPreferredWidth() + viewWidth - tableWidth);
	}
    }

    private class DelayedSelectionUpdater implements Runnable {
	File editFile;

	DelayedSelectionUpdater() {
	    this(null);
	}

	DelayedSelectionUpdater(File editFile) {
	    this.editFile = editFile;
	    SwingUtilities.invokeLater(this);
	}

	public void run() {
	    setFileSelected();
	    if (editFile != null) {
		editFileName(getModel().indexOf(editFile));
		editFile = null;
	    }
	}
    }


    /**
     * Creates a selection listener for the list of files and directories.
     *
     * @param fc a <code>JFileChooser</code>
     * @return a <code>ListSelectionListener</code>
     */
    public ListSelectionListener createListSelectionListener(JFileChooser fc) {
	return new SelectionListener() {
	    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
		    JFileChooser chooser = getFileChooser();
		    FileSystemView fsv = chooser.getFileSystemView();
		    JList list = (JList) e.getSource();

		    if (chooser.isMultiSelectionEnabled()) {
			File[] files = null;
			Object[] objects = list.getSelectedValues();
			if (objects != null) {
			    if (objects.length == 1
				&& ((File)objects[0]).isDirectory()
				&& chooser.isTraversable(((File)objects[0]))
				&& (chooser.getFileSelectionMode() == chooser.FILES_ONLY
				    || !fsv.isFileSystem(((File)objects[0])))) {
				setDirectorySelected(true);
				setDirectory(((File)objects[0]));
			    } else {
				files = new File[objects.length];
				int j = 0;
				for (int i = 0; i < objects.length; i++) {
				    File f = (File)objects[i];
				    boolean isDir = f.isDirectory();
				    boolean isFile = ShellFolder.disableFileChooserSpeedFix() ? f.isFile() : !isDir;
				    if ((chooser.isFileSelectionEnabled() && isFile)
					|| (chooser.isDirectorySelectionEnabled()
					    && fsv.isFileSystem(f)
					    && isDir)) {
					files[j++] = f;
				    }
				}
				if (j == 0) {
				    files = null;
				} else if (j < objects.length) {
				    File[] tmpFiles = new File[j];
				    System.arraycopy(files, 0, tmpFiles, 0, j);
				    files = tmpFiles;
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
			    && (chooser.getFileSelectionMode() == chooser.FILES_ONLY
				|| !fsv.isFileSystem(file))) {

			    setDirectorySelected(true);
			    setDirectory(file);
			    chooser.setSelectedFile(null);
			} else {
			    setDirectorySelected(false);

			    if (file != null) {
			       chooser.setSelectedFile(file);
			    }
			}
		    }
		}
	    }
	};
    }

    private MouseListener createSingleClickListener(JFileChooser fc, JList list) {
	return new SingleClickListener(list);
    }

    int lastIndex = -1;
    File editFile = null;
    int editX = 20;

    private int getEditIndex() {
	return lastIndex;
    }

    private void setEditIndex(int i) {
	lastIndex = i;
    }

    private void resetEditIndex() {
	lastIndex = -1;
    }

    private void cancelEdit() {
	if (editFile != null) {
	    editFile = null;
	    list.remove(editCell);
	    centerPanel.repaint();
	} else if (detailsTable != null && detailsTable.isEditing()) {
	    detailsTable.getCellEditor().cancelCellEditing();
	}
    }

    JTextField editCell = null;

    private void editFileName(int index) {
	ensureIndexIsVisible(index);
	if (listViewPanel.isVisible()) {
	    editFile = (File)getModel().getElementAt(index);
	    Rectangle r = list.getCellBounds(index, index);
	    if (editCell == null) {
		editCell = new JTextField();
		editCell.addActionListener(new EditActionListener());
		editCell.addFocusListener(editorFocusListener);
		editCell.setNextFocusableComponent(list);
	    }
	    list.add(editCell);
	    editCell.setText(getFileChooser().getName(editFile));
	    if (list.getComponentOrientation().isLeftToRight()) {
		editCell.setBounds(editX + r.x, r.y, r.width - editX, r.height);
	    } else {
		editCell.setBounds(r.x, r.y, r.width - editX, r.height);
	    }
	    editCell.requestFocus();
	    editCell.selectAll();
	} else if (detailsViewPanel.isVisible()) {
	    detailsTable.editCellAt(index, COLUMN_FILENAME);
	}
    }


    protected class SingleClickListener extends MouseAdapter {
	JList list;

	public  SingleClickListener(JList list) {
	    this.list = list;
	}

	public void mouseClicked(MouseEvent e) {
	    if (SwingUtilities.isLeftMouseButton(e)) {
		if (e.getClickCount() == 1) {
		    JFileChooser fc = getFileChooser();
		    int index = list.locationToIndex(e.getPoint());
		    if ((!fc.isMultiSelectionEnabled() || fc.getSelectedFiles().length <= 1)
			&& index >= 0 && list.isSelectedIndex(index)
			&& getEditIndex() == index && editFile == null) {

			editFileName(index);
		    } else {
			if (index >= 0) {
			    setEditIndex(index);
			} else {
			    resetEditIndex();
			}
		    }
		} else {
		    // on double click (open or drill down one directory) be
		    // sure to clear the edit index
		    resetEditIndex();
		}
	    }	    
	}
    }

    public Action getNewFolderAction() {
	return newFolderAction;
    }

    /**
     * Creates a new folder.
     */
    protected class WindowsNewFolderAction extends NewFolderAction {
	public void actionPerformed(ActionEvent e) {
	    JFileChooser fc = getFileChooser();
	    File oldFile = fc.getSelectedFile();
	    super.actionPerformed(e);
	    File newFile = fc.getSelectedFile();
	    if (newFile != null && !newFile.equals(oldFile) && newFile.isDirectory()) {
		newFolderFile = newFile;
	    }
	}
    }

    class EditActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    applyEdit();
	} 
    }

    private void applyEdit() {
	if (editFile != null && editFile.exists()) {
	    JFileChooser chooser = getFileChooser();
	    String oldDisplayName = chooser.getName(editFile);
	    String oldFileName = editFile.getName();
	    String newDisplayName = editCell.getText().trim();
	    String newFileName;

	    if (!newDisplayName.equals(oldDisplayName)) {
		newFileName = newDisplayName;
		//Check if extension is hidden from user
		int i1 = oldFileName.length();
		int i2 = oldDisplayName.length();
		if (i1 > i2 && oldFileName.charAt(i2) == '.') {
		    newFileName = newDisplayName + oldFileName.substring(i2);
		}

		// rename
		FileSystemView fsv = chooser.getFileSystemView();
		File f2 = fsv.createFileObject(editFile.getParentFile(), newFileName);
		if (!f2.exists() && getModel().renameFile(editFile, f2)) {
		    if (fsv.isParent(chooser.getCurrentDirectory(), f2)) {
			if (chooser.isMultiSelectionEnabled()) {
			    chooser.setSelectedFiles(new File[] { f2 });
			} else {
			    chooser.setSelectedFile(f2);
			}
		    } else {
			//Could be because of delay in updating Desktop folder
			//chooser.setSelectedFile(null);
		    }
		} else {
		    // PENDING(jeff) - show a dialog indicating failure
		}
	    }
	} 
        if (detailsTable != null && detailsTable.isEditing()) {
            detailsTable.getCellEditor().stopCellEditing();
        }
	cancelEdit();
    }

    protected class FileRenderer extends DefaultListCellRenderer  {

	public void setBounds(int x, int y, int width, int height) { 
	    super.setBounds(x, y, Math.min(width, this.getPreferredSize().width+4), height); 
	}

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

    /**
     * Returns the preferred size of the specified
     * <code>JFileChooser</code>.
     * The preferred size is at least as large,
     * in both height and width,
     * as the preferred size recommended
     * by the file chooser's layout manager.
     *
     * @param c  a <code>JFileChooser</code>
     * @return   a <code>Dimension</code> specifying the preferred
     *           width and height of the file chooser
     */
    public Dimension getPreferredSize(JComponent c) {
	int prefWidth = PREF_SIZE.width;
	Dimension d = c.getLayout().preferredLayoutSize(c);
	if (d != null) {
	    return new Dimension(d.width < prefWidth ? prefWidth : d.width,
				 d.height < PREF_SIZE.height ? PREF_SIZE.height : d.height);
	} else {
	    return new Dimension(prefWidth, PREF_SIZE.height);
	}
    }

    /**
     * Returns the minimum size of the <code>JFileChooser</code>.
     *
     * @param c  a <code>JFileChooser</code>
     * @return   a <code>Dimension</code> specifying the minimum
     *           width and height of the file chooser
     */
    public Dimension getMinimumSize(JComponent c) {
	return MIN_SIZE;
    }

    /**
     * Returns the maximum size of the <code>JFileChooser</code>.
     *
     * @param c  a <code>JFileChooser</code>
     * @return   a <code>Dimension</code> specifying the maximum 
     *           width and height of the file chooser
     */
    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    void setFileSelected() {
	if (getFileChooser().isMultiSelectionEnabled() && !isDirectorySelected()) {
	    File[] files = getFileChooser().getSelectedFiles();	// Should be selected
	    Object[] selectedObjects = list.getSelectedValues(); // Are actually selected

	    if (ShellFolder.disableFileChooserSpeedFix()) {
		// Remove files that shouldn't be selected
		for (int j = 0; j < selectedObjects.length; j++) {
		    boolean found = false;
		    for (int i = 0; i < files.length; i++) {
			if (files[i].equals(selectedObjects[j])) {
			    found = true;
			    break;
			}
		    }
		    if (!found) {
			int index = getModel().indexOf(selectedObjects[j]);
			if (index >= 0) {
			    listSelectionModel.removeSelectionInterval(index, index);
			}
		    }
		}
		// Add files that should be selected
		for (int i = 0; i < files.length; i++) {
		    boolean found = false;
		    for (int j = 0; j < selectedObjects.length; j++) {
			if (files[i].equals(selectedObjects[j])) {
			    found = true;
			    break;
			}
		    }
		    if (!found) {
			int index = getModel().indexOf(files[i]);
			if (index >= 0) {
			    listSelectionModel.addSelectionInterval(index, index);
			}
		    }
		}
	    } else {
		listSelectionModel.setValueIsAdjusting(true);
		try {
		    Arrays.sort(files);
		    Arrays.sort(selectedObjects);

		    int shouldIndex = 0;
		    int actuallyIndex = 0;

		    // Remove files that shouldn't be selected and add files which should be selected
		    // Note: Assume files are already sorted in compareTo order.
		    while (shouldIndex < files.length &&
			   actuallyIndex < selectedObjects.length) {
			int comparison = files[shouldIndex].compareTo(selectedObjects[actuallyIndex]);
			if (comparison < 0) {
			    int index = getModel().indexOf(files[shouldIndex]);
			    listSelectionModel.addSelectionInterval(index, index);
			    shouldIndex++;
			} else if (comparison > 0) {
			    int index = getModel().indexOf(selectedObjects[actuallyIndex]);
			    listSelectionModel.removeSelectionInterval(index, index);
			    actuallyIndex++;
			} else {
			    // Do nothing
			    shouldIndex++;
			    actuallyIndex++;
			}

		    }

		    while (shouldIndex < files.length) {
			int index = getModel().indexOf(files[shouldIndex]);
			listSelectionModel.addSelectionInterval(index, index);
			shouldIndex++;
		    }

		    while (actuallyIndex < selectedObjects.length) {
			int index = getModel().indexOf(selectedObjects[actuallyIndex]);
			listSelectionModel.removeSelectionInterval(index, index);
			actuallyIndex++;
		    }
		} finally {
		    listSelectionModel.setValueIsAdjusting(false);
		}
	    }
	} else {
	    JFileChooser chooser = getFileChooser();
	    File f = null;
	    if (isDirectorySelected()) {
		f = getDirectory();
	    } else {
		f = chooser.getSelectedFile();
	    }
	    int i;
	    if (f != null && (i = getModel().indexOf(f)) >= 0) {
		listSelectionModel.setSelectionInterval(i, i);
		ensureIndexIsVisible(i);
	    } else {
		listSelectionModel.clearSelection();
	    }
	}
    }


    private String fileNameString(File file) {
	if (file == null) {
	    return null;
	} else {
	    JFileChooser fc = getFileChooser();
	    if (fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()) {
		return file.getPath();
	    } else {
		return file.getName();
	    }
	}
    }

    private String fileNameString(File[] files) {
	StringBuffer buf = new StringBuffer();
	for (int i = 0; files != null && i < files.length; i++) {
	    if (i > 0) {
		buf.append(" ");
	    }
	    if (files.length > 1) {
		buf.append("\"");
	    }
	    buf.append(fileNameString(files[i]));
	    if (files.length > 1) {
		buf.append("\"");
	    }
	}
	return buf.toString();
    }

    /* The following methods are used by the PropertyChange Listener */

    private void doSelectedFileChanged(PropertyChangeEvent e) {
	applyEdit();
	File f = (File) e.getNewValue();
	JFileChooser fc = getFileChooser();
	if (f != null 
	    && ((fc.isFileSelectionEnabled() && !f.isDirectory())
		|| (f.isDirectory() && fc.isDirectorySelectionEnabled()))) {

	    setFileName(fileNameString(f));
	    setFileSelected();
	}
    }
    
    private void doSelectedFilesChanged(PropertyChangeEvent e) {
	applyEdit();
	File[] files = (File[]) e.getNewValue();
	JFileChooser fc = getFileChooser();
	if (files != null
	    && files.length > 0
	    && (files.length > 1 || fc.isDirectorySelectionEnabled() || !files[0].isDirectory())) {
	    setFileName(fileNameString(files));
	    setFileSelected();
	}
    }
    
    private void doDirectoryChanged(PropertyChangeEvent e) {
	JFileChooser fc = getFileChooser();
	FileSystemView fsv = fc.getFileSystemView();

	applyEdit();
	resetEditIndex();
	clearIconCache();
	listSelectionModel.clearSelection();
	ensureIndexIsVisible(0);
	File currentDirectory = fc.getCurrentDirectory();
	if (shortCutPanel != null) {
	    shortCutPanel.doDirectoryChanged(currentDirectory);
	}
	if(currentDirectory != null) {
	    directoryComboBoxModel.addItem(currentDirectory);
	    getNewFolderAction().setEnabled(currentDirectory.canWrite());
	    getChangeToParentDirectoryAction().setEnabled(!fsv.isRoot(currentDirectory));

	    if (fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()) {
		if (fsv.isFileSystem(currentDirectory)) {
		    setFileName(currentDirectory.getPath());
		} else {
		    setFileName(null);
		}
	    }
	}
    }

    private void doFilterChanged(PropertyChangeEvent e) {
	applyEdit();
	resetEditIndex();
	clearIconCache();
	listSelectionModel.clearSelection();
    }

    private void doFileSelectionModeChanged(PropertyChangeEvent e) {
	applyEdit();
	resetEditIndex();
	clearIconCache();
	listSelectionModel.clearSelection();

	JFileChooser fc = getFileChooser();
	File currentDirectory = fc.getCurrentDirectory();
	if (currentDirectory != null
	    && fc.isDirectorySelectionEnabled()
	    && !fc.isFileSelectionEnabled()
	    && fc.getFileSystemView().isFileSystem(currentDirectory)) {

	    setFileName(currentDirectory.getPath());
	} else {
	    setFileName(null);
	}
    }

    private void doMultiSelectionChanged(PropertyChangeEvent e) {
	if (getFileChooser().isMultiSelectionEnabled()) {
	    listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	} else {
	    listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    listSelectionModel.clearSelection();
	    getFileChooser().setSelectedFiles(null);
	}
    }
    
    private void doAccessoryChanged(PropertyChangeEvent e) {
	if(getAccessoryPanel() != null) {
	    if(e.getOldValue() != null) {
		getAccessoryPanel().remove((JComponent) e.getOldValue());
	    }
	    JComponent accessory = (JComponent) e.getNewValue();
	    if(accessory != null) {
		getAccessoryPanel().add(accessory, BorderLayout.CENTER);
	    }
	}
    }

    private void doApproveButtonTextChanged(PropertyChangeEvent e) {
	JFileChooser chooser = getFileChooser();
	approveButton.setText(getApproveButtonText(chooser));
	approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
	approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
    }

    private void doDialogTypeChanged(PropertyChangeEvent e) {
	JFileChooser chooser = getFileChooser();
	approveButton.setText(getApproveButtonText(chooser));
	approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
	approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
	if (chooser.getDialogType() == JFileChooser.SAVE_DIALOG) {
	    lookInLabel.setText(saveInLabelText);
	} else {
	    lookInLabel.setText(lookInLabelText);
	}
    }

    private void doApproveButtonMnemonicChanged(PropertyChangeEvent e) {
	approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
    }

    private void doControlButtonsChanged(PropertyChangeEvent e) {
	if(getFileChooser().getControlButtonsAreShown()) {
	    addControlButtons();
	} else {
	    removeControlButtons();
	}
    }

    /*
     * Listen for filechooser property changes, such as
     * the selected file changing, or the type of the dialog changing.
     */
    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
	return new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent e) {
		String s = e.getPropertyName();
		if(s.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
		    doSelectedFileChanged(e);
		} else if (s.equals(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY)) {
		    doSelectedFilesChanged(e);
		} else if(s.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
		    doDirectoryChanged(e);
		} else if(s.equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
		    doFilterChanged(e);
		} else if(s.equals(JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY)) {
		    doFileSelectionModeChanged(e);
		} else if(s.equals(JFileChooser.MULTI_SELECTION_ENABLED_CHANGED_PROPERTY)) {
		    doMultiSelectionChanged(e);
		} else if(s.equals(JFileChooser.ACCESSORY_CHANGED_PROPERTY)) {
		    doAccessoryChanged(e);
		} else if (s.equals(JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY) ||
			   s.equals(JFileChooser.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY)) { 
		    doApproveButtonTextChanged(e);
		} else if(s.equals(JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY)) {
		    doDialogTypeChanged(e);
		} else if(s.equals(JFileChooser.APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY)) {
		    doApproveButtonMnemonicChanged(e);
		} else if(s.equals(JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY)) {
		    doControlButtonsChanged(e);
		} else if (s.equals("componentOrientation")) {
		    ComponentOrientation o = (ComponentOrientation)e.getNewValue();
		    JFileChooser cc = (JFileChooser)e.getSource();
		    if (o != (ComponentOrientation)e.getOldValue()) {
			cc.applyComponentOrientation(o);
		    }
		    if (detailsTable != null) {
			detailsTable.setComponentOrientation(o);
			detailsTable.getParent().getParent().setComponentOrientation(o);
		    }
		} else if (s.equals("ancestor")) {
		    if (e.getOldValue() == null && e.getNewValue() != null) {
			// Ancestor was added, set initial focus
			filenameTextField.selectAll();
			filenameTextField.requestFocus();
		    }
		}
	    }
	};
    }


    protected void removeControlButtons() {
	getBottomPanel().remove(getButtonPanel());
    }

    protected void addControlButtons() {
	getBottomPanel().add(getButtonPanel());
    }

    private void ensureIndexIsVisible(int i) {
	if (i >= 0) {
	    list.ensureIndexIsVisible(i);
	    if (detailsTable != null) {
		detailsTable.scrollRectToVisible(detailsTable.getCellRect(i, COLUMN_FILENAME, true));
	    }
	}
    }

    public void ensureFileIsVisible(JFileChooser fc, File f) {
	ensureIndexIsVisible(getModel().indexOf(f));
    }

    public void rescanCurrentDirectory(JFileChooser fc) {
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

    /**
     * Property to remember whether a directory is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param directorySelected if a directory is currently selected.
     * @since 1.4
     */
    protected void setDirectorySelected(boolean directorySelected) {
	super.setDirectorySelected(directorySelected);
	JFileChooser chooser = getFileChooser();
	if(directorySelected) {
	    approveButton.setText(directoryOpenButtonText);
	    approveButton.setToolTipText(directoryOpenButtonToolTipText);
	    approveButton.setMnemonic(directoryOpenButtonMnemonic);
	} else {
	    approveButton.setText(getApproveButtonText(chooser));
	    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
	    approveButton.setMnemonic(getApproveButtonMnemonic(chooser));
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

	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    if (value == null) {
		setText("");
		return this;
	    }
	    File directory = (File)value;
	    setText(getFileChooser().getName(directory));
	    Icon icon = getFileChooser().getIcon(directory);
	    ii.icon = icon;
	    ii.depth = directoryComboBoxModel.getDepth(index);
	    setIcon(ii);

	    return this;
	}
    }

    final static int space = 10;
    class IndentIcon implements Icon {

	Icon icon = null;
	int depth = 0;

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    if (c.getComponentOrientation().isLeftToRight()) {
		icon.paintIcon(c, g, x+depth*space, y);
	    } else {
		icon.paintIcon(c, g, x, y);
	    }
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
	int[] depths = null;
	File selectedDirectory = null;
	JFileChooser chooser = getFileChooser();
	FileSystemView fsv = chooser.getFileSystemView();

	public DirectoryComboBoxModel() {
	    // Add the current directory to the model, and make it the
	    // selectedDirectory
	    File dir = getFileChooser().getCurrentDirectory();
	    if(dir != null) {
		addItem(dir);
	    }
	}

	/**
	 * Adds the directory to the model and sets it to be selected,
	 * additionally clears out the previous selected directory and
	 * the paths leading up to it, if any.
	 */
	private void addItem(File directory) {

	    if(directory == null) {
		return;
	    }

	    directories.clear();

	    File[] baseFolders;
	    if (useShellFolder) {
		baseFolders = (File[])ShellFolder.get("fileChooserComboBoxFolders");
	    } else {
		baseFolders = fsv.getRoots();
	    }
	    directories.addAll(Arrays.asList(baseFolders));

	    // Get the canonical (full) path. This has the side
	    // benefit of removing extraneous chars from the path,
	    // for example /foo/bar/ becomes /foo/bar
	    File canonical = null;
	    try {
		canonical = directory.getCanonicalFile();
	    } catch (IOException e) {
		// Maybe drive is not ready. Can't abort here.
		canonical = directory;
	    }

	    // create File instances of each directory leading up to the top
	    try {
		File sf = ShellFolder.getShellFolder(canonical);
		File f = sf;
		Vector path = new Vector(10);
		do {
		    path.addElement(f);
		} while ((f = f.getParentFile()) != null);

		int pathCount = path.size();
		// Insert chain at appropriate place in vector
		for (int i = 0; i < pathCount; i++) {
		    f = (File)path.get(i);
		    if (directories.contains(f)) {
			int topIndex = directories.indexOf(f);
			for (int j = i-1; j >= 0; j--) {
			    directories.insertElementAt(path.get(j), topIndex+i-j);
			}
			break;
		    }
		}
		calculateDepths();
		setSelectedItem(sf);
	    } catch (FileNotFoundException ex) {
		calculateDepths();
	    }
	}

	private void calculateDepths() {
	    depths = new int[directories.size()];
	    for (int i = 0; i < depths.length; i++) {
		File dir = (File)directories.get(i);
		File parent = dir.getParentFile();
		depths[i] = 0;
		if (parent != null) {
		    for (int j = i-1; j >= 0; j--) {
			if (parent.equals((File)directories.get(j))) {
			    depths[i] = depths[j] + 1;
			    break;
			}
		    }
		}
	    }
	}

	public int getDepth(int i) {
	    return (depths != null && i >= 0 && i < depths.length) ? depths[i] : 0;
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
	    } else if (prop == JFileChooser.FILE_FILTER_CHANGED_PROPERTY) {
		fireContentsChanged(this, -1, -1);
	    }
	}

	public void setSelectedItem(Object filter) {
	    if(filter != null) {
		getFileChooser().setFileFilter((FileFilter) filter);
		setFileName(null);
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
	JFileChooser fc = getFileChooser();
	File f = fc.getSelectedFile();
	if (!e.getValueIsAdjusting() && f != null && !getFileChooser().isTraversable(f)) {
	    setFileName(fileNameString(f));
	}
    }

    /**
     * Acts when DirectoryComboBox has changed the selected item.
     */
    protected class DirectoryComboBoxAction implements ActionListener {




	public void actionPerformed(ActionEvent e) {
	    File f = (File)directoryComboBox.getSelectedItem();
	    getFileChooser().setCurrentDirectory(f);
	}
    }

    protected JButton getApproveButton(JFileChooser fc) {
	return approveButton;
    }

    public FileView getFileView(JFileChooser fc) {
	return fileView;
    }

    // ***********************
    // * FileView operations *
    // ***********************
    protected class WindowsFileView extends BasicFileView {
	/* FileView type descriptions */

	public Icon getIcon(File f) {
	    Icon icon = getCachedIcon(f);
	    if (icon != null) {
		return icon;
	    }
	    if (f != null) {
		icon = getFileChooser().getFileSystemView().getSystemIcon(f);
	    }
	    if (icon == null) {
		icon = super.getIcon(f);
	    }
	    cacheIcon(f, icon);
	    return icon;
	}
    }
}

