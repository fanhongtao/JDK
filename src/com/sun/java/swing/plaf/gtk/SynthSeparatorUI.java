/*
 * @(#)SynthSeparatorUI.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.beans.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;
import javax.swing.plaf.UIResource;

/**
 * A Synth L&F implementation of SeparatorUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.11 01/23/03 (based on revision 1.21 of BasicSeparatorUI)
 * @author Shannon Hickey
 */
class SynthSeparatorUI extends SeparatorUI implements
                                        PropertyChangeListener, SynthUI {
    private SynthStyle style;

    public static ComponentUI createUI(JComponent c) {
        return new SynthSeparatorUI();
    }
    
    public void installUI(JComponent c) {
        installDefaults((JSeparator)c);
        installListeners((JSeparator)c);
    }
    
    public void uninstallUI(JComponent c) {
        uninstallDefaults((JSeparator)c);
        uninstallListeners((JSeparator)c);
    }
    
    public void installDefaults(JSeparator c) {
        fetchStyle(c);
    }
    
    private void fetchStyle(JSeparator sep) {
        SynthContext context = getContext(sep, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            if (sep instanceof JToolBar.Separator) {
                Dimension size = ((JToolBar.Separator)sep).getSeparatorSize();
                if (size == null || size instanceof UIResource) {
                    size = (Dimension)style.get(
                                      context, "ToolBar.separatorSize");
                    ((JToolBar.Separator)sep).setSeparatorSize(size);
                }
            }
        }

        context.dispose();
    }

    public void uninstallDefaults(JSeparator c) {
        SynthContext context = getContext(c, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }
    
    public void installListeners(JSeparator c) {
        c.addPropertyChangeListener(this);
    }
    
    public void uninstallListeners(JSeparator c) {
        c.removePropertyChangeListener(this);
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
        SynthLookAndFeel.paintForeground(context, g, null);
    }
    
    public Dimension getPreferredSize(JComponent c) {
        SynthContext context = getContext(c);

        int thickness = ((Integer)context.getStyle().get(
                             context, "Separator.thickness")).intValue();
        Insets insets = c.getInsets();
        Dimension size;

        if (((JSeparator)c).getOrientation() == JSeparator.VERTICAL) {
            size = new Dimension(insets.left + insets.right + thickness,
                                 insets.top + insets.bottom);
        } else {
            size = new Dimension(insets.left + insets.right,
                                 insets.top + insets.bottom + thickness);
        }
        context.dispose();
        return size;
    }
    
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }
    
    public Dimension getMaximumSize(JComponent c) {
        return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
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
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
            fetchStyle((JSeparator)evt.getSource());
        }
    }
}
