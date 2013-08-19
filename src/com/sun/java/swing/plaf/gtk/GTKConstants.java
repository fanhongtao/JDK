/*
 * @(#)GTKConstants.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

/**
 * @version 1.11, 01/23/03
 * @author Scott Violet
 */
interface GTKConstants {

    /**
     * Used to indicate a constant is not defined.
     */
    public static final int UNDEFINED = -100;


    public static final int SHADOW_IN = 0;
    public static final int SHADOW_OUT = 1;

    // These values are not respected currently,
    // but have been included for completeness
    public static final int SHADOW_ETCHED_IN = 2;
    public static final int SHADOW_ETCHED_OUT = 3;
    public static final int SHADOW_NONE = 4;


    public static final int EXPANDER_COLLAPSED = 0;
    public static final int EXPANDER_EXPANDED = 1;


    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;


    public static final int ARROW_UP = 100;
    public static final int ARROW_DOWN = 101;
    public static final int ARROW_LEFT = 102;
    public static final int ARROW_RIGHT = 103;


    public static final int LTR = 0;
    public static final int RTL = 1;


    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

}
