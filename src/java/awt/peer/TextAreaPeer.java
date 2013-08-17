/*
 * @(#)TextAreaPeer.java	1.8 96/11/23
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
