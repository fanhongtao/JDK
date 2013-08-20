/*
 * @(#)ListSelectionModel.java	1.21 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.event.*;

/**
 * This interface represents the current state of the 
 * selection for any of the components that display a 
 * list of values with stable indices.  The selection is 
 * modeled as a set of intervals, each interval represents
 * a contiguous range of selected list elements.
 * The methods for modifying the set of selected intervals
 * all take a pair of indices, index0 and index1, that represent
 * a closed interval, i.e. the interval includes both index0 and
 * index1.
 * 
 * @version 1.21 12/19/03
 * @author Hans Muller
 * @author Philip Milne
 * @see DefaultListSelectionModel
 */

public interface ListSelectionModel 
{
    /**
     * A value for the selectionMode property: select one list index
     * at a time.
     * 
     * @see #setSelectionMode
     */
    int SINGLE_SELECTION = 0;

    /**
     * A value for the selectionMode property: select one contiguous
     * range of indices at a time.
     * 
     * @see #setSelectionMode
     */
    int SINGLE_INTERVAL_SELECTION = 1;

    /**
     * A value for the selectionMode property: select one or more 
     * contiguous ranges of indices at a time.
     * 
     * @see #setSelectionMode
     */
    int MULTIPLE_INTERVAL_SELECTION = 2;


    /** 
     * Change the selection to be between index0 and index1 inclusive.
     * If this represents a change to the current selection, then
     * notify each ListSelectionListener. Note that index0 doesn't have
     * to be less than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener
     */
    void setSelectionInterval(int index0, int index1);


    /** 
     * Change the selection to be the set union of the current selection
     * and the indices between index0 and index1 inclusive.  If this represents 
     * a change to the current selection, then notify each 
     * ListSelectionListener. Note that index0 doesn't have to be less
     * than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener
     */
    void addSelectionInterval(int index0, int index1);


    /** 
     * Change the selection to be the set difference of the current selection
     * and the indices between index0 and index1 inclusive.  If this represents 
     * a change to the current selection, then notify each 
     * ListSelectionListener.  Note that index0 doesn't have to be less
     * than or equal to index1.  
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener
     */
    void removeSelectionInterval(int index0, int index1);


    /**
     * Returns the first selected index or -1 if the selection is empty.
     */
    int getMinSelectionIndex();


    /**
     * Returns the last selected index or -1 if the selection is empty.
     */
    int getMaxSelectionIndex();


    /** 
     * Returns true if the specified index is selected.
     */
    boolean isSelectedIndex(int index);

    
    /**
     * Return the first index argument from the most recent call to 
     * setSelectionInterval(), addSelectionInterval() or removeSelectionInterval().
     * The most recent index0 is considered the "anchor" and the most recent
     * index1 is considered the "lead".  Some interfaces display these
     * indices specially, e.g. Windows95 displays the lead index with a 
     * dotted yellow outline.
     * 
     * @see #getLeadSelectionIndex
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     */
    int getAnchorSelectionIndex();


    /**
     * Set the anchor selection index. 
     * 
     * @see #getAnchorSelectionIndex
     */
    void setAnchorSelectionIndex(int index);


    /**
     * Return the second index argument from the most recent call to 
     * setSelectionInterval(), addSelectionInterval() or removeSelectionInterval().
     * 
     * @see #getAnchorSelectionIndex
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     */
    int getLeadSelectionIndex();

    /**
     * Set the lead selection index. 
     * 
     * @see #getLeadSelectionIndex
     */
    void setLeadSelectionIndex(int index);

    /**
     * Change the selection to the empty set.  If this represents
     * a change to the current selection then notify each ListSelectionListener.
     * 
     * @see #addListSelectionListener
     */
    void clearSelection();

    /**
     * Returns true if no indices are selected.
     */
    boolean isSelectionEmpty();
    
    /** 
     * Insert length indices beginning before/after index.  This is typically 
     * called to sync the selection model with a corresponding change
     * in the data model.
     */
    void insertIndexInterval(int index, int length, boolean before);

    /** 
     * Remove the indices in the interval index0,index1 (inclusive) from
     * the selection model.  This is typically called to sync the selection
     * model width a corresponding change in the data model.
     */
    void removeIndexInterval(int index0, int index1);

    /**
     * This property is true if upcoming changes to the value
     * of the model should be considered a single event. For example
     * if the model is being updated in response to a user drag,
     * the value of the valueIsAdjusting property will be set to true
     * when the drag is initiated and be set to false when
     * the drag is finished.  This property allows listeners to 
     * to update only when a change has been finalized, rather
     * than always handling all of the intermediate values.
     * 
     * @param valueIsAdjusting The new value of the property.
     * @see #getValueIsAdjusting
     */
    void setValueIsAdjusting(boolean valueIsAdjusting);

    /**
     * Returns true if the value is undergoing a series of changes.
     * @return true if the value is currently adjusting
     * @see #setValueIsAdjusting
     */
    boolean getValueIsAdjusting();

    /**
     * Set the selection mode. The following selectionMode values are allowed:
     * <ul>
     * <li> <code>SINGLE_SELECTION</code> 
     *   Only one list index can be selected at a time.  In this
     *   mode the setSelectionInterval and addSelectionInterval 
     *   methods are equivalent, and only the second index
     *   argument (the "lead index") is used.
     * <li> <code>SINGLE_INTERVAL_SELECTION</code>
     *   One contiguous index interval can be selected at a time.
     *   In this mode setSelectionInterval and addSelectionInterval 
     *   are equivalent.
     * <li> <code>MULTIPLE_INTERVAL_SELECTION</code>
     *   In this mode, there's no restriction on what can be selected.
     * </ul>
     * 
     * @see #getSelectionMode
     */
    void setSelectionMode(int selectionMode);

    /**
     * Returns the current selection mode.
     * @return The value of the selectionMode property.
     * @see #setSelectionMode
     */
    int getSelectionMode();

    /**
     * Add a listener to the list that's notified each time a change
     * to the selection occurs.
     * 
     * @param x the ListSelectionListener
     * @see #removeListSelectionListener
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     * @see #removeSelectionInterval
     * @see #clearSelection
     * @see #insertIndexInterval
     * @see #removeIndexInterval
     */  
    void addListSelectionListener(ListSelectionListener x);

    /**
     * Remove a listener from the list that's notified each time a 
     * change to the selection occurs.
     * 
     * @param x the ListSelectionListener
     * @see #addListSelectionListener
     */  
    void removeListSelectionListener(ListSelectionListener x);
}

