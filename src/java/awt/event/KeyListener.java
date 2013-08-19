/*
 * @(#)KeyListener.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving keyboard events (keystrokes).
 * The class that is interested in processing a keyboard event
 * either implements this interface (and all the methods it
 * contains) or extends the abstract <code>KeyAdapter</code> class
 * (overriding only the methods of interest).
 * <P>
 * The listener object created from that class is then registered with a
 * component using the component's <code>addKeyListener</code> 
 * method. A keyboard event is generated when a key is pressed, released,
 * or typed (pressedn and released). The relevant method in the listener 
 * object is then invoked, and the <code>KeyEvent</code> is passed to it.
 *
 * @author Carl Quinn
 * @version 1.16 01/23/03
 *
 * @see KeyAdapter
 * @see KeyEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/keylistener.html">Tutorial: Writing a Key Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @since 1.1
 */
public interface KeyListener extends EventListener {

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key typed event.
     */
    public void keyTyped(KeyEvent e);

    /**
     * Invoked when a key has been pressed. 
     * See the class description for {@link KeyEvent} for a definition of 
     * a key pressed event.
     */
    public void keyPressed(KeyEvent e);

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key released event.
     */
    public void keyReleased(KeyEvent e);
}
