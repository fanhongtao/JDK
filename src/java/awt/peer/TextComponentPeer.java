/*
 * @(#)TextComponentPeer.java	1.14 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.awt.peer;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface TextComponentPeer extends ComponentPeer {
    void setEditable(boolean editable);
    String getText();
    void setText(String l);
    int getSelectionStart();
    int getSelectionEnd();
    void select(int selStart, int selEnd);
    void setCaretPosition(int pos);
    int getCaretPosition();
    int getIndexAtPoint(int x, int y);
    Rectangle getCharacterBounds(int i);
    long filterEvents(long mask);
}
