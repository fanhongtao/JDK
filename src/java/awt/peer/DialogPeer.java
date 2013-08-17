/*
 * @(#)DialogPeer.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.*;

public interface DialogPeer extends WindowPeer {
    void setTitle(String title);
    void setResizable(boolean resizeable);
}


