/*
 * @(#)SynthArrowButton.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.synth;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/**
 * JButton object that draws a scaled Arrow in one of the cardinal directions.
 *
 * @version 1.15, 12/19/03
 * @author Scott Violet
 */
class SynthArrowButton extends JButton implements SwingConstants, UIResource {
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
        putClientProperty("__arrow_direction__", new Integer(dir));
        repaint();
    }

    public int getDirection() {
        return direction;
    }


    private static class SynthArrowButtonUI extends SynthButtonUI {
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            updateStyle(b);
        }

        protected void paint(SynthContext context, Graphics g) {
            SynthArrowButton button = (SynthArrowButton)context.
                                      getComponent();
            context.getPainter().paintArrowButtonForeground(
                context, g, 0, 0, button.getWidth(), button.getHeight(),
                button.getDirection());
        }

        void paintBackground(SynthContext context, Graphics g, JComponent c) {
            context.getPainter().paintArrowButtonBackground(context, g, 0, 0,
                                                c.getWidth(), c.getHeight());
        }

        public void paintBorder(SynthContext context, Graphics g, int x,
                                int y, int w, int h) {
            context.getPainter().paintArrowButtonBorder(context, g, x, y, w,h);
        }

        public Dimension getMinimumSize() {
            return new Dimension(5, 5);
        }

        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
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
