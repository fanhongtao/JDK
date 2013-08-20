/*
 * @(#)MouseInputAdapter.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.awt.event.MouseEvent;

/**
 * The adapter which receives mouse events and mouse motion events.
 * The methods in this class are empty;  this class is provided as a
 * convenience for easily creating listeners by extending this class
 * and overriding only the methods of interest.
 *
 * @version 1.12 12/19/03
 * @author Philip Milne
 */
public abstract class MouseInputAdapter implements MouseInputListener {

// The MouseListener methods

    // implements java.awt.event.MouseListener
    public void mouseClicked(MouseEvent e) {}
    // implements java.awt.event.MouseListener
    public void mousePressed(MouseEvent e) {}
    // implements java.awt.event.MouseListener
    public void mouseReleased(MouseEvent e) {}
    // implements java.awt.event.MouseListener
    public void mouseEntered(MouseEvent e) {}
    // implements java.awt.event.MouseListener
    public void mouseExited(MouseEvent e) {}

// The MouseMotionListener methods

    // implements java.awt.event.MouseMotionListener
    public void mouseDragged(MouseEvent e) {}
    // implements java.awt.event.MouseMotionListener
    public void mouseMoved(MouseEvent e) {}
}
