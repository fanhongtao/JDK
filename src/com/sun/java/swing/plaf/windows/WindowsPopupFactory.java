/*
 * @(#)WindowsPopupFactory.java	1.2 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.windows;

import javax.swing.*;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import sun.awt.AppContext;

/**
 * A class which creates popup windows for the Windows look and feel.
 * This class coerces all popups to be heavyweight window instances
 * of com.sun.java.swing.plaf.windows.WindowsPopupWindow, which allows
 * the window to be tagged with a specific usage (i.e. tooltip, menu, etc.).
 * The native AWT code for showing the window then interprets this
 * tag and implements appropriate transition effects (sliding, etc.).
 * <p>
 * Note that support for transition effects may be supported with a
 * different mechanism in the future and so this class is 
 * package-private and targeted for Swing implementation use only.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.2 12/03/01
 * @author Amy Fowler
 */
public class WindowsPopupFactory extends PopupFactory {

    /**
     * The shared instanceof <code>PopupFactory</code> is per
     * <code>AppContext</code>. This is the key used in the
     * <code>AppContext</code> to locate the <code>PopupFactory</code>.
     */
    private static final Object SharedInstanceKey = 
        new StringBuffer("PopupFactory.SharedInstanceKey");

    /**
     * Max number of items to store in any one particular cache.
     */
    private static final int MAX_CACHE_SIZE = 5;


    /**
     * Creates a <code>Popup</code> for the Component <code>owner</code>
     * containing the Component <code>contents</code>. <code>owner</code>
     * is used to determine which <code>Window</code> the new
     * <code>Popup</code> will parent the <code>Component</code> the
     * <code>Popup</code> creates to. A null <code>owner</code> implies there
     * is no valid parent. <code>x</code> and
     * <code>y</code> specify the preferred initial location to place
     * the <code>Popup</code> at. Based on screen size, or other paramaters,
     * the <code>Popup</code> may not display at <code>x</code> and
     * <code>y</code>.
     *
     * @param owner    Component mouse coordinates are relative to, may be null
     * @param contents Contents of the Popup
     * @param x        Initial x screen coordinate
     * @param y        Initial y screen coordinate
     * @exception IllegalArgumentException if contents is null
     * @return Popup containing Contents
     */
    public Popup getPopup(Component owner, Component contents,
                          int x, int y) throws IllegalArgumentException {
        if (contents == null) {
            throw new IllegalArgumentException(
                          "Popup.getPopup must be passed non-null contents");
        }

	Popup popup = HeavyWeightPopup.getHeavyWeightPopup(owner, 
							   contents, 
							   x, y);

        return popup != null? popup : super.getPopup(owner, contents, x, y);
    }


    /**
     * Popup implementation that uses a Window as the popup.
     */
    private static class HeavyWeightPopup extends Popup {
        private static final Object heavyWeightPopupCacheKey = 
                 new StringBuffer("PopupFactory.heavyWeightPopupCache");

        /**
         * Returns either a new or recycled <code>Popup</code> containing
         * the specified children.
         */
        static Popup getHeavyWeightPopup(Component owner, Component contents,
                                         int ownerX, int ownerY) {
            Window window = (owner != null) ? SwingUtilities.
                              getWindowAncestor(owner) : null;
            HeavyWeightPopup popup = null;

            if (window != null) {
                popup = getRecycledHeavyWeightPopup(window);
            }
            if (popup == null) {
                popup = new HeavyWeightPopup();
            }
            popup.reset(owner, contents, ownerX, ownerY);
            return popup;
        }

        /**
         * Returns a previously disposed heavy weight <code>Popup</code>
         * associated with <code>window</code>. This will return null if
         * there is no <code>HeavyWeightPopup</code> associated with
         * <code>window</code>.
         */
        private static HeavyWeightPopup getRecycledHeavyWeightPopup(Window w) {
            synchronized (HeavyWeightPopup.class) {
                List cache;
                Map heavyPopupCache = getHeavyWeightPopupCache();

                if (heavyPopupCache.containsKey(w)) {
                    cache = (List)heavyPopupCache.get(w);
                } else {
                    return null;
                }
                int c;
                if ((c = cache.size()) > 0) {
                    HeavyWeightPopup r = (HeavyWeightPopup)cache.get(0);
                    cache.remove(0);
                    return r;
                }
                return null;
            }
        }

        /**
         * Returns the cache to use for heavy weight popups. Maps from
         * <code>Window</code> to a <code>List</code> of
         * <code>HeavyWeightPopup</code>s.
         */
        private static Map getHeavyWeightPopupCache() {
            synchronized (HeavyWeightPopup.class) {

                Map cache = (Map)AppContext.getAppContext().get(heavyWeightPopupCacheKey);

                if (cache == null) {
                    cache = new HashMap(2);
                    AppContext.getAppContext().put(heavyWeightPopupCacheKey,
                                                 cache);
                }
                return cache;
            }
        }

        /**
         * Recycles the passed in <code>HeavyWeightPopup</code>.
         */
        private static void recycleHeavyWeightPopup(HeavyWeightPopup popup) {
            synchronized (HeavyWeightPopup.class) {
                List cache;
                Object window = SwingUtilities.getWindowAncestor(
                                     popup.getWindow());
                Map heavyPopupCache = getHeavyWeightPopupCache();

                if (window instanceof WindowsPopupFactory.DefaultFrame) {
                    // Don't cache these, it means the owner didn't have a
                    // valid Window parent, and that we should never see this
                    // Window again.
                    return;
                } else if (heavyPopupCache.containsKey(window)) {
                    cache = (List)heavyPopupCache.get(window);
                } else {
                    cache = new ArrayList();
                    heavyPopupCache.put(window, cache);
                    // Clean up if the Window is closed
                    final Window w = (Window)window;

                    w.addWindowListener(new WindowAdapter() {
                        public void windowClosed(WindowEvent e) {
                            synchronized(HeavyWeightPopup.class) {
                                Map heavyPopupCache2 =
                                              getHeavyWeightPopupCache();
                                heavyPopupCache2.remove(w);
                            }
                        }
                    });
                }

                if(cache.size() < MAX_CACHE_SIZE) {
                    cache.add(popup);
                }
            }
        }

	private WindowsPopupWindow window;

	HeavyWeightPopup() {
	}

        HeavyWeightPopup(Component owner, Component contents, int x, int y) {
            if (contents == null) {
                throw new IllegalArgumentException("Contents must be non-null");
            }
            reset(owner, contents, x, y);
        }

        public WindowsPopupWindow getWindow() {
	    return window;
	}

        public void show() {
	    if (window != null) {
		window.show();
	    }
        }

        public void hide() {
	    window.hide();
            window.getContentPane().removeAll();
            recycleHeavyWeightPopup(this);
        }

        void reset(Component owner, Component contents, int ownerX, int ownerY) {
            if (window == null) {
                window = createWindow(owner);
            }

	    if (WindowsLookAndFeel.useNativeAnimation) {
	        if (contents instanceof JToolTip) {
		    window.setWindowType(WindowsPopupWindow.TOOLTIP_WINDOW_TYPE);
	        } else if (contents instanceof JPopupMenu) {
		    if (owner instanceof JComboBox) {
		        window.setWindowType(WindowsPopupWindow.COMBOBOX_POPUP_WINDOW_TYPE);
		    } else if (owner instanceof JMenu) {
		        if (((JMenu)owner).isTopLevelMenu()) {
			    window.setWindowType(WindowsPopupWindow.MENU_WINDOW_TYPE);
		        } else {
			    window.setWindowType(WindowsPopupWindow.SUBMENU_WINDOW_TYPE);
		        }
		    } else {
		        window.setWindowType(WindowsPopupWindow.POPUPMENU_WINDOW_TYPE);
		    }
	        }
	    }

            window.setLocation(ownerX, ownerY);
            window.getContentPane().add(contents, BorderLayout.CENTER);
            window.invalidate();
            window.pack();
        }

        WindowsPopupWindow createWindow(Component owner) {
            return new WindowsPopupWindow(getParentWindow(owner));
        }

        private Window getParentWindow(Component owner) {
            Window window = null;

            if (owner instanceof Window) {
                window = (Window)owner;
            } else if (owner != null) {
                window = SwingUtilities.getWindowAncestor(owner);
            }
            if (window == null) {
                window = new DefaultFrame();
            }
            return window;
        } 
    }

    /**
     * Used if no valid Window ancestor of the supplied owner is found.
     * <p>
     * PopupFactory uses this as a way to know when the Popup shouldn't
     * be cached based on the Window.
     */
    static class DefaultFrame extends Frame {
    }
}


