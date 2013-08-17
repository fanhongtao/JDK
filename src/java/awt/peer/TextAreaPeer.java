/*
 * @(#)TextAreaPeer.java	1.9 98/07/01
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

public interface TextAreaPeer extends TextComponentPeer {
    void insert(String text, int pos);
    void replaceRange(String text, int start, int end);
    Dimension getPreferredSize(int rows, int columns);
    Dimension getMinimumSize(int rows, int columns);

    /**
     * DEPRECATED:  Replaced by insert(String, int).
     */
    void insertText(String txt, int pos);

    /**
     * DEPRECATED:  Replaced by ReplaceRange(String, int, int).
     */
    void replaceText(String txt, int start, int end);

    /**
     * DEPRECATED:  Replaced by getPreferredSize(int, int).
     */
    Dimension preferredSize(int rows, int cols);

    /**
     * DEPRECATED:  Replaced by getMinimumSize(int, int).
     */
    Dimension minimumSize(int rows, int cols);
}    
