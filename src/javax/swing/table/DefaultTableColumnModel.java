/*
 * @(#)DefaultTableColumnModel.java	1.27 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;

/**
 * The standard column-handler for a JTable.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.27 11/29/01
 * @author Alan Chung
 * @author Philip Milne
 * @see JTable
 */
public class DefaultTableColumnModel implements TableColumnModel,
			PropertyChangeListener, ListSelectionListener, Serializable
{
//
// Instance Variables
//

    /** Array of TableColumn objects in this model */
    protected Vector tableColumns;

    /** Model for keeping track of column selections */
    protected ListSelectionModel selectionModel;

    /** Width margin between each column */
    protected int columnMargin;

    /** List of TableColumnModelListener */
    protected EventListenerList listenerList = new EventListenerList();

    /** Change event (only one needed) */
    transient protected ChangeEvent changeEvent = null;

    /** Column selection allowed in this column model */
    protected boolean columnSelectionAllowed;

    /** A local cache of the combined width of all columns */
    protected int totalColumnWidth;

//
// Constructors
//
    /**
     * Creates a default table column model.
     */
    public DefaultTableColumnModel() {
	super();

	// Initialize local ivars to default
	tableColumns = new Vector();
	setSelectionModel(createSelectionModel());
	setColumnMargin(1);
	setColumnSelectionAllowed(false);
    }

//
// Modifying the model
//

    /**
     *  Appends <I>aColumn</I> to the end of the receiver's tableColumns array.
     *  This method also posts the columnAdded() event to its listeners.
     *
     * @param	column		The <B>TableColumn</B> to be added
     * @exception IllegalArgumentException	if <I>aColumn</I> is null
     * @see	#removeColumn
     */
    public void addColumn(TableColumn aColumn) {
	if (aColumn == null) {
	    throw new IllegalArgumentException("Object is null");
	}

	tableColumns.addElement(aColumn);
	aColumn.addPropertyChangeListener(this);
	recalcWidthCache();

	// Post columnAdded event notification
	fireColumnAdded(new TableColumnModelEvent(this, 0,
						  getColumnCount() - 1));
    }

    /**
     *  Deletes the <B>TableColumn</B> <I>column</I> from the
     *  receiver's table columns array.  This method will do nothing if
     *  <I>column</I> is not in the table's columns list.  tile() is called
     *  to resize both the header and table views.
     *  This method also posts the columnRemoved() event to its listeners.
     *
     * @param	column		The <B>TableColumn</B> to be removed
     * @see	#addColumn
     */
    public void removeColumn(TableColumn column) {
	int columnIndex = tableColumns.indexOf(column);

	if (columnIndex != -1) {
	    // Adjust for the selection
	    if (selectionModel != null)
		selectionModel.removeIndexInterval(columnIndex,columnIndex);

	    column.removePropertyChangeListener(this);
	    tableColumns.removeElementAt(columnIndex);
	    recalcWidthCache();

	    // Post columnAdded event notification.  (JTable and JTableHeader
	    // listens so they can adjust size and redraw)
	    fireColumnRemoved(new TableColumnModelEvent(this,
					   getColumnCount() - 1, 0));
	}
    }

    /**
     * Moves the column and heading at <I>columnIndex</I> to <I>newIndex</I>.
     * The old column at <I>columnIndex</I> will now be found at <I>newIndex</I>,
     * The column that used to be at <I>newIndex</I> is shifted left or right
     * to make room.
     * This will not move any columns if <I>columnIndex</I> equals <I>newIndex</I>.
     * This method also posts the columnMoved() event to its listeners.
     *
     * @param	columnIndex			the index of column to be moved
     * @param	newIndex			New index to move the column
     * @exception IllegalArgumentException	if <I>column</I> or
     * 						<I>newIndex</I>
     *						are not in the valid range
     */
    public void moveColumn(int columnIndex, int newIndex) {
	if ((columnIndex < 0) || (columnIndex >= getColumnCount()) ||
	    (newIndex < 0) || (newIndex >= getColumnCount()))
	    throw new IllegalArgumentException("moveColumn() - Index out of range");

	TableColumn aColumn;

	// Do nothing if the parameters will result in a no-op move
	if (columnIndex == newIndex)
	    return;

	aColumn = (TableColumn)tableColumns.elementAt(columnIndex);

	boolean reselect = false;
	if (selectionModel.isSelectedIndex(columnIndex)) {
	    selectionModel.removeSelectionInterval(columnIndex,columnIndex);
	    reselect = true;
	}
	tableColumns.removeElementAt(columnIndex);
	tableColumns.insertElementAt(aColumn, newIndex);
	if (reselect)
	    selectionModel.addSelectionInterval(newIndex, newIndex);

	// Post columnMoved event notification.  (JTable and JTableHeader
	// listens so they can adjust size and redraw)
	fireColumnMoved(new TableColumnModelEvent(this, columnIndex,
							       newIndex));
    }

    /**
     * Sets the column margin to <I>newMargin</I>.
     * This method also posts the columnMarginChanged() event to its
     * listeners.
     *
     * @param	newMargin		the width margin of the column
     * @see	#getColumnMargin
     * @see	#getTotalColumnWidth
     */
    public void setColumnMargin(int newMargin) {
	if (newMargin != columnMargin) {
	    columnMargin = newMargin;
	    recalcWidthCache();

	    // Post columnMarginChanged event notification.
	    fireColumnMarginChanged();
	}
    }

//
// Querying the model
//

    /**
     * Returns the number of columns in the receiver's table columns array.
     *
     * @return		the number of columns in the receiver's table columns array
     * @see		#getColumns
     */
    public int getColumnCount() {
	return tableColumns.size();
    }

    /**
     * Returns an Enumeration of all the columns in the model
     */
    public Enumeration getColumns() {
	return tableColumns.elements();
    }

    /**
     * Returns the index of the first column in the receiver's
     * columns array whose identifier is equal to <I>identifier</I>,
     * when compared using <I>equals()</I>.
     *
     * @return		the index of the first table column in the receiver's
     *			tableColumns array whose identifier is equal to
     *			<I>identifier</I>, when compared using equals().
     * @param		identifier			the identifier object
     * @exception       IllegalArgumentException	if <I>identifier</I> is null or no TableColumn has this identifier
     * @see		#getColumn
     */
    public int getColumnIndex(Object identifier) {
	if (identifier == null) {
	    throw new IllegalArgumentException("Identifier is null");
	}

	Enumeration enumeration = getColumns();
	TableColumn aColumn;
	int index = 0;

	while (enumeration.hasMoreElements()) {
	    aColumn = (TableColumn)enumeration.nextElement();
	    // Compare them this way in case the column's identifier is null.
	    if (identifier.equals(aColumn.getIdentifier()))
		return index;
	    index++;
	}
	throw new IllegalArgumentException("Identifier not found");
    }

    /**
     * Returns the <B>TableColumn</B> object for the column at <I>columnIndex</I>
     *
     * @return	the TableColumn object for the column at <I>columnIndex</I>
     * @param	columnIndex	the index of the column desired
     */
    public TableColumn getColumn(int columnIndex) {
	return (TableColumn)tableColumns.elementAt(columnIndex);
    }

    /**
     * Returns the width margin for <B>TableColumn</B>.
     * The default columnMargin is 1.
     *
     * @return	the maximum width for the <B>TableColumn</B>.
     * @see	#setColumnMargin
     */
    public int getColumnMargin()
    {
	return columnMargin;
    }

    /**
     * Returns the index of the column that lies on the <I>xPosition</I>,
     * or -1 if it lies outside the any of the column's bounds.
     *
     * @return	the index of the column or -1 if no column is found
     */
    public int getColumnIndexAtX(int xPosition) {
	int index = 0;
	Point aPoint = new Point(xPosition, 1);
	Rectangle columnRect = new Rectangle(0,0,0,3);
	Enumeration enumeration = getColumns();

	while (enumeration.hasMoreElements()) {
	    TableColumn aColumn = (TableColumn)enumeration.nextElement();
	    columnRect.width = aColumn.getWidth() + columnMargin;

	    if (columnRect.contains(aPoint))
		return index;

	    columnRect.x += columnRect.width;
	    index++;
	}
	return -1;
    }

    // implements javax.swing.table.TableColumnModel
    public int getTotalColumnWidth() {
	return totalColumnWidth;
    }

//
// Selection model
//

    /**
     *  Sets the selection model for this TableColumnModel to <I>newModel</I>
     *  and registers with for listner notifications from the new selection
     *  model.  If <I>newModel</I> is null, it means columns are not
     *  selectable.
     *
     * @param	newModel	the new selection model
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see	#getSelectionModel
     */
    public void setSelectionModel(ListSelectionModel newModel) {
        if (newModel == null) {
            throw new IllegalArgumentException("Cannot set a null SelectionModel");
        }

	ListSelectionModel oldModel = selectionModel;

	if (newModel != oldModel) {
	    if (oldModel != null) {
		oldModel.removeListSelectionListener(this);
	    }

	    selectionModel= newModel;

	    if (newModel != null) {
		newModel.addListSelectionListener(this);
	    }
	}
    }

    /**
     * Returns the <B>ListSelectionModel</B> that is used to maintain column
     * selection state.
     *
     * @return	the object that provides column selection state.  Or
     *		<B>null</B> if row selection is not allowed.
     * @see	#setSelectionModel()
     */
    public ListSelectionModel getSelectionModel() {
	return selectionModel;
    }

    // implements javax.swing.table.TableColumnModel
    public void setColumnSelectionAllowed(boolean flag) {
	columnSelectionAllowed = flag;
    }

    // implements javax.swing.table.TableColumnModel
    public boolean getColumnSelectionAllowed() {
	return columnSelectionAllowed;
    }

    // implements javax.swing.table.TableColumnModel
    public int[] getSelectedColumns() {
	if (selectionModel != null) {
	    int iMin = selectionModel.getMinSelectionIndex();
	    int iMax = selectionModel.getMaxSelectionIndex();

	    if ((iMin == -1) || (iMax == -1)) {
		return new int[0];
	    }

	    int[] rvTmp = new int[1+ (iMax - iMin)];
	    int n = 0;
	    for(int i = iMin; i <= iMax; i++) {
		if (selectionModel.isSelectedIndex(i)) {
		    rvTmp[n++] = i;
		}
	    }
	    int[] rv = new int[n];
	    System.arraycopy(rvTmp, 0, rv, 0, n);
	    return rv;
	}
	return  new int[0];
    }

    // implements javax.swing.table.TableColumnModel
    public int getSelectedColumnCount() {
	if (selectionModel != null) {
	    int iMin = selectionModel.getMinSelectionIndex();
	    int iMax = selectionModel.getMaxSelectionIndex();
	    int count = 0;

	    for(int i = iMin; i <= iMax; i++) {
		if (selectionModel.isSelectedIndex(i)) {
		    count++;
		}
	    }
	    return count;
	}
	return 0;
    }

//
// Listener Support Methods
//

    // implements javax.swing.table.TableColumnModel
    public void addColumnModelListener(TableColumnModelListener x) {
	listenerList.add(TableColumnModelListener.class, x);
    }

    // implements javax.swing.table.TableColumnModel
    public void removeColumnModelListener(TableColumnModelListener x) {
	listenerList.remove(TableColumnModelListener.class, x);
    }

//
//   Event firing methods
//

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireColumnAdded(TableColumnModelEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableColumnModelListener.class) {
		// Lazily create the event:
		// if (e == null)
		//  e = new ChangeEvent(this);
		((TableColumnModelListener)listeners[i+1]).
		    columnAdded(e);
	    }
	}
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireColumnRemoved(TableColumnModelEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableColumnModelListener.class) {
		// Lazily create the event:
		// if (e == null)
		//  e = new ChangeEvent(this);
		((TableColumnModelListener)listeners[i+1]).
		    columnRemoved(e);
	    }
	}
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireColumnMoved(TableColumnModelEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableColumnModelListener.class) {
		// Lazily create the event:
		// if (e == null)
		//  e = new ChangeEvent(this);
		((TableColumnModelListener)listeners[i+1]).
		    columnMoved(e);
	    }
	}
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireColumnSelectionChanged(ListSelectionEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableColumnModelListener.class) {
		// Lazily create the event:
		// if (e == null)
		//  e = new ChangeEvent(this);
		((TableColumnModelListener)listeners[i+1]).
		    columnSelectionChanged(e);
	    }
	}
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireColumnMarginChanged() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableColumnModelListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((TableColumnModelListener)listeners[i+1]).
		    columnMarginChanged(changeEvent);
	    }
	}
    }


//
// Implementing the PropertyChangeListener interface
//

    // PENDING(alan)
    // implements java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent evt) {
	String name = evt.getPropertyName();

	if (TableColumn.COLUMN_WIDTH_PROPERTY.equals(name)) {
	    recalcWidthCache();
	}
	else if (TableColumn.HEADER_VALUE_PROPERTY.equals(name) ||
		 TableColumn.HEADER_RENDERER_PROPERTY.equals(name)) {
	}
	else if (TableColumn.CELL_RENDERER_PROPERTY.equals(name)) {
	}
    }

//
// Implementing ListSelectionListener interface
//

    // implements javax.swing.event.ListSelectionListener
    public void valueChanged(ListSelectionEvent e) {
	fireColumnSelectionChanged(e);
    }

//
// Protected Methods
//

    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }

    protected void recalcWidthCache() {
	Enumeration enumeration = getColumns();

	totalColumnWidth = 0;

	while (enumeration.hasMoreElements()) {
	    totalColumnWidth += ((TableColumn)enumeration.nextElement()).getWidth() +
				columnMargin;
	}
    }

} // End of class DefaultTableColumnModel

