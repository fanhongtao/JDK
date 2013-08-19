/*
 * @(#)GTKStyle.java	1.89 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.plaf.*;
import javax.swing.text.DefaultEditorKit;
import java.util.*;
import sun.awt.*;
import java.security.*;
import java.awt.RenderingHints;

/**
 * @version 1.89, 01/23/03
 * @author Scott Violet
 */
class GTKStyle extends DefaultSynthStyle implements GTKConstants {
    private static final Object PENDING = new StringBuffer("Pending");

    private static final Color BLACK_COLOR = new ColorUIResource(Color.BLACK);
    private static final Color WHITE_COLOR = new ColorUIResource(Color.WHITE);

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

    private static final GTKGraphics GTK_GRAPHICS = new GTKGraphics();

    private static final Object ICON_SIZE_KEY = new StringBuffer("IconSize");

    /**
     * Default size for icons on dialogs (optionpane).
     */
    private static final Dimension DIALOG_ICON_SIZE        = new Dimension(48, 48);

    /**
     * Default size for icons on buttons.
     */
    private static final Dimension BUTTON_ICON_SIZE        = new Dimension(20, 20);

    // These have been included for completeness, but currently are not used
    private static final Dimension MENU_ICON_SIZE          = new Dimension(16, 16);
    private static final Dimension SMALL_TOOLBAR_ICON_SIZE = new Dimension(18, 18);
    private static final Dimension LARGE_TOOLBAR_ICON_SIZE = new Dimension(24, 24);
    private static final Dimension DND_ICON_SIZE           = new Dimension(32, 32);

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
     * GTK components have a single font which is used in all states.
     */
    private Font font;

    /**
     * Icons.
     */
    private GTKStockIconInfo[] icons;


    /**
     * Sets the size of a particular icon type.
     */
    static void setIconSize(String key, int w, int h) {
        getIconSizeMap().put(key, new Dimension(w, h));
    }

    /**
     * Returns the size of a particular icon type. This should NOT
     * modify the return value.
     */
    static Dimension getIconSize(String key) {
        return (Dimension)getIconSizeMap().get(key);
    }

    private static Map getIconSizeMap() {
        AppContext appContext = AppContext.getAppContext();
        Map iconSizes = (Map)appContext.get(ICON_SIZE_KEY);

        if (iconSizes == null) {
            iconSizes = new HashMap();

            iconSizes.put("gtk-dialog", DIALOG_ICON_SIZE);
            iconSizes.put("gtk-button", BUTTON_ICON_SIZE);
            iconSizes.put("gtk-menu", MENU_ICON_SIZE);
            iconSizes.put("gtk-small-toolbar", SMALL_TOOLBAR_ICON_SIZE);
            iconSizes.put("gtk-large-toolbar", LARGE_TOOLBAR_ICON_SIZE);
            iconSizes.put("gtk-dnd", DND_ICON_SIZE);
            
            appContext.put(ICON_SIZE_KEY, iconSizes);
        }

        return iconSizes;
    }

    /**
     * Calculates the LIGHT color from the background color.
     */
    static Color calculateLightColor(Color bg) {
        return GTKColorType.adjustColor(bg, 1.3f, 1.3f, 1.3f);
    }

    /**
     * Calculates the DARK color from the background color.
     */
    static Color calculateDarkColor(Color bg) {
        return GTKColorType.adjustColor(bg, 1.0f, .7f, .7f);
    }

    /**
     * Calculates the MID color from the background color.
     */
    static Color calculateMidColor(Color bg) {
        int r = bg.getRed();
        int g = bg.getGreen();
        int b = bg.getBlue();
        int rLight = Math.min(255, (int)(r * 1.3));
        int gLight = Math.min(255, (int)(g * 1.3));
        int bLight = Math.min(255, (int)(b * 1.3));
        int rDark = (int)(r * .7);
        int gDark = (int)(g * .7);
        int bDark = (int)(b * .7);
        return new ColorUIResource(
                     (rLight + rDark) / 2, (gLight + gDark) / 2,
                     (bLight + bDark) / 2);
    }

    /**
     * Creates an array of colors populated based on the passed in
     * the background color. Specifically this sets the
     * BACKGROUND, LIGHT, DARK, MID, BLACK, WHITE and FOCUS colors
     * from that of color, which is assumed to be the background.
     */
    static Color[] getColorsFrom(Color bg, Color fg) {
        Color[] colors = new Color[GTKColorType.MAX_COUNT];
        int r = bg.getRed();
        int g = bg.getGreen();
        int b = bg.getBlue();
        int rLight = Math.min(255, (int)(r * 1.3));
        int gLight = Math.min(255, (int)(g * 1.3));
        int bLight = Math.min(255, (int)(b * 1.3));
        int rDark = (int)(r * .7);
        int gDark = (int)(g * .7);
        int bDark = (int)(b * .7);

        colors[GTKColorType.BACKGROUND.getID()] = bg;
        colors[GTKColorType.LIGHT.getID()] = new ColorUIResource(
                                                 rLight, gLight, bLight);
        colors[GTKColorType.DARK.getID()] = new ColorUIResource(
                                                 rDark, gDark, bDark);
        colors[GTKColorType.MID.getID()] = new ColorUIResource(
                     (rLight + rDark) / 2, (gLight + gDark) / 2,
                     (bLight + bDark) / 2);
        colors[GTKColorType.BLACK.getID()] = BLACK_COLOR;
        colors[GTKColorType.WHITE.getID()] = WHITE_COLOR;
        colors[GTKColorType.FOCUS.getID()] = BLACK_COLOR;
        colors[GTKColorType.FOREGROUND.getID()] = fg;
        colors[GTKColorType.TEXT_FOREGROUND.getID()] = fg;
        colors[GTKColorType.TEXT_BACKGROUND.getID()] = WHITE_COLOR;
        return colors;
    }

    public GTKStyle(DefaultSynthStyle style) {
        super(style);
        if (style instanceof GTKStyle) {
            GTKStyle gStyle = (GTKStyle)style;
            font = gStyle.font;
            xThickness = gStyle.xThickness;
            yThickness = gStyle.yThickness;
            icons = gStyle.icons;
            classSpecificValues = cloneClassSpecificValues(
                                       gStyle.classSpecificValues);
        }
    }

    public GTKStyle() {
        super(new Insets(-1, -1, -1, -1), true, null, null);
    }

    public GTKStyle(Font font) {
        this();
        this.font = font;
    }

    public GTKStyle(StateInfo[] states,
                    CircularIdentityList classSpecificValues,
                    Font font,
                    int xThickness, int yThickness,
                    GTKStockIconInfo[] icons) {
        super(new Insets(-1, -1, -1, -1), true, states, null);
        this.font = font;
        this.xThickness = xThickness;
        this.yThickness = yThickness;
        this.icons = icons;
        this.classSpecificValues = classSpecificValues;
    }

    public SynthGraphics getSynthGraphics(SynthContext context) {
        return GTK_GRAPHICS;
    }

    public GTKEngine getEngine(SynthContext context) {
        GTKEngine engine = (GTKEngine)get(context, "engine");

        if (engine == null) {
            return GTKEngine.INSTANCE;
        }
        return engine;
    }

    public SynthPainter getBorderPainter(SynthContext state) {
        SynthPainter painter = super.getBorderPainter(state);

        if (painter == null) {
            return GTKPainter.INSTANCE;
        }
        return painter;
    }

    public SynthPainter getBackgroundPainter(SynthContext state) {
        SynthPainter painter = super.getBackgroundPainter(state);

        if (painter == null) {
            return GTKPainter.INSTANCE;
        }
        return painter;
    }

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
     *
     * Note, the key used here should only contain the letters A-Z, a-z, the
     * digits 0-9, and the '-' character. If you need to request a value for
     * a key having characters outside this list, replace any other characters
     * with '-'. (ie. "default_border" should be "default-border").
     */
    public Object getClassSpecificValue(SynthContext context, String key) {
        if (classSpecificValues != null) {
            String gtkClass = GTKStyleFactory.gtkClassFor(context.getRegion());

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
     * Returns a class specific property.
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
     * Returns a class specific property.
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
     * Returns a class specific property.
     */
    public boolean getClassSpecificBoolValue(SynthContext context, String key,
                                             boolean defaultValue) {
        Object value = getClassSpecificValue(context, key);

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }
        return defaultValue;
    }

    public Object get(SynthContext context, Object key) {
        Object value = super.get(context, key);

        if (value == null) {
            if (key == "foreground" || key == "focus" ||
                      key == "SplitPane.dragPainter" ||
                      key == "ScrollPane.viewportBorderPainter") {
                return GTKPainter.INSTANCE;
            }
            else if (key == "ScrollPane.viewportBorderInsets") {
                return GTKPainter.INSTANCE.getScrollPaneInsets(context,
                                           new Insets(0,0,0,0));
            }
            synchronized (DATA) {
                value = DATA.get(key);
                try {
                    while (value == PENDING) {
                        DATA.wait();
                        value = DATA.get(key);
                    }
                } catch (InterruptedException ie) {
                }
                if (value instanceof UIDefaults.LazyValue) {
                    DATA.put(key, PENDING);
                }
            }
            if (value instanceof StyleSpecificValue) {
                put(key, ((StyleSpecificValue)value).getValue(context));
            }
            else if (value instanceof UIDefaults.ActiveValue) {
                value = ((UIDefaults.ActiveValue)value).
                                             createValue(null);
            }
            else if (value instanceof UIDefaults.LazyValue) {
                value = ((UIDefaults.LazyValue)value).
                                             createValue(null);
                synchronized(DATA) {
                    DATA.put(key, value);
                    DATA.notifyAll();
                }
            }
            else if (value == null) {
                // See if this is a class specific value.
                Object classKey = CLASS_SPECIFIC_MAP.get(key);

                if (classKey != null) {
                    value = getClassSpecificValue(context, (String)key);
                }
            }
        }
        return value;
    }

    protected Font _getFont(JComponent c, Region id, int state) {
        if (font != null) {
            return font;
        }
        
        state = GTKLookAndFeel.synthStateToGTKState(id, state);

        Font f = super._getFont(c, id, state);

        if (f == null) {
            return DEFAULT_FONT;
        }
        return f;
    }

    /**
     * This method is to be used inside the GTK package when we want a
     * color for an explicit state, it will not attempt to remap the state
     * as getColor will.
     */
    Color getGTKColor(JComponent c, Region id,
                      int state, ColorType type) {
        Color color = super._getColor(c, id, state, type);
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
        return _getColor(c, id, state, type);
    }

    protected Color _getColor(JComponent c, Region id, int state,
                              ColorType type) {
        Color color = super._getColor(c, id, state, type);

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
        return false;
    }

    public int getXThickness() {
        return xThickness;
    }

    public int getYThickness() {
        return yThickness;
    }

    private Icon getStockIcon(SynthContext context, String key, String size) {
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
                                                        size);
            }
            
            if (bestSource != null) {
                icon = bestSource.toIcon();
            }
        }
        
        if (icon == null) {
            // Use a default icon
            icon = (Icon)((UIDefaults.LazyValue)LookAndFeel.makeIcon(
                              GTKStyle.class, "resources/" + key + "-" + size +
                              ".png")).createValue(null);
        }
        
        // If we used a default, or if the stock icon we found had a wildcard size,
        // we force the size to match that requested
        if (icon != null && (bestSource == null || bestSource.getSize() == null)) {
            Dimension iconSize = getIconSize(size);

            if (iconSize != null && (icon.getIconWidth() != iconSize.width ||
                                    icon.getIconHeight() != iconSize.height)) {
                Image image = new BufferedImage(iconSize.width, iconSize.height,
                                                BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = (Graphics2D)image.getGraphics();

                // for nicer scaling
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                Image oldImage = null;
                if (icon instanceof ImageIcon) {
                    oldImage = ((ImageIcon)icon).getImage();
                } else {
                    oldImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);

                    Graphics g = oldImage.getGraphics();
                    icon.paintIcon(null, g, 0, 0);
                    g.dispose();
                }

                g2d.drawImage(oldImage, 0, 0, iconSize.width, iconSize.height, null);
                g2d.dispose();
                
                icon = new ImageIcon(image);
            }
        }

        return icon;
    }

    /**
     * Adds the specific label based properties from <code>style</code> to
     * this style.
     */
    void addLabelProperties(GTKStyle style) {
        // Take the font
        this.font = style.font;
        // And TEXT_FOREGROUND
        if (states == null) {
            if (style.states == null) {
                return;
            }
            states = new StateInfo[style.states.length];
            for (int counter = 0; counter < style.states.length; counter++) {
                Color color = style.states[counter].getColor(
                                     GTKColorType.FOREGROUND);

                states[counter] = createStateInfo(style.states[counter].
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
            if (style.states != null) {
                for (int oCounter = style.states.length - 1; oCounter >= 0;
                         oCounter--) {
                    boolean matched = false;
                    StateInfo oState = style.states[oCounter];
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
        return new GTKStateInfo(state, null, null, null, colors, null);
    }

    /**
     * Adds a value specific to the style.
     */
    void put(Object key, Object value) {
        if (data == null) {
            data = new HashMap();
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
     * Creates a clone of the receiver.
     */
    public Object clone() {
        GTKStyle style = (GTKStyle)super.clone();

        style.classSpecificValues = cloneClassSpecificValues(
                                         style.classSpecificValues);
        return style;
    }

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
        if (font != null) {
            gtkStyle.font = font;
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
        
        GTKStockIconInfo(String key, GTKIconSource[] sources) {
            this.key = key.intern();
            this.sources = sources;
            Arrays.sort(this.sources);
        }
        
        public String getKey() {
            return key;
        }
        
        public GTKIconSource getBestIconSource(int direction, int state, String size) {
            for (int i = 0; i < sources.length; i++) {
                GTKIconSource src = sources[i];
                
                if ((src.direction == UNDEFINED || src.direction == direction)
                        && (src.state == UNDEFINED || src.state == state)
                        && (src.size == null || sizesMatch(src.size, size))) {
                    return src;
                }
            }
            
            return null;
        }

        private static boolean sizesMatch(String sizeOne, String sizeTwo) {
            //Dimension one = getIconSize(sizeOne);
            //Dimension two = getIconSize(sizeTwo);

            //return one.width == two.width && one.height == two.height;

            // An earlier version of GTK compared the actual pixel sizes for
            // equality. Now it just compares the logical sizes.
            return sizeOne == sizeTwo;
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
        private String size;

        GTKIconSource(String image, int direction, int state, String size) {
            this.image = image;
            this.direction = direction;
            this.state = state;
            
            if (size != null) {
                this.size = size.intern();
            }
        }

        public int getDirection() {
            return direction;
        }
        
        public int getState() {
            return state;
        }

        public String getSize() {
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
            } else if (size != null && other.size == null) {
                return -1;
            } else if (size == null && other.size != null) {
                return 1;
            } else {
                return 0;
            }
        }

        public String toString() {
            return "image=" + image + ", dir=" + getDirectionName(direction)
                   + ", state=" + getStateName(state, "*")
                   + ", size=" + (size == null ? "*" : size);
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
        StringBuffer buf = new StringBuffer();

        buf.append("class=");
        buf.append(getClass().getName()).append('\n');

        if (font != null) {
            buf.append("font=").append(font.toString()).append('\n');
        }

        if (xThickness != UNDEFINED_THICKNESS) {
            buf.append("xt=").append(String.valueOf(xThickness)).append('\n');
        }

        if (yThickness != UNDEFINED_THICKNESS) {
            buf.append("yt=").append(String.valueOf(yThickness)).append('\n');
        }

        if (states != null) {
            buf.append("*** Colors and Pixmaps ***\n");
            for (int i = 0; i < states.length; i++) {
                buf.append(states[i].toString()).append('\n');
            }
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

        public GTKStateInfo(int state, SynthPainter bPainter,
                            SynthPainter bgPainter, Font font, Color[] colors,
                            Object backgroundImage) {
            super(state, bPainter, bgPainter, font, colors);
            this.backgroundImage = backgroundImage;
        }

        public GTKStateInfo(StateInfo info) {
            super(info);
            if (info instanceof GTKStateInfo) {
                backgroundImage = ((GTKStateInfo)info).backgroundImage;
            }
        }

        void setColor(ColorType type, Color color) {
            if (colors == null) {
                if (color == null) {
                    return;
                }
                colors = new Color[GTKColorType.MAX_COUNT];
            }
            colors[type.getID()] = color;
        }

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

        public Object clone() {
            return new GTKStateInfo(this);
        }

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
        private String size;
        private boolean loadedLTR;
        private boolean loadedRTL;
        private Icon ltrIcon;
        private Icon rtlIcon;
        private SynthStyle style;

        GTKStockIcon(String key, String size) {
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
        DEFAULT_COLORS[1][GTKColorType.TEXT_FOREGROUND.getID()] = BLACK_COLOR;
        DEFAULT_COLORS[2][GTKColorType.TEXT_FOREGROUND.getID()] = BLACK_COLOR;
        DEFAULT_COLORS[4][GTKColorType.TEXT_FOREGROUND.getID()] =
                DEFAULT_COLORS[2][GTKColorType.TEXT_FOREGROUND.getID()];

        CLASS_SPECIFIC_MAP = new HashMap();
        CLASS_SPECIFIC_MAP.put("CheckBox.iconTextGap", "indicator_spacing");
        CLASS_SPECIFIC_MAP.put("Slider.thumbHeight", "slider_width");
        CLASS_SPECIFIC_MAP.put("Slider.trackBorder", "trough_border");
        CLASS_SPECIFIC_MAP.put("SplitPaneDivider.size", "handle_size");
        CLASS_SPECIFIC_MAP.put("Tree.expanderSize", "expander_size");
        CLASS_SPECIFIC_MAP.put("ScrollBar.thumbHeight", "slider_width");


        Integer caretBlinkRate = new Integer(500);
        Insets zeroInsets = new InsetsUIResource(0, 0, 0, 0);

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

        Object[] defaults = {
	    "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                         "SPACE", "pressed",
                "released SPACE", "released",
                         "ENTER", "pressed",
                "released ENTER", "released"
              }),


	    "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
                         "SPACE", "pressed",
                "released SPACE", "released",
              }),
            "CheckBox.icon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getCheckBoxIcon"),


            "CheckBoxMenuItem.arrowIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getCheckBoxMenuItemArrowIcon"),
            "CheckBoxMenuItem.checkIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getCheckBoxMenuItemCheckIcon"),


            "ColorChooser.panels", new UIDefaults.ActiveValue() {
                public Object createValue(UIDefaults table) {
                    return new AbstractColorChooserPanel[] {
                                       new GTKColorChooserPanel() };
                }
            },


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


            "EditorPane.caretBlinkRate", caretBlinkRate,
            "EditorPane.margin", editorMargin,
            "EditorPane.focusInputMap", multilineInputMap,


            "EditorPane.caretForeground", BLACK_COLOR,
            "EditorPane.caretBlinkRate", caretBlinkRate,
            "EditorPane.margin", editorMargin,
            "EditorPane.focusInputMap", multilineInputMap,


	    "FileChooser.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelselection"
		 }),
            "FileChooser.cancelIcon", new GTKStockIcon("gtk-cancel", "gtk-button"),
            "FileChooser.okIcon",     new GTKStockIcon("gtk-ok",     "gtk-button"),


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
                       "typed \010", DefaultEditorKit.deletePrevCharAction,
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


	    "InternalFrameTitlePane.titlePaneLayout",
				new GTKLazyValue("com.sun.java.swing.plaf.gtk.Metacity",
						 "getTitlePaneLayout"),
            "InternalFrame.windowBindings", new Object[] {
                  "shift ESCAPE", "showSystemMenu",
                    "ctrl SPACE", "showSystemMenu",
                        "ESCAPE", "hideSystemMenu" },


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
		             "DOWN", "selectNextRow",
		          "KP_DOWN", "selectNextRow",
		       "shift DOWN", "selectNextRowExtendSelection",
		    "shift KP_DOWN", "selectNextRowExtendSelection",
		             "LEFT", "selectPreviousColumn",
		          "KP_LEFT", "selectPreviousColumn",
		       "shift LEFT", "selectPreviousColumnExtendSelection",
		    "shift KP_LEFT", "selectPreviousColumnExtendSelection",
		            "RIGHT", "selectNextColumn",
		         "KP_RIGHT", "selectNextColumn",
		      "shift RIGHT", "selectNextColumnExtendSelection",
		   "shift KP_RIGHT", "selectNextColumnExtendSelection",
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
	    "List.focusInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		             "LEFT", "selectNextColumn",
		          "KP_LEFT", "selectNextColumn",
		       "shift LEFT", "selectNextColumnExtendSelection",
		    "shift KP_LEFT", "selectNextColumnExtendSelection",
		            "RIGHT", "selectPreviousColumn",
		         "KP_RIGHT", "selectPreviousColumn",
		      "shift RIGHT", "selectPreviousColumnExtendSelection",
		   "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
		 }),


 	    "Menu.shortcutKeys", new int[] {KeyEvent.ALT_MASK},
            "Menu.arrowIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getMenuArrowIcon"),

	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },


            "MenuItem.arrowIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getMenuItemArrowIcon"),

            "OptionPane.minimumSize", new DimensionUIResource(262, 90),
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },
	    "OptionPane.buttonClickThreshhold", new Integer(500),
            "OptionPane.errorIcon", new GTKStockIcon("gtk-dialog-error", 
                                        "gtk-dialog"),
            "OptionPane.informationIcon", new GTKStockIcon("gtk-dialog-info", 
                                        "gtk-dialog"),
            "OptionPane.warningIcon", new GTKStockIcon("gtk-dialog-warning", 
                                        "gtk-dialog"),
            "OptionPane.questionIcon", new GTKStockIcon("gtk-dialog-question", 
                                        "gtk-dialog"),
            "OptionPane.yesIcon", new GTKStockIcon("gtk-yes", "gtk-button"),
            "OptionPane.noIcon", new GTKStockIcon("gtk-no", "gtk-button"),
            "OptionPane.cancelIcon", new GTKStockIcon("gtk-cancel",
                                                      "gtk-button"),
            "OptionPane.okIcon", new GTKStockIcon("gtk-ok", "gtk-button"),


            "PasswordField.caretForeground", BLACK_COLOR,
            "PasswordField.caretBlinkRate", caretBlinkRate,
            "PasswordField.margin", zeroInsets,
            "PasswordField.focusInputMap", fieldInputMap,


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


	    "RadioButton.focusInputMap",
                   new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released",
                           "RETURN", "pressed"
	           }),
            "RadioButton.icon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getRadioButtonIcon"),


            "RadioButtonMenuItem.arrowIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getRadioButtonMenuItemArrowIcon"),
            "RadioButtonMenuItem.checkIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getRadioButtonMenuItemCheckIcon"),


            // These bindings are only enabled when there is a default
            // button set on the rootpane.
            "RootPane.defaultButtonWindowKeyBindings", new Object[] {
		               "ENTER", "press",
		      "released ENTER", "release",
		          "ctrl ENTER", "press",
                 "ctrl released ENTER", "release"
            },


            "ScrollBar.thumbHeight", new Integer(14),
            "ScrollBar.width", new Integer(16),
            "ScrollBar.minimumThumbSize", new Dimension(8, 8),
            "ScrollBar.maximumThumbSize", new Dimension(4096, 4096),
            "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE,
            "ScrollBar.focusInputMap",
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
            "ScrollBar.focusInputMap.RightToLeft",
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


            "SplitPaneDivider.size", new Integer(7),
            "SplitPaneDivider.oneTouchOffset", new Integer(2),
            "SplitPaneDivider.oneTouchButtonSize", new Integer(5),


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
            "Table.ancestorInputMap.RightToLeft",
                    new UIDefaults.LazyInputMap(new Object[] {
		                "RIGHT", "selectPreviousColumn",
		             "KP_RIGHT", "selectPreviousColumn",
		                 "LEFT", "selectNextColumn",
		              "KP_LEFT", "selectNextColumn",
		          "shift RIGHT", "selectPreviousColumnExtendSelection",
		       "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
		           "shift LEFT", "selectNextColumnExtendSelection",
		        "shift KP_LEFT", "selectNextColumnExtendSelection",
		         "ctrl PAGE_UP", "scrollRightChangeSelection",
		       "ctrl PAGE_DOWN", "scrollLeftChangeSelection",
		   "ctrl shift PAGE_UP", "scrollRightExtendSelection",
		 "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection",
                    }),

            "TextArea.caretForeground", BLACK_COLOR,
            "TextArea.caretBlinkRate", caretBlinkRate,
            "TextArea.margin", zeroInsets,
            "TextArea.focusInputMap", multilineInputMap,


            "TextField.caretForeground", BLACK_COLOR,
            "TextField.caretBlinkRate", caretBlinkRate,
            "TextField.margin", zeroInsets,
            "TextField.focusInputMap", fieldInputMap,


            "TextPane.caretForeground", BLACK_COLOR,
            "TextPane.caretBlinkRate", caretBlinkRate,
            "TextPane.margin", editorMargin,
            "TextPane.focusInputMap", multilineInputMap,


	    "ToggleButton.focusInputMap",
                   new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	           }),


            "ToolBar.separatorSize", new DimensionUIResource(10, 10),
            "ToolBar.handleIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getToolBarHandleIcon"),
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


            "Tree.drawHorizontalLegs", Boolean.FALSE,
            "Tree.drawVerticalLegs", Boolean.FALSE,
            "Tree.rowHeight", new Integer(-1),
            "Tree.scrollsOnExpand", Boolean.FALSE,
            "Tree.expanderSize", new Integer(10),
            "Tree.expandedIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getTreeExpandedIcon"),
            "Tree.collapsedIcon", new GTKLazyValue(
                              "com.sun.java.swing.plaf.gtk.GTKIconFactory",
                              "getTreeCollapsedIcon"),
            "Tree.trailingControlOffset", new Integer(12),
            "Tree.controlSize", new Integer(18),
            "Tree.indent", new Integer(14),
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
            };

        for (int counter = 0, max = defaults.length; counter < max;
                 counter++) {
            DATA.put(defaults[counter], defaults[++counter]);
        }
    }
}
