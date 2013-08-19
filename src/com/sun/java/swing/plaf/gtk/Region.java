/*
 * @(#)Region.java	1.26 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import java.util.*;

/**
 * A typesafe enumeration of the distinct rendering portions of
 * Swing's Components. Some Components support more than one region.
 *
 * @version 1.26, 01/23/03
 * @author Scott Violet
 */
class Region {
    private static final Map uiToRegionMap = new HashMap();

    public static final Region ARROW_BUTTON = new Region("ArrowButton",
                                                         "ArrowButtonUI");

    public static final Region BUTTON = new Region("Button",
                                                   "ButtonUI");

    public static final Region CHECK_BOX = new Region("CheckBox",
                                                   "CheckBoxUI");

    public static final Region CHECK_BOX_MENU_ITEM = new Region(
                                     "CheckBoxMenuItem", "CheckBoxMenuItemUI");

    public static final Region COLOR_CHOOSER = new Region(
                                     "ColorChooser", "ColorChooserUI");

    public static final Region COMBO_BOX = new Region(
                                     "ComboBox", "ComboBoxUI");

    public static final Region DESKTOP_PANE = new Region("DesktopPane",
                                                         "DesktopPaneUI");
    public static final Region DESKTOP_ICON = new Region("DesktopIcon",
                                                         "DesktopIconUI");
                                                         
    public static final Region EDITOR_PANE = new Region("EditorPane",
                                                        "EditorPaneUI");

    public static final Region FILE_CHOOSER = new Region("FileChooser",
                                                         "FileChooserUI");

    public static final Region FORMATTED_TEXT_FIELD = new Region(
                            "FormattedTextField", "FormattedTextFieldUI");

    public static final Region INTERNAL_FRAME = new Region("InternalFrame",
                                                           "InternalFrameUI");
    public static final Region INTERNAL_FRAME_TITLE_PANE =
                         new Region("InternalFrameTitlePane",
                                    "InternalFrameTitlePaneUI");

    public static final Region LABEL = new Region("Label", "LabelUI");

    public static final Region LIST = new Region("List", "ListUI");

    public static final Region MENU = new Region("Menu", "MenuUI");

    public static final Region MENU_BAR = new Region("MenuBar", "MenuBarUI");

    public static final Region MENU_ITEM = new Region("MenuItem","MenuItemUI");
    public static final Region MENU_ITEM_ACCELERATOR = new Region(
                                         "MenuItemAccelerator");

    public static final Region OPTION_PANE = new Region("OptionPane",
                                                        "OptionPaneUI");

    public static final Region PANEL = new Region("Panel", "PanelUI");
    
    public static final Region PASSWORD_FIELD = new Region("PasswordField",
                                                           "PasswordFieldUI");

    public static final Region POPUP_MENU = new Region("PopupMenu",
                                                       "PopupMenuUI");

    public static final Region POPUP_MENU_SEPARATOR = new Region(
                           "PopupMenuSeparator", "PopupMenuSeparatorUI");

    public static final Region PROGRESS_BAR = new Region("ProgressBar",
                                                         "ProgressBarUI");

    public static final Region RADIO_BUTTON = new Region(
                               "RadioButton", "RadioButtonUI");

    public static final Region RADIO_BUTTON_MENU_ITEM = new Region(
                               "RadioButtonMenuItem", "RadioButtonMenuItemUI");

    public static final Region ROOT_PANE = new Region("RootPane",
                                                      "RootPaneUI");

    public static final Region SCROLL_BAR = new Region("ScrollBar",
                                                       "ScrollBarUI");
    public static final Region SCROLL_BAR_TRACK = new Region("ScrollBarTrack");
    public static final Region SCROLL_BAR_THUMB = new Region("ScrollBarThumb");

    public static final Region SCROLL_PANE = new Region("ScrollPane",
                                                        "ScrollPaneUI");

    public static final Region SEPARATOR = new Region("Separator",
                                                      "SeparatorUI");

    public static final Region SLIDER = new Region("Slider", "SliderUI");
    public static final Region SLIDER_TRACK = new Region("SliderTrack");
    public static final Region SLIDER_THUMB = new Region("SliderThumb");

    public static final Region SPINNER = new Region("Spinner", "SpinnerUI");

    public static final Region SPLIT_PANE = new Region("SplitPane",
                                                      "SplitPaneUI");
    public static final Region SPLIT_PANE_DIVIDER = new Region(
                                        "SplitPaneDivider", "SplitPaneDividerUI");

    public static final Region TABBED_PANE = new Region("TabbedPane",
                                                        "TabbedPaneUI");
    public static final Region TABBED_PANE_TAB = new Region("TabbedPaneTab");
    public static final Region TABBED_PANE_TAB_AREA =
                                 new Region("TabbedPaneTabArea");
    public static final Region TABBED_PANE_CONTENT =
                                 new Region("TabbedPaneContent");

    public static final Region TABLE = new Region("Table", "TableUI");

    public static final Region TABLE_HEADER = new Region("TableHeader",
                                                         "TableHeaderUI");
                                 
    public static final Region TEXT_AREA = new Region("TextArea",
                                                      "TextAreaUI");

    public static final Region TEXT_FIELD = new Region("TextField",
                                                       "TextFieldUI");

    public static final Region TEXT_PANE = new Region("TextPane",
                                                      "TextPaneUI");
    
    public static final Region TOGGLE_BUTTON = new Region("ToggleButton",
                                                          "ToggleButtonUI");
    
    public static final Region TOOL_BAR = new Region("ToolBar", "ToolBarUI");
    public static final Region TOOL_BAR_CONTENT = new Region("ToolBarContent");
    public static final Region TOOL_BAR_DRAG_WINDOW = new Region(
                                        "ToolBarDragWindow", null, false);

    public static final Region TOOL_TIP = new Region("ToolTip", "ToolTipUI");

    public static final Region TOOL_BAR_SEPARATOR = new Region(
                          "ToolBarSeparator", "ToolBarSeparatorUI");

    public static final Region TREE = new Region("Tree", "TreeUI");
    public static final Region TREE_CELL = new Region("TreeCell");

    public static final Region VIEWPORT = new Region("Viewport", "ViewportUI");


    private String name;
    private boolean subregion;


    static Region getRegion(JComponent c) {
        return (Region)uiToRegionMap.get(c.getUIClassID());
    }

    static void registerUIs(UIDefaults table) {
        Iterator uis = uiToRegionMap.keySet().iterator();

        while (uis.hasNext()) {
            Object key = uis.next();

            table.put(key, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        }
    }


    Region(String name) {
        this(name, null, true);
    }

    Region(String name, String ui) {
        this(name, ui, false);
    }

    Region(String name, String ui, boolean subregion) {
        this.name = name;
        if (ui != null) {
            uiToRegionMap.put(ui, this);
        }
        this.subregion = subregion;
    }

    /**
     * Returns true if the Region is a subregion of a Component.
     *
     * @return true if the Region is a subregion of a Component.
     */
    public boolean isSubregion() {
        return subregion;
    }

    /**
     * Returns the name of the region.
     *
     * @return name of the subregion.
     */
    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
