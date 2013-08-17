/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.15 02/06/02
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
