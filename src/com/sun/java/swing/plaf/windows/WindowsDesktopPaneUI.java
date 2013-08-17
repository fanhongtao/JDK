/*
 * @(#)WindowsDesktopPaneUI.java	1.13 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.ComponentUI;
import java.awt.event.*;

/**
 * Windows desktop pane.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version %i% 11/29/01
 * @author David Kloba
 */
public class WindowsDesktopPaneUI extends BasicDesktopPaneUI
{
    public static ComponentUI createUI(JComponent c) {
        return new WindowsDesktopPaneUI();
    }

    protected void installDesktopManager() {
	if(desktop.getDesktopManager() == null) {
	    desktopManager = new WindowsDesktopManager();
	    desktop.setDesktopManager(desktopManager);
	}
    }

    void switchFrame(boolean next) {
        WindowsDesktopManager dm = 
            (WindowsDesktopManager)desktop.getDesktopManager();
        if (dm == null) {
            return;
        }
        if (next) {
            dm.activateNextFrame();
        } else {
            dm.activatePreviousFrame();
        }
    }

    protected void installKeyboardActions() {
      super.installKeyboardActions();
        desktop.registerKeyboardAction(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchFrame(true);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.CTRL_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        desktop.registerKeyboardAction(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchFrame(false);
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_F6, 
                                   InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), 
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        // Request focus if it isn't set.
        if(!desktop.requestDefaultFocus()) {
            desktop.requestFocus();
        }
    }

    protected void uninstallKeyboardActions() {
        super.uninstallKeyboardActions();
        desktop.resetKeyboardActions();
    }

    
}

