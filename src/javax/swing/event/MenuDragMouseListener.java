/*
 * @(#)MenuDragMouseListener.java	1.8 00/02/02
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
 * Defines a menu mouse-drag listener.
 *
 * @version 1.8 02/02/00
 * @author Georges Saab
 */
public interface MenuDragMouseListener extends EventListener {
    /**
     * Invoked when the dragged mouse has entered a menu component's 
     * display area.
     *
     * @param e  a MenuDragMouseEvent object
     */
    void menuDragMouseEntered(MenuDragMouseEvent e);
    /**
     * Invoked when the dragged mouse has left a menu component's 
     * display area.
     *
     * @param e  a MenuDragMouseEvent object
     */
    void menuDragMouseExited(MenuDragMouseEvent e);
    /**
     * Invoked when the mouse is being dragged in a menu component's 
     * display area.
     *
     * @param e  a MenuDragMouseEvent object
     */
    void menuDragMouseDragged(MenuDragMouseEvent e);
    /**
     * Invoked when a dragged mouse is release in a menu component's 
     * display area.
     *
     * @param e  a MenuDragMouseEvent object
     */
    void menuDragMouseReleased(MenuDragMouseEvent e);
}

