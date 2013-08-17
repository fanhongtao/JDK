/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;



/**
 * Provides the Windows look and feel for a text field.  This 
 * is basically the following customizations to the default
 * look-and-feel.
 * <ul>
 * <li>The border is beveled (using the standard control color).
 * <li>The background is white by default.
 * <li>The highlight color is a dark color, blue by default.
 * <li>The foreground color is high contrast in the selected
 *  area, white by default.  The unselected foreground is black.
 * <li>The cursor blinks at about 1/2 second intervals.
 * <li>The entire value is selected when focus is gained.
 * <li>Shift-left-arrow and shift-right-arrow extend selection
 * <li>Cntrl-left-arrow and cntrl-right-arrow act like home and 
 *   end respectively.
 * </ul>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Timothy Prinzing
 * @version 1.16 02/06/02
 */
public class WindowsTextFieldUI extends BasicTextFieldUI
{
    /**
     * Creates a UI for a JTextField.
     *
     * @param c the text field
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new WindowsTextFieldUI();
    }

    /**
     * Creates the caret for a field.
     *
     * @return the caret
     */
    protected Caret createCaret() {
	return new WindowsFieldCaret();
    }

    /**
     * WindowsFieldCaret has different scrolling behavior than
     * DefaultCaret.
     */
    static class WindowsFieldCaret extends DefaultCaret implements UIResource {

	public WindowsFieldCaret() {
	    super();
	}

	/**
	 * Adjusts the visibility of the caret according to
	 * the windows feel which seems to be to move the
	 * caret out into the field by about a quarter of
	 * a field length if not visible.
	 */
	protected void adjustVisibility(Rectangle r) {
	    JTextField field = (JTextField) getComponent();
	    BoundedRangeModel vis = field.getHorizontalVisibility();
	    int x = r.x + vis.getValue();
	    int quarterSpan = vis.getExtent() / 4;
	    if (x < vis.getValue()) {
		vis.setValue(x - quarterSpan);
	    } else if (x > vis.getValue() + vis.getExtent()) {
		vis.setValue(x - (3 * quarterSpan));
	    }
	}

	/**
	 * Gets the painter for the Highlighter.
	 *
	 * @return the painter
	 */
	protected Highlighter.HighlightPainter getSelectionPainter() {
	    return WindowsTextUI.WindowsPainter;
	}
    }

}

