/*
 * @(#)BasicTableHeaderUI.java	1.40 98/08/26
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

package javax.swing.plaf.basic;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Enumeration;
import java.awt.event.*;
import java.awt.*;
import javax.swing.plaf.*;

/**
 * BasicTableHeaderUI implementation
 *
 * @version 1.40 08/26/98
 * @author Philip Milne
 * @author Alan Chung
 */
public class BasicTableHeaderUI extends TableHeaderUI {

//
// Instance Variables
//

    /** The JTableHeader that is delegating the painting to this UI. */
    protected JTableHeader header;
    protected CellRendererPane rendererPane;

    // Listeners that are attached to the JTable
    protected MouseInputListener mouseInputListener;

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    public class MouseInputHandler implements MouseInputListener {

        private boolean phantomMousePressed = false;

        private int lastEffectiveMouseX;

        public void mouseClicked(MouseEvent e) {}

        public void mousePressed(MouseEvent e) {
            if (phantomMousePressed) {
                // System.err.println("BasicTableHeaderUI recieved two consecutive mouse pressed events");
                // Catching this causes errors rather than fixing them.
                // return;
            }
            phantomMousePressed = true;

            header.setDraggedColumn(null);
            header.setResizingColumn(null);
            header.setDraggedDistance(0);

            Point p = e.getPoint();
            lastEffectiveMouseX = p.x;

            // First find which header cell was hit
            TableColumnModel columnModel = header.getColumnModel();
            int index = columnModel.getColumnIndexAtX(p.x);

            if (index != -1) {
                // The last 3 pixels + 3 pixels of next column are for resizing
                int resizeIndex = getResizingColumn(p);
                if (header.getResizingAllowed() && (resizeIndex != -1)) {
                    TableColumn hitColumn = columnModel.getColumn(resizeIndex);
                    header.setResizingColumn(hitColumn);
                }
                else if (header.getReorderingAllowed()) {
                    TableColumn hitColumn = columnModel.getColumn(index);
                    header.setDraggedColumn(hitColumn);
                }
                else {  // Not allowed to reorder or resize.
                }
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (getResizingColumn(e.getPoint()) != -1) {
                Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                if (header.getCursor() != resizeCursor) {
                    header.setCursor(resizeCursor);
                }
            }
            else {
                Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                if (header.getCursor() != defaultCursor) {
                    header.setCursor(defaultCursor);
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            int mouseX = e.getX();
            int deltaX = mouseX - lastEffectiveMouseX;

            if (deltaX == 0) {
                return;
            }

            TableColumn resizingColumn  = header.getResizingColumn();
            TableColumn draggedColumn  = header.getDraggedColumn();

            if (resizingColumn != null) {
	        int oldWidth = resizingColumn.getWidth();
                int newWidth = oldWidth + deltaX;
                resizingColumn.setWidth(newWidth);

                // PENDING(philip): Should't need to refer to the table here.
                int resizingColumnIndex = viewIndexForColumn(resizingColumn);
                header.getTable().sizeColumnsToFit(resizingColumnIndex); 
                int acheivedDeltaX = resizingColumn.getWidth() - oldWidth;
                lastEffectiveMouseX = lastEffectiveMouseX + acheivedDeltaX;

                header.revalidate();
                header.repaint();
                if (header.getUpdateTableInRealTime()) {
                    JTable table = header.getTable();
                    table.revalidate();
                    table.repaint();
                }
            }
            else if (draggedColumn != null) {
                move(e, deltaX);
                lastEffectiveMouseX = mouseX;
            }
            else {
                // Neither dragging nor resizing ...
                lastEffectiveMouseX = mouseX;
            }
        }

        public void mouseReleased(MouseEvent e) {
            phantomMousePressed = false;

            header.setResizingColumn(null);
            header.setDraggedColumn(null);
            header.setDraggedDistance(0);

            // Repaint to finish cleaning up
            header.repaint();
            JTable table = header.getTable();
            if (table != null)
                table.repaint();
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}
//
// Protected & Private Methods
//

        private int viewIndexForColumn(TableColumn aColumn) {
            TableColumnModel cm = header.getColumnModel();
            for (int column = 0; column < cm.getColumnCount(); column++) {
                if (cm.getColumn(column) == aColumn) {
                    return column;
                }
            }
            return -1;
        }

        private void move(MouseEvent e, int delta) {
            TableColumnModel columnModel = header.getColumnModel();
            int lastColumn = columnModel.getColumnCount() - 1;

            TableColumn draggedColumn = header.getDraggedColumn();
            int draggedDistance = header.getDraggedDistance() + delta;
            int hitColumnIndex = viewIndexForColumn(draggedColumn);

            // Now check if we have moved enough to do a swap
            if ((draggedDistance < 0) && (hitColumnIndex != 0)) {
                // Moving left; check prevColumn
                int width = columnModel.getColumnMargin() +
                    columnModel.getColumn(hitColumnIndex-1).getWidth();
                if (-draggedDistance > (width / 2)) {
                    // Swap me
                    columnModel.moveColumn(hitColumnIndex, hitColumnIndex-1);

                    draggedDistance = width + draggedDistance;
                    hitColumnIndex--;
                }
            }
            else if ((draggedDistance > 0) && (hitColumnIndex != lastColumn)) {
                // Moving right; check nextColumn
                int width = columnModel.getColumnMargin() +
                    columnModel.getColumn(hitColumnIndex+1).getWidth();
                if (draggedDistance > (width / 2)) {
                    // Swap me
                    columnModel.moveColumn(hitColumnIndex, hitColumnIndex+1);

                    draggedDistance = -(width - draggedDistance);
                    hitColumnIndex++;
                }
            }

            // Redraw, compute how much we are moving and the total redraw rect
            Rectangle redrawRect = header.getHeaderRect(hitColumnIndex);  // where I was
            redrawRect.x += header.getDraggedDistance();
            // draggedDistance += delta;
            Rectangle redrawRect2 = header.getHeaderRect(hitColumnIndex); // where I'm now
            redrawRect2.x += draggedDistance;
            redrawRect = redrawRect.union(redrawRect2);  // Union the 2 rects

            header.repaint(redrawRect.x, 0, redrawRect.width, redrawRect.height);
            if (header.getUpdateTableInRealTime()) {
                JTable table = header.getTable();
                if (table != null)
                    table.repaint(redrawRect.x, 0, redrawRect.width,
                                  (table.getRowHeight() +
                                   table.getIntercellSpacing().height)
                                  * table.getRowCount());
            }

            header.setDraggedColumn(columnModel.getColumn(hitColumnIndex));
            header.setDraggedDistance(draggedDistance);
        }

        private int getResizingColumn(Point p) {
            int column = 0;
            Rectangle resizeRect = new Rectangle(-3,0,6,header.getSize().height);
            int columnMargin = header.getColumnModel().getColumnMargin();
            Enumeration enumeration = header.getColumnModel().getColumns();

            while (enumeration.hasMoreElements()) {
                TableColumn aColumn = (TableColumn)enumeration.nextElement();
                resizeRect.x += aColumn.getWidth() + columnMargin;

                if (resizeRect.x > p.x) {
                    // Don't have to check the rest, we already gone past p
                    break;
                }
                if (resizeRect.contains(p))
                    return column;

                column++;
            }
            return -1;
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
        return new BasicTableHeaderUI();
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
        LookAndFeel.installColorsAndFont(header, "TableHeader.background",
                                         "TableHeader.foreground", "TableHeader.font");
    }

    /**
     * Attaches listeners to the JTableHeader.
     */
    protected void installListeners() {
        mouseInputListener = createMouseInputListener();

        header.addMouseListener(mouseInputListener);
        header.addMouseMotionListener(mouseInputListener);
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

    protected void uninstallDefaults() {}

    protected void uninstallListeners() {
        header.removeMouseListener(mouseInputListener);
        header.removeMouseMotionListener(mouseInputListener);

        mouseInputListener = null;
    }

    protected void uninstallKeyboardActions() {}

//
// Paint Methods and support
//

    public void paint(Graphics g, JComponent c) {
        Rectangle clipBounds = g.getClipBounds();

        if (header.getColumnModel() == null)
            return;

        int column = 0;
        boolean drawn = false;
        int draggedColumnIndex = -1;
        Rectangle draggedCellRect = null;
        Dimension size = header.getSize();
        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);

        Enumeration enumeration = header.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn)enumeration.nextElement();
            int columnMargin = header.getColumnModel().getColumnMargin();
            cellRect.width = aColumn.getWidth() + columnMargin;
            // Note: The header cellRect includes columnMargin so the
            //       drawing of header cells will not have any gaps.

            if (cellRect.intersects(clipBounds)) {
                drawn = true;
                if (aColumn != header.getDraggedColumn()) {
                    paintCell(g, cellRect, column);
                }
                else {
                    // Draw a gray well in place of the moving column
                    g.setColor(header.getParent().getBackground());
                    g.fillRect(cellRect.x, cellRect.y,
                               cellRect.width, cellRect.height);
                    draggedCellRect = new Rectangle(cellRect);
                    draggedColumnIndex = column;
                }
            }
            else {
                if (drawn)
                    // Don't need to iterate through the rest
                    break;
            }

            cellRect.x += cellRect.width;
            column++;
        }

        // draw the dragged cell if we are dragging
        TableColumn draggedColumnObject = header.getDraggedColumn();
        if (draggedColumnObject != null && draggedCellRect != null) {
            draggedCellRect.x += header.getDraggedDistance();
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        Component component = renderer.getTableCellRendererComponent(
                  header.getTable(), aColumn.getHeaderValue(),
                  false, false, -1, columnIndex);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
                            cellRect.width, cellRect.height, true);
    }

//
// Size Methods
//

    private int getHeaderHeight() {
        int height = 0;
        TableColumnModel columnModel = header.getColumnModel();
        for(int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            TableCellRenderer renderer = aColumn.getHeaderRenderer();
            Component comp = renderer.getTableCellRendererComponent(header.getTable(),
                                               aColumn.getHeaderValue(), false, false,
                                               -1, column);
            height = Math.max(height, comp.getPreferredSize().height);
        }
        return height;
    }

    private Dimension createHeaderSize(long width) {
        TableColumnModel columnModel = header.getColumnModel();
        // None of the callers include the intercell spacing, do it here.
        width += columnModel.getColumnMargin() * columnModel.getColumnCount();
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

}  // End of Class BasicTableHeaderUI

