/*
 * @(#)NodeChangeListener.java	1.2 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.prefs;

/**
 * A listener for receiving preference node change events.
 *
 * @author  Josh Bloch
 * @version 1.2, 12/03/01
 * @see     Preferences
 * @see     NodeChangeEvent
 * @see     PreferenceChangeListener
 * @since   1.4
 */

public interface NodeChangeListener extends java.util.EventListener {
    /**
     * This method gets called when a child node is added.
     *
     * @param evt A node change event object describing the parent
     *            and child node.
     */
    void childAdded(NodeChangeEvent evt);

    /**
     * This method gets called when a child node is removed.
     *
     * @param evt A node change event object describing the parent
     *            and child node.
     */
    void childRemoved(NodeChangeEvent evt);
}
