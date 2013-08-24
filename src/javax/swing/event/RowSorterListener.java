/*
 * @(#)RowSorterListener.java	1.2 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;

/**
 * <code>RowSorterListener</code>s are notified of changes to a
 * <code>RowSorter</code>.
 *
 * @version 1.2 11/17/05
 * @see javax.swing.RowSorter
 * @since 1.6
 */
public interface RowSorterListener extends java.util.EventListener {
    /**
     * Notification that the <code>RowSorter</code> has changed.  The event
     * describes the scope of the change.
     *
     * @param e the event, will not be null
     */
    public void sorterChanged(RowSorterEvent e);
}
