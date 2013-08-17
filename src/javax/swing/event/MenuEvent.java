/*
 * @(#)MenuEvent.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;

import java.util.EventObject;


/**
 * MenuEvent is used to notify interested parties that 
 * the menu which is the event source has been posted,
 * selected, or canceled.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.10 11/29/01
 * @author Georges Saab
 * @author David Karlton
 */
public class MenuEvent extends EventObject {
    /**
     * Constructs a MenuEvent object.
     *
     * @param source  the Object that originated the event
     *                (typically <code>this</code>)
     */
    public MenuEvent(Object source) {
        super(source);
    }
}

