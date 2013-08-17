/*
 * @(#)TableCellRenderer.java	1.11 98/08/26
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

import java.awt.Component;
import javax.swing.*;

/**
 * This interface defines the methods any object that would like to be
 * a renderer for cell in a JTable.
 *
 * @version 1.11 08/26/98
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
