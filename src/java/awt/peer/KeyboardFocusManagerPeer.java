/*
 * @(#)KeyboardFocusManagerPeer.java	1.3 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
