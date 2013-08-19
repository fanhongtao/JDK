/*
 * @(#)JTable.java	1.208 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.util.*;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.accessibility.*;

import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.table.*;
import javax.swing.border.*;

import java.text.NumberFormat;
import java.text.DateFormat;

/**
 * The <code>JTable</code> is used to display and edit regular two-dimensional tables
 * of cells.
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/table.html">How to Use Tables</a>
 * in <em>The Java Tutorial</em>
 * for task-oriented documentation and examples of using <code>JTable</code>.
 *
 * <p>
 * The <code>JTable</code> has many
 * facilities that make it possible to customize its rendering and editing
 * but provides defaults for these features so that simple tables can be
 * set up easily.  For example, to set up a table with 10 rows and 10
 * columns of numbers:
 * <p>
 * <pre>
 *      TableModel dataModel = new AbstractTableModel() {
 *          public int getColumnCount() { return 10; }
 *          public int getRowCount() { return 10;}
 *          public Object getValueAt(int row, int col) { return new Integer(row*col); }
 *      };
 *      JTable table = new JTable(dataModel);
 *      JScrollPane scrollpane = new JScrollPane(table);
 * </pre>
 * <p>
 * Note that if you wish to use a <code>JTable</code> in a standalone
 * view (outside of a <code>JScrollPane</code>) and want the header
 * displayed, you can get it using {@link #getTableHeader} and
 * display it separately.
 * <p>
 * When designing applications that use the <code>JTable</code> it is worth paying
 * close attention to the data structures that will represent the table's data.
 * The <code>DefaultTableModel</code> is a model implementation that
 * uses a <code>Vector</code> of <code>Vector</code>s of <code>Object</code>s to
 * store the cell values. As well as copying the data from an
 * application into the <code>DefaultTableModel</code>,
 * it is also possible to wrap the data in the methods of the
 * <code>TableModel</code> interface so that the data can be passed to the
 * <code>JTable</code> directly, as in the example above. This often results
 * in more efficient applications because the model is free to choose the
 * internal representation that best suits the data.
 * A good rule of thumb for deciding whether to use the <code>AbstractTableModel</code>
 * or the <code>DefaultTableModel</code> is to use the <code>AbstractTableModel</code>
 * as the base class for creating subclasses and the <code>DefaultTableModel</code>
 * when subclassing is not required.
 * <p>
 * The "TableExample" directory in the demo area of the source distribution
 * gives a number of complete examples of <code>JTable</code> usage,
 * covering how the <code>JTable</code> can be used to provide an
 * editable view of data taken from a database and how to modify
 * the columns in the display to use specialized renderers and editors.
 * <p>
 * The <code>JTable</code> uses integers exclusively to refer to both the rows and the columns
 * of the model that it displays. The <code>JTable</code> simply takes a tabular range of cells
 * and uses <code>getValueAt(int, int)</code> to retrieve the
 * values from the model during painting.
 * <p>
 * By default, columns may be rearranged in the <code>JTable</code> so that the
 * view's columns appear in a different order to the columns in the model.
 * This does not affect the implementation of the model at all: when the
 * columns are reordered, the <code>JTable</code> maintains the new order of the columns
 * internally and converts its column indices before querying the model.
 * <p>
 * So, when writing a <code>TableModel</code>, it is not necessary to listen for column
 * reordering events as the model will be queried in its own coordinate
 * system regardless of what is happening in the view.
 * In the examples area there is a demonstration of a sorting algorithm making
 * use of exactly this technique to interpose yet another coordinate system
 * where the order of the rows is changed, rather than the order of the columns.
 * <p>
 * As for all <code>JComponent</code> classes, you can use
 * {@link InputMap} and {@link ActionMap} to associate an
 * {@link Action} object with a {@link KeyStroke} and execute the
 * action under specified conditions.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JTable"><code>JTable</code> key assignments</a>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: A component which displays data in a two dimensional grid.
 *
 * @version 1.208 01/23/03
 * @author Philip Milne
 */
/* The first versions of the JTable, contained in Swing-0.1 through
 * Swing-0.4, were written by Alan Chung.
 */
public class JTable extends JComponent implements TableModelListener, Scrollable,
    TableColumnModelListener, ListSelectionListener, CellEditorListener,
    Accessible
{
//
// Static Constants
//

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TableUI";

    /** Do not adjust column widths automatically; use a scrollbar. */
    public static final int     AUTO_RESIZE_OFF = 0;

    /** When a column is adjusted in the UI, adjust the next column the opposite way. */
    public static final int     AUTO_RESIZE_NEXT_COLUMN = 1;

    /** During UI adjustment, change subsequent columns to preserve the total width;
      * this is the default behavior. */
    public static final int     AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;

    /** During all resize operations, apply adjustments to the last column only. */
    public static final int     AUTO_RESIZE_LAST_COLUMN = 3;

    /** During all resize operations, proportionately resize all columns. */
    public static final int     AUTO_RESIZE_ALL_COLUMNS = 4;


//
// Instance Variables
//

    /** The <code>TableModel</code> of the table. */
    protected TableModel        dataModel;

    /** The <code>TableColumnModel</code> of the table. */
    protected TableColumnModel  columnModel;

    /** The <code>ListSelectionModel</code> of the table, used to keep track of row selections. */
    protected ListSelectionModel selectionModel;

    /** The <code>TableHeader</code> working with the table. */
    protected JTableHeader      tableHeader;

    /** The height in pixels of each row in the table. */
    protected int               rowHeight;

    /** The height in pixels of the margin between the cells in each row. */
    protected int               rowMargin;

    /** The color of the grid. */
    protected Color             gridColor;

    /** The table draws horizontal lines between cells if <code>showHorizontalLines</code> is true. */
    protected boolean           showHorizontalLines;

    /** The table draws vertical lines between cells if <code>showVerticalLines</code> is true. */
    protected boolean           showVerticalLines;

    /**
     *  Determines if the table automatically resizes the
     *  width of the table's columns to take up the entire width of the
     *  table, and how it does the resizing.
     */
    protected int               autoResizeMode;

    /**
     *  The table will query the <code>TableModel</code> to build the default
     *  set of columns if this is true.
     */
    protected boolean           autoCreateColumnsFromModel;

    /** Used by the <code>Scrollable</code> interface to determine the initial visible area. */
    protected Dimension         preferredViewportSize;

    /** True if row selection is allowed in this table. */
    protected boolean           rowSelectionAllowed;

    /**
     * Obsolete as of Java 2 platform v1.3.  Please use the
     * <code>rowSelectionAllowed</code> property and the
     * <code>columnSelectionAllowed</code> property of the
     * <code>columnModel</code> instead. Or use the
     * method <code>getCellSelectionEnabled</code>.
     */
    /*
     * If true, both a row selection and a column selection
     * can be non-empty at the same time, the selected cells are the
     * the cells whose row and column are both selected.
     */
    protected boolean           cellSelectionEnabled;

    /** If editing, the <code>Component</code> that is handling the editing. */
    transient protected Component       editorComp;

    /**
     * The object that overwrites the screen real estate occupied by the
     * current cell and allows the user to change its contents.
     */
    transient protected TableCellEditor cellEditor;

    /** Identifies the column of the cell being edited. */
    transient protected int             editingColumn;

    /** Identifies the row of the cell being edited. */
    transient protected int             editingRow;

    /**
     * A table of objects that display the contents of a cell,
     * indexed by class as declared in <code>getColumnClass</code>
     * in the <code>TableModel</code> interface.
     */
    transient protected Hashtable defaultRenderersByColumnClass;

    /**
     * A table of objects that display and edit the contents of a cell,
     * indexed by class as declared in <code>getColumnClass</code>
     * in the <code>TableModel</code> interface.
     */
    transient protected Hashtable defaultEditorsByColumnClass;

    /** The foreground color of selected cells. */
    protected Color selectionForeground;

    /** The background color of selected cells. */
    protected Color selectionBackground;

//
// Private state
//

    private SizeSequence rowModel;
    private boolean dragEnabled;
    private boolean surrendersFocusOnKeystroke;
    private PropertyChangeListener editorRemover = null;
    /**
     * The last value of getValueIsAdjusting from the column selection models
     * columnSelectionChanged notification. Used to test if a repaint is
     * needed.
     */
    private boolean columnSelectionAdjusting;
    /**
     * The last value of getValueIsAdjusting from the row selection models
     * valueChanged notification. Used to test if a repaint is needed.
     */
    private boolean rowSelectionAdjusting;

//
// Constructors
//

    /**
     * Constructs a default <code>JTable</code> that is initialized with a default
     * data model, a default column model, and a default selection
     * model.
     *
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public JTable() {
        this(null, null, null);
    }

    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, a default column model,
     * and a default selection model.
     *
     * @param dm        the data model for the table
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public JTable(TableModel dm) {
        this(dm, null, null);
    }

    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code>
     * as the column model, and a default selection model.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @see #createDefaultSelectionModel
     */
    public JTable(TableModel dm, TableColumnModel cm) {
        this(dm, cm, null);
    }

    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code> as the
     * column model, and <code>sm</code> as the selection model.
     * If any of the parameters are <code>null</code> this method
     * will initialize the table with the corresponding default model.
     * The <code>autoCreateColumnsFromModel</code> flag is set to false
     * if <code>cm</code> is non-null, otherwise it is set to true
     * and the column model is populated with suitable
     * <code>TableColumns</code> for the columns in <code>dm</code>.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @param sm        the row selection model for the table
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public JTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super();
        setLayout(null);

	setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			   JComponent.getManagingFocusForwardTraversalKeys());
	setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			   JComponent.getManagingFocusBackwardTraversalKeys());

        if (cm == null) {
            cm = createDefaultColumnModel();
            autoCreateColumnsFromModel = true;
        }
        setColumnModel(cm);

        if (sm == null) {
            sm = createDefaultSelectionModel();
        }
	setSelectionModel(sm);

    // Set the model last, that way if the autoCreatColumnsFromModel has
    // been set above, we will automatically populate an empty columnModel
    // with suitable columns for the new model.
        if (dm == null) {
            dm = createDefaultDataModel();
        }
	setModel(dm);

        initializeLocalVars();
        updateUI();
    }

    /**
     * Constructs a <code>JTable</code> with <code>numRows</code>
     * and <code>numColumns</code> of empty cells using
     * <code>DefaultTableModel</code>.  The columns will have
     * names of the form "A", "B", "C", etc.
     *
     * @param numRows           the number of rows the table holds
     * @param numColumns        the number of columns the table holds
     * @see javax.swing.table.DefaultTableModel
     */
    public JTable(int numRows, int numColumns) {
        this(new DefaultTableModel(numRows, numColumns));
    }

    /**
     * Constructs a <code>JTable</code> to display the values in the
     * <code>Vector</code> of <code>Vectors</code>, <code>rowData</code>,
     * with column names, <code>columnNames</code>.  The
     * <code>Vectors</code> contained in <code>rowData</code>
     * should contain the values for that row. In other words,
     * the value of the cell at row 1, column 5 can be obtained
     * with the following code:
     * <p>
     * <pre>((Vector)rowData.elementAt(1)).elementAt(5);</pre>
     * <p>
     * @param rowData           the data for the new table
     * @param columnNames       names of each column
     */
    public JTable(Vector rowData, Vector columnNames) {
        this(new DefaultTableModel(rowData, columnNames));
    }

    /**
     * Constructs a <code>JTable</code> to display the values in the two dimensional array,
     * <code>rowData</code>, with column names, <code>columnNames</code>.
     * <code>rowData</code> is an array of rows, so the value of the cell at row 1,
     * column 5 can be obtained with the following code:
     * <p>
     * <pre> rowData[1][5]; </pre>
     * <p>
     * All rows must be of the same length as <code>columnNames</code>.
     * <p>
     * @param rowData           the data for the new table
     * @param columnNames       names of each column
     */
    public JTable(final Object[][] rowData, final Object[] columnNames) {
        this(new AbstractTableModel() {
            public String getColumnName(int column) { return columnNames[column].toString(); }
            public int getRowCount() { return rowData.length; }
            public int getColumnCount() { return columnNames.length; }
            public Object getValueAt(int row, int col) { return rowData[row][col]; }
            public boolean isCellEditable(int row, int column) { return true; }
            public void setValueAt(Object value, int row, int col) {
                rowData[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        });
    }

    /**
     * Calls the <code>configureEnclosingScrollPane</code> method.
     *
     * @see #configureEnclosingScrollPane
     */
    public void addNotify() {
        super.addNotify();
        configureEnclosingScrollPane();
    }

    /**
     * If this <code>JTable</code> is the <code>viewportView</code> of an enclosing <code>JScrollPane</code>
     * (the usual situation), configure this <code>ScrollPane</code> by, amongst other things,
     * installing the table's <code>tableHeader</code> as the <code>columnHeaderView</code> of the scroll pane.
     * When a <code>JTable</code> is added to a <code>JScrollPane</code> in the usual way,
     * using <code>new JScrollPane(myTable)</code>, <code>addNotify</code> is
     * called in the <code>JTable</code> (when the table is added to the viewport).
     * <code>JTable</code>'s <code>addNotify</code> method in turn calls this method,
     * which is protected so that this default installation procedure can
     * be overridden by a subclass.
     *
     * @see #addNotify
     */
    protected void configureEnclosingScrollPane() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(getTableHeader());
		//  scrollPane.getViewport().setBackingStoreEnabled(true);
                Border border = scrollPane.getBorder();
                if (border == null || border instanceof UIResource) {
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
    }

    /**
     * Calls the <code>unconfigureEnclosingScrollPane</code> method.
     *
     * @see #unconfigureEnclosingScrollPane
     */
    public void removeNotify() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
            removePropertyChangeListener("permanentFocusOwner", editorRemover);
	editorRemover = null;
        unconfigureEnclosingScrollPane();
        super.removeNotify();
    }

    /**
     * Reverses the effect of <code>configureEnclosingScrollPane</code>
     * by replacing the <code>columnHeaderView</code> of the enclosing
     * scroll pane with <code>null</code>. <code>JTable</code>'s
     * <code>removeNotify</code> method calls
     * this method, which is protected so that this default uninstallation
     * procedure can be overridden by a subclass.
     *
     * @see #removeNotify
     * @see #configureEnclosingScrollPane
     */
    protected void unconfigureEnclosingScrollPane() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(null);
            }
        }
    }

//
// Static Methods
//

    /**
     * Equivalent to <code>new JScrollPane(aTable)</code>.
     *
     * @deprecated As of Swing version 1.0.2,
     * replaced by <code>new JScrollPane(aTable)</code>.
     */
    static public JScrollPane createScrollPaneForTable(JTable aTable) {
        return new JScrollPane(aTable);
    }

//
// Table Attributes
//

    /**
     * Sets the <code>tableHeader</code> working with this <code>JTable</code> to <code>newHeader</code>.
     * It is legal to have a <code>null</code> <code>tableHeader</code>.
     *
     * @param   tableHeader                       new tableHeader
     * @see     #getTableHeader
     * @beaninfo
     *  bound: true
     *  description: The JTableHeader instance which renders the column headers.
     */
    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != tableHeader) {
	    JTableHeader old = this.tableHeader;
            // Release the old header
            if (old != null) {
                old.setTable(null);
	    }
            this.tableHeader = tableHeader;
            if (tableHeader != null) {
                tableHeader.setTable(this);
	    }
	    firePropertyChange("tableHeader", old, tableHeader);
        }
    }

    /**
     * Returns the <code>tableHeader</code> used by this <code>JTable</code>.
     *
     * @return  the <code>tableHeader</code> used by this table
     * @see     #setTableHeader
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * Sets the height, in pixels, of all cells to <code>rowHeight</code>,
     * revalidates, and repaints.
     * The height of the cells will be equal to the row height minus
     * the row margin.
     *
     * @param   rowHeight                       new row height
     * @exception IllegalArgumentException      if <code>rowHeight</code> is
     *                                          less than 1
     * @see     #getRowHeight
     * @beaninfo
     *  bound: true
     *  description: The height of the specified row.
     */
    public void setRowHeight(int rowHeight) {
        if (rowHeight <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
	int old = this.rowHeight;
        this.rowHeight = rowHeight;
	rowModel = null;
        resizeAndRepaint();
	firePropertyChange("rowHeight", old, rowHeight);
    }

    /**
     * Returns the height of a table row, in pixels.
     * The default row height is 16.0.
     *
     * @return  the height in pixels of a table row
     * @see     #setRowHeight
     */
    public int getRowHeight() {
        return rowHeight;
    }

    private SizeSequence getRowModel() {
	if (rowModel == null) {
	    rowModel = new SizeSequence(getRowCount(), getRowHeight());
	}
	return rowModel;
    }

    /**
     * Sets the height for <code>row</code> to <code>rowHeight</code>,
     * revalidates, and repaints. The height of the cells in this row
     * will be equal to the row height minus the row margin.
     *
     * @param   row                             the row whose height is being
  						changed
     * @param   rowHeight                       new row height, in pixels
     * @exception IllegalArgumentException      if <code>rowHeight</code> is
     *                                          less than 1
     * @beaninfo
     *  bound: true
     *  description: The height in pixels of the cells in <code>row</code>
     */
    public void setRowHeight(int row, int rowHeight) {
        if (rowHeight <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
	getRowModel().setSize(row, rowHeight);
	resizeAndRepaint();
    }

    /**
     * Returns the height, in pixels, of the cells in <code>row</code>.
     * @param   row              the row whose height is to be returned
     * @return the height, in pixels, of the cells in the row
     */
    public int getRowHeight(int row) {
	return (rowModel == null) ? getRowHeight() : rowModel.getSize(row);
    }

    /**
     * Sets the amount of empty space between cells in adjacent rows.
     *
     * @param  rowMargin  the number of pixels between cells in a row
     * @see     #getRowMargin
     * @beaninfo
     *  bound: true
     *  description: The amount of space between cells.
     */
    public void setRowMargin(int rowMargin) {
	int old = this.rowMargin;
        this.rowMargin = rowMargin;
        resizeAndRepaint();
	firePropertyChange("rowMargin", old, rowMargin);
    }

    /**
     * Gets the amount of empty space, in pixels, between cells. Equivalent to:
     * <code>getIntercellSpacing().height</code>.
     * @return the number of pixels between cells in a row
     *
     * @see     #setRowMargin
     */
    public int getRowMargin() {
        return rowMargin;
    }

    /**
     * Sets the <code>rowMargin</code> and the <code>columnMargin</code> --
     * the height and width of the space between cells -- to
     * <code>intercellSpacing</code>.
     *
     * @param   intercellSpacing        a <code>Dimension</code>
     *					specifying the new width
     *					and height between cells
     * @see     #getIntercellSpacing
     * @beaninfo
     *  description: The spacing between the cells,
     *               drawn in the background color of the JTable.
     */
    public void setIntercellSpacing(Dimension intercellSpacing) {
        // Set the rowMargin here and columnMargin in the TableColumnModel
        setRowMargin(intercellSpacing.height);
        getColumnModel().setColumnMargin(intercellSpacing.width);

        resizeAndRepaint();
    }

    /**
     * Returns the horizontal and vertical space between cells.
     * The default spacing is (1, 1), which provides room to draw the grid.
     *
     * @return  the horizontal and vertical spacing between cells
     * @see     #setIntercellSpacing
     */
    public Dimension getIntercellSpacing() {
        return new Dimension(getColumnModel().getColumnMargin(), rowMargin);
    }

    /**
     * Sets the color used to draw grid lines to <code>gridColor</code> and redisplays.
     * The default color is look and feel dependent.
     *
     * @param   gridColor                       the new color of the grid lines
     * @exception IllegalArgumentException      if <code>gridColor</code> is <code>null</code>
     * @see     #getGridColor
     * @beaninfo
     *  bound: true
     *  description: The grid color.
     */
    public void setGridColor(Color gridColor) {
        if (gridColor == null) {
            throw new IllegalArgumentException("New color is null");
        }
	Color old = this.gridColor;
        this.gridColor = gridColor;
	firePropertyChange("gridColor", old, gridColor);
        // Redraw
        repaint();
    }

    /**
     * Returns the color used to draw grid lines.
     * The default color is look and feel dependent.
     *
     * @return  the color used to draw grid lines
     * @see     #setGridColor
     */
    public Color getGridColor() {
        return gridColor;
    }

    /**
     *  Sets whether the table draws grid lines around cells.
     *  If <code>showGrid</code> is true it does; if it is false it doesn't.
     *  There is no <code>getShowGrid</code> method as this state is held
     *  in two variables -- <code>showHorizontalLines</code> and <code>showVerticalLines</code> --
     *  each of which can be queried independently.
     *
     * @param   showGrid                 true if table view should draw grid lines
     *
     * @see     #setShowVerticalLines
     * @see     #setShowHorizontalLines
     * @beaninfo
     *  description: The color used to draw the grid lines.
     */
    public void setShowGrid(boolean showGrid) {
        setShowHorizontalLines(showGrid);
        setShowVerticalLines(showGrid);

        // Redraw
        repaint();
    }

    /**
     *  Sets whether the table draws horizontal lines between cells.
     *  If <code>showHorizontalLines</code> is true it does; if it is false it doesn't.
     *
     * @param   showHorizontalLines      true if table view should draw horizontal lines
     * @see     #getShowHorizontalLines
     * @see     #setShowGrid
     * @see     #setShowVerticalLines
     * @beaninfo
     *  bound: true
     *  description: Whether horizontal lines should be drawn in between the cells.
     */
    public void setShowHorizontalLines(boolean showHorizontalLines) {
        boolean old = this.showHorizontalLines;
	this.showHorizontalLines = showHorizontalLines;
	firePropertyChange("showHorizontalLines", old, showHorizontalLines);

        // Redraw
        repaint();
    }

    /**
     *  Sets whether the table draws vertical lines between cells.
     *  If <code>showVerticalLines</code> is true it does; if it is false it doesn't.
     *
     * @param   showVerticalLines              true if table view should draw vertical lines
     * @see     #getShowVerticalLines
     * @see     #setShowGrid
     * @see     #setShowHorizontalLines
     * @beaninfo
     *  bound: true
     *  description: Whether vertical lines should be drawn in between the cells.
     */
    public void setShowVerticalLines(boolean showVerticalLines) {
        boolean old = this.showVerticalLines;
	this.showVerticalLines = showVerticalLines;
	firePropertyChange("showVerticalLines", old, showVerticalLines);
        // Redraw
        repaint();
    }

    /**
     * Returns true if the table draws horizontal lines between cells, false if it
     * doesn't. The default is true.
     *
     * @return  true if the table draws horizontal lines between cells, false if it
     *          doesn't
     * @see     #setShowHorizontalLines
     */
    public boolean getShowHorizontalLines() {
        return showHorizontalLines;
    }

    /**
     * Returns true if the table draws vertical lines between cells, false if it
     * doesn't. The default is true.
     *
     * @return  true if the table draws vertical lines between cells, false if it
     *          doesn't
     * @see     #setShowVerticalLines
     */
    public boolean getShowVerticalLines() {
        return showVerticalLines;
    }

    /**
     * Sets the table's auto resize mode when the table is resized.
     *
     * @param   mode One of 5 legal values:
     *                   AUTO_RESIZE_OFF,
     *                   AUTO_RESIZE_NEXT_COLUMN,
     *                   AUTO_RESIZE_SUBSEQUENT_COLUMNS,
     *                   AUTO_RESIZE_LAST_COLUMN,
     *                   AUTO_RESIZE_ALL_COLUMNS
     *
     * @see     #getAutoResizeMode
     * @see     #doLayout
     * @beaninfo
     *  bound: true
     *  description: Whether the columns should adjust themselves automatically.
     *        enum: AUTO_RESIZE_OFF                JTable.AUTO_RESIZE_OFF
     *              AUTO_RESIZE_NEXT_COLUMN        JTable.AUTO_RESIZE_NEXT_COLUMN
     *              AUTO_RESIZE_SUBSEQUENT_COLUMNS JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
     *              AUTO_RESIZE_LAST_COLUMN        JTable.AUTO_RESIZE_LAST_COLUMN
     *              AUTO_RESIZE_ALL_COLUMNS        JTable.AUTO_RESIZE_ALL_COLUMNS
     */
    public void setAutoResizeMode(int mode) {
        if ((mode == AUTO_RESIZE_OFF) ||
            (mode == AUTO_RESIZE_NEXT_COLUMN) ||
            (mode == AUTO_RESIZE_SUBSEQUENT_COLUMNS) ||
            (mode == AUTO_RESIZE_LAST_COLUMN) ||
            (mode == AUTO_RESIZE_ALL_COLUMNS)) {
	    int old = autoResizeMode;
            autoResizeMode = mode;
            resizeAndRepaint();
            if (tableHeader != null) {
		tableHeader.resizeAndRepaint();
	    }
	    firePropertyChange("autoResizeMode", old, autoResizeMode);
        }
    }

    /**
     * Returns the auto resize mode of the table.  The default mode
     * is AUTO_RESIZE_SUBSEQUENT_COLUMNS.
     *
     * @return  the autoResizeMode of the table
     *
     * @see     #setAutoResizeMode
     * @see     #doLayout
     */
    public int getAutoResizeMode() {
        return autoResizeMode;
    }

    /**
     * Sets this table's <code>autoCreateColumnsFromModel</code> flag.
     * This method calls <code>createDefaultColumnsFromModel</code> if
     * <code>autoCreateColumnsFromModel</code> changes from false to true.
     *
     * @param   autoCreateColumnsFromModel   true if <code>JTable</code> should automatically create columns
     * @see     #getAutoCreateColumnsFromModel
     * @see     #createDefaultColumnsFromModel
     * @beaninfo
     *  bound: true
     *  description: Automatically populates the columnModel when a new TableModel is submitted.
     */
    public void setAutoCreateColumnsFromModel(boolean autoCreateColumnsFromModel) {
        if (this.autoCreateColumnsFromModel != autoCreateColumnsFromModel) {
	    boolean old = this.autoCreateColumnsFromModel;
            this.autoCreateColumnsFromModel = autoCreateColumnsFromModel;
            if (autoCreateColumnsFromModel) {
                createDefaultColumnsFromModel();
	    }
	    firePropertyChange("autoCreateColumnsFromModel", old, autoCreateColumnsFromModel);
        }
    }

    /**
     * Determines whether the table will create default columns from the model.
     * If true, <code>setModel</code> will clear any existing columns and
     * create new columns from the new model.  Also, if the event in
     * the <code>tableChanged</code> notification specifies that the
     * entire table changed, then the columns will be rebuilt.
     * The default is true.
     *
     * @return  the autoCreateColumnsFromModel of the table
     * @see     #setAutoCreateColumnsFromModel
     * @see     #createDefaultColumnsFromModel
     */
    public boolean getAutoCreateColumnsFromModel() {
        return autoCreateColumnsFromModel;
    }

    /**
     * Creates default columns for the table from
     * the data model using the <code>getColumnCount</code> method
     * defined in the <code>TableModel</code> interface.
     * <p>
     * Clears any existing columns before creating the
     * new columns based on information from the model.
     *
     * @see     #getAutoCreateColumnsFromModel
     */
    public void createDefaultColumnsFromModel() {
        TableModel m = getModel();
        if (m != null) {
            // Remove any current columns
            TableColumnModel cm = getColumnModel();
            while (cm.getColumnCount() > 0) {
                cm.removeColumn(cm.getColumn(0));
	    }

            // Create new columns from the data model info
            for (int i = 0; i < m.getColumnCount(); i++) {
                TableColumn newColumn = new TableColumn(i);
                addColumn(newColumn);
            }
        }
    }

    /**
     * Sets a default cell renderer to be used if no renderer has been set in
     * a <code>TableColumn</code>. If renderer is <code>null</code>,
     * removes the default renderer for this column class.
     *
     * @param  columnClass     set the default cell renderer for this columnClass
     * @param  renderer        default cell renderer to be used for this
     *			       columnClass
     * @see     #getDefaultRenderer
     * @see     #setDefaultEditor
     */
    public void setDefaultRenderer(Class columnClass, TableCellRenderer renderer) {
	if (renderer != null) {
	    defaultRenderersByColumnClass.put(columnClass, renderer);
	}
	else {
	    defaultRenderersByColumnClass.remove(columnClass);
	}
    }

    /**
     * Returns the cell renderer to be used when no renderer has been set in
     * a <code>TableColumn</code>. During the rendering of cells the renderer is fetched from
     * a <code>Hashtable</code> of entries according to the class of the cells in the column. If
     * there is no entry for this <code>columnClass</code> the method returns
     * the entry for the most specific superclass. The <code>JTable</code> installs entries
     * for <code>Object</code>, <code>Number</code>, and <code>Boolean</code>, all of which can be modified
     * or replaced.
     *
     * @param   columnClass   return the default cell renderer
     *			      for this columnClass
     * @return  the renderer for this columnClass
     * @see     #setDefaultRenderer
     * @see     #getColumnClass
     */
    public TableCellRenderer getDefaultRenderer(Class columnClass) {
        if (columnClass == null) {
            return null;
        }
        else {
            Object renderer = defaultRenderersByColumnClass.get(columnClass);
            if (renderer != null) {
                return (TableCellRenderer)renderer;
            }
            else {
                return getDefaultRenderer(columnClass.getSuperclass());
            }
        }
    }

    /**
     * Sets a default cell editor to be used if no editor has been set in
     * a <code>TableColumn</code>. If no editing is required in a table, or a
     * particular column in a table, uses the <code>isCellEditable</code>
     * method in the <code>TableModel</code> interface to ensure that this
     * <code>JTable</code> will not start an editor in these columns.
     * If editor is <code>null</code>, removes the default editor for this
     * column class.
     *
     * @param  columnClass  set the default cell editor for this columnClass
     * @param  editor   default cell editor to be used for this columnClass
     * @see     TableModel#isCellEditable
     * @see     #getDefaultEditor
     * @see     #setDefaultRenderer
     */
    public void setDefaultEditor(Class columnClass, TableCellEditor editor) {
        if (editor != null) {
	    defaultEditorsByColumnClass.put(columnClass, editor);
	}
	else {
	    defaultEditorsByColumnClass.remove(columnClass);
	}
    }

    /**
     * Returns the editor to be used when no editor has been set in
     * a <code>TableColumn</code>. During the editing of cells the editor is fetched from
     * a <code>Hashtable</code> of entries according to the class of the cells in the column. If
     * there is no entry for this <code>columnClass</code> the method returns
     * the entry for the most specific superclass. The <code>JTable</code> installs entries
     * for <code>Object</code>, <code>Number</code>, and <code>Boolean</code>, all of which can be modified
     * or replaced.
     *
     * @param   columnClass  return the default cell editor for this columnClass
     * @return the default cell editor to be used for this columnClass
     * @see     #setDefaultEditor
     * @see     #getColumnClass
     */
    public TableCellEditor getDefaultEditor(Class columnClass) {
        if (columnClass == null) {
            return null;
        }
        else {
            Object editor = defaultEditorsByColumnClass.get(columnClass);
            if (editor != null) {
                return (TableCellEditor)editor;
            }
            else {
                return getDefaultEditor(columnClass.getSuperclass());
            }
        }
    }

    /**
     * Sets the <code>dragEnabled</code> property,
     * which must be <code>true</code> to enable
     * automatic drag handling (the first part of drag and drop)
     * on this component.
     * The <code>transferHandler</code> property needs to be set
     * to a non-<code>null</code> value for the drag to do
     * anything.  The default value of the <code>dragEnabled</code
     * property
     * is <code>false</code>.
     *
     * <p>
     *
     * When automatic drag handling is enabled,
     * most look and feels begin a drag-and-drop operation
     * whenever the user presses the mouse button over a selection
     * and then moves the mouse a few pixels.
     * Setting this property to <code>true</code>
     * can therefore have a subtle effect on
     * how selections behave.
     *
     * <p>
     *
     * Some look and feels might not support automatic drag and drop;
     * they will ignore this property.  You can work around such
     * look and feels by modifying the component
     * to directly call the <code>exportAsDrag</code> method of a
     * <code>TransferHandler</code>.
     *
     * @param b the value to set the <code>dragEnabled</code> property to
     * @exception HeadlessException if
     *            <code>b</code> is <code>true</code> and
     *            <code>GraphicsEnvironment.isHeadless()</code>
     *            returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #getDragEnabled
     * @see #setTransferHandler
     * @see TransferHandler
     * @since 1.4
     *
     * @beaninfo
     *  description: determines whether automatic drag handling is enabled
     *        bound: false
     */
    public void setDragEnabled(boolean b) {
        if (b && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
	dragEnabled = b;
    }

    /**
     * Gets the value of the <code>dragEnabled</code> property.
     *
     * @return  the value of the <code>dragEnabled</code> property
     * @see #setDragEnabled
     * @since 1.4
     */
    public boolean getDragEnabled() {
	return dragEnabled;
    }


//
// Selection methods
//
    /**
     * Sets the table's selection mode to allow only single selections, a single
     * contiguous interval, or multiple intervals.
     * <P>
     * <bold>Note:</bold>
     * <code>JTable</code> provides all the methods for handling
     * column and row selection.  When setting states,
     * such as <code>setSelectionMode</code>, it not only
     * updates the mode for the row selection model but also sets similar
     * values in the selection model of the <code>columnModel</code>.
     * If you want to have the row and column selection models operating
     * in different modes, set them both directly.
     * <p>
     * Both the row and column selection models for <code>JTable</code>
     * default to using a <code>DefaultListSelectionModel</code>
     * so that <code>JTable</code> works the same way as the
     * <code>JList</code>. See the <code>setSelectionMode</code> method
     * in <code>JList</code> for details about the modes.
     *
     * @see JList#setSelectionMode
     * @beaninfo
     * description: The selection mode used by the row and column selection models.
     *        enum: SINGLE_SELECTION            ListSelectionModel.SINGLE_SELECTION
     *              SINGLE_INTERVAL_SELECTION   ListSelectionModel.SINGLE_INTERVAL_SELECTION
     *              MULTIPLE_INTERVAL_SELECTION ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
     */
    public void setSelectionMode(int selectionMode) {
        clearSelection();
        getSelectionModel().setSelectionMode(selectionMode);
        getColumnModel().getSelectionModel().setSelectionMode(selectionMode);
    }

    /**
     * Sets whether the rows in this model can be selected.
     *
     * @param rowSelectionAllowed   true if this model will allow row selection
     * @see #getRowSelectionAllowed
     * @beaninfo
     *  bound: true
     *    attribute: visualUpdate true
     *  description: If true, an entire row is selected for each selected cell.
     */
    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
	boolean old = this.rowSelectionAllowed;
        this.rowSelectionAllowed = rowSelectionAllowed;
        if (old != rowSelectionAllowed) {
            repaint();
        }
	firePropertyChange("rowSelectionAllowed", old, rowSelectionAllowed);
    }

    /**
     * Returns true if rows can be selected.
     *
     * @return true if rows can be selected, otherwise false
     * @see #setRowSelectionAllowed
     */
    public boolean getRowSelectionAllowed() {
        return rowSelectionAllowed;
    }

    /**
     * Sets whether the columns in this model can be selected.
     *
     * @param columnSelectionAllowed   true if this model will allow column selection
     * @see #getColumnSelectionAllowed
     * @beaninfo
     *  bound: true
     *    attribute: visualUpdate true
     *  description: If true, an entire column is selected for each selected cell.
     */
    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
	boolean old = columnModel.getColumnSelectionAllowed();
        columnModel.setColumnSelectionAllowed(columnSelectionAllowed);
        if (old != columnSelectionAllowed) {
            repaint();
        }
	firePropertyChange("columnSelectionAllowed", old, columnSelectionAllowed);
    }

    /**
     * Returns true if columns can be selected.
     *
     * @return true if columns can be selected, otherwise false
     * @see #setColumnSelectionAllowed
     */
    public boolean getColumnSelectionAllowed() {
        return columnModel.getColumnSelectionAllowed();
    }

    /**
     * Sets whether this table allows both a column selection and a
     * row selection to exist simultaneously. When set,
     * the table treats the intersection of the row and column selection
     * models as the selected cells. Override <code>isCellSelected</code> to
     * change this default behavior. This method is equivalent to setting
     * both the <code>rowSelectionAllowed</code> property and
     * <code>columnSelectionAllowed</code> property of the
     * <code>columnModel</code> to the supplied value.
     *
     * @param  cellSelectionEnabled   	true if simultaneous row and column
     *					selection is allowed
     * @see #getCellSelectionEnabled
     * @see #isCellSelected
     * @beaninfo
     *  bound: true
     *    attribute: visualUpdate true
     *  description: Select a rectangular region of cells rather than
     *		     rows or columns.
     */
    public void setCellSelectionEnabled(boolean cellSelectionEnabled) {
	setRowSelectionAllowed(cellSelectionEnabled);
	setColumnSelectionAllowed(cellSelectionEnabled);
        boolean old = this.cellSelectionEnabled;
	this.cellSelectionEnabled = cellSelectionEnabled;
	firePropertyChange("cellSelectionEnabled", old, cellSelectionEnabled);
    }

    /**
     * Returns true if both row and column selection models are enabled.
     * Equivalent to <code>getRowSelectionAllowed() &&
     * getColumnSelectionAllowed()</code>.
     *
     * @return true if both row and column selection models are enabled
     *
     * @see #setCellSelectionEnabled
     */
    public boolean getCellSelectionEnabled() {
        return getRowSelectionAllowed() && getColumnSelectionAllowed();
    }

    /**
     *  Selects all rows, columns, and cells in the table.
     */
    public void selectAll() {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
	if (getRowCount() > 0 && getColumnCount() > 0) {
	    setRowSelectionInterval(0, getRowCount()-1);
	    setColumnSelectionInterval(0, getColumnCount()-1);
	}
    }

    /**
     * Deselects all selected columns and rows.
     */
    public void clearSelection() {
        columnModel.getSelectionModel().clearSelection();
        selectionModel.clearSelection();
    }

    private int boundRow(int row) throws IllegalArgumentException {
	if (row < 0 || row >= getRowCount()) {
	    throw new IllegalArgumentException("Row index out of range");
	}
	return row;
    }

    private int boundColumn(int col) {
	if (col< 0 || col >= getColumnCount()) {
	    throw new IllegalArgumentException("Column index out of range");
	}
	return col;
    }

    /**
     * Selects the rows from <code>index0</code> to <code>index1</code>,
     * inclusive.
     *
     * @exception IllegalArgumentException      if <code>index0</code> or
     *						<code>index1</code> lie outside
     *                                          [0, <code>getRowCount()</code>-1]
     * @param   index0 one end of the interval
     * @param   index1 the other end of the interval
     */
    public void setRowSelectionInterval(int index0, int index1) {
        selectionModel.setSelectionInterval(boundRow(index0), boundRow(index1));
    }

    /**
     * Selects the columns from <code>index0</code> to <code>index1</code>,
     * inclusive.
     *
     * @exception IllegalArgumentException      if <code>index0</code> or
     *						<code>index1</code> lie outside
     *                                          [0, <code>getColumnCount()</code>-1]
     * @param   index0 one end of the interval
     * @param   index1 the other end of the interval
     */
    public void setColumnSelectionInterval(int index0, int index1) {
        columnModel.getSelectionModel().setSelectionInterval(boundColumn(index0), boundColumn(index1));
    }

    /**
     * Adds the rows from <code>index0</code> to <code>index1</code>, inclusive, to
     * the current selection.
     *
     * @exception IllegalArgumentException      if <code>index0</code> or <code>index1</code>
     *                                          lie outside [0, <code>getRowCount()</code>-1]
     * @param   index0 one end of the interval
     * @param   index1 the other end of the interval
     */
    public void addRowSelectionInterval(int index0, int index1) {
        selectionModel.addSelectionInterval(boundRow(index0), boundRow(index1));
    }

    /**
     * Adds the columns from <code>index0</code> to <code>index1</code>,
     * inclusive, to the current selection.
     *
     * @exception IllegalArgumentException      if <code>index0</code> or
     *						<code>index1</code> lie outside
     *                                          [0, <code>getColumnCount()</code>-1]
     * @param   index0 one end of the interval
     * @param   index1 the other end of the interval
     */
    public void addColumnSelectionInterval(int index0, int index1) {
        columnModel.getSelectionModel().addSelectionInterval(boundColumn(index0), boundColumn(index1));
    }

    /**
     * Deselects the rows from <code>index0</code> to <code>index1</code>, inclusive.
     *
     * @exception IllegalArgumentException      if <code>index0</code> or
     *						<code>index1</code> lie outside
     *                                          [0, <code>getRowCount()</code>-1]
     * @param   index0 one end of the interval
     * @param   index1 the other end of the interval
     */
    public void removeRowSelectionInterval(int index0, int index1) {
        selectionModel.removeSelectionInterval(boundRow(index0), boundRow(index1));
    }

    /**
     * Deselects the columns from <code>index0</code> to <code>index1</code>, inclusive.
     *
     * @exception IllegalArgumentException      if <code>index0</code> or
     *						<code>index1</code> lie outside
     *                                          [0, <code>getColumnCount()</code>-1]
     * @param   index0 one end of the interval
     * @param   index1 the other end of the interval
     */
    public void removeColumnSelectionInterval(int index0, int index1) {
        columnModel.getSelectionModel().removeSelectionInterval(boundColumn(index0), boundColumn(index1));
    }

    /**
     * Returns the index of the first selected row, -1 if no row is selected.
     * @return the index of the first selected row
     */
    public int getSelectedRow() {
	return selectionModel.getMinSelectionIndex();
    }

    /**
     * Returns the index of the first selected column,
     * -1 if no column is selected.
     * @return the index of the first selected column
     */
    public int getSelectedColumn() {
        return columnModel.getSelectionModel().getMinSelectionIndex();
    }

    /**
     * Returns the indices of all selected rows.
     *
     * @return an array of integers containing the indices of all selected rows,
     *         or an empty array if no row is selected
     * @see #getSelectedRow
     */
    public int[] getSelectedRows() {
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

    /**
     * Returns the indices of all selected columns.
     *
     * @return an array of integers containing the indices of all selected columns,
     *         or an empty array if no column is selected
     * @see #getSelectedColumn
     */
    public int[] getSelectedColumns() {
        return columnModel.getSelectedColumns();
    }

    /**
     * Returns the number of selected rows.
     *
     * @return the number of selected rows, 0 if no rows are selected
     */
    public int getSelectedRowCount() {
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

    /**
     * Returns the number of selected columns.
     *
     * @return the number of selected columns, 0 if no columns are selected
     */
    public int getSelectedColumnCount() {
        return columnModel.getSelectedColumnCount();
    }

    /**
     * Returns true if the row at the specified index is selected.
     *
     * @return true if the row at index <code>row</code> is selected, where 0 is the
     *              first row
     * @exception IllegalArgumentException      if <code>row</code> is not in the
     *                                          valid range
     */
    public boolean isRowSelected(int row) {
	return selectionModel.isSelectedIndex(row);
    }

    /**
     * Returns true if the column at the specified index is selected.
     *
     * @param   column   the column in the column model
     * @return true if the column at index <code>column</code> is selected, where
     *              0 is the first column
     * @exception IllegalArgumentException      if <code>column</code> is not in the
     *                                          valid range
     */
    public boolean isColumnSelected(int column) {
        return columnModel.getSelectionModel().isSelectedIndex(column);
    }

    /**
     * Returns true if the cell at the specified position is selected.
     * @param row   the row being queried
     * @param column  the column being queried
     *
     * @return true if the cell at index <code>(row, column)</code> is selected,
     *              where the first row and first column are at index 0
     * @exception IllegalArgumentException      if <code>row</code> or <code>column</code>
     *                                          are not in the valid range
     */
    public boolean isCellSelected(int row, int column) {
	if (!getRowSelectionAllowed() && !getColumnSelectionAllowed()) {
	    return false;
	}
	return (!getRowSelectionAllowed() || isRowSelected(row)) &&
               (!getColumnSelectionAllowed() || isColumnSelected(column));
    }

    private void changeSelectionModel(ListSelectionModel sm, int index,
				      boolean toggle, boolean extend, boolean selected) {
        if (extend) {
            if (toggle) {
		sm.setAnchorSelectionIndex(index);
	    }
	    else {
		sm.setLeadSelectionIndex(index);
	    }
        }
	else {
            if (toggle) {
                if (selected) {
                    sm.removeSelectionInterval(index, index);
                }
                else {
                    sm.addSelectionInterval(index, index);
                }
            }
	    else {
                sm.setSelectionInterval(index, index);
            }
        }
    }

    /**
     * Updates the selection models of the table, depending on the state of the
     * two flags: <code>toggle</code> and <code>extend</code>. All changes
     * to the selection that are the result of keyboard or mouse events received
     * by the UI are channeled through this method so that the behavior may be
     * overridden by a subclass.
     * <p>
     * This implementation uses the following conventions:
     * <ul>
     * <li> <code>toggle</code>: <em>false</em>, <code>extend</code>: <em>false</em>.
     *      Clear the previous selection and ensure the new cell is selected.
     * <li> <code>toggle</code>: <em>false</em>, <code>extend</code>: <em>true</em>.
     *      Extend the previous selection to include the specified cell.
     * <li> <code>toggle</code>: <em>true</em>, <code>extend</code>: <em>false</em>.
     *      If the specified cell is selected, deselect it. If it is not selected, select it.
     * <li> <code>toggle</code>: <em>true</em>, <code>extend</code>: <em>true</em>.
     *      Leave the selection state as it is, but move the anchor index to the specified location.
     * </ul>
     * @param  rowIndex   affects the selection at <code>row</code>
     * @param  columnIndex  affects the selection at <code>column</code>
     * @param  toggle  see description above
     * @param  extend  if true, extend the current selection
     *
     */
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        ListSelectionModel rsm = getSelectionModel();
        ListSelectionModel csm = getColumnModel().getSelectionModel();

	// Check the selection here rather than in each selection model.
	// This is significant in cell selection mode if we are supposed
	// to be toggling the selection. In this case it is better to
	// ensure that the cell's selection state will indeed be changed.
	// If this were done in the code for the selection model it
	// might leave a cell in selection state if the row was
	// selected but the column was not - as it would toggle them both.
	boolean selected = isCellSelected(rowIndex, columnIndex);

        changeSelectionModel(csm, columnIndex, toggle, extend, selected);
        changeSelectionModel(rsm, rowIndex, toggle, extend, selected);

        // Scroll after changing the selection as blit scrolling is immediate,
        // so that if we cause the repaint after the scroll we end up painting
        // everything!
        if (getAutoscrolls()) {
	    Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
	    if (cellRect != null) {
		scrollRectToVisible(cellRect);
	    }
	}
    }

    /**
     * Returns the foreground color for selected cells.
     *
     * @return the <code>Color</code> object for the foreground property
     * @see #setSelectionForeground
     * @see #setSelectionBackground
     */
    public Color getSelectionForeground() {
        return selectionForeground;
    }

    /**
     * Sets the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a <a href="http://java.sun.com/docs/books/tutorial/javabeans/whatis/beanDefinition.html">JavaBeans</a> bound property.
     *
     * @param selectionForeground  the <code>Color</code> to use in the foreground
     *                             for selected list items
     * @see #getSelectionForeground
     * @see #setSelectionBackground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo
     *       bound: true
     * description: A default foreground color for selected cells.
     */
    public void setSelectionForeground(Color selectionForeground) {
        Color old = this.selectionForeground;
        this.selectionForeground = selectionForeground;
        firePropertyChange("selectionForeground", old, selectionForeground);
        if ( !selectionForeground.equals(old) )
        {
            repaint();
        }
    }

    /**
     * Returns the background color for selected cells.
     *
     * @return the <code>Color</code> used for the background of selected list items
     * @see #setSelectionBackground
     * @see #setSelectionForeground
     */
    public Color getSelectionBackground() {
        return selectionBackground;
    }

    /**
     * Sets the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a <a href="http://java.sun.com/docs/books/tutorial/javabeans/whatis/beanDefinition.html">JavaBeans</a> bound property.
     *
     * @param selectionBackground  the <code>Color</code> to use for the background
     *                             of selected cells
     * @see #getSelectionBackground
     * @see #setSelectionForeground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo
     *       bound: true
     * description: A default background color for selected cells.
     */
    public void setSelectionBackground(Color selectionBackground) {
        Color old = this.selectionBackground;
        this.selectionBackground = selectionBackground;
        firePropertyChange("selectionBackground", old, selectionBackground);
        if ( !selectionBackground.equals(old) )
        {
            repaint();
        }
    }

    /**
     * Returns the <code>TableColumn</code> object for the column in the table
     * whose identifier is equal to <code>identifier</code>, when compared using
     * <code>equals</code>.
     *
     * @return  the <code>TableColumn</code> object that matches the identifier
     * @exception IllegalArgumentException      if <code>identifier</code> is <code>null</code> or no <code>TableColumn</code> has this identifier
     *
     * @param   identifier                      the identifier object
     */
    public TableColumn getColumn(Object identifier) {
        TableColumnModel cm = getColumnModel();
        int columnIndex = cm.getColumnIndex(identifier);
        return cm.getColumn(columnIndex);
    }

//
// Informally implement the TableModel interface.
//

    /**
     * Maps the index of the column in the view at
     * <code>viewColumnIndex</code> to the index of the column
     * in the table model.  Returns the index of the corresponding
     * column in the model.  If <code>viewColumnIndex</code>
     * is less than zero, returns <code>viewColumnIndex</code>.
     *
     * @param   viewColumnIndex     the index of the column in the view
     * @return  the index of the corresponding column in the model
     *
     * @see #convertColumnIndexToView
     */
    public int convertColumnIndexToModel(int viewColumnIndex) {
        if (viewColumnIndex < 0) {
            return viewColumnIndex;
        }
        return getColumnModel().getColumn(viewColumnIndex).getModelIndex();
    }

    /**
     * Maps the index of the column in the table model at
     * <code>modelColumnIndex</code> to the index of the column
     * in the view.  Returns the index of the
     * corresponding column in the view; returns -1 if this column is not
     * being displayed.  If <code>modelColumnIndex</code> is less than zero,
     * returns <code>modelColumnIndex</code>.
     *
     * @param   modelColumnIndex     the index of the column in the model
     * @return   the index of the corresponding column in the view
     *
     * @see #convertColumnIndexToModel
     */
    public int convertColumnIndexToView(int modelColumnIndex) {
        if (modelColumnIndex < 0) {
            return modelColumnIndex;
        }
        TableColumnModel cm = getColumnModel();
        for (int column = 0; column < getColumnCount(); column++) {
            if (cm.getColumn(column).getModelIndex() == modelColumnIndex) {
                return column;
            }
        }
        return -1;
    }

    /**
     * Returns the number of rows in this table's model.
     * @return the number of rows in this table's model
     *
     * @see #getColumnCount
     */
    public int getRowCount() {
        return getModel().getRowCount();
    }

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     * @see #removeColumn
     */
    public int getColumnCount() {
        return getColumnModel().getColumnCount();
    }

    /**
     * Returns the name of the column appearing in the view at
     * column position <code>column</code>.
     *
     * @param  column    the column in the view being queried
     * @return the name of the column at position <code>column</code>
			in the view where the first column is column 0
     */
    public String getColumnName(int column) {
        return getModel().getColumnName(convertColumnIndexToModel(column));
    }

    /**
     * Returns the type of the column appearing in the view at
     * column position <code>column</code>.
     *
     * @param   column   the column in the view being queried
     * @return the type of the column at position <code>column</code>
     * 		in the view where the first column is column 0
     */
    public Class getColumnClass(int column) {
        return getModel().getColumnClass(convertColumnIndexToModel(column));
    }

    /**
     * Returns the cell value at <code>row</code> and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s column
     *		    order.  This is an important distinction because as the
     *		    user rearranges the columns in the table,
     *		    the column at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return  the Object at the specified cell
     */
    public Object getValueAt(int row, int column) {
        return getModel().getValueAt(row, convertColumnIndexToModel(column));
    }

    /**
     * Sets the value for the cell in the table model at <code>row</code>
     * and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s column
     *		    order.  This is an important distinction because as the
     *		    user rearranges the columns in the table,
     *		    the column at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     * <code>aValue</code> is the new value.
     *
     * @param   aValue          the new value
     * @param   row             the row of the cell to be changed
     * @param   column          the column of the cell to be changed
     * @see #getValueAt
     */
    public void setValueAt(Object aValue, int row, int column) {
        getModel().setValueAt(aValue, row, convertColumnIndexToModel(column));
    }

    /**
     * Returns true if the cell at <code>row</code> and <code>column</code>
     * is editable.  Otherwise, invoking <code>setValueAt</code> on the cell
     * will have no effect.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s column
     *		    order.  This is an important distinction because as the
     *		    user rearranges the columns in the table,
     *		    the column at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     *
     * @param   row      the row whose value is to be queried
     * @param   column   the column whose value is to be queried
     * @return  true if the cell is editable
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return getModel().isCellEditable(row, convertColumnIndexToModel(column));
    }
//
// Adding and removing columns in the view
//

    /**
     *  Appends <code>aColumn</code> to the end of the array of columns held by
     *  this <code>JTable</code>'s column model.
     *  If the column name of <code>aColumn</code> is <code>null</code>,
     *  sets the column name of <code>aColumn</code> to the name
     *  returned by <code>getModel().getColumnName()</code>.
     *  <p>
     *  To add a column to this <code>JTable</code> to display the
     *  <code>modelColumn</code>'th column of data in the model with a
     *  given <code>width</code>, <code>cellRenderer</code>,
     *  and <code>cellEditor</code> you can use:
     *  <pre>
     *
     *      addColumn(new TableColumn(modelColumn, width, cellRenderer, cellEditor));
     *
     *  </pre>
     *  [Any of the <code>TableColumn</code> constructors can be used
     *  instead of this one.]
     *  The model column number is stored inside the <code>TableColumn</code>
     *  and is used during rendering and editing to locate the appropriates
     *  data values in the model. The model column number does not change
     *  when columns are reordered in the view.
     *
     *  @param  aColumn         the <code>TableColumn</code> to be added
     *  @see    #removeColumn
     */
    public void addColumn(TableColumn aColumn) {
        if (aColumn.getHeaderValue() == null) {
	    int modelColumn = aColumn.getModelIndex();
	    String columnName = getModel().getColumnName(modelColumn);
            aColumn.setHeaderValue(columnName);
        }
        getColumnModel().addColumn(aColumn);
    }

    /**
     *  Removes <code>aColumn</code> from this <code>JTable</code>'s
     *  array of columns.  Note: this method does not remove the column
     *  of data from the model; it just removes the <code>TableColumn</code>
     *  that was responsible for displaying it.
     *
     *  @param  aColumn         the <code>TableColumn</code> to be removed
     *  @see    #addColumn
     */
    public void removeColumn(TableColumn aColumn) {
        getColumnModel().removeColumn(aColumn);
    }

    /**
     * Moves the column <code>column</code> to the position currently
     * occupied by the column <code>targetColumn</code> in the view.
     * The old column at <code>targetColumn</code> is
     * shifted left or right to make room.
     *
     * @param   column                  the index of column to be moved
     * @param   targetColumn            the new index of the column
     */
    public void moveColumn(int column, int targetColumn) {
        getColumnModel().moveColumn(column, targetColumn);
    }

//
// Cover methods for various models and helper methods
//

    /**
     * Returns the index of the column that <code>point</code> lies in,
     * or -1 if the result is not in the range
     * [0, <code>getColumnCount()</code>-1].
     *
     * @param   point   the location of interest
     * @return  the index of the column that <code>point</code> lies in,
     *		or -1 if the result is not in the range
     *		[0, <code>getColumnCount()</code>-1]
     * @see     #rowAtPoint
     */
    public int columnAtPoint(Point point) {
        int x = point.x;
        if( !getComponentOrientation().isLeftToRight() ) {
            x = getWidth() - x;
        }
        return getColumnModel().getColumnIndexAtX(x);
    }

    /**
     * Returns the index of the row that <code>point</code> lies in,
     * or -1 if the result is not in the range
     * [0, <code>getRowCount()</code>-1].
     *
     * @param   point   the location of interest
     * @return  the index of the row that <code>point</code> lies in,
     *          or -1 if the result is not in the range
     *          [0, <code>getRowCount()</code>-1]
     * @see     #columnAtPoint
     */
    public int rowAtPoint(Point point) {
        int y = point.y;
	int result = (rowModel == null) ?  y/getRowHeight() : rowModel.getIndex(y);
        if (result < 0) {
            return -1;
        }
        else if (result >= getRowCount()) {
            return -1;
        }
        else {
            return result;
        }
    }

    /**
     * Returns a rectangle for the cell that lies at the intersection of
     * <code>row</code> and <code>column</code>.
     * If <code>includeSpacing</code> is true then the value returned
     * has the full height and width of the row and column
     * specified. If it is false, the returned rectangle is inset by the
     * intercell spacing to return the true bounds of the rendering or
     * editing component as it will be set during rendering.
     * <p>
     * If the column index is valid but the row index is less
     * than zero the method returns a rectangle with the
     * <code>y</code> and <code>height</code> values set appropriately
     * and the <code>x</code> and <code>width</code> values both set
     * to zero. In general, when either the row or column indices indicate a
     * cell outside the appropriate range, the method returns a rectangle
     * depicting the closest edge of the closest cell that is within
     * the table's range. When both row and column indices are out
     * of range the returned rectangle covers the closest
     * point of the closest cell.
     * <p>
     * In all cases, calculations that use this method to calculate
     * results along one axis will not fail because of anomalies in
     * calculations along the other axis. When the cell is not valid
     * the <code>includeSpacing</code> parameter is ignored.
     *
     * @param   row                   the row index where the desired cell
     *                                is located
     * @param   column                the column index where the desired cell
     *                                is located in the display; this is not
     *                                necessarily the same as the column index
     *                                in the data model for the table; the
     *                                {@link #convertColumnIndexToView(int)}
     *                                method may be used to convert a data
     *                                model column index to a display
     *                                column index
     * @param   includeSpacing        if false, return the true cell bounds -
     *                                computed by subtracting the intercell
     *				      spacing from the height and widths of
     *				      the column and row models
     *
     * @return  the rectangle containing the cell at location
     *          <code>row</code>,<code>column</code>
     */
    public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        Rectangle r = new Rectangle();
	boolean valid = true;
	if (row < 0) {
	    // y = height = 0;
	    valid = false;
	}
	else if (row >= getRowCount()) {
	    r.y = getHeight();
	    valid = false;
	}
	else {
	    r.height = getRowHeight(row);
	    r.y = (rowModel == null) ? row * r.height : rowModel.getPosition(row);
	}

	if (column < 0) {
	    if( !getComponentOrientation().isLeftToRight() ) {
		r.x = getWidth();
	    }
	    // otherwise, x = width = 0;
	    valid = false;
	}
	else if (column >= getColumnCount()) {
	    if( getComponentOrientation().isLeftToRight() ) {
		r.x = getWidth();
	    }
	    // otherwise, x = width = 0;
	    valid = false;
	}
	else { 
            TableColumnModel cm = getColumnModel(); 
            if( getComponentOrientation().isLeftToRight() ) {
                for(int i = 0; i < column; i++) { 
                    r.x += cm.getColumn(i).getWidth();
                }
            } else {
                for(int i = cm.getColumnCount()-1; i > column; i--) {
                    r.x += cm.getColumn(i).getWidth();
                }
            }
            r.width = cm.getColumn(column).getWidth(); 
	}

        if (valid && !includeSpacing) {
            int rm = getRowMargin();
            int cm = getColumnModel().getColumnMargin();
            // This is not the same as grow(), it rounds differently.
            r.setBounds(r.x + cm/2, r.y + rm/2, r.width - cm, r.height - rm);
        }
        return r;
    }

    private int viewIndexForColumn(TableColumn aColumn) {
	TableColumnModel cm = getColumnModel();
	for (int column = 0; column < cm.getColumnCount(); column++) {
	    if (cm.getColumn(column) == aColumn) {
		return column;
	    }
	}
	return -1;
    }

    /**
     * Causes this table to lay out its rows and columns.  Overridden so
     * that columns can be resized to accomodate a change in the size of
     * a containing parent.
     * Resizes one or more of the columns in the table
     * so that the total width of all of this <code>JTable</code>'s
     * columns is equal to the width of the table.
     * <p>
     * Before the layout begins the method gets the
     * <code>resizingColumn</code> of the <code>tableHeader</code>.
     * When the method is called as a result of the resizing of an enclosing window,
     * the <code>resizingColumn</code> is <code>null</code>. This means that resizing
     * has taken place "outside" the <code>JTable</code> and the change -
     * or "delta" - should be distributed to all of the columns regardless
     * of this <code>JTable</code>'s automatic resize mode.
     * <p>
     * If the <code>resizingColumn</code> is not <code>null</code>, it is one of
     * the columns in the table that has changed size rather than
     * the table itself. In this case the auto-resize modes govern
     * the way the extra (or deficit) space is distributed
     * amongst the available columns.
     * <p>
     * The modes are:
     * <ul>
     * <li>  AUTO_RESIZE_OFF: Don't automatically adjust the column's
     * widths at all. Use a horizontal scrollbar to accomodate the
     * columns when their sum exceeds the width of the
     * <code>Viewport</code>.  If the <code>JTable</code> is not
     * enclosed in a <code>JScrollPane</code> this may
     * leave parts of the table invisible.
     * <li>  AUTO_RESIZE_NEXT_COLUMN: Use just the column after the
     * resizing column. This results in the "boundary" or divider
     * between adjacent cells being independently adjustable.
     * <li>  AUTO_RESIZE_SUBSEQUENT_COLUMNS: Use all columns after the
     * one being adjusted to absorb the changes.  This is the
     * default behavior.
     * <li>  AUTO_RESIZE_LAST_COLUMN: Automatically adjust the
     * size of the last column only. If the bounds of the last column
     * prevent the desired size from being allocated, set the
     * width of the last column to the appropriate limit and make
     * no further adjustments.
     * <li>  AUTO_RESIZE_ALL_COLUMNS: Spread the delta amongst all the columns
     * in the <code>JTable</code>, including the one that is being
     * adjusted.
     * </ul>
     * <p>
     * <bold>Note:</bold> When a <code>JTable</code> makes adjustments
     *   to the widths of the columns it respects their minimum and
     *   maximum values absolutely.  It is therefore possible that,
     *   even after this method is called, the total width of the columns
     *   is still not equal to the width of the table. When this happens
     *   the <code>JTable</code> does not put itself
     *   in AUTO_RESIZE_OFF mode to bring up a scroll bar, or break other
     *   commitments of its current auto-resize mode -- instead it
     *   allows its bounds to be set larger (or smaller) than the total of the
     *   column minimum or maximum, meaning, either that there
     *   will not be enough room to display all of the columns, or that the
     *   columns will not fill the <code>JTable</code>'s bounds.
     *   These respectively, result in the clipping of some columns
     *   or an area being painted in the <code>JTable</code>'s
     *   background color during painting.
     * <p>
     *   The mechanism for distributing the delta amongst the available
     *   columns is provided in a private method in the <code>JTable</code>
     *   class:
     * <pre>
     *   adjustSizes(long targetSize, final Resizable3 r, boolean inverse)
     * </pre>
     *   an explanation of which is provided in the following section.
     *   <code>Resizable3</code> is a private
     *   interface that allows any data structure containing a collection
     *   of elements with a size, preferred size, maximum size and minimum size
     *   to have its elements manipulated by the algorithm.
     * <p>
     * <H3> Distributing the delta </H3>
     * <p>
     * <H4> Overview </H4>
     * <P>
     * Call "DELTA" the difference between the target size and the
     * sum of the preferred sizes of the elements in r. The individual
     * sizes are calculated by taking the original preferred
     * sizes and adding a share of the DELTA - that share being based on
     * how far each preferred size is from its limiting bound (minimum or
     * maximum).
     * <p>
     * <H4>Definition</H4>
     * <P>
     * Call the individual constraints min[i], max[i], and pref[i].
     * <p>
     * Call their respective sums: MIN, MAX, and PREF.
     * <p>
     * Each new size will be calculated using:
     * <p>
     * <pre>
     *          size[i] = pref[i] + delta[i]
     * </pre>
     * where each individual delta[i] is calculated according to:
     * <p>
     * If (DELTA < 0) we are in shrink mode where:
     * <p>
     * <PRE>
     *                        DELTA
     *          delta[i] = ------------ * (pref[i] - min[i])
     *                     (PREF - MIN)
     * </PRE>
     * If (DELTA > 0) we are in expand mode where:
     * <p>
     * <PRE>
     *                        DELTA
     *          delta[i] = ------------ * (max[i] - pref[i])
     *                      (MAX - PREF)
     * </PRE>
     * <P>
     * The overall effect is that the total size moves that same percentage,
     * k, towards the total minimum or maximum and that percentage guarantees
     * accomodation of the required space, DELTA.
     *
     * <H4>Details</H4>
     * <P>
     * Naive evaluation of the formulae presented here would be subject to
     * the aggregated rounding errors caused by doing this operation in finite
     * precision (using ints). To deal with this, the multiplying factor above,
     * is constantly recalculated and this takes account of the rounding
     * errors in the previous iterations. The result is an algorithm that
     * produces a set of integers whose values exactly sum to the supplied
     * <code>targetSize</code>, and does so by spreading the rounding
     * errors evenly over the given elements.
     *
     * <H4>When the MAX and MIN bounds are hit</H4>
     * <P>
     * When <code>targetSize</code> is outside the [MIN, MAX] range,
     * the algorithm sets all sizes to their appropriate limiting value
     * (maximum or minimum).
     *
     */
    public void doLayout() {
	TableColumn resizingColumn = getResizingColumn();
	if (resizingColumn == null) {
            setWidthsFromPreferredWidths(false);
	}
        else {
	    int columnIndex = viewIndexForColumn(resizingColumn);
	    int delta = getWidth() - getColumnModel().getTotalColumnWidth();
	    accommodateDelta(columnIndex, delta);
	    delta = getWidth() - getColumnModel().getTotalColumnWidth();
	    if (delta != 0) {
		resizingColumn.setWidth(resizingColumn.getWidth() + delta);
	    }
	}

	super.doLayout();
    }

    private TableColumn getResizingColumn() {
	return (tableHeader == null) ? null
	                             : tableHeader.getResizingColumn();
    }

    /**
     * Sizes the table columns to fit the available space.
     * @deprecated As of Swing version 1.0.3,
     * replaced by <code>doLayout()</code>.
     * @see #doLayout
     */
    public void sizeColumnsToFit(boolean lastColumnOnly) {
        int oldAutoResizeMode = autoResizeMode;
        setAutoResizeMode(lastColumnOnly ? AUTO_RESIZE_LAST_COLUMN
                                         : AUTO_RESIZE_ALL_COLUMNS);
        sizeColumnsToFit(-1);
        setAutoResizeMode(oldAutoResizeMode);
    }

    /**
     * Obsolete as of Java 2 platform v1.4.  Please use the
     * <code>doLayout()</code> method instead.
     * @param resizingColumn    the column whose resizing made this adjustment
     *                          necessary or -1 if there is no such column
     * @see  #doLayout
     */
    public void sizeColumnsToFit(int resizingColumn) {
        if (resizingColumn == -1) {
            setWidthsFromPreferredWidths(false);
	}
	else {
	    if (autoResizeMode == AUTO_RESIZE_OFF) {
                TableColumn aColumn = getColumnModel().getColumn(resizingColumn);
                aColumn.setPreferredWidth(aColumn.getWidth());
	    }
	    else {
                int delta = getWidth() - getColumnModel().getTotalColumnWidth();
	        accommodateDelta(resizingColumn, delta);
	    }
	}
    }

    private void setWidthsFromPreferredWidths(final boolean inverse) {
        int totalWidth     = getWidth();
	int totalPreferred = getPreferredSize().width;
	int target = !inverse ? totalWidth : totalPreferred;

	final TableColumnModel cm = columnModel;
	Resizable3 r = new Resizable3() {
	    public int  getElementCount()      { return cm.getColumnCount(); }
	    public int  getLowerBoundAt(int i) { return cm.getColumn(i).getMinWidth(); }
	    public int  getUpperBoundAt(int i) { return cm.getColumn(i).getMaxWidth(); }
	    public int  getMidPointAt(int i)  {
	        if (!inverse) {
		    return cm.getColumn(i).getPreferredWidth();
	        }
	        else {
		    return cm.getColumn(i).getWidth();
	        }
	    }
	    public void setSizeAt(int s, int i) {
	        if (!inverse) {
		    cm.getColumn(i).setWidth(s);
	        }
	        else {
		    cm.getColumn(i).setPreferredWidth(s);
	        }
	    }
	};

	adjustSizes(target, r, inverse);
    }


    // Distribute delta over columns, as indicated by the autoresize mode.
    private void accommodateDelta(int resizingColumnIndex, int delta) {
        int columnCount = getColumnCount();
        int from = resizingColumnIndex;
        int to = columnCount;

	// Use the mode to determine how to absorb the changes.
	switch(autoResizeMode) {
	    case AUTO_RESIZE_NEXT_COLUMN:
		from = from + 1;
		to = Math.min(from + 1, columnCount); break;
	    case AUTO_RESIZE_SUBSEQUENT_COLUMNS:
		from = from + 1;
		to = columnCount; break;
	    case AUTO_RESIZE_LAST_COLUMN:
		from = columnCount - 1;
		to = from + 1; break;
	    case AUTO_RESIZE_ALL_COLUMNS:
		from = 0;
		to = columnCount; break;
	    default:
		return;
	}

	final int start = from;
	final int end = to;
	final TableColumnModel cm = columnModel;
	Resizable3 r = new Resizable3() {
	    public int  getElementCount()       { return end-start; }
	    public int  getLowerBoundAt(int i)  { return cm.getColumn(i+start).getMinWidth(); }
	    public int  getUpperBoundAt(int i)  { return cm.getColumn(i+start).getMaxWidth(); }
	    public int  getMidPointAt(int i)    { return cm.getColumn(i+start).getWidth(); }
	    public void setSizeAt(int s, int i) {        cm.getColumn(i+start).setWidth(s); }
	};

	int totalWidth = 0;
        for(int i = from; i < to; i++) {
            TableColumn aColumn = columnModel.getColumn(i);
            int input = aColumn.getWidth();
	    totalWidth = totalWidth + input;
        }

        adjustSizes(totalWidth + delta, r, false);

	setWidthsFromPreferredWidths(true);
	// setWidthsFromPreferredWidths(false);
	return;
    }

    private interface Resizable2 {
        public int  getElementCount();
        public int  getLowerBoundAt(int i);
        public int  getUpperBoundAt(int i);
        public void setSizeAt(int newSize, int i);
    }

    private interface Resizable3 extends Resizable2 {
        public int  getMidPointAt(int i);
    }


    private void adjustSizes(long target, final Resizable3 r, boolean inverse) {
	int N = r.getElementCount();
	long totalPreferred = 0;
	for(int i = 0; i < N; i++) {
	    totalPreferred += r.getMidPointAt(i);
	}
	Resizable2 s;
        if ((target < totalPreferred) == !inverse) {
	    s = new Resizable2() {
	        public int  getElementCount()      { return r.getElementCount(); }
	        public int  getLowerBoundAt(int i) { return r.getLowerBoundAt(i); }
	        public int  getUpperBoundAt(int i) { return r.getMidPointAt(i); }
	        public void setSizeAt(int newSize, int i) { r.setSizeAt(newSize, i); }

	    };
	}
	else {
	    s = new Resizable2() {
	        public int  getElementCount()      { return r.getElementCount(); }
	        public int  getLowerBoundAt(int i) { return r.getMidPointAt(i); }
	        public int  getUpperBoundAt(int i) { return r.getUpperBoundAt(i); }
	        public void setSizeAt(int newSize, int i) { r.setSizeAt(newSize, i); }

	    };
	}
	adjustSizes(target, s, !inverse);
    }

    private void adjustSizes(long target, Resizable2 r, boolean limitToRange) {
	long totalLowerBound = 0;
	long totalUpperBound = 0;
	for(int i = 0; i < r.getElementCount(); i++) {
	    totalLowerBound += r.getLowerBoundAt(i);
	    totalUpperBound += r.getUpperBoundAt(i);
	}

	if (limitToRange) {
	    target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
	}

	for(int i = 0; i < r.getElementCount(); i++) {
	    int lowerBound = r.getLowerBoundAt(i);
	    int upperBound = r.getUpperBoundAt(i);
	    // Check for zero. This happens when the distribution of the delta
	    // finishes early due to a series of "fixed" entries at the end.
	    // In this case, lowerBound == upperBound, for all subsequent terms.
	    int newSize;
	    if (totalLowerBound == totalUpperBound) {
	        newSize = lowerBound;
	    }
	    else {
	        double f = (double)(target - totalLowerBound)/(totalUpperBound - totalLowerBound);
		newSize = (int)Math.round(lowerBound+f*(upperBound - lowerBound));
		// We'd need to round manually in an all integer version.
	        // size[i] = (int)(((totalUpperBound - target) * lowerBound +
		//     (target - totalLowerBound) * upperBound)/(totalUpperBound-totalLowerBound));
	    }
	    r.setSizeAt(newSize, i);
	    target -= newSize;
	    totalLowerBound -= lowerBound;
	    totalUpperBound -= upperBound;
	}
    }

    /**
     * Overrides <code>JComponent</code>'s <code>getToolTipText</code>
     * method in order to allow the renderer's tips to be used
     * if it has text set.
     * <p>
     * <bold>Note:</bold> For <code>JTable</code> to properly display
     * tooltips of its renderers
     * <code>JTable</code> must be a registered component with the
     * <code>ToolTipManager</code>.
     * This is done automatically in <code>initializeLocalVars</code>,
     * but if at a later point <code>JTable</code> is told
     * <code>setToolTipText(null)</code> it will unregister the table
     * component, and no tips from renderers will display anymore.
     *
     * @see JComponent#getToolTipText
     */
    public String getToolTipText(MouseEvent event) {
        String tip = null;
        Point p = event.getPoint();

        // Locate the renderer under the event location
        int hitColumnIndex = columnAtPoint(p);
        int hitRowIndex = rowAtPoint(p);

        if ((hitColumnIndex != -1) && (hitRowIndex != -1)) {
            TableCellRenderer renderer = getCellRenderer(hitRowIndex, hitColumnIndex);
            Component component = prepareRenderer(renderer, hitRowIndex, hitColumnIndex);

            // Now have to see if the component is a JComponent before
            // getting the tip
            if (component instanceof JComponent) {
                // Convert the event to the renderer's coordinate system
                Rectangle cellRect = getCellRect(hitRowIndex, hitColumnIndex, false);
                p.translate(-cellRect.x, -cellRect.y);
                MouseEvent newEvent = new MouseEvent(component, event.getID(),
                                          event.getWhen(), event.getModifiers(),
                                          p.x, p.y, event.getClickCount(),
                                          event.isPopupTrigger());

                tip = ((JComponent)component).getToolTipText(newEvent);
            }
        }

        // No tip from the renderer get our own tip
        if (tip == null)
            tip = getToolTipText();

        return tip;
    }

//
// Editing Support
//

    /**
     * Sets whether editors in this JTable get the keyboard focus
     * when an editor is activated as a result of the JTable
     * forwarding keyboard events for a cell.
     * By default, this property is false, and the JTable
     * retains the focus unless the cell is clicked.
     *
     * @param surrendersFocusOnKeystroke true if the editor should get the focus
     *          when keystrokes cause the editor to be
     *          activated
     *
     *
     * @see #getSurrendersFocusOnKeystroke
     */
    public void setSurrendersFocusOnKeystroke(boolean surrendersFocusOnKeystroke) {
        this.surrendersFocusOnKeystroke = surrendersFocusOnKeystroke;
    }

    /**
     * Returns true if the editor should get the focus
     * when keystrokes cause the editor to be activated
     *
     * @return  true if the editor should get the focus
     *          when keystrokes cause the editor to be
     *          activated
     *
     * @see #setSurrendersFocusOnKeystroke
     */
    public boolean getSurrendersFocusOnKeystroke() {
        return surrendersFocusOnKeystroke;
    }

    /**
     * Programmatically starts editing the cell at <code>row</code> and
     * <code>column</code>, if the cell is editable.  Note that this is
     * a convenience method for <code>editCellAt(int, int, null)</code>.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @exception IllegalArgumentException      if <code>row</code> or
     *                                          <code>column</code>
     *                                          is not in the valid range
     * @return  false if for any reason the cell cannot be edited
     */
    public boolean editCellAt(int row, int column) {
        return editCellAt(row, column, null);
    }

    /**
     * Programmatically starts editing the cell at <code>row</code> and
     * <code>column</code>, if the cell is editable.
     * To prevent the <code>JTable</code> from editing a particular table,
     * column or cell value, return false from the <code>isCellEditable</code>
     * method in the <code>TableModel</code> interface.
     *
     * @param   row     the row to be edited
     * @param   column  the column to be edited
     * @param   e       event to pass into <code>shouldSelectCell</code>;
     *                  note that as of Java 2 platform v1.2, the call to
     *                  <code>shouldSelectCell</code> is no longer made
     * @exception IllegalArgumentException      if <code>row</code> or
     *                                          <code>column</code>
     *                                          is not in the valid range
     * @return  false if for any reason the cell cannot be edited
     */
    public boolean editCellAt(int row, int column, EventObject e){
        if (cellEditor != null && !cellEditor.stopCellEditing()) {
            return false;
        }

	if (row < 0 || row >= getRowCount() ||
	    column < 0 || column >= getColumnCount()) {
	    return false;
	}

        if (!isCellEditable(row, column))
            return false;

        if (editorRemover == null) {
            KeyboardFocusManager fm =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
            editorRemover = new CellEditorRemover(fm);
            fm.addPropertyChangeListener("permanentFocusOwner", editorRemover);
        }

        TableCellEditor editor = getCellEditor(row, column);
        if (editor != null && editor.isCellEditable(e)) {
	    editorComp = prepareEditor(editor, row, column);
	    if (editorComp == null) {
		removeEditor();
		return false;
	    }
	    editorComp.setBounds(getCellRect(row, column, false));
	    add(editorComp);
	    editorComp.validate();

	    setCellEditor(editor);
	    setEditingRow(row);
	    setEditingColumn(column);
	    editor.addCellEditorListener(this);

	    return true;
        }
        return false;
    }

    /**
     * Returns true if a cell is being edited.
     *
     * @return  true if the table is editing a cell
     * @see     #editingColumn
     * @see     #editingRow
     */
    public boolean isEditing() {
        return (cellEditor == null)? false : true;
    }

    /**
     * Returns the component that is handling the editing session.
     * If nothing is being edited, returns null.
     *
     * @return  Component handling editing session
     */
    public Component getEditorComponent() {
        return editorComp;
    }

    /**
     * Returns the index of the column that contains the cell currently
     * being edited.  If nothing is being edited, returns -1.
     *
     * @return  the index of the column that contains the cell currently
     *		being edited; returns -1 if nothing being edited
     * @see #editingRow
     */
    public int getEditingColumn() {
        return editingColumn;
    }

    /**
     * Returns the index of the row that contains the cell currently
     * being edited.  If nothing is being edited, returns -1.
     *
     * @return  the index of the row that contains the cell currently
     *		being edited; returns -1 if nothing being edited
     * @see #editingColumn
     */
    public int getEditingRow() {
        return editingRow;
    }

//
// Managing TableUI
//

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the <code>TableUI</code> object that renders this component
     */
    public TableUI getUI() {
        return (TableUI)ui;
    }

    /**
     * Sets the L&F object that renders this component and repaints.
     *
     * @param ui  the TableUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel.
     */
    public void setUI(TableUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            repaint();
        }
    }

    private void updateSubComponentUI(Object componentShell) {
        if (componentShell == null) {
            return;
        }
        Component component = null;
        if (componentShell instanceof Component) {
            component = (Component)componentShell;
        }
        if (componentShell instanceof DefaultCellEditor) {
            component = ((DefaultCellEditor)componentShell).getComponent();
        }

        if (component != null && component instanceof JComponent) {
            ((JComponent)component).updateUI();
        }
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        // Update the UIs of the cell renderers, cell editors and header renderers.
        TableColumnModel cm = getColumnModel();
        for(int column = 0; column < cm.getColumnCount(); column++) {
            TableColumn aColumn = cm.getColumn(column);
	    updateSubComponentUI(aColumn.getCellRenderer());
            updateSubComponentUI(aColumn.getCellEditor());
	    updateSubComponentUI(aColumn.getHeaderRenderer());
        }

        // Update the UIs of all the default renderers.
        Enumeration defaultRenderers = defaultRenderersByColumnClass.elements();
        while (defaultRenderers.hasMoreElements()) {
            updateSubComponentUI(defaultRenderers.nextElement());
        }

        // Update the UIs of all the default editors.
        Enumeration defaultEditors = defaultEditorsByColumnClass.elements();
        while (defaultEditors.hasMoreElements()) {
            updateSubComponentUI(defaultEditors.nextElement());
        }

        setUI((TableUI)UIManager.getUI(this));
        resizeAndRepaint();
    }

    /**
     * Returns the suffix used to construct the name of the L&F class used to
     * render this component.
     *
     * @return the string "TableUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


//
// Managing models
//

    /**
     * Sets the data model for this table to <code>newModel</code> and registers
     * with it for listener notifications from the new data model.
     *
     * @param   dataModel        the new data source for this table
     * @exception IllegalArgumentException      if <code>newModel</code> is <code>null</code>
     * @see     #getModel
     * @beaninfo
     *  bound: true
     *  description: The model that is the source of the data for this view.
     */
    public void setModel(TableModel dataModel) {
        if (dataModel == null) {
            throw new IllegalArgumentException("Cannot set a null TableModel");
	}
        if (this.dataModel != dataModel) {
	    TableModel old = this.dataModel;
            if (old != null) {
                old.removeTableModelListener(this);
	    }
            this.dataModel = dataModel;
            dataModel.addTableModelListener(this);
            // If this method is called from the JTable constructor,
            // the column model will be null. In this case we can't use
            // the usual methods to update the internal state. In all other
            // cases, use the usual tableChanged() method to reconfigure
            // the JTable for the new model.
            if (getColumnModel() != null) {
                tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
            }
	    firePropertyChange("model", old, dataModel);
        }
    }

    /**
     * Returns the <code>TableModel</code> that provides the data displayed by this
     * <code>JTable</code>.
     *
     * @return  the <code>TableModel</code> that provides the data displayed by this <code>JTable</code>
     * @see     #setModel
     */
    public TableModel getModel() {
        return dataModel;
    }

    /**
     * Sets the column model for this table to <code>newModel</code> and registers
     * for listener notifications from the new column model. Also sets
     * the column model of the <code>JTableHeader</code> to <code>columnModel</code>.
     *
     * @param   columnModel        the new data source for this table
     * @exception IllegalArgumentException      if <code>columnModel</code> is <code>null</code>
     * @see     #getColumnModel
     * @beaninfo
     *  bound: true
     *  description: The object governing the way columns appear in the view.
     */
    public void setColumnModel(TableColumnModel columnModel) {
        if (columnModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }
        TableColumnModel old = this.columnModel;
        if (columnModel != old) {
            if (old != null) {
                old.removeColumnModelListener(this);
	    }
            this.columnModel = columnModel;
            columnModel.addColumnModelListener(this);

            // Set the column model of the header as well.
            if (tableHeader != null) {
                tableHeader.setColumnModel(columnModel);
            }

	    firePropertyChange("columnModel", old, columnModel);
            resizeAndRepaint();
        }
    }

    /**
     * Returns the <code>TableColumnModel</code> that contains all column information
     * of this table.
     *
     * @return  the object that provides the column state of the table
     * @see     #setColumnModel
     */
    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    /**
     * Sets the row selection model for this table to <code>newModel</code>
     * and registers for listener notifications from the new selection model.
     *
     * @param   newModel        the new selection model
     * @exception IllegalArgumentException      if <code>newModel</code> is <code>null</code>
     * @see     #getSelectionModel
     * @beaninfo
     *      bound: true
     *      description: The selection model for rows.
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

            selectionModel = newModel;

            if (newModel != null) {
                newModel.addListSelectionListener(this);
            }
	    firePropertyChange("selectionModel", oldModel, newModel);
            repaint();
        }
    }

    /**
     * Returns the <code>ListSelectionModel</code> that is used to maintain row
     * selection state.
     *
     * @return  the object that provides row selection state, <code>null</code>
     *          if row selection is not allowed
     * @see     #setSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

//
// Implementing TableModelListener interface
//

    /**
     * Invoked when this table's <code>TableModel</code> generates
     * a <code>TableModelEvent</code>.
     * The <code>TableModelEvent</code> should be constructed in the
     * coordinate system of the model; the appropriate mapping to the
     * view coordinate system is performed by this <code>JTable</code>
     * when it receives the event.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by <code>JTable</code>.
     * <p>
     * Note that as of 1.3, this method clears the selection, if any.
     */
    public void tableChanged(TableModelEvent e) {
        if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            // The whole thing changed
            clearSelection();
            rowModel = null;

            if (getAutoCreateColumnsFromModel()) {
		// This will effect invalidation of the JTable and JTableHeader.
                createDefaultColumnsFromModel();
		return;
	    }

	    resizeAndRepaint();
            return;
        }

	// The totalRowHeight calculated below will be incorrect if
	// there are variable height rows. Repaint the visible region,
	// but don't return as a revalidate may be necessary as well.
	if (rowModel != null) {
	    repaint();
	}

        if (e.getType() == TableModelEvent.INSERT) {
            tableRowsInserted(e);
            return;
        }

        if (e.getType() == TableModelEvent.DELETE) {
            tableRowsDeleted(e);
            return;
        }

        int modelColumn = e.getColumn();
        int start = e.getFirstRow();
        int end = e.getLastRow();

        Rectangle dirtyRegion;
        if (modelColumn == TableModelEvent.ALL_COLUMNS) {
            // 1 or more rows changed
            dirtyRegion = new Rectangle(0, start * getRowHeight(),
                                        getColumnModel().getTotalColumnWidth(), 0);
        }
        else {
            // A cell or column of cells has changed.
            // Unlike the rest of the methods in the JTable, the TableModelEvent
            // uses the coordinate system of the model instead of the view.
            // This is the only place in the JTable where this "reverse mapping"
            // is used.
            int column = convertColumnIndexToView(modelColumn);
            dirtyRegion = getCellRect(start, column, false);
        }

        // Now adjust the height of the dirty region according to the value of "end".
        // Check for Integer.MAX_VALUE as this will cause an overflow.
        if (end != Integer.MAX_VALUE) {
	    dirtyRegion.height = (end-start+1)*getRowHeight();
            repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
        }
        // In fact, if the end is Integer.MAX_VALUE we need to revalidate anyway
        // because the scrollbar may need repainting.
        else {
	    clearSelection();
            resizeAndRepaint();
            rowModel = null;
        }
    }

    /*
     * Invoked when rows have been inserted into the table.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param e the TableModelEvent encapsulating the insertion
     */
    private void tableRowsInserted(TableModelEvent e) {
        int start = e.getFirstRow();
        int end = e.getLastRow();
        if (start < 0) {
            start = 0;
	}
	if (end < 0) {
	    end = getRowCount()-1;
	}

        // Adjust the selection to account for the new rows.
	int length = end - start + 1;
	selectionModel.insertIndexInterval(start, length, true);

	// If we have variable height rows, adjust the row model.
	if (rowModel != null) {
	    rowModel.insertEntries(start, length, getRowHeight());
	}
        int rh = getRowHeight() ;
        Rectangle drawRect = new Rectangle(0, start * rh,
                                        getColumnModel().getTotalColumnWidth(),
                                           (getRowCount()-start) * rh);

        revalidate();
        // PENDING(milne) revalidate calls repaint() if parent is a ScrollPane
	// repaint still required in the unusual case where there is no ScrollPane
        repaint(drawRect);
    }

    /*
     * Invoked when rows have been removed from the table.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param e the TableModelEvent encapsulating the deletion
     */
    private void tableRowsDeleted(TableModelEvent e) {
        int start = e.getFirstRow();
        int end = e.getLastRow();
        if (start < 0) {
            start = 0;
	}
	if (end < 0) {
	    end = getRowCount()-1;
	}

        int deletedCount = end - start + 1;
        int previousRowCount = getRowCount() + deletedCount;
        // Adjust the selection to account for the new rows
	selectionModel.removeIndexInterval(start, end);

	// If we have variable height rows, adjust the row model.
	if (rowModel != null) {
	    rowModel.removeEntries(start, deletedCount);
	}

        int rh = getRowHeight();
        Rectangle drawRect = new Rectangle(0, start * rh,
                                        getColumnModel().getTotalColumnWidth(),
                                        (previousRowCount - start) * rh);

        revalidate();
        // PENDING(milne) revalidate calls repaint() if parent is a ScrollPane
	// repaint still required in the unusual case where there is no ScrollPane
        repaint(drawRect);
    }

//
// Implementing TableColumnModelListener interface
//

    /**
     * Invoked when a column is added to the table column model.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @see TableColumnModelListener
     */
    public void columnAdded(TableColumnModelEvent e) {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
        resizeAndRepaint();
    }

    /**
     * Invoked when a column is removed from the table column model.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @see TableColumnModelListener
     */
    public void columnRemoved(TableColumnModelEvent e) {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
        resizeAndRepaint();
    }

    /**
     * Invoked when a column is repositioned. If a cell is being
     * edited, then editing is stopped and the cell is redrawn.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param e   the event received
     * @see TableColumnModelListener
     */
    public void columnMoved(TableColumnModelEvent e) {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
        repaint();
    }

    /**
     * Invoked when a column is moved due to a margin change.
     * If a cell is being edited, then editing is stopped and the cell
     * is redrawn.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param  e    the event received
     * @see TableColumnModelListener
     */
    public void columnMarginChanged(ChangeEvent e) {
	if (isEditing()) {
            removeEditor();
        }
	TableColumn resizingColumn = getResizingColumn();
	// Need to do this here, before the parent's
	// layout manager calls getPreferredSize().
	if (resizingColumn != null && autoResizeMode == AUTO_RESIZE_OFF) {
	    resizingColumn.setPreferredWidth(resizingColumn.getWidth());
	}
	resizeAndRepaint();
    }

    private int limit(int i, int a, int b) {
	return Math.min(b, Math.max(i, a));
    }

    /**
     * Invoked when the selection model of the <code>TableColumnModel</code>
     * is changed.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param  e  the event received
     * @see TableColumnModelListener
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
        boolean isAdjusting = e.getValueIsAdjusting();
        if (columnSelectionAdjusting && !isAdjusting) {
            // The assumption is that when the model is no longer adjusting
            // we will have already gotten all the changes, and therefore
            // don't need to do an additional paint.
            columnSelectionAdjusting = false;
            return;
        }
        columnSelectionAdjusting = isAdjusting;
	// The getCellRect() call will fail unless there is at least one row.
	if (getRowCount() <= 0 || getColumnCount() <= 0) {
	    return;
	}
        int firstIndex = limit(e.getFirstIndex(), 0, getColumnCount()-1);
        int lastIndex = limit(e.getLastIndex(), 0, getColumnCount()-1);
        int minRow = 0;
        int maxRow = getRowCount() - 1;
        if (getRowSelectionAllowed()) {
            minRow = selectionModel.getMinSelectionIndex();
            maxRow = selectionModel.getMaxSelectionIndex();
            if (minRow == -1 || maxRow == -1) {
                return;
            }
        }
        Rectangle firstColumnRect = getCellRect(minRow, firstIndex, false);
        Rectangle lastColumnRect = getCellRect(maxRow, lastIndex, false);
        Rectangle dirtyRegion = firstColumnRect.union(lastColumnRect);
        repaint(dirtyRegion);
    }

//
// Implementing ListSelectionListener interface
//

    /**
     * Invoked when the row selection changes -- repaints to show the new
     * selection.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param e   the event received
     * @see ListSelectionListener
     */
    public void valueChanged(ListSelectionEvent e) {
        boolean isAdjusting = e.getValueIsAdjusting();
        if (rowSelectionAdjusting && !isAdjusting) {
            // The assumption is that when the model is no longer adjusting
            // we will have already gotten all the changes, and therefore
            // don't need to do an additional paint.
            rowSelectionAdjusting = false;
            return;
        }
        rowSelectionAdjusting = isAdjusting;
	// The getCellRect() calls will fail unless there is at least one column.
	if (getRowCount() <= 0 || getColumnCount() <= 0) {
	    return;
	}
        int firstIndex = limit(e.getFirstIndex(), 0, getRowCount()-1);
        int lastIndex = limit(e.getLastIndex(), 0, getRowCount()-1);
        Rectangle firstRowRect = getCellRect(firstIndex, 0, false);
        Rectangle lastRowRect = getCellRect(lastIndex, getColumnCount()-1, false);
        Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
        repaint(dirtyRegion);
    }

//
// Implementing the CellEditorListener interface
//

    /**
     * Invoked when editing is finished. The changes are saved and the
     * editor is discarded.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param  e  the event received
     * @see CellEditorListener
     */
    public void editingStopped(ChangeEvent e) {
        // Take in the new value
        TableCellEditor editor = getCellEditor();
        if (editor != null) {
            Object value = editor.getCellEditorValue();
            setValueAt(value, editingRow, editingColumn);
            removeEditor();
        }
    }

    /**
     * Invoked when editing is canceled. The editor object is discarded
     * and the cell is rendered once again.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by JTable.
     *
     * @param  e  the event received
     * @see CellEditorListener
     */
    public void editingCanceled(ChangeEvent e) {
        removeEditor();
    }

//
// Implementing the Scrollable interface
//

    /**
     * Sets the preferred size of the viewport for this table.
     *
     * @param size  a <code>Dimension</code> object specifying the <code>preferredSize</code> of a
     *              <code>JViewport</code> whose view is this table
     * @see Scrollable#getPreferredScrollableViewportSize
     * @beaninfo
     * description: The preferred size of the viewport.
     */
    public void setPreferredScrollableViewportSize(Dimension size) {
        preferredViewportSize = size;
    }

    /**
     * Returns the preferred size of the viewport for this table.
     *
     * @return a <code>Dimension</code> object containing the <code>preferredSize</code> of the <code>JViewport</code>
     *         which displays this table
     * @see Scrollable#getPreferredScrollableViewportSize
     */
    public Dimension getPreferredScrollableViewportSize() {
        return preferredViewportSize;
    }

    /**
     * Returns the scroll increment (in pixels) that completely exposes one new
     * row or column (depending on the orientation).
     * <p>
     * This method is called each time the user requests a unit scroll.
     *
     * @param visibleRect the view area visible within the viewport
     * @param orientation either <code>SwingConstants.VERTICAL</code>
     *                	or <code>SwingConstants.HORIZONTAL</code>
     * @param direction less than zero to scroll up/left,
     *                  greater than zero for down/right
     * @return the "unit" increment for scrolling in the specified direction
     * @see Scrollable#getScrollableUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
                                          int direction) {
        // PENDING(alan): do something smarter
        if (orientation == SwingConstants.HORIZONTAL) {
            return 100;
        }
        return getRowHeight();
    }

    /**
     * Returns <code>visibleRect.height</code> or
     * <code>visibleRect.width</code>,
     * depending on this table's orientation.  Note that as of Swing 1.1.1
     * (Java 2 v 1.2.2) the value
     * returned will ensure that the viewport is cleanly aligned on
     * a row boundary.
     *
     * @return <code>visibleRect.height</code> or
     * 					<code>visibleRect.width</code>
     * 					per the orientation
     * @see Scrollable#getScrollableBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation,
                                           int direction) {
	if (orientation == SwingConstants.VERTICAL) {
	    int rh = getRowHeight();
	    return (rh > 0) ? Math.max(rh, (visibleRect.height / rh) * rh) : visibleRect.height;
	}
	else {
	    return visibleRect.width;
	}
    }

    /**
     * Returns false if <code>autoResizeMode</code> is set to
     * <code>AUTO_RESIZE_OFF</code>, which indicates that the
     * width of the viewport does not determine the width
     * of the table.  Otherwise returns true.
     *
     * @return false if <code>autoResizeMode</code> is set
     *   to <code>AUTO_RESIZE_OFF</code>, otherwise returns true
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportWidth() {
        return !(autoResizeMode == AUTO_RESIZE_OFF);
    }

    /**
     * Returns false to indicate that the height of the viewport does not
     * determine the height of the table.
     *
     * @return false
     * @see Scrollable#getScrollableTracksViewportHeight
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

//
// Protected Methods
//

    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
	boolean retValue = super.processKeyBinding(ks, e, condition, pressed);

	// Start editing when a key is typed. UI classes can disable this behavior
	// by setting the client property JTable.autoStartsEdit to Boolean.FALSE.
	if (!retValue && condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT &&
	    isFocusOwner() &&
	    !Boolean.FALSE.equals((Boolean)getClientProperty("JTable.autoStartsEdit"))) {
	    // We do not have a binding for the event.
	    Component editorComponent = getEditorComponent();
	    if (editorComponent == null) {
		// Only attempt to install the editor on a KEY_PRESSED,
		if (e == null || e.getID() != KeyEvent.KEY_PRESSED) {
		    return false;
		}
		// Don't start when just a modifier is pressed
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL ||
		    code == KeyEvent.VK_ALT) {
		    return false;
		}
		// Try to install the editor
		int anchorRow = getSelectionModel().getAnchorSelectionIndex();
		int anchorColumn = getColumnModel().getSelectionModel().
		                   getAnchorSelectionIndex();
		if (anchorRow != -1 && anchorColumn != -1 && !isEditing()) {
		    if (!editCellAt(anchorRow, anchorColumn)) {
			return false;
		    }
		}
		editorComponent = getEditorComponent();
		if (editorComponent == null) {
		    return false;
		}
	    }
	    // If the editorComponent is a JComponent, pass the event to it.
	    if (editorComponent instanceof JComponent) {
		retValue = ((JComponent)editorComponent).processKeyBinding
		                        (ks, e, WHEN_FOCUSED, pressed);
	        // If we have started an editor as a result of the user
	        // pressing a key and the surrendersFocusOnKeystroke property
	        // is true, give the focus to the new editor.
                if (getSurrendersFocusOnKeystroke()) {
                    editorComponent.requestFocus();
                }
	    }
	}
        return retValue;
    }

    private void setLazyValue(Hashtable h, Class c, String s) {
	h.put(c, new UIDefaults.ProxyLazyValue(s));
    }

    private void setLazyRenderer(Class c, String s) {
	setLazyValue(defaultRenderersByColumnClass, c, s);
    }

    /**
     * Creates default cell renderers for objects, numbers, doubles, dates,
     * booleans, and icons.
     * @see javax.swing.table.DefaultTableCellRenderer
     *
     */
    protected void createDefaultRenderers() {
        defaultRenderersByColumnClass = new UIDefaults();

        // Objects
        setLazyRenderer(Object.class, "javax.swing.table.DefaultTableCellRenderer");

	// Numbers
        setLazyRenderer(Number.class, "javax.swing.JTable$NumberRenderer");

	// Doubles and Floats
        setLazyRenderer(Float.class, "javax.swing.JTable$DoubleRenderer");
        setLazyRenderer(Double.class, "javax.swing.JTable$DoubleRenderer");

	// Dates
	setLazyRenderer(Date.class, "javax.swing.JTable$DateRenderer");

        // Icons and ImageIcons
        setLazyRenderer(Icon.class, "javax.swing.JTable$IconRenderer");
        setLazyRenderer(ImageIcon.class, "javax.swing.JTable$IconRenderer");

        // Booleans
        setLazyRenderer(Boolean.class, "javax.swing.JTable$BooleanRenderer");
    }

    /**
     * Default Renderers
     **/
    static class NumberRenderer extends DefaultTableCellRenderer {
	public NumberRenderer() {
	    super();
	    setHorizontalAlignment(JLabel.RIGHT);
	}
    }

    static class DoubleRenderer extends NumberRenderer {
	NumberFormat formatter;
	public DoubleRenderer() { super(); }

	public void setValue(Object value) {
	    if (formatter == null) {
		formatter = NumberFormat.getInstance();
	    }
	    setText((value == null) ? "" : formatter.format(value));
	}
    }

    static class DateRenderer extends DefaultTableCellRenderer {
	DateFormat formatter;
	public DateRenderer() { super(); }

	public void setValue(Object value) {
	    if (formatter==null) {
		formatter = DateFormat.getDateInstance();
	    }
	    setText((value == null) ? "" : formatter.format(value));
	}
    }

    static class IconRenderer extends DefaultTableCellRenderer {
	public IconRenderer() {
	    super();
	    setHorizontalAlignment(JLabel.CENTER);
	}
	public void setValue(Object value) { setIcon((value instanceof Icon) ? (Icon)value : null); }
    }


    static class BooleanRenderer extends JCheckBox implements TableCellRenderer
    {
	public BooleanRenderer() {
	    super();
	    setHorizontalAlignment(JLabel.CENTER);
	}

        public Component getTableCellRendererComponent(JTable table, Object value,
						       boolean isSelected, boolean hasFocus, int row, int column) {
	    if (isSelected) {
	        setForeground(table.getSelectionForeground());
	        super.setBackground(table.getSelectionBackground());
	    }
	    else {
	        setForeground(table.getForeground());
	        setBackground(table.getBackground());
	    }
            setSelected((value != null && ((Boolean)value).booleanValue()));
            return this;
        }
    }

    private void setLazyEditor(Class c, String s) {
	setLazyValue(defaultEditorsByColumnClass, c, s);
    }

    /**
     * Creates default cell editors for objects, numbers, and boolean values.
     * @see DefaultCellEditor
     */
    protected void createDefaultEditors() {
        defaultEditorsByColumnClass = new UIDefaults();

        // Objects
    	setLazyEditor(Object.class, "javax.swing.JTable$GenericEditor");

        // Numbers
        setLazyEditor(Number.class, "javax.swing.JTable$NumberEditor");

        // Booleans
        setLazyEditor(Boolean.class, "javax.swing.JTable$BooleanEditor");
    }

    /**
     * Default Editors
     */
    static class GenericEditor extends DefaultCellEditor {

	Class[] argTypes = new Class[]{String.class};
	java.lang.reflect.Constructor constructor;
	Object value;

	public GenericEditor() { super(new JTextField()); }

	public boolean stopCellEditing() {
	    String s = (String)super.getCellEditorValue();
	    // Here we are dealing with the case where a user
	    // has deleted the string value in a cell, possibly
	    // after a failed validation. Return null, so that
	    // they have the option to replace the value with
	    // null or use escape to restore the original.
	    // For Strings, return "" for backward compatibility.
	    if ("".equals(s)) {
		if (constructor.getDeclaringClass() == String.class) {
		    value = s;
		}
		super.stopCellEditing();
	    }

	    try {
		value = constructor.newInstance(new Object[]{s});
	    }
	    catch (Exception e) {
		((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
		return false;
	    }
	    return super.stopCellEditing();
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int column) {
	    this.value = null;
	    ((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
	    try {
		Class type = table.getColumnClass(column);
		// Since our obligation is to produce a value which is
		// assignable for the required type it is OK to use the
		// String constructor for columns which are declared
		// to contain Objects. A String is an Object.
		if (type == Object.class) {
		    type = String.class;
		}
		constructor = type.getConstructor(argTypes);
	    }
	    catch (Exception e) {
		return null;
	    }
	    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public Object getCellEditorValue() {
	    return value;
	}
    }

    static class NumberEditor extends GenericEditor {

	public NumberEditor() {
	    ((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
	}
    }

    static class BooleanEditor extends DefaultCellEditor {
	public BooleanEditor() {
	    super(new JCheckBox());
	    JCheckBox checkBox = (JCheckBox)getComponent();
	    checkBox.setHorizontalAlignment(JCheckBox.CENTER);
	}
    }

    /**
     * Initializes table properties to their default values.
     */
    protected void initializeLocalVars() {
	getSelectionModel().setAnchorSelectionIndex(0);

        setOpaque(true);
        createDefaultRenderers();
        createDefaultEditors();

        setTableHeader(createDefaultTableHeader());

        setShowGrid(true);
        setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        setRowHeight(16);
        setRowMargin(1);
        setRowSelectionAllowed(true);
        setCellEditor(null);
        setEditingColumn(-1);
	setEditingRow(-1);
        setSurrendersFocusOnKeystroke(false);
        setPreferredScrollableViewportSize(new Dimension(450, 400));

        // I'm registered to do tool tips so we can draw tips for the renderers
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this);

        setAutoscrolls(true);
    }

    /**
     * Returns the default table model object, which is
     * a <code>DefaultTableModel</code>.  A subclass can override this
     * method to return a different table model object.
     *
     * @return the default table model object
     * @see javax.swing.table.DefaultTableModel
     */
    protected TableModel createDefaultDataModel() {
        return new DefaultTableModel();
    }

    /**
     * Returns the default column model object, which is
     * a <code>DefaultTableColumnModel</code>.  A subclass can override this
     * method to return a different column model object.
     *
     * @return the default column model object
     * @see javax.swing.table.DefaultTableColumnModel
     */
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModel();
    }

    /**
     * Returns the default selection model object, which is
     * a <code>DefaultListSelectionModel</code>.  A subclass can override this
     * method to return a different selection model object.
     *
     * @return the default selection model object
     * @see javax.swing.DefaultListSelectionModel
     */
    protected ListSelectionModel createDefaultSelectionModel() {
        return new DefaultListSelectionModel();
    }

    /**
     * Returns the default table header object, which is
     * a <code>JTableHeader</code>.  A subclass can override this
     * method to return a different table header object.
     *
     * @return the default table header object
     * @see javax.swing.table.JTableHeader
     */
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel);
    }

    /**
     * Equivalent to <code>revalidate</code> followed by <code>repaint</code>.
     */
    protected void resizeAndRepaint() {
        revalidate();
        repaint();
    }

    /**
     * Returns the cell editor.
     *
     * @return the <code>TableCellEditor</code> that does the editing
     * @see #cellEditor
     */
    public TableCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * Sets the <code>cellEditor</code> variable.
     *
     * @param anEditor  the TableCellEditor that does the editing
     * @see #cellEditor
     * @beaninfo
     *  bound: true
     *  description: The table's active cell editor, if one exists.
     */
    public void setCellEditor(TableCellEditor anEditor) {
	TableCellEditor oldEditor = cellEditor;
        cellEditor = anEditor;
	firePropertyChange("tableCellEditor", oldEditor, anEditor);
    }

    /**
     * Sets the <code>editingColumn</code> variable.
     * @param aColumn  the column of the cell to be edited
     *
     * @see #editingColumn
     */
    public void setEditingColumn(int aColumn) {
        editingColumn = aColumn;
    }

    /**
     * Sets the <code>editingRow</code> variable.
     * @param aRow  the row of the cell to be edited
     *
     * @see #editingRow
     */
    public void setEditingRow(int aRow) {
        editingRow = aRow;
    }

    /**
     * Returns an appropriate renderer for the cell specified by this row and
     * column. If the <code>TableColumn</code> for this column has a non-null
     * renderer, returns that.  If not, finds the class of the data in
     * this column (using <code>getColumnClass</code>)
     * and returns the default renderer for this type of data.
     * <p>
     * <b>Note:</b>
     * Throughout the table package, the internal implementations always
     * use this method to provide renderers so that this default behavior
     * can be safely overridden by a subclass.
     *
     * @param row       the row of the cell to render, where 0 is the first row
     * @param column    the column of the cell to render,
     *			where 0 is the first column
     * @return the assigned renderer; if <code>null</code>
     *			returns the default renderer
     * 			for this type of object
     * @see javax.swing.table.DefaultTableCellRenderer
     * @see javax.swing.table.TableColumn#setCellRenderer
     * @see #setDefaultRenderer
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellRenderer renderer = tableColumn.getCellRenderer();
        if (renderer == null) {
            renderer = getDefaultRenderer(getColumnClass(column));
        }
        return renderer;
    }

    /**
     * Prepares the renderer by querying the data model for the
     * value and selection state
     * of the cell at <code>row</code>, <code>column</code>.
     * Returns the component (may be a <code>Component</code>
     * or a <code>JComponent</code>) under the event location.
     * <p>
     * <b>Note:</b>
     * Throughout the table package, the internal implementations always
     * use this method to prepare renderers so that this default behavior
     * can be safely overridden by a subclass.
     *
     * @param renderer  the <code>TableCellRenderer</code> to prepare
     * @param row       the row of the cell to render, where 0 is the first row
     * @param column    the column of the cell to render,
     *			where 0 is the first column
     * @return          the <code>Component</code> under the event location
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Object value = getValueAt(row, column);
	boolean isSelected = isCellSelected(row, column);
	boolean rowIsAnchor = (selectionModel.getAnchorSelectionIndex() == row);
	boolean colIsAnchor =
	    (columnModel.getSelectionModel().getAnchorSelectionIndex() == column);
	boolean hasFocus = (rowIsAnchor && colIsAnchor) && isFocusOwner();

	return renderer.getTableCellRendererComponent(this, value,
	                                              isSelected, hasFocus,
	                                              row, column);
    }

    /**
     * Returns an appropriate editor for the cell specified by
     * <code>row</code> and <code>column</code>. If the
     * <code>TableColumn</code> for this column has a non-null editor,
     * returns that.  If not, finds the class of the data in this
     * column (using <code>getColumnClass</code>)
     * and returns the default editor for this type of data.
     * <p>
     * <b>Note:</b>
     * Throughout the table package, the internal implementations always
     * use this method to provide editors so that this default behavior
     * can be safely overridden by a subclass.
     *
     * @param row       the row of the cell to edit, where 0 is the first row
     * @param column    the column of the cell to edit,
     *			where 0 is the first column
     * @return          the editor for this cell;
     *			if <code>null</code> return the default editor for
     *  		this type of cell
     * @see DefaultCellEditor
     */
    public TableCellEditor getCellEditor(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellEditor editor = tableColumn.getCellEditor();
        if (editor == null) {
            editor = getDefaultEditor(getColumnClass(column));
        }
        return editor;
    }


    /**
     * Prepares the editor by querying the data model for the value and
     * selection state of the cell at <code>row</code>, <code>column</code>.
     * <p>
     * <b>Note:</b>
     * Throughout the table package, the internal implementations always
     * use this method to prepare editors so that this default behavior
     * can be safely overridden by a subclass.
     *
     * @param editor  the <code>TableCellEditor</code> to set up
     * @param row     the row of the cell to edit,
     *		      where 0 is the first row
     * @param column  the column of the cell to edit,
     *		      where 0 is the first column
     * @return the <code>Component</code> being edited
     */
    public Component prepareEditor(TableCellEditor editor, int row, int column) {
        Object value = getValueAt(row, column);
        boolean isSelected = isCellSelected(row, column);
        Component comp = editor.getTableCellEditorComponent(this, value, isSelected,
                                                  row, column);
        if (comp instanceof JComponent) {
	    JComponent jComp = (JComponent)comp;
	    if (jComp.getNextFocusableComponent() == null) {
		jComp.setNextFocusableComponent(this);
	    }
	}
	return comp;
    }

    /**
     * Discards the editor object and frees the real estate it used for
     * cell rendering.
     */
    public void removeEditor() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
            removePropertyChangeListener("permanentFocusOwner", editorRemover);
	editorRemover = null;

        TableCellEditor editor = getCellEditor();
        if(editor != null) {
            editor.removeCellEditorListener(this);

            if (editorComp != null) {
		remove(editorComp);
	    }

            Rectangle cellRect = getCellRect(editingRow, editingColumn, false);

            setCellEditor(null);
            setEditingColumn(-1);
            setEditingRow(-1);
            editorComp = null;

            repaint(cellRect);
        }
    }

//
// Serialization
//

    /**
     * See readObject() and writeObject() in JComponent for more
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }

    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException
    {
        s.defaultReadObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
        createDefaultRenderers();
        createDefaultEditors();

        // If ToolTipText != null, then the tooltip has already been
        // registered by JComponent.readObject() and we don't want
        // to re-register here
        if (getToolTipText() == null) {
            ToolTipManager.sharedInstance().registerComponent(this);
         }
    }

    /* Called from the JComponent's EnableSerializationFocusListener to
     * do any Swing-specific pre-serialization configuration.
     */
    void compWriteObjectNotify() {
        super.compWriteObjectNotify();
        // If ToolTipText != null, then the tooltip has already been
        // unregistered by JComponent.compWriteObjectNotify()
        if (getToolTipText() == null) {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }

    /**
     * Returns a string representation of this table. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  a string representation of this table
     */
    protected String paramString() {
	String gridColorString = (gridColor != null ?
				  gridColor.toString() : "");
	String showHorizontalLinesString = (showHorizontalLines ?
					    "true" : "false");
	String showVerticalLinesString = (showVerticalLines ?
					  "true" : "false");
	String autoResizeModeString;
        if (autoResizeMode == AUTO_RESIZE_OFF) {
	    autoResizeModeString = "AUTO_RESIZE_OFF";
	} else if (autoResizeMode == AUTO_RESIZE_NEXT_COLUMN) {
	    autoResizeModeString = "AUTO_RESIZE_NEXT_COLUMN";
	} else if (autoResizeMode == AUTO_RESIZE_SUBSEQUENT_COLUMNS) {
	    autoResizeModeString = "AUTO_RESIZE_SUBSEQUENT_COLUMNS";
	} else if (autoResizeMode == AUTO_RESIZE_LAST_COLUMN) {
	    autoResizeModeString = "AUTO_RESIZE_LAST_COLUMN";
	} else if (autoResizeMode == AUTO_RESIZE_ALL_COLUMNS)  {
	    autoResizeModeString = "AUTO_RESIZE_ALL_COLUMNS";
	} else autoResizeModeString = "";
	String autoCreateColumnsFromModelString = (autoCreateColumnsFromModel ?
						   "true" : "false");
	String preferredViewportSizeString = (preferredViewportSize != null ?
					      preferredViewportSize.toString()
					      : "");
	String rowSelectionAllowedString = (rowSelectionAllowed ?
					    "true" : "false");
	String cellSelectionEnabledString = (cellSelectionEnabled ?
					    "true" : "false");
	String selectionForegroundString = (selectionForeground != null ?
					    selectionForeground.toString() :
					    "");
	String selectionBackgroundString = (selectionBackground != null ?
					    selectionBackground.toString() :
					    "");

	return super.paramString() +
	",autoCreateColumnsFromModel=" + autoCreateColumnsFromModelString +
	",autoResizeMode=" + autoResizeModeString +
	",cellSelectionEnabled=" + cellSelectionEnabledString +
	",editingColumn=" + editingColumn +
	",editingRow=" + editingRow +
	",gridColor=" + gridColorString +
	",preferredViewportSize=" + preferredViewportSizeString +
	",rowHeight=" + rowHeight +
	",rowMargin=" + rowMargin +
	",rowSelectionAllowed=" + rowSelectionAllowedString +
	",selectionBackground=" + selectionBackgroundString +
	",selectionForeground=" + selectionForegroundString +
	",showHorizontalLines=" + showHorizontalLinesString +
	",showVerticalLines=" + showVerticalLinesString;
    }

    // This class tracks changes in the keyboard focus state. It is used
    // when the JTable is editing to determine when to cancel the edit.
    // If focus switches to a component outside of the jtable, but in the
    // same window, this will cancel editing.
    class CellEditorRemover implements PropertyChangeListener {
        KeyboardFocusManager focusManager;

        public CellEditorRemover(KeyboardFocusManager fm) {
            this.focusManager = fm;
        }

        public void propertyChange(PropertyChangeEvent ev) {
            if (!isEditing() || getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE) {
                return;
            }

            Component c = focusManager.getPermanentFocusOwner();
            while (c != null) {
                if (c == JTable.this) {
                    // focus remains inside the table
                    return;
                } else if ((c instanceof Window) ||
                           (c instanceof Applet && c.getParent() == null)) {
                    if (c == SwingUtilities.getRoot(JTable.this)) {
                        if (!getCellEditor().stopCellEditing()) {
                            getCellEditor().cancelCellEditing();
                        }
                    }
                    break;
                }
                c = c.getParent();
            }
        }
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JTable.
     * For tables, the AccessibleContext takes the form of an
     * AccessibleJTable.
     * A new AccessibleJTable instance is created if necessary.
     *
     * @return an AccessibleJTable that serves as the
     *         AccessibleContext of this JTable
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJTable();
        }
        return accessibleContext;
    }

    //
    // *** should also implement AccessibleSelection?
    // *** and what's up with keyboard navigation/manipulation?
    //
    /**
     * This class implements accessibility support for the
     * <code>JTable</code> class.  It provides an implementation of the
     * Java Accessibility API appropriate to table user-interface elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    protected class AccessibleJTable extends AccessibleJComponent
    implements AccessibleSelection, ListSelectionListener, TableModelListener,
    TableColumnModelListener, CellEditorListener, PropertyChangeListener,
    AccessibleExtendedTable {

        int lastSelectedRow;
        int lastSelectedCol;

        AccessibleJTable() {
            super();
	    JTable.this.addPropertyChangeListener(this);
            JTable.this.getSelectionModel().addListSelectionListener(this);
            TableColumnModel tcm = JTable.this.getColumnModel();
            tcm.addColumnModelListener(this);
	    tcm.getSelectionModel().addListSelectionListener(this);
            JTable.this.getModel().addTableModelListener(this);
            lastSelectedRow = JTable.this.getSelectedRow();
            lastSelectedCol = JTable.this.getSelectedColumn();
        }

    // Listeners to track model, etc. changes to as to re-place the other
    // listeners

        /**
	 * Track changes to selection model, column model, etc. so as to
	 * be able to re-place listeners on those in order to pass on
	 * information to the Accessibility PropertyChange mechanism
	 */
        public void propertyChange(PropertyChangeEvent e) {
	    String name = e.getPropertyName();
	    Object oldValue = e.getOldValue();
	    Object newValue = e.getNewValue();

	        // re-set tableModel listeners
	    if (name.compareTo("model") == 0) {

		if (oldValue != null && oldValue instanceof TableModel) {
		    ((TableModel) oldValue).removeTableModelListener(this);
		}
		if (newValue != null && newValue instanceof TableModel) {
		    ((TableModel) newValue).addTableModelListener(this);
		}

	        // re-set selectionModel listeners
	    } else if (name.compareTo("selectionModel") == 0) {

		Object source = e.getSource();
		if (source == JTable.this) {	// row selection model

		    if (oldValue != null &&
			oldValue instanceof ListSelectionModel) {
			((ListSelectionModel) oldValue).removeListSelectionListener(this);
		    }
		    if (newValue != null &&
			newValue instanceof ListSelectionModel) {
			((ListSelectionModel) newValue).addListSelectionListener(this);
		    }

		} else if (source == JTable.this.getColumnModel()) {

		    if (oldValue != null &&
			oldValue instanceof ListSelectionModel) {
			((ListSelectionModel) oldValue).removeListSelectionListener(this);
		    }
		    if (newValue != null &&
			newValue instanceof ListSelectionModel) {
			((ListSelectionModel) newValue).addListSelectionListener(this);
		    }

		} else {
		  //	    System.out.println("!!! Bug in source of selectionModel propertyChangeEvent");
		}

	        // re-set columnModel listeners
		// and column's selection property listener as well
	    } else if (name.compareTo("columnModel") == 0) {

		if (oldValue != null && oldValue instanceof TableColumnModel) {
		    TableColumnModel tcm = (TableColumnModel) oldValue;
		    tcm.removeColumnModelListener(this);
		    tcm.getSelectionModel().removeListSelectionListener(this);
		}
		if (newValue != null && newValue instanceof TableColumnModel) {
		    TableColumnModel tcm = (TableColumnModel) newValue;
		    tcm.addColumnModelListener(this);
		    tcm.getSelectionModel().addListSelectionListener(this);
		}

	        // re-se cellEditor listeners
	    } else if (name.compareTo("tableCellEditor") == 0) {

		if (oldValue != null && oldValue instanceof TableCellEditor) {
		    ((TableCellEditor) oldValue).removeCellEditorListener((CellEditorListener) this);
		}
		if (newValue != null && newValue instanceof TableCellEditor) {
		    ((TableCellEditor) newValue).addCellEditorListener((CellEditorListener) this);
		}
	    }
	}


    // Listeners to echo changes to the AccessiblePropertyChange mechanism

        /*
	 * Describes a change in the accessible table model.
	 */
	protected class AccessibleJTableModelChange
            implements AccessibleTableModelChange {

	    protected int type;
	    protected int firstRow;
	    protected int lastRow;
	    protected int firstColumn;
	    protected int lastColumn;

	    protected AccessibleJTableModelChange(int type, int firstRow,
						  int lastRow, int firstColumn,
						  int lastColumn) {
		this.type = type;
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstColumn = firstColumn;
		this.lastColumn = lastColumn;
	    }

	    public int getType() {
		return type;
	    }

	    public int getFirstRow() {
		return firstRow;
	    }

	    public int getLastRow() {
		return lastRow;
	    }

	    public int getFirstColumn() {
		return firstColumn;
	    }

	    public int getLastColumn() {
		return lastColumn;
	    }
	}

        /**
         * Track changes to the table contents
         */
        public void tableChanged(TableModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
           if (e != null) {
	       int firstColumn = e.getColumn();
	       int lastColumn = e.getColumn();
	       if (firstColumn == TableModelEvent.ALL_COLUMNS) {
	           firstColumn = 0;
	           lastColumn = getColumnCount() - 1;
	       }

	       // Fire a property change event indicating the table model
	       // has changed.
	       AccessibleJTableModelChange change =
	           new AccessibleJTableModelChange(e.getType(),
					           e.getFirstRow(),
					           e.getLastRow(),
					           firstColumn,
					           lastColumn);
	       firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			          null, change);
            }
        }

        /**
         * Track changes to the table contents (row insertions)
         */
        public void tableRowsInserted(TableModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);

	   // Fire a property change event indicating the table model
	   // has changed.
	   int firstColumn = e.getColumn();
	   int lastColumn = e.getColumn();
	   if (firstColumn == TableModelEvent.ALL_COLUMNS) {
	       firstColumn = 0;
	       lastColumn = getColumnCount() - 1;
	   }
	   AccessibleJTableModelChange change =
	       new AccessibleJTableModelChange(e.getType(),
					       e.getFirstRow(),
					       e.getLastRow(),
					       firstColumn,
					       lastColumn);
	   firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			      null, change);
        }

        /**
         * Track changes to the table contents (row deletions)
         */
        public void tableRowsDeleted(TableModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);

	   // Fire a property change event indicating the table model
	   // has changed.
	   int firstColumn = e.getColumn();
	   int lastColumn = e.getColumn();
	   if (firstColumn == TableModelEvent.ALL_COLUMNS) {
	       firstColumn = 0;
	       lastColumn = getColumnCount() - 1;
	   }
	   AccessibleJTableModelChange change =
	       new AccessibleJTableModelChange(e.getType(),
					       e.getFirstRow(),
					       e.getLastRow(),
					       firstColumn,
					       lastColumn);
	   firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			      null, change);
        }

        /**
         * Track changes to the table contents (column insertions)
         */
        public void columnAdded(TableColumnModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);

	   // Fire a property change event indicating the table model
	   // has changed.
	   int type = AccessibleTableModelChange.INSERT;
	   AccessibleJTableModelChange change =
	       new AccessibleJTableModelChange(type,
					       0,
					       0,
					       e.getFromIndex(),
					       e.getToIndex());
	   firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			      null, change);
        }

        /**
         * Track changes to the table contents (column deletions)
         */
        public void columnRemoved(TableColumnModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
	   // Fire a property change event indicating the table model
	   // has changed.
	   int type = AccessibleTableModelChange.DELETE;
	   AccessibleJTableModelChange change =
	       new AccessibleJTableModelChange(type,
					       0,
					       0,
					       e.getFromIndex(),
					       e.getToIndex());
	   firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			      null, change);
        }

        /**
         * Track changes of a column repositioning.
         *
         * @see TableColumnModelListener
         */
        public void columnMoved(TableColumnModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);

	   // Fire property change events indicating the table model
	   // has changed.
	   int type = AccessibleTableModelChange.DELETE;
	   AccessibleJTableModelChange change =
	       new AccessibleJTableModelChange(type,
					       0,
					       0,
					       e.getFromIndex(),
					       e.getFromIndex());
	   firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			      null, change);

	   int type2 = AccessibleTableModelChange.INSERT;
	   AccessibleJTableModelChange change2 =
	       new AccessibleJTableModelChange(type2,
					       0,
					       0,
					       e.getToIndex(),
					       e.getToIndex());
	   firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED,
			      null, change2);
        }

        /**
         * Track changes of a column moving due to margin changes.
         *
         * @see TableColumnModelListener
         */
        public void columnMarginChanged(ChangeEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Track that the selection model of the TableColumnModel changed.
         *
         * @see TableColumnModelListener
         */
        public void columnSelectionChanged(ListSelectionEvent e) {
            // we should now re-place our TableColumn listener
        }

        /**
         * Track changes to a cell's contents.
         *
         * Invoked when editing is finished. The changes are saved, the
         * editor object is discarded, and the cell is rendered once again.
         *
         * @see CellEditorListener
         */
        public void editingStopped(ChangeEvent e) {
           // it'd be great if we could figure out which cell, and pass that
           // somehow as a parameter
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Invoked when editing is canceled. The editor object is discarded
         * and the cell is rendered once again.
         *
         * @see CellEditorListener
         */
        public void editingCanceled(ChangeEvent e) {
            // nothing to report, 'cause nothing changed
        }

        /**
         * Track changes to table cell selections
         */
        public void valueChanged(ListSelectionEvent e) {
            firePropertyChange(AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
                               Boolean.valueOf(false), Boolean.valueOf(true));

            int selectedRow = JTable.this.getSelectedRow();
            int selectedCol = JTable.this.getSelectedColumn();
            if (selectedRow != lastSelectedRow ||
                selectedCol != lastSelectedCol) {
                Accessible oldA = getAccessibleAt(lastSelectedRow,
                                                  lastSelectedCol);
                Accessible newA = getAccessibleAt(selectedRow, selectedCol);
                firePropertyChange(AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY,
                                   oldA, newA);
                 lastSelectedRow = selectedRow;
                 lastSelectedCol = selectedCol;
             }
        }




    // AccessibleContext support

        /**
         * Get the AccessibleSelection associated with this object.  In the
         * implementation of the Java Accessibility API for this class,
	 * return this object, which is responsible for implementing the
         * AccessibleSelection interface on behalf of itself.
	 *
	 * @return this object
         */
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }

        /**
         * Gets the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TABLE;
        }

        /**
         * Returns the <code>Accessible</code> child, if one exists,
         * contained at the local coordinate <code>Point</code>.
         *
         * @param p the point defining the top-left corner of the
         *    <code>Accessible</code>, given in the coordinate space
         *    of the object's parent
         * @return the <code>Accessible</code>, if it exists,
         *    at the specified location; else <code>null</code>
         */
        public Accessible getAccessibleAt(Point p) {
            int column = columnAtPoint(p);
            int row = rowAtPoint(p);

            if ((column != -1) && (row != -1)) {
                TableColumn aColumn = getColumnModel().getColumn(column);
                TableCellRenderer renderer = aColumn.getCellRenderer();
                if (renderer == null) {
                    Class columnClass = getColumnClass(column);
                    renderer = getDefaultRenderer(columnClass);
                }
                Component component = renderer.getTableCellRendererComponent(
                                  JTable.this, null, false, false,
                                  row, column);
                return new AccessibleJTableCell(JTable.this, row, column,
                      getAccessibleIndexAt(row, column));
            }
            return null;
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement <code>Accessible</code>,
         * then this method should return the number of children of this object.
         *
         * @return the number of accessible children in the object
         */
        public int getAccessibleChildrenCount() {
            return (JTable.this.getColumnCount() * JTable.this.getRowCount());
        }

        /**
         * Returns the nth <code>Accessible</code> child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            if (i < 0 || i >= getAccessibleChildrenCount()) {
                return null;
            } else {
                // children increase across, and then down, for tables
                // (arbitrary decision)
                int column = getAccessibleColumnAtIndex(i);
                int row = getAccessibleRowAtIndex(i);

                TableColumn aColumn = getColumnModel().getColumn(column);
                TableCellRenderer renderer = aColumn.getCellRenderer();
                if (renderer == null) {
                    Class columnClass = getColumnClass(column);
                    renderer = getDefaultRenderer(columnClass);
                }
                Component component = renderer.getTableCellRendererComponent(
                                  JTable.this, null, false, false,
                                  row, column);
                return new AccessibleJTableCell(JTable.this, row, column,
                      getAccessibleIndexAt(row, column));
            }
        }

    // AccessibleSelection support

        /**
         * Returns the number of <code>Accessible</code> children
         * currently selected.
         * If no children are selected, the return value will be 0.
         *
         * @return the number of items currently selected
         */
        public int getAccessibleSelectionCount() {
            int rowsSel = JTable.this.getSelectedRowCount();
            int colsSel = JTable.this.getSelectedColumnCount();

            if (JTable.this.cellSelectionEnabled) { // a contiguous block
                return rowsSel * colsSel;

            } else {
                // a column swath and a row swath, with a shared block
                if (JTable.this.getRowSelectionAllowed() &&
                    JTable.this.getColumnSelectionAllowed()) {
                    return rowsSel * JTable.this.getColumnCount() +
                           colsSel * JTable.this.getRowCount() -
                           rowsSel * colsSel;

                // just one or more rows in selection
                } else if (JTable.this.getRowSelectionAllowed()) {
                    return rowsSel * JTable.this.getColumnCount();

                // just one or more rows in selection
                } else if (JTable.this.getColumnSelectionAllowed()) {
                    return colsSel * JTable.this.getRowCount();

                } else {
                    return 0;    // JTable doesn't allow selections
                }
            }
        }

        /**
         * Returns an <code>Accessible</code> representing the
         * specified selected child in the object.  If there
         * isn't a selection, or there are fewer children selected
         * than the integer passed in, the return
         * value will be <code>null</code>.
         * <p>Note that the index represents the i-th selected child, which
         * is different from the i-th child.
         *
         * @param i the zero-based index of selected children
         * @return the i-th selected child
         * @see #getAccessibleSelectionCount
         */
        public Accessible getAccessibleSelection(int i) {
            if (i < 0 || i > getAccessibleSelectionCount()) {
                return (Accessible) null;
            }

            int rowsSel = JTable.this.getSelectedRowCount();
            int colsSel = JTable.this.getSelectedColumnCount();
            int rowIndicies[] = getSelectedRows();
            int colIndicies[] = getSelectedColumns();
            int ttlCols = JTable.this.getColumnCount();
            int ttlRows = JTable.this.getRowCount();
            int r;
            int c;

            if (JTable.this.cellSelectionEnabled) { // a contiguous block
                r = rowIndicies[i / colsSel];
                c = colIndicies[i % colsSel];
                return getAccessibleChild((r * ttlCols) + c);
            } else {

                // a column swath and a row swath, with a shared block
                if (JTable.this.getRowSelectionAllowed() &&
                    JTable.this.getColumnSelectionAllowed()) {

                    // Situation:
                    //   We have a table, like the 6x3 table below,
                    //   wherein three colums and one row selected
                    //   (selected cells marked with "*", unselected "0"):
                    //
                    //            0 * 0 * * 0
                    //            * * * * * *
                    //            0 * 0 * * 0
                    //

                    // State machine below walks through the array of
                    // selected rows in two states: in a selected row,
                    // and not in one; continuing until we are in a row
                    // in which the ith selection exists.  Then we return
                    // the appropriate cell.  In the state machine, we
                    // always do rows above the "current" selected row first,
                    // then the cells in the selected row.  If we're done
                    // with the state machine before finding the requested
                    // selected child, we handle the rows below the last
                    // selected row at the end.
                    //
                    int curIndex = i;
                    final int IN_ROW = 0;
                    final int NOT_IN_ROW = 1;
                    int state = (rowIndicies[0] == 0 ? IN_ROW : NOT_IN_ROW);
                    int j = 0;
                    int prevRow = -1;
                    while (j < rowIndicies.length) {
                        switch (state) {

                        case IN_ROW:   // on individual row full of selections
                            if (curIndex < ttlCols) { // it's here!
                                c = curIndex % ttlCols;
                                r = rowIndicies[j];
                                return getAccessibleChild((r * ttlCols) + c);
                            } else {                               // not here
                                curIndex -= ttlCols;
                            }
                            // is the next row in table selected or not?
                            if (j + 1 == rowIndicies.length ||
                                rowIndicies[j] != rowIndicies[j+1] - 1) {
                                state = NOT_IN_ROW;
                                prevRow = rowIndicies[j];
                            }
                            j++;  // we didn't return earlier, so go to next row
                            break;

                        case NOT_IN_ROW:  // sparse bunch of rows of selections
                            if (curIndex <
                                (colsSel * (rowIndicies[j] -
                                (prevRow == -1 ? 0 : (prevRow + 1))))) {

                                // it's here!
                                c = colIndicies[curIndex % colsSel];
                                r = (j > 0 ? rowIndicies[j-1] + 1 : 0)
                                    + curIndex / colsSel;
                                return getAccessibleChild((r * ttlCols) + c);
                            } else {                               // not here
                                curIndex -= colsSel * (rowIndicies[j] -
                                (prevRow == -1 ? 0 : (prevRow + 1)));
                            }
                            state = IN_ROW;
                            break;
                        }
                    }
                    // we got here, so we didn't find it yet; find it in
                    // the last sparse bunch of rows
                    if (curIndex <
                        (colsSel * (ttlRows -
                        (prevRow == -1 ? 0 : (prevRow + 1))))) { // it's here!
                        c = colIndicies[curIndex % colsSel];
                        r = rowIndicies[j-1] + curIndex / colsSel + 1;
                        return getAccessibleChild((r * ttlCols) + c);
                    } else {                               // not here
                        // we shouldn't get to this spot in the code!
//                      System.out.println("Bug in AccessibleJTable.getAccessibleSelection()");
                    }

                // one or more rows selected
                } else if (JTable.this.getRowSelectionAllowed()) {
                    c = i % ttlCols;
                    r = rowIndicies[i / ttlCols];
                    return getAccessibleChild((r * ttlCols) + c);

                // one or more columns selected
                } else if (JTable.this.getColumnSelectionAllowed()) {
                    c = colIndicies[i % colsSel];
                    r = i / colsSel;
                    return getAccessibleChild((r * ttlCols) + c);
                }
            }
            return (Accessible) null;
        }

        /**
         * Determines if the current child of this object is selected.
         *
         * @param i the zero-based index of the child in this
         *    <code>Accessible</code> object
         * @return true if the current child of this object is selected
         * @see AccessibleContext#getAccessibleChild
         */
        public boolean isAccessibleChildSelected(int i) {
            int column = getAccessibleColumnAtIndex(i);
            int row = getAccessibleRowAtIndex(i);
            return JTable.this.isCellSelected(row, column);
        }

        /**
         * Adds the specified <code>Accessible</code> child of the
         * object to the object's selection.  If the object supports
         * multiple selections, the specified child is added to
         * any existing selection, otherwise
         * it replaces any existing selection in the object.  If the
         * specified child is already selected, this method has no effect.
         * <p>
         * This method only works on <code>JTable</code>s which have
         * individual cell selection enabled.
         *
         * @param i the zero-based index of the child
         * @see AccessibleContext#getAccessibleChild
         */
        public void addAccessibleSelection(int i) {
            if (JTable.this.cellSelectionEnabled) {
                int column = getAccessibleColumnAtIndex(i);
                int row = getAccessibleRowAtIndex(i);
                JTable.this.addRowSelectionInterval(row, row);
                JTable.this.addColumnSelectionInterval(column, column);
            }
        }

        /**
         * Removes the specified child of the object from the object's
         * selection.  If the specified item isn't currently selected, this
         * method has no effect.
         * <p>
         * This method only works on <code>JTables</code> which have
         * individual cell selection enabled.
         *
         * @param i the zero-based index of the child
         * @see AccessibleContext#getAccessibleChild
         */
        public void removeAccessibleSelection(int i) {
            if (JTable.this.cellSelectionEnabled) {
                int column = getAccessibleColumnAtIndex(i);
                int row = getAccessibleRowAtIndex(i);
                JTable.this.removeRowSelectionInterval(row, row);
                JTable.this.removeColumnSelectionInterval(column, column);
            }
        }

        /**
         * Clears the selection in the object, so that no children in the
         * object are selected.
         */
        public void clearAccessibleSelection() {
            JTable.this.clearSelection();
        }

        /**
         * Causes every child of the object to be selected, but only
         * if the <code>JTable</code> supports multiple selections,
         * and if individual cell selection is enabled.
         */
        public void selectAllAccessibleSelection() {
            if (JTable.this.cellSelectionEnabled) {
                JTable.this.selectAll();
            }
        }

        // begin AccessibleExtendedTable implementation -------------

        /**
         * Returns the row number of an index in the table.
         *
         * @param index the zero-based index in the table
         * @return the zero-based row of the table if one exists;
         * otherwise -1.
         */
        public int getAccessibleRow(int index) {
	    return getAccessibleRowAtIndex(index);
        }

        /**
         * Returns the column number of an index in the table.
         *
         * @param index the zero-based index in the table
         * @return the zero-based column of the table if one exists;
         * otherwise -1.
         */
        public int getAccessibleColumn(int index) {
	    return getAccessibleColumnAtIndex(index);
        }

        /**
         * Returns the index at a row and column in the table.
         *
         * @param r zero-based row of the table
         * @param c zero-based column of the table
         * @return the zero-based index in the table if one exists;
         * otherwise -1.
         */
        public int getAccessibleIndex(int r, int c) {
	    return getAccessibleIndexAt(r, c);
        }

        // end of AccessibleExtendedTable implementation ------------

	// start of AccessibleTable implementation ------------------

	private Accessible caption;
	private Accessible summary;
	private Accessible [] rowDescription;
	private Accessible [] columnDescription;

        /**
         * Gets the <code>AccessibleTable</code> associated with this
         * object.  In the implementation of the Java Accessibility
         * API for this class, return this object, which is responsible
         * for implementing the <code>AccessibleTables</code> interface
         * on behalf of itself.
	 *
	 * @return this object
         */
        public AccessibleTable getAccessibleTable() {
            return this;
        }

	/**
	 * Returns the caption for the table.
	 *
	 * @return the caption for the table
	 */
	public Accessible getAccessibleCaption() {
	    return this.caption;
	}

	/**
	 * Sets the caption for the table.
	 *
	 * @param a the caption for the table
	 */
	public void setAccessibleCaption(Accessible a) {
	    Accessible oldCaption = caption;
	    this.caption = a;
	    firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_CAPTION_CHANGED,
			       oldCaption, this.caption);
	}

	/**
	 * Returns the summary description of the table.
	 *
	 * @return the summary description of the table
	 */
	public Accessible getAccessibleSummary() {
	    return this.summary;
	}

	/**
	 * Sets the summary description of the table.
	 *
	 * @param a the summary description of the table
	 */
	public void setAccessibleSummary(Accessible a) {
	    Accessible oldSummary = summary;
	    this.summary = a;
	    firePropertyChange(AccessibleContext.ACCESSIBLE_TABLE_SUMMARY_CHANGED,
			       oldSummary, this.summary);
	}

        /*
         * Returns the total number of rows in this table.
         *
         * @return the total number of rows in this table
         */
        public int getAccessibleRowCount() {
            return JTable.this.getRowCount();
        }

        /*
         * Returns the total number of columns in the table.
         *
         * @return the total number of columns in the table
         */
        public int getAccessibleColumnCount() {
            return JTable.this.getColumnCount();
        }

        /*
         * Returns the <code>Accessible</code> at a specified row
	 * and column in the table.
         *
         * @param r zero-based row of the table
         * @param c zero-based column of the table
         * @return the <code>Accessible</code> at the specified row and column
	 * in the table
         */
        public Accessible getAccessibleAt(int r, int c) {
            return getAccessibleChild((r * getAccessibleColumnCount()) + c);
        }

	/**
	 * Returns the number of rows occupied by the <code>Accessible</code>
	 * at a specified row and column in the table.
	 *
	 * @return the number of rows occupied by the <code>Accessible</code>
	 *     at a specified row and column in the table
	 */
	public int getAccessibleRowExtentAt(int r, int c) {
	    return 1;
	}

	/**
	 * Returns the number of columns occupied by the
         * <code>Accessible</code> at a given (row, column).
	 *
	 * @return the number of columns occupied by the <code>Accessible</code>
	 *     at a specified row and column in the table
	 */
	public int getAccessibleColumnExtentAt(int r, int c) {
	    return 1;
	}

	/**
	 * Returns the row headers as an <code>AccessibleTable</code>.
	 *
	 * @return an <code>AccessibleTable</code> representing the row
	 * headers
	 */
        public AccessibleTable getAccessibleRowHeader() {
	    // row headers are not supported
	    return null;
        }

	/**
	 * Returns the row headers as an <code>AccessibleTable</code>.
	 *
	 * @return an <code>AccessibleTable</code> representing
	 *     the row headers
	 */
	public void setAccessibleRowHeader(AccessibleTable a) {
	    // row headers are not supported
	}

	/**
	 * Returns the column headers as an <code>AccessibleTable</code>.
	 *
	 * @return an <code>AccessibleTable</code> representing the column
	 *      headers
	 */
        public AccessibleTable getAccessibleColumnHeader() {
	    return new AccessibleTableHeader(JTable.this.getTableHeader());
        }
	
        /*
         * Private class representing a table column header
         */
	private class AccessibleTableHeader implements AccessibleTable {
	    private JTableHeader header;
	    private TableColumnModel headerModel;

	    AccessibleTableHeader(JTableHeader header) {
		this.header = header;
		this.headerModel = header.getColumnModel();
	    }

	    /**
	     * Returns the caption for the table.
	     *
	     * @return the caption for the table
	     */
	    public Accessible getAccessibleCaption() { return null; }
	    
	    
	    /**
	     * Sets the caption for the table.
	     *
	     * @param a the caption for the table
	     */
	    public void setAccessibleCaption(Accessible a) {}
	    
	    /**
	     * Returns the summary description of the table.
	     * 
	     * @return the summary description of the table
	     */
	    public Accessible getAccessibleSummary() { return null; }
	    
	    /**
	     * Sets the summary description of the table
	     *
	     * @param a the summary description of the table
	     */
	    public void setAccessibleSummary(Accessible a) {}
	    
	    /**
	     * Returns the number of rows in the table.
	     *
	     * @return the number of rows in the table
	     */
	    public int getAccessibleRowCount() { return 1; }
	    
	    /**
	     * Returns the number of columns in the table.
	     *
	     * @return the number of columns in the table
	     */
	    public int getAccessibleColumnCount() {
		return headerModel.getColumnCount();
	    }
	    
	    /**
	     * Returns the Accessible at a specified row and column
	     * in the table.
	     *
	     * @param row zero-based row of the table
	     * @param column zero-based column of the table
	     * @return the Accessible at the specified row and column
	     */
	    public Accessible getAccessibleAt(int row, int column) {

                TableColumn aColumn = headerModel.getColumn(column);
                TableCellRenderer renderer = aColumn.getHeaderRenderer();
                if (renderer == null) {
                    renderer = header.getDefaultRenderer();
                }
                Component component = renderer.getTableCellRendererComponent(
                                  header.getTable(),
                                  aColumn.getHeaderValue(), false, false,
                                  -1, column);
                if (component instanceof Accessible) {
                    return ((Accessible) component);
                } else {
                    return null;
                }
            }

	    /**
	     * Returns the number of rows occupied by the Accessible at
	     * a specified row and column in the table.
	     *
	     * @return the number of rows occupied by the Accessible at a
	     * given specified (row, column)
	     */
	    public int getAccessibleRowExtentAt(int r, int c) { return 1; }
	    
	    /**
	     * Returns the number of columns occupied by the Accessible at
	     * a specified row and column in the table.
	     *
	     * @return the number of columns occupied by the Accessible at a
	     * given specified row and column
	     */
	    public int getAccessibleColumnExtentAt(int r, int c) { return 1; }
	    
	    /**
	     * Returns the row headers as an AccessibleTable.
	     *
	     * @return an AccessibleTable representing the row
	     * headers
	     */
	    public AccessibleTable getAccessibleRowHeader() { return null; }
	    
	    /**
	     * Sets the row headers.
	     *
	     * @param table an AccessibleTable representing the
	     * row headers
	     */
	    public void setAccessibleRowHeader(AccessibleTable table) {}
	    
	    /**
	     * Returns the column headers as an AccessibleTable.
	     *
	     * @return an AccessibleTable representing the column
	     * headers
	     */
	    public AccessibleTable getAccessibleColumnHeader() { return null; }
	    
	    /**
	     * Sets the column headers.
	     *
	     * @param table an AccessibleTable representing the
	     * column headers
	     */
	    public void setAccessibleColumnHeader(AccessibleTable table) {}
	    
	    /**
	     * Returns the description of the specified row in the table.
	     *
	     * @param r zero-based row of the table
	     * @return the description of the row
	     */
	    public Accessible getAccessibleRowDescription(int r) { return null; }
	    
	    /**
	     * Sets the description text of the specified row of the table.
	     *
	     * @param r zero-based row of the table
	     * @param a the description of the row
	     */
	    public void setAccessibleRowDescription(int r, Accessible a) {}
	    
	    /**
	     * Returns the description text of the specified column in the table.
	     *
	     * @param c zero-based column of the table
	     * @return the text description of the column
	     */
	    public Accessible getAccessibleColumnDescription(int c) { return null; }
	    
	    /**
	     * Sets the description text of the specified column in the table.
	     *
	     * @param c zero-based column of the table
	     * @param a the text description of the column
	     */
	    public void setAccessibleColumnDescription(int c, Accessible a) {}
	    
	    /**
	     * Returns a boolean value indicating whether the accessible at
	     * a specified row and column is selected.
	     *
	     * @param r zero-based row of the table
	     * @param c zero-based column of the table
	     * @return the boolean value true if the accessible at the
	     * row and column is selected. Otherwise, the boolean value 
	     * false
	     */
	    public boolean isAccessibleSelected(int r, int c) { return false; }
	    
	    /**
	     * Returns a boolean value indicating whether the specified row
	     * is selected.
	     *
	     * @param r zero-based row of the table
	     * @return the boolean value true if the specified row is selected.
	     * Otherwise, false.
	     */
	    public boolean isAccessibleRowSelected(int r) { return false; }
	    
	    /**
	     * Returns a boolean value indicating whether the specified column
	     * is selected.
	     *
	     * @param r zero-based column of the table
	     * @return the boolean value true if the specified column is selected.
	     * Otherwise, false.
	     */
	    public boolean isAccessibleColumnSelected(int c) { return false; }
	    
	    /**
	     * Returns the selected rows in a table.
	     *
	     * @return an array of selected rows where each element is a
	     * zero-based row of the table
	     */
	    public int [] getSelectedAccessibleRows() { return new int[0]; }
	    
	    /**
	     * Returns the selected columns in a table.
	     *
	     * @return an array of selected columns where each element is a
	     * zero-based column of the table
	     */
	    public int [] getSelectedAccessibleColumns() { return new int[0]; }
	}
	

	/**
	 * Returns the column headers as an <code>AccessibleTable</code>.
	 *
	 * @return an <code>AccessibleTable</code> representing the column
	 *      headers
	 */
	public void setAccessibleColumnHeader(AccessibleTable a) {
	    // XXX not implemented
	}

	/**
	 * Returns the description of the specified row in the table.
	 *
	 * @param r zero-based row of the table
	 * @return the description of the row
	 */
	public Accessible getAccessibleRowDescription(int r) {
	    if (r < 0 || r >= getAccessibleRowCount()) {
		throw new IllegalArgumentException(new Integer(r).toString());
	    }
	    if (rowDescription == null) {
		return null;
	    } else {
		return rowDescription[r];
	    }
	}

	/**
	 * Sets the description text of the specified row of the table.
	 *
	 * @param r zero-based row of the table
	 * @param a the description of the row
	 */
	public void setAccessibleRowDescription(int r, Accessible a) {
	    if (r < 0 || r >= getAccessibleRowCount()) {
		throw new IllegalArgumentException(new Integer(r).toString());
	    }
	    if (rowDescription == null) {
		int numRows = getAccessibleRowCount();
		rowDescription = new Accessible[numRows];
	    }
	    rowDescription[r] = a;
	}

	/**
	 * Returns the description of the specified column in the table.
	 *
	 * @param c zero-based column of the table
	 * @return the description of the column
	 */
	public Accessible getAccessibleColumnDescription(int c) {
	    if (c < 0 || c >= getAccessibleColumnCount()) {
		throw new IllegalArgumentException(new Integer(c).toString());
	    }
	    if (columnDescription == null) {
		return null;
	    } else {
		return columnDescription[c];
	    }
	}

	/**
	 * Sets the description text of the specified column of the table.
	 *
	 * @param c zero-based column of the table
	 * @param a the description of the column
	 */
	public void setAccessibleColumnDescription(int c, Accessible a) {
	    if (c < 0 || c >= getAccessibleColumnCount()) {
		throw new IllegalArgumentException(new Integer(c).toString());
	    }
	    if (columnDescription == null) {
		int numColumns = getAccessibleColumnCount();
		columnDescription = new Accessible[numColumns];
	    }
	    columnDescription[c] = a;
	}

	/**
	 * Returns a boolean value indicating whether the accessible at a
	 * given (row, column) is selected.
	 *
	 * @param r zero-based row of the table
	 * @param c zero-based column of the table
	 * @return the boolean value true if the accessible at (row, column)
	 *     is selected; otherwise, the boolean value false
	 */
	public boolean isAccessibleSelected(int r, int c) {
	    return JTable.this.isCellSelected(r, c);
	}

	/**
	 * Returns a boolean value indicating whether the specified row
	 * is selected.
	 *
	 * @param r zero-based row of the table
	 * @return the boolean value true if the specified row is selected;
	 *     otherwise, false
	 */
	public boolean isAccessibleRowSelected(int r) {
	    return JTable.this.isRowSelected(r);
	}

	/**
	 * Returns a boolean value indicating whether the specified column
	 * is selected.
	 *
	 * @param c zero-based column of the table
	 * @return the boolean value true if the specified column is selected;
	 *     otherwise, false
	 */
	public boolean isAccessibleColumnSelected(int c) {
	    return JTable.this.isColumnSelected(c);
	}

	/**
	 * Returns the selected rows in a table.
	 *
	 * @return an array of selected rows where each element is a
	 *     zero-based row of the table
	 */
	public int [] getSelectedAccessibleRows() {
	    return JTable.this.getSelectedRows();
	}

	/**
	 * Returns the selected columns in a table.
	 *
	 * @return an array of selected columns where each element is a
	 *     zero-based column of the table
	 */
	public int [] getSelectedAccessibleColumns() {
	    return JTable.this.getSelectedColumns();
	}

        /**
         * Returns the row at a given index into the table.
         *
         * @param i zero-based index into the table
         * @return the row at a given index
         */
        public int getAccessibleRowAtIndex(int i) {
	    int columnCount = getAccessibleColumnCount();
	    if (columnCount == 0) {
		return -1;
	    } else {
		return (i / columnCount);
	    }
        }

        /**
         * Returns the column at a given index into the table.
         *
         * @param i zero-based index into the table
         * @return the column at a given index
         */
        public int getAccessibleColumnAtIndex(int i) {
	    int columnCount = getAccessibleColumnCount();
	    if (columnCount == 0) {
		return -1;
	    } else {
		return (i % columnCount);
	    }
        }

        /**
         * Returns the index at a given (row, column) in the table.
         *
         * @param r zero-based row of the table
         * @param c zero-based column of the table
         * @return the index into the table
         */
        public int getAccessibleIndexAt(int r, int c) {
            return ((r * getAccessibleColumnCount()) + c);
        }

	// end of AccessibleTable implementation --------------------

        /**
         * The class provides an implementation of the Java Accessibility
	 * API appropriate to table cells.
         */
        protected class AccessibleJTableCell extends AccessibleContext
            implements Accessible, AccessibleComponent {

            private JTable parent;
            private int row;
            private int column;
            private int index;

            /**
             *  Constructs an <code>AccessibleJTableHeaderEntry</code>.
             */
            public AccessibleJTableCell(JTable t, int r, int c, int i) {
                parent = t;
                row = r;
                column = c;
                index = i;
                this.setAccessibleParent(parent);
            }

            /**
             * Gets the <code>AccessibleContext</code> associated with this
	     * component. In the implementation of the Java Accessibility
	     * API for this class, return this object, which is its own
	     * <code>AccessibleContext</code>.
             *
             * @return this object
             */
            public AccessibleContext getAccessibleContext() {
                return this;
            }

            private AccessibleContext getCurrentAccessibleContext() {
                TableColumn aColumn = getColumnModel().getColumn(column);
                TableCellRenderer renderer = aColumn.getCellRenderer();
                if (renderer == null) {
                    Class columnClass = getColumnClass(column);
                    renderer = getDefaultRenderer(columnClass);
                }
                Component component = renderer.getTableCellRendererComponent(
                                  JTable.this, getValueAt(row, column),
				  false, false, row, column);
                if (component instanceof Accessible) {
                    return ((Accessible) component).getAccessibleContext();
                } else {
                    return null;
                }
            }

            private Component getCurrentComponent() {
                TableColumn aColumn = getColumnModel().getColumn(column);
                TableCellRenderer renderer = aColumn.getCellRenderer();
                if (renderer == null) {
                    Class columnClass = getColumnClass(column);
                    renderer = getDefaultRenderer(columnClass);
                }
                return renderer.getTableCellRendererComponent(
                                  JTable.this, null, false, false,
                                  row, column);
            }

        // AccessibleContext methods

            /**
             * Gets the accessible name of this object.
             *
             * @return the localized name of the object; <code>null</code>
             *     if this object does not have a name
             */
            public String getAccessibleName() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    String name = ac.getAccessibleName();
                    if ((name != null) && (name != "")) {
                        return ac.getAccessibleName();
                    }
                }
                if ((accessibleName != null) && (accessibleName != "")) {
                    return accessibleName;
                } else {
		    return null;
                }
            }

            /**
             * Sets the localized accessible name of this object.
             *
             * @param s the new localized name of the object
             */
            public void setAccessibleName(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleName(s);
                } else {
                    super.setAccessibleName(s);
                }
            }

            //
            // *** should check toolTip text for desc. (needs MouseEvent)
            //
            /**
             * Gets the accessible description of this object.
             *
             * @return the localized description of the object;
             *     <code>null</code> if this object does not have
             *     a description
             */
            public String getAccessibleDescription() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleDescription();
                } else {
                    return super.getAccessibleDescription();
                }
            }

            /**
             * Sets the accessible description of this object.
             *
             * @param s the new localized description of the object
             */
            public void setAccessibleDescription(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleDescription(s);
                } else {
                    super.setAccessibleDescription(s);
                }
            }

            /**
             * Gets the role of this object.
             *
             * @return an instance of <code>AccessibleRole</code>
             *      describing the role of the object
             * @see AccessibleRole
             */
            public AccessibleRole getAccessibleRole() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleRole();
                } else {
                    return AccessibleRole.UNKNOWN;
                }
            }

            /**
             * Gets the state set of this object.
             *
             * @return an instance of <code>AccessibleStateSet</code>
             *     containing the current state set of the object
             * @see AccessibleState
             */
            public AccessibleStateSet getAccessibleStateSet() {
                AccessibleContext ac = getCurrentAccessibleContext();
		AccessibleStateSet as = null;

                if (ac != null) {
                    as = ac.getAccessibleStateSet();
                }
		if (as == null) {
                    as = new AccessibleStateSet();
                }
		Rectangle rjt = JTable.this.getVisibleRect();
                Rectangle rcell = JTable.this.getCellRect(row, column, false);
		if (rjt.intersects(rcell)) {
		    as.add(AccessibleState.SHOWING);
                } else {
                    if (as.contains(AccessibleState.SHOWING)) {
			 as.remove(AccessibleState.SHOWING);
		    }
		}
                if (parent.isCellSelected(row, column)) {
	            as.add(AccessibleState.SELECTED);
                } else if (as.contains(AccessibleState.SELECTED)) {
		    as.remove(AccessibleState.SELECTED);
                }
		if ((row == getSelectedRow()) && (column == getSelectedColumn())) {
		    as.add(AccessibleState.ACTIVE);
		}
	        as.add(AccessibleState.TRANSIENT);
                return as;
            }

            /**
             * Gets the <code>Accessible</code> parent of this object.
             *
             * @return the Accessible parent of this object;
             *     <code>null</code> if this object does not
             *     have an <code>Accessible</code> parent
             */
            public Accessible getAccessibleParent() {
                return parent;
            }

            /**
             * Gets the index of this object in its accessible parent.
             *
             * @return the index of this object in its parent; -1 if this
             *     object does not have an accessible parent
             * @see #getAccessibleParent
             */
            public int getAccessibleIndexInParent() {
                return index;
            }

            /**
             * Returns the number of accessible children in the object.
             *
             * @return the number of accessible children in the object
             */
            public int getAccessibleChildrenCount() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleChildrenCount();
                } else {
                    return 0;
                }
            }

            /**
             * Returns the specified <code>Accessible</code> child of the
             * object.
             *
             * @param i zero-based index of child
             * @return the <code>Accessible</code> child of the object
             */
            public Accessible getAccessibleChild(int i) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    Accessible accessibleChild = ac.getAccessibleChild(i);
                    ac.setAccessibleParent(this);
                    return accessibleChild;
                } else {
                    return null;
                }
            }

            /**
             * Gets the locale of the component. If the component
             * does not have a locale, then the locale of its parent
             * is returned.
             *
             * @return this component's locale; if this component does
             *    not have a locale, the locale of its parent is returned
             * @exception IllegalComponentStateException if the
             *    <code>Component</code> does not have its own locale
             *    and has not yet been added to a containment hierarchy
             *    such that the locale can be determined from the
             *    containing parent
             * @see #setLocale
             */
            public Locale getLocale() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getLocale();
                } else {
                    return null;
                }
            }

            /**
             * Adds a <code>PropertyChangeListener</code> to the listener list.
             * The listener is registered for all properties.
             *
             * @param l  the <code>PropertyChangeListener</code>
             *     to be added
             */
            public void addPropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.addPropertyChangeListener(l);
                } else {
                    super.addPropertyChangeListener(l);
                }
            }

            /**
             * Removes a <code>PropertyChangeListener</code> from the
             * listener list. This removes a <code>PropertyChangeListener</code>
             * that was registered for all properties.
             *
             * @param l  the <code>PropertyChangeListener</code>
             *    to be removed
             */
            public void removePropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.removePropertyChangeListener(l);
                } else {
                    super.removePropertyChangeListener(l);
                }
            }

            /**
             * Gets the <code>AccessibleAction</code> associated with this
             * object if one exists.  Otherwise returns <code>null</code>.
             *
             * @return the <code>AccessibleAction</code>, or <code>null</code>
             */
            public AccessibleAction getAccessibleAction() {
                return getCurrentAccessibleContext().getAccessibleAction();
            }

            /**
             * Gets the <code>AccessibleComponent</code> associated with
             * this object if one exists.  Otherwise returns <code>null</code>.
             *
             * @return the <code>AccessibleComponent</code>, or
             *    <code>null</code>
             */
            public AccessibleComponent getAccessibleComponent() {
                return this; // to override getBounds()
            }

            /**
             * Gets the <code>AccessibleSelection</code> associated with
             * this object if one exists.  Otherwise returns <code>null</code>.
             *
             * @return the <code>AccessibleSelection</code>, or
             *    <code>null</code>
             */
            public AccessibleSelection getAccessibleSelection() {
                return getCurrentAccessibleContext().getAccessibleSelection();
            }

            /**
             * Gets the <code>AccessibleText</code> associated with this
             * object if one exists.  Otherwise returns <code>null</code>.
             *
             * @return the <code>AccessibleText</code>, or <code>null</code>
             */
            public AccessibleText getAccessibleText() {
                return getCurrentAccessibleContext().getAccessibleText();
            }

            /**
             * Gets the <code>AccessibleValue</code> associated with
             * this object if one exists.  Otherwise returns <code>null</code>.
             *
             * @return the <code>AccessibleValue</code>, or <code>null</code>
             */
            public AccessibleValue getAccessibleValue() {
                return getCurrentAccessibleContext().getAccessibleValue();
            }


        // AccessibleComponent methods

            /**
             * Gets the background color of this object.
             *
             * @return the background color, if supported, of the object;
             *     otherwise, <code>null</code>
             */
            public Color getBackground() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getBackground();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getBackground();
                    } else {
                        return null;
                    }
                }
            }

            /**
             * Sets the background color of this object.
             *
             * @param c the new <code>Color</code> for the background
             */
            public void setBackground(Color c) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setBackground(c);
                } else {
                    Component cp = getCurrentComponent();
                    if (cp != null) {
                        cp.setBackground(c);
                    }
                }
            }

            /**
             * Gets the foreground color of this object.
             *
             * @return the foreground color, if supported, of the object;
             *     otherwise, <code>null</code>
             */
            public Color getForeground() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getForeground();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getForeground();
                    } else {
                        return null;
                    }
                }
            }

            /**
             * Sets the foreground color of this object.
             *
             * @param c the new <code>Color</code> for the foreground
             */
            public void setForeground(Color c) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setForeground(c);
                } else {
                    Component cp = getCurrentComponent();
                    if (cp != null) {
                        cp.setForeground(c);
                    }
                }
            }

            /**
             * Gets the <code>Cursor</code> of this object.
             *
             * @return the <code>Cursor</code>, if supported,
             *    of the object; otherwise, <code>null</code>
             */
            public Cursor getCursor() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getCursor();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getCursor();
                    } else {
                        Accessible ap = getAccessibleParent();
                        if (ap instanceof AccessibleComponent) {
                            return ((AccessibleComponent) ap).getCursor();
                        } else {
                            return null;
                        }
                    }
                }
            }

            /**
             * Sets the <code>Cursor</code> of this object.
             *
             * @param c the new <code>Cursor</code> for the object
             */
            public void setCursor(Cursor c) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setCursor(c);
                } else {
                    Component cp = getCurrentComponent();
                    if (cp != null) {
                        cp.setCursor(c);
                    }
                }
            }

            /**
             * Gets the <code>Font</code> of this object.
             *
             * @return the <code>Font</code>,if supported,
             *   for the object; otherwise, <code>null</code>
             */
            public Font getFont() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getFont();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getFont();
                    } else {
                        return null;
                    }
                }
            }

            /**
             * Sets the <code>Font</code> of this object.
             *
             * @param f the new <code>Font</code> for the object
             */
            public void setFont(Font f) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setFont(f);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setFont(f);
                    }
                }
            }

            /**
             * Gets the <code>FontMetrics</code> of this object.
             *
             * @param f the <code>Font</code>
             * @return the <code>FontMetrics</code> object, if supported;
             *    otherwise <code>null</code>
             * @see #getFont
             */
            public FontMetrics getFontMetrics(Font f) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getFontMetrics(f);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getFontMetrics(f);
                    } else {
                        return null;
                    }
                }
            }

            /**
             * Determines if the object is enabled.
             *
             * @return true if object is enabled; otherwise, false
             */
            public boolean isEnabled() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isEnabled();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isEnabled();
                    } else {
                        return false;
                    }
                }
            }

            /**
             * Sets the enabled state of the object.
             *
             * @param b if true, enables this object; otherwise, disables it
             */
            public void setEnabled(boolean b) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setEnabled(b);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setEnabled(b);
                    }
                }
            }

            /**
             * Determines if this object is visible.  Note: this means that the
             * object intends to be visible; however, it may not in fact be
             * showing on the screen because one of the objects that this object
             * is contained by is not visible.  To determine if an object is
             * showing on the screen, use <code>isShowing</code>.
             *
             * @return true if object is visible; otherwise, false
             */
            public boolean isVisible() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isVisible();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isVisible();
                    } else {
                        return false;
                    }
                }
            }

            /**
             * Sets the visible state of the object.
             *
             * @param b if true, shows this object; otherwise, hides it
             */
            public void setVisible(boolean b) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setVisible(b);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setVisible(b);
                    }
                }
            }

            /**
             * Determines if the object is showing.  This is determined
             * by checking the visibility of the object and ancestors
             * of the object.  Note: this will return true even if the
             * object is obscured by another (for example,
             * it happens to be underneath a menu that was pulled down).
             *
             * @return true if the object is showing; otherwise, false
             */
            public boolean isShowing() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    if (ac.getAccessibleParent() != null) {
                        return ((AccessibleComponent) ac).isShowing();
                    } else {
                        // Fixes 4529616 - AccessibleJTableCell.isShowing()
                        // returns false when the cell on the screen
                        // if no parent
                        return isVisible();
                    }
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isShowing();
                    } else {
                        return false;
                    }
                }
            }

            /**
             * Checks whether the specified point is within this
             * object's bounds, where the point's x and y coordinates
             * are defined to be relative to the coordinate system of
             * the object.
             *
             * @param p the <code>Point</code> relative to the
             *    coordinate system of the object
             * @return true if object contains <code>Point</code>;
             *    otherwise false
             */
            public boolean contains(Point p) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    Rectangle r = ((AccessibleComponent) ac).getBounds();
                    return r.contains(p);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        Rectangle r = c.getBounds();
                        return r.contains(p);
                    } else {
                        return getBounds().contains(p);
                    }
                }
            }

            /**
             * Returns the location of the object on the screen.
             *
             * @return location of object on screen -- can be
             *    <code>null</code> if this object is not on the screen
             */
            public Point getLocationOnScreen() {
                if (parent != null) {
                    Point parentLocation = parent.getLocationOnScreen();
                    Point componentLocation = getLocation();
                    componentLocation.translate(parentLocation.x, parentLocation.y);
                    return componentLocation;
                } else {
                    return null;
                }
            }

            /**
             * Gets the location of the object relative to the parent
             * in the form of a point specifying the object's
             * top-left corner in the screen's coordinate space.
             *
             * @return an instance of <code>Point</code> representing
             *    the top-left corner of the object's bounds in the
             *    coordinate space of the screen; <code>null</code> if
             *    this object or its parent are not on the screen
             */
            public Point getLocation() {
                if (parent != null) {
                    Rectangle r = parent.getCellRect(row, column, false);
                    if (r != null) {
                        return r.getLocation();
                    }
                }
                return null;
            }

            /**
             * Sets the location of the object relative to the parent.
             */
            public void setLocation(Point p) {
//              if ((parent != null)  && (parent.contains(p))) {
//                  ensureIndexIsVisible(indexInParent);
//              }
            }

            public Rectangle getBounds() {
                if (parent != null) {
                    return parent.getCellRect(row, column, false);
                } else {
                    return null;
                }
            }

            public void setBounds(Rectangle r) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setBounds(r);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setBounds(r);
                    }
                }
            }

            public Dimension getSize() {
                if (parent != null) {
                    Rectangle r = parent.getCellRect(row, column, false);
                    if (r != null) {
                        return r.getSize();
                    }
                }
                return null;
            }

            public void setSize (Dimension d) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setSize(d);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setSize(d);
                    }
                }
            }

            public Accessible getAccessibleAt(Point p) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getAccessibleAt(p);
                } else {
                    return null;
                }
            }

            public boolean isFocusTraversable() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isFocusTraversable();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isFocusTraversable();
                    } else {
                        return false;
                    }
                }
            }

            public void requestFocus() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).requestFocus();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.requestFocus();
                    }
                }
            }

            public void addFocusListener(FocusListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).addFocusListener(l);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.addFocusListener(l);
                    }
                }
            }

            public void removeFocusListener(FocusListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).removeFocusListener(l);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.removeFocusListener(l);
                    }
                }
            }

        } // inner class AccessibleJTableCell

    }  // inner class AccessibleJTable

}  // End of Class JTable



