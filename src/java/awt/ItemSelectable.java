/*
 * @(#)ItemSelectable.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.event.*;

/**
 * The interface for objects which contain a set of items for
 * which zero or more can be selected.
 *
 * @version 1.6 12/10/01
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
     * @param l the listener to recieve events
     * @see ItemEvent
     */    
    public void addItemListener(ItemListener l);

    /**
     * Removes an item listener.
     * @param l the listener being removed
     * @see ItemEvent
     */ 
    public void removeItemListener(ItemListener l);

}
