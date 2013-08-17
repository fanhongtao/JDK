/*
 * @(#)ListPeer.java	1.9 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
