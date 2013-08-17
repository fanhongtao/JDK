/*
 * %W% %E%
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

import javax.swing.event.*;

/**
 * A model that supports at most one indexed selection.
 *
 * @version %I% %G%
 * @author Dave Moore
 */
public interface SingleSelectionModel {
    /**
     * Returns the model's selection.
     *
     * @return  the model's selection, or -1 if there is no selection
     * @see     #setSelectedIndex
     */
    public int getSelectedIndex();

    /**
     * Sets the model's selected index to <I>index</I>.
     *
     * Notifies any listeners if the model changes
     *
     * @param an int specifying the model selection
     * @see   #getSelectedIndex
     * @see   #addChangeListener
     */
    public void setSelectedIndex(int index);

    /**
     * Clears the selection (to -1).
     */
    public void clearSelection();

    /**
     * Returns true if the selection model currently has a selected value.
     * @return true if a value is currently selected
     */
    public boolean isSelected();

    /**
     * Adds <I>listener</I> as a listener to changes in the model.
     * @param l the ChangeListener to add
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes <I>listener</I> as a listener to changes in the model.
     * @param l the ChangeListener to remove
     */
    void removeChangeListener(ChangeListener listener);
}
