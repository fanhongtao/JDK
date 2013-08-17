/*
 * @(#)PrintEvent.java	1.2 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.print.event;

/**
 *
 * Class PrintEvent is the super class of all Print Service API events.
 */

public class PrintEvent extends java.util.EventObject {

    /**
     * Constructs a PrintEvent object.
     * @param source is the source of the event
     */
    public PrintEvent (Object source) {
        super(source);
    }

    /**
     * @return a message describing the event
     */
    public String toString() {
	return ("PrintEvent on " + getSource().toString());
    }
    
}
