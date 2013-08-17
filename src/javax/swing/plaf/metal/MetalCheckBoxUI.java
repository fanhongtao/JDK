/*
 * @(#)MetalCheckBoxUI.java	1.14 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import java.io.Serializable;


/**
 * CheckboxUI implementation for MetalCheckboxUI
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.14 02/02/00
 * @author Michael C. Albers
 *
 */
public class MetalCheckBoxUI extends MetalRadioButtonUI {
    
    // NOTE: MetalCheckBoxUI inherts from MetalRadioButtonUI instead
    // of BasicCheckBoxUI because we want to pick up all the
    // painting changes made in MetalRadioButtonUI.

    private final static MetalCheckBoxUI checkboxUI = new MetalCheckBoxUI();

    private final static String propertyPrefix = "CheckBox" + ".";

    private boolean defaults_initialized = false;

    // ********************************
    //         Create PlAF
    // ********************************
    public static ComponentUI createUI(JComponent b) {
        return checkboxUI;
    }

    public String getPropertyPrefix() {
	return propertyPrefix;
    }

    // ********************************
    //          Defaults
    // ********************************
    public void installDefaults(AbstractButton b) {
	super.installDefaults(b);
	if(!defaults_initialized) {
	    icon = UIManager.getIcon(getPropertyPrefix() + "icon");
	    defaults_initialized = true;
	}
    }
    
    protected void uninstallDefaults(AbstractButton b) {
	super.uninstallDefaults(b);
	defaults_initialized = false;
    }

}
