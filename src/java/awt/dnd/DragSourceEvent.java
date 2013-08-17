/*
 * @(#)DragSourceEvent.java	1.4 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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
 * <p>
 * base class for DragSourceDragEvent and DragSourceDropEvent
 * </p>
 *
 * @version 1.4
 * @since JDK1.2
 *
 */

public class DragSourceEvent extends EventObject {

    /**
     * construct a DragSourceEvent
     */

    public DragSourceEvent(DragSourceContext dsc) { super(dsc); }

    /**
     * @return the DragSourceContext that originated the event
     */

    public DragSourceContext getDragSourceContext() {
	return (DragSourceContext)getSource();
    }
}
