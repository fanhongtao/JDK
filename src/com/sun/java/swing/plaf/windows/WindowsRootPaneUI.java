/*
 * @(#)WindowsRootPaneUI.java	1.15 04/04/16
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.KeyEventPostProcessor;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;

import javax.swing.plaf.basic.BasicRootPaneUI;
import javax.swing.plaf.basic.ComboPopup;

/**
 * Windows implementation of RootPaneUI, there is one shared between all
 * JRootPane instances.
 *
 * @version 1.15 04/16/04
 * @author Mark Davidson
 * @since 1.4
 */
public class WindowsRootPaneUI extends BasicRootPaneUI {

    private final static WindowsRootPaneUI windowsRootPaneUI = new WindowsRootPaneUI();
    static final AltProcessor altProcessor = new AltProcessor();

    public static ComponentUI createUI(JComponent c) {
	return windowsRootPaneUI;
    }

    static class AltProcessor implements KeyEventPostProcessor {
        static boolean altKeyPressed = false;
        static boolean menuCanceledOnPress = false;
        static JRootPane root = null;
        static Window winAncestor = null;

        void altPressed(KeyEvent ev) {
            MenuSelectionManager msm =
                MenuSelectionManager.defaultManager();
            MenuElement[] path = msm.getSelectedPath();
            if (path.length > 0 && ! (path[0] instanceof ComboPopup)) {
                msm.clearSelectedPath();
                menuCanceledOnPress = true;
                ev.consume();
            } else if(path.length > 0) { // We are in ComboBox
                menuCanceledOnPress = false;
                WindowsLookAndFeel.setMnemonicHidden(false);
                WindowsUtils.repaintMnemonicsInWindow(winAncestor);
                ev.consume();
            } else {
                menuCanceledOnPress = false;
	        WindowsLookAndFeel.setMnemonicHidden(false);
                WindowsUtils.repaintMnemonicsInWindow(winAncestor);
                JMenuBar mbar = root != null ? root.getJMenuBar() : null;
                if(mbar == null && winAncestor instanceof JFrame) {
                    mbar = ((JFrame)winAncestor).getJMenuBar();
                }
                JMenu menu = mbar != null ? mbar.getMenu(0) : null;
                if(menu != null) {
                    ev.consume();
                }
            }
        }

        void altReleased(KeyEvent ev) {
            if (menuCanceledOnPress) {
	        WindowsLookAndFeel.setMnemonicHidden(true);
                WindowsUtils.repaintMnemonicsInWindow(winAncestor);
                return;
            }

            MenuSelectionManager msm =
                MenuSelectionManager.defaultManager();
            if (msm.getSelectedPath().length == 0) {
                // if no menu is active, we try activating the menubar

                JMenuBar mbar = root != null ? root.getJMenuBar() : null;
                if(mbar == null && winAncestor instanceof JFrame) {
                    mbar = ((JFrame)winAncestor).getJMenuBar();
                }
                JMenu menu = mbar != null ? mbar.getMenu(0) : null;

                if (menu != null) {
                    MenuElement[] path = new MenuElement[2];
                    path[0] = mbar;
                    path[1] = menu;
                    msm.setSelectedPath(path);
                } else if(!WindowsLookAndFeel.isMnemonicHidden()) {
                    WindowsLookAndFeel.setMnemonicHidden(true);
                    WindowsUtils.repaintMnemonicsInWindow(winAncestor);
                }
            } else {
                if((msm.getSelectedPath())[0] instanceof ComboPopup) {
                    WindowsLookAndFeel.setMnemonicHidden(true);
                    WindowsUtils.repaintMnemonicsInWindow(winAncestor);
                }
            }

        }

        public boolean postProcessKeyEvent(KeyEvent ev) {
            if (ev.getKeyCode() == KeyEvent.VK_ALT) {
                root = SwingUtilities.getRootPane(ev.getComponent());
                winAncestor = (root == null ? null : 
                        SwingUtilities.getWindowAncestor(root));

                if (ev.getID() == KeyEvent.KEY_PRESSED) {
                    if (!altKeyPressed) {
                        altPressed(ev);
                    }
                    altKeyPressed = true;
                    return true;
                } else if (ev.getID() == KeyEvent.KEY_RELEASED) {
                    if (altKeyPressed) {
                        altReleased(ev);
                    } else {
                        MenuSelectionManager msm =
                            MenuSelectionManager.defaultManager();
                        MenuElement[] path = msm.getSelectedPath();
                        if (path.length <= 0) {
                            WindowsLookAndFeel.setMnemonicHidden(true);
                            WindowsUtils.repaintMnemonicsInWindow(winAncestor);
                        }
                    }
                    altKeyPressed = false;
                }
                root = null;
                winAncestor = null;
            } else {
                altKeyPressed = false;
            }
            return false;
        }
    }
}
