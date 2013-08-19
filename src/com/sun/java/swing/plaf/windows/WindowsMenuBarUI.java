/*
 * @(#)WindowsMenuBarUI.java	1.14 03/05/06
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.basic.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
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

    protected void installDefaults() {
	// The following is added for 1.4.2 only. In 1.5 we will be using a new
	// DesktopProperty for the menubar background color.
	UIDefaults lafDefaults = UIManager.getLookAndFeelDefaults();
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    Color color = xp.getColor("sysmetrics.menubar", null);
	    if (color != null) {
		// Override default from WindowsLookAndFeel
		lafDefaults.put("MenuBar.background", new ColorUIResource(color));
	    }
	} else {
	    // Restore default from WindowsLookAndFeel
	    Object classicBackgroundProperty =
		lafDefaults.get("MenuBar.classicBackground");
	    if ((classicBackgroundProperty instanceof Object[]) &&
		((Object[])classicBackgroundProperty).length > 0) {

		lafDefaults.put("MenuBar.background",
				((Object[])classicBackgroundProperty)[0]);
	    }
	}

	super.installDefaults();
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

