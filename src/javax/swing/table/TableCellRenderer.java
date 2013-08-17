/*
 * @(#)TableCellRenderer.java	1.12 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import java.awt.Component;
import javax.swing.*;

/**
 * This interface defines the methods any object that would like to be
 * a renderer for cell in a JTable.
 *
 * @version 1.12 11/29/01
 * @author Alan Chung
 */

public interface TableCellRenderer {

    /**
     *  This method is sent to the renderer by the drawing table to
     *  configure the renderer appropriately before drawing.  Return
     *  the Component used for drawing.
     *
     * @param	table		the JTable that is asking the renderer to draw.
     *				This parameter can be null.
     * @param	value		the value of the cell to be rendered.  It is
     *				up to the specific renderer to interpret
     *				and draw the value.  eg. if value is the
     *				String "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  null is a valid value.
     * @param	isSelected	true is the cell is to be renderer with
     *				selection highlighting
     * @param	row	        the row index of the cell being drawn.  When
     *				drawing the header the rowIndex is -1.
     * @param	column	        the column index of the cell being drawn
     */
    Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus, 
					    int row, int column);
}
