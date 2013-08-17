/*
 * @(#)Autoscroll.java	1.3 98/03/31
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

import java.awt.Insets;
import java.awt.Point;

/**
 * <p>
 * During DnD operations it is possible that a user may wish to drop the 
 * subject of the operation on a region of a scrollable GUI control that is
 * not currently visible to the user.
 * </p>
 * <p>
 * In such situations it is desirable that the GUI control detect this
 * and institute a scroll operation in order to make obscured region(s)
 * visible to the user. This feature is known as autoscrolling.
 * </p>
 * <p>
 * If a GUI control is both an active DropTarget and is also scrollable it
 * can receive notifications of autoscrolling gestures, by the user, from
 * the DnD system by implementing this interface.
 * </p>
 * <p>
 * An autoscrolling gesture is initiated by the user by keeping the drag
 * cursor motionless with a border region of the Component, referred to as
 * the "autoscrolling region", for a predefined period of time, this will
 * result in repeated scroll requests to the Component until the Drag cursor
 * resumes its motion.
 * </p>
 *
 * @version 1.3
 * @since JDK1.2
 *
 */

public interface Autoscroll {

    /**
     * return the Insets describing the autoscrolling region or border relative
     * to the geometry of the implementing Component.
     *
     * This value is read once by the DropTarget upon entry of the drag cursor
     * into the associated Component.
     */

    public Insets getAutoscrollInsets();

    /**
     * notify the Component to autoscroll
     *
     * @param cursorLocn the location of the cursor that triggered this operation
     */

    public void autoscroll(Point cursorLocn);

}
