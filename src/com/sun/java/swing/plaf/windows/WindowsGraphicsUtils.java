/*
 * @(#)WindowsGraphicsUtils.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import com.sun.java.swing.SwingUtilities2;

import java.awt.*;

import javax.swing.*;

/**
 * A collection of static utility methods used for rendering the Windows look 
 * and feel.
 * 
 * @version 1.13 12/19/03
 * @author Mark Davidson
 * @since 1.4
 */
public class WindowsGraphicsUtils {
    
    /**
     * Renders a text String in Windows without the mnemonic.
     * This is here because the WindowsUI hiearchy doesn't match the Component heirarchy. All
     * the overriden paintText methods of the ButtonUI delegates will call this static method.
     * <p>
     * @param g Graphics context
     * @param b Current button to render
     * @param textRect Bounding rectangle to render the text.
     * @param text String to render
     */
    public static void paintText(Graphics g, AbstractButton b, 
					Rectangle textRect, String text,
					int textShiftOffset) {
        ButtonModel model = b.getModel();
        FontMetrics fm = SwingUtilities2.getFontMetrics(b, g);

	int mnemIndex = b.getDisplayedMnemonicIndex();
	// W2K Feature: Check to see if the Underscore should be rendered.
	if (WindowsLookAndFeel.isMnemonicHidden() == true) {
            mnemIndex = -1;
	}

	/* Draw the Text */
	Color color = b.getForeground();
	if(model.isEnabled()) {
	    /*** paint the text normally */
            if(!(b instanceof JMenuItem && model.isArmed()) 
                && !(b instanceof JMenu && (model.isSelected() || model.isRollover()))) {
                /* We shall not set foreground color for selected menu or
                 * armed menuitem. Foreground must be set in appropriate
                 * Windows* class because these colors passes from
                 * BasicMenuItemUI as protected fields and we can't
                 * reach them from this class */
	        g.setColor(b.getForeground());
            }
	    SwingUtilities2.drawStringUnderlineCharAt(b, g,text, mnemIndex,
					  textRect.x + textShiftOffset,
					  textRect.y + fm.getAscent() + textShiftOffset);
	} else {	/*** paint the text disabled ***/
	    color        = UIManager.getColor("Button.disabledForeground");
	    Color shadow = UIManager.getColor("Button.disabledShadow");

	    XPStyle xp = XPStyle.getXP();
	    if (xp != null) {
		color = xp.getColor("button.pushbutton(disabled).textcolor", color);
	    } else {
		// Paint shadow only if not XP
		if (shadow == null) {
		    shadow = b.getBackground().darker();
		}
		g.setColor(shadow);
		SwingUtilities2.drawStringUnderlineCharAt(b, g,text,
							     mnemIndex,
							     textRect.x, 
							     textRect.y + fm.getAscent());
	    }
	    if (color == null) {
		color = b.getBackground().brighter();
	    }
	    g.setColor(color);
	    SwingUtilities2.drawStringUnderlineCharAt(b, g,text,
							 mnemIndex,
							 textRect.x - 1, 
							 textRect.y + fm.getAscent() - 1);
	}
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }
}

