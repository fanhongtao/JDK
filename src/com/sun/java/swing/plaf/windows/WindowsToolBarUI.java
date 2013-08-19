/*
 * @(#)WindowsToolBarUI.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

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
	    String category = "toolbar";
	    String subCategory = (String)c.getClientProperty("XPStyle.subClass");
	    if (subCategory != null) {
		category = subCategory + "::" + category;
	    }
	    xp.getSkin(category).paintSkin(g, 0, 0, c.getSize().width, c.getSize().height, 0);
	} else {
	    super.paint(g, c);
	}
    }

}

