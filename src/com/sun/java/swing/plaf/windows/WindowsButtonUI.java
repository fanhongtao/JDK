/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.*;

import java.awt.*;

/**
 * Windows button.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.19 02/06/02
 * @author Jeff Dinkins
 *
 */
public class WindowsButtonUI extends BasicButtonUI
{
    private final static WindowsButtonUI windowsButtonUI = new WindowsButtonUI();

    protected int dashedRectGapX;
    protected int dashedRectGapY;
    protected int dashedRectGapWidth;
    protected int dashedRectGapHeight;

    protected Color focusColor;

    private boolean defaults_initialized = false;
    
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c){
	return windowsButtonUI;
    }
    
    // ********************************
    //         Create Listeners
    // ********************************
    protected BasicButtonListener createButtonListener(AbstractButton b) {
	return new WindowsButtonListener(b); 
    }

    // ********************************
    //            Defaults
    // ********************************
    protected void installDefaults(AbstractButton b) {
	super.installDefaults(b);
	if(!defaults_initialized) {
	    String pp = getPropertyPrefix();
	    dashedRectGapX = UIManager.getInt(pp + "dashedRectGapX");
	    dashedRectGapY = UIManager.getInt(pp + "dashedRectGapY");
	    dashedRectGapWidth = UIManager.getInt(pp + "dashedRectGapWidth");
	    dashedRectGapHeight = UIManager.getInt(pp + "dashedRectGapHeight");
	    focusColor = UIManager.getColor(pp + "focus");
	    defaults_initialized = true;
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
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
	// focus painted same color as text on Basic??
	int width = b.getWidth();
	int height = b.getHeight();
	g.setColor(getFocusColor());
	BasicGraphicsUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
					  width - dashedRectGapWidth, height - dashedRectGapHeight);
    }
    
    protected void paintButtonPressed(Graphics g, AbstractButton b){
	setTextShiftOffset();
    }
    
}

