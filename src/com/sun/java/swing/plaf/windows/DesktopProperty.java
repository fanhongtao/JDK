/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.windows;

import java.awt.*;
import java.beans.*;
import java.lang.ref.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * Wrapper for a value from the desktop. The value is lazily looked up, and
 * can be accessed using the <code>UIManager.ActiveValue</code> method
 * <code>createValue</code>. If the underlying desktop property changes this
 * will force the UIs to update all known Frames. You can invoke
 * <code>invalidate</code> to force the value to be fetched again.
 *
 * @version @(#)DesktopProperty.java	1.5 03/01/23
 */
// NOTE: Don't rely on this class staying in this location. It is likely
// to move to a different package in the future.
public class DesktopProperty implements UIDefaults.ActiveValue {
    /**
     * Indicates if an updateUI call is pending.
     */
    private static boolean updatePending;

    /**
     * ReferenceQueue of unreferenced WeakPCLs.
     */
    private static ReferenceQueue queue;


    /**
     * PropertyChangeListener attached to the Toolkit.
     */
    private WeakPCL pcl;
    /**
     * Key used to lookup value from desktop.
     */
    private String key;
    /**
     * Value to return.
     */
    private Object value;
    /**
     * Fallback value in case we get null from desktop.
     */
    private Object fallback;

    /**
     * Toolkit.
     */
    private Toolkit toolkit;


    static {
        queue = new ReferenceQueue();
    }

    /**
     * Cleans up any lingering state held by unrefeernced
     * DesktopProperties.
     */
    static void flushUnreferencedProperties() {
        WeakPCL pcl;

        while ((pcl = (WeakPCL)queue.poll()) != null) {
            pcl.dispose();
        }
    }


    /**
     * Sets whether or not an updateUI call is pending.
     */
    private static synchronized void setUpdatePending(boolean update) {
	updatePending = update;
    }

    /**
     * Returns true if a UI update is pending.
     */
    private static synchronized boolean isUpdatePending() {
	return updatePending;
    }
 
    /**
     * Updates the UIs of all the known Frames.
     */
    private static void updateAllUIs() {
	// Check if the current UI is WindowsLookAndfeel and flush the XP style map.
	// Note: Change the package test if this class is moved to a different package.
	Class uiClass = UIManager.getLookAndFeel().getClass();
 	if (uiClass.getPackage().equals(DesktopProperty.class.getPackage())) {
	    XPStyle.invalidateStyle();
 	}
        Frame appFrames[] = Frame.getFrames();
	for (int j=0; j < appFrames.length; j++) {
	    updateWindowUI(appFrames[j]);			    
	}
    }

    /**
     * Updates the UI of the passed in window and all its children.
     */
    private static void updateWindowUI(Window window) {
        SwingUtilities.updateComponentTreeUI(window);
	Window ownedWins[] = window.getOwnedWindows();
	for (int i=0; i < ownedWins.length; i++) {
	    updateWindowUI(ownedWins[i]);
	}
    }


    /**
     * Creates a DesktopProperty.
     *
     * @param key Key used in looking up desktop value.
     * @param fallback Value used if desktop property is null.
     * @param toolkit Toolkit used to fetch property from, can be null
     *        in which default will be used.
     */
    public DesktopProperty(String key, Object fallback, Toolkit toolkit) {
        this.key = key;
        this.fallback = fallback;
        this.toolkit = toolkit;
        // The only sure fire way to clear our references is to create a
        // Thread and wait for a reference to be added to the queue.
        // Because it is so rare that you will actually change the look
        // and feel, this stepped is forgoed and a middle ground of
        // flushing references from the constructor is instead done.
        // The implication is that once one DesktopProperty is created
        // there will most likely be n (number of DesktopProperties created
        // by the LookAndFeel) WeakPCLs around, but this number will not
        // grow past n.
        flushUnreferencedProperties();
    }

    /**
     * UIManager.LazyValue method, returns the value from the desktop
     * or the fallback value if the desktop value is null.
     */
    public Object createValue(UIDefaults table) {
        if (value == null) {
            value = configureValue(getValueFromDesktop());
            if (value == null) {
                value = configureValue(getDefaultValue());
            }
        }
        return value;
    }

    /**
     * Returns the value from the desktop.
     */
    protected Object getValueFromDesktop() {
        if (this.toolkit == null) {
            this.toolkit = Toolkit.getDefaultToolkit();
        }
        Object value = toolkit.getDesktopProperty(getKey());
        pcl = new WeakPCL(this, toolkit, getKey());
        toolkit.addPropertyChangeListener(getKey(), pcl);
        return value;
    }

    /**
     * Returns the value to use if the desktop property is null.
     */
    protected Object getDefaultValue() {
        return fallback;
    }

    /**
     * Invalides the current value so that the next invocation of
     * <code>createValue</code> will ask for the property again.
     */
    public void invalidate() {
        if (pcl != null) {
            toolkit.removePropertyChangeListener(getKey(), pcl);
            toolkit = null;
            pcl = null;
            value = null;
        }
    }

    /**
     * Requests that all components in the GUI hierarchy be updated
     * to reflect dynamic changes in this look&feel.  This update occurs
     * by uninstalling and re-installing the UI objects. Requests are
     * batched and collapsed into a single update pass because often
     * many desktop properties will change at once.
     */    
    protected void updateUI() {
	if (!isUpdatePending()) {
            setUpdatePending(true);
            Runnable uiUpdater = new Runnable() {
                public void run() {
                    updateAllUIs();
		    setUpdatePending(false);
                }
            };
            SwingUtilities.invokeLater(uiUpdater);
	}
    }

    /**
     * Configures the value as appropriate for a defaults property in
     * the UIDefaults table.
     */
    protected Object configureValue(Object value) {
        if (value != null) {
            if (value instanceof Color) {
                return new ColorUIResource((Color)value);
            }
            else if (value instanceof Font) {
                return new FontUIResource((Font)value);
            }
            else if (value instanceof UIDefaults.ProxyLazyValue) {
                value = ((UIDefaults.ProxyLazyValue)value).createValue(null);
            }
            else if (value instanceof UIDefaults.ActiveValue) {
                value = ((UIDefaults.ActiveValue)value).createValue(null);
            }
        }
        return value;
    }

    /**
     * Returns the key used to lookup the desktop properties value.
     */
    protected String getKey() {
        return key;
    }



    /**
     * As there is typically only one Toolkit, the PropertyChangeListener
     * is handled via a WeakReference so as not to pin down the
     * DesktopProperty.
     */
    private static class WeakPCL extends WeakReference
                               implements PropertyChangeListener {
        private Toolkit kit;
        private String key;

        WeakPCL(Object target, Toolkit kit, String key) {
            super(target, queue);
            this.kit = kit;
            this.key = key;
        }

        public void propertyChange(PropertyChangeEvent pce) {
            DesktopProperty property = (DesktopProperty)get();

            if (property == null) {
                // The property was GC'ed, we're no longer interested in
                // PropertyChanges, remove the listener.
                dispose();
            }
            else {
                property.invalidate();
                property.updateUI();
            }
        }

        void dispose() {
            kit.removePropertyChangeListener(key, this);
        }
    }
}
