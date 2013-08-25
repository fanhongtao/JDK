/*
 * @(#)WindowsMenuItemUIAccessor.java	1.1 06/12/14
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JMenuItem;

import com.sun.java.swing.plaf.windows.TMSchema.Part;
import com.sun.java.swing.plaf.windows.TMSchema.State;

/**
 * Accessor interface for WindowsMenuItemUI to allow for "multiple implementation 
 * inheritance".  
 * 
 * @version 1.1 12/14/06
 * @author Igor Kushnirskiy
 */
interface WindowsMenuItemUIAccessor {
    JMenuItem getMenuItem();
    State getState(JMenuItem menuItem);
    Part getPart(JMenuItem menuItem);
}
