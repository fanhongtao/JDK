/*
 * @(#)DragSourceListener.java	1.5 98/03/18
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

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;

/**
 * <p>
 * The DragSourceListener defines the event interface for originators of
 * Drag and Drop operations to track the state of the users gesture to
 * provide the appropriate feedback to the user.
 * </p>
 *
 * @version 1.5
 * @since JDK1.2
 *
 */

public interface DragSourceListener extends EventListener {

    /**
     * as the hotspot enters a platform dependent drop site
     */

    void dragEnter(DragSourceDragEvent dsde);

    /**
     * as the hotspot moves over a platform dependent drop site
     */

    void dragOver(DragSourceDragEvent dsde);

    /**
     * the user has modified the drop gesture
     */

    void dropActionChanged(DragSourceDragEvent dsde);

    /**
     * as the hotspot exits a platform dependent drop site
     */

    void dragExit(DragSourceEvent dse);

    /**
     * as the operation completes
     */

    void dragDropEnd(DragSourceDropEvent dsde);
}
