/*
 * @(#)BasicPasswordFieldUI.java	1.30 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.basic;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.plaf.*;


/**
 * Provides the Windows look and feel for a password field.
 * The only difference from the standard text field is that
 * the view of the text is simply a string of the echo 
 * character as specified in JPasswordField, rather than the 
 * real text contained in the field.
 *
 * @author  Timothy Prinzing
 * @version 1.30 12/19/03
 */
public class BasicPasswordFieldUI extends BasicTextFieldUI {

    /**
     * Creates a UI for a JPasswordField.
     *
     * @param c the JPasswordField
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicPasswordFieldUI();
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

    /**
     * Create the action map for Password Field.  This map provides
     * same actions for double mouse click and
     * and for triple mouse click (see bug 4231444).
     */

    ActionMap createActionMap() {
	ActionMap map = super.createActionMap();
	if (map.get(DefaultEditorKit.selectWordAction) != null) {
	    Action a = map.get(DefaultEditorKit.selectLineAction);
	    if (a != null) {
		map.remove(DefaultEditorKit.selectWordAction);
		map.put(DefaultEditorKit.selectWordAction, a);
	    }
	}
	return map;
    }

}





