/*
 * @(#)DragGestureListener.java	1.6 99/04/22
 *
 * Copyright 1998, 1999 by Sun Microsystems, Inc.,
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
 * This interface is sourced from a <code>DragGestureRecognizer</code> 
 * and is invoked
 * when an object of that (sub)class detects a drag initiating
 * gesture.
 * <p>
 * The implementor of this interface is responsible for starting the drag
 * as a result of receiving such notification.
 *
 * @see java.awt.dnd.DragGestureRecognizer
 * @see java.awt.dnd.DragGestureEvent
 * @see java.awt.dnd.DragSource
 */

 public interface DragGestureListener extends EventListener {

    /**
     * A <code>DragGestureRecognizer</code> has detected 
     * a platform-dependent drag initiating gesture and 
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param dge the <code>DragGestureEvent</code> describing 
     * the gesture that has just occurred
     */

     void dragGestureRecognized(DragGestureEvent dge);
}
