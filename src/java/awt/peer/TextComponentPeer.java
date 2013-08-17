/*
 * @(#)TextComponentPeer.java	1.5 98/07/01
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
