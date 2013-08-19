/*
 * @(#)SynthPanelUI.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * BasicPanel implementation
 *
 * @version 1.9, 01/23/03 (based on BasicPanelUI v 1.7)
 * @author Steve Wilson
 */
class SynthPanelUI extends PanelUI implements PropertyChangeListener, SynthUI {
    private SynthStyle style;

    public static ComponentUI createUI(JComponent c) {
        return new SynthPanelUI();
    }

    public void installUI(JComponent c) {
        JPanel p = (JPanel)c;

        super.installUI(p);
        installDefaults(p);
        installListeners(p);
    }

    public void uninstallUI(JComponent c) {
        JPanel p = (JPanel)c;

        super.uninstallUI(c);
        uninstallListeners(p);
        uninstallDefaults(p);
    }

    protected void installListeners(JPanel p) {
        p.addPropertyChangeListener(this);
    }

    protected void installDefaults(JPanel p) {
        fetchStyle(p);
    }

    private void fetchStyle(JPanel c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallDefaults(JPanel p) {
        SynthContext context = getContext(p, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners(JPanel p) {
        p.removePropertyChangeListener((PropertyChangeListener)
                        SynthLookAndFeel.getSynthEventListener(p));
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

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        // do actual painting
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (SynthLookAndFeel.shouldUpdateStyle(pce)) {
            fetchStyle((JPanel)pce.getSource());
        }
    }
}
