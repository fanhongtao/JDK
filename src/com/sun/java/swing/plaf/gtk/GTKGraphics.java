/*
 * @(#)GTKGraphics.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @version 1.8, 01/23/03
 * @author Joshua Outwater
 */
class GTKGraphics extends SynthGraphics {
    public void paintText(SynthContext context, Graphics g, String text,
                          int x, int y, int mnemonicIndex) {
        int componentState = context.getComponentState();
        if ((componentState & SynthConstants.DISABLED) ==
                              SynthConstants.DISABLED){
            Color oldColor = g.getColor();
            g.setColor(context.getStyle().getColor(context,
                                                   GTKColorType.WHITE));
            x += 1;
            y += 1;
            super.paintText(context, g, text, x, y, mnemonicIndex);

            x -= 1;
            y -= 1;
            g.setColor(oldColor);
            super.paintText(context, g, text, x, y, mnemonicIndex);
        }
        else {
            super.paintText(context, g, text, x, y, mnemonicIndex);
        }
    }

    /**
     * Paints text at the specified location. This will not attempt to
     * render the text as html nor will it offset by the insets of the
     * component.
     *
     * @param ss SynthContext
     * @param g Graphics used to render string in.
     * @param text Text to render
     * @param bounds Bounds of the text to be drawn.
     * @param mnemonicIndex Index to draw string at.
     */
    public void paintText(SynthContext context, Graphics g, String text,
                          Rectangle bounds, int mnemonicIndex) {
        Color color = g.getColor();

        Region region = context.getRegion();
        if ((region == Region.RADIO_BUTTON || region == Region.CHECK_BOX ||
             region == Region.TABBED_PANE_TAB) &&
                (context.getComponentState() & SynthConstants.FOCUSED) != 0) {
            ((GTKStyle)(context.getStyle())).getEngine(context).paintFocus(
                    context, g, SynthConstants.ENABLED,
                        "checkbutton", bounds.x - 2, bounds.y - 2,
                        bounds.width + 4, bounds.height + 4);
        }
        g.setColor(color);
        super.paintText(context, g, text, bounds, mnemonicIndex);
    }
}
