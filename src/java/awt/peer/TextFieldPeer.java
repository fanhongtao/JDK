/*
 * @(#)TextFieldPeer.java	1.8 98/07/01
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

public interface TextFieldPeer extends TextComponentPeer {
    void setEchoChar(char echoChar);
    Dimension getPreferredSize(int columns);
    Dimension getMinimumSize(int columns);

    /**
     * DEPRECATED:  Replaced by setEchoChar(char echoChar).
     */
    void setEchoCharacter(char c);

    /**
     * DEPRECATED:  Replaced by getPreferredSize(int).
     */
    Dimension preferredSize(int cols);

    /**
     * DEPRECATED:  Replaced by getMinimumSize(int).
     */
    Dimension minimumSize(int cols);
}
