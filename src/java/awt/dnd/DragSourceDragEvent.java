/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.dnd.DragSourceEvent;

/**
 * The <code>DragSourceDragEvent</code> is 
 * delivered from the <code>DragSourceContextPeer</code>,
 * via the <code>DragSourceContext</code>, to the currently 
 * registered <code>DragSourceListener</code>.
 * It contains state regarding the current state of the operation to enable
 * the operations initiator to provide the end user with the appropriate
 * drag over feedback.
 *
 * @version 	1.21, 02/06/02
 * @since 1.2
 *
 */

public class DragSourceDragEvent extends DragSourceEvent {

    /**
     * Constructs a <code>DragSourceDragEvent</code>. 
     * This class is typically 
     * instantiated by the <code>DragSourceContextPeer</code> 
     * rather than directly 
     * by client code.
     * <P>
     * @param dsc the <code>DragSourceContext</code> that is to manage 
     *            notifications for this event.
     * @param dropAction the value of one of the static fields from 
     *        <code>DNDConstants</code> indicating the type of user drop 
     *        action this event represents.
     * @param actions the value of one of the static fields from 
     *        <code>DNDConstants</code> indicating the type of target drop
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
     * This method returns the logical intersection of the current target, 
     * source, and user actions.
     * <P>
     * @return the logical intersection 
     * of the current target, source and user actions
     */

    public int getTargetActions() {
	return targetActions;
    }

    /**
     * This method returns an <code>int</code> representing
     * the current state of the input device modifiers
     * associated with the user's gesture. Typically these
     * would be mouse buttons or keyboard modifiers.
     * <P>
     * @return the current state of the input device modifiers
     */

    public int getGestureModifiers() {
	return gestureModifiers;
    }

    /**
     * This method returns an <code>int</code> representing 
     * the user's currently selected drop action.
     * <P>
     * @return the user's currently selected drop action
     */

    public int getUserAction() { return dropAction; }

    /**
     * This method returns an <code>int</code> representing   
     * the effective drop action which is the 
     * intersection of the user's
     * selected action, and the source and target actions.     
     * <P>
     * @return the effective drop action which is the 
     * intersection of the user's
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








