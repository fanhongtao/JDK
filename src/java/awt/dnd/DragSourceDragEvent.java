/*
 * @(#)DragSourceDragEvent.java	1.8 98/10/17
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

import java.awt.dnd.DragSourceEvent;

/**
 * <p>
 * The DragSourceDragEvent is delivered from the DragSourceContextPeer,
 * via the DragSourceContext, to the currently registered DragSourceListener.
 * It contains state regarding the current state of the operation to enable
 * the operations initiator to provide the end user with the appropriate
 * drag over feedback.
 * </p>
 *
 * @version 1.8
 * @since JDK1.2
 *
 */

public class DragSourceDragEvent extends DragSourceEvent {

    /**
     * Constructs a DragSourceDragEvent. This class is typically 
     * instantiated by the DragSourceClientPeer rather than directly 
     * by client code.
     * 
     * @param dsc the DragSourceContext that is to manage 
     *            notifications for this event.
     * @param dropAction the value of one of the static fields from 
     *        {@link DNDConstants} indicating the type of user drop 
     *        action this event represents.
     * @param actions the value of one of the static fields from 
     *        {@link DNDConstants} indicating the type of target drop
     *        action supported by and returned from the current drop target.
     * @param modifiers specifies the state of the input device modifiers 
     *        associated with the user gesture.
     */

    public DragSourceDragEvent(DragSourceContext dsc, int dropAction, int actions, int modifiers) {
	super(dsc);
	
	targetActions    = actions;
	gestureModifiers = modifiers;
	this.dropAction  = dropAction;
    }

    /**
     * @return the logical intersection of the current target, source and user actions
     */

    public int getTargetActions() {
	return targetActions;
    }

    /**
     * @return the current device modifiers
     */

    public int getGestureModifiers() {
	return gestureModifiers;
    }

    /**
     * @return the users currently selected drop action
     */

    public int getUserAction() { return dropAction; }

    /**
     * @return the effective drop action which is the intersection of the users
     * selected action, and the source and target actions.
     */

    public int getDropAction() {
	return dropAction & targetActions & getDragSourceContext().getSourceActions();
    }

    /*
     * fields
     */

    private int	    targetActions    = DnDConstants.ACTION_NONE;
    private int	    dropAction       = DnDConstants.ACTION_NONE;
    private int	    gestureModifiers = 0;
}
