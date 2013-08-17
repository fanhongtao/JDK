/*
 * @(#)WindowConstants.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;


/**
 * Constants used to control the window-closing operation.
 * For example, see {@link JFrame#setDefaultCloseOperation}
 *
 * @version 1.9 11/29/01
 * @author Amy Fowler
 */
public interface WindowConstants
{
    /**
     * The do-nothing default window close operation
     */
    public static final int DO_NOTHING_ON_CLOSE = 0;

    /**
     * The hide-window default window close operation
     */
    public static final int HIDE_ON_CLOSE = 1;

    /**
     * The dispose-window default window close operation
     */
    public static final int DISPOSE_ON_CLOSE = 2;

    /**
     * The exit application default window close operation. If a window
     * has this set as the close operation and is closed in an applet,
     * a SecurityException may be thrown. It is recommended you only use
     * this in an Application. <p>
     * When new API is allowed this will be added.
     */
    // static final int EXIT_ON_CLOSE = 3;
}
