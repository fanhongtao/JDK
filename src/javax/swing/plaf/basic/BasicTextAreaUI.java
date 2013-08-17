/*
 * @(#)BasicTextAreaUI.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.plaf.basic;

import java.beans.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;

/**
 * Provides the look and feel for a plain text editor.  In this
 * implementation the default UI is extended to act as a simple
 * view factory.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 * @version 1.54 10/20/98
 */
public class BasicTextAreaUI extends BasicTextUI {

    /**
     * Creates a UI for a JTextArea.
     *
     * @param ta a text area
     * @return the UI
     */
    public static ComponentUI createUI(JComponent ta) {
        return new BasicTextAreaUI();
    }

    /**
     * Constructs a new BasicTextAreaUI object.
     */
    public BasicTextAreaUI() {
	super();
    }

    /**
     * Fetches the name used as a key to look up properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("TextArea")
     */
    protected String getPropertyPrefix() {
	return "TextArea";
    }

    /**
     * This method gets called when a bound property is changed
     * on the associated JTextComponent.  This is a hook
     * which UI implementations may change to reflect how the
     * UI displays bound properties of JTextComponent subclasses.
     * This is implemented to rebuild the View when the
     * <em>WrapLine</em> or the <em>WrapStyleWord</em> property changes.
     *
     * @param evt the property change event
     */
    protected void propertyChange(PropertyChangeEvent evt) {
	if (evt.getPropertyName().equals("lineWrap") ||
	    evt.getPropertyName().equals("wrapStyleWord")) {
	    // rebuild the view
	    modelChanged();
	}
    }

    /**
     * Creates the view for an element.  Returns a WrappedPlainView or
     * PlainView.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
	JTextComponent c = getComponent();
	if (c instanceof JTextArea) {
	    JTextArea area = (JTextArea) c;
	    View v;
	    if (area.getLineWrap()) {
		v = new WrappedPlainView(elem, area.getWrapStyleWord());
	    } else {
		v = new PlainView(elem);
	    }
	    return v;
	}
	return null;
    }
    
}
