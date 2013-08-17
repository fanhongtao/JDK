/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * Basic implementation of RootPaneUI, there is one shared between all
 * JRootPane instances.
 *
 * @version 1.4 02/06/02
 * @author Scott Violet
 */
public class BasicRootPaneUI extends RootPaneUI implements
                  PropertyChangeListener {
    private static RootPaneUI rootPaneUI = new BasicRootPaneUI();


    public static ComponentUI createUI(JComponent c) {
        return rootPaneUI;
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
	ActionMap am = getActionMap(root);
	SwingUtilities.replaceUIActionMap(root, am);
	updateDefaultButtonBindings(root);
    }

    protected void uninstallDefaults(JRootPane root) {
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

    InputMap getInputMap(int condition, JComponent c) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    return createInputMap(condition, c);
	}
	return null;
    }

    ActionMap getActionMap(JComponent c) {
	return createActionMap(c);
    }

    ComponentInputMap createInputMap(int condition, JComponent c) {
	return new ComponentInputMapUIResource(c);
    }

    ActionMap createActionMap(JComponent c) {
	ActionMap map = new ActionMapUIResource();

	map.put("press", new DefaultAction((JRootPane)c, true));
	map.put("release", new DefaultAction((JRootPane)c, false));
	return map;
    }

    /**
     * Invoked when the default button property has changed. This reloads
     * the bindings from the defaults table with name
     * <code>RootPane.defaultButtonWindowKeyBindings</code>.
     */
    void updateDefaultButtonBindings(JRootPane root) {
	InputMap km = SwingUtilities.getUIInputMap(root, JComponent.
					       WHEN_IN_FOCUSED_WINDOW);
	if (km != null) {
	    km.clear();
	    if (root.getDefaultButton() != null) {
		Object[] bindings = (Object[])UIManager.get
		          ("RootPane.defaultButtonWindowKeyBindings");
		if (bindings != null) {
		    LookAndFeel.loadKeyBindings(km, bindings);
		}
	    }
	}
    }

    /**
     * Invoked when a property changes on the root pane. If the event
     * indicates the <code>defaultButton</code> has changed, this will
     * reinstall the keyboard actions.
     */
    public void propertyChange(PropertyChangeEvent e) {
	if(e.getPropertyName().equals("defaultButton")) {
	    updateDefaultButtonBindings((JRootPane)e.getSource());
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
                ButtonModel model = owner.getModel();
                if (press) {
                    model.setArmed(true);
                    model.setPressed(true);
                } else {
                    model.setPressed(false);
                }
            }
        }
        public boolean isEnabled() {
	    JButton owner = root.getDefaultButton();
            return (owner != null && owner.getModel().isEnabled());
        }
    }
}
