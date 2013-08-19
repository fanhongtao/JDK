/*
 * @(#)SynthArrowButton.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;

/**
 * JButton object that draws a scaled Arrow in one of the cardinal directions.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.12, 01/23/03 (based on BasicArrowButton 1.24)
 * @author David Kloba
 */
class SynthArrowButton extends JButton implements SwingConstants {
    private int direction;

    public SynthArrowButton(int direction) {
        super();
        setFocusable(false);
        setDirection(direction);
        setDefaultCapable(false);
    }

    public String getUIClassID() {
        return "ArrowButtonUI";
    }

    public void updateUI() {
        setUI(new SynthArrowButtonUI());
    }

    public void setDirection(int dir) {
        direction = dir;
    }

    public int getDirection() {
        return direction;
    }

    public Dimension getMinimumSize() {
        return new Dimension(5, 5);
    }

    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    private static class SynthArrowButtonUI extends SynthButtonUI {
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            fetchStyle(b);
        }

        protected void paint(SynthContext context, Graphics g) {
            SynthLookAndFeel.paintForeground(context, g, null);
        }

        public Dimension getPreferredSize(JComponent c) {
            SynthContext context = getContext(c);
            int size = context.getStyle().getInt(context, "ArrowButton.size",
                                                 16);

            context.dispose();
            return new Dimension(size, size);
        }
    }
}
