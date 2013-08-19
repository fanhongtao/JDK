/*
 * @(#)WindowsPopupMenuUI.java	1.17 02/04/17
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
    static KeyEventPostProcessor altProcessor = null;

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

        if (altProcessor == null) {
            altProcessor = new AltProcessor();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addKeyEventPostProcessor(altProcessor);
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

    static class AltProcessor implements KeyEventPostProcessor {
        static boolean altKeyPressed = false;
        static boolean menuCanceledOnPress = false;

        void altPressed() {
            MenuSelectionManager msm =
                MenuSelectionManager.defaultManager();
            MenuElement[] path = msm.getSelectedPath();
            if (path.length > 0 && ! (path[0] instanceof ComboPopup)) {
                msm.clearSelectedPath();
                menuCanceledOnPress = true;
            } else {
                menuCanceledOnPress = false;
            }
        }

        void altReleased(KeyEvent ev) {
            if (menuCanceledOnPress) {
                return;
            }

            MenuSelectionManager msm =
                MenuSelectionManager.defaultManager();
            if (msm.getSelectedPath().length == 0) {
                // if no menu is active, we try activating the menubar
                JRootPane root = SwingUtilities.getRootPane(ev.getComponent());
                java.awt.Window w = SwingUtilities.getWindowAncestor(root);

                JMenuBar mbar = root != null ? root.getJMenuBar() : null;
                JMenu menu = mbar != null ? mbar.getMenu(0) : null;

                if (menu != null) {
                    MenuElement[] path = new MenuElement[2];
                    path[0] = mbar;
                    path[1] = menu;
                    msm.setSelectedPath(path);
                }
            }
        }

        public boolean postProcessKeyEvent(KeyEvent ev) {
            if (ev.getKeyCode() == KeyEvent.VK_ALT) {
                if (ev.getID() == KeyEvent.KEY_PRESSED) {
                    if (!altKeyPressed) {
                        altPressed();
                    }
                    altKeyPressed = true;
                } else if (ev.getID() == KeyEvent.KEY_RELEASED) {
                    if (altKeyPressed) {
                        altReleased(ev);
                    }
                    altKeyPressed = false;
                }
            } else {
                altKeyPressed = false;
            }
            return false;
        }
    }
}
