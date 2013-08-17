/*
 * @(#)ChangeEvent.java	1.2 00/01/12
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
 * ChangeEvent is used to notify interested parties that 
 * state has changed in the event source.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.10 08/28/98
 * @author Jeff Dinkins
 */
public class ChangeEvent extends EventObject {
    /**
     * Constructs a ChangeEvent object.
     *
     * @param source  the Object that is the source of the event
     *                (typically <code>this</code>)
     */
    public ChangeEvent(Object source) {
        super(source);
    }
}

