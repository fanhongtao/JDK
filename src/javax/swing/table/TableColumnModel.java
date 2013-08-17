/*
 * @(#)TableColumnModel.java	1.17 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import java.util.Enumeration;
import javax.swing.event.ChangeEvent;
import javax.swing.event.*;
import javax.swing.*;


/**
 * Defines the requirements for a model object suitable for
 * use with JTable.
 *
 * @version 1.17 11/29/01
 * @author Alan Chung
 * @author Philip Milne
 * @see DefaultTableColumnModel
 */
public interface TableColumnModel
{
//
// Modifying the model
//

    /**
     *  Appends <I>aColumn</I> to the end of the receiver's tableColumns array.
     *  This method also posts the columnAdded() event to its listeners.
     *
     * @param   aColumn         The <B>TableColumn</B> to be added
     * @see     #removeColumn
     */
    public void addColumn(TableColumn aColumn);

    /**
     *  Deletes the <B>TableColumn</B> <I>column</I> from the 
     *  receiver's table columns array.  This method will do nothing if 
     *  <I>column</I> is not in the table's columns list.
     *  This method also posts the columnRemoved() event to its listeners.
     *
     * @param   column          The <B>TableColumn</B> to be removed
     * @see     #addColumn
     */
    public void removeColumn(TableColumn column);
    
    /**
     * Moves the column and heading at <I>columnIndex</I> to <I>newIndex</I>.
     * The old column at <I>columnIndex</I> will now be found at <I>newIndex</I>,
     * The column that used to be at <I>newIndex</I> is shifted left or right
     * to make room.
     * This will not move any columns if <I>columnIndex</I> equals <I>newIndex</I>.
     * This method also posts the columnMoved() event to its listeners.
     *
     * @param   columnIndex                     the index of column to be moved
     * @param   newIndex                        New index to move the column
     * @exception IllegalArgumentException      if <I>column</I> or 
     *                                          <I>newIndex</I>
     *                                          are not in the valid range
     */
    public void moveColumn(int columnIndex, int newIndex);

    /**
     * Sets the <B>TableColumn's</B> column margin to <I>newMargin</I>.
     * This method also posts the columnMarginChanged() event to its
     * listeners.
     *
     * @param   newMargin               the width margin of the column
     * @see     #getColumnMargin
     */
    public void setColumnMargin(int newMargin);
    
//
// Querying the model
//

    /** Returns the number of columns in the model */
    public int getColumnCount();
    
    /** Returns an Enumeration of all the columns in the model */
    public Enumeration getColumns();

    /**
     * Returns the index of the first column in the receiver's
     * columns array whose identifier is equal to <I>identifier</I>,
     * when compared using <I>equals()</I>.
     *
     * @return          the index of the first table column in the receiver's
     *                  tableColumns array whose identifier is equal to
     *                  <I>identifier</I>, when compared using equals().
     * @param           identifier                      the identifier object
     * @exception IllegalArgumentException      if <I>identifier</I> is null or no TableColumn has this identifier
     * @see             #getColumn
     */
    public int getColumnIndex(Object columnIdentifier);

    /**
     * Returns the <B>TableColumn</B> object for the column at <I>columnIndex</I>
     *
     * @return  the TableColumn object for the column at <I>columnIndex</I>
     * @param   columnIndex     the index of the column desired
     */
    public TableColumn getColumn(int columnIndex);

    /** Returns the width margin between each column */
    public int getColumnMargin();
    
    /**
     * Returns the index of the column that lies on the <I>xPosition</I>,
     * or -1 if it lies outside the any of the column's bounds.
     *
     * @return  the index of the column or -1 if no column is found
     */
    public int getColumnIndexAtX(int xPosition);
    
    /** Returns the total width of all the columns. */
    public int getTotalColumnWidth();

//
// Selection
//

    /**
     * Sets whether the columns in this model can be selected.
     * @see #getColumnSelectionAllowed
     */
    public void setColumnSelectionAllowed(boolean flag);

    /**
     * Returns true if columns can be selected.
     * @return true if columns can be selected
     * @see #setColumnSelectionAllowed
     */
    public boolean getColumnSelectionAllowed();

    /**
     * Returns an array of indexes for selected columns
     * @return an array of ints giving the indexes of all selected columns,
     *         or an empty int array if no column is selected.
     */
    public int[] getSelectedColumns();

    /**
     * Returns the number of selected columns.
     *
     * @return the number of selected columns, or 0 if no columns are selected
     */
    public int getSelectedColumnCount();

    /**
     * Sets the selection model, which handles selections.
     *
     * @param newModel  a ListSelectionModel object
     * @see #getSelectionModel
     */
    public void setSelectionModel(ListSelectionModel newModel); 
    
    /**
     * Returns the current selection model.
     *
     * @return a ListSelectionModel object representing the selection model val
     * @see #setSelectionModel
     */
    public ListSelectionModel getSelectionModel(); 
    
//
// Listener
//

    /**
     * Add a listener for table column model events.
     *
     * @param x  a TableColumnModelListener object
     */
    public void addColumnModelListener(TableColumnModelListener x);
    /**
     * Remove a listener for table column model events.
     *
     * @param x  a TableColumnModelListener object
     */
    public void removeColumnModelListener(TableColumnModelListener x);
}
