/*
 * @(#)SynthRadioButtonUI.java	1.5 03/01/23
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
 * This comes from BasicRadioButtonUI v 1.65.
 *
 * @version 1.5, 01/23/03
 * @author Jeff Dinkins
 */
class SynthRadioButtonUI extends SynthToggleButtonUI {
    /**
     * Icon to use if one has not been specified for the radio button.
     */
    private Icon icon;


    // ********************************
    //        Create PLAF 
    // ********************************
    public static ComponentUI createUI(JComponent b) {
        return new SynthRadioButtonUI();
    }

    void fetchStyle(AbstractButton b) {
        super.fetchStyle(b);
        icon = null;
    }

    protected String getPropertyPrefix() {
        return "RadioButton.";
    }

    /**
     * Returns the Icon used in calculating the pref/min/max size.
     */
    protected Icon getSizingIcon(AbstractButton b) {
        return getIcon(b);
    }

    /**
     * Returns the Icon to use in painting the button.
     */
    protected Icon getIcon(AbstractButton b) {
        Icon icon = b.getIcon();

        if (icon != null) {
            Icon sIcon = super.getIcon(b);

            if (sIcon != null) {
                icon = sIcon;
            }
        }
        else {
            icon = getDefaultIcon(b);
        }
        return icon;
    }

    protected Icon getDefaultIcon(AbstractButton b) {
        if (icon == null) {
            SynthContext context = getContext(b);
            icon = context.getStyle().getIcon(context,
                                              getPropertyPrefix() + "icon");
            context.dispose();
        }
        return icon;
    }
}
