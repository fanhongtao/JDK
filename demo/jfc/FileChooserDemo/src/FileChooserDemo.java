/*
 * @(#)FileChooserDemo.java	1.12 99/10/13
 *
 * Copyright (c) 1998, 1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
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
 * 1.12 10/13/99
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

    JButton showButton;

    JButton noAllFilesFilterButton;
    JButton yesAllFilesFilterButton;

    JCheckBox useFileViewCheckBox;
    JCheckBox accessoryCheckBox;
    JCheckBox setHiddenCheckBox;
    JCheckBox showFullDescriptionCheckBox;
    JCheckBox useControlsCheckBox;

    JRadioButton singleSelectionRadioButton;
    JRadioButton multiSelectionRadioButton;

    JRadioButton addFiltersRadioButton;
    JRadioButton defaultFiltersRadioButton;

    JRadioButton openRadioButton;
    JRadioButton saveRadioButton;
    JRadioButton customButton;

    JRadioButton metalRadioButton;
    JRadioButton motifRadioButton;
    JRadioButton windowsRadioButton;

    JRadioButton justFilesRadioButton;
    JRadioButton justDirectoriesRadioButton;
    JRadioButton bothFilesAndDirectoriesRadioButton;

    JTextField customField;

    ExampleFileFilter jpgFilter, gifFilter, bothFilter;
    ExampleFileView fileView;

    JPanel buttonPanel;

    public final static Dimension hpad10 = new Dimension(10,1);
    public final static Dimension vpad10 = new Dimension(1,10);
    public final static Insets insets = new Insets(5,10, 0, 10);

    FilePreviewer previewer;
    JFileChooser chooser;

    public FileChooserDemo() {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	chooser = new JFileChooser();
	previewer = new FilePreviewer(chooser);
	chooser.setAccessory(previewer);

	// Create Filters
	jpgFilter = new ExampleFileFilter("jpg", "JPEG Compressed Image Files");
	gifFilter = new ExampleFileFilter("gif", "GIF Image Files");
	bothFilter = new ExampleFileFilter(new String[] {"jpg", "gif"}, "JPEG and GIF Image Files");

	// Create Custom FileView
	fileView = new ExampleFileView();
	fileView.putIcon("jpg", new ImageIcon("images/jpgIcon.jpg"));
	fileView.putIcon("gif", new ImageIcon("images/gifIcon.gif"));

	chooser.setAccessory(previewer);
	chooser.setFileView(fileView);

	// create a radio listener to listen to option changes
	OptionListener optionListener = new OptionListener();

	// Create options
	openRadioButton = new JRadioButton("Open");
	openRadioButton.setSelected(true);
	openRadioButton.addActionListener(optionListener);

	saveRadioButton = new JRadioButton("Save");
	saveRadioButton.addActionListener(optionListener);

	customButton = new JRadioButton("Custom");
	customButton.addActionListener(optionListener);

	customField = new JTextField() {
	    public Dimension getMaximumSize() {
		return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
	    }
	};
	customField.setText("Doit");
	customField.setAlignmentY(JComponent.TOP_ALIGNMENT);
	customField.setEnabled(false);
	customField.addActionListener(optionListener);

	ButtonGroup group1 = new ButtonGroup();
	group1.add(openRadioButton);
	group1.add(saveRadioButton);
	group1.add(customButton);

	// filter buttons
	noAllFilesFilterButton = new JButton("Remove \"All Files\" Filter");
	noAllFilesFilterButton.addActionListener(optionListener);

	yesAllFilesFilterButton = new JButton("Add \"All Files\" Filter");
	yesAllFilesFilterButton.addActionListener(optionListener);

	defaultFiltersRadioButton = new JRadioButton("Default Filtering");
	defaultFiltersRadioButton.setSelected(true);
	defaultFiltersRadioButton.addActionListener(optionListener);

	addFiltersRadioButton = new JRadioButton("Add JPG and GIF Filters");
	addFiltersRadioButton.addActionListener(optionListener);

	ButtonGroup group2 = new ButtonGroup();
	group2.add(addFiltersRadioButton);
	group2.add(defaultFiltersRadioButton);

	accessoryCheckBox = new JCheckBox("Show Preview");
	accessoryCheckBox.addActionListener(optionListener);
	accessoryCheckBox.setSelected(true);

	// more options
	setHiddenCheckBox = new JCheckBox("Show Hidden Files");
	setHiddenCheckBox.addActionListener(optionListener);

	showFullDescriptionCheckBox = new JCheckBox("Show Extensions");
	showFullDescriptionCheckBox.addActionListener(optionListener);
	showFullDescriptionCheckBox.setSelected(true);

	useFileViewCheckBox = new JCheckBox("Use FileView");
	useFileViewCheckBox.addActionListener(optionListener);
	useFileViewCheckBox.setSelected(true);

	useControlsCheckBox = new JCheckBox("Show Control Buttons");
	useControlsCheckBox.addActionListener(optionListener);
	useControlsCheckBox.setSelected(true);

	// File or Directory chooser options
	ButtonGroup group3 = new ButtonGroup();
	justFilesRadioButton = new JRadioButton("Just Select Files");
	justFilesRadioButton.setSelected(true);
	group3.add(justFilesRadioButton);
	justFilesRadioButton.addActionListener(optionListener);

	justDirectoriesRadioButton = new JRadioButton("Just Select Directories");
	group3.add(justDirectoriesRadioButton);
	justDirectoriesRadioButton.addActionListener(optionListener);

	bothFilesAndDirectoriesRadioButton = new JRadioButton("Select Files or Directories");
	group3.add(bothFilesAndDirectoriesRadioButton);
	bothFilesAndDirectoriesRadioButton.addActionListener(optionListener);

	singleSelectionRadioButton = new JRadioButton("Single Selection", true);
	singleSelectionRadioButton.addActionListener(optionListener);

	multiSelectionRadioButton = new JRadioButton("Multi Selection");
	multiSelectionRadioButton.addActionListener(optionListener);

	ButtonGroup group4 = new ButtonGroup();
	group4.add(singleSelectionRadioButton);
	group4.add(multiSelectionRadioButton);


	// Create show button
	showButton = new JButton("Show FileChooser");
	showButton.addActionListener(this);
        showButton.setMnemonic('s');

	// Create laf buttons.
	metalRadioButton = new JRadioButton(metal);
        metalRadioButton.setMnemonic('o');
	metalRadioButton.setActionCommand(metalClassName);

	motifRadioButton = new JRadioButton(motif);
        motifRadioButton.setMnemonic('m');
	motifRadioButton.setActionCommand(motifClassName);

	windowsRadioButton = new JRadioButton(windows);
        windowsRadioButton.setMnemonic('w');
	windowsRadioButton.setActionCommand(windowsClassName);

	ButtonGroup group5 = new ButtonGroup();
	group5.add(metalRadioButton);
	group5.add(motifRadioButton);
	group5.add(windowsRadioButton);

        // Register a listener for the laf buttons.
	metalRadioButton.addActionListener(optionListener);
	motifRadioButton.addActionListener(optionListener);
	windowsRadioButton.addActionListener(optionListener);

	// ********************************************************
	// ******************** Dialog Type ***********************
	// ********************************************************
	JPanel control1 = new InsetPanel(insets);
	control1.setBorder(BorderFactory.createTitledBorder("Dialog Type"));

	control1.setLayout(new BoxLayout(control1, BoxLayout.Y_AXIS));
	control1.add(Box.createRigidArea(vpad10));
	control1.add(openRadioButton);
	control1.add(Box.createRigidArea(vpad10));
	control1.add(saveRadioButton);
	control1.add(Box.createRigidArea(vpad10));
	control1.add(customButton);
	control1.add(Box.createRigidArea(vpad10));
	control1.add(customField);
	control1.add(Box.createGlue());

	// ********************************************************
	// ***************** Filter Controls **********************
	// ********************************************************
	JPanel control2 = new InsetPanel(insets);
	control2.setBorder(BorderFactory.createTitledBorder("Filter Controls"));
	control2.setLayout(new BoxLayout(control2, BoxLayout.Y_AXIS));
	control2.add(Box.createRigidArea(vpad10));
	control2.add(noAllFilesFilterButton);
	control2.add(Box.createRigidArea(vpad10));
	control2.add(yesAllFilesFilterButton);
	control2.add(Box.createRigidArea(vpad10));
	control2.add(defaultFiltersRadioButton);
	control2.add(Box.createRigidArea(vpad10));
	control2.add(addFiltersRadioButton);
	control2.add(Box.createRigidArea(vpad10));
	control2.add(Box.createGlue());

	// ********************************************************
	// ****************** Display Options *********************
	// ********************************************************
	JPanel control3 = new InsetPanel(insets);
	control3.setBorder(BorderFactory.createTitledBorder("Display Options"));
	control3.setLayout(new BoxLayout(control3, BoxLayout.Y_AXIS));
	control3.add(Box.createRigidArea(vpad10));
	control3.add(setHiddenCheckBox);
	control3.add(Box.createRigidArea(vpad10));
	control3.add(showFullDescriptionCheckBox);
	control3.add(Box.createRigidArea(vpad10));
	control3.add(useFileViewCheckBox);
	control3.add(Box.createRigidArea(vpad10));
	control3.add(accessoryCheckBox);
	control3.add(Box.createRigidArea(vpad10));
	control3.add(useControlsCheckBox);
	control3.add(Box.createRigidArea(vpad10));
	control3.add(Box.createGlue());

	// ********************************************************
	// ************* File & Directory Options *****************
	// ********************************************************
	JPanel control4 = new InsetPanel(insets);
	control4.setBorder(BorderFactory.createTitledBorder("File and Directory Options"));
	control4.setLayout(new BoxLayout(control4, BoxLayout.Y_AXIS));
	control4.add(Box.createRigidArea(vpad10));
	control4.add(justFilesRadioButton);
	control4.add(Box.createRigidArea(vpad10));
	control4.add(justDirectoriesRadioButton);
	control4.add(Box.createRigidArea(vpad10));
	control4.add(bothFilesAndDirectoriesRadioButton);
	control4.add(Box.createRigidArea(vpad10));
	control4.add(Box.createRigidArea(vpad10));
	control4.add(singleSelectionRadioButton);
	control4.add(Box.createRigidArea(vpad10));
	control4.add(multiSelectionRadioButton);
	control4.add(Box.createRigidArea(vpad10));
	control4.add(Box.createGlue());


	// ********************************************************
	// **************** Look & Feel Switch ********************
	// ********************************************************
	JPanel panel = new InsetPanel(insets);
	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	panel.add(Box.createRigidArea(hpad10));
	panel.add(metalRadioButton);
	panel.add(Box.createRigidArea(hpad10));
	panel.add(motifRadioButton);
	panel.add(Box.createRigidArea(hpad10));
	panel.add(windowsRadioButton);
	panel.add(Box.createRigidArea(hpad10));
	panel.add(showButton);
	panel.add(Box.createRigidArea(hpad10));

	// ********************************************************
	// ****************** Wrap 'em all up *********************
	// ********************************************************
	JPanel wrapper = new JPanel();
	wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));

	add(Box.createRigidArea(vpad10));

	wrapper.add(Box.createRigidArea(hpad10));
	wrapper.add(control1);
	wrapper.add(Box.createRigidArea(hpad10));
	wrapper.add(control2);
	wrapper.add(Box.createRigidArea(hpad10));
	wrapper.add(control3);
	wrapper.add(Box.createRigidArea(hpad10));
	wrapper.add(control4);
	wrapper.add(Box.createRigidArea(hpad10));
	wrapper.add(Box.createRigidArea(hpad10));
	wrapper.add(panel);
	wrapper.add(Box.createRigidArea(hpad10));

	add(wrapper);
	add(Box.createRigidArea(vpad10));
	add(Box.createRigidArea(vpad10));
	add(panel);
	add(Box.createRigidArea(vpad10));
	add(Box.createRigidArea(vpad10));
    }

    public void actionPerformed(ActionEvent e) {
	int retval = chooser.showDialog(frame, null);
	if(retval == JFileChooser.APPROVE_OPTION) {
	    File theFile = chooser.getSelectedFile();
	    if(theFile != null) {
		File [] files = chooser.getSelectedFiles();
		if(chooser.isMultiSelectionEnabled() && files != null && files.length > 1) {
		    String filenames = "";
		    for(int i = 0; i < files.length; i++) {
			filenames = filenames + "\n" + files[i].getPath();
		    }
		    JOptionPane.showMessageDialog(
			frame, "You chose these files: \n" + filenames
		    );
		} else if(theFile.isDirectory()) {
		    JOptionPane.showMessageDialog(
			frame, "You chose this directory: " +
			chooser.getSelectedFile().getPath()
		    );
		} else {
		    JOptionPane.showMessageDialog(
			frame, "You chose this file: " +
			chooser.getSelectedFile().getPath()
		    );
		}
		return;
	    }
	} else if(retval == JFileChooser.CANCEL_OPTION) {
	   JOptionPane.showMessageDialog(frame, "User cancelled operation. No file was chosen.");
	} else if(retval == JFileChooser.ERROR_OPTION) {
	   JOptionPane.showMessageDialog(frame, "An error occured. No file was chosen.");
	} else {
	   JOptionPane.showMessageDialog(frame, "Unknown operation occured.");
	}
    }

    /** An ActionListener that listens to the radio buttons. */
    class OptionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    JComponent c = (JComponent) e.getSource();
	    if(c == openRadioButton) {
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		customField.setEnabled(false);
		repaint();
	    } else if (c == useControlsCheckBox) {
		boolean showButtons = ((JCheckBox)c).isSelected();
		chooser.setControlButtonsAreShown(showButtons);
	    } else if (c == saveRadioButton) {
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		customField.setEnabled(false);
		repaint();
	    } else if (c == customButton || c == customField) {
		customField.setEnabled(true);
		chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
		chooser.setApproveButtonText(customField.getText());
		repaint();
	    } else if(c == noAllFilesFilterButton) {
		chooser.setAcceptAllFileFilterUsed(false);
	    } else if(c == yesAllFilesFilterButton) {
		chooser.setAcceptAllFileFilterUsed(true);
	    } else if(c == defaultFiltersRadioButton) {
		chooser.resetChoosableFileFilters();
	    } else if(c == addFiltersRadioButton) {
		chooser.addChoosableFileFilter(bothFilter);
		chooser.addChoosableFileFilter(jpgFilter);
		chooser.addChoosableFileFilter(gifFilter);
	    } else if(c == setHiddenCheckBox) {
		chooser.setFileHidingEnabled(!setHiddenCheckBox.isSelected());
	    } else if(c == accessoryCheckBox) {
		if(accessoryCheckBox.isSelected()) {
		    chooser.setAccessory(previewer);
		} else {
		    chooser.setAccessory(null);
		}
	    } else if(c == useFileViewCheckBox) {
		if(useFileViewCheckBox.isSelected()) {
		    chooser.setFileView(fileView);
		} else {
		    chooser.setFileView(null);
		}
	    } else if(c == showFullDescriptionCheckBox) {
		jpgFilter.setExtensionListInDescription(showFullDescriptionCheckBox.isSelected());
		gifFilter.setExtensionListInDescription(showFullDescriptionCheckBox.isSelected());
		bothFilter.setExtensionListInDescription(showFullDescriptionCheckBox.isSelected());
		if(addFiltersRadioButton.isSelected()) {
		    chooser.resetChoosableFileFilters();
		    chooser.addChoosableFileFilter(bothFilter);
		    chooser.addChoosableFileFilter(jpgFilter);
		    chooser.setFileFilter(gifFilter);
		}
	    } else if(c == justFilesRadioButton) {
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    } else if(c == justDirectoriesRadioButton) {
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    } else if(c == bothFilesAndDirectoriesRadioButton) {
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    } else if(c == singleSelectionRadioButton) {
		if(singleSelectionRadioButton.isSelected()) {
		    chooser.setMultiSelectionEnabled(false);
		} 
	    } else if(c == multiSelectionRadioButton) {
		if(multiSelectionRadioButton.isSelected()) {
		    chooser.setMultiSelectionEnabled(true);
		} 
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
	    metalRadioButton.setSelected(true);
	} else if (lnfName.indexOf(windows) >= 0) {
	    windowsRadioButton.setSelected(true);
	} else if (lnfName.indexOf(motif) >= 0) {
	    motifRadioButton.setSelected(true);
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

    class InsetPanel extends JPanel {
	Insets i;
	InsetPanel(Insets i) {
	    this.i = i;
	}
	public Insets getInsets() {
	    return i;
	}
    }
}
