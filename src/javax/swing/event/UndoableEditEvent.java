/*
 * @(#)UndoableEditEvent.java	1.12 98/08/28
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

package javax.swing.event;

import javax.swing.undo.*;

/**
 * An event indicating that an operation which can be undone has occurred.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.12 08/28/98
 * @author Ray Ryan
 */
public class UndoableEditEvent extends java.util.EventObject {
    private UndoableEdit myEdit;

    /**
     * Constructs an UndoableEditEvent object.
     *
     * @param source  the Object that originated the event
     *                (typically <code>this</code>)
     * @param edit    an UndoableEdit object
     */
    public UndoableEditEvent(Object source, UndoableEdit edit) {
	super(source);
	myEdit = edit;
    }
    
    /**
     * Returns the edit value.
     *
     * @return the UndoableEdit object encapsulating the edit
     */
    public UndoableEdit getEdit() {
	return myEdit;
    }
}
