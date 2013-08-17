/*
 * @(#)ComponentListener.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving component events.
 * Component events are provided for notification purposes ONLY;
 * The AWT will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program registers a ComponentListener or not.
 *
 * @version 1.8 12/10/01
 * @author Carl Quinn
 */
public interface ComponentListener extends EventListener {
    /**
     * Invoked when component has been resized.
     */
    public void componentResized(ComponentEvent e);

    /**
     * Invoked when component has been moved.
     */    
    public void componentMoved(ComponentEvent e);

    /**
     * Invoked when component has been shown.
     */
    public void componentShown(ComponentEvent e);

    /**
     * Invoked when component has been hidden.
     */
    public void componentHidden(ComponentEvent e);
}
