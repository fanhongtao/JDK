/*
 * @(#)ListCellRenderer.java	1.12 98/08/26
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
 * @version 1.12 08/26/98
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
