/*
 * @(#)BasicDesktopPaneUI.java	1.57 05/10/31
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

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
import sun.swing.DefaultLookup;
import sun.swing.UIAction;
import sun.awt.AppContext;

/**
 * Basic L&F for a desktop.
 *
 * @version 1.57 10/31/05
 * @author Steve Wilson
 */
public class BasicDesktopPaneUI extends DesktopPaneUI {
    // Old actions forward to an instance of this.
    private static final Actions SHARED_ACTION = new Actions();
    private static final Object FRAMES_CACHE_KEY = 
                new StringBuilder("BASIC_DESKTOP_PANE_UI.FRAMES_CACHE");

    private static Dimension minSize = new Dimension(0,0);
    private static Dimension maxSize = new Dimension(Integer.MAX_VALUE,
            Integer.MAX_VALUE);
    private Handler handler;
    private PropertyChangeListener pcl;

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
    @Deprecated
    protected KeyStroke minimizeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke maximizeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke closeKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke navigateKey;
    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of 1.3.
     */
    @Deprecated
    protected KeyStroke navigateKey2;

    public static ComponentUI createUI(JComponent c) {
        return new BasicDesktopPaneUI();
    }

    public BasicDesktopPaneUI() {
    }

    private static Vector getCurrentFramesCache() {
        synchronized (BasicDesktopPaneUI.class) {
            AppContext appContext = AppContext.getAppContext();
            Vector framesCache = (Vector) appContext.get(FRAMES_CACHE_KEY);
            if(framesCache == null) {
                framesCache = new Vector();
                appContext.put(FRAMES_CACHE_KEY,framesCache);
            }
            return framesCache;
        }
    }

    public void installUI(JComponent c)   {
	desktop = (JDesktopPane)c;
	installDefaults();
	installDesktopManager();
        installListeners();
	installKeyboardActions();
    }

    public void uninstallUI(JComponent c) {
	uninstallKeyboardActions();
        uninstallListeners();
	uninstallDesktopManager();
        uninstallDefaults();
	desktop = null;
        handler = null;
    }

    protected void installDefaults() {
	if (desktop.getBackground() == null || 
	    desktop.getBackground() instanceof UIResource) {
	    desktop.setBackground(UIManager.getColor("Desktop.background"));
	}
        LookAndFeel.installProperty(desktop, "opaque", Boolean.TRUE);
    }

    protected void uninstallDefaults() { }

    /**
     * Installs the <code>PropertyChangeListener</code> returned from
     * <code>createPropertyChangeListener</code> on the
     * <code>JDesktopPane</code>.
     *
     * @since 1.5
     * @see #createPropertyChangeListener
     */
    protected void installListeners() {
        pcl = createPropertyChangeListener();
        desktop.addPropertyChangeListener(pcl);
    }

    /**
     * Uninstalls the <code>PropertyChangeListener</code> returned from 
     * <code>createPropertyChangeListener</code> from the
     * <code>JDesktopPane</code>.
     *
     * @since 1.5
     * @see #createPropertyChangeListener
     */
    protected void uninstallListeners() {
        desktop.removePropertyChangeListener(pcl);
        pcl = null;
    }

    protected void installDesktopManager() {
        desktopManager = desktop.getDesktopManager();
	if(desktopManager == null) {
	    desktopManager = new BasicDesktopManager();
	    desktop.setDesktopManager(desktopManager);
	}
    }

    protected void uninstallDesktopManager() {
	if(desktop.getDesktopManager() instanceof UIResource) {
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
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
			inputMap);
	}

        LazyActionMap.installLazyActionMap(desktop, BasicDesktopPaneUI.class,
                "DesktopPane.actionMap");
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
	    return (InputMap)DefaultLookup.get(desktop, this,
                    "Desktop.ancestorInputMap");
	}
	return null;
    }

    InputMap createInputMap(int condition) {
	if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW) {
	    Object[] bindings = (Object[])DefaultLookup.get(desktop,
                    this, "Desktop.windowBindings");

	    if (bindings != null) {
		return LookAndFeel.makeComponentInputMap(desktop, bindings);
	    }
	}
	return null;
    }

    static void loadActionMap(LazyActionMap map) {
        map.put(new Actions(Actions.RESTORE));
        map.put(new Actions(Actions.CLOSE));
        map.put(new Actions(Actions.MOVE));
        map.put(new Actions(Actions.RESIZE));
        map.put(new Actions(Actions.LEFT));
        map.put(new Actions(Actions.SHRINK_LEFT));
        map.put(new Actions(Actions.RIGHT));
        map.put(new Actions(Actions.SHRINK_RIGHT));
        map.put(new Actions(Actions.UP));
        map.put(new Actions(Actions.SHRINK_UP));
        map.put(new Actions(Actions.DOWN));
        map.put(new Actions(Actions.SHRINK_DOWN));
        map.put(new Actions(Actions.ESCAPE));
        map.put(new Actions(Actions.MINIMIZE));
        map.put(new Actions(Actions.MAXIMIZE));
        map.put(new Actions(Actions.NEXT_FRAME));
        map.put(new Actions(Actions.PREVIOUS_FRAME));
        map.put(new Actions(Actions.NAVIGATE_NEXT));
        map.put(new Actions(Actions.NAVIGATE_PREVIOUS));
    }

    protected void uninstallKeyboardActions(){ 
      unregisterKeyboardActions();
      SwingUtilities.replaceUIInputMap(desktop, JComponent.
				     WHEN_IN_FOCUSED_WINDOW, null);
      SwingUtilities.replaceUIInputMap(desktop, JComponent.
				     WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
      SwingUtilities.replaceUIActionMap(desktop, null);
    }

    public void paint(Graphics g, JComponent c) {}

    public Dimension getPreferredSize(JComponent c) {return null;}

    public Dimension getMinimumSize(JComponent c) {
	return minSize;
	}
    public Dimension getMaximumSize(JComponent c){
	return maxSize;
    }

    /**
     * Returns the <code>PropertyChangeListener</code> to install on
     * the <code>JDesktopPane</code>.
     *
     * @since 1.5
     * @return The PropertyChangeListener that will be added to track
     * changes in the desktop pane.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    private class Handler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();

            if ("desktopManager" == propertyName) {
                installDesktopManager();
            }
        }
    }

    /**
     * The default DesktopManager installed by the UI.
     */
    private class BasicDesktopManager extends DefaultDesktopManager
            implements UIResource {
    }

    private static class Actions extends UIAction {
        private static String CLOSE = "close";
        private static String ESCAPE = "escape";
        private static String MAXIMIZE = "maximize";
        private static String MINIMIZE = "minimize";
        private static String MOVE = "move";
        private static String RESIZE = "resize";
        private static String RESTORE = "restore";
        private static String LEFT = "left";
        private static String RIGHT = "right";
        private static String UP = "up";
        private static String DOWN = "down";
        private static String SHRINK_LEFT = "shrinkLeft";
        private static String SHRINK_RIGHT = "shrinkRight";
        private static String SHRINK_UP = "shrinkUp";
        private static String SHRINK_DOWN = "shrinkDown";
        private static String NEXT_FRAME = "selectNextFrame";
        private static String PREVIOUS_FRAME = "selectPreviousFrame";
        private static String NAVIGATE_NEXT = "navigateNext";
        private static String NAVIGATE_PREVIOUS = "navigatePrevious";
        private final int MOVE_RESIZE_INCREMENT = 10;
        private static boolean moving = false;
        private static boolean resizing = false;
        private static JInternalFrame sourceFrame = null;
        private static Component focusOwner = null;

        Actions() {
            super(null);
        }

        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JDesktopPane dp = (JDesktopPane)e.getSource();
            String key = getName();

            if (CLOSE == key || MAXIMIZE == key || MINIMIZE == key ||
                    RESTORE == key) {
                setState(dp, key);
            }
            else if (ESCAPE == key) {
                if (sourceFrame == dp.getSelectedFrame() &&
                        focusOwner != null) {
                    focusOwner.requestFocus();
                }
                moving = false;
                resizing = false;
                sourceFrame = null;
                focusOwner = null;
            }
            else if (MOVE == key || RESIZE == key) {
                sourceFrame = dp.getSelectedFrame();
                if (sourceFrame == null) {
                    return;
                }
                moving = (key == MOVE) ? true : false;
                resizing = (key == RESIZE) ? true : false;

                focusOwner = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().getFocusOwner();
                if (!SwingUtilities.isDescendingFrom(focusOwner, sourceFrame)) {
                    focusOwner = null;
                }
                sourceFrame.requestFocus();
            }
            else if (LEFT == key ||
                     RIGHT == key ||
                     UP == key ||
                     DOWN == key ||
                     SHRINK_RIGHT == key ||
                     SHRINK_LEFT == key ||
                     SHRINK_UP == key ||
                     SHRINK_DOWN == key) {
                JInternalFrame c = dp.getSelectedFrame();
                if (sourceFrame == null || c != sourceFrame ||
                        KeyboardFocusManager.
                            getCurrentKeyboardFocusManager().getFocusOwner() !=
                                sourceFrame) {
                    return;
                }
                Dimension size = c.getSize();
                Dimension minSize = c.getMinimumSize();
                Point loc = c.getLocation();
                if (LEFT == key) {
                    if (moving) {
                        c.setLocation(loc.x - MOVE_RESIZE_INCREMENT, loc.y);
                    } else if (resizing) {
                        c.setLocation(loc.x - MOVE_RESIZE_INCREMENT, loc.y);
                        c.setSize(size.width + MOVE_RESIZE_INCREMENT,
                                size.height);
                    }
                } else if (RIGHT == key) {
                    if (moving) {
                        c.setLocation(loc.x + MOVE_RESIZE_INCREMENT, loc.y);
                    } else if (resizing) {
                        c.setLocation(loc.x, loc.y);
                        c.setSize(size.width + MOVE_RESIZE_INCREMENT,
                                size.height);
                    }
                } else if (UP == key) {
                    if (moving) {
                        c.setLocation(loc.x, loc.y - MOVE_RESIZE_INCREMENT);
                    } else if (resizing) {
                        c.setLocation(loc.x, loc.y - MOVE_RESIZE_INCREMENT);
                        c.setSize(size.width,
                                size.height + MOVE_RESIZE_INCREMENT);
                    }
                } else if (DOWN == key) {
                    if (moving) {
                        c.setLocation(loc.x, loc.y + MOVE_RESIZE_INCREMENT);
                    } else if (resizing) {
                        c.setLocation(loc.x, loc.y);
                        c.setSize(size.width,
                                size.height + MOVE_RESIZE_INCREMENT);
                    }
                } else if (SHRINK_LEFT == key && resizing) {
                    if (minSize.width < (size.width - MOVE_RESIZE_INCREMENT)) {
                        c.setLocation(loc.x, loc.y);
                        c.setSize(size.width - MOVE_RESIZE_INCREMENT,
                                size.height);
                    } else {
                        c.setSize(minSize.width, size.height);
                    }
                } else if (SHRINK_RIGHT == key && resizing) {
                    if (minSize.width < (size.width - MOVE_RESIZE_INCREMENT)) {
                        c.setLocation(loc.x + MOVE_RESIZE_INCREMENT, loc.y);
                        c.setSize(size.width - MOVE_RESIZE_INCREMENT,
                                size.height);
                    } else {
                        c.setLocation(loc.x - minSize.width + size.width,
                                loc.y);
                        c.setSize(minSize.width, size.height);
                    }
                } else if (SHRINK_UP == key && resizing) {
                    if (minSize.height <
                            (size.height - MOVE_RESIZE_INCREMENT)) {
                        c.setLocation(loc.x, loc.y);
                        c.setSize(size.width,
                                size.height - MOVE_RESIZE_INCREMENT);
                    } else {
                        c.setSize(size.width, minSize.height);
                    }
                } else if (SHRINK_DOWN == key  && resizing) {
                    if (minSize.height <
                            (size.height - MOVE_RESIZE_INCREMENT)) {
                        c.setLocation(loc.x, loc.y + MOVE_RESIZE_INCREMENT);
                        c.setSize(size.width,
                                size.height - MOVE_RESIZE_INCREMENT);
                    } else {
                        c.setLocation(loc.x,
                                loc.y - minSize.height + size.height);
                        c.setSize(size.width, minSize.height);
                    }
                }
            }
            else if (NEXT_FRAME == key || PREVIOUS_FRAME == key) {
                selectFrame(dp, (key == NEXT_FRAME) ? true : false);
            }
            else if (NAVIGATE_NEXT == key ||
                     NAVIGATE_PREVIOUS == key) {
                boolean moveForward = true;
                if (NAVIGATE_PREVIOUS == key) {
                    moveForward = false;
                }
                Container cycleRoot = dp.getFocusCycleRootAncestor();

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
                                        focusNextComponent(dp);
                            } else {
                                KeyboardFocusManager.
                                    getCurrentKeyboardFocusManager().
                                    focusPreviousComponent(dp);
                            }
                        } finally {
                            sPolicy.setImplicitDownCycleTraversal(idc);
                        }
                    }
                }
            }
        }

        private void selectFrame(JDesktopPane dp, boolean forward) {
            Vector framesCache = getCurrentFramesCache();
            if (forward) {
                // navigate to the next frame
                int i = 0;
                verifyFramesCache(dp);
                if (framesCache.size() == 0) {
                    return;
                }

                JInternalFrame f = dp.getSelectedFrame();

                if (f != null) {
                    i = framesCache.indexOf(f);
                }
                if (i == -1) {
                    /* if the frame is not there, its icon may be */
                    i = framesCache.indexOf(f.getDesktopIcon());
                    if (i == -1) {
                        /* error */
                        return;
                    }
                }
                if (++i == framesCache.size()) {
                    /* wrap */
                    i = 0;
                }
                JComponent c = (JComponent) framesCache.elementAt(i);
                if (c instanceof JInternalFrame) {
                    try {
                        ((JInternalFrame)c).setSelected(true);
                        dp.getDesktopManager().activateFrame((JInternalFrame)c);
                    } catch (PropertyVetoException pve) {}
                } else {
                    /* it had better be an icon! */
                    if (!(c instanceof JInternalFrame.JDesktopIcon)){
                        /* error */
                        return;
                    }
                    try {
                        ((JInternalFrame)((JInternalFrame.JDesktopIcon)c).
                                getInternalFrame()).setSelected(true);
                        dp.getDesktopManager().activateFrame(
                                ((JInternalFrame.JDesktopIcon)c).
                                        getInternalFrame());
                    } catch (PropertyVetoException pve) {}
                }
            } else {
                // navigate to the previous internal frame
                int i = 0;
                verifyFramesCache(dp);
                if (framesCache.size() == 0) {
                    return;
                }
                JInternalFrame f = dp.getSelectedFrame();
                if (f != null) {
                    i = framesCache.indexOf(f);
                }
                if (i == -1) {
                    /* if the frame is not there, its icon may be */
                    i = framesCache.indexOf(f.getDesktopIcon());
                    if (i == -1) {
                        /* error */
                        return;
                    }
                }
                if (--i == -1) {
                    /* wrap */
                    i = framesCache.size() - 1;
                }
                JComponent c = (JComponent) framesCache.elementAt(i);
                if (c instanceof JInternalFrame) {
                    try {
                        ((JInternalFrame)c).setSelected(true);
                    } catch (PropertyVetoException pve) {}
                } else {
                    /* it had better be an icon! */
                    if (!(c instanceof JInternalFrame.JDesktopIcon)) {
                        /* error */
                        return;
                    }
                    try {
                        ((JInternalFrame)((JInternalFrame.JDesktopIcon)c).
                                getInternalFrame()).setSelected(true);
                    } catch (PropertyVetoException pve) {}
                }
            }
        }

        private void setState(JDesktopPane dp, String state) {
            if (state == CLOSE) {
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                f.doDefaultCloseAction();
            } else if (state == MAXIMIZE) {
                // maximize the selected frame
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                if (!f.isMaximum()) {
                    if (f.isIcon()) {
                        try {
                            f.setIcon(false);
                            f.setMaximum(true);
                        } catch (PropertyVetoException pve) {}
                    } else {
                        try {
                            f.setMaximum(true);
                        } catch (PropertyVetoException pve) {
                        }
                    }
                }
            } else if (state == MINIMIZE) {
                // minimize the selected frame
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
                if (!f.isIcon()) {
                    try {
                        f.setIcon(true);
                    } catch (PropertyVetoException pve) {
                    }
                }
            } else if (state == RESTORE) {
                // restore the selected minimized or maximized frame
                JInternalFrame f = dp.getSelectedFrame();
                if (f == null) {
                    return;
                }
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
        }


        public boolean isEnabled(Object sender) {
            if (sender instanceof JDesktopPane) {
                JDesktopPane dp = (JDesktopPane)sender;
                JInternalFrame iFrame = dp.getSelectedFrame();
                if (iFrame == null) {
                    return false;
                }
                String action = getName();
                if (action == Actions.CLOSE) {
                    return iFrame.isClosable();
                } else if (action == Actions.MINIMIZE) {
                    return iFrame.isIconifiable();
                } else if (action == Actions.MAXIMIZE) {
                    return iFrame.isMaximizable();
                }
                return true;
            }
            return false;
        }
    }


    /*
     * Handles restoring a minimized or maximized internal frame.
     */
    protected class OpenAction extends AbstractAction {
	public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.RESTORE);
	}

	public boolean isEnabled() { 
	    return true;
	}
    }

    /*
     * Handles closing an internal frame
     */
    protected class CloseAction extends AbstractAction {
	public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.CLOSE);
	}

	public boolean isEnabled() { 
            JInternalFrame iFrame = desktop.getSelectedFrame();
            if (iFrame != null) {
                return iFrame.isClosable();
            }
            return false;
	}
    }

    /*
     * Handles minimizing an internal frame
     */
    protected class MinimizeAction extends AbstractAction {
	public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.MINIMIZE);
	}

	public boolean isEnabled() { 
            JInternalFrame iFrame = desktop.getSelectedFrame();
            if (iFrame != null) {
	        return iFrame.isIconifiable();
            }
            return false;
	}
    }

    /*
     * Handles maximizing an internal frame
     */
    protected class MaximizeAction extends AbstractAction {
	public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.setState(dp, Actions.MAXIMIZE);
	}

	public boolean isEnabled() { 
            JInternalFrame iFrame = desktop.getSelectedFrame();
            if (iFrame != null) {
	        return iFrame.isMaximizable();
            }
            return false;
        }
    }

    /*
     * Handles navigating to the next internal frame. 
     */
    protected class NavigateAction extends AbstractAction {
	public void actionPerformed(ActionEvent evt) {
            JDesktopPane dp = (JDesktopPane)evt.getSource();
            SHARED_ACTION.selectFrame(dp, true);
	}

	public boolean isEnabled() { 
	    return true;
	}
    }

    private static void verifyFramesCache(JDesktopPane dp) {
        Vector framesCache = getCurrentFramesCache();
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
       	JInternalFrame [] allFrames = dp.getAllFrames();
       	if (framesHaveClosed || allFrames.length != framesCache.size()) {
	    // Cache frames starting at the lowest layer.
	    framesCache.clear();
	    int low = dp.lowestLayer();
	    int high = dp.highestLayer();
	    for (int i = high; i >= low; i--) {
		Component [] comp = dp.getComponentsInLayer(i);
		if (comp.length > 0) {
		    for (int j = 0; j < comp.length; j++) {
			framesCache.addElement(comp[j]);
		    }
		}
	    }
       	}
    }
}
