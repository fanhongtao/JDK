/*
 * @(#)MenuEvent.java	1.11 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version 1.11 02/02/00
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

