/*
 * @(#)WindowsSpinnerUI.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
	if (XPStyle.getXP() != null) {
	    JButton xpButton = new XPStyle.GlyphButton("spin.down");
	    xpButton.setRequestFocusEnabled(false);
            installPreviousButtonListeners(xpButton);
            return xpButton;
        }
        return super.createPreviousButton();
    }

    protected Component createNextButton() {
	if (XPStyle.getXP() != null) {
	    JButton xpButton = new XPStyle.GlyphButton("spin.up");
	    xpButton.setRequestFocusEnabled(false);
            installNextButtonListeners(xpButton);
	    return xpButton;
        }
        return super.createNextButton();
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

