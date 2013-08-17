/*
 * @(#)ListPeer.java	1.11 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.Dimension;

public interface ListPeer extends ComponentPeer {
    int[] getSelectedIndexes();
    void add(String item, int index);
    void delItems(int start, int end);
    void removeAll();
    void select(int index);
    void deselect(int index);
    void makeVisible(int index);
    void setMultipleMode(boolean b);
    Dimension getPreferredSize(int rows);
    Dimension getMinimumSize(int rows);

    /**
     * DEPRECATED:  Replaced by add(String, int).
     */
    void addItem(String item, int index);

    /**
     * DEPRECATED:  Replaced by removeAll().
     */
    void clear();

    /**
     * DEPRECATED:  Replaced by setMultipleMode(boolean).
     */
    void setMultipleSelections(boolean v);

    /**
     * DEPRECATED:  Replaced by getPreferredSize(int).
     */
    Dimension preferredSize(int v);

    /**
     * DEPRECATED:  Replaced by getMinimumSize(int).
     */
    Dimension minimumSize(int v);
}    
