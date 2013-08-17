/*
 * @(#)MenuPeer.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.MenuItem;

public interface MenuPeer extends MenuItemPeer {
    void addSeparator();
    void addItem(MenuItem item);
    void delItem(int index);
}
