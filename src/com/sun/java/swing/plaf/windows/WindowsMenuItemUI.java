/*
 * @(#)WindowsMenuItemUI.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
	// Note: This method is almost identical to the same method in WindowsMenuUI
	ButtonModel model = menuItem.getModel();

	if(!model.isEnabled()) {
	    // *** paint the text disabled
	    WindowsGraphicsUtils.paintText(g, menuItem, textRect, text, 0);
	} else {
	    FontMetrics fm = g.getFontMetrics();
	    int mnemonicIndex = menuItem.getDisplayedMnemonicIndex();
	    // W2K Feature: Check to see if the Underscore should be rendered.
	    if (WindowsLookAndFeel.isMnemonicHidden() == true) {
		mnemonicIndex = -1;
	    }

	    Color oldColor = g.getColor();

	    // *** paint the text normally
	    if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
		g.setColor(selectionForeground); // Uses protected field.
	    }
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,
                                          mnemonicIndex,
					  textRect.x, 
					  textRect.y + fm.getAscent());
	    g.setColor(oldColor);
	}
    }

}

