/*
 * @(#)MotifRadioButtonUI.java	1.23 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import javax.swing.plaf.*;

import java.awt.*;

/**
 * RadioButtonUI implementation for MotifRadioButtonUI
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.23 11/17/05
 * @author Rich Schiavi
 */
public class MotifRadioButtonUI extends BasicRadioButtonUI {

    private static final MotifRadioButtonUI motifRadioButtonUI = new MotifRadioButtonUI();

    protected Color focusColor;

    private boolean defaults_initialized = false;
    
    // ********************************
    //         Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
	return motifRadioButtonUI;
    }

    // ********************************
    //          Install Defaults
    // ********************************
    public void installDefaults(AbstractButton b) {
	super.installDefaults(b);
	if(!defaults_initialized) {
	    focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
	    defaults_initialized = true;
	}
    }

    protected void uninstallDefaults(AbstractButton b) {
	super.uninstallDefaults(b);
	defaults_initialized = false;
    }
    
    // ********************************
    //          Default Accessors
    // ********************************

    protected Color getFocusColor() {
	return focusColor;
    }
    
    // ********************************
    //         Paint Methods
    // ********************************
    protected void paintFocus(Graphics g, Rectangle t, Dimension d){
	g.setColor(getFocusColor());
	g.drawRect(0,0,d.width-1,d.height-1);
    } 
    
} 
