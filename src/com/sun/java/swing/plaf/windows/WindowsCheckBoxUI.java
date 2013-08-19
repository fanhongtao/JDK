/*
 * @(#)WindowsCheckBoxUI.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.plaf.*;

import java.awt.*;

/**
 * Windows check box.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.19 01/23/03
 * @author Jeff Dinkins
 */
public class WindowsCheckBoxUI extends WindowsRadioButtonUI
{
    // NOTE: MetalCheckBoxUI inherts from MetalRadioButtonUI instead
    // of BasicCheckBoxUI because we want to pick up all the
    // painting changes made in MetalRadioButtonUI.

    private static final WindowsCheckBoxUI windowsCheckBoxUI = new WindowsCheckBoxUI();

    private final static String propertyPrefix = "CheckBox" + ".";

    private boolean defaults_initialized = false;
    
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
	return windowsCheckBoxUI;
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

    public void uninstallDefaults(AbstractButton b) {
	super.uninstallDefaults(b);
	defaults_initialized = false;
    }

}

