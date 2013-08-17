/*
 * @(#)MenuListener.java	1.6 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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

