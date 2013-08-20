/*
 * @(#)WindowsDesktopPaneUI.java	1.23 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
 * @version %i% 12/19/03
 * @author David Kloba
 */
public class WindowsDesktopPaneUI extends BasicDesktopPaneUI
{
    public static ComponentUI createUI(JComponent c) {
        return new WindowsDesktopPaneUI();
    }

    protected void installDesktopManager() {
	desktopManager = desktop.getDesktopManager();
	if(desktopManager == null) {
	    desktopManager = new WindowsDesktopManager();
	    desktop.setDesktopManager(desktopManager);
	}
    }

    protected void installDefaults() {
        super.installDefaults();
    }

    protected void installKeyboardActions() {
	super.installKeyboardActions();

        // Request focus if it isn't set.
        if(!desktop.requestDefaultFocus()) {
            desktop.requestFocus();
        }
    }
}
