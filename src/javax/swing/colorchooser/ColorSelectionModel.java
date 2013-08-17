/*
 * @(#)ColorSelectionModel.java	1.5 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.colorchooser;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;

/**
 * A model that supports selecting a Color.
 *
 * @version 1.5 08/26/98
 * @author Steve Wilson
 *
 * @see java.awt.Color
 */
public interface ColorSelectionModel {
    /**
     * @return  the model's selection.
     * @see     #setSelectedColor
     */
    Color getSelectedColor();

    /**
     * Sets the model's selected color to <I>color</I>.
     *
     * Notifies any listeners if the model changes
     *
     * @see   #getSelectedColor
     * @see   #addChangeListener
     */
    void setSelectedColor(Color color);

    /**
     * Adds <I>listener</I> as a listener to changes in the model.
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes <I>listener</I> as a listener to changes in the model.
     */
    void removeChangeListener(ChangeListener listener);
}
