/*
 * @(#)LazyActionMap.java	1.2 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * An ActionMap that populates its contents as necessary. The
 * contents can be populated in one of two ways:
 * <ul><li>By way of the <code>LazyActionMap.Loader</code> interface.
 *         This should be used if the ActionMap will be unique and not
 *         shared.
 * <li>By passing in a <code>Class</code> object that has the
 *         public method <code>loadActionMap</code> which can be used for
 *         populating the ActionMap. This is used if the ActionMap can be
 *        shared.
 * </ul>       
 *
 * @version 1.2, 01/23/03
 * @author Scott Violet
 */
class LazyActionMap extends ActionMapUIResource {
    /**
     * This will either be an instanceof Loader, or a Class object
     * indicating we should use the static method loadActionMap
     * to load the actions.
     */
    private transient Object _loader;
    /**
     * Component passed to loadActionMap, may be null.
     */
    private transient JComponent _component;

    /**
     * Installs an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the <code>loader</code>
     * when necessary.
     * <p>
     * This should be used if the ActionMap needs to be specific to
     * the Component.
     *
     * @param c JComponent to install the AcitonMap on.
     * @param loader Object used to populate ActionMap when needed.
     */
    static void installLazyActionMap(JComponent c, Loader loader) {
        SwingUtilities.replaceUIActionMap(c, new LazyActionMap(loader, c));
    }

    /**
     * Installs an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p>
     * This should be used if the ActionMap can be shared.
     *
     * @param c JComponent to install the ActionMap on.
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *        existing map and what resulting Map will be registered on.
     */
    static void installLazyActionMap(JComponent c, Class loaderClass,
                                     String defaultsKey) {
        ActionMap map = (ActionMap)UIManager.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        SwingUtilities.replaceUIActionMap(c, map);
    }

    /**
     * Returns an ActionMap registered under the given name, creating
     * one as necessary.
     *
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *        existing map and what resulting Map will be registered on.
     */
    static ActionMap getActionMap(Class loaderClass, String defaultsKey) {
        ActionMap map = (ActionMap)UIManager.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        return map;
    }


    private LazyActionMap(Loader loader, JComponent c) {
        _loader = loader;
        _component = c;
    }

    private LazyActionMap(Class loader) {
        _loader = loader;
    }

    public void put(Object key, Action action) {
        loadIfNecessary();
        super.put(key, action);
    }

    public Action get(Object key) {
        loadIfNecessary();
        return super.get(key);
    }

    public void remove(Object key) {
        loadIfNecessary();
        super.remove(key);
    }

    public void clear() {
        loadIfNecessary();
        super.clear();
    }

    public Object[] keys() {
        loadIfNecessary();
        return super.keys();
    }

    public int size() {
        loadIfNecessary();
        return super.size();
    }

    public Object[] allKeys() {
        loadIfNecessary();
        return super.allKeys();
    }

    private void loadIfNecessary() {
        if (_loader != null) {
            Object loader = _loader;

            _loader = null;
            if (loader instanceof Loader) {
                ((Loader)loader).loadActionMap(_component, this);
            }
            else {
                Class klass = (Class)loader;
                try {
                    Method method = klass.getDeclaredMethod("loadActionMap",
                                      new Class[] { ActionMap.class });
                    method.invoke(klass, new Object[] { this });
                } catch (NoSuchMethodException nsme) {
                    assert false : "LazyActionMap unable to load actions " +
                        klass;
                } catch (IllegalAccessException iae) {
                    assert false : "LazyActionMap unable to load actions " +
                        iae;
                } catch (InvocationTargetException ite) {
                    assert false : "LazyActionMap unable to load actions " +
                        ite;
                } catch (IllegalArgumentException iae) {
                    assert false : "LazyActionMap unable to load actions " +
                        iae;
                }
            }
            _component = null;
        }
    }


    /**
     * Interface used to populate the ActionMap.
     */
    public interface Loader {
        public void loadActionMap(JComponent c, ActionMap map);
    }
}
