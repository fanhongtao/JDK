/*
 * @(#)MouseMotionAdapter.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

/**
 * An abstract adapter class for receiving mouse motion events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Mouse motion events occur when a mouse is moved or dragged.
 * (Many such events will be generated in a normal program.
 * To track clicks and other mouse events, use the MouseAdapter.)
 * <P>
 * Extend this class to create a <code>MouseEvent</code> listener 
 * and override the methods for the events of interest. (If you implement the 
 * <code>MouseMotionListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <P>
 * Create a listener object using the extended class and then register it with 
 * a component using the component's <code>addMouseMotionListener</code> 
 * method. When the mouse is moved or dragged, the relevant method in the 
 * listener object is invoked and the <code>MouseEvent</code> is passed to it.
 *
 * @author Amy Fowler
 * @version 1.14 01/23/03
 *
 * @see MouseEvent
 * @see MouseMotionListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/mousemotionlistener.html">Tutorial: Writing a Mouse Motion Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @since 1.1
 */
public abstract class MouseMotionAdapter implements MouseMotionListener {
    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) {}

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e) {}
}
