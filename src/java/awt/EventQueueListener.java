/*
 * @(#)EventQueueListener.java	1.3 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
