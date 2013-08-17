/*
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
 * @version 1.26, 02/06/02
 * @author Tom Santos
 */

public class WindowsComboBoxUI extends BasicComboBoxUI {
    private static final JTextField sizer = new JTextField();

    public static ComponentUI createUI(JComponent c) {
        return new WindowsComboBoxUI();
    }  

    public void installUI( JComponent c ) {
        super.installUI( c );
        comboBox.setRequestFocusEnabled( true );
    }
    
    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension( cachedMinimumSize );
        }
        
        Dimension size = getDisplaySize();
        Insets insets = getInsets();
        
        if ( comboBox.getRenderer() instanceof UIResource ) {
            sizer.setFont( comboBox.getFont() );
            size.height = sizer.getPreferredSize().height;
        }
        else {
            size.height += insets.top + insets.bottom;
        }
        
        int buttonSize = size.height - (insets.top + insets.bottom);
        size.width +=  insets.left + insets.right + buttonSize;

        cachedMinimumSize.setSize( size.width, size.height ); 
        isMinimumSizeDirty = false;

        return size;
    }

    protected void selectNextPossibleValue() {
        super.selectNextPossibleValue();
    }

    protected void selectPreviousPossibleValue() {
        super.selectPreviousPossibleValue();
    } 

    protected void installKeyboardActions() {
	ActionMap oldMap = (ActionMap)UIManager.get("ComboBox.actionMap");
        super.installKeyboardActions();
	if (oldMap == null) {
	    ActionMap map = (ActionMap)UIManager.get("ComboBox.actionMap");
	    if (map != null) {
		// The actions we install are a little different, override
		// them here.
		map.put("selectPrevious", new UpAction());
		map.put("selectNext", new DownAction());
	    }
	}
    }

    void windowsSetPopupVisible( boolean visible ) {
        setPopupVisible( comboBox, visible );
    }

    protected ComboPopup createPopup() {
        return new WindowsComboPopup( comboBox );
    }

    /** 
     * Subclassed to add Windows specific Key Bindings.
     */
    protected class WindowsComboPopup extends BasicComboPopup {

        public WindowsComboPopup( JComboBox cBox ) {
            super( cBox );
        }

        protected KeyListener createKeyListener() {
            return new InvocationKeyHandler();
        }

        protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
            public void keyReleased( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F4 ) {
                    if ( isVisible() ) {
                        hide();
                    }
                    else {
                        show();
                    }
                }
                else if ( e.isAltDown() && e.getKeyCode() != KeyEvent.VK_ALT ) {
                    if ( e.getKeyCode() == KeyEvent.VK_UP ||
                         e.getKeyCode() == KeyEvent.VK_DOWN ) {
                        if ( isVisible() ) {
                            hide();
                        }
                        else {
                            show();
                        }
                    }
                }
                else if ( comboBox.isEditable() &&
                          !isVisible() &&
                          (e.getKeyCode() == KeyEvent.VK_UP ||
                           e.getKeyCode() == KeyEvent.VK_DOWN) ) {
                    show();
                }
                else if ( !comboBox.isEditable() && isVisible() ) {
                    super.keyReleased( e );
                }
            }
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
