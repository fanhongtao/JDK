/*
 * @(#)ListCellRenderer.java	1.17 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.Component;


/**
 * Identifies components that can be used as "rubber stamps" to paint
 * the cells in a JList.  For example, to use a JLabel as a
 * ListCellRenderer, you would write something like this:
 * <pre>
 * class MyCellRenderer extends JLabel implements ListCellRenderer {
 *     public MyCellRenderer() {
 *         setOpaque(true);
 *     }
 *     public Component getListCellRendererComponent(
 *         JList list,
 *         Object value,
 *         int index,
 *         boolean isSelected,
 *         boolean cellHasFocus)
 *     {
 *         setText(value.toString());
 *         setBackground(isSelected ? Color.red : Color.white);
 *         setForeground(isSelected ? Color.white : Color.black);
 *         return this;
 *     }
 * }
 * </pre>
 *
 * @see JList
 * @see DefaultListCellRenderer
 *
 * @version 1.17 12/19/03
 * @author Hans Muller
 */
public interface ListCellRenderer
{
    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     *
     * @param list The JList we're painting.
     * @param value The value returned by list.getModel().getElementAt(index).
     * @param index The cells index.
     * @param isSelected True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     *
     * @see JList
     * @see ListSelectionModel
     * @see ListModel
     */
    Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus);
}
