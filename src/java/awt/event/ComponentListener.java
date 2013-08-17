/*
 * @(#)ComponentListener.java	1.7 98/07/01
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
 * The listener interface for receiving component events.
 * Component events are provided for notification purposes ONLY;
 * The AWT will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program registers a ComponentListener or not.
 *
 * @version 1.7 07/01/98
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
