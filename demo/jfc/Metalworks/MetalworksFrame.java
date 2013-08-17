/*
 * @(#)MetalworksFrame.java	1.10 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;

import javax.swing.plaf.metal.*;


/**
  * This is the main container frame for the Metalworks demo app
  *
  * @version 1.10 08/26/98
  * @author Steve Wilson
  */
public class MetalworksFrame extends JFrame {

    JMenuBar menuBar;
    JDesktopPane desktop;
    JInternalFrame toolPalette;
    JCheckBoxMenuItem showToolPaletteMenuItem;

    static final Integer DOCLAYER = new Integer(5);
    static final Integer TOOLLAYER = new Integer(6);
    static final Integer HELPLAYER = new Integer(7);

    static final String ABOUTMSG = "Metalworks \n \nAn application written to show off the Java Look & Feel. \n \nWritten by the JavaSoft Look & Feel Team \n  Michael Albers\n  Tom Santos\n  Jeff Shapiro\n  Steve Wilson";


    public MetalworksFrame() {
        super("Metalworks");
        final int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	setBounds ( inset, inset, screenSize.width - inset*2, screenSize.height - inset*2 );
	buildContent();
	buildMenus();
	this.addWindowListener(new WindowAdapter() {
	                       public void windowClosing(WindowEvent e) {
				   quit();
			       }});
	UIManager.addPropertyChangeListener(new UISwitchListener((JComponent)getRootPane()));
    }

    protected void buildMenus() {
        menuBar = new JMenuBar();
	menuBar.setOpaque(true);
	JMenu file = buildFileMenu();
	JMenu edit = buildEditMenu();
	JMenu views = buildViewsMenu();
	JMenu help = buildHelpMenu();

	// load a theme from a text file
	MetalTheme myTheme = null;
	try {
	    myTheme =  new PropertiesMetalTheme(new FileInputStream("MyTheme.theme"));
	} catch (IOException e) {System.out.println(e);}

	// build an array of themes
	MetalTheme[] themes = { new DefaultMetalTheme(),
				new GreenMetalTheme(),
				new AquaMetalTheme(),
				new KhakiMetalTheme(),
				new DemoMetalTheme(),
				new ContrastMetalTheme(),
				new BigContrastMetalTheme(),
	                        myTheme };

	// put the themes in a menu
	JMenu themeMenu = new MetalThemeMenu("Theme", themes);

	menuBar.add(file);
	menuBar.add(edit);
	menuBar.add(views);
	menuBar.add(themeMenu);
	menuBar.add(help);
	setJMenuBar(menuBar);	
    }

    protected JMenu buildFileMenu() {
	JMenu file = new JMenu("File");
	JMenuItem newWin = new JMenuItem("New");
	JMenuItem open = new JMenuItem("Open");
	JMenuItem quit = new JMenuItem("Quit");

	newWin.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   newDocument();
			       }});

	open.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   openDocument();
			       }});

	quit.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   quit();
			       }});

	file.add(newWin);
	file.add(open);
	file.addSeparator();
	file.add(quit);
	return file;
    }

    protected JMenu buildEditMenu() {
	JMenu edit = new JMenu("Edit");
	JMenuItem undo = new JMenuItem("Undo");
	JMenuItem copy = new JMenuItem("Copy");
	JMenuItem cut = new JMenuItem("Cut");
	JMenuItem paste = new JMenuItem("Paste");
	JMenuItem prefs = new JMenuItem("Preferences...");

	undo.setEnabled(false);
	copy.setEnabled(false);
	cut.setEnabled(false);
	paste.setEnabled(false);

	prefs.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   openPrefsWindow();
			       }});

	edit.add(undo);
	edit.addSeparator();
	edit.add(cut);
	edit.add(copy);
	edit.add(paste);
	edit.addSeparator();
	edit.add(prefs);
	return edit;
    }

    protected JMenu buildViewsMenu() {
	JMenu views = new JMenu("Views");

	JMenuItem inBox = new JMenuItem("Open In-Box");
	JMenuItem outBox = new JMenuItem("Open Out-Box");
	outBox.setEnabled(false);

	inBox.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   openInBox();
			       }});

	views.add(inBox);
	views.add(outBox);
	return views;
    }

    protected JMenu buildHelpMenu() {
	JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About Metalworks...");
	JMenuItem openHelp = new JMenuItem("Open Help Window");

	about.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        showAboutBox();
	    }
	});

	openHelp.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   openHelpWindow();
			       }});

	help.add(about);
	help.add(openHelp);

	return help;
    }

    protected void buildContent() {
        desktop = new JDesktopPane();
        getContentPane().add(desktop);
    }

    public void quit() {
        System.exit(0);
    }

    public void newDocument() {
	JInternalFrame doc = new MetalworksDocumentFrame();
	desktop.add(doc, DOCLAYER);
	try { 
	    doc.setSelected(true); 
	} catch (java.beans.PropertyVetoException e2) {}
    }

    public void openDocument() {
        JFileChooser chooser = new JFileChooser();
	chooser.showOpenDialog(this);
    }

    public void openHelpWindow() {
	JInternalFrame help = new MetalworksHelp();
	desktop.add(help, HELPLAYER);
	try { 
	    help.setSelected(true); 
	} catch (java.beans.PropertyVetoException e2) {}
    }

    public void showAboutBox() {
        JOptionPane.showMessageDialog(this, ABOUTMSG);
    }

    public void openPrefsWindow() {
        MetalworksPrefs dialog = new MetalworksPrefs(this);
	dialog.show();

    }

    public void openInBox() {
	JInternalFrame doc = new MetalworksInBox();
	desktop.add(doc, DOCLAYER);
	try { 
	    doc.setSelected(true); 
	} catch (java.beans.PropertyVetoException e2) {}
    }

}


