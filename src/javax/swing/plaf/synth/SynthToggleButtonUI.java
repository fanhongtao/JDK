/*
 * @(#)SynthToggleButtonUI.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.View;

/**
 * Synth's ToggleButtonUI.
 * <p>
 * @version 1.6, 12/19/03
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

    void paintBackground(SynthContext context, Graphics g, JComponent c) {
        context.getPainter().paintToggleButtonBackground(context, g, 0, 0,
                                                c.getWidth(), c.getHeight());
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintToggleButtonBorder(context, g, x, y, w, h);
    }
}
