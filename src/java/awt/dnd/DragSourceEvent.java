/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.dnd;

import java.awt.dnd.DragSourceContext;

import java.util.EventObject;

/**
 * This class is the base class for 
 * <code>DragSourceDragEvent</code> and 
 * <code>DragSourceDropEvent</code>.
 * @since 1.2
 *
 */

public class DragSourceEvent extends EventObject {

    /**
     * Construct a <code>DragSourceEvent</code>
     * given a specified <code>DragSourceContext</code>.
     * <P>
     * @param dsc the <code>DragSourceContext</code>
     */

    public DragSourceEvent(DragSourceContext dsc) { super(dsc); }

    /**
     * This method returns the <code>DragSourceContext</code> that 
     * originated the event.
     * <P>
     * @return the <code>DragSourceContext</code> that originated the event
     */

    public DragSourceContext getDragSourceContext() {
	return (DragSourceContext)getSource();
    }
}

