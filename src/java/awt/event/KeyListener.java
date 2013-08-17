/*
 * @(#)KeyListener.java	1.10 98/09/21
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
 * @see KeyAdapter
 * @see KeyEvent
 * @see <a href="http://java.sun.com/docs/books/tutorial/post1.0/ui/keylistener.html">Tutorial: Writing a Key Listener</a>
 * @see <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">Reference: The Java Class Libraries (update file)</a>
 *
 * @version 1.10 09/21/98
 * @author Carl Quinn
 */
public interface KeyListener extends EventListener {

    /**
     * Invoked when a key has been typed.
     * This event occurs when a key press is followed by a key release.
     */
    public void keyTyped(KeyEvent e);

    /**
     * Invoked when a key has been pressed.
     */
    public void keyPressed(KeyEvent e);

    /**
     * Invoked when a key has been released.
     */
    public void keyReleased(KeyEvent e);
}
