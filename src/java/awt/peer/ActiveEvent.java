/*
 * @(#)ActiveEvent.java	1.3 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

/**
 * An interface for events that know how dispatch themselves.
 * By implementing this interface an event can be placed upon the event 
 * queue and this method will be called to dispatch the event.  This allows
 * objects that are not components to arrange for behavior to occur on
 * a different thread from the current thread. 
 *
 * Peer implementations can use this facility to avoid making upcalls 
 * that could potentially cause a deadlock.
 *
 * @author  Timothy Prinzing
 * @version 1.3 12/10/01
 */
public interface ActiveEvent {

    /**
     * Dispatch the event to it's target, listeners of the events source, 
     * or do whatever it is this event is supposed to do.
     */
    public void dispatch();
}
