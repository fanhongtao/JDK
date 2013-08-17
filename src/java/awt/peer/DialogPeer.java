/*
 * @(#)DialogPeer.java	1.8 00/03/28
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 * 
 */

package java.awt.peer;

import java.awt.*;

public interface DialogPeer extends WindowPeer {
    void setTitle(String title);
    void setResizable(boolean resizeable);
}


