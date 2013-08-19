/*
 * @(#)SynthTableHeaderUI.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Enumeration;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import javax.swing.plaf.*;

/**
 * BasicTableHeaderUI implementation
 *
 * @version 1.8, 01/23/03 (based on BasicTableHeaderUI v 1.59)
 * @author Alan Chung
 * @author Philip Milne
 */
class SynthTableHeaderUI extends TableHeaderUI implements
           PropertyChangeListener, SynthUI {

    private static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); 

//
// Instance Variables
//

    /** The JTableHeader that is delegating the painting to this UI. */
    protected JTableHeader header;
    protected CellRendererPane rendererPane;

    // Listeners that are attached to the JTable
    protected MouseInputListener mouseInputListener;

    private SynthStyle style;

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    class MouseInputHandler implements MouseInputListener {

        private int mouseXOffset; 
	private Cursor otherCursor = resizeCursor; 

        public void mouseClicked(MouseEvent e) {}

	private boolean canResize(TableColumn column) { 
	    return (column != null) && header.getResizingAllowed() && column.getResizable(); 
	}

        private TableColumn getResizingColumn(Point p) { 
	    return getResizingColumn(p, header.columnAtPoint(p)); 
	}

        private TableColumn getResizingColumn(Point p, int column) { 
            if (column == -1) { 
                 return null; 
            }
	    Rectangle r = header.getHeaderRect(column); 
	    r.grow(-3, 0); 
	    if (r.contains(p)) { 
		return null; 
	    }
	    int midPoint = r.x + r.width/2; 
	    int columnIndex;
            if( header.getComponentOrientation().isLeftToRight() ) {
                columnIndex = (p.x < midPoint) ? column - 1 : column; 
            } else {
                columnIndex = (p.x < midPoint) ? column : column - 1; 
            }
	    if (columnIndex == -1) { 
		return null; 
	    }
	    return header.getColumnModel().getColumn(columnIndex); 
        }

        public void mousePressed(MouseEvent e) {
            header.setDraggedColumn(null);
            header.setResizingColumn(null);
            header.setDraggedDistance(0);

            Point p = e.getPoint();

            // First find which header cell was hit
            TableColumnModel columnModel = header.getColumnModel();
            int index = header.columnAtPoint(p);

            if (index != -1) {
                // The last 3 pixels + 3 pixels of next column are for resizing
                TableColumn resizingColumn = getResizingColumn(p, index); 
                if (canResize(resizingColumn)) {
                    header.setResizingColumn(resizingColumn); 
                    if( header.getComponentOrientation().isLeftToRight() ) {
                        mouseXOffset = p.x - resizingColumn.getWidth(); 
                    } else {
                        mouseXOffset = p.x + resizingColumn.getWidth(); 
                    }
                }
                else if (header.getReorderingAllowed()) {
                    TableColumn hitColumn = columnModel.getColumn(index);
                    header.setDraggedColumn(hitColumn);
		    mouseXOffset = p.x; 
                }
            }
        }

	private void swapCursor() { 
	    Cursor tmp = header.getCursor(); 
	    header.setCursor(otherCursor); 
	    otherCursor = tmp; 
	}

        public void mouseMoved(MouseEvent e) { 
            if (canResize(getResizingColumn(e.getPoint())) != 
		(header.getCursor() == resizeCursor)) {
                swapCursor();
            }
       }

        public void mouseDragged(MouseEvent e) {
            int mouseX = e.getX();

            TableColumn resizingColumn  = header.getResizingColumn();
            TableColumn draggedColumn  = header.getDraggedColumn();

            boolean headerLeftToRight = header.getComponentOrientation().isLeftToRight();

            if (resizingColumn != null) {
		int oldWidth = resizingColumn.getWidth();
		int newWidth;
		if (headerLeftToRight) {
                    newWidth = mouseX - mouseXOffset;
                } else  {
                    newWidth = mouseXOffset - mouseX;
		}
                resizingColumn.setWidth(newWidth);

		Container container;
		if ((header.getParent() == null) ||
		    ((container = header.getParent().getParent()) == null) ||
		    !(container instanceof JScrollPane)) {
		    return;
		}

		if (!container.getComponentOrientation().isLeftToRight() &&
		    !headerLeftToRight) {
		    JTable table = header.getTable();
		    if (table != null) {
			JViewport viewport = ((JScrollPane)container).getViewport();
			int viewportWidth = viewport.getWidth();
			int diff = newWidth - oldWidth;
			int newHeaderWidth = table.getWidth() + diff;

			/* Resize a table */
			Dimension tableSize = table.getSize();
			tableSize.width += diff;
			table.setSize(tableSize);

			/* If this table is in AUTO_RESIZE_OFF mode and
			 * has a horizontal scrollbar, we need to update
			 * a view's position.
			 */
			if ((newHeaderWidth >= viewportWidth) &&
			    (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF)) {
			    Point p = viewport.getViewPosition();
			    p.x = Math.max(0, Math.min(newHeaderWidth - viewportWidth, p.x + diff));
			    viewport.setViewPosition(p);

			    /* Update the original X offset value. */
			    mouseXOffset += diff;
			}
		    }
		}
            }
            else if (draggedColumn != null) {
		TableColumnModel cm = header.getColumnModel();
		int draggedDistance = mouseX - mouseXOffset;
		int direction = (draggedDistance < 0) ? -1 : 1;
		int columnIndex = viewIndexForColumn(draggedColumn);
		int newColumnIndex = columnIndex + (headerLeftToRight ? direction : -direction); 
		if (0 <= newColumnIndex && newColumnIndex < cm.getColumnCount()) {
		    int width = cm.getColumn(newColumnIndex).getWidth();
		    if (Math.abs(draggedDistance) > (width / 2)) {
			mouseXOffset = mouseXOffset + direction * width; 
			header.setDraggedDistance(draggedDistance - direction * width);	
			cm.moveColumn(columnIndex, newColumnIndex); 
			return; 
		    }
		}
		setDraggedDistance(draggedDistance, columnIndex); 
	    }
        }

        public void mouseReleased(MouseEvent e) { 
	    setDraggedDistance(0, viewIndexForColumn(header.getDraggedColumn())); 

            header.setResizingColumn(null);
            header.setDraggedColumn(null);
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}
//
// Protected & Private Methods
//

	private void setDraggedDistance(int draggedDistance, int column) { 
            header.setDraggedDistance(draggedDistance);	
	    if (column != -1) { 
		header.getColumnModel().moveColumn(column, column); 
	    }
	}
    }

//
//  Factory methods for the Listeners
//

    /**
     * Creates the mouse listener for the JTable.
     */
    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler();
    }

//
//  The installation/uninstall procedures and support
//

    public static ComponentUI createUI(JComponent h) {
        return new SynthTableHeaderUI();
    }

//  Installation

    public void installUI(JComponent c) {
        header = (JTableHeader)c;

        rendererPane = new CellRendererPane();
        header.add(rendererPane);

        installDefaults();
        installListeners();
        installKeyboardActions();
    }

    /**
     * Initialize JTableHeader properties, e.g. font, foreground, and background.
     * The font, foreground, and background properties are only set if their
     * current value is either null or a UIResource, other properties are set
     * if the current value is null.
     *
     * @see #installUI
     */
    protected void installDefaults() {
        fetchStyle(header);
    }

    private void fetchStyle(JTableHeader c) {
        SynthContext context = getContext(c, ENABLED);
        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();

        TableCellRenderer renderer = header.getDefaultRenderer();

        // PENDING: JTableHeader should install the default in such a way
        // that we know we can override it.
        header.setDefaultRenderer(new HeadererRenderer());
    }

    /**
     * Attaches listeners to the JTableHeader.
     */
    protected void installListeners() {
        mouseInputListener = createMouseInputListener();

        header.addMouseListener(mouseInputListener);
        header.addMouseMotionListener(mouseInputListener);
        header.addPropertyChangeListener(this);
    }

    /**
     * Register all keyboard actions on the JTableHeader.
     */
    protected void installKeyboardActions() { }

// Uninstall methods

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

        header.remove(rendererPane);
        rendererPane = null;
        header = null;
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(header, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void uninstallListeners() {
        header.removeMouseListener(mouseInputListener);
        header.removeMouseMotionListener(mouseInputListener);

        header.removePropertyChangeListener(this);
        mouseInputListener = null;
    }

    protected void uninstallKeyboardActions() {}

//
// Paint Methods and support
//

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
	if (header.getColumnModel().getColumnCount() <= 0) { 
	    return; 
	}
        boolean ltr = header.getComponentOrientation().isLeftToRight();

	Rectangle clip = g.getClipBounds(); 
        Point left = clip.getLocation();
        Point right = new Point( clip.x + clip.width - 1, clip.y );
	TableColumnModel cm = header.getColumnModel(); 
        int cMin = header.columnAtPoint( ltr ? left : right );
        int cMax = header.columnAtPoint( ltr ? right : left );
        // This should never happen. 
        if (cMin == -1) {
	    cMin =  0;
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
	    cMax = cm.getColumnCount()-1;  
        }

	TableColumn draggedColumn = header.getDraggedColumn(); 
	int columnWidth;
	int columnMargin = cm.getColumnMargin();
        Rectangle cellRect = header.getHeaderRect(cMin); 
	TableColumn aColumn;
	if (ltr) {
	    for(int column = cMin; column <= cMax ; column++) { 
		aColumn = cm.getColumn(column); 
		columnWidth = aColumn.getWidth();
		cellRect.width = columnWidth - columnMargin;
		if (aColumn != draggedColumn) {
		    paintCell(g, cellRect, column);
		} 
		cellRect.x += columnWidth;
	    }
	} else {
	    aColumn = cm.getColumn(cMin);
	    if (aColumn != draggedColumn) {
		columnWidth = aColumn.getWidth();
		cellRect.width = columnWidth - columnMargin;
		cellRect.x += columnMargin;
		paintCell(g, cellRect, cMin);
	    }
	    for(int column = cMin+1; column <= cMax; column++) {
		aColumn = cm.getColumn(column);
		columnWidth = aColumn.getWidth();
		cellRect.width = columnWidth - columnMargin;
		cellRect.x -= columnWidth;
		if (aColumn != draggedColumn) {
		    paintCell(g, cellRect, column);
		}
	    }
	} 

        // Paint the dragged column if we are dragging. 
        if (draggedColumn != null) { 
            int draggedColumnIndex = viewIndexForColumn(draggedColumn); 
	    Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex); 
            
            // Draw a gray well in place of the moving column. 
            g.setColor(header.getParent().getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y,
                               draggedCellRect.width, draggedCellRect.height);

            draggedCellRect.x += header.getDraggedDistance();

	    // Fill the background. 
            g.setColor(context.getStyle().getColor(context,
                                                   ColorType.BACKGROUND));
	    g.fillRect(draggedCellRect.x, draggedCellRect.y,
		       draggedCellRect.width, draggedCellRect.height);
 
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }

	// Remove all components in the rendererPane. 
	rendererPane.removeAll(); 
    }

    private Component getHeaderRenderer(int columnIndex) { 
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex); 
	TableCellRenderer renderer = aColumn.getHeaderRenderer(); 
        if (renderer == null) { 
	    renderer = header.getDefaultRenderer(); 
	}
	return renderer.getTableCellRendererComponent(header.getTable(), 
						aColumn.getHeaderValue(), false, false, 
                                                -1, columnIndex);
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        Component component = getHeaderRenderer(columnIndex); 
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
                            cellRect.width, cellRect.height, true);
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = header.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
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
// Size Methods
//

    private int getHeaderHeight() {
        int height = 0; 
	boolean accomodatedDefault = false; 
        TableColumnModel columnModel = header.getColumnModel();
        for(int column = 0; column < columnModel.getColumnCount(); column++) { 
	    TableColumn aColumn = columnModel.getColumn(column); 
	    // Configuring the header renderer to calculate its preferred size is expensive. 
	    // Optimise this by assuming the default renderer always has the same height. 
	    if (aColumn.getHeaderRenderer() != null || !accomodatedDefault) { 
		Component comp = getHeaderRenderer(column); 
		int rendererHeight = comp.getPreferredSize().height; 
		height = Math.max(height, rendererHeight); 
		// If the header value is empty (== "") in the 
		// first column (and this column is set up 
		// to use the default renderer) we will 
		// return zero from this routine and the header 
		// will disappear altogether. Avoiding the calculation 
		// of the preferred size is such a performance win for 
		// most applications that we will continue to 
		// use this cheaper calculation, handling these 
		// issues as `edge cases'. 
		if (rendererHeight > 0) { 
		    accomodatedDefault = true; 
		}
	    }
        }
        return height;
    }

    private Dimension createHeaderSize(long width) {
        TableColumnModel columnModel = header.getColumnModel();
        // None of the callers include the intercell spacing, do it here.
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int)width, getHeaderHeight());
    }


    /**
     * Return the minimum size of the header. The minimum width is the sum 
     * of the minimum widths of each column (plus inter-cell spacing).
     */
    public Dimension getMinimumSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getMinWidth();
        }
        return createHeaderSize(width);
    }

    /**
     * Return the preferred size of the header. The preferred height is the 
     * maximum of the preferred heights of all of the components provided 
     * by the header renderers. The preferred width is the sum of the 
     * preferred widths of each column (plus inter-cell spacing).
     */
    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createHeaderSize(width);
    }

    /**
     * Return the maximum size of the header. The maximum width is the sum 
     * of the maximum widths of each column (plus inter-cell spacing).
     */
    public Dimension getMaximumSize(JComponent c) {
        long width = 0;
        Enumeration enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            width = width + aColumn.getMaxWidth();
        }
        return createHeaderSize(width);
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
            fetchStyle((JTableHeader)evt.getSource());
        }
    }


    private class HeadererRenderer extends DefaultTableCellRenderer
                                 implements UIResource {
        HeadererRenderer() {
            setName("TableHeader.renderer");
            setBorder(null);
            setHorizontalAlignment(JLabel.LEADING);
            // Recreate the UI to trigger refetching of the style.
            updateUI();
        }

        public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}  // End of Class SynthTableHeaderUI
