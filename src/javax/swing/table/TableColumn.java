/*
 * @(#)TableColumn.java	1.34 98/10/21
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
import javax.swing.border.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.lang.Integer;
import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *  A <B>TableColumn</B> represents all the attributes of a column in a
 *  <B>JTable</B>, such as width, resizibility, minimum and maximum width.
 *  In addition, the <B>TableColumn</B> provides slots for a renderer and 
 *  editor that can be used to display and edit the values in this column. 
 *  <p>
 *  It is also possible to specify renderers and editors on a per type basis
 *  rather than a per column basis - see the <I>setDefaultRenderer(Class)</I> method
 *  in the <B>JTable</B>. This default mechanism is only used when the renderer (or
 *  editor) in the <B>TableColumn</B> is <I>null</I>.
 * <p>
 *  The TableColumn stores the link between the columns in the <B>JTable</B>
 *  and the columns in the <B>TableModel</B>. This, the <I>modelIndex</I>, is the
 *  column in the TableModel which will be queried for the data values for the
 *  cells in this column. As the column moves around in the view this
 *  <I>modelIndex</I> does not change.
 *  <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.34 10/21/98
 * @author Alan Chung
 * @author Philip Milne
 * @see TableColumnModel
 *
 * @see DefaultTableColumnModel
 * @see JTable#getDefaultRenderer(Class)
 * @see JTable#getDefaultEditor(Class)
 * @see JTable#getCellRenderer(int, int)
 * @see JTable#getCellEditor(int, int)
 */
public class TableColumn extends Object implements Serializable {
//
// Static Constants
//

    /** Bound property name. */
    public final static String COLUMN_WIDTH_PROPERTY = "columWidth";
    /** Bound property name. */
    public final static String HEADER_VALUE_PROPERTY = "headerValue";
    /** Bound property name. */
    public final static String HEADER_RENDERER_PROPERTY = "headerRenderer";
    /** Bound property name. */
    public final static String CELL_RENDERER_PROPERTY = "cellRenderer";

//
//  Instance Variables
//

    /**
      * The index of the column in the model which is to be displayed by
      * this TableColumn. As columns are moved around in the view the
      * model index remains constant.
      */
    protected int	modelIndex;

    /**
     *  This object is not used internally by the drawing machinery of
     *  the JTable. Identifiers may be set in the TableColumn as as an
     *  optional way to tag and locate TableColumns. The table package does
     *  not modify or invoke any methods in these identifer objects other
     *  than the <I>equals</I> method which is used in the
     *  <I>getColumnIndex()</I> method in the <B>DefaultTableColumnModel</B>.
     */
    protected Object	identifier;

    /** The width of the column */
    protected int	width;

    /** The minimum width of the column */
    protected int	minWidth;

    /** The minimum width of the column */
    private int         preferredWidth;

    /** The maximum width of the column */
    protected int	maxWidth;

    /** The renderer used to draw the header of the column */
    protected TableCellRenderer	headerRenderer;

    /** The header value of the column */
    protected Object		headerValue;

    /** The renderer used to draw the data cells of the column */
    protected TableCellRenderer	cellRenderer;

    /** The editor used to edit the data cells of the column */
    protected TableCellEditor	cellEditor;

    /** Resizable flag */
    protected boolean	isResizable;

    /**
     *  Counter used to disable posting of resizing notifications until the
     *  end of the resize
     */
    transient protected int	resizedPostingDisableCount;

    /**
     * If any PropertyChangeListeners have been registered, the
     * changeSupport field describes them.
     */
    private SwingPropertyChangeSupport changeSupport;

//
// Constructors
//

    /** 
     *  Cover method, using a default model index of 0, 
     *  default width of 75, a null renderer and a null editor. 
     *  This methods is intended for serialization. 
     *  @see #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public TableColumn() {
	this(0);
    }

    /** 
     *  Cover method, using a default width of 75, a null renderer and a null editor. 
     *  @see #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public TableColumn(int modelIndex) {
	this(modelIndex, 75, null, null);
    }

    /** 
     *  Cover method, using a null renderer and a null editor. 
     *  @see #TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public TableColumn(int modelIndex, int width) {
	this(modelIndex, width, null, null);
    }

    /**
     *  Creates and initializes an instance of <B>TableColumn</B> with
     *  <I>modelIndex</I>. The <I>modelIndex</I> is the index of the column 
     *  in the model which will supply the data for this column in the table. 
     *  The modelIndex does not change as the columns are reordered in the view. 
     *  The width parameter is used to set both the preferredWidth for this 
     *  column and the intial width. The renderer and editor are the objects 
     *  used repsectively to render and edit values in this column. When 
     *  these are null, default values, provided by the getDefaultRenderer(Class) 
     *  and getDefaultEditor(Class) methods in the JTable are used to 
     *  provide defaults based on the type of the data in this column. 
     *  This column-centric rendering strategy can be circumvented by 
     *  overriding the getCellRenderer() methods in the JTable. 
     *  <p>
     *
     * @param	modelIndex	the column in the model which provides the values for this column
     * @see JTable#getDefaultRenderer(Class)
     * @see JTable#getDefaultEditor(Class)
     * @see JTable#getCellRenderer(int, int)
     * @see JTable#getCellEditor(int, int)
     */
    public TableColumn(int modelIndex, int width,
				 TableCellRenderer cellRenderer,
				 TableCellEditor cellEditor) {
	super();
	this.modelIndex = modelIndex;
	this.width = width;
	this.preferredWidth = width;

	this.cellRenderer = cellRenderer;
	this.cellEditor = cellEditor;

	// Set other instance variables to default values.
	minWidth = 15;
	maxWidth = Integer.MAX_VALUE;
	isResizable = true;
	resizedPostingDisableCount = 0;
	setHeaderRenderer(createDefaultHeaderRenderer());
	headerValue = null;
    }

//
// Modifying and Querying attributes
//

    /**
     * Sets the model index for this column. The model index is the
     * index of the column in the model that will be displayed by this
     * TableColumn. As the TableColumn is moved around in the view
     * the model index remains constant.
     */
    public void setModelIndex(int anIndex)
    {
	modelIndex = anIndex;
    }

    /**
     * Gets the model index for this column.
     */
    public int getModelIndex()
    {
	return modelIndex;
    }

    /**
     * Sets the <B>TableColumn</B>'s identifier to <I>anIdentifier</I>.
     * Note identifiers are not used by the JTable, they are purely a
     * convenience for the external tagging and location of columns.
     *
     * @param	   anIdentifier		an identifier for this column
     * @see	   #getIdentifier
     */
    public void setIdentifier(Object anIdentifier)
    {
	identifier = anIdentifier;
    }


    /**
     *  Returns the identifier object for this column. Note identifiers are not
     *  used by the JTable, they are purely a convenience for external use.
     *  If the identifier is <I>null</I> <I>getIdentifier()</I> returns
     *  <code>getHeaderValue()</code> as a default.
     *
     * @return	the idenitifer object for this column
     * @see	#setIdentifier
     */
    public Object getIdentifier()
    {
        return (identifier != null) ? identifier : getHeaderValue();

    }

    /**
     * Sets the <B>TableCellRenderer</B> used to draw the <B>TableColumn's</B>
     * header to <I>aRenderer</I>.  Posts a bound property change notification
     * with the name HEADER_RENDERER_PROPERTY.
     *
     * @exception IllegalArgumentException	if <I>aRenderer</I> is null.
     * @param	  aRenderer			the new header renderer
     * @see	  #getHeaderRenderer
     */
    public void setHeaderRenderer(TableCellRenderer aRenderer)
    {
	TableCellRenderer oldRenderer = headerRenderer;

	if (aRenderer == null) {
	    throw new IllegalArgumentException("Object is null");
	}
	headerRenderer = aRenderer;

	// Post header renderer changed event notification
	if (changeSupport != null) {
	    changeSupport.firePropertyChange(HEADER_RENDERER_PROPERTY,
					     oldRenderer, headerRenderer);
	}
    }

    /**
     * Returns the <B>TableCellRenderer</B> used to draw the header of the
     * <B>TableColumn</B>. The default header renderer is a
     * <B>JCellRenderer</B> initialized with a <B>JLabel</B>.
     *
     * @return	the <B>TableCellRenderer</B> used to draw the header
     * @see	#setHeaderRenderer
     * @see	#setHeaderValue
     */
    public TableCellRenderer getHeaderRenderer()
    {
	return headerRenderer;
    }

    /**
     * Sets the <B>Object</B> used as the value for the headerRenderer
     * Posts a bound property change notification with the name
     * HEADER_VALUE_PROPERTY.
     *
     * @param	  aValue			the new header value
     * @see	  #getHeaderValue
     */
    public void setHeaderValue(Object aValue)
    {
	Object oldValue = headerValue;

	headerValue = aValue;

	// Post header value changed event notification
	if (changeSupport != null) {
	    changeSupport.firePropertyChange(HEADER_VALUE_PROPERTY,
					     oldValue, headerValue);
	}
    }

    /**
     * Returns the <B>Object</B> used as the value for the header renderer.
     *
     * @return	the <B>Object</B> used as the value for the header renderer
     * @see	#setHeaderValue
     */
    public Object getHeaderValue() {
	return headerValue;
    }

    /**
     * Sets the <B>TableCellRenderer</B> used by <B>JTable</B> to draw
     * individual values for this column to <I>aRenderer</I>.  Posts a
     * bound property change notification with the name CELL_RENDERER_PROPERTY.
     *
     * @param	aRenderer			the new data cell renderer
     * @see	#getCellRenderer
     */
    public void setCellRenderer(TableCellRenderer aRenderer)
    {
	TableCellRenderer oldRenderer = cellRenderer;

	cellRenderer = aRenderer;

	// Post cell renderer changed event notification
	if (changeSupport != null) {
	    changeSupport.firePropertyChange(CELL_RENDERER_PROPERTY,
					     oldRenderer, cellRenderer);
	}
    }

    /**
     * Returns the <B>TableCellRenderer</B> used by the <B>JTable</B> to draw
     * values for this column.  The <I>cellRenderer</I> of the column not
     * only controls the visual look for the column, but is also used to
     * interpret the value object supplied by the TableModel. When the 
     * <I>cellRenderer</I> is null, the JTable uses a default renderer based on 
     * class of the cells in that column. The default value for a 
     * <I>cellRenderer</I> is null.  
     *
     * @return	the <B>TableCellRenderer</B> used by the <B>JTable</B> to
     * 		draw values for this column
     * @see	#setCellRenderer
     * @see	JTable#setDefaultRenderer
     */
    public TableCellRenderer getCellRenderer()
    {
	return cellRenderer;
    }

    /**
     * Sets the <B>TableCellEditor</B> used by <B>JTable</B> to draw individual
     * values for this column to <I>anEditor</I>.  
     *
     * @param	anEditor			the new data cell editor
     * @see	#getCellEditor
     */
    public void setCellEditor(TableCellEditor anEditor)
    {
	cellEditor = anEditor;
    }

    /**
     * Returns the <B>TableCellEditor</B> used by the <B>JTable</B> to draw
     * values for this column.  The <I>cellEditor</I> of the column not
     * only controls the visual look for the column, but is also used to
     * interpret the value object supplied by the TableModel. When the 
     * <I>cellEditor</I> is null, the JTable uses a default editor based on 
     * class of the cells in that column. The default value for a 
     * <I>cellEditor</I> is null.  
     * 
     *
     * @return	the <B>TableCellEditor</B> used by the <B>JTable</B> to
     * 		draw values for this column
     * @see	#setCellEditor
     * @see	JTable#setDefaultEditor
     */
    public TableCellEditor getCellEditor()
    {
	return cellEditor;
    }

    /**
     * This method should not be used to set the widths of columns in the JTable - 
     * use, setPreferredWidth() instead. Like a layout manager in the 
     * AWT, the JTable adjusts a column's width automatically whenever the 
     * table itself changes size, or a column's preferred width is changed. 
     * Setting widths programmatically therefore has no long term effect. 
     * <p>
     * This methods, sets this column's width to <I>newWidth</I>.  
     * If <I>newWidth</I> exceeds the minimum or maximum width, 
     * it's adjusted to the appropriate limiting value. Posts a bound property
     * change notification with the name COLUMN_WIDTH_PROPERTY.
     * <p>
     * @param	newWidth		The new width value
     * @see	#getWidth
     * @see	#setMinWidth
     * @see	#setMaxWidth
     * @see	#setPreferredWidth
     * @see     JTable#sizeColumnsToFit(int)
     */
    public void setWidth(int width)
    {
	int oldWidth = this.width;

	// Set the width, and check min & max
	this.width = Math.min(Math.max(width, minWidth), maxWidth); 

	// Post resize event notification
	if (changeSupport != null && this.width != oldWidth) {
	    changeSupport.firePropertyChange(COLUMN_WIDTH_PROPERTY,
					   new Integer(oldWidth), new Integer(this.width));
	}
    }

    /**
     * Returns the width of the <B>TableColumn</B>. The default width is
     * 75.
     *
     * @return	the width of the <B>TableColumn</B>
     * @see	#setWidth
     */
    public int getWidth()
    {
	return width;
    }

    /**
     * Sets this column's preferred width to <I>preferredWidth</I>.  
     * If <I>preferredWidth</I> exceeds the minimum or maximum width, 
     * it's adjusted to the appropriate limiting value. 
     * <p>
     * For details on how the widths of columns in the JTable 
     * (and JTableHeader) are calculated from the preferredWidth, 
     * see the sizeColumnsToFit(int) method in the JTable. 
     *
     * @param	preferredWidth		The new preferred width.
     * @see	#getPreferredWidth
     * @see     JTable#sizeColumnsToFit(int)
     */
    public void setPreferredWidth(int preferredWidth) {
	this.preferredWidth = Math.min(Math.max(preferredWidth, minWidth), maxWidth); 
    }

    /**
     * Returns the preferred width of the <B>TableColumn</B>. 
     * The default preferred width is 75.
     *
     * @return	the width of the <B>TableColumn</B>
     * @see	#setPreferredWidth
     */
    public int getPreferredWidth() {
	return preferredWidth;
    }

    /**
     * Sets the <B>TableColumn's</B> minimum width to <I>newMinWidth</I>,
     * also adjusting the current width if it's less than this value.
     *
     * @param	newMinWidth		the new minimum width value
     * @see	#getMinWidth
     * @see	#setPreferredWidth
     * @see	#setMaxWidth
     */
    public void setMinWidth(int minWidth)
    {
	this.minWidth = Math.max(minWidth, 0);

	if (width < minWidth) {
	    setWidth(minWidth);
	}
    }

    /**
     * Returns the minimum width for the <B>TableColumn</B>. The
     * <B>TableColumn's</B> width can't be made less than this either
     * by the user or programmatically.  The default minWidth is 15.
     *
     * @return	the minimum width for the <B>TableColumn</B>
     * @see	#setMinWidth
     */
    public int getMinWidth()
    {
	return minWidth;
    }

    /**
     * Sets the <B>TableColumn's</B> maximum width to <I>newMaxWidth</I>,
     * also adjusting the current width if it's greater than this value.
     *
     * @param	newMaxWidth		the new maximum width value
     * @see	#getMaxWidth
     * @see	#setPreferredWidth
     * @see	#setMinWidth
     */
    public void setMaxWidth(int maxWidth)
    {
	this.maxWidth = Math.max(minWidth, maxWidth);

	if (width > maxWidth) {
	    this.setWidth(maxWidth); 
	}
    }

    /**
     * Returns the maximum width for the <B>TableColumn</B>. The
     * <B>TableColumn's</B> width can't be made larger than this
     * either by the user or programmatically.  The default maxWidth
     * is Integer.MAX_VALUE.
     *
     * @return	the maximum width for the <B>TableColumn</B>.
     * @see	#setMaxWidth
     */
    public int getMaxWidth()
    {
	return maxWidth;
    }

    /**
     * Sets whether the user can resize the receiver in its
     * <B>JTableView</B>.
     *
     * @param	flag		true if the column isResizable
     * @see	#getResizable
     */
    public void setResizable(boolean flag)
    {
	isResizable = flag;
    }

    /**
     * Returns true if the user is allowed to resize the <B>TableColumn</B>
     * width, false otherwise. You can change the width programmatically
     * regardless of this setting.  The default is true.
     *
     * @return	true if the user is allowed to resize the <B>TableColumn</B>
     * 		width, false otherwise.
     * @see	#setResizable
     */
    public boolean getResizable()
    {
	return isResizable;
    }

    /**
     * Resizes the <B>TableColumn</B> to fit the width of its header cell.
     * If the maximum width is less than the width of the header, the
     * maximum is increased to the header's width. Similarly, if the
     * minimum width is greater than the width of the header, the minimum
     * is reduced to the header's width.
     *
     * @see	#setPreferredWidth
     */
    public void sizeWidthToFit() {
	// Get the preferred width of the header
        Component comp;
	comp = this.getHeaderRenderer().getTableCellRendererComponent(null,
				getHeaderValue(), false, false, 0, 0);
	int headerWidth = comp.getPreferredSize().width;

	// Have to adjust the max or min before setting the width
	if (headerWidth > this.getMaxWidth())
	    this.setMaxWidth(headerWidth);
	if (headerWidth < this.getMinWidth())
	    this.setMinWidth(headerWidth);

	// Set the width
	this.setWidth(headerWidth);
    }

    /**
     * Turns off listener-notifications would otherwise occur 
     * when a column is resized.
     */
    public void disableResizedPosting() {
	resizedPostingDisableCount++;
    }

    /**
     * Turns on listener-notifications so that listeners are once
     * again informed when a column is resized.
     */
    public void enableResizedPosting() {
	resizedPostingDisableCount--;
    }

//
// Property Change Support
//

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A PropertyChangeEvent will get fired in response to an
     * explicit setFont, setBackground, or SetForeground on the
     * current component.  Note that if the current component is
     * inheriting its foreground, background, or font from its
     * container, then no event will be fired in response to a
     * change in the inherited property.
     *
     * @param listener  The PropertyChangeListener to be added
     */

    public synchronized void addPropertyChangeListener(
                                PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     */

    public synchronized void removePropertyChangeListener(
                                PropertyChangeListener listener) {
        if (changeSupport != null) {
	    changeSupport.removePropertyChangeListener(listener);
	}
    }

//
// Protected Methods
//

    protected TableCellRenderer createDefaultHeaderRenderer() {
	DefaultTableCellRenderer label = new DefaultTableCellRenderer() {
	    public Component getTableCellRendererComponent(JTable table, Object value,
                         boolean isSelected, boolean hasFocus, int row, int column) {
	        if (table != null) {
	            JTableHeader header = table.getTableHeader();
	            if (header != null) {
	                setForeground(header.getForeground());
	                setBackground(header.getBackground());
	                setFont(header.getFont());
	            }
                }

                setText((value == null) ? "" : value.toString());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	        return this;
            }
	};
	label.setHorizontalAlignment(JLabel.CENTER);
	return label;
    }

} // End of class TableColumn
