/*
 * @(#)WindowsToolBarUI.java	1.18 06/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import javax.swing.plaf.*;

import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicToolBarUI;

import com.sun.java.swing.plaf.windows.TMSchema.Part;

public class WindowsToolBarUI extends BasicToolBarUI {
    
    public static ComponentUI createUI(JComponent c) {
	return new WindowsToolBarUI();
    }

    protected void installDefaults() {
	if (XPStyle.getXP() != null) {
	    setRolloverBorders(true);
	}
	super.installDefaults();
    }

    protected Border createRolloverBorder() {
	if (XPStyle.getXP() != null) {
	    return new EmptyBorder(3, 3, 3, 3);
	} else {
	    return super.createRolloverBorder();
	}
    }

    protected Border createNonRolloverBorder() {
	if (XPStyle.getXP() != null) {
	    return new EmptyBorder(3, 3, 3, 3);
	} else {
	    return super.createNonRolloverBorder();
	}
    }

    public void paint(Graphics g, JComponent c) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
            xp.getSkin(c, Part.TP_TOOLBAR).paintSkin(g, 0, 0, c.getWidth(), 
                c.getHeight(), null);
	} else {
	    super.paint(g, c);
	}
    }

    static Border getRolloverBorder(AbstractButton b) {
        XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            return xp.getBorder(b, WindowsButtonUI.getXPButtonType(b));
        }
        return null;
    }
}

