/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.72 02/06/02
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
        ResourceBundle bundle = ResourceBundle.getBundle("com.sun.java.swing.plaf.windows.resources.windows");
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
/*
       Object comboBoxBorder = new WindowsBorders.ComboBoxBorder(
                                           table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"));
*/
       Object comboBoxBorder = new BasicBorders.FieldBorder(
                                                            table.getColor("controlShadow"),
                                                            table.getColor("controlDkShadow"),
                                                            table.getColor("controlHighlight"),
                                                            table.getColor("controlLtHighlight"));

	
        Object menuItemCheckIcon = WindowsIconFactory.getMenuItemCheckIcon();

        Object menuItemArrowIcon = WindowsIconFactory.getMenuItemArrowIcon();

        Object menuArrowIcon = WindowsIconFactory.getMenuArrowIcon();

	Object menuItemAcceleratorDelimiter = new String("+");

        Object[] defaults = {
	    "TextField.focusInputMap", fieldInputMap,
	    "PasswordField.focusInputMap", fieldInputMap,
	    "TextArea.focusInputMap", multilineInputMap,
	    "TextPane.focusInputMap", multilineInputMap,
	    "EditorPane.focusInputMap", multilineInputMap,

	    // Buttons
	    "Button.dashedRectGapX", new Integer(5),
	    "Button.dashedRectGapY", new Integer(4),
	    "Button.dashedRectGapWidth", new Integer(10),
	    "Button.dashedRectGapHeight", new Integer(8),
	    "Button.textShiftOffset", new Integer(1),
            "Button.focus", black,
	    "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                         "SPACE", "pressed",
                "released SPACE", "released"
              }),

            "CheckBox.background", table.get("control"),
            "CheckBox.shadow", table.get("controlShadow"),
            "CheckBox.darkShadow", table.get("controlDkShadow"),
            "CheckBox.highlight", table.get("window"),
            "CheckBox.icon", checkBoxIcon,
            "CheckBox.border", radioButtonBorder,
            "CheckBox.focus", black,
	    "CheckBox.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
		 }),

            "RadioButton.background", table.get("control"),
            "RadioButton.shadow", table.get("controlShadow"),
            "RadioButton.darkShadow", table.get("controlDkShadow"),
            "RadioButton.highlight", table.get("window"),
            "RadioButton.icon", radioButtonIcon,
            "RadioButton.border", radioButtonBorder,
            "RadioButton.focus", black,
	    "RadioButton.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                          "SPACE", "pressed",
                 "released SPACE", "released"
	      }),

	    "ToggleButton.textShiftOffset", new Integer(1),
            "ToggleButton.focus", black,
            "ToggleButton.border", radioButtonBorder,
            "ToggleButton.background", table.get("control"),
            "ToggleButton.foreground", table.get("controlText"),
            "ToggleButton.focus", table.get("controlText"),
            "ToggleButton.font", dialogPlain12,
	    "ToggleButton.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	        }),

            "ComboBox.border", comboBoxBorder,
	    "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
		   "ESCAPE", "hidePopup",
		  "PAGE_UP", "pageUpPassThrough",
		"PAGE_DOWN", "pageDownPassThrough",
		     "HOME", "homePassThrough",
		      "END", "endPassThrough",
		     "DOWN", "selectNext",
		  "KP_DOWN", "selectNext",
		       "UP", "selectPrevious",
		    "KP_UP", "selectPrevious"
	      }),

	    // DeskTop.
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

	    // List.
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

	    // Menus
            "Menu.border", marginBorder,
            "Menu.font", dialogPlain12,
            "Menu.foreground", table.get("menuText"),
            "Menu.background", table.get("menu"),
            "Menu.selectionForeground", table.get("textHighlightText"),
            "Menu.selectionBackground", table.get("textHighlight"),
            "Menu.arrowIcon", menuArrowIcon,
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

	    // MenuBar.
	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },

            "MenuItem.border", marginBorder,
            "MenuItem.font", dialogPlain12,
            "MenuItem.foreground", table.get("menuText"),
            "MenuItem.background", table.get("menu"),
            "MenuItem.selectionForeground", table.get("textHighlightText"),
            "MenuItem.selectionBackground", table.get("textHighlight"),
	    "MenuItem.acceleratorDelimiter", menuItemAcceleratorDelimiter,
            "MenuItem.checkIcon", menuItemCheckIcon,
            "MenuItem.arrowIcon", menuItemArrowIcon,

	    // OptionPane.
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },

	    // ScrollBar.
	    "ScrollBar.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "negativeUnitIncrement",
		    "KP_RIGHT", "negativeUnitIncrement",
		        "DOWN", "positiveUnitIncrement",
		     "KP_DOWN", "positiveUnitIncrement",
		   "PAGE_DOWN", "positiveBlockIncrement",
	      "ctrl PAGE_DOWN", "positiveBlockIncrement",
		        "LEFT", "positiveUnitIncrement",
		     "KP_LEFT", "positiveUnitIncrement",
		          "UP", "negativeUnitIncrement",
		       "KP_UP", "negativeUnitIncrement",
		     "PAGE_UP", "negativeBlockIncrement",
	        "ctrl PAGE_UP", "negativeBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

	    // ScrollPane.
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

	    // Slider.
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

            "SplitPane.background", table.get("control"),
            "SplitPane.highlight", table.get("controllHighlight"),
            "SplitPane.shadow", table.get("controlShadow"),
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
		        "F6", "toggleFocus"
		 }),

	    // TabbedPane
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

	    // Table.
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

	    // ToolBar.
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

	    "FileChooser.newFolderIcon", LookAndFeel.makeIcon(getClass(), "icons/NewFolder.gif"),
	    "FileChooser.upFolderIcon", LookAndFeel.makeIcon(getClass(), "icons/UpFolder.gif"),
	    "FileChooser.homeFolderIcon", LookAndFeel.makeIcon(getClass(), "icons/HomeFolder.gif"),
	    "FileChooser.detailsViewIcon", LookAndFeel.makeIcon(getClass(), "icons/DetailsView.gif"),
	    "FileChooser.listViewIcon", LookAndFeel.makeIcon(getClass(), "icons/ListView.gif"),

            "FileChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
            "FileChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
            "FileChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),
	    "FileChooser.ancestorInputMap", 
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelSelection"
		 }),

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

	    "InternalFrame.windowBindings", new Object[] {
		"shift ESCAPE", "showSystemMenu",
		  "ctrl SPACE", "showSystemMenu",
		      "ESCAPE", "hideSystemMenu"},

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
}
