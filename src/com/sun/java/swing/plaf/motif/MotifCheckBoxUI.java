/*
 * @(#)MotifCheckBoxUI.java	1.18 98/08/28
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

import javax.swing.plaf.*;

import java.awt.*;

/**
 * MotifCheckBox implementation
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.18 08/28/98
 * @author Rich Schiavi
 */
public class MotifCheckBoxUI extends MotifRadioButtonUI {

    private static final MotifCheckBoxUI motifCheckBoxUI = new MotifCheckBoxUI();

    private final static String propertyPrefix = "CheckBox" + ".";

    private boolean defaults_initialized = false;


    // ********************************
    //         Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c){
	return motifCheckBoxUI;
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
