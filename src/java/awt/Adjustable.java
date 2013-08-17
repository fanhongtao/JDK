/*
 * @(#)Adjustable.java	1.6 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

import java.awt.event.*;

/**
 * The interface for objects which have an adjustable numeric value
 * contained within a bounded range of values.
 *
 * @version 1.6 07/01/98
 * @author Amy Fowler
 * @author Tim Prinzing
 */

public interface Adjustable {

    /**
     * The horizontal orientation.  
     */
    public static final int HORIZONTAL = 0; 

    /**
     * The vertical orientation.  
     */
    public static final int VERTICAL = 1;    

    /**
     * Gets the orientation of the adjustable object.
     */
    int getOrientation();

    /**
     * Sets the minimum value of the adjustable object.
     * @param min the minimum value
     */
    void setMinimum(int min);

    /**
     * Gets the minimum value of the adjustable object.
     */
    int getMinimum();

    /**
     * Sets the maximum value of the adjustable object.
     * @param max the maximum value
     */
    void setMaximum(int max);

    /**
     * Gets the maximum value of the adjustable object.
     */
    int getMaximum();

    /**
     * Sets the unit value increment for the adjustable object.
     * @param u the unit increment
     */
    void setUnitIncrement(int u);

    /**
     * Gets the unit value increment for the adjustable object.
     */
    int getUnitIncrement();

    /**
     * Sets the block value increment for the adjustable object.
     * @param b the block increment
     */
    void setBlockIncrement(int b);

    /**
     * Gets the block value increment for the adjustable object.
     */
    int getBlockIncrement();

    /**
     * Sets the length of the proportionl indicator of the
     * adjustable object.
     * @param v the length of the indicator
     */
    void setVisibleAmount(int v);

    /**
     * Gets the length of the propertional indicator.
     */
    int getVisibleAmount();

    /**
     * Sets the current value of the adjustable object. This
     * value must be within the range defined by the minimum and
     * maximum values for this object.
     * @param v the current value 
     */
    void setValue(int v);

    /**
     * Gets the current value of the adjustable object.
     */
    int getValue();

    /**
     * Add a listener to recieve adjustment events when the value of
     * the adjustable object changes.
     * @param l the listener to recieve events
     * @see AdjustmentEvent
     */    
    void addAdjustmentListener(AdjustmentListener l);

    /**
     * Removes an adjustment listener.
     * @param l the listener being removed
     * @see AdjustmentEvent
     */ 
    void removeAdjustmentListener(AdjustmentListener l);

}    
