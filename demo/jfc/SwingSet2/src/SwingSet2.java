/*
 * @(#)SwingSet2.java	1.19 99/11/08
 *
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
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
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;

import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import java.lang.reflect.*;
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
 * @version 1.19 11/08/99
 * @author Jeff Dinkins
 */
public class SwingSet2 extends JPanel {

    String[] demos = {
      "ButtonDemo",
      "ColorChooserDemo",
      "ComboBoxDemo",
      "FileChooserDemo",
      "HtmlDemo",
      "ListDemo",
      "OptionPaneDemo",
      "ProgressBarDemo",
      "ScrollPaneDemo",
      "SliderDemo",
      "SplitPaneDemo",
      "TabbedPaneDemo",
      "TableDemo",
      "ToolTipDemo",
      "TreeDemo"
    };

    void loadDemos() {
	for(int i = 0; i < demos.length;) {
            if(isApplet() && demos[i].equals("FileChooserDemo")) {
	       // don't load the file chooser demo if we are
               // an applet
	    } else {
	       loadDemo(demos[i]);
            }
	    i++;
	}
    }

    // Possible Look & Feels
    private String mac      = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    private String metal    = "javax.swing.plaf.metal.MetalLookAndFeel";
    private String motif    = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private String windows  = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    // The current Look & Feel
    private String currentLookAndFeel = metal;

    // List of demos
    private Vector demosVector = new Vector();

    // The preferred size of the demo
    private int PREFERRED_WIDTH = 680;
    private int PREFERRED_HEIGHT = 640;
    
    // Box spacers
    private Dimension HGAP = new Dimension(1,5);
    private Dimension VGAP = new Dimension(5,1);

    // Resource bundle for internationalized and accessible text
    private ResourceBundle bundle = null;

    // A place to hold on to the visible demo
    private DemoModule currentDemo = null;
    private JPanel demoPanel = null;

    // About Box
    private JDialog aboutBox = null;

    // Status Bar
    private JTextField statusField = null;

    // Tool Bar
    private ToggleButtonToolBar toolbar = null;
    private ButtonGroup toolbarGroup = new ButtonGroup();

    // Menus
    private JMenuBar menuBar = null;
    private JMenu themesMenu = null;
    private ButtonGroup lafMenuGroup = new ButtonGroup();
    private ButtonGroup themesMenuGroup = new ButtonGroup();

    // Used only if swingset is an application 
    private static JFrame frame = null;
    private JWindow splashScreen = null;

    // Used only if swingset is an applet 
    private SwingSet2Applet applet = null;

    // To debug or not to debug, that is the question
    private boolean DEBUG = true;
    private int debugCounter = 0;

    // The tab pane that holds the demo
    private JTabbedPane tabbedPane = null;

    private JEditorPane demoSrcPane = null;

    private JLabel splashLabel = null;

    // contentPane cache, saved from the applet or application frame
    Container contentPane = null;


    /**
     * SwingSet2 Constructor
     */
    public SwingSet2(SwingSet2Applet applet) {
	// Note that the applet may null if this is started as an application
	this.applet = applet;

	// setLayout(new BorderLayout());
	setLayout(new BorderLayout());

	// set the preferred size of the demo
	setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));

	// Create and throw the splash screen up. Since this will
	// physically throw bits on the screen, we need to do this
	// on the GUI thread using invokeLater.
	createSplashScreen();

	// do the following on the gui thread
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		showSplashScreen();
	    }
	});
	    
	initializeDemo();
	preloadFirstDemo();

	// Show the demo and take down the splash screen. Note that
	// we again must do this on the GUI thread using invokeLater.
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		showSwingSet2();
		hideSplash();
	    }
	});

	// Start loading the rest of the demo in the background
	DemoLoadThread demoLoader = new DemoLoadThread(this);
	demoLoader.start();
    }


    /**
     * SwingSet2 Main. Called only if we're an application, not an applet.
     */
    public static void main(String[] args) {
	frame = createFrame();
	SwingSet2 swingset = new SwingSet2(null);
    }

    // *******************************************************
    // *************** Demo Loading Methods ******************
    // *******************************************************
    
    
    
    public void initializeDemo() {
	JPanel top = new JPanel();
	top.setLayout(new BorderLayout());
	add(top, BorderLayout.NORTH);

	menuBar = createMenus();
	top.add(menuBar, BorderLayout.NORTH);

	JPanel toolbarPanel = new JPanel();
	toolbarPanel.setLayout(new BorderLayout());
	toolbar = new ToggleButtonToolBar();
	toolbarPanel.add(toolbar, BorderLayout.CENTER);
	top.add(toolbarPanel, BorderLayout.SOUTH);

	tabbedPane = new JTabbedPane();
	add(tabbedPane, BorderLayout.CENTER);
	tabbedPane.getModel().addChangeListener(new TabListener());

	statusField = new JTextField("");
	statusField.setEditable(false);
	add(statusField, BorderLayout.SOUTH);
	
	demoPanel = new JPanel();
	demoPanel.setLayout(new BorderLayout());
	demoPanel.setBorder(new EtchedBorder());
	tabbedPane.addTab("Hi There!", demoPanel);
	
	// Add html src code viewer 
	demoSrcPane = new JEditorPane("text/html", getString("SourceCode.loading"));
	demoSrcPane.setEditable(false);
    
	JPanel p = new JPanel();
	p.setLayout(new BorderLayout() );
	p.add(demoSrcPane, BorderLayout.CENTER);
    
	JScrollPane scroller = new JScrollPane();
	scroller.getViewport().add(p);
    
	tabbedPane.addTab(
	    getString("TabbedPane.src_label"),
	    null,
	    scroller,
	    getString("TabbedPane.src_tooltip")
	);
    }

    DemoModule currentTabDemo = null;
    class TabListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    SingleSelectionModel model = (SingleSelectionModel) e.getSource();
	    boolean srcSelected = model.getSelectedIndex() == 1;
	    if(currentTabDemo != currentDemo && demoSrcPane != null && srcSelected) {
		demoSrcPane.setText(getString("SourceCode.loading"));
		repaint();
	    }
	    if(currentTabDemo != currentDemo && srcSelected) {
		currentTabDemo = currentDemo;
		setSourceCode(currentDemo);
	    } 
	}
    }


    /**
     * Create menus
     */
    public JMenuBar createMenus() {
	JMenuItem mi;
	// ***** create the menubar ****
	JMenuBar menuBar = new JMenuBar();
	menuBar.getAccessibleContext().setAccessibleName(
	    getString("MenuBar.accessible_description"));

	// ***** create File menu 
	JMenu fileMenu = (JMenu) menuBar.add(new JMenu(getString("FileMenu.file_label")));
        fileMenu.setMnemonic(getMnemonic("FileMenu.file_mnemonic"));
	fileMenu.getAccessibleContext().setAccessibleDescription(getString("FileMenu.accessible_description"));

	createMenuItem(fileMenu, "FileMenu.about_label", "FileMenu.about_mnemonic",
		       "FileMenu.about_accessible_description", new AboutAction(this));

        fileMenu.addSeparator();

	createMenuItem(fileMenu, "FileMenu.open_label", "FileMenu.open_mnemonic",
		       "FileMenu.open_accessible_description", null);

	createMenuItem(fileMenu, "FileMenu.save_label", "FileMenu.save_mnemonic",
		       "FileMenu.save_accessible_description", null);

	createMenuItem(fileMenu, "FileMenu.save_as_label", "FileMenu.save_as_mnemonic",
		       "FileMenu.save_as_accessible_description", null);


	if(!isApplet()) {
	    fileMenu.addSeparator();
	    
	    createMenuItem(fileMenu, "FileMenu.exit_label", "FileMenu.exit_mnemonic",
			   "FileMenu.exit_accessible_description", new ExitAction(this)
	    );
	}


	// ***** create laf switcher menu 
	JMenu lafMenu = (JMenu) menuBar.add(new JMenu(getString("LafMenu.laf_label")));
        lafMenu.setMnemonic(getMnemonic("LafMenu.laf_mnemonic"));
	lafMenu.getAccessibleContext().setAccessibleDescription(
	    getString("LafMenu.laf_accessible_description"));

	mi = createLafMenuItem(lafMenu, "LafMenu.java_label", "LafMenu.java_mnemonic",
		       "LafMenu.java_accessible_description", metal);
	mi.setSelected(true); // this is the default l&f

	createLafMenuItem(lafMenu, "LafMenu.mac_label", "LafMenu.mac_mnemonic",
		       "LafMenu.mac_accessible_description", mac);

	createLafMenuItem(lafMenu, "LafMenu.motif_label", "LafMenu.motif_mnemonic",
		       "LafMenu.motif_accessible_description", motif);

	createLafMenuItem(lafMenu, "LafMenu.windows_label", "LafMenu.windows_mnemonic",
		       "LafMenu.windows_accessible_description", windows);

	// ***** create themes menu 
	themesMenu = (JMenu) menuBar.add(new JMenu(getString("ThemesMenu.themes_label")));
        themesMenu.setMnemonic(getMnemonic("ThemesMenu.themes_mnemonic"));
	themesMenu.getAccessibleContext().setAccessibleDescription(
	    getString("ThemesMenu.themes_accessible_description"));

	mi = createThemesMenuItem(themesMenu, "ThemesMenu.default_label", "ThemesMenu.default_mnemonic",
		       "ThemesMenu.default_accessible_description", new DefaultMetalTheme());
	mi.setSelected(true); // This is the default theme
	
	createThemesMenuItem(themesMenu, "ThemesMenu.aqua_label", "ThemesMenu.aqua_mnemonic",
		       "ThemesMenu.aqua_accessible_description", new AquaTheme());

	createThemesMenuItem(themesMenu, "ThemesMenu.charcoal_label", "ThemesMenu.charcoal_mnemonic",
		       "ThemesMenu.charcoal_accessible_description", new CharcoalTheme());

	createThemesMenuItem(themesMenu, "ThemesMenu.contrast_label", "ThemesMenu.contrast_mnemonic",
		       "ThemesMenu.contrast_accessible_description", new ContrastTheme());

	createThemesMenuItem(themesMenu, "ThemesMenu.emerald_label", "ThemesMenu.emerald_mnemonic",
		       "ThemesMenu.emerald_accessible_description", new EmeraldTheme());

	createThemesMenuItem(themesMenu, "ThemesMenu.ruby_label", "ThemesMenu.ruby_mnemonic",
		       "ThemesMenu.ruby_accessible_description", new RubyTheme());

	return menuBar;
    }

    /**
     * Creates a generic menu item
     */
    public JMenuItem createMenuItem(JMenu menu, String label, String mnemonic,
			       String accessibleDescription, Action action) {
        JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(getString(label)));
	mi.setMnemonic(getMnemonic(mnemonic));
	mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
	mi.addActionListener(action);
	if(action == null) {
	    mi.setEnabled(false);
	}
	return mi;
    }

    /**
     * Creates a JRadioButtonMenuItem for the Themes menu
     */
    public JMenuItem createThemesMenuItem(JMenu menu, String label, String mnemonic,
			       String accessibleDescription, DefaultMetalTheme theme) {
        JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(getString(label)));
	themesMenuGroup.add(mi);
	mi.setMnemonic(getMnemonic(mnemonic));
	mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
	mi.addActionListener(new ChangeThemeAction(this, theme));

	return mi;
    }

    /**
     * Creates a JRadioButtonMenuItem for the Look and Feel menu
     */
    public JMenuItem createLafMenuItem(JMenu menu, String label, String mnemonic,
			       String accessibleDescription, String laf) {
        JMenuItem mi = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(getString(label)));
	lafMenuGroup.add(mi);
	mi.setMnemonic(getMnemonic(mnemonic));
	mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
	mi.addActionListener(new ChangeLookAndFeelAction(this, laf));

	mi.setEnabled(isAvailableLookAndFeel(laf));

	return mi;
    }

    /**
     * Load the first demo. This is done separately from the remaining demos
     * so that we can get SwingSet2 up and available to the user quickly.
     */
    public void preloadFirstDemo() {
	DemoModule demo = addDemo(new InternalFrameDemo(this));
	setDemo(demo);
    }


    /**
     * Add a demo to the toolbar
     */
    public DemoModule addDemo(DemoModule demo) {
	demosVector.addElement(demo);
	// do the following on the gui thread
	SwingUtilities.invokeLater(new SwingSetRunnable(this, demo) {
	    public void run() {
		SwitchToDemoAction action = new SwitchToDemoAction(swingset, (DemoModule) obj);
		JToggleButton tb = swingset.getToolBar().addToggleButton(action);
		swingset.getToolBarGroup().add(tb);
		if(swingset.getToolBarGroup().getSelection() == null) {
		    tb.setSelected(true);
		}
		tb.setText(null);
		tb.setToolTipText(((DemoModule)obj).getToolTip());

		if(demos[demos.length-1].equals(obj.getClass().getName())) {
		    setStatus("");
		} 
		  
	    }
	});
	return demo;
    }


    /**
     * Sets the current demo
     */
    public void setDemo(DemoModule demo) {
	currentDemo = demo;

	demoPanel.removeAll();
	demoPanel.add(demo.getDemoPanel(), BorderLayout.CENTER);

	tabbedPane.setSelectedIndex(0);
	tabbedPane.setTitleAt(0, demo.getName());
	tabbedPane.setToolTipTextAt(0, demo.getToolTip());
    }


    /**
     * Bring up the SwingSet2 demo by showing the frame (only
     * applicable if coming up as an application, not an applet);
     */
    public void showSwingSet2() {
	if(!isApplet() && getFrame() != null) {
	    // put swingset in a frame and show it
	    JFrame f = getFrame();
	    f.setTitle(getString("Frame.title"));
	    f.getContentPane().add(this, BorderLayout.CENTER);
	    f.pack();
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    getFrame().setLocation(
		screenSize.width/2 - f.getSize().width/2,
		screenSize.height/2 - f.getSize().height/2);
	    getFrame().show();
	} 
    }

    /**
     * Show the spash screen while the rest of the demo loads
     */
    public void createSplashScreen() {
	splashLabel = new JLabel(createImageIcon("Splash.jpg", "Splash.accessible_description"));
	
	if(!isApplet()) {
	    splashScreen = new JWindow();
	    splashScreen.getContentPane().add(splashLabel);
	    splashScreen.pack();
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    splashScreen.setLocation(screenSize.width/2 - splashScreen.getSize().width/2,
				     screenSize.height/2 - splashScreen.getSize().height/2);
	} 
    }

    public void showSplashScreen() {
	if(!isApplet()) {
	    splashScreen.show();
	} else {
	    add(splashLabel, BorderLayout.CENTER);
	    validate();
	    repaint();
	}
    }

    /**
     * pop down the spash screen
     */
    public void hideSplash() {
	if(!isApplet()) {
	    splashScreen.setVisible(false);
	    splashScreen = null;
	    splashLabel = null;
	}
    }

    // *******************************************************
    // ****************** Utility Methods ********************
    // *******************************************************

    /**
     * Loads a demo from a classname
     */
    void loadDemo(String classname) {
	setStatus(getString("Status.loading") + getString(classname + ".name"));
	DemoModule demo = null;
	try {
	    Class demoClass = Class.forName(classname);
	    Constructor demoConstructor = demoClass.getConstructor(new Class[]{SwingSet2.class});
	    demo = (DemoModule) demoConstructor.newInstance(new Object[]{this});
	    addDemo(demo);
	} catch (Exception e) {
	    System.out.println("Error occurred loading demo: " + classname);
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
         } catch(Exception e) { // If ANYTHING weird happens, return false
             return false;
         }
     }


    /**
     * Determines if this is an applet or application
     */
    public boolean isApplet() {
	return (applet != null);
    }

    /**
     * Returns the applet instance
     */
    public SwingSet2Applet getApplet() {
	return applet;
    }


    /**
     * Returns the frame instance
     */
    public JFrame getFrame() {
	return frame;
    }

    /**
     * Returns the menubar
     */
    public JMenuBar getMenuBar() {
	return menuBar;
    }

    /**
     * Returns the toolbar
     */
    public ToggleButtonToolBar getToolBar() {
	return toolbar;
    }

    /**
     * Returns the toolbar button group
     */
    public ButtonGroup getToolBarGroup() {
	return toolbarGroup;
    }

    /**
     * Returns the content pane wether we're in an applet
     * or application
     */
    public Container getContentPane() {
	if(contentPane == null) {
	    if(getFrame() != null) {
		contentPane = getFrame().getContentPane();
	    } else if (getApplet() != null) {
		contentPane = getApplet().getContentPane();
	    }
	}
	return contentPane;
    }

    /**
     * Create a frame for SwingSet2 to reside in if brought up
     * as an application.
     */
    public static JFrame createFrame() {
	JFrame frame = new JFrame();
	WindowListener l = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	};
	frame.addWindowListener(l);
	return frame;
    }


    /**
     * Set the status 
     */
    public void setStatus(String s) {
	// do the following on the gui thread
	SwingUtilities.invokeLater(new SwingSetRunnable(this, s) {
	    public void run() {
		swingset.statusField.setText((String) obj);
	    }
	});
    }


    /**
     * This method returns a string from the demo's resource bundle.
     */
    public String getString(String key) {
	String value = null;
	try {
	    value = getResourceBundle().getString(key);
	} catch (MissingResourceException e) {
	    System.out.println("java.util.MissingResourceException: Couldn't find value for: " + key);
	}
	if(value == null) {
	    value = "Could not find resource: " + key + "  ";
	}
	return value;
    }

    /**
     * Returns the resource bundle associated with this demo. Used
     * to get accessable and internationalized strings.
     */
    public ResourceBundle getResourceBundle() {
	if(bundle == null) {
	    bundle = ResourceBundle.getBundle("resources.swingset");
	}
	return bundle;
    }

    /**
     * Returns a mnemonic from the resource bundle. Typically used as
     * keyboard shortcuts in menu items.
     */
    public char getMnemonic(String key) {
	return (getString(key)).charAt(0);
    }

    /**
     * Creates an icon from an image contained in the "images" directory.
     */
    public ImageIcon createImageIcon(String filename, String description) {
	String path = "/resources/images/" + filename;
	return new ImageIcon(getClass().getResource(path)); 
    }

    /**
     * If DEBUG is defined, prints debug information out to std ouput.
     */
    public void debug(String s) {
	if(DEBUG) {
	    System.out.println((debugCounter++) + ": " + s);
	}
    }

    /**
     * Stores the current L&F, and calls updateLookAndFeel, below
     */
    public void setLookAndFeel(String laf) {
	if(currentLookAndFeel != laf) {
	    currentLookAndFeel = laf;
	    themesMenu.setEnabled(laf == metal);
	    updateLookAndFeel();
	}
    }

    /**
     * Sets the current L&F on each demo module
     */
    public void updateLookAndFeel() {
	try {
	    UIManager.setLookAndFeel(currentLookAndFeel);
	    SwingUtilities.updateComponentTreeUI(this);
	} catch (Exception ex) {
	    System.out.println("Failed loading L&F: " + currentLookAndFeel);
	    System.out.println(ex);
	}

	// lazily update update the UI's for the remaining demos
	for (int i = 0; i < demosVector.size(); i++) {
	    DemoModule demo = (DemoModule) demosVector.elementAt(i);
	    if(currentDemo != demo) {
		// do the following on the gui thread
		SwingUtilities.invokeLater(new SwingSetRunnable(this, demo) {
		    public void run() {
			SwingUtilities.updateComponentTreeUI(((DemoModule)obj).getDemoPanel());
		    }
		});
	    }
	}
    }

    /**
     * Loads and puts the source code text into JEditorPane in the "Source Code" tab
     */
    public void setSourceCode(DemoModule demo) {
	// do the following on the gui thread
	SwingUtilities.invokeLater(new SwingSetRunnable(this, demo) {
	    public void run() {
		swingset.demoSrcPane.setText(((DemoModule)obj).getSourceCode());
		swingset.demoSrcPane.setCaretPosition(0);

	    }
	});
    }

    // *******************************************************
    // **************   ToggleButtonToolbar  *****************
    // *******************************************************
    static Insets zeroInsets = new Insets(1,1,1,1);
    protected class ToggleButtonToolBar extends JToolBar {
	public ToggleButtonToolBar() {
	    super();
	}

	JToggleButton addToggleButton(Action a) {
	    JToggleButton tb = new JToggleButton(
		(String)a.getValue(Action.NAME),
		(Icon)a.getValue(Action.SMALL_ICON)
	    );
	    tb.setMargin(zeroInsets);
	    tb.setText(null);
	    tb.setEnabled(a.isEnabled());
	    tb.setToolTipText((String)a.getValue(Action.SHORT_DESCRIPTION));
	    tb.setAction(a);
	    add(tb);
	    return tb;
	}
    }

    // *******************************************************
    // ******************   Runnables  ***********************
    // *******************************************************

    /**
     * Generic SwingSet2 runnable. This is intended to run on the
     * AWT gui event thread so as not to muck things up by doing
     * gui work off the gui thread. Accepts a SwingSet2 and an Object
     * as arguments, which gives subtypes of this class the two
     * "must haves" needed in most runnables for this demo.
     */
    class SwingSetRunnable implements Runnable {
	protected SwingSet2 swingset;
	protected Object obj;
	
	public SwingSetRunnable(SwingSet2 swingset, Object obj) {
	    this.swingset = swingset;
	    this.obj = obj;
	}

	public void run() {
	}
    }
	
    
    // *******************************************************
    // ********************   Actions  ***********************
    // *******************************************************
    
    public class SwitchToDemoAction extends AbstractAction {
	SwingSet2 swingset;
	DemoModule demo;
	
	public SwitchToDemoAction(SwingSet2 swingset, DemoModule demo) {
	    super(demo.getName(), demo.getIcon());
	    this.swingset = swingset;
	    this.demo = demo;
	}

	public void actionPerformed(ActionEvent e) {
	    swingset.setDemo(demo);
	}
    }

    class OkAction extends AbstractAction {
	JDialog aboutBox;

        protected OkAction(JDialog aboutBox) {
            super("OkAction");
	    this.aboutBox = aboutBox;
        }

        public void actionPerformed(ActionEvent e) {
	    aboutBox.setVisible(false);
	}
    }

    class ChangeLookAndFeelAction extends AbstractAction {
	SwingSet2 swingset;
	String laf;
        protected ChangeLookAndFeelAction(SwingSet2 swingset, String laf) {
            super("ChangeTheme");
	    this.swingset = swingset;
	    this.laf = laf;
        }

        public void actionPerformed(ActionEvent e) {
	    swingset.setLookAndFeel(laf);
	}
    }

    class ChangeThemeAction extends AbstractAction {
	SwingSet2 swingset;
	DefaultMetalTheme theme;
        protected ChangeThemeAction(SwingSet2 swingset, DefaultMetalTheme theme) {
            super("ChangeTheme");
	    this.swingset = swingset;
	    this.theme = theme;
        }

        public void actionPerformed(ActionEvent e) {
	    MetalLookAndFeel.setCurrentTheme(theme);
	    swingset.updateLookAndFeel();
	}
    }

    class ExitAction extends AbstractAction {
	SwingSet2 swingset;
        protected ExitAction(SwingSet2 swingset) {
            super("ExitAction");
	    this.swingset = swingset;
        }

        public void actionPerformed(ActionEvent e) {
	    System.exit(0);
        }
    }

    class AboutAction extends AbstractAction {
	SwingSet2 swingset;
        protected AboutAction(SwingSet2 swingset) {
            super("AboutAction");
	    this.swingset = swingset;
        }
	
        public void actionPerformed(ActionEvent e) {
	    if(aboutBox == null) {
		// JPanel panel = new JPanel(new BorderLayout());
		JPanel panel = new AboutPanel(swingset);
		panel.setLayout(new BorderLayout());

		aboutBox = new JDialog(swingset.getFrame(), getString("AboutBox.title"), false);
		aboutBox.getContentPane().add(panel, BorderLayout.CENTER);

		// JButton button = new JButton(getString("AboutBox.ok_button_text"));
		JPanel buttonpanel = new JPanel();
		buttonpanel.setOpaque(false);
		JButton button = (JButton) buttonpanel.add(
		    new JButton(getString("AboutBox.ok_button_text"))
		);
		panel.add(buttonpanel, BorderLayout.SOUTH);

		button.addActionListener(new OkAction(aboutBox));
	    }
	    aboutBox.pack();
	    Point p = swingset.getLocationOnScreen();
	    aboutBox.setLocation(p.x + 10, p.y +10);
	    aboutBox.show();
	}
    }

    // *******************************************************
    // **********************  Misc  *************************
    // *******************************************************

    class DemoLoadThread extends Thread {
	SwingSet2 swingset;
	
	public DemoLoadThread(SwingSet2 swingset) {
	    this.swingset = swingset;
	}

	public void run() {
	    swingset.loadDemos();
	}
    }

    class AboutPanel extends JPanel {
	ImageIcon aboutimage = null;
	SwingSet2 swingset = null;

	public AboutPanel(SwingSet2 swingset) {
	    this.swingset = swingset;
	    aboutimage = swingset.createImageIcon("About.jpg", "AboutBox.accessible_description");
	    setOpaque(false);
	}

	public void paint(Graphics g) {
	    aboutimage.paintIcon(this, g, 0, 0);
	    super.paint(g);
	}

	public Dimension getPreferredSize() {
	    return new Dimension(aboutimage.getIconWidth(),
				 aboutimage.getIconHeight());
	}
    }

}

