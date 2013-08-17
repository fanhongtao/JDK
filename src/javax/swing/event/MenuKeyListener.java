/*
 * @(#)MenuKeyListener.java	1.6 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.event;


import java.util.EventListener;


/**
 * MenuKeyListener
 *
 * @version 1.6 02/02/00
 * @author Georges Saab
 */
public interface MenuKeyListener extends EventListener {
    /**
     * Invoked when a key has been typed.
     * This event occurs when a key press is followed by a key release.
     */
    void menuKeyTyped(MenuKeyEvent e);

    /**
     * Invoked when a key has been pressed.
     */
    void menuKeyPressed(MenuKeyEvent e);

    /**
     * Invoked when a key has been released.
     */
    void menuKeyReleased(MenuKeyEvent e);
}

