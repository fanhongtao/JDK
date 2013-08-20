/*
 * @(#)TreeCellEditor.java	1.14 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTree;

/**
  * Adds to CellEditor the extensions necessary to configure an editor
  * in a tree.
  *
  * @see javax.swing.JTree
  *
  * @version 1.14 12/19/03
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
     * @param	tree		the JTree that is asking the editor to edit;
     *				this parameter can be null
     * @param	value		the value of the cell to be edited
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
