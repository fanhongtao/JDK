/*
 * @(#)XPStyle.java	1.26 06/11/30
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
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
import java.awt.image.*;
import java.io.*;
import java.security.AccessController;
import java.util.*;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.JTextComponent;

import sun.awt.windows.ThemeReader;
import sun.security.action.GetPropertyAction;
import sun.swing.CachedPainter;

/**
 * Implements Windows XP Styles for the Windows Look and Feel.
 *
 * @version 1.26 11/30/06
 * @author Leif Samuelsson
 */
class XPStyle {
    // Singleton instance of this class
    private static XPStyle xp;

    // Singleton instance of SkinPainter
    private SkinPainter skinPainter = new SkinPainter();

    private static Boolean themeActive = null;

    private HashMap<String, Skin>   skinMap;
    private HashMap<String, Border> borderMap;
    private HashMap<String, Color>  colorMap;

    private boolean flatMenus;

    static {
        invalidateStyle();
    }

    /** Static method for clearing the hashmap and loading the
     * current XP style and theme
     */
    static synchronized void invalidateStyle() {
        xp = null;
        themeActive = null;
    }

    /** Get the singleton instance of this class
     *
     * @return the singleton instance of this class or null if XP styles
     * are not active or if this is not Windows XP
     */
    static synchronized XPStyle getXP() {
        if (themeActive == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            themeActive =
                (Boolean)toolkit.getDesktopProperty("win.xpstyle.themeActive");
            if (themeActive == null) {
                themeActive = Boolean.FALSE;
            }
            if (themeActive.booleanValue()) {
                GetPropertyAction propertyAction =
                    new GetPropertyAction("swing.noxp");
                if (AccessController.doPrivileged(propertyAction) == null &&
                    ThemeReader.isThemed() &&
                    !(UIManager.getLookAndFeel()
                      instanceof WindowsClassicLookAndFeel)) {

                    xp = new XPStyle();
                }
            }
        }
        return xp;
    }


    /*
     * Create a string key representing the corresponding Windows control (here
     * referred to as "category" or "widget"), its state if applicable, and an
     * optional attribute name.
     *
     * The generated string will conform to one of the following patterns:
     *
     * "category"
     * "category(state)"
     * "category.attributeKey"
     * "category(state).attributeKey"
     * "subAppName::category"
     * "subAppName::category(state)"
     * "subAppName::category.attributeKey"
     * "subAppName::category(state).attributeKey"
     *
     * TODO: Remove use of key strings and use the Part/State/Prop
     * enums instead.
     */
    private static String makeKey(Component c, String category,
                                  String state, String attributeKey) {
        String key = category;
        if (c instanceof JComponent) {
            String subCategory =
                (String)((JComponent)c).getClientProperty("XPStyle.subAppName");
            // We're using the syntax "subAppName::controlName" here, as used by
            // msstyles. See documentation for SetWindowTheme on MSDN.
            if (subCategory != null) {
                key = subCategory + "::" + key;
            }
        }
        if (state != null) {
            key += "("+state+")";
        }
        if (attributeKey != null) {
            key += "." + attributeKey;
        }
        return key;
    }

    /** Get a named <code>String</code> value from the current style
     *
     * @param category a <code>String</code>
     * @param state a <code>String</code>
     * @param attributeKey a <code>String</code>
     * @return a <code>String</code> or null if key is not found
     *    in the current style
     *
     * This is currently only used by WindowsInternalFrameTitlePane for painting
     * title foregound and can be removed when no longer needed
     */
    String getString(Component c, String category,
                     String state, String attributeKey) {
        return getEnumName(c, category, state, attributeKey);
    }

    private static String getEnumName(Component c, String category,
                                      String state, String attributeKey) {
        String key = makeKey(c, category, state, attributeKey);
        Part part = Part.getPart(getWidgetAndPart(key));
        int prop  = Prop.getPropValue(key);

        int enumValue = ThemeReader.getEnum(part.getWidget(), part.value,
                                            State.getStateValue(key), prop);
        if (enumValue == -1) {
            return null;
        }
        return Enum.getEnum(prop, enumValue).enumName;
    }



    /** Get a named <code>int</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return an <code>int</code> or null if key is not found
     *    in the current style
     */
    int getInt(Component c, String category, String state,
               String attributeKey, int fallback) {
        String key = makeKey(c, category, state, attributeKey);
        Part part = Part.getPart(getWidgetAndPart(key));
        int prop  = Prop.getPropValue(key);
        return ThemeReader.getInt(part.getWidget(), part.value,
                                  State.getStateValue(key), prop);
    }

    /** Get a named <code>Dimension</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Dimension</code> or null if key is not found
     *    in the current style
     *
     * This is currently only used by WindowsProgressBarUI and the value
     * should probably be cached there instead of here.
     */
    Dimension getDimension(Component c, String category,
                           String state, String attributeKey) {
        String key = makeKey(c, category, state, attributeKey);
        Part part = Part.getPart(getWidgetAndPart(key));
        int prop  = Prop.getPropValue(key);
        return ThemeReader.getPosition(part.getWidget(), part.value,
                                       State.getStateValue(key), prop);
    }

    /** Get a named <code>Point</code> (e.g. a location or an offset) value
     *  from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Point</code> or null if key is not found
     *    in the current style
     *
     * This is currently only used by WindowsInternalFrameTitlePane for painting
     * title foregound and can be removed when no longer needed
     */
    Point getPoint(Component c, String category,
                   String state, String attributeKey) {
        String key = makeKey(c, category, state, attributeKey);
        Part part = Part.getPart(getWidgetAndPart(key));
        int prop  = Prop.getPropValue(key);
        Dimension d = ThemeReader.getPosition(part.getWidget(), part.value,
                                              State.getStateValue(key), prop);
        if (d != null) {
            return new Point(d.width, d.height);
        } else {
            return null;
        }
    }

    /** Get a named <code>Insets</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return an <code>Insets</code> object or null if key is not found
     *    in the current style
     *
     * This is currently only used to create borders and by
     * WindowsInternalFrameTitlePane for painting title foregound.
     * The return value is already cached in those places.
     */
    Insets getMargin(Component c, String category,
                     String state, String attributeKey) {
        String key = makeKey(c, category, state, attributeKey);
        Part part = Part.getPart(getWidgetAndPart(key));
        int prop  = Prop.getPropValue(key);
        return ThemeReader.getThemeMargins(part.getWidget(), part.value,
                                           State.getStateValue(key), prop);
    }

    /** Get a named <code>Color</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Color</code> or null if key is not found
     *    in the current style
     */
    synchronized Color getColor(Component c, String category,
                                String state, String attributeKey,
                                Color fallback) {
        String key = makeKey(c, category, state, attributeKey);
        Color color = colorMap.get(key);
        if (color == null) {
            Part part = Part.getPart(getWidgetAndPart(key));
            int prop  = Prop.getPropValue(key);
            color = ThemeReader.getColor(part.getWidget(), part.value,
                                         State.getStateValue(key), prop);
            if (color != null) {
                color = new ColorUIResource(color);
                colorMap.put(key, color);
            }
        }
        return (color != null) ? color : fallback;
    }


    /** Get a named <code>Border</code> value from the current style
     *
     * @param key a <code>String</code>
     * @return a <code>Border</code> or null if key is not found
     *    in the current style or if the style for the particular
     *    category is not defined as "borderfill".
     */
    synchronized Border getBorder(Component c, String category) {
        if (category == "menu") {
            // Special case because XP has no skin for menus
            if (flatMenus) {
                // TODO: The classic border uses this color, but we should
                // create a new UI property called "PopupMenu.borderColor"
                // instead.
                return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"),
                                        1);
            } else {
                return null;    // Will cause L&F to use classic border
            }
        }
        String key = makeKey(c, category, null, null);
        Border border = borderMap.get(key);
        if (border == null) {
            String bgType = getEnumName(c, category, null, "bgtype");
            if ("borderfill".equalsIgnoreCase(bgType)) {
                int thickness = getInt(c, category, null, "bordersize", 1);
                Color color = getColor(c, category, null,
                                       "bordercolor", Color.black);
                border = new XPFillBorder(color, thickness);
            } else if ("imagefile".equalsIgnoreCase(bgType)) {
                Insets m = getMargin(c, category, null, "sizingmargins");
                if (m != null) {
                    if (getBoolean(c, category, null, "borderonly")) {
                        border = new XPImageBorder(c, category);
                    } else {
                        if(category == "toolbar.button") {
                            border = new XPEmptyBorder(new Insets(3,3,3,3));
                        } else {
                            border = new XPEmptyBorder(m);
                        }
                    }
                }
            }
            if (border != null) {
                borderMap.put(category, border);
            }
        }
        return border;
    }

    private class XPFillBorder extends LineBorder implements UIResource {
        XPFillBorder(Color color, int thickness) {
            super(color, thickness);
        }

        public Insets getBorderInsets(Component c)       {
            return getBorderInsets(c, new Insets(0,0,0,0));
        }

        public Insets getBorderInsets(Component c, Insets insets)       {
            Insets margin = null;
            //
            // Ideally we'd have an interface defined for classes which
            // support margins (to avoid this hackery), but we've
            // decided against it for simplicity
            //
           if (c instanceof AbstractButton) {
               margin = ((AbstractButton)c).getMargin();
           } else if (c instanceof JToolBar) {
               margin = ((JToolBar)c).getMargin();
           } else if (c instanceof JTextComponent) {
               margin = ((JTextComponent)c).getMargin();
           }
           insets.top    = (margin != null? margin.top : 0)    + thickness;
           insets.left   = (margin != null? margin.left : 0)   + thickness;
           insets.bottom = (margin != null? margin.bottom : 0) + thickness;
           insets.right =  (margin != null? margin.right : 0)  + thickness;
               
           return insets;
        }
    }

    private class XPImageBorder extends AbstractBorder implements UIResource {
        Skin skin;

        XPImageBorder(Component c, String category) {
            this.skin = getSkin(c, category);
        }

        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            skin.paintSkin(g, x, y, width, height, 0);
        }

        public Insets getBorderInsets(Component c)       {
            return getBorderInsets(c, new Insets(0,0,0,0));
        }

        public Insets getBorderInsets(Component c, Insets insets)       {
            Insets margin = null;
            Insets borderInsets = skin.getContentMargin();
            //
            // Ideally we'd have an interface defined for classes which
            // support margins (to avoid this hackery), but we've
            // decided against it for simplicity
            //
           if (c instanceof AbstractButton) {
               margin = ((AbstractButton)c).getMargin();
           } else if (c instanceof JToolBar) {
               margin = ((JToolBar)c).getMargin();
           } else if (c instanceof JTextComponent) {
               margin = ((JTextComponent)c).getMargin();
           }
           insets.top    = (margin != null? margin.top : 0)    + borderInsets.top;
           insets.left   = (margin != null? margin.left : 0)   + borderInsets.left;
           insets.bottom = (margin != null? margin.bottom : 0) + borderInsets.bottom;
           insets.right  = (margin != null? margin.right : 0)  + borderInsets.right;
               
           return insets;
        }
    }

    private class XPEmptyBorder extends EmptyBorder implements UIResource {
        XPEmptyBorder(Insets m) {
            super(m.top+2, m.left+2, m.bottom+2, m.right+2);
        }

        public Insets getBorderInsets(Component c)       {
            return getBorderInsets(c, getBorderInsets());
        }

        public Insets getBorderInsets(Component c, Insets insets)       {
            insets = super.getBorderInsets(c, insets);
                
            Insets margin = null;
            if (c instanceof AbstractButton) {
                Insets m = ((AbstractButton)c).getMargin();
                // if this is a toolbar button then ignore getMargin()
                // and subtract the padding added by the constructor
                if(c.getParent() instanceof JToolBar 
                   && ! (c instanceof JRadioButton)
                   && ! (c instanceof JCheckBox)
                   && m instanceof InsetsUIResource) {
                    insets.top -= 2;
                    insets.left -= 2;
                    insets.bottom -= 2;
                    insets.right -= 2;
                } else {
                    margin = m;
                }
            } else if (c instanceof JToolBar) {
                margin = ((JToolBar)c).getMargin();
            } else if (c instanceof JTextComponent) {
                margin = ((JTextComponent)c).getMargin();
            }
            if (margin != null) {
                insets.top    = margin.top + 2;
                insets.left   = margin.left + 2;
                insets.bottom = margin.bottom + 2;
                insets.right  = margin.right + 2;
            }
            return insets;
        }
    }


    /** Get an <code>XPStyle.Skin</code> object from the current style
     * for a named category (component type)
     *
     * @param category a <code>String</code>
     * @return an <code>XPStyle.Skin</code> object or null if the category is
     * not found in the current style
     */
    synchronized Skin getSkin(Component c, String category) {
        String key = makeKey(c, category, null, null);
        Skin skin = skinMap.get(key);
        if (skin == null) {
            skin = new Skin(key);
            skinMap.put(key, skin);
        }
        return skin;
    }




    /** A class which encapsulates attributes for a given category
     * (component type) and which provides methods for painting backgrounds
     * and glyphs
     */
    class Skin {
        private String category;
        private Dimension size = null;

        private Skin(String category) {
            this.category = category;
        }

        Insets getContentMargin() {
            // This is only called by WindowsTableHeaderUI so far.
            return getMargin(null, category, null, "sizingmargins");
        }

        private int getWidth(int state) {
            if (size == null) {
                size = getPartSize(category, state);
            }
            return size.width;
        }

        int getWidth() {
            return getWidth(1);
        }

        private int getHeight(int state) {
            if (size == null) {
                size = getPartSize(category, state);
            }
            return size.height;
        }

        int getHeight() {
            return getHeight(1);
        }

        /** Paint a skin at x, y.
         *
         * @param g   the graphics context to use for painting
         * @param dx  the destination <i>x</i> coordinate.
         * @param dy  the destination <i>y</i> coordinate.
         * @param index which subimage to paint (usually depends on component state)
         */
        void paintSkin(Graphics g, int dx, int dy, int index) {
            paintSkin(g, dx, dy, getWidth(index+1), getHeight(index+1), index);
        }

        /** Paint a skin in an area defined by a rectangle.
         *
         * @param g the graphics context to use for painting
         * @param r     a <code>Rectangle</code> defining the area to fill,
         *                     may cause the image to be stretched or tiled
         * @param index which subimage to paint (usually depends on component state)
         */
        void paintSkin(Graphics g, Rectangle r, int index) {
            paintSkin(g, r.x, r.y, r.width, r.height, index);
        }

        /** Paint a skin at a defined position and size
         *
         * @param g   the graphics context to use for painting
         * @param dx  the destination <i>x</i> coordinate.
         * @param dy  the destination <i>y</i> coordinate.
         * @param dw  the width of the area to fill, may cause
         *                  the image to be stretched or tiled
         * @param dh  the height of the area to fill, may cause
         *                  the image to be stretched or tiled
         * @param index which subimage to paint (usually depends on component state)
         */
        void paintSkin(Graphics g, int dx, int dy, int dw, int dh, int index) {
            skinPainter.paint(null, g, dx, dy, dw, dh, category, index);
        }

        void paintSkin(Graphics g, int dx, int dy, int dw, int dh, int index, 
                boolean borderFill) {
            String bgType = getEnumName(null, category, null, "bgtype");
            if (borderFill && "borderfill".equalsIgnoreCase(bgType)) {
                return;
            }
            skinPainter.paint(null, g, dx, dy, dw, dh, category, index);
        }
    }

    private static class SkinPainter extends CachedPainter {
        SkinPainter() {
            super(30);
            flush();
        }

        protected void paintToImage(Component c, Image image, Graphics g,
                                    int w, int h, Object[] args) {
            String widgetAndPart = (String)args[0];
            int state = (Integer)args[1] + 1;
            Part part = Part.getPart(widgetAndPart);
            WritableRaster raster = ((BufferedImage)image).getRaster();
            DataBufferInt buffer = (DataBufferInt)raster.getDataBuffer();
            ThemeReader.paintBackground(buffer.getData(), part.getWidget(),
                                        part.value, state, 0, 0, w, h, w);
        }

        protected Image createImage(Component c, int w, int h,
                                    GraphicsConfiguration config, Object[] args) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
    }

    static class GlyphButton extends JButton {
        private Skin skin;
        private Image glyphImage;
        private boolean vertical;

        public GlyphButton(Component parent, String category) {
            XPStyle xp = getXP();
            skin = xp.getSkin(parent, category);
            setBorder(null);
            setContentAreaFilled(false);
        }   

        public boolean isFocusTraversable() {
            return false;
        }

        public void paintComponent(Graphics g) {
            int index = 0;
            if (!isEnabled()) {
                index = 3;
            } else if (getModel().isPressed()) {
                index = 2;
            } else if (getModel().isRollover()) {
                index = 1;
            }
            Dimension d = getSize();
            skin.paintSkin(g, 0, 0, d.width, d.height, index);
        }

        protected void paintBorder(Graphics g) {    
        }

        public Dimension getPreferredSize() {
            return new Dimension(16, 16);
        }

        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    // Private constructor
    private XPStyle() {
        flatMenus = getSysBoolean("flatmenus");

        skinMap   = new HashMap<String, Skin>();
        colorMap  = new HashMap<String, Color>();
        borderMap = new HashMap<String, Border>();
        // Note: All further access to the maps must be synchronized
    }


    private boolean getBoolean(Component c, String category,
                               String state, String attributeKey) {
        String key = makeKey(c, category, state, attributeKey);
        Part part = Part.getPart(getWidgetAndPart(key));
        int prop  = Prop.getPropValue(key);
        return ThemeReader.getBoolean(part.getWidget(), part.value,
                                      State.getStateValue(key), prop);
    }








    // Parts and States
    // See http://msdn.microsoft.com/library/default.asp?url=/library/en-us/shellcc/platform/commctls/userex/topics/partsandstates.asp

    public enum Part {
        BP_PUSHBUTTON ("button.pushbutton",  1),
        BP_RADIOBUTTON("button.radiobutton", 2),
        BP_CHECKBOX   ("button.checkbox",    3),
        BP_GROUPBOX   ("button.groupbox",    4),

        CP_COMBOBOX      ("combobox",                0),
        CP_DROPDOWNBUTTON("combobox.dropdownbutton", 1),

        EP_EDIT    ("edit",          0),
        EP_EDITTEXT("edit.edittext", 1),

        HP_HEADERITEM("header.headeritem", 1),

        LBP_LISTBOX("listbox", 0),

        LVP_LISTVIEW("listview", 0),

	MP_MENUITEM    ("menu.menuitem",     1),
	MP_MENUDROPDOWN("menu.menudropdown", 2),

        PP_PROGRESS ("progress",           0),
        PP_BAR      ("progress.bar",       1),
        PP_BARVERT  ("progress.barvert",   2),
        PP_CHUNK    ("progress.chunk",     3),
        PP_CHUNKVERT("progress.chunkvert", 4),

        RP_GRIPPER    ("rebar.gripper",     1),
        RP_GRIPPERVERT("rebar.grippervert", 2),

        SBP_ARROWBTN      ("scrollbar.arrowbtn",       1),
        SBP_THUMBBTNHORZ  ("scrollbar.thumbbtnhorz",   2),
        SBP_THUMBBTNVERT  ("scrollbar.thumbbtnvert",   3),
        SBP_LOWERTRACKHORZ("scrollbar.lowertrackhorz", 4),
        SBP_LOWERTRACKVERT("scrollbar.lowertrackvert", 5),
        SBP_GRIPPERHORZ   ("scrollbar.gripperhorz",    8),
        SBP_GRIPPERVERT   ("scrollbar.grippervert",    9),

        SPNP_SPINUP  ("spin.up",   1),
        SPNP_SPINDOWN("spin.down", 2),

        TABP_TABITEM         ("tab.tabitem",          1),
        TABP_TABITEMLEFTEDGE ("tab.tabitemleftedge",  2),
        TABP_TABITEMRIGHTEDGE("tab.tabitemrightedge", 3),
        TABP_PANE            ("tab.pane",             9),

        TP_TOOLBAR        ("toolbar",                   0),
        TP_PLACESBAR      ("placesbar::toolbar",        0),
        TP_BUTTON         ("toolbar.button",            1),
        TP_PLACESBARBUTTON("placesbar::toolbar.button", 1),
        TP_SEPARATOR      ("toolbar.separator",         5),
        TP_SEPARATORVERT  ("toolbar.separatorvert",     6),

        TKP_TRACK      ("trackbar.track",       1),
        TKP_TRACKVERT  ("trackbar.trackvert",   2),
        TKP_THUMB      ("trackbar.thumb",       3),
        TKP_THUMBBOTTOM("trackbar.thumbbottom", 4),
        TKP_THUMBTOP   ("trackbar.thumbtop",    5),
        TKP_THUMBVERT  ("trackbar.thumbvert",   6),
        TKP_THUMBLEFT  ("trackbar.thumbleft",   7),
        TKP_THUMBRIGHT ("trackbar.thumbright",  8),
        TKP_TICS       ("trackbar.tics",        9),
        TKP_TICSVERT   ("trackbar.ticsvert",   10),

        TVP_TREEVIEW("treeview",       0),
        TVP_GLYPH   ("treeview.glyph", 2),

        WP_WINDOW       ("window",                0),
        WP_CAPTION      ("window.caption",        1),
        WP_MINCAPTION   ("window.mincaption",     3),
        WP_MAXCAPTION   ("window.maxcaption",     5),
        WP_FRAMELEFT    ("window.frameleft",      7),
        WP_FRAMERIGHT   ("window.frameright",     8),
        WP_FRAMEBOTTOM  ("window.framebottom",    9),
        WP_MINBUTTON    ("window.minbutton",     15),
        WP_MAXBUTTON    ("window.maxbutton",     17),
        WP_CLOSEBUTTON  ("window.closebutton",   18),
        WP_RESTOREBUTTON("window.restorebutton", 21);


        private final int value;
        private final String name;

        Part(String name, int value) {
            this.value = value;
            this.name = name;
        }

        public String getWidget() {
            int i = name.indexOf(".");
            return (i > 0) ? name.substring(0, i) : name;
        }

        public String toString() {
            return name+"="+value; 
        }

        private static Part getPart(String name) {
            for (Part p  : Part.values() ) {
                if (p.name.equals(name)) {
                    return p;
                }
            }       
            return null;
        }
    }

    public enum State {

        ETS_NORMAL  (Part.EP_EDITTEXT, "normal",   1),
        ETS_DISABLED(Part.EP_EDITTEXT, "disabled", 4),
        ETS_READONLY(Part.EP_EDITTEXT, "readonly", 6),

        // States for BP_PUSHBUTTON
        PBS_NORMAL   (Part.BP_PUSHBUTTON, "normal",    1),
        PBS_HOT      (Part.BP_PUSHBUTTON, "hot",       2),
        PBS_PRESSED  (Part.BP_PUSHBUTTON, "pressed",   3),
        PBS_DISABLED (Part.BP_PUSHBUTTON, "disabled",  4),
        PBS_DEFAULTED(Part.BP_PUSHBUTTON, "defaulted", 5),

        // States for BP_RADIOBUTTON
        RBS_UNCHECKEDNORMAL  (Part.BP_RADIOBUTTON, "uncheckednormal",   1),
        RBS_UNCHECKEDHOT     (Part.BP_RADIOBUTTON, "uncheckedhot",      2),
        RBS_UNCHECKEDPRESSED (Part.BP_RADIOBUTTON, "uncheckedpressed",  3),
        RBS_UNCHECKEDDISABLED(Part.BP_RADIOBUTTON, "uncheckeddisabled", 4),
        RBS_CHECKEDNORMAL    (Part.BP_RADIOBUTTON, "checkednormal",     5),
        RBS_CHECKEDHOT       (Part.BP_RADIOBUTTON, "checkedhot",        6),
        RBS_CHECKEDPRESSED   (Part.BP_RADIOBUTTON, "checkedpressed",    7),
        RBS_CHECKEDDISABLED  (Part.BP_RADIOBUTTON, "checkeddisabled",   8),

        // States for BP_CHECKBOX
        CBS_UNCHECKEDNORMAL  (Part.BP_CHECKBOX, "uncheckednormal",   1),
        CBS_UNCHECKEDHOT     (Part.BP_CHECKBOX, "uncheckedhot",      2),
        CBS_UNCHECKEDPRESSED (Part.BP_CHECKBOX, "uncheckedpressed",  3),
        CBS_UNCHECKEDDISABLED(Part.BP_CHECKBOX, "uncheckeddisabled", 4),
        CBS_CHECKEDNORMAL    (Part.BP_CHECKBOX, "checkednormal",     5),
        CBS_CHECKEDHOT       (Part.BP_CHECKBOX, "checkedhot",        6),
        CBS_CHECKEDPRESSED   (Part.BP_CHECKBOX, "checkedpressed",    7),
        CBS_CHECKEDDISABLED  (Part.BP_CHECKBOX, "checkeddisabled",   8),
        CBS_MIXEDNORMAL      (Part.BP_CHECKBOX, "mixednormal",       9),
        CBS_MIXEDHOT         (Part.BP_CHECKBOX, "mixedhot",         10),
        CBS_MIXEDPRESSED     (Part.BP_CHECKBOX, "mixedpressed",     11),
        CBS_MIXEDDISABLED    (Part.BP_CHECKBOX, "mixeddisabled",    12),

        // States for TP_BUTTON
        TS_NORMAL  (Part.TP_BUTTON, "normal",    1),
        TS_HOT     (Part.TP_BUTTON, "hot",       2),
        TS_PRESSED (Part.TP_BUTTON, "pressed",   3),
        TS_DISABLED(Part.TP_BUTTON, "disabled",  4),

        CS_ACTIVE(Part.WP_CAPTION, "active", 1),

        FS_ACTIVE(Part.WP_WINDOW,  "active", 1);

        private final Part part;
        private final String stateName;
        private final int value;

        State(Part part, String stateName, int value) {
            this.part      = part;
            this.stateName = stateName;
            this.value     = value;
        }

        public String toString() {
            return part+"("+stateName+"="+value+")"; 
        }


        private static int getStateValue(String key) {
            String widgetAndPart = "";
            String stateName = null;

            int i = key.lastIndexOf('.');
            if (i > 0 && key.length() > i+1) {
                widgetAndPart = key.substring(0, i);
                i = widgetAndPart.lastIndexOf('(');
                if (i > 0) {
                    int i2 = widgetAndPart.indexOf(')', i);
                    if (i2 == widgetAndPart.length() - 1) {
                        stateName = widgetAndPart.substring(i+1, i2);
                        widgetAndPart = widgetAndPart.substring(0, i);
                    }
                }
            }
            return (stateName != null)
                                ? State.getState(widgetAndPart, stateName).value
                                : 0;
        }

        private static State getState(String widgetAndPart, String stateName) {
            for (State s  : State.values() ) {
                if (s.part.name.equals(widgetAndPart) &&
                    (stateName == null || s.stateName.equals(stateName))) {
                    return s;
                }
            }
            return null;
        }
    }

    public enum Prop {
        TMT_COLOR("color", Color.class, 204),
        TMT_SIZE("size", Dimension.class, 207),

        TMT_FLATMENUS("flatmenus", Boolean.class, 1001),

        // only draw the border area of the image
        TMT_BORDERONLY("borderonly", Boolean.class, 2203),

        // the size of the border line for bgtype=BorderFill
        TMT_BORDERSIZE("bordersize", Integer.class, 2403),

        // size of progress control chunks
        TMT_PROGRESSCHUNKSIZE("progresschunksize", Integer.class, 2411),
        // size of progress control spaces
        TMT_PROGRESSSPACESIZE("progressspacesize", Integer.class, 2412),

        // where char shadows are drawn, relative to orig. chars
        TMT_TEXTSHADOWOFFSET("textshadowoffset", Point.class, 3402),

        // size of dest rect that exactly source
        TMT_NORMALSIZE("normalsize", Dimension.class, 3409),


        // margins used for 9-grid sizing
        TMT_SIZINGMARGINS ("sizingmargins",  Insets.class, 3601),
        // margins that define where content can be placed
        TMT_CONTENTMARGINS("contentmargins", Insets.class, 3602),
        // margins that define where caption text can be placed
        TMT_CAPTIONMARGINS("captionmargins", Insets.class, 3603),

        // color of borders for BorderFill 
        TMT_BORDERCOLOR("bordercolor", Color.class, 3801),
        // color of bg fill
        TMT_FILLCOLOR  ("fillcolor",   Color.class, 3802),
        // color text is drawn in
        TMT_TEXTCOLOR  ("textcolor",   Color.class, 3803),

        // color of text shadow
        TMT_TEXTSHADOWCOLOR("textshadowcolor", Color.class, 3818),

        // basic drawing type for each part
        TMT_BGTYPE("bgtype", Integer.class, 4001),

        // type of shadow to draw with text
        TMT_TEXTSHADOWTYPE("textshadowtype", Integer.class, 4010);


        Prop(String propName, Class type, int value) {
            this.propName = propName;
            this.type     = type;
            this.value    = value;
        }

        private final String propName;
        private final Class type;
        private final int value;

        public String toString() {
            return propName+"["+type.getName()+"] = "+value; 
        }

        private static Prop getProp(String propName) {
            for (Prop p : Prop.values() ) {
                if (p.propName.equals(propName)) {
                    return p;
                }
            }
            return null;
        }

        private static int getPropValue(String key) {
            String propName = key;
            int i = key.lastIndexOf('.');
            if (i > 0 && key.length() > i+1) {
                propName = key.substring(i+1);
            }
            return Prop.getProp(propName).value;
        }

    }


    public enum Enum {
        BT_IMAGEFILE (Prop.TMT_BGTYPE, "imagefile",  0),
        BT_BORDERFILL(Prop.TMT_BGTYPE, "borderfill", 1),

        TST_SINGLE(Prop.TMT_TEXTSHADOWTYPE, "single", 1),
        TST_CONTINUOUS(Prop.TMT_TEXTSHADOWTYPE, "continuous", 2);


        Enum(Prop prop, String enumName, int value) {
            this.prop = prop;
            this.enumName = enumName;
            this.value = value;
        }

        private final Prop prop;
        private final String enumName;
        private final int value;

        public String toString() {
            return prop+"="+enumName+"="+value; 
        }

        private static Enum getEnum(String enumName) {
            for (Enum e : Enum.values() ) {
                if (e.enumName.equals(enumName)) {
                    return e;
                }
            }
            return null;
        }

        private static Enum getEnum(int propval, int enumval) {
            for (Enum e : Enum.values() ) {
                if (e.prop.value == propval && e.value == enumval) {
                    return e;
                }
            }
            return null;
        }
    }




    private static String getWidgetAndPart(String key) {
        // Example: "controlname.partname(statename).propname"
        int i = key.lastIndexOf('.');
        if (i > 0 && key.length() > i+1) {
            key = key.substring(0, i);

            i = key.lastIndexOf('(');
            if (i > 0) {
                int i2 = key.indexOf(')', i);
                if (i2 == key.length() - 1) {
                    key = key.substring(0, i);
                }
            }
        }
        return key;
    }


    private static Dimension getPartSize(String widgetAndPart, int state) {
        // Note that a state name in the string takes precedence over stateNum
        Part part = Part.getPart(widgetAndPart);
        return ThemeReader.getPartSize(part.getWidget(), part.value, state);
    }

    private static boolean getSysBoolean(String propName) {
        Prop prop = Prop.getProp(propName);
        // We can use any widget name here, I guess.
        return ThemeReader.getSysBoolean("window", prop.value);
    }
}
