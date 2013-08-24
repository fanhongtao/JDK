/*
 * @(#)GTKStyle.java	1.117 09/08/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import com.sun.java.swing.SwingUtilities2;
import javax.swing.plaf.synth.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.util.*;
import java.security.*;

import sun.swing.plaf.synth.*;
import sun.awt.AppContext;

/**
 * SynthStyle implementation used in GTK. All painting is mapped to
 * a <code>GTKEngine</code>.
 *
 * @version 1.117, 08/10/09
 * @author Scott Violet
 */
public class GTKStyle extends DefaultSynthStyle implements GTKConstants {
    private static final String ICON_PROPERTY_PREFIX = "gtk.icon.";

    static final Color BLACK_COLOR = new ColorUIResource(Color.BLACK);
    static final Color WHITE_COLOR = new ColorUIResource(Color.WHITE);

    private static final Color[][] DEFAULT_COLORS;
    /**
     * State the color array at an particular index in DEFAULT_COLORS
     * represents.
     */
    private static final int[] DEFAULT_COLOR_MAP;
    // NOTE: This will only be used if you weren't using GTKLookAndFeel
    // to create a GTKStyle, otherwise the default comes from GTKLookAndFeel.
    private static final Font DEFAULT_FONT = new FontUIResource(
                                         "sansserif", Font.PLAIN, 10);
    /**
     * Backing style properties that are used if the style does not
     * defined the property.
     */
    // PENDING: This needs to be massaged so that it does not need to
    // be AppContext specific. In particular some of the Map values are
    // exposed and mutable and could effect other applets.
    private static final HashMap DATA = new HashMap();

    /**
     * Maps from a key that is passed to Style.get to the equivalent class
     * specific key.
     */
    private static final HashMap CLASS_SPECIFIC_MAP;

    private static final GTKGraphicsUtils GTK_GRAPHICS =new GTKGraphicsUtils();

    /**
     * Indicates the thickness has not been set.
     */
    static final int UNDEFINED_THICKNESS = -1;

    // NOTE: If you add a new field to this class you will need to update
    // addto, and possibly the clone and GTKStyle(GTKStyle) constructors.
    private int xThickness = 2;
    private int yThickness = 2;

    /**
     * Represents the values that are specific to a particular class.
     * This is a CircularIdentityList of CircularIdentityLists, where the
     * first level entries are gtk class names, and the second
     * CircularIdentityLists contains the actual key/value pairs.
     */
    private CircularIdentityList classSpecificValues;
    
    /**
     * Icons.
     */
    private GTKStockIconInfo[] icons;

    /**
     * Calculates the LIGHT color from the background color.
     */
    static Color calculateLightColor(Color bg) {
        return GTKColorType.adjustColor(bg, 1.0f, 1.3f, 1.3f);
    }

    /**
     * Calculates the DARK color from the background color.
     */
    static Color calculateDarkColor(Color bg) {
        return GTKColorType.adjustColor(bg, 1.0f, .7f, .7f);
    }

    /**
     * Calculates the MID color from the light and dark colors.
     */
    static Color calculateMidColor(Color lightColor, Color darkColor) {
        int light = lightColor.getRGB();
        int dark = darkColor.getRGB();
        int rLight = (light & 0xFF0000) >> 16;
        int rDark = (dark & 0xFF0000) >> 16;
        int gLight = (light & 0x00FF00) >> 8;
        int gDark = (dark & 0x00FF00) >> 8;
        int bLight = (light & 0xFF);
        int bDark = (dark & 0xFF);
        return new ColorUIResource((((rLight + rDark) / 2) << 16) |
                                   (((gLight + gDark) / 2) << 8) |
                                   ((bLight + bDark) / 2));
    }

    /**
     * Calculates the MID color from the background color.
     */
    static Color calculateMidColor(Color bg) {
        return calculateMidColor(calculateLightColor(bg),
                                 calculateDarkColor(bg));
    }

    /**
     * Creates an array of colors populated based on the passed in
     * the background color. Specifically this sets the
     * BACKGROUND, LIGHT, DARK, MID, BLACK, WHITE and FOCUS colors
     * from that of color, which is assumed to be the background.
     */
    static Color[] getColorsFrom(Color bg, Color fg) {
        Color lightColor = calculateLightColor(bg);
        Color darkColor = calculateDarkColor(bg);
        Color midColor = calculateMidColor(lightColor, darkColor);
        Color[] colors = new Color[GTKColorType.MAX_COUNT];
        colors[GTKColorType.BACKGROUND.getID()] = bg;
        colors[GTKColorType.LIGHT.getID()] = lightColor;
        colors[GTKColorType.DARK.getID()] = darkColor;
        colors[GTKColorType.MID.getID()] = midColor;
        colors[GTKColorType.BLACK.getID()] = BLACK_COLOR;
        colors[GTKColorType.WHITE.getID()] = WHITE_COLOR;
        colors[GTKColorType.FOCUS.getID()] = BLACK_COLOR;
        colors[GTKColorType.FOREGROUND.getID()] = fg;
        colors[GTKColorType.TEXT_FOREGROUND.getID()] = fg;
        colors[GTKColorType.TEXT_BACKGROUND.getID()] = WHITE_COLOR;
        return colors;
    }

    /**
     * Creates a new GTKStyle that is a copy of the passed in style.
     */
    public GTKStyle(DefaultSynthStyle style) {
        super(style);
        if (style instanceof GTKStyle) {
            GTKStyle gStyle = (GTKStyle)style;
            xThickness = gStyle.xThickness;
            yThickness = gStyle.yThickness;
            icons = gStyle.icons;
            classSpecificValues = cloneClassSpecificValues(
                                       gStyle.classSpecificValues);
        }
    }

    /**
     * Creates an empty GTKStyle.
     */
    public GTKStyle() {
        super(new Insets(-1, -1, -1, -1), true, null, null);
    }

    /**
     * Creates a GTKStyle with the specified font.
     *
     * @param font Font to use in GTK.
     */
    public GTKStyle(Font font) {
        this();
        setFont(font);
    }

    /**
     * Creates a GTKStyle with the specified parameters.
     *
     * @param states StateInfo specifying the colors and fonts to use for
     *        a particular state.
     * @param classSpecificValues Values that are specific to a particular
     *        class
     * @param font to use.
     * @param xThickness X thickness
     * @param yThickness Y thickness
     * @param GTKStockIconInfo stock icons for this style.
     */
    GTKStyle(StateInfo[] states,
                    CircularIdentityList classSpecificValues,
                    Font font,
                    int xThickness, int yThickness,
                    GTKStockIconInfo[] icons) {
        super(new Insets(-1, -1, -1, -1), true, states, null);
        setFont(font);
        this.xThickness = xThickness;
        this.yThickness = yThickness;
        this.icons = icons;
        this.classSpecificValues = classSpecificValues;
    }

    /**
     * {@inheritDoc}
     */
    public void installDefaults(SynthContext context) {
        super.installDefaults(context);
        if (!context.getRegion().isSubregion()) {
            context.getComponent().putClientProperty(
                SwingUtilities2.AA_TEXT_PROPERTY_KEY,
                GTKLookAndFeel.aaText);
        }        
    }

    public SynthGraphicsUtils getGraphicsUtils(SynthContext context) {
        return GTK_GRAPHICS;
    }

    /**
     * Returns the object used to renderer the look.
     *
     * @param context SynthContext indentifying requestor
     * @return GTKEngine used to provide the look
     */
    public GTKEngine getEngine(SynthContext context) {
        GTKEngine engine = (GTKEngine)get(context, "engine");

        if (engine == null) {
            return GTKEngine.INSTANCE;
        }
        return engine;
    }

    /**
     * Returns a <code>SynthPainter</code> that will route the appropriate
     * calls to a <code>GTKEngine</code>.
     *
     * @param state SynthContext indentifying requestor
     * @return SynthPainter
     */
    public SynthPainter getPainter(SynthContext state) {
        return GTKPainter.INSTANCE;
    }

    /**
     * Returns the Insets. If <code>to</code> is non-null the resulting
     * insets will be placed in it, otherwise a new Insets object will be
     * created and returned.
     *
     * @param context SynthContext indentifying requestor
     * @param to Where to place Insets
     * @return Insets.
     */
    public Insets getInsets(SynthContext state, Insets insets) {
        insets = super.getInsets(state, insets);

        if (insets.top == -1) {
            insets.left = insets.right = insets.top = insets.bottom = 0;
            insets = GTKPainter.INSTANCE.getInsets(state, insets);
        }
        return insets;
    }

    /**
     * Returns the value for a class specific property. A class specific value
     * is a value that will be picked up based on class hierarchy.
     * For example, a value specified for JComponent would be inherited on
     * JButtons and JTrees, but not Button.
     * <p>
     * Note, the key used here should only contain the letters A-Z, a-z, the
     * digits 0-9, and the '-' character. If you need to request a value for
     * a key having characters outside this list, replace any other characters
     * with '-'. (ie. "default_border" should be "default-border").
     *
     * @param region Region requesting class specific value
     * @param key Key identifying class specific value
     * @return Value, or null if one has not been defined.
     */
    public Object getClassSpecificValue(Region region, String key) {
        if (classSpecificValues != null) {
            String gtkClass = GTKStyleFactory.gtkClassFor(region);

            while (gtkClass != null) {
                CircularIdentityList classValues = (CircularIdentityList)
                                classSpecificValues.get(gtkClass);

                if (classValues != null) {
                    Object value = classValues.get(key);

                    if (value != null) {
                        return value;
                    }
                }
                gtkClass = GTKStyleFactory.gtkSuperclass(gtkClass);
            }
        }
        return null;
    }

    /**
     * Returns the value for a class specific property. A class specific value
     * is a value that will be picked up based on class hierarchy.
     * For example, a value specified for JComponent would be inherited on
     * JButtons and JTrees, but not Button.
     * <p>
     * Note, the key used here should only contain the letters A-Z, a-z, the
     * digits 0-9, and the '-' character. If you need to request a value for
     * a key having characters outside this list, replace any other characters
     * with '-'. (ie. "default_border" should be "default-border").
     *
     * @param context SynthContext indentifying requestor
     * @param key Key identifying class specific value
     * @return Value, or null if one has not been defined.
     */
    public Object getClassSpecificValue(SynthContext context, String key) {
        return getClassSpecificValue(context.getRegion(), key);
    }
    
    /**
     * Convenience method to get a class specific integer value.
     *
     * @param context SynthContext indentifying requestor
     * @param key Key identifying class specific value
     * @param defaultValue Returned if there is no value for the specified
     *        type
     * @return Value, or defaultValue if <code>key</code> is not defined
     */
    public int getClassSpecificIntValue(SynthContext context, String key,
                                           int defaultValue) {
        Object value = getClassSpecificValue(context, key);

        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return defaultValue;
    }

    /**
     * Convenience method to get a class specific Insets value.
     *
     * @param context SynthContext indentifying requestor
     * @param key Key identifying class specific value
     * @param defaultValue Returned if there is no value for the specified
     *        type
     * @return Value, or defaultValue if <code>key</code> is not defined
     */
    public Insets getClassSpecificInsetsValue(SynthContext context, String key,
                                              Insets defaultValue) {
        Object value = getClassSpecificValue(context, key);

        if (value instanceof Insets) {
            return (Insets)value;
        }
        return defaultValue;
    }

    /**
     * Convenience method to get a class specific Boolean value.
     *
     * @param context SynthContext indentifying requestor
     * @param key Key identifying class specific value
     * @param defaultValue Returned if there is no value for the specified
     *        type
     * @return Value, or defaultValue if <code>key</code> is not defined
     */
    public boolean getClassSpecificBoolValue(SynthContext context, String key,
                                             boolean defaultValue) {
        Object value = getClassSpecificValue(context, key);

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }
        return defaultValue;
    }

    public Object getDefaultValue(SynthContext context, Object key) {
        // See if this is a class specific value.
        Object classKey = CLASS_SPECIFIC_MAP.get(key);
        Object value = null;

        if (classKey != null) {
            value = getClassSpecificValue(context, (String)classKey);
            if (value != null) {
                return value;
            }
        }
            
        if (key == "ScrollPane.viewportBorderInsets") {
            return GTKPainter.INSTANCE.getScrollPaneInsets(context,
                                                     new Insets(0,0,0,0));
        } else if (key == "Slider.tickColor") {
            return getColor(context.getComponent(), context.getRegion(),
                            context.getComponentState(), ColorType.FOREGROUND);
        }
        synchronized (DATA) {
            value = DATA.get(key);
        }
        if (value instanceof StyleSpecificValue) {
            put(key, ((StyleSpecificValue)value).getValue(context));
        }
        if (value == null && key != "engine") {
            // For backward compatability we'll fallback to the UIManager.
            // We don't go to the UIManager for engine as the engine is GTK
            // specific.
            value = UIManager.get(key);
            if (key == "Table.rowHeight") {
                int focusLineWidth = getClassSpecificIntValue(
                         context, "focus-line-width", 0);
                if (value == null && focusLineWidth > 0) {
                    value = new Integer(16 + 2 * focusLineWidth);
                }
            }
        }
        // Don't call super, we don't want to pick up defaults from
        // SynthStyle.
        return value;
    }

    /**
     * Returns the font for the specified state. This should NOT callback
     * to the JComponent.
     *
     * @param c JComponent the style is associated with
     * @param id Region identifier
     * @param state State of the region.
     * @return Font to render with
     */
    protected Font getFontForState(JComponent c, Region id, int state) {
        state = GTKLookAndFeel.synthStateToGTKState(id, state);

        Font f = super.getFontForState(c, id, state);

        if (f == null) {
            return DEFAULT_FONT;
        }
        return f;
    }

    Color getGTKColor(int state, ColorType type) {
        return getGTKColor(null, null, state, type);
    }

    /**
     * This method is to be used from within GTK when do NOT want
     * the component state to be mapped. It will NOT remap the state as
     * the other various getters do.
     *
     * @param c JComponent the style is associated with
     * @param id Region identifier
     * @param state State of the region.
     * @param type Type of color being requested.
     * @return Color to render with
     */
    public Color getGTKColor(JComponent c, Region id,
                      int state, ColorType type) {
        // NOTE: c and id are only ever null when this is called from
        // GTKLookAndFeel.loadSystemColorDefaults.
        if (c != null && id != null) {
            if (!id.isSubregion() &&
                (state & SynthConstants.ENABLED) == SynthConstants.ENABLED) {
                if (type == ColorType.BACKGROUND) {
                    Color bg = c.getBackground();
                    if (!(bg instanceof UIResource)) {
                        return bg;
                    }
                }
                else if (type == ColorType.FOREGROUND) {
                    Color fg = c.getForeground();
                    if (!(fg instanceof UIResource)) {
                        return fg;
                    }
                }
                else if (type == ColorType.TEXT_FOREGROUND) {
                    Color fg = c.getForeground();
                    if (!(fg instanceof UIResource)) {
                        return fg;
                    }
                }
                else if (type == ColorType.TEXT_BACKGROUND) {
                    Color bg = c.getBackground();
                    if (!(bg instanceof UIResource)) {
                        return bg;
                    }
                }
            }
        }
        Color color = super.getColorForState(c, id, state, type);
        if (color != null) {
            return color;
        }
        return getDefaultColor(c, id, state, type);
    }

    /**
     * getColor is overriden to map the state from a Synth state to the
     * GTK state, this should be not used when you have already mapped the
     * state. Additionally this will map TEXT_FOREGROUND to the
     * <code>c.getForeground()</code> if it is non-null and not a UIResource.
     *
     * @param c JComponent the style is associated with
     * @param id Region identifier
     * @param state State of the region.
     * @param type Type of color being requested.
     * @return Color to render with
     */
    public Color getColor(JComponent c, Region id, int state,
                          ColorType type) {
        if (id == Region.LABEL && type == ColorType.TEXT_FOREGROUND) {
            type = ColorType.FOREGROUND;
        }
        state = GTKLookAndFeel.synthStateToGTKState(id, state);
        if (!id.isSubregion() &&
                (state & SynthConstants.ENABLED) == SynthConstants.ENABLED) {
            if (type == ColorType.BACKGROUND) {
                return c.getBackground();
            }
            else if (type == ColorType.FOREGROUND) {
                return c.getForeground();
            }
            else if (type == ColorType.TEXT_FOREGROUND) {
                Color fg = c.getForeground();
                if (fg != null && !(fg instanceof UIResource)) {
                    // Only use the fg for text if specified.
                    return fg;
                }
            }
        }
        return getColorForState(c, id, state, type);
    }

    /**
     * Returns the color for the specified state. This redirects to the
     * JComponent <code>c</code> as necessary. If this does not redirect
     * to the JComponent <code>getColorForState</code> is invoked.
     *
     * @param c JComponent the style is associated with
     * @param id Region identifier
     * @param state State of the region.
     * @param type Type of color being requested.
     * @return Color to render with
     */
    protected Color getColorForState(JComponent c, Region id, int state,
                                     ColorType type) {
        Color color = super.getColorForState(c, id, state, type);

        if (color != null) {
            return color;
        }
        if (type == ColorType.FOCUS) {
            return BLACK_COLOR;
        }
        else if (type == GTKColorType.BLACK) {
            return BLACK_COLOR;
        }
        else if (type == GTKColorType.WHITE) {
            return WHITE_COLOR;
        }
        if (type == ColorType.TEXT_FOREGROUND && (GTKStyleFactory.
                    isLabelBearing(id) || id == Region.MENU_ITEM_ACCELERATOR ||
                    id == Region.TABBED_PANE_TAB)) {
            type = ColorType.FOREGROUND;
        }
        else if (id == Region.TABLE || id == Region.LIST ||
                 id == Region.TREE || id == Region.TREE_CELL){
            if (type == ColorType.FOREGROUND) {
                type = ColorType.TEXT_FOREGROUND;
                if (state == SynthConstants.PRESSED) {
                    state = SynthConstants.SELECTED;
                }
            }
            else if (type == ColorType.BACKGROUND) {
                type = ColorType.TEXT_BACKGROUND;
                if (state == SynthConstants.PRESSED) {
                    state = SynthConstants.SELECTED;
                }
            }
        }
        return getDefaultColor(c, id, state, type);
    }

    /**
     * Returns the Color to use if the GTKStyle does not specify a color.
     *
     * @param c JComponent the style is associated with
     * @param id Region identifier
     * @param state State of the region.
     * @param type Type of color being requested.
     * @return Color to render with
     */
    Color getDefaultColor(JComponent c, Region id, int state,
                          ColorType type) {
        if (type == ColorType.FOCUS) {
            return BLACK_COLOR;
        }
        else if (type == GTKColorType.BLACK) {
            return BLACK_COLOR;
        }
        else if (type == GTKColorType.WHITE) {
            return WHITE_COLOR;
        }
        for (int counter = DEFAULT_COLOR_MAP.length - 1;
                     counter >= 0; counter--) {
            if ((DEFAULT_COLOR_MAP[counter] & state) != 0) {
                if (type.getID() < DEFAULT_COLORS[counter].length) {
                    return DEFAULT_COLORS[counter][type.getID()];
                }
            }
        }
        if (type.getID() < DEFAULT_COLORS[2].length) {
            return DEFAULT_COLORS[2][type.getID()];
        }
        return null;
    }

    /**
     * Returns the value to initialize the opacity property of the Component
     * to. A Style should NOT assume the opacity will remain this value, the
     * developer may reset it or override it.
     *
     * @param context SynthContext indentifying requestor
     * @return opaque Whether or not the JComponent is opaque.
     */
    public boolean isOpaque(SynthContext context) {
        Region region = context.getRegion();
        if (region == Region.COMBO_BOX ||
              region == Region.DESKTOP_PANE ||
              region == Region.DESKTOP_ICON ||
              region == Region.EDITOR_PANE ||
              region == Region.FORMATTED_TEXT_FIELD ||
              region == Region.INTERNAL_FRAME ||
              region == Region.LIST ||
              region == Region.MENU_BAR ||
              region == Region.PASSWORD_FIELD || 
              region == Region.POPUP_MENU ||
              region == Region.PROGRESS_BAR ||
              region == Region.ROOT_PANE ||
              region == Region.SCROLL_PANE ||
              region == Region.SPINNER ||
              region == Region.TABLE ||
              region == Region.TEXT_AREA ||
              region == Region.TEXT_FIELD ||
              region == Region.TEXT_PANE ||
              region == Region.TOOL_BAR_DRAG_WINDOW ||
              region == Region.TOOL_TIP ||
              region == Region.TREE ||
              region == Region.VIEWPORT) {
            return true;
        }
        Component c = context.getComponent();
        String name = c.getName();
        if (name == "ComboBox.renderer" || name == "ComboBox.listRenderer") {
            return true;
        }
        return false;
    }

    /**
     * Returns the X thickness to use for this GTKStyle.
     *
     * @return x thickness.
     */
    public int getXThickness() {
        return xThickness;
    }

    /**
     * Returns the Y thickness to use for this GTKStyle.
     *
     * @return x thickness.
     */
    public int getYThickness() {
        return yThickness;
    }

    private Icon getStockIcon(SynthContext context, String key, int type) {
        Icon icon = null;
        GTKStockIconInfo iconInfo = null;
        GTKIconSource bestSource = null;
        int direction = LTR;
        
        if (context != null) {
            ComponentOrientation co = context.getComponent().
                                              getComponentOrientation();

            if (co == null || co.isLeftToRight()) {
                direction = LTR;
            }
            else {
                direction = RTL;
            }
        }
        // See if the style defines an icon
        if (icons != null) {
            for (int i = 0; i < icons.length; i++) {
                // find the first one that matches our key
                if (icons[i].getKey() == key) {
                    iconInfo = icons[i];
                    break;
                }
            }
            
            if (iconInfo != null) {
                // PENDING(shannonh) - pass in actual state
                bestSource = iconInfo.getBestIconSource(direction,
                                                        SynthConstants.ENABLED,
                                                        type);
            }
            
            if (bestSource != null) {
                icon = bestSource.toIcon();
            }
        }
        
        if (icon == null) {
            // Use a default icon
            String propName = ICON_PROPERTY_PREFIX + key + '.' + type + '.' +
                              (direction == RTL ? "rtl" : "ltr"); 
            Image img = (Image)Toolkit.getDefaultToolkit().
                                       getDesktopProperty(propName);
            if (img != null) {
                icon = new ImageIcon(img);
                return icon;
            } else {
                icon = (Icon)((UIDefaults.LazyValue)LookAndFeel.makeIcon(
                              GTKStyle.class, "resources/" + key + "-" + type +
                              ".png")).createValue(null);
            }
        }
        
        if (icon == null) {
            return null;
        }
        BufferedImage image = null; 

        // If the stock icon we found had a wildcard size, 
        // we force the size to match that requested 
        if (bestSource == null || bestSource.getSize() == UNDEFINED) { 
            Dimension iconSize = GTKStockIconInfo.getIconSize(type); 
            
            if (iconSize != null && (icon.getIconWidth() != iconSize.width || 
                    icon.getIconHeight() != iconSize.height)) { 
                image = new BufferedImage(iconSize.width, iconSize.height, 
                        BufferedImage.TYPE_INT_ARGB); 
                
                Graphics2D g2d = (Graphics2D)image.getGraphics(); 
                
                // for nicer scaling 
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR); 
                
                Image oldImage = getImageFromIcon(icon, false); 
                g2d.drawImage(oldImage, 0, 0, iconSize.width, iconSize.height, null); 
                g2d.dispose(); 
            }
        }
/* This is not done for now. We cache icons and use cached copies regardless
   of the component state, so we don't want to cache desaturated icons
   
        if (bestSource == null || bestSource.getState() == UNDEFINED) { 
            // We may need to change saturation for some states
            int state = context.getComponentState();
            if (state == SynthConstants.DISABLED ||
                state == SynthConstants.MOUSE_OVER) {
                
                if (image == null) {
                    image = (BufferedImage)getImageFromIcon(icon, true);
                }
                float rescaleFactor =
                        (state == SynthConstants.DISABLED ? 0.8f : 1.2f); 
                RescaleOp op = new RescaleOp(rescaleFactor, 0, null);
                // RescaleOp allows for in-place filtering
                op.filter(image, image);
            }
        }
*/        
        if (image != null) {
            icon = new ImageIcon(image);
        }
        return icon;
    }
    
    private Image getImageFromIcon(Icon icon, boolean requireBufferedImage) {
        Image img = null;
        
        if (icon instanceof ImageIcon) { 
            img = ((ImageIcon)icon).getImage();
            if (requireBufferedImage && !(img instanceof BufferedImage)) {
                img = null;
            }
        }
        if (img == null) { 
            img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                                    BufferedImage.TYPE_INT_ARGB);                             
            Graphics g = img.getGraphics(); 
            icon.paintIcon(null, g, 0, 0); 
            g.dispose(); 
        } 
        return img;
    }
    
    /**          
     * Adds the specific label based properties from <code>style</code> to
     * this style.
     */
    void addLabelProperties(GTKStyle style) {
        StateInfo[] states = getStateInfo();
        StateInfo[] oStates = style.getStateInfo();
        // Take the font
        setFont(style.getFontForState(null, null, 0));
        // And TEXT_FOREGROUND
        if (states == null) {
            if (oStates == null) {
                return;
            }
            states = new StateInfo[oStates.length];
            for (int counter = 0; counter < oStates.length; counter++) {
                Color color = oStates[counter].getColor(
                                     GTKColorType.FOREGROUND);

                states[counter] = createStateInfo(oStates[counter].
                     getComponentState(), GTKColorType.TEXT_FOREGROUND, color);
            }
        }
        else {
            // Reset the text foreground of all our states, this will ensure
            // the correct color is picked up if style doesn't specify a
            // text color.
            for (int counter = states.length - 1; counter >= 0; counter--) {
                ((GTKStateInfo)states[counter]).setColor(
                               GTKColorType.TEXT_FOREGROUND, null);
            }
            if (oStates != null) {
                for (int oCounter = oStates.length - 1; oCounter >= 0;
                         oCounter--) {
                    boolean matched = false;
                    StateInfo oState = oStates[oCounter];
                    int componentState = oState.getComponentState();
                    Color color = oState.getColor(GTKColorType.FOREGROUND);

                    for (int tCounter = states.length - 1; tCounter >= 0;
                             tCounter--) {
                        if (componentState == states[tCounter].
                                     getComponentState()) {
                            ((GTKStateInfo)states[tCounter]).setColor(
                                      GTKColorType.TEXT_FOREGROUND, color);
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        StateInfo[] newStates = new StateInfo[states.length+1];
                        System.arraycopy(states, 0, newStates, 0,
                                         states.length);
                        newStates[states.length] = createStateInfo(
                                 componentState, GTKColorType.TEXT_FOREGROUND,
                                 color);
                        states = newStates;
                    }
                }
            }
        }
    }

    /**
     * Creates a StateInfo with the specified component state, ColorType
     * and color. Subclasses that create a custom GTKStateInfo will need
     * to subclass and override this.
     */
    GTKStateInfo createStateInfo(int state, ColorType type, Color color) {
        Color[] colors = new Color[GTKColorType.MAX_COUNT];

        colors[type.getID()] = color;
        return new GTKStateInfo(state, null, colors, null);
    }

    /**
     * Adds a value specific to the style.
     */
    void put(Object key, Object value) {
        Map data = getData();
        if (data== null) {
            data = new HashMap();
            setData(data);
        }
        data.put(key, value);
    }

    /**
     * Returns true if the style should fill in the background of the
     * specified context for the specified state.
     */
    boolean fillBackground(SynthContext context, int state) {
        GTKStateInfo info = (GTKStateInfo)getStateInfo(state);

        if (info != null) {
            Object backgroundImage = info.getBackgroundImage();

            if (backgroundImage == "<none>" || backgroundImage == null) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Returns the Icon to fill the background in with for the specified
     * context and state.
     */
    Image getBackgroundImage(SynthContext context, int state) {
        GTKStateInfo info = (GTKStateInfo)getStateInfo(state);

        if (info != null) {
            Object backgroundImage = info.getBackgroundImage();

            if (backgroundImage instanceof Image) {
                return (Image)backgroundImage;
            }
        }
        return null;
    }

    /**
     * Creates a clone of this GTKStyle.
     *
     * @return Clone of this GTKStyle.
     */
    public Object clone() {
        GTKStyle style = (GTKStyle)super.clone();

        style.classSpecificValues = cloneClassSpecificValues(
                                         style.classSpecificValues);
        return style;
    }

    /**
     * Merges the contents of this Style with that of the passed in Style,
     * returning the resulting merged syle. Properties of this
     * <code>GTKStyle</code> will take precedence over those of the
     * passed in <code>DefaultSynthStyle</code>. For example, if this
     * style specifics a non-null font, the returned style will have its
     * font so to that regardless of the <code>style</code>'s font.
     *
     * @param style Style to add our styles to
     * @return Merged style.
     */
    public DefaultSynthStyle addTo(DefaultSynthStyle style) {
        if (!(style instanceof GTKStyle)) {
            style = new GTKStyle(style);
        }
        GTKStyle gtkStyle = (GTKStyle)super.addTo(style);
        if (xThickness != UNDEFINED_THICKNESS) {
            gtkStyle.xThickness = xThickness;
        }
        if (yThickness != UNDEFINED_THICKNESS) {
            gtkStyle.yThickness = yThickness;
        }
        if (gtkStyle.icons == null) {
            gtkStyle.icons = icons;
        }
        else if (icons != null) {
            GTKStockIconInfo[] mergedIcons =
                new GTKStockIconInfo[gtkStyle.icons.length + icons.length];
                
            System.arraycopy(icons, 0, mergedIcons, 0, icons.length);
            System.arraycopy(gtkStyle.icons, 0, mergedIcons, icons.length, gtkStyle.icons.length);
            
            gtkStyle.icons = mergedIcons;
        }
        
        if (gtkStyle.classSpecificValues == null) {
            gtkStyle.classSpecificValues =
                cloneClassSpecificValues(classSpecificValues);
        } else {
            addClassSpecificValues(classSpecificValues, gtkStyle.classSpecificValues);
        }
            
        return gtkStyle;
    }

    /**
     * Adds the data from one set of class specific values into another.
     *
     * @param from the list to grab data from (may be null)
     * @param to   the list to add data to (may not be null)
     */
    static void addClassSpecificValues(CircularIdentityList from,
                                        CircularIdentityList to) {
        if (to == null) {
            throw new IllegalArgumentException("to may not be null");
        }
        
        if (from == null) {
            return;
        }

        synchronized(from) {
            Object firstKey = from.next();
            if (firstKey != null) {
                Object key = firstKey;
                do {
                    CircularIdentityList cList = ((CircularIdentityList)
                            from.get());
                    CircularIdentityList oSublist = (CircularIdentityList)
                                     to.get(key);
                    if (oSublist == null) {
                        to.set(key, cList.clone());
                    }
                    else {
                        Object cFirstKey = cList.next();

                        if (cFirstKey != null) {
                            Object cKey = cFirstKey;
                            do {
                                oSublist.set(cKey, cList.get());
                                cKey = cList.next();
                            } while (cKey != cFirstKey);
                        }
                    }
                    key = from.next();
                } while (key != firstKey);
            }
        }
    }

    /**
     * Clones the class specific values.
     */
    static CircularIdentityList cloneClassSpecificValues(
                    CircularIdentityList list) {
        if (list == null) {
            return null;
        }
        CircularIdentityList clone;
        synchronized(list) {
            Object firstKey = list.next();
            if (firstKey == null) {
                // Empty list
                return null;
            }
            clone = new CircularIdentityList();
            Object key = firstKey;
            do {
                clone.set(key, ((CircularIdentityList)list.get()).clone());
                key = list.next();
            } while (key != firstKey);
        }
        return clone;
    }

    /**    
     * GTKStockIconInfo mirrors the information from a stock declaration
     * in the rc file: stock icon id, and a set of icon source
     * specifications.
     */
    static class GTKStockIconInfo {
        private String key;
        private GTKIconSource[] sources;
        private static Map<String,Integer> ICON_TYPE_MAP;
        private static final Object ICON_SIZE_KEY = new Object(); // IconSize
        
        GTKStockIconInfo(String key, GTKIconSource[] sources) {
            this.key = key.intern();
            this.sources = sources;
            Arrays.sort(this.sources);
        }
        
        public String getKey() {
            return key;
        }
        
        public GTKIconSource getBestIconSource(int direction, int state, int size) {
            for (int i = 0; i < sources.length; i++) {
                GTKIconSource src = sources[i];
                
                if ((src.direction == UNDEFINED || src.direction == direction)
                        && (src.state == UNDEFINED || src.state == state)
                        && (src.size == UNDEFINED || src.size == size)) {
                    return src;
                }
            }
            
            return null;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer("STOCK ICON " + key + ":\n");
            
            for (int i = 0; i < sources.length; i++) {
                buf.append("    ").append(sources[i].toString()).append('\n');
            }
            
            // remove last newline
            buf.deleteCharAt(buf.length() - 1);
            
            return buf.toString();
        }
        
        /**
         * Return icon type (GtkIconSize value) given a symbolic name which can
         * occur in a theme file.
         * 
         * @param size symbolic name, e.g. gtk-button
         * @return icon type. Valid types are 1 to 6 
         */ 
        public static int getIconType(String size) {
            if (size == null) {
                return UNDEFINED;
            }
            if (ICON_TYPE_MAP == null) {
                initIconTypeMap();
            }
            Integer n = ICON_TYPE_MAP.get(size);
            return n != null ? n.intValue() : UNDEFINED;
        }
        
        private static void initIconTypeMap() {
            ICON_TYPE_MAP = new HashMap<String,Integer>();
            ICON_TYPE_MAP.put("gtk-menu", new Integer(1));
            ICON_TYPE_MAP.put("gtk-small-toolbar", new Integer(2));
            ICON_TYPE_MAP.put("gtk-large-toolbar", new Integer(3));
            ICON_TYPE_MAP.put("gtk-button", new Integer(4));
            ICON_TYPE_MAP.put("gtk-dnd", new Integer(5));
            ICON_TYPE_MAP.put("gtk-dialog", new Integer(6));
        }

        /** 
         * Return the size of a particular icon type (logical size)
         * 
         * @param type icon type (GtkIconSize value)
         * @return a Dimension object, or null if lsize is invalid
         */
        public static Dimension getIconSize(int type) {
            Dimension[] iconSizes = getIconSizesMap();
            return type >= 0 && type < iconSizes.length ?
                iconSizes[type] : null;  
        }
        
        /**
         * Change icon size in a type to size mapping. This is called by code
         * that parses the gtk-icon-sizes setting
         *  
         * @param type icon type (GtkIconSize value)
         * @param w the new icon width
         * @param h the new icon height
         */ 
        public static void setIconSize(int type, int w, int h) {
            Dimension[] iconSizes = getIconSizesMap();
            if (type >= 0 && type < iconSizes.length) {
                iconSizes[type] = new Dimension(w, h);
            }
        }
        
        private static Dimension[] getIconSizesMap() {
            AppContext appContext = AppContext.getAppContext(); 
            Dimension[] iconSizes = (Dimension[])appContext.get(ICON_SIZE_KEY); 

            if (iconSizes == null) { 
                iconSizes = new Dimension[7];
                iconSizes[0] = null;                  // GTK_ICON_SIZE_INVALID
                iconSizes[1] = new Dimension(16, 16); // GTK_ICON_SIZE_MENU
                iconSizes[2] = new Dimension(18, 18); // GTK_ICON_SIZE_SMALL_TOOLBAR
                iconSizes[3] = new Dimension(24, 24); // GTK_ICON_SIZE_LARGE_TOOLBAR
                iconSizes[4] = new Dimension(20, 20); // GTK_ICON_SIZE_BUTTON
                iconSizes[5] = new Dimension(32, 32); // GTK_ICON_SIZE_DND
                iconSizes[6] = new Dimension(48, 48); // GTK_ICON_SIZE_DIALOG
                appContext.put(ICON_SIZE_KEY, iconSizes);
            }
            return iconSizes;
        }
    }


    /**
     * GTKIconSource represents a single icon source specification,
     * as declared inside a stock definition in an rc file:
     * path to image, size, direction, and state.
     */
    static class GTKIconSource implements Comparable {
        private Object image;
        private int direction;
        private int state;
        private int size;
        
        GTKIconSource(String image, int direction, int state, String size) {
            this.image = image;
            this.direction = direction;
            this.state = state;
            
            this.size = GTKStockIconInfo.getIconType(size);
        }

        public int getDirection() {
            return direction;
        }
        
        public int getState() {
            return state;
        }

        public int getSize() {
            return size;
        }
        
        public int compareTo(Object o) {
            GTKIconSource other = (GTKIconSource)o;            

            if (direction != UNDEFINED && other.direction == UNDEFINED) {
                return -1;
            } else if (direction == UNDEFINED && other.direction != UNDEFINED) {
                return 1;
            } else if (state != UNDEFINED && other.state == UNDEFINED) {
                return -1;
            } else if (state == UNDEFINED && other.state != UNDEFINED) {
                return 1;
            } else if (size != UNDEFINED && other.size == UNDEFINED) {
                return -1;
            } else if (size == UNDEFINED && other.size != UNDEFINED) {
                return 1;
            } else {
                return 0;
            }
        }

        public String toString() {
            return "image=" + image + ", dir=" + getDirectionName(direction)
                   + ", state=" + getStateName(state, "*")
                   + ", size=" + (size == UNDEFINED ? "*" : ""+size);
        }
        
        // used above by toString()
        private static String getDirectionName(int dir) {
            switch(dir) {
                case LTR: return "LTR";
                case RTL: return "RTL";
                case UNDEFINED: return "*";
            }

            return "UNKNOWN";
        }
        
        public Icon toIcon() {
            if (image == null || image instanceof Icon) {
                return (Icon)image;
            }
            
            ImageIcon ii = (ImageIcon)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new ImageIcon((String)image);
                }
            });

            if (ii.getIconWidth() > 0 && ii.getIconHeight() > 0) {
                image = ii;
            } else {
                // if we decide to mimic GTK and show a broken image,
                // it would be assigned to 'image' here
                image = null;
            }
            
            return (Icon)image;
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());

        if (xThickness != UNDEFINED_THICKNESS) {
            buf.append("xt=").append(String.valueOf(xThickness)).append('\n');
        }

        if (yThickness != UNDEFINED_THICKNESS) {
            buf.append("yt=").append(String.valueOf(yThickness)).append('\n');
        }

        if (classSpecificValues != null) {
            buf.append("*** Properties ***\n");
            buf.append(classSpecValsToString(classSpecificValues)).append('\n');
        }

        if (icons != null) {
            buf.append("*** Stock Icons ***\n");
            for (int i = 0; i < icons.length; i++) {
                buf.append(icons[i].toString()).append('\n');
            }
        }

        // remove last newline
        buf.deleteCharAt(buf.length() - 1);

        return buf.toString();
    }

    // used by toString()
    private static String classSpecValsToString(CircularIdentityList parent) {
        StringBuffer buf = new StringBuffer();

        Object parentFirst = parent.next();
            
        if (parentFirst == null) {
            return "";
        }

        Object parentKey = parentFirst;

        do {
            buf.append(parentKey).append('\n');

            CircularIdentityList child = (CircularIdentityList)parent.get();

            Object childFirst = child.next();
                
            if (childFirst == null) {
                break;
            }
                    
            Object childKey = childFirst;
                    
            do {
                buf.append("    ").append(childKey).append('=').append(child.get()).append('\n');
                childKey = child.next();
            } while (childKey != childFirst);
            
            parentKey = parent.next();
        } while (parentKey != parentFirst);

        // remove last newline
        buf.deleteCharAt(buf.length() - 1);

        return buf.toString();
    }

    /**
     * A subclass of StateInfo adding additional GTK state information.
     */
    public static class GTKStateInfo extends StateInfo {
        // <none>: fill in with background color
        // <parent>: do nothing, parent will have handled it
        // image: paint it.
        private Object backgroundImage;

        /**
         * Creates a new GTKStateInfo with the specified properties
         *
         * @param state Component state that this StateInfo should be used
         * for
         * @param font Font for this state
         * @param colors Colors for this state
         * @param backgroundImage Background image
         */
        public GTKStateInfo(int state, Font font, Color[] colors,
                            Object backgroundImage) {
            super(state, font, colors);
            this.backgroundImage = backgroundImage;
        }

        /**
         * Creates a GTKStateInfo that is a copy of the passed in
         * <code>StateInfo</code>.
         *
         * @param info StateInfo to copy.
         */
        public GTKStateInfo(StateInfo info) {
            super(info);
            if (info instanceof GTKStateInfo) {
                backgroundImage = ((GTKStateInfo)info).backgroundImage;
            }
        }

        void setColor(ColorType type, Color color) {
            Color[] colors = getColors();
            if (colors == null) {
                if (color == null) {
                    return;
                }
                colors = new Color[GTKColorType.MAX_COUNT];
                setColors(colors);
            }
            colors[type.getID()] = color;
        }

        /**
         * Returns the Color to used for the specified ColorType.
         *
         * @return Color.
         */
        public Color getColor(ColorType type) {
            Color color = super.getColor(type);

            if (color == null) {
                Color[] colors = getColors();
                Color bg;

                if (colors != null && (bg = super.getColor(
                                        GTKColorType.BACKGROUND)) != null) {
                    if (type == GTKColorType.LIGHT) {
                        color = colors[GTKColorType.LIGHT.getID()] =
                                  calculateLightColor(bg);
                    }
                    else if (type == GTKColorType.MID) {
                        color = colors[GTKColorType.MID.getID()] =
                                       calculateMidColor(bg);
                    }
                    else if (type == GTKColorType.DARK) {
                        color = colors[GTKColorType.DARK.getID()] =
                                       calculateDarkColor(bg);
                    }
                }
            }
            return color;
        }

        /**
         * This returns the background image, and will be one of:
         * the String "<none>", the String "<parent>" or an Image.
         *
         * @return the background.
         */
        Object getBackgroundImage() {
            if (backgroundImage == null ||
                     (backgroundImage instanceof Image) ||
                     backgroundImage == "<none>" ||
                     backgroundImage == "<parent>") {
                return backgroundImage;
            }

            ImageIcon ii = (ImageIcon)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new ImageIcon((String)backgroundImage);
                }
            });

            if (ii.getIconWidth() > 0 && ii.getIconHeight() > 0) {
                backgroundImage = ii.getImage();
            } else {
                backgroundImage = null;
            }

            return backgroundImage;
        }

        /**
         * Creates and returns a copy of this GTKStateInfo.
         *
         * @return Copy of this StateInfo.
         */
        public Object clone() {
            return new GTKStateInfo(this);
        }

        /**
         * Merges the contents of this GTKStateInfo with that of the passed in
         * GTKStateInfo, returning the resulting merged StateInfo. Properties
         * of this <code>GTKStateInfo</code> will take precedence over those
         * of the
         * passed in <code>GTKStateInfo</code>. For example, if this
         * GTKStateInfo specifics a non-null font, the returned GTKStateInfo
         * will have its font so to that regardless of the
         * <code>GTKStateInfo</code>'s font.
         *
         * @param info StateInfo to add our styles to
         * @return Merged StateInfo.
         */
        public StateInfo addTo(StateInfo info) {
            if (!(info instanceof GTKStateInfo)) {
                info = new GTKStateInfo(info);
            }
            else {
                super.addTo(info);
            }
            GTKStateInfo gInfo = (GTKStateInfo)info;

            if (backgroundImage != null) {
                gInfo.backgroundImage = backgroundImage;
            }
            return gInfo;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            
            buf.append(getStateName(getComponentState(), "UNDEFINED")).append(":\n");
            
            if (getColor(GTKColorType.FOREGROUND) != null) {
                buf.append("    fg=").append(getColor(GTKColorType.FOREGROUND)).append('\n');
            }
            
            if (getColor(GTKColorType.BACKGROUND) != null) {
                buf.append("    bg=").append(getColor(GTKColorType.BACKGROUND)).append('\n');
            }
            
            if (getColor(GTKColorType.TEXT_FOREGROUND) != null) {
                buf.append("    text=").append(getColor(GTKColorType.TEXT_FOREGROUND)).append('\n');
            }
            
            if (getColor(GTKColorType.TEXT_BACKGROUND) != null) {
                buf.append("    base=").append(getColor(GTKColorType.TEXT_BACKGROUND)).append('\n');
            }
            
            if (backgroundImage != null) {
                buf.append("    pmn=").append(backgroundImage).append('\n');
            }
            
            // remove last newline
            buf.deleteCharAt(buf.length() - 1);

            return buf.toString();
        }
    }
    
    // used by toString() in some of our inner classes
    static String getStateName(int state, String undef) {
        switch(state) {
            case SynthConstants.ENABLED: return "NORMAL";
            case SynthConstants.PRESSED: return "ACTIVE";
            case SynthConstants.SELECTED: return "SELECTED";
            case SynthConstants.MOUSE_OVER: return "PRELIGHT";
            case SynthConstants.DISABLED: return "INSENSITIVE";
            case UNDEFINED: return undef;
        }
        
        return "UNKNOWN";
    }
    
    /**
     * A tagging interface indicating that a value coming from
     * DATA should be added to the Style's data after invoking getValue.
     * This is useful for lazy type properties that need to key off information
     * kept in the style.
     */
    interface StyleSpecificValue {
        public Object getValue(SynthContext context);
    }


    /**
     * An Icon that is fetched using getStockIcon.
     */
    private static class GTKStockIcon extends SynthIcon implements Cloneable,
                                              StyleSpecificValue {
        private String key;
        private int size;
        private boolean loadedLTR;
        private boolean loadedRTL;
        private Icon ltrIcon;
        private Icon rtlIcon;
        private SynthStyle style;

        GTKStockIcon(String key, int size) {
            this.key = key;
            this.size = size;
        }

        public void paintIcon(SynthContext context, Graphics g, int x,
                              int y, int w, int h) {
            Icon icon = getIcon(context);

            if (icon != null) {
                if (context == null) {
                    icon.paintIcon(null, g, x, y);
                }
                else {
                    icon.paintIcon(context.getComponent(), g, x, y);
                }
            }
        }

        public int getIconWidth(SynthContext context) {
            Icon icon = getIcon(context);

            if (icon != null) {
                return icon.getIconWidth();
            }
            return 0;
        }

        public int getIconHeight(SynthContext context) {
            Icon icon = getIcon(context);

            if (icon != null) {
                return icon.getIconHeight();
            }
            return 0;
        }

        private Icon getIcon(SynthContext context) {
            if (context != null) {
                ComponentOrientation co = context.getComponent().
                                                  getComponentOrientation();
                SynthStyle style = context.getStyle();

                if (style != this.style) {
                    this.style = style;
                    loadedLTR = loadedRTL = false;
                }
                if (co == null || co.isLeftToRight()) {
                    if (!loadedLTR) {
                        loadedLTR = true;
                        ltrIcon = ((GTKStyle)context.getStyle()).
                                  getStockIcon(context, key, size);
                    }
                    return ltrIcon;
                }
                else if (!loadedRTL) {
                    loadedRTL = true;
                    rtlIcon = ((GTKStyle)context.getStyle()).
                              getStockIcon(context, key,size);
                }
                return rtlIcon;
            }
            return ltrIcon;
        }

        public Object getValue(SynthContext context) {
            try {
                return clone();
            } catch (CloneNotSupportedException cnse) {
            }
            return null;
        }
    }


    /**
     * MetalLazyValue is a slimmed down version of <code>ProxyLaxyValue</code>.
     * The code is duplicate so that it can get at the package private
     * classes in gtk.
     */
    static class GTKLazyValue implements UIDefaults.LazyValue {
        /**
         * Name of the class to create.
         */
        private String className;
        private String methodName;

        GTKLazyValue(String name) {
            this(name, null);
        }

        GTKLazyValue(String name, String methodName) {
            this.className = name;
            this.methodName = methodName;
        }

        public Object createValue(UIDefaults table) {
            try {
                Class c = Class.forName(className, true,Thread.currentThread().
                                        getContextClassLoader());

                if (methodName == null) {
                    return c.newInstance();
                }
                Method m = c.getMethod(methodName, null);

                return m.invoke(c, null);
            } catch (ClassNotFoundException cnfe) {
            } catch (IllegalAccessException iae) {
            } catch (InvocationTargetException ite) {
            } catch (NoSuchMethodException nsme) {
            } catch (InstantiationException ie) {
            }
            return null;
        }
    }


    static {
        DEFAULT_COLOR_MAP = new int[] {
            SynthConstants.PRESSED, SynthConstants.SELECTED,
            SynthConstants.ENABLED, SynthConstants.MOUSE_OVER,
            SynthConstants.DISABLED
        };

        DEFAULT_COLORS = new Color[5][];

        // 2.0 colors
        // 
        if (!GTKLookAndFeel.is2_2()) {
            DEFAULT_COLORS[0] = getColorsFrom(
                    new ColorUIResource(195, 195, 195), BLACK_COLOR);
            DEFAULT_COLORS[1] = getColorsFrom(
                    new ColorUIResource(0, 0, 156), WHITE_COLOR);
            DEFAULT_COLORS[2] = getColorsFrom(
                    new ColorUIResource(214, 214, 214), BLACK_COLOR);
            DEFAULT_COLORS[3] = getColorsFrom(
                    new ColorUIResource(233, 233, 233), BLACK_COLOR);
            DEFAULT_COLORS[4] = getColorsFrom(
                    new ColorUIResource(214, 214, 214),
                    new ColorUIResource(117, 117, 117));
            DEFAULT_COLORS[0][GTKColorType.TEXT_BACKGROUND.getID()] = new
                    ColorUIResource(188, 210, 238);
            DEFAULT_COLORS[1][GTKColorType.TEXT_BACKGROUND.getID()] = new
                    ColorUIResource(164, 223, 255);
            DEFAULT_COLORS[1][GTKColorType.TEXT_FOREGROUND.getID()] =
                    BLACK_COLOR;
            DEFAULT_COLORS[2][GTKColorType.TEXT_FOREGROUND.getID()] =
                    BLACK_COLOR;
            DEFAULT_COLORS[4][GTKColorType.TEXT_FOREGROUND.getID()] =
                DEFAULT_COLORS[2][GTKColorType.TEXT_FOREGROUND.getID()];
        }
        else {
            // 2.2 colors
            DEFAULT_COLORS[0] = getColorsFrom(
                    new ColorUIResource(186, 181, 171), BLACK_COLOR);
            DEFAULT_COLORS[1] = getColorsFrom(
                    new ColorUIResource(75, 105, 131), WHITE_COLOR);
            DEFAULT_COLORS[2] = getColorsFrom(
                    new ColorUIResource(220, 218, 213), BLACK_COLOR);
            DEFAULT_COLORS[3] = getColorsFrom(
                    new ColorUIResource(238, 235, 231), BLACK_COLOR);
            DEFAULT_COLORS[4] = getColorsFrom(
                    new ColorUIResource(220, 218, 213),
                    new ColorUIResource(117, 117, 117));
            DEFAULT_COLORS[0][GTKColorType.TEXT_BACKGROUND.getID()] = new
                    ColorUIResource(128, 125, 116);
            DEFAULT_COLORS[1][GTKColorType.TEXT_BACKGROUND.getID()] = new
                    ColorUIResource(75, 105, 131);
            DEFAULT_COLORS[2][GTKColorType.TEXT_BACKGROUND.getID()] =
                    WHITE_COLOR;
            DEFAULT_COLORS[3][GTKColorType.TEXT_BACKGROUND.getID()] =
                    WHITE_COLOR;
            DEFAULT_COLORS[4][GTKColorType.TEXT_BACKGROUND.getID()] = new
                    ColorUIResource(238, 235, 231);
            DEFAULT_COLORS[0][GTKColorType.TEXT_FOREGROUND.getID()] =
                    WHITE_COLOR;
            DEFAULT_COLORS[1][GTKColorType.TEXT_FOREGROUND.getID()] =
                    WHITE_COLOR;
            DEFAULT_COLORS[2][GTKColorType.TEXT_FOREGROUND.getID()] =
                    BLACK_COLOR;
            DEFAULT_COLORS[3][GTKColorType.TEXT_FOREGROUND.getID()] =
                    BLACK_COLOR;
            DEFAULT_COLORS[4][GTKColorType.TEXT_FOREGROUND.getID()] = new
                    ColorUIResource(117, 117, 117);
        }

        CLASS_SPECIFIC_MAP = new HashMap();
        CLASS_SPECIFIC_MAP.put("CheckBox.iconTextGap", "indicator-spacing");
        CLASS_SPECIFIC_MAP.put("Slider.thumbHeight", "slider-width");
        CLASS_SPECIFIC_MAP.put("Slider.trackBorder", "trough-border");
        CLASS_SPECIFIC_MAP.put("SplitPane.size", "handle-size");
        CLASS_SPECIFIC_MAP.put("Tree.expanderSize", "expander-size");
        CLASS_SPECIFIC_MAP.put("ScrollBar.thumbHeight", "slider-width");
        CLASS_SPECIFIC_MAP.put("TextArea.caretForeground", "cursor-color");
        CLASS_SPECIFIC_MAP.put("TextArea.caretAspectRatio", "cursor-aspect-ratio");
        CLASS_SPECIFIC_MAP.put("TextField.caretForeground", "cursor-color");
        CLASS_SPECIFIC_MAP.put("TextField.caretAspectRatio", "cursor-aspect-ratio");
        CLASS_SPECIFIC_MAP.put("PasswordField.caretForeground", "cursor-color");
        CLASS_SPECIFIC_MAP.put("PasswordField.caretAspectRatio", "cursor-aspect-ratio");
        CLASS_SPECIFIC_MAP.put("FormattedTextField.caretForeground", "cursor-color");
        CLASS_SPECIFIC_MAP.put("FormattedTextField.caretAspectRatio", "cursor-aspect-");
        CLASS_SPECIFIC_MAP.put("TextPane.caretForeground", "cursor-color");
        CLASS_SPECIFIC_MAP.put("TextPane.caretAspectRatio", "cursor-aspect-ratio");
        CLASS_SPECIFIC_MAP.put("EditorPane.caretForeground", "cursor-color");
        CLASS_SPECIFIC_MAP.put("EditorPane.caretAspectRatio", "cursor-aspect-ratio");

        Object[] defaults = {
            "FileChooser.cancelIcon", new GTKStockIcon("gtk-cancel", 4),
            "FileChooser.okIcon",     new GTKStockIcon("gtk-ok",     4),

            "OptionPane.errorIcon", new GTKStockIcon("gtk-dialog-error", 6), 
            "OptionPane.informationIcon", new GTKStockIcon("gtk-dialog-info", 6), 
            "OptionPane.warningIcon", new GTKStockIcon("gtk-dialog-warning", 6),
            "OptionPane.questionIcon", new GTKStockIcon("gtk-dialog-question", 6), 
            "OptionPane.yesIcon", new GTKStockIcon("gtk-yes", 4),
            "OptionPane.noIcon", new GTKStockIcon("gtk-no", 4),
            "OptionPane.cancelIcon", new GTKStockIcon("gtk-cancel", 4),
            "OptionPane.okIcon", new GTKStockIcon("gtk-ok", 4),
        };

        for (int counter = 0, max = defaults.length; counter < max;
                 counter++) {
            DATA.put(defaults[counter], defaults[++counter]);
        }
    }
}
