/*
 * @(#)WindowsMenuBarUI.java	1.15 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import java.awt.event.ActionEvent;


/**
 * Windows rendition of the component.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 */
public class WindowsMenuBarUI extends BasicMenuBarUI
{
    public static ComponentUI createUI(JComponent x) {
	return new WindowsMenuBarUI();
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();
	ActionMap map = SwingUtilities.getUIActionMap(menuBar);
        if (map == null) {
            map = new ActionMapUIResource();
            SwingUtilities.replaceUIActionMap(menuBar, map);
        }
        map.put("takeFocus", new TakeFocus());
    } 

    /**
     * Action that activates the menu (e.g. when F10 is pressed).
     * Unlike BasicMenuBarUI.TakeFocus, this Action will not show menu popup.
     */
    private static class TakeFocus extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    JMenuBar menuBar = (JMenuBar)e.getSource();
	    JMenu menu = menuBar.getMenu(0);
	    if (menu != null) {
                MenuSelectionManager msm =
                    MenuSelectionManager.defaultManager();
                MenuElement path[] = new MenuElement[2];
                path[0] = (MenuElement)menuBar;
                path[1] = (MenuElement)menu;
                msm.setSelectedPath(path);

                // show mnemonics
                WindowsLookAndFeel.setMnemonicHidden(false);
                WindowsLookAndFeel.repaintRootPane(menuBar);
	    }
	}
    }
}

