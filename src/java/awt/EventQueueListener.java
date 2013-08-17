/*
 * @(#)EventQueueListener.java	1.2 98/07/01
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

package java.awt;

import java.awt.event.*;
import java.util.EventListener;

/*
 * Based on new public 1.2 API, allows listeners
 * to be notified when event posted to event queue
 */
interface EventQueueListener extends EventListener {
    /*
     * Called when event is posted (via EventQueue.postEvent)
     * @param	event	Event that is about to be added to the queue
     */
    public void eventPosted(AWTEvent theEvent);
}
