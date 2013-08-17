/*
 * @(#)MotifToggleButtonUI.java	1.14 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
 
package com.sun.java.swing.plaf.motif;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


/**
 * BasicToggleButton implementation
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.14 08/28/98
 * @author Rich Schiavi
 */
public class MotifToggleButtonUI extends BasicToggleButtonUI 
{
    private final static MotifToggleButtonUI motifToggleButtonUI = new MotifToggleButtonUI();

    protected Color selectColor;

    private boolean defaults_initialized = false;
    
    // ********************************
    //         Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent b) {
	return motifToggleButtonUI;
    }

    // ********************************
    //          Install Defaults
    // ********************************
    public void installDefaults(AbstractButton b) {
	super.installDefaults(b);
	if(!defaults_initialized) {
	    selectColor = UIManager.getColor(getPropertyPrefix() + "select");
	    defaults_initialized = true;
	}
	b.setOpaque(false);
    }

    protected void uninstallDefaults(AbstractButton b) {
	super.uninstallDefaults(b);
	defaults_initialized = false;
    }
    
    // ********************************
    //          Default Accessors
    // ********************************

    protected Color getSelectColor() {
	return selectColor;
    }
    
    // ********************************
    //         Paint Methods
    // ********************************
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled()) {
	    Color oldColor = g.getColor();
	    Dimension size = b.getSize();
	    Insets insets = b.getInsets();
	    Insets margin = b.getMargin();
	    //	Border border = b.getBorder();

	    g.setColor(getSelectColor());
	    g.fillRect(insets.left - margin.left,
		       insets.top - margin.top, 
		       size.width - (insets.left-margin.left) - (insets.right - margin.right),
		       size.height - (insets.top-margin.top) - (insets.bottom - margin.bottom));
		       g.setColor(oldColor);
	}
    }
    
    public Insets getInsets(JComponent c) { 
	Border border = c.getBorder();
	Insets i = border != null? border.getBorderInsets(c) : new Insets(0,0,0,0);
	return i;
    }

} 


