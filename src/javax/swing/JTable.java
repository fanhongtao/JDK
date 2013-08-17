/*
 * @(#)JTable.java	1.110 98/09/18
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

package javax.swing;

import java.util.*;

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

import java.text.DateFormat;
import java.text.NumberFormat; 

/**
 * JTable is a user-interface component that presents data in a two-dimensional
 * table format. The JTable has many facilities that make it possible to
 * customize its rendering and editing but provides defaults
 * for these features so that simple tables can be set up easily.
 * For example, to set up a table with 10 rows and 10 columns of numbers:
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
 * Because the JTable is now much easier to set up with custom models
 * the DefaultTableModel is less useful than it was in previous releases.
 * Instead of copying the data in an application into the DefaultTableModel,
 * we recommend wrapping it in the methods of the TableModel interface and
 * passing the real data to the JTable as above. This technique is nearly as concise
 * as using a DefaultTableModel and starting this way has a number of advantages
 * over the longer term. In particular: it is a scalable technique,
 * is easier to handle dynamic or editable tables and often results in much
 * more efficient applications because the model is free to choose the
 * internal representation that best suits the data.
 * <p>
 * The "Table" directory in the examples/demo area gives a number of complete
 * examples of JTable usage, covering how the JTable can be used to provide
 * an editable view of data taken from a database and how to modify the columns
 * in the display to use specialized renderers and editors. For example, overriding
 * AbstractTableModel's <code>getColumnClass()</code> method to return a value of
 * <code>ImageIcon.class</code> for a given column allows icons to be displayed,
 * while returning a value of <code>Number.class</code> allows digits to be
 * right-justified in the column.
 * <p>
 * The JTable uses integers exclusively to refer to both the rows and the columns
 * of the model that it displays. The JTable simply takes a tabular range of cells
 * and uses <code>getValueAt(int, int)</code> to retrieve and display the appropriate
 * values from the model.
 * <p>
 * If <code>getTableHeader().setReorderingAllowed(boolean)</code> is used to
 * enable column reordering columns may be rearranged in the JTable so that the
 * view's columns appear in a different order to the columns in the model.
 * This does not affect the implementation of the model at all: when the
 * columns are reordered, the JTable maintains the new order of the columns
 * internally and converts its column indices before querying the model.
 * <p>
 * So, when writing a TableModel, it is not necessary to listen for column
 * reordering events as the the model will be queried in its own co-ordinate
 * system regardless of what is happening in the view.
 * In the examples area there is a demonstration of a sorting algorithm making
 * use of exactly this technique to interpose yet another co-ordinate system
 * where the order of the rows is changed, rather than the order of the columns.
 * <p>
 * The general rule for the JTable API and the APIs of all its associated classes,
 * including the the column model and both the row and column selection models, is:
 * methods using integer indices for rows and columns always use the co-ordinate
 * system of the view. There are three exceptions to this rule:
 * <ul>
 * <li> All references to rows and columns in the TableModel
 *      interface are in the co-ordinate system of the model.
 * <li> The index <I>modelIndex</I> in the TableColumn constructors
 *      refers to the index of the column in the model, not the view.
 * <li> All constructors for the TableModelEvent, which describes changes
 *      that have taken place in a table model, use the co-ordinate system
 *      of the model.
 * </ul>
 * The TableColumn provides a slot for holding an identifier or "tag" for each column
 * and the JTable and TableColumModel both support <I>getColumn(Object id)</I>
 * conveniences for locating columns by their identifier. If no identifier is
 * explicitly set, the TableColumn returns its header value (the name of the column)
 * as a default. A different identifier, which can be of any type, can be set
 * using the TableColumn's <I>setIdentifier()</I> method. All of the JTable's
 * functions operate correctly regardless of the type and uniqueness of these
 * identifiers.
 * <p>
 * The <I>convertColumnIndexToView()</I> and
 * <I>convertColumnIndexToModel()</I> methods have been provided to
 * convert between the two co-ordinate systems but
 * they are rarely needed during normal use.
 * <p>
 * Like all JComponent classes, you can use
 * {@link JComponent#registerKeyboardAction} to associate an
 * {@link Action} object with a {@link KeyStroke} and execute the
 * action under specified conditions.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/table.html">How to Use Tables</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JTable">JTable</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 *
 * @beaninfo
 *   attribute: isContainer false
 *
 * @version 1.110 09/18/98
 * @author Philip Milne
 * @author Alan Chung
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

    /** Do not adjust column widths automatically, use a scrollbar */
    public static final int     AUTO_RESIZE_OFF = 0;

    /** When a column is adjusted in the UI, adjust the next column the opposite way */
    public static final int     AUTO_RESIZE_NEXT_COLUMN = 1;

    /** During UI adjustment, change subsequent columns to preserve the total width */
    public static final int     AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;

    /** During all resize operations, apply adjustments to the last column only */
    public static final int     AUTO_RESIZE_LAST_COLUMN = 3;

    /** During all resize operations, proportionately resize all columns */
    public static final int     AUTO_RESIZE_ALL_COLUMNS = 4;


//
// Instance Variables
//

    /** The TableModel of the table */
    protected TableModel        dataModel;

    /** The TableColumnModel of the table */
    protected TableColumnModel  columnModel;

    /** The ListSelectionModel of the table, used to keep track of row selections */
    protected ListSelectionModel selectionModel;

    /** The TableHeader working with the table */
    protected JTableHeader      tableHeader;

    /** The height of all rows in the table */
    protected int               rowHeight;

    /** The height margin between rows */
    protected int               rowMargin;

    /** The color of the grid */
    protected Color             gridColor;

    /** The table draws horizontal lines between cells if showHorizontalLines is true */
    protected boolean           showHorizontalLines;

    /** The table draws vertical lines between cells if showVerticalLines is true */
    protected boolean           showVerticalLines;

    /**
     *  This mode value determines if table automatically resizes the
     *  width the table's columns to take up the entire width of the
     *  table, and how it does the resizing.
     */
    protected int               autoResizeMode;

    /**
     *  The table will query the TableModel to build the default
     *  set of columns if this is true.
     */
    protected boolean           autoCreateColumnsFromModel;

    /** Used by the Scrollable interface to determine the initial visible area */
    protected Dimension         preferredViewportSize;

    /** Row selection allowed in this table */
    protected boolean           rowSelectionAllowed;

    /**
     * If this is true, then both a row selection and a column selection
     * can be non-empty at the same time, the selected cells are the
     * the cells whose row and column are both selected.
     */
    protected boolean           cellSelectionEnabled;

    /** If editing, Component that is handling the editing. */
    transient protected Component       editorComp;

    /**
     * The object that overwrites the screen real estate occupied by the
     * current cell and allows the user to change those contents.
     */
    transient protected TableCellEditor cellEditor;

    /** Identifies the column of the cell being edited. */
    transient protected int             editingColumn;

    /** Identifies the row of the cell being edited. */
    transient protected int             editingRow;

    /**
     * A table of objects that display the contents of a cell,
     * indexed by class.
     */
    transient protected Hashtable defaultRenderersByColumnClass;

    /**
     * A table of objects that display and edit the contents of a cell,
     * indexed by class.
     */
    transient protected Hashtable defaultEditorsByColumnClass;

    /** The foreground color of selected cells */
    protected Color selectionForeground;

    /** The background color of selected cells */
    protected Color selectionBackground;

//
// Constructors
//

    /**
     * Constructs a default JTable which is initialized with a default
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
     * Constructs a JTable which is initialized with <i>dm</i> as the
     * data model, a default column model, and a default selection
     * model.
     *
     * @param dm        The data model for the table
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public JTable(TableModel dm) {
        this(dm, null, null);
    }

    /**
     * Constructs a JTable which is initialized with <i>dm</i> as the
     * data model, <i>cm</i> as the column model, and a default selection
     * model.
     *
     * @param dm        The data model for the table
     * @param cm        The column model for the table
     * @see #createDefaultSelectionModel
     */
    public JTable(TableModel dm, TableColumnModel cm) {
        this(dm, cm, null);
    }

    /**
     * Constructs a JTable which is initialized with <i>dm</i> as the
     * data model, <i>cm</i> as the column model, and <i>sm</i> as the
     * selection model.  If any of the parameters are <b>null</b> this
     * method will initialize the table with the corresponding
     * default model. The <i>autoCreateColumnsFromModel</i> flag is set
     * to false if <i>cm</i> is non-null, otherwise it is set to true
     * and the column model is populated with suitable TableColumns
     * for the columns in <i>dm</i>.
     *
     * @param dm        The data model for the table
     * @param cm        The column model for the table
     * @param sm        The row selection model for the table
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public JTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super();
        setLayout(null);

        if (cm == null) {
            cm = createDefaultColumnModel();
            autoCreateColumnsFromModel = true;
        }
        setColumnModel(cm);

        if (sm == null)
            sm = createDefaultSelectionModel();
        setSelectionModel(sm);

    // Set the model last, that way if the autoCreatColumnsFromModel has
    // been set above, we will automatically populate an empty columnModel
    // with suitable columns for the new model.
        if (dm == null)
            dm = createDefaultDataModel();
        setModel(dm);

        initializeLocalVars();
        updateUI();
    }

    /**
     * Constructs a JTable with <i>numRows</i> and <i>numColumns</i> of
     * empty cells using the DefaultTableModel.  The columns will have
     * names of the form "A", "B", "C", etc.
     *
     * @param numRows           The number of rows the table holds
     * @param numColumns        The number of columns the table holds
     * @see javax.swing.table.DefaultTableModel
     */
    public JTable(int numRows, int numColumns) {
        this(new DefaultTableModel(numRows, numColumns));
    }

    /**
     * Constructs a JTable to display the values in the Vector of Vectors,
     * <i>rowData</i>, with column names, <i>columnNames</i>.
     * The Vectors contained in <i>rowData</i> should contain the values
     * for that row. In other words, the value of the cell at row 1,
     * column 5 can be obtained with the following code:
     * <p>
     * <pre>((Vector)rowData.elementAt(1)).elementAt(5);</pre>
     * <p>
     * All rows must be of the same length as <i>columnNames</i>.
     * <p>
     * @param rowData           The data for the new table
     * @param columnNames       Names of each column
     */
    public JTable(final Vector rowData, final Vector columnNames) {
        this(new AbstractTableModel() {
            public String getColumnName(int column) { return columnNames.elementAt(column).toString(); }
            public int getRowCount() { return rowData.size(); }
            public int getColumnCount() { return columnNames.size(); }
            public Object getValueAt(int row, int column) {
                return ((Vector)rowData.elementAt(row)).elementAt(column);
            }
            public boolean isCellEditable(int row, int column) { return true; }
            public void setValueAt(Object value, int row, int column) {
                ((Vector)rowData.elementAt(row)).setElementAt(value, column);
                fireTableCellUpdated(row, column);
            }
        });
    }

    /**
     * Constructs a JTable to display the values in the two dimensional array,
     * <i>rowData</i>, with column names, <i>columnNames</i>.
     * <i>rowData</i> is an Array of rows, so the value of the cell at row 1,
     * column 5 can be obtained with the following code:
     * <p>
     * <pre> rowData[1][5]; </pre>
     * <p>
     * All rows must be of the same length as <i>columnNames</i>.
     * <p>
     * @param rowData           The data for the new table
     * @param columnNames       Names of each column
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
     * Calls <code>configureEnclosingScrollPane</code>.
     *
     * @see #configureEnclosingScrollPane
     */
    public void addNotify() {
        super.addNotify();
        configureEnclosingScrollPane();
    }

    /**
     * If the JTable is the viewportView of an enclosing JScrollPane
     * (the usual situation), configure this ScrollPane by, amongst other things,
     * installing the table's tableHeader as the columnHeaderView of the scrollpane.
     * When a JTable is added to a JScrollPane in the usual way,
     * using <code>new JScrollPane(myTable)</code>, <code>addNotify</code> is
     * called in the JTable (when the table is added to the viewport).
     * JTable's <code>addNotify</code> method in turn calls this method
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
                scrollPane.getViewport().setBackingStoreEnabled(true);
                scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
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
     * Sets the tableHeader working with this JTable to <I>newHeader</I>.
     * It is legal to have a <B>null</B> tableHeader.
     *
     * @param   newHeader                       new tableHeader
     * @see     #getTableHeader
     * @beaninfo
     * description: The JTableHeader instance which renders the column headers.
     */
    public void setTableHeader(JTableHeader newHeader) {
        if (tableHeader != newHeader) {
            // Release the old header
            if (tableHeader != null)
                tableHeader.setTable(null);

            tableHeader = newHeader;
            if (tableHeader != null)
                tableHeader.setTable(this);
        }
    }

    /**
     * Returns the tableHeader working with this JTable.
     *
     * @return  the tableHeader working with the receiver
     * @see     #setTableHeader
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * Sets the height for rows to <I>newRowHeight</I> and invokes tile
     *
     * @param   newRowHeight                    new row height
     * @exception IllegalArgumentException      If <I>newRowHeight</I> is
     *                                          less than 1.
     * @see     #getRowHeight
     * @beaninfo
     * description: The height of the cells including the inter-cell spacing.
     */
    public void setRowHeight(int newHeight) {
        if (newHeight <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
        rowHeight = newHeight;

        resizeAndRepaint();
    }

    /**
     * Returns the height of a table row in the receiver.
     * The default row height is 16.0.
     *
     * @return  the height of each row in the receiver
     * @see     #setRowHeight
     */
    public int getRowHeight() {
        return rowHeight;
    }

    /**
     * Sets the amount of emtpy space between rows.
     *
     * @see     #getRowMargin
     */
    public void setRowMargin(int rowMargin) {
        this.rowMargin = rowMargin;
    }

    /**
     * Gets the amount of emtpy space between rows. Equivalent to:
     * <code>getIntercellSpacing().height</code>.
     *
     * @see     #setRowMargin
     */
    public int getRowMargin() {
        return rowMargin;
    }

    /**
     * Sets the width and height between cells to <I>newSpacing</I> and
     * redisplays the receiver.
     *
     * @param   newSpacing              The new width and height intercellSpacing
     * @see     #getIntercellSpacing
     * @beaninfo
     * description: The spacing between the cells, drawn in the background color of the JTable.
     */
    public void setIntercellSpacing(Dimension newSpacing) {
        // Set the rowMargin here and columnMargin in the TableColumnModel
        rowMargin = newSpacing.height;
        getColumnModel().setColumnMargin(newSpacing.width);

        resizeAndRepaint();
    }

    /**
     * Returns the horizontal and vertical spacing between cells.
     * The default spacing is (3, 2).
     *
     * @return  the horizontal and vertical spacing between cells
     * @see     #setIntercellSpacing
     */
    public Dimension getIntercellSpacing() {
        return new Dimension(getColumnModel().getColumnMargin(), rowMargin);
    }

    /**
     * Sets the color used to draw grid lines to <I>color</I> and redisplays
     * the receiver. The default color is gray.
     *
     * @param   color                           new color of the grid
     * @exception IllegalArgumentException      if <I>color</I> is null
     * @see     #getGridColor
     */
    public void setGridColor(Color newColor) {
        if (newColor == null) {
            throw new IllegalArgumentException("New color is null");
        }
        gridColor = newColor;

        // Redraw
        repaint();
    }

    /**
     * Returns the color used to draw grid lines. The default color is gray.
     *
     * @return  the color used to draw grid lines
     * @see     #setGridColor
     */
    public Color getGridColor() {
        return gridColor;
    }

    /**
     *  Sets whether the receiver draws grid lines around cells.
     *  If <I>flag</I> is true it does; if it is false it doesn't.
     *  There is no getShowGrid() method as the this state is held
     *  in two variables: showHorizontalLines and showVerticalLines
     *  each of which may be queried independently.
     *
     * @param   flag                    true if table view should draw grid lines
     *
     * @see     #setShowVerticalLines
     * @see     #setShowHorizontalLines
     * @beaninfo
     * description: The color used to draw the grid lines.
     */
    public void setShowGrid(boolean b) {
        setShowHorizontalLines(b);
        setShowVerticalLines(b);

        // Redraw
        repaint();
    }

    /**
     *  Sets whether the receiver draws horizontal lines between cells.
     *  If <I>flag</I> is true it does; if it is false it doesn't.
     *
     * @param   flag                    true if table view should draw horizontal lines
     * @see     #getShowHorizontalLines
     * @see     #setShowGrid
     * @see     #setShowVerticalLines
     * @beaninfo
     * description: Whether horizontal lines should be drawn in between the cells.
     */
    public void setShowHorizontalLines(boolean b) {
        showHorizontalLines = b;

        // Redraw
        repaint();
    }

    /**
     *  Sets whether the receiver draws vertical lines between cells.
     *  If <I>flag</I> is true it does; if it is false it doesn't.
     *
     * @param   flag                    true if table view should draw vertical lines
     * @see     #getShowVerticalLines
     * @see     #setShowGrid
     * @see     #setShowHorizontalLines
     * @beaninfo
     * description: Whether vertical lines should be drawn in between the cells.
     */
    public void setShowVerticalLines(boolean b) {
        showVerticalLines = b;

        // Redraw
        repaint();
    }

    /**
     * Returns true if the receiver draws horizontal lines between cells, false if it
     * doesn't. The default is true.
     *
     * @return  true if the receiver draws horizontal lines between cells, false if it
     *          doesn't
     * @see     #setShowHorizontalLines
     */
    public boolean getShowHorizontalLines() {
        return showHorizontalLines;
    }

    /**
     * Returns true if the receiver draws vertical lines between cells, false if it
     * doesn't. The default is true.
     *
     * @return  true if the receiver draws vertical lines between cells, false if it
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
     * @see     #sizeColumnsToFit(int)
     * @beaninfo
     * description: Whether the columns should adjust themselves automatically.
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
            autoResizeMode = mode;
            resizeAndRepaint();
            tableHeader.resizeAndRepaint();
        }
    }

    /**
     * Returns auto resize mode of the table.
     *
     * @return  the autoResizeMode of the table
     *
     * @see     #setAutoResizeMode
     * @see     #sizeColumnsToFit(int)
     */
    public int getAutoResizeMode() {
        return autoResizeMode;
    }

    /**
     * Sets the table's autoCreateColumnsFromModel flag.  This method
     * will call createDefaultColumnsFromModel() if <i>createColumns</i>
     * is true.
     *
     * @param   createColumns   true if JTable should auto create columns
     * @see     #getAutoCreateColumnsFromModel
     * @see     #createDefaultColumnsFromModel
     * @beaninfo
     * description: Automatically populate the columnModel when a new TableModel is submitted.
     */
    public void setAutoCreateColumnsFromModel(boolean createColumns) {
        if (autoCreateColumnsFromModel != createColumns) {
            autoCreateColumnsFromModel = createColumns;

            if (autoCreateColumnsFromModel)
                createDefaultColumnsFromModel();
        }
    }

    /**
     * Returns whether the table will create default columns from the model.
     * If this is true, setModel() will clear any existing columns and
     * create new columns from the new model.  Also if the event in the
     * the tableChanged() notification specified the entired table changed
     * then the columns will be rebuilt.  The default is true.
     *
     * @return  the autoCreateColumnsFromModel of the table
     * @see     #setAutoCreateColumnsFromModel
     * @see     #createDefaultColumnsFromModel
     */
    public boolean getAutoCreateColumnsFromModel() {
        return autoCreateColumnsFromModel;
    }

    /**
     * This method will create default columns for the table from
     * the data model using the getColumnCount() and getColumnClass() methods
     * defined in the TableModel interface.
     * <p>
     * This method will clear any exsiting columns before creating the
     * new columns based on information from the model.
     *
     * @see     #getAutoCreateColumnsFromModel
     */
    public void createDefaultColumnsFromModel() {
        TableModel m = getModel();
        if (m != null) {
            // Remove any current columns
            TableColumnModel cm = getColumnModel();
            cm.removeColumnModelListener(this);
            while (cm.getColumnCount() > 0)
                cm.removeColumn(cm.getColumn(0));

            // Create new columns from the data model info
            for (int i = 0; i < m.getColumnCount(); i++) {
                TableColumn newColumn = new TableColumn(i);
                addColumn(newColumn);
            }
            cm.addColumnModelListener(this);
        }
    }

    /**
     * Set a default renderer to be used if no renderer has been set in
     * a TableColumn. If renderer is null, remove the default renderer 
     * for this column class. 
     *
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
     * Returns the renderer to be used when no renderer has been set in
     * a TableColumn. During the rendering of cells the renderer is fetched from
     * a Hashtable of entries according to the class of the cells in the column. If
     * there is no entry for this <I>columnClass</I> the method returns
     * the entry for the most specific superclass. The JTable installs entries
     * for <I>Object</I>, <I>Number</I> and <I>Boolean</I> all which can be modified
     * or replaced.
     *
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
     * Set a default editor to be used if no editor has been set in
     * a TableColumn. If no editing is required in a table, or a
     * particular column in a table, use the isCellEditable()
     * method in the TableModel interface to ensure that the
     * JTable will not start an editor in these columns. 
     * If editor is null, remove the default editor for this 
     * column class. 
     *
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
     * a TableColumn. During the editing of cells the editor is fetched from
     * a Hashtable of entries according to the class of the cells in the column. If
     * there is no entry for this <I>columnClass</I> the method returns
     * the entry for the most specific superclass. The JTable installs entries
     * for <I>Object</I>, <I>Number</I> and <I>Boolean</I> all which can be modified
     * or replaced.
     *
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

//
// Selection methods
//
    /**
     * Sets the table's selection mode to allow only single selections, a single
     * contiguous interval, or multiple intervals.
     *
     * NOTE:<br>
     * JTable provides all the methods for handling column and row selection.
     * When setting states, such as setSelectionMode, it not only
     * updates the mode for the row selection model but also sets similar
     * values in the selection model of the columnModel.
     * If you want to have the row and column selection models operating 
     * in different modes, set them both directly. 
     * <p>
     * Both the row and column selection models for the JTable default
     * to using a DefaultListSelectionModel so that JTable works the same
     * way as the JList. See setSelectionMode() in JList for details
     * about the modes.
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
     * @see #getRowSelectionAllowed
     * @beaninfo
     * description: If true, an entire row is selected for each selected cell.
     */
    public void setRowSelectionAllowed(boolean flag) {
        rowSelectionAllowed = flag;
    }

    /**
     * Returns true if rows can be selected.
     *
     * @return true if rows can be selected
     * @see #setRowSelectionAllowed
     */
    public boolean getRowSelectionAllowed() {
        return rowSelectionAllowed;
    }

    /**
     * Sets whether the columns in this model can be selected.
     *
     * @see #getColumnSelectionAllowed
     * @beaninfo
     * description: If true, an entire column is selected for each selected cell.
     */
    public void setColumnSelectionAllowed(boolean flag) {
        columnModel.setColumnSelectionAllowed(flag);
    }

    /**
     * Returns true if columns can be selected.
     *
     * @return true if columns can be selected.
     * @see #setColumnSelectionAllowed
     */
    public boolean getColumnSelectionAllowed() {
        return columnModel.getColumnSelectionAllowed();
    }

    /**
     * Sets whether this table allows both a column selection and a
     * row selection to exist at the same time. When set, this results
     * in a facility to select a rectangular region of cells in the display.
     * This flag over-rides the row and column selection
     * modes ensuring that cell selection is possible whenever this flag is set.

     * @see #getCellSelectionEnabled
     * @beaninfo
     * description: Select a rectangular region of cells rather than rows or columns.
     */
    public void setCellSelectionEnabled(boolean flag) {
        cellSelectionEnabled = flag;
    }

    /**
     * Returns true if simultaneous row and column selections are allowed
     *
     * @return true if simultaneous row and column selections are allowed
     * @see #setCellSelectionEnabled
     */
    public boolean getCellSelectionEnabled() {
        return cellSelectionEnabled;
    }

    /**
     *  Select all rows, columns and cells in the table. 
     */
    public void selectAll() {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
        setRowSelectionInterval(0, getRowCount()-1);
	setColumnSelectionInterval(0, getColumnCount()-1);
    }

    /**
     * Deselects all selected columns and rows.
     */
    public void clearSelection() {
        columnModel.getSelectionModel().clearSelection();
        selectionModel.clearSelection();
    }

    /**
     * Selects the rows from <i>index0</i> to <i>index1</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
    public void setRowSelectionInterval(int index0, int index1) {
        selectionModel.setSelectionInterval(index0, index1);
    }

    /**
     * Selects the columns from <i>index0</i> to <i>index1</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
    public void setColumnSelectionInterval(int index0, int index1) {
        columnModel.getSelectionModel().setSelectionInterval(index0, index1);
    }

    /**
     * Adds the rows from <i>index0</i> to <i>index0</i> inclusive to
     * the current selection.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
    public void addRowSelectionInterval(int index0, int index1) {
        selectionModel.addSelectionInterval(index0, index1);
    }

    /**
     * Adds the columns from <i>index0</i> to <i>index0</i> inclusive to
     * the current selection.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
    public void addColumnSelectionInterval(int index0, int index1) {
        columnModel.getSelectionModel().addSelectionInterval(index0, index1);
    }

    /**
     * Deselects the rows from <i>index0</i> to <i>index0</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
    public void removeRowSelectionInterval(int index0, int index1) {
        selectionModel.removeSelectionInterval(index0, index1);
    }

    /**
     * Deselects the columns from <i>index0</i> to <i>index0</i> inclusive.
     *
     * @param   index0 one end of the interval.
     * @param   index1 other end of the interval
     */
    public void removeColumnSelectionInterval(int index0, int index1) {
        columnModel.getSelectionModel().removeSelectionInterval(index0, index1);
    }

    /**
     * Returns the index of the last row selected or added to the selection.
     *
     * @return the index of the last row selected or added to the selection,
     *         (lead selection) or -1 if no row is selected.
     * @see #getSelectedRows
     */
    public int getSelectedRow() {
        if (selectionModel != null) {
            return selectionModel.getAnchorSelectionIndex();
        }
        return -1;
    }

    /**
     * Returns the index of the last column selected or added to the selection.
     *
     * @return the index of the last column selected or added to the selection,
     *         (lead selection) or -1 if no column is selected.
     * @see #getSelectedColumns
     */
    public int getSelectedColumn() {
        return columnModel.getSelectionModel().getAnchorSelectionIndex();
    }

    /**
     * Returns the indices of all selected rows.
     *
     * @return an array of ints containing the indices of all selected rows,
     *         or an empty array if no row is selected.
     * @see #getSelectedRow
     */
    public int[] getSelectedRows() {
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

    /**
     * Returns the indices of all selected columns.
     *
     * @return an array of ints containing the indices of all selected columns,
     *         or an empty array if no column is selected.
     * @see #getSelectedColumn
     */
    public int[] getSelectedColumns() {
        return columnModel.getSelectedColumns();
    }

    /**
     * Returns the number of selected rows.
     *
     * @return the number of selected rows, 0 if no columns are selected
     */
    public int getSelectedRowCount() {
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

    /**
     * Returns the number of selected columns.
     *
     * @return the number of selected columns, 0 if no columns are selected
     */
    public int getSelectedColumnCount() {
        return columnModel.getSelectedColumnCount();
    }

    /**
     * Returns true if the row at the specified index is selected
     *
     * @return true if the row at index <I>row</I> is selected, where 0 is the
     *              first row
     * @exception IllegalArgumentException      if <I>row</I> is not in the
     *                                          valid range
     */
    public boolean isRowSelected(int row) {
        if (selectionModel != null)
            return selectionModel.isSelectedIndex(row);
        return false;
    }

    /**
     * Returns true if the column at the specified index is selected
     *
     * @return true if the column at index <I>column</I> is selected, where
     *              0 is the first column
     * @exception IllegalArgumentException      if <I>column</I> is not in the
     *                                          valid range
     */
    public boolean isColumnSelected(int column) {
        return columnModel.getSelectionModel().isSelectedIndex(column);
    }

    /**
     * Returns true if the cell at the specified position is selected.
     *
     * @return true if the cell at index <I>(row, column)</I> is selected,
     *              where the first row and first column are at index 0
     * @exception IllegalArgumentException      if <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     */
    public boolean isCellSelected(int row, int column) {
        if (cellSelectionEnabled)
            return isRowSelected(row) && isColumnSelected(column);
        else
            return (getRowSelectionAllowed() && isRowSelected(row)) ||
                   (getColumnSelectionAllowed() && isColumnSelected(column));
    }

    /**
     * Returns the foreground color for selected cells.
     *
     * @return the Color object for the foreground property
     * @see #setSelectionForeground
     * @see #setSelectionBackground
     */
    public Color getSelectionForeground() {
        return selectionForeground;
    }

    /**
     * Set the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionForeground  the Color to use in the foreground
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
        Color oldValue = this.selectionForeground;
        this.selectionForeground = selectionForeground;
        firePropertyChange("selectionForeground", oldValue, selectionForeground);
    }

    /**
     * Returns the background color for selected cells.
     *
     * @return the Color used for the background of selected list items
     * @see #setSelectionBackground
     * @see #setSelectionForeground
     */
    public Color getSelectionBackground() {
        return selectionBackground;
    }

    /**
     * Set the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionBackground  the Color to use for the background
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
        Color oldValue = this.selectionBackground;
        this.selectionBackground = selectionBackground;
        firePropertyChange("selectionBackground", oldValue, selectionBackground);
    }

    /**
     * Returns the <B>TableColumn</B> object for the column in the table
     * whose identifier is equal to <I>identifier</I>, when compared using
     * <I>equals()</I>.
     *
     * @return  the TableColumn object with matching identifier
     * @exception IllegalArgumentException      if <I>identifier</I> is null or no TableColumn has this identifier
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
     * Return the index of the column in the model whose data is being displayed in
     * the column <I>viewColumnIndex</I> in the display. Returns <I>viewColumnIndex</I>
     * unchanged when <I>viewColumnIndex</I> is less than zero.
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
     * Return the index of the column in the view which is displaying the
     * data from the column <I>modelColumnIndex</I> in the model. Returns
     * -1 if this column is not being displayed. Returns <I>modelColumnIndex</I>
     * unchanged when <I>modelColumnIndex</I> is less than zero.
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
     * Returns the number of rows in the table.
     *
     * @see #getColumnCount
     */
    public int getRowCount() {
        return getModel().getRowCount();
    }

    /**
     * Returns the number of columns in the column model, note this may
     * be different to the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     */
    public int getColumnCount() {
        return getColumnModel().getColumnCount();
    }

    /**
     * Returns the name of the column at the specified view position.
     *
     * @return the name of the column at position <I>column</I> in the view
     *         where the first column is column 0.
     */
    public String getColumnName(int column) {
        return getModel().getColumnName(convertColumnIndexToModel(column));
    }

    /**
     * Returns the type of the column at the specified view position.
     *
     * @return the type of the column at position <I>column</I> in the view
     *         where the first column is column 0.
     */
    public Class getColumnClass(int column) {
        return getModel().getColumnClass(convertColumnIndexToModel(column));
    }

    /**
     * Returns the cell value at <I>row</I> and <I>column</I>.
     * <p>
     * <b>NOTE</b>: The column is specified in the table view's display
     *              order, and not in the TableModel's column order.  This is
     *              an important distinction because as the user rearranges
     *              the columns in the table, what is at column 2 changes.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     * @param   row             the row whose value is to be looked up
     * @param   column          the column whose value is to be looked up
     * @return  the Object at the specified cell
     */
    public Object getValueAt(int row, int column) {
        return getModel().getValueAt(row, convertColumnIndexToModel(column));
    }

    /**
     * Sets the value for the cell at <I>row</I> and <I>column</I>.
     * <I>aValue</I> is the new value.
     *
     * @param   aValue          the new value
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     * @see #getValueAt
     */
    public void setValueAt(Object aValue, int row, int column) {
        getModel().setValueAt(aValue, row, convertColumnIndexToModel(column));
    }

    /**
     * Returns true if the cell at <I>row</I> and <I>column</I>
     * is editable.  Otherwise, setValueAt() on the cell will not change
     * the value of that cell.
     *
     * @param   row      the row whose value is to be looked up
     * @param   column   the column whose value is to be looked up
     * @return  true if the cell is editable.
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int column) {
        return getModel().isCellEditable(row, convertColumnIndexToModel(column));
    }
//
// Adding and removing columns in the view
//

    /**
     *  Appends <I>aColumn</I> to the end of the array of columns held by
     *  the JTable's column model.
     *  If the header value of <I>aColumn</I> is <I>null</I>,
     *  sets the header value of <I>aColumn</I> to the name
     *  returned by <code>getModel().getColumnName()</code>.
     *  <p>
     *  To add a column to the JTable to display the <I>modelColumn</I>'th column of
     *  data in the model, with a given <I>width</I>,
     *  <I>cellRenderer</I> and <I>cellEditor</I> you can use:
     *  <pre>
     *
     *      addColumn(new TableColumn(modelColumn, width, cellRenderer, cellEditor));
     *
     *  </pre>
     *  [All of the other constructors in the TableColumn can be used in place of
     *  this one.] The model column is stored inside the TableColumn and is used during
     *  rendering and editing to locate the appropriate data values in the
     *  model. The model column does not change when columns are reordered
     *  in the view.
     *
     *  @param  aColumn         The <B>TableColumn</B> to be added
     *  @see    #removeColumn
     */
    public void addColumn(TableColumn aColumn) {
        int modelColumn = aColumn.getModelIndex();
        String columnName = getModel().getColumnName(modelColumn);
        if (aColumn.getHeaderValue() == null) {
            aColumn.setHeaderValue(columnName);
        }
        getColumnModel().addColumn(aColumn);
    }

    /**
     *  Removes <I>aColumn</I> from the JTable's array of columns.
     *  Note: this method does not remove the column of data from the
     *  model it just removes the TableColumn that was displaying it.
     *
     *  @param  aColumn         The <B>TableColumn</B> to be removed
     *  @see    #addColumn
     */
    public void removeColumn(TableColumn aColumn) {
        getColumnModel().removeColumn(aColumn);
    }

    /**
     * Moves the column <I>column</I> to the position currently occupied by the
     * column <I>targetColumn</I>.  The old column at <I>targetColumn</I> is
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
     * Returns the index of the column that <I>point</I> lies in, or -1 if it
     * lies outside the receiver's bounds.
     *
     * @return  the index of the column that <I>point</I> lies in, or -1 if it
     *          lies outside the receiver's bounds
     * @see     #rowAtPoint
     */
    public int columnAtPoint(Point point) {
        return getColumnModel().getColumnIndexAtX(point.x);
    }

    /**
     * Returns the index of the row that <I>point</I> lies in, or -1 if is
     * not in the range [0, getRowCount()-1].
     *
     * @return  the index of the row that <I>point</I> lies in, or -1 if it
     *          is not in the range [0, getRowCount()-1]
     * @see     #columnAtPoint
     */
    public int rowAtPoint(Point point) {
        int y = point.y;

 //       if (y < 0 || y >= getBounds().height) {
 //           return -1;
 //       }

        int rowHeight = getRowHeight();
        int rowSpacing = getIntercellSpacing().height;
        int totalRowHeight = rowHeight + rowSpacing;
        int result = y/totalRowHeight;
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
     * Returns a rectangle locating the cell that lies at the intersection of
     * <I>row</I> and <I>column</I>.   If <I>includeSpacing</I> is true then
     * the value returned includes the intercellSpacing margin.  If it is false,
     * then the returned rect is inset by half of intercellSpacing.
     * (This is the true frame of the cell)
     *
     * @param   row                             the row to compute
     * @param   column                          the column to compute
     * @param   includeSpacing                  if true, the rect returned will
     *                                          include the correct
     *                                          intercellSpacing
     * @return  the rectangle containing the cell at index
     *          <I>row</I>,<I>column</I>
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range.
     */
    public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        int index = 0;
        Rectangle cellFrame;
        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration enumeration = getColumnModel().getColumns();
        TableColumn aColumn;

        cellFrame = new Rectangle();
        cellFrame.height = getRowHeight() + rowMargin;
        cellFrame.y = row * cellFrame.height;

        while (enumeration.hasMoreElements()) {
            aColumn = (TableColumn)enumeration.nextElement();
            cellFrame.width = aColumn.getWidth() + columnMargin;

            if (index == column)
                break;

            cellFrame.x += cellFrame.width;
            index++;
        }

        if (!includeSpacing) {
            Dimension spacing = getIntercellSpacing();
            // This is not the same as grow(), it rounds differently.
            cellFrame.setBounds(cellFrame.x +      spacing.width/2,
                                cellFrame.y +      spacing.height/2,
                                cellFrame.width -  spacing.width,
                                cellFrame.height - spacing.height);
        }
        return cellFrame;
    }

    /**
     * Calls super.reshape(), and is overridden simply to detect changes in 
     * our bounds. After reshaping we resize the columns (similar to triggering 
     * a layout) to fit the new bounds for the component using sizeColumnsToFit(). 
     *
     * @see #sizeColumnsToFit(int)
     */
    public void reshape(int x, int y, int width, int height) {
	boolean widthChanged = (getWidth() != width); 
        super.reshape(x, y, width, height);
        if (widthChanged) {
	    sizeColumnsToFit(-1);
	}
    }


    /**
     * Sizes the table columns to fit the available space.
     * @deprecated As of Swing version 1.0.3,
     * replaced by <code>sizeColumnsToFit(int)</code>.
     * @see #sizeColumnsToFit(int)
     */
    public void sizeColumnsToFit(boolean lastColumnOnly) {
        int oldAutoResizeMode = autoResizeMode;
        setAutoResizeMode(lastColumnOnly ? AUTO_RESIZE_LAST_COLUMN
                                         : AUTO_RESIZE_ALL_COLUMNS);
        sizeColumnsToFit(-1);
        setAutoResizeMode(oldAutoResizeMode);
    }

    /**
     *       This method will resize one or more ot the columns in the table
     *       so that the total width of all of the JTable's columns will be
     *       equal to the width of the table. 
     * <p>
     *       When setBounds() is called on the JTable, often as 
     *       a result of resizing of an enclosing window -
     *       this method is called with <code>resizingColumn</code>
     *       set to -1. This means that resizing has taken place 'outside' the
     *       JTable and the change - or 'delta' - should be distributed to all
     *       of the columns regardless of the JTable's autoResizeMode mode.
     * <p>
     *       If the <code>resizingColumn</code> is not -1, it is one of 
     *       the columns in the table that has changed size rather than 
     *       the table itself. In this case the auto-resize modes govern 
     *       the way the extra (or defecit) space is distributed 
     *       amongst the availible columns. 
     * <p>
     *       The modes are:
     * <ul>
     * <li>  AUTO_RESIZE_OFF Don't automatically adjust the column's
     *       widths at all. Use a horizontal scrollbar to accomodate the
     *       columns when their sum exceeds the width of the Viewport.
     *       If the JTable is not enclosed in a JScrollPane this may 
     *       leave parts of the table invisible.
     * <li>  AUTO_RESIZE_NEXT_COLUMN: Use just the column after the
     *       resizing column. This results in the 'boundry' or divider
     *       between adjacent cells being independently adjustable.
     * <li>  AUTO_RESIZE_SUBSEQUENT_COLUMNS: Use all columns after the
     *       one being adjusted to absorb the changes.
     * <li>  AUTO_RESIZE_LAST_COLUMN Only ever automatically adjust the
     *       size of the last column. If the bounds of the last column
     *       prevent the desired size from being allocated, set the
     *       width of the last column to the appropriate limit and make
     *       no further adjustments.
     * <li>  AUTO_RESIZE_ALL_COLUMNS Spread the delta amongst all the columns
     *       in the JTable, including the one that is being adjusted.
     * </ul>
     * <p>
     * Note: When the JTable makes adjustments to the widths of the
     *   columns it respects their minimum and maximum values absolutely.
     *   It is therefore possible that, even after this method is called,
     *   the total width of the columns is still not equal to the width
     *   of the table. When this happens the JTable does not put itself
     *   in AUTO_RESIZE_OFF mode to bring up a ScrollBar, or break other
     *   commitments of its current auto-resize mode - instead it
     *   allows its bounds to be set larger (or smaller) than the total of the
     *   column minima or maxima, meaning, either that there
     *   will not be enough room to display all of the columns, or that the
     *   columns will not fill the JTable's bounds. These respectively, result
     *   in the clipping of some columns or an area being painted in the
     *   JTable's background color during painting.
     * <p>
     *   The mechanism for distributing the delta amongst the availible 
     *   columns is provided in a private method in the JTable class: 
     * <pre>
     *   adjustSizes(long targetSize, final Resizable3 r, boolean inverse)
     * </pre>
     *   an explanation of which is provided below. Resizable3 is a private 
     *   interface that allows any data structure containing a collection 
     *   of elements with a size, preferredSize, maximumSize and minimumSize 
     *   to have its elements manipulated by the algorithm. 
     * <p>
     * <H3> Distributing the delta </H3>
     * <p>
     * <H4> Overview </H4>
     * <P>
     * Call 'DELTA' the difference between the targetSize and the 
     * sum of the preferred sizes of the elements in r. The individual 
     * sizes are calculated by taking the original preferred 
     * sizes and adding a share of the DELTA - that share being based on 
     * how far each preferred size is from its limiting bound (minimum or 
     * maximum).
     * <p>
     * <H4>Definition</H4>
     * <P>
     * Call the individual constraints min[i], max[i] and pref[i]. 
     * <p>
     * Call their respective sums: MIN, MAX and PREF. 
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
     *                           DELTA
     *         delta[i] =     ------------ * (pref[i] - min[i])
     *                        (PREF - MIN)
     * </PRE>
     * If (DELTA > 0) we are in expand mode where: 
     * <p>
     * <PRE>
     *                           DELTA
     *         delta[i] =     ------------ * (max[i] - pref[i])
     *                        (MAX - PREF)
     * </PRE>
     * <P>
     * The overall effect is that the total size moves that same percentage, 
     * k, towards the total minimum or maximum and that percentage guarentees 
     * accomodation of the required space, DELTA.
     *
     * <H4>Details</H4>
     * <P>
     * Naive evaluation of the formulae presented here would be subject to 
     * the aggregated rounding errors caused by doing this operation in finite 
     * precision (using ints). To deal with this, the muliplying factor above, 
     * is constantly recalculated and this takes account of the rounding errors in
     * the previous iterations. The result is an algorithm which produces 
     * a set of integers whose values exactly sum to the supplied 
     * <code>targetSize</code>, and does so by spreading the rounding 
     * errors evenly over the given elements.
     *
     * <H4>When the MAX and MIN bounds are hit</H4>
     * <P>
     * When <code>targetSize</code> is outside the [MIN, MAX] range, the algorithm 
     * sets all sizes to either their appropriate limiting value (maximum or minimum).
     *
     * @param resizingColumn    The column whose resizing made this adjustment
     *                          necessary or -1 if there is no such column.
     * @see TableColumn#setWidth
     */
    public void sizeColumnsToFit(int resizingColumn) { 
        if (resizingColumn == -1) {
            setWidthsFromPreferredWidths(false); 
	}
	else { 
	    if (autoResizeMode == AUTO_RESIZE_OFF) {
	        Enumeration enumeration = getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
		    TableColumn aColumn = (TableColumn)enumeration.nextElement();
		    aColumn.setPreferredWidth(aColumn.getWidth()); 
		}
	    } 
	    else {
                int delta = getWidth() - getColumnModel().getTotalColumnWidth(); 
	        accommodateDelta(resizingColumn, delta);         
	    }
	}
    }

    private void setWidthsFromPreferredWidths(final boolean inverse) {
        int columnCount = getColumnCount(); 
	int totalIntercellSpacing = columnCount * getColumnModel().getColumnMargin(); 
        int totalWidth     = getWidth()               - totalIntercellSpacing; 
	int totalPreferred = getPreferredSize().width - totalIntercellSpacing; 
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
	    case AUTO_RESIZE_NEXT_COLUMN:        from = from + 1; to = from + 1; break;
	    case AUTO_RESIZE_SUBSEQUENT_COLUMNS: from = from + 1; to = columnCount; break;
	    case AUTO_RESIZE_LAST_COLUMN:        from = columnCount - 1; to = from + 1; break;
	    case AUTO_RESIZE_ALL_COLUMNS:        from = 0; to = columnCount; break;
	    default:                             return;
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
	    // finishes early due to a series of 'fixed' entries at the end. 
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
     * Overrides JComponent's setToolTipText method to allow use of the
     * renderer's tips (if the renderer has text set).
     * <p>
     * NOTE: For JTable to properly display tooltips of its renderers
     *       JTable must be a registered component with the ToolTipManager.
     *       This is done automatically in initializeLocalVars(), but
     *       if at a later point JTable is told setToolTipText(null)
     *       it will unregister the table component, and no tips from
     *       renderers will display anymore.
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
     * Programmatically starts editing the cell at <I>row</I> and
     * <I>column</I>, if the cell is editable.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     * @return  false if for any reason the cell cannot be edited.
     */
    public boolean editCellAt(int row, int column) {
        return editCellAt(row, column, null);
    }

    /**
     * Programmatically starts editing the cell at <I>row</I> and
     * <I>column</I>, if the cell is editable.
     * To prevent the JTable from editing a particular table, column or
     * cell value, return false from the isCellEditable() method in the
     * TableModel interface.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @param   e                               event to pass into
     *                                          shouldSelectCell
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     * @return  false if for any reason the cell cannot be edited.
     */
    public boolean editCellAt(int row, int column, EventObject e){
        if (isEditing()) {
            // Try to stop the current editor
            if (cellEditor != null) {
                boolean stopped = cellEditor.stopCellEditing();
                if (!stopped)
                    return false;       // The current editor not resigning
            }
        }

        if (!isCellEditable(row, column))
            return false;

        TableCellEditor editor = getCellEditor(row, column);
        if (editor != null) {
            // prepare editor - size it then added it to the table
            editorComp = prepareEditor(editor, row, column);

            if (editor.isCellEditable(e)) {
                editorComp.setBounds(getCellRect(row, column, false));
                this.add(editorComp);
                editorComp.validate();

                setCellEditor(editor);
                setEditingRow(row);
                setEditingColumn(column);
                editor.addCellEditorListener(this);

                return true;
            }
        }
        return false;
    }

    /**
     * Returns  true is the table is editing a cell.
     *
     * @return  true is the table is editing a cell
     * @see     #editingColumn
     * @see     #editingRow
     */
    public boolean isEditing() {
        return (cellEditor == null)? false : true;
    }

    /**
     * If the receiver is currently editing this will return the Component
     * that was returned from the CellEditor.
     *
     * @return  Component handling editing session
     */
    public Component getEditorComponent() {
        return editorComp;
    }

    /**
     * This returns the index of the editing column.
     *
     * @return  the index of the column being edited
     * @see #editingRow
     */
    public int getEditingColumn() {
        return editingColumn;
    }

    /**
     * Returns the index of the editing row.
     *
     * @return  the index of the row being edited
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
     * @return the TableUI object that renders this component
     */
    public TableUI getUI() {
        return (TableUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the TableUI L&F object
     * @see UIDefaults#getUI
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
     * Notification from the UIManager that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * UIManager.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        // Update the UIs of the cell renderers, cell editors and header renderers.
        TableColumnModel cm = getColumnModel();
        for(int column = 0; column < cm.getColumnCount(); column++) {
            TableColumn aColumn = cm.getColumn(column);
           // updateSubComponentUI(aColumn.getCellRenderer());
            updateSubComponentUI(aColumn.getCellEditor());
           // updateSubComponentUI(aColumn.getHeaderRenderer());
        }

        // Update the UIs of all the default renderers.
        /*
        Enumeration defaultRenderers = defaultRenderersByColumnClass.elements();
        while (defaultRenderers.hasMoreElements()) {
            updateSubComponentUI(defaultRenderers.nextElement());
        }
        */

        // Update the UIs of all the default editors.
        Enumeration defaultEditors = defaultEditorsByColumnClass.elements();
        while (defaultEditors.hasMoreElements()) {
            updateSubComponentUI(defaultEditors.nextElement());
        }

        setUI((TableUI)UIManager.getUI(this));
        resizeAndRepaint();
        invalidate();//PENDING
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "TableUI"
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
     * Sets the data model for this table to <I>newModel</I> and registers
     * with for listner notifications from the new data model.
     *
     * @param   newModel        the new data source for this table
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see     #getModel
     * @beaninfo
     * description: The model that is the source of the data for this view.
     */
    public void setModel(TableModel newModel) {
        TableModel oldModel = dataModel;

        if (newModel == null)
            throw new IllegalArgumentException("Cannot set a null TableModel");

        if (newModel != oldModel) {
            if (oldModel != null)
                oldModel.removeTableModelListener(this);
            dataModel = newModel;
            newModel.addTableModelListener(this);
            // If this method is called from the JTable constructor,
            // the column model will be null. In this case we can't use
            // the usual methods to update the internal state. In all other
            // cases, use the usual tableChanged() method to reconfigure
            // the JTable for the new model.
            if (getColumnModel() != null) {
                tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
            }
	    firePropertyChange("model", oldModel, newModel);
        }
    }

    /**
     * Returns the <B>TableModel</B> that provides the data displayed by
     * the receiver.
     *
     * @return  the object that provides the data displayed by the receiver
     * @see     #setModel
     */
    public TableModel getModel() {
        return dataModel;
    }

    /**
     * Sets the column model for this table to <I>newModel</I> and registers
     * with for listner notifications from the new column model. Also sets
     * the column model of the JTableHeader to <I>newModel</I>.
     *
     * @param   newModel        the new data source for this table
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see     #getColumnModel
     * @beaninfo
     * description: The object governing the way columns appear in the view.
     */
    public void setColumnModel(TableColumnModel newModel) {
        if (newModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }

        TableColumnModel oldModel = columnModel;
        if (newModel != oldModel) {
            if (oldModel != null)
                oldModel.removeColumnModelListener(this);

            columnModel = newModel;
            newModel.addColumnModelListener(this);


            // Set the column model of the header as well.
            if (tableHeader != null) {
                tableHeader.setColumnModel(newModel);
            }

	    firePropertyChange("columnModel", oldModel, newModel);
            resizeAndRepaint();
        }
    }

    /**
     * Returns the <B>TableColumnModel</B> that contains all column inforamtion
     * of this table.
     *
     * @return  the object that provides the column state of the table
     * @see     #setColumnModel
     */
    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    /**
     * Sets the row selection model for this table to <I>newModel</I>
     * and registers with for listner notifications from the new selection model.
     *
     * @param   newModel        the new selection model
     * @exception IllegalArgumentException      if <I>newModel</I> is null
     * @see     #getSelectionModel
     * @beaninfo
     * description: The selection model for rows.
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
     * Returns the <B>ListSelectionModel</B> that is used to maintain row
     * selection state.
     *
     * @return  the object that provides row selection state.  Or <B>null</B>
     *          if row selection is not allowed.
     * @see     #setSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

//
// Implementing TableModelListener interface
//

    /**
     * The TableModelEvent should be constructed in the co-ordinate system
     * of the model, the appropriate mapping to the view co-ordinate system
     * is performed by the JTable when it recieves the event.
     */
    public void tableChanged(TableModelEvent e) {
        if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            // The whole thing changed
            clearSelection();

            if (getAutoCreateColumnsFromModel())
                createDefaultColumnsFromModel();

            resizeAndRepaint();
            if (tableHeader != null) {
                tableHeader.resizeAndRepaint();
            }
            return;
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

        if (start == TableModelEvent.HEADER_ROW) {
            start = 0;
            end = Integer.MAX_VALUE;
        }

        int rowHeight = getRowHeight() + rowMargin;
        Rectangle dirtyRegion;
        if (modelColumn == TableModelEvent.ALL_COLUMNS) {
            // 1 or more rows changed
            dirtyRegion = new Rectangle(0, start * rowHeight,
                                        getColumnModel().getTotalColumnWidth(), 0);
        }
        else {
            // A cell or column of cells has changed.
            // Unlike the rest of the methods in the JTable, the TableModelEvent
            // uses the co-ordinate system of the model instead of the view.
            // This is the only place in the JTable where this "reverse mapping"
            // is used.
            int column = convertColumnIndexToView(modelColumn);
            dirtyRegion = getCellRect(start, column, false);
        }

        // Now adjust the height of the dirty region according to the value of "end".
        // Check for Integer.MAX_VALUE as this will cause an overflow.
        if (end != Integer.MAX_VALUE) {
            dirtyRegion.height = (end-start+1)*rowHeight;
            repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
        }
        // In fact, if the end is Integer.MAX_VALUE we need to revalidate anyway
        // because the scrollbar may need repainting.
        else {
            resizeAndRepaint();
        }
    }

    /*
     * Invoked when rows have been inserted into the table.
     *
     * @param e the TableModelEvent encapsulating the insertion
     */
    private void tableRowsInserted(TableModelEvent e) {
        int start = e.getFirstRow();
        int end = e.getLastRow();
        if (start < 0)
            start = 0;

        // 1 or more rows added, so we have to repaint from the first
        // new row to the end of the table.  (Everything shifts down)
        int rowHeight = getRowHeight() + rowMargin;
        Rectangle drawRect = new Rectangle(0, start * rowHeight,
                                        getColumnModel().getTotalColumnWidth(),
                                           (getRowCount()-start) * rowHeight);

        // Adjust the selection to account for the new rows
        if (selectionModel != null) {
            if (end < 0)
                end = getRowCount()-1;
            int length = end - start + 1;

            selectionModel.insertIndexInterval(start, length, true);
        }
        revalidate();
        // PENDING(philip) Find a way to stop revalidate calling repaint
        // repaint(drawRect);
    }

    /*
     * Invoked when rows have been removed from the table.
     *
     * @param e the TableModelEvent encapsulating the deletion
     */
    private void tableRowsDeleted(TableModelEvent e) {
        int start = e.getFirstRow();
        int end = e.getLastRow();
        if (start < 0)
            start = 0;

        int deletedCount = e.getLastRow() - end + 1;
        int previousRowCount = getRowCount() + deletedCount;
        // 1 or more rows added, so we have to repaint from the first
        // new row to the end of the table.  (Everything shifts up)
        int rowHeight = getRowHeight() + rowMargin;
        Rectangle drawRect = new Rectangle(0, start * rowHeight,
                                        getColumnModel().getTotalColumnWidth(),
                                        (previousRowCount - start) * rowHeight);

        // Adjust the selection to account for the new rows
        if (selectionModel != null) {
            if (end < 0)
                end = getRowCount()-1;

            selectionModel.removeIndexInterval(start, end);
        }
        revalidate();
        // PENDING(philip) Find a way to stop revalidate calling repaint
        // repaint(drawRect);
    }

//
// Implementing TableColumnModelListener interface
//

    /**
     * Tells listeners that a column was added to the model.
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
     * Tells listeners that a column was removed from the model.
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
     * Tells listeners that a column was repositioned.
     *
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
     * Tells listeners that a column was moved due to a margin change.
     *
     * @see TableColumnModelListener
     */
    public void columnMarginChanged(ChangeEvent e) {
        // If I'm currently editing, then I should stop editing
        if (isEditing()) {
            removeEditor();
        }
        resizeAndRepaint();
    }

    /**
     * Tells listeners that the selection model of the
     * TableColumnModel changed.
     *
     * @see TableColumnModelListener
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
            repaint();
        }
        Rectangle firstColumnRect = getCellRect(0, firstIndex, false);
        Rectangle lastColumnRect = getCellRect(getRowCount(), lastIndex, false);
        Rectangle dirtyRegion = firstColumnRect.union(lastColumnRect);
        // This marks this entire column as dirty but the painting system will
        // intersect this with the clip rect of the viewport and redraw only
        // the visible cells.
        repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
    }

//
// Implementing ListSelectionListener interface
//

    /**
     * Invoked when the selection changes -- repaints to show the new
     * selection.
     *
     * @see ListSelectionListener
     */
    public void valueChanged(ListSelectionEvent e) {
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
            repaint();
        }
        Rectangle firstRowRect = getCellRect(firstIndex, 0, false);
        Rectangle lastRowRect = getCellRect(lastIndex, getColumnCount(), false);
        Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
        // This marks this entire row as dirty but the painting system will
        // intersect this with the clip rect of the viewport and redraw only
        // the visible cells.
        repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
    }

//
// Implementing the CellEditorListener interface
//

    /**
     * Invoked when editing is finished. The changes are saved, the
     * editor object is discarded, and the cell is rendered once again.
     *
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
     *
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
     * @param size  a Dimension object specifying the preferredSize of a
     *              JViewport whose view is this table
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
     * @return a Dimension object containing the preferredSize of the JViewport
     *         which displays this table
     * @see Scrollable#getPreferredScrollableViewportSize
     */
    public Dimension getPreferredScrollableViewportSize() {
        return preferredViewportSize;
    }

    /**
     * Returns the scroll increment that completely exposes one new row
     * or column (depending on the orientation).
     * <p>
     * This method is called each time the user requests a unit scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @see Scrollable#getScrollableUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
                                          int direction) {
        // PENDING(alan): do something smarter
        if (orientation == SwingConstants.HORIZONTAL) {
            return 100;
        }
        return rowHeight + rowMargin;
    }

    /**
     * Returns The visibleRect.height or visibleRect.width, depending on the
     * table's orientation.
     *
     * @return The visibleRect.height or visibleRect.width per the orientation.
     * @see Scrollable#getScrollableBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation,
                                           int direction) {
        return (orientation == SwingConstants.VERTICAL) ? visibleRect.height :
            visibleRect.width;
    }

    /**
     * Returns false to indicate that the width of the viewport does not
     * determine the width of the table.
     *
     * @return false
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

    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer
    {
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

    protected void createDefaultRenderers() {
        defaultRenderersByColumnClass = new Hashtable();

        // Objects
        DefaultTableCellRenderer label = new DefaultTableCellRenderer();
        setDefaultRenderer(Object.class, label);

	// Numbers
        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer() { 
	    NumberFormat formatter = NumberFormat.getInstance(); 
            public void setValue(Object value) { 
		setText((value == null) ? "" : formatter.format(value)); 
	    }
        };
        numberRenderer.setHorizontalAlignment(JLabel.RIGHT);
        setDefaultRenderer(Number.class, numberRenderer);

	// Dates
        DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer() {
	    DateFormat formatter = DateFormat.getDateInstance(); 
            public void setValue(Object value) { 
	        setText((value == null) ? "" : formatter.format(value)); }
        };
        dateRenderer.setHorizontalAlignment(JLabel.RIGHT);
	setDefaultRenderer(Date.class, dateRenderer); 

        // Icons
        DefaultTableCellRenderer centeredLabel = new DefaultTableCellRenderer() {
            public void setValue(Object value) { setIcon((Icon)value); }
        };
        centeredLabel.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(ImageIcon.class, centeredLabel);

        // Booleans
/*      DefaultTableCellRenderer booleanRenderer = new DefaultTableCellRenderer() {
            Icon trueIcon = UIManager.getIcon("CheckBox.icon");
            public void setValue(Object value) {
                setIcon((value != null && ((Boolean)value).booleanValue()) ? trueIcon : null);
            }
        };
        booleanRenderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(Boolean.class, booleanRenderer);
*/
        CheckBoxRenderer booleanRenderer = new CheckBoxRenderer();
        booleanRenderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(Boolean.class, booleanRenderer);
    }

    /**
     * Creates default cell editors for Objects, numbers, and boolean values.
     */
    protected void createDefaultEditors() {
        defaultEditorsByColumnClass = new Hashtable();

        // Objects
        JTextField textField = new JTextField();
        textField.setBorder(new LineBorder(Color.black));
        setDefaultEditor(Object.class, new DefaultCellEditor(textField));

        // Numbers
        JTextField rightAlignedTextField = new JTextField();
        rightAlignedTextField.setHorizontalAlignment(JTextField.RIGHT);
        rightAlignedTextField.setBorder(new LineBorder(Color.black));
        setDefaultEditor(Number.class, new DefaultCellEditor(rightAlignedTextField));

        // Booleans
        JCheckBox centeredCheckBox = new JCheckBox();
        centeredCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
        setDefaultEditor(Boolean.class, new DefaultCellEditor(centeredCheckBox));
    }

    /**
     * Initializes table properties to their default values.
     */
    protected void initializeLocalVars() {
        setOpaque(true);
        createDefaultRenderers();
        createDefaultEditors();

        setTableHeader(createDefaultTableHeader());

        setShowGrid(true);
        setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        setRowHeight(16);
        rowMargin = 1;
        setRowSelectionAllowed(true);
        setCellSelectionEnabled(false);
        cellEditor = null;
        editingColumn = editingRow = -1;
        preferredViewportSize = new Dimension(450,400);

        // I'm registered to do tool tips so we can draw tips for the renderers
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this);

        setAutoscrolls(true);
    }

    /**
     * Returns the default table model object which is
     * a DefaultTableModel.  Subclass can override this
     * method to return a different table model object.
     *
     * @return the default table model object
     */
    protected TableModel createDefaultDataModel() {
        return new DefaultTableModel();
    }

    /**
     * Returns the default column model object which is
     * a DefaultTableColumnModel.  Subclass can override this
     * method to return a different column model object
     *
     * @return the default column model object
     */
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModel();
    }

    /**
     * Returns the default selection model object which is
     * a DefaultListSelectionModel.  Subclass can override this
     * method to return a different selection model object.
     *
     * @return the default selection model object
     */
    protected ListSelectionModel createDefaultSelectionModel() {
        return new DefaultListSelectionModel();
    }

    /**
     * Returns the default table header object which is
     * a JTableHeader.  Subclass can override this
     * method to return a different table header object
     *
     * @return the default table header object
     */
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel);
    }

    /**
     * Equivalent to revalidate() followed by repaint().
     */
    protected void resizeAndRepaint() {
        revalidate();
        repaint(); 
    }

    /**
     * Return the cellEditor.
     *
     * @return the TableCellEditor that does the editing
     * @see #cellEditor
     */
    public TableCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * Set the cellEditor variable.
     *
     * @param anEditor  the TableCellEditor that does the editing
     * @see #cellEditor
     */
    public void setCellEditor(TableCellEditor anEditor) {
	TableCellEditor oldEditor = cellEditor;
        cellEditor = anEditor;
	firePropertyChange("tableCellEditor", oldEditor, anEditor);
    }

    /**
     * Set the editingColumn variable.
     *
     * @see #editingColumn
     */
    public void setEditingColumn(int aColumn) {
        editingColumn = aColumn;
    }

    /**
     * Set the editingRow variable.
     *
     * @see #editingRow
     */
    public void setEditingRow(int aRow) {
        editingRow = aRow;
    }

    /**
     * Return an appropriate renderer for the cell specified by this this row and
     * column. If the TableColumn for this column has a non-null renderer, return that.
     * If not, find the class of the data in this column (using getColumnClass())
     * and return the default renderer for this type of data.
     *
     * @param row       the row of the cell to render, where 0 is the first
     * @param column    the column of the cell to render, where 0 is the first
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
     * Prepares the specified renderer with an appropriate value
     * from the dataModel, and an appropriate selection value from
     * the selection models.
     *
     * @param renderer  the TableCellRenderer to prepare
     * @param row       the row of the cell to render, where 0 is the first
     * @param column    the column of the cell to render, where 0 is the first
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Object value = getValueAt(row, column);
	boolean isSelected = isCellSelected(row, column);
	boolean rowIsAnchor = (getSelectedRow() == row);
	boolean columnIsAnchor = (getSelectedColumn() == column);
	boolean hasFocus = (rowIsAnchor && columnIsAnchor) && hasFocus();

	return renderer.getTableCellRendererComponent(this, value,
	                                              isSelected, hasFocus,
	                                              row, column);
    }

    /**
     * Return an appropriate editor for the cell specified by this this row and
     * column. If the TableColumn for this column has a non-null editor, return that.
     * If not, find the class of the data in this column (using getColumnClass())
     * and return the default editor for this type of data.
     *
     * @param row       the row of the cell to edit, where 0 is the first
     * @param column    the column of the cell to edit, where 0 is the first
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
     * Prepares the specified editor using the value at the specified cell.
     *
     * @param editor  the TableCellEditor to set up
     * @param row     the row of the cell to edit, where 0 is the first
     * @param column  the column of the cell to edit, where 0 is the first
     */
    public Component prepareEditor(TableCellEditor editor, int row, int column) {
        Object value = getValueAt(row, column);
        boolean isSelected = isCellSelected(row, column);
        Component comp = editor.getTableCellEditorComponent(this, value, isSelected,
                                                  row, column);
        if((comp != null) && (comp.getFont() == null)) {
            comp.setFont(getFont());
        }
        return comp;
    }

    /**
     * Discard the editor object and return the real estate it used to
     * cell rendering.
     */
    public void removeEditor() {
        TableCellEditor editor = getCellEditor();
        if(editor != null) {
            editor.removeCellEditorListener(this);

            // PENDING(alan): This is a temp work around for a JComboBox bug
            if (editorComp instanceof JComboBox) {
                ((JComboBox)editorComp).hidePopup();
            }

            remove(editorComp);

            if (!(editorComp instanceof JComponent) ||
                 ((JComponent)editorComp).hasFocus()) {
                requestFocus();
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

    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException
    {
        s.defaultReadObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
        createDefaultRenderers();
        createDefaultEditors();
    }


    /**
     * Returns a string representation of this JTable. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JTable.
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

    /**
     * We override this method, whose implementation in JComponent 
     * returns false, to return true. This allows us to give a special 
     * meaning to TAB and SHIFT-TAB in the JTable. 
     */
    public boolean isManagingFocus() {
        return true;
    }    

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJTable();
        }
        return accessibleContext;
    }

    //
    // *** should also implement AccessibleSelction?
    // *** and what's up with keyboard navigation/manipulation?
    //
    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJTable extends AccessibleJComponent
    implements AccessibleSelection, ListSelectionListener, TableModelListener,
    TableColumnModelListener, CellEditorListener, PropertyChangeListener  {

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
		    TableColumnModel tcm = (TableColumnModel) oldValue;
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

        /**
         * Track changes to the table contents
         */
        public void tableChanged(TableModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Track changes to the table contents (row insertions)
         */
        public void tableRowsInserted(TableModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Track changes to the table contents (row deletions)
         */
        public void tableRowsDeleted(TableModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Track changes to the table contents (column insertions)
         */
        public void columnAdded(TableColumnModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Track changes to the table contents (column deletions)
         */
        public void columnRemoved(TableColumnModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
        }

        /**
         * Track changes of a column repositioning.
         *
         * @see TableColumnModelListener
         */
        public void columnMoved(TableColumnModelEvent e) {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              null, null);
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
                               new Boolean(false), new Boolean(true));

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
         * Get the AccessibleSelection associated with this object if one
         * exists.  Otherwise return null.
         *
         * @return the AccessibleSelection, or null
         */
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TABLE;
        }

        /**
         * Returns the Accessible child, if one exists, contained at the local
         * coordinate Point.
         *
         * @param p The point defining the top-left corner of the Accessible,
         * given in the coordinate space of the object's parent.
         * @return the Accessible, if it exists, at the specified location;
         * else null
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
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return (JTable.this.getColumnCount() * JTable.this.getRowCount());
        }

        /**
         * Return the nth Accessible child of the object.
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
         * Returns the number of Accessible children currently selected.
         * If no children are selected, the return value will be 0.
         *
         * @return the number of items currently selected.
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
         * Returns an Accessible representing the specified selected child
         * in the object.  If there isn't a selection, or there are
         * fewer children selected than the integer passed in, the return
         * value will be null.
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
         * @return true if the current child of this object is selected
         * @param i the zero-based index of the child in this Accessible object.
         * @see AccessibleContext#getAccessibleChild
         */
        public boolean isAccessibleChildSelected(int i) {
            int column = getAccessibleColumnAtIndex(i);
            int row = getAccessibleRowAtIndex(i);
            return JTable.this.isCellSelected(row, column);
        }

        /**
         * Adds the specified Accessible child of the object to the object's
         * selection.  If the object supports multiple selections,
         * the specified child is added to any existing selection, otherwise
         * it replaces any existing selection in the object.  If the
         * specified child is already selected, this method has no effect.
         *
         * This method only works on JTables which have individual cell
         * selection enabled.
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
         *
         * This method only works on JTables which have individual cell
         * selection enabled.
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
         * if the JTable supports multiple selections, and if individual
         * cell selection is enabled.
         */
        public void selectAllAccessibleSelection() {
            if (JTable.this.cellSelectionEnabled) {
                JTable.this.selectAll();
            }
        }

//        /**
//         * Get the AccessibleTable associated with this object if one
//         * exists.  Otherwise return null.
//         */
//        public AccessibleTable getAccessibleTable() {
//            return this;
//        }


    // AccessibleTable methods

        /*
         * Returns the total number of rows in the table
         *
         * @return the total number of rows in the table
         */
        private int getAccessibleRowCount() {
            return JTable.this.getRowCount();
        }

        /*
         * Returns the total number of columns in the table
         *
         * @return the total number of columns in the table
         */
        private int getAccessibleColumnCount() {
            return JTable.this.getColumnCount();
        }

        /*
         * Returns the row at a given index into the table
         *
         * @param i zero-based index into the table
         * @return the row at a given index
         */
        private int getAccessibleRowAtIndex(int i) {
            return (i / getAccessibleColumnCount());
        }

        /*
         * Returns the column at a given index into the table
         *
         * @param i zero-based index into the table
         * @return the column at a given index
         */
        private int getAccessibleColumnAtIndex(int i) {
            return (i % getAccessibleColumnCount());
        }

        /*
         * Returns the index at a given (row, column) in the table
         *
         * @param r zero-based row of the table
         * @param c zero-based column of the table
         * @return the index into the table
         */
        private int getAccessibleIndexAt(int r, int c) {
            return ((r * getAccessibleColumnCount()) + c);
        }

        /*
         * Returns the Accessible at a given (row, column) in the table
         *
         * @param r zero-based row of the table
         * @param c zero-based column of the table
         * @return the Accessible at the specified (row, column)
         */
        private Accessible getAccessibleAt(int r, int c) {
            return getAccessibleChild((r * getAccessibleColumnCount()) + c);
        }

        /*
         * Return the Accessible representing the row header, if
         * there is one (may be null).
         *
         * @param row zero-based row of the table
         * @return the Accessible header of the row
         */
        private Accessible getAccessibleRowHeader(int row) {
            return null;
        }

        /*
         * Return the Accessible representing the column header, if
         * there is one (may be null)
         *
         * @param column zero-based column of the table
         * @return the Accessible header of the column
         */
        private Accessible getAccessibleColumnHeader(int column) {
            JTableHeader header = JTable.this.getTableHeader();
            AccessibleContext ac = header.getAccessibleContext();
            if (ac != null) {
                return ac.getAccessibleChild(column);
            } else {
                return null;
            }
        }


        /**
         * The class used to obtain the AccessibleRole for a cell.
         */
        protected class AccessibleJTableCell extends AccessibleContext
            implements Accessible, AccessibleComponent {

            private JTable parent;
            private int row;
            private int column;
            private int index;

            /**
             *  Constructs an AccessiblJTableHeaaderEntry
             */
            public AccessibleJTableCell(JTable t, int r, int c, int i) {
                parent = t;
                row = r;
                column = c;
                index = i;
                this.setAccessibleParent(parent);
            }

            /**
             * Get the AccessibleContext associated with this
             *
             * @return the AccessibleContext of this JComponent
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
             * Get the accessible name of this object.
             *
             * @return the localized name of the object; null if this
             * object does not have a name
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
                    return parent.getValueAt(row, column).toString();
                }
            }

            /**
             * Set the localized accessible name of this object.
             *
             * @param s the new localized name of the object.
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
            // *** should check toolip text for desc. (needs MouseEvent)
            //
            /**
             * Get the accessible description of this object.
             *
             * @return the localized description of the object; null if
             * this object does not have a description
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
             * Set the accessible description of this object.
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
             * Get the role of this object.
             *
             * @return an instance of AccessibleRole describing the role of the object
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
             * Get the state set of this object.
             *
             * @return an instance of AccessibleStateSet containing the
             * current state set of the object
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
             * Get the Accessible parent of this object.
             *
             * @return the Accessible parent of this object; null if this
             * object does not have an Accessible parent
             */
            public Accessible getAccessibleParent() {
                return parent;
            }

            /**
             * Get the index of this object in its accessible parent.
             *
             * @return the index of this object in its parent; -1 if this
             * object does not have an accessible parent.
             * @see #getAccessibleParent
             */
            public int getAccessibleIndexInParent() {
                return index;
            }

            /**
             * Returns the number of accessible children in the object.
             *
             * @return the number of accessible children in the object.
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
             * Return the specified Accessible child of the object.
             *
             * @param i zero-based index of child
             * @return the Accessible child of the object
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
             * Gets the locale of the component. If the component does not have a
             * locale, then the locale of its parent is returned.
             *
             * @return This component's locale. If this component does not have a locale, the locale of its parent is returned.
             * @exception IllegalComponentStateException
             * If the Component does not have its own locale and has not yet been added to a containment hierarchy such that the locale can be
             * determined from the containing parent.
             * @see setLocale
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
             * Add a PropertyChangeListener to the listener list.
             * The listener is registered for all properties.
             *
             * @param listener  The PropertyChangeListener to be added
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
             * Remove a PropertyChangeListener from the listener list.
             * This removes a PropertyChangeListener that was registered
             * for all properties.
             *
             * @param listener  The PropertyChangeListener to be removed
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
             * Get the AccessibleAction associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleAction, or null
             */
            public AccessibleAction getAccessibleAction() {
                return getCurrentAccessibleContext().getAccessibleAction();
            }

            /**
             * Get the AccessibleComponent associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleComponent, or null
             */
            public AccessibleComponent getAccessibleComponent() {
                return this; // to override getBounds()
            }

            /**
             * Get the AccessibleSelection associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleSelection, or null
             */
            public AccessibleSelection getAccessibleSelection() {
                return getCurrentAccessibleContext().getAccessibleSelection();
            }

            /**
             * Get the AccessibleText associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleText, or null
             */
            public AccessibleText getAccessibleText() {
                return getCurrentAccessibleContext().getAccessibleText();
            }

            /**
             * Get the AccessibleValue associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleValue, or null
             */
            public AccessibleValue getAccessibleValue() {
                return getCurrentAccessibleContext().getAccessibleValue();
            }


        // AccessibleComponent methods

            /**
             * Get the background color of this object.
             *
             * @return the background color, if supported, of the object;
             * otherwise, null
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
             * Set the background color of this object.
             *
             * @param c the new Color for the background
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
             * Get the foreground color of this object.
             *
             * @return the foreground color, if supported, of the object;
             * otherwise, null
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
             * Set the foreground color of this object.
             *
             * @param c the new Color for the foreground
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
             * Get the Cursor of this object.
             *
             * @return the Cursor, if supported, of the object; otherwise, null
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
             * Set the Cursor of this object.
             *
             * @param c the new Cursor for the object
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
             * Get the Font of this object.
             *
             * @return the Font,if supported, for the object; otherwise, null
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
             * Set the Font of this object.
             *
             * @param f the new Font for the object
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
             * Get the FontMetrics of this object.
             *
             * @param f the Font
             * @return the FontMetrics, if supported, the object; otherwise, null
             * @see getFont
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
             * Determine if the object is enabled.
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
             * Set the enabled state of the object.
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
             * Determine if the object is visible.  Note: this means that the
             * object intends to be visible; however, it may not in fact be
             * showing on the screen because one of the objects that this object
             * is contained by is not visible.  To determine if an object is
             * showing on the screen, use isShowing().
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
             * Set the visible state of the object.
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
             * Determine if the object is showing.  This is determined by checking
             * the visibility of the object and ancestors of the object.  Note: this
             * will return true even if the object is obscured by another (for example,
             * it happens to be underneath a menu that was pulled down).
             *
             * @return true if object is showing; otherwise, false
             */
            public boolean isShowing() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isShowing();
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
             * Checks whether the specified point is within this object's bounds,
             * where the point's x and y coordinates are defined to be relative to the
             * coordinate system of the object.
             *
             * @param p the Point relative to the coordinate system of the object
             * @return true if object contains Point; otherwise false
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
             * @return location of object on screen -- can be null if this object
             * is not on the screen
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
             * Gets the location of the object relative to the parent in the form
             * of a point specifying the object's top-left corner in the screen's
             * coordinate space.
             *
             * @return An instance of Point representing the top-left corner of the
             * objects's bounds in the coordinate space of the screen; null if
             * this object or its parent are not on the screen
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
