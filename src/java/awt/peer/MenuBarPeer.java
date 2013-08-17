/*
 * @(#)MenuBarPeer.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.Menu;

public interface MenuBarPeer extends MenuComponentPeer {
    void addMenu(Menu m);
    void delMenu(int index);
    void addHelpMenu(Menu m);
}
