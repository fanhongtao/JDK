/*
 * @(#)MotifMenuMouseListener.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.motif;

import java.awt.event.*;
import javax.swing.MenuSelectionManager;

/**
 * A default MouseListener for menu elements
 *
 * @version 1.8 01/23/03
 * @author Arnaud Weber
 */
class MotifMenuMouseListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
    }
    public void mouseReleased(MouseEvent e) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
    }
    public void mouseEntered(MouseEvent e) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
    }
    public void mouseExited(MouseEvent e) {
        MenuSelectionManager.defaultManager().processMouseEvent(e);
    }
}
