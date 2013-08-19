/*
 * @(#)SynthLookAndFeel.java	1.37 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.ref.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import sun.awt.AppContext;

/**
 * @version 1.37, 01/23/03
 */
class SynthLookAndFeel extends LookAndFeel {
    static final Insets EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(
                                                            0, 0, 0, 0);
    private static final Object STYLE_FACTORY_KEY =
                  new StringBuffer("com.sun.java.swing.plaf.gtk.StyleCache");

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


    /**
     * Sets the SynthStyleFactory for the current AppContext, null unsets
     * its for the current AppContext.
     */
    public static void setStyleFactory(SynthStyleFactory cache) {
        synchronized(SynthLookAndFeel.class) {
            if (lastFactory == null) {
                lastFactory = cache;
                lastContext = AppContext.getAppContext();
            }
            else if (cache == null) {
                AppContext context = AppContext.getAppContext();

                if (lastContext == context) {
                    lastFactory = null;
                }
                AppContext.getAppContext().put(STYLE_FACTORY_KEY, null);
            }
            else {
                // More than one active
                multipleApps = true;
                AppContext.getAppContext().put(STYLE_FACTORY_KEY, cache);
            }
        }
    }

    /**
     * Returns the current SynthStyleFactory.
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

    public static int getComponentState(Component c) {
        if (c.isEnabled()) {
            if (c.isFocusOwner()) {
                return SynthUI.ENABLED | SynthUI.FOCUSED;
            }
            return SynthUI.ENABLED;
        }
        return SynthUI.DISABLED;
    }

    public static SynthStyle getStyle(JComponent c, Region region) {
        return getStyleFactory().getStyle(c, region);
    }

    /**
     * Returns the modifier to use in registering accelerators and the like.
     */
    public static int getAcceleratorModifier() {
        // PENDING: this should be an instance method.
        return InputEvent.ALT_MASK;
    }

    /**
     * Returns true if the MouseEvent represents a primary MouseButton.
     */
    public static boolean isPrimaryMouseButton(MouseEvent me) {
        // PENDING: this should be an instance method.
        return SwingUtilities.isLeftMouseButton(me);
    }

    /**
     * Returns true if the Style should be updated in response to the
     * specified PropertyChangeEvent.
     */
    static boolean shouldUpdateStyle(PropertyChangeEvent event) {
        // 'ancestor' will be interned in JComponent, making this safe.
        return ("ancestor" == event.getPropertyName() &&
                event.getNewValue() != null);
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

    static SynthEventListener getSynthEventListener(Component c) {
        PropertyChangeListener[] listeners = c.getPropertyChangeListeners();

        for (int counter = listeners.length - 1; counter >= 0; counter--) {
            if (listeners[counter] instanceof SynthEventListener) {
                return (SynthEventListener)listeners[counter];
            }
        }
        return null;
    }

    static void playSound(JComponent c, Object actionKey) {
        // PENDING: sounds
/*
        ActionMap map = c.getActionMap();
        if (map != null) {
            Action audioAction = map.get(actionName);
            if (audioAction != null) {
                // pass off firing the Action to a utility method
                playSound(audioAction);
            }
        }
*/
    }

    /**
     * Returns the Region for c, or defaultRegion if a Region has
     * not been registered for c yet.
     */
    // PENDING(sky) - need to rethink this.
    // Made public so that DefaultSynthStyleFactory could access it
    public static Region getRegion(JComponent c) {
        return Region.getRegion(c);
    }

    /**
     * A convenience method to return where the foreground should be
     * painted for the Component identified by the passed in
     * AbstractSynthContext.
     */
    public static Insets getPaintingInsets(SynthContext state,
                                           Insets insets) {
        if (state.isSubregion()) {
            insets = state.getStyle().getInsets(state, insets);
        }
        else {
            insets = state.getComponent().getInsets(insets);
        }
        return insets;
    }

    /**
     * Convenience method to transfer focus to the next child of component.
     */
    // PENDING: remove this when a variant of this is added to awt.
    static void compositeRequestFocus(Component component) {
 	if (component instanceof Container) {
 	    Container container = (Container)component;
 	    if (container.isFocusCycleRoot()) {
 		FocusTraversalPolicy policy = container.
                                              getFocusTraversalPolicy();
 		Component comp = policy.getDefaultComponent(container);
 		if (comp!=null) {
 		    comp.requestFocus();
 		    return;
 		}
 	    }
 	    Container rootAncestor = container.getFocusCycleRootAncestor();
 	    if (rootAncestor!=null) {
 		FocusTraversalPolicy policy = rootAncestor.
                                                  getFocusTraversalPolicy();
 		Component comp = policy.getComponentAfter(rootAncestor,
                                                          container);
 		
 		if (comp!=null && SwingUtilities.isDescendingFrom(comp,
                                                                  container)) {
 		    comp.requestFocus();
 		    return;
 		}
 	    }
 	}
 	component.requestFocus();
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

    /**
     * Obtains a foreground Painter from context's SynthStyle and
     * paints it.
     *
     * @param bounds region to paint in, if null the bounds of
     *        the component are used.
     */
    static void paintForeground(SynthContext context, Graphics g,
                                Rectangle bounds) {
        JComponent c = context.getComponent();
        SynthPainter painter = (SynthPainter)context.getStyle().get(
                                   context, "foreground");

        if (painter != null) {
            if (bounds == null) {
                // PENDING: Should this offset by the insets?
/*
                Insets insets = c.getInsets();

                painter.paint(context, "foreground", g,
                              insets.left, insets.top, c.getWidth() -
                              (insets.left + insets.right),
                              c.getHeight() - (insets.top + insets.bottom));
*/
                painter.paint(context, "foreground", g, 0, 0, c.getWidth(),
                              c.getHeight());
            }
            else {
                painter.paint(context, "foreground", g,
                              bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
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

        SynthPainter painter = style.getBackgroundPainter(state);

        // PENDING: may need to reorder these.
        // Paint the background, if necessary.
        if (painter != null) {
            // NOTE: This intentionally does not look at insets, it is
            // up to the painter to honor or ignore these.
            painter.paint(state, "background", g, x, y, width, height);
        }
        // And the border, if necessary.
        if (state.isSubregion()) {
            SynthPainter borderPainter = style.getBorderPainter(state);

            if (borderPainter != null) {
                borderPainter.paint(state, "border", g, x, y, width,
                                    height);
            }
        }
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    /**
     * Creates the Synth look and feel class for the passed in Component.
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

                                             
    public SynthLookAndFeel() {
    }

    public void initialize() {
    }

    public void uninitialize() {
        // PENDING: this should be possible, but unfortunately there are
        // a handful of things that retain references to the LookAndFeel
        // and expect things to work, these should all be fixed and this
        // uncommented.
        // setStyleFactory(null);
        super.uninitialize();
    }

    public UIDefaults getDefaults() {
	UIDefaults table = new UIDefaults();
        Region.registerUIs(table);
        table.setDefaultLocale(Locale.getDefault());
        table.addResourceBundle(
              "com.sun.swing.internal.plaf.basic.resources.basic" );

        // These need to be defined for JColorChooser to work.
        table.put("ColorChooser.swatchesRecentSwatchSize",
                  new Dimension(10, 10));
        table.put("ColorChooser.swatchesDefaultRecentColor", Color.RED);
        table.put("ColorChooser.swatchesSwatchSize", new Dimension(10, 10));

        return table;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public String getDescription() {
        return "Synth look and feel";
    }

    public String getName() {
        return "Synth look and feel";
    }

    public String getID() {
        return "Synth";
    }
}
