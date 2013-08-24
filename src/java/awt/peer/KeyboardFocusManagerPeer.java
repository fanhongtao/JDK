/*
 * @(#)KeyboardFocusManagerPeer.java	1.4 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.Component;
import java.awt.Window;

public interface KeyboardFocusManagerPeer {
    void setCurrentFocusedWindow(Window win);
    Window getCurrentFocusedWindow();
    
    void setCurrentFocusOwner(Component comp);
    Component getCurrentFocusOwner();

    void clearGlobalFocusOwner(Window activeWindow);
}
