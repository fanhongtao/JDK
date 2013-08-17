/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.util.EventListener;

/**
 * TableModelListener defines the interface for an object that listens
 * to changes in a TableModel.
 *
 * @version 1.13 02/06/02
 * @author Alan Chung
 * @see javax.swing.table.TableModel
 */

public interface TableModelListener extends java.util.EventListener
{
    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e);
}

