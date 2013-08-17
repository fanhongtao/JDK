/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.event.*;
import java.io.Serializable;
import java.util.EventListener; 

/**
 * A generic implementation of BoundedRangeModel.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.36 02/06/02
 * @author David Kloba
 * @author Hans Muller
 * @see BoundedRangeModel
 */
public class DefaultBoundedRangeModel implements BoundedRangeModel, Serializable
{
    /**
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;

    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();

    private int value = 0;
    private int extent = 0;
    private int min = 0;
    private int max = 100;
    private boolean isAdjusting = false;


    /**
     * Initializes all of the properties with default values.
     * Those values are:
     * <ul>
     * <li><code>value</code> = 0
     * <li><code>extent</code> = 0
     * <li><code>minimum</code> = 0
     * <li><code>maximum</code> = 100
     * <li><code>adjusting</code> = false
     * </ul>
     */
    public DefaultBoundedRangeModel() {
    }


    /**
     * Initializes value, extent, minimum and maximum. Adjusting is false.
     * Throws an IllegalArgumentException if the following constraints
     * aren't satisfied:
     * <pre>
     * min <= value <= value+extent <= max
     * </pre>
     */
    public DefaultBoundedRangeModel(int value, int extent, int min, int max)
    {
        if ((max >= min) && 
	    (value >= min) && 
	    ((value + extent) >= value) &&   
	    ((value + extent) <= max)) {
            this.value = value;
            this.extent = extent;
            this.min = min;
            this.max = max;
        }
        else {
            throw new IllegalArgumentException("invalid range properties");
        }
    }


    /**
     * Return the model's current value.
     * @return the model's current value
     * @see #setValue
     * @see BoundedRangeModel#getValue
     */
    public int getValue() {
      return value; 
    }


    /**
     * Return the model's extent.
     * @return the model's extent
     * @see #setExtent
     * @see BoundedRangeModel#getExtent
     */
    public int getExtent() {
      return extent; 
    }


    /**
     * Return the model's minimum.
     * @return the model's minimum
     * @see #setMinimum
     * @see BoundedRangeModel#getMinimum
     */
    public int getMinimum() {
      return min; 
    }


    /**
     * Return the model's maximum.
     * @return  the model's maximum
     * @see #setMaximum
     * @see BoundedRangeModel#getMaximum
     */
    public int getMaximum() {
        return max; 
    }


    /** 
     * Sets the current value of the model. For a slider, that
     * determines where the knob appears. Ensures that the new 
     * value, <I>n</I> falls within the model's constraints:
     * <pre>
     *     minimum <= value <= value+extent <= maximum
     * </pre>
     * 
     * @see BoundedRangeModel#setValue
     */
    public void setValue(int n) {
        int newValue = Math.max(n, min);
        if(newValue + extent > max) {
            newValue = max - extent; 
        }
        setRangeProperties(newValue, extent, min, max, isAdjusting);
    }


    /** 
     * Sets the extent to <I>n</I> after ensuring that <I>n</I> 
     * is greater than or equal to zero and falls within the model's 
     * constraints:
     * <pre>
     *     minimum <= value <= value+extent <= maximum
     * </pre>
     * @see BoundedRangeModel#setExtent
     */
    public void setExtent(int n) {
        int newExtent = Math.max(0, n);
        if(value + newExtent > max) {
            newExtent = max - value;
        }
        setRangeProperties(value, newExtent, min, max, isAdjusting);
    }


    /** 
     * Sets the minimum to <I>n</I> after ensuring that <I>n</I> 
     * that the other three properties obey the model's constraints:
     * <pre>
     *     minimum <= value <= value+extent <= maximum
     * </pre>
     * @see #getMinimum
     * @see BoundedRangeModel#setMinimum
     */
    public void setMinimum(int n) {
        int newMax = Math.max(n, max);
        int newValue = Math.max(n, value);
        int newExtent = Math.min(newMax - newValue, extent);
        setRangeProperties(newValue, newExtent, n, newMax, isAdjusting);
    }


    /** 
     * Sets the maximum to <I>n</I> after ensuring that <I>n</I> 
     * that the other three properties obey the model's constraints:
     * <pre>
     *     minimum <= value <= value+extent <= maximum
     * </pre>
     * @see BoundedRangeModel#setMaximum
     */
    public void setMaximum(int n) {
        int newMin = Math.min(n, min);
        int newValue = Math.min(n, value);
        int newExtent = Math.min(n - newValue, extent);

        setRangeProperties(newValue, newExtent, newMin, n, isAdjusting);
    }


    /**
     * Sets the valueIsAdjusting property.
     * 
     * @see #getValueIsAdjusting
     * @see #setValue
     * @see BoundedRangeModel#setValueIsAdjusting
     */
    public void setValueIsAdjusting(boolean b) {
        setRangeProperties(value, extent, min, max, b);
    }


    /**
     * Returns true if the value is in the process of changing
     * as a result of actions being taken by the user.
     *
     * @return the value of the valueIsAdjusting property
     * @see #setValue
     * @see BoundedRangeModel#getValueIsAdjusting
     */
    public boolean getValueIsAdjusting() {
        return isAdjusting; 
    }


    /**
     * Sets all of the BoundedRangeModel properties after forcing
     * the arguments to obey the usual constraints:
     * <pre>
     *     minimum <= value <= value+extent <= maximum
     * </pre>
     * <p>
     * At most, one ChangeEvent is generated.
     * 
     * @see BoundedRangeModel#setRangeProperties
     * @see #setValue
     * @see #setExtent
     * @see #setMinimum
     * @see #setMaximum
     * @see #setValueIsAdjusting
     */
    public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting)
    {
        if (newMin > newMax) {
            newMin = newMax;
	}
        if (newValue > newMax) {
            newMax = newValue;
	}
        if (newValue < newMin) {
            newMin = newValue;
	}

	/* Convert the addends to long so that extent can be 
	 * Integer.MAX_VALUE without rolling over the sum.
	 * A JCK test covers this, see bug 4097718.
	 */
        if (((long)newExtent + (long)newValue) > newMax) {
            newExtent = newMax - newValue;
	}
	
        if (newExtent < 0) {
            newExtent = 0;
	}

        boolean isChange =
            (newValue != value) ||
            (newExtent != extent) ||
            (newMin != min) ||
            (newMax != max) ||
            (adjusting != isAdjusting);

        if (isChange) {
            value = newValue;
            extent = newExtent;
            min = newMin;
            max = newMax;
            isAdjusting = adjusting;

            fireStateChanged();
        }
    }


    /**
     * Adds a ChangeListener.  The change listeners are run each
     * time any one of the Bounded Range model properties changes.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     * @see BoundedRangeModel#addChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    

    /**
     * Removes a ChangeListener.
     *
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     * @see BoundedRangeModel#removeChangeListener
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

    
    /**
     * Returns a string that displays all of the BoundedRangeModel properties.
     */
    public String toString()  {
        String modelString =
            "value=" + getValue() + ", " +
            "extent=" + getExtent() + ", " +
            "min=" + getMinimum() + ", " +
            "max=" + getMaximum() + ", " +
            "adj=" + getValueIsAdjusting();

        return getClass().getName() + "[" + modelString + "]";
    }

    /**
     * Return an array of all the listeners of the given type that 
     * were added to this model. 
     *
     * @returns all of the objects recieving <em>listenerType</em> notifications 
     *          from this model
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }
}

