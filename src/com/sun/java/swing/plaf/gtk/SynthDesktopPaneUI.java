/*
 * @(#)SynthDesktopPaneUI.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.plaf.*;

import java.beans.*;

import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.*;
import java.util.Vector;

/**
 * Synth L&F for a desktop.
 *
 * @version 1.9, 01/23/03 (originally from version 1.44 of BasicDesktopPaneUI)
 * @author Joshua Outwater
 * @author Steve Wilson
 */
class SynthDesktopPaneUI extends DesktopPaneUI implements
                  PropertyChangeListener, SynthUI, LazyActionMap.Loader {
    private static Dimension minSize = new Dimension(0,0);
    private static Dimension maxSize =
        new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private SynthStyle style;
    protected JDesktopPane desktop;
    protected DesktopManager desktopManager;

    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    protected KeyStroke minimizeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    protected KeyStroke maximizeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    protected KeyStroke closeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    protected KeyStroke navigateKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    protected KeyStroke navigateKey2;

    public static ComponentUI createUI(JComponent c) {
        return new SynthDesktopPaneUI();
    }

    public SynthDesktopPaneUI() {
    }

    public void installUI(JComponent c)   {
        desktop = (JDesktopPane)c;
        installDefaults();
        installDesktopManager();
        installKeyboardActions();
        installListeners();
    }

    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions();
        uninstallDesktopManager();
        uninstallDefaults();
        uninstallListeners();
        desktop = null;
    }

    protected void installListeners() {
        desktop.addPropertyChangeListener(this);
    }

    protected void installDefaults() {
        fetchStyle(desktop);
    }

    private void fetchStyle(JDesktopPane c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void uninstallListeners() {
        desktop.removePropertyChangeListener(this);
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(desktop, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void installDesktopManager() {
        if(desktop.getDesktopManager() == null) {
            desktopManager = new DefaultDesktopManager();
            desktop.setDesktopManager(desktopManager);
        }
    }

    protected void uninstallDesktopManager() {
        if(desktop.getDesktopManager() == desktopManager) {
            desktop.setDesktopManager(null);
        }
        desktopManager = null;
    }

    protected void installKeyboardActions(){
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        if (inputMap != null) {
            SwingUtilities.replaceUIInputMap(desktop,
                JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
        }
        inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if (inputMap != null) {
            SwingUtilities.replaceUIInputMap(desktop,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
        }
        LazyActionMap.installLazyActionMap(desktop, this);
        registerKeyboardActions();
    }

    protected void registerKeyboardActions(){
    }
 
    protected void unregisterKeyboardActions(){
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            return createInputMap(condition);
        }
        else if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap)UIManager.get("Desktop.ancestorInputMap");
        }
        return null;
    }

    InputMap createInputMap(int condition) {
        if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
            Object[] bindings =
                (Object[])UIManager.get("Desktop.windowBindings");

            if (bindings != null) {
                return LookAndFeel.makeComponentInputMap(desktop, bindings);
            }
        }
        return null;
    }


    public void loadActionMap(JComponent c, ActionMap map) {
        map.put("restore", new OpenAction());
        map.put("close", new CloseAction());
        map.put("move", new MoveResizeAction("move"));
        map.put("resize", new MoveResizeAction("resize"));
        map.put("left", new MoveResizeAction("left"));
        map.put("shrinkLeft", new MoveResizeAction("shrinkLeft"));
        map.put("right", new MoveResizeAction("right"));
        map.put("shrinkRight", new MoveResizeAction("shrinkRight"));
        map.put("up", new MoveResizeAction("up"));
        map.put("shrinkUp", new MoveResizeAction("shrinkUp"));
        map.put("down", new MoveResizeAction("down"));
        map.put("shrinkDown", new MoveResizeAction("shrinkDown"));
        map.put("escape", new MoveResizeAction("escape"));
        map.put("minimize", new MinimizeAction());
        map.put("maximize", new MaximizeAction());
        map.put("selectNextFrame", new NavigateAction());
        map.put("selectPreviousFrame", new PreviousAction());
        map.put("navigateNext", new NavigateOutAction(true));
        map.put("navigatePrevious", new NavigateOutAction(false));
    }

    protected void uninstallKeyboardActions() {
      unregisterKeyboardActions();
      SwingUtilities.replaceUIInputMap(
          desktop, JComponent.WHEN_IN_FOCUSED_WINDOW, null);
      SwingUtilities.replaceUIInputMap(
            desktop, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
      SwingUtilities.replaceUIActionMap(desktop, null);
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

    public Dimension getPreferredSize(JComponent c) {return null;}

    public Dimension getMinimumSize(JComponent c) {
        return minSize;
    }
    public Dimension getMaximumSize(JComponent c){
        return maxSize;
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
            fetchStyle((JDesktopPane)evt.getSource());
        }
    }

    /*
     * Key binding for accessibility -----------------
     */
    private static Vector framesCache;
    private boolean moving = false;
    private boolean resizing = false;
    private final int MOVE_RESIZE_INCREMENT = 10;

    /*
     * Handles restoring a minimized or maximized internal frame.
     */
    protected class OpenAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            // restore the selected minimized or maximized frame
            JInternalFrame f = desktop.getSelectedFrame();
            if (f == null) return;
                try {
                    if (f.isIcon()) {
                        f.setIcon(false);
                    } else if (f.isMaximum()) {
                        f.setMaximum(false);
                    }
                    f.setSelected(true);
                } catch (PropertyVetoException pve) {
                }
        }

        public boolean isEnabled() { 
            return true;
        }
    }

    /*
     * Handles closing an internal frame
     */
    protected class CloseAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JInternalFrame f = desktop.getSelectedFrame();
            if (f == null) {
                return;
            }
            if (f.isClosable()) {
                try {
                    f.setClosed(true);
                } catch (PropertyVetoException pve) {
                }
            }
        }

        public boolean isEnabled() { 
            return true;
        }
    }

    /*
     * Handles moving and resizing an internal frame
     */
    private class MoveResizeAction extends AbstractAction {
        private String command;
        
        public MoveResizeAction(String command) {
            this.command = command;
        }
    
        public void actionPerformed(ActionEvent e) {
            JInternalFrame c = desktop.getSelectedFrame(); 
            if (c == null) {
                return;
            }
            if ("move".equals(command)) {
                moving = true;
                resizing = false;
                return;
            } else if ("resize".equals(command)) {
                moving = false;
                resizing = true;
                return;
            } else if ("escape".equals(command)) {
                moving = resizing = false;
                return;
            }
            if (!moving && !resizing) {
                return;
            }
    
            Dimension size = c.getSize();
            Dimension minSize = c.getMinimumSize();
            Point loc = c.getLocation();
    
            if ("left".equals(command)) {
                if (moving) {
                    c.setLocation(loc.x - MOVE_RESIZE_INCREMENT, loc.y);
                } else if (resizing) {
                    c.setLocation(loc.x - MOVE_RESIZE_INCREMENT, loc.y);
                    c.setSize(size.width + MOVE_RESIZE_INCREMENT, size.height);
                }
            } else if ("right".equals(command)) {
                if (moving) {
                    c.setLocation(loc.x + MOVE_RESIZE_INCREMENT, loc.y);
                } else if (resizing) {
                    c.setLocation(loc.x, loc.y);
                    c.setSize(size.width + MOVE_RESIZE_INCREMENT, size.height);
                }
            } else if ("up".equals(command)) {
                if (moving) {
                    c.setLocation(loc.x, loc.y - MOVE_RESIZE_INCREMENT);
                } else if (resizing) {
                    c.setLocation(loc.x, loc.y - MOVE_RESIZE_INCREMENT);
                    c.setSize(size.width, size.height + MOVE_RESIZE_INCREMENT);
                }
            } else if ("down".equals(command)) {
                if (moving) {
                    c.setLocation(loc.x, loc.y + MOVE_RESIZE_INCREMENT);
                } else if (resizing) {
                    c.setLocation(loc.x, loc.y);
                    c.setSize(size.width, size.height + MOVE_RESIZE_INCREMENT);
                }
            } else if ("shrinkLeft".equals(command) && resizing) {
                if (minSize.width < (size.width - MOVE_RESIZE_INCREMENT)) {
                    c.setLocation(loc.x, loc.y);
                    c.setSize(size.width - MOVE_RESIZE_INCREMENT, size.height);
                } else {
                    c.setSize(minSize.width, size.height);
                }
            } else if ("shrinkRight".equals(command) && resizing) {
                if (minSize.width < (size.width - MOVE_RESIZE_INCREMENT)) {
                    c.setLocation(loc.x + MOVE_RESIZE_INCREMENT, loc.y);
                    c.setSize(size.width - MOVE_RESIZE_INCREMENT, size.height);
                } else {
                    c.setLocation(loc.x - minSize.width + size.width , loc.y);
                    c.setSize(minSize.width, size.height);
                }
            } else if ("shrinkUp".equals(command) && resizing) {
                if (minSize.height < (size.height - MOVE_RESIZE_INCREMENT)) {
                    c.setLocation(loc.x, loc.y);
                    c.setSize(size.width, size.height - MOVE_RESIZE_INCREMENT);
                } else {
                    c.setSize(size.width, minSize.height);
                }
            } else if ("shrinkDown".equals(command) && resizing) {
                if (minSize.height < (size.height - MOVE_RESIZE_INCREMENT)) {
                    c.setLocation(loc.x, loc.y + MOVE_RESIZE_INCREMENT);
                    c.setSize(size.width, size.height - MOVE_RESIZE_INCREMENT);
                } else {
                    c.setLocation(loc.x, loc.y - minSize.height + size.height);
                    c.setSize(size.width, minSize.height);
                }
            }
        }

        public boolean isEnabled() { 
            return true;
        }
    }

    /*
     * Handles minimizing an internal frame
     */
    protected class MinimizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            // minimize the selected frame
            JInternalFrame f = desktop.getSelectedFrame();
            if (f == null) {
                return;
            }
            if (f.isIconifiable() && ! f.isIcon()) {
                try {
                    f.setIcon(true);
                } catch (PropertyVetoException pve) {
                }
            }
        }
    
        public boolean isEnabled() {
            return true;
        }
    }

    /*
     * Handles maximizing an internal frame
     */
    protected class MaximizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            // maximize the selected frame
            JInternalFrame f = desktop.getSelectedFrame();
            if (f == null) {
                return;
            }
            if (f.isMaximizable() && !f.isMaximum()) {
                if (f.isIcon()) {
                    try {
                       f.setIcon(false);
                       f.setMaximum(true);
                    } catch (PropertyVetoException pve) {}
                } else
                    try {
                        f.setMaximum(true);
                    } catch (PropertyVetoException pve) {
                }
            }
        }
    
        public boolean isEnabled() { 
            return true;
        }
    }

    /*
     * Handles navigating to the next internal frame. 
     */
    protected class NavigateAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            // navigate to the next frame
            int i = 0;
            verifyFramesCache();
            if (framesCache.size() == 0) {
                return;
            }
            JInternalFrame f = desktop.getSelectedFrame();
            if (f != null) {
                i = framesCache.indexOf(f);
            }
            if (i == -1) {
                /* if the frame is not there, its icon may be */
                i = framesCache.indexOf(f.getDesktopIcon());
                if (i == -1) {
                    return; /* error */
                }
            }
            if (++i == framesCache.size()) {
                i = 0; /* wrap */
            }
            JComponent c = (JComponent) framesCache.elementAt(i);
            if (c instanceof JInternalFrame) {
                try {
                    ((JInternalFrame) c).setSelected(true);
                    desktopManager.activateFrame((JInternalFrame) c);
                }
                catch (PropertyVetoException pve) {}
            } else {
                /* it had better be an icon! */
                if (!(c instanceof JInternalFrame.JDesktopIcon)) {
                    /* error */
                    return;
                }
                try {
                    ((JInternalFrame)((JInternalFrame.JDesktopIcon)c).
                        getInternalFrame()).setSelected(true);
                    desktopManager.activateFrame(
                        ((JInternalFrame.JDesktopIcon) c).getInternalFrame());
                } catch (PropertyVetoException pve) {}
            }
        }

        public boolean isEnabled() { 
            return true;
        }
    }

    /*
     * Handles navigating to the previous internal frame. 
     */
    private class PreviousAction extends AbstractAction {
    public void actionPerformed(ActionEvent e) {
        // navigate to the previous internal frame
            int i = 0;
        verifyFramesCache();
        if (framesCache.size() == 0) return;
        JInternalFrame f = desktop.getSelectedFrame();
        if (f != null) i = framesCache.indexOf(f);
        if (i == -1) {
          /* if the frame is not there, its icon may be */
          i = framesCache.indexOf(f.getDesktopIcon());
          if (i == -1) {
        /* error */ return;
          }
        }
        if (--i == -1) /* wrap */ i = framesCache.size() - 1;
        JComponent c = (JComponent) framesCache.elementAt(i);
        if (c instanceof JInternalFrame) {
          try {
        ((JInternalFrame) c).setSelected(true);
          }
          catch (PropertyVetoException pve) {}
        } else {
          /* it had better be an icon! */
          if (!(c instanceof JInternalFrame.JDesktopIcon)){
        /* error */
        return;
          }
          try {
        ((JInternalFrame)((JInternalFrame.JDesktopIcon) c).getInternalFrame()).setSelected(true);
          }
          catch (PropertyVetoException pve) {}
        }
    }
    public boolean isEnabled() { 
        return true;
    }
    }

    /**
     * Handles navigating to the component before or after the desktop.
     */
    private class NavigateOutAction extends AbstractAction {
        private boolean moveForward;

        public NavigateOutAction(boolean moveForward) {
            this.moveForward = moveForward;
        }

        public void actionPerformed(ActionEvent e) {
            Container cycleRoot = desktop.getFocusCycleRootAncestor();

            if (cycleRoot != null) {
                FocusTraversalPolicy policy =
                        cycleRoot.getFocusTraversalPolicy();
                if (policy != null && policy instanceof
                        SortingFocusTraversalPolicy) {
                    SortingFocusTraversalPolicy sPolicy =
                            (SortingFocusTraversalPolicy)policy;
                    boolean idc = sPolicy.getImplicitDownCycleTraversal();
                    try {
                        sPolicy.setImplicitDownCycleTraversal(false);
                        if (moveForward) {
                            KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                focusNextComponent(desktop);
                        } else {
                            KeyboardFocusManager.
                                getCurrentKeyboardFocusManager().
                                focusPreviousComponent(desktop);
                        }
                    } finally {
                        sPolicy.setImplicitDownCycleTraversal(idc);
                    }
                }
            }
        }

        public boolean isEnabled() {
            return true;
        }
    }

    /*
     * Verifies the internal frames cache is up to date.
     */
    private void verifyFramesCache() {

    // Need to initialize?
    if (framesCache == null) {
        framesCache = new Vector();
    }

    // Check whether any internal frames have closed in
    // which case we have to refresh the frames cache.
           boolean framesHaveClosed = false;
    int len = framesCache.size();
    for (int i = 0; i < len; i++) {
        JComponent c = 
        (JComponent)framesCache.elementAt(i);
        if (c instanceof JInternalFrame) {
        JInternalFrame f = (JInternalFrame)c;
        if (f.isClosed()) {
            framesHaveClosed = true;
            break;
        }
        }
        else if (c instanceof JInternalFrame.JDesktopIcon) {
        JInternalFrame.JDesktopIcon icon = 
            (JInternalFrame.JDesktopIcon)c;
        JInternalFrame f = (JInternalFrame)icon.getInternalFrame();
        if (f.isClosed()) {
            framesHaveClosed = true;
            break;
        }
        }
    } 
           JInternalFrame [] allFrames = desktop.getAllFrames();
           if (framesHaveClosed || allFrames.length != framesCache.size()) {
        // Cache frames starting at the lowest layer.
        framesCache.clear();
        int low = desktop.lowestLayer();
        int high = desktop.highestLayer();
        for (int i = high; i >= low; i--) {
        Component [] comp = desktop.getComponentsInLayer(i);
        if (comp.length > 0) {
            for (int j = 0; j < comp.length; j++) {
            framesCache.addElement(comp[j]);
            }
        }
        }
           }
    }
    // End of accessibility keybindings
}

