/*
 * @(#)ComponentAdapter.java	1.10 98/09/21
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

/**
 * An abstract adapter class for receiving component events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Extend this class to create a <code>ComponentEvent</code> listener 
 * and override the methods for the events of interest. (If you implement the 
 * <code>ComponentListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <P>
 * Create a listener object using your class and then register it with a
 * component using the component's <code>addComponentListener</code> 
 * method. When the component's size, location, or visibility
 * changes, the relevant method in the listener object is invoked,
 * and the <code>ComponentEvent</code> is passed to it.
 *
 * @see ComponentEvent
 * @see ComponentListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/componentlistener.html">Tutorial: Writing a Component Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 * 
 * @version 1.10 09/21/98
 * @author Carl Quinn
 */
public abstract class ComponentAdapter implements ComponentListener {
    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e) {}
    
    /**
     * Invoked when the component's position changes.
     */    
    public void componentMoved(ComponentEvent e) {}
    
    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(ComponentEvent e) {}
    
    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e) {}
}
