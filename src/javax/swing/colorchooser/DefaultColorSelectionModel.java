/*
 * @(#)DefaultColorSelectionModel.java	1.5 98/08/26
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
import java.io.Serializable;

/**
 * A generic implementation of ColorSelectionModel.
 *
 * @version 1.5 08/26/98
 * @author Steve Wilson
 *
 * @see java.awt.Color
 */
public class DefaultColorSelectionModel implements ColorSelectionModel, Serializable {

    /**
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;

    protected EventListenerList listenerList = new EventListenerList();

    private Color selectedColor;

    /**
      * Default constructor.  Initializes selectedColor to Color.white
      */
    public DefaultColorSelectionModel() {
        selectedColor = Color.white;
    }

    /**
      *Initializes selectedColor to <I>color</I>
      */
    public DefaultColorSelectionModel(Color color) {
        selectedColor = color;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color color) {
        if ( !selectedColor.equals(color) ) {
	    selectedColor = color;
	    fireStateChanged();
        }
    }


    /**
     * Adds a ChangeListener to the model.
     */
    public void addChangeListener(ChangeListener l) {
	listenerList.add(ChangeListener.class, l);
    }

    /**
     * Removes a ChangeListener from the model.
     */
    public void removeChangeListener(ChangeListener l) {
	listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Run each ChangeListeners stateChanged() method.
     *
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged()
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }

}
