/*
 * @(#)WindowsPopupMenuUI.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


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
public class WindowsPopupMenuUI extends BasicPopupMenuUI {

    static MnemonicListener mnemonicListener = null;

    public static ComponentUI createUI(JComponent c) {
	return new WindowsPopupMenuUI();
    }

    public void installListeners() {
        super.installListeners();
	if (! UIManager.getBoolean("Button.showMnemonics") &&
            mnemonicListener == null) {

            mnemonicListener = new MnemonicListener();
            MenuSelectionManager.defaultManager().
                addChangeListener(mnemonicListener);
        }
    }

    /**
     * Returns the <code>Popup</code> that will be responsible for
     * displaying the <code>JPopupMenu</code>.
     *
     * @param popupMenu JPopupMenu requesting Popup
     * @param x     Screen x location Popup is to be shown at
     * @param y     Screen y location Popup is to be shown at.
     * @return Popup that will show the JPopupMenu
     * @since 1.4
     */
    public Popup getPopup(JPopupMenu popupMenu, int x, int y) {
        PopupFactory popupFactory = PopupFactory.getSharedInstance();
        return popupFactory.getPopup(popupMenu.getInvoker(), popupMenu, x, y);
    }

    static class MnemonicListener implements ChangeListener {
        JRootPane repaintRoot = null;

        public void stateChanged(ChangeEvent ev) {
	    MenuSelectionManager msm = (MenuSelectionManager)ev.getSource();
            MenuElement[] path = msm.getSelectedPath();
            if (path.length == 0) {
                // menu was canceled -- hide mnemonics
                WindowsLookAndFeel.setMnemonicHidden(true);
                if (repaintRoot != null) repaintRoot.repaint();
            } else {
                Component c = (Component)path[0];
                if (c instanceof JPopupMenu) c = ((JPopupMenu)c).getInvoker();
                repaintRoot = SwingUtilities.getRootPane(c);
            }
        }
    }
}
