/*
 * @(#)ContainerListener.java	1.2 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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
 * The listener interface for receiving container events.
 * Container events are provided for notification purposes ONLY;
 * The AWT will automatically handle add and remove operations
 * internally.
 *
 * @version 1.2 07/01/98
 * @author Tim Prinzing
 * @author Amy Fowler
 */
public interface ContainerListener extends EventListener {
    /**
     * Invoked when a component has been added to the container.
     */
    public void componentAdded(ContainerEvent e);

    /**
     * Invoked when a component has been removed from the container.
     */    
    public void componentRemoved(ContainerEvent e);

}
