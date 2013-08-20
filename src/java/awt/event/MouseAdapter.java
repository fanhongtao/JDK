/*
 * @(#)MouseAdapter.java	1.17 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

/**
 * An abstract adapter class for receiving mouse events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Mouse events let you track when a mouse is pressed, released, clicked, 
 * when it enters a component, and when it exits.
 * (To track mouse moves and mouse drags, use the MouseMotionAdapter.)
 * <P>
 * Extend this class to create a <code>MouseEvent</code> listener 
 * and override the methods for the events of interest. (If you implement the 
 * <code>MouseListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <P>
 * Create a listener object using the extended class and then register it with 
 * a component using the component's <code>addMouseListener</code> 
 * method. When a mouse button is pressed, released, or clicked (pressed and
 * released), or when the mouse cursor enters or exits the component,
 * the relevant method in the listener object is invoked
 * and the <code>MouseEvent</code> is passed to it.
 *
 * @author Carl Quinn
 * @version 1.8 08/02/97
 *
 * @see MouseEvent 
 * @see MouseListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/mouselistener.html">Tutorial: Writing a Mouse Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @since 1.1
 */
public abstract class MouseAdapter implements MouseListener {
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {}
}
