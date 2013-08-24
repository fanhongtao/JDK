/*
 * @(#)WindowsPopupMenuSeparatorUI.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;
import javax.swing.plaf.ComponentUI;

/**
 * Windows L&F implementation of PopupMenuSeparatorUI.
 *
 * @version 1.2 11/17/05
 * @author Leif Samuelsson
 */

public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {

    public static ComponentUI createUI(JComponent c) {
        return new WindowsPopupMenuSeparatorUI();
    }

    public void paint(Graphics g, JComponent c) {
        Dimension s = c.getSize();
	int y = s.height / 2;

	g.setColor(c.getForeground());
	g.drawLine(1, y - 1, s.width - 2, y - 1);

	g.setColor(c.getBackground());
	g.drawLine(1, y,     s.width - 2, y);
    }

    public Dimension getPreferredSize(JComponent c) {
	int fontHeight = c.getFontMetrics(c.getFont()).getHeight();

	return new Dimension(0, fontHeight/2 + 2);
    }

}
