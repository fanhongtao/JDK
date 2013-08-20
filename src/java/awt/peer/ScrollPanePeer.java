/*
 * @(#)ScrollPanePeer.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.Point;
import java.awt.Adjustable;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface ScrollPanePeer extends ContainerPeer {
    int getHScrollbarHeight();
    int getVScrollbarWidth();
    void setScrollPosition(int x, int y);
    void childResized(int w, int h);
    void setUnitIncrement(Adjustable adj, int u);
    void setValue(Adjustable adj, int v);
}
