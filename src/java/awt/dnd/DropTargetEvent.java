/*
 * @(#)DropTargetEvent.java	1.10 99/04/22
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

import java.util.EventObject;

import java.awt.Component;
import java.awt.Point;

import java.awt.datatransfer.DataFlavor;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;

/**
 * The <code>DropTargetEvent</code> is the base 
 * class for both the <code>DropTargetDragEvent</code>
 * and the <code>DropTargetDropEvent</code>. 
 * It encapsulates the current state of the Drag and
 * Drop operations, in particular the current 
 * <code>DropTargetContext</code>.
 *
 * @version 1.10
 * @since JDK1.2
 *
 */

public class DropTargetEvent extends java.util.EventObject {

    /**
     * Construct a <code>DropTargetEvent</code> with 
     * a specified <code>DropTargetContext</code>.
     * <P>
     * @param dtc the <code>DropTargetContext</code>
     */

    public DropTargetEvent(DropTargetContext dtc) {
	super(dtc.getDropTarget());

	context  = dtc;
    }

    /**
     * This method returns the <code>DropTargetContext</code>
     * associated with this <code>DropTargetEvent</code>.
     * <P>
     * @return the <code>DropTargetContext</code>
     */

    public DropTargetContext getDropTargetContext() {
	return context;
    }

    /**
     * The <code>DropTargetConext</code> associated with this
     * <code>DropTargetEvent</code>.
     */
    protected DropTargetContext   context;
}
