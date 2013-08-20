/*
 * @(#)FramePeer.java	1.28 04/06/08
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.*;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface FramePeer extends WindowPeer {
    void setTitle(String title);
    void setIconImage(Image im);
    void setMenuBar(MenuBar mb);
    void setResizable(boolean resizeable);
    void setState(int state);
    int  getState();
    void setMaximizedBounds(Rectangle bounds); // XXX
    void setBoundsPrivate(int x, int y, int width, int height);
}
