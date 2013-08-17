/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.EventListener; 
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;

/**
 * The standard column-handler for a <code>JTable</code>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.36 02/06/02
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
	getSelectionModel().setAnchorSelectionIndex(0); 
	setColumnMargin(1); 
	invalidateWidthCache(); 
	setColumnSelectionAllowed(false);
    }

//
// Modifying the model
//

    /**
     *  Appends <code>aColumn</code> to the end of the
     *  <code>tableColumns</code> array.
     *  This method also posts the <code>columnAdded</code>
     *  event to its listeners.
     *
     * @param	column		the <code>TableColumn</code> to be added
     * @exception IllegalArgumentException	if <code>aColumn</code> is
     *				<code>null</code>
     * @see	#removeColumn
     */
    public void addColumn(TableColumn aColumn) {
	if (aColumn == null) {
	    throw new IllegalArgumentException("Object is null");
	}

	tableColumns.addElement(aColumn);
	aColumn.addPropertyChangeListener(this);
	invalidateWidthCache();

	// Post columnAdded event notification
	fireColumnAdded(new TableColumnModelEvent(this, 0,
						  getColumnCount() - 1));
    }

    /**
     *  Deletes the <code>column</code> from the
     *  <code>tableColumns</code> array.  This method will do nothing if
     *  <code>column</code> is not in the table's columns list.
     *  <code>tile</code> is called
     *  to resize both the header and table views.
     *  This method also posts a <code>columnRemoved</code>
     *  event to its listeners.
     *
     * @param	column		the <code>TableColumn</code> to be removed
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
	    invalidateWidthCache();

	    // Post columnAdded event notification.  (JTable and JTableHeader
	    // listens so they can adjust size and redraw)
	    fireColumnRemoved(new TableColumnModelEvent(this,
					   getColumnCount() - 1, 0));
	}
    }

    /**
     * Moves the column and heading at <code>columnIndex</code> to
     * <code>newIndex</code>.  The old column at <code>columnIndex</code>
     * will now be found at <code>newIndex</code>.  The column
     * that used to be at <code>newIndex</code> is shifted
     * left or right to make room.  This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>.  This method
     * also posts a <code>columnMoved</code> event to its listeners.
     *
     * @param	columnIndex			the index of column to be moved
     * @param	newIndex			new index to move the column
     * @exception IllegalArgumentException	if <code>column</code> or
     * 						<code>newIndex</code>
     *						are not in the valid range
     */
    public void moveColumn(int columnIndex, int newIndex) {
	if ((columnIndex < 0) || (columnIndex >= getColumnCount()) ||
	    (newIndex < 0) || (newIndex >= getColumnCount()))
	    throw new IllegalArgumentException("moveColumn() - Index out of range");

	TableColumn aColumn;

	// If the column has not yet moved far enough to change positions 
	// post the event anyway, the "draggedDistance" property of the 
	// tableHeader will say how far the column has been dragged. 
	// Here we are really trying to get the best out of an 
	// API that could do with some rethinking. We preserve backward 
	// compatibility by slightly bending the meaning of these methods. 
	if (columnIndex == newIndex) { 
	    fireColumnMoved(new TableColumnModelEvent(this, columnIndex, newIndex));	    
	    return;
	}
	aColumn = (TableColumn)tableColumns.elementAt(columnIndex);

	boolean reselect = false;
	if (selectionModel.isSelectedIndex(columnIndex)) {
	    selectionModel.removeSelectionInterval(columnIndex,columnIndex);
	    reselect = true;
	}
	tableColumns.removeElementAt(columnIndex);
	tableColumns.insertElementAt(aColumn, newIndex);
	if (reselect) {
	    selectionModel.addSelectionInterval(newIndex, newIndex);
	}
	// Post columnMoved event notification.  (JTable and JTableHeader
	// listens so they can adjust size and redraw)
	fireColumnMoved(new TableColumnModelEvent(this, columnIndex,
							       newIndex));
    }

    /**
     * Sets the column margin to <code>newMargin</code>.  This method
     * also posts a <code>columnMarginChanged</code> event to its
     * listeners.
     *
     * @param	newMargin		the new margin width, in pixels
     * @see	#getColumnMargin
     * @see	#getTotalColumnWidth
     */
    public void setColumnMargin(int newMargin) {
	if (newMargin != columnMargin) {
	    columnMargin = newMargin;
	    // Post columnMarginChanged event notification.
	    fireColumnMarginChanged();
	}
    }

//
// Querying the model
//

    /**
     * Returns the number of columns in the <code>tableColumns</code> array.
     *
     * @return	the number of columns in the <code>tableColumns</code> array
     * @see	#getColumns
     */
    public int getColumnCount() {
	return tableColumns.size();
    }

    /**
     * Returns an <code>Enumeration</code> of all the columns in the model.
     * @return an <code>Enumeration</code> of the columns in the model
     */
    public Enumeration getColumns() {
	return tableColumns.elements();
    }

    /**
     * Returns the index of the first column in the <code>tableColumns</code>
     * array whose identifier is equal to <code>identifier</code>,
     * when compared using <code>equals</code>.
     *
     * @param		identifier		the identifier object
     * @return		the index of the first column in the 
     *			<code>tableColumns</code> array whose identifier
     *			is equal to <code>identifier</code>
     * @exception       IllegalArgumentException  if <code>identifier</code>
     *				is <code>null</code>, or if no
     *				<code>TableColumn</code> has this
     *				<code>identifier</code>
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
     * Returns the <code>TableColumn</code> object for the column
     * at <code>columnIndex</code>.
     *
     * @param	columnIndex	the index of the column desired
     * @return	the <code>TableColumn</code> object for the column
     *				at <code>columnIndex</code>
     */
    public TableColumn getColumn(int columnIndex) {
	return (TableColumn)tableColumns.elementAt(columnIndex);
    }

    /**
     * Returns the width margin for <code>TableColumn</code>.
     * The default <code>columnMargin</code> is 1.
     *
     * @return	the maximum width for the <code>TableColumn</code>
     * @see	#setColumnMargin
     */
    public int getColumnMargin() {
	return columnMargin;
    }

    /**
     * Returns the index of the column that lies at position <code>x</code>,
     * or -1 if no column covers this point.
     *
     * @param  x  the horizontal location of interest
     * @return	the index of the column or -1 if no column is found
     */
    public int getColumnIndexAtX(int x) {
	if (x < 0) { 
	    return -1; 
	}
	int cc = getColumnCount(); 
	for(int column = 0; column < cc; column++) { 
	    x = x - getColumn(column).getWidth(); 
	    if (x < 0) { 
		return column; 
	    }
	}
	return -1; 
    }

    /**
     * Returns the total combined width of all columns.
     * @return the <code>totalColumnWidth</code> property
     */
    public int getTotalColumnWidth() { 
	if (totalColumnWidth == -1) { 
	    recalcWidthCache(); 
	}
	return totalColumnWidth;
    }

//
// Selection model
//

    /**
     *  Sets the selection model for this <code>TableColumnModel</code>
     *  to <code>newModel</code>
     *  and registers for listener notifications from the new selection
     *  model.  If <code>newModel</code> is <code>null</code>,
     *  an exception is thrown.
     *
     * @param	newModel	the new selection model
     * @exception IllegalArgumentException      if <code>newModel</code>
     *						is <code>null</code>
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
     * Returns the <code>ListSelectionModel</code> that is used to
     * maintain column selection state.
     *
     * @return	the object that provides column selection state.  Or
     *		<code>null</code> if row selection is not allowed.
     * @see	#setSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
	return selectionModel;
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Sets whether column selection is allowed.  The default is false.
     * @param  true if column selection will be allowed, false otherwise
     */
    public void setColumnSelectionAllowed(boolean flag) {
	columnSelectionAllowed = flag;
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Returns true if column selection is allowed, otherwise false.
     * The default is false.
     * @return the <code>columnSelectionAllowed</code> property
     */
    public boolean getColumnSelectionAllowed() {
	return columnSelectionAllowed;
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Returns an array of selected columns.  If <code>selectionModel</code>
     * is <code>null</code>, returns an empty array.
     * @return an array of selected columns or an empty array if nothing
     *			is selected or the <code>selectionModel</code> is
     *			<code>null</code>
     */
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
    /**
     * Returns the number of columns selected.
     * @return the number of columns selected
     */
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
    /**
     * Adds a listener for table column model events.
     * @param x  a <code>TableColumnModelListener</code> object
     */
    public void addColumnModelListener(TableColumnModelListener x) {
	listenerList.add(TableColumnModelListener.class, x);
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Removes a listener for table column model events.
     * @param x  a <code>TableColumnModelListener</code> object
     */
    public void removeColumnModelListener(TableColumnModelListener x) {
	listenerList.remove(TableColumnModelListener.class, x);
    }

//
//   Event firing methods
//

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param e  the event received
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

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param  e  the event received
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

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param  e the event received
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

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param e the event received
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

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param  e the event received
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

    /**
     * Returns an array of all the listeners of the given type that 
     * were added to this model. 
     *
     * @param listenerType  the listener class to match
     * @return  all of the objects receiving <code>listenerType</code>
     *		notifications from this model
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }

//
// Implementing the PropertyChangeListener interface
//

    // PENDING(alan)
    // implements java.beans.PropertyChangeListener
    /**
     * Property Change Listener change method.  Used to track changes
     * to the column width or preferred column width.
     *
     * @param  evt  <code>PropertyChangeEvent</code>
     */
    public void propertyChange(PropertyChangeEvent evt) {
	String name = evt.getPropertyName();

	if (name == "width" || name == "preferredWidth") {
	    invalidateWidthCache(); 
	    // This is a misnomer, we're using this method 
	    // simply to cause a relayout. 
	    fireColumnMarginChanged(); 
	}

    }

//
// Implementing ListSelectionListener interface
//

    // implements javax.swing.event.ListSelectionListener
    /**
     * A <code>ListSelectionListener</code> that forwards
     * <code>ListSelectionEvents</code> when there is a column
     * selection change.
     *
     * @param e  the change event
     */
    public void valueChanged(ListSelectionEvent e) {
	fireColumnSelectionChanged(e);
    }

//
// Protected Methods
//

    /**
     * Creates a new default list selection model.
     */
    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }

    /**
     * Recalculates the total combined width of all columns.  Updates the
     * <code>totalColumnWidth</code> property.
     */
    protected void recalcWidthCache() {
	Enumeration enumeration = getColumns();
	totalColumnWidth = 0;
	while (enumeration.hasMoreElements()) {
	    totalColumnWidth += ((TableColumn)enumeration.nextElement()).getWidth();
	}
    } 

    private void invalidateWidthCache() { 
	totalColumnWidth = -1; 
    }

} // End of class DefaultTableColumnModel

