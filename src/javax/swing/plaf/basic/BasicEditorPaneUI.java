/*
 * @(#)BasicEditorPaneUI.java	1.18 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.border.*;


/**
 * Provides the look and feel for a JEditorPane.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 * @version 1.18 08/28/98
 */
public class BasicEditorPaneUI extends BasicTextUI {

    /**
     * Creates a UI for the JTextPane.
     *
     * @param c the JTextPane component
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicEditorPaneUI();
    }

    /**
     * Creates a new BasicEditorPaneUI.
     */
    public BasicEditorPaneUI() {
	super();
    }

    /**
     * Fetches the name used as a key to lookup properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("EditorPane")
     */
    protected String getPropertyPrefix() {
	return "EditorPane";
    }

    /**
     * Fetches the EditorKit for the UI.  This is whatever is
     * currently set in the associated JEditorPane.
     *
     * @return the editor capabilities
     * @see TextUI#getEditorKit
     */
    public EditorKit getEditorKit(JTextComponent tc) {
	JEditorPane pane = (JEditorPane) getComponent();
	return pane.getEditorKit();
    }

}


