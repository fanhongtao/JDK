/*
 * @(#)PopupMenuEvent.java	1.9 00/02/02
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
 * PopupMenuEvent only contains the source of the event which is the JPoupMenu
 * sending the event
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.9 02/02/00
 * @author Arnaud Weber
 */
public class PopupMenuEvent extends EventObject {
    /**
     * Constructs a PopupMenuEvent object.
     *
     * @param source  the Object that originated the event
     *                (typically <code>this</code>)
     */
    public PopupMenuEvent(Object source) {
        super(source);
    }
}
