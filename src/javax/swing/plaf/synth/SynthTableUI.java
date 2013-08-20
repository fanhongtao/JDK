/*
 * @(#)SynthTableUI.java	1.17 04/07/23
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TooManyListenersException;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.text.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.util.Date;
import java.util.EventObject;

import javax.swing.text.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import sun.swing.plaf.synth.SynthUI;

/**
 * SynthTableUI implementation
 *
 * @version 1.17, 07/23/04
 * @author Philip Milne
 */
class SynthTableUI extends BasicTableUI implements SynthUI,
        PropertyChangeListener {
//
// Instance Variables
//

    private SynthStyle style;

    private boolean useTableColors;
    private boolean useUIBorder;

    // TableCellRenderer installed on the JTable at the time we're installed,
    // cached so that we can reinstall them at uninstallUI time.
    private TableCellRenderer dateRenderer;
    private TableCellRenderer numberRenderer;
    private TableCellRenderer doubleRender;
    private TableCellRenderer floatRenderer;
    private TableCellRenderer iconRenderer;
    private TableCellRenderer imageIconRenderer;
    private TableCellRenderer booleanRenderer;
    private TableCellRenderer objectRenderer;

//
//  The installation/uninstall procedures and support
//

    public static ComponentUI createUI(JComponent c) {
        return new SynthTableUI();
    }

    /**
     * Initialize JTable properties, e.g. font, foreground, and background.
     * The font, foreground, and background properties are only set if their
     * current value is either null or a UIResource, other properties are set
     * if the current value is null.
     *
     * @see #installUI
     */
    protected void installDefaults() {
        dateRenderer = installRendererIfPossible(Date.class, null);
        numberRenderer = installRendererIfPossible(Number.class, null);
        doubleRender = installRendererIfPossible(Double.class, null);
        floatRenderer = installRendererIfPossible(Float.class, null);
        iconRenderer = installRendererIfPossible(Icon.class, null);
        imageIconRenderer = installRendererIfPossible(ImageIcon.class, null);
        booleanRenderer = installRendererIfPossible(Boolean.class,
                                 new SynthBooleanTableCellRenderer());
        objectRenderer = installRendererIfPossible(Object.class,
                                        new SynthTableCellRenderer());
        updateStyle(table);
    }

    private TableCellRenderer installRendererIfPossible(Class objectClass,
                                     TableCellRenderer renderer) {
        TableCellRenderer currentRenderer = table.getDefaultRenderer(
                                 objectClass);
        if (currentRenderer instanceof UIResource) {
            table.setDefaultRenderer(objectClass, renderer);
        }
        return currentRenderer;
    }

    private void updateStyle(JTable c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            context.setComponentState(ENABLED | SELECTED);

            Color sbg = table.getSelectionBackground();
            if (sbg == null || sbg instanceof UIResource) {
                table.setSelectionBackground(style.getColor(
                                        context, ColorType.TEXT_BACKGROUND));
            }

            Color sfg = table.getSelectionForeground();
            if (sfg == null || sfg instanceof UIResource) {
                table.setSelectionForeground(style.getColor(
                                  context, ColorType.TEXT_FOREGROUND));
            }

            context.setComponentState(ENABLED);

            Color gridColor = table.getGridColor();
            if (gridColor == null || gridColor instanceof UIResource) {
                gridColor = (Color)style.get(context, "Table.gridColor");
                if (gridColor == null) {
                    gridColor = style.getColor(context, ColorType.FOREGROUND);
                }
                table.setGridColor(gridColor);
            }

            useTableColors = style.getBoolean(context,
                                  "Table.rendererUseTableColors", true);
            useUIBorder = style.getBoolean(context,
                                  "Table.rendererUseUIBorder", true);

            Object rowHeight = style.get(context, "Table.rowHeight");
            if (rowHeight != null) {
                LookAndFeel.installProperty(table, "rowHeight", rowHeight);
            }
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }
        context.dispose();
    }

    /**
     * Attaches listeners to the JTable.
     */
    protected void installListeners() {
        super.installListeners();
        table.addPropertyChangeListener(this);
    }

    protected void uninstallDefaults() {
        table.setDefaultRenderer(Date.class, dateRenderer);
        table.setDefaultRenderer(Number.class, numberRenderer);
        table.setDefaultRenderer(Double.class, doubleRender);
        table.setDefaultRenderer(Float.class, floatRenderer);
        table.setDefaultRenderer(Icon.class, iconRenderer);
        table.setDefaultRenderer(ImageIcon.class, imageIconRenderer);
        table.setDefaultRenderer(Boolean.class, booleanRenderer);
        table.setDefaultRenderer(Object.class, objectRenderer);

	if (table.getTransferHandler() instanceof UIResource) {
	    table.setTransferHandler(null);
	}
        SynthContext context = getContext(table, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners() {
        table.removePropertyChangeListener(this);
        super.uninstallListeners();
    }

    //
    // SynthUI
    //
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

//
//  Paint methods and support
//

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintTableBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintTableBorder(context, g, x, y, w, h);
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
	if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
	    return;
	}
	Rectangle clip = g.getClipBounds();
	Point upperLeft = clip.getLocation();
	Point lowerRight = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);
        int rMin = table.rowAtPoint(upperLeft);
        int rMax = table.rowAtPoint(lowerRight);
        // This should never happen.
        if (rMin == -1) {
	    rMin = 0;
        }
        // If the table does not have enough rows to fill the view we'll get -1.
        // Replace this with the index of the last row.
        if (rMax == -1) {
	    rMax = table.getRowCount()-1;
        }

        boolean ltr = table.getComponentOrientation().isLeftToRight();
        int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight); 
        int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);        
        // This should never happen.
        if (cMin == -1) {
	    cMin = 0;
        }
	// If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
	    cMax = table.getColumnCount()-1;
        }

        // Paint the grid.
        paintGrid(context, g, rMin, rMax, cMin, cMax);

        // Paint the cells.
	paintCells(context, g, rMin, rMax, cMin, cMax);
    }

    /*
     * Paints the grid lines within <I>aRect</I>, using the grid
     * color set with <I>setGridColor</I>. Paints vertical lines
     * if <code>getShowVerticalLines()</code> returns true and paints
     * horizontal lines if <code>getShowHorizontalLines()</code>
     * returns true.
     */
    private void paintGrid(SynthContext context, Graphics g, int rMin,
                           int rMax, int cMin, int cMax) {
        g.setColor(table.getGridColor());

	Rectangle minCell = table.getCellRect(rMin, cMin, true);
	Rectangle maxCell = table.getCellRect(rMax, cMax, true);
        Rectangle damagedArea = minCell.union( maxCell );
        SynthGraphicsUtils synthG = context.getStyle().getGraphicsUtils(
                     context);

        if (table.getShowHorizontalLines()) {
	    int tableWidth = damagedArea.x + damagedArea.width;
	    int y = damagedArea.y;
	    for (int row = rMin; row <= rMax; row++) {
		y += table.getRowHeight(row);
                synthG.drawLine(context, "Table.grid",
                                g, damagedArea.x, y - 1, tableWidth - 1,y - 1);
	    }
	}
        if (table.getShowVerticalLines()) {
	    TableColumnModel cm = table.getColumnModel();
	    int tableHeight = damagedArea.y + damagedArea.height;
	    int x;
	    if (table.getComponentOrientation().isLeftToRight()) {
		x = damagedArea.x;
		for (int column = cMin; column <= cMax; column++) {
		    int w = cm.getColumn(column).getWidth();
		    x += w;
                    synthG.drawLine(context, "Table.grid", g, x - 1, 0,
                                    x - 1, tableHeight - 1);
		}
	    } else {
		x = damagedArea.x + damagedArea.width;
		for (int column = cMin; column < cMax; column++) {
		    int w = cm.getColumn(column).getWidth();
		    x -= w;
                    synthG.drawLine(context, "Table.grid", g, x - 1, 0, x - 1,
                                    tableHeight - 1);
		}
		x -= cm.getColumn(cMax).getWidth();
                synthG.drawLine(context, "Table.grid", g, x, 0, x,
                                tableHeight - 1);
	    }
	}
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = table.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    private void paintCells(SynthContext context, Graphics g, int rMin,
                            int rMax, int cMin, int cMax) {
	JTableHeader header = table.getTableHeader();
	TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();

	TableColumnModel cm = table.getColumnModel();
	int columnMargin = cm.getColumnMargin();

        Rectangle cellRect;
	TableColumn aColumn;
	int columnWidth;
	if (table.getComponentOrientation().isLeftToRight()) {
	    for(int row = rMin; row <= rMax; row++) {
		cellRect = table.getCellRect(row, cMin, false);
                for(int column = cMin; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    if (aColumn != draggedColumn) {
                        paintCell(context, g, cellRect, row, column);
                    }
                    cellRect.x += columnWidth;
        	}
	    }
	} else {
	    for(int row = rMin; row <= rMax; row++) {
                cellRect = table.getCellRect(row, cMin, false);
                aColumn = cm.getColumn(cMin);
                if (aColumn != draggedColumn) {
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    paintCell(context, g, cellRect, row, cMin);
                }
                for(int column = cMin+1; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    cellRect.x -= columnWidth;
                    if (aColumn != draggedColumn) {
                        paintCell(context, g, cellRect, row, column);
                    }
        	}
	    }
	}

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
	    paintDraggedArea(context, g, rMin, rMax, draggedColumn, header.getDraggedDistance());
	}

	// Remove any renderers that may be left in the rendererPane.
	rendererPane.removeAll();
    }

    private void paintDraggedArea(SynthContext context, Graphics g, int rMin, int rMax, TableColumn draggedColumn, int distance) {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);

        Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
	Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);

	Rectangle vacatedColumnRect = minCell.union(maxCell);

	// Paint a gray well in place of the moving column.
	g.setColor(table.getParent().getBackground());
	g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
		   vacatedColumnRect.width, vacatedColumnRect.height);

	// Move to the where the cell has been dragged.
	vacatedColumnRect.x += distance;

	// Fill the background.
	g.setColor(context.getStyle().getColor(context, ColorType.BACKGROUND));
	g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
		   vacatedColumnRect.width, vacatedColumnRect.height);

        SynthGraphicsUtils synthG = context.getStyle().getGraphicsUtils(
                                            context);
        

	// Paint the vertical grid lines if necessary.
	if (table.getShowVerticalLines()) {
	    g.setColor(table.getGridColor());
	    int x1 = vacatedColumnRect.x;
	    int y1 = vacatedColumnRect.y;
	    int x2 = x1 + vacatedColumnRect.width - 1;
	    int y2 = y1 + vacatedColumnRect.height - 1;
	    // Left
            synthG.drawLine(context, "Table.grid", g, x1-1, y1, x1-1, y2);
	    // Right
	    synthG.drawLine(context, "Table.grid", g, x2, y1, x2, y2);
	}

	for(int row = rMin; row <= rMax; row++) {
	    // Render the cell value
	    Rectangle r = table.getCellRect(row, draggedColumnIndex, false);
	    r.x += distance;
	    paintCell(context, g, r, row, draggedColumnIndex);

	    // Paint the (lower) horizontal grid line if necessary.
	    if (table.getShowHorizontalLines()) {
		g.setColor(table.getGridColor());
		Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
		rcr.x += distance;
		int x1 = rcr.x;
		int y1 = rcr.y;
		int x2 = x1 + rcr.width - 1;
		int y2 = y1 + rcr.height - 1;
		synthG.drawLine(context, "Table.grid", g, x1, y2, x2, y2);
	    }
	}
    }

    private void paintCell(SynthContext context, Graphics g,
            Rectangle cellRect, int row, int column) {
        if (table.isEditing() && table.getEditingRow()==row &&
                                 table.getEditingColumn()==column) {
            Component component = table.getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        }
        else {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component component = table.prepareRenderer(renderer, row, column);
            rendererPane.paintComponent(g, component, table, cellRect.x,
                    cellRect.y, cellRect.width, cellRect.height, true);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (SynthLookAndFeel.shouldUpdateStyle(event)) {
            updateStyle((JTable)event.getSource());
        }
    }


    private class SynthBooleanTableCellRenderer extends JCheckBox implements
                      TableCellRenderer {
	public SynthBooleanTableCellRenderer() {
	    super();
	    setHorizontalAlignment(JLabel.CENTER);
	}

        public String getName() {
            String name = super.getName();
            if (name == null) {
                return "Table.cellRenderer";
            }
            return name;
        }

        public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
	    if (isSelected) {
	        setForeground(table.getSelectionForeground());
	        setBackground(table.getSelectionBackground());
	    }
	    else {
	        setForeground(table.getForeground());
	        setBackground(table.getBackground());
	    }
            setSelected((value != null && ((Boolean)value).booleanValue()));
            // NOTE: We don't do this as otherwise the the JCheckBox will
            // think it is selected when it may not be. This means JCheckBox
            // renderers don't render the selection correctly.
/*
            if (!useTableColors && (isSelected || hasFocus)) {
                SynthLookAndFeel.setSelectedUI((SynthButtonUI)SynthLookAndFeel.
                             getUIOfType(getUI(), SynthButtonUI.class),
                                   isSelected, hasFocus, table.isEnabled());
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }
*/
            return this;
        }

        public void paint(Graphics g) {
            super.paint(g);
            // Refer to comment above for why this is commented out.
            // SynthLookAndFeel.resetSelectedUI();
        }
    }

    private class SynthTableCellRenderer extends DefaultTableCellRenderer {
        private Object numberFormat;
        private Object dateFormat;
        private boolean opaque;

        public void setOpaque(boolean isOpaque) {
            opaque = isOpaque;
        }

        public boolean isOpaque() {
            return opaque;
        }

        public String getName() {
            String name = super.getName();
            if (name == null) {
                return "Table.cellRenderer";
            }
            return name;
        }

        public void setBorder(Border b) {
            if (useUIBorder || b instanceof SynthBorder) {
                super.setBorder(b);
            }
        }

        public Component getTableCellRendererComponent(
                  JTable table, Object value, boolean isSelected,
                  boolean hasFocus, int row, int column) {
            if (!useTableColors && (isSelected || hasFocus)) {
                SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.
                             getUIOfType(getUI(), SynthLabelUI.class),
                                   isSelected, hasFocus, table.isEnabled());
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);

            setIcon(null);
            Class columnClass = table.getColumnClass(column);
            configureValue(value, columnClass);

            return this;
        }

        private void configureValue(Object value, Class columnClass) {
            if (columnClass == Object.class || columnClass == null) {
                setHorizontalAlignment(JLabel.LEADING);
            } else if (columnClass == Float.class || columnClass == Double.class) {
                if (numberFormat == null) {
                    numberFormat = NumberFormat.getInstance();
                }
                setHorizontalAlignment(JLabel.TRAILING);
                setText((value == null) ? "" : ((NumberFormat)numberFormat).format(value));
            }
            else if (columnClass == Number.class) {
                setHorizontalAlignment(JLabel.TRAILING);
                // Super will have set value.
            }
            else if (columnClass == Icon.class || columnClass == ImageIcon.class) {
                setHorizontalAlignment(JLabel.CENTER);
                setIcon((Icon)value);
                setText("");
            }
            else if (columnClass == Date.class) {
                if (dateFormat == null) {
                    dateFormat = DateFormat.getDateInstance();
                }
                setHorizontalAlignment(JLabel.LEADING);
                setText((value == null) ? "" : ((Format)dateFormat).format(value));
            }
            else {
                configureValue(value, columnClass.getSuperclass());
            }
        }

        public void paint(Graphics g) {
            super.paint(g);
            SynthLookAndFeel.resetSelectedUI();
        }
    }
}
