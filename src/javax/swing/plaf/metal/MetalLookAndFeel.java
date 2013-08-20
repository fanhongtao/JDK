/*
 * @(#)MetalLookAndFeel.java	1.182 04/04/02
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultEditorKit;
import java.util.*;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.lang.reflect.*;
import java.net.URL;
import java.io.Serializable;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingLazyValue;


/**
 * Implements the Java look and feel (codename: Metal).
 * <p>
 * By default metal uses bold fonts for many controls.  To make all
 * controls (with the exception of the internal frame title bars and
 * client decorated frame title bars) use plain fonts you can do either of
 * the following:
 * <ul>
 * <li>Set the system property <code>swing.boldMetal</code> to
 *     <code>false</code>.  For example,
 *     <code>java&nbsp;-Dswing.boldMetal=false&nbsp;MyApp</code>.
 * <li>Set the defaults property <code>swing.boldMetal</code> to
 *     <code>Boolean.FALSE</code>.  For example:
 *     <code>UIManager.put("swing.boldMetal",&nbsp;Boolean.FALSE);</code>
 * </ul>
 * The defaults property <code>swing.boldMetal</code>, if set,
 * takes precendence over the system property of the same name. After
 * setting this defaults property you need to re-install the
 * <code>MetalLookAndFeel</code>, as well as update the UI
 * of any previously created widgets. Otherwise the results are undefined.
 * These lines of code show you how to accomplish this:
 * <pre>
 *   // turn off bold fonts
 *   UIManager.put("swing.boldMetal", Boolean.FALSE);
 *
 *   // re-install the Metal Look and Feel
 *   UIManager.setLookAndFeel(new MetalLookAndFeel());
 *
 *   // only needed to update existing widgets
 *   SwingUtilities.updateComponentTreeUI(rootComponent);
 * </pre>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version @(#)MetalLookAndFeel.java	1.182 04/04/02
 * @author Steve Wilson
 */
public class MetalLookAndFeel extends BasicLookAndFeel
{

    private static boolean METAL_LOOK_AND_FEEL_INITED = false;

    private static MetalTheme currentTheme;
    private static boolean isOnlyOneContext = true;
    private static AppContext cachedAppContext;

    /**
     * True if checked for windows yet.
     */
    private static boolean checkedWindows;
    /**
     * True if running on Windows.
     */
    private static boolean isWindows;

    /**
     * Set to true first time we've checked swing.useSystemFontSettings.
     */
    private static boolean checkedSystemFontSettings;

    /**
     * True indicates we should use system fonts, unless the developer has
     * specified otherwise with Application.useSystemFontSettings.
     */
    private static boolean useSystemFonts;


    /**
     * Returns true if running on Windows.
     */
    static boolean isWindows() {
        if (!checkedWindows) {
            String osName = (String)AccessController.doPrivileged(
                new GetPropertyAction("os.name"));
            if (osName != null && osName.indexOf("Windows") != -1) {
                isWindows = true;
                String systemFonts = (String)AccessController.doPrivileged(
                    new GetPropertyAction("swing.useSystemFontSettings"));
                useSystemFonts = (systemFonts != null &&
                               (Boolean.valueOf(systemFonts).booleanValue()));
            }
            checkedWindows = true;
        }
        return isWindows;
    }

    /**
     * Returns true if system fonts should be used, this is only useful
     * for windows.
     */
    static boolean useSystemFonts() {
        if (isWindows() && useSystemFonts) {
            if (METAL_LOOK_AND_FEEL_INITED) {
                Object value = UIManager.get(
                                 "Application.useSystemFontSettings");

                return (value == null || Boolean.TRUE.equals(value));
            }
            // If an instanceof MetalLookAndFeel hasn't been inited yet, we
            // don't want to trigger loading of a UI by asking the UIManager
            // for a property, assume the user wants system fonts. This will
            // be properly adjusted when install is invoked on the
            // MetalTheme
            return true;
        }
        return false;
    }

    /**
     * Returns true if the high contrast theme should be used as the default
     * theme.
     */
    private static boolean useHighContrastTheme() {
        if (isWindows() && useSystemFonts()) {
            Boolean highContrast = (Boolean)Toolkit.getDefaultToolkit().
                                  getDesktopProperty("win.highContrast.on");

            return (highContrast == null) ? false : highContrast.
                                            booleanValue();
        }
        return false;
    }

    /**
     * Returns true if we're using the Ocean Theme.
     */
    static boolean usingOcean() {
        return (getCurrentTheme() instanceof OceanTheme);
    }

    public String getName() {
        return "Metal";
    }

    public String getID() {
        return "Metal";
    }

    public String getDescription() {
        return "The Java(tm) Look and Feel";
    }

    
    public boolean isNativeLookAndFeel() {
        return false;
    }


    public boolean isSupportedLookAndFeel() {
        return true;
    }
    
    /**
     * Returns true if the <code>LookAndFeel</code> returned
     * <code>RootPaneUI</code> instances support providing Window decorations
     * in a <code>JRootPane</code>.
     * <p>
     * This implementation returns true, since it does support providing
     * these border and window title pane decorations.
     *
     * @return True if the RootPaneUI instances created support client side
     *              decorations
     * @see JDialog#setDefaultLookAndFeelDecorated
     * @see JFrame#setDefaultLookAndFeelDecorated
     * @see JRootPane#setWindowDecorationStyle
     * @since 1.4
     */
    public boolean getSupportsWindowDecorations() {
        return true;
    }

    /** 
     * Creates the mapping from
     * UI class IDs to <code>ComponentUI</code> classes,
     * putting the ID-<code>ComponentUI</code> pairs
     * in the passed-in defaults table.
     * Each <code>JComponent</code> class
     * specifies its own UI class ID string.
     * For example, 
     * <code>JButton</code> has the UI class ID "ButtonUI",
     * which this method maps to "javax.swing.plaf.metal.MetalButtonUI".
     * 
     * @see BasicLookAndFeel#getDefaults
     * @see javax.swing.JComponent#getUIClassID
     */
    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);
        final String metalPackageName = "javax.swing.plaf.metal.";

        Object[] uiDefaults = {
                   "ButtonUI", metalPackageName + "MetalButtonUI",
                 "CheckBoxUI", metalPackageName + "MetalCheckBoxUI",
                 "ComboBoxUI", metalPackageName + "MetalComboBoxUI",
              "DesktopIconUI", metalPackageName + "MetalDesktopIconUI",
              "FileChooserUI", metalPackageName + "MetalFileChooserUI",
            "InternalFrameUI", metalPackageName + "MetalInternalFrameUI",
                    "LabelUI", metalPackageName + "MetalLabelUI",
       "PopupMenuSeparatorUI", metalPackageName + "MetalPopupMenuSeparatorUI",
              "ProgressBarUI", metalPackageName + "MetalProgressBarUI",
              "RadioButtonUI", metalPackageName + "MetalRadioButtonUI",
                "ScrollBarUI", metalPackageName + "MetalScrollBarUI",
               "ScrollPaneUI", metalPackageName + "MetalScrollPaneUI",
                "SeparatorUI", metalPackageName + "MetalSeparatorUI",
                   "SliderUI", metalPackageName + "MetalSliderUI",
                "SplitPaneUI", metalPackageName + "MetalSplitPaneUI",
               "TabbedPaneUI", metalPackageName + "MetalTabbedPaneUI",
                "TextFieldUI", metalPackageName + "MetalTextFieldUI",
             "ToggleButtonUI", metalPackageName + "MetalToggleButtonUI",
                  "ToolBarUI", metalPackageName + "MetalToolBarUI",
                  "ToolTipUI", metalPackageName + "MetalToolTipUI",
                     "TreeUI", metalPackageName + "MetalTreeUI",
                 "RootPaneUI", metalPackageName + "MetalRootPaneUI",
        };

        table.putDefaults(uiDefaults);
    }

    /**
     * Load the SystemColors into the defaults table.  The keys
     * for SystemColor defaults are the same as the names of
     * the public fields in SystemColor.
     */
    protected void initSystemColorDefaults(UIDefaults table)
    {
        MetalTheme theme = getCurrentTheme();
        Color control = theme.getControl();
        Object[] systemColors = {
                "desktop", theme.getDesktopColor(), /* Color of the desktop background */
          "activeCaption", theme.getWindowTitleBackground(), /* Color for captions (title bars) when they are active. */
      "activeCaptionText", theme.getWindowTitleForeground(), /* Text color for text in captions (title bars). */
    "activeCaptionBorder", theme.getPrimaryControlShadow(), /* Border color for caption (title bar) window borders. */
        "inactiveCaption", theme.getWindowTitleInactiveBackground(), /* Color for captions (title bars) when not active. */
    "inactiveCaptionText", theme.getWindowTitleInactiveForeground(), /* Text color for text in inactive captions (title bars). */
  "inactiveCaptionBorder", theme.getControlShadow(), /* Border color for inactive caption (title bar) window borders. */
                 "window", theme.getWindowBackground(), /* Default color for the interior of windows */
           "windowBorder", control, /* ??? */
             "windowText", theme.getUserTextColor(), /* ??? */
                   "menu", theme.getMenuBackground(), /* Background color for menus */
               "menuText", theme.getMenuForeground(), /* Text color for menus  */
                   "text", theme.getWindowBackground(), /* Text background color */
               "textText", theme.getUserTextColor(), /* Text foreground color */
          "textHighlight", theme.getTextHighlightColor(), /* Text background color when selected */
      "textHighlightText", theme.getHighlightedTextColor(), /* Text color when selected */
       "textInactiveText", theme.getInactiveSystemTextColor(), /* Text color when disabled */
                "control", control, /* Default color for controls (buttons, sliders, etc) */
            "controlText", theme.getControlTextColor(), /* Default color for text in controls */
       "controlHighlight", theme.getControlHighlight(), /* Specular highlight (opposite of the shadow) */
     "controlLtHighlight", theme.getControlHighlight(), /* Highlight color for controls */
          "controlShadow", theme.getControlShadow(), /* Shadow color for controls */
        "controlDkShadow", theme.getControlDarkShadow(), /* Dark shadow color for controls */
              "scrollbar", control, /* Scrollbar background (usually the "track") */
                   "info", theme.getPrimaryControl(), /* ToolTip Background */
               "infoText", theme.getPrimaryControlInfo()  /* ToolTip Text */
        };

        table.putDefaults(systemColors);
    }

    /**
     * Initialize the defaults table with the name of the ResourceBundle
     * used for getting localized defaults.
     */
    private void initResourceBundle(UIDefaults table) {
        table.addResourceBundle( "com.sun.swing.internal.plaf.metal.resources.metal" );
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults( table );

        initResourceBundle(table);

        Color acceleratorForeground = getAcceleratorForeground();
        Color acceleratorSelectedForeground = getAcceleratorSelectedForeground();
        Color control = getControl();
        Color controlHighlight = getControlHighlight();
        Color controlShadow = getControlShadow();
        Color controlDarkShadow = getControlDarkShadow();
        Color controlTextColor = getControlTextColor();
        Color focusColor = getFocusColor();
        Color inactiveControlTextColor = getInactiveControlTextColor();
        Color menuBackground = getMenuBackground();
        Color menuSelectedBackground = getMenuSelectedBackground();
        Color menuDisabledForeground = getMenuDisabledForeground();
        Color menuSelectedForeground = getMenuSelectedForeground();
        Color primaryControl = getPrimaryControl();
        Color primaryControlDarkShadow = getPrimaryControlDarkShadow();
        Color primaryControlShadow = getPrimaryControlShadow();
        Color systemTextColor = getSystemTextColor();

        Insets zeroInsets = new InsetsUIResource(0, 0, 0, 0);

        Integer zero = new Integer(0);

	Object textFieldBorder = 
	    new SwingLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getTextFieldBorder");

        Object dialogBorder = new MetalLazyValue(
                          "javax.swing.plaf.metal.MetalBorders$DialogBorder");

        Object questionDialogBorder = new MetalLazyValue(
                  "javax.swing.plaf.metal.MetalBorders$QuestionDialogBorder");

	Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[] {
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
                       "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                           "ctrl H", DefaultEditorKit.deletePrevCharAction,
                           "DELETE", DefaultEditorKit.deleteNextCharAction,
                            "RIGHT", DefaultEditorKit.forwardAction,
                             "LEFT", DefaultEditorKit.backwardAction,
                         "KP_RIGHT", DefaultEditorKit.forwardAction,
                          "KP_LEFT", DefaultEditorKit.backwardAction,
			    "ENTER", JTextField.notifyAction,
		  "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
                   "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
	});

        Object passwordInputMap = new UIDefaults.LazyInputMap(new Object[] {
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
                        "ctrl LEFT", DefaultEditorKit.beginLineAction,
                     "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
                       "ctrl RIGHT", DefaultEditorKit.endLineAction,
                    "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
                  "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
               "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
                 "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
              "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
                           "ctrl A", DefaultEditorKit.selectAllAction,
                             "HOME", DefaultEditorKit.beginLineAction,
                              "END", DefaultEditorKit.endLineAction,
                       "shift HOME", DefaultEditorKit.selectionBeginLineAction,
                        "shift END", DefaultEditorKit.selectionEndLineAction,
                       "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                           "ctrl H", DefaultEditorKit.deletePrevCharAction,
                           "DELETE", DefaultEditorKit.deleteNextCharAction,
                            "RIGHT", DefaultEditorKit.forwardAction,
                             "LEFT", DefaultEditorKit.backwardAction,
                         "KP_RIGHT", DefaultEditorKit.forwardAction,
                          "KP_LEFT", DefaultEditorKit.backwardAction,
                            "ENTER", JTextField.notifyAction,
                  "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
                   "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
        });

	Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[] {
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

			       "UP", DefaultEditorKit.upAction,
			    "KP_UP", DefaultEditorKit.upAction,
			     "DOWN", DefaultEditorKit.downAction,
			  "KP_DOWN", DefaultEditorKit.downAction,
			  "PAGE_UP", DefaultEditorKit.pageUpAction,
			"PAGE_DOWN", DefaultEditorKit.pageDownAction,
		    "shift PAGE_UP", "selection-page-up",
 	          "shift PAGE_DOWN", "selection-page-down",
	       "ctrl shift PAGE_UP", "selection-page-left",
 	     "ctrl shift PAGE_DOWN", "selection-page-right",
			 "shift UP", DefaultEditorKit.selectionUpAction,
		      "shift KP_UP", DefaultEditorKit.selectionUpAction,
		       "shift DOWN", DefaultEditorKit.selectionDownAction,
		    "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
			    "ENTER", DefaultEditorKit.insertBreakAction,
                       "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                           "ctrl H", DefaultEditorKit.deletePrevCharAction,
                           "DELETE", DefaultEditorKit.deleteNextCharAction,
                            "RIGHT", DefaultEditorKit.forwardAction,
                             "LEFT", DefaultEditorKit.backwardAction, 
                         "KP_RIGHT", DefaultEditorKit.forwardAction,
                          "KP_LEFT", DefaultEditorKit.backwardAction,
			      "TAB", DefaultEditorKit.insertTabAction,
		  "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
			"ctrl HOME", DefaultEditorKit.beginAction,
			 "ctrl END", DefaultEditorKit.endAction,
		  "ctrl shift HOME", DefaultEditorKit.selectionBeginAction,
		   "ctrl shift END", DefaultEditorKit.selectionEndAction,
                           "ctrl T", "next-link-action",
                     "ctrl shift T", "previous-link-action",
                       "ctrl SPACE", "activate-link-action",
                   "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
	});

        Object scrollPaneBorder = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ScrollPaneBorder");
        Object buttonBorder = 
	    	    new SwingLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getButtonBorder");
  
        Object toggleButtonBorder =  
	    new SwingLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getToggleButtonBorder");

        Object titledBorderBorder = 
	    new SwingLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {controlShadow});

        Object desktopIconBorder = 
	    new SwingLazyValue(
			  "javax.swing.plaf.metal.MetalBorders",
			  "getDesktopIconBorder");

        Object menuBarBorder = 
	    new SwingLazyValue(
			  "javax.swing.plaf.metal.MetalBorders$MenuBarBorder");

        Object popupMenuBorder = 
	    new SwingLazyValue(
			 "javax.swing.plaf.metal.MetalBorders$PopupMenuBorder");
        Object menuItemBorder = 
	    new SwingLazyValue(
			 "javax.swing.plaf.metal.MetalBorders$MenuItemBorder");

	Object menuItemAcceleratorDelimiter = new String("-");
        Object toolBarBorder = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ToolBarBorder");

	Object progressBarBorder = new SwingLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {controlDarkShadow, new Integer(1)});

        Object toolTipBorder = new SwingLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {primaryControlDarkShadow});

        Object toolTipBorderInactive = new SwingLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {controlDarkShadow});

        Object focusCellHighlightBorder = new SwingLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {focusColor});

        Object tabbedPaneTabAreaInsets = new InsetsUIResource(4, 2, 0, 6);

        Object tabbedPaneTabInsets = new InsetsUIResource(0, 9, 1, 9);

	final Object[] internalFrameIconArgs = new Object[1];
	internalFrameIconArgs[0] = new Integer(16);

	Object[] defaultCueList = new Object[] {
		"OptionPane.errorSound",
		"OptionPane.informationSound",
		"OptionPane.questionSound",
		"OptionPane.warningSound" };

        MetalTheme theme = getCurrentTheme();
        Object menuTextValue = new FontActiveValue(theme,
                                                   MetalTheme.MENU_TEXT_FONT);
        Object controlTextValue = new FontActiveValue(theme,
                               MetalTheme.CONTROL_TEXT_FONT);
        Object userTextValue = new FontActiveValue(theme,
                                                   MetalTheme.USER_TEXT_FONT);
        Object windowTitleValue = new FontActiveValue(theme,
                               MetalTheme.WINDOW_TITLE_FONT);
        Object subTextValue = new FontActiveValue(theme,
                                                  MetalTheme.SUB_TEXT_FONT);
        Object systemTextValue = new FontActiveValue(theme,
                                                 MetalTheme.SYSTEM_TEXT_FONT);
        //
        // DEFAULTS TABLE
        //

        Object[] defaults = {
	    // *** Auditory Feedback
	    "AuditoryCues.defaultCueList", defaultCueList,
	    // this key defines which of the various cues to render 
            // This is disabled until sound bugs can be resolved.
	    "AuditoryCues.playList", null, // defaultCueList,

            // Text (Note: many are inherited)
            "TextField.border", textFieldBorder,
	    "TextField.font", userTextValue,

            "PasswordField.border", textFieldBorder,
            // passwordField.font should actually map to
            // win.ansiFixed.font.height on windows.
            "PasswordField.font", userTextValue,

            // TextArea.font should actually map to win.ansiFixed.font.height
            // on windows.
            "TextArea.font", userTextValue,

	    "TextPane.background", table.get("window"),
            "TextPane.font", userTextValue,

	    "EditorPane.background", table.get("window"),
	    "EditorPane.font", userTextValue,

	    "TextField.focusInputMap", fieldInputMap,
	    "PasswordField.focusInputMap", passwordInputMap,
	    "TextArea.focusInputMap", multilineInputMap,
	    "TextPane.focusInputMap", multilineInputMap,
	    "EditorPane.focusInputMap", multilineInputMap,

            // FormattedTextFields
            "FormattedTextField.border", textFieldBorder,
            "FormattedTextField.font", userTextValue,
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
                       "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                           "ctrl H", DefaultEditorKit.deletePrevCharAction,
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
            

            // Buttons
            "Button.defaultButtonFollowsFocus", Boolean.FALSE,
            "Button.disabledText", inactiveControlTextColor,
            "Button.select", controlShadow,
            "Button.border", buttonBorder,
            "Button.font", controlTextValue,
            "Button.focus", focusColor,
            "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                          "SPACE", "pressed",
                 "released SPACE", "released"
              }),

            "CheckBox.disabledText", inactiveControlTextColor,
            "Checkbox.select", controlShadow,
            "CheckBox.font", controlTextValue,
            "CheckBox.focus", focusColor,
            "CheckBox.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxIcon"),
	    "CheckBox.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
		 }),

            "RadioButton.disabledText", inactiveControlTextColor,
            "RadioButton.select", controlShadow,
            "RadioButton.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonIcon"),
            "RadioButton.font", controlTextValue,
            "RadioButton.focus", focusColor,
	    "RadioButton.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                          "SPACE", "pressed",
                 "released SPACE", "released"
	      }),

            "ToggleButton.select", controlShadow,
            "ToggleButton.disabledText", inactiveControlTextColor,
            "ToggleButton.focus", focusColor,
            "ToggleButton.border", toggleButtonBorder,
            "ToggleButton.font", controlTextValue,
	    "ToggleButton.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	        }),


            // File View 
            "FileView.directoryIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"),
            "FileView.fileIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"),
            "FileView.computerIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeComputerIcon"),
            "FileView.hardDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeHardDriveIcon"),
            "FileView.floppyDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFloppyDriveIcon"),

            // File Chooser
            "FileChooser.detailsViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"),
            "FileChooser.homeFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"),
            "FileChooser.listViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"),
            "FileChooser.newFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"),
            "FileChooser.upFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"),

            "FileChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
            "FileChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
            "FileChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),
	    "FileChooser.usesSingleFilePane", Boolean.TRUE,
	    "FileChooser.ancestorInputMap", 
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelSelection",
		     "F2", "editFileName",
		     "F5", "refresh",
		     "BACK_SPACE", "Go Up",
		     "ENTER", "approveSelection"
		 }),


            // ToolTip
            "ToolTip.font", systemTextValue,
            "ToolTip.border", toolTipBorder,
            "ToolTip.borderInactive", toolTipBorderInactive,
            "ToolTip.backgroundInactive", control,
            "ToolTip.foregroundInactive", controlDarkShadow,
            "ToolTip.hideAccelerator", Boolean.FALSE,
 
            // Slider Defaults
            "Slider.border", null,
            "Slider.foreground", primaryControlShadow,
            "Slider.focus", focusColor,
	    "Slider.focusInsets", zeroInsets,
            "Slider.trackWidth", new Integer( 7 ),
            "Slider.majorTickLength", new Integer( 6 ),
            "Slider.horizontalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getHorizontalSliderThumbIcon"),
            "Slider.verticalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getVerticalSliderThumbIcon"),
	    "Slider.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "positiveUnitIncrement",
		    "KP_RIGHT", "positiveUnitIncrement",
		        "DOWN", "negativeUnitIncrement",
		     "KP_DOWN", "negativeUnitIncrement",
		   "PAGE_DOWN", "negativeBlockIncrement",
	      "ctrl PAGE_DOWN", "negativeBlockIncrement",
		        "LEFT", "negativeUnitIncrement",
		     "KP_LEFT", "negativeUnitIncrement",
		          "UP", "positiveUnitIncrement",
		       "KP_UP", "positiveUnitIncrement",
		     "PAGE_UP", "positiveBlockIncrement",
                "ctrl PAGE_UP", "positiveBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

            // Progress Bar
	    "ProgressBar.font", controlTextValue,
            "ProgressBar.foreground", primaryControlShadow, 
	    "ProgressBar.selectionBackground", primaryControlDarkShadow, 
	    "ProgressBar.border", progressBarBorder,
            "ProgressBar.cellSpacing", zero,
            "ProgressBar.cellLength", new Integer(1),

            // Combo Box
            "ComboBox.background", control,
            "ComboBox.foreground", controlTextColor,
            "ComboBox.selectionBackground", primaryControlShadow,
            "ComboBox.selectionForeground", controlTextColor,
            "ComboBox.font", controlTextValue,
	    "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "hidePopup",
		    "PAGE_UP", "pageUpPassThrough",
		  "PAGE_DOWN", "pageDownPassThrough",
		       "HOME", "homePassThrough",
		        "END", "endPassThrough",
		       "DOWN", "selectNext",
		    "KP_DOWN", "selectNext",
		   "alt DOWN", "togglePopup",
		"alt KP_DOWN", "togglePopup",
		     "alt UP", "togglePopup",
		  "alt KP_UP", "togglePopup",
		      "SPACE", "spacePopup",
		     "ENTER", "enterPressed",
		         "UP", "selectPrevious",
		      "KP_UP", "selectPrevious"
	      }),

            // Internal Frame Defaults
            "InternalFrame.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameDefaultMenuIcon"),
            "InternalFrame.border", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$InternalFrameBorder"),
            "InternalFrame.optionDialogBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$OptionDialogBorder"),
            "InternalFrame.paletteBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PaletteBorder"),
	    "InternalFrame.paletteTitleHeight", new Integer(11),
	    "InternalFrame.paletteCloseIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory$PaletteCloseIcon"),
            "InternalFrame.closeIcon", 
                  new SwingLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameCloseIcon",
				     internalFrameIconArgs),
            "InternalFrame.maximizeIcon", 
                  new SwingLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameMaximizeIcon",
				     internalFrameIconArgs),
            "InternalFrame.iconifyIcon", 
                  new SwingLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameMinimizeIcon",
				     internalFrameIconArgs),
            "InternalFrame.minimizeIcon", 
                  new SwingLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameAltMaximizeIcon",
				     internalFrameIconArgs),
            "InternalFrame.titleFont",  windowTitleValue,
	    "InternalFrame.windowBindings", null,
	    // Internal Frame Auditory Cue Mappings
            "InternalFrame.closeSound", "sounds/FrameClose.wav",
            "InternalFrame.maximizeSound", "sounds/FrameMaximize.wav",
            "InternalFrame.minimizeSound", "sounds/FrameMinimize.wav",
            "InternalFrame.restoreDownSound", "sounds/FrameRestoreDown.wav",
            "InternalFrame.restoreUpSound", "sounds/FrameRestoreUp.wav",

            // Desktop Icon
            "DesktopIcon.border", desktopIconBorder,
            "DesktopIcon.font", controlTextValue,
            "DesktopIcon.foreground", controlTextColor,
            "DesktopIcon.background", control,
            "DesktopIcon.width", new Integer(160),

	    "Desktop.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		 "ctrl F5", "restore", 
		 "ctrl F4", "close",
		 "ctrl F7", "move", 
		 "ctrl F8", "resize",
		   "RIGHT", "right",
		"KP_RIGHT", "right",
             "shift RIGHT", "shrinkRight",
          "shift KP_RIGHT", "shrinkRight",
		    "LEFT", "left",
		 "KP_LEFT", "left",
              "shift LEFT", "shrinkLeft",
           "shift KP_LEFT", "shrinkLeft",
		      "UP", "up",
		   "KP_UP", "up",
                "shift UP", "shrinkUp",
             "shift KP_UP", "shrinkUp",
		    "DOWN", "down",
		 "KP_DOWN", "down",
              "shift DOWN", "shrinkDown",
           "shift KP_DOWN", "shrinkDown",
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

            // Titled Border
            "TitledBorder.font", controlTextValue,
            "TitledBorder.titleColor", systemTextColor,
            "TitledBorder.border", titledBorderBorder,

            // Label
            "Label.font", controlTextValue,
            "Label.foreground", systemTextColor,
            "Label.disabledForeground", getInactiveSystemTextColor(),

            // List
            "List.font", controlTextValue,
            "List.focusCellHighlightBorder", focusCellHighlightBorder,
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
                    "ctrl shift UP", "selectPreviousRowExtendSelection",
                 "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                          "ctrl UP", "selectPreviousRowChangeLead",
                       "ctrl KP_UP", "selectPreviousRowChangeLead",
		             "DOWN", "selectNextRow",
		          "KP_DOWN", "selectNextRow",
		       "shift DOWN", "selectNextRowExtendSelection",
		    "shift KP_DOWN", "selectNextRowExtendSelection",
                  "ctrl shift DOWN", "selectNextRowExtendSelection",
               "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                        "ctrl DOWN", "selectNextRowChangeLead",
                     "ctrl KP_DOWN", "selectNextRowChangeLead",
		             "LEFT", "selectPreviousColumn",
		          "KP_LEFT", "selectPreviousColumn",
		       "shift LEFT", "selectPreviousColumnExtendSelection",
		    "shift KP_LEFT", "selectPreviousColumnExtendSelection",
                  "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
               "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl LEFT", "selectPreviousColumnChangeLead",
                     "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
		            "RIGHT", "selectNextColumn",
		         "KP_RIGHT", "selectNextColumn",
		      "shift RIGHT", "selectNextColumnExtendSelection",
		   "shift KP_RIGHT", "selectNextColumnExtendSelection",
                 "ctrl shift RIGHT", "selectNextColumnExtendSelection",
              "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                       "ctrl RIGHT", "selectNextColumnChangeLead",
                    "ctrl KP_RIGHT", "selectNextColumnChangeLead",
		             "HOME", "selectFirstRow",
		       "shift HOME", "selectFirstRowExtendSelection",
                  "ctrl shift HOME", "selectFirstRowExtendSelection",
                        "ctrl HOME", "selectFirstRowChangeLead",
		              "END", "selectLastRow",
		        "shift END", "selectLastRowExtendSelection",
                   "ctrl shift END", "selectLastRowExtendSelection",
                         "ctrl END", "selectLastRowChangeLead",
		          "PAGE_UP", "scrollUp",
		    "shift PAGE_UP", "scrollUpExtendSelection",
               "ctrl shift PAGE_UP", "scrollUpExtendSelection",
                     "ctrl PAGE_UP", "scrollUpChangeLead",
		        "PAGE_DOWN", "scrollDown",
		  "shift PAGE_DOWN", "scrollDownExtendSelection",
             "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
                   "ctrl PAGE_DOWN", "scrollDownChangeLead",
		           "ctrl A", "selectAll",
		       "ctrl SLASH", "selectAll",
		  "ctrl BACK_SLASH", "clearSelection",
                            "SPACE", "addToSelection",
                       "ctrl SPACE", "toggleAndAnchor",
                      "shift SPACE", "extendTo",
                 "ctrl shift SPACE", "moveSelectionTo"
		 }),

            // ScrollBar
            "ScrollBar.background", control,
            "ScrollBar.highlight", controlHighlight,
            "ScrollBar.shadow", controlShadow,
            "ScrollBar.darkShadow", controlDarkShadow,
            "ScrollBar.thumb", primaryControlShadow,
            "ScrollBar.thumbShadow", primaryControlDarkShadow,
            "ScrollBar.thumbHighlight", primaryControl,
            "ScrollBar.width", new Integer( 17 ),
	    "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE,
	    "ScrollBar.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "positiveUnitIncrement",
		    "KP_RIGHT", "positiveUnitIncrement",
		        "DOWN", "positiveUnitIncrement",
		     "KP_DOWN", "positiveUnitIncrement",
		   "PAGE_DOWN", "positiveBlockIncrement",
		        "LEFT", "negativeUnitIncrement",
		     "KP_LEFT", "negativeUnitIncrement",
		          "UP", "negativeUnitIncrement",
		       "KP_UP", "negativeUnitIncrement",
		     "PAGE_UP", "negativeBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

	    // ScrollPane
	    "ScrollPane.border", scrollPaneBorder,
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

            // Tabbed Pane
            "TabbedPane.font", controlTextValue,
            "TabbedPane.tabAreaBackground", control,
            "TabbedPane.background", controlShadow,
            "TabbedPane.light", control,
            "TabbedPane.focus", primaryControlDarkShadow,
            "TabbedPane.selected", control,
            "TabbedPane.selectHighlight", controlHighlight,
            "TabbedPane.tabAreaInsets", tabbedPaneTabAreaInsets,
            "TabbedPane.tabInsets", tabbedPaneTabInsets,
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
            
            // Table
	    "Table.font", userTextValue,
            "Table.focusCellHighlightBorder", focusCellHighlightBorder,
            "Table.scrollPaneBorder", scrollPaneBorder,
      	    "Table.gridColor", controlShadow,  // grid line color
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
                          "shift RIGHT", "selectNextColumnExtendSelection",
                       "shift KP_RIGHT", "selectNextColumnExtendSelection",
                     "ctrl shift RIGHT", "selectNextColumnExtendSelection",
                  "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                           "ctrl RIGHT", "selectNextColumnChangeLead",
                        "ctrl KP_RIGHT", "selectNextColumnChangeLead",
		                 "LEFT", "selectPreviousColumn",
		              "KP_LEFT", "selectPreviousColumn",
                           "shift LEFT", "selectPreviousColumnExtendSelection",
                        "shift KP_LEFT", "selectPreviousColumnExtendSelection",
                      "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
                   "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                            "ctrl LEFT", "selectPreviousColumnChangeLead",
                         "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
		                 "DOWN", "selectNextRow",
		              "KP_DOWN", "selectNextRow",
                           "shift DOWN", "selectNextRowExtendSelection",
                        "shift KP_DOWN", "selectNextRowExtendSelection",
                      "ctrl shift DOWN", "selectNextRowExtendSelection",
                   "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                            "ctrl DOWN", "selectNextRowChangeLead",
                         "ctrl KP_DOWN", "selectNextRowChangeLead",
		                   "UP", "selectPreviousRow",
		                "KP_UP", "selectPreviousRow",
                             "shift UP", "selectPreviousRowExtendSelection",
                          "shift KP_UP", "selectPreviousRowExtendSelection",
                        "ctrl shift UP", "selectPreviousRowExtendSelection",
                     "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                              "ctrl UP", "selectPreviousRowChangeLead",
                           "ctrl KP_UP", "selectPreviousRowChangeLead",
                                 "HOME", "selectFirstColumn",
                           "shift HOME", "selectFirstColumnExtendSelection",
                      "ctrl shift HOME", "selectFirstRowExtendSelection",
                            "ctrl HOME", "selectFirstRow",
                                  "END", "selectLastColumn",
                            "shift END", "selectLastColumnExtendSelection",
                       "ctrl shift END", "selectLastRowExtendSelection",
                             "ctrl END", "selectLastRow",
		              "PAGE_UP", "scrollUpChangeSelection",
                        "shift PAGE_UP", "scrollUpExtendSelection",
                   "ctrl shift PAGE_UP", "scrollLeftExtendSelection",
                         "ctrl PAGE_UP", "scrollLeftChangeSelection",
		            "PAGE_DOWN", "scrollDownChangeSelection",
		      "shift PAGE_DOWN", "scrollDownExtendSelection",
                 "ctrl shift PAGE_DOWN", "scrollRightExtendSelection",
		       "ctrl PAGE_DOWN", "scrollRightChangeSelection",
		                  "TAB", "selectNextColumnCell",
		            "shift TAB", "selectPreviousColumnCell",
		                "ENTER", "selectNextRowCell",
		          "shift ENTER", "selectPreviousRowCell",
		               "ctrl A", "selectAll",
                           "ctrl SLASH", "selectAll",
                      "ctrl BACK_SLASH", "clearSelection",
		               "ESCAPE", "cancel",
		                   "F2", "startEditing",
                                "SPACE", "addToSelection",
                           "ctrl SPACE", "toggleAndAnchor",
                          "shift SPACE", "extendTo",
                     "ctrl shift SPACE", "moveSelectionTo"
		 }),

	    "TableHeader.font", userTextValue,
	    "TableHeader.cellBorder", new SwingLazyValue(
					  "javax.swing.plaf.metal.MetalBorders$TableHeaderBorder"),

            // MenuBar
            "MenuBar.border", menuBarBorder,
            "MenuBar.font", menuTextValue,
	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },

            // Menu
            "Menu.border", menuItemBorder,
            "Menu.borderPainted", Boolean.TRUE,
	    "Menu.menuPopupOffsetX", zero,
	    "Menu.menuPopupOffsetY", zero,
	    "Menu.submenuPopupOffsetX", new Integer(-4),
	    "Menu.submenuPopupOffsetY", new Integer(-3),
            "Menu.font", menuTextValue,
            "Menu.selectionForeground", menuSelectedForeground,
            "Menu.selectionBackground", menuSelectedBackground,
            "Menu.disabledForeground", menuDisabledForeground,
            "Menu.acceleratorFont", subTextValue,
            "Menu.acceleratorForeground", acceleratorForeground,
            "Menu.acceleratorSelectionForeground", acceleratorSelectedForeground,
            "Menu.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"),
            "Menu.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuArrowIcon"),

            // Menu Item
            "MenuItem.border", menuItemBorder,
            "MenuItem.borderPainted", Boolean.TRUE,
            "MenuItem.font", menuTextValue,
            "MenuItem.selectionForeground", menuSelectedForeground,
            "MenuItem.selectionBackground", menuSelectedBackground,
            "MenuItem.disabledForeground", menuDisabledForeground,
            "MenuItem.acceleratorFont", subTextValue,
            "MenuItem.acceleratorForeground", acceleratorForeground,
            "MenuItem.acceleratorSelectionForeground", acceleratorSelectedForeground,
	    "MenuItem.acceleratorDelimiter", menuItemAcceleratorDelimiter,
            "MenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"),
            "MenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"),
	         // Menu Item Auditory Cue Mapping
	    "MenuItem.commandSound", "sounds/MenuItemCommand.wav",

	    // OptionPane.
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },
	    // Option Pane Auditory Cue Mappings
            "OptionPane.informationSound", "sounds/OptionPaneInformation.wav",
            "OptionPane.warningSound", "sounds/OptionPaneWarning.wav",
            "OptionPane.errorSound", "sounds/OptionPaneError.wav",
            "OptionPane.questionSound", "sounds/OptionPaneQuestion.wav",

            // Option Pane Special Dialog Colors, used when MetalRootPaneUI
            // is providing window manipulation widgets.
            "OptionPane.errorDialog.border.background",
                        new ColorUIResource(153, 51, 51),
            "OptionPane.errorDialog.titlePane.foreground",
                        new ColorUIResource(51, 0, 0),
            "OptionPane.errorDialog.titlePane.background",
                        new ColorUIResource(255, 153, 153),
            "OptionPane.errorDialog.titlePane.shadow",
                        new ColorUIResource(204, 102, 102),
            "OptionPane.questionDialog.border.background",
                        new ColorUIResource(51, 102, 51),
            "OptionPane.questionDialog.titlePane.foreground",
                        new ColorUIResource(0, 51, 0),
            "OptionPane.questionDialog.titlePane.background",
                        new ColorUIResource(153, 204, 153),
            "OptionPane.questionDialog.titlePane.shadow",
                        new ColorUIResource(102, 153, 102),
            "OptionPane.warningDialog.border.background",
                        new ColorUIResource(153, 102, 51),
            "OptionPane.warningDialog.titlePane.foreground",
                        new ColorUIResource(102, 51, 0),
            "OptionPane.warningDialog.titlePane.background",
                        new ColorUIResource(255, 204, 153),
            "OptionPane.warningDialog.titlePane.shadow",
                        new ColorUIResource(204, 153, 102),
            // OptionPane fonts are defined below
           
            // Separator
            "Separator.background", getSeparatorBackground(),
            "Separator.foreground", getSeparatorForeground(),

            // Popup Menu
            "PopupMenu.border", popupMenuBorder,          
	         // Popup Menu Auditory Cue Mappings
            "PopupMenu.popupSound", "sounds/PopupMenuPopup.wav",
            "PopupMenu.font", menuTextValue,

            // CB & RB Menu Item
            "CheckBoxMenuItem.border", menuItemBorder,
            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
            "CheckBoxMenuItem.font", menuTextValue,
            "CheckBoxMenuItem.selectionForeground", menuSelectedForeground,
            "CheckBoxMenuItem.selectionBackground", menuSelectedBackground,
            "CheckBoxMenuItem.disabledForeground", menuDisabledForeground,
            "CheckBoxMenuItem.acceleratorFont", subTextValue,
            "CheckBoxMenuItem.acceleratorForeground", acceleratorForeground,
            "CheckBoxMenuItem.acceleratorSelectionForeground", acceleratorSelectedForeground,
            "CheckBoxMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxMenuItemIcon"),
            "CheckBoxMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"),
	    "CheckBoxMenuItem.commandSound", "sounds/MenuItemCommand.wav",

            "RadioButtonMenuItem.border", menuItemBorder,
            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
            "RadioButtonMenuItem.font", menuTextValue,
            "RadioButtonMenuItem.selectionForeground", menuSelectedForeground,
            "RadioButtonMenuItem.selectionBackground", menuSelectedBackground,
            "RadioButtonMenuItem.disabledForeground", menuDisabledForeground,
            "RadioButtonMenuItem.acceleratorFont", subTextValue,
            "RadioButtonMenuItem.acceleratorForeground", acceleratorForeground,
            "RadioButtonMenuItem.acceleratorSelectionForeground", acceleratorSelectedForeground,
            "RadioButtonMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonMenuItemIcon"),
            "RadioButtonMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"),
	    "RadioButtonMenuItem.commandSound", "sounds/MenuItemCommand.wav",

            "Spinner.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                               "UP", "increment",
                            "KP_UP", "increment",
                             "DOWN", "decrement",
                          "KP_DOWN", "decrement",
               }),
	    "Spinner.arrowButtonInsets", zeroInsets,
	    "Spinner.border", textFieldBorder,
	    "Spinner.arrowButtonBorder", buttonBorder,
            "Spinner.font", controlTextValue,

	    // SplitPane

	    "SplitPane.dividerSize", new Integer(10),
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
            "SplitPane.centerOneTouchButtons", Boolean.FALSE,
            "SplitPane.dividerFocusColor", primaryControl,

            // Tree
            // Tree.font was mapped to system font pre 1.4.1
            "Tree.font", userTextValue,
            "Tree.textBackground", getWindowBackground(),
            "Tree.selectionBorderColor", focusColor,
            "Tree.openIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"),
            "Tree.closedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"),
            "Tree.leafIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"),
            "Tree.expandedIcon", new SwingLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getTreeControlIcon",
				     new Object[] {Boolean.valueOf(MetalIconFactory.DARK)}),
            "Tree.collapsedIcon", new SwingLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getTreeControlIcon",
				     new Object[] {Boolean.valueOf( MetalIconFactory.LIGHT )}),

            "Tree.line", primaryControl, // horiz lines
            "Tree.hash", primaryControl,  // legs
	    "Tree.rowHeight", zero,
	    "Tree.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                                    "ADD", "expand",
                               "SUBTRACT", "collapse",
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
                          "ctrl shift UP", "selectPreviousExtendSelection",
                       "ctrl shift KP_UP", "selectPreviousExtendSelection",
                                "ctrl UP", "selectPreviousChangeLead",
                             "ctrl KP_UP", "selectPreviousChangeLead",
		                   "DOWN", "selectNext",
		                "KP_DOWN", "selectNext",
		             "shift DOWN", "selectNextExtendSelection",
		          "shift KP_DOWN", "selectNextExtendSelection",
                        "ctrl shift DOWN", "selectNextExtendSelection",
                     "ctrl shift KP_DOWN", "selectNextExtendSelection",
                              "ctrl DOWN", "selectNextChangeLead",
                           "ctrl KP_DOWN", "selectNextChangeLead",
		                  "RIGHT", "selectChild",
		               "KP_RIGHT", "selectChild",
		                   "LEFT", "selectParent",
		                "KP_LEFT", "selectParent",
		                "PAGE_UP", "scrollUpChangeSelection",
		          "shift PAGE_UP", "scrollUpExtendSelection",
                     "ctrl shift PAGE_UP", "scrollUpExtendSelection",
                           "ctrl PAGE_UP", "scrollUpChangeLead",
		              "PAGE_DOWN", "scrollDownChangeSelection",
		        "shift PAGE_DOWN", "scrollDownExtendSelection",
                   "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
                         "ctrl PAGE_DOWN", "scrollDownChangeLead",
		                   "HOME", "selectFirst",
		             "shift HOME", "selectFirstExtendSelection",
                        "ctrl shift HOME", "selectFirstExtendSelection",
                              "ctrl HOME", "selectFirstChangeLead",
		                    "END", "selectLast",
		              "shift END", "selectLastExtendSelection",
                         "ctrl shift END", "selectLastExtendSelection",
                               "ctrl END", "selectLastChangeLead",
		                     "F2", "startEditing",
		                 "ctrl A", "selectAll",
		             "ctrl SLASH", "selectAll",
		        "ctrl BACK_SLASH", "clearSelection",
		              "ctrl LEFT", "scrollLeft",
		           "ctrl KP_LEFT", "scrollLeft",
		             "ctrl RIGHT", "scrollRight",
		          "ctrl KP_RIGHT", "scrollRight",
                                  "SPACE", "addToSelection",
                             "ctrl SPACE", "toggleAndAnchor",
                            "shift SPACE", "extendTo",
                       "ctrl shift SPACE", "moveSelectionTo"
		 }),
	    "Tree.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancel"
		 }),

            // ToolBar
            "ToolBar.border", toolBarBorder,
            "ToolBar.background", menuBackground,
            "ToolBar.foreground", getMenuForeground(),
            "ToolBar.font", menuTextValue,
            "ToolBar.dockingBackground", menuBackground,
            "ToolBar.floatingBackground", menuBackground,
            "ToolBar.dockingForeground", primaryControlDarkShadow, 
            "ToolBar.floatingForeground", primaryControl,
            "ToolBar.rolloverBorder", new MetalLazyValue(
                         "javax.swing.plaf.metal.MetalBorders",
                         "getToolBarRolloverBorder"),
            "ToolBar.nonrolloverBorder", new MetalLazyValue(
                         "javax.swing.plaf.metal.MetalBorders",
                         "getToolBarNonrolloverBorder"),
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

            // RootPane
            "RootPane.frameBorder", new MetalLazyValue(
                      "javax.swing.plaf.metal.MetalBorders$FrameBorder"),
            "RootPane.plainDialogBorder", dialogBorder,
            "RootPane.informationDialogBorder", dialogBorder,
            "RootPane.errorDialogBorder", new MetalLazyValue(
                      "javax.swing.plaf.metal.MetalBorders$ErrorDialogBorder"),
            "RootPane.colorChooserDialogBorder", questionDialogBorder,
            "RootPane.fileChooserDialogBorder", questionDialogBorder,
            "RootPane.questionDialogBorder", questionDialogBorder,
            "RootPane.warningDialogBorder", new MetalLazyValue(
                    "javax.swing.plaf.metal.MetalBorders$WarningDialogBorder"),
	    // These bindings are only enabled when there is a default
	    // button set on the rootpane.
	    "RootPane.defaultButtonWindowKeyBindings", new Object[] {
		             "ENTER", "press",
		    "released ENTER", "release",
		        "ctrl ENTER", "press",
	       "ctrl released ENTER", "release"
	      },
        };

        table.putDefaults(defaults);

        if (isWindows() && useSystemFonts() && theme.isSystemTheme()) {
            Toolkit kit = Toolkit.getDefaultToolkit();
            Object messageFont = new MetalFontDesktopProperty(
                              "win.messagebox.font.height", kit, MetalTheme.
                              CONTROL_TEXT_FONT);

            defaults = new Object[] {
                "OptionPane.messageFont", messageFont,
                "OptionPane.buttonFont", messageFont,
            };
            table.putDefaults(defaults);
        }
    }

    protected void createDefaultTheme() {
        getCurrentTheme();
    }

    public UIDefaults getDefaults() {
        // PENDING: move this to initialize when API changes are allowed
        METAL_LOOK_AND_FEEL_INITED = true;

        createDefaultTheme();
        UIDefaults table = super.getDefaults();
        currentTheme.addCustomEntriesToTable(table);
        currentTheme.install();
        return table;
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

    /**
     * Set the theme to be used by <code>MetalLookAndFeel</code>.
     * This may not be null.
     * <br>
     * After setting the theme, you need to re-install the
     * <code>MetalLookAndFeel</code>, as well as update the UI
     * of any previously created widgets. Otherwise the results are undefined.
     * These lines of code show you how to accomplish this:
     * <pre>
     *   // turn off bold fonts
     *   MetalLookAndFeel.setCurrentTheme(theme);
     *
     *   // re-install the Metal Look and Feel
     *   UIManager.setLookAndFeel(new MetalLookAndFeel());
     *
     *   // only needed to update existing widgets
     *   SwingUtilities.updateComponentTreeUI(rootComponent);
     * </pre>
     *
     * @param theme the theme to be used, non-null
     * @throws NullPointerException if given a null parameter
     * @see #getCurrentTheme
     */
    public static void setCurrentTheme(MetalTheme theme) {
        // NOTE: because you need to recreate the look and feel after
        // this step, we don't bother blowing away any potential windows
        // values.
        if (theme == null) {
            throw new NullPointerException("Can't have null theme");
        }
        currentTheme = theme;
	cachedAppContext = AppContext.getAppContext();
	cachedAppContext.put( "currentMetalTheme", theme );
    }

    /**
     * Return the theme currently being used by <code>MetalLookAndFeel</code>.
     * This will always be non-null, as it will set the current theme if one
     * hasn't been set already.
     *
     * @return the current theme
     * @see #setCurrentTheme
     * @since 1.5
     */
    public static MetalTheme getCurrentTheme() {
        AppContext context = AppContext.getAppContext();

	if ( cachedAppContext != context ) {
	    currentTheme = (MetalTheme)context.get( "currentMetalTheme" );
            if (currentTheme == null) {
                // This will happen in two cases:
                // . When MetalLookAndFeel is first being initialized.
                // . When a new AppContext has been created that hasn't
                //   triggered UIManager to load a LAF. Rather than invoke
                //   a method on the UIManager, which would trigger the loading
                //   of a potentially different LAF, we directly set the
                //   Theme here.
                if (useHighContrastTheme()) {
                    currentTheme = new MetalHighContrastTheme();
                }
                else {
                    // Create the default theme. We prefer Ocean, but will
                    // use DefaultMetalTheme if told to.
                    String theme = (String)AccessController.doPrivileged(
                                   new GetPropertyAction("swing.metalTheme"));
                    if ("steel".equals(theme)) {
                        currentTheme = new DefaultMetalTheme();
                    }
                    else {
                        currentTheme = new OceanTheme();
                    }
                }
                setCurrentTheme(currentTheme);
            }
	    cachedAppContext = context;
	}

	return currentTheme;
    }

    /**
     * Returns an <code>Icon</code> with a disabled appearance.
     * This method is used to generate a disabled <code>Icon</code> when
     * one has not been specified.  For example, if you create a
     * <code>JButton</code> and only specify an <code>Icon</code> via
     * <code>setIcon</code> this method will be called to generate the
     * disabled <code>Icon</code>. If null is passed as <code>icon</code>
     * this method returns null. 
     * <p>
     * Some look and feels might not render the disabled Icon, in which
     * case they will ignore this.
     *
     * @param component JComponent that will display the Icon, may be null
     * @param icon Icon to generate disable icon from.
     * @return Disabled icon, or null if a suitable Icon can not be
     *         generated.
     * @since 1.5
     */
    public Icon getDisabledIcon(JComponent component, Icon icon) {
        if ((icon instanceof ImageIcon) && MetalLookAndFeel.usingOcean()) {
            return MetalUtils.getOceanDisabledButtonIcon(
                                  ((ImageIcon)icon).getImage());
        }
        return super.getDisabledIcon(component, icon);
    }

    /**
     * Returns an <code>Icon</code> for use by disabled
     * components that are also selected. This method is used to generate an
     * <code>Icon</code> for components that are in both the disabled and
     * selected states but do not have a specific <code>Icon</code> for this
     * state.  For example, if you create a <code>JButton</code> and only
     * specify an <code>Icon</code> via <code>setIcon</code> this method
     * will be called to generate the disabled and selected
     * <code>Icon</code>. If null is passed as <code>icon</code> this method
     * returns null. 
     * <p>
     * Some look and feels might not render the disabled and selected Icon,
     * in which case they will ignore this.
     *
     * @param component JComponent that will display the Icon, may be null
     * @param icon Icon to generate disabled and selected icon from.
     * @return Disabled and Selected icon, or null if a suitable Icon can not
     *         be generated.
     * @since 1.5
     */
    public Icon getDisabledSelectedIcon(JComponent component, Icon icon) {
        if ((icon instanceof ImageIcon) && MetalLookAndFeel.usingOcean()) {
            return MetalUtils.getOceanDisabledButtonIcon(
                                  ((ImageIcon)icon).getImage());
        }
        return super.getDisabledSelectedIcon(component, icon);
    }

    public static FontUIResource getControlTextFont() { return getCurrentTheme().getControlTextFont();}
    public static FontUIResource getSystemTextFont() { return getCurrentTheme().getSystemTextFont();}
    public static FontUIResource getUserTextFont() { return getCurrentTheme().getUserTextFont();}
    public static FontUIResource getMenuTextFont() { return getCurrentTheme().getMenuTextFont();}
    public static FontUIResource getWindowTitleFont() { return getCurrentTheme().getWindowTitleFont();}
    public static FontUIResource getSubTextFont() { return getCurrentTheme().getSubTextFont();}

    public static ColorUIResource getDesktopColor() { return getCurrentTheme().getDesktopColor(); }
    public static ColorUIResource getFocusColor() { return getCurrentTheme().getFocusColor(); }

    public static ColorUIResource getWhite() { return getCurrentTheme().getWhite(); }
    public static ColorUIResource getBlack() { return getCurrentTheme().getBlack(); }
    public static ColorUIResource getControl() { return getCurrentTheme().getControl(); }
    public static ColorUIResource getControlShadow() { return getCurrentTheme().getControlShadow(); }
    public static ColorUIResource getControlDarkShadow() { return getCurrentTheme().getControlDarkShadow(); }
    public static ColorUIResource getControlInfo() { return getCurrentTheme().getControlInfo(); } 
    public static ColorUIResource getControlHighlight() { return getCurrentTheme().getControlHighlight(); }
    public static ColorUIResource getControlDisabled() { return getCurrentTheme().getControlDisabled(); }

    public static ColorUIResource getPrimaryControl() { return getCurrentTheme().getPrimaryControl(); }  
    public static ColorUIResource getPrimaryControlShadow() { return getCurrentTheme().getPrimaryControlShadow(); }  
    public static ColorUIResource getPrimaryControlDarkShadow() { return getCurrentTheme().getPrimaryControlDarkShadow(); }  
    public static ColorUIResource getPrimaryControlInfo() { return getCurrentTheme().getPrimaryControlInfo(); } 
    public static ColorUIResource getPrimaryControlHighlight() { return getCurrentTheme().getPrimaryControlHighlight(); }  

    public static ColorUIResource getSystemTextColor() { return getCurrentTheme().getSystemTextColor(); }
    public static ColorUIResource getControlTextColor() { return getCurrentTheme().getControlTextColor(); }  
    public static ColorUIResource getInactiveControlTextColor() { return getCurrentTheme().getInactiveControlTextColor(); }  
    public static ColorUIResource getInactiveSystemTextColor() { return getCurrentTheme().getInactiveSystemTextColor(); }
    public static ColorUIResource getUserTextColor() { return getCurrentTheme().getUserTextColor(); }
    public static ColorUIResource getTextHighlightColor() { return getCurrentTheme().getTextHighlightColor(); }
    public static ColorUIResource getHighlightedTextColor() { return getCurrentTheme().getHighlightedTextColor(); }

    public static ColorUIResource getWindowBackground() { return getCurrentTheme().getWindowBackground(); }
    public static ColorUIResource getWindowTitleBackground() { return getCurrentTheme().getWindowTitleBackground(); }
    public static ColorUIResource getWindowTitleForeground() { return getCurrentTheme().getWindowTitleForeground(); }
    public static ColorUIResource getWindowTitleInactiveBackground() { return getCurrentTheme().getWindowTitleInactiveBackground(); }
    public static ColorUIResource getWindowTitleInactiveForeground() { return getCurrentTheme().getWindowTitleInactiveForeground(); }

    public static ColorUIResource getMenuBackground() { return getCurrentTheme().getMenuBackground(); }
    public static ColorUIResource getMenuForeground() { return getCurrentTheme().getMenuForeground(); }
    public static ColorUIResource getMenuSelectedBackground() { return getCurrentTheme().getMenuSelectedBackground(); }
    public static ColorUIResource getMenuSelectedForeground() { return getCurrentTheme().getMenuSelectedForeground(); }
    public static ColorUIResource getMenuDisabledForeground() { return getCurrentTheme().getMenuDisabledForeground(); }
    public static ColorUIResource getSeparatorBackground() { return getCurrentTheme().getSeparatorBackground(); }
    public static ColorUIResource getSeparatorForeground() { return getCurrentTheme().getSeparatorForeground(); }
    public static ColorUIResource getAcceleratorForeground() { return getCurrentTheme().getAcceleratorForeground(); }
    public static ColorUIResource getAcceleratorSelectedForeground() { return getCurrentTheme().getAcceleratorSelectedForeground(); }


    /**
     * MetalLazyValue is a slimmed down version of <code>ProxyLaxyValue</code>.
     * The code is duplicate so that it can get at the package private
     * classes in metal.
     */
    private static class MetalLazyValue implements UIDefaults.LazyValue {
        /**
         * Name of the class to create.
         */
        private String className;
        private String methodName;

        MetalLazyValue(String name) {
            this.className = name;
        }

        MetalLazyValue(String name, String methodName) {
            this(name);
            this.methodName = methodName;
        }

        public Object createValue(UIDefaults table) {
            try {
                final Class c = Class.forName(className);

                if (methodName == null) {
                    return c.newInstance();
                }
                Method method = (Method)AccessController.doPrivileged(
                    new PrivilegedAction() {
                    public Object run() {
                        Method[] methods = c.getDeclaredMethods();
                        for (int counter = methods.length - 1; counter >= 0;
                             counter--) {
                            if (methods[counter].getName().equals(methodName)){
                                methods[counter].setAccessible(true);
                                return methods[counter];
                            }
                        }
                        return null;
                    }
                });
                if (method != null) {
                    return method.invoke(null, null);
                }
            } catch (ClassNotFoundException cnfe) {
            } catch (InstantiationException ie) {
            } catch (IllegalAccessException iae) {
            } catch (InvocationTargetException ite) {
            }
            return null;
        }
    }


    /**
     * FontActiveValue redirects to the appropriate metal theme method.
     */
    private static class FontActiveValue implements UIDefaults.ActiveValue {
        private int type;
        private MetalTheme theme;

        FontActiveValue(MetalTheme theme, int type) {
            this.theme = theme;
            this.type = type;
        }

        public Object createValue(UIDefaults table) {
            Object value = null;
            switch (type) {
            case MetalTheme.CONTROL_TEXT_FONT:
                value = theme.getControlTextFont();
                break;
            case MetalTheme.SYSTEM_TEXT_FONT:
                value = theme.getSystemTextFont();
                break;
            case MetalTheme.USER_TEXT_FONT:
                value = theme.getUserTextFont();
                break;
            case MetalTheme.MENU_TEXT_FONT:
                value = theme.getMenuTextFont();
                break;
            case MetalTheme.WINDOW_TITLE_FONT:
                value = theme.getWindowTitleFont();
                break;
            case MetalTheme.SUB_TEXT_FONT:
                value = theme.getSubTextFont();
                break;
            }
            return value;
        }
    }
}
