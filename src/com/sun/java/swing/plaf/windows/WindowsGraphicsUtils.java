/*
 * @(#)WindowsGraphicsUtils.java	1.7 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * A collection of static utility methods used for rendering the Windows look 
 * and feel.
 * 
 * @version 1.7 12/03/01
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
        FontMetrics fm = g.getFontMetrics();

	int mnemIndex = b.getDisplayedMnemonicIndex();
	// W2K Feature: Check to see if the Underscore should be rendered.
	if (WindowsLookAndFeel.isMnemonicHidden() == true) {
            mnemIndex = -1;
	}

	/* Draw the Text */
	if(model.isEnabled()) {
	    /*** paint the text normally */
	    g.setColor(b.getForeground());
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g,text, mnemIndex,
					  textRect.x + textShiftOffset,
					  textRect.y + fm.getAscent() + textShiftOffset);
	}
	else {
	    /*** paint the text disabled ***/
	    if ( UIManager.getColor("Button.disabledForeground") instanceof Color &&
		 UIManager.getColor("Button.disabledShadow") instanceof Color) {
		g.setColor( UIManager.getColor("Button.disabledShadow") );
		BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,
                                              mnemIndex,
					      textRect.x, 
					      textRect.y + fm.getAscent());
		g.setColor( UIManager.getColor("Button.disabledForeground") );
		BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,
                                              mnemIndex,
					      textRect.x - 1, 
					      textRect.y + fm.getAscent() - 1);
	    } else {
		g.setColor(b.getBackground().brighter());
		BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,
                                              mnemIndex, 
					      textRect.x, 
					      textRect.y + fm.getAscent());
		g.setColor(b.getBackground().darker());
		BasicGraphicsUtils.drawStringUnderlineCharAt(g,text,
                                              mnemIndex, 
					      textRect.x - 1, 
					      textRect.y + fm.getAscent() - 1);
	    }
	}
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }
}

