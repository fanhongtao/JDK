/*
 * @(#)ListPeer.java	1.10 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
