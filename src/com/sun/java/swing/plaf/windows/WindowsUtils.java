/*
 * @(#)WindowsUtils.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

/**
 * This is a collection of utility methods needed by the Windows L&F
 *
 * @version 1.7 01/23/03
 * @author Brian Beck
 */

class WindowsUtils {
    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }
}
