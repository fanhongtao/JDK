/*
 * @(#)WindowsToggleButtonUI.java	1.26 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.*;

import java.awt.*;



/**
 * A Windows toggle button.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.26 01/23/03
 * @author Jeff Dinkins
 */
public class WindowsToggleButtonUI extends BasicToggleButtonUI
{
    protected static int dashedRectGapX;
    protected static int dashedRectGapY;
    protected static int dashedRectGapWidth;
    protected static int dashedRectGapHeight;

    protected Color focusColor;
    
    private final static WindowsToggleButtonUI windowsToggleButtonUI = new WindowsToggleButtonUI();

    private boolean defaults_initialized = false;
    
    public static ComponentUI createUI(JComponent b) {
	return windowsToggleButtonUI;
    }

    // ********************************
    //            Defaults
    // ********************************
    protected void installDefaults(AbstractButton b) {
	super.installDefaults(b);
	if(!defaults_initialized) {
	    String pp = getPropertyPrefix();
	    dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
	    dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
	    dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
	    dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
	    focusColor = UIManager.getColor(pp + "focus");
	    defaults_initialized = true;
	}

	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    b.setBorder(xp.getBorder("button.pushbutton"));
	    b.setOpaque(false);
	    b.setRolloverEnabled(true);
	}
    }

    protected void uninstallDefaults(AbstractButton b) {
	super.uninstallDefaults(b);
	defaults_initialized = false;
    }
    

    protected Color getFocusColor() {
	return focusColor;
    }
    
    
    // ********************************
    //         Paint Methods
    // ********************************

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (XPStyle.getXP() == null &&
	    b.isContentAreaFilled() && 
	    !(b.getBorder() instanceof UIResource)) {
	    // This is a special case in which the toggle button in the
	    // Rollover JToolBar will render the button in a pressed state
	    Color oldColor = g.getColor();

            int w = b.getWidth();
	    int h = b.getHeight();
	    UIDefaults table = UIManager.getLookAndFeelDefaults();

	    Color shade = table.getColor("ToggleButton.shadow");
	    Component p = b.getParent();
	    if (p != null && p.getBackground().equals(shade)) {
		shade = table.getColor("ToggleButton.darkShadow");
	    }
	    g.setColor(shade);
	    g.drawRect(0, 0, w-1, h-1);
	    g.setColor(table.getColor("ToggleButton.highlight"));
	    g.drawLine(w-1, 0, w-1, h-1);
	    g.drawLine(0, h-1, w-1, h-1);
	    g.setColor(oldColor);
	}
    }

    public void paint(Graphics g, JComponent c) {
	if (XPStyle.getXP() != null) {
	    WindowsButtonUI.paintXPButtonBackground(g, c);
	}
	super.paint(g, c);
    }


    /**
     * Overridden method to render the text without the mnemonic
     */
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
	WindowsGraphicsUtils.paintText(g, b, textRect, text, getTextShiftOffset());
    } 

    protected void paintFocus(Graphics g, AbstractButton b,
			      Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
	if (b.getParent() instanceof JToolBar) {
	    // Windows doesn't draw the focus rect for buttons in a toolbar.
	    return;
	}
	g.setColor(getFocusColor());
	BasicGraphicsUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
					  b.getWidth() - dashedRectGapWidth,
					  b.getHeight() - dashedRectGapHeight);
    }

    // ********************************
    //          Layout Methods
    // ********************************
    public Dimension getPreferredSize(JComponent c) {
	Dimension d = super.getPreferredSize(c);

	/* Ensure that the width and height of the button is odd,
	 * to allow for the focus line if focus is painted
	 */
        AbstractButton b = (AbstractButton)c;
	if (b.isFocusPainted()) {
	    if(d.width % 2 == 0) { d.width += 1; }
	    if(d.height % 2 == 0) { d.height += 1; }
	}
	return d;
    }
}

