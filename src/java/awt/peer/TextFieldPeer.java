/*
 * @(#)TextFieldPeer.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.awt.peer;

import java.awt.Dimension;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
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
