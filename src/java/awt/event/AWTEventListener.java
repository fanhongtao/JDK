/*
 * @(#)AWTEventListener.java	1.2 98/07/27
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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
import java.awt.AWTEvent;

/**
 * The listener interface for receiving notification of events
 * dispatched to objects that are instances of Component or
 * MenuComponent or their subclasses.  Unlike the other EventListeners
 * in this package, AWTEventListeners passively observe events
 * being dispatched in the AWT, system-wide.  Most applications
 * should never use this class; applications which might use
 * AWTEventListeners include event recorders for automated testing,
 * and facilities such as the Java Accessibility package.
 * <p>
 * The class that is interested in monitoring AWT events
 * implements this interface, and the object created with that
 * class is registered with the Toolkit, using the Toolkit's
 * <code>addAWTEventListener</code> method.  When an event is
 * dispatched anywhere in the AWT, that object's
 * <code>eventDispatcheded</code> method is invoked.
 *
 * @see java.awt.AWTEvent
 * @see java.awt.Toolkit#addEventListener
 * @see java.awt.Toolkit#removeEventListener
 *
 * @version 1.2, 07/27/98
 * @author Fred Ecks
 */
public interface AWTEventListener extends EventListener {

    /**
     * Invoked when an event is dispatched in the AWT.
     */
    public void eventDispatched(AWTEvent event);

}
