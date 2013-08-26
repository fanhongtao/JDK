/*
 * @(#)KeyboardFocusManagerPeer.java	1.5 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
