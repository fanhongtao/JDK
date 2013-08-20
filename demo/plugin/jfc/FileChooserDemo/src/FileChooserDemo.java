/*
 * @(#)FileChooserDemo.java	1.28 05/03/25
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)FileChooserDemo.java	1.28 05/03/25
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
 * 1.28 03/25/05
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

    JCheckBox showAllFilesFilterCheckBox;
    JCheckBox showImageFilesFilterCheckBox;
    JCheckBox showFullDescriptionCheckBox;
    
    JCheckBox useFileViewCheckBox;
    JCheckBox accessoryCheckBox;
    JCheckBox setHiddenCheckBox;
    JCheckBox useControlsCheckBox;
    JCheckBox enableDragCheckBox;

    JRadioButton singleSelectionRadioButton;
    JRadioButton multiSelectionRadioButton;

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
    public final static Dimension vpad20 = new Dimension(1,20);
    public final static Dimension vpad7 = new Dimension(1, 7);
    public final static Dimension vpad4 = new Dimension(1, 4);
    public final static Insets insets = new Insets(5, 10, 0, 10);

    FilePreviewer previewer;
    JFileChooser chooser;

    public FileChooserDemo() {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	chooser = new JFileChooser();
	previewer = new FilePreviewer(chooser);

	// Create Filters
	jpgFilter = new ExampleFileFilter("jpg", "JPEG Compressed Image Files");
	gifFilter = new ExampleFileFilter("gif", "GIF Image Files");
	bothFilter = new ExampleFileFilter(new String[] {"jpg", "gif"}, "JPEG and GIF Image Files");

	// Create Custom FileView
	fileView = new ExampleFileView();
	fileView.putIcon("jpg", new ImageIcon(getClass().getResource("/resources/images/jpgIcon.jpg")));
	fileView.putIcon("gif", new ImageIcon(getClass().getResource("/resources/images/gifIcon.gif")));

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

	customField = new JTextField(8) {
	    public Dimension getMaximumSize() {
		return new Dimension(getPreferredSize().width, getPreferredSize().height);
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
        showAllFilesFilterCheckBox = new JCheckBox("Show \"All Files\" Filter");
        showAllFilesFilterCheckBox.addActionListener(optionListener);
        showAllFilesFilterCheckBox.setSelected(true);
        
        showImageFilesFilterCheckBox = new JCheckBox("Show JPG and GIF Filters");
        showImageFilesFilterCheckBox.addActionListener(optionListener);
        showImageFilesFilterCheckBox.setSelected(false);
        
	accessoryCheckBox = new JCheckBox("Show Preview");
	accessoryCheckBox.addActionListener(optionListener);
	accessoryCheckBox.setSelected(false);

	// more options
	setHiddenCheckBox = new JCheckBox("Show Hidden Files");
	setHiddenCheckBox.addActionListener(optionListener);

	showFullDescriptionCheckBox = new JCheckBox("With File Extensions");
	showFullDescriptionCheckBox.addActionListener(optionListener);
	showFullDescriptionCheckBox.setSelected(true);
        showFullDescriptionCheckBox.setEnabled(false);

	useFileViewCheckBox = new JCheckBox("Use FileView");
	useFileViewCheckBox.addActionListener(optionListener);
	useFileViewCheckBox.setSelected(false);

	useControlsCheckBox = new JCheckBox("Show Control Buttons");
	useControlsCheckBox.addActionListener(optionListener);
	useControlsCheckBox.setSelected(true);
        
        enableDragCheckBox = new JCheckBox("Enable Dragging");
        enableDragCheckBox.addActionListener(optionListener);

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
        metalRadioButton.setMnemonic('m');
	metalRadioButton.setActionCommand(metalClassName);
	metalRadioButton.setEnabled(isAvailableLookAndFeel(metalClassName));

	motifRadioButton = new JRadioButton(motif);
        motifRadioButton.setMnemonic('o');
	motifRadioButton.setActionCommand(motifClassName);
	motifRadioButton.setEnabled(isAvailableLookAndFeel(motifClassName));

	windowsRadioButton = new JRadioButton(windows);
        windowsRadioButton.setMnemonic('w');
	windowsRadioButton.setActionCommand(windowsClassName);
	windowsRadioButton.setEnabled(isAvailableLookAndFeel(windowsClassName));

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
	control1.add(Box.createRigidArea(vpad20));
	control1.add(openRadioButton);
	control1.add(Box.createRigidArea(vpad7));
	control1.add(saveRadioButton);
	control1.add(Box.createRigidArea(vpad7));
	control1.add(customButton);
	control1.add(Box.createRigidArea(vpad4));
        JPanel fieldWrapper = new JPanel();
        fieldWrapper.setLayout(new BoxLayout(fieldWrapper, BoxLayout.X_AXIS));
        fieldWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldWrapper.add(Box.createRigidArea(hpad10));
        fieldWrapper.add(Box.createRigidArea(hpad10));
        fieldWrapper.add(customField);
	control1.add(fieldWrapper);
        control1.add(Box.createRigidArea(vpad20));
	control1.add(Box.createGlue());

	// ********************************************************
	// ***************** Filter Controls **********************
	// ********************************************************
	JPanel control2 = new InsetPanel(insets);
	control2.setBorder(BorderFactory.createTitledBorder("Filter Controls"));
	control2.setLayout(new BoxLayout(control2, BoxLayout.Y_AXIS));
	control2.add(Box.createRigidArea(vpad20));
        control2.add(showAllFilesFilterCheckBox);
	control2.add(Box.createRigidArea(vpad7));
        control2.add(showImageFilesFilterCheckBox);
        control2.add(Box.createRigidArea(vpad4));
        JPanel checkWrapper = new JPanel();
        checkWrapper.setLayout(new BoxLayout(checkWrapper, BoxLayout.X_AXIS));
        checkWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkWrapper.add(Box.createRigidArea(hpad10));
        checkWrapper.add(Box.createRigidArea(hpad10));
        checkWrapper.add(showFullDescriptionCheckBox);
        control2.add(checkWrapper);
	control2.add(Box.createRigidArea(vpad20));
	control2.add(Box.createGlue());

	// ********************************************************
	// ****************** Display Options *********************
	// ********************************************************
	JPanel control3 = new InsetPanel(insets);
	control3.setBorder(BorderFactory.createTitledBorder("Display Options"));
	control3.setLayout(new BoxLayout(control3, BoxLayout.Y_AXIS));
	control3.add(Box.createRigidArea(vpad20));
	control3.add(setHiddenCheckBox);
	control3.add(Box.createRigidArea(vpad7));
	control3.add(useFileViewCheckBox);
	control3.add(Box.createRigidArea(vpad7));
	control3.add(accessoryCheckBox);
	control3.add(Box.createRigidArea(vpad7));
	control3.add(useControlsCheckBox);
        control3.add(Box.createRigidArea(vpad7));
        control3.add(enableDragCheckBox);
	control3.add(Box.createRigidArea(vpad20));
	control3.add(Box.createGlue());

	// ********************************************************
	// ************* File & Directory Options *****************
	// ********************************************************
	JPanel control4 = new InsetPanel(insets);
	control4.setBorder(BorderFactory.createTitledBorder("File and Directory Options"));
	control4.setLayout(new BoxLayout(control4, BoxLayout.Y_AXIS));
	control4.add(Box.createRigidArea(vpad20));
	control4.add(justFilesRadioButton);
	control4.add(Box.createRigidArea(vpad7));
	control4.add(justDirectoriesRadioButton);
	control4.add(Box.createRigidArea(vpad7));
	control4.add(bothFilesAndDirectoriesRadioButton);
	control4.add(Box.createRigidArea(vpad20));
	control4.add(singleSelectionRadioButton);
	control4.add(Box.createRigidArea(vpad7));
	control4.add(multiSelectionRadioButton);
	control4.add(Box.createRigidArea(vpad20));
	control4.add(Box.createGlue());


	// ********************************************************
	// **************** Look & Feel Switch ********************
	// ********************************************************
	JPanel panel = new JPanel();
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

	add(Box.createRigidArea(vpad20));

	wrapper.add(Box.createRigidArea(hpad10));
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

	add(wrapper);
	add(Box.createRigidArea(vpad20));
	add(panel);
	add(Box.createRigidArea(vpad20));
    }

    public void actionPerformed(ActionEvent e) {
        if (customButton.isSelected()) {
            chooser.setApproveButtonText(customField.getText());    
        }        
	if (chooser.isMultiSelectionEnabled()) {
	    chooser.setSelectedFiles(null);
	} else {
	    chooser.setSelectedFile(null);
	}
        // clear the preview from the previous display of the chooser
        JComponent accessory = chooser.getAccessory();
        if (accessory != null) {
            ((FilePreviewer)accessory).loadImage(null);
        }
	int retval = chooser.showDialog(frame, null);
	if (retval == JFileChooser.APPROVE_OPTION) {
	    if (chooser.isMultiSelectionEnabled()) {
		File [] files = chooser.getSelectedFiles();
		if (files != null && files.length > 0) {
		    String filenames = "";
		    for (int i = 0; i < files.length; i++) {
			filenames = filenames + "\n" + files[i].getPath();
		    }
		    JOptionPane.showMessageDialog(frame,
						  "You chose these files: \n" + filenames);
		}
	    } else {
		File theFile = chooser.getSelectedFile();
		if (theFile != null) {
		    if (theFile.isDirectory()) {
			JOptionPane.showMessageDialog(frame,
						      "You chose this directory: " +
						      theFile.getPath());
		    } else {
			JOptionPane.showMessageDialog(frame,
						      "You chose this file: " +
						      theFile.getPath());
		    }
		}
	    }
	} else if (retval == JFileChooser.CANCEL_OPTION) {
	    JOptionPane.showMessageDialog(frame, "User cancelled operation. No file was chosen.");
	} else if (retval == JFileChooser.ERROR_OPTION) {
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
            } else if (c == enableDragCheckBox) {
                boolean enableDrag = ((JCheckBox)c).isSelected();
                chooser.setDragEnabled(enableDrag);
	    } else if (c == saveRadioButton) {
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		customField.setEnabled(false);
		repaint();
	    } else if (c == customButton || c == customField) {
		customField.setEnabled(true);
		chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);		
		repaint();
            } else if (c == showAllFilesFilterCheckBox) {
                chooser.setAcceptAllFileFilterUsed(((JCheckBox)c).isSelected());
            } else if (c == showImageFilesFilterCheckBox) {
                if (((JCheckBox)c).isSelected()) {
                    chooser.addChoosableFileFilter(bothFilter);
                    chooser.addChoosableFileFilter(jpgFilter);
                    chooser.addChoosableFileFilter(gifFilter);
                    showFullDescriptionCheckBox.setEnabled(true);
                } else {
                    chooser.resetChoosableFileFilters();
                    showFullDescriptionCheckBox.setEnabled(false);
                }
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

	public FilePreviewer(JFileChooser fc) {
	    setPreferredSize(new Dimension(100, 50));
	    fc.addPropertyChangeListener(this);
	}

	public void loadImage(File f) {
            if (f == null) {
                thumbnail = null;
            } else {
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
		if(isShowing()) {
                    loadImage((File) e.getNewValue());
		    repaint();
		}
	    }
	}

	public void paint(Graphics g) {
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

    /**
     * A utility function that layers on top of the LookAndFeel's
     * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
     * is supported. Returns false if the LookAndFeel is not supported
     * and/or if there is any kind of error checking if the LookAndFeel
     * is supported.
     *
     * The L&F menu will use this method to detemine whether the various
     * L&F options should be active or inactive.
     *
     */
     protected boolean isAvailableLookAndFeel(String laf) {
         try { 
             Class lnfClass = Class.forName(laf);
             LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
             return newLAF.isSupportedLookAndFeel();
         } catch (Exception e) { // If ANYTHING weird happens, return false
             return false;
         }
     }
}
