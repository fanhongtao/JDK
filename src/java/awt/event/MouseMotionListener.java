/*
 * @(#)MouseMotionListener.java	1.6 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving mouse motion events on a component.
 * (For clicks and other mouse events, use the MouseListener.)
 * <P>
 * The class that is interested in processing a mouse motion event
 * either implements this interface (and all the methods it
 * contains) or extends the abstract <code>MouseMotionAdapter</code> class
 * (overriding only the methods of interest).
 * <P>
 * The listener object created from that class is then registered with a
 * component using the component's <code>addMouseMotionListener</code> 
 * method. A mouse motion event is generated when the mouse is moved
 * or dragged. (Many such events will be generated). When a mouse motion event
 * occurs, the relevant method in the listener object is invoked, and 
 * the <code>MouseEvent</code> is passed to it.
 *
 * @see MouseMotionAdapter
 * @see MouseEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/mousemotionlistener.html">Tutorial: Writing a Mouse Motion Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @version 1.6 09/21/98
 * @author Amy Fowler
 */
public interface MouseMotionListener extends EventListener {

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e);

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e);

}
