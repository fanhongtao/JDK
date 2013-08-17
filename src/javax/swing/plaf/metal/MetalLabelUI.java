/*
 * @(#)MetalLabelUI.java	1.7 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


import java.awt.*;


/**
 * A Windows L&F implementation of LabelUI.  This implementation 
 * is completely static, i.e. there's only one UIView implementation 
 * that's shared by all JLabel objects.
 *
 * @version 1.7 02/02/00
 * @author Hans Muller
 */

public class MetalLabelUI extends BasicLabelUI
{
    protected static MetalLabelUI metalLabelUI = new MetalLabelUI();


    public static ComponentUI createUI(JComponent c) {
	return metalLabelUI;
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
	int accChar = l.getDisplayedMnemonic();
	g.setColor(UIManager.getColor("Label.disabledForeground"));
	BasicGraphicsUtils.drawString(g, s, accChar, textX, textY);
    }
}

