/*
 * @(#)SynthRootPaneUI.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * Basic implementation of RootPaneUI, there is one shared between all
 * JRootPane instances.
 *
 * @version 1.9, 01/23/03 (based on BasicRootPaneUI v 1.8)
 * @author Scott Violet
 */
class SynthRootPaneUI extends RootPaneUI implements SynthUI,
                  PropertyChangeListener, LazyActionMap.Loader {
    private SynthStyle style;

    public static ComponentUI createUI(JComponent c) {
        return new SynthRootPaneUI();
    }

    public void installUI(JComponent c) { 
        installDefaults((JRootPane)c);
        installComponents((JRootPane)c);
        installListeners((JRootPane)c);
        installKeyboardActions((JRootPane)c);
    }

    
    public void uninstallUI(JComponent c) { 
        uninstallDefaults((JRootPane)c);
        uninstallComponents((JRootPane)c);
        uninstallListeners((JRootPane)c);
        uninstallKeyboardActions((JRootPane)c);
    }

    protected void installDefaults(JRootPane c){
        fetchStyle(c);
    }

    protected void installComponents(JRootPane root) {
    }

    protected void installListeners(JRootPane root) {
	root.addPropertyChangeListener(this);
    }

    protected void installKeyboardActions(JRootPane root) {
	InputMap km = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, root);
	SwingUtilities.replaceUIInputMap(root, JComponent.WHEN_IN_FOCUSED_WINDOW,
				       km);
        LazyActionMap.installLazyActionMap(root, this);
	updateDefaultButtonBindings(root);
    }

    protected void uninstallDefaults(JRootPane root) {
        SynthContext context = getContext(root, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallComponents(JRootPane root) {
    }

    protected void uninstallListeners(JRootPane root) {
	root.removePropertyChangeListener(this);
    }

    protected void uninstallKeyboardActions(JRootPane root) {
	SwingUtilities.replaceUIInputMap(root, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, null);
	SwingUtilities.replaceUIActionMap(root, null);
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

    private void fetchStyle(JComponent  c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    InputMap getInputMap(int condition, JComponent c) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    return createInputMap(condition, c);
	}
	return null;
    }

    ComponentInputMap createInputMap(int condition, JComponent c) {
	return new RootPaneInputMap(c);
    }

    public void loadActionMap(JComponent c, ActionMap map) {
	map.put("press", new DefaultAction((JRootPane)c, true));
	map.put("release", new DefaultAction((JRootPane)c, false));
    }

    /**
     * Invoked when the default button property has changed. This reloads
     * the bindings from the defaults table with name
     * <code>RootPane.defaultButtonWindowKeyBindings</code>.
     */
    void updateDefaultButtonBindings(JRootPane root) {
	InputMap km = SwingUtilities.getUIInputMap(root, JComponent.
					       WHEN_IN_FOCUSED_WINDOW);
	while (km != null && !(km instanceof RootPaneInputMap)) {
	    km = km.getParent();
	}
	if (km != null) {
	    km.clear();
	    if (root.getDefaultButton() != null) {
                SynthContext context = getContext(root, ENABLED);
		Object[] bindings = (Object[])style.get(context,
                         "RootPane.defaultButtonWindowKeyBindings");
		if (bindings != null) {
		    LookAndFeel.loadKeyBindings(km, bindings);
		}
                context.dispose();
	    }
	}
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

    /**
     * Invoked when a property changes on the root pane. If the event
     * indicates the <code>defaultButton</code> has changed, this will
     * reinstall the keyboard actions.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            fetchStyle((JRootPane)e.getSource());
        }
	if ("defaultButton".equals(e.getPropertyName())) {
	    JRootPane rootpane = (JRootPane)e.getSource();
	    updateDefaultButtonBindings(rootpane);
	    if (rootpane.getClientProperty("temporaryDefaultButton") == null) {
		rootpane.putClientProperty("initialDefaultButton", e.getNewValue());
	    }
	}
    }


    // This was transplanted from JRootPane.
    static class DefaultAction extends AbstractAction {
        JRootPane root;
        boolean press;
        DefaultAction(JRootPane root, boolean press) {
            this.root = root;
            this.press = press;
        }
        public void actionPerformed(ActionEvent e) {
	    JButton owner = root.getDefaultButton();
            if (owner != null && SwingUtilities.getRootPane(owner) == root) {
                if (press) {
                    owner.doClick(20);
                }
            }
        }
        public boolean isEnabled() {
	    JButton owner = root.getDefaultButton();
            return (owner != null && owner.getModel().isEnabled());
        }
    }

    private static class RootPaneInputMap extends ComponentInputMapUIResource {
	public RootPaneInputMap(JComponent c) {
	    super(c);
	}
    }
}
