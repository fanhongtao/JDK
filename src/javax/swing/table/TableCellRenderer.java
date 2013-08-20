/*
 * @(#)TableCellRenderer.java	1.18 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import java.awt.Component;
import javax.swing.*;

/**
 * This interface defines the method required by any object that
 * would like to be a renderer for cells in a <code>JTable</code>.
 *
 * @version 1.18 12/19/03
 * @author Alan Chung
 */

public interface TableCellRenderer {

    /**
     *  Returns the component used for drawing the cell.  This method is
     *  used to configure the renderer appropriately before drawing.
     *
     * @param	table		the <code>JTable</code> that is asking the 
     *				renderer to draw; can be <code>null</code>
     * @param	value		the value of the cell to be rendered.  It is
     *				up to the specific renderer to interpret
     *				and draw the value.  For example, if
     *				<code>value</code>
     *				is the string "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  <code>null</code> is a
     *				valid value
     * @param	isSelected	true if the cell is to be rendered with the
     *				selection highlighted; otherwise false
     * @param	hasFocus	if true, render cell appropriately.  For
     *				example, put a special border on the cell, if
     *				the cell can be edited, render in the color used
     *				to indicate editing
     * @param	row	        the row index of the cell being drawn.  When
     *				drawing the header, the value of
     *				<code>row</code> is -1
     * @param	column	        the column index of the cell being drawn
     */
    Component getTableCellRendererComponent(JTable table, Object value,
					    boolean isSelected, boolean hasFocus, 
					    int row, int column);
}
