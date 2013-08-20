/*
 * @(#)SynthCheckBoxUI.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import java.io.Serializable;


/**
 * Synth's CheckBoxUI.
 *
 * @version 1.6, 12/19/03
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

    void paintBackground(SynthContext context, Graphics g, JComponent c) {
        context.getPainter().paintCheckBoxBackground(context, g, 0, 0,
                                                  c.getWidth(), c.getHeight());
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintCheckBoxBorder(context, g, x, y, w, h);
    }
}
