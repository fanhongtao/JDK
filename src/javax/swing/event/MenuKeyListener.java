/*
 * @(#)MenuKeyListener.java	1.11 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;


import java.util.EventListener;


/**
 * MenuKeyListener
 *
 * @version 1.11 03/23/10
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

