/*
 * @(#)SynthContext.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import java.util.*;

/**
 * An immutable transient object containing contextual information about
 * a Region. If you are passed a SynthContext you should not keep a reference
 * to it as it may be reset and change.
 *
 * @version 1.7, 01/23/03
 * @author Scott Violet
 */
class SynthContext {
    private static final Map contextMap;

    private JComponent component;
    private Region region;
    private SynthStyle style;
    private int state;


    static {
        contextMap = new HashMap();
    }


    static SynthContext getContext(Class type, JComponent component,
                                   Region region, SynthStyle style,
                                   int state) {
        SynthContext context = null;

        synchronized(contextMap) {
            java.util.List instances = (java.util.List)contextMap.get(type);

            if (instances != null) {
                int size = instances.size();

                if (size > 0) {
                    context = (SynthContext)instances.remove(size - 1);
                }
            }
        }
        if (context == null) {
            // PENDING: this needs to be investigated when running in
            // a sandbox. It is possible this will throw as the
            // constructor is package private.
            try {
                context = (SynthContext)type.newInstance();
            } catch (IllegalAccessException iae) {
                System.out.println("could not create: " + iae);
            } catch (InstantiationException ie) {
                System.out.println("ie: " + ie);
            }
        }
        context.reset(component, region, style, state);
        return context;
    }

    static void releaseContext(SynthContext context) {
        synchronized(contextMap) {
            java.util.List instances = (java.util.List)contextMap.get(
                                       context.getClass());

            if (instances == null) {
                instances = new ArrayList(5);
                contextMap.put(context.getClass(), instances);
            }
            instances.add(context);
        }
    }


    SynthContext() {
    }

    SynthContext(JComponent component, Region region, SynthStyle style,
                 int state) {
        reset(component, region, style, state);
    }


    /**
     * Returns the hosting component containg the region.
     *
     * @return Hosting Component 
     */
    public JComponent getComponent() {
        return component;
    }

    /**
     * Returns the Region identifying this state.
     *
     * @return Region of the hosting component
     */
    public Region getRegion() {
        return region;
    }

    /**
     * A convenience method for <code>getRegion().isSubregion()</code>.
     */
    boolean isSubregion() {
        return getRegion().isSubregion();
    }

    void setStyle(SynthStyle style) {
        this.style = style;
    }

    /**
     * Returns the style associated with this Region.
     *
     * @return SynthStyle associated with the region.
     */
    public SynthStyle getStyle() {
        return style;
    }

    void setComponentState(int state) {
        this.state = state;
    }

    /**
     * Returns the state of the widget, which is a bitmask of the
     * values defined in SynthUI. A region will at least be in one of
     * <code>ENABLED</code>, <code>MOUSE_OVER</code>, <code>PRESSED</code>
     * or <code>DISABLED</code>.
     *
     * @return State of Component
     */
    public int getComponentState() {
        return state;
    }

    /**
     * Resets the state of the Context.
     */
    void reset(JComponent component, Region region, SynthStyle style,
               int state) {
        this.component = component;
        this.region = region;
        this.style = style;
        this.state = state;
    }

    void dispose() {
        this.component = null;
        this.style = null;
        releaseContext(this);
    }
}
