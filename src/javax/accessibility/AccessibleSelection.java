/*
 * @(#)AccessibleSelection.java	1.7 98/08/26
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

package javax.accessibility;

/**
 * This AccessibleSelection interface
 * provides the standard mechanism for an assistive technology to determine 
 * what the current selected children are, as well as modify the selection set.
 * Any object that has children that can be selected should support this
 * the AccessibleSelection interface.  Applications can determine if an object supports the 
 * AccessibleSelection interface by first obtaining its AccessibleContext (see
 * {@link Accessible}) and then calling the
 * {@link AccessibleContext#getAccessibleSelection} method.
 * If the return value is not null, the object supports this interface.
 *
 * @see Accessible
 * @see Accessible#getAccessibleContext
 * @see AccessibleContext
 * @see AccessibleContext#getAccessibleSelection
 *
 * @version     1.7 08/26/98 21:14:11
 * @author	Peter Korn
 * @author      Hans Muller
 * @author      Willie Walker
 */
public interface AccessibleSelection {

    /**
     * Returns the number of Accessible children currently selected.
     * If no children are selected, the return value will be 0.
     *
     * @return the number of items currently selected.
     */
     public int getAccessibleSelectionCount();

    /**
     * Returns an Accessible representing the specified selected child
     * in the object.  If there isn't a selection, or there are 
     * fewer children selected than the integer passed in, the return
     * value will be null.
     * <p>Note that the index represents the i-th selected child, which
     * is different from the i-th child.
     *
     * @param i the zero-based index of selected children
     * @return the i-th selected child
     * @see #getAccessibleSelectionCount
     */
     public Accessible getAccessibleSelection(int i);

    /**
     * Determines if the current child of this object is selected.
     *
     * @return true if the current child of this object is selected; else false.
     * @param i the zero-based index of the child in this Accessible object.
     * @see AccessibleContext#getAccessibleChild
     */
     public boolean isAccessibleChildSelected(int i);

    /**
     * Adds the specified Accessible child of the object to the object's
     * selection.  If the object supports multiple selections,
     * the specified child is added to any existing selection, otherwise
     * it replaces any existing selection in the object.  If the
     * specified child is already selected, this method has no effect.
     *
     * @param i the zero-based index of the child
     * @see AccessibleContext#getAccessibleChild
     */
     public void addAccessibleSelection(int i);

    /**
     * Removes the specified child of the object from the object's
     * selection.  If the specified item isn't currently selected, this
     * method has no effect.
     *
     * @param i the zero-based index of the child
     * @see AccessibleContext#getAccessibleChild
     */
     public void removeAccessibleSelection(int i);

    /**
     * Clears the selection in the object, so that no children in the
     * object are selected.
     */
     public void clearAccessibleSelection();

    /**
     * Causes every child of the object to be selected
     * if the object supports multiple selections.
     */
     public void selectAllAccessibleSelection();
}
