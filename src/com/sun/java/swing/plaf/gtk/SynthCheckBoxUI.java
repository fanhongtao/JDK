/*
 * @(#)SynthCheckBoxUI.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import java.io.Serializable;


/**
 * This comes from BasicCheckBoxUI v 1.35
 *
 * @version 1.4, 01/23/03
 * @author Jeff Dinkins
 */
class SynthCheckBoxUI extends SynthRadioButtonUI {

    // ********************************
    //            Create PLAF 
    // ********************************
    public static ComponentUI createUI(JComponent b) {
        return new SynthCheckBoxUI();
    }

    protected String getPropertyPrefix() {
	return "CheckBox.";
    }
}
