/*
 * @(#)TextComponentPeer.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

public interface TextComponentPeer extends ComponentPeer {
    void setEditable(boolean editable);
    String getText();
    void setText(String l);
    int getSelectionStart();
    int getSelectionEnd();
    void select(int selStart, int selEnd);
    void setCaretPosition(int pos);
    int getCaretPosition();
}
