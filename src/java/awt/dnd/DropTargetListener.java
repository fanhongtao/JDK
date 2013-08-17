/*
 * @(#)DropTargetListener.java	1.5 98/03/18
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

import java.util.EventListener;

import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

/**
 * <p>
 * The DropTargetListener interface is the callback interface used by the
 * DropTarget class to provide notification of DnD operations that involve
 * the subject DropTarget.
 * </p>
 *
 * @version 1.5
 * @since JDK1.2
 *
 */

public interface DropTargetListener extends EventListener {

    /**
     * a Drag operation has encountered the DropTarget
     */

    void dragEnter(DropTargetDragEvent dtde);

    /**
     * a Drag operation is ongoing on the DropTarget
     */

    void dragOver(DropTargetDragEvent dtde);

    /**
     * The user as modified the current drop gesture
     */

    void dropActionChanged(DropTargetDragEvent dtde);

    /**
     * The Drag operation has departed the DropTarget without dropping.
     */

    void dragExit(DropTargetEvent dte);

    /**
     * The Drag operation has terminated with a Drop on this DropTarget
     */

    void drop(DropTargetDropEvent dtde);
}
