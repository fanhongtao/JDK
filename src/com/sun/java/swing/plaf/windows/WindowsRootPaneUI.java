/*
 * @(#)WindowsRootPaneUI.java	1.5 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Event;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;

import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * Windows implementation of RootPaneUI, there is one shared between all
 * JRootPane instances.
 *
 * @version 1.5 12/03/01
 * @author Mark Davidson
 * @since 1.4
 */
public class WindowsRootPaneUI extends BasicRootPaneUI {

    private final static WindowsRootPaneUI windowsRootPaneUI = new WindowsRootPaneUI();
    private final static RepaintAction repaintAction = new RepaintAction();

    public static ComponentUI createUI(JComponent c) {
	return windowsRootPaneUI;
    }

    protected void installKeyboardActions(JRootPane root) {
	super.installKeyboardActions(root);

	InputMap km = SwingUtilities.getUIInputMap(root, 
				     JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	if (km == null) {
	    km = new InputMapUIResource();
	    SwingUtilities.replaceUIInputMap(root, 
				     JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, km);
	}
	km.put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, Event.ALT_MASK, false), "repaint");

	ActionMap am = SwingUtilities.getUIActionMap(root);
	if (am == null) {
	    am = new ActionMapUIResource();
	    SwingUtilities.replaceUIActionMap(root, am);
	}
	am.put("repaint", repaintAction);
    }    

    protected void uninstallKeyboardActions(JRootPane root) {
	super.uninstallKeyboardActions(root);
	
	SwingUtilities.replaceUIInputMap(root, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	SwingUtilities.replaceUIActionMap(root, null);
    }

    /**
     * Repaints the hiearchy if the Alt key is pressed.
     */
    static class RepaintAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
	    WindowsLookAndFeel.setMnemonicHidden(false);
	    Object object = e.getSource();
	    if (object instanceof Component) {
                // Find the topmost root pane and invoke repaint() on it
                JRootPane root = null;
                for (Component c=(Component)object; c!=null; c=c.getParent()) {
                    if (c instanceof JRootPane) {
                        root = (JRootPane)c;
                    }
                }
                if (root != null) {
                    root.repaint();
                } else {
                    ((Component)object).repaint();
                }
	    }
	}
    }
}
