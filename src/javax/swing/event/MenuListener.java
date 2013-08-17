/*
 * @(#)MenuListener.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.event;


import java.util.EventListener;
 
 
/**
 * Defines a listener for menu events.
 *
 * @version 1.6 08/26/98
 * @author Georges Saab
 */
public interface MenuListener extends EventListener {
    /**
     * Invoked when a menu item is selected.
     *
     * @param e  a MenuEvent object
     */
    void menuSelected(MenuEvent e);
    /**
     * Invoked when the menu selection changes.
     *
     * @param e  a MenuEvent object
     */
    void menuDeselected(MenuEvent e);
    /**
     * Invoked when the menu selection is canceled.
     *
     * @param e  a MenuEvent object
     */
    void menuCanceled(MenuEvent e);
}

