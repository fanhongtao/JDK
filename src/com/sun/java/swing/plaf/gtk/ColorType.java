/*
 * @(#)ColorType.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

/**
 * A typesafe enumeration of colors that can be fetched from a style.
 *
 * @version 1.9, 01/23/03
 * @author Scott Violet
 */
class ColorType {
    /**
     * ColorType for the foreground of a region.
     */
    public static final ColorType FOREGROUND = new ColorType("Foreground");

    /**
     * ColorType for the background of a region.
     */
    public static final ColorType BACKGROUND = new ColorType("Background");

    /**
     * ColorType for the foreground of a region.
     */
    public static final ColorType TEXT_FOREGROUND = new ColorType(
                                       "TextForeground");

    /**
     * ColorType for the background of a region.
     */
    public static final ColorType TEXT_BACKGROUND =new ColorType(
                                       "TextBackground");

    /**
     * ColorType for the focus.
     */
    public static final ColorType FOCUS = new ColorType("Focus");

    public static final int MAX_COUNT;

    private static int nextID;

    private String description;
    private int index;

    static {
        MAX_COUNT = Math.max(FOREGROUND.getID(), Math.max(
                                 BACKGROUND.getID(), FOCUS.getID())) + 1;
    }

    /**
     * Creates a new ColorType with the specified description.
     *
     * @param description String description of the ColorType.
     */
    protected ColorType(String description) {
        this.description = description;
        synchronized(ColorType.class) {
            this.index = nextID++;
        }
    }

    /**
     * Returns a unique id, as an integer, for this ColorType.
     *
     * @return a unique id, as an integer, for this ColorType.
     */
    public final int getID() {
        return index;
    }

    public String toString() {
        return description;
    }
}
