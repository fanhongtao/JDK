/*
 * @(#)GTKLookAndFeel.java	1.55 03/05/08
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.DefaultEditorKit;
import java.io.IOException;

/**
 * @version 1.55, 05/08/03
 * @author Scott Violet
 */
public class GTKLookAndFeel extends SynthLookAndFeel {
    /**
     * Font to use in places where there is no widget.
     */
    private Font fallbackFont;

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

	table.put("FileChooserUI", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

        table.addResourceBundle(
              "com.sun.java.swing.plaf.gtk.resources.gtk" );

        Object tempBorder = new GTKStyle.GTKLazyValue(
            "com.sun.java.swing.plaf.gtk.GTKPainter$ListTableFocusBorder");
        table.put("List.focusCellHighlightBorder", tempBorder);
        table.put("Table.focusCellHighlightBorder", tempBorder);

        // These exist for better backward compatability with existing apps
        // that use these directly and don't check for null.
        table.put("Table.gridColor", new ColorUIResource(Color.gray));
        table.put("Table.selectionForeground",
                  new ColorUIResource(Color.BLACK));
        table.put("Table.selectionBackground", new ColorUIResource(0x000080));
        table.put("control", new ColorUIResource(0xC0C0C0));

        if (fallbackFont != null) {
            table.put("TitledBorder.font", fallbackFont);
        }
        table.put("TitledBorder.titleColor", new ColorUIResource(Color.BLACK));
        table.put("TitledBorder.border", new UIDefaults.ProxyLazyValue(
                      "javax.swing.plaf.BorderUIResource",
                      "getEtchedBorderUIResource"));

        return table;
    }


    /**
     * Creates the GTK look and feel class for the passed in Component.
     */
    public static ComponentUI createUI(JComponent c) {
        String key = c.getUIClassID().intern();

        if (key == "FileChooserUI") {
            return GTKFileChooserUI.createUI(c);
	}
        // PENDING: this is only necessary while gtk and synth are in the
        // same package.
        return SynthLookAndFeel.createUI(c);
    }

    public void initialize() {
	loadStylesFromThemeFiles();
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
                if (filename == null || !parseThemeFile(filename, parser)) {
	            // Try to load user's theme first
	            String userHome = System.getProperty("user.home");
	            if (userHome != null) {
                        parseThemeFile(userHome + "/.gtkrc-2.0", parser);
	            }
	            // Now try to load "Default" theme
	            String themeName = (String)Toolkit.getDefaultToolkit().
                        getDesktopProperty("gnome.Net/ThemeName");
	            if (themeName == null) {
	        	themeName = "Default";
	            }
                    if (!parseThemeFile(userHome + "/.themes/" + themeName +
                                "/gtk-2.0/gtkrc", parser)) {
                        String themeDirName =
                            System.getProperty("swing.gtkthemedir");
                        if (themeDirName == null) {
                            String[] dirs = new String[] {
                                "/usr/share/themes", // Redhat/Debian/Solaris
                                "/opt/gnome2/share/themes" // SUSE
                            };

                            // Find the first existing rc file in the list.
                            for (int i = 0; i < dirs.length; i++) {
                                if (new File(dirs[i] + "/" + themeName +
                                        "/gtk-2.0/gtkrc").canRead()) {
                                    themeDirName = dirs[i];
                                    break;
                                }
                            }
                        }

                        if (themeDirName != null) {
	        	    parseThemeFile(themeDirName + "/" + themeName +
                                    "/gtk-2.0/gtkrc", parser);
                        }
                    }
	        }

                setStyleFactory(handleParsedData(parser));

                parser.clearParser();

		return null;
	    }
	});
    }

    private boolean parseThemeFile(String fileName, GTKParser parser) {
        File file = new File(fileName);
	if (file.canRead()) {
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
                // Only allow resizing of existing icon sizes.
                if (GTKStyle.getIconSize(size) != null) {
                    GTKStyle.setIconSize(size, w, h);
                }
            } else {
                System.err.println("Invalid size in gtk-icon-sizes: " + w + "," + h);
            }
        }
        
        return true;
    }

}
