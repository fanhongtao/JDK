/*
 * @(#)ContainerListener.java	1.3 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving container events.
 * Container events are provided for notification purposes ONLY;
 * The AWT will automatically handle add and remove operations
 * internally.
 *
 * @version 1.3 12/10/01
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
