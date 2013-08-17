/*
 * @(#)MouseInputAdapter.java	1.6 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.event;

import java.awt.event.MouseEvent;

/**
 * The adapter which receives mouse events and mouse motion events.
 * The methods in this class are empty;  this class is provided as a
 * convenience for easily creating listeners by extending this class
 * and overriding only the methods of interest.
 *
 * @version 1.6 08/26/98
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
