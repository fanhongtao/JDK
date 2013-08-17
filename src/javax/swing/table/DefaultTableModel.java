/*
 * @(#)DefaultTableModel.java	1.19 98/08/28
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

import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.event.TableModelEvent;


/**
 * This is an implementation of TableModel that uses a Vector of Vectors
 * to store the cell value objects.
 * <p>
 * <b>Note:</b><br>
 * The DefaultTableModel's API contains the methods addColumn(),
 * removeColumn(), but not methods to insert a column at an index
 * nor methods to move the columns.  This is because JTable does
 * not display the columns based on the order of the columns in
 * this model.  So rearranging them here doesn't do much.  See
 * the column ordering methods in TableColumnModel.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.19 08/28/98
 * @author Alan Chung
 * @author Philip Milne
 *
 * @see TableModel
 * @see #getDataVector
 */
public class DefaultTableModel extends AbstractTableModel implements Serializable {

//
// Instance Variables
//

    /** The Vector of Vector of Object values */
    protected Vector    dataVector;

    /** The Vector of column identifiers */
    protected Vector    columnIdentifiers;

//
// Constructors
//

    /**
     *  Constructs a default DefaultTableModel which is a table of
     *  zero columns and zero rows.
     */
    public DefaultTableModel() {
        this((Vector)null, 0);
    }

    /**
     *  Constructs a DefaultTableModel with <i>numRows</i> and
     *  <i>numColumns</i> of <b>null</b> object values.
     *
     * @param numRows           The number of rows the table holds
     * @param numColumns        The number of columns the table holds
     *
     * @see #setValueAt
     */
    public DefaultTableModel(int numRows, int numColumns) {
        Vector names = new Vector(numColumns);
        names.setSize(numColumns);
        setColumnIdentifiers(names);
        dataVector = new Vector();
        setNumRows(numRows);
    }

    /**
     *  Constructs a DefaultTableModel with as many columns as there are
     *  elements in <i>columnNames</i> and <i>numRows</i> of <b>null</b>
     *  object values.  Each column's name will be taken from
     *  the <i>columnNames</i> vector.
     *
     * @param columnNames       Vector containing the names of the new columns.
     *                          If this null then the model has no columns
     * @param numRows           The number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public DefaultTableModel(Vector columnNames, int numRows) {
        setColumnIdentifiers(columnNames);
        dataVector = new Vector();
        setNumRows(numRows);
    }

    /**
     *  Constructs a DefaultTableModel with as many columns as there are
     *  elements in <i>columnNames</i> and <i>numRows</i> of <b>null</b>
     *  object values.  Each column's name will be taken from
     *  the <i>columnNames</i> array.
     *
     * @param columnNames       Array containing the names of the new columns.
     *                          If this null then the model has no columns
     * @param numRows           The number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public DefaultTableModel(Object[] columnNames, int numRows) {
        this(convertToVector(columnNames), numRows);
    }

    /**
     *  Constructs a DefaultTableModel and initializes the table
     *  by passing <i>data</i> and <i>columnNames</i> to the setDataVector()
     *  method.
     *
     * @param data              The data of the table
     * @param columnNames       Vector containing the names of the new columns.
     * @see #getDataVector
     * @see #setDataVector
     */
    public DefaultTableModel(Vector data, Vector columnNames) {
        setDataVector(data, columnNames);
    }

    /**
     *  Constructs a DefaultTableModel and initializes the table
     *  by passing <i>data</i> and <i>columnNames</i> to the setDataVector()
     *  method. The first index in the Object[][] is the row index and
     *  the second is the column index.
     *
     * @param data              The data of the table
     * @param columnNames       The names of the columns.
     * @see #getDataVector
     * @see #setDataVector
     */
    public DefaultTableModel(Object[][] data, Object[] columnNames) {
        setDataVector(data, columnNames);
    }

//
// Querying and Modifying the data structure
//

    /**
     *  This returns the Vector of Vectors that contains the table's
     *  data values.  The vectors contained in the outer vector are
     *  each a single row of values.  In other words, to get to the cell
     *  at row 1, column 5 <p>
     *
     *  <code>((Vector)getDataVector().elementAt(1)).elementAt(5);</code><p>
     *
     *  You can directly alter the returned Vector.  You can change the cell
     *  values, the number of rows. If you need to alter the number of columns
     *  in the model, you can do so with addColumn(), removeColumn(), or
     *  the setDataVector() methods.  Once you have finished modifying the
     *  dataVector,  you <b>must</b> inform the model of the new data using
     *  one of the notification methods. The notification methods
     *  will generate the appropriate TableModelListener messages to notify
     *  the JTable and any other listeners of this model.
     *
     * @see #newDataAvailable
     * @see #newRowsAdded
     * @see #setDataVector
     */
    public Vector getDataVector() {
        return dataVector;
    }

    /**
     *  This replaces the current dataVector instance variable with the
     *  new Vector of rows, <i>newData</i>. <i>columnNames</i> are the names
     *  of the new columns.  The first name in <i>columnNames</i> is
     *  mapped to column 0 in <i>newData</i>. Each row in <i>newData</i>
     *  is adjusted to match the number of columns in <i>columnNames</i>
     *  either by truncating the Vector if it is too long, or adding
     *  null values if it is too short.
     *  <p>
     *
     * @param   newData         The new data vector
     * @param   columnNames     The names of the columns
     * @see #newDataAvailable
     * @see #getDataVector
     */
    public void setDataVector(Vector newData, Vector columnNames) {
        if (newData == null)
            throw new IllegalArgumentException("setDataVector() - Null parameter");

        // Clear all the previous data.
        dataVector = new Vector(0);

        // Install the new column structure, this will fireTableStructureChanged
        setColumnIdentifiers(columnNames);

        // Add the new rows.
        dataVector = newData;

        // Make all the new rows the right length and generate a notification.
        newRowsAdded(new TableModelEvent(this, 0, getRowCount()-1,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     *  This replaces the value in the dataVector instance variable with the
     *  values in the array <i>newData</i>.  The first index in the Object[][]
     *  array is the row index and the second is the column index.
     *  <i>columnNames</i> are the names of the new columns.
     *
     * @see #setDataVector(Vector, Vector)
     */
    public void setDataVector(Object[][] newData, Object[] columnNames) {
        setDataVector(convertToVector(newData), convertToVector(columnNames));
    }

    /**
     *  Equivalent to fireTableChanged.
     *
     */
    public void newDataAvailable(TableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     *  This method will make sure the new rows have the correct number of columns.
     *  It does so using the setSize method in Vector which truncates Vectors
     *  which are too long, and appends nulls if they are too short.
     *  This method also sends out a tableChanged() notification message
     *  to all the listeners.
     *
     * @parameter event         This TableModelEvent describes where the
     *                          rows were added.  If <b>null</b> it assumes
     *                          all the rows were newly added.
     * @see #getDataVector
     */
    public void newRowsAdded(TableModelEvent event) {
        int start = event.getFirstRow();
        int end = event.getLastRow();
        if (start < 0)
                start = 0;
        if (end < 0)
                end = getRowCount()-1;

        // Have to make sure all the new columns have the correct
        // number of columns
        for (int i = start; i < end; i++)
            ((Vector)dataVector.elementAt(i)).setSize(getColumnCount());

        // Now we send the notification
        fireTableChanged(event);
    }

    /**
     *  Equivalent to fireTableChanged().
     *
     */
    public void rowsRemoved(TableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     * Replaces the column identifiers in the model.
     *
     * @param   newIdentifiers  Vector of column identifiers.  A null means
     *                          setting the model to zero columns
     * @see #setNumRows
     */
    public void setColumnIdentifiers(Vector newIdentifiers) {
        if (newIdentifiers != null) {
            columnIdentifiers = newIdentifiers;
        }
        else {
            columnIdentifiers = new Vector();
        }

        // Generate notification
        fireTableStructureChanged();
    }

    /**
     * Replaces the column identifiers in the model.  If the number of
     * <i>newIdentifiers</i> is greater than the current numColumns,
     * new columns are added to the end of each row in the model.
     * If the number of <i>newIdentifier</i> is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded. <p>
     *
     * @param   newIdentifiers  Array of column identifiers.  A null means
     *                          setting the model to zero columns
     * @see #setNumRows
     */
    public void setColumnIdentifiers(Object[] newIdentifiers) {
        setColumnIdentifiers(convertToVector(newIdentifiers));
    }

    /**
     *  Sets the number of rows in the model.  If the new size is greater
     *  than the current size, new rows are added to the end of the model
     *  If the new size is less than the current size, all
     *  rows at index <i>newSize</i> and greater are discarded. <p>
     *
     * @param   newSize   the new number of rows
     * @see #setColumnIdentifiers
     */
    public void setNumRows(int newSize) {
        if ((newSize < 0) || (newSize == getRowCount()))
            return;

        int oldNumRows = getRowCount();
        if (newSize <= getRowCount()) {
            // newSize is smaller than our current size, so we can just
            // let Vector discard the extra rows
            dataVector.setSize(newSize);

            // Generate notification
            fireTableRowsDeleted(getRowCount(), oldNumRows-1);
        }
        else {
            int columnCount = getColumnCount();
            // We are adding rows to the model
            while(getRowCount() < newSize) {
                Vector newRow = new Vector(columnCount);
                newRow.setSize(columnCount);
                dataVector.addElement(newRow);
            }

            // Generate notification
            fireTableRowsInserted(oldNumRows, getRowCount()-1);
        }
    }

    /**
     *  Add a column to the model.  The new column will have the
     *  idenitifier <i>columnName</i>.  This method will send a
     *  tableChanged() notification message to all the listeners.
     *  This method is a cover for <i>addColumn(Object, Vector)</i> which
     *  uses null as the data vector.
     *
     * @param   columnName the identifier of the column being added
     * @exception IllegalArgumentException      if columnName is null
     */
    public void addColumn(Object columnName) {
        addColumn(columnName, (Vector)null);
    }

    /**
     *  Add a column to the model.  The new column will have the
     *  idenitifier <i>columnName</i>.  <i>columnData</i> is the
     *  optional Vector of data for the column.  If it is <b>null</b>
     *  the column is filled with <b>null</b> values.  Otherwise,
     *  the new data will be added to model starting with the first
     *  element going to row 0, etc.  This method will send a
     *  tableChanged() notification message to all the listeners.
     *
     * @param   columnName the identifier of the column being added
     * @param   columnData       optional data of the column being added
     * @exception IllegalArgumentException      if columnName is null
     */
    public void addColumn(Object columnName, Vector columnData) {
        if (columnName == null)
            throw new IllegalArgumentException("addColumn() - null parameter");

        columnIdentifiers.addElement(columnName);

        // Fill in the new column, with nulls or with columnData
        int index = 0;
        Enumeration enumeration = dataVector.elements();
        while (enumeration.hasMoreElements()) {
            Object value;

            if ((columnData != null) && (index < columnData.size()))
                value = columnData.elementAt(index);
            else
                value = null;

            ((Vector)enumeration.nextElement()).addElement(value);
            index++;
        }

        // Generate notification
        fireTableStructureChanged();
    }

    /**
     *  Adds a column to the model with name <i>columnName</i>.
     *
     * @see #addColumn(Object, Vector)
     */
    public void addColumn(Object columnName, Object[] columnData) {
        addColumn(columnName, convertToVector(columnData));
    }

    /**
     *  Add a row to the end of the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(Vector rowData) {
        if (rowData == null) {
            rowData = new Vector(getColumnCount());
        }
        else {
            rowData.setSize(getColumnCount());
        }

        dataVector.addElement(rowData);

        // Generate notification
        newRowsAdded(new TableModelEvent(this, getRowCount()-1, getRowCount()-1,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     *  Add a row to the end of the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(Object[] rowData) {
        addRow(convertToVector(rowData));
    }

    /**
     *  Insert a row at <i>row</i> in the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
    public void insertRow(int row, Vector rowData) {
        if (rowData == null) {
            rowData = new Vector(getColumnCount());
        }
        else {
            rowData.setSize(getColumnCount());
        }

        dataVector.insertElementAt(rowData, row);

        // Generate notification
        newRowsAdded(new TableModelEvent(this, row, row,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     *  Insert a row at <i>row</i> in the model.  The new row will contain
     *  <b>null</b> values unless <i>rowData</i> is specified.  Notification
     *  of the row being added will be generated.
     *
     * @param   row      the row index of the row to be inserted
     * @param   rowData          optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
    public void insertRow(int row, Object[] rowData) {
        insertRow(row, convertToVector(rowData));
    }

    /**
     *  Moves one or more rows starting at <i>startIndex</i> to <i>endIndex</i>
     *  in the model to the <i>toIndex</i>.    This method will send a
     *  tableChanged() notification message to all the listeners. <p>
     *
     *  Examples of moves:<p>
     *  1. moveRow(1,3,5);<p>
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|B|C|D|g|h|i|j|k   - after
     *  2. moveRow(6,7,1);<p>
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     *
     * @param   startIndex       the starting row index to be moved
     * @param   endIndex         the ending row index to be moved
     * @param   toIndex          the destination of the rows to be moved
     * @exception  ArrayIndexOutOfBoundsException  if any of the indices are out of
     *                           range.  Or if endIndex is less than startIndex.
     */
    public void moveRow(int startIndex, int endIndex, int toIndex) {
        if ((startIndex < 0) || (startIndex >= getRowCount()))
            throw new ArrayIndexOutOfBoundsException(startIndex);
        if ((endIndex < 0) || (endIndex >= getRowCount()))
            throw new ArrayIndexOutOfBoundsException(endIndex);
        if (startIndex > endIndex)
            throw new ArrayIndexOutOfBoundsException();

        if ((startIndex <= toIndex) && (toIndex <= endIndex))
            return;                     // Nothing to move

        boolean shift = toIndex < startIndex;

        // Do the move by first removing the row, then reinserting it
        for (int i = startIndex; i <= endIndex; i++) {
            Object aRow = dataVector.elementAt(i);
            dataVector.removeElementAt(i);
            dataVector.insertElementAt(aRow, toIndex);

            if (shift)
                toIndex++;
        }

        // Generate notification
        fireTableDataChanged();
    }

    /**
     *  Remove the row at <i>row</i> from the model.  Notification
     *  of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid.
     */
    public void removeRow(int row) {
        dataVector.removeElementAt(row);

        // Generate notification
        fireTableRowsDeleted(row, row);
    }

//
// Implementing the TableModel interface
//

    /**
     * Returns the number of rows in this data table.
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return dataVector.size();
    }

    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columnIdentifiers.size();
    }

    /**
     * Returns the column name.
     * @return a name for this column using the string value of the
     * appropriate member in <I>columnIdentfiers</I>. If <I>columnIdentfiers</I>
     * is null or does not have and entry for this index return the default
     * name provided by the superclass.
     */
    public String getColumnName(int column) {
        if (columnIdentifiers == null || columnIdentifiers.size() <= column) {
            return super.getColumnName(column);
        }
        Object id = columnIdentifiers.elementAt(column);
        if (id == null) {
            return super.getColumnName(column);
        }
        else {
            return id.toString();
        }
    }

    /**
     * Returns true if the cell at <I>row</I> and <I>column</I>
     * is editable.  Otherwise, the setValueAt() on the cell will not change
     * the value of that cell.
     *
     * @param   row             the row whose value is to be looked up
     * @param   column          the column whose value is to be looked up
     * @return                  true if the cell is editable.
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /**
     * Returns an attribute value for the cell at <I>row</I>
     * and <I>column</I>.
     *
     * @param   row             the row whose value is to be looked up
     * @param   column          the column whose value is to be looked up
     * @return                  the value Object at the specified cell
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given.
     */
    public Object getValueAt(int row, int column) {
        Vector rowVector = (Vector)dataVector.elementAt(row);
        return rowVector.elementAt(column);
    }

    /**
     * Sets the object value for the cell at <I>column</I> and
     * <I>row</I>.  <I>aValue</I> is the new value.  This method
     * will generate a tableChanged() notification.
     *
     * @param   aValue          the new value.  This can be null.
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given.
     */
    public void setValueAt(Object aValue, int row, int column) {
        Vector rowVector = (Vector)dataVector.elementAt(row);
        rowVector.setElementAt(aValue, column);

        // generate notification
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

//
// Protected Methods
//

    /** Returns a Vector that contains the same objects as the array */
    protected static Vector convertToVector(Object[] anArray) {
        if (anArray == null)
            return null;

        Vector v = new Vector(anArray.length);
        for (int i=0; i < anArray.length; i++) {
            v.addElement(anArray[i]);
        }
        return v;
    }

    /** Returns a Vector of Vectors that contains the same objects as the array */
    protected static Vector convertToVector(Object[][] anArray) {
        if (anArray == null)
            return null;

        Vector v = new Vector(anArray.length);
        for (int i=0; i < anArray.length; i++) {
            v.addElement(convertToVector(anArray[i]));
        }
        return v;
    }

} // End of class DefaultTableModel
