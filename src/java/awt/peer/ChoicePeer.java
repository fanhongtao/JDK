/*
 * @(#)ChoicePeer.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.*;     

public interface ChoicePeer extends ComponentPeer {
    void add(String item, int index);
    void remove(int index);
    void select(int index);

    /*
     * DEPRECATED:  Replaced by add(String, int).
     */
    void addItem(String item, int index);
}
