/*
 * @(#)GTKStyleFactory.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * GTKStyleFactory extends DefaultSynthStyleFactory providing a mapping that
 * mirrors the gtk name space. Styles registered for REGION are mapped to
 * the corresponding gtk class name. Similarly styles registered for
 * CLASS are mapped to the corresponding gtk class name, including the
 * corresponding gtk class hierarchy.
 * 
 * @version 1.21, 01/23/03
 * @author Scott Violet
 */
class GTKStyleFactory extends SynthStyleFactory {
    /**
     * Indicates lookup should be done using the name of the Component.
     * If the name is null this fallsback to the name of the region.
     */
    public static final int WIDGET = 0;
    /**
     * Indicates lookup should be done using the region name.
     */
    public static final int WIDGET_CLASS = 1;
    /**
     * Indicates lookup should be done using the class name
     */
    public static final int CLASS = 2;

    /**
     * Maps from a GTK class name to its super class.
     */
    private static final Map GTK_CLASS_MAP;

    /**
     * Maps from Region to gtk class.
     */
    private static final Map REGION_MAP;

    /**
     * List of StyleAssociations for WIDGET.
     */
    private java.util.List _widgetStyles;

    /**
     * List of StyleAssociations for WIDGET_CLASS.
     */
    private java.util.List _widgetClassStyles;

    /**
     * List of StyleAssociations for CLASS.
     */
    private java.util.List _classStyles;

    /**
     * Labels are special cased. This is set to true when a Style for
     * a label bearing widget is asked for and triggers massaging of
     * the path and class name lookup.
     */
    private boolean _isLabel;


    /**
     * Will hold the label style and label bearing components style. This
     * is used as the key to mergedStyleMap.
     */
    private BakedArrayList _labelStyleList;

    /**
     * Maps from a List containing the label style and label bearing components
     * style to the merged style.
     */
    private Map _mergedStyleMap;

    /**
     * All lookups will at least get this style.
     */
    private SynthStyle _defaultStyle;

    /**
     * Default style for tooltips.
     */
    private GTKStyle _tooltipStyle;

    /**
     * Maps from List to the resolved DefaultSynthStyle.
     */
    private Map _resolvedStyles;

    /**
     * Used for Style lookup to avoid garbage.
     */
    private BakedArrayList _tmpList;

    /**
     * StringBuffer used in building paths.
     */
    private StringBuffer _tmpPath;

    /**
     * Used in matching class styles, will contain the class depth of the
     * matches.
     */
    private int[] _depth;


    static {
        REGION_MAP = new HashMap();
        REGION_MAP.put(Region.ARROW_BUTTON, "GtkButton");
        REGION_MAP.put(Region.BUTTON, "GtkButton");
        REGION_MAP.put(Region.CHECK_BOX, "GtkCheckButton");
        REGION_MAP.put(Region.CHECK_BOX_MENU_ITEM, "GtkCheckMenuItem");
        REGION_MAP.put(Region.COLOR_CHOOSER, "GtkColorSelectionDialog");
        REGION_MAP.put(Region.COMBO_BOX, "GtkCombo");
        REGION_MAP.put(Region.DESKTOP_ICON, "GtkLabel");
        REGION_MAP.put(Region.EDITOR_PANE, "GtkTextView");
        REGION_MAP.put(Region.FORMATTED_TEXT_FIELD, "GtkEntry");
        REGION_MAP.put(Region.INTERNAL_FRAME, "GtkFrame");
        REGION_MAP.put(Region.INTERNAL_FRAME_TITLE_PANE, "GtkLabel");
        REGION_MAP.put(Region.LABEL, "GtkLabel");
        REGION_MAP.put(Region.LIST, "GtkTreeView");
        // GTK doesn't use menu's as swing does, Swing's menus better
        // map to a GTKMenuItem.
        REGION_MAP.put(Region.MENU, "GtkMenuItem");
        REGION_MAP.put(Region.MENU_BAR, "GtkMenuBar");
        REGION_MAP.put(Region.MENU_ITEM, "GtkMenuItem");
        REGION_MAP.put(Region.MENU_ITEM_ACCELERATOR, "GtkLabel");
        REGION_MAP.put(Region.OPTION_PANE, "GtkMessageDialog");
        REGION_MAP.put(Region.PANEL, "GtkContainer");
        REGION_MAP.put(Region.PASSWORD_FIELD, "GtkEntry"); 
        // GTK does not have a distinct class for popups.
        REGION_MAP.put(Region.POPUP_MENU, "GtkMenu");
        REGION_MAP.put(Region.POPUP_MENU_SEPARATOR, "GtkSeparatorMenuItem");
        REGION_MAP.put(Region.PROGRESS_BAR, "GtkProgressBar");
        REGION_MAP.put(Region.RADIO_BUTTON, "GtkRadioButton");
        REGION_MAP.put(Region.RADIO_BUTTON_MENU_ITEM, "GtkRadioMenuItem");
        REGION_MAP.put(Region.ROOT_PANE, "GtkContainer");
        // GTK has two subclasses for the two directions.
        REGION_MAP.put(Region.SCROLL_BAR, "GtkScrollbar");
        REGION_MAP.put(Region.SCROLL_BAR_TRACK, "GtkScrollbar");
        REGION_MAP.put(Region.SCROLL_BAR_THUMB, "GtkScrollbar");
        REGION_MAP.put(Region.SCROLL_PANE, "GtkScrolledWindow");
        // GTK has two subclasses of GtkSeparator for the two directions
        REGION_MAP.put(Region.SEPARATOR, "GtkSeparator");
        // GTK has two subclasses of GtkScale for the two directions
        REGION_MAP.put(Region.SLIDER, "GtkScale");
        REGION_MAP.put(Region.SLIDER_TRACK, "GtkScale");
        REGION_MAP.put(Region.SLIDER_THUMB, "GtkScale");
        REGION_MAP.put(Region.SPINNER, "GtkSpinButton");
        // GTK has two subclasses of GtkPaned for the two diretions.
        REGION_MAP.put(Region.SPLIT_PANE, "GtkPaned");
        REGION_MAP.put(Region.SPLIT_PANE_DIVIDER, "GtkPaned");
        REGION_MAP.put(Region.TABBED_PANE, "GtkNotebook");
        REGION_MAP.put(Region.TABBED_PANE_TAB_AREA, "GtkNotebook");
        REGION_MAP.put(Region.TABBED_PANE_CONTENT, "GtkNotebook");
        REGION_MAP.put(Region.TABBED_PANE_TAB, "GtkNotebook");
        REGION_MAP.put(Region.TABLE, "GtkTreeView");
        // It would appear the headers are drawn as buttons.
        REGION_MAP.put(Region.TABLE_HEADER, "GtkButton");
        REGION_MAP.put(Region.TEXT_AREA, "GtkTextView");
        REGION_MAP.put(Region.TEXT_FIELD, "GtkEntry");
        REGION_MAP.put(Region.TEXT_PANE, "GtkTextView");
        REGION_MAP.put(Region.TOGGLE_BUTTON, "GtkToggleButton");
        REGION_MAP.put(Region.TOOL_BAR, "GtkToolbar");
        REGION_MAP.put(Region.TOOL_BAR_DRAG_WINDOW, "GtkToolbar");
        // GTK does not define a distinct class for the toolbar separator
        REGION_MAP.put(Region.TOOL_BAR_SEPARATOR, "GtkSeparator");
        REGION_MAP.put(Region.TOOL_TIP, "GtkWindow");
        REGION_MAP.put(Region.TREE, "GtkTreeView");
        REGION_MAP.put(Region.TREE_CELL, "GtkTreeView");
        REGION_MAP.put(Region.VIEWPORT, "GtkViewport");


        GTK_CLASS_MAP = new HashMap();
        GTK_CLASS_MAP.put("GtkFrame", "GtkBin");
        GTK_CLASS_MAP.put("GtkProgress", "GtkWidget");
        GTK_CLASS_MAP.put("GtkViewport", "GtkBin");
        GTK_CLASS_MAP.put("GtkMessageDialog", "GtkDialog");
        GTK_CLASS_MAP.put("GtkCombo", "GtkHBox");
        GTK_CLASS_MAP.put("GtkHBox", "GtkBox");
        GTK_CLASS_MAP.put("GtkBox", "GtkContainer");
        GTK_CLASS_MAP.put("GtkTooltips", "GtkObject");
        GTK_CLASS_MAP.put("GtkToolbar", "GtkContainer");
        GTK_CLASS_MAP.put("GtkLabel", "GtkMisc");
        GTK_CLASS_MAP.put("GtkMisc", "GtkWidget");
        GTK_CLASS_MAP.put("GtkTreeView", "GtkContainer");
        GTK_CLASS_MAP.put("GtkTextView", "GtkContainer");
        GTK_CLASS_MAP.put("GtkNotebook", "GtkContainer");
        GTK_CLASS_MAP.put("GtkSeparatorMenuItem", "GtkMenuItem");
        GTK_CLASS_MAP.put("GtkSpinButton", "GtkEntry");
        GTK_CLASS_MAP.put("GtkSeparator", "GtkWidget");
        GTK_CLASS_MAP.put("GtkScale", "GtkRange");
        GTK_CLASS_MAP.put("GtkRange", "GtkWidget");
        GTK_CLASS_MAP.put("GtkPaned", "GtkContainer");
        GTK_CLASS_MAP.put("GtkScrolledWindow", "GtkBin");
        GTK_CLASS_MAP.put("GtkScrollbar", "GtkRange");
        GTK_CLASS_MAP.put("GtkProgressBar", "GtkProgress");
        GTK_CLASS_MAP.put("GtkRadioButton", "GtkCheckButton");
        GTK_CLASS_MAP.put("GtkRadioMenuItem", "GtkCheckMenuItem");
        GTK_CLASS_MAP.put("GtkCheckMenuItem", "GtkMenuItem");
        GTK_CLASS_MAP.put("GtkMenuItem", "GtkItem");
        GTK_CLASS_MAP.put("GtkItem", "GtkBin");
        GTK_CLASS_MAP.put("GtkMenu", "GtkMenuShell");
        GTK_CLASS_MAP.put("GtkMenuBar", "GtkMenuShell");
        GTK_CLASS_MAP.put("GtkMenuShell", "GtkContainer");
        GTK_CLASS_MAP.put("GtkEntry", "GtkWidget");
        GTK_CLASS_MAP.put("GtkColorSelectionDialog", "GtkDialog");
        GTK_CLASS_MAP.put("GtkDialog", "GtkWindow");
        GTK_CLASS_MAP.put("GtkWindow", "GtkBin");
        GTK_CLASS_MAP.put("GtkCheckButton", "GtkToggleButton");
        GTK_CLASS_MAP.put("GtkToggleButton", "GtkButton");
        GTK_CLASS_MAP.put("GtkButton", "GtkBin");
        GTK_CLASS_MAP.put("GtkBin", "GtkContainer");
        GTK_CLASS_MAP.put("GtkContainer", "GtkWidget");
        GTK_CLASS_MAP.put("GtkWidget", "GtkObject");
        GTK_CLASS_MAP.put("GtkObject", "GObject");
    }

    /**
     * In GTK Button and other widgets that display text are actually
     * implemented as two separate widgets, one for the text and one for
     * the button, this method returns true if in GTK the Region would
     * contain a Label to draw the text.
     */
    static final boolean isLabelBearing(Region id) {
        return (id == Region.BUTTON || id == Region.CHECK_BOX ||
                id == Region.CHECK_BOX_MENU_ITEM || id == Region.MENU ||
                id == Region.MENU_ITEM || id == Region.RADIO_BUTTON ||
                id == Region.RADIO_BUTTON_MENU_ITEM ||
                id == Region.TABBED_PANE_TAB ||
                id == Region.TOGGLE_BUTTON || id == Region.TOOL_TIP);
    }

    /**
     * Returns the gtk class that corresponds to the passed in region.
     */
    static String gtkClassFor(Region region) {
        String name = (String)REGION_MAP.get(region);

        if (name == null) {
            // There are no GTK equivalents for some GTK classes, force
            // a match.
            return "XXX";
        }
        return name;
    }

    /**
     * Returns the super class of the passed in gtk class, or null if
     * <code>gtkClass</code> is the root class.
     */
    static String gtkSuperclass(String gtkClass) {
        return (String)GTK_CLASS_MAP.get(gtkClass);
    }

    GTKStyleFactory() {
        this(null);
    }

    GTKStyleFactory(GTKStyle baseStyle) {
        _tmpList = new BakedArrayList(5);
        _resolvedStyles = new HashMap();
        _tmpPath = new StringBuffer();
        _mergedStyleMap = new HashMap();
        _defaultStyle = (baseStyle == null ? new GTKStyle() : baseStyle);
        _labelStyleList = new BakedArrayList(2);
    }

    public synchronized void addStyle(DefaultSynthStyle style,
                         String path, int type) throws PatternSyntaxException {
        // GTK only supports * and ?, escape everything else.
        int length = path.length();
        StringBuffer buffer = new StringBuffer(length * 2);
        for (int counter = 0; counter < length; counter++) {
            char aChar = path.charAt(counter);

            if (aChar == '*') {
                buffer.append(".*");
            }
            else if (aChar == '?') {
                buffer.append('.');
            }
            else if (Character.isLetterOrDigit(aChar)) {
                buffer.append(aChar);
            }
            else {
                buffer.append('\\');
                buffer.append(aChar);
            }
        }
        path = buffer.toString();

        switch (type) {
        case WIDGET:
            if (_widgetStyles == null) {
                _widgetStyles = new ArrayList(1);
            }
            _widgetStyles.add(StyleAssociation.createStyleAssociation(
                                  path, style));
            break;
        case WIDGET_CLASS:
            if (_widgetClassStyles == null) {
                _widgetClassStyles = new ArrayList(1);
            }
            _widgetClassStyles.add(StyleAssociation.createStyleAssociation(
                                       path, style));
            break;
        case CLASS:
            if (_classStyles == null) {
                _classStyles = new ArrayList(1);
            }
            _classStyles.add(StyleAssociation.createStyleAssociation(
                                 path, style));
            break;
        default:
            throw new IllegalArgumentException("type must be one of " +
                                              "CLASS, WIDGET_CLASS or WIDGET");
        }
    }

    /**
     * Returns the style for the specified Component.
     *
     * @param c Component asking for
     * @param id ID of the Component
     */
    public synchronized SynthStyle getStyle(JComponent c, Region id) {
        SynthStyle style = _getStyle(c, id);

        if (isLabelBearing(id)) {
            style = getMergedStyle(c, id, style);
        }
        return style;
    }

    private SynthStyle _getStyle(JComponent c, Region id) {
        BakedArrayList matches = _tmpList;

        matches.clear();
        getMatchingStyles(matches, c, id);

        // Use a cached Style if possible, otherwise create a new one.
        matches.bake();
        SynthStyle style = getCachedStyle(matches);

        if (style == null) {
            style = mergeStyles(matches);

            if (style != null) {
                cacheStyle(matches, style);
            }
        }
        return style;
    }

    /**
     * Fetches any styles that match the passed into arguments into
     * <code>matches</code>.
     */
    private void getMatchingStyles(java.util.List matches, JComponent c,
                                   Region id) {
        // TableHeaderer.renderer is special cased as it descends from
        // DefaultTableCellRenderer which does NOT pass along the property
        // change that would trigger the style to be refetched.
        if (c != null && (c.getParent() != null ||
                          c.getName() == "TableHeader.renderer")) {
            // First match on WIDGET
            if (_widgetStyles != null) {
                getMatches(getPath(WIDGET, c, id), _widgetStyles, matches, c,
                           id);
            }
            // Then match on WIDGET_CLASS
            if (_widgetClassStyles != null) {
                getMatches(getPath(WIDGET_CLASS, c, id), _widgetClassStyles,
                           matches, c, id);
            }
            // Lastly match on CLASS
            if (_classStyles != null) {
                getClassMatches(matches, c, id);
            }
        }
        if (id == Region.TOOL_TIP) {
            matches.add(getToolTipStyle());
        }
        matches.add(_defaultStyle);
    }

    private void getMatches(CharSequence path, java.util.List styles,
                            java.util.List matches, JComponent c, Region id) {
        for (int counter = styles.size() - 1; counter >= 0; counter--){
            StyleAssociation sa = (StyleAssociation)styles.get(counter);

            if (sa.matches(path)) {
                matches.add(sa.getStyle());
            }
        }
    }

    private void getClassMatches(java.util.List matches, JComponent c,
                                 Region id) {
        if (_depth == null) {
            _depth = new int[4];
        }
        int[] sDepth = _depth;
        int matched = 0;
        int startMatchLength = matches.size();

        for (int counter = _classStyles.size() - 1; counter >= 0; counter--){
            StyleAssociation sa = (StyleAssociation)_classStyles.get(counter);
            Object klass = getClass(c, id);

            while (klass != null) {
                if (sa.matches(getClassName(klass))) {
                    int depth = 0;
                    while ((klass = getSuperclass(klass)) != null) {
                        depth++;
                    }
                    if (matched == 0) {
                        sDepth[0] = depth;
                        matches.add(sa.getStyle());
                    }
                    else {
                        int sCounter = 0;
                        while (sCounter < matched && depth < sDepth[sCounter]){
                            sCounter++;
                        }
                        matches.add(sCounter + startMatchLength,
                                       sa.getStyle());
                        if (matched + 1 == sDepth.length) {
                            int[] newDepth = new int[sDepth.length * 2];
                            System.arraycopy(sDepth, 0, newDepth, 0,
                                             sDepth.length);
                            _depth = newDepth;
                            sDepth = newDepth;
                        }
                        if (sCounter < matched) {
                            System.arraycopy(sDepth, 0, sDepth, 0, sCounter);
                            System.arraycopy(sDepth, sCounter, sDepth,
                                             sCounter + 1, matched - sCounter);
                        }
                        sDepth[sCounter] = depth;
                    }
                    matched++;
                    break;
                }
                klass = getSuperclass(klass);
            }
        }
    }


    /**
     * Caches the specified style.
     */
    private void cacheStyle(java.util.List styles, SynthStyle style) {
        BakedArrayList cachedStyles = new BakedArrayList(styles);

        _resolvedStyles.put(cachedStyles, style);
    }

    /**
     * Returns the cached style from the passed in arguments.
     */
    private SynthStyle getCachedStyle(java.util.List styles) {
        if (styles.size() == 0) {
            return null;
        }
        return (SynthStyle)_resolvedStyles.get(styles);
    }

    /**
     * Creates a single Style from the passed in styles. The passed in List
     * is reverse sorted, that is the most recently added style found to
     * match will be first.
     */
    private SynthStyle mergeStyles(java.util.List styles) {
        int size = styles.size();

        if (size == 0) {
            return null;
        }
        else if (size == 1) {
            return (SynthStyle)((DefaultSynthStyle)styles.get(0)).clone();
        }
        // NOTE: merging is done backwards as DefaultSynthStyleFactory reverses
        // order, that is, the most specific style is first.
        DefaultSynthStyle style = (DefaultSynthStyle)styles.get(size - 1);

        style = (DefaultSynthStyle)style.clone();
        for (int counter = size - 2; counter >= 0; counter--) {
            style = ((DefaultSynthStyle)styles.get(counter)).addTo(style);
        }
        return style;
    }

    /**
     * Builds the path returning a CharSequence describing the path. A
     * temporary StringBuffer is provided that should NOT be cached.
     */
    private CharSequence getPath(int type, Component c, Region id) {
        _tmpPath.setLength(0);

        if (type == WIDGET && id == Region.TOOL_TIP) {
            if (c.getName() == null) {
                _tmpPath.append("gtk-tooltips");
            }
            else {
                _tmpPath.append(c.getName());
            }
        }
        else {
            _getPath(_tmpPath, type, c, id);
        }

        if (_isLabel) {
            if (_tmpPath.length() > 0) {
                _tmpPath.append('.');
            }
            _tmpPath.append(getName(c, Region.LABEL));
        }
        return _tmpPath;
    }

    private void _getPath(StringBuffer path, int type, Component c,Region id) {
        if (c instanceof JComponent) {
            boolean isSubregion = (id != null && id.isSubregion());

            if (isSubregion) {
                _getPath(path, type, c, null);
            }
            else {
                _getPath(path, type, c.getParent(), id);
            }
            String key = null;

            if (type == WIDGET && !isSubregion) {
                key = c.getName();
            }
            if (key == null) {
                if (isSubregion) {
                    key = getName(c, id);
                }
                else {
                    Region region = SynthLookAndFeel.getRegion((JComponent)c);

                    if (region != null) {
                        key = getName(c, region);
                    }
                }
            }
            if (path.length() > 0) {
                path.append('.');
            }
            path.append(key);
        }
    }


    /**
     * Returns a class identifer for <code>c</code>.
     */
    protected Object getClass(JComponent c, Region id) {
        if (_isLabel) {
            id = Region.LABEL;
        }
        else if (id == Region.ROOT_PANE) {
            Object name = getRootPaneParentType(c);

            if (name != null) {
                return name;
            }
        }
        String klass = gtkClassFor(id);
        if (klass == "GtkLabel") {
            if (c.getName() == "TableHeader.renderer") {
                return "GtkButton";
            }
        }
        return klass;
    }

    private SynthStyle getMergedStyle(JComponent c, Region id,
                                      SynthStyle style) {
        SynthStyle labelStyle;
        try {
            _isLabel = true;
            labelStyle = (GTKStyle)_getStyle(c, id);
        } finally {
            _isLabel = false;
        }
        _labelStyleList.clear();
        _labelStyleList.add(style);
        _labelStyleList.add(labelStyle);
        _labelStyleList.bake();

        GTKStyle mergedStyle = (GTKStyle)_mergedStyleMap.get(_labelStyleList);

        if (mergedStyle == null) {
            mergedStyle = (GTKStyle)((DefaultSynthStyle)style).clone();
            mergedStyle.addLabelProperties((GTKStyle)labelStyle);
            _mergedStyleMap.put(_labelStyleList, mergedStyle);
            _labelStyleList = new BakedArrayList(2);
        }
        return mergedStyle;
    }

    /**
     * Returns the super class of a klass as returned from
     * <code>getClass</code>, or null if <code>klass</code> has no
     * super classes.
     */
    private Object getSuperclass(Object klass) {
        return gtkSuperclass((String)klass);
    }

    /**
     * Returns the name of a class returned from <code>getClass</code>.
     */
    private String getClassName(Object klass) {
        return (String)klass;
    }

    /**
     * Returns the name of the Region.
     */
    private String getName(Component c, Region region) {
        if (region == Region.ROOT_PANE && c != null) {
            String name = getRootPaneParentType(c);

            if (name != null) {
                return name;
            }
        }
        return gtkClassFor(region);
    }

    private String getRootPaneParentType(Component c) {
        Component parent = c.getParent();

        if (parent instanceof Frame) {
            return "GtkWindow";
        }
        else if (parent instanceof Dialog) {
            return "GtkDialog";
        }
        else if (parent instanceof Window) {
            return "GtkWindow";
        }
        else if (parent instanceof JInternalFrame) {
            return "GtkFrame";
        }
        return null;
    }

    private GTKStyle getToolTipStyle() {
        if (_tooltipStyle == null) {
            // PENDING: this should have a priority of gtk, but we don't
            // yet supported priority.
            Color[] ttColors = new Color[GTKColorType.MAX_COUNT];
            ttColors[GTKColorType.BACKGROUND.getID()] = new ColorUIResource(
                0xFFFFC0);
            ttColors[GTKColorType.FOREGROUND.getID()] =  new ColorUIResource(
                0x000000);
            _tooltipStyle = new GTKStyle(new GTKStyle.GTKStateInfo[] {
                new GTKStyle.GTKStateInfo(SynthConstants.ENABLED, null, null,
                null, ttColors, null)}, null, null,
                GTKStyle.UNDEFINED_THICKNESS, GTKStyle.UNDEFINED_THICKNESS,
                null);
        }
        return _tooltipStyle;
    }


    /**
     * A specialized ArrayList that caches its hashCode as well as overriding
     * equals to avoid creating an Iterator.
     */
    private static class BakedArrayList extends ArrayList {
        private int hashCode;

        BakedArrayList(int size) {
            super(size);
        }

        BakedArrayList(java.util.List data) {
            this(data.size());
            for (int counter = 0, max = data.size(); counter < max; counter++){
                add(data.get(counter));
            }
            bake();
        }

        void bake() {
            hashCode = 1;
            for (int counter = size() - 1; counter >= 0; counter--) {
                hashCode = 31 * hashCode + get(counter).hashCode();
            }
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object o) {
            BakedArrayList list = (BakedArrayList)o;
            int size = size();

            if (list.size() != size) {
                return false;
            }
            while (size-- > 0) {
                if (!get(size).equals(list.get(size))) {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * StyleAssociation is used to lookup a style for a particular
     * component (or region).
     */
    private static class StyleAssociation {
        /**
         * The style
         */
        private SynthStyle _style;

        /**
         * Pattern used for matching.
         */
        private Pattern _pattern;
        /**
         * Matcher used for testing if path matches that of pattern.
         */
        private Matcher _matcher;


        /**
         * Returns a StyleAssociation that can be used to determine if
         * a particular string matches the returned association.
         */
        public static StyleAssociation createStyleAssociation(
                           String text, SynthStyle style)
                           throws PatternSyntaxException {
            return new StyleAssociation(text, style);
        }


        StyleAssociation(String text, SynthStyle style)
                            throws PatternSyntaxException {
            _style = style;
            _pattern = Pattern.compile(text);
        }

        // NOTE: Matcher is not thread safe, it is assumed this will not be
        // called from multiple threads.
        public boolean matches(CharSequence path) {
            if (_matcher == null) {
                _matcher = _pattern.matcher(path);
            }
            else {
                _matcher.reset(path);
            }
            return _matcher.matches();
        }

        /**
         * Returns the text used in matching the string.
         */
        public String getText() {
            return _pattern.pattern();
        }

        /**
         * Returns the style this association is mapped to.
         */
        public SynthStyle getStyle() {
            return _style;
        }
    }
}
