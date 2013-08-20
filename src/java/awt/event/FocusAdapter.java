/*
 * @(#)FocusAdapter.java	1.16 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

/**
 * An abstract adapter class for receiving keyboard focus events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Extend this class to create a <code>FocusEvent</code> listener 
 * and override the methods for the events of interest. (If you implement the 
 * <code>FocusListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <P>
 * Create a listener object using the extended class and then register it with 
 * a component using the component's <code>addFocusListener</code> 
 * method. When the component gains or loses the keyboard focus,
 * the relevant method in the listener object is invoked,
 * and the <code>FocusEvent</code> is passed to it.
 *
 * @see FocusEvent
 * @see FocusListener
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/focuslistener.html">Tutorial: Writing a Focus Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @author Carl Quinn
 * @version 1.16 12/19/03
 * @since 1.1
 */
public abstract class FocusAdapter implements FocusListener {
    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent e) {}

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent e) {}
}
