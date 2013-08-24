/*
 * @(#)WindowsMenuItemUI.java	1.22 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


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
public class WindowsMenuItemUI extends BasicMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
	return new WindowsMenuItemUI();
    }

    /**
     * Method which renders the text of the current menu item.
     * <p>
     * @param g Graphics context
     * @param menuItem Current menu item to render
     * @param textRect Bounding rectangle to render the text.
     * @param text String to render
     */
    protected void paintText(Graphics g, JMenuItem menuItem,
                             Rectangle textRect, String text) {

	ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();

        if(model.isEnabled() &&
            (model.isArmed() || (menuItem instanceof JMenu &&
             model.isSelected()))) {
            g.setColor(selectionForeground); // Uses protected field.
        }

        WindowsGraphicsUtils.paintText(g, menuItem, textRect, text, 0);

        g.setColor(oldColor);
    }
}

