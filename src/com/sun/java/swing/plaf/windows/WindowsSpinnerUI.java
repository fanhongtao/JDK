/*
 * @(#)WindowsSpinnerUI.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.event.*;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.*;



public class WindowsSpinnerUI extends BasicSpinnerUI {
    public static ComponentUI createUI(JComponent c) {
        return new WindowsSpinnerUI();
    }

    protected Component createPreviousButton() {
        AbstractButton classicButton = (AbstractButton)super.createPreviousButton();

	if (XPStyle.getXP() != null) {
	    JButton xpButton = new XPStyle.GlyphButton("spin.down");
	    xpButton.setRequestFocusEnabled(false);
	    xpButton.addActionListener((ActionListener)getUIResource(classicButton.getActionListeners()));
	    xpButton.addMouseListener((MouseListener)getUIResource(classicButton.getMouseListeners()));
	    return xpButton;
	} else {
	    return classicButton;
	}
    }

    protected Component createNextButton() {
        AbstractButton classicButton = (AbstractButton)super.createNextButton();

	if (XPStyle.getXP() != null) {
	    JButton xpButton = new XPStyle.GlyphButton("spin.up");
	    xpButton.setRequestFocusEnabled(false);
	    xpButton.addActionListener((ActionListener)getUIResource(classicButton.getActionListeners()));
	    xpButton.addMouseListener((MouseListener)getUIResource(classicButton.getMouseListeners()));
	    return xpButton;
	} else {
	    return classicButton;
	}
    }

    private UIResource getUIResource(Object[] listeners) {
        for (int counter = 0; counter < listeners.length; counter++) {
            if (listeners[counter] instanceof UIResource) {
                return (UIResource)listeners[counter];
            }
        }
        return null;
    }
}

