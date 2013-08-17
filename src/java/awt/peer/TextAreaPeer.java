/*
 * @(#)TextAreaPeer.java	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
