/*
 * @(#)WindowListener.java	1.7 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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
 * The listener interface for receiving window events.
 *
 * @version 1.7 07/01/98
 * @author Carl Quinn
 */
public interface WindowListener extends EventListener {
    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened(WindowEvent e);

    /**
     * Invoked when a window is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void windowClosing(WindowEvent e);

    /**
     * Invoked when a window has been closed.
     */
    public void windowClosed(WindowEvent e);

    /**
     * Invoked when a window is iconified.
     */
    public void windowIconified(WindowEvent e);

    /**
     * Invoked when a window is de-iconified.
     */
    public void windowDeiconified(WindowEvent e);

    /**
     * Invoked when a window is activated.
     */
    public void windowActivated(WindowEvent e);

    /**
     * Invoked when a window is de-activated.
     */
    public void windowDeactivated(WindowEvent e);
}
