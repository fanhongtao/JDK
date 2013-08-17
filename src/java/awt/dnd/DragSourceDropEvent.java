/*
 * @(#)DragSourceDropEvent.java	1.4 98/03/18
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
import java.awt.dnd.DnDConstants;

/**
 * <p>
 * The DragSourceDropEvent is delivered from the DragSourceContextPeer,
 * via the DragSourceContext, to its currently registered DragSourceListener.
 * It contains sufficient information for the originator of the operation
 * to provide appropriate feedback to the end user when the operation completes.
 * </p>
 *
 * @version 1.4
 * @since JDK1.2
 *
 */

public class DragSourceDropEvent extends DragSourceEvent {

    /**
     * construct a DragSourceDropEvent for a drop
     */

    public DragSourceDropEvent(DragSourceContext dsc, int action, boolean success) {
	super(dsc);

	dropSuccess = success;
	dropAction  = action;
    }

    /**
     * construct a DragSourceDropEvent for a drag that does not result in a drop
     */

    public DragSourceDropEvent(DragSourceContext dsc) {
	super(dsc);

	dropSuccess = false;
    }

    /**
     * @return if the drop was successful
     */

    public boolean getDropSuccess() { return dropSuccess; }

    /**
     * @return the action performed by the target on the subject of the drop
     */

    public int getDropAction() { return dropAction; }

    /*
     * fields
     */

    private boolean dropSuccess;
    private int	    dropAction   = DnDConstants.ACTION_NONE;
}
