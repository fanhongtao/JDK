/*
 * @(#)SynthEditorPaneUI.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import java.beans.PropertyChangeEvent;

/**
 * Provides the look and feel for a JEditorPane in the
 * Synth look and feel.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author  Shannon Hickey
 * @version 1.3 01/23/03 (based on revision 1.29 of BasicEditorPaneUI)
 */
class SynthEditorPaneUI extends SynthTextUI {

    /**
     * Creates a UI for the JTextPane.
     *
     * @param c the JTextPane component
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new SynthEditorPaneUI();
    }

    /**
     * Fetches the name used as a key to lookup properties through the
     * UIManager.  This is used as a prefix to all the standard
     * text properties.
     *
     * @return the name ("EditorPane")
     */
    protected String getPropertyPrefix() {
        return "EditorPane";
    }

    /**
     * Fetches the EditorKit for the UI.  This is whatever is
     * currently set in the associated JEditorPane.
     *
     * @return the editor capabilities
     * @see TextUI#getEditorKit
     */
    public EditorKit getEditorKit(JTextComponent tc) {
        JEditorPane pane = (JEditorPane) getComponent();
        return pane.getEditorKit();
    }

    /**
     * Fetch an action map to use.  The map for a JEditorPane
     * is not shared because it changes with the EditorKit.
     */
    ActionMap getActionMap() {
        ActionMap am = new ActionMapUIResource();
        am.put("requestFocus", new FocusAction());
        EditorKit editorKit = getEditorKit(getComponent());
        if (editorKit != null) {
            Action[] actions = editorKit.getActions();
            if (actions != null) {
                addActions(am, actions);
            }
        }
        am.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        am.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        am.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
        return am;
    }

    /**
     * This method gets called when a bound property is changed
     * on the associated JTextComponent.  This is a hook
     * which UI implementations may change to reflect how the
     * UI displays bound properties of JTextComponent subclasses.
     * This is implemented to rebuild the ActionMap based upon an
     * EditorKit change.
     *
     * @param evt the property change event
     */
    protected void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("editorKit")) {
            ActionMap map = SwingUtilities.getUIActionMap(getComponent());
            if (map != null) {
                Object oldValue = evt.getOldValue();
                if (oldValue instanceof EditorKit) {
                    Action[] actions = ((EditorKit)oldValue).getActions();
                    if (actions != null) {
                        removeActions(map, actions);
                    }
                }
                Object newValue = evt.getNewValue();
                if (newValue instanceof EditorKit) {
                    Action[] actions = ((EditorKit)newValue).getActions();
                    if (actions != null) {
                        addActions(map, actions);
                    }
                }
            }
            updateFocusTraversalKeys();
        } else if ("editable".equals(evt.getPropertyName())) {
            updateFocusTraversalKeys();
        }
    }

    void removeActions(ActionMap map, Action[] actions) {
        int n = actions.length;
        for (int i = 0; i < n; i++) {
            Action a = actions[i];
            map.remove(a.getValue(Action.NAME));
        }
    }

    void addActions(ActionMap map, Action[] actions) {
        int n = actions.length;
        for (int i = 0; i < n; i++) {
            Action a = actions[i];
            map.put(a.getValue(Action.NAME), a);
        }
    }

}
