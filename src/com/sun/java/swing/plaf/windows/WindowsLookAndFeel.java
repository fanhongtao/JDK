/*
 * @(#)WindowsLookAndFeel.java	1.142 03/05/06
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * <p>These classes are designed to be used while the
 * corresponding <code>LookAndFeel</code> class has been installed
 * (<code>UIManager.setLookAndFeel(new <i>XXX</i>LookAndFeel())</code>).
 * Using them while a different <code>LookAndFeel</code> is installed
 * may produce unexpected results, including exceptions.
 * Additionally, changing the <code>LookAndFeel</code>
 * maintained by the <code>UIManager</code> without updating the
 * corresponding <code>ComponentUI</code> of any
 * <code>JComponent</code>s may also produce unexpected results,
 * such as the wrong colors showing up, and is generally not
 * encouraged.
 * 
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
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.net.URL;
import java.io.Serializable;
import java.util.*;

import sun.awt.shell.ShellFolder;
import sun.java2d.SunGraphicsEnvironment;
import sun.security.action.GetPropertyAction;

/**
 * Implements the Windows95/98/NT/2000 Look and Feel.
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
 * @version 1.142 05/06/03
 * @author unattributed
 */
public class WindowsLookAndFeel extends BasicLookAndFeel
{
    private Toolkit toolkit;
    private boolean updatePending = false;

    private boolean useSystemFontSettings = true;
    private boolean useSystemFontSizeSettings;

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

    public void initialize() {
	toolkit = Toolkit.getDefaultToolkit();

	// Set the flag which determines which version of Windows should
	// be rendered. This flag only need to be set once.
	// if version <= 4.0 then the classic LAF should be loaded.
	String osVersion = System.getProperty("os.version");
	if (osVersion != null) {
	    Float version = Float.valueOf(osVersion);
	    if (version.floatValue() <= 4.0) {
		isClassicWindows = true;
	    } else {
		isClassicWindows = false;
	    }
	}

	// Using the fonts set by the user can potentially cause
	// performance and compatibility issues, so allow this feature
	// to be switched off either at runtime or programmatically
	//
	String systemFonts = (String) java.security.AccessController.doPrivileged(
               new GetPropertyAction("swing.useSystemFontSettings"));
	useSystemFontSettings = (systemFonts == null ||
                                 Boolean.valueOf(systemFonts).booleanValue());

        if (useSystemFontSettings) {
            Object value = UIManager.get("Application.useSystemFontSettings");

            useSystemFontSettings = (value == null ||
                                     Boolean.TRUE.equals(value));
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
            addKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);

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
	       "LabelUI", windowsPackageName + "WindowsLabelUI",
         "RadioButtonUI", windowsPackageName + "WindowsRadioButtonUI",
        "ToggleButtonUI", windowsPackageName + "WindowsToggleButtonUI",
         "ProgressBarUI", windowsPackageName + "WindowsProgressBarUI",
	      "SliderUI", windowsPackageName + "WindowsSliderUI",
	   "SeparatorUI", windowsPackageName + "WindowsSeparatorUI",
           "SplitPaneUI", windowsPackageName + "WindowsSplitPaneUI",
	     "SpinnerUI", windowsPackageName + "WindowsSpinnerUI",
	  "TabbedPaneUI", windowsPackageName + "WindowsTabbedPaneUI",
            "TextAreaUI", windowsPackageName + "WindowsTextAreaUI",
           "TextFieldUI", windowsPackageName + "WindowsTextFieldUI",
       "PasswordFieldUI", windowsPackageName + "WindowsPasswordFieldUI",
            "TextPaneUI", windowsPackageName + "WindowsTextPaneUI",
          "EditorPaneUI", windowsPackageName + "WindowsEditorPaneUI",
                "TreeUI", windowsPackageName + "WindowsTreeUI",
	     "ToolBarUI", windowsPackageName + "WindowsToolBarUI",	      
    "ToolBarSeparatorUI", windowsPackageName + "WindowsToolBarSeparatorUI",
            "ComboBoxUI", windowsPackageName + "WindowsComboBoxUI",
	 "TableHeaderUI", windowsPackageName + "WindowsTableHeaderUI",
       "InternalFrameUI", windowsPackageName + "WindowsInternalFrameUI",
         "DesktopPaneUI", windowsPackageName + "WindowsDesktopPaneUI",
         "DesktopIconUI", windowsPackageName + "WindowsDesktopIconUI",
         "FileChooserUI", windowsPackageName + "WindowsFileChooserUI",
	        "MenuUI", windowsPackageName + "WindowsMenuUI",
	    "MenuItemUI", windowsPackageName + "WindowsMenuItemUI",
	     "MenuBarUI", windowsPackageName + "WindowsMenuBarUI",
	   "PopupMenuUI", windowsPackageName + "WindowsPopupMenuUI",
	   "ScrollBarUI", windowsPackageName + "WindowsScrollBarUI",
	    "RootPaneUI", windowsPackageName + "WindowsRootPaneUI"
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

   /**
     * Initialize the defaults table with the name of the ResourceBundle
     * used for getting localized defaults.
     */
    private void initResourceBundle(UIDefaults table) {
        table.addResourceBundle( "com.sun.java.swing.plaf.windows.resources.windows" );
    }

    // XXX - there are probably a lot of redundant values that could be removed. 
    // ie. Take a look at RadioButtonBorder, etc...
    protected void initComponentDefaults(UIDefaults table) 
    {
        super.initComponentDefaults( table );

        initResourceBundle(table);

        // *** Shared Fonts
	Integer twelve = new Integer(12);
	Integer eight = new Integer(8);
	Integer ten = new Integer(10);
	Integer fontPlain = new Integer(Font.PLAIN);
	Integer fontBold = new Integer(Font.BOLD);

	Object dialogPlain12 = new UIDefaults.ProxyLazyValue(
			       "javax.swing.plaf.FontUIResource",
			       null,
			       new Object[] {"Dialog", fontPlain, twelve});

	Object serifPlain12 = new UIDefaults.ProxyLazyValue( // XXX - not used
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"Serif", fontPlain, twelve});
	Object sansSerifPlain12 =  new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"SansSerif", fontPlain, twelve});
	Object monospacedPlain12 = new UIDefaults.ProxyLazyValue( // XXX - Not used
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"MonoSpaced", fontPlain, twelve});
	Object dialogBold12 = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"Dialog", fontBold, twelve});

        // *** Colors
	// XXX - some of these doens't seem to be used
        ColorUIResource red = new ColorUIResource(Color.red);
        ColorUIResource black = new ColorUIResource(Color.black);
        ColorUIResource white = new ColorUIResource(Color.white);
        ColorUIResource yellow = new ColorUIResource(Color.yellow);
        ColorUIResource gray = new ColorUIResource(Color.gray);
        ColorUIResource lightGray = new ColorUIResource(Color.lightGray);
        ColorUIResource darkGray = new ColorUIResource(Color.darkGray);
        ColorUIResource scrollBarTrack = lightGray;
        ColorUIResource scrollBarTrackHighlight = darkGray;

	// Set the flag which determines which version of Windows should
	// be rendered. This flag only need to be set once.
	// if version <= 4.0 then the classic LAF should be loaded.
	String osVersion = System.getProperty("os.version");
	if (osVersion != null) {
	    try {
		Float version = Float.valueOf(osVersion);
		if (version.floatValue() <= 4.0) {
		    isClassicWindows = true;
		} else {
		    isClassicWindows = false;
		}
	    } catch (NumberFormatException ex) {
		isClassicWindows = false;
	    }
	}

        // *** Tree 
        ColorUIResource treeSelection = new ColorUIResource(0, 0, 128);
        Object treeExpandedIcon = WindowsTreeUI.ExpandedIcon.createExpandedIcon();

        Object treeCollapsedIcon = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();


	// *** Text

	Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[] {
	              "control C", DefaultEditorKit.copyAction,
	              "control V", DefaultEditorKit.pasteAction,
                      "control X", DefaultEditorKit.cutAction,
			   "COPY", DefaultEditorKit.copyAction,
			  "PASTE", DefaultEditorKit.pasteAction,
			    "CUT", DefaultEditorKit.cutAction,
                 "control INSERT", DefaultEditorKit.copyAction,
                   "shift INSERT", DefaultEditorKit.pasteAction,
                   "shift DELETE", DefaultEditorKit.cutAction,	    
	              "control A", DefaultEditorKit.selectAllAction,
	     "control BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
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
		     "typed \010", DefaultEditorKit.deletePrevCharAction,
                         "DELETE", DefaultEditorKit.deleteNextCharAction,
                          "RIGHT", DefaultEditorKit.forwardAction,
                           "LEFT", DefaultEditorKit.backwardAction,
                       "KP_RIGHT", DefaultEditorKit.forwardAction,
                        "KP_LEFT", DefaultEditorKit.backwardAction,
	                  "ENTER", JTextField.notifyAction,
                "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
	});

	Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[] {
		      "control C", DefaultEditorKit.copyAction,
		      "control V", DefaultEditorKit.pasteAction,
		      "control X", DefaultEditorKit.cutAction,
			   "COPY", DefaultEditorKit.copyAction,
			  "PASTE", DefaultEditorKit.pasteAction,
			    "CUT", DefaultEditorKit.cutAction,
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
	     "control BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
			   "HOME", DefaultEditorKit.beginLineAction,
			    "END", DefaultEditorKit.endLineAction,
		     "shift HOME", DefaultEditorKit.selectionBeginLineAction,
		      "shift END", DefaultEditorKit.selectionEndLineAction,
		   "control HOME", DefaultEditorKit.beginAction,
		    "control END", DefaultEditorKit.endAction,
	     "control shift HOME", DefaultEditorKit.selectionBeginAction,
	      "control shift END", DefaultEditorKit.selectionEndAction,
			     "UP", DefaultEditorKit.upAction,
			   "DOWN", DefaultEditorKit.downAction,
		     "typed \010", DefaultEditorKit.deletePrevCharAction,
                         "DELETE", DefaultEditorKit.deleteNextCharAction,
                          "RIGHT", DefaultEditorKit.forwardAction,
                           "LEFT", DefaultEditorKit.backwardAction,
                       "KP_RIGHT", DefaultEditorKit.forwardAction,
                        "KP_LEFT", DefaultEditorKit.backwardAction,
			"PAGE_UP", DefaultEditorKit.pageUpAction,
		      "PAGE_DOWN", DefaultEditorKit.pageDownAction,
		  "shift PAGE_UP", "selection-page-up",
 	        "shift PAGE_DOWN", "selection-page-down",
	     "ctrl shift PAGE_UP", "selection-page-left",
 	   "ctrl shift PAGE_DOWN", "selection-page-right",
		       "shift UP", DefaultEditorKit.selectionUpAction,
		     "shift DOWN", DefaultEditorKit.selectionDownAction,
			  "ENTER", DefaultEditorKit.insertBreakAction,
			    "TAB", DefaultEditorKit.insertTabAction,
                      "control T", "next-link-action",
                "control shift T", "previous-link-action",
                  "control SPACE", "activate-link-action",
                "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
	});

	Object menuItemAcceleratorDelimiter = new String("+");

	Object ControlBackgroundColor = new DesktopProperty(
                                                       "win.3d.backgroundColor", 
						        table.get("control"),
                                                       toolkit);
	Object ControlLightColor      = new DesktopProperty(
                                                       "win.3d.lightColor", 
							table.get("controlHighlight"),
                                                       toolkit);
	Object ControlHighlightColor  = new DesktopProperty(
                                                       "win.3d.highlightColor", 
							table.get("controlLtHighlight"),
                                                       toolkit);
	Object ControlShadowColor     = new DesktopProperty(
                                                       "win.3d.shadowColor", 
							table.get("controlShadow"),
                                                       toolkit);
	Object ControlDarkShadowColor = new DesktopProperty(
                                                       "win.3d.darkShadowColor", 
							table.get("controlDkShadow"),
                                                       toolkit);
	Object ControlTextColor       = new DesktopProperty(
                                                       "win.button.textColor", 
							table.get("controlText"),
                                                       toolkit);
	Object MenuBackgroundColor    = new DesktopProperty(
                                                       "win.menu.backgroundColor", 
							table.get("menu"),
                                                       toolkit);
	Object MenuTextColor          = new DesktopProperty(
                                                       "win.menu.textColor", 
							table.get("menuText"),
                                                       toolkit);
	Object SelectionBackgroundColor = new DesktopProperty(
                                                       "win.item.highlightColor", 
							table.get("textHighlight"),
                                                       toolkit);
	Object SelectionTextColor     = new DesktopProperty(
                                                       "win.item.highlightTextColor", 
							table.get("textHighlightText"),
                                                       toolkit);
	Object WindowBackgroundColor  = new DesktopProperty(
                                                       "win.frame.backgroundColor", 
							table.get("window"),
                                                       toolkit);
	Object WindowTextColor        = new DesktopProperty(
                                                       "win.frame.textColor", 
							table.get("windowText"),
                                                       toolkit);
        Object WindowBorderWidth      = new DesktopProperty(
                                                       "win.frame.sizingBorderWidth",
                                                       new Integer(1),
                                                       toolkit);
	Object InactiveTextColor      = new DesktopProperty(
                                                       "win.text.grayedTextColor", 
							table.get("textInactiveText"),
                                                       toolkit);
	Object ScrollbarBackgroundColor = new DesktopProperty(
                                                       "win.scrollbar.backgroundColor", 
							table.get("scrollbar"),
                                                       toolkit);

        Object MenuFont = dialogPlain12;
        Object FixedControlFont = monospacedPlain12;
        Object ControlFont = dialogPlain12;
        Object MessageFont = dialogPlain12;
        Object WindowFont = dialogBold12;
        Object ToolTipFont = sansSerifPlain12;

	Object scrollBarWidth = new DesktopProperty("win.scrollbar.width",
						    new Integer(16), toolkit);

	Object showMnemonics = new DesktopProperty("win.menu.keyboardCuesOn",
						     Boolean.TRUE, toolkit);

        if (useSystemFontSettings) {
            MenuFont = getDesktopFontValue("win.menu.font", MenuFont, toolkit);
            FixedControlFont = getDesktopFontValue("win.ansi.font",
                                                   FixedControlFont, toolkit);
            ControlFont = getDesktopFontValue("win.ansiVar.font",
                                              ControlFont, toolkit);
            MessageFont = getDesktopFontValue("win.messagebox.font",
                                              MessageFont, toolkit);
            WindowFont = getDesktopFontValue("win.frame.captionFont",
                                             WindowFont, toolkit);
            ToolTipFont = getDesktopFontValue("win.tooltip.font", ToolTipFont,
                                              toolkit);
        }
        if (useSystemFontSizeSettings) {
            MenuFont = new WindowsFontProperty("win.menu.font.height",
                                  toolkit, "Dialog", Font.PLAIN, 12);
            FixedControlFont = new WindowsFontProperty(
                       "win.ansi.font.height", toolkit, "MonoSpaced",
                       Font.PLAIN, 12);
            ControlFont = new WindowsFontProperty("win.ansiVar.font.height",
                             toolkit, "Dialog", Font.PLAIN, 12);
            MessageFont = new WindowsFontProperty("win.messagebox.font.height",
                              toolkit, "Dialog", Font.PLAIN, 12);
            WindowFont = new WindowsFontProperty(
                             "win.frame.captionFont.height", toolkit,
                             "Dialog", Font.BOLD, 12);
            ToolTipFont = new WindowsFontProperty("win.tooltip.font.height",
                              toolkit, "SansSerif", Font.PLAIN, 12);
        }


        Object[] defaults = {
	    // *** Auditory Feedback
	    // this key defines which of the various cues to render 
	    // Overridden from BasicL&F. This L&F should play all sounds
	    // all the time. The infrastructure decides what to play.
            // This is disabled until sound bugs can be resolved.
	    "AuditoryCues.playList", null, // table.get("AuditoryCues.cueList"),

	    "Application.useSystemFontSettings", Boolean.valueOf(useSystemFontSettings),

	    "TextField.focusInputMap", fieldInputMap,
	    "PasswordField.focusInputMap", fieldInputMap,
	    "TextArea.focusInputMap", multilineInputMap,
	    "TextPane.focusInputMap", multilineInputMap,
	    "EditorPane.focusInputMap", multilineInputMap,

	    // Buttons
	    "Button.font", ControlFont,
	    "Button.background", ControlBackgroundColor,
	    "Button.foreground", ControlTextColor,
	    "Button.shadow", ControlShadowColor,
            "Button.darkShadow", ControlDarkShadowColor,
            "Button.light", ControlLightColor,
            "Button.highlight", ControlHighlightColor,
	    "Button.disabledForeground", InactiveTextColor,
	    "Button.disabledShadow", ControlHighlightColor,
            "Button.focus", black,
	    "Button.dashedRectGapX", new Integer(5),
	    "Button.dashedRectGapY", new Integer(4),
	    "Button.dashedRectGapWidth", new Integer(10),
	    "Button.dashedRectGapHeight", new Integer(8),
	    "Button.textShiftOffset", new Integer(1),
	    // W2K keyboard navigation hidding.
	    "Button.showMnemonics", showMnemonics, 
            "Button.focusInputMap",
               new UIDefaults.LazyInputMap(new Object[] {
                            "SPACE", "pressed",
                   "released SPACE", "released"
                 }),

	    "CheckBox.font", ControlFont,
            "CheckBox.interiorBackground", WindowBackgroundColor,
 	    "CheckBox.background", ControlBackgroundColor,
	    "CheckBox.foreground", ControlTextColor,
            "CheckBox.shadow", ControlShadowColor,
            "CheckBox.darkShadow", ControlDarkShadowColor,
            "CheckBox.light", ControlLightColor,
            "CheckBox.highlight", ControlHighlightColor,
            "CheckBox.focus", black,
	    "CheckBox.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
		 }),

            "CheckBoxMenuItem.font", MenuFont,
	    "CheckBoxMenuItem.background", MenuBackgroundColor,
	    "CheckBoxMenuItem.foreground", MenuTextColor,
	    "CheckBoxMenuItem.selectionForeground", SelectionTextColor,
	    "CheckBoxMenuItem.selectionBackground", SelectionBackgroundColor,
	    "CheckBoxMenuItem.acceleratorForeground", MenuTextColor,
	    "CheckBoxMenuItem.acceleratorSelectionForeground", SelectionTextColor,
	    "CheckBoxMenuItem.commandSound", "win.sound.menuCommand",

	    "ComboBox.font", ControlFont,
	    "ComboBox.background", WindowBackgroundColor,
	    "ComboBox.foreground", WindowTextColor,
	    "ComboBox.buttonBackground", ControlBackgroundColor,
	    "ComboBox.buttonShadow", ControlShadowColor,
	    "ComboBox.buttonDarkShadow", ControlDarkShadowColor,
	    "ComboBox.buttonHighlight", ControlHighlightColor,
            "ComboBox.selectionBackground", SelectionBackgroundColor,
            "ComboBox.selectionForeground", SelectionTextColor,
            "ComboBox.disabledBackground", ControlBackgroundColor,
            "ComboBox.disabledForeground", InactiveTextColor,
	    "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
		   "ESCAPE", "hidePopup",
		  "PAGE_UP", "pageUpPassThrough",
		"PAGE_DOWN", "pageDownPassThrough",
		     "HOME", "homePassThrough",
		      "END", "endPassThrough",
		     "DOWN", "selectNext",
		  "KP_DOWN", "selectNext",
		       "UP", "selectPrevious",
		    "KP_UP", "selectPrevious",
		   "ENTER", "enterPressed",
		       "F4", "togglePopup"
	      }),

	    // DeskTop.
	    "Desktop.background", new DesktopProperty(
                                                 "win.desktop.backgroundColor",
						  table.get("desktop"),
                                                 toolkit),
	    "Desktop.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		   "ctrl F5", "restore", 
		   "ctrl F4", "close",
		   "ctrl F7", "move", 
		   "ctrl F8", "resize",
		   "RIGHT", "right",
		   "KP_RIGHT", "right",
		   "LEFT", "left",
		   "KP_LEFT", "left",
		   "UP", "up",
		   "KP_UP", "up",
		   "DOWN", "down",
		   "KP_DOWN", "down",
		   "ESCAPE", "escape",
		   "ctrl F9", "minimize", 
		   "ctrl F10", "maximize",
		   "ctrl F6", "selectNextFrame",
		   "ctrl TAB", "selectNextFrame",
		   "ctrl alt F6", "selectNextFrame",
		   "shift ctrl alt F6", "selectPreviousFrame",
                   "ctrl F12", "navigateNext",
                   "shift ctrl F12", "navigatePrevious"
	       }),

            // DesktopIcon
            "DesktopIcon.width", new Integer(160),

	    "EditorPane.font", ControlFont,
	    "EditorPane.background", WindowBackgroundColor,
	    "EditorPane.foreground", WindowTextColor,
	    "EditorPane.selectionBackground", SelectionBackgroundColor,
	    "EditorPane.selectionForeground", SelectionTextColor,
	    "EditorPane.caretForeground", WindowTextColor,
	    "EditorPane.inactiveForeground", InactiveTextColor,

	    "FileChooser.homeFolderIcon",  new LazyFileChooserIcon(null,                 "icons/HomeFolder.gif"),
	    "FileChooser.listViewIcon",    new LazyFileChooserIcon("fileChooserIcon ListView",   "icons/ListView.gif"),
	    "FileChooser.detailsViewIcon", new LazyFileChooserIcon("fileChooserIcon DetailsView","icons/DetailsView.gif"),
	    "FileChooser.upFolderIcon",    new LazyFileChooserIcon("fileChooserIcon UpFolder",   "icons/UpFolder.gif"),
	    "FileChooser.newFolderIcon",   new LazyFileChooserIcon("fileChooserIcon NewFolder",  "icons/NewFolder.gif"),

            "FileChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
            "FileChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
            "FileChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),
	    "FileChooser.ancestorInputMap", 
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelSelection",
		     "BACK_SPACE", "Go Up",
		     "ENTER", "approveSelection"
		 }),

	    "FileView.directoryIcon", LookAndFeel.makeIcon(getClass(), "icons/Directory.gif"),
	    "FileView.fileIcon", LookAndFeel.makeIcon(getClass(), "icons/File.gif"),
	    "FileView.computerIcon", LookAndFeel.makeIcon(getClass(), "icons/Computer.gif"),
	    "FileView.hardDriveIcon", LookAndFeel.makeIcon(getClass(), "icons/HardDrive.gif"),
	    "FileView.floppyDriveIcon", LookAndFeel.makeIcon(getClass(), "icons/FloppyDrive.gif"),

            "InternalFrame.titleFont", WindowFont,
	    "InternalFrame.borderColor", ControlBackgroundColor,
	    "InternalFrame.borderShadow", ControlShadowColor,
	    "InternalFrame.borderDarkShadow", ControlDarkShadowColor,
	    "InternalFrame.borderHighlight", ControlHighlightColor,
	    "InternalFrame.borderLight", ControlLightColor,
            "InternalFrame.borderWidth", WindowBorderWidth,
            "InternalFrame.minimizeIconBackground", ControlBackgroundColor,
            "InternalFrame.resizeIconHighlight", ControlLightColor,
            "InternalFrame.resizeIconShadow", ControlShadowColor,
            "InternalFrame.activeBorderColor", new DesktopProperty(
                                                       "win.frame.activeBorderColor",
                                                       table.get("windowBorder"),
                                                       toolkit),
            "InternalFrame.inactiveBorderColor", new DesktopProperty(
                                                       "win.frame.inactiveBorderColor",
                                                       table.get("windowBorder"),
                                                       toolkit),
	    "InternalFrame.activeTitleBackground", new DesktopProperty(
                                                        "win.frame.activeCaptionColor",
							 table.get("activeCaption"),
                                                        toolkit),
	    "InternalFrame.activeTitleGradient", new DesktopProperty(
		                                        "win.frame.activeCaptionGradientColor",
							 table.get("activeCaption"),
                                                        toolkit),
	    "InternalFrame.activeTitleForeground", new DesktopProperty(
                                                        "win.frame.captionTextColor",
							 table.get("activeCaptionText"),
                                                        toolkit),
	    "InternalFrame.inactiveTitleBackground", new DesktopProperty(
                                                        "win.frame.inactiveCaptionColor",
							 table.get("inactiveCaption"),
                                                        toolkit),
	    "InternalFrame.inactiveTitleGradient", new DesktopProperty(
                                                        "win.frame.inactiveCaptionGradientColor",
							 table.get("inactiveCaption"),
                                                        toolkit),
	    "InternalFrame.inactiveTitleForeground", new DesktopProperty(
                                                        "win.frame.inactiveCaptionTextColor",
							 table.get("inactiveCaptionText"),
                                                        toolkit),
            
            "InternalFrame.maximizeIcon", 
                WindowsIconFactory.createFrameMaximizeIcon(),
            "InternalFrame.minimizeIcon", 
                WindowsIconFactory.createFrameMinimizeIcon(),
            "InternalFrame.iconifyIcon", 
                WindowsIconFactory.createFrameIconifyIcon(),
            "InternalFrame.closeIcon", 
                WindowsIconFactory.createFrameCloseIcon(),

	    // Internal Frame Auditory Cue Mappings
            "InternalFrame.closeSound", "win.sound.close",
            "InternalFrame.maximizeSound", "win.sound.maximize",
            "InternalFrame.minimizeSound", "win.sound.minimize",
            "InternalFrame.restoreDownSound", "win.sound.restoreDown",
            "InternalFrame.restoreUpSound", "win.sound.restoreUp",

	    "InternalFrame.windowBindings", new Object[] {
		"shift ESCAPE", "showSystemMenu",
		  "ctrl SPACE", "showSystemMenu",
		      "ESCAPE", "hideSystemMenu"},
	    
	    // Label
	    "Label.font", ControlFont,
	    "Label.background", ControlBackgroundColor,
	    "Label.foreground", ControlTextColor,
	    "Label.disabledForeground", InactiveTextColor,
	    "Label.disabledShadow", ControlHighlightColor,

	    // List.
	    "List.font", ControlFont,
	    "List.background", WindowBackgroundColor,
	    "List.foreground", WindowTextColor,
	    "List.selectionBackground", SelectionBackgroundColor,
	    "List.selectionForeground", SelectionTextColor,
            "List.focusCellBorderColor", ControlHighlightColor,
	    "List.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                           "ctrl C", "copy",
                           "ctrl V", "paste",
                           "ctrl X", "cut",
                             "COPY", "copy",
                            "PASTE", "paste",
                              "CUT", "cut",
		               "UP", "selectPreviousRow",
		            "KP_UP", "selectPreviousRow",
		         "shift UP", "selectPreviousRowExtendSelection",
		      "shift KP_UP", "selectPreviousRowExtendSelection",
		             "DOWN", "selectNextRow",
		          "KP_DOWN", "selectNextRow",
		       "shift DOWN", "selectNextRowExtendSelection",
		    "shift KP_DOWN", "selectNextRowExtendSelection",
		             "LEFT", "selectPreviousColumn",
		          "KP_LEFT", "selectPreviousColumn",
		       "shift LEFT", "selectPreviousColumnExtendSelection",
		    "shift KP_LEFT", "selectPreviousColumnExtendSelection",
		            "RIGHT", "selectNextColumn",
		         "KP_RIGHT", "selectNextColumn",
		      "shift RIGHT", "selectNextColumnExtendSelection",
		   "shift KP_RIGHT", "selectNextColumnExtendSelection",
		       "ctrl SPACE", "selectNextRowExtendSelection",
		             "HOME", "selectFirstRow",
		       "shift HOME", "selectFirstRowExtendSelection",
		              "END", "selectLastRow",
		        "shift END", "selectLastRowExtendSelection",
		          "PAGE_UP", "scrollUp",
		    "shift PAGE_UP", "scrollUpExtendSelection",
		        "PAGE_DOWN", "scrollDown",
		  "shift PAGE_DOWN", "scrollDownExtendSelection",
		           "ctrl A", "selectAll",
		       "ctrl SLASH", "selectAll",
		  "ctrl BACK_SLASH", "clearSelection"
		 }),

	    // PopupMenu
	    "PopupMenu.font", MenuFont,
	    "PopupMenu.background", MenuBackgroundColor,
	    "PopupMenu.foreground", MenuTextColor,
            "PopupMenu.popupSound", "win.sound.menuPopup",

	    // Menus
            "Menu.font", MenuFont,
            "Menu.foreground", MenuTextColor,
            "Menu.background", MenuBackgroundColor,
	    "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE,
            "Menu.selectionForeground", SelectionTextColor,
            "Menu.selectionBackground", SelectionBackgroundColor,
	    "Menu.acceleratorForeground", MenuTextColor,
	    "Menu.acceleratorSelectionForeground", SelectionTextColor,
	    "Menu.menuPopupOffsetX", new Integer(0),
	    "Menu.menuPopupOffsetY", new Integer(0),
	    "Menu.submenuPopupOffsetX", new Integer(-4),
	    "Menu.submenuPopupOffsetY", new Integer(-3),
            "Menu.crossMenuMnemonic", Boolean.FALSE,

	    // MenuBar.
	    "MenuBar.font", MenuFont,
	    "MenuBar.background", MenuBackgroundColor,
	    // The background color may be overridden for XP in WindowsMenuBarUI.
	    // Save the classic background here in case the user switches from
	    // XP to classic at runtime.
	    "MenuBar.classicBackground", new Object[] { MenuBackgroundColor },
	    "MenuBar.foreground", MenuTextColor,
	    "MenuBar.shadow", ControlShadowColor,
	    "MenuBar.highlight", ControlHighlightColor,
	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },

            "MenuItem.font", MenuFont,
            "MenuItem.acceleratorFont", MenuFont,
            "MenuItem.foreground", MenuTextColor,
            "MenuItem.background", MenuBackgroundColor,
            "MenuItem.selectionForeground", SelectionTextColor,
            "MenuItem.selectionBackground", SelectionBackgroundColor,
	    "MenuItem.disabledForeground", InactiveTextColor,
	    "MenuItem.acceleratorForeground", MenuTextColor,
	    "MenuItem.acceleratorSelectionForeground", SelectionTextColor,
	    "MenuItem.acceleratorDelimiter", menuItemAcceleratorDelimiter,
	         // Menu Item Auditory Cue Mapping
	    "MenuItem.commandSound", "win.sound.menuCommand",

	    "RadioButton.font", ControlFont,
            "RadioButton.interiorBackground", WindowBackgroundColor,
            "RadioButton.background", ControlBackgroundColor,
	    "RadioButton.foreground", ControlTextColor,
            "RadioButton.shadow", ControlShadowColor,
            "RadioButton.darkShadow", ControlDarkShadowColor,
            "RadioButton.light", ControlLightColor,
	    "RadioButton.highlight", ControlHighlightColor,
            "RadioButton.focus", black,
	    "RadioButton.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                          "SPACE", "pressed",
                 "released SPACE", "released"
	      }),


            "RadioButtonMenuItem.font", MenuFont,
	    "RadioButtonMenuItem.foreground", MenuTextColor,
	    "RadioButtonMenuItem.background", MenuBackgroundColor,
	    "RadioButtonMenuItem.selectionForeground", SelectionTextColor,
	    "RadioButtonMenuItem.selectionBackground", SelectionBackgroundColor,
	    "RadioButtonMenuItem.disabledForeground", InactiveTextColor,
	    "RadioButtonMenuItem.acceleratorForeground", MenuTextColor,
	    "RadioButtonMenuItem.acceleratorSelectionForeground", SelectionTextColor,
	    "RadioButtonMenuItem.commandSound", "win.sound.menuCommand",

	    // OptionPane.
	    "OptionPane.font", MessageFont,
	    "OptionPane.messageFont", MessageFont,
	    "OptionPane.buttonFont", MessageFont,
	    "OptionPane.background", ControlBackgroundColor,
	    "OptionPane.foreground", ControlTextColor,
            "OptionPane.messageForeground", ControlTextColor,
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },
	         // Option Pane Auditory Cue Mappings
            "OptionPane.errorSound", "win.sound.hand", // Error
            "OptionPane.informationSound", "win.sound.asterisk", // Info Plain
            "OptionPane.questionSound", "win.sound.question", // Question
            "OptionPane.warningSound", "win.sound.exclamation", // Warning

            "FormattedTextField.focusInputMap",
              new UIDefaults.LazyInputMap(new Object[] {
			   "ctrl C", DefaultEditorKit.copyAction,
			   "ctrl V", DefaultEditorKit.pasteAction,
			   "ctrl X", DefaultEditorKit.cutAction,
			     "COPY", DefaultEditorKit.copyAction,
			    "PASTE", DefaultEditorKit.pasteAction,
			      "CUT", DefaultEditorKit.cutAction,
		       "shift LEFT", DefaultEditorKit.selectionBackwardAction,
                    "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
		      "shift RIGHT", DefaultEditorKit.selectionForwardAction,
		   "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
			"ctrl LEFT", DefaultEditorKit.previousWordAction,
		     "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
		       "ctrl RIGHT", DefaultEditorKit.nextWordAction,
		    "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
		  "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
	       "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
		 "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
	      "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
			   "ctrl A", DefaultEditorKit.selectAllAction,
			     "HOME", DefaultEditorKit.beginLineAction,
			      "END", DefaultEditorKit.endLineAction,
		       "shift HOME", DefaultEditorKit.selectionBeginLineAction,
		        "shift END", DefaultEditorKit.selectionEndLineAction,
		       "typed \010", DefaultEditorKit.deletePrevCharAction,
                           "DELETE", DefaultEditorKit.deleteNextCharAction,
                            "RIGHT", DefaultEditorKit.forwardAction,
                             "LEFT", DefaultEditorKit.backwardAction,
                         "KP_RIGHT", DefaultEditorKit.forwardAction,
                          "KP_LEFT", DefaultEditorKit.backwardAction,
			    "ENTER", JTextField.notifyAction,
		  "ctrl BACK_SLASH", "unselect",
                   "control shift O", "toggle-componentOrientation",
                           "ESCAPE", "reset-field-edit",
                               "UP", "increment",
                            "KP_UP", "increment",
                             "DOWN", "decrement",
                          "KP_DOWN", "decrement",
              }),

	    // *** Panel
	    "Panel.font", ControlFont,
	    "Panel.background", ControlBackgroundColor,
	    "Panel.foreground", WindowTextColor,

	    // *** PasswordField
	    "PasswordField.font", FixedControlFont,
	    "PasswordField.background", WindowBackgroundColor,
	    "PasswordField.foreground", WindowTextColor,
	    "PasswordField.inactiveForeground", InactiveTextColor,
	    "PasswordField.inactiveBackground", ControlBackgroundColor,
	    "PasswordField.selectionBackground", SelectionBackgroundColor,
	    "PasswordField.selectionForeground", SelectionTextColor,
	    "PasswordField.caretForeground",WindowTextColor,

	    // *** ProgressBar
	    "ProgressBar.font", ControlFont,
	    "ProgressBar.foreground",  SelectionBackgroundColor,
	    "ProgressBar.background", ControlBackgroundColor,
	    "ProgressBar.shadow", ControlShadowColor,
	    "ProgressBar.highlight", ControlHighlightColor,
	    "ProgressBar.selectionForeground", ControlBackgroundColor,
	    "ProgressBar.selectionBackground", SelectionBackgroundColor,
            "ProgressBar.cellLength", new Integer(7),
            "ProgressBar.cellSpacing", new Integer(2),

	    // *** RootPane.
	    // These bindings are only enabled when there is a default
	    // button set on the rootpane.
	    "RootPane.defaultButtonWindowKeyBindings", new Object[] {
		             "ENTER", "press",
		    "released ENTER", "release",
		        "ctrl ENTER", "press",
	       "ctrl released ENTER", "release"
	      },

	    // *** ScrollBar.
	    "ScrollBar.background", ScrollbarBackgroundColor,
	    "ScrollBar.foreground", ControlBackgroundColor,
	    "ScrollBar.track", white,
	    "ScrollBar.trackForeground", ScrollbarBackgroundColor,
	    "ScrollBar.trackHighlight", black,
	    "ScrollBar.trackHighlightForeground", scrollBarTrackHighlight,
	    "ScrollBar.thumb", ControlBackgroundColor,
	    "ScrollBar.thumbHighlight", ControlHighlightColor,
	    "ScrollBar.thumbDarkShadow", ControlDarkShadowColor,
	    "ScrollBar.thumbShadow", ControlShadowColor,
            "ScrollBar.width", scrollBarWidth,
	    "ScrollBar.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "positiveUnitIncrement",
		    "KP_RIGHT", "positiveUnitIncrement",
		        "DOWN", "positiveUnitIncrement",
		     "KP_DOWN", "positiveUnitIncrement",
		   "PAGE_DOWN", "positiveBlockIncrement",
	      "ctrl PAGE_DOWN", "positiveBlockIncrement",
		        "LEFT", "negativeUnitIncrement",
		     "KP_LEFT", "negativeUnitIncrement",
		          "UP", "negativeUnitIncrement",
		       "KP_UP", "negativeUnitIncrement",
		     "PAGE_UP", "negativeBlockIncrement",
	        "ctrl PAGE_UP", "negativeBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

	    // *** ScrollPane.
	    "ScrollPane.font", ControlFont,
	    "ScrollPane.background", ControlBackgroundColor,
	    "ScrollPane.foreground", ControlTextColor,
	    "ScrollPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		           "RIGHT", "unitScrollRight",
		        "KP_RIGHT", "unitScrollRight",
		            "DOWN", "unitScrollDown",
		         "KP_DOWN", "unitScrollDown",
		            "LEFT", "unitScrollLeft",
		         "KP_LEFT", "unitScrollLeft",
		              "UP", "unitScrollUp",
		           "KP_UP", "unitScrollUp",
		         "PAGE_UP", "scrollUp",
		       "PAGE_DOWN", "scrollDown",
		    "ctrl PAGE_UP", "scrollLeft",
		  "ctrl PAGE_DOWN", "scrollRight",
		       "ctrl HOME", "scrollHome",
		        "ctrl END", "scrollEnd"
		 }),

	    // *** Separator
            "Separator.background", ControlHighlightColor,
            "Separator.foreground", ControlShadowColor,

	    // *** Slider.
	    "Slider.foreground", ControlBackgroundColor,
	    "Slider.background", ControlBackgroundColor,
	    "Slider.highlight", ControlHighlightColor,
	    "Slider.shadow", ControlShadowColor,
	    "Slider.focus", ControlDarkShadowColor,
	    "Slider.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "positiveUnitIncrement",
		    "KP_RIGHT", "positiveUnitIncrement",
		        "DOWN", "negativeUnitIncrement",
		     "KP_DOWN", "negativeUnitIncrement",
		   "PAGE_DOWN", "negativeBlockIncrement",
		        "LEFT", "negativeUnitIncrement",
		     "KP_LEFT", "negativeUnitIncrement",
		          "UP", "positiveUnitIncrement",
		       "KP_UP", "positiveUnitIncrement",
		     "PAGE_UP", "positiveBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

            // Spinner
            "Spinner.font", FixedControlFont,
            "Spinner.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                               "UP", "increment",
                            "KP_UP", "increment",
                             "DOWN", "decrement",
                          "KP_DOWN", "decrement",
               }),

	    // *** SplitPane
            "SplitPane.background", ControlBackgroundColor,
            "SplitPane.highlight", ControlLightColor,
            "SplitPane.shadow", ControlShadowColor,
	    "SplitPane.darkShadow", ControlDarkShadowColor,
	    "SplitPane.dividerSize", new Integer(5),
	    "SplitPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		        "UP", "negativeIncrement",
		      "DOWN", "positiveIncrement",
		      "LEFT", "negativeIncrement",
		     "RIGHT", "positiveIncrement",
		     "KP_UP", "negativeIncrement",
		   "KP_DOWN", "positiveIncrement",
		   "KP_LEFT", "negativeIncrement",
		  "KP_RIGHT", "positiveIncrement",
		      "HOME", "selectMin",
		       "END", "selectMax",
		        "F8", "startResize",
		        "F6", "toggleFocus",
		  "ctrl TAB", "focusOutForward",
 	    "ctrl shift TAB", "focusOutBackward"
	       }),

	    // *** TabbedPane
            "TabbedPane.font", ControlFont,
            "TabbedPane.background", ControlBackgroundColor,
            "TabbedPane.foreground", ControlTextColor,
            "TabbedPane.highlight", ControlHighlightColor,
            "TabbedPane.light", ControlLightColor,
            "TabbedPane.shadow", ControlShadowColor,
            "TabbedPane.darkShadow", ControlDarkShadowColor,
            "TabbedPane.focus", ControlTextColor,
	    "TabbedPane.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		         "RIGHT", "navigateRight",
	              "KP_RIGHT", "navigateRight",
	                  "LEFT", "navigateLeft",
	               "KP_LEFT", "navigateLeft",
	                    "UP", "navigateUp",
	                 "KP_UP", "navigateUp",
	                  "DOWN", "navigateDown",
	               "KP_DOWN", "navigateDown",
	             "ctrl DOWN", "requestFocusForVisibleComponent",
	          "ctrl KP_DOWN", "requestFocusForVisibleComponent",
		}),
	    "TabbedPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		   "ctrl PAGE_DOWN", "navigatePageDown",
	             "ctrl PAGE_UP", "navigatePageUp",
	                  "ctrl UP", "requestFocus",
	               "ctrl KP_UP", "requestFocus",
		 }),

	    // *** Table
	    "Table.font", ControlFont,
	    "Table.foreground", ControlTextColor,  // cell text color
	    "Table.background", WindowBackgroundColor,  // cell background color
            "Table.highlight", ControlHighlightColor,
            "Table.light", ControlLightColor,
            "Table.shadow", ControlShadowColor,
            "Table.darkShadow", ControlDarkShadowColor,
	    "Table.selectionForeground", SelectionTextColor,
	    "Table.selectionBackground", SelectionBackgroundColor,
      	    "Table.gridColor", gray,  // grid line color
	    "Table.focusCellBackground", WindowBackgroundColor,
	    "Table.focusCellForeground", ControlTextColor,
	    "Table.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                               "ctrl C", "copy",
                               "ctrl V", "paste",
                               "ctrl X", "cut",
                                 "COPY", "copy",
                                "PASTE", "paste",
                                  "CUT", "cut",
		                "RIGHT", "selectNextColumn",
		             "KP_RIGHT", "selectNextColumn",
		                 "LEFT", "selectPreviousColumn",
		              "KP_LEFT", "selectPreviousColumn",
		                 "DOWN", "selectNextRow",
		              "KP_DOWN", "selectNextRow",
		                   "UP", "selectPreviousRow",
		                "KP_UP", "selectPreviousRow",
		          "shift RIGHT", "selectNextColumnExtendSelection",
		       "shift KP_RIGHT", "selectNextColumnExtendSelection",
		           "shift LEFT", "selectPreviousColumnExtendSelection",
		        "shift KP_LEFT", "selectPreviousColumnExtendSelection",
		           "shift DOWN", "selectNextRowExtendSelection",
		        "shift KP_DOWN", "selectNextRowExtendSelection",
		             "shift UP", "selectPreviousRowExtendSelection",
		          "shift KP_UP", "selectPreviousRowExtendSelection",
		              "PAGE_UP", "scrollUpChangeSelection",
		            "PAGE_DOWN", "scrollDownChangeSelection",
		                 "HOME", "selectFirstColumn",
		                  "END", "selectLastColumn",
		        "shift PAGE_UP", "scrollUpExtendSelection",
		      "shift PAGE_DOWN", "scrollDownExtendSelection",
		           "shift HOME", "selectFirstColumnExtendSelection",
		            "shift END", "selectLastColumnExtendSelection",
		         "ctrl PAGE_UP", "scrollLeftChangeSelection",
		       "ctrl PAGE_DOWN", "scrollRightChangeSelection",
		            "ctrl HOME", "selectFirstRow",
		             "ctrl END", "selectLastRow",
		   "ctrl shift PAGE_UP", "scrollRightExtendSelection",
		 "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection",
		      "ctrl shift HOME", "selectFirstRowExtendSelection",
		       "ctrl shift END", "selectLastRowExtendSelection",
		                  "TAB", "selectNextColumnCell",
		            "shift TAB", "selectPreviousColumnCell",
		                "ENTER", "selectNextRowCell",
		          "shift ENTER", "selectPreviousRowCell",
		               "ctrl A", "selectAll",
		               "ESCAPE", "cancel",
		                   "F2", "startEditing"
		 }),

	    "TableHeader.font", ControlFont,
	    "TableHeader.foreground", ControlTextColor, // header text color
	    "TableHeader.background", ControlBackgroundColor, // header background

	    // *** TextArea
	    "TextArea.font", FixedControlFont,
	    "TextArea.background", WindowBackgroundColor,
	    "TextArea.foreground", WindowTextColor,
	    "TextArea.inactiveForeground", InactiveTextColor,
	    "TextArea.selectionBackground", SelectionBackgroundColor,
	    "TextArea.selectionForeground", SelectionTextColor,
	    "TextArea.caretForeground", WindowTextColor,

	    // *** TextField
	    "TextField.font", ControlFont,
	    "TextField.background", WindowBackgroundColor,
	    "TextField.foreground", WindowTextColor,
	    "TextField.shadow", ControlShadowColor,
	    "TextField.darkShadow", ControlDarkShadowColor,
	    "TextField.light", ControlLightColor,
	    "TextField.highlight", ControlHighlightColor,
	    "TextField.inactiveForeground", InactiveTextColor,
	    "TextField.inactiveBackground", ControlBackgroundColor,
	    "TextField.selectionBackground", SelectionBackgroundColor,
	    "TextField.selectionForeground", SelectionTextColor,
	    "TextField.caretForeground", WindowTextColor,

	    // *** TextPane
	    "TextPane.font", ControlFont,
	    "TextPane.background", WindowBackgroundColor,
	    "TextPane.foreground", WindowTextColor,
	    "TextPane.selectionBackground", SelectionBackgroundColor,
	    "TextPane.selectionForeground", SelectionTextColor,
	    "TextPane.caretForeground", WindowTextColor,

	    // *** TitledBorder
            "TitledBorder.font", ControlFont,
            "TitledBorder.titleColor", ControlTextColor,

	    // *** ToggleButton
	    "ToggleButton.font", ControlFont,
            "ToggleButton.background", ControlBackgroundColor,
            "ToggleButton.foreground", ControlTextColor,
	    "ToggleButton.shadow", ControlShadowColor,
            "ToggleButton.darkShadow", ControlDarkShadowColor,
            "ToggleButton.light", ControlLightColor,
            "ToggleButton.highlight", ControlHighlightColor,
            "ToggleButton.focus", ControlTextColor,
	    "ToggleButton.textShiftOffset", new Integer(1),
 	    "ToggleButton.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	        }),

	    // *** ToolBar
	    "ToolBar.font", MenuFont,
	    "ToolBar.background", ControlBackgroundColor,
	    "ToolBar.foreground", ControlTextColor,
	    "ToolBar.shadow", ControlShadowColor,
	    "ToolBar.darkShadow", ControlDarkShadowColor,
	    "ToolBar.light", ControlLightColor,
	    "ToolBar.highlight", ControlHighlightColor,
	    "ToolBar.dockingBackground", ControlBackgroundColor,
	    "ToolBar.dockingForeground", red,
	    "ToolBar.floatingBackground", ControlBackgroundColor,
	    "ToolBar.floatingForeground", darkGray,
	    "ToolBar.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		        "UP", "navigateUp",
		     "KP_UP", "navigateUp",
		      "DOWN", "navigateDown",
		   "KP_DOWN", "navigateDown",
		      "LEFT", "navigateLeft",
		   "KP_LEFT", "navigateLeft",
		     "RIGHT", "navigateRight",
		  "KP_RIGHT", "navigateRight"
		 }),
	    "ToolBar.separatorSize", null,

	    // *** ToolTip
            "ToolTip.font", ToolTipFont,
            "ToolTip.background", new DesktopProperty(
                                           "win.tooltip.backgroundColor",
					    table.get("info"), toolkit),
            "ToolTip.foreground", new DesktopProperty(
                                           "win.tooltip.textColor",
					    table.get("infoText"), toolkit),

	    // *** Tree
	    "Tree.font", ControlFont,
	    "Tree.background", WindowBackgroundColor,
            "Tree.foreground", WindowTextColor,
	    "Tree.hash", gray,
	    "Tree.textForeground", WindowTextColor,
	    "Tree.textBackground", WindowBackgroundColor,
	    "Tree.selectionForeground", SelectionTextColor,
	    "Tree.selectionBackground", SelectionBackgroundColor,
            "Tree.expandedIcon", treeExpandedIcon,
            "Tree.collapsedIcon", treeCollapsedIcon,
	    "Tree.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                                 "ctrl C", "copy",
                                 "ctrl V", "paste",
                                 "ctrl X", "cut",
                                   "COPY", "copy",
                                  "PASTE", "paste",
                                    "CUT", "cut",
		                     "UP", "selectPrevious",
		                  "KP_UP", "selectPrevious",
		               "shift UP", "selectPreviousExtendSelection",
		            "shift KP_UP", "selectPreviousExtendSelection",
		                   "DOWN", "selectNext",
		                "KP_DOWN", "selectNext",
		             "shift DOWN", "selectNextExtendSelection",
		          "shift KP_DOWN", "selectNextExtendSelection",
		                  "RIGHT", "selectChild",
		               "KP_RIGHT", "selectChild",
		                   "LEFT", "selectParent",
		                "KP_LEFT", "selectParent",
		                "PAGE_UP", "scrollUpChangeSelection",
		          "shift PAGE_UP", "scrollUpExtendSelection",
		              "PAGE_DOWN", "scrollDownChangeSelection",
		        "shift PAGE_DOWN", "scrollDownExtendSelection",
		                   "HOME", "selectFirst",
		             "shift HOME", "selectFirstExtendSelection",
		                    "END", "selectLast",
		              "shift END", "selectLastExtendSelection",
		                     "F2", "startEditing",
		                 "ctrl A", "selectAll",
		             "ctrl SLASH", "selectAll",
		        "ctrl BACK_SLASH", "clearSelection",
		             "ctrl SPACE", "toggleSelectionPreserveAnchor",
		            "shift SPACE", "extendSelection",
		              "ctrl HOME", "selectFirstChangeLead",
		               "ctrl END", "selectLastChangeLead",
		                "ctrl UP", "selectPreviousChangeLead",
		             "ctrl KP_UP", "selectPreviousChangeLead",
		              "ctrl DOWN", "selectNextChangeLead",
		           "ctrl KP_DOWN", "selectNextChangeLead",
		         "ctrl PAGE_DOWN", "scrollDownChangeLead",
		   "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
		           "ctrl PAGE_UP", "scrollUpChangeLead",
		     "ctrl shift PAGE_UP", "scrollUpExtendSelection",
		              "ctrl LEFT", "scrollLeft",
		           "ctrl KP_LEFT", "scrollLeft",
		             "ctrl RIGHT", "scrollRight",
		          "ctrl KP_RIGHT", "scrollRight",
		                  "SPACE", "toggleSelectionPreserveAnchor",
		 }),
	    "Tree.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancel"
		 }),

	    // *** Viewport
	    "Viewport.font", ControlFont,
	    "Viewport.background", ControlBackgroundColor,
	    "Viewport.foreground", WindowTextColor,


        };

        table.putDefaults(defaults);
	table.putDefaults(getLazyValueDefaults());
    }

    /**
     * If we support loading of fonts from the desktop this will return
     * a DesktopProperty representing the font. If the font can't be
     * represented in the current encoding this will return null and
     * turn off the use of system fonts.
     */
    private Object getDesktopFontValue(String fontName, Object backup,
                                       Toolkit kit) {
        if (useSystemFontSettings) {
            DesktopProperty prop = new DesktopProperty(fontName, backup, kit);
            Font font = (Font)prop.createValue(null);
            if (!SunGraphicsEnvironment.isLogicalFont(font) &&
                   !SunGraphicsEnvironment.fontSupportsDefaultEncoding(font)) {
                // If this font can't be supported then turn off using
                // system fonts and fallback to use system font sizes.
                useSystemFontSettings = false;
                useSystemFontSizeSettings = true;
                return null;
            }
            return prop;
        }
        return null;
    }

    // When a desktop property change is detected, these classes must be
    // reinitialized in the defaults table to ensure the classes reference
    // the updated desktop property values (colors mostly)
    //
    private Object[] getLazyValueDefaults() {

	XPStyle xp = XPStyle.getXP();

	Object buttonBorder;
	if (xp != null) {
	    buttonBorder = xp.getBorder("button.pushbutton");
	} else {
	    buttonBorder = new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getButtonBorder");
	}
	Object textFieldBorder;
	Object textFieldMargin;
	if (xp != null) {
	    textFieldBorder = xp.getBorder("edit");
	    textFieldMargin = new InsetsUIResource(1, 5, 2, 4);
	} else {
	    textFieldBorder = new UIDefaults.ProxyLazyValue(
			       "javax.swing.plaf.basic.BasicBorders", 
			       "getTextFieldBorder");
	    textFieldMargin = new InsetsUIResource(1, 1, 1, 1);
	}

	Object spinnerBorder = textFieldBorder;

	Object comboBoxBorder = (xp != null) ? xp.getBorder("combobox") : textFieldBorder;

	// For focus rectangle for cells and trees.
	Object focusCellHighlightBorder = new UIDefaults.ProxyLazyValue(
			  "com.sun.java.swing.plaf.windows.WindowsBorders",
			  "getFocusCellHighlightBorder");

	Object etchedBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource",
			  "getEtchedBorderUIResource");

	Object internalFrameBorder = new UIDefaults.ProxyLazyValue(
                "com.sun.java.swing.plaf.windows.WindowsBorders", 
		"getInternalFrameBorder");

        Object loweredBevelBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource",
			  "getLoweredBevelBorderUIResource");


        Object marginBorder = new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders$MarginBorder");

	Object menuBarBorder = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.basic.BasicBorders", 
		"getMenuBarBorder");


	Object popupMenuBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.basic.BasicBorders",
			  "getInternalFrameBorder");

	// *** ProgressBar
	Object progressBarBorder = new UIDefaults.ProxyLazyValue(
			      "com.sun.java.swing.plaf.windows.WindowsBorders", 
			      "getProgressBarBorder");

	Object radioButtonBorder = new UIDefaults.ProxyLazyValue(
			       "javax.swing.plaf.basic.BasicBorders", 
			       "getRadioButtonBorder");

	Object scrollPaneBorder = (xp != null) ? xp.getBorder("listbox") : textFieldBorder;
	Object tableScrollPaneBorder = (xp != null) ? scrollPaneBorder : loweredBevelBorder;

	Object tableHeaderBorder = new UIDefaults.ProxyLazyValue(
			  "com.sun.java.swing.plaf.windows.WindowsBorders",
			  "getTableHeaderBorder");

	// *** ToolBar
	Object toolBarBorder = new UIDefaults.ProxyLazyValue(
			      "com.sun.java.swing.plaf.windows.WindowsBorders", 
			      "getToolBarBorder");

        // *** ToolTips
        Object toolTipBorder = new UIDefaults.ProxyLazyValue(
                              "javax.swing.plaf.BorderUIResource",
			      "getBlackLineBorderUIResource");



        Object checkBoxIcon = new UIDefaults.ProxyLazyValue(
		     "com.sun.java.swing.plaf.windows.WindowsIconFactory",
		     "getCheckBoxIcon");

        Object radioButtonIcon = new UIDefaults.ProxyLazyValue(
		     "com.sun.java.swing.plaf.windows.WindowsIconFactory",
		     "getRadioButtonIcon");

        Object menuItemCheckIcon = new UIDefaults.ProxyLazyValue(
		     "com.sun.java.swing.plaf.windows.WindowsIconFactory",
		     "getMenuItemCheckIcon");

        Object menuItemArrowIcon = new UIDefaults.ProxyLazyValue(
		     "com.sun.java.swing.plaf.windows.WindowsIconFactory",
		     "getMenuItemArrowIcon");

        Object menuArrowIcon = new UIDefaults.ProxyLazyValue(
		     "com.sun.java.swing.plaf.windows.WindowsIconFactory",
		     "getMenuArrowIcon");


        Object[] lazyDefaults = {
	    "Button.border", buttonBorder,
            "CheckBox.border", radioButtonBorder,
            "ComboBox.border", comboBoxBorder,
	    "DesktopIcon.border", internalFrameBorder,
	    "FormattedTextField.border", textFieldBorder,
	    "FormattedTextField.margin", textFieldMargin,
	    "InternalFrame.border", internalFrameBorder,
	    "List.focusCellHighlightBorder", focusCellHighlightBorder,
	    "Table.focusCellHighlightBorder", focusCellHighlightBorder,
	    "Tree.selectionBorderColor", focusCellHighlightBorder,
	    "Menu.border", marginBorder,
	    "MenuBar.border", menuBarBorder,
            "MenuItem.border", marginBorder,
            "PasswordField.border", textFieldBorder,
            "PasswordField.margin", textFieldMargin,
	    "PopupMenu.border", popupMenuBorder,
	    "ProgressBar.border", progressBarBorder,
            "RadioButton.border", radioButtonBorder,
	    "ScrollPane.border", scrollPaneBorder,
	    "Spinner.border", spinnerBorder,
	    "Table.scrollPaneBorder", tableScrollPaneBorder,
	    "TableHeader.cellBorder", tableHeaderBorder,
	    "TextField.border", textFieldBorder,
	    "TextField.margin", textFieldMargin,
            "TitledBorder.border", etchedBorder,
            "ToggleButton.border", radioButtonBorder,
	    "ToolBar.border", toolBarBorder,
            "ToolTip.border", toolTipBorder,

            "CheckBox.icon", checkBoxIcon,
            "Menu.arrowIcon", menuArrowIcon,
            "MenuItem.checkIcon", menuItemCheckIcon,
            "MenuItem.arrowIcon", menuItemArrowIcon,
            "RadioButton.icon", radioButtonIcon
	};

	return lazyDefaults;
    }

    public void uninitialize() {
	toolkit = null;

        if (WindowsPopupMenuUI.mnemonicListener != null) {
            MenuSelectionManager.defaultManager().
                removeChangeListener(WindowsPopupMenuUI.mnemonicListener);
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
            removeKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
        DesktopProperty.flushUnreferencedProperties();
    }


    // Toggle flag for drawing the mnemonic state
    private static boolean isMnemonicHidden = true;

    // Flag which indicates that the Win98/Win2k/WinME features
    // should be disabled.
    private static boolean isClassicWindows = false;

    /**
     * Sets the state of the hide mnemonic flag. This flag is used by the 
     * component UI delegates to determine if the mnemonic should be rendered.
     * This method is a non operation if the underlying operating system
     * does not support the mnemonic hiding feature.
     * 
     * @param hide true if mnemonics should be hidden
     * @since 1.4
     */
    public static void setMnemonicHidden(boolean hide) {
	if (UIManager.getBoolean("Button.showMnemonics") == true) {
	    // Do not hide mnemonics if the UI defaults do not support this
	    isMnemonicHidden = false;
	} else {
	    isMnemonicHidden = hide;
	}
    }

    /**
     * Gets the state of the hide mnemonic flag. This only has meaning 
     * if this feature is supported by the underlying OS.
     *
     * @return true if mnemonics are hidden, otherwise, false
     * @see #setMnemonicHidden
     * @since 1.4
     */
    public static boolean isMnemonicHidden() {
	if (UIManager.getBoolean("Button.showMnemonics") == true) {
	    // Do not hide mnemonics if the UI defaults do not support this
	    isMnemonicHidden = false;
	}
	return isMnemonicHidden;
    }

    /**
     * Gets the state of the flag which indicates if the old Windows
     * look and feel should be rendered. This flag is used by the
     * component UI delegates as a hint to determine which style the component
     * should be rendered.
     *
     * @return true if Windows 95 and Windows NT 4 look and feel should
     *         be rendered
     * @since 1.4
     */
    public static boolean isClassicWindows() {
	return isClassicWindows;
    }

    /**
     * <p>
     * Invoked when the user attempts an invalid operation, 
     * such as pasting into an uneditable <code>JTextField</code> 
     * that has focus.
     * </p>
     * <p>
     * If the user has enabled visual error indication on
     * the desktop, this method will flash the caption bar
     * of the active window. The user can also set the
     * property awt.visualbell=true to achieve the same
     * results.
     * </p>
     *
     * @param component Component the error occured in, may be 
     *			null indicating the error condition is 
     *			not directly associated with a 
     *			<code>Component</code>.
     * 
     * @see javax.swing.LookAndFeel#provideErrorFeedback
     */
     public void provideErrorFeedback(Component component) {
	 super.provideErrorFeedback(component);
     }

    // ********* Auditory Cue support methods and objects *********

    /**
     * Returns an <code>Action</code>.
     * <P>
     * This Action contains the information and logic to render an
     * auditory cue. The <code>Object</code> that is passed to this
     * method contains the information needed to render the auditory 
     * cue. Normally, this <code>Object</code> is a <code>String</code> 
     * that points to a <code>Toolkit</code> <code>desktopProperty</code>.
     * This <code>desktopProperty</code> is resolved by AWT and the 
     * Windows OS.
     * <P>
     * This <code>Action</code>'s <code>actionPerformed</code> method
     * is fired by the <code>playSound</code> method.
     *
     * @return      an Action which knows how to render the auditory
     *              cue for one particular system or user activity
     * @see #playSound(Action)
     * @since 1.4
     */
    protected Action createAudioAction(Object key) {
	if (key != null) {
	    String audioKey = (String)key;
	    String audioValue = (String)UIManager.get(key);
	    return new AudioAction(audioKey, audioValue);
    	} else {
	    return null;
	}
    }

    static void repaintRootPane(Component c) {
        JRootPane root = null;
        for (; c != null; c = c.getParent()) {
            if (c instanceof JRootPane) {
                root = (JRootPane)c;
            }
        }

        if (root != null) {
            root.repaint();
        } else {
            c.repaint();
        }
    }

    /**
     * Pass the name String to the super constructor. This is used 
     * later to identify the Action and decide whether to play it or 
     * not. Store the resource String. It is used to get the audio 
     * resource. In this case, the resource is a <code>Runnable</code> 
     * supplied by <code>Toolkit</code>. This <code>Runnable</code> is
     * effectively a pointer down into the Win32 OS that knows how to
     * play the right sound.
     *
     * @since 1.4
     */
    private static class AudioAction extends AbstractAction {
	private Runnable audioRunnable;
	private String audioResource;
	/**
	 * We use the String as the name of the Action and as a pointer to
	 * the underlying OSes audio resource.
	 */
	public AudioAction(String name, String resource) {
	    super(name);
	    audioResource = resource;
	}
	public void actionPerformed(ActionEvent e) {
	    if (audioRunnable == null) {
		audioRunnable = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty(audioResource);
	    }
	    if (audioRunnable != null) {
                // Runnable appears to block until completed playing, hence
                // start up another thread to handle playing.
                new Thread(audioRunnable).start();
	    }
	}
    }

    /**
     * Get an <code>Icon</code> from the native library (comctl32.dll) if available,
     * otherwise get it from an image resource file.
     *
     * @since 1.4
     */
    private static class LazyFileChooserIcon implements UIDefaults.LazyValue {
	private String nativeImage;
	private String resource;

	LazyFileChooserIcon(String nativeImage, String resource) {
	    this.nativeImage = nativeImage;
	    this.resource = resource;
	}

	public Object createValue(UIDefaults table) {
	    if (nativeImage != null) {
		Image image = (Image)ShellFolder.get(nativeImage);
		return (image != null) ? new ImageIcon(image) : LookAndFeel.makeIcon(getClass(), resource);
	    } else {
		return LookAndFeel.makeIcon(getClass(), resource);
	    }
	}
    }


    /**
     * DesktopProperty for fonts that only gets sizes, font name and style
     * are passed in.
     */
    private static class WindowsFontProperty extends DesktopProperty {
        private String fontName;
        private int fontSize;
        private int fontStyle;

        WindowsFontProperty(String key, Toolkit toolkit, String fontName,
                            int fontStyle, int fontSize) {
            super(key, null, toolkit);
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.fontStyle = fontStyle;
        }

        protected Object configureValue(Object value) {
            if (value == null) {
                value = new FontUIResource(fontName, fontStyle, fontSize);
            }
            else if (value instanceof Integer) {
                value = new FontUIResource(fontName, fontStyle,
                                           ((Integer)value).intValue());
            }
            return value;
        }
    }
}
