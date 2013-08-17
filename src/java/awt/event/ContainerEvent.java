/*
 * @(#)ContainerEvent.java	1.4 98/07/01
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

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Component;

/**
 * The class for container-level events.
 * These events are provided for notification purposes ONLY;
 * The AWT will automatically handle container add and remove
 * operations internally.
 *
 * @see ContainerListener
 *
 * @version 1.4 07/01/98
 * @author Tim Prinzing
 * @author Amy Fowler
 */
public class ContainerEvent extends ComponentEvent {

    /**
     * Marks the first integer id for the range of container event ids.
     */
    public static final int CONTAINER_FIRST		= 300;

    /**
     * Marks the last integer id for the range of container event ids.
     */
    public static final int CONTAINER_LAST		= 301;

   /**
     * The component moved event type.
     */
    public static final int COMPONENT_ADDED	= CONTAINER_FIRST;

    /**
     * The component resized event type.
     */
    public static final int COMPONENT_REMOVED = 1 + CONTAINER_FIRST;

    Component child;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -4114942250539772041L;

    /**
     * Constructs a ContainerEvent object with the specified source
     * container, type, and child which is being added or removed. 
     * @param source the container where the event originated
     * @id the event type
     * @child the child component
     */
    public ContainerEvent(Component source, int id, Component child) {
        super(source, id);
        this.child = child;
    }

    /**
     * Returns the container where this event originated.
     */
    public Container getContainer() {
        return (Container)source; // cast should always be OK, type was checked in constructor
    }

    /**
     * Returns the child component that was added or removed in
     * this event.
     */
    public Component getChild() {
        return child;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case COMPONENT_ADDED:
              typeStr = "COMPONENT_ADDED";
              break;
          case COMPONENT_REMOVED:
              typeStr = "COMPONENT_REMOVED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + ",child="+child.getName();
    }
}
