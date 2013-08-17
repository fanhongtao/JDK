/*
 * @(#)BasicTextFieldUI.java	1.78 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.plaf.*;

/**
 * Basis of a look and feel for a JTextField.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 * @version 1.78 11/29/01
 */
public class BasicTextFieldUI extends BasicTextUI {

    /**
     * Creates a UI for a JTextField.
     *
     * @param c the text field
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicTextFieldUI();
    }

    /**
     * Creates a new BasicTextFieldUI.
     */
    public BasicTextFieldUI() {
	super();
    }

    /**
     * Fetches the name used as a key to lookup properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("TextField")
     */
    protected String getPropertyPrefix() {
	return "TextField";
    }

    /**
     * Creates the caret for a field.
     *
     * @return the caret
     */
    protected Caret createCaret() {
	return new BasicFieldCaret();
    }

    /**
     * Creates a view (FieldView) based on an element.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
	return new FieldView(elem);
    }

    /**
     * BasicFieldCaret has different scrolling behavior than
     * DefaultCaret, selects the field when focus enters it, and
     * deselects the field when focus leaves.
     */
    static class BasicFieldCaret extends DefaultCaret implements UIResource {

	public BasicFieldCaret() {
	    super();
	}

    }

}
