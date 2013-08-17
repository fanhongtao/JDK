/*
 * @(#)WindowsUtils.java	1.2 00/01/12
 *
 * Copyright 1999, 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

/**
 * This is a collection of utility methods needed by the Windows L&F
 *
 * @version 1.2 02/11/99
 * @author Brian Beck
 */

class WindowsUtils {
    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( Component c ) {
        /*if[JDK1.2]
        return c.getComponentOrientation().isLeftToRight();
        else[JDK1.2]*/
        return true;
        /*end[JDK1.2]*/
    }
}
