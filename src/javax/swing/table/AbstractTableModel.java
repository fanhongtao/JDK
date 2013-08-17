/*
 * @(#)AbstractTableModel.java	1.23 98/08/28
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

package javax.swing.table;

import javax.swing.*;
import javax.swing.event.*;
import java.io.Serializable;


/**
 *  This abstract class provides default implementations for most of
 *  the methods in the <B>TableModel</B> interface. It takes care of
 *  the management of listners and provides some conveniences for generating
 *  TableModelEvents and dispatching them to the listeners.
 *  To create a concrete TableModel as a sublcass of
 *  AbstractTableModel you need only provide implementations for the
 *  following three methods:
 *
 *  <pre>
 *  public int getRowCount();
 *  public int getColumnCount();
 *  public Object getValueAt(int row, int column);
 *  </pre>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.23 08/28/98
 * @author Alan Chung
 * @author Philip Milne
 */
public abstract class AbstractTableModel implements TableModel, Serializable
{
//
// Instance Variables
//

    /** List of listeners */
    protected EventListenerList listenerList = new EventListenerList();

//
// Default Implementation of the Interface
//

    /**
     *  Return a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.
     */
    public String getColumnName(int column) {
	String result = "";
	for (; column >= 0; column = column / 26 - 1) {
	    result = (char)((char)(column%26)+'A') + result;
	}
        return result;
    }

    /**
     * Convenience method for locating columns by name.
     * Implementation is naive so this should be overridden if
     * this method is to be called often. This method is not
     * in the TableModel interface and is not used by the JTable.
     */
    public int findColumn(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     *  Returns Object.class by default
     */
    public Class getColumnClass(int columnIndex) {
	return Object.class;
    }

    /**
     *  This default implementation returns false for all cells
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
    }

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }


//
//  Managing Listeners
//

    /**
     * Add a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public void addTableModelListener(TableModelListener l) {
	listenerList.add(TableModelListener.class, l);
    }

    /**
     * Remove a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public void removeTableModelListener(TableModelListener l) {
	listenerList.remove(TableModelListener.class, l);
    }

//
//  Fire methods
//

    /**
     * Notify all listeners that all cell values in the table's rows may have changed.
     * The number of rows may also have changed and the JTable should redraw the
     * table from scratch. The structure of the table, ie. the order of the
     * columns is assumed to be the same.
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notify all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the JTable recieves this event and its <I>autoCreateColumnsFromModel</I>
     * flag is set it discards any TableColumns that it had and reallocates
     * default ones in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the JTable.
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Notify all listeners that rows in the (inclusive) range
     * [<I>firstRow</I>, <I>lastRow</I>] have been inserted.
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     * Notify all listeners that rows in the (inclusive) range
     * [<I>firstRow</I>, <I>lastRow</I>] have been updated.
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    /**
     * Notify all listeners that rows in the (inclusive) range
     * [<I>firstRow</I>, <I>lastRow</I>] have been deleted.
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    /**
     * Notify all listeners that the value of the cell at (row, column)
     * has been updated.
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableCellUpdated(int row, int column) {
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

    /**
     * Forward the given notification event to all TableModelListeners that registered
     * themselves as listeners for this table model.
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableModelListener.class) {
		((TableModelListener)listeners[i+1]).tableChanged(e);
	    }
	}
    }

} // End of class AbstractTableModel
