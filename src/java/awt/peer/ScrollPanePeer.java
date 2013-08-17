/*
 * @(#)ScrollPanePeer.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.Point;
import java.awt.Adjustable;

public interface ScrollPanePeer extends ContainerPeer {
    int getHScrollbarHeight();
    int getVScrollbarWidth();
    void setScrollPosition(int x, int y);
    void childResized(int w, int h);
    void setUnitIncrement(Adjustable adj, int u);
    void setValue(Adjustable adj, int v);
}
