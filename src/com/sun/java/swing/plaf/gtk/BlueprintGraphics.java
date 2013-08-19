/*
 * @(#)BlueprintGraphics.java	1.4 04/01/13
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;

/**
 * @version 1.4 01/13/04
 * @author Joshua Outwater
 */
class BlueprintGraphics extends SynthGraphics {
    public void paintText(SynthContext context, Graphics g, String text,
            int x, int y, int mnemonicIndex) {
        int state = context.getComponentState();
        Region region = context.getRegion();

        // Paint menu and menu items with shadow defined by blueprint colors.
        if (((state & SynthConstants.MOUSE_OVER) == SynthConstants.MOUSE_OVER
                    && (region == Region.MENU_ITEM ||
                        region == Region.CHECK_BOX_MENU_ITEM ||
                        region == Region.RADIO_BUTTON_MENU_ITEM)) ||
                ((state & SynthConstants.SELECTED) == SynthConstants.SELECTED
                    && region == Region.MENU)) {
            Color oldColor = g.getColor();

            g.setColor(context.getStyle().getColor(context,
                        GTKColorType.BLACK));
            super.paintText(context, g, text, x + 1, y + 1, mnemonicIndex);

            g.setColor(context.getStyle().getColor(context,
                        GTKColorType.WHITE));
            super.paintText(context, g, text, x, y, mnemonicIndex);

            g.setColor(oldColor);
        } else {
            super.paintText(context, g, text, x, y, mnemonicIndex);
        }
    }
}
