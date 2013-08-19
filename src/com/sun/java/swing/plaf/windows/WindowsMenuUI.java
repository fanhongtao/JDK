/*
 * @(#)WindowsMenuUI.java	1.20 03/05/06
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.event.MouseInputListener;
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
public class WindowsMenuUI extends BasicMenuUI {

    private boolean isMouseOver = false;

    public static ComponentUI createUI(JComponent x) {
	return new WindowsMenuUI();
    }

    /**
     * Draws the background of the menu.
     * @since 1.4
     */
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
	JMenu menu = (JMenu)menuItem;
	ButtonModel model = menuItem.getModel();

	// Use superclass method for the old Windows LAF,
        // for submenus, and for XP toplevel if selected or pressed
	if (WindowsLookAndFeel.isClassicWindows() ||
	    !menu.isTopLevelMenu() ||
	    (XPStyle.getXP() != null && (model.isArmed() || model.isSelected()))) {

	    super.paintBackground(g, menuItem, bgColor);
	    return;
	}

	Color oldColor = g.getColor();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Color highlight = table.getColor("controlLtHighlight");
	Color shadow = table.getColor("controlShadow");

	g.setColor(menuItem.getBackground());
	g.fillRect(0,0, menuWidth, menuHeight);

        if(menuItem.isOpaque()) {
            if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
		// Draw a lowered bevel border
		g.setColor(shadow);
		g.drawLine(0,0, menuWidth - 1,0);
		g.drawLine(0,0, 0,menuHeight - 2);

		g.setColor(highlight);
		g.drawLine(menuWidth - 1,0, menuWidth - 1,menuHeight - 2);
		g.drawLine(0,menuHeight - 2, menuWidth - 1,menuHeight - 2);
            } else {
		if (isMouseOver() && model.isEnabled()) {
		    if (XPStyle.getXP() != null) {
			g.setColor(selectionBackground); // Uses protected field.
			g.fillRect(0, 0, menuWidth, menuHeight);
		    } else {
			// Draw a raised bevel border
			g.setColor(highlight);
			g.drawLine(0,0, menuWidth - 1,0);
			g.drawLine(0,0, 0,menuHeight - 2);

			g.setColor(shadow);
			g.drawLine(menuWidth - 1,0, menuWidth - 1,menuHeight - 2);
			g.drawLine(0,menuHeight - 2, menuWidth - 1,menuHeight - 2);
		    }
		} else {
		    g.setColor(menuItem.getBackground());
		    g.fillRect(0,0, menuWidth, menuHeight);
		}
            }
        }
	g.setColor(oldColor);
    }

    /**
     * Method which renders the text of the current menu item.
     * <p>
     * @param g Graphics context
     * @param menuItem Current menu item to render
     * @param textRect Bounding rectangle to render the text.
     * @param text String to render
     * @since 1.4
     */
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
	// Note: This method is almost identical to the same method in WindowsMenuItemUI
	JMenu menu = (JMenu)menuItem;
	ButtonModel model = menuItem.getModel();

	if(!model.isEnabled()) {
	    // *** paint the text disabled
	    WindowsGraphicsUtils.paintText(g, menuItem, textRect, text, 0);
	} else {
	    FontMetrics fm = g.getFontMetrics();
	    int mnemonicIndex = menuItem.getDisplayedMnemonicIndex();
	    // W2K Feature: Check to see if the Underscore should be rendered.
	    if (WindowsLookAndFeel.isMnemonicHidden()) {
		mnemonicIndex = -1;
	    }

	    Color oldColor = g.getColor();

	    // For Win95, the selected text color is the selection forground color
	    if ((model.isSelected() && (WindowsLookAndFeel.isClassicWindows() ||
					!menu.isTopLevelMenu())) ||
		(XPStyle.getXP() != null && (isMouseOver() ||
					     model.isArmed() ||
					     model.isSelected()))) {
		g.setColor(selectionForeground); // Uses protected field.
            }
 	    BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,
                                          mnemonicIndex, 
					  textRect.x,
					  textRect.y + fm.getAscent());
	    g.setColor(oldColor);
	}
    }

    /**
     * Set the temporary flag to indicate if the mouse has entered the menu.
     */
    private void setMouseOver(boolean over) {
	isMouseOver = over;
    }

    /**
     * Get the temporary flag to indicate if the mouse has entered the menu.
     */
    private boolean isMouseOver() {
	return isMouseOver;
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
        return new WindowsMouseInputHandler();
    }

    /**
     * This class implements a mouse handler that sets the rollover flag to
     * true when the mouse enters the menu and false when it exits.
     * @since 1.4
     */
    protected class WindowsMouseInputHandler extends BasicMenuUI.MouseInputHandler {
	public void mouseEntered(MouseEvent evt) {
	    super.mouseEntered(evt);
	    if (!WindowsLookAndFeel.isClassicWindows()) {
		setMouseOver(true);
		menuItem.repaint();
	    }
	}

	public void mouseExited(MouseEvent evt) {
	    super.mouseExited(evt);
	    if (!WindowsLookAndFeel.isClassicWindows()) {
		setMouseOver(false);
		menuItem.repaint();
	    }
	}
    }
}

