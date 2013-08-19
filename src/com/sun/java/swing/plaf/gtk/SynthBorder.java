/*
 * @(#)SynthBorder.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;

/**
 * SynthBorder is a border that delegates to a Painter. The Insets
 * are determined at construction time.
 *
 * @version 1.9, 01/23/03
 * @author Scott Violet
 */
class SynthBorder extends AbstractBorder implements UIResource {
    private SynthUI ui;
    private Insets insets;

    SynthBorder(SynthUI ui, Insets insets) {
        this.ui = ui;
        this.insets = insets;
    }

    SynthBorder(SynthUI ui) {
        this(ui, null);
    }

    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
        JComponent jc = (JComponent)c;
        SynthContext context = ui.getContext(jc);
        SynthStyle style = context.getStyle();
        if (style == null) {
            assert false: "SynthBorder is being used outside after the UI " +
                          "has been uninstalled";
            return;
        }
        SynthPainter painter = style.getBorderPainter(context);

        if (painter != null) {
            painter.paint(context, "border", g, x, y, width, height);
        }
        context.dispose();
    }

    /**
     * This default implementation returns a new <code>Insets</code>
     * instance where the <code>top</code>, <code>left</code>,
     * <code>bottom</code>, and 
     * <code>right</code> fields are set to <code>0</code>.
     * @param c the component for which this border insets value applies
     * @return the new <code>Insets</code> object initialized to 0
     */
    public Insets getBorderInsets(Component c) { 
        return getBorderInsets(c, null);
    }

    /** 
     * Reinitializes the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     * @return the <code>insets</code> object
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        if (this.insets != null) {
            if (insets == null) {
                return new Insets(this.insets.top, this.insets.left,
                                  this.insets.bottom, this.insets.right);
            }
            insets.top = this.insets.top;
            insets.bottom = this.insets.bottom;
            insets.left = this.insets.left;
            insets.right = this.insets.left;
            return insets;
        }
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        return insets;
    }

    /**
     * This default implementation returns false.
     * @return false
     */
    public boolean isBorderOpaque() {
        return false;
    }
}
