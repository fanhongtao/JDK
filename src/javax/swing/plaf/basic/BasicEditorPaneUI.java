/*
 * @(#)BasicEditorPaneUI.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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


