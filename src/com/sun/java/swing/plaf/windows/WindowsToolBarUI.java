/*
 * @(#)WindowsToolBarUI.java	1.10 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicToolBarUI;

public class WindowsToolBarUI extends BasicToolBarUI {
    
    public static ComponentUI createUI(JComponent c) {
	return new WindowsToolBarUI();
    }
}

