/*
 * @(#)WindowsComboBoxUI.java	1.31 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.31, 12/03/01
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
    
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        ActionMap map = (ActionMap)UIManager.get("ComboBox.actionMap");
        if (map != null) {
            map.put("selectPrevious", new UpAction());
            map.put("selectNext", new DownAction());
        }
    }

    protected ComboPopup createPopup() {
        return new WindowsComboPopup( comboBox );
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
     * Subclassed to add Windows specific Key Bindings.
     * This class is now obsolete and doesn't do anything. 
     * Only included for backwards API compatibility.
     * Do not call or override.
     * 
     * @deprecated As of Java 2 platform v1.4.
     */
    protected class WindowsComboPopup extends BasicComboPopup {

        public WindowsComboPopup( JComboBox cBox ) {
            super( cBox );
        }

        protected KeyListener createKeyListener() {
            return new InvocationKeyHandler();
        }

        protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
        }
    }


    /** 
     * Subclassed to highlight selected item in an editable combo box.
     */
    public static class WindowsComboBoxEditor
        extends BasicComboBoxEditor.UIResource {

        public void setItem(Object item) {
            super.setItem(item);
            selectAll();
        }
    }

    static class DownAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( comboBox.isEnabled() ) {
		WindowsComboBoxUI ui = (WindowsComboBoxUI)comboBox.getUI();
		if ( !comboBox.isEditable() || (comboBox.isEditable() &&
						ui.isPopupVisible(comboBox))) {
		    ui.selectNextPossibleValue();
		}
	    }
	}
    }


    static class UpAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JComboBox comboBox = (JComboBox)e.getSource();
	    if ( comboBox.isEnabled() ) {
		WindowsComboBoxUI ui = (WindowsComboBoxUI)comboBox.getUI();
		if ( !comboBox.isEditable() || (comboBox.isEditable() &&
        					ui.isPopupVisible(comboBox))) {
		    ui.selectPreviousPossibleValue();
		}
	    }
	}
    }
}
