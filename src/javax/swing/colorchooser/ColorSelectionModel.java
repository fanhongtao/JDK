/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;

/**
 * A model that supports selecting a Color.
 *
 * @version 1.8 02/06/02
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
