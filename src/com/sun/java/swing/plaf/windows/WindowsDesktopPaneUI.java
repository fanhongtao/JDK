/*
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
 * @version %i% 02/06/02
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
	ActionMap map = SwingUtilities.getUIActionMap(desktop);
	if (map != null) {
	    map.put("selectNextFrame", new SwitchFrameAction(true));
	    map.put("selectPreviousFrame", new SwitchFrameAction(false));
	}

        // Request focus if it isn't set.
        if(!desktop.requestDefaultFocus()) {
            desktop.requestFocus();
        }
    }


    class SwitchFrameAction extends AbstractAction {
	boolean direction;

	SwitchFrameAction(boolean direction) {
	    this.direction = direction;
	}

	public void actionPerformed(ActionEvent e) {
	    switchFrame(direction);
	}
    }
}

