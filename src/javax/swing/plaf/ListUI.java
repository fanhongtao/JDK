/*
 * @(#)ListUI.java	1.6 98/09/21
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
 * @version 1.6 09/21/98
 * @author Hans Muller
 */

public abstract class ListUI extends ComponentUI
{
    /** 
     * Convert a point in JList coordinates to the index
     * of the cell at that location.  Returns -1 if there's no
     * cell the specified location.  
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
