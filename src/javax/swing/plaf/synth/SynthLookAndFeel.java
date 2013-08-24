/*
 * @(#)SynthLookAndFeel.java	1.46 09/08/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import sun.swing.DefaultLookup;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.lang.ref.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import sun.awt.AppContext;
import sun.swing.plaf.synth.*;

/**
 * SynthLookAndFeel provides the basis for creating a customized look and
 * feel. SynthLookAndFeel does not directly provide a look, all painting is
 * delegated.
 * You need to either provide a configuration file, by way of the
 * {@link #load} method, or provide your own {@link SynthStyleFactory}
 * to {@link #setStyleFactory}. Refer to the
 * <a href="package-summary.html">package summary</a> for an example of
 * loading a file, and {@link javax.swing.plaf.synth.SynthStyleFactory} for
 * an example of providing your own <code>SynthStyleFactory</code> to
 * <code>setStyleFactory</code>.
 * <p>
 * <strong>Warning:</strong>
 * This class implements {@link Serializable} as a side effect of it 
 * extending {@link BasicLookAndFeel}. It is not intended to be serialized.
 * An attempt to serialize it will 
 * result in {@link NotSerializableException}.
 * 
 * @serial exclude 
 * @version 1.46, 08/10/09
 * @since 1.5
 * @author Scott Violet
 */
public class SynthLookAndFeel extends BasicLookAndFeel {
    /**
     * Used in a handful of places where we need an empty Insets.
     */
    static final Insets EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(
                                                            0, 0, 0, 0);

    /**
     * AppContext key to get the current SynthStyleFactory.
     */
    private static final Object STYLE_FACTORY_KEY = new Object(); // com.sun.java.swing.plaf.gtk.StyleCache

    /**
     * The last SynthStyleFactory that was asked for from AppContext
     * <code>lastContext</code>.
     */
    private static SynthStyleFactory lastFactory;
    /**
     * If this is true it indicates there is more than one AppContext active
     * and that we need to make sure in getStyleCache the requesting
     * AppContext matches that of <code>lastContext</code> before returning
     * it.
     */
    private static boolean multipleApps;
    /**
     * AppContext lastLAF came from.
     */
    private static AppContext lastContext;

    // Refer to setSelectedUI
    static ComponentUI selectedUI;
    // Refer to setSelectedUI
    static int selectedUIState;

    /**
     * SynthStyleFactory for the this SynthLookAndFeel.
     */
    private SynthStyleFactory factory;

    /**
     * Map of defaults table entries. This is populated via the load
     * method.
     */
    private Map defaultsMap;


    /**
     * Used by the renderers. For the most part the renderers are implemented
     * as Labels, which is problematic in so far as they are never selected.
     * To accomodate this SynthLabelUI checks if the current 
     * UI matches that of <code>selectedUI</code> (which this methods sets), if
     * it does, then a state as set by this method is returned. This provides
     * a way for labels to have a state other than selected.
     */
    static void setSelectedUI(ComponentUI uix, boolean selected,
                              boolean focused, boolean enabled) {
        selectedUI = uix;
        selectedUIState = 0;
        if (selected) {
            selectedUIState = SynthConstants.SELECTED;
            if (focused) {
                selectedUIState |= SynthConstants.FOCUSED;
            }
        }
        else {
            selectedUIState = SynthConstants.FOCUSED;
            if (enabled) {
                selectedUIState |= SynthConstants.ENABLED;
            }
            else {
                selectedUIState |= SynthConstants.DISABLED;
            }
        }
    }

    /**
     * Clears out the selected UI that was last set in setSelectedUI.
     */
    static void resetSelectedUI() {
        selectedUI = null;
    }


    /**
     * Sets the SynthStyleFactory that the UI classes provided by
     * synth will use to obtain a SynthStyle.
     *
     * @param cache SynthStyleFactory the UIs should use.
     */
    public static void setStyleFactory(SynthStyleFactory cache) {
        // We assume the setter is called BEFORE the getter has been invoked
        // for a particular AppContext.
        synchronized(SynthLookAndFeel.class) {
            AppContext context = AppContext.getAppContext();
            if (!multipleApps && context != lastContext &&
                                 lastContext != null) {
                multipleApps = true;
            }
            lastFactory = cache;
            lastContext = context;
            context.put(STYLE_FACTORY_KEY, cache);
        }
    }

    /**
     * Returns the current SynthStyleFactory.
     *
     * @return SynthStyleFactory
     */
    public static SynthStyleFactory getStyleFactory() {
        synchronized(SynthLookAndFeel.class) {
            if (!multipleApps) {
                return lastFactory;
            }
            AppContext context = AppContext.getAppContext();

            if (lastContext == context) {
                return lastFactory;
            }
            lastContext = context;
            lastFactory = (SynthStyleFactory)AppContext.getAppContext().get
                                           (STYLE_FACTORY_KEY);
            return lastFactory;
        }
    }

    /**
     * Returns the component state for the specified component. This should
     * only be used for Components that don't have any special state beyond
     * that of ENABLED, DISABLED or FOCUSED. For example, buttons shouldn't
     * call into this method.
     */
    static int getComponentState(Component c) {
        if (c.isEnabled()) {
            if (c.isFocusOwner()) {
                return SynthUI.ENABLED | SynthUI.FOCUSED;
            }
            return SynthUI.ENABLED;
        }
        return SynthUI.DISABLED;
    }

    /**
     * Gets a SynthStyle for the specified region of the specified component.
     * This is not for general consumption, only custom UIs should call this
     * method.
     *
     * @param c JComponent to get the SynthStyle for
     * @param region Identifies the region of the specified component
     * @return SynthStyle to use.
     */
    public static SynthStyle getStyle(JComponent c, Region region) {
        return getStyleFactory().getStyle(c, region);
    }

    /**
     * Returns true if the Style should be updated in response to the
     * specified PropertyChangeEvent. This forwards to
     * <code>shouldUpdateStyleOnAncestorChanged</code> as necessary.
     */
    static boolean shouldUpdateStyle(PropertyChangeEvent event) {
        String eName = event.getPropertyName();
        if ("name" == eName) {
            // Always update on a name change
            return true;
        }
        if ("ancestor" == eName && event.getNewValue() != null) {
            // Only update on an ancestor change when getting a valid
            // parent and the LookAndFeel wants this.
            LookAndFeel laf = UIManager.getLookAndFeel();
            return (laf instanceof SynthLookAndFeel &&
                    ((SynthLookAndFeel)laf).
                     shouldUpdateStyleOnAncestorChanged());
        }
        return false;
    }

    /**
     * A convience method that will reset the Style of StyleContext if
     * necessary.
     *
     * @return newStyle
     */
    static SynthStyle updateStyle(SynthContext context, SynthUI ui) {
        SynthStyle newStyle = getStyle(context.getComponent(),
                                       context.getRegion());
        SynthStyle oldStyle = context.getStyle();

        if (newStyle != oldStyle) {
            if (oldStyle != null) {
                oldStyle.uninstallDefaults(context);
            }
            context.setStyle(newStyle);
            newStyle.installDefaults(context, ui);
        }
        return newStyle;
    }

    /**
     * Updates the style associated with <code>c</code>, and all its children.
     * This is a lighter version of
     * <code>SwingUtilities.updateComponentTreeUI</code>.
     *
     * @param c Component to update style for.
     */
    public static void updateStyles(Component c) {
        _updateStyles(c);
        c.repaint();
    }

    // Implementation for updateStyles
    private static void _updateStyles(Component c) {
        if (c instanceof JComponent) {
            // Yes, this is hacky. A better solution is to get the UI
            // and cast, but JComponent doesn't expose a getter for the UI
            // (each of the UIs do), making that approach impractical.
            String name = c.getName();
            c.setName(null);
            if (name != null) {
                c.setName(name);
            }
            ((JComponent)c).revalidate();
        }
        Component[] children = null;
        if (c instanceof JMenu) {
            children = ((JMenu)c).getMenuComponents();
        }
        else if (c instanceof Container) {
            children = ((Container)c).getComponents();
        }
        if (children != null) {
            for(int i = 0; i < children.length; i++) {
                updateStyles(children[i]);
            }
        }
    }

    /**
     * Returns the Region for the JComponent <code>c</code>.
     *
     * @param c JComponent to fetch the Region for
     * @return Region corresponding to <code>c</code>
     */
    public static Region getRegion(JComponent c) {
        return Region.getRegion(c);
    }

    /**
     * A convenience method to return where the foreground should be
     * painted for the Component identified by the passed in
     * AbstractSynthContext.
     */
    static Insets getPaintingInsets(SynthContext state, Insets insets) {
        if (state.isSubregion()) {
            insets = state.getStyle().getInsets(state, insets);
        }
        else {
            insets = state.getComponent().getInsets(insets);
        }
        return insets;
    }

    /**
     * A convenience method that handles painting of the background.
     * All SynthUI implementations should override update and invoke
     * this method.
     */
    static void update(SynthContext state, Graphics g) {
        paintRegion(state, g, null);
    }

    /**
     * A convenience method that handles painting of the background for
     * subregions. All SynthUI's that have subregions should invoke
     * this method, than paint the foreground.
     */
    static void updateSubregion(SynthContext state, Graphics g,
                                Rectangle bounds) {
        paintRegion(state, g, bounds);
    }

    private static void paintRegion(SynthContext state, Graphics g,
                                    Rectangle bounds) {
        JComponent c = state.getComponent();
        SynthStyle style = state.getStyle();
        int x, y, width, height;

        if (bounds == null) {
            x = 0;
            y = 0;
            width = c.getWidth();
            height = c.getHeight();
        }
        else {
            x = bounds.x;
            y = bounds.y;
            width = bounds.width;
            height = bounds.height;
        }

        // Fill in the background, if necessary.
        boolean subregion = state.isSubregion();
        if ((subregion && style.isOpaque(state)) ||
                          (!subregion && c.isOpaque())) {
            g.setColor(style.getColor(state, ColorType.BACKGROUND));
            g.fillRect(x, y, width, height);
        }
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    /**
     * Returns the ui that is of type <code>klass</code>, or null if
     * one can not be found.
     */
    static Object getUIOfType(ComponentUI ui, Class klass) {
        if (klass.isInstance(ui)) {
            return ui;
        }
        return null;
    }

    /**
     * Creates the Synth look and feel <code>ComponentUI</code> for
     * the passed in <code>JComponent</code>.
     *
     * @param c JComponent to create the <code>ComponentUI</code> for
     * @return ComponentUI to use for <code>c</code>
     */
    public static ComponentUI createUI(JComponent c) {
        String key = c.getUIClassID().intern();

        if (key == "ButtonUI") {
            return SynthButtonUI.createUI(c);
        }
        else if (key == "CheckBoxUI") {
            return SynthCheckBoxUI.createUI(c);
        }
        else if (key == "CheckBoxMenuItemUI") {
            return SynthCheckBoxMenuItemUI.createUI(c);
        }
        else if (key == "ColorChooserUI") {
            return SynthColorChooserUI.createUI(c);
        }
        else if (key == "ComboBoxUI") {
            return SynthComboBoxUI.createUI(c);
        }
        else if (key == "DesktopPaneUI") {
            return SynthDesktopPaneUI.createUI(c);
        }
        else if (key == "DesktopIconUI") {
            return SynthDesktopIconUI.createUI(c);
        }
        else if (key == "EditorPaneUI") {
            return SynthEditorPaneUI.createUI(c);
        }
        else if (key == "FileChooserUI") {
            return SynthFileChooserUI.createUI(c);
        }
        else if (key == "FormattedTextFieldUI") {
            return SynthFormattedTextFieldUI.createUI(c);
        }
        else if (key == "InternalFrameUI") {
            return SynthInternalFrameUI.createUI(c);
        }
        else if (key == "LabelUI") {
            return SynthLabelUI.createUI(c);
        }
        else if (key == "ListUI") {
            return SynthListUI.createUI(c);
        }
        else if (key == "MenuBarUI") {
            return SynthMenuBarUI.createUI(c);
        }
        else if (key == "MenuUI") {
            return SynthMenuUI.createUI(c);
        }
        else if (key == "MenuItemUI") {
            return SynthMenuItemUI.createUI(c);
        }
        else if (key == "OptionPaneUI") {
            return SynthOptionPaneUI.createUI(c);
        }
        else if (key == "PanelUI") {
            return SynthPanelUI.createUI(c);
        }
        else if (key == "PasswordFieldUI") {
            return SynthPasswordFieldUI.createUI(c);
        }
        else if (key == "PopupMenuSeparatorUI") {
            return SynthSeparatorUI.createUI(c);
        }
        else if (key == "PopupMenuUI") {
            return SynthPopupMenuUI.createUI(c);
        }
        else if (key == "ProgressBarUI") {
            return SynthProgressBarUI.createUI(c);
        }
        else if (key == "RadioButtonUI") {
            return SynthRadioButtonUI.createUI(c);
        }
        else if (key == "RadioButtonMenuItemUI") {
            return SynthRadioButtonMenuItemUI.createUI(c);
        }
        else if (key == "RootPaneUI") {
            return SynthRootPaneUI.createUI(c);
        }
        else if (key == "ScrollBarUI") {
            return SynthScrollBarUI.createUI(c);
        }
        else if (key == "ScrollPaneUI") {
            return SynthScrollPaneUI.createUI(c);
        }
        else if (key == "SeparatorUI") {
            return SynthSeparatorUI.createUI(c);
        }
        else if (key == "SliderUI") {
            return SynthSliderUI.createUI(c);
        }
        else if (key == "SpinnerUI") {
            return SynthSpinnerUI.createUI(c);
        }
        else if (key == "SplitPaneUI") {
            return SynthSplitPaneUI.createUI(c);
        }
        else if (key == "TabbedPaneUI") {
            return SynthTabbedPaneUI.createUI(c);
        }
        else if (key == "TableUI") {
            return SynthTableUI.createUI(c);
        }
        else if (key == "TableHeaderUI") {
            return SynthTableHeaderUI.createUI(c);
        }
        else if (key == "TextAreaUI") {
            return SynthTextAreaUI.createUI(c);
        }
        else if (key == "TextFieldUI") {
            return SynthTextFieldUI.createUI(c);
        }
        else if (key == "TextPaneUI") {
            return SynthTextPaneUI.createUI(c);
        }
        else if (key == "ToggleButtonUI") {
            return SynthToggleButtonUI.createUI(c);
        }
        else if (key == "ToolBarSeparatorUI") {
            return SynthSeparatorUI.createUI(c);
        }
        else if (key == "ToolBarUI") {
            return SynthToolBarUI.createUI(c);
        }
        else if (key == "ToolTipUI") {
            return SynthToolTipUI.createUI(c);
        }
        else if (key == "TreeUI") {
            return SynthTreeUI.createUI(c);
        }
        else if (key == "ViewportUI") {
            return SynthViewportUI.createUI(c);
        }
        return null;
    }


    /**
     * Creates a SynthLookAndFeel.
     * <p>
     * For the returned <code>SynthLookAndFeel</code> to be useful you need to
     * invoke <code>load</code> to specify the set of
     * <code>SynthStyle</code>s, or invoke <code>setStyleFactory</code>.
     *
     * @see #load
     * @see #setStyleFactory
     */
    public SynthLookAndFeel() {
        factory = new DefaultSynthStyleFactory();
    }

    /**
     * Loads the set of <code>SynthStyle</code>s that will be used by
     * this <code>SynthLookAndFeel</code>. <code>resourceBase</code> is
     * used to resolve any path based resources, for example an
     * <code>Image</code> would be resolved by
     * <code>resourceBase.getResource(path)</code>. Refer to
     * <a href="doc-files/synthFileFormat.html">Synth File Format</a>
     * for more information.
     *
     * @param input InputStream to load from
     * @param resourceBase Used to resolve any images or other resources
     * @throws ParseException If there is an error in parsing
     * @throws IllegalArgumentException if input or resourceBase is null
     */
    public void load(InputStream input, Class<?> resourceBase) throws
                       ParseException, IllegalArgumentException {
        if (defaultsMap == null) {
            defaultsMap = new HashMap();
        }
        new SynthParser().parse(input, (DefaultSynthStyleFactory)factory,
                                resourceBase, defaultsMap);
    }

    /**
     * Called by UIManager when this look and feel is installed.
     */
    public void initialize() {
        super.initialize();
        DefaultLookup.setDefaultLookup(new SynthDefaultLookup());
        setStyleFactory(factory);
    }

    /**
     * Called by UIManager when this look and feel is uninstalled.
     */
    public void uninitialize() {
        // We should uninstall the StyleFactory here, but unfortunately
        // there are a handful of things that retain references to the
        // LookAndFeel and expect things to work
        super.uninitialize();
    }

    /**
     * Returns the defaults for this SynthLookAndFeel.
     *
     * @return Defaults able.
     */
    public UIDefaults getDefaults() {
	UIDefaults table = new UIDefaults();
        Region.registerUIs(table);
        table.setDefaultLocale(Locale.getDefault());
        table.addResourceBundle(
              "com.sun.swing.internal.plaf.basic.resources.basic" );
        table.addResourceBundle("com.sun.swing.internal.plaf.synth.resources.synth");

        // These need to be defined for JColorChooser to work.
        table.put("ColorChooser.swatchesRecentSwatchSize",
                  new Dimension(10, 10));
        table.put("ColorChooser.swatchesDefaultRecentColor", Color.RED);
        table.put("ColorChooser.swatchesSwatchSize", new Dimension(10, 10));

        // These are needed for PopupMenu.
        table.put("PopupMenu.selectedWindowInputMapBindings", new Object[] {
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
        });
        table.put("PopupMenu.selectedWindowInputMapBindings.RightToLeft",
                  new Object[] {
		    "LEFT", "selectChild",
		 "KP_LEFT", "selectChild",
		   "RIGHT", "selectParent",
		"KP_RIGHT", "selectParent",
                  });

        if (defaultsMap != null) {
            table.putAll(defaultsMap);
        }
        return table;
    }

    /**
     * Returns true, SynthLookAndFeel is always supported.
     *
     * @return true.
     */
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    /**
     * Returns false, SynthLookAndFeel is not a native look and feel.
     *
     * @return true.
     */
    public boolean isNativeLookAndFeel() {
        return false;
    }

    /**
     * Returns a textual description of SynthLookAndFeel.
     *
     * @return textual description of synth.
     */
    public String getDescription() {
        return "Synth look and feel";
    }

    /**
     * Return a short string that identifies this look and feel.
     *
     * @return a short string identifying this look and feel.
     */
    public String getName() {
        return "Synth look and feel";
    }

    /**
     * Return a string that identifies this look and feel.
     *
     * @return a short string identifying this look and feel.
     */
    public String getID() {
        return "Synth";
    }

    /**
     * Returns whether or not the UIs should update their
     * <code>SynthStyles</code> from the <code>SynthStyleFactory</code>
     * when the ancestor of the <code>JComponent</code> changes. A subclass
     * that provided a <code>SynthStyleFactory</code> that based the
     * return value from <code>getStyle</code> off the containment hierarchy
     * would override this method to return true.
     *
     * @return whether or not the UIs should update their
     * <code>SynthStyles</code> from the <code>SynthStyleFactory</code>
     * when the ancestor changed.
     */
    public boolean shouldUpdateStyleOnAncestorChanged() {
        return false;
    }
    
    private void writeObject(java.io.ObjectOutputStream out) 
            throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
}
