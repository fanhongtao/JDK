/*
 * @(#)MenuItemPeer.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

public interface MenuItemPeer extends MenuComponentPeer {
    void setLabel(String label);
    void setEnabled(boolean b);

    /**
     * DEPRECATED:  Replaced by setEnabled(boolean).
     */
    void enable();

    /**
     * DEPRECATED:  Replaced by setEnabled(boolean).
     */
    void disable();
}

