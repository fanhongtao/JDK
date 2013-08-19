/*
 * @(#)SynthMenuBarUI.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.border.*;
import javax.swing.plaf.*;

/**
 * A default L&F implementation of MenuBarUI.  This implementation
 * is a "combined" view/controller.
 *
 * @version 1.7, 01/23/03 (based on BasicMenuBarUI v 1.77)
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
class SynthMenuBarUI extends MenuBarUI implements SynthUI {
    protected JMenuBar              menuBar = null;
    protected ContainerListener     containerListener;
    protected ChangeListener        changeListener;
    private PropertyChangeListener  propertyChangeListener;
    private SynthStyle style;

    public static ComponentUI createUI(JComponent x) {
	return new SynthMenuBarUI();
    }

    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("takeFocus", new TakeFocus()); 
    }

    public void installUI(JComponent c) {
	menuBar = (JMenuBar) c;

	installDefaults();
        installListeners();
        installKeyboardActions();

    }

    protected void installDefaults() {
	if (menuBar.getLayout() == null ||
	    menuBar.getLayout() instanceof UIResource) {
            menuBar.setLayout(new DefaultMenuLayout(menuBar,BoxLayout.LINE_AXIS));
        }
        fetchStyle(menuBar);
    }

    private void fetchStyle(JMenuBar c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void installListeners() {
        containerListener = createContainerListener();
        changeListener = createChangeListener();
        propertyChangeListener = createPropertyChangeListener();
	
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
	    if (menu!=null)
		menu.getModel().addChangeListener(changeListener);        
	}
	menuBar.addContainerListener(containerListener);
        menuBar.addPropertyChangeListener(propertyChangeListener);
    }

    protected void installKeyboardActions() {
	InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

	SwingUtilities.replaceUIInputMap(menuBar,
			   JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);

        LazyActionMap.installLazyActionMap(menuBar, SynthMenuBarUI.class,
                                           "MenuBar.actionMap");
    } 

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            SynthContext context = getContext(menuBar, ENABLED);
	    Object[] bindings = (Object[])context.getStyle().get(context,
                                                  "MenuBar.windowBindings");
            InputMap map;
	    if (bindings != null) {
		map = LookAndFeel.makeComponentInputMap(menuBar, bindings);
	    }
            else {
                map = null;
            }
            context.dispose();
            return map;
	}
	return null;
    }

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

	menuBar = null;
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(menuBar, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners() {
	menuBar.removeContainerListener(containerListener);
        menuBar.removePropertyChangeListener(propertyChangeListener);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
	    JMenu menu = menuBar.getMenu(i);
	    if (menu !=null)
		menu.getModel().removeChangeListener(changeListener);
        }

	containerListener = null;
	changeListener = null;
        propertyChangeListener = null;
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIInputMap(menuBar, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, null);
	SwingUtilities.replaceUIActionMap(menuBar, null);
    }

    protected ContainerListener createContainerListener() {
	return new ContainerHandler();
    }

    protected ChangeListener createChangeListener() {
        return new ChangeHandler();
    }

    private PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
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

    /*
     * This PropertyChangeListener is used to adjust the default layout
     * manger when the menuBar is given a right-to-left ComponentOrientation.
     * This is a hack to work around the fact that the DefaultMenuLayout
     * (BoxLayout) isn't aware of ComponentOrientation.  When BoxLayout is
     * made aware of ComponentOrientation, this listener will no longer be
     * necessary.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JMenuBar)e.getSource());
            }
            if( name.equals("componentOrientation")
                && (menuBar.getLayout() instanceof UIResource) )
            {
                menuBar.setLayout(new DefaultMenuLayout(menuBar,BoxLayout.LINE_AXIS));
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
        TakeFocus() {
	}
	
	public void actionPerformed(ActionEvent e) {
	    JMenuBar menuBar = (JMenuBar)e.getSource();
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
    }
}


