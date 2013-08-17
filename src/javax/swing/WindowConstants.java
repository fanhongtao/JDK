/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;


/**
 * Constants used to control the window-closing operation.
 * The <code>setDefaultCloseOperation</code> and 
 * <code>getDefaultCloseOperation</code> methods
 * provided by <code>JFrame</code>,
 * <code>JInternalFrame</code>, and
 * <code>JDialog</code>
 * use these constants.
 * For examples of setting the default window-closing operation, see 
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/frame.html#windowevents">Responding to Window-Closing Events</a>,
 * a section in <em>The Java Tutorial</em>.
 * 
 *
 * @version 1.14 02/06/02
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

}
