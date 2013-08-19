/*
 * @(#)BluecurveColorType.java	1.2 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

/**
 * @version 1.2, 01/23/03
 * @author Scott Violet
 */
class BluecurveColorType extends GTKColorType {
    // Used for menuitems:
    // Outer is also used for the radio button selected dot
    static final ColorType OUTER = new BluecurveColorType("Outer");
    static final ColorType INNER_LEFT = new BluecurveColorType("Inner Left");
    static final ColorType INNER_RIGHT = new BluecurveColorType("Inner Right");
    static final ColorType TOP_GRADIENT = new BluecurveColorType("Bottom");
    static final ColorType BOTTOM_GRADIENT = new BluecurveColorType("Top");

    // Used by popupmenu
    // OUTER2 is also used for the scratches on split panes.
    static final ColorType OUTER2 = new BluecurveColorType("Outer2");
    static final ColorType INNER_RIGHT2 = new BluecurveColorType(
                                              "Inner Right2");

    // Used by buttons
    static final ColorType OUTER3 = new BluecurveColorType("Outer3");

    // Used by MenuBar
    static final ColorType OUTER4 = new BluecurveColorType("Outer4");

    // Used by arrows
    static final ColorType OUTER5 = new BluecurveColorType("Outer5");

    static final int MIN_ID;
    static final int MAX_ID;

    static {
        MIN_ID = OUTER.getID();
        MAX_ID = OUTER5.getID();
    }

    BluecurveColorType(String string) {
        super(string);
    }
}
