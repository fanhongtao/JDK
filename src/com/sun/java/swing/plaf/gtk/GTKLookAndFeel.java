/*
 * @(#)GTKLookAndFeel.java	1.75 06/12/14
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import com.sun.java.swing.SwingUtilities2;
import java.lang.ref.*;
import javax.swing.plaf.synth.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.plaf.*;
import javax.swing.text.DefaultEditorKit;
import java.io.IOException;

import sun.security.action.GetPropertyAction;

/**
 * @version 1.75, 12/14/06
 * @author Scott Violet
 */
public class GTKLookAndFeel extends SynthLookAndFeel {
    private static final boolean IS_22;

    /**
     * Whether or not text is drawn antialiased.  This keys off the
     * desktop property 'gnome.Xft/Antialias'.
     */
    static Boolean aaText = Boolean.FALSE;

    /**
     * Whether or not the default locale is CJK. If it is,
     * the GNOME desktop property for antialiasing is ignored
     * and the text is always rendered w/o aa.
     * This is done to be consistent with what GTK does - they
     * disable text aa for CJK locales as well.
     *
     * Note: this doesn't work well with changing locales
     * at runtime. But most of Swing/2D code (including fonts
     * initialization) doesn't either.
     */
    static boolean cjkLocale;

    /**
     * Font to use in places where there is no widget.
     */
    private Font fallbackFont;

    /**
     * If true, GTKLookAndFeel is inside the <code>initialize</code>
     * method.
     */
    private boolean inInitialize;

    static {
        // Backup for specifying the version, this isn't currently documented.
        // If you pass in anything but 2.2 you got the 2.0 colors/look.
        String version = (String)java.security.AccessController.doPrivileged(
               new GetPropertyAction("swing.gtk.version"));
        if (version != null) {
            IS_22 = version.equals("2.2");
        }
        else {
            IS_22 = true;
        }
    }

    /**
     * Returns true if running on system containing at least 2.2.
     */
    static boolean is2_2() {
        // NOTE: We're currently hard coding to use 2.2.
        // If we want to support both GTK 2.0 and 2.2, we'll
        // need to get the major/minor/micro version from the .so.
        // Refer to bug 4912613 for details.
        return IS_22;
    }

    /**
     * Maps a swing constant to a GTK constant.
     */
    static int SwingOrientationConstantToGTK(int side) {
        switch (side) {
        case SwingConstants.LEFT:
            return GTKConstants.LEFT;
        case SwingConstants.RIGHT:
            return GTKConstants.RIGHT;
        case SwingConstants.TOP:
            return GTKConstants.TOP;
        case SwingConstants.BOTTOM:
            return GTKConstants.BOTTOM;
        }
        assert false : "Unknowning orientation: " + side;
        return side;
    }

    /**
     * Maps from a Synth state to the corresponding GTK state. 
     * The GTK states are named differently than Synth's states, the
     * following gives the mapping:
     * <table><tr><td>Synth<td>GTK
     * <tr><td>SynthConstants.PRESSED<td>ACTIVE
     * <tr><td>SynthConstants.SELECTED<td>SELECTED
     * <tr><td>SynthConstants.MOUSE_OVER<td>PRELIGHT
     * <tr><td>SynthConstants.DISABLED<td>INACTIVE
     * <tr><td>SynthConstants.ENABLED<td>NORMAL
     * </table>
     * Additionally some widgets are special cased.
     */
    static int synthStateToGTKState(Region region, int state) {
        int orgState = state;

        if ((state & SynthConstants.PRESSED) != 0) {
            if (region == Region.RADIO_BUTTON
                    || region == Region.CHECK_BOX
                    || region == Region.TOGGLE_BUTTON
                    || region == Region.MENU
                    || region == Region.MENU_ITEM
                    || region == Region.RADIO_BUTTON_MENU_ITEM
                    || region == Region.CHECK_BOX_MENU_ITEM
                    || region == Region.SPLIT_PANE) {
                state = SynthConstants.MOUSE_OVER;
            } else {
                state = SynthConstants.PRESSED;
            }
        }
        else if ((state & SynthConstants.SELECTED) != 0) {
            if (region == Region.MENU) {
                state = SynthConstants.MOUSE_OVER;
            } else if (region == Region.RADIO_BUTTON ||
                          region == Region.TOGGLE_BUTTON ||
                          region == Region.RADIO_BUTTON_MENU_ITEM ||
                          region == Region.CHECK_BOX_MENU_ITEM ||
                          region == Region.CHECK_BOX ||
                          region == Region.BUTTON) {
                // If the button is SELECTED and is PRELIGHT we need to
                // make the state MOUSE_OVER otherwise we don't paint the
                // PRELIGHT.
                if ((state & SynthConstants.MOUSE_OVER) != 0) {
                    state = SynthConstants.MOUSE_OVER;
                } else if ((state & SynthConstants.DISABLED) != 0){
                    state = SynthConstants.DISABLED;
                } else {
                    state = SynthConstants.PRESSED;
                }
            } else if (region == Region.TABBED_PANE_TAB) {
                state = SynthConstants.ENABLED;
            } else {
                state = SynthConstants.SELECTED;
            }
        }
        else if ((state & SynthConstants.MOUSE_OVER) != 0) {
            state = SynthConstants.MOUSE_OVER;
        }
        else if ((state & SynthConstants.DISABLED) != 0) {
            state = SynthConstants.DISABLED;
        }
        else {
            if (region == Region.SLIDER_TRACK) {
                state = SynthConstants.PRESSED;
            } else if (region == Region.TABBED_PANE_TAB) {
                state = SynthConstants.PRESSED;
            } else {
                state = SynthConstants.ENABLED;
            }
        }
        return state;
    }

    static boolean isText(Region region) {
        // These Regions treat FOREGROUND as TEXT.
        return (region == Region.TEXT_FIELD ||
                region == Region.FORMATTED_TEXT_FIELD ||
                region == Region.LIST ||
                region == Region.PASSWORD_FIELD ||
                region == Region.SPINNER ||
                region == Region.TABLE ||
                region == Region.TEXT_AREA ||
                region == Region.TEXT_FIELD ||
                region == Region.TEXT_PANE ||
                region == Region.TREE);
    }

    public UIDefaults getDefaults() {
        // We need to call super for basic's properties file.
        UIDefaults table = super.getDefaults();

        initResourceBundle(table);
        // For compatability with apps expecting certain defaults we'll
        // populate the table with the values from basic.
        initSystemColorDefaults(table);
        initComponentDefaults(table);
        return table;
    }

    private void initResourceBundle(UIDefaults table) {
        table.addResourceBundle("com.sun.java.swing.plaf.gtk.resources.gtk");
    }

    protected void initComponentDefaults(UIDefaults table) {
        // For compatability with apps expecting certain defaults we'll
        // populate the table with the values from basic.
        super.initComponentDefaults(table);

        Object focusBorder = new GTKStyle.GTKLazyValue(
            "com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder",
            "getUnselectedCellBorder");
        Object focusSelectedBorder = new GTKStyle.GTKLazyValue(
            "com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder",
            "getSelectedCellBorder");

        GTKStyleFactory factory = (GTKStyleFactory)getStyleFactory(); 
        GTKStyle tableStyle = (GTKStyle)factory.getStyle("GtkTreeView");
        Color tableFocusCellBg = tableStyle.getColorForState(null, Region.TABLE,
                SynthConstants.ENABLED, GTKColorType.BACKGROUND);
        Color tableFocusCellFg = tableStyle.getColorForState(null, Region.TABLE,
                SynthConstants.ENABLED, GTKColorType.FOREGROUND);
        
        Integer caretBlinkRate = new Integer(500);
        Insets zeroInsets = new InsetsUIResource(0, 0, 0, 0);

        Double defaultCaretAspectRatio = new Double(0.025);
        Color caretColor = table.getColor("caretColor");
        
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

        Object editorMargin = new InsetsUIResource(3,3,3,3);

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

        class FontLazyValue implements UIDefaults.LazyValue {
            private Region region;
            FontLazyValue(Region region) {
                this.region = region;
            }
            public Object createValue(UIDefaults table) {
                GTKStyleFactory factory = (GTKStyleFactory)getStyleFactory();
                GTKStyle style = (GTKStyle)factory.getStyle(
                        GTKStyleFactory.gtkClassFor(region));
                return style.getFontForState(
                        null, region, SynthConstants.ENABLED);
            }
        }
        
        Object[] defaults = new Object[] {
            "ArrowButton.size", new Integer(13),


            "Button.defaultButtonFollowsFocus", Boolean.FALSE,
	    "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                         "SPACE", "pressed",
                "released SPACE", "released",
                         "ENTER", "pressed",
                "released ENTER", "released"
              }),
            "Button.font", new FontLazyValue(Region.BUTTON),


	    "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                         "SPACE", "pressed",
                "released SPACE", "released",
              }),
            "CheckBox.icon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getCheckBoxIcon"),
            "CheckBox.font", new FontLazyValue(Region.CHECK_BOX),


            "CheckBoxMenuItem.arrowIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getCheckBoxMenuItemArrowIcon"),
            "CheckBoxMenuItem.checkIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getCheckBoxMenuItemCheckIcon"),
            "CheckBoxMenuItem.font", 
                new FontLazyValue(Region.CHECK_BOX_MENU_ITEM),
            "CheckBoxMenuItem.margin", zeroInsets,


            "ColorChooser.showPreviewPanelText", Boolean.FALSE,
            "ColorChooser.panels", new UIDefaults.ActiveValue() {
                public Object createValue(UIDefaults table) {
                    return new AbstractColorChooserPanel[] {
                                       new GTKColorChooserPanel() };
                }
            },
            "ColorChooser.font", new FontLazyValue(Region.COLOR_CHOOSER),


	    "ComboBox.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
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
            "ComboBox.font", new FontLazyValue(Region.COMBO_BOX),


            "EditorPane.caretForeground", caretColor,
            "EditorPane.caretAspectRatio", defaultCaretAspectRatio,
            "EditorPane.caretBlinkRate", caretBlinkRate,
            "EditorPane.margin", editorMargin,
            "EditorPane.focusInputMap", multilineInputMap,
            "EditorPane.font", new FontLazyValue(Region.EDITOR_PANE),


	    "FileChooser.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelSelection"
		 }),
            "FileChooserUI", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",


            "FormattedTextField.caretForeground", caretColor,
            "FormattedTextField.caretAspectRatio", defaultCaretAspectRatio,
            "FormattedTextField.caretBlinkRate", caretBlinkRate,
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
            "FormattedTextField.font", 
                new FontLazyValue(Region.FORMATTED_TEXT_FIELD),


	    "InternalFrameTitlePane.titlePaneLayout",
				new GTKStyle.GTKLazyValue("com.sun.java.swing.plaf.gtk.Metacity",
						 "getTitlePaneLayout"),
            "InternalFrame.windowBindings", new Object[] {
                  "shift ESCAPE", "showSystemMenu",
                    "ctrl SPACE", "showSystemMenu",
                        "ESCAPE", "hideSystemMenu" },
            "InternalFrame.layoutTitlePaneAtOrigin", Boolean.TRUE,
            "InternalFrame.useTaskBar", Boolean.TRUE,

            "Label.font", new FontLazyValue(Region.LABEL), 

            "List.focusCellHighlightBorder", focusBorder,
            "List.focusSelectedCellHighlightBorder", focusSelectedBorder,
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
	    "List.focusInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		             "LEFT", "selectNextColumn",
		          "KP_LEFT", "selectNextColumn",
		       "shift LEFT", "selectNextColumnExtendSelection",
		    "shift KP_LEFT", "selectNextColumnExtendSelection",
                  "ctrl shift LEFT", "selectNextColumnExtendSelection",
               "ctrl shift KP_LEFT", "selectNextColumnExtendSelection",
                        "ctrl LEFT", "selectNextColumnChangeLead",
                     "ctrl KP_LEFT", "selectNextColumnChangeLead",
		            "RIGHT", "selectPreviousColumn",
		         "KP_RIGHT", "selectPreviousColumn",
		      "shift RIGHT", "selectPreviousColumnExtendSelection",
		   "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                 "ctrl shift RIGHT", "selectPreviousColumnExtendSelection",
              "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                       "ctrl RIGHT", "selectPreviousColumnChangeLead",
                    "ctrl KP_RIGHT", "selectPreviousColumnChangeLead",
		 }),
            "List.font", new FontLazyValue(Region.LIST),


 	    "Menu.shortcutKeys", new int[] {KeyEvent.ALT_MASK},
            "Menu.arrowIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getMenuArrowIcon"),
            "Menu.font", new FontLazyValue(Region.MENU),
            "Menu.margin", zeroInsets,


	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },
            "MenuBar.font", new FontLazyValue(Region.MENU_BAR),


            "MenuItem.arrowIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getMenuItemArrowIcon"),
            "MenuItem.font", new FontLazyValue(Region.MENU_ITEM),
            "MenuItem.margin", zeroInsets,


            "OptionPane.setButtonMargin", Boolean.FALSE,
            "OptionPane.sameSizeButtons", Boolean.TRUE,
            "OptionPane.buttonOrientation", new Integer(SwingConstants.RIGHT),
            "OptionPane.minimumSize", new DimensionUIResource(262, 90),
            "OptionPane.buttonPadding", new Integer(10),
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },
	    "OptionPane.buttonClickThreshhold", new Integer(500),
            "OptionPane.isYesLast", Boolean.TRUE,
            "OptionPane.font", new FontLazyValue(Region.OPTION_PANE),

            "Panel.font", new FontLazyValue(Region.PANEL),

            "PasswordField.caretForeground", caretColor,
            "PasswordField.caretAspectRatio", defaultCaretAspectRatio,
            "PasswordField.caretBlinkRate", caretBlinkRate,
            "PasswordField.margin", zeroInsets,
            "PasswordField.focusInputMap", passwordInputMap,
            "PasswordField.font", new FontLazyValue(Region.PASSWORD_FIELD),


            "PopupMenu.consumeEventOnClose", Boolean.TRUE,
	    "PopupMenu.selectedWindowInputMapBindings", new Object[] {
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
	    "PopupMenu.selectedWindowInputMapBindings.RightToLeft",
                  new Object[] {
		    "LEFT", "selectChild",
		 "KP_LEFT", "selectChild",
		   "RIGHT", "selectParent",
		"KP_RIGHT", "selectParent",
	    },
            "PopupMenu.font", new FontLazyValue(Region.POPUP_MENU),

            "ProgressBar.horizontalSize", new DimensionUIResource(146, 16),
            "ProgressBar.verticalSize", new DimensionUIResource(16, 146),
            "ProgressBar.font", new FontLazyValue(Region.PROGRESS_BAR),

	    "RadioButton.focusInputMap",
                   new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released",
                           "RETURN", "pressed"
	           }),
            "RadioButton.icon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getRadioButtonIcon"),
            "RadioButton.font", new FontLazyValue(Region.RADIO_BUTTON),


            "RadioButtonMenuItem.arrowIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getRadioButtonMenuItemArrowIcon"),
            "RadioButtonMenuItem.checkIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getRadioButtonMenuItemCheckIcon"),
            "RadioButtonMenuItem.font", new FontLazyValue(Region.RADIO_BUTTON_MENU_ITEM), 
            "RadioButtonMenuItem.margin", zeroInsets,

            // These bindings are only enabled when there is a default
            // button set on the rootpane.
            "RootPane.defaultButtonWindowKeyBindings", new Object[] {
		               "ENTER", "press",
		      "released ENTER", "release",
		          "ctrl ENTER", "press",
                 "ctrl released ENTER", "release"
            },


            "ScrollBar.squareButtons", Boolean.TRUE,
            "ScrollBar.thumbHeight", new Integer(14),
            "ScrollBar.width", new Integer(16),
            "ScrollBar.minimumThumbSize", new Dimension(8, 8),
            "ScrollBar.maximumThumbSize", new Dimension(4096, 4096),
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
            "ScrollBar.ancestorInputMap.RightToLeft",
                    new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "negativeUnitIncrement",
		    "KP_RIGHT", "negativeUnitIncrement",
		        "LEFT", "positiveUnitIncrement",
		     "KP_LEFT", "positiveUnitIncrement",
                    }),


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
            "ScrollPane.ancestorInputMap.RightToLeft",
                    new UIDefaults.LazyInputMap(new Object[] {
		    "ctrl PAGE_UP", "scrollRight",
		  "ctrl PAGE_DOWN", "scrollLeft",
                    }),
            "ScrollPane.font", new FontLazyValue(Region.SCROLL_PANE),


            "Separator.insets", zeroInsets,
            "Separator.thickness", new Integer(2),


            "Slider.paintValue", Boolean.TRUE,
            "Slider.thumbWidth", new Integer(30),
            "Slider.thumbHeight", new Integer(14),
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
            "Slider.focusInputMap.RightToLeft",
                    new UIDefaults.LazyInputMap(new Object[] {
                            "RIGHT", "negativeUnitIncrement",
                         "KP_RIGHT", "negativeUnitIncrement",
                             "LEFT", "positiveUnitIncrement",
                          "KP_LEFT", "positiveUnitIncrement",
                         }),


            "Spinner.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                               "UP", "increment",
                            "KP_UP", "increment",
                             "DOWN", "decrement",
                          "KP_DOWN", "decrement",
               }),
            "Spinner.font", new FontLazyValue(Region.SPINNER),


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


            "SplitPane.size", new Integer(7),
            "SplitPane.oneTouchOffset", new Integer(2),
            "SplitPane.oneTouchButtonSize", new Integer(5),
            "SplitPane.supportsOneTouchButtons", Boolean.FALSE,


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
			 "SPACE", "selectTabWithFocus"
		}),
	    "TabbedPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		         "ctrl TAB", "navigateNext",
		   "ctrl shift TAB", "navigatePrevious",
		   "ctrl PAGE_DOWN", "navigatePageDown",
	             "ctrl PAGE_UP", "navigatePageUp",
	                  "ctrl UP", "requestFocus",
	               "ctrl KP_UP", "requestFocus",
		 }),

            "TabbedPane.selectionFollowsFocus", Boolean.FALSE,
            "TabbedPane.font", new FontLazyValue(Region.TABBED_PANE),

            "Table.focusCellBackground", tableFocusCellBg,
            "Table.focusCellForeground", tableFocusCellFg,
            "Table.focusCellHighlightBorder", focusBorder,
            "Table.focusSelectedCellHighlightBorder", focusSelectedBorder,
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
            "Table.ancestorInputMap.RightToLeft",
                    new UIDefaults.LazyInputMap(new Object[] {
                                "RIGHT", "selectPreviousColumn",
                             "KP_RIGHT", "selectPreviousColumn",
                          "shift RIGHT", "selectPreviousColumnExtendSelection",
                       "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                     "ctrl shift RIGHT", "selectPreviousColumnExtendSelection",
                  "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                          "shift RIGHT", "selectPreviousColumnChangeLead",
                       "shift KP_RIGHT", "selectPreviousColumnChangeLead",
                                 "LEFT", "selectNextColumn",
                              "KP_LEFT", "selectNextColumn",
                           "shift LEFT", "selectNextColumnExtendSelection",
                        "shift KP_LEFT", "selectNextColumnExtendSelection",
                      "ctrl shift LEFT", "selectNextColumnExtendSelection",
                   "ctrl shift KP_LEFT", "selectNextColumnExtendSelection",
                            "ctrl LEFT", "selectNextColumnChangeLead",
                         "ctrl KP_LEFT", "selectNextColumnChangeLead",
                         "ctrl PAGE_UP", "scrollRightChangeSelection",
                       "ctrl PAGE_DOWN", "scrollLeftChangeSelection",
                   "ctrl shift PAGE_UP", "scrollRightExtendSelection",
                 "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection",
                    }),
            "Table.font", new FontLazyValue(Region.TABLE),
            
            "TableHeader.font", new FontLazyValue(Region.TABLE_HEADER),

            "TextArea.caretForeground", caretColor,
            "TextArea.caretAspectRatio", defaultCaretAspectRatio,
            "TextArea.caretBlinkRate", caretBlinkRate,
            "TextArea.margin", zeroInsets,
            "TextArea.focusInputMap", multilineInputMap,
            "TextArea.font", new FontLazyValue(Region.TEXT_AREA),


            "TextField.caretForeground", caretColor,
            "TextField.caretAspectRatio", defaultCaretAspectRatio,
            "TextField.caretBlinkRate", caretBlinkRate,
            "TextField.margin", zeroInsets,
            "TextField.focusInputMap", fieldInputMap,
            "TextField.font", new FontLazyValue(Region.TEXT_FIELD),


            "TextPane.caretForeground", caretColor,
            "TextPane.caretAspectRatio", defaultCaretAspectRatio,
            "TextPane.caretBlinkRate", caretBlinkRate,
            "TextPane.margin", editorMargin,
            "TextPane.focusInputMap", multilineInputMap,
            "TextPane.font", new FontLazyValue(Region.TEXT_PANE),


            "TitledBorder.titleColor", new ColorUIResource(Color.BLACK),
            "TitledBorder.border", new UIDefaults.ProxyLazyValue(
                      "javax.swing.plaf.BorderUIResource",
                      "getEtchedBorderUIResource"),

	    "ToggleButton.focusInputMap",
                   new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	           }),
            "ToggleButton.font", new FontLazyValue(Region.TOGGLE_BUTTON),


            "ToolBar.separatorSize", new DimensionUIResource(10, 10),
            "ToolBar.handleIcon", new UIDefaults.ActiveValue() {
                public Object createValue(UIDefaults table) {
                    return GTKIconFactory.getToolBarHandleIcon();
                }
            },
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
            "ToolBar.font", new FontLazyValue(Region.TOOL_BAR),

            "ToolTip.font", new FontLazyValue(Region.TOOL_TIP),

            "Tree.padding", new Integer(4),
            "Tree.drawHorizontalLines", Boolean.FALSE,
            "Tree.drawVerticalLines", Boolean.FALSE,
            "Tree.rowHeight", new Integer(-1),
            "Tree.scrollsOnExpand", Boolean.FALSE,
            "Tree.expanderSize", new Integer(10),
	    "Tree.closedIcon", null,
	    "Tree.leafIcon", null,
	    "Tree.openIcon", null,
            "Tree.expandedIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getTreeExpandedIcon"),
            "Tree.collapsedIcon", new GTKStyle.GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getTreeCollapsedIcon"),
            "Tree.leftChildIndent", new Integer(2),
            "Tree.rightChildIndent", new Integer(12),
            "Tree.scrollsHorizontallyAndVertically", Boolean.FALSE,
            "Tree.drawsFocusBorder", Boolean.TRUE,
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
                                "typed +", "expand",
                                "typed -", "collapse",
                             "BACK_SPACE", "moveSelectionToParent",
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
            "Tree.focusInputMap.RightToLeft",
                    new UIDefaults.LazyInputMap(new Object[] {
		                  "RIGHT", "selectParent",
		               "KP_RIGHT", "selectParent",
		                   "LEFT", "selectChild",
		                "KP_LEFT", "selectChild",
		 }),
            "Tree.ancestorInputMap",
                      new UIDefaults.LazyInputMap(new Object[] {
		         "ESCAPE", "cancel"
                      }),
            "Tree.font", new FontLazyValue(Region.TREE),
            
            "Viewport.font", new FontLazyValue(Region.VIEWPORT)
        };
	table.putDefaults(defaults);

        if (fallbackFont != null) {
            table.put("TitledBorder.font", fallbackFont);
        }
    }

    protected void initSystemColorDefaults(UIDefaults table) {
        GTKStyleFactory factory = (GTKStyleFactory)getStyleFactory();
        GTKStyle windowStyle = (GTKStyle)factory.getStyle("GtkWindow");
        table.put("window", windowStyle.getGTKColor(SynthConstants.ENABLED,
                                                    GTKColorType.BACKGROUND));
        table.put("windowText", windowStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.TEXT_FOREGROUND));

        GTKStyle entryStyle = (GTKStyle)factory.getStyle("GtkEntry");
        table.put("text", entryStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.TEXT_BACKGROUND));
        table.put("textText", entryStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.TEXT_FOREGROUND));
        table.put("textHighlight",
                  entryStyle.getGTKColor(SynthConstants.SELECTED,
                                         GTKColorType.TEXT_BACKGROUND));
        table.put("textHighlightText",
                  entryStyle.getGTKColor(SynthConstants.SELECTED,
                                         GTKColorType.TEXT_FOREGROUND));
        table.put("textInactiveText",
                  entryStyle.getGTKColor(SynthConstants.DISABLED,
                                         GTKColorType.TEXT_FOREGROUND));
        Object caretColor =
            entryStyle.getClassSpecificValue(Region.TEXT_FIELD, "cursor-color");
        if (caretColor == null) {
            caretColor = GTKStyle.BLACK_COLOR;
        }
        table.put("caretColor", caretColor);
        
        GTKStyle widgetStyle = (GTKStyle)factory.getStyle("GtkWidget");
        table.put("control", widgetStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.BACKGROUND));
        table.put("controlText", widgetStyle.getGTKColor(
                      SynthConstants.ENABLED,
                      GTKColorType.TEXT_FOREGROUND));
        
        table.put("controlHighlight", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.BACKGROUND));
        table.put("controlLtHighlight", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.LIGHT));
        table.put("controlShadow", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.DARK));
        table.put("controlDkShadow", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.BLACK));
        
        
        GTKStyle menuStyle = (GTKStyle)factory.getStyle("GtkMenuItem");
        table.put("menu", menuStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.BACKGROUND));
        table.put("menuText", menuStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.TEXT_FOREGROUND));
        
        GTKStyle scrollbarStyle = (GTKStyle)factory.getStyle("GtkScrollbar");
        table.put("scrollbar", scrollbarStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.BACKGROUND));
        
        GTKStyle infoStyle = (GTKStyle)factory.getStyle("GtkMessageDialog");
        table.put("info", scrollbarStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.BACKGROUND));
        table.put("infoText", scrollbarStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.TEXT_FOREGROUND));
        
        GTKStyle desktopStyle = (GTKStyle)factory.getStyle("GtkContainer");
        table.put("desktop", scrollbarStyle.getGTKColor(SynthConstants.ENABLED,
                                           GTKColorType.BACKGROUND));
        
        // colors specific only for GTK
        table.put("light", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.LIGHT));
        table.put("mid", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.MID));
        table.put("dark", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.DARK));
        table.put("black", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.BLACK));
        table.put("white", widgetStyle.getGTKColor(
                SynthConstants.ENABLED, GTKColorType.WHITE));
    }

    /**
     * Creates the GTK look and feel class for the passed in Component.
     */
    public static ComponentUI createUI(JComponent c) {
        String key = c.getUIClassID().intern();

        if (key == "FileChooserUI") {
            return GTKFileChooserUI.createUI(c);
	}
        return SynthLookAndFeel.createUI(c);
    }

    /**
     * Updates the <code>aaText</code> field.
     */
    static void updateAAText() {
        if (!cjkLocale) {
            Object aaValue = Toolkit.getDefaultToolkit().
                getDesktopProperty("gnome.Xft/Antialias");
            aaText = Boolean.valueOf(((aaValue instanceof Number) &&
                                      ((Number)aaValue).intValue() == 1));
        }
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    private static boolean isLocalDisplay() {
        try {
            Class x11Class = Class.forName("sun.awt.X11GraphicsEnvironment");
            Method isDisplayLocalMethod = x11Class.getMethod(
                      "isDisplayLocal", new Class[0]);
            return (Boolean)isDisplayLocalMethod.invoke(null, null);
        } catch (NoSuchMethodException nsme) {
        } catch (ClassNotFoundException cnfe) {
        } catch (IllegalAccessException iae) {
        } catch (InvocationTargetException ite) {
        }
        // If we get here we're most likely being run on Windows, return
        // false.
        return false;
    }


    public void initialize() {
        super.initialize();
        inInitialize = true;
        loadStylesFromThemeFiles();
        inInitialize = false;

        Toolkit kit = Toolkit.getDefaultToolkit();
        WeakPCL pcl = new WeakPCL(this, kit, "gnome.Net/ThemeName");
        kit.addPropertyChangeListener(pcl.getKey(), pcl);
        pcl = new WeakPCL(this, kit, "gnome.Gtk/FontName");
        kit.addPropertyChangeListener(pcl.getKey(), pcl);
        pcl = new WeakPCL(this, kit, "gnome.Xft/DPI");
        kit.addPropertyChangeListener(pcl.getKey(), pcl);

        String language = Locale.getDefault().getLanguage();
        cjkLocale = 
            (Locale.CHINESE.getLanguage().equals(language) ||
             Locale.JAPANESE.getLanguage().equals(language) ||
             Locale.KOREAN.getLanguage().equals(language));

        if (isLocalDisplay()) {
            pcl = new WeakPCL(this, kit, "gnome.Xft/Antialias");
            kit.addPropertyChangeListener(pcl.getKey(), pcl);
            updateAAText();
        }
        flushUnreferenced();
    }

    static ReferenceQueue queue = new ReferenceQueue();

    static void flushUnreferenced() {
        WeakPCL pcl;

        while ((pcl = (WeakPCL)queue.poll()) != null) {
            pcl.dispose();
        }
    }

    static class WeakPCL extends WeakReference implements
            PropertyChangeListener {
        private Toolkit kit;
        private String key;

        WeakPCL(Object target, Toolkit kit, String key) {
            super(target, queue);
            this.kit = kit;
            this.key = key;
        }

        public String getKey() { return key; }

        public void propertyChange(final PropertyChangeEvent pce) {
            final GTKLookAndFeel lnf = (GTKLookAndFeel)get();

            if (lnf == null || UIManager.getLookAndFeel() != lnf) { 
                // The property was GC'ed, we're no longer interested in
                // PropertyChanges, remove the listener.
                dispose();
            }
            else {
                // We are using invokeLater here because we are getting called
                // on the AWT-Motif thread which can cause a deadlock.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if ("gnome.Xft/Antialias".equals(
                                                  pce.getPropertyName())) {
                            updateAAText();
                        }
                        lnf.loadStylesFromThemeFiles();
                        Frame appFrames[] = Frame.getFrames();
                        for (int i = 0; i < appFrames.length; i++) {
                            SynthLookAndFeel.updateStyles(appFrames[i]);
                        }
                    }
                });
            }
        }

        void dispose() {
            kit.removePropertyChangeListener(key, this);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
	loadStylesFromThemeFiles();
        Frame appFrames[] = Frame.getFrames();
        for (int i = 0; i < appFrames.length; i++) {
            SynthLookAndFeel.updateStyles(appFrames[i]);
        }
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public String getDescription() {
        return "GTK look and feel";
    }

    public String getName() {
        return "GTK look and feel";
    }

    public String getID() {
        return "GTK";
    }

    // Subclassed to pass in false to the superclass, we don't want to try
    // and load the system colors.
    protected void loadSystemColors(UIDefaults table, String[] systemColors, boolean useNative) {
        super.loadSystemColors(table, systemColors, false);
    }

    private void loadStylesFromThemeFiles() {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                GTKParser parser = new GTKParser();

                // GTK rc file parsing:
                // First, attempts to load the file specified in the
                // swing.gtkthemefile system property.
                // RC files come from one of the following locations:
                // 1 - environment variable GTK2_RC_FILES, which is colon
                //     separated list of rc files or
                // 2 - SYSCONFDIR/gtk-2.0/gtkrc and ~/.gtkrc-2.0
                // 
                // Additionally the default Theme file is parsed last. The default
                // theme name comes from the desktop property gnome.Net/ThemeName
                //     Default theme is looked for in ~/.themes/THEME/gtk-2.0/gtkrc
                //     and env variable GTK_DATA_PREFIX/THEME/gtkrc or
                //     GTK_DATA_PREFIX/THEME/gtk-2.0/gtkrc
                //     (or compiled GTK_DATA_PREFIX) GTK_DATA_PREFIX is
                //     /usr/share/themes on debian,
                //     /usr/sfw/share/themes on Solaris.
                // Lastly key bindings are supposed to come from a different theme
                // with the path built as above, using the desktop property
                // named gnome.Gtk/KeyThemeName.

                // Try system property override first:
                String filename = System.getProperty("swing.gtkthemefile");
                String sep = File.separator;

                if (filename == null || !parseThemeFile(filename, parser)) {
	            // Try to load user's theme first
	            String userHome = System.getProperty("user.home");
	            if (userHome != null) {
                        parseThemeFile(userHome + sep + ".gtkrc-2.0", parser);
	            }
	            // Now try to load "Default" theme
	            String themeName = (String)Toolkit.getDefaultToolkit().
                        getDesktopProperty("gnome.Net/ThemeName");
	            if (themeName == null) {
	        	themeName = "Default";
	            }
                    String[] dirs = new String[] {
                        userHome + "/.themes",
                        System.getProperty("swing.gtkthemedir"),
                        "/usr/share/themes" // Debian/Redhat/Solaris/SuSE
                    };

                    String themeDirName = null;
                    // Find the first existing rc file in the list.
                    for (int i = 0; i < dirs.length; i++) {
                        if (dirs[i] == null) {
                            continue;
                        }

                        if (new File(dirs[i] + sep + themeName + sep +
                                    "gtk-2.0" + sep + "gtkrc").canRead()) {
                            themeDirName = dirs[i];
                            break;
                        }
                    }

                    if (themeDirName != null) {
                        parseThemeFile(themeDirName + sep + themeName + sep +
                                "gtk-2.0" + sep + "gtkrc", parser);
                    }
	        }
                setStyleFactory(handleParsedData(parser));
                parser.clearParser();
		return null;
	    }
	});
        // If we are in initialize initializations will be
        // called later, don't do it now.
        if (!inInitialize) {
            UIDefaults table = UIManager.getLookAndFeelDefaults();
            initSystemColorDefaults(table);
            initComponentDefaults(table);            
        }
    }

    private boolean parseThemeFile(String fileName, GTKParser parser) {
        File file = new File(fileName);
	if (file.exists()) {
            try {
                parser.parseFile(file, fileName);
            } catch (IOException ioe) {
                System.err.println("error: (" + ioe.toString()
                                   + ") while parsing file: \""
                                   + fileName
                                   + "\"");
            }
            return true;
	}
        return false; // file doesn't exist
    }

    /**
     * This method is responsible for handling the data that was parsed.
     * One of it's jobs is to fetch and deal with the GTK settings stored
     * in the parser. It's other job is to create a style factory with an
     * appropriate default style, load into it the styles from the parser,
     * and return that factory.
     */
    private GTKStyleFactory handleParsedData(GTKParser parser) {
        HashMap settings = parser.getGTKSettings();

        /*
         * The following is a list of the settings that GTK supports and their meanings.
         * Currently, we only support a subset ("gtk-font-name" and "gtk-icon-sizes"):
         *
         *   "gtk-can-change-accels"     : Whether menu accelerators can be changed
         *                                 by pressing a key over the menu item.
         *   "gtk-color-palette"         : Palette to use in the color selector.
         *   "gtk-cursor-blink"          : Whether the cursor should blink.
         *   "gtk-cursor-blink-time"     : Length of the cursor blink cycle, in milleseconds.
         *   "gtk-dnd-drag-threshold"    : Number of pixels the cursor can move before dragging.
         *   "gtk-double-click-time"     : Maximum time allowed between two clicks for them
         *                                 to be considered a double click (in milliseconds).
         *   "gtk-entry-select-on-focus" : Whether to select the contents of an entry when it
         *                                 is focused.
         *   "gtk-font-name"             : Name of default font to use.
         *   "gtk-icon-sizes"            : List of icon sizes (gtk-menu=16,16:gtk-button=20,20...
         *   "gtk-key-theme-name"        : Name of key theme RC file to load.
         *   "gtk-menu-bar-accel"        : Keybinding to activate the menu bar.
         *   "gtk-menu-bar-popup-delay"  : Delay before the submenus of a menu bar appear.
         *   "gtk-menu-popdown-delay"    : The time before hiding a submenu when the pointer is
         *                                 moving towards the submenu.
         *   "gtk-menu-popup-delay"      : Minimum time the pointer must stay over a menu item
         *                                 before the submenu appear.
         *   "gtk-split-cursor"          : Whether two cursors should be displayed for mixed
         *                                 left-to-right and right-to-left text.
         *   "gtk-theme-name"            : Name of theme RC file to load.
         *   "gtk-toolbar-icon-size"     : Size of icons in default toolbars.
         *   "gtk-toolbar-style"         : Whether default toolbars have text only, text and icons,
         *                                 icons only, etc.
         */

        Object iconSizes = settings.get("gtk-icon-sizes");
        if (iconSizes instanceof String) {
            if (!configIconSizes((String)iconSizes)) {
                System.err.println("Error parsing gtk-icon-sizes string: '" + iconSizes + "'");
            }
        }

        // Desktop property appears to have preference over rc font.
        Object fontName = Toolkit.getDefaultToolkit().getDesktopProperty(
                                  "gnome.Gtk/FontName");

        if (!(fontName instanceof String)) {
            fontName = settings.get("gtk-font-name");
            if (!(fontName instanceof String)) {
                fontName = "sans 10";
            }
        }
        Font defaultFont = PangoFonts.lookupFont((String)fontName);
        GTKStyle defaultStyle = new GTKStyle(defaultFont);
        GTKStyleFactory factory = new GTKStyleFactory(defaultStyle);

        parser.loadStylesInto(factory);
        fallbackFont = defaultFont;
        return factory;
    }

    private boolean configIconSizes(String sizeString) {
        String[] sizes = sizeString.split(":");
        for (int i = 0; i < sizes.length; i++) {
            String[] splits = sizes[i].split("=");

            if (splits.length != 2) {
                return false;
            }
            
            String size = splits[0].trim().intern();
            if (size.length() < 1) {
                return false;
            }

            splits = splits[1].split(",");
            
            if (splits.length != 2) {
                return false;
            }
            
            String width = splits[0].trim();
            String height = splits[1].trim();
            
            if (width.length() < 1 || height.length() < 1) {
                return false;
            }

            int w = 0;
            int h = 0;

            try {
                w = Integer.parseInt(width);
                h = Integer.parseInt(height);
            } catch (NumberFormatException nfe) {
                return false;
            }

            if (w > 0 && h > 0) {
                int type = GTKStyle.GTKStockIconInfo.getIconType(size); 
                GTKStyle.GTKStockIconInfo.setIconSize(type, w, h);
            } else {
                System.err.println("Invalid size in gtk-icon-sizes: " + w + "," + h);
            }
        }
        
        return true;
    }

    /**
     * Returns whether or not the UIs should update their
     * <code>SynthStyles</code> from the <code>SynthStyleFactory</code>
     * when the ancestor of the Component changes.
     *
     * @return whether or not the UIs should update their
     * <code>SynthStyles</code> from the <code>SynthStyleFactory</code>
     * when the ancestor changed.
     */
    public boolean shouldUpdateStyleOnAncestorChanged() {
        return true;
    }
}
