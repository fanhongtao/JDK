/*
 * @(#)WindowsSeparatorUI.java	1.11 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;
import javax.swing.*;


/**
 * Draws Windows toolbar separators.
 * XXX TODO: This is actually a WindowsToolBarSeparator. Should introduce that
 * class and revert this back to WindowsSeparator.
 * <p>
 *
 * @version 1.11 12/03/01
 * @author Mark Davidson
 */
public class WindowsSeparatorUI extends BasicToolBarSeparatorUI {

    public static ComponentUI createUI( JComponent c ) {
        return new WindowsSeparatorUI();
    }

    public void paint( Graphics g, JComponent c ) {
	Color temp = g.getColor();
	
	UIDefaults table = UIManager.getLookAndFeelDefaults();

	Color shadow = table.getColor("ToolBar.shadow");
	Color highlight = table.getColor("ToolBar.highlight");
	
	Dimension size = c.getSize();

	if (((JSeparator)c).getOrientation() == SwingConstants.HORIZONTAL) {
	    int x = (size.width / 2) - 1;
	    g.setColor(shadow);
	    g.drawLine(x, 2, x, size.height - 2);

	    g.setColor(highlight);
	    g.drawLine(x + 1, 2, x + 1, size.height - 2);
	} else {
	    int y = (size.height / 2) - 1;
	    g.setColor(shadow);
	    g.drawLine(2, y, size.width - 2, y);
	    g.setColor(highlight);
	    g.drawLine(2, y + 1, size.width - 2, y + 1);
	}
	g.setColor(temp);
    }
}

