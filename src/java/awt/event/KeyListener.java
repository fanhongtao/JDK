/*
 * @(#)KeyListener.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving keyboard events.
 *
 * @version 1.8 12/10/01
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
