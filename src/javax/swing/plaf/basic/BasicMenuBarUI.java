/*
 * @(#)BasicMenuBarUI.java	1.67 98/08/26
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

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;

import javax.swing.border.*;
import javax.swing.plaf.*;


/**
 * A default L&F implementation of MenuBarUI.  This implementation
 * is a "combined" view/controller.
 *
 * @version 1.67 08/26/98
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicMenuBarUI extends MenuBarUI  {
    protected JMenuBar              menuBar = null;
    protected ContainerListener     containerListener;
    protected ChangeListener        changeListener;

    public static ComponentUI createUI(JComponent x) {
	return new BasicMenuBarUI();
    }

    public void installUI(JComponent c) {
	menuBar = (JMenuBar) c;

	installDefaults();
        installListeners();
        installKeyboardActions();

    }

    protected void installDefaults() {
	if (menuBar.getLayout() == null ||
	    menuBar.getLayout() instanceof UIResource)
	    menuBar.setLayout(new DefaultMenuLayout(menuBar, BoxLayout.X_AXIS));

	menuBar.setOpaque(true);
	LookAndFeel.installBorder(menuBar,"MenuBar.border");
	LookAndFeel.installColorsAndFont(menuBar,
					      "MenuBar.background",
					      "MenuBar.foreground",
					      "MenuBar.font");
    }

    protected void installListeners() {
        containerListener = createContainerListener();
        changeListener = createChangeListener();
	
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
	    if (menu!=null)
		menu.getModel().addChangeListener(changeListener);        
	}
	menuBar.addContainerListener(containerListener);
    }

    protected void installKeyboardActions() {
	menuBar.registerKeyboardAction(
		new TakeFocus(menuBar),
		KeyStroke.getKeyStroke(KeyEvent.VK_F10,
				       0,
				       false),
		JComponent.WHEN_IN_FOCUSED_WINDOW);
    } 

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

	menuBar = null;
    }

    protected void uninstallDefaults() {
	LookAndFeel.uninstallBorder(menuBar);
    }

    protected void uninstallListeners() {
	menuBar.removeContainerListener(containerListener);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
	    JMenu menu = menuBar.getMenu(i);
	    if (menu !=null)
		menu.getModel().removeChangeListener(changeListener);
        }

	containerListener = null;
	changeListener = null;
    }

    protected void uninstallKeyboardActions() {
	menuBar.unregisterKeyboardAction(
		       KeyStroke.getKeyStroke(KeyEvent.VK_F10,
					      0,
					      false));
    }

    protected ContainerListener createContainerListener() {
	return new ContainerHandler();
    }

    protected ChangeListener createChangeListener() {
        return new ChangeHandler();
    }

    private class ChangeHandler implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    int i,c;
	    for(i=0,c = menuBar.getMenuCount() ; i < c ; i++) {
		JMenu menu = menuBar.getMenu(i);
		if(menu !=null && menu.isSelected()) {
		    menuBar.getSelectionModel().setSelectedIndex(i);
		    break;
		}
	    }
	}
    }

    public Dimension getPreferredSize(JComponent c) {
        return null;
    }

    public Dimension getMinimumSize(JComponent c) {
        return null;
    }

    public Dimension getMaximumSize(JComponent c) {
        return null;
    }

    private class ContainerHandler implements ContainerListener {
	public void componentAdded(ContainerEvent e) {
	    Component c = e.getChild();
	    if (c instanceof JMenu)
		((JMenu)c).getModel().addChangeListener(changeListener);
	}
	public void componentRemoved(ContainerEvent e) {
	    Component c = e.getChild();
	    if (c instanceof JMenu)
		((JMenu)c).getModel().removeChangeListener(changeListener);
	}
    }


    private static class TakeFocus extends AbstractAction {
	JMenuBar menuBar;

        TakeFocus(JMenuBar menuBar) {
	    super("takeFocus");
	    this.menuBar = menuBar;
	}
	
	public void actionPerformed(ActionEvent e) {
            MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
	    MenuElement me[];
	    MenuElement subElements[];
	    JMenu menu = menuBar.getMenu(0);
	    if (menu!=null) {
		    me = new MenuElement[3];
		    me[0] = (MenuElement) menuBar;
		    me[1] = (MenuElement) menu;
		    me[2] = (MenuElement) menu.getPopupMenu();
		    defaultManager.setSelectedPath(me);
	    }
	}
	public boolean isEnabled() {
	    return menuBar.isEnabled();
	}
    }
    
}


