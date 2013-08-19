/*
 * @(#)WindowsLabelUI.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;



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
public class WindowsLabelUI extends BasicLabelUI {

    private final static WindowsLabelUI windowsLabelUI = new WindowsLabelUI();

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c){
	return windowsLabelUI;
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, 
				    int textX, int textY) {
	int mnemonicIndex = l.getDisplayedMnemonicIndex();
	// W2K Feature: Check to see if the Underscore should be rendered.
	if (WindowsLookAndFeel.isMnemonicHidden() == true) {
	    mnemonicIndex = -1;
	}

        g.setColor(l.getForeground());
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemonicIndex,
                                                     textX, textY);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, 
				     int textX, int textY) {
	int mnemonicIndex = l.getDisplayedMnemonicIndex();
	// W2K Feature: Check to see if the Underscore should be rendered.
	if (WindowsLookAndFeel.isMnemonicHidden() == true) {
	    mnemonicIndex = -1;
	}
	if ( UIManager.getColor("Label.disabledForeground") instanceof Color &&
	     UIManager.getColor("Label.disabledShadow") instanceof Color) {
	    g.setColor( UIManager.getColor("Label.disabledShadow") );
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s,
							 mnemonicIndex,
							 textX + 1, textY + 1);
	    g.setColor( UIManager.getColor("Label.disabledForeground") );
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s,
							 mnemonicIndex,
							 textX, textY);
	} else {
	    Color background = l.getBackground();
	    g.setColor(background.brighter());
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemonicIndex,
							 textX + 1, textY + 1);
	    g.setColor(background.darker());
	    BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, mnemonicIndex,
							 textX, textY);
	}
    }
}

