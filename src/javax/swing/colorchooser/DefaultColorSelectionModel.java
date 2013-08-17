/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;
import java.io.Serializable;

/**
 * A generic implementation of ColorSelectionModel.
 *
 * @version 1.11 02/06/02
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
        if (color != null && !selectedColor.equals(color)) {
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
     * <!-- @see #setRangeProperties    //bad link-->
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
