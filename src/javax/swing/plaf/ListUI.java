/*
 * @(#)ListUI.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.JList;
import java.awt.Point;
import java.awt.Rectangle;


/**
 * The JList pluggable look and feel delegate.  This interface adds
 * methods that allow the JList component to map locations, e.g. mouse
 * coordinates, to list cells and from cell indices to the bounds of 
 * the cell.
 *
 * @version 1.11 01/23/03
 * @author Hans Muller
 */

public abstract class ListUI extends ComponentUI
{
    /** 
     * Convert a point in <code>JList</code> coordinates to the closest index
     * of the cell at that location. To determine if the cell actually
     * contains the specified location use a combination of this method and
     * <code>getCellBounds</code>.  Returns -1 if the model is empty.
     * 
     * @param location The JList relative coordinates of the cell
     * @return The index of the cell at location, or -1.
     */
    public abstract int locationToIndex(JList list, Point location);


    /** 
     * Returns the origin of the specified item in JList
     * coordinates, null if index isn't valid.
     * 
     * @param index The index of the JList cell.
     * @return The origin of the index'th cell.
     */
    public abstract Point indexToLocation(JList list, int index);


    /** 
     * Returns the bounds of the specified item in JList
     * coordinates, null if index isn't valid.
     * 
     * @param index The index of the JList cell.
     * @return The bounds of the index'th cell.
     */
    public abstract Rectangle getCellBounds(JList list, int index1, int index2);
}
