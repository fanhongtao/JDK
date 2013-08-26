/*
 * @(#)WindowPeer.java	1.28 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface WindowPeer extends ContainerPeer {
    void toFront();
    void toBack();
    void setAlwaysOnTop(boolean alwaysOnTop);
    void updateFocusableWindowState();
    boolean requestWindowFocus();
    void setModalBlocked(Dialog blocker, boolean blocked);
    void updateMinimumSize();
    void updateIconImages();

    void setOpacity(float opacity);
    void setOpaque(boolean isOpaque);
    void updateWindow();

    void repositionSecurityWarning();
}

