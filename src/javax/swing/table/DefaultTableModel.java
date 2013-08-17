/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.event.TableModelEvent;


/**
 * This is an implementation of <code>TableModel</code> that
 * uses a <code>Vector</code> of <code>Vectors</code> to store the
 * cell value objects.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.28 02/06/02
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

    /**
     * The <code>Vector</code> of <code>Vectors</code> of 
     * <code>Object</code> values.
     */
    protected Vector    dataVector;

    /** The <code>Vector</code> of column identifiers. */
    protected Vector    columnIdentifiers;

//
// Constructors
//

    /**
     *  Constructs a default <code>DefaultTableModel</code> 
     *  which is a table of zero columns and zero rows.
     */
    public DefaultTableModel() {
        this((Vector)null, 0);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> with
     *  <code>numRows</code> and <code>numColumns</code> of
     *  <code>null</code> object values.
     *
     * @param numRows           the number of rows the table holds
     * @param numColumns        the number of columns the table holds
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
     *  Constructs a <code>DefaultTableModel</code> with as many columns
     *  as there are elements in <code>columnNames</code>
     *  and <code>numRows</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> vector.
     *
     * @param columnNames       <code>vector</code> containing the names
     *				of the new columns.  If this is 
     *                          <code>null</code> then the model has no columns
     * @param numRows           the number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public DefaultTableModel(Vector columnNames, int numRows) {
        setColumnIdentifiers(columnNames);
        dataVector = new Vector();
        setNumRows(numRows);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> with as many
     *  columns as there are elements in <code>columnNames</code>
     *  and <code>numRows</code> of <code>null</code>
     *  object values.  Each column's name will be taken from
     *  the <code>columnNames</code> array.
     *
     * @param columnNames       <code>array</code> containing the names
     *				of the new columns.  If this is
     *                          <code>null</code> then the model has no columns
     * @param numRows           the number of rows the table holds
     * @see #setDataVector
     * @see #setValueAt
     */
    public DefaultTableModel(Object[] columnNames, int numRows) {
        this(convertToVector(columnNames), numRows);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> and initializes the table
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataVector</code> method.
     *
     * @param data              the data of the table
     * @param columnNames       <code>vector</code> containing the names
     *				of the new columns
     * @see #getDataVector
     * @see #setDataVector
     */
    public DefaultTableModel(Vector data, Vector columnNames) {
        setDataVector(data, columnNames);
    }

    /**
     *  Constructs a <code>DefaultTableModel</code> and initializes the table
     *  by passing <code>data</code> and <code>columnNames</code>
     *  to the <code>setDataVector</code>
     *  method. The first index in the <code>Object[][]</code> array is
     *  the row index and the second is the column index.
     *
     * @param data              the data of the table
     * @param columnNames       the names of the columns
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
     *  Returns the <code>Vector</code> of <code>Vectors</code>
     *  that contains the table's
     *  data values.  The vectors contained in the outer vector are
     *  each a single row of values.  In other words, to get to the cell
     *  at row 1, column 5: <p>
     *
     *  <code>((Vector)getDataVector().elementAt(1)).elementAt(5);</code><p>
     *
     * @return  the vector of vectors containing the tables data values
     *
     * @see #newDataAvailable
     * @see #newRowsAdded
     * @see #setDataVector
     */
    public Vector getDataVector() {
        return dataVector;
    }

    /**
     *  Replaces the current <code>dataVector</code> instance variable with the
     *  new Vector of rows, <code>newData</code>.
     *  <code>columnNames</code> are the names
     *  of the new columns.  The first name in <code>columnNames</code> is
     *  mapped to column 0 in <code>newData</code>. Each row in
     *  <code>newData</code>
     *  is adjusted to match the number of columns in <code>columnNames</code>
     *  either by truncating the <code>Vector</code> if it is too long,
     *  or adding <code>null</code> values if it is too short.
     *  <p>
     *
     * @param   newData         the new data vector
     * @param   columnNames     the names of the columns
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
     *  Replaces the value in the <code>dataVector</code> instance 
     *  variable with the values in the array <code>newData</code>.
     *  The first index in the <code>Object[][]</code>
     *  array is the row index and the second is the column index.
     *  <code>columnNames</code> are the names of the new columns.
     *
     * @param newData		the new data vector
     * @param columnNames	the names of the columns
     * @see #setDataVector(Vector, Vector)
     */
    public void setDataVector(Object[][] newData, Object[] columnNames) {
        setDataVector(convertToVector(newData), convertToVector(columnNames));
    }

    /**
     *  Equivalent to <code>fireTableChanged</code>.
     *
     * @param event  the change event 
     *
     */
    public void newDataAvailable(TableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     *  Ensures that the new rows have the correct number of columns.
     *  This is accomplished by  using the <code>setSize</code> method in
     *  <code>Vector</code> which truncates vectors
     *  which are too long, and appends <code>null</code>s if they
     *  are too short.
     *  This method also sends out a <code>tableChanged</code>
     *  notification message to all the listeners.
     *
     * @param event         this <code>TableModelEvent</code> describes 
     *                           where the rows were added. 
     *				 If <code>null</code> it assumes
     *                           all the rows were newly added
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
     *  Equivalent to <code>fireTableChanged</code>.
     *
     *  @param event the change event
     *
     */
    public void rowsRemoved(TableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     * Replaces the column identifiers in the model.  If the number of
     * <code>newIdentifier</code>s is greater than the current number
     * of columns, new columns are added to the end of each row in the model.
     * If the number of <code>newIdentifier</code>s is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded. <p>
     *
     * @param   newIdentifiers  vector of column identifiers.  If
     *				<code>null</code>, set the model
     *                          to zero columns
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
     * <code>newIdentifier</code>s is greater than the current number
     * of columns, new columns are added to the end of each row in the model.
     * If the number of <code>newIdentifier</code>s is less than the current
     * number of columns, all the extra columns at the end of a row are
     * discarded. <p>
     *
     * @param   newIdentifiers  array of column identifiers. 
     *				If <code>null</code>, set
     *                          the model to zero columns
     * @see #setNumRows
     */
    public void setColumnIdentifiers(Object[] newIdentifiers) {
        setColumnIdentifiers(convertToVector(newIdentifiers));
    }

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use <code>setRowCount</code> instead.
     */
    /*
     *  Sets the number of rows in the model.  If the new size is greater
     *  than the current size, new rows are added to the end of the model
     *  If the new size is less than the current size, all
     *  rows at index <code>newSize</code> and greater are discarded. <p>
     *
     * @param   newSize   the new number of rows
     * @see #setRowCount
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
     *  Sets the number of rows in the model.  If the new size is greater
     *  than the current size, new rows are added to the end of the model
     *  If the new size is less than the current size, all
     *  rows at index <code>rowCount</code> and greater are discarded. <p>
     *
     *  @see #setColumnCount
     */
    public void setRowCount(int rowCount) { 
	setNumRows(rowCount); 
    } 

    /**
     *  Sets the number of columns in the model.  If the new size is greater
     *  than the current size, new columns are added to the end of the model 
     *  with <code>null</code> cell values.
     *  If the new size is less than the current size, all columns at index
     *  <code>columnCount</code> and greater are discarded. 
     *
     *  @param columnCount  the new number of columns in the model
     *
     *  @see #setColumnCount
     */
    public void setColumnCount(int columnCount) { 
	for (int r = 0; r < getRowCount(); r++) { 
	    Vector row = (Vector)dataVector.elementAt(r); 
	    row.setSize(columnCount); 
	}
	columnIdentifiers.setSize(columnCount); 
	fireTableStructureChanged();
    } 

    /**
     *  Adds a column to the model.  The new column will have the
     *  identifier <code>columnName</code>.  This method will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *  This method is a cover for <code>addColumn(Object, Vector)</code> which
     *  uses <code>null</code> as the data vector.
     *
     * @param   columnName the identifier of the column being added
     * @exception IllegalArgumentException      if <code>columnName</code>
     *						is <code>null</code>
     */
    public void addColumn(Object columnName) {
        addColumn(columnName, (Vector)null);
    }

    /**
     *  Adds a column to the model.  The new column will have the
     *  identifier <code>columnName</code>.  <code>columnData</code> is the
     *  optional vector of data for the column.  If it is <code>null</code>
     *  the column is filled with <code>null</code> values.  Otherwise,
     *  the new data will be added to model starting with the first
     *  element going to row 0, etc.  This method will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *
     * @param   columnName the identifier of the column being added
     * @param   columnData       optional data of the column being added
     * @exception IllegalArgumentException      if <code>columnName</code>
     *						is <code>null</code>
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
     *  Adds a column to the model.  The new column will have the
     *  identifier <code>columnName</code>.  <code>columnData</code> is the
     *  optional array of data for the column.  If it is <code>null</code>
     *  the column is filled with <code>null</code> values.  Otherwise,
     *  the new data will be added to model starting with the first
     *  element going to row 0, etc.  This method will send a
     *  <code>tableChanged</code> notification message to all the listeners.
     *
     * @see #addColumn(Object, Vector)
     */
    public void addColumn(Object columnName, Object[] columnData) {
        addColumn(columnName, convertToVector(columnData));
    }

    /**
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
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
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(Object[] rowData) {
        addRow(convertToVector(rowData));
    }

    /**
     *  Inserts a row at <code>row</code> in the model.  The new row
     *  will contain <code>null</code> values unless <code>rowData</code>
     *  is specified.  Notification of the row being added will be generated.
     *
     * @param   row             the row index of the row to be inserted
     * @param   rowData         optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
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
     *  Inserts a row at <code>row</code> in the model.  The new row
     *  will contain <code>null</code> values unless <code>rowData</code>
     *  is specified.  Notification of the row being added will be generated.
     *
     * @param   row      the row index of the row to be inserted
     * @param   rowData          optional data of the row being added
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
     */
    public void insertRow(int row, Object[] rowData) {
        insertRow(row, convertToVector(rowData));
    }

    /**
     *  Moves one or more rows starting at <code>startIndex</code>
     *  to <code>endIndex</code> in the model to the <code>toIndex</code>.
     *  This method will send a <code>tableChanged</code> notification
     *  message to all the listeners. <p>
     *
     * <pre>
     *  Examples of moves:<p>
     *  1. moveRow(1,3,5);<p>
     *          a|B|C|D|e|f|g|h|i|j|k   - before
     *          a|e|f|B|C|D|g|h|i|j|k   - after<p>
     *  2. moveRow(6,7,1);<p>
     *          a|b|c|d|e|f|G|H|i|j|k   - before
     *          a|G|H|b|c|d|e|f|i|j|k   - after
     * </pre>
     *
     * @param   startIndex       the starting row index to be moved
     * @param   endIndex         the ending row index to be moved
     * @param   toIndex          the destination of the rows to be moved
     * @exception  ArrayIndexOutOfBoundsException  if any of the indices
     *                           are out of range; or if <code>endIndex</code>
     *				 is less than <code>startIndex</code>
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
     *  Removes the row at <code>row</code> from the model.  Notification
     *  of the row being removed will be sent to all the listeners.
     *
     * @param   row      the row index of the row to be removed
     * @exception  ArrayIndexOutOfBoundsException  if the row was invalid
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
     *
     * @return a name for this column using the string value of the
     * appropriate member in <code>columnIdentifiers</code>.
     * If <code>columnIdentifiers</code> is <code>null</code>
     * or does not have an entry for this index, returns the default
     * name provided by the superclass
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
     * Returns true regardless of parameter values.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  true
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /**
     * Returns an attribute value for the cell at <code>row</code>
     * and <code>column</code>.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  the value Object at the specified cell
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public Object getValueAt(int row, int column) {
        Vector rowVector = (Vector)dataVector.elementAt(row);
        return rowVector.elementAt(column);
    }

    /**
     * Sets the object value for the cell at <code>column</code> and
     * <code>row</code>.  <code>aValue</code> is the new value.  This method
     * will generate a <code>tableChanged</code> notification.
     *
     * @param   aValue          the new value; this can be null
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
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

    /** 
     * Returns a vector that contains the same objects as the array.
     * @param anArray  the array to be converted
     * @return  the new vector; if <code>anArray</code> is <code>null</code>,
     *				returns <code>null</code>
     */
    protected static Vector convertToVector(Object[] anArray) {
        if (anArray == null)
            return null;

        Vector v = new Vector(anArray.length);
        for (int i=0; i < anArray.length; i++) {
            v.addElement(anArray[i]);
        }
        return v;
    }

    /** 
     * Returns a vector of vectors that contains the same objects as the array.
     * @param anArray  the double array to be converted
     * @return the new vector of vectors; if <code>anArray</code> is
     *				<code>null</code>, returns <code>null</code>
     */
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
