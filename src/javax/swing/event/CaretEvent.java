/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;

import java.util.EventObject;


/**
 * CaretEvent is used to notify interested parties that 
 * the text caret has changed in the event source.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.10 02/06/02
 * @author  Timothy Prinzing
 */
public abstract class CaretEvent extends EventObject {

    /**
     * Creates a new CaretEvent object.
     *
     * @param source the object responsible for the event
     */
    public CaretEvent(Object source) {
        super(source);
    }

    /**
     * Fetches the location of the caret.
     *
     * @return the dot >= 0
     */
    public abstract int getDot();

    /**
     * Fetches the location of other end of a logical
     * selection.  If there is no selection, this
     * will be the same as dot.
     *
     * @return the mark >= 0
     */
    public abstract int getMark();
}

