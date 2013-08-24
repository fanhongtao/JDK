/*
 * @(#)MetalLabelUI.java	1.15 05/11/30
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import sun.swing.SwingUtilities2;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


import java.awt.*;


/**
 * A Windows L&F implementation of LabelUI.  This implementation 
 * is completely static, i.e. there's only one UIView implementation 
 * that's shared by all JLabel objects.
 *
 * @version 1.15 11/30/05
 * @author Hans Muller
 */

public class MetalLabelUI extends BasicLabelUI
{
   /**
    * The default <code>MetalLabelUI</code> instance. This field might
    * not be used. To change the default instance use a subclass which
    * overrides the <code>createUI</code> method, and place that class
    * name in defaults table under the key "LabelUI".
    */ 
    protected static MetalLabelUI metalLabelUI = new MetalLabelUI();
    private final static MetalLabelUI SAFE_METAL_LABEL_UI = new MetalLabelUI();


    public static ComponentUI createUI(JComponent c) {
        if (System.getSecurityManager() != null) {
            return SAFE_METAL_LABEL_UI;
        } else {
            return metalLabelUI;
        }
    }

    /**
     * Just paint the text gray (Label.disabledForeground) rather than 
     * in the labels foreground color.
     *
     * @see #paint
     * @see #paintEnabledText
     */
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY)
    {
	int mnemIndex = l.getDisplayedMnemonicIndex();
	g.setColor(UIManager.getColor("Label.disabledForeground"));
	SwingUtilities2.drawStringUnderlineCharAt(l, g, s, mnemIndex,
                                                   textX, textY);
    }
}

