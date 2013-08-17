/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DnDConstants;

/**
 * The <code>DragSourceDropEvent</code> is delivered 
 * from the <code>DragSourceContextPeer</code>,
 * via the <code>DragSourceContext</code>, to its currently 
 * registered <code>DragSourceListener</code>'s dragDropEnd()
 * method.
 * It contains sufficient information for the 
 * originator of the operation
 * to provide appropriate feedback to the end user 
 * when the operation completes.
 * <P>
 * @version 	1.13, 02/06/02
 * <P>
 * @since 1.2
 */

public class DragSourceDropEvent extends DragSourceEvent {

    /**
     * Construct a <code>DragSourceDropEvent</code> for a drop, 
     * given the 
     * <code>DragSourceContext</code>, the drop action, 
     * and a <code>boolean</code> indicating if the drop was successful.
     * <P>
     * @param dsc the <code>DragSourceContext</code> 
     * associated with this <code>DragSourceDropEvent</code>
     * @param action the drop action
     * @param success a boolean indicating if the drop was successful
     */

    public DragSourceDropEvent(DragSourceContext dsc, int action, boolean success) {
	super(dsc);

	dropSuccess = success;
	dropAction  = action;
    }

    /**
     * Construct a <code>DragSourceDropEvent</code>
     * for a drag that does not result in a drop.
     * <P>
     * @param dsc the <code>DragSourceContext</code>
     */

    public DragSourceDropEvent(DragSourceContext dsc) {
	super(dsc);

	dropSuccess = false;
    }

    /**
     * This method returns a <code>boolean</code> indicating 
     * if the drop was a success.
     * <P>
     * @return if the drop was successful
     */

    public boolean getDropSuccess() { return dropSuccess; }

    /**
     * This method returns an <code>int</code> representing 
     * the action performed by the target on the subject of the drop.
     * <P>
     * @return the action performed by the target on the subject of the drop
     */

    public int getDropAction() { return dropAction; }

    /*
     * fields
     */

    private boolean dropSuccess;
    private int	    dropAction   = DnDConstants.ACTION_NONE;
}







