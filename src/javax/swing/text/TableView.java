/*
 * @(#)TableView.java	1.18 98/08/26
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
package javax.swing.text;

import java.awt.*;
import javax.swing.SizeRequirements;

/**
 * <p>
 * Implements View interface for a table, that is composed of an
 * element structure where the child elements of the element
 * this view is responsible for represent rows and the child 
 * elements of the row elements are cells.  The cell elements can
 * have an arbitrary element structure under them, which will
 * be built with the ViewFactory returned by the getViewFactory
 * method.
 * <pre>
 *
 *   TABLE
 *     ROW
 *       CELL
 *       CELL
 *     ROW
 *       CELL
 *       CELL
 *
 * </pre>
 * <p>
 * This is implemented as a hierarchy of boxes, the table itself
 * is a vertical box, the rows are horizontal boxes, and the cells
 * are vertical boxes.  The cells are allowed to span multiple
 * columns and rows.  By default, the table can be thought of as
 * being formed over a grid (i.e. somewhat like one would find in
 * gridbag layout), where table cells can request to span more
 * than one grid cell.  The default horizontal span of table cells
 * will be based upon this grid, but can be changed by reimplementing
 * the requested span of the cell (i.e. table cells can have independant
 * spans if desired).
 * 
 * @author  Timothy Prinzing
 * @version 1.18 08/26/98
 * @see     View
 */
public abstract class TableView extends BoxView {

    /**
     * Constructs a TableView for the given element.
     *
     * @param elem the element that this view is responsible for
     */
    public TableView(Element elem) {
	super(elem, View.Y_AXIS);
    }

    /**
     * Creates a new table row.
     *
     * @param elem an element
     * @return the row
     */
    protected TableRow createTableRow(Element elem) {
	return new TableRow(elem);
    }

    /**
     * Creates a new table cell.
     *
     * @param elem an element
     * @return the cell
     */
    protected TableCell createTableCell(Element elem) {
	return new TableCell(elem);
    }
    
    /**
     * The number of columns in the table.
     */
    int getColumnCount() {
	return columnSpans.length;
    }

    /**
     * Fetches the span (width) of the given column.  
     * This is used by the nested cells to query the 
     * sizes of grid locations outside of themselves.
     */
    int getColumnSpan(int col) {
	return columnSpans[col];
    }

    /**
     * The number of rows in the table.
     */
    int getRowCount() {
	return getViewCount();
    }

    /**
     * Fetches the span (height) of the given row.
     * This is used by the nested cells to query the 
     * sizes of grid locations outside of themselves.
     */
    int getRowSpan(int row) {
	if (row < getViewCount()) {
	    return getSpan(Y_AXIS, row);
	}
	return 0;
    }

    /**
     * Loads all of the children to initialize the view.
     * This is called by the <code>setParent</code> method.
     * This is reimplemented to build rows using the
     * <code>createTableRow</code> method and then 
     * proxy cell entries for each of the cells that
     * span multiple columns or rows, substantially 
     * reducing the complexity of the layout calculations.
     *
     * @param f the view factory
     */
    protected void loadChildren(ViewFactory f) {
	Element e = getElement();

	//AbstractDocument.AbstractElement ae = (AbstractDocument.AbstractElement)e;
	//ae.dump(System.out, 0);

	int n = e.getElementCount();
	for (int i = 0; i < n; i++) {
	    // elements that represent something other than rows
	    // should return null.
	    View v = createTableRow(e.getElement(i));
	    if (v != null) {
		append(v);
	    }
	}

	// fill in the proxy cells
	loadProxyCells();
    }

    /**
     * Fill in the proxy cells that are placeholders
     * for multi-column, multi-row, and missing grid
     * locations.
     */
    void loadProxyCells() {
	// fill in the proxy cells
	int n = getViewCount();
	for (int row = 0; row < n; row++) {
	    View rv = getView(row);
	    for (int col = 0; col < rv.getViewCount(); col++) {
		View cv = rv.getView(col);
		if (cv instanceof TableCell) {
		    TableCell cell = (TableCell) cv;
		    if ((cell.getColumnCount() > 1) ||
			(cell.getRowCount() > 1)) {
			// fill in the proxy entries for this cell
			int rowLimit = row + cell.getRowCount();
			int colLimit = col + cell.getColumnCount();
			for (int i = row; i < rowLimit; i++) {
			    for (int j = col; j < colLimit; j++) {
				if (i != row || j != col) {
				    addProxy(i, j, cell);
				}
			    }
			}
		    }
		}
	    }
	}
    }

    /**
     * Layout the columns to fit within the given target span.
     *
     * @param targetSpan the given span for total of all the table
     *  columns.
     * @param reqs the requirements desired for each column.  This
     *  is the column maximum of the cells minimum, preferred, and
     *  maximum requested span.  
     * @param spans the return value of how much to allocated to
     *  each column.
     * @param offsets the return value of the offset from the
     *  origin for each column.
     * @returns the offset from the origin and the span for each column 
     *  in the offsets and spans parameters.
     */
    protected void layoutColumns(int targetSpan, int[] offsets, int[] spans, 
				 SizeRequirements[] reqs) {
	// allocate using the convenience method on SizeRequirements
	SizeRequirements.calculateTiledPositions(targetSpan, null, reqs, 
						 offsets, spans);
    }

    /**
     * Adds a cell to fill in for another cells overflow.  The proxy cells
     * are simply for simplification of layout and have no useful semantics.
     */
    void addProxy(int row, int col, TableCell host) {
	TableRow rv = (TableRow) getView(row);
	if (rv != null) {
	    // if the column is not a valid location, it means
	    // some grid points need to be synthesized.
	    int needed = rv.getViewCount();
	    for (int i = rv.getViewCount(); i < col; i++) {
		rv.insert(i, new ProxyCell(getElement()));
	    }

	    // insert the requested cell
	    rv.insert(col, new ProxyCell(host));
	}
    }

    /**
     * Perform layout for the minor axis of the box (i.e. the
     * axis orthoginal to the axis that it represents).  The results 
     * of the layout should be placed in the given arrays which represent 
     * the allocations to the children along the minor axis.  This 
     * is called by the superclass whenever the layout needs to be 
     * updated along the minor axis.
     * <p>
     * This is implemented to call the 
     * <a href="#layoutColumns">layoutColumns</a> method, and then
     * forward to the superclass to actually carry out the layout
     * of the tables rows.
     *
     * @param targetSpan the total span given to the view, which
     *  whould be used to layout the children.
     * @param axis the axis being layed out.
     * @param offsets the offsets from the origin of the view for
     *  each of the child views.  This is a return value and is
     *  filled in by the implementation of this method.
     * @param spans the span of each child view.  This is a return
     *  value and is filled in by the implementation of this method.
     * @returns the offset and span for each child view in the
     *  offsets and spans parameters.
     */
    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	layoutColumns(targetSpan, columnOffsets, columnSpans, columnRequirements);
	super.layoutMinorAxis(targetSpan, axis, offsets, spans);
    }

    /**
     * Calculate the requirements for the minor axis.  This is called by
     * the superclass whenever the requirements need to be updated (i.e.
     * a preferenceChanged was messaged through this view).  
     * <p>
     * This is implemented to calculate the requirements as the sum of the 
     * requirements of the columns.
     */
    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	int ncols = 0;
	int nrows = getViewCount();
	for (int i = 0; i < nrows; i++) {
	    View row = getView(i);
	    ncols = Math.max(ncols, row.getViewCount());
	}

	columnSpans = new int[ncols];
	columnOffsets = new int[ncols];
	columnRequirements = new SizeRequirements[ncols];
	for (int i = 0; i < ncols; i++) {
	    columnRequirements[i] = new SizeRequirements();
	}

	// calculate column requirements for each column
	for (int i = 0; i < nrows; i++) {
	    View row = getView(i);
	    ncols = row.getViewCount();
	    for (int j = 0; j < ncols; j++) {
		View v = row.getView(j);
		SizeRequirements req = columnRequirements[j];
		req.minimum = Math.max((int) v.getMinimumSpan(axis),
				       req.minimum);
		req.preferred = Math.max((int) v.getPreferredSpan(axis),
				       req.preferred);
		req.maximum = Math.max((int) v.getMaximumSpan(axis),
				       req.maximum);
		if (v instanceof GridCell) {
		    GridCell cell = (GridCell) v;
		    cell.setGridLocation(i, j);
		}
	    }
	}

	// the requirements are the sum of the columns.
	if (r == null) {
	    r = new SizeRequirements();
	}
	long min = 0;
	long pref = 0;
	long max = 0;
	for (int i = 0; i < ncols; i++) {
	    SizeRequirements req = columnRequirements[i];
	    min += req.minimum;
	    pref += req.preferred;
	    max += req.maximum;
	}
	r.minimum = (int) min;
	r.preferred = (int) pref;
	r.maximum = (int) max;
	r.alignment = 0;
	return r;
    }

    /**
     * Fetches the child view that represents the given position in
     * the model.  This is implemented to walk through the children
     * looking for a range that contains the given position.  In this
     * view the children do not necessarily have a one to one mapping 
     * with the child elements.
     *
     * @param pos  the search position >= 0
     * @param a  the allocation to the table on entry, and the
     *   allocation of the view containing the position on exit
     * @returns  the view representing the given position, or 
     *   null if there isn't one
     */
    protected View getViewAtPosition(int pos, Rectangle a) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            int p0 = v.getStartOffset();
            int p1 = v.getEndOffset();
            if ((pos >= p0) && (pos < p1)) {
                // it's in this view.
		if (a != null) {
		    childAllocation(i, a);
		}
                return v;
            }
        }
	if (pos == getEndOffset()) {
	    View v = getView(n - 1);
	    if (a != null) {
		this.childAllocation(n - 1, a);
	    }
	    return v;
	}
        return null;
    }

    // ---- variables ----------------------------------------------------

    int[] columnSpans;
    int[] columnOffsets;
    SizeRequirements[] columnRequirements;

    /**
     * View of a row in a table.
     */
    public class TableRow extends BoxView {

	/**
	 * Constructs a TableView for the given element.
	 *
	 * @param elem the element that this view is responsible for
	 */
        public TableRow(Element elem) {
	    super(elem, View.X_AXIS);
	}

	/**
	 * Perform layout for the major axis of the box (i.e. the
	 * axis that it represents).  The results of the layout should
	 * be placed in the given arrays which represent the allocations
	 * to the children along the major axis.  
	 * <p>
	 * This is re-implemented to give each child the span of the column 
	 * width for the table, and to give cells that span multiple columns 
	 * the multi-column span.
	 *
	 * @param targetSpan the total span given to the view, which
	 *  whould be used to layout the children.
	 * @param axis the axis being layed out.
	 * @param offsets the offsets from the origin of the view for
	 *  each of the child views.  This is a return value and is
	 *  filled in by the implementation of this method.
	 * @param spans the span of each child view.  This is a return
	 *  value and is filled in by the implementation of this method.
	 * @returns the offset and span for each child view in the
	 *  offsets and spans parameters.
	 */
        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	    System.arraycopy(columnOffsets, 0, offsets, 0, offsets.length);
	    System.arraycopy(columnSpans, 0, spans, 0, spans.length);

	    // spread out multi-column cells
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		if (v instanceof TableCell) {
		    TableCell cell = (TableCell) v;
		    int ncols = cell.getColumnCount();
		    if (ncols > 1) {
			for (int j = 1; j < ncols; j++) {
			    spans[i] += spans[i+j];
			}
		    }
		}
	    }
	}

	/**
	 * Perform layout for the minor axis of the box (i.e. the
	 * axis orthoginal to the axis that it represents).  The results 
	 * of the layout should be placed in the given arrays which represent 
	 * the allocations to the children along the minor axis.  This 
	 * is called by the superclass whenever the layout needs to be 
	 * updated along the minor axis.
	 * <p>
	 * This is implemented to delegate to the superclass, then adjust
	 * the span for any cell that spans multiple rows.
	 *
	 * @param targetSpan the total span given to the view, which
	 *  whould be used to layout the children.
	 * @param axis the axis being layed out.
	 * @param offsets the offsets from the origin of the view for
	 *  each of the child views.  This is a return value and is
	 *  filled in by the implementation of this method.
	 * @param spans the span of each child view.  This is a return
	 *  value and is filled in by the implementation of this method.
	 * @returns the offset and span for each child view in the
	 *  offsets and spans parameters.
	 */
        protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	    super.layoutMinorAxis(targetSpan, axis, offsets, spans);

	    // spread out multi-row cells
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		if (v instanceof TableCell) {
		    TableCell cell = (TableCell) v;
		    int nrows = cell.getRowCount();
		    if (nrows > 1) {
			for (int j = 1; j < nrows; j++) {
			    spans[i] += getRowSpan(cell.getGridRow()+j);
			}
		    }
		}
	    }
	}

	/**
	 * Loads all of the children to initialize the view.
	 * This is called by the <code>setParent</code> method.
	 * This is reimplemented to build cells using the
	 * <code>createTableCell</code> method.
	 *
	 * @param f the view factory
	 */
        protected void loadChildren(ViewFactory f) {
	    Element e = getElement();
	    int n = e.getElementCount();
	    if (n > 0) {
		View[] added = new View[n];
		for (int i = 0; i < n; i++) {
		    added[i] = createTableCell(e.getElement(i));
		}
		replace(0, 0, added);
	    }
	}
	
	/**
	 * Determines the resizability of the view along the
	 * given axis.  A value of 0 or less is not resizable.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @return the resize weight
	 * @exception IllegalArgumentException for an invalid axis
	 */
        public int getResizeWeight(int axis) {
	    return 1;
	}

	/**
	 * Fetches the child view that represents the given position in
	 * the model.  This is implemented to walk through the children
	 * looking for a range that contains the given position.  In this
	 * view the children do not necessarily have a one to one mapping 
	 * with the child elements.
	 *
	 * @param pos  the search position >= 0
	 * @param a  the allocation to the table on entry, and the
	 *   allocation of the view containing the position on exit
	 * @returns  the view representing the given position, or 
	 *   null if there isn't one
	 */
        protected View getViewAtPosition(int pos, Rectangle a) {
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		int p0 = v.getStartOffset();
		int p1 = v.getEndOffset();
		if ((pos >= p0) && (pos < p1)) {
		    // it's in this view.
		    if (a != null) {
			childAllocation(i, a);
		    }
		    return v;
		}
	    }
	    if (pos == getEndOffset()) {
		View v = getView(n - 1);
		if (a != null) {
		    this.childAllocation(n - 1, a);
		}
		return v;
	    }
	    return null;
	}

    }

    interface GridCell {

        /**
         * Sets the grid location.
         *
         * @param row the row >= 0
         * @param col the column >= 0
         */
        public void setGridLocation(int row, int col);

	/**
	 * Gets the row of the grid location
	 */
	public int getGridRow();

	/**
	 * Gets the column of the grid location
	 */
	public int getGridColumn();

	/**
	 * Gets the number of columns this cell spans (e.g. the
	 * grid width).
         *
         * @return the number of columns
	 */
	public int getColumnCount();

	/**
	 * Gets the number of rows this cell spans (that is, the
	 * grid height).
         *
         * @return the number of rows
	 */
	public int getRowCount();

    }

    /**
     * View of a cell in a table
     */
    public class TableCell extends BoxView implements GridCell {

	/**
	 * Constructs a TableCell for the given element.
	 *
	 * @param elem the element that this view is responsible for
	 */
        public TableCell(Element elem) {
	    super(elem, View.Y_AXIS);
	}
	
	// --- GridCell methods -------------------------------------

	/**
	 * Gets the number of columns this cell spans (e.g. the
	 * grid width).
         *
         * @return the number of columns
	 */
	public int getColumnCount() {
	    return 1;
	}

	/**
	 * Gets the number of rows this cell spans (that is, the
	 * grid height).
         *
         * @return the number of rows
	 */
	public int getRowCount() {
	    return 1;
	}


        /**
         * Sets the grid location.
         *
         * @param row the row >= 0
         * @param col the column >= 0
         */
        public void setGridLocation(int row, int col) {
            this.row = row;
            this.col = col;
	}

	/**
	 * Gets the row of the grid location
	 */
        public int getGridRow() {
	    return row;
	}

	/**
	 * Gets the column of the grid location
	 */
        public int getGridColumn() {
	    return col;
	}

	// --- View methods -----------------------------

	/**
	 * Determines the preferred span for this view along an
	 * axis.  This is implemented to return the preferred span 
	 * reported by the superclass divided by the row/column count 
	 * for the cell so that multi-column and multi-row cells 
	 * distribute their requirements across all the columns/rows
	 * that they participate in.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @returns  the span the view would like to be rendered into.
	 *           Typically the view is told to render into the span
	 *           that is returned, although there is no guarantee.  
	 *           The parent may choose to resize or break the view.
	 */
        public float getPreferredSpan(int axis) {
	    if (axis == X_AXIS) {
		return super.getPreferredSpan(axis) / getColumnCount();
	    } else {
		return super.getPreferredSpan(axis) / getRowCount();
	    }
	}

	int row;
	int col;
    }

	
    /**
     * A special table cell that simply occupies space in the
     * table at a grid location to make calculations easier to
     * deal with.  This is used to hold grid space for cells that
     * span columns, cells that span rows, and grid locations
     * that have no cells.
     */
    class ProxyCell extends View implements GridCell {

	ProxyCell(Element e) {
	    super(e);
	    host = null;
	}

	ProxyCell(TableCell host) {
	    super(host.getElement());
	    this.host = host;
	}
	
        /**
         * Sets the grid location.
         *
         * @param row the row >= 0
         * @param col the column >= 0
         */
        public void setGridLocation(int row, int col) {
            this.row = row;
            this.col = col;
	    preferenceChanged(null, true, true);
	}

	/**
	 * Gets the row of the grid location
	 */
        public int getGridRow() {
	    return row;
	}

	/**
	 * Gets the column of the grid location
	 */
        public int getGridColumn() {
	    return col;
	}

	/**
	 * Gets the number of columns this cell spans (e.g. the
	 * grid width).
         *
         * @return the number of columns
	 */
	public int getColumnCount() {
	    return 1;
	}

	/**
	 * Gets the number of rows this cell spans (that is, the
	 * grid height).
         *
         * @return the number of rows
	 */
	public int getRowCount() {
	    return 1;
	}

	/**
	 * Determines the resizability of the view along the
	 * given axis.  A value of 0 or less is not resizable.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @return the resize weight
	 * @exception IllegalArgumentException for an invalid axis
	 */
        public int getResizeWeight(int axis) {
	    return 1;
	}

	/**
	 * Loads all of the children to initialize the view.
	 * This is called by the <code>setParent</code> method.
	 * This is reimplemented to do nothing... proxy cells 
	 * are just a place holder.
	 *
	 * @param f the view factory
	 */
        protected void loadChildren(ViewFactory f) {
	}

	/**
	 * Renders using the given rendering surface and area on that
	 * surface.  This is implemented to do nothing as proxy cells
	 * are supposed to be invisible place holders.
	 *
	 * @param g the rendering surface to use
	 * @param allocation the allocated region to render into
	 * @see View#paint
	 */
        public void paint(Graphics g, Shape allocation) {
	}

        public float getPreferredSpan(int axis) {
	    if (host != null) {
		return host.getPreferredSpan(axis);
	    }
	    return 0;
	}

	/**
	 * Provides a mapping from the document model coordinate space
	 * to the coordinate space of the view mapped to it.  Fot the
	 * proxy cell, which simply takes up space, the request is
	 * forwarded to the host if there is one.
	 *
	 * @param pos the position to convert >= 0
	 * @param a the allocated region to render into
	 * @return the bounding box of the given position is returned
	 * @exception BadLocationException  if the given position does
	 *   not represent a valid location in the associated document
	 * @see View#modelToView
	 */
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	    if (host != null) {
		return host.modelToView(pos, a, b);
	    }
	    return null;
	}

	/**
	 * Provides a mapping from the view coordinate space to the logical
	 * coordinate space of the model.
	 *
	 * @param x the X coordinate >= 0
	 * @param y the Y coordinate >= 0
	 * @param a the allocated region to render into
	 * @return the location within the model that best represents the
	 *  given point in the view >= 0
	 * @see View#viewToModel
	 */
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	    if (host != null) {
		return host.viewToModel(x, y, a, bias);
	    }
	    return -1;
	}

	TableCell host;
	int row;
	int col;
    }
}
