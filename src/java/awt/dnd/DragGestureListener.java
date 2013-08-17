/*
 * @(#)DragGestureListener.java	1.2 98/06/29
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



package java.awt.dnd;

import java.util.EventListener;

/**
 * <p>
 * This interface is sourced from a DragGestureRecognizer and is invoked
 * when an object of that (sub)class detects a Drag and Drop initiating
 * gesture.
 * </p>
 * <p>
 * The implementor of this interface is responsible for starting the drag
 * as a result of receiving such a notification.
 * </p>
 *
 * @see java.awt.dnd.DragGestureRecognizer
 * @see java.awt.dnd.DragGestureEvent
 * @see java.awt.dnd.DragSource
 */

 public interface DragGestureListener extends EventListener {

    /**
     * A DragGestureRecognizer has detected a platform-dependent Drag and
     * Drop action initiating gesture and is notifying this Listener
     * in order for it to initiate the action for the user.
     *
     * @param dge The DragGestureEvent describing the gesture that has just occurred
     */

     void dragGestureRecognized(DragGestureEvent dge);
}
