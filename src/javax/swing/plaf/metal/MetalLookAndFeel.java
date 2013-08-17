/*
 * @(#)MetalLookAndFeel.java	1.86 98/08/28
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
 * @version 1.86 08/28/98
 * @author Steve Wilson
 */
public class MetalLookAndFeel extends BasicLookAndFeel
{
  
    private static MetalTheme currentTheme;

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

        Border marginBorder = new BasicBorders.MarginBorder();
        Border flush3DBorder = new MetalBorders.Flush3DBorder();

	Border textFieldBorder = new BorderUIResource.CompoundBorderUIResource(
						  new MetalBorders.TextFieldBorder(),
					          marginBorder);


        Object textBorder = new BorderUIResource.CompoundBorderUIResource(
                                                  flush3DBorder,
                                                  marginBorder);

	JTextComponent.KeyBinding[] fieldBindings = {
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.copyAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.pasteAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.cutAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionBackwardAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionForwardAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.previousWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.nextWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 
								 InputEvent.CTRL_MASK | 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionPreviousWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
								 InputEvent.CTRL_MASK |
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionNextWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.selectAllAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
					  DefaultEditorKit.beginLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
					  DefaultEditorKit.endLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionBeginLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionEndLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					  JTextField.notifyAction)
	};

	JTextComponent.KeyBinding[] multilineBindings = {
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.copyAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.pasteAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.cutAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionBackwardAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionForwardAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.previousWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.nextWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 
								 InputEvent.CTRL_MASK | 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionPreviousWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 
								 InputEvent.CTRL_MASK |
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionNextWordAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A, 
								 InputEvent.CTRL_MASK),
					  DefaultEditorKit.selectAllAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
					  DefaultEditorKit.beginLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
					  DefaultEditorKit.endLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionBeginLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, 
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionEndLineAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
					  DefaultEditorKit.upAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
					  DefaultEditorKit.downAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
					  DefaultEditorKit.pageUpAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
					  DefaultEditorKit.pageDownAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionUpAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
								 InputEvent.SHIFT_MASK),
					  DefaultEditorKit.selectionDownAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					  DefaultEditorKit.insertBreakAction),
	    new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
					  DefaultEditorKit.insertTabAction)
	};

        Object scrollPaneBorder = new MetalBorders.ScrollPaneBorder();

        Object buttonBorder =  new BorderUIResource.CompoundBorderUIResource(
                                           new MetalBorders.ButtonBorder(),
                                           marginBorder);

        Object toggleButtonBorder =  new BorderUIResource.CompoundBorderUIResource(
                                           new MetalBorders.ToggleButtonBorder(),
                                           marginBorder);

        Object titledBorderBorder = new BorderUIResource.LineBorderUIResource(
                                                 table.getColor("controlShadow"));

        Object desktopIconBorder = new BorderUIResource.CompoundBorderUIResource(
                                          new LineBorder(getControlDarkShadow(), 1),
                                          new MatteBorder (2,2,1,2, getControl()));
     
	//   Object internalFrameBorder = new MetalBorders.InternalFrameBorder();

        Object menuBarBorder = new MetalBorders.MenuBarBorder();
        Object popupMenuBorder = new MetalBorders.PopupMenuBorder();
        Object menuItemBorder = new MetalBorders.MenuItemBorder();
        Object toolBarBorder = new MetalBorders.ToolBarBorder();

	Object progressBarBorder = new BorderUIResource.LineBorderUIResource(
                                             getControlDarkShadow(), 1);

        Object toolTipBorder = new BorderUIResource.LineBorderUIResource(
                                             getPrimaryControlDarkShadow());

        Object focusCellHighlightBorder = new BorderUIResource.LineBorderUIResource(
                                             getFocusColor());

        Object tabbedPaneTabAreaInsets = new InsetsUIResource(4, 2, 0, 6);

	Object sliderFocusInsets = new InsetsUIResource( 0, 0, 0, 0 );


        //
        // DEFAULTS TABLE
        //

	final int internalFrameIconSize = 16;

        Object[] defaults = {
            // Text (Note: many are inherited)
            "TextField.border", textFieldBorder,
	    "TextField.font", getUserTextFont(),
	    "TextField.caretForeground", getUserTextColor(),

            "PasswordField.border", textBorder,
            "PasswordField.font", getUserTextFont(),
	    "PasswordField.caretForeground", getUserTextColor(),

            "TextArea.font", getUserTextFont(),
            "TextArea.caretForeground", getUserTextColor(),

	    "TextPane.selectionBackground", table.get("textHighlight"),
	    "TextPane.selectionForeground", table.get("textHighlightText"),
	    "TextPane.background", table.get("window"),
	    "TextPane.foreground", table.get("textText"),
            "TextPane.font", getUserTextFont(),
            "TextPane.caretForeground", getUserTextColor(),

	    "EditorPane.selectionBackground", table.get("textHighlight"),
	    "EditorPane.selectionForeground", table.get("textHighlightText"),
	    "EditorPane.background", table.get("window"),
	    "EditorPane.foreground", table.get("textText"),
	    "EditorPane.font", getUserTextFont(),
	    "EditorPane.caretForeground", getUserTextColor(),

	    "TextField.keyBindings", fieldBindings,
	    "PasswordField.keyBindings", fieldBindings,
	    "TextArea.keyBindings", multilineBindings,
	    "TextPane.keyBindings", multilineBindings,
	    "EditorPane.keyBindings", multilineBindings,
            

            // Buttons
            "Button.background", getControl(),
            "Button.foreground", getControlTextColor(),
            "Button.disabledText", getInactiveControlTextColor(),
            "Button.select", getControlShadow(),
            "Button.border", buttonBorder,
            "Button.font", getControlTextFont(),
            "Button.focus", getFocusColor(),

            "CheckBox.background", getControl(),
            "CheckBox.foreground", getControlTextColor(),
            "CheckBox.disabledText", getInactiveControlTextColor(),
            "Checkbox.select", getControlShadow(),
            "CheckBox.font", getControlTextFont(),
            "CheckBox.focus", getFocusColor(),
            "CheckBox.icon", new MetalCheckBoxIcon(),

            "RadioButton.background", getControl(),
            "RadioButton.foreground", getControlTextColor(),
            "RadioButton.disabledText", getInactiveControlTextColor(),
            "RadioButton.select", getControlShadow(),
            "RadioButton.icon", MetalIconFactory.getRadioButtonIcon(),
            "RadioButton.font", getControlTextFont(),
            "RadioButton.focus", getFocusColor(),

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
            "ToggleButton.font", getControlTextFont(),


            // File View 
            "FileView.directoryIcon", MetalIconFactory.getTreeFolderIcon(),
            "FileView.fileIcon", MetalIconFactory.getTreeLeafIcon(),
            "FileView.computerIcon", MetalIconFactory.getTreeComputerIcon(),
            "FileView.hardDriveIcon", MetalIconFactory.getTreeHardDriveIcon(),
            "FileView.floppyDriveIcon", MetalIconFactory.getTreeFloppyDriveIcon(),

            // File Chooser
            "FileChooser.detailsViewIcon", MetalIconFactory.getFileChooserDetailViewIcon(),
            "FileChooser.homeFolderIcon", MetalIconFactory.getFileChooserHomeFolderIcon(),
            "FileChooser.listViewIcon", MetalIconFactory.getFileChooserListViewIcon(),
            "FileChooser.newFolderIcon", MetalIconFactory.getFileChooserNewFolderIcon(),
            "FileChooser.upFolderIcon", MetalIconFactory.getFileChooserUpFolderIcon(),

            "FileChooser.lookInLabelMnemonic", new Integer(KeyEvent.VK_I),
            "FileChooser.fileNameLabelMnemonic", new Integer(KeyEvent.VK_N),
            "FileChooser.filesOfTypeLabelMnemonic", new Integer(KeyEvent.VK_T),


            // ToolTip
            "ToolTip.font", getSystemTextFont(),
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
            "Slider.horizontalThumbIcon", MetalIconFactory.getHorizontalSliderThumbIcon(),
            "Slider.verticalThumbIcon", MetalIconFactory.getVerticalSliderThumbIcon(),

            // Progress Bar
	    "ProgressBar.font", getControlTextFont(),
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
            "ComboBox.font", getControlTextFont(),

            // Internal Frame Defaults
            "InternalFrame.icon", MetalIconFactory.getInternalFrameDefaultMenuIcon(),
            "InternalFrame.border", new MetalBorders.InternalFrameBorder(),
            "InternalFrame.paletteBorder", new MetalBorders.PaletteBorder(),
	    "InternalFrame.paletteTitleHeight", new Integer(11),
	    "InternalFrame.paletteCloseIcon", new MetalIconFactory.PaletteCloseIcon(),
            "InternalFrame.closeIcon", MetalIconFactory.getInternalFrameCloseIcon(internalFrameIconSize),
            "InternalFrame.maximizeIcon", MetalIconFactory.getInternalFrameMaximizeIcon(internalFrameIconSize),
            "InternalFrame.iconizeIcon", MetalIconFactory.getInternalFrameMinimizeIcon(internalFrameIconSize),
            "InternalFrame.minimizeIcon", MetalIconFactory.getInternalFrameAltMaximizeIcon(internalFrameIconSize),
            "InternalFrame.font",  getWindowTitleFont(),

            // Desktop Icon
            "DesktopIcon.border", desktopIconBorder,
            "DesktopIcon.font", getControlTextFont(),
            "DesktopIcon.foreground", getControlTextColor(),
            "DesktopIcon.background", getControl(),

            // Titled Border
            "TitledBorder.font", getControlTextFont(),
            "TitledBorder.titleColor", getSystemTextColor(),
            "TitledBorder.border", titledBorderBorder,

            // Label
            "Label.font", getControlTextFont(),
            "Label.background", table.get("control"),
            "Label.foreground", getSystemTextColor(),
            "Label.disabledForeground", getInactiveSystemTextColor(),

            // List
            "List.focusCellHighlightBorder", focusCellHighlightBorder,

            // ScrollBar
            "ScrollBar.background", getControl(),
            "ScrollBar.highlight", getControlHighlight(),
            "ScrollBar.shadow", getControlShadow(),
            "ScrollBar.darkShadow", getControlDarkShadow(),
            "ScrollBar.thumb", getPrimaryControlShadow(),
            "ScrollBar.thumbShadow", getPrimaryControlDarkShadow(),
            "ScrollBar.thumbHighlight", getPrimaryControl(),
            "ScrollBar.width", new Integer( 17 ),

	    // ScrollPane
	    "ScrollPane.border", scrollPaneBorder,
	    "ScrollPane.background", table.get("control"/*"window"*/),

            // Tabbed Pane
            "TabbedPane.font", getControlTextFont(),
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
            
            // Table
	    "Table.font", getUserTextFont(),
            "Table.focusCellHighlightBorder", focusCellHighlightBorder,
            "Table.focusCellBackground", table.get("window"),
            "Table.scrollPaneBorder", scrollPaneBorder,
      	    "Table.gridColor", getControlShadow(),  // grid line color

	    "Table.font", getUserTextFont(),
	    "TableHeader.cellBorder", new MetalUtils.TableHeaderBorder(),



            // MenuBar
            "MenuBar.border", menuBarBorder,
            "MenuBar.font", getMenuTextFont(),
            "MenuBar.foreground", getMenuForeground(),
            "MenuBar.background", getMenuBackground(),

            // Menu
            "Menu.border", menuItemBorder,
            "Menu.borderPainted", Boolean.TRUE,
            "Menu.font", getMenuTextFont(),
            "Menu.foreground", getMenuForeground(),
            "Menu.background", getMenuBackground(),
            "Menu.selectionForeground", getMenuSelectedForeground(),
            "Menu.selectionBackground", getMenuSelectedBackground(),
            "Menu.disabledForeground", getMenuDisabledForeground(),
            "Menu.acceleratorFont", getSubTextFont(),
            "Menu.acceleratorForeground", getAcceleratorForeground(),
            "Menu.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "Menu.checkIcon", MetalIconFactory.getMenuItemCheckIcon(),
            "Menu.arrowIcon", MetalIconFactory.getMenuArrowIcon(),

            // Menu Item
            "MenuItem.border", menuItemBorder,
            "MenuItem.borderPainted", Boolean.TRUE,
            "MenuItem.font", getMenuTextFont(),
            "MenuItem.foreground", getMenuForeground(),
            "MenuItem.background", getMenuBackground(),
            "MenuItem.selectionForeground", getMenuSelectedForeground(),
            "MenuItem.selectionBackground", getMenuSelectedBackground(),
            "MenuItem.disabledForeground", getMenuDisabledForeground(),
            "MenuItem.acceleratorFont", getSubTextFont(),
            "MenuItem.acceleratorForeground", getAcceleratorForeground(),
            "MenuItem.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "MenuItem.checkIcon", MetalIconFactory.getMenuItemCheckIcon(),
            "MenuItem.arrowIcon", MetalIconFactory.getMenuItemArrowIcon(),

            // Separator
            "Separator.background", getSeparatorBackground(),
            "Separator.foreground", getSeparatorForeground(),

            // Popup Menu
            "PopupMenu.background", getMenuBackground(),
            "PopupMenu.border", popupMenuBorder,          

            // CB & RB Menu Item
            "CheckBoxMenuItem.border", menuItemBorder,
            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
            "CheckBoxMenuItem.font", getMenuTextFont(),
            "CheckBoxMenuItem.foreground", getMenuForeground(),
            "CheckBoxMenuItem.background", getMenuBackground(),
            "CheckBoxMenuItem.selectionForeground", getMenuSelectedForeground(),
            "CheckBoxMenuItem.selectionBackground", getMenuSelectedBackground(),
            "CheckBoxMenuItem.disabledForeground", getMenuDisabledForeground(),
            "CheckBoxMenuItem.acceleratorFont", getSubTextFont(),
            "CheckBoxMenuItem.acceleratorForeground", getAcceleratorForeground(),
            "CheckBoxMenuItem.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "CheckBoxMenuItem.checkIcon", MetalIconFactory.getCheckBoxMenuItemIcon(),
            "CheckBoxMenuItem.arrowIcon", MetalIconFactory.getMenuItemArrowIcon(),

            "RadioButtonMenuItem.border", menuItemBorder,
            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
            "RadioButtonMenuItem.font", getMenuTextFont(),
            "RadioButtonMenuItem.foreground", getMenuForeground(),
            "RadioButtonMenuItem.background", getMenuBackground(),
            "RadioButtonMenuItem.selectionForeground", getMenuSelectedForeground(),
            "RadioButtonMenuItem.selectionBackground", getMenuSelectedBackground(),
            "RadioButtonMenuItem.disabledForeground", getMenuDisabledForeground(),
            "RadioButtonMenuItem.acceleratorFont", getSubTextFont(),
            "RadioButtonMenuItem.acceleratorForeground", getAcceleratorForeground(),
            "RadioButtonMenuItem.acceleratorSelectionForeground", getAcceleratorSelectedForeground(),
            "RadioButtonMenuItem.checkIcon", MetalIconFactory.getRadioButtonMenuItemIcon(),
            "RadioButtonMenuItem.arrowIcon", MetalIconFactory.getMenuItemArrowIcon(),

	    // SplitPane

	    "SplitPane.dividerSize", new Integer(8),

            // Tree
            "Tree.background", getWindowBackground(),
	    "Tree.font", getSystemTextFont(),
            "Tree.textForeground", table.get("textText"),
            "Tree.textBackground", getWindowBackground(),
            "Tree.selectionForeground", table.get("textHighlightText"),
            "Tree.selectionBackground", table.get("textHighlight"),
            "Tree.selectionBorderColor", MetalLookAndFeel.getFocusColor(),
            "Tree.openIcon", MetalIconFactory.getTreeFolderIcon(),
            "Tree.closedIcon", MetalIconFactory.getTreeFolderIcon(),
            "Tree.leafIcon", MetalIconFactory.getTreeLeafIcon(),
            "Tree.expandedIcon", MetalIconFactory.getTreeControlIcon( MetalIconFactory.DARK ),
            "Tree.collapsedIcon", MetalIconFactory.getTreeControlIcon( MetalIconFactory.LIGHT ),
            "Tree.line", getPrimaryControl(), // horiz lines
            "Tree.hash", getPrimaryControl(),  // legs
	    "Tree.rowHeight", new Integer(0),

            // ToolBar
            "ToolBar.border", toolBarBorder,
            "ToolBar.background", getMenuBackground(),
            "ToolBar.foreground", getMenuForeground(),
            "ToolBar.font", getMenuTextFont(),
            "ToolBar.dockingBackground", getMenuBackground(),
            "ToolBar.floatingBackground", getMenuBackground(),
            "ToolBar.dockingForeground", getPrimaryControlDarkShadow(), 
            "ToolBar.floatingForeground", getPrimaryControl(),
        };

        table.putDefaults(defaults);
    }

    protected void createDefaultTheme() {
        if( currentTheme == null) 
        currentTheme =  new DefaultMetalTheme();
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
    }

    public static FontUIResource getControlTextFont() { return currentTheme.getControlTextFont();}
    public static FontUIResource getSystemTextFont() { return currentTheme.getSystemTextFont();}
    public static FontUIResource getUserTextFont() { return currentTheme.getUserTextFont();}
    public static FontUIResource getMenuTextFont() { return currentTheme.getMenuTextFont();}
    public static FontUIResource getWindowTitleFont() { return currentTheme.getWindowTitleFont();}
    public static FontUIResource getSubTextFont() { return currentTheme.getSubTextFont();}

    public static ColorUIResource getDesktopColor() { return currentTheme.getDesktopColor(); }
    public static ColorUIResource getFocusColor() { return currentTheme.getFocusColor(); }

    public static ColorUIResource getWhite() { return currentTheme.getWhite(); }
    public static ColorUIResource getBlack() { return currentTheme.getBlack(); }
    public static ColorUIResource getControl() { return currentTheme.getControl(); }
    public static ColorUIResource getControlShadow() { return currentTheme.getControlShadow(); }
    public static ColorUIResource getControlDarkShadow() { return currentTheme.getControlDarkShadow(); }
    public static ColorUIResource getControlInfo() { return currentTheme.getControlInfo(); } 
    public static ColorUIResource getControlHighlight() { return currentTheme.getControlHighlight(); }
    public static ColorUIResource getControlDisabled() { return currentTheme.getControlDisabled(); }

    public static ColorUIResource getPrimaryControl() { return currentTheme.getPrimaryControl(); }  
    public static ColorUIResource getPrimaryControlShadow() { return currentTheme.getPrimaryControlShadow(); }  
    public static ColorUIResource getPrimaryControlDarkShadow() { return currentTheme.getPrimaryControlDarkShadow(); }  
    public static ColorUIResource getPrimaryControlInfo() { return currentTheme.getPrimaryControlInfo(); } 
    public static ColorUIResource getPrimaryControlHighlight() { return currentTheme.getPrimaryControlHighlight(); }  

    public static ColorUIResource getSystemTextColor() { return currentTheme.getSystemTextColor(); }
    public static ColorUIResource getControlTextColor() { return currentTheme.getControlTextColor(); }  
    public static ColorUIResource getInactiveControlTextColor() { return currentTheme.getInactiveControlTextColor(); }  
    public static ColorUIResource getInactiveSystemTextColor() { return currentTheme.getInactiveSystemTextColor(); }
    public static ColorUIResource getUserTextColor() { return currentTheme.getUserTextColor(); }
    public static ColorUIResource getTextHighlightColor() { return currentTheme.getTextHighlightColor(); }
    public static ColorUIResource getHighlightedTextColor() { return currentTheme.getHighlightedTextColor(); }

    public static ColorUIResource getWindowBackground() { return currentTheme.getWindowBackground(); }
    public static ColorUIResource getWindowTitleBackground() { return currentTheme.getWindowTitleBackground(); }
    public static ColorUIResource getWindowTitleForeground() { return currentTheme.getWindowTitleForeground(); }
    public static ColorUIResource getWindowTitleInactiveBackground() { return currentTheme.getWindowTitleInactiveBackground(); }
    public static ColorUIResource getWindowTitleInactiveForeground() { return currentTheme.getWindowTitleInactiveForeground(); }

    public static ColorUIResource getMenuBackground() { return currentTheme.getMenuBackground(); }
    public static ColorUIResource getMenuForeground() { return currentTheme.getMenuForeground(); }
    public static ColorUIResource getMenuSelectedBackground() { return currentTheme.getMenuSelectedBackground(); }
    public static ColorUIResource getMenuSelectedForeground() { return currentTheme.getMenuSelectedForeground(); }
    public static ColorUIResource getMenuDisabledForeground() { return currentTheme.getMenuDisabledForeground(); }
    public static ColorUIResource getSeparatorBackground() { return currentTheme.getSeparatorBackground(); }
    public static ColorUIResource getSeparatorForeground() { return currentTheme.getSeparatorForeground(); }
    public static ColorUIResource getAcceleratorForeground() { return currentTheme.getAcceleratorForeground(); }
    public static ColorUIResource getAcceleratorSelectedForeground() { return currentTheme.getAcceleratorSelectedForeground(); }

}
