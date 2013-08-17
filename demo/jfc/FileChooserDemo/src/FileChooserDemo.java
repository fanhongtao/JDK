/*
 * @(#)FileChooserDemo.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.io.File;
import java.awt.event.*;
import java.beans.*;


/**
 *
 * A demo which makes extensive use of the file chooser.
 *
 * 1.10 11/29/01
 * @author Jeff Dinkins
 */
public class FileChooserDemo extends JPanel implements ActionListener {
    static JFrame frame;

    static String metal= "Metal";
    static String metalClassName = "javax.swing.plaf.metal.MetalLookAndFeel";

    static String motif = "Motif";
    static String motifClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

    static String windows = "Windows";
    static String windowsClassName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";


    JButton button;
    JCheckBox useFileViewButton, accessoryButton, hiddenButton, showFullDescriptionButton;
    JButton noAllFilesFilterButton, yesAllFilesFilterButton;
    JRadioButton addFiltersButton, defaultFiltersButton;
    JRadioButton openButton, saveButton, customButton;
    JRadioButton metalButton, motifButton, windowsButton;
    JRadioButton justFilesButton, justDirectoriesButton, bothFilesAndDirectoriesButton;

    JTextField customField;

    ExampleFileFilter jpgFilter, gifFilter, bothFilter;
    ExampleFileView fileView;

    JPanel buttonPanel;

    public final static Dimension hpad10 = new Dimension(10,1);
    public final static Dimension vpad10 = new Dimension(1,10);

    FilePreviewer previewer;
    JFileChooser chooser;

    public FileChooserDemo() {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	chooser = new JFileChooser();
	previewer = new FilePreviewer(chooser);
	chooser.setAccessory(previewer);

	jpgFilter = new ExampleFileFilter("jpg", "JPEG Compressed Image Files");
	gifFilter = new ExampleFileFilter("gif", "GIF Image Files");
	bothFilter = new ExampleFileFilter(new String[] {"jpg", "gif"}, "JPEG and GIF Image Files");

	fileView = new ExampleFileView();
	fileView.putIcon("jpg", new ImageIcon("images/jpgIcon.jpg"));
	fileView.putIcon("gif", new ImageIcon("images/gifIcon.gif"));

	chooser.setAccessory(previewer);
	chooser.setFileView(fileView);

	// create a radio listener to listen to option changes
	OptionListener optionListener = new OptionListener();

	// Create options
	openButton = new JRadioButton("Open");
	openButton.setSelected(true);
	openButton.addActionListener(optionListener);

	saveButton = new JRadioButton("Save");
	saveButton.addActionListener(optionListener);

	customButton = new JRadioButton("Custom");
	customButton.addActionListener(optionListener);

	customField = new JTextField("Doit");
	customField.setAlignmentY(JComponent.TOP_ALIGNMENT);
	customField.setEnabled(false);
	customField.addActionListener(optionListener);

	ButtonGroup group1 = new ButtonGroup();
	group1.add(openButton);
	group1.add(saveButton);
	group1.add(customButton);

	// filter buttons
	noAllFilesFilterButton = new JButton("Remove \"All Files\" Filter");
	noAllFilesFilterButton.addActionListener(optionListener);

	yesAllFilesFilterButton = new JButton("Add \"All Files\" Filter");
	yesAllFilesFilterButton.addActionListener(optionListener);

	defaultFiltersButton = new JRadioButton("Default Filtering");
	defaultFiltersButton.setSelected(true);
	defaultFiltersButton.addActionListener(optionListener);

	addFiltersButton = new JRadioButton("Add JPG and GIF Filters");
	addFiltersButton.addActionListener(optionListener);

	ButtonGroup group2 = new ButtonGroup();
	group2.add(addFiltersButton);
	group2.add(defaultFiltersButton);

	accessoryButton = new JCheckBox("Show Preview");
	accessoryButton.addActionListener(optionListener);
	accessoryButton.setSelected(true);

	// more options
	hiddenButton = new JCheckBox("Show Hidden Files");
	hiddenButton.addActionListener(optionListener);

	showFullDescriptionButton = new JCheckBox("Show Extensions");
	showFullDescriptionButton.addActionListener(optionListener);
	showFullDescriptionButton.setSelected(true);

	useFileViewButton = new JCheckBox("Use FileView");
	useFileViewButton.addActionListener(optionListener);
	useFileViewButton.setSelected(true);

	// File or Directory chooser options
	ButtonGroup group3 = new ButtonGroup();
	justFilesButton = new JRadioButton("Just Select Files");
	justFilesButton.setSelected(true);
	group3.add(justFilesButton);
	justFilesButton.addActionListener(optionListener);

	justDirectoriesButton = new JRadioButton("Just Select Directories");
	group3.add(justDirectoriesButton);
	justDirectoriesButton.addActionListener(optionListener);

	bothFilesAndDirectoriesButton = new JRadioButton("Select Files or Directories");
	group3.add(bothFilesAndDirectoriesButton);
	bothFilesAndDirectoriesButton.addActionListener(optionListener);

	// Create show button
	button = new JButton("Show FileChooser");
	button.addActionListener(this);
        button.setMnemonic('s');

	// Create laf buttons.
	metalButton = new JRadioButton(metal);
        metalButton.setMnemonic('o');
	metalButton.setActionCommand(metalClassName);

	motifButton = new JRadioButton(motif);
        motifButton.setMnemonic('m');
	motifButton.setActionCommand(motifClassName);

	windowsButton = new JRadioButton(windows);
        windowsButton.setMnemonic('w');
	windowsButton.setActionCommand(windowsClassName);

	ButtonGroup group4 = new ButtonGroup();
	group4.add(metalButton);
	group4.add(motifButton);
	group4.add(windowsButton);

        // Register a listener for the laf buttons.
	metalButton.addActionListener(optionListener);
	motifButton.addActionListener(optionListener);
	windowsButton.addActionListener(optionListener);

	JPanel control1 = new JPanel();
	control1.setLayout(new BoxLayout(control1, BoxLayout.X_AXIS));
	control1.add(Box.createRigidArea(hpad10));
	control1.add(openButton);
	control1.add(Box.createRigidArea(hpad10));
	control1.add(saveButton);
	control1.add(Box.createRigidArea(hpad10));
	control1.add(customButton);
	control1.add(customField);
	control1.add(Box.createRigidArea(hpad10));

	JPanel control2 = new JPanel();
	control2.setLayout(new BoxLayout(control2, BoxLayout.X_AXIS));
	control2.add(Box.createRigidArea(hpad10));
	control2.add(noAllFilesFilterButton);
	control2.add(Box.createRigidArea(hpad10));
	control2.add(yesAllFilesFilterButton);
	control2.add(Box.createRigidArea(hpad10));
	control2.add(defaultFiltersButton);
	control2.add(Box.createRigidArea(hpad10));
	control2.add(addFiltersButton);
	control2.add(Box.createRigidArea(hpad10));
	control2.add(accessoryButton);
	control2.add(Box.createRigidArea(hpad10));

	JPanel control3 = new JPanel();
	control3.setLayout(new BoxLayout(control3, BoxLayout.X_AXIS));
	control3.add(Box.createRigidArea(hpad10));
	control3.add(hiddenButton);
	control3.add(Box.createRigidArea(hpad10));
	control3.add(showFullDescriptionButton);
	control3.add(Box.createRigidArea(hpad10));
	control3.add(useFileViewButton);
	control3.add(Box.createRigidArea(hpad10));

	JPanel control4 = new JPanel();
	control4.setLayout(new BoxLayout(control4, BoxLayout.X_AXIS));
	control4.add(Box.createRigidArea(hpad10));
	control4.add(justFilesButton);
	control4.add(Box.createRigidArea(hpad10));
	control4.add(justDirectoriesButton);
	control4.add(Box.createRigidArea(hpad10));
	control4.add(bothFilesAndDirectoriesButton);
	control4.add(Box.createRigidArea(hpad10));

	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	panel.add(Box.createRigidArea(hpad10));
	panel.add(button);
	panel.add(Box.createRigidArea(hpad10));
	panel.add(metalButton);
	panel.add(Box.createRigidArea(hpad10));
	panel.add(motifButton);
	panel.add(Box.createRigidArea(hpad10));
	panel.add(windowsButton);
	panel.add(Box.createRigidArea(hpad10));

	add(Box.createRigidArea(vpad10));
	add(control1);
	add(Box.createRigidArea(vpad10));
	add(control2);
	add(Box.createRigidArea(vpad10));
	add(control3);
	add(Box.createRigidArea(vpad10));
	add(control4);
	add(Box.createRigidArea(vpad10));
	add(Box.createRigidArea(vpad10));
	add(panel);
	add(Box.createRigidArea(vpad10));
    }

    public void actionPerformed(ActionEvent e) {
	int retval = chooser.showDialog(frame, null);
	if(retval == JFileChooser.APPROVE_OPTION) {
	    File theFile = chooser.getSelectedFile();
	    if(theFile != null) {
		if(theFile.isDirectory()) {
		    JOptionPane.showMessageDialog(
			frame, "You chose this directory: " +
			chooser.getSelectedFile().getAbsolutePath()
		    );
		} else {
		    JOptionPane.showMessageDialog(
			frame, "You chose this file: " +
			chooser.getSelectedFile().getAbsolutePath()
		    );
		}
		return;
	    }
	}
	JOptionPane.showMessageDialog(frame, "No file was chosen.");
    }

    /** An ActionListener that listens to the radio buttons. */
    class OptionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    JComponent c = (JComponent) e.getSource();
	    if(c == openButton) {
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		customField.setEnabled(false);
		repaint();
	    } else if (c == saveButton) {
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		customField.setEnabled(false);
		repaint();
	    } else if (c == customButton || c == customField) {
		customField.setEnabled(true);
		chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
		chooser.setApproveButtonText(customField.getText());
		repaint();
	    } else if(c == noAllFilesFilterButton) {
		// chooser.setAcceptAllFileFilterUsed(false);
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
	    } else if(c == yesAllFilesFilterButton) {
		// chooser.setAcceptAllFileFilterUsed(true);
		chooser.addChoosableFileFilter(chooser.getAcceptAllFileFilter());
	    } else if(c == defaultFiltersButton) {
		chooser.resetChoosableFileFilters();
	    } else if(c == addFiltersButton) {
		chooser.addChoosableFileFilter(bothFilter);
		chooser.addChoosableFileFilter(jpgFilter);
		chooser.addChoosableFileFilter(gifFilter);
	    } else if(c == hiddenButton) {
		chooser.setFileHidingEnabled(!hiddenButton.isSelected());
	    } else if(c == accessoryButton) {
		if(accessoryButton.isSelected()) {
		    chooser.setAccessory(previewer);
		} else {
		    chooser.setAccessory(null);
		}
	    } else if(c == useFileViewButton) {
		if(useFileViewButton.isSelected()) {
		    chooser.setFileView(fileView);
		} else {
		    chooser.setFileView(null);
		}
	    } else if(c == showFullDescriptionButton) {
		jpgFilter.setExtensionListInDescription(showFullDescriptionButton.isSelected());
		gifFilter.setExtensionListInDescription(showFullDescriptionButton.isSelected());
		bothFilter.setExtensionListInDescription(showFullDescriptionButton.isSelected());
		if(addFiltersButton.isSelected()) {
		    chooser.resetChoosableFileFilters();
		    chooser.addChoosableFileFilter(bothFilter);
		    chooser.addChoosableFileFilter(jpgFilter);
		    chooser.setFileFilter(gifFilter);
		}
	    } else if(c == justFilesButton) {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    } else if(c == justDirectoriesButton) {
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    } else if(c == bothFilesAndDirectoriesButton) {
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    } else {
		String lnfName = e.getActionCommand();

		try {
		    UIManager.setLookAndFeel(lnfName);
		    SwingUtilities.updateComponentTreeUI(frame);
		    if(chooser != null) {
			SwingUtilities.updateComponentTreeUI(chooser);
		    }
		    frame.pack();
		} catch (UnsupportedLookAndFeelException exc) {
		    System.out.println("Unsupported L&F Error:" + exc);
		    JRadioButton button = (JRadioButton)e.getSource();
		    button.setEnabled(false);
		    updateState();
		} catch (IllegalAccessException exc) {
		    System.out.println("IllegalAccessException Error:" + exc);
		} catch (ClassNotFoundException exc) {
		    System.out.println("ClassNotFoundException Error:" + exc);
		} catch (InstantiationException exc) {
		    System.out.println("InstantiateException Error:" + exc);
		}
	    }

	}
    }

    public void updateState() {
	String lnfName = UIManager.getLookAndFeel().getClass().getName();
	if (lnfName.indexOf(metal) >= 0) {
	    metalButton.setSelected(true);
	} else if (lnfName.indexOf(windows) >= 0) {
	    windowsButton.setSelected(true);
	} else if (lnfName.indexOf(motif) >= 0) {
	    motifButton.setSelected(true);
	} else {
	    System.err.println("FileChooserDemo if using an unknown L&F: " + lnfName);
	}
    }

    class FilePreviewer extends JComponent implements PropertyChangeListener {
	ImageIcon thumbnail = null;
	File f = null;

	public FilePreviewer(JFileChooser fc) {
	    setPreferredSize(new Dimension(100, 50));
	    fc.addPropertyChangeListener(this);
	}

	public void loadImage() {
	    if(f != null) {
		ImageIcon tmpIcon = new ImageIcon(f.getPath());
		if(tmpIcon.getIconWidth() > 90) {
		    thumbnail = new ImageIcon(
			tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
		} else {
		    thumbnail = tmpIcon;
		}
	    }
	}

	public void propertyChange(PropertyChangeEvent e) {
	    String prop = e.getPropertyName();
	    if(prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
		f = (File) e.getNewValue();
		if(isShowing()) {
		    loadImage();
		    repaint();
		}
	    }
	}

	public void paint(Graphics g) {
	    if(thumbnail == null) {
		loadImage();
	    }
	    if(thumbnail != null) {
		int x = getWidth()/2 - thumbnail.getIconWidth()/2;
		int y = getHeight()/2 - thumbnail.getIconHeight()/2;
		if(y < 0) {
		    y = 0;
		}

		if(x < 5) {
		    x = 5;
		}
		thumbnail.paintIcon(this, g, x, y);
	    }
	}
    }

    public static void main(String s[]) {
	/*
	   NOTE: By default, the look and feel will be set to the
	   Cross Platform Look and Feel (which is currently Metal).
	   The user may someday be able to override the default
	   via a system property. If you as the developer want to
	   be sure that a particular L&F is set, you can do so
	   by calling UIManager.setLookAndFeel(). For example, the
	   first code snippet below forcibly sets the UI to be the
	   System Look and Feel. The second code snippet forcibly
	   sets the look and feel to the Cross Platform L&F.

	   Snippet 1:
	   try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	   } catch (Exception exc) {
	      System.err.println("Error loading L&F: " + exc);
	   }

	   Snippet 2:
	   try {
	      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	   } catch (Exception exc) {
	      System.err.println("Error loading L&F: " + exc);
	   }
	*/

	try {
	    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	} catch (Exception exc) {
	    System.err.println("Error loading L&F: " + exc);
	}

	FileChooserDemo panel = new FileChooserDemo();

	frame = new JFrame("FileChooserDemo");
	frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	});
	frame.getContentPane().add("Center", panel);
	frame.pack();
	frame.setVisible(true);

	panel.updateState();
    }
}
