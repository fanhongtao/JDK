/*
 * %W% %E%
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

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * An event reported to a child component that originated from an
 * ancestor in the component hierarchy.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version %I% %G%
 * @author Dave Moore
 */
public class AncestorEvent extends AWTEvent {
    /**
     * An ancestor-component was added to the hierarchy of
     * visible objects (made visible), and is currently being displayed.
     */
    public static final int ANCESTOR_ADDED = 1;
    /**
     * An ancestor-component was removed from the hierarchy
     * of visible objects (hidden) and is no longer being displayed.
     */
    public static final int ANCESTOR_REMOVED = 2;
    /** An ancestor-component changed its position on the screen. */
    public static final int ANCESTOR_MOVED = 3;

    Container ancestor;
    Container ancestorParent;

    /**
     * Constructs an AncestorEvent object to identify a change
     * in an ancestor-component's display-status.
     *
     * @param source          the JComponent that originated the event
     *                        (typically <code>this</code>)
     * @param id              an int specifying {@link ANCESTOR_ADDED}, 
     *                        {@link ANCESTOR_REMOVED} or {@link ANCESTOR_MOVED}
     * @param ancestor        a Container object specifying the ancestor-component
     *                        whose display-status changed
     * @param ancestorParent  a Container object specifying the ancestor's parent
     */
    public AncestorEvent(JComponent source, int id, Container ancestor, Container ancestorParent) {
        super(source, id);
        this.ancestor = ancestor;
        this.ancestorParent = ancestorParent;
    }

    /**
     * Returns the ancestor that the event actually occured on.
     */
    public Container getAncestor() {
        return ancestor;
    }

    /**
     * Returns the parent of the ancestor the event actually occured on.
     * This is most interesting in an ANCESTOR_REMOVED event, as
     * the ancestor may no longer be in the component hierarchy.
     */
    public Container getAncestorParent() {
        return ancestorParent;
    }

    /**
     * Returns the component that the listener was added to.
     */
    public JComponent getComponent() {
        return (JComponent)getSource();
    }
}
