/*
 * @(#)SynthFormattedTextFieldUI.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * Provides the look and feel implementation for
 * <code>JFormattedTextField</code>.
 *
 * @version 1.3, 01/23/03 (based on BasicFormattedTextFieldUI v 1.2)
 */
class SynthFormattedTextFieldUI extends SynthTextFieldUI {
    /**
     * Creates a UI for a JFormattedTextField.
     *
     * @param c the formatted text field
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new SynthFormattedTextFieldUI();
    }

    /**
     * Fetches the name used as a key to lookup properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name "FormattedTextField"
     */
    protected String getPropertyPrefix() {
	return "FormattedTextField";
    }
}
