/*
 * @(#)DragSourceEvent.java	1.9 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.dnd;

import java.awt.dnd.DragSourceContext;

import java.util.EventObject;

/**
 * This class is the base class for 
 * <code>DragSourceDragEvent</code> and 
 * <code>DragSourceDropEvent</code>.
 * @since JDK1.2
 *
 */

public class DragSourceEvent extends EventObject {

    /**
     * Construct a <code>DragSourceEvent</code>
     * given a specified <code>DragSourceContext</code>.
     * <P>
     * @param dsc the <code>DragSourceContext</code>
     */

    public DragSourceEvent(DragSourceContext dsc) { super(dsc); }

    /**
     * This method returns the <code>DragSourceContext</code> that 
     * originated the event.
     * <P>
     * @return the <code>DragSourceContext</code> that originated the event
     */

    public DragSourceContext getDragSourceContext() {
	return (DragSourceContext)getSource();
    }
}

