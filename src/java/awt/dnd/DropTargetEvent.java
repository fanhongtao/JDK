/*
 * @(#)DropTargetEvent.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 	1.18, 12/19/03
 * @since 1.2
 *
 */

public class DropTargetEvent extends java.util.EventObject {

    private static final long serialVersionUID = 2821229066521922993L;

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
     * The <code>DropTargetContext</code> associated with this
     * <code>DropTargetEvent</code>.
     *
     * @serial
     */
    protected DropTargetContext   context;
}
