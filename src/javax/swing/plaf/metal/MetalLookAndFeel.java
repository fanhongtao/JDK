/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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

import java.net.URL;
import java.io.Serializable;


/**
 * Implements The Metal Look and Feel.
 * <p>
 * For the keyboard keys defined for each component in this Look and
 * Feel (L&F), see 
 * <a href="../../doc-files/Key-Metal.html">Component Keystroke Actions for the Metal L&F</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version @(#)MetalLookAndFeel.java	1.119 02/02/06
 * @author Steve Wilson
 */
public class MetalLookAndFeel extends BasicLookAndFeel
{
  
    private static MetalTheme currentTheme;
    private static boolean isOnlyOneContext = true;
    private static sun.awt.AppContext cachedAppContext = sun.awt.AppContext.getAppContext();

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
	//        String basicPackageName = "javax.swing.plaf.basic.";
        String metalPackageName = "javax.swing.plaf.metal.";

        Object[] uiDefaults = {
                   "ButtonUI", metalPackageName + "MetalButtonUI",
                 "CheckBoxUI", metalPackageName + "MetalCheckBoxUI",
              "RadioButtonUI", metalPackageName + "MetalRadioButtonUI",
             "ToggleButtonUI", metalPackageName + "MetalToggleButtonUI",
              "ProgressBarUI", metalPackageName + "MetalProgressBarUI",
                "ScrollBarUI", metalPackageName + "MetalScrollBarUI",
               "ScrollPaneUI", metalPackageName + "MetalScrollPaneUI",
                "SplitPaneUI", metalPackageName + "MetalSplitPaneUI",
                   "SliderUI", metalPackageName + "MetalSliderUI",
                "SeparatorUI", metalPackageName + "MetalSeparatorUI",
       "PopupMenuSeparatorUI", metalPackageName + "MetalPopupMenuSeparatorUI",
               "TabbedPaneUI", metalPackageName + "MetalTabbedPaneUI",
                "TextFieldUI", metalPackageName + "MetalTextFieldUI",
                     "TreeUI", metalPackageName + "MetalTreeUI",
                    "LabelUI", metalPackageName + "MetalLabelUI",
                  "ToolBarUI", metalPackageName + "MetalToolBarUI",
                  "ToolTipUI", metalPackageName + "MetalToolTipUI",
                 "ComboBoxUI", metalPackageName + "MetalComboBoxUI",
            "InternalFrameUI", metalPackageName + "MetalInternalFrameUI",
              "DesktopIconUI", metalPackageName + "MetalDesktopIconUI",
              "FileChooserUI", metalPackageName + "MetalFileChooserUI",
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
        Object[] systemColors = {
                "desktop", getDesktopColor(), /* Color of the desktop background */
          "activeCaption", getWindowTitleBackground(), /* Color for captions (title bars) when they are active. */
      "activeCaptionText", getWindowTitleForeground(), /* Text color for text in captions (title bars). */
    "activeCaptionBorder", getPrimaryControlShadow(), /* Border color for caption (title bar) window borders. */
        "inactiveCaption", getWindowTitleInactiveBackground(), /* Color for captions (title bars) when not active. */
    "inactiveCaptionText", getWindowTitleInactiveForeground(), /* Text color for text in inactive captions (title bars). */
  "inactiveCaptionBorder", getControlShadow(), /* Border color for inactive caption (title bar) window borders. */
                 "window", getWindowBackground(), /* Default color for the interior of windows */
           "windowBorder", getControl(), /* ??? */
             "windowText", getUserTextColor(), /* ??? */
                   "menu", getMenuBackground(), /* Background color for menus */
               "menuText", getMenuForeground(), /* Text color for menus  */
                   "text", getWindowBackground(), /* Text background color */
               "textText", getUserTextColor(), /* Text foreground color */
          "textHighlight", getTextHighlightColor(), /* Text background color when selected */
      "textHighlightText", getHighlightedTextColor(), /* Text color when selected */
       "textInactiveText", getInactiveSystemTextColor(), /* Text color when disabled */
                "control", getControl(), /* Default color for controls (buttons, sliders, etc) */
            "controlText", getControlTextColor(), /* Default color for text in controls */
       "controlHighlight", getControlHighlight(), /* Specular highlight (opposite of the shadow) */
     "controlLtHighlight", getControlHighlight(), /* Highlight color for controls */
          "controlShadow", getControlShadow(), /* Shadow color for controls */
        "controlDkShadow", getControlDarkShadow(), /* Dark shadow color for controls */
              "scrollbar", getControl(), /* Scrollbar background (usually the "track") */
                   "info", getPrimaryControl(), /* ToolTip Background */
               "infoText", getPrimaryControlInfo()  /* ToolTip Text */
        };

        for(int i = 0; i < systemColors.length; i += 2) {
            table.put((String)systemColors[i], systemColors[i + 1]);
        }
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

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults( table );

        loadResourceBundle(table);

	Object textFieldBorder = 
	    new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getTextFieldBorder");

        Object textBorder = 
	    new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getTextBorder");

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
		       "typed \010", DefaultEditorKit.deletePrevCharAction,
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
		       "typed \010", DefaultEditorKit.deletePrevCharAction,
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

        Object scrollPaneBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders$ScrollPaneBorder");
        Object buttonBorder = 
	    	    new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getButtonBorder");
  
        Object toggleButtonBorder =  
	    new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders",
					  "getToggleButtonBorder");

        Object titledBorderBorder = 
	    new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {table.getColor("controlShadow")});

        Object desktopIconBorder = 
	    new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalBorders",
			  "getDesktopIconBorder");

        Object menuBarBorder = 
	    new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalBorders$MenuBarBorder");

        Object popupMenuBorder = 
	    new UIDefaults.ProxyLazyValue(
			 "javax.swing.plaf.metal.MetalBorders$PopupMenuBorder");
        Object menuItemBorder = 
	    new UIDefaults.ProxyLazyValue(
			 "javax.swing.plaf.metal.MetalBorders$MenuItemBorder");

	Object menuItemAcceleratorDelimiter = new String("-");
        Object toolBarBorder = new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders$ToolBarBorder");

	Object progressBarBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {getControlDarkShadow(), new Integer(1)});

        Object toolTipBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {getPrimaryControlDarkShadow()});

        Object focusCellHighlightBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  new Object[] {getFocusColor()});

        Object tabbedPaneTabAreaInsets = new InsetsUIResource(4, 2, 0, 6);

	Object sliderFocusInsets = new InsetsUIResource( 0, 0, 0, 0 );	

	final Object[] internalFrameIconArgs = new Object[1];
	internalFrameIconArgs[0] = new Integer(16);

        //
        // DEFAULTS TABLE
        //

        Object[] defaults = {
            // Text (Note: many are inherited)
            "TextField.border", textFieldBorder,
	    "TextField.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
	    "TextField.caretForeground", getUserTextColor(),

            "PasswordField.border", textBorder,
            "PasswordField.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
	    "PasswordField.caretForeground", getUserTextColor(),

            "TextArea.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
            "TextArea.caretForeground", getUserTextColor(),

	    "TextPane.selectionBackground", table.get("textHighlight"),
	    "TextPane.selectionForeground", table.get("textHighlightText"),
	    "TextPane.background", table.get("window"),
	    "TextPane.foreground", table.get("textText"),
            "TextPane.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
            "TextPane.caretForeground", getUserTextColor(),

	    "EditorPane.selectionBackground", table.get("textHighlight"),
	    "EditorPane.selectionForeground", table.get("textHighlightText"),
	    "EditorPane.background", table.get("window"),
	    "EditorPane.foreground", table.get("textText"),
	    "EditorPane.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
	    "EditorPane.caretForeground", getUserTextColor(),

	    "TextField.focusInputMap", fieldInputMap,
	    "PasswordField.focusInputMap", fieldInputMap,
	    "TextArea.focusInputMap", multilineInputMap,
	    "TextPane.focusInputMap", multilineInputMap,
	    "EditorPane.focusInputMap", multilineInputMap,
            

            // Buttons
            "Button.background", getControl(),
            "Button.foreground", getControlTextColor(),
            "Button.disabledText", getInactiveControlTextColor(),
            "Button.select", getControlShadow(),
            "Button.border", buttonBorder,
            "Button.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "Button.focus", getFocusColor(),
	    "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                         "SPACE", "pressed",
                "released SPACE", "released"
              }),

            "CheckBox.background", getControl(),
            "CheckBox.foreground", getControlTextColor(),
            "CheckBox.disabledText", getInactiveControlTextColor(),
            "Checkbox.select", getControlShadow(),
            "CheckBox.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "CheckBox.focus", getFocusColor(),
            "CheckBox.icon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxIcon"),
	    "CheckBox.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
		 }),

            "RadioButton.background", getControl(),
            "RadioButton.foreground", getControlTextColor(),
            "RadioButton.disabledText", getInactiveControlTextColor(),
            "RadioButton.select", getControlShadow(),
            "RadioButton.icon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonIcon"),
            "RadioButton.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "RadioButton.focus", getFocusColor(),
	    "RadioButton.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                          "SPACE", "pressed",
                 "released SPACE", "released"
	      }),

            "ToggleButton.background", getControl(),
            "ToggleButton.foreground", getControlTextColor(),
            "ToggleButton.select", getControlShadow(),
            "ToggleButton.text", getControl(),
            "ToggleButton.disabledText", getInactiveControlTextColor(),
            "ToggleButton.disabledSelectedText", getControlDarkShadow(),
            "ToggleButton.disabledBackground", getControl(),
            "ToggleButton.disabledSelectedBackground", getControlShadow(),
            "ToggleButton.focus", getFocusColor(),
            "ToggleButton.border", toggleButtonBorder,
            "ToggleButton.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
	    "ToggleButton.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	        }),


            // File View 
            "FileView.directoryIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"),
            "FileView.fileIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"),
            "FileView.computerIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeComputerIcon"),
            "FileView.hardDriveIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeHardDriveIcon"),
            "FileView.floppyDriveIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFloppyDriveIcon"),

            // File Chooser
            "FileChooser.detailsViewIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"),
            "FileChooser.homeFolderIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"),
            "FileChooser.listViewIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"),
            "FileChooser.newFolderIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"),
            "FileChooser.upFolderIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"),

            "FileChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
            "FileChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
            "FileChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),
	    "FileChooser.ancestorInputMap", 
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelSelection"
		 }),


            // ToolTip
            "ToolTip.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getSystemTextFont"),
            "ToolTip.border", toolTipBorder,
            "ToolTip.background", table.get("info"),
            "ToolTip.foreground", table.get("infoText"),

            // Slider Defaults
            "Slider.border", null,
            "Slider.foreground", getPrimaryControlShadow(),
            "Slider.background", getControl(),
            "Slider.focus", getFocusColor(),
	    "Slider.focusInsets", sliderFocusInsets,
            "Slider.trackWidth", new Integer( 7 ),
            "Slider.majorTickLength", new Integer( 6 ),
            "Slider.horizontalThumbIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getHorizontalSliderThumbIcon"),
            "Slider.verticalThumbIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getVerticalSliderThumbIcon"),
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
	    "ProgressBar.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "ProgressBar.foreground", getPrimaryControlShadow(), 
            "ProgressBar.background", getControl(),
            "ProgressBar.foregroundHighlight", getPrimaryControlShadow(), 
            "ProgressBar.backgroundHighlight", getControl(),
	    "ProgressBar.selectionForeground", getControl(),
	    "ProgressBar.selectionBackground", getPrimaryControlDarkShadow(), 
	    "ProgressBar.border", progressBarBorder,
            "ProgressBar.cellSpacing", new Integer(0),
            "ProgressBar.cellLength", new Integer(1),

            // Combo Box
            "ComboBox.background", table.get("control"),
            "ComboBox.foreground", table.get("controlText"),
            "ComboBox.selectionBackground", getPrimaryControlShadow(),
            "ComboBox.selectionForeground", getControlTextColor(),
            "ComboBox.listBackground", getControl(),
            "ComboBox.listForeground", getControlTextColor(),
            "ComboBox.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
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
		         "UP", "selectPrevious",
		      "KP_UP", "selectPrevious"
	      }),

            // Internal Frame Defaults
            "InternalFrame.icon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameDefaultMenuIcon"),
            "InternalFrame.border", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders$InternalFrameBorder"),
            "InternalFrame.optionDialogBorder", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders$OptionDialogBorder"),
            "InternalFrame.paletteBorder", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalBorders$PaletteBorder"),
	    "InternalFrame.paletteTitleHeight", new Integer(11),
	    "InternalFrame.paletteCloseIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory$PaletteCloseIcon"),
            "InternalFrame.closeIcon", 
                  new UIDefaults.ProxyLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameCloseIcon",
				     internalFrameIconArgs),
            "InternalFrame.maximizeIcon", 
                  new UIDefaults.ProxyLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameMaximizeIcon",
				     internalFrameIconArgs),
            "InternalFrame.iconifyIcon", 
                  new UIDefaults.ProxyLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameMinimizeIcon",
				     internalFrameIconArgs),
            "InternalFrame.minimizeIcon", 
                  new UIDefaults.ProxyLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getInternalFrameAltMaximizeIcon",
				     internalFrameIconArgs),
            "InternalFrame.font",  new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getWindowTitleFont"),
	    "InternalFrame.windowBindings", new Object[] {
	      "shift ESCAPE", "showSystemMenu",
		"ctrl SPACE", "showSystemMenu",
	            "ESCAPE", "hideSystemMenu"},

            // Desktop Icon
            "DesktopIcon.border", desktopIconBorder,
            "DesktopIcon.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "DesktopIcon.foreground", getControlTextColor(),
            "DesktopIcon.background", getControl(),

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
       "shift ctrl alt F6", "selectPreviousFrame"
	      }),

            // Titled Border
            "TitledBorder.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "TitledBorder.titleColor", getSystemTextColor(),
            "TitledBorder.border", titledBorderBorder,

            // Label
            "Label.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "Label.background", table.get("control"),
            "Label.foreground", getSystemTextColor(),
            "Label.disabledForeground", getInactiveSystemTextColor(),

            // List
            "List.focusCellHighlightBorder", focusCellHighlightBorder,
	    "List.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		               "UP", "selectPreviousRow",
		            "KP_UP", "selectPreviousRow",
		         "shift UP", "selectPreviousRowExtendSelection",
		      "shift KP_UP", "selectPreviousRowExtendSelection",
		             "DOWN", "selectNextRow",
		          "KP_DOWN", "selectNextRow",
		       "shift DOWN", "selectNextRowExtendSelection",
		    "shift KP_DOWN", "selectNextRowExtendSelection",
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

            // ScrollBar
            "ScrollBar.background", getControl(),
            "ScrollBar.highlight", getControlHighlight(),
            "ScrollBar.shadow", getControlShadow(),
            "ScrollBar.darkShadow", getControlDarkShadow(),
            "ScrollBar.thumb", getPrimaryControlShadow(),
            "ScrollBar.thumbShadow", getPrimaryControlDarkShadow(),
            "ScrollBar.thumbHighlight", getPrimaryControl(),
            "ScrollBar.width", new Integer( 17 ),
	    "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE,
	    "ScrollBar.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "negativeUnitIncrement",
		    "KP_RIGHT", "negativeUnitIncrement",
		        "DOWN", "positiveUnitIncrement",
		     "KP_DOWN", "positiveUnitIncrement",
		   "PAGE_DOWN", "positiveBlockIncrement",
		        "LEFT", "positiveUnitIncrement",
		     "KP_LEFT", "positiveUnitIncrement",
		          "UP", "negativeUnitIncrement",
		       "KP_UP", "negativeUnitIncrement",
		     "PAGE_UP", "negativeBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

	    // ScrollPane
	    "ScrollPane.border", scrollPaneBorder,
	    "ScrollPane.background", table.get("control"/*"window"*/),
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
            "TabbedPane.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getControlTextFont"),
            "TabbedPane.tabAreaBackground", getControl(),
            "TabbedPane.background", getControlShadow(),
            "TabbedPane.foreground", getControlTextColor(),
            "TabbedPane.highlight", getControl(),
            "TabbedPane.lightHighlight", getControlHighlight(),
            "TabbedPane.darkShadow", getControlDarkShadow(),
            "TabbedPane.focus", getPrimaryControlDarkShadow(),
            "TabbedPane.selected", getControl(),
            "TabbedPane.selectHighlight", getControlHighlight(),
            "TabbedPane.tabAreaInsets", tabbedPaneTabAreaInsets,
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
	    "Table.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
            "Table.focusCellHighlightBorder", focusCellHighlightBorder,
            "Table.focusCellBackground", table.get("window"),
            "Table.scrollPaneBorder", scrollPaneBorder,
      	    "Table.gridColor", getControlShadow(),  // grid line color
	    "Table.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
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

	    "TableHeader.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getUserTextFont"),
	    "TableHeader.cellBorder", new UIDefaults.ProxyLazyValue(
					  "javax.swing.plaf.metal.MetalBorders$TableHeaderBorder"),

            // MenuBar
            "MenuBar.border", menuBarBorder,
            "MenuBar.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getMenuTextFont"),
            "MenuBar.foreground", getMenuForeground(),
            "MenuBar.background", getMenuBackground(),
	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },

            // Menu
            "Menu.border", menuItemBorder,
            "Menu.borderPainted", Boolean.TRUE,
            "Menu.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getMenuTextFont"),
            "Menu.foreground", getMenuForeground(),
            "Menu.background", getMenuBackground(),
            "Menu.selectionForeground", getMenuSelectedForeground(),
            "Menu.selectionBackground", getMenuSelectedBackground(),
            "Menu.disabledForeground", getMenuDisabledForeground(),
            "Menu.acceleratorFont", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getSubTextFont"),
            "Menu.acceleratorForeground", getAcceleratorForeground(),
            "Menu.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "Menu.checkIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"),
            "Menu.arrowIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuArrowIcon"),
	    // These window InputMap bindings are used when the Menu is
	    // selected.
	    "Menu.selectedWindowInputMapBindings", new Object[] {
		  "ESCAPE", "cancel",
                    "DOWN", "selectNext",
		 "KP_DOWN", "selectNext",
		      "UP", "selectPrevious",
		   "KP_UP", "selectPrevious",
		    "LEFT", "selectParent",
		 "KP_LEFT", "selectParent",
		   "RIGHT", "selectChild",
		"KP_RIGHT", "selectChild",
		   "ENTER", "return",
		   "SPACE", "return"
	    },

            // Menu Item
            "MenuItem.border", menuItemBorder,
            "MenuItem.borderPainted", Boolean.TRUE,
            "MenuItem.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getMenuTextFont"),
            "MenuItem.foreground", getMenuForeground(),
            "MenuItem.background", getMenuBackground(),
            "MenuItem.selectionForeground", getMenuSelectedForeground(),
            "MenuItem.selectionBackground", getMenuSelectedBackground(),
            "MenuItem.disabledForeground", getMenuDisabledForeground(),
            "MenuItem.acceleratorFont", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getSubTextFont"),
            "MenuItem.acceleratorForeground", getAcceleratorForeground(),
            "MenuItem.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
	    "MenuItem.acceleratorDelimiter", menuItemAcceleratorDelimiter,
            "MenuItem.checkIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"),
            "MenuItem.arrowIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"),

	    // OptionPane.
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },

            // Separator
            "Separator.background", getSeparatorBackground(),
            "Separator.foreground", getSeparatorForeground(),

            // Popup Menu
            "PopupMenu.background", getMenuBackground(),
            "PopupMenu.border", popupMenuBorder,          

            // CB & RB Menu Item
            "CheckBoxMenuItem.border", menuItemBorder,
            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
            "CheckBoxMenuItem.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getMenuTextFont"),
            "CheckBoxMenuItem.foreground", getMenuForeground(),
            "CheckBoxMenuItem.background", getMenuBackground(),
            "CheckBoxMenuItem.selectionForeground", getMenuSelectedForeground(),
            "CheckBoxMenuItem.selectionBackground", getMenuSelectedBackground(),
            "CheckBoxMenuItem.disabledForeground", getMenuDisabledForeground(),
            "CheckBoxMenuItem.acceleratorFont", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getSubTextFont"),
            "CheckBoxMenuItem.acceleratorForeground", getAcceleratorForeground(),
            "CheckBoxMenuItem.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "CheckBoxMenuItem.checkIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxMenuItemIcon"),
            "CheckBoxMenuItem.arrowIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"),

            "RadioButtonMenuItem.border", menuItemBorder,
            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
            "RadioButtonMenuItem.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getMenuTextFont"),
            "RadioButtonMenuItem.foreground", getMenuForeground(),
            "RadioButtonMenuItem.background", getMenuBackground(),
            "RadioButtonMenuItem.selectionForeground", getMenuSelectedForeground(),
            "RadioButtonMenuItem.selectionBackground", getMenuSelectedBackground(),
            "RadioButtonMenuItem.disabledForeground", getMenuDisabledForeground(),
            "RadioButtonMenuItem.acceleratorFont", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getSubTextFont"),
            "RadioButtonMenuItem.acceleratorForeground", getAcceleratorForeground(),
            "RadioButtonMenuItem.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "RadioButtonMenuItem.checkIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonMenuItemIcon"),
            "RadioButtonMenuItem.arrowIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"),

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
		        "F6", "toggleFocus"
		 }),

            // Tree
            "Tree.background", getWindowBackground(),
	    "Tree.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getSystemTextFont"),
            "Tree.textForeground", table.get("textText"),
            "Tree.textBackground", getWindowBackground(),
            "Tree.selectionForeground", table.get("textHighlightText"),
            "Tree.selectionBackground", table.get("textHighlight"),
            "Tree.selectionBorderColor", MetalLookAndFeel.getFocusColor(),
            "Tree.openIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"),
            "Tree.closedIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"),
            "Tree.leafIcon", new UIDefaults.ProxyLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"),
            "Tree.expandedIcon", new UIDefaults.ProxyLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getTreeControlIcon",
				     new Object[] {new Boolean(MetalIconFactory.DARK)}),
            "Tree.collapsedIcon", new UIDefaults.ProxyLazyValue(
				     "javax.swing.plaf.metal.MetalIconFactory", 
				     "getTreeControlIcon",
				     new Object[] {new Boolean( MetalIconFactory.LIGHT )}),

            "Tree.line", getPrimaryControl(), // horiz lines
            "Tree.hash", getPrimaryControl(),  // legs
	    "Tree.rowHeight", new Integer(0),
	    "Tree.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
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
		                  "ENTER", "toggle",
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

            // ToolBar
            "ToolBar.border", toolBarBorder,
            "ToolBar.background", getMenuBackground(),
            "ToolBar.foreground", getMenuForeground(),
            "ToolBar.font", new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.metal.MetalLookAndFeel",
			  "getMenuTextFont"),
            "ToolBar.dockingBackground", getMenuBackground(),
            "ToolBar.floatingBackground", getMenuBackground(),
            "ToolBar.dockingForeground", getPrimaryControlDarkShadow(), 
            "ToolBar.floatingForeground", getPrimaryControl(),
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
    }

    protected void createDefaultTheme() {
        if( getCurrentTheme() == null) {
            setCurrentTheme( new DefaultMetalTheme() );
        }
    }

    public UIDefaults getDefaults() {
        createDefaultTheme();
        UIDefaults table = super.getDefaults();
        currentTheme.addCustomEntriesToTable(table);
        return table;
    }

    public static void setCurrentTheme(MetalTheme theme) {
        if (theme == null) {
            throw new NullPointerException("Can't have null theme");
        }
        currentTheme = theme;
	cachedAppContext = sun.awt.AppContext.getAppContext();
	cachedAppContext.put( "currentMetalTheme", theme );
    }

    private static MetalTheme getCurrentTheme() {
        sun.awt.AppContext context =  sun.awt.AppContext.getAppContext();

	if ( cachedAppContext != context ) {
	    currentTheme = (MetalTheme)context.get( "currentMetalTheme" );
	    cachedAppContext = context;
	}

	return currentTheme;
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

}
