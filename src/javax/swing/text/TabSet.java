/*
 * @(#)TabSet.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.text;

import java.io.Serializable;

/**
 * A TabSet is comprised of many TabStops. It offers methods for locating the
 * closest TabStop to a given position and finding all the potential TabStops.
 * It is also immutable.
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
 * @author  Scott Violet
 * @version 1.13 01/23/03
 */
public class TabSet implements Serializable
{
    /** TabStops this TabSet contains. */
    private TabStop[]              tabs;

    /**
     * Creates and returns an instance of TabSet. The array of Tabs
     * passed in must be sorted in ascending order.
     */
    public TabSet(TabStop[] tabs) {
	// PENDING(sky): If this becomes a problem, make it sort.
	if(tabs != null) {
	    int          tabCount = tabs.length;

	    this.tabs = new TabStop[tabCount];
	    System.arraycopy(tabs, 0, this.tabs, 0, tabCount);
	}
	else
	    this.tabs = null;
    }

    /**
     * Returns the number of Tab instances the receiver contains.
     */
    public int getTabCount() {
	return (tabs == null) ? 0 : tabs.length;
    }

    /**
     * Returns the TabStop at index <code>index</code>. This will throw an
     * IllegalArgumentException if <code>index</code> is outside the range
     * of tabs.
     */
    public TabStop getTab(int index) {
	int          numTabs = getTabCount();

	if(index < 0 || index >= numTabs)
	    throw new IllegalArgumentException(index +
					      " is outside the range of tabs");
	return tabs[index];
    }

    /**
     * Returns the Tab instance after <code>location</code>. This will
     * return null if there are no tabs after <code>location</code>.
     */
    public TabStop getTabAfter(float location) {
	int     index = getTabIndexAfter(location);

	return (index == -1) ? null : tabs[index];
    }

    /**
     * @return the index of the TabStop <code>tab</code>, or -1 if
     * <code>tab</code> is not contained in the receiver.
     */
    public int getTabIndex(TabStop tab) {
	for(int counter = getTabCount() - 1; counter >= 0; counter--)
	    // should this use .equals?
	    if(getTab(counter) == tab)
		return counter;
	return -1;
    }

    /**
     * Returns the index of the Tab to be used after <code>location</code>.
     * This will return -1 if there are no tabs after <code>location</code>.
     */
    public int getTabIndexAfter(float location) {
	int     current, min, max;

	min = 0;
	max = getTabCount();
	while(min != max) {
	    current = (max - min) / 2 + min;
	    if(location > tabs[current].getPosition()) {
		if(min == current)
		    min = max;
		else
		    min = current;
	    }
	    else {
		if(current == 0 || location > tabs[current - 1].getPosition())
		    return current;
		max = current;
	    }
	}
	// no tabs after the passed in location.
	return -1;
    }

    /**
     * Returns the string representation of the set of tabs.
     */
    public String toString() {
	int            tabCount = getTabCount();
	StringBuffer   buffer = new StringBuffer("[ ");

	for(int counter = 0; counter < tabCount; counter++) {
	    if(counter > 0)
		buffer.append(" - ");
	    buffer.append(getTab(counter).toString());
	}
	buffer.append(" ]");
	return buffer.toString();
    }
}
