/*
 * @(#)SynthLabelUI.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import sun.swing.plaf.synth.SynthUI;

/**
 * Synth's LabelUI.
 *
 * @version 1.18, 12/19/03
 * @author Scott Violet
 */
class SynthLabelUI extends BasicLabelUI implements SynthUI {
    private SynthStyle style;

    /**
     * Returns the LabelUI implementation used for the skins look and feel.
     */
    public static ComponentUI createUI(JComponent c){
        return new SynthLabelUI();
    }


    protected void installDefaults(JLabel c) {
        updateStyle(c);
    }

    void updateStyle(JLabel c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults(JLabel c){
        SynthContext context = getContext(c, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
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
        int state = SynthLookAndFeel.getComponentState(c);
        if (SynthLookAndFeel.selectedUI == this &&
                        state == SynthConstants.ENABLED) {
            state = SynthLookAndFeel.selectedUIState | SynthConstants.ENABLED;
        }
        return state;
    }

    /**
     * Notifies this UI delegate that it's time to paint the specified
     * component.  This method is invoked by <code>JComponent</code> 
     * when the specified component is being painted. 
     */
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintLabelBackground(context,
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
        JLabel label = (JLabel)context.getComponent();
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();

        g.setColor(context.getStyle().getColor(context,
                                               ColorType.TEXT_FOREGROUND));
        g.setFont(style.getFont(context));
        context.getStyle().getGraphicsUtils(context).paintText(
            context, g, label.getText(), icon,
            label.getHorizontalAlignment(), label.getVerticalAlignment(),
            label.getHorizontalTextPosition(), label.getVerticalTextPosition(),
            label.getIconTextGap(), label.getDisplayedMnemonicIndex(), 0);
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintLabelBorder(context, g, x, y, w, h);
    }

    public Dimension getPreferredSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).
            getPreferredSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }


    public Dimension getMinimumSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).
            getMinimumSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }

    public Dimension getMaximumSize(JComponent c) {
        JLabel label = (JLabel)c;
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                                          label.getDisabledIcon();
        SynthContext context = getContext(c);
        Dimension size = context.getStyle().getGraphicsUtils(context).
               getMaximumSize(
               context, context.getStyle().getFont(context), label.getText(),
               icon, label.getHorizontalAlignment(),
               label.getVerticalAlignment(), label.getHorizontalTextPosition(),
               label.getVerticalTextPosition(), label.getIconTextGap(),
               label.getDisplayedMnemonicIndex());

        context.dispose();
        return size;
    }


    public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JLabel)e.getSource());
        }
    }
}
