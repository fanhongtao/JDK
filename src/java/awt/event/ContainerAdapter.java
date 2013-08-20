/*
 * @(#)ContainerAdapter.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

/**
 * An abstract adapter class for receiving container events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Extend this class to create a <code>ContainerEvent</code> listener 
 * and override the methods for the events of interest. (If you implement the 
 * <code>ContainerListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <P>
 * Create a listener object using the extended class and then register it with 
 * a component using the component's <code>addContainerListener</code> 
 * method. When the container's contents change because a component has
 * been added or removed, the relevant method in the listener object is invoked,
 * and the <code>ContainerEvent</code> is passed to it.
 *
 * @see ContainerEvent
 * @see ContainerListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/containerlistener.html">Tutorial: Writing a Container Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @author Amy Fowler
 * @version 1.12 12/19/03
 * @since 1.1
 */
public abstract class ContainerAdapter implements ContainerListener {
    /**
     * Invoked when a component has been added to the container.
     */
    public void componentAdded(ContainerEvent e) {}

    /**
     * Invoked when a component has been removed from the container.
     */    
    public void componentRemoved(ContainerEvent e) {}
}
