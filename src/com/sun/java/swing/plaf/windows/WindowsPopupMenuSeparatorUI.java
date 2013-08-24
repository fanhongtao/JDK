/*
 * @(#)WindowsPopupMenuSeparatorUI.java	1.3 07/01/18
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.windows.TMSchema.Part;
import com.sun.java.swing.plaf.windows.TMSchema.State;
import com.sun.java.swing.plaf.windows.XPStyle.Skin;

/**
 * Windows L&F implementation of PopupMenuSeparatorUI.
 *
 * @version 1.3 01/18/07
 * @author Leif Samuelsson
 * @author Igor Kushnirskiy
 */

public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {

    public static ComponentUI createUI(JComponent c) {
        return new WindowsPopupMenuSeparatorUI();
    }

    public void paint(Graphics g, JComponent c) {
        Dimension s = c.getSize();
        if (WindowsMenuItemUI.isVistaPainting()) {
            int x = 1;
            Component parent = c.getParent();
            if (parent instanceof JComponent) {
                Object gutterOffsetObject = 
                    ((JComponent) parent).getClientProperty(
                        WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
                if (gutterOffsetObject instanceof Integer) {
                    /* 
                     * gutter offset is in parent's coordinates.
                     * See comment in 
                     * WindowsPopupMenuUI.getTextOffset(JComponent)
                     */
                    x = ((Integer) gutterOffsetObject).intValue() - c.getX();
                    x += WindowsPopupMenuUI.getGutterWidth();
                }
            }
            Skin skin = XPStyle.getXP().getSkin(c, Part.MP_POPUPSEPARATOR);
            int skinHeight = skin.getHeight();
            int y = (s.height - skinHeight) / 2;
            skin.paintSkin(g, x, y, s.width - x - 1, skinHeight, State.NORMAL);
        } else {
            int y = s.height / 2;
            g.setColor(c.getForeground());
            g.drawLine(1, y - 1, s.width - 2, y - 1);

            g.setColor(c.getBackground());
            g.drawLine(1, y,     s.width - 2, y);
        }
    }

    public Dimension getPreferredSize(JComponent c) {
	int fontHeight = c.getFontMetrics(c.getFont()).getHeight();

	return new Dimension(0, fontHeight/2 + 2);
    }

}
