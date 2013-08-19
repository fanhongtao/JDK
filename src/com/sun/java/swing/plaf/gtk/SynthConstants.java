/*
 * @(#)SynthConstants.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;

/**
 * Constants used by Synth.
 *
 * @version 1.5, 01/23/03
 */
interface SynthConstants {
    /**
     * Primary state indicating the component is enabled.
     */
    public static final int ENABLED = 1 << 0;
    /**
     * Primary state indicating the mouse is over the region.
     */
    public static final int MOUSE_OVER = 1 << 1;
    /**
     * Primary state indicating the region is in a pressed state. Pressed
     * does not necessarily mean the user has pressed the mouse button.
     */
    public static final int PRESSED = 1 << 2;
    /**
     * Primary state indicating the region is in a disabled state.
     */
    public static final int DISABLED = 1 << 3;

    /**
     * Indicates the region has focus.
     */
    public static final int FOCUSED = 1 << 8;
    /**
     * Indicates the region is selected.
     */
    public static final int SELECTED = 1 << 9;
    /**
     * Indicates the region is the default.
     */
    public static final int DEFAULT = 1 << 10;
}
