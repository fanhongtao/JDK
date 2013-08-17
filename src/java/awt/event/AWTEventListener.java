/*
 * @(#)AWTEventListener.java	1.3 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.3, 11/29/01
 * @author Fred Ecks
 */
public interface AWTEventListener extends EventListener {

    /**
     * Invoked when an event is dispatched in the AWT.
     */
    public void eventDispatched(AWTEvent event);

}
