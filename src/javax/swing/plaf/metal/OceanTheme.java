/*
 * @(#)OceanTheme.java	1.16 05/01/04
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.*;
import com.sun.java.swing.SwingUtilities2;
import sun.swing.PrintColorUIResource;

/**
 * This class provides an updated look for applications using
 * <code>MetalLookAndFeel<code>. The designers of the Metal
 * Look and Feel strive to keep the default look up to date,
 * possibly through the use of new themes in the future.
 * Therefore, developers should only use this class directly
 * when they wish to customize the "Ocean" look, or force
 * it to be the current theme, regardless of future updates.
 *
 * @version 1.16 01/04/05
 * @since 1.5
 * @see MetalLookAndFeel#setCurrentTheme
 */
public class OceanTheme extends DefaultMetalTheme {
    private static final ColorUIResource PRIMARY1 =
                              new ColorUIResource(0x6382BF);
    private static final ColorUIResource PRIMARY2 =
                              new ColorUIResource(0xA3B8CC);
    private static final ColorUIResource PRIMARY3 =
                              new ColorUIResource(0xB8CFE5);
    private static final ColorUIResource SECONDARY1 =
                              new ColorUIResource(0x7A8A99);
    private static final ColorUIResource SECONDARY2 =
                              new ColorUIResource(0xB8CFE5);
    private static final ColorUIResource SECONDARY3 =
                              new ColorUIResource(0xEEEEEE);

    private static final ColorUIResource CONTROL_TEXT_COLOR =
                              new PrintColorUIResource(0x333333, Color.BLACK);
    private static final ColorUIResource INACTIVE_CONTROL_TEXT_COLOR =
                              new ColorUIResource(0x999999);
    private static final ColorUIResource MENU_DISABLED_FOREGROUND =
                              new ColorUIResource(0x999999);
    private static final ColorUIResource OCEAN_BLACK =
                              new PrintColorUIResource(0x333333, Color.BLACK);

    // ComponentOrientation Icon
    // Delegates to different icons based on component orientation
    private static class COIcon extends IconUIResource {
        private Icon rtl;

        public COIcon(Icon ltr, Icon rtl) {
            super(ltr);
            this.rtl = rtl;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {        
            if (MetalUtils.isLeftToRight(c)) {
                super.paintIcon(c, g, x, y);
            } else {
                rtl.paintIcon(c, g, x, y);
            }
        }
    }

    // InternalFrame Icon
    // Delegates to different icons based on button state
    private static class IFIcon extends IconUIResource {
        private Icon pressed;

        public IFIcon(Icon normal, Icon pressed) {
            super(normal);
            this.pressed = pressed;
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            ButtonModel model = ((AbstractButton)c).getModel();
            if (model.isPressed() && model.isArmed()) {
                pressed.paintIcon(c, g, x, y);
            } else {
                super.paintIcon(c, g, x, y);
            }
        }
    }

    /**
     * Construct an instance of <code>OceanTheme</code>
     */
    public OceanTheme() {
    }

    /**
     * Add this theme's custom entries to the defaults table.
     *
     * @param table the defaults table, non-null
     * @throws NullPointerException if the parameter is null
     */
    public void addCustomEntriesToTable(UIDefaults table) {
        Object focusBorder = new UIDefaults.ProxyLazyValue(
                      "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
                      new Object[] {getPrimary1()});
        // .30 0 DDE8F3 white secondary2
        java.util.List buttonGradient = Arrays.asList(
                 new Object[] {new Float(.3f), new Float(0f),
                 new ColorUIResource(0xDDE8F3), getWhite(), getSecondary2() });

        // Other possible properties that aren't defined:
        //
        // Used when generating the disabled Icons, provides the region to
        // constrain grays to.
        // Button.disabledGrayRange -> Object[] of Integers giving min/max
        // InternalFrame.inactiveTitleGradient -> Gradient when the
        //   internal frame is inactive.
        Color cccccc = new ColorUIResource(0xCCCCCC);
        Color dadada = new ColorUIResource(0xDADADA);
        Color c8ddf2 = new ColorUIResource(0xC8DDF2);
        Object directoryIcon = getIconResource("icons/ocean/directory.gif");
        Object fileIcon = getIconResource("icons/ocean/file.gif");
        java.util.List sliderGradient = Arrays.asList(new Object[] {
            new Float(.3f), new Float(.2f),
            c8ddf2, getWhite(), new ColorUIResource(SECONDARY2) });

        Object[] defaults = new Object[] {
            "Button.gradient", buttonGradient,
            "Button.rollover", Boolean.TRUE,
            "Button.toolBarBorderBackground", INACTIVE_CONTROL_TEXT_COLOR, 
            "Button.disabledToolBarBorderBackground", cccccc,
            "Button.rolloverIconType", "ocean",

            "CheckBox.rollover", Boolean.TRUE,
            "CheckBox.gradient", buttonGradient,

            "CheckBoxMenuItem.gradient", buttonGradient,

            // home2
            "FileChooser.homeFolderIcon",
                 getIconResource("icons/ocean/homeFolder.gif"),
            // directory2
            "FileChooser.newFolderIcon",
                 getIconResource("icons/ocean/newFolder.gif"),
            // updir2
            "FileChooser.upFolderIcon",
                 getIconResource("icons/ocean/upFolder.gif"),

            // computer2
            "FileView.computerIcon",
                 getIconResource("icons/ocean/computer.gif"),
            "FileView.directoryIcon", directoryIcon,
            // disk2
            "FileView.hardDriveIcon",
                 getIconResource("icons/ocean/hardDrive.gif"),
            "FileView.fileIcon", fileIcon,
            // floppy2
            "FileView.floppyDriveIcon",
                 getIconResource("icons/ocean/floppy.gif"),

            "Label.disabledForeground", getInactiveControlTextColor(),
            
            "Menu.opaque", Boolean.FALSE,

            "MenuBar.gradient", Arrays.asList(new Object[] {
                     new Float(1f), new Float(0f),
                     getWhite(), dadada, 
                     new ColorUIResource(dadada) }),
            "MenuBar.borderColor", cccccc,

            "InternalFrame.activeTitleGradient", buttonGradient,
            // close2
            "InternalFrame.closeIcon",
                     new UIDefaults.LazyValue() {
                         public Object createValue(UIDefaults table) {
                             return new IFIcon(getHastenedIcon("icons/ocean/close.gif", table),
                                               getHastenedIcon("icons/ocean/close-pressed.gif", table));
                         }
                     },
            // minimize
            "InternalFrame.iconifyIcon",
                     new UIDefaults.LazyValue() {
                         public Object createValue(UIDefaults table) {
                             return new IFIcon(getHastenedIcon("icons/ocean/iconify.gif", table),
                                               getHastenedIcon("icons/ocean/iconify-pressed.gif", table));
                         }
                     },
            // restore
            "InternalFrame.minimizeIcon",
                     new UIDefaults.LazyValue() {
                         public Object createValue(UIDefaults table) {
                             return new IFIcon(getHastenedIcon("icons/ocean/minimize.gif", table),
                                               getHastenedIcon("icons/ocean/minimize-pressed.gif", table));
                         }
                     },
            // menubutton3
            "InternalFrame.icon",
                     getIconResource("icons/ocean/menu.gif"),
            // maximize2
            "InternalFrame.maximizeIcon",
                     new UIDefaults.LazyValue() {
                         public Object createValue(UIDefaults table) {
                             return new IFIcon(getHastenedIcon("icons/ocean/maximize.gif", table),
                                               getHastenedIcon("icons/ocean/maximize-pressed.gif", table));
                         }
                     },
            // paletteclose
            "InternalFrame.paletteCloseIcon",
                     new UIDefaults.LazyValue() {
                         public Object createValue(UIDefaults table) {
                             return new IFIcon(getHastenedIcon("icons/ocean/paletteClose.gif", table),
                                               getHastenedIcon("icons/ocean/paletteClose-pressed.gif", table));
                         }
                     },

            "List.focusCellHighlightBorder", focusBorder,

            "MenuBarUI", "javax.swing.plaf.metal.MetalMenuBarUI",

            "OptionPane.errorIcon",
                   getIconResource("icons/ocean/error.png"),
            "OptionPane.informationIcon",
                   getIconResource("icons/ocean/info.png"),
            "OptionPane.questionIcon",
                   getIconResource("icons/ocean/question.png"),
            "OptionPane.warningIcon",
                   getIconResource("icons/ocean/warning.png"),

            "RadioButton.gradient", buttonGradient,
            "RadioButton.rollover", Boolean.TRUE,

            "RadioButtonMenuItem.gradient", buttonGradient,

            "ScrollBar.gradient", buttonGradient,

            "Slider.altTrackColor", new ColorUIResource(0xD2E2EF),
            "Slider.gradient", sliderGradient,
            "Slider.focusGradient", sliderGradient,

            "SplitPane.oneTouchButtonsOpaque", Boolean.FALSE,
            "SplitPane.dividerFocusColor", c8ddf2,

            "TabbedPane.borderHightlightColor", getPrimary1(),
            "TabbedPane.contentAreaColor", c8ddf2,
            "TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3),
            "TabbedPane.selected", c8ddf2,
            "TabbedPane.tabAreaBackground", dadada,
            "TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6),
            "TabbedPane.unselectedBackground", SECONDARY3,

            "Table.focusCellHighlightBorder", focusBorder,
            "Table.gridColor", SECONDARY1,

            "ToggleButton.gradient", buttonGradient,

            "ToolBar.borderColor", cccccc,
            "ToolBar.isRollover", Boolean.TRUE,

            "Tree.closedIcon", directoryIcon,

            "Tree.collapsedIcon",
                  new UIDefaults.LazyValue() {
                      public Object createValue(UIDefaults table) {
                          return new COIcon(getHastenedIcon("icons/ocean/collapsed.gif", table),
                                            getHastenedIcon("icons/ocean/collapsed-rtl.gif", table));
                      }
                  },

            "Tree.expandedIcon",
                  getIconResource("icons/ocean/expanded.gif"),
            "Tree.leafIcon", fileIcon,
            "Tree.openIcon", directoryIcon,
            "Tree.selectionBorderColor", getPrimary1()
        };
        table.putDefaults(defaults);
    }

    /**
     * Overriden to enable picking up the system fonts, if applicable.
     */
    boolean isSystemTheme() {
        return true;
    }

    /**
     * Return the name of this theme, "Ocean".
     *
     * @return "Ocean"
     */
    public String getName() {
        return "Ocean";
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Primary 1". The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Primary 1" color.
     */
    protected ColorUIResource getPrimary1() {
        return PRIMARY1;
    } 

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Primary 2". The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Primary 2" color.
     */
    protected ColorUIResource getPrimary2() {
        return PRIMARY2;
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Primary 3". The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Primary 3" color.
     */
    protected ColorUIResource getPrimary3() {
        return PRIMARY3;
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Secondary 1". The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Secondary 1" color.
     */
    protected ColorUIResource getSecondary1() {
        return SECONDARY1;
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Secondary 2". The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Secondary 2" color.
     */
    protected ColorUIResource getSecondary2() {
        return SECONDARY2;
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Secondary 3". The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Secondary 3" color.
     */
    protected ColorUIResource getSecondary3() {
        return SECONDARY3;
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * as "Black". The Look and Feel will use this color
     * in painting as it sees fit. This color does not necessarily
     * synch up with the typical concept of black, nor is
     * it necessarily used for all black items.
     *
     * @return the "Black" color.
     */
    protected ColorUIResource getBlack() {
        return OCEAN_BLACK;
    }

    /**
     * Return the color that the Metal Look and Feel should use
     * for the desktop background. The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Desktop" color.
     */
    public ColorUIResource getDesktopColor() {
        return MetalTheme.white;
    }

    /**
     * Return the color that the Metal Look and Feel should use as the default
     * color for inactive controls. The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Inactive Control Text" color.
     */
    public ColorUIResource getInactiveControlTextColor() {
        return INACTIVE_CONTROL_TEXT_COLOR;
    }

    /**
     * Return the color that the Metal Look and Feel should use as the default
     * color for controls. The Look and Feel will use this color
     * in painting as it sees fit.
     *
     * @return the "Control Text" color.
     */
    public ColorUIResource getControlTextColor() {
        return CONTROL_TEXT_COLOR;
    }

    /**
     * Return the color that the Metal Look and Feel should use as the
     * foreground color for disabled menu items. The Look and Feel will use
     * this color in painting as it sees fit.
     *
     * @return the "Menu Disabled Foreground" color.
     */
    public ColorUIResource getMenuDisabledForeground() {
        return MENU_DISABLED_FOREGROUND;
    }

    private Object getIconResource(String iconID) {
        return SwingUtilities2.makeIcon(getClass(), OceanTheme.class, iconID);
    }

    // makes use of getIconResource() to fetch an icon and then hastens it
    // - calls createValue() on it and returns the actual icon
    private Icon getHastenedIcon(String iconID, UIDefaults table) {
        Object res = getIconResource(iconID);
        return (Icon)((UIDefaults.LazyValue)res).createValue(table);
    }
}
