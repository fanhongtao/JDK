/*
 * @(#)InternalFrameAdapter.java	1.5 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.event;

/**
 * An abstract adapter class for receiving internal frame events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects, and is functionally 
 * equivalent to the WindowAdapter class in the AWT.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/components/windowlistener.html">Writing a Window Listener</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a> and
 * <a href="http://www.awl.com/cp/javaseries/jcl1_2.html">The Java Class Libraries (update)</a>
 *
 * @see InternalFrameEvent
 * @see InternalFrameListener
 * @see java.awt.event.WindowListener
 *
 * @version 1.5 08/26/98
 * @author Thomas Ball
 */
public abstract class InternalFrameAdapter implements InternalFrameListener {
    /**
     * Invoked when an internal frame has been opened.
     */
    public void internalFrameOpened(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void internalFrameClosing(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame has been closed.
     */
    public void internalFrameClosed(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is iconified.
     */
    public void internalFrameIconified(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is de-iconified.
     */
    public void internalFrameDeiconified(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is activated.
     */
    public void internalFrameActivated(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is de-activated.
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {}
}
