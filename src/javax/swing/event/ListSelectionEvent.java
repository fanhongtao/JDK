/*
 * @(#)ListSelectionEvent.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.util.EventObject;
import javax.swing.*;


/** 
 * An event that characterizes a change in the current
 * selection.  The change is limited to a row interval.
 * ListSelectionListeners will generally query the source of
 * the event for the new selected status of each potentially
 * changed row.
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
 * @version 1.20 12/19/03
 * @author Hans Muller
 * @author Ray Ryan
 * @see ListSelectionModel
 */
public class ListSelectionEvent extends EventObject
{
    private int firstIndex;
    private int lastIndex;
    private boolean isAdjusting;

    /** 
     * Represents a change in selection status between <code>firstIndex</code>
     * and <code>lastIndex</code> inclusive
     * (</code>firstIndex</code> is less than or equal to 
     * <code>lastIndex</code>).  At least one of the rows within the range will
     * have changed, a good <code>ListSelectionModel</code> implementation will
     * keep the range as small as possible.
     * 
     * @param firstIndex the first index that changed
     * @param lastIndex the last index that changed, lastIndex >= firstIndex
     * @param isAdjusting an indication that this is one of rapid a series of events
     */
    public ListSelectionEvent(Object source, int firstIndex, int lastIndex,
			      boolean isAdjusting)
    {
	super(source);
	this.firstIndex = firstIndex;
	this.lastIndex = lastIndex;
	this.isAdjusting = isAdjusting;
    }

    /**
     * Returns the index of the first row whose selection may have changed.
     * @return the first row whose selection value may have changed,
     *         where zero is the first row
     */
    public int getFirstIndex() { return firstIndex; }

    /**
     * Returns the index of the last row whose selection may have changed.
     * @return the last row whose selection value may have changed,
     *         where zero is the first row
     */
    public int getLastIndex() { return lastIndex; }

    /**
     * Returns true if this is one of multiple change events.
     * @return true if this is one of a rapid series of events
     */
    public boolean getValueIsAdjusting() { return isAdjusting; }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
	String properties = 
	    " source=" + getSource() +  
            " firstIndex= " + firstIndex + 
            " lastIndex= " + lastIndex + 
	    " isAdjusting= " + isAdjusting +
            " ";
        return getClass().getName() + "[" + properties + "]";
    }
}

