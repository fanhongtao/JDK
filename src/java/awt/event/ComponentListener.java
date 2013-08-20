/*
 * @(#)ComponentListener.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving component events.
 * The class that is interested in processing a component event
 * either implements this interface (and all the methods it
 * contains) or extends the abstract <code>ComponentAdapter</code> class
 * (overriding only the methods of interest).
 * The listener object created from that class is then registered with a
 * component using the component's <code>addComponentListener</code> 
 * method. When the component's size, location, or visibility
 * changes, the relevant method in the listener object is invoked,
 * and the <code>ComponentEvent</code> is passed to it.
 * <P>
 * Component events are provided for notification purposes ONLY;
 * The AWT will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program registers a <code>ComponentListener</code> or not.
 *
 * @see ComponentAdapter
 * @see ComponentEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/componentlistener.html">Tutorial: Writing a Component Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @author Carl Quinn
 * @version 1.16 12/19/03
 * @since 1.1
 */
public interface ComponentListener extends EventListener {
    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e);

    /**
     * Invoked when the component's position changes.
     */    
    public void componentMoved(ComponentEvent e);

    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(ComponentEvent e);

    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e);
}
