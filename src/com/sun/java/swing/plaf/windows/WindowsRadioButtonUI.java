/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.plaf.*;

import java.awt.*;


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
public class WindowsRadioButtonUI extends BasicRadioButtonUI
{
    private static final WindowsRadioButtonUI windowsRadioButtonUI = new WindowsRadioButtonUI();

    protected int dashedRectGapX;
    protected int dashedRectGapY;
    protected int dashedRectGapWidth;
    protected int dashedRectGapHeight;

    protected Color focusColor;

    private boolean initialized = false;
    
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
	return windowsRadioButtonUI;
    }

    // ********************************
    //           Defaults
    // ********************************
    public void installDefaults(AbstractButton b) {
	super.installDefaults(b);
	if(!initialized) {
	    dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
	    dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
	    dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
	    dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
	    focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
	    initialized = true;
	}
    }

    protected Color getFocusColor() {
	return focusColor;
    }
    
    // ********************************
    //          Paint Methods
    // ********************************
    protected void paintFocus(Graphics g, Rectangle textRect, Dimension d){
	g.setColor(getFocusColor());
	BasicGraphicsUtils.drawDashedRect(g, textRect.x, textRect.y, textRect.width, textRect.height);
    } 

}

