/*
 * @(#)MotifButtonUI.java	1.19 98/08/28
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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;

/**
 * MotifButton implementation
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.19 08/28/98
 * @author Rich Schiavi
 */
public class MotifButtonUI extends BasicButtonUI {

    private final static MotifButtonUI motifButtonUI = new MotifButtonUI();

    protected Color selectColor; 

    private boolean defaults_initialized = false;
    
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c){
	return motifButtonUI;
    }
    
    // ********************************
    //         Create Listeners
    // ********************************
    protected BasicButtonListener createButtonListener(AbstractButton b){
	return new MotifButtonListener(b);
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
    //          Paint Methods
    // ********************************
    public void paint(Graphics g, JComponent c) {
        fillContentArea( g, (AbstractButton)c , c.getBackground() );   
	super.paint(g,c);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect){
	// focus painting is handled by the border
    }
    
    protected void paintButtonPressed(Graphics g, AbstractButton b) {

        fillContentArea( g, b , selectColor );

    }

    protected void fillContentArea( Graphics g, AbstractButton b, Color fillColor) {

        if (b.isContentAreaFilled()) {
	    Insets margin = b.getMargin();
	    Insets insets = b.getInsets();
	    Dimension size = b.getSize();
	    g.setColor(fillColor);
	    g.fillRect(insets.left - margin.left,
		       insets.top - margin.top, 
		       size.width - (insets.left-margin.left) - (insets.right - margin.right),
		       size.height - (insets.top-margin.top) - (insets.bottom - margin.bottom));
	}
    }
}


