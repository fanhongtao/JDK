/*
 * @(#)WindowsComboBoxUI.java	1.44 04/05/18
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


/**
 * Windows combo box.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.44, 05/18/04
 * @author Tom Santos
 */

public class WindowsComboBoxUI extends BasicComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new WindowsComboBoxUI();
    }  

    public void installUI( JComponent c ) {
        super.installUI( c );
        comboBox.setRequestFocusEnabled( true );
    }

    /**
     * If necessary paints the currently selected item.
     *
     * @param g Graphics to paint to
     * @param bounds Region to paint current value to
     * @param hasFocus whether or not the JComboBox has focus
     * @throws NullPointerException if any of the arguments are null.
     * @since 1.5
     */
    public void paintCurrentValue(Graphics g, Rectangle bounds,
                                  boolean hasFocus) {
	if (XPStyle.getXP() != null) {
	    bounds.x += 2;
	    bounds.y += 2;
	    bounds.width -= 3;
	    bounds.height -= 4;
	} else {
	    bounds.x += 1;
	    bounds.y += 1;
	    bounds.width -= 2;
	    bounds.height -= 2;
	}
	super.paintCurrentValue(g, bounds, hasFocus);
    }
    
    public Dimension getPreferredSize( JComponent c ) {
        Dimension d = super.getPreferredSize(c);
        d.width += 4;
        d.height += 2;
        if (XPStyle.getXP() != null) {
            d.height += 2;
        }
        return d;
    }

    /**
     * Creates a layout manager for managing the components which make up the 
     * combo box.
     * 
     * @return an instance of a layout manager
     */
    protected LayoutManager createLayoutManager() {
        return new BasicComboBoxUI.ComboBoxLayoutManager() {
	    public void layoutContainer(Container parent) {
		super.layoutContainer(parent);

		if (XPStyle.getXP() != null && arrowButton != null) {
		    Dimension d = parent.getSize();
		    Insets insets = getInsets();
		    int buttonWidth = arrowButton.getPreferredSize().width;
		    arrowButton.setBounds(WindowsUtils.isLeftToRight((JComboBox)parent)
					  ? (d.width - insets.right - buttonWidth)
					  : insets.left,
					  insets.top,
					  buttonWidth, d.height - insets.top - insets.bottom);
		}
	    }
	};
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();
    }

    protected ComboPopup createPopup() {
        return super.createPopup();
    }

    /**
     * Creates the default editor that will be used in editable combo boxes.  
     * A default editor will be used only if an editor has not been 
     * explicitly set with <code>setEditor</code>.
     *
     * @return a <code>ComboBoxEditor</code> used for the combo box
     * @see javax.swing.JComboBox#setEditor
     */
    protected ComboBoxEditor createEditor() {
	return new WindowsComboBoxEditor();
    }
 
    /**
     * Creates an button which will be used as the control to show or hide
     * the popup portion of the combo box.
     *
     * @return a button which represents the popup control
     */
    protected JButton createArrowButton() {
	if (XPStyle.getXP() != null) {
	    return new XPComboBoxButton();
	} else {
	    return super.createArrowButton();
	}
    }

    private static class XPComboBoxButton extends XPStyle.GlyphButton {
        public XPComboBoxButton() {
	    super("combobox.dropdownbutton");
	    setRequestFocusEnabled(false);
	}   

        public Dimension getPreferredSize() {
            return new Dimension(17, 20);
        }
    }


    /** 
     * Subclassed to add Windows specific Key Bindings.
     * This class is now obsolete and doesn't do anything. 
     * Only included for backwards API compatibility.
     * Do not call or override.
     * 
     * @deprecated As of Java 2 platform v1.4.
     */
    @Deprecated
    protected class WindowsComboPopup extends BasicComboPopup {

        public WindowsComboPopup( JComboBox cBox ) {
            super( cBox );
        }

        protected KeyListener createKeyListener() {
            return new InvocationKeyHandler();
        }

        protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
	    protected InvocationKeyHandler() {
		WindowsComboPopup.this.super();
	    }
        }
    }


    /** 
     * Subclassed to highlight selected item in an editable combo box.
     */
    public static class WindowsComboBoxEditor
        extends BasicComboBoxEditor.UIResource {

        public void setItem(Object item) {
            super.setItem(item);
            if (editor.hasFocus()) {
                editor.selectAll();
            }
        }
    }
}
