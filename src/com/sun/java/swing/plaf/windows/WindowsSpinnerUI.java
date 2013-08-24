/*
 * @(#)WindowsSpinnerUI.java	1.14 06/12/19
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

import com.sun.java.swing.plaf.windows.TMSchema.Part;



public class WindowsSpinnerUI extends BasicSpinnerUI {
    public static ComponentUI createUI(JComponent c) {
        return new WindowsSpinnerUI();
    }

    protected Component createPreviousButton() {
	if (XPStyle.getXP() != null) {
            JButton xpButton = new XPStyle.GlyphButton(spinner, Part.SPNP_SPINDOWN);
	    xpButton.setRequestFocusEnabled(false);
            installPreviousButtonListeners(xpButton);
            return xpButton;
        }
        return super.createPreviousButton();
    }

    protected Component createNextButton() {
	if (XPStyle.getXP() != null) {
            JButton xpButton = new XPStyle.GlyphButton(spinner, Part.SPNP_SPINUP);
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

