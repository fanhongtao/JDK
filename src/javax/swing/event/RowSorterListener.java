/*
 * @(#)RowSorterListener.java	1.3 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;

/**
 * <code>RowSorterListener</code>s are notified of changes to a
 * <code>RowSorter</code>.
 *
 * @version 1.3 03/23/10
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
