/*
 * @(#)TreeCellEditor.java	1.7 98/09/21
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

package javax.swing.tree;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTree;

/**
  * Adds to CellEditor the extensions necessary to configure an editor
  * in a tree.
  *
  * @version 1.7 09/21/98
  * @author Scott Violet
  */

public interface TreeCellEditor extends CellEditor
{
    /**
     * Sets an initial <I>value</I> for the editor.  This will cause
     * the editor to stopEditing and lose any partially edited value
     * if the editor is editing when this method is called. <p>
     *
     * Returns the component that should be added to the client's
     * Component hierarchy.  Once installed in the client's hierarchy
     * this component will then be able to draw and receive user input.
     *
     * @param	table		the JTree that is asking the editor to edit
     *				This parameter can be null.
     * @param	value		the value of the cell to be edited.
     * @param	isSelected	true is the cell is to be renderer with
     *				selection highlighting
     * @param	expanded	true if the node is expanded
     * @param	leaf		true if the node is a leaf node
     * @param	row		the row index of the node being edited
     * @return	the component for editing
     */
    Component getTreeCellEditorComponent(JTree tree, Object value,
					 boolean isSelected, boolean expanded,
					 boolean leaf, int row);
}
