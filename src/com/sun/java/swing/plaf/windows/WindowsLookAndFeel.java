/*
 * @(#)WindowsLookAndFeel.java	1.48 98/09/14
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

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultEditorKit;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import java.net.URL;
import java.io.Serializable;
import java.util.*;


/**
 * Implements the Windows95 Look and Feel.
 * UI classes not implemented specifically for Windows will
 * default to those implemented in Basic.  
 * <p>
 * For the keyboard keys defined for each component in this Look and
 * Feel (L&F), see 
 * <a href="../../../../../../javax/swing/doc-files/Key-Win32.html">Component Keystroke Actions for the Windows L&F</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.48 09/14/98
 * @author unattributed
 */
public class WindowsLookAndFeel extends BasicLookAndFeel
{
    public String getName() {
        return "Windows";
    }


    public String getDescription() {
        return "The Microsoft Windows Look and Feel";
    }


    public String getID() {
        return "Windows";
    }
    
    public boolean isNativeLookAndFeel() {
        String osName = System.getProperty("os.name");
        return (osName != null) && (osName.indexOf("Windows") != -1);
    }


    public boolean isSupportedLookAndFeel() {
        return isNativeLookAndFeel();
    }

    
    /** 
     * Initialize the uiClassID to BasicComponentUI mapping.
     * The JComponent classes define their own uiClassID constants
     * (see AbstractComponent.getUIClassID).  This table must
     * map those constants to a BasicComponentUI class of the
     * appropriate type.
     * 
     * @see BasicLookAndFeel#getDefaults
     */
    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);


        String windowsPackageName = "com.sun.java.swing.plaf.windows.";
        Object[] uiDefaults = {
                   "ButtonUI", windowsPackageName + "WindowsButtonUI",
                 "CheckBoxUI", windowsPackageName + "WindowsCheckBoxUI",
              "RadioButtonUI", windowsPackageName + "WindowsRadioButtonUI",
             "ToggleButtonUI", windowsPackageName + "WindowsToggleButtonUI",
              "ProgressBarUI", windowsPackageName + "WindowsProgressBarUI",
                "SplitPaneUI", windowsPackageName + "WindowsSplitPaneUI",
                 "TextAreaUI", windowsPackageName + "WindowsTextAreaUI",
                "TextFieldUI", windowsPackageName + "WindowsTextFieldUI",
            "PasswordFieldUI", windowsPackageName + "WindowsPasswordFieldUI",
                 "TextPaneUI", windowsPackageName + "WindowsTextPaneUI",
               "EditorPaneUI", windowsPackageName + "WindowsEditorPaneUI",
                     "TreeUI", windowsPackageName + "WindowsTreeUI",
                 "ComboBoxUI", windowsPackageName + "WindowsComboBoxUI",
            "InternalFrameUI", windowsPackageName + "WindowsInternalFrameUI",
              "DesktopPaneUI", windowsPackageName + "WindowsDesktopPaneUI",
              "FileChooserUI", windowsPackageName + "WindowsFileChooserUI",
        };

        table.putDefaults(uiDefaults);
    }

    /**
     * Load the SystemColors into the defaults table.  The keys
     * for SystemColor defaults are the same as the names of
     * the public fields in SystemColor.  If the table is being
     * created on a native Windows platform we use the SystemColor
     * values, otherwise we create color objects whose values match
     * the defaults Windows95 colors.
     */
    protected void initSystemColorDefaults(UIDefaults table)
    {
        String[] defaultSystemColors = {
                "desktop", "#005C5C", /* Color of the desktop background */
          "activeCaption", "#000080", /* Color for captions (title bars) when they are active. */
      "activeCaptionText", "#FFFFFF", /* Text color for text in captions (title bars). */
    "activeCaptionBorder", "#C0C0C0", /* Border color for caption (title bar) window borders. */
        "inactiveCaption", "#808080", /* Color for captions (title bars) when not active. */
    "inactiveCaptionText", "#C0C0C0", /* Text color for text in inactive captions (title bars). */
  "inactiveCaptionBorder", "#C0C0C0", /* Border color for inactive caption (title bar) window borders. */
                 "window", "#FFFFFF", /* Default color for the interior of windows */
           "windowBorder", "#000000", /* ??? */
             "windowText", "#000000", /* ??? */
                   "menu", "#C0C0C0", /* Background color for menus */
       "menuPressedItemB", "#000080", /* LightShadow of menubutton highlight */ 
       "menuPressedItemF", "#FFFFFF", /* Default color for foreground "text" in menu item */
               "menuText", "#000000", /* Text color for menus  */
                   "text", "#C0C0C0", /* Text background color */
               "textText", "#000000", /* Text foreground color */
          "textHighlight", "#000080", /* Text background color when selected */
      "textHighlightText", "#FFFFFF", /* Text color when selected */
       "textInactiveText", "#808080", /* Text color when disabled */
                "control", "#C0C0C0", /* Default color for controls (buttons, sliders, etc) */
            "controlText", "#000000", /* Default color for text in controls */
       "controlHighlight", "#C0C0C0",

  /*"controlHighlight", "#E0E0E0",*/ /* Specular highlight (opposite of the shadow) */
     "controlLtHighlight", "#FFFFFF", /* Highlight color for controls */
          "controlShadow", "#808080", /* Shadow color for controls */
        "controlDkShadow", "#000000", /* Dark shadow color for controls */
              "scrollbar", "#E0E0E0", /* Scrollbar background (usually the "track") */
                   "info", "#FFFFE1", /* ??? */
               "infoText", "#000000"  /* ??? */
        };

        loadSystemColors(table, defaultSystemColors, isNativeLookAndFeel());
    }

    private void loadResourceBundle(UIDefaults table) {
        ResourceBundle bundle = ResourceBundle.getBundle("javax.swing.plaf.metal.resources.metal");
	Enumeration iter = bundle.getKeys();
	while(iter.hasMoreElements()) {
	    String key = (String)iter.nextElement();
	    //System.out.println("key :" +key+ " value: " + bundle.getObject(key));
	    table.put( key, bundle.getObject(key) );
	}
    }

    protected void initComponentDefaults(UIDefaults table) 
    {
        super.initComponentDefaults( table );

        loadResourceBundle(table);

        // *** Fonts
        FontUIResource dialogPlain12 = new FontUIResource("Dialog", Font.PLAIN, 12);
        FontUIResource serifPlain12 = new FontUIResource("Serif", Font.PLAIN, 12);
        FontUIResource sansSerifPlain12 = new FontUIResource("SansSerif", Font.PLAIN, 12);
        FontUIResource monospacedPlain12 = new FontUIResource("Monospaced", Font.PLAIN, 12);

        // *** Colors
        ColorUIResource red = new ColorUIResource(Color.red);
        ColorUIResource black = new ColorUIResource(Color.black);
        ColorUIResource white = new ColorUIResource(Color.white);
        ColorUIResource yellow = new ColorUIResource(Color.yellow);
        ColorUIResource gray = new ColorUIResource(Color.gray);
        ColorUIResource lightGray = new ColorUIResource(Color.lightGray);
        ColorUIResource darkGray = new ColorUIResource(Color.darkGray);
        ColorUIResource scrollBarTrack = new ColorUIResource(224, 224, 224);

        // *** Tree 
        ColorUIResource treeSelection = new ColorUIResource(0, 0, 128);

        Object treeExpandedIcon = WindowsTreeUI.ExpandedIcon.createExpandedIcon();

        Object treeCollapsedIcon = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();


	// *** Text

	JTextComponent.KeyBinding[] fieldBindings = makeKeyBindings( new Object[]{
	              "control C", DefaultEditorKit.copyAction,
	              "control V", DefaultEditorKit.pasteAction,
                      "control X", DefaultEditorKit.cutAction,
                 "control INSERT", DefaultEditorKit.copyAction,
                   "shift INSERT", DefaultEditorKit.pasteAction,
                   "shift DELETE", DefaultEditorKit.cutAction,	    
	              "control A", DefaultEditorKit.selectAllAction,
	             "shift LEFT", DefaultEditorKit.selectionBackwardAction,
	            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
	           "control LEFT", DefaultEditorKit.previousWordAction,
	          "control RIGHT", DefaultEditorKit.nextWordAction,
	     "control shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "control shift RIGHT", DefaultEditorKit.selectionNextWordAction,
	                   "HOME", DefaultEditorKit.beginLineAction,
	                    "END", DefaultEditorKit.endLineAction,
	             "shift HOME", DefaultEditorKit.selectionBeginLineAction,
	              "shift END", DefaultEditorKit.selectionEndLineAction,
	                  "ENTER", JTextField.notifyAction,
	});

	JTextComponent.KeyBinding[] multilineBindings = makeKeyBindings( new Object[]{
		      "control C", DefaultEditorKit.copyAction,
		      "control V", DefaultEditorKit.pasteAction,
		      "control X", DefaultEditorKit.cutAction,
                 "control INSERT", DefaultEditorKit.copyAction,
                   "shift INSERT", DefaultEditorKit.pasteAction,
                   "shift DELETE", DefaultEditorKit.cutAction,	    
		     "shift LEFT", DefaultEditorKit.selectionBackwardAction,
		    "shift RIGHT", DefaultEditorKit.selectionForwardAction,
		   "control LEFT", DefaultEditorKit.previousWordAction,
		  "control RIGHT", DefaultEditorKit.nextWordAction,
	     "control shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
	    "control shift RIGHT", DefaultEditorKit.selectionNextWordAction,
		      "control A", DefaultEditorKit.selectAllAction,
			   "HOME", DefaultEditorKit.beginLineAction,
			    "END", DefaultEditorKit.endLineAction,
		     "shift HOME", DefaultEditorKit.selectionBeginLineAction,
		      "shift END", DefaultEditorKit.selectionEndLineAction,
			     "UP", DefaultEditorKit.upAction,
			   "DOWN", DefaultEditorKit.downAction,
			"PAGE_UP", DefaultEditorKit.pageUpAction,
		      "PAGE_DOWN", DefaultEditorKit.pageDownAction,
		       "shift UP", DefaultEditorKit.selectionUpAction,
		     "shift DOWN", DefaultEditorKit.selectionDownAction,
			  "ENTER", DefaultEditorKit.insertBreakAction,
			    "TAB", DefaultEditorKit.insertTabAction
	});


        Border marginBorder = new BasicBorders.MarginBorder();

        Object checkBoxIcon = WindowsIconFactory.getCheckBoxIcon();

        Object radioButtonIcon =WindowsIconFactory.getRadioButtonIcon();


	// *** ProgressBar
	Object progressBarBorder = new BorderUIResource.CompoundBorderUIResource(
	                               new WindowsBorders.ProgressBarBorder(
                                           table.getColor("controlShadow"),
                                           table.getColor("controlLtHighlight")),
	                               new EmptyBorder(1,1,1,1)
	                               );


        // *** ToolTips
        Object toolTipBorder = BorderUIResource.getBlackLineBorderUIResource();


        Object radioButtonBorder = new BorderUIResource.CompoundBorderUIResource(
                                      new BasicBorders.RadioButtonBorder(
                                           table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"),
                                           table.getColor("controlLtHighlight")),
                                      marginBorder);

       Object comboBoxBorder = new WindowsBorders.ComboBoxBorder(
                                           table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"));


	
        Object menuItemCheckIcon = WindowsIconFactory.getMenuItemCheckIcon();

        Object menuItemArrowIcon = WindowsIconFactory.getMenuItemArrowIcon();

        Object menuArrowIcon = WindowsIconFactory.getMenuArrowIcon();

        Object[] defaults = {
	    "TextField.keyBindings", fieldBindings,
	    "PasswordField.keyBindings", fieldBindings,
	    "TextArea.keyBindings", multilineBindings,
	    "TextPane.keyBindings", multilineBindings,
	    "EditorPane.keyBindings", multilineBindings,

	    // Buttons
	    "Button.dashedRectGapX", new Integer(5),
	    "Button.dashedRectGapY", new Integer(4),
	    "Button.dashedRectGapWidth", new Integer(10),
	    "Button.dashedRectGapHeight", new Integer(8),
	    "Button.textShiftOffset", new Integer(1),
            "Button.focus", black,

            "CheckBox.background", table.get("control"),
            "CheckBox.shadow", table.get("controlShadow"),
            "CheckBox.darkShadow", table.get("controlDkShadow"),
            "CheckBox.highlight", table.get("controlLtHighlight"),
            "CheckBox.icon", checkBoxIcon,
            "CheckBox.border", radioButtonBorder,
            "CheckBox.focus", black,

            "RadioButton.background", table.get("control"),
            "RadioButton.shadow", table.get("controlShadow"),
            "RadioButton.darkShadow", table.get("controlDkShadow"),
            "RadioButton.highlight", table.get("controlLtHighlight"),
            "RadioButton.icon", radioButtonIcon,
            "RadioButton.border", radioButtonBorder,
            "RadioButton.focus", black,

	    "ToggleButton.textShiftOffset", new Integer(1),
            "ToggleButton.focus", black,
            "ToggleButton.border", radioButtonBorder,
            "ToggleButton.background", table.get("control"),
            "ToggleButton.foreground", table.get("controlText"),
            "ToggleButton.focus", table.get("controlText"),
            "ToggleButton.font", dialogPlain12,

            "ComboBox.border", comboBoxBorder,

	    // Menus
            "Menu.border", marginBorder,
            "Menu.font", dialogPlain12,
            "Menu.foreground", table.get("menuText"),
            "Menu.background", table.get("menu"),
            "Menu.selectionForeground", table.get("textHighlightText"),
            "Menu.selectionBackground", table.get("textHighlight"),
            "Menu.arrowIcon", menuArrowIcon,

            "MenuItem.border", marginBorder,
            "MenuItem.font", dialogPlain12,
            "MenuItem.foreground", table.get("menuText"),
            "MenuItem.background", table.get("menu"),
            "MenuItem.selectionForeground", table.get("textHighlightText"),
            "MenuItem.selectionBackground", table.get("textHighlight"),
            "MenuItem.checkIcon", menuItemCheckIcon,
            "MenuItem.arrowIcon", menuItemArrowIcon,

            "SplitPane.background", table.get("control"),
            "SplitPane.highlight", table.get("controllHighlight"),
            "SplitPane.shadow", table.get("controlShadow"),
	    "SplitPane.dividerSize", new Integer(3),

            "ToolTip.font", sansSerifPlain12,
            "ToolTip.border", toolTipBorder,
            "ToolTip.background", table.get("info"),
            "ToolTip.foreground", table.get("infoText"),

	    "ProgressBar.font", dialogPlain12,
	    "ProgressBar.foreground",  table.get("textHighlight"), 
	    "ProgressBar.background", table.get("control"), 
	    "ProgressBar.selectionForeground", table.get("control"),
	    "ProgressBar.selectionBackground", table.get("textHighlight"), 
	    "ProgressBar.border", progressBarBorder,
            "ProgressBar.cellLength", new Integer(7),
            "ProgressBar.cellSpacing", new Integer(2),

	    "Tree.font", dialogPlain12,
	    "Tree.background", table.get("window"),
            "Tree.foreground", table.get("textText"),
	    "Tree.hash", gray,
	    "Tree.textForeground", table.get("textText"),
	    "Tree.textBackground", table.get("window"),
	    "Tree.selectionForeground", table.get("textHighlightText"),
	    "Tree.selectionBackground", table.get("textHighlight"),
            "Tree.selectionBorderColor", yellow,
            "Tree.expandedIcon", treeExpandedIcon,
            "Tree.collapsedIcon", treeCollapsedIcon,

	    "FileChooser.newFolderIcon", LookAndFeel.makeIcon(getClass(), "icons/NewFolder.gif"),
	    "FileChooser.upFolderIcon", LookAndFeel.makeIcon(getClass(), "icons/UpFolder.gif"),
	    "FileChooser.homeFolderIcon", LookAndFeel.makeIcon(getClass(), "icons/HomeFolder.gif"),
	    "FileChooser.detailsViewIcon", LookAndFeel.makeIcon(getClass(), "icons/DetailsView.gif"),
	    "FileChooser.listViewIcon", LookAndFeel.makeIcon(getClass(), "icons/ListView.gif"),

            "FileChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
            "FileChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
            "FileChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),

	    "FileView.directoryIcon", LookAndFeel.makeIcon(getClass(), "icons/Directory.gif"),
	    "FileView.fileIcon", LookAndFeel.makeIcon(getClass(), "icons/File.gif"),
	    "FileView.computerIcon", LookAndFeel.makeIcon(getClass(), "icons/Computer.gif"),
	    "FileView.hardDriveIcon", LookAndFeel.makeIcon(getClass(), "icons/HardDrive.gif"),
	    "FileView.floppyDriveIcon", LookAndFeel.makeIcon(getClass(), "icons/FloppyDrive.gif"),

            "InternalFrame.minimizeIconBackground", table.get("control"),
            "InternalFrame.resizeIconHighlight", table.get("controlHighlight"),
            "InternalFrame.resizeIconShadow", table.get("controlShadow"),
            
            "InternalFrame.maximizeIcon", 
                WindowsIconFactory.createFrameMaximizeIcon(),
            "InternalFrame.minimizeIcon", 
                WindowsIconFactory.createFrameMinimizeIcon(),
            "InternalFrame.iconifyIcon", 
                WindowsIconFactory.createFrameIconifyIcon(),
            "InternalFrame.closeIcon", 
                WindowsIconFactory.createFrameCloseIcon(),

        };

        table.putDefaults(defaults);
    }
}
