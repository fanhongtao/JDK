/*
 * @(#)TableCellEditor.java	1.11 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.table;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.*;

/**
 * This interface defines the methods any object that would like to be
 * an editor of values for components such as ListBox, ComboBox, Tree, or
 * Table, etc.
 *
 * @version 1.11 11/29/01
 * @author Alan Chung
 */


public interface TableCellEditor extends CellEditor {

    /**
     *  Sets an initial <I>value</I> for the editor.  This will cause
     *  the editor to stopEditing and lose any partially edited value
     *  if the editor is editing when this method is called. <p>
     *
     *  Returns the component that should be added to the client's
     *  Component hierarchy.  Once installed in the client's hierarchy
     *  this component will then be able to draw and receive user input.
     *
     * @param	table		the JTable that is asking the editor to edit
     *				This parameter can be null.
     * @param	value		the value of the cell to be edited.  It is
     *				up to the specific editor to interpret
     *				and draw the value.  eg. if value is the
     *				String "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  null is a valid value.
     * @param	isSelected	true is the cell is to be renderer with
     *				selection highlighting
     * @param	row     	the row of the cell being edited
     * @param	column  	the column of the cell being edited
     * @return	the component for editing
     */
    Component getTableCellEditorComponent(JTable table, Object value,
					  boolean isSelected,
					  int row, int column);
}

