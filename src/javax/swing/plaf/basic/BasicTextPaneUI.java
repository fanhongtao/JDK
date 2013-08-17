/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.border.*;


/**
 * Provides the look and feel for a styled text editor.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 * @version 1.62 02/06/02
 */
public class BasicTextPaneUI extends BasicEditorPaneUI {

    /**
     * Creates a UI for the JTextPane.
     *
     * @param c the JTextPane object
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicTextPaneUI();
    }

    /**
     * Creates a new BasicTextPaneUI.
     */
    public BasicTextPaneUI() {
	super();
    }

    /**
     * Fetches the name used as a key to lookup properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("TextPane")
     */
    protected String getPropertyPrefix() {
	return "TextPane";
    }

    /**
     * Fetches the EditorKit for the UI.  This is whatever is
     * currently set in the associated JEditorPane.
     *
     * @param tc the text component for which this UI is installed
     * @return the editor capabilities
     * @see TextUI#getEditorKit
     */
    public EditorKit getEditorKit(JTextComponent tc) {
	JEditorPane pane = (JEditorPane) getComponent();
	return pane.getEditorKit();
    }

    /**
     * This method gets called when a bound property is changed
     * on the associated JTextComponent.  This is a hook
     * which UI implementations may change to reflect how the
     * UI displays bound properties of JTextComponent subclasses.
     * If the font or foreground has changed, and the
     * Document is a StyledDocument, the appropriate property
     * is set in the default style.
     *
     * @param evt the property change event
     */
    protected void propertyChange(PropertyChangeEvent evt) {
	super.propertyChange(evt);
	StyledDocument doc = (StyledDocument)getComponent().getDocument();
	Style style = doc.getStyle(StyleContext.DEFAULT_STYLE);
	if (style == null)
	    return;

	String name = evt.getPropertyName();
	// foreground
	if (name.equals("foreground")) {
	    Color color = (Color)evt.getNewValue();
	    if (color != null) {
		StyleConstants.setForeground(style, color);
	    }
	    else {
		style.removeAttribute(StyleConstants.Foreground);
	    }
	}
	// font
	else if (name.equals("font")) {
	    Font font = (Font)evt.getNewValue();
	    if (font != null) {
		StyleConstants.setFontFamily(style, font.getName());
		StyleConstants.setFontSize(style, font.getSize());
		StyleConstants.setBold(style, font.isBold());
		StyleConstants.setItalic(style, font.isItalic());
	    }
	    else {
		style.removeAttribute(StyleConstants.FontFamily);
		style.removeAttribute(StyleConstants.FontSize);
		style.removeAttribute(StyleConstants.Bold);
		style.removeAttribute(StyleConstants.Italic);
	    }
	}
    }
}
