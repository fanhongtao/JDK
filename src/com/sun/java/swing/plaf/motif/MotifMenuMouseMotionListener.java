/*
 * @(#)MotifMenuMouseMotionListener.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.motif;

import java.awt.event.*;
import javax.swing.MenuSelectionManager;

/**
 * A default MouseListener for menu elements
 *
 * @version 1.10 11/17/05
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
