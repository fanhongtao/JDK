/*
 * @(#)DropTargetEvent.java	1.7 98/04/14
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

import java.util.EventObject;

import java.awt.Component;
import java.awt.Point;

import java.awt.datatransfer.DataFlavor;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;

/**
 * <p>
 * The DropTargetEvent is the base class for both the DropTargetDragEvent and
 * The DropTargetDropEvent. It encapsulates the current state of the Drag and
 * Drop operations, in particular the current DropTargetContext.
 * </p>
 *
 * @version 1.7
 * @since JDK1.2
 *
 */

public class DropTargetEvent extends java.util.EventObject {

    /**
     * Construct a DropTargetEvent
     */

    public DropTargetEvent(DropTargetContext dtc) {
	super(dtc.getDropTarget());

	context  = dtc;
    }

    /**
     * @return the DropTargetContext
     */

    public DropTargetContext getDropTargetContext() {
	return context;
    }

    protected DropTargetContext   context;
}
