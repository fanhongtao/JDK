/*
 * @(#)GTKGraphicsUtils.java	1.17 06/06/07
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.plaf.synth.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @version 1.17, 06/07/06
 * @author Joshua Outwater
 */
class GTKGraphicsUtils extends SynthGraphicsUtils {
    public void paintText(SynthContext context, Graphics g, String text,
                          int x, int y, int mnemonicIndex) {
        if (context.getRegion() == Region.INTERNAL_FRAME_TITLE_PANE) {
            // Metacity handles painting of text on internal frame title,
            // ignore this.
            return;
        }
        int componentState = context.getComponentState();
        if ((componentState & SynthConstants.DISABLED) ==
                              SynthConstants.DISABLED){
            Color orgColor = g.getColor();
            g.setColor(context.getStyle().getColor(context,
                                                   GTKColorType.WHITE));
            x += 1;
            y += 1;
            super.paintText(context, g, text, x, y, mnemonicIndex);

            g.setColor(orgColor);
            x -= 1;
            y -= 1;
            super.paintText(context, g, text, x, y, mnemonicIndex);
        }
        else {
            String themeName = GTKLookAndFeel.getGtkThemeName();
            if (themeName != null && themeName.startsWith("blueprint") &&
                shouldShadowText(context.getRegion(), componentState)) {

                g.setColor(Color.BLACK);
                super.paintText(context, g, text, x+1, y+1, mnemonicIndex);
                g.setColor(Color.WHITE);
            }
            
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
        Region id = context.getRegion();
        if ((id == Region.RADIO_BUTTON ||
             id == Region.CHECK_BOX ||
             id == Region.TABBED_PANE_TAB) &&
            (context.getComponentState() & SynthConstants.FOCUSED) != 0)
        {
            JComponent source = context.getComponent();
            if (!(source instanceof AbstractButton) ||
                ((AbstractButton)source).isFocusPainted()) {

                Color color = g.getColor();
                GTKPainter.INSTANCE.paintFocus(context, g, id,
                        context.getComponentState(), "checkbutton",
                        bounds.x - 2, bounds.y - 2,
                        bounds.width + 4, bounds.height + 4);
                g.setColor(color);
            }
        }
        super.paintText(context, g, text, bounds, mnemonicIndex);
    }

    private static boolean shouldShadowText(Region id, int state) {
        int gtkState = GTKLookAndFeel.synthStateToGTKState(id, state);
        return((gtkState == SynthConstants.MOUSE_OVER) &&
               (id == Region.MENU ||
                id == Region.MENU_ITEM || 
                id == Region.CHECK_BOX_MENU_ITEM ||
                id == Region.RADIO_BUTTON_MENU_ITEM));
    }
}
