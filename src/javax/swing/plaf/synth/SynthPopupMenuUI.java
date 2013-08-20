/*
 * @(#)SynthPopupMenuUI.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;

import java.applet.Applet;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.*;
import java.awt.AWTEvent;
import java.awt.Toolkit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.*;
import sun.swing.plaf.synth.SynthUI;

/**
 * Synth's PopupMenuUI.
 *
 * @version 1.21, 12/19/03
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
class SynthPopupMenuUI extends BasicPopupMenuUI implements
                PropertyChangeListener, SynthUI {
    /**
     * Maximum size of the text portion of the children menu items.
     */
    private int maxTextWidth;

    /**
     * Maximum size of the text for the acclerator portion of the children
     * menu items.
     */
    private int maxAcceleratorWidth;

    private SynthStyle style;

    public static ComponentUI createUI(JComponent x) {
	return new SynthPopupMenuUI();
    }

    public void installDefaults() {
	if (popupMenu.getLayout() == null ||
	    popupMenu.getLayout() instanceof UIResource) {
	    popupMenu.setLayout(new DefaultMenuLayout(
                                    popupMenu, BoxLayout.Y_AXIS));
        }
        updateStyle(popupMenu);
    }

    private void updateStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();
    }

    protected void installListeners() {
        super.installListeners();
        popupMenu.addPropertyChangeListener(this);
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(popupMenu, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        if (popupMenu.getLayout() instanceof UIResource) {
            popupMenu.setLayout(null);
        }
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        popupMenu.removePropertyChangeListener(this);
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    /**
     * Resets the max text and accerator widths.
     */
    void resetAcceleratorWidths() {
        maxTextWidth = maxAcceleratorWidth = 0;
    }

    /**
     * Adjusts the width needed to display the maximum menu item string.
     *
     * @param width Text width.
     * @return max width
     */
    int adjustTextWidth(int width) {
        maxTextWidth = Math.max(maxTextWidth, width);
        return maxTextWidth;
    }

    /**
     * Adjusts the width needed to display the maximum accelerator.
     *
     * @param width Text width.
     * @return max width
     */
    int adjustAcceleratorWidth(int width) {
        maxAcceleratorWidth = Math.max(maxAcceleratorWidth, width);
        return maxAcceleratorWidth;
    }

    /**
     * Maximum size to display text of children menu items.
     */
    int getMaxTextWidth() {
        return maxTextWidth;
    }

    /**
     * Maximum size needed to display accelerators of children menu items.
     */
    int getMaxAcceleratorWidth() {
        return maxAcceleratorWidth;
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintPopupMenuBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintPopupMenuBorder(context, g, x, y, w, h);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle(popupMenu);
        }
    }
}
