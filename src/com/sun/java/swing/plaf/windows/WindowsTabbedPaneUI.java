/*
 * @(#)WindowsTabbedPaneUI.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;



/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsTabbedPaneUI extends BasicTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new WindowsTabbedPaneUI();
    }

    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected ) {
	if (XPStyle.getXP() == null) {
	    super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
	}
    }

    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h, boolean isSelected ) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    XPStyle.Skin skin = xp.getSkin("tab.tabitem");

	    int rotate = 0;
	    int tx = 0;
	    int ty = 0;

	    switch (tabPlacement) {
	      case RIGHT:  rotate =  90; tx = w;         break;
	      case BOTTOM: rotate = 180; tx = w; ty = h; break;
	      case LEFT:   rotate = 270;         ty = h; break;
	    }

	    g.translate(x+tx, y+ty);
	    if (rotate != 0 && (g instanceof Graphics2D)) {
		((Graphics2D)g).rotate(Math.toRadians((double)rotate));
	    }
	    if (rotate == 90 || rotate == 270) {
		skin.paintSkin(g, 0, 0, h, w, isSelected ? 2 : 0);
	    } else {
		skin.paintSkin(g, 0, 0, w, h, isSelected ? 2 : 0);
	    }
	    if (rotate != 0 && (g instanceof Graphics2D)) {
		((Graphics2D)g).rotate(-Math.toRadians((double)rotate));
	    }
	    g.translate(-x-tx, -y-ty);
	} else {
	    super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
	}
    }
}

