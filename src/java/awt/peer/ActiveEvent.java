/*
 * @(#)ActiveEvent.java	1.2 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * @version 1.2 07/01/98
 */
public interface ActiveEvent {

    /**
     * Dispatch the event to it's target, listeners of the events source, 
     * or do whatever it is this event is supposed to do.
     */
    public void dispatch();
}
