/*
 * @(#)SwingSet.java	1.84 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import java.net.*;

/**
 * A demo that shows all of the Swing components.
 *
 * @version 1.74 06/09/98
 * @author Jeff Dinkins (code, code, and more code)
 * @author Chester Rose (graphic artist)
 * @author Arnaud Weber (applet support)
 * @author Peter Korn   (accessbility support)
 * @author Georges Saab (menus, toolbars)
 */
public class SwingSet extends JPanel
{
    // This
    SwingSet swing;

    // The Frame
    public static JFrame frame;

    // Current ui
    public String currentUI = "Metal";

    // The width and height of the frame
    public static int WIDTH = 790;
    public static int HEIGHT = 550;
    public static int INITIAL_WIDTH = 400;
    public static int INITIAL_HEIGHT = 200;

    public final static Dimension hpad5 = new Dimension(5,1);
    public final static Dimension hpad10 = new Dimension(10,1);
    public final static Dimension hpad20 = new Dimension(20,1);
    public final static Dimension hpad25 = new Dimension(25,1);
    public final static Dimension hpad30 = new Dimension(30,1);
    public final static Dimension hpad40 = new Dimension(40,1);
    public final static Dimension hpad80 = new Dimension(80,1);

    public final static Dimension vpad5 = new Dimension(1,5);
    public final static Dimension vpad10 = new Dimension(1,10);
    public final static Dimension vpad20 = new Dimension(1,20);
    public final static Dimension vpad25 = new Dimension(1,25);
    public final static Dimension vpad30 = new Dimension(1,30);
    public final static Dimension vpad40 = new Dimension(1,40);
    public final static Dimension vpad80 = new Dimension(1,80);

    public final static Insets insets0 = new Insets(0,0,0,0);
    public final static Insets insets5 = new Insets(5,5,5,5);
    public final static Insets insets10 = new Insets(10,10,10,10);
    public final static Insets insets15 = new Insets(15,15,15,15);
    public final static Insets insets20 = new Insets(20,20,20,20);

    public final static Border emptyBorder0 = new EmptyBorder(0,0,0,0);
    public final static Border emptyBorder5 = new EmptyBorder(5,5,5,5);
    public final static Border emptyBorder10 = new EmptyBorder(10,10,10,10);
    public final static Border emptyBorder15 = new EmptyBorder(15,15,15,15);
    public final static Border emptyBorder20 = new EmptyBorder(20,20,20,20);

    public final static Border etchedBorder10 = new CompoundBorder(
                                                        new EtchedBorder(),
                                                        emptyBorder10);

    public final static Border raisedBorder = new BevelBorder(BevelBorder.RAISED);
    public final static Border lightLoweredBorder = new BevelBorder(BevelBorder.LOWERED, 
                                                          Color.white, Color.gray);
    public final static Border loweredBorder = new SoftBevelBorder(BevelBorder.LOWERED);

    public Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
    public Font boldFont = new Font("Dialog", Font.BOLD, 12);
    public Font bigFont = new Font("Dialog", Font.PLAIN, 18);
    public Font bigBoldFont = new Font("Dialog", Font.BOLD, 18);
    public Font reallyBigFont = new Font("Dialog", Font.PLAIN, 18);
    public Font reallyBigBoldFont = new Font("Dialog", Font.BOLD, 24);

    // LookAndFeel class names
    static String macClassName =
            "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    static String metalClassName =
            "javax.swing.plaf.metal.MetalLookAndFeel";
    static String motifClassName = 
	    "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    static String windowsClassName = 
	    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    // L&F radio buttons
    JRadioButtonMenuItem macMenuItem;
    JRadioButtonMenuItem metalMenuItem;
    JRadioButtonMenuItem motifMenuItem;
    JRadioButtonMenuItem windowsMenuItem;

    // Some images used in the demo
    public ImageIcon jpgIcon;
    public ImageIcon gifIcon;

    public ImageIcon blueDot;
    public ImageIcon redDot;
    public ImageIcon invisibleDot;

    public ImageIcon duke2;
    public ImageIcon dukeSnooze;
    public ImageIcon dukeWave;
    public ImageIcon dukeWaveRed;
    public ImageIcon dukeMagnify;

    public ImageIcon cow;

    public ImageIcon tiger;
    public ImageIcon littleTiger;

    public ImageIcon upButton;
    public ImageIcon downButton;
    public ImageIcon disabledButton;


    // The panels used in the demo
    public JPanel borderPanel;
    public JPanel borderedPanePanel;
    public JPanel buttonPanel;
    public JPanel checkboxPanel;
    public JPanel comboBoxPanel;
    public JPanel dateChooserPanel;
    public JPanel debugGraphicsPanel;
    public JPanel htmlPanel;
    public JPanel labelPanel;
    public JPanel listBoxPanel;
    public JPanel logoPanel;
    public JPanel menuPanel;
    public JPanel progressBarPanel;
    public JPanel radioButtonPanel;
    public JPanel scrollPanePanel;
    public JPanel sliderPanel;
    public JPanel splitPanePanel;
    public JPanel tablePanel;
    public JPanel textPanel;
    public JPanel toggleButtonPanel;
    public JPanel toolTipPanel;
    public JPanel treePanel;
    public JPanel windowPanel;

    // Track progress
    public static int totalPanels = 23; // PENDING(jeff) there has got to be a better way...
    public static int currentProgressValue;
    public static JLabel progressLabel = null;
    public static JProgressBar progressBar = null;

    // Used when switching to DebugGraphicsPanel
    public Component previousPage;

    // Button controls
    public Vector currentControls;
    public Vector labels = new Vector();
    public Vector buttons = new Vector();
    public Vector checkboxes = new Vector();
    public Vector radioButtons = new Vector();
    public Vector toggleButtons = new Vector();

    // Some components used in the demo
    public JTabbedPane tabbedPane;
    public JPanel borderedPane;
    public JList listBox;
    public TabPlacementChanger tabPlacement;
    public int toolTipIndex;
    public ComponentOrientationChanger componentOrientationChanger;

    // This != null if we are an applet
    java.applet.Applet applet;
    static SwingSet instance;

    public SwingSet() {
	this(null);
    }

    /*******************************************/
    /****** Construct the SwingSet demo ********/
    /*******************************************/
    public SwingSet(java.applet.Applet anApplet) {
	super(true); // double buffer

	instance = this;
	applet = anApplet;
	loadImages();
	swing = this;
	setName("Main SwingSet Panel");
	DebugGraphics.setFlashTime(30);
	setFont(defaultFont);
	setLayout(new BorderLayout());
        currentProgressValue = 0;

	// Add a MenuBar
	add(createMenuBar(), BorderLayout.NORTH);

	// Create a tab pane
	tabbedPane = new JTabbedPane();

        // Add magic key to enable timer logging
	// PENDING(Arnaud) - is this really needed? 
        tabbedPane.registerKeyboardAction(new ToggleLogging(),
		     KeyStroke.getKeyStroke('l', InputEvent.ALT_MASK), WHEN_IN_FOCUSED_WINDOW);
        
	// Add the tab to the center
	add(tabbedPane, BorderLayout.CENTER);

	// Add the Button panel
	progressLabel.setText("Loading Title Page");
	ImageIcon swingLogo = loadImageIcon("images/swingLabelSmall.gif","Swing!");
	JPanel logoPanel = createLogo();
	tabbedPane.addTab("", swingLogo, logoPanel);
	// set the Tab's AccessibleName 'cause we are using a graphic only
	tabbedPane.getAccessibleContext().getAccessibleChild(tabbedPane.indexOfTab(swingLogo)).getAccessibleContext().setAccessibleName("Swing!");

	tabbedPane.setSelectedIndex(0);
	progressBar.setValue(++currentProgressValue);

	// Buttons
	progressLabel.setText("Loading Button Example");
	buttonPanel = new ButtonPanel(this);
	tabbedPane.addTab("Buttons", null, buttonPanel);
	progressBar.setValue(++currentProgressValue);

	// RadioButtons
	progressLabel.setText("Loading RadioButton Example");
	radioButtonPanel = new RadioButtonPanel(this);
	tabbedPane.addTab("RadioButtons", null, radioButtonPanel);
	progressBar.setValue(++currentProgressValue);

	// ToggleButtons
	progressLabel.setText("Loading ToggleButton Example");
	toggleButtonPanel = new ToggleButtonPanel(this);
	tabbedPane.addTab("ToggleButtons", null, toggleButtonPanel);
	progressBar.setValue(++currentProgressValue);

	// CheckBoxMenuItem
	progressLabel.setText("Loading Checkbox Example");
	checkboxPanel = new CheckboxPanel(this);
	tabbedPane.addTab("Checkboxes", null, checkboxPanel);
	progressBar.setValue(++currentProgressValue);

	// Labels
	progressLabel.setText("Loading Label Example");
	labelPanel = new LabelPanel(this);
	tabbedPane.addTab("Labels", null, labelPanel);
	progressBar.setValue(++currentProgressValue);

	// Borders
	progressLabel.setText("Loading Border Example");
	borderPanel = new BorderPanel();
	tabbedPane.addTab("Borders", null, borderPanel);
	progressBar.setValue(++currentProgressValue);

	// ComboBox
	progressLabel.setText("Loading ComboBox Example");
	comboBoxPanel = new ComboBoxPanel(this);
	tabbedPane.addTab("ComboBox",null,comboBoxPanel);
	progressBar.setValue(++currentProgressValue);

	// DebugGraphics
	progressLabel.setText("Loading DebugGraphics Example");
	debugGraphicsPanel = new DebugGraphicsPanel(this);
	tabbedPane.addTab("DebugGraphics", null, debugGraphicsPanel);
	progressBar.setValue(++currentProgressValue);

	// Internal Frame
	progressLabel.setText("Loading Internal Frame Example");
	windowPanel = new InternalWindowPanel();
	tabbedPane.addTab("Internal Frame", null, windowPanel);
	progressBar.setValue(++currentProgressValue);

	// ListBox
	progressLabel.setText("Loading ListBox Example");
	listBoxPanel = new ListPanel(this);
	tabbedPane.addTab("ListBox", null, listBoxPanel);
	progressBar.setValue(++currentProgressValue);

	// Menus
	progressLabel.setText("Loading Menu Example");
	menuPanel = createMenus();
	tabbedPane.addTab("Menus & ToolBars", null, menuPanel);
	progressBar.setValue(++currentProgressValue);
	// This is unfortunately needed right now, since JMenu
	// has no way to know when an ancestor is removed from
	// the hierarchy so that it can deselect itself.  We
	// do this explicitly here so that menus aren't left
	// hanging when you switch tabs -- better underlying 
	// support will exist in future versions.
	tabbedPane.addContainerListener(new ContainerAdapter() {
	    public void componentRemoved(ContainerEvent e) {
		Component c = e.getChild();
		if (c == menuPanel) 
		    menuBar.setSelected(null);
	    }

	});

	// ProgressBar
	progressLabel.setText("Loading ProgressBar Example");
	progressBarPanel = new ProgressPanel(this);
	tabbedPane.addTab("ProgressBar", null, progressBarPanel);
	progressBar.setValue(++currentProgressValue);

	// ScrollPane
	progressLabel.setText("Loading ScrollPane Example");
	scrollPanePanel = new ScrollPanePanel();
	tabbedPane.addTab("ScrollPane", littleTiger, scrollPanePanel);
	progressBar.setValue(++currentProgressValue);


	// Sliders
	progressLabel.setText("Loading Slider Example");
	sliderPanel = new SliderPanel(swing);
	tabbedPane.addTab("Slider", null, sliderPanel);
	progressBar.setValue(++currentProgressValue);

	// SplitPane
	progressLabel.setText("Loading SplitPane Example");
	splitPanePanel = new SplitPanePanel(this);
	tabbedPane.addTab("SplitPane", null, splitPanePanel);
	progressBar.setValue(++currentProgressValue);


	// Table
	progressLabel.setText("Loading Table Example");
	tablePanel = new TablePanel(swing);
	tabbedPane.addTab("TableView", null, tablePanel);
	progressBar.setValue(++currentProgressValue);

	// Text
	progressLabel.setText("Loading Text Example");
	textPanel = new TextPanel(swing);
	tabbedPane.addTab("Plain Text", null, textPanel);
	progressBar.setValue(++currentProgressValue);

	// HTML Text
	// PENDING(jeff) make this work when we are an applet
	if(!isApplet()) {
	    progressLabel.setText("Loading HTML Text Example");
	    htmlPanel = new HtmlPanel(swing);
	    tabbedPane.addTab("<html><center><font color=yellow>HTML</font> Text</center></html>", null, htmlPanel);
	    progressBar.setValue(++currentProgressValue);
	}

	// borderedPane
	progressLabel.setText("Loading BorderedPane Example");
	borderedPanePanel = new BorderedPanePanel(this);
	tabbedPane.addTab("BorderedPane", null, borderedPanePanel);
	progressBar.setValue(++currentProgressValue);

	// ToolTips
	progressLabel.setText("Loading ToolTip Example");
	toolTipPanel = new ToolTipPanel(swing);
	tabbedPane.addTab("ToolTips", cow, toolTipPanel);
	toolTipIndex = currentProgressValue;
	progressBar.setValue(++currentProgressValue);

	// TreeView
	progressLabel.setText("Loading TreeView Example");
	treePanel = new TreePanel(this);
	tabbedPane.addTab("TreeView", null, treePanel);
	progressBar.setValue(++currentProgressValue);

	// Add Tab change listener
	createTabListener();

    }

    class ToggleLogging extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
           Timer.setLogTimers(!Timer.getLogTimers());
        }

        public boolean isEnabled() {
            return true;
        }
    }

  /** Image loading **/
  void loadImages() {
    jpgIcon = loadImageIcon("images/jpgIcon.jpg", "An icon that represents jpg images");
    gifIcon = loadImageIcon("images/gifIcon.gif", "An icon that represents gif images");
    blueDot   = loadImageIcon("images/dot.gif","A blue bullet icon - to draw attention to a menu item");
    redDot   = loadImageIcon("images/redDot.gif","A red bullet icon - to draw attention to a menu item");
    invisibleDot   = loadImageIcon("images/noDot.gif","An invisible bullet, used in visual spacing of menu items");
    duke2 = loadImageIcon("images/duke2.gif","Duke with hands at sides");
    dukeSnooze = loadImageIcon("images/dukeSnooze.gif","Sleeping Duke");
    dukeWave   = loadImageIcon("images/dukeWave.gif","Duke waving");
    dukeWaveRed = loadImageIcon("images/dukeWaveRed.gif","Duke waving with bright red nose");
    dukeMagnify = loadImageIcon("images/dukeMagnify.gif","Duke with a magnifying glass");
    cow         = loadImageIcon("images/cowSmall.gif","Black and white cow");
    tiger       = loadImageIcon("images/BigTiger.gif","Fierce looking tiger");
    littleTiger = loadImageIcon("images/SmallTiger.gif","Fierce looking tiger");
    upButton    = loadImageIcon("images/buttonImage2.gif","Round button with gold border, green on the inside, and dark triangle pointing right.");
    downButton  = loadImageIcon("images/buttonImage3.gif","Round button with gold border, green on the inside, and green triangle pointing right.");
    disabledButton = loadImageIcon("images/buttonImage4.gif","Round button with gold border, green on the inside, and greyed out triangle pointing right.");
  }

    private class AccessibilityEasterListener extends MouseAdapter {
        StringTokenizer descriptionTokens;
        String descriptionText;
	JMenuItem menuItem;
        public AccessibilityEasterListener(JMenuItem mi, String descs) {
	    super();
	    descriptionTokens = new StringTokenizer(descs, ",");
	    descriptionText = descs;
	    menuItem = mi;
        }

        public void mouseEntered(MouseEvent e) {
            if (!descriptionTokens.hasMoreTokens()) {
	        descriptionTokens = new StringTokenizer(descriptionText, ",");
            }
            menuItem.getAccessibleContext().setAccessibleDescription(descriptionTokens.nextToken());
        }
    }

    /*******************************************/
    /************ create components ************/
    /*******************************************/

    /**
     * A utility function that layers on top of the LookAndFeel's
     * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
     * is supported. Returns false if the LookAndFeel is not supported
     * and/or if there is any kind of error checking if the LookAndFeel
     * is supported. 
     * The L&F menu will use this method to detemine whether the various
     * L&F options should be active or inactive.
     *
     */
     protected static boolean isAvailableLookAndFeel(String classname) {
	 try { // Try to create a L&F given a String
	     Class lnfClass = Class.forName(classname);
	     LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
	     return newLAF.isSupportedLookAndFeel();
	 } catch(Exception e) { // If ANYTHING weird happens, return false
	     return false;
	 }
     }


    /**
     * MenuBar
     */
    JDialog aboutBox;
    JCheckBoxMenuItem cb;
    JRadioButtonMenuItem rb;

    JMenuBar createMenuBar() {
	// MenuBar
	JMenuBar menuBar = new JMenuBar();
	menuBar.getAccessibleContext().setAccessibleName("Swing menus");

	JMenuItem mi;

	// File Menu
	JMenu file = (JMenu) menuBar.add(new JMenu("File"));
        file.setMnemonic('F');
	file.getAccessibleContext().setAccessibleDescription("The standard 'File' application menu");
        mi = (JMenuItem) file.add(new JMenuItem("About"));
        mi.setMnemonic('t');
	mi.getAccessibleContext().setAccessibleDescription("Find out about the SwingSet application");
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// tabbedPane.setSelectedIndex(0);
                if(aboutBox == null) {
                    aboutBox = new JDialog(SwingSet.sharedInstance().getFrame(), "About Swing!", false);
                    JPanel groupPanel = new JPanel(new BorderLayout());
		    ImageIcon groupPicture = loadImageIcon("images/Copyright.gif",
                      "SwingSet demo is Copyright (c) 1997 Sun Microsystems, Inc.  All Rights Reserved.");
                    aboutBox.getContentPane().add(groupPanel, BorderLayout.CENTER);
		    JLabel groupLabel = (new JLabel(groupPicture));
		    groupLabel.getAccessibleContext().setAccessibleName("SwingSet demo Copyright");
		    groupLabel.getAccessibleContext().setAccessibleDescription("The JFC Swing Toolkit is a cooperative effort between JavaSoft and Netscape.  The SwingSet demo is Copyright 1997 Sun Microsystems, Inc.  All Rights Reserved.");
                    groupPanel.add(groupLabel, BorderLayout.CENTER);
                    JPanel buttonPanel = new JPanel(true);
                    groupPanel.add(buttonPanel, BorderLayout.SOUTH);
                    JButton button = (JButton) buttonPanel.add(new JButton("OK"));
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            aboutBox.setVisible(false);
                        }
                    });
                }
		aboutBox.pack();
		aboutBox.show();
	    }
	});

        file.addSeparator();
        mi = (JMenuItem) file.add(new JMenuItem("Open"));
        mi.setMnemonic('O');
	mi.setEnabled(false);
	mi.getAccessibleContext().setAccessibleDescription("Placeholder sample menu item for opening a file");
        mi = (JMenuItem) file.add(new JMenuItem("Save"));
        mi.setMnemonic('S');
	mi.setEnabled(false);
	mi.getAccessibleContext().setAccessibleDescription("Placeholder sample menu item for saving a file");
        mi = (JMenuItem) file.add(new JMenuItem("Save As..."));
        mi.setMnemonic('A');
	mi.setEnabled(false);
	mi.getAccessibleContext().setAccessibleDescription("Placeholder sample menu item for saving a file with a new name");
        file.addSeparator();
        mi = (JMenuItem) file.add(new JMenuItem("Exit"));
        mi.setMnemonic('x');
	mi.getAccessibleContext().setAccessibleDescription("Exit the SwingSet application");
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	}
	);

	// Options Menu
	JMenu options = (JMenu) menuBar.add(new JMenu("Options"));
        options.setMnemonic('p');
	options.getAccessibleContext().setAccessibleDescription("Look and Feel options: select one of several different Look and Feels for the SwingSet application");

        // Look and Feel Radio control
	ButtonGroup group = new ButtonGroup();
	ToggleUIListener toggleUIListener = new ToggleUIListener();

        metalMenuItem = (JRadioButtonMenuItem) options.add(new JRadioButtonMenuItem("Java Look and Feel"));
	metalMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Metal"));
	metalMenuItem.setSelected(true);
	metalMenuItem.setEnabled(isAvailableLookAndFeel(metalClassName));
	group.add(metalMenuItem);
	metalMenuItem.addItemListener(toggleUIListener);
	metalMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));

        motifMenuItem = (JRadioButtonMenuItem) options.add(new JRadioButtonMenuItem("Motif Look and Feel"));
	motifMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("CDE/Motif"));
	motifMenuItem.setEnabled(isAvailableLookAndFeel(motifClassName));
	group.add(motifMenuItem);
	motifMenuItem.addItemListener(toggleUIListener);
	motifMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));

        windowsMenuItem = (JRadioButtonMenuItem) options.add(new JRadioButtonMenuItem("Windows Style Look and Feel"));
	windowsMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Windows"));
	windowsMenuItem.setEnabled(isAvailableLookAndFeel(windowsClassName));
	group.add(windowsMenuItem);
	windowsMenuItem.addItemListener(toggleUIListener);
	windowsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));

	macMenuItem = (JRadioButtonMenuItem) options.add(new JRadioButtonMenuItem("Macintosh Look and Feel"));
	macMenuItem.setSelected(UIManager.getLookAndFeel().getName().equals("Macintosh"));
	macMenuItem.setEnabled(isAvailableLookAndFeel(macClassName));
	group.add(macMenuItem);
	macMenuItem.addItemListener(toggleUIListener);
	macMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));

	// non-supported stuff
	/*  
	    rb = (JRadioButtonMenuItem)
	        options.add(new JRadioButtonMenuItem("Java Look and Feel (High Contrast Theme)"));
	    rb.setSelected(UIManager.getLookAndFeel().getName().equals("Metal HCT"));
	    group.add(rb);
	    rb.addItemListener(toggleUIListener);
	    rb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
	    
	    rb = (JRadioButtonMenuItem)
	         options.add(new JRadioButtonMenuItem("Organic Look and Feel (Santa Fe)"));
	    rb.getAccessibleContext().setAccessibleDescription(
	         "The Organic Look and Feel with a brown/yellow color scheme");
	    rb.setSelected(UIManager.getLookAndFeel().getName().equals("Java"));
	    group.add(rb);
	    rb.addItemListener(toggleUIListener);
	    rb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.ALT_MASK));
	    
	    rb = (JRadioButtonMenuItem)
	        options.add(new JRadioButtonMenuItem("Organic Look and Feel (Vancouver)"));
	    rb.getAccessibleContext().setAccessibleDescription(
	        "The Organic Look and Feel with a neutral grey color scheme");
	    group.add(rb);
	    rb.addItemListener(toggleUIListener);
	    rb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.ALT_MASK));
	    
	    rb = (JRadioButtonMenuItem)
	        options.add(new JRadioButtonMenuItem("Organic Look and Feel (Dallas)"));
	    rb.getAccessibleContext().setAccessibleDescription(
	        "The Organic Look and Feel with a high contrast color scheme and large print");
	    group.add(rb);
	    rb.addItemListener(toggleUIListener);
	    rb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, ActionEvent.ALT_MASK));
	*/

        // Tab Placement submenu
        options.addSeparator();

        tabPlacement = new TabPlacementChanger();
	tabPlacement.getAccessibleContext().setAccessibleDescription(
	       "Sub-menu containing options for placement of the TabbedPane");
        options.add(tabPlacement);

        // Create the Component Orientation submenu.  This will only be done
        // when running under JDK1.2 or above.
        componentOrientationChanger = ComponentOrientationChanger.create();
        if( componentOrientationChanger != null ) {
            options.addSeparator();
            options.add(componentOrientationChanger);
        }


        // Tooltip checkbox
        options.addSeparator();

        cb = (JCheckBoxMenuItem) options.add(new JCheckBoxMenuItem("Show ToolTips"));
	cb.setSelected(true);

	cb.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JCheckBoxMenuItem cb = (JCheckBoxMenuItem)e.getSource();
		if(cb.isSelected()) {
                    ToolTipManager.sharedInstance().setEnabled(true);
		} else {
                    ToolTipManager.sharedInstance().setEnabled(false);
		}
	    }
	});

	ActionListener easterListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		tabbedPane.setSelectedIndex(toolTipIndex);
		((ToolTipPanel)toolTipPanel).itsEaster(true);
		swing.invalidate();
		swing.validate();
		swing.repaint();
	    }
	};

	// Contributors Menu
	JMenu people = (JMenu) menuBar.add(new JMenu("The Swing Team"));
        people.setMnemonic('A');
	people.getAccessibleContext().setAccessibleDescription(
	        "Listing of all of the individual contributors to Swing");

        mi = (JMenuItem) people.add(new JMenuItem("Michael Albers", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Mark Andrews", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Tom Ball", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Jeff Dinkins", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Amy Fowler", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("James Gosling", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Earl Johnson", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addMouseListener(new AccessibilityEasterListener(mi, "Accessibility Program Manager, Founder of Sun's Accessibility Effort"));

        mi = (JMenuItem) people.add(new JMenuItem("Will Walker", blueDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addMouseListener(new AccessibilityEasterListener(mi, "Wrote Java Accessibility API,Wrote AccessX,Designed RAP prototol,Contributor to UltraSonix"));
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		tabbedPane.setSelectedIndex(toolTipIndex);
		((ToolTipPanel)toolTipPanel).itsEaster(false);
		swing.invalidate();
		swing.validate();
		swing.repaint();
	    }
	});

        mi = (JMenuItem) people.add(new JMenuItem("Peter Korn", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addMouseListener(new AccessibilityEasterListener(mi, "Wrote Accessibility API,Wrote outSPOKEN for Windows,Wrote GUIAccess for Windows,Contributed to outSPOKEN for Macintosh,Contributed to inLARGE for Macintosh"));

        mi = (JMenuItem) people.add(new JMenuItem("Rick Levenson", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Philip Milne", redDot));

	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Hans Muller", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Tim Prinzing", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Chris Ryan", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Georges Saab", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Tom Santos", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Jeff Shapiro", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Rich Schiavi", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Nancy Schorr", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Harry Vertelney", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Scott Violet", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);


        mi = (JMenuItem) people.add(new JMenuItem("Kathy Walrath", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

        mi = (JMenuItem) people.add(new JMenuItem("Arnaud Weber", redDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);
	mi.addActionListener(easterListener);

        mi = (JMenuItem) people.add(new JMenuItem("Steve Wilson", invisibleDot));
	mi.setHorizontalTextPosition(JMenuItem.RIGHT);

	// Chooser Menu
	JMenu choosers = (JMenu) menuBar.add(new JMenu("Choosers"));
	choosers.setMnemonic('H');
	choosers.getAccessibleContext().setAccessibleDescription("Invoke one of the Swing Choosers");
	mi = (JMenuItem) choosers.add(new JMenuItem("Color Chooser"));
	ActionListener startColorChooser = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		tabbedPane.setSelectedIndex(toolTipIndex);
		Color color = JColorChooser.showDialog(SwingSet.this, "Color Chooser", toolTipPanel.getBackground());
		toolTipPanel.setBackground(color); 
		toolTipPanel.repaint();
		
	    }
	};
	mi.addActionListener(startColorChooser);
	
	if(!isApplet()) {
	    mi = (JMenuItem) choosers.add(new JMenuItem("File Chooser"));
	    ActionListener startFileChooser = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    ExampleFileFilter filter = new ExampleFileFilter(
			new String[] {"jpg", "gif"}, "JPEG and GIF Image Files"
		    );
		    ExampleFileView fileView = new ExampleFileView();
		    fileView.putIcon("jpg", jpgIcon);
		    fileView.putIcon("gif", gifIcon);
		    chooser.setFileView(fileView);
		    chooser.addChoosableFileFilter(filter);
		    chooser.setFileFilter(filter);
		    chooser.setAccessory(new FilePreviewer(chooser));

		    File swingFile = new File("images/swing-64.gif");
		    if(swingFile.exists()) {
			chooser.setCurrentDirectory(swingFile);
			chooser.setSelectedFile(swingFile);
		    } 

		    int retval = chooser.showOpenDialog(SwingSet.this);
		    if(retval == 0) {
			File theFile = chooser.getSelectedFile();
			if(theFile != null) {
			    JOptionPane.showMessageDialog(SwingSet.this, "You chose this file: " +
							  chooser.getSelectedFile().getAbsolutePath());
			    return;
			}
		    } 
		    JOptionPane.showMessageDialog(SwingSet.this, "No file chosen");
		}
	    };
	    mi.addActionListener(startFileChooser);
	}

	createOptionsMenu(menuBar);
	return menuBar;
    }

    /**
     * Menus
     */
    JMenuBar menuBar;

    JPanel createMenus() {
	JPanel p = createVerticalPanel(true);
	p.setBorder(emptyBorder10);

	// ********************
	// ***** MenuBar ******
	// ********************
	JLabel l = new JLabel("Menus:");
	l.setAlignmentX(LEFT_ALIGNMENT);
	l.setAlignmentY(TOP_ALIGNMENT);
	l.setFont(boldFont);
	p.add(l);
	p.add(Box.createRigidArea(vpad10));
	menuBar = new JMenuBar();
	menuBar.setAlignmentX(LEFT_ALIGNMENT);
	menuBar.setAlignmentY(TOP_ALIGNMENT);
	p.add(menuBar);
	p.add(Box.createRigidArea(vpad40));
	l.setLabelFor(menuBar);	// make label Mnemonic go to menu bar
	l.setDisplayedMnemonic('m');

	// File
	JMenu file = (JMenu) menuBar.add(new JMenu("File"));
	file.setMnemonic('i');
	JMenuItem newItem =
	file.add(new JMenuItem("New", loadImageIcon("images/new.gif","New")));
	newItem.setHorizontalTextPosition(JButton.RIGHT);
	newItem.setMnemonic('N');
	JMenuItem open = (JMenuItem)
	file.add(new JMenuItem("Open", loadImageIcon("images/open.gif","Open")));
	open.setHorizontalTextPosition(JButton.RIGHT);
	open.setMnemonic('O');
	JMenuItem save = (JMenuItem)
	file.add(new JMenuItem("Save", loadImageIcon("images/save.gif","Save")));
	save.setHorizontalTextPosition(JButton.RIGHT);
	save.setMnemonic('S');

	// Edit
	JMenu edit = (JMenu) menuBar.add(new JMenu("Edit"));
	edit.setMnemonic('E');
	JMenuItem cut = (JMenuItem)
	edit.add(new JMenuItem("Cut", loadImageIcon("images/cut.gif","Cut")));
	cut.setHorizontalTextPosition(JButton.RIGHT);
	cut.setMnemonic('t');
	JMenuItem copy = (JMenuItem)
	edit.add(new JMenuItem("Copy", loadImageIcon("images/copy.gif","Copy")));
	copy.setHorizontalTextPosition(JButton.RIGHT);
	copy.setMnemonic('C');
	JMenuItem paste = (JMenuItem)
	edit.add(new JMenuItem("Paste", loadImageIcon("images/paste.gif","Paste")));
	paste.setHorizontalTextPosition(JButton.RIGHT);
	paste.setMnemonic('P');

	// Letters
	JMenu letters = (JMenu) menuBar.add(new JMenu("Letters "));
	letters.setMnemonic('t');

	JMenu letterMenu;
	JMenu subMenu;
        JMenu tmpMenu;

	// C
        letterMenu = (JMenu) letters.add((tmpMenu = new JMenu("A")));
        tmpMenu.setMnemonic('A');
        subMenu = (JMenu) letterMenu.add(new JMenu("A is for Airplane"));
          subMenu.add(new JMenuItem("Cessna 152"));
          subMenu.add(new JMenuItem("Boeing 747"));
          subMenu.add(new JMenuItem("Piper Cherokee"));

        subMenu = (JMenu) letterMenu.add(new JMenu("A is for Alicia Silverstone"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Clueless"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Batman"));

        subMenu = (JMenu) letterMenu.add(new JMenu("A is for Apple"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Fuji"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Granny Smith"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Macintosh"));
	  cb.setSelected(true);

	// B
        letterMenu = (JMenu) letters.add((tmpMenu = new JMenu("B")));
        tmpMenu.setMnemonic('B');
        subMenu = (JMenu) letterMenu.add(new JMenu("B is for Swing Babies!"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Ewan"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Matthew"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Montana"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Nathan"));
	  cb.setSelected(true);

        subMenu = (JMenu) letterMenu.add(new JMenu("B is for Band"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Alice In Chains"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("King Crimson"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Meat Puppets"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Rush"));
	  cb.setSelected(true);

        subMenu = (JMenu) letterMenu.add(new JMenu("B is for Baywatch"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Pam Anderson"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("David Hasslehoff"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Yasmine Bleeth"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Carmine Electra"));

	// C
        letterMenu = (JMenu) letters.add((tmpMenu = new JMenu("C")));
        tmpMenu.setMnemonic('c');
        subMenu = (JMenu) letterMenu.add(new JMenu("C is for Cookie"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Chocolate Chip"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Fortune"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Oatmeal"));
	  cb.setSelected(true);

        subMenu = (JMenu) letterMenu.add(new JMenu("C is for Cool"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("James Dean"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("The Fonz"));
	  cb.setSelected(true);

        subMenu = (JMenu) letterMenu.add(new JMenu("C is for Cats"));
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Ridley"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Quigley"));
	  cb.setSelected(true);
          cb = (JCheckBoxMenuItem) subMenu.add(new JCheckBoxMenuItem("Kizmet "));
	  cb.setSelected(true);


	// Colors
	JMenu colors = (JMenu) menuBar.add(new JMenu("Colors"));
	colors.setMnemonic('C');
	colors.setHorizontalTextPosition(JButton.RIGHT);
	colors.setIcon(new ColoredSquare(Color.orange));
	menuBar.validate();

        JMenuItem red = colors.add(new JMenuItem("Red"));
	red.setHorizontalTextPosition(JButton.RIGHT);
	red.setIcon(new ColoredSquare(Color.red));

        JMenuItem blue = colors.add(new JMenuItem("Blue"));
	blue.setHorizontalTextPosition(JButton.RIGHT);
	blue.setIcon(new ColoredSquare(Color.blue));

        JMenuItem green = colors.add(new JMenuItem("Green"));
	green.setHorizontalTextPosition(JButton.RIGHT);
	green.setIcon(new ColoredSquare(Color.green));

        JMenuItem yellow = colors.add(new JMenuItem("Yellow"));
	yellow.setHorizontalTextPosition(JButton.RIGHT);
	yellow.setIcon(new ColoredSquare(Color.yellow));

	// Numbers
	JMenu numbers = (JMenu) menuBar.add(new JMenu("Numbers"));
	numbers.setMnemonic('u');
        numbers.add(new JMenuItem("1234"));
        numbers.add(new JMenuItem("1005"));
        numbers.add(new JMenuItem("2222"));

	JMenu drinks = (JMenu) menuBar.add(new JMenu("Drinks"));
	drinks.setMnemonic('D');
        drinks.add(new JMenuItem("Thai Iced Tea"));
        drinks.add(new JMenuItem("Root Beer"));
        drinks.add(new JMenuItem("Green Tea"));
        drinks.add(new JMenuItem("Apple Juice"));
	drinks.addSeparator();
	ImageIcon softdrink = 
             loadImageIcon("images/ImageClub/food/softdrink.gif","soft drink");
        drinks.add(new JMenuItem("Softdrink", softdrink));

	JMenu music = (JMenu) menuBar.add(new JMenu("Music"));
	music.setMnemonic('s');
        music.add(new JMenuItem("Rock"));
        music.add(new JMenuItem("Country"));
        music.add(new JMenuItem("Classical"));
        music.add(new JMenuItem("Jazz"));

	JMenu food = (JMenu) menuBar.add(new JMenu("Junk Food"));
	food.setMnemonic('J');
	ImageIcon burger = loadImageIcon("images/ImageClub/food/burger.gif","burger");
	ImageIcon fries  = loadImageIcon("images/ImageClub/food/fries.gif","fries");
	ImageIcon hotdog = loadImageIcon("images/ImageClub/food/hotdog.gif","hot dog");
	ImageIcon pizza  = loadImageIcon("images/ImageClub/food/pizza.gif","pizza");

        addMenuItem(food, "Burger", burger);
	addMenuItem(food, "Fries", fries);
	addMenuItem(food, "Hotdog", hotdog);
	addMenuItem(food, "Pizza", pizza);

	// ********************
	// ****** ToolBar *****
	// ********************
	l = new JLabel("ToolBar:");
	l.setFont(boldFont);
	l.setAlignmentX(LEFT_ALIGNMENT);
	l.setAlignmentY(TOP_ALIGNMENT);
	p.add(l);
	p.add(Box.createRigidArea(vpad10));

	JPanel p1 = (JPanel)p.add(new JPanel());
	p1.setAlignmentX(LEFT_ALIGNMENT);
	p1.setAlignmentY(TOP_ALIGNMENT);

	p1.setLayout(new BorderLayout());
	JToolBar toolBar = new JToolBar();
	addTool(toolBar, "new");
	toolBar.addSeparator( new Dimension(3,3) );
	addTool(toolBar, "open");
	toolBar.addSeparator( new Dimension(3,3) );
	addTool(toolBar, "save");
	toolBar.addSeparator( new Dimension(5,5) );
	toolBar.addSeparator( new Dimension(5,5) );
	addTool(toolBar, "cut");
	toolBar.addSeparator( new Dimension(3,3) );
	addTool(toolBar, "copy");
	toolBar.addSeparator( new Dimension(3,3) );
	addTool(toolBar, "paste");

	toolBar.putClientProperty( "JToolBar.isRollover", Boolean.FALSE );
	l.setLabelFor(toolBar);  // make label Mnemonic go to toolbar
	l.setDisplayedMnemonic('B');

	p1.add(toolBar, BorderLayout.NORTH);

	JPanel textWrapper = new JPanel(new BorderLayout());
	textWrapper.setAlignmentX(LEFT_ALIGNMENT);
 	textWrapper.setBorder(swing.loweredBorder);
	
	p1.add(textWrapper, BorderLayout.CENTER);

	String text = SwingSet.contentsOfFile("ToolBar.txt");
	JTextArea textArea = new JTextArea(text);
	textArea.getAccessibleContext().setAccessibleName("ToolBar information");
	JScrollPane scroller = new JScrollPane() {
	    public Dimension getPreferredSize() {
		return new Dimension(10,10);
	    }
	    public float getAlignmentX() {
		return LEFT_ALIGNMENT;
	    }
	};
	scroller.getViewport().add(textArea);
	textArea.setFont(new Font("Dialog", Font.PLAIN, 12));
	textWrapper.add(scroller, BorderLayout.CENTER);
	
	textArea.setEditable(false);
	return p;
    }

    void createOptionsMenu(JMenuBar menuBar) {
	JMenu optionMenu = (JMenu)menuBar.add(new JMenu("Dialogs"));
        optionMenu.setMnemonic('D');
	JMenuItem item;
	item = new JMenuItem("Message Dialog");
	item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(SwingSet.this, "Plain message");
	    }
	});
	optionMenu.add(item);

	item = new JMenuItem("Warning Dialog");
	item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(SwingSet.this, "Example Warning",
				    "Warning", JOptionPane.WARNING_MESSAGE);
	    }
	});
	optionMenu.add(item);

	item = new JMenuItem("Confirmation Dialog");
	item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int        result;
		result = JOptionPane.showConfirmDialog(SwingSet.this, "Is SWING cool?");
		if(result == JOptionPane.YES_OPTION)
		    JOptionPane.showMessageDialog(SwingSet.this, "All right!");
		else if(result == JOptionPane.NO_OPTION)
		    JOptionPane.showMessageDialog(SwingSet.this, "That is too bad, please send us email describing what you don't like and how we can change it.");
	    }
	});
	optionMenu.add(item);

	item = new JMenuItem("Input Dialog");
	item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String          result;

		result = JOptionPane.showInputDialog(SwingSet.this, "Please enter your name:");
		if(result != null) {
		    Object[] message = new Object[2];
		    message[0] = "Thank you for using SWING ";
		    message[1] = result;
		    JOptionPane.showMessageDialog(SwingSet.this, message);
		}
	    }
	});
	optionMenu.add(item);

	item = new JMenuItem("Component Dialog");
	item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		Object[]      message = new Object[4];
                JComboBox cb = new JComboBox();
                cb.addItem("One");
                cb.addItem("Two");
                cb.addItem("Three");
		message[0] = "JOptionPane can contain any number of components, and any number options.";
		message[1] = new JButton("a button");
		message[2] = new JTextField("a text field");
                message[3] = cb;
                

		String[]      options = { "Option 1", "Option 2", "Option 3",
					  "Option 4" };
		JOptionPane.showOptionDialog(SwingSet.this, message, "Example", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
	    }
	});
	optionMenu.add(item);
    }
	

    void addMenuItem(JMenu menu, String text, Icon g) {
	JMenuItem mi = menu.add(new JMenuItem(text, g));
	mi.setHorizontalTextPosition(JButton.CENTER);
	mi.setHorizontalAlignment(JButton.LEFT);
	mi.setVerticalTextPosition(JButton.BOTTOM);
    }

    public void addTool(JToolBar toolBar, String name) {
	JButton b = 
           (JButton) toolBar.add(
               new JButton(loadImageIcon("images/" + name + ".gif",name)));
	b.setToolTipText(name);
	b.setMargin(insets0);
	b.getAccessibleContext().setAccessibleName(name);
    }

    /**
     * Text
     */
    JPanel createText() {
	return new JPanel();
    }

    /**
     * Tab Listener
     */
    void createTabListener() {
	// add listener to know when we've been shown
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tab = (JTabbedPane) e.getSource();
                int index = tab.getSelectedIndex();
                Component currentPage = tab.getComponentAt(index);
		RepaintManager repaintManager = 
                    RepaintManager.currentManager(instance);

		if(!repaintManager.isDoubleBufferingEnabled()) {
		  repaintManager.setDoubleBufferingEnabled(true);
		}

		if(previousPage == debugGraphicsPanel) {
		    ((DebugGraphicsPanel)debugGraphicsPanel).resetAll();
		}

                if(currentPage == buttonPanel) {
		    currentControls = buttons;
		} else if(currentPage == radioButtonPanel) {
		    currentControls = radioButtons;
		} else if(currentPage == toggleButtonPanel) {
		    currentControls = toggleButtons;
		} else if(currentPage == checkboxPanel) {
		    currentControls = checkboxes;
		} else if(currentPage == listBoxPanel) {
		    ((ListPanel)listBoxPanel).resetAll();
		} else if(currentPage == debugGraphicsPanel) {
		    repaintManager.setDoubleBufferingEnabled(false);
		    invalidate();
		    validate();
		} else if(currentPage == labelPanel) {
		    currentControls = labels;
                }
                previousPage = currentPage;
            }
        };
        tabbedPane.addChangeListener(changeListener);
    }



    /**
     *
     */
    JPanel createControllButtons() {
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
	p.setBorder(emptyBorder5);

	return p;
    }

    /**
     * create Logo
     */
    JPanel createLogo() {
	JPanel p = new JPanel();
	p.setLayout(new BorderLayout());
	ImageIcon logo = loadImageIcon("images/AboutSwing.jpg","Swing!");
	JLabel logoLabel = new JLabel(logo);
	logoLabel.getAccessibleContext().setAccessibleName("Swing!");
	p.add(logoLabel, BorderLayout.CENTER);
	p.setBorder(new MatteBorder(6,6,6,6, 
                SwingSet.sharedInstance().loadImageIcon(
                        "images/AboutBorder.gif","About Box Border")
                )
        );

	return p;
    }


    public static void main(String[] args) {
        String vers = System.getProperty("java.version");
        if (vers.compareTo("1.1.2") < 0) {
            System.out.println("!!!WARNING: Swing must be run with a " +
                               "1.1.2 or higher version VM!!!");
        }

	// Force SwingSet to come up in the Cross Platform L&F
	try {
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    // If you want the System L&F instead, comment out the above line and
	    // uncomment the following:
	    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception exc) {
	    System.out.println("Error loading L&F: " + exc);
	}

	WindowListener l = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	};

	frame = new JFrame("SwingSet");
	frame.addWindowListener(l);
	frame.getAccessibleContext().setAccessibleDescription("A sample application to demonstrate the Swing UI components");

	JOptionPane.setRootFrame(frame);

	JPanel progressPanel = new JPanel() {
	    public Insets getInsets() {
		return new Insets(40,30,20,30);
	    }
	};
	progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
	frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

	Dimension d = new Dimension(400, 20);
	SwingSet.progressLabel = new JLabel("Loading, please wait...");
	SwingSet.progressLabel.setAlignmentX(CENTER_ALIGNMENT);
	SwingSet.progressLabel.setMaximumSize(d);
	SwingSet.progressLabel.setPreferredSize(d);
	progressPanel.add(progressLabel);
	progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

	SwingSet.progressBar = new JProgressBar(0, SwingSet.totalPanels);
	SwingSet.progressBar.setStringPainted(true);
	SwingSet.progressLabel.setLabelFor(progressBar);
	SwingSet.progressBar.setAlignmentX(CENTER_ALIGNMENT);
	SwingSet.progressBar.getAccessibleContext().setAccessibleName("SwingSet loading progress");
	progressPanel.add(SwingSet.progressBar);

	// show the frame
	frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setLocation(screenSize.width/2 - INITIAL_WIDTH/2,
			  screenSize.height/2 - INITIAL_HEIGHT/2);
	frame.show();

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	SwingSet sw = new SwingSet();
	frame.getContentPane().removeAll();
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(sw, BorderLayout.CENTER);
	frame.setLocation(screenSize.width/2 - WIDTH/2,
			  screenSize.height/2 - HEIGHT/2);

	frame.setSize(WIDTH, HEIGHT);
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	frame.validate();
	frame.repaint();
        sw.requestDefaultFocus();
    }


    /**
     * Switch the between the Windows, Motif, Mac, and the Java Look and Feel
     */
    class ToggleUIListener implements ItemListener {
	public void itemStateChanged(ItemEvent e) {
	    Component root = SwingSet.sharedInstance().getRootComponent();
	    root.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
            try {
	       if(rb.isSelected() && rb.getText().equals("Windows Style Look and Feel")) {
		   currentUI = "Windows";
	    	   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                   tabPlacement.setEnabled(true);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } else if(rb.isSelected() && rb.getText().equals("Macintosh Look and Feel")) {
		   currentUI = "Macintosh";
	    	   UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
                   tabPlacement.setEnabled(false);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } else if(rb.isSelected() && rb.getText().equals("Motif Look and Feel")) {
		   currentUI = "Motif";
	    	   UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                   tabPlacement.setEnabled(true);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } else if(rb.isSelected() && rb.getText().equals("Java Look and Feel")) {
		   currentUI = "Metal";
                   // javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(
		   //      new javax.swing.plaf.metal.DefaultMetalTheme());
	    	   UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                   tabPlacement.setEnabled(true);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } 
	       /* non-supported stuff
	       else if(rb.isSelected() && rb.getText().equals(
	              "Java Look and Feel (High Contrast Theme)")) {
	    	   javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(
		      new javax.swing.plaf.metal.ContrastMetalTheme());
	    	   UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } else if(rb.isSelected() && rb.getText().equals("Organic Look and Feel (Santa Fe)")) {
	    	   javax.swing.plaf.organic.OrganicLookAndFeel.setCurrentTheme(
		      new javax.swing.plaf.organic.OrganicDefaultTheme());
	    	   UIManager.setLookAndFeel("javax.swing.plaf.organic.OrganicLookAndFeel");
                   tabPlacement.setEnabled(true);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } else if(rb.isSelected() && rb.getText().equals("Organic Look and Feel (Vancouver)")) {
	    	   javax.swing.plaf.organic.OrganicLookAndFeel.setCurrentTheme(
		      new javax.swing.plaf.organic.OrganicGrayTheme());
	    	   UIManager.setLookAndFeel("javax.swing.plaf.organic.OrganicLookAndFeel");
                   tabPlacement.setEnabled(true);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       } else if(rb.isSelected() && rb.getText().equals("Organic Look and Feel (Dallas)")) {
	    	   javax.swing.plaf.organic.OrganicLookAndFeel.setCurrentTheme(
		      new javax.swing.plaf.organic.OrganicBigTheme());
	    	   UIManager.setLookAndFeel("javax.swing.plaf.organic.OrganicLookAndFeel");
                   tabPlacement.setEnabled(true);
	    	   SwingUtilities.updateComponentTreeUI(getRootComponent());
	       }
               */
            } catch (UnsupportedLookAndFeelException exc) {
		// Error - unsupported L&F
		rb.setEnabled(false);
                System.err.println("Unsupported LookAndFeel: " + rb.getText());
		
		// Set L&F to JLF
		try {
		    currentUI = "Metal";
		    metalMenuItem.setSelected(true);
		    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		    tabPlacement.setEnabled(true);
		    SwingUtilities.updateComponentTreeUI(getRootComponent());
		} catch (Exception exc2) {
		  exc2.printStackTrace();
		    System.err.println("Could not load LookAndFeel: " + exc2);
		    exc2.printStackTrace();
		}
            } catch (Exception exc) {
                rb.setEnabled(false);
		  exc.printStackTrace();
                System.err.println("Could not load LookAndFeel: " + rb.getText());
		exc.printStackTrace();
            }

	    root.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

    }
            

   class TabPlacementChanger extends JMenu implements ItemListener {
       JRadioButtonMenuItem topRb, leftRb, rightRb, bottomRb;

       public TabPlacementChanger() {
           super("Tab Placement");

           ButtonGroup tabGroup = new ButtonGroup();

           topRb = (JRadioButtonMenuItem)add(new JRadioButtonMenuItem("Top"));
	   topRb.getAccessibleContext().setAccessibleDescription("Position the TabbedPane on the top of the window");
           topRb.setSelected(true);
           topRb.addItemListener(this);
           tabGroup.add(topRb);
        
           leftRb = (JRadioButtonMenuItem)add(new JRadioButtonMenuItem("Left"));
	   leftRb.getAccessibleContext().setAccessibleDescription("Position the TabbedPane on the left of the window");
           leftRb.addItemListener(this);
           tabGroup.add(leftRb);

           bottomRb = (JRadioButtonMenuItem)add(new JRadioButtonMenuItem("Bottom"));
	   bottomRb.getAccessibleContext().setAccessibleDescription("Position the TabbedPane on the bottom of the window");
           bottomRb.addItemListener(this);
           tabGroup.add(bottomRb);

           rightRb = (JRadioButtonMenuItem)add(new JRadioButtonMenuItem("Right"));
	   rightRb.getAccessibleContext().setAccessibleDescription("Position the TabbedPane on the right of the window");
           rightRb.addItemListener(this);
           tabGroup.add(rightRb);
        }

	public void itemStateChanged(ItemEvent e) {
	    JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.getSource();
            if (rb.isSelected()) {
                String selected = rb.getText();
                int placement;
                if (selected.equals("Top")) {
                    placement = JTabbedPane.TOP;
                } else if (selected.equals("Left")) {
                    placement = JTabbedPane.LEFT;
                } else if (selected.equals("Bottom")) {
                    placement = JTabbedPane.BOTTOM;
                } else {
                    placement = JTabbedPane.RIGHT;
                }
                tabbedPane.setTabPlacement(placement);
                tabbedPane.validate();
            }
        }
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (!enabled) {
                topRb.setSelected(true);
            } else {
                int placement = tabbedPane.getTabPlacement();
                switch(placement) {
                  case JTabbedPane.TOP:
                      topRb.setSelected(true);
                      break;
                  case JTabbedPane.LEFT:
                      leftRb.setSelected(true);
                      break;
                  case JTabbedPane.BOTTOM:
                      bottomRb.setSelected(true);
                      break;
                  case JTabbedPane.RIGHT:
                      rightRb.setSelected(true);
                      break;
                  default:
                }
            }
        }
    }

                    

    // ***********************************************************
    // *********** Create the button controls listeners **********
    // ***********************************************************
    ItemListener buttonPadListener = new ItemListener() {
	Component c;
	AbstractButton b;

	public void itemStateChanged(ItemEvent e) {
	    // *** pad = 0
	    int pad = -1;
	    JRadioButton rb = (JRadioButton) e.getSource();
	    if(rb.getText().equals("0") && rb.isSelected()) {
		pad = 0;
	    } else if(rb.getText().equals("10") && rb.isSelected()) {
		pad = 10;
	    } 

	    for(int i = 0; i < currentControls.size(); i++) {
		b = (AbstractButton) currentControls.elementAt(i);
		if(pad == -1) {
		    b.setMargin(null);
		} else if(pad == 0) {
		    b.setMargin(insets0);
		} else {
		    b.setMargin(insets10);
		}
	    }
	    int index = tabbedPane.getSelectedIndex();
	    Component currentPage = tabbedPane.getComponentAt(index);
	    currentPage.invalidate();
	    currentPage.validate();
	    currentPage.repaint();
	}
    };

    ItemListener buttonDisplayListener = new ItemListener() {
	Component c;
	AbstractButton b;

	public void itemStateChanged(ItemEvent e) {
	    JCheckBox cb = (JCheckBox) e.getSource();
	    if(cb.getText().equals("Enabled")) {
		for(int i = 0; i < currentControls.size(); i++) {
		    c = (Component) currentControls.elementAt(i);
		    c.setEnabled(cb.isSelected());
		    c.invalidate();
		}
	    } else if(cb.getText().equals("Paint Border")) {
		c = (Component) currentControls.elementAt(0);
		if(c instanceof AbstractButton) {
		    for(int i = 0; i < currentControls.size(); i++) {
			b = (AbstractButton) currentControls.elementAt(i);
			b.setBorderPainted(cb.isSelected());
		        b.invalidate();
		    }
		}
	    } else if(cb.getText().equals("Paint Focus")) {
		c = (Component) currentControls.elementAt(0);
		if(c instanceof AbstractButton) {
		    for(int i = 0; i < currentControls.size(); i++) {
			b = (AbstractButton) currentControls.elementAt(i);
			b.setFocusPainted(cb.isSelected());
		        b.invalidate();
		    }
		}
	    } else if(cb.getText().equals("Content Filled")) {
		c = (Component) currentControls.elementAt(0);
		if(c instanceof AbstractButton) {
		    for(int i = 0; i < currentControls.size(); i++) {
			b = (AbstractButton) currentControls.elementAt(i);
			b.setContentAreaFilled(cb.isSelected());
		        b.invalidate();
		    }
		}
	    }
	    int index = tabbedPane.getSelectedIndex();
	    Component currentPage = tabbedPane.getComponentAt(index);
	    currentPage.invalidate();
	    currentPage.validate();
	    currentPage.repaint();
	}
    };

    // Title Pane tile position
    ActionListener borderedPaneListener = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    JRadioButton b = (JRadioButton) e.getSource();
	    if(b.getText().equals("Above Top")) {((TitledBorder)borderedPane.getBorder()).setTitlePosition(TitledBorder.ABOVE_TOP);}
	    if(b.getText().equals("Top")) {((TitledBorder)borderedPane.getBorder()).setTitlePosition(TitledBorder.TOP);}
	    if(b.getText().equals("Below Top")) {((TitledBorder)borderedPane.getBorder()).setTitlePosition(TitledBorder.BELOW_TOP);}

	    if(b.getText().equals("Above Bottom")) {((TitledBorder)borderedPane.getBorder()).setTitlePosition(TitledBorder.ABOVE_BOTTOM);}
	    if(b.getText().equals("Bottom")) {((TitledBorder)borderedPane.getBorder()).setTitlePosition(TitledBorder.BOTTOM);}
	    if(b.getText().equals("Below Bottom")) {((TitledBorder)borderedPane.getBorder()).setTitlePosition(TitledBorder.BELOW_BOTTOM);}

	    if(b.getText().equals("Left")) {((TitledBorder)borderedPane.getBorder()).setTitleJustification(TitledBorder.LEFT);}
	    if(b.getText().equals("Center")) {((TitledBorder)borderedPane.getBorder()).setTitleJustification(TitledBorder.CENTER);}
	    if(b.getText().equals("Right")) {((TitledBorder)borderedPane.getBorder()).setTitleJustification(TitledBorder.RIGHT);}

	    borderedPane.invalidate();
	    borderedPane.validate();
	    borderedPane.repaint();
	}
    };

    public static JPanel createHorizontalPanel(boolean threeD) {
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
	if(threeD) {
	    p.setBorder(loweredBorder);
	}
	return p;
    }

    public static JPanel createVerticalPanel(boolean threeD) {
	JPanel p = new JPanel();
	p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
	if(threeD) {
	    p.setBorder(loweredBorder);
	}	
	return p;
    }

    public static String contentsOfFile(String filename) {
	String s = new String();
	File f;
	char[] buff = new char[50000];
	InputStream is;
	InputStreamReader reader;
	boolean fromApplet = SwingSet.sharedInstance().isApplet();
	URL url;

	try {
	  if(fromApplet) {
	    url = new URL(SwingSet.sharedInstance().getApplet().getCodeBase(),filename);
	    is = url.openStream();
	    reader = new InputStreamReader(is);
	  } else {
	    f = new File(filename);
	    reader = new FileReader(f);
	  }
	  int nch;
	  while ((nch = reader.read(buff, 0, buff.length)) != -1) {
	    s = s + new String(buff, 0, nch);
	  }
	} catch (java.io.IOException ex) {
	    s = "Could not load file: " + filename;
	}

	return s;
    }

    class ColoredSquare implements Icon {
	Color color;
	public ColoredSquare(Color c) {
	    this.color = c;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    Color oldColor = g.getColor();
	    g.setColor(color);
	    g.fill3DRect(x,y,getIconWidth(), getIconHeight(), true);
	    g.setColor(oldColor);
	}
	public int getIconWidth() { return 12; }
	public int getIconHeight() { return 12; }

    }

  public ImageIcon loadImageIcon(String filename, String description) {
    if(applet == null) {
      return new ImageIcon(filename, description);
    } else {
      URL url;
      try {
	url = new URL(applet.getCodeBase(),filename);
      } catch(MalformedURLException e) {
	  System.err.println("Error trying to load image " + filename);
	  return null;
      }
      return new ImageIcon(url, description);
    }
  }

  public static SwingSet sharedInstance() {
    return instance;
  }

  public java.applet.Applet getApplet() {
    return applet;
  }

  public boolean isApplet() {
    return (applet != null);
  }

  public Container getRootComponent() {
    if(isApplet())
      return applet;
    else
      return frame;
  }

  public Frame getFrame() {
    if(isApplet()) {
      Container parent;
      for(parent = getApplet(); parent != null && !(parent instanceof Frame) ; parent = parent.getParent());
      if(parent != null)
	return (Frame)parent;
      else
	return null;
    } else
      return frame;
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
}
