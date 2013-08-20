/*
 * @(#)DefaultMetalTheme.java	1.27 04/03/03
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.metal;

import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

import sun.security.action.GetPropertyAction;

/**
 * This class describes the default Metal Theme.
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
 * @version 1.27 03/03/04
 * @author Steve Wilson
 */
public class DefaultMetalTheme extends MetalTheme {
    /**
     * Whether or not fonts should be plain.  This is only used if
     * the defaults property 'swing.boldMetal' == "false".
     */
    private static final boolean PLAIN_FONTS;

    /**
     * Names of the fonts to use.
     */
    private static final String[] fontNames = {
        "Dialog", "Dialog", "Dialog", "Dialog", "Dialog", "Dialog"
    };
    /**
     * Styles for the fonts.  This is ignored if the defaults property
     * <code>swing.boldMetal</code> is false, or PLAIN_FONTS is true.
     */
    private static final int[] fontStyles = {
        Font.BOLD, Font.PLAIN, Font.PLAIN, Font.BOLD, Font.BOLD, Font.PLAIN
    };
    /**
     * Sizes for the fonts.
     */
    private static final int[] fontSizes = {
        12, 12, 12, 12, 12, 10
    };

    // note the properties listed here can currently be used by people
    // providing runtimes to hint what fonts are good.  For example the bold 
    // dialog font looks bad on a Mac, so Apple could use this property to 
    // hint at a good font.
    //
    // However, we don't promise to support these forever.  We may move 
    // to getting these from the swing.properties file, or elsewhere.
    /**
     * System property names used to look up fonts.
     */
    private static final String[] defaultNames = {
        "swing.plaf.metal.controlFont",
        "swing.plaf.metal.systemFont",
        "swing.plaf.metal.userFont",
        "swing.plaf.metal.controlFont",
        "swing.plaf.metal.controlFont",
        "swing.plaf.metal.smallFont"
    };

    /**
     * Returns the ideal font name for the font identified by key.
     */
    static String getDefaultFontName(int key) {
        return fontNames[key];
    }

    /**
     * Returns the ideal font size for the font identified by key.
     */
    static int getDefaultFontSize(int key) {
        return fontSizes[key];
    }

    /**
     * Returns the ideal font style for the font identified by key.
     */
    static int getDefaultFontStyle(int key) {
        if (key != WINDOW_TITLE_FONT) {
            Object boldMetal = UIManager.get("swing.boldMetal");
            if (boldMetal != null) {
                if (Boolean.FALSE.equals(boldMetal)) {
                    return Font.PLAIN;
                }
            }
            else if (PLAIN_FONTS) {
                return Font.PLAIN;
            }
        }
        return fontStyles[key];
    }

    /**
     * Returns the default used to look up the specified font.
     */
    static String getDefaultPropertyName(int key) {
        return defaultNames[key];
    }

    static {
        Object boldProperty = java.security.AccessController.doPrivileged(
            new GetPropertyAction("swing.boldMetal"));
        if (boldProperty == null || !"false".equals(boldProperty)) {
            PLAIN_FONTS = false;
        }
        else {
            PLAIN_FONTS = true;
        }
    }

    private static final ColorUIResource primary1 = new ColorUIResource(
                              102, 102, 153);
    private static final ColorUIResource primary2 = new ColorUIResource(153,
                              153, 204);
    private static final ColorUIResource primary3 = new ColorUIResource(
                              204, 204, 255);
    private static final ColorUIResource secondary1 = new ColorUIResource(
                              102, 102, 102);
    private static final ColorUIResource secondary2 = new ColorUIResource(
                              153, 153, 153);
    private static final ColorUIResource secondary3 = new ColorUIResource(
                              204, 204, 204);

    private FontDelegate fontDelegate;

    public String getName() { return "Steel"; }

    public DefaultMetalTheme() {
        install();
    }

    // these are blue in Metal Default Theme
    protected ColorUIResource getPrimary1() { return primary1; } 
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

    // these are gray in Metal Default Theme
    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }


    public FontUIResource getControlTextFont() { 
        return getFont(CONTROL_TEXT_FONT);
    }

    public FontUIResource getSystemTextFont() { 
        return getFont(SYSTEM_TEXT_FONT);
    }

    public FontUIResource getUserTextFont() { 
        return getFont(USER_TEXT_FONT);
    }

    public FontUIResource getMenuTextFont() { 
        return getFont(MENU_TEXT_FONT);
    }

    public FontUIResource getWindowTitleFont() { 
        return getFont(WINDOW_TITLE_FONT);
    }

    public FontUIResource getSubTextFont() { 
        return getFont(SUB_TEXT_FONT);
    }

    private FontUIResource getFont(int key) {
        return fontDelegate.getFont(key);
    }

    void install() {
        if (MetalLookAndFeel.isWindows() &&
                             MetalLookAndFeel.useSystemFonts()) {
            fontDelegate = new WindowsFontDelegate();
        }
        else {
            fontDelegate = new FontDelegate();
        }
    }

    /**
     * Returns true if this is a theme provided by the core platform.
     */
    boolean isSystemTheme() {
        return (getClass() == DefaultMetalTheme.class);
    }

    /**
     * FontDelegates add an extra level of indirection to obtaining fonts.
     */
    private static class FontDelegate {
        private static int[] defaultMapping = {
            CONTROL_TEXT_FONT, SYSTEM_TEXT_FONT,
            USER_TEXT_FONT, CONTROL_TEXT_FONT,
            CONTROL_TEXT_FONT, SUB_TEXT_FONT
        };
        FontUIResource fonts[];

        // menu and window are mapped to controlFont
        public FontDelegate() {
            fonts = new FontUIResource[6];
        }

        public FontUIResource getFont(int type) {
            int mappedType = defaultMapping[type];
            if (fonts[type] == null) {
                Font f = getPrivilegedFont(mappedType);

                if (f == null) {
                    f = new Font(getDefaultFontName(type),
                             getDefaultFontStyle(type),
                             getDefaultFontSize(type));
                }
                fonts[type] = new FontUIResource(f);
            }
            return fonts[type];
        }

        /**
         * This is the same as invoking
         * <code>Font.getFont(key)</code>, with the exception
         * that it is wrapped inside a <code>doPrivileged</code> call.
         */
        protected Font getPrivilegedFont(final int key) {
            return (Font)java.security.AccessController.doPrivileged(
                new java.security.PrivilegedAction() {
                    public Object run() {
                        return Font.getFont(getDefaultPropertyName(key));
                    }
                }
                );
        }
    }

    /**
     * The WindowsFontDelegate uses DesktopProperties to obtain fonts.
     */
    private static class WindowsFontDelegate extends FontDelegate {
        private MetalFontDesktopProperty[] props;
        private boolean[] checkedPriviledged;

        public WindowsFontDelegate() {
            props = new MetalFontDesktopProperty[6];
            checkedPriviledged = new boolean[6];
        }

        public FontUIResource getFont(int type) {
            if (fonts[type] != null) {
                return fonts[type];
            }
            if (!checkedPriviledged[type]) {
                Font f = getPrivilegedFont(type);

                checkedPriviledged[type] = true;
                if (f != null) {
                    fonts[type] = new FontUIResource(f);
                    return fonts[type];
                }
            }
            if (props[type] == null) {
                props[type] = new MetalFontDesktopProperty(type);
            }
            // While passing null may seem bad, we don't actually use
            // the table and looking it up is rather expensive.
            return (FontUIResource)props[type].createValue(null);
        }
    }
}
