/*
 * @(#)BlueprintGraphicsUtils.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.plaf.synth.*;

/**
 * @version 1.5 12/19/03
 * @author Joshua Outwater
 */
class BlueprintGraphicsUtils extends SynthGraphicsUtils {
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
            g.setColor(context.getStyle().getColor(context,
                        GTKColorType.BLACK));
            super.paintText(context, g, text, x + 1, y + 1, mnemonicIndex);

            g.setColor(context.getStyle().getColor(context,
                        GTKColorType.WHITE));
            super.paintText(context, g, text, x, y, mnemonicIndex);
        } else {
            super.paintText(context, g, text, x, y, mnemonicIndex);
        }
    }
}
