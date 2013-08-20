/*
 * @(#)MotifMenuMouseMotionListener.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.motif;

import java.awt.event.*;
import javax.swing.MenuSelectionManager;

/**
 * A default MouseListener for menu elements
 *
 * @version 1.9 12/19/03
 * @author Arnaud Weber
 */
class MotifMenuMouseMotionListener implements MouseMotionListener {
    public void mouseDragged(MouseEvent e) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
    }

    public void mouseMoved(MouseEvent e) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
    }
}
