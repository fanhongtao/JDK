/*
 * @(#)ItemSelectable.java	1.11 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt;

import java.awt.event.*;

/**
 * The interface for objects which contain a set of items for
 * which zero or more can be selected.
 *
 * @version 1.11 02/02/00
 * @author Amy Fowler
 */

public interface ItemSelectable {

    /**
     * Returns the selected items or null if no items are selected.
     */
    public Object[] getSelectedObjects();

    /**
     * Add a listener to recieve item events when the state of
     * an item changes.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l the listener to recieve events
     * @see ItemEvent
     */    
    public void addItemListener(ItemListener l);

    /**
     * Removes an item listener.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param 	l the listener being removed
     * @see ItemEvent
     */ 
    public void removeItemListener(ItemListener l);
}
