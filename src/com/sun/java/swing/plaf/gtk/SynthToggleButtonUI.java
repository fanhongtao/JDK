/*
 * @(#)SynthToggleButtonUI.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.View;

/**
 * Synth's ToggleButtonUI, derived from BasicToggleButtonUI version 1.55.
 * <p>
 * @version 1.4, 01/23/03
 * @author Jeff Dinkins
 */
class SynthToggleButtonUI extends SynthButtonUI {
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent b) {
        return new SynthToggleButtonUI();
    }

    protected String getPropertyPrefix() {
        return "ToggleButton.";
    }
}
