/*
 * @(#)SynthPasswordFieldUI.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;

/**
 * Provides the Synth look and feel for a password field.
 * The only difference from the standard text field is that
 * the view of the text is simply a string of the echo 
 * character as specified in JPasswordField, rather than the 
 * real text contained in the field.
 *
 * @author  Shannon Hickey
 * @version 1.4 01/23/03 (based on revision 1.27 of BasicPasswordFieldUI)
 */
class SynthPasswordFieldUI extends SynthTextFieldUI {

    /**
     * Creates a UI for a JPasswordField.
     *
     * @param c the JPasswordField
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new SynthPasswordFieldUI();
    }

    /**
     * Fetches the name used as a key to look up properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("PasswordField")
     */
    protected String getPropertyPrefix() {
        return "PasswordField";
    }

    /**
     * Creates a view (PasswordView) for an element.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
        return new PasswordView(elem);
    }

}
