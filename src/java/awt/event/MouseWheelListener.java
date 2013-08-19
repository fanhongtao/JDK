/*
 * @(#)MouseWheelListener.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving mouse wheel events on a component.
 * (For clicks and other mouse events, use the MouseListener.  For mouse 
 * movement and drags, use the MouseMotionListener.)
 * <P>
 * The class that is interested in processing a mouse wheel event
 * implements this interface (and all the methods it contains).
 * <P>
 * The listener object created from that class is then registered with a
 * component using the component's <code>addMouseWheelListener</code> 
 * method. A mouse wheel event is generated when the mouse wheel is rotated.
 * When a mouse wheel event occurs, that object's <code>mouseWheelMoved</code>
 * method is invoked.
 *
 * @author Brent Christian
 * @version 1.3 01/23/03
 * @see MouseWheelEvent
 * @since 1.4
 */
public interface MouseWheelListener extends EventListener {

    /**
     * Invoked when the mouse wheel is rotated.
     * @see MouseWheelEvent
     */
    public void mouseWheelMoved(MouseWheelEvent e);
}
