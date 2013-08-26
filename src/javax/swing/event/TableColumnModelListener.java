/*
 * @(#)TableColumnModelListener.java	1.15 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import java.util.EventListener;

/**
 * TableColumnModelListener defines the interface for an object that listens
 * to changes in a TableColumnModel.
 *
 * @version 1.15 03/23/10
 * @author Alan Chung
 * @see TableColumnModelEvent
 */

public interface TableColumnModelListener extends java.util.EventListener
{
    /** Tells listeners that a column was added to the model. */
    public void columnAdded(TableColumnModelEvent e);

    /** Tells listeners that a column was removed from the model. */
    public void columnRemoved(TableColumnModelEvent e);

    /** Tells listeners that a column was repositioned. */
    public void columnMoved(TableColumnModelEvent e);

    /** Tells listeners that a column was moved due to a margin change. */
    public void columnMarginChanged(ChangeEvent e);

    /**
     * Tells listeners that the selection model of the
     * TableColumnModel changed.
     */
    public void columnSelectionChanged(ListSelectionEvent e);
}

