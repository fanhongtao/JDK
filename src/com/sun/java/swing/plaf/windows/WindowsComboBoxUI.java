/*
 * @(#)WindowsComboBoxUI.java	1.18 98/10/30
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
 * @version 1.18 10/30/98
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

    JComboBox windowsGetComboBox() {
        return comboBox;
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();

        ActionListener downAction = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( windowsGetComboBox().isEnabled() ) {
                    if ( !windowsGetComboBox().isEditable() ||
                         (windowsGetComboBox().isEditable() && isPopupVisible(windowsGetComboBox())) ) {
                        selectNextPossibleValue();
                    }
                }
            }
        };

        comboBox.registerKeyboardAction( downAction,
                                         KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ),
                                         JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ActionListener upAction = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( windowsGetComboBox().isEnabled() ) {
                    if ( !windowsGetComboBox().isEditable() ||
                         (windowsGetComboBox().isEditable() && isPopupVisible(windowsGetComboBox())) ) {
                        selectPreviousPossibleValue();
                    }
                }
            }
        };

        comboBox.registerKeyboardAction( upAction,
                                         KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ),
                                         JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    void windowsSetPopupVisible( boolean visible ) {
        setPopupVisible( comboBox, visible );
    }

    protected void uninstallKeyboardActions() {
        super.uninstallKeyboardActions();
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0));
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0));
    }

    protected ComboPopup createPopup() {
        return new WindowsComboPopup( comboBox );
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */          
    public class WindowsComboPopup extends BasicComboPopup {
        // This is here because the compiler isn't currently letting this
        // inner class have access to its parent's inherited data members.
        JComboBox comboBox;

        public WindowsComboPopup( JComboBox cBox ) {
            super( cBox );
            comboBox = cBox;
        }

        protected KeyListener createKeyListener() {
            return new InvocationKeyHandler();
        }

        /**
         * This inner class is marked &quot;public&quot; due to a compiler bug.
         * This class should be treated as a &quot;protected&quot; inner class.
         * Instantiate it only within subclasses of <FooUI>.
         */          
        public class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
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
}

