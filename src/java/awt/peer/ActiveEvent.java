/*
 * @(#)ActiveEvent.java	1.1 97/02/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 * @version 1.1 02/27/97
 */
public interface ActiveEvent {

    /**
     * Dispatch the event to it's target, listeners of the events source, 
     * or do whatever it is this event is supposed to do.
     */
    public void dispatch();
}
