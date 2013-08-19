/*
 * @(#)SynthStyle.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * Bag of style properties.
 *
 * @version 1.18, 01/23/03
 * @author Scott Violet
 */
abstract class SynthStyle {
    /**
     * Shared SynthGraphics.
     */
    private static final SynthGraphics SYNTH_GRAPHICS = new SynthGraphics();


    /**
     * Returns a SynthGraphics.
     *
     * @param context SynthContext indentifying requestor
     * @return SynthGraphics
     */
    public SynthGraphics getSynthGraphics(SynthContext context) {
        return SYNTH_GRAPHICS;
    }

    /**
     * Returns a Color for the specified state.
     *
     * @param state SynthContext indentifying requestor
     * @param type Type of color being requested.
     * @return Color
     */
    public Color getColor(SynthContext state, ColorType type) {
        return getColor(state.getComponent(), state.getRegion(),
                        state.getComponentState(), type);
    }

    /**
     * Returns a Font for the specified state.
     *
     * @param state SynthContext indentifying requestor
     * @return Font
     */
    public Font getFont(SynthContext state) {
        return getFont(state.getComponent(), state.getRegion(),
                       state.getComponentState());
    }

    public Color getColor(JComponent c, Region id, int state,
                          ColorType type) {
        if (!id.isSubregion() &&
                (state & SynthConstants.ENABLED) == SynthConstants.ENABLED) {
            if (type == ColorType.BACKGROUND) {
                return c.getBackground();
            }
            else if (type == ColorType.FOREGROUND ||
                     type == ColorType.TEXT_FOREGROUND) {
                return c.getForeground();
            }
        }
        return _getColor(c, id, state, type);
    }

    protected abstract Color _getColor(JComponent c, Region id, int state,
                                       ColorType type);

    public Font getFont(JComponent c, Region id, int state) {
        if (state == SynthConstants.ENABLED) {
            return c.getFont();
        }
        Font cFont = c.getFont();
        if (cFont != null && !(cFont instanceof UIResource)) {
            return cFont;
        }
        return _getFont(c, id, state);
    }

    protected abstract Font _getFont(JComponent c, Region id, int state);

    /**
     * Returns the Insets that are used to calculate sizing information.
     *
     * @param state SynthContext indentifying requestor
     * @param insets Insets to place return value in.
     * @return Sizing Insets.
     */
    public Insets getInsets(SynthContext state, Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        insets.top = insets.bottom = insets.left = insets.right = 0;
        return insets;
    }

    /**
     * Returns the Border for the passed in Component. This may return null.
     * <p>
     * The returned border should also render the focus indicator, as
     * appropriate.
     * <p>
     * Some components may not support borders for different states.
     *
     * @param state SynthContext indentifying requestor
     * @return SynthPainter for the Border.
     */
    public SynthPainter getBorderPainter(SynthContext state) {
        return null;
    }

    /**
     * Returns the Painter used to paint the background.
     *
     * @param state SynthContext indentifying requestor
     * @return SynthPainter for the background.
     */
    public SynthPainter getBackgroundPainter(SynthContext state) {
        return null;
    }

    /**
     * Returns true if the region is opaque.
     *
     * @param state SynthContext indentifying requestor
     * @return true if region is opaque.
     */
    public boolean isOpaque(SynthContext state) {
        return true;
    }

    /**
     * Getter for a region specific style property.
     *
     * @param state SynthContext indentifying requestor
     * @param key Property being requested.
     */
    public abstract Object get(SynthContext state, Object key);

    void installDefaults(SynthContext context, SynthUI ui) {
        // Special case the Border as this will likely change when the LAF
        // can have more control over this.
        if (!context.isSubregion()) {
            JComponent c = context.getComponent();
            Border border = c.getBorder();

            if (border == null || border instanceof UIResource) {
                c.setBorder(new SynthBorder(ui, getInsets(context, null)));
            }
        }
        installDefaults(context);
    }

    /**
     * Installs the necessary state from this Style onto <code>c</code>.
     *
     * @param context SynthContext identifying component to install properties
     *        to.
     */
    public void installDefaults(SynthContext context) {
        if (!context.isSubregion()) {
            JComponent c = context.getComponent();
            Region region = context.getRegion();
            Font font = c.getFont();

            if (font == null || (font instanceof UIResource)) {
                c.setFont(_getFont(c, region, SynthUI.ENABLED));
            }

            Color background = c.getBackground();
            if (background == null || (background instanceof UIResource)) {
                c.setBackground(_getColor(c, region, SynthUI.ENABLED,
                                          ColorType.BACKGROUND));
            }

            Color foreground = c.getForeground();
            if (foreground == null || (foreground instanceof UIResource)) {
                c.setForeground(_getColor(c, region, SynthUI.ENABLED,
                         ColorType.FOREGROUND));
            }
            // PENDING: there needs to be a better way to express this.
            if (region != Region.LABEL ||
                       !(c instanceof javax.swing.table.TableCellRenderer)) {
                c.setOpaque(isOpaque(context));
            }
        }
    }

    /**
     * Uninstalls any state that this style installed on <code>c</code>.
     * <p>
     * Styles should NOT depend upon this being called, in certain cases
     * it may never be called.
     *
     * @param state SynthContext identifying component to install properties
     *        to.
     */
    public void uninstallDefaults(SynthContext state) {
        if (!state.isSubregion()) {
            // NOTE: because getForeground, getBackground and getFont will look
            // at the parent Container, if we set them to null it may
            // mean we they return a non-null and non-UIResource value
            // preventing install from correctly settings its colors/font. For
            // this reason we do not uninstall the fg/bg/font.

            JComponent c = state.getComponent();
            Border border = c.getBorder();

            if (border instanceof UIResource) {
                c.setBorder(null);
            }
        }
    }

    /**
     * Convenience method to get an integer value from the Style.
     */
    public int getInt(SynthContext context, Object key, int defaultValue) {
        Object value = get(context, key);

        if (value instanceof Integer) {
            return ((Integer)value).intValue();
        }
        return defaultValue;
    }

    /**
     * Convenience method to get a Boolean value from the Style.
     */
    public boolean getBoolean(SynthContext state, Object key,
                              boolean defaultValue) {
        Object value = get(state, key);

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }
        return defaultValue;
    }

    /**
     * Convenience method to get a Boolean value from the Style.
     */
    public Icon getIcon(SynthContext state, Object key) {
        Object value = get(state, key);

        if (value instanceof Icon) {
            return (Icon)value;
        }
        return null;
    }

    /**
     * Convenience method to get a String value from the Style.
     */
    public String getString(SynthContext state, Object key,
                              String defaultValue) {
        Object value = get(state, key);

        if (value instanceof String) {
            return (String)value;
        }
        return defaultValue;
    }
}
