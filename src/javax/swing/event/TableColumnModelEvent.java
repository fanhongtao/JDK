/*
 * @(#)TableColumnModelEvent.java	1.19 06/04/11
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.util.EventObject;
import javax.swing.table.*;

/**
 * <B>TableColumnModelEvent</B> is used to notify listeners that a table
 * column model has changed, such as a column was added, removed, or
 * moved.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.19 04/11/06
 * @author Alan Chung
 * @see TableColumnModelListener
 */
public class TableColumnModelEvent extends java.util.EventObject
{
//
//  Instance Variables
//

    /** The index of the column from where it was moved or removed */
    protected int	fromIndex;

    /** The index of the column to where it was moved or added */
    protected int	toIndex;

//
// Constructors
//

    /**
     * Constructs a {@code TableColumnModelEvent} object.
     *
     * @param source  the {@code TableColumnModel} that originated the event
     * @param from    an int specifying the index from where the column was
     *                moved or removed
     * @param to      an int specifying the index to where the column was
     *                moved or added
     * @see #getFromIndex
     * @see #getToIndex
     */
    public TableColumnModelEvent(TableColumnModel source, int from, int to) {
	super(source);
	fromIndex = from;
	toIndex = to;
    }
    
//
// Querying Methods
//

    /** Returns the fromIndex.  Valid for removed or moved events */
    public int getFromIndex() { return fromIndex; };

    /** Returns the toIndex.  Valid for add and moved events */
    public int getToIndex() { return toIndex; };
}
