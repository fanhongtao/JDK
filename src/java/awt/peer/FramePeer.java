/*
 * @(#)FramePeer.java	1.18 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.*;

public interface FramePeer extends WindowPeer {
    void setTitle(String title);
    void setIconImage(Image im);
    void setMenuBar(MenuBar mb);
    void setResizable(boolean resizeable);
}


