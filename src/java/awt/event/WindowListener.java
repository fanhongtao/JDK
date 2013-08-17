/*
 * @(#)WindowListener.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving window events.
 *
 * @version 1.8 12/10/01
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
