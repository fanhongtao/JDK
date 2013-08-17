/*
 * @(#)MotifMenuMouseMotionListener.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.java.swing.plaf.motif;

import java.awt.event.*;
import javax.swing.MenuSelectionManager;

/**
 * A default MouseListener for menu elements
 *
 * @version 1.4 08/26/98
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
