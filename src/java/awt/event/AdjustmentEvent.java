/*
 * @(#)AdjustmentEvent.java	1.12 98/07/01
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

package java.awt.event;

import java.awt.Adjustable;
import java.awt.AWTEvent;
import java.awt.Event;

/**
 * The adjustment event emitted by Adjustable objects.
 * @see java.awt.Adjustable
 * @see AdjustmentListener
 *
 * @version 1.12 07/01/98
 * @author Amy Fowler
 */
public class AdjustmentEvent extends AWTEvent {

    /**
     * Marks the first integer id for the range of adjustment event ids.
     */
    public static final int ADJUSTMENT_FIRST 	= 601;

    /**
     * Marks the last integer id for the range of adjustment event ids.
     */
    public static final int ADJUSTMENT_LAST 	= 601;

    /**
     * The adjustment value changed event.
     */
    public static final int ADJUSTMENT_VALUE_CHANGED = ADJUSTMENT_FIRST; //Event.SCROLL_LINE_UP

    /**
     * The unit increment adjustment type.
     */
    public static final int UNIT_INCREMENT	= 1;

    /**
     * The unit decrement adjustment type.
     */
    public static final int UNIT_DECREMENT	= 2;

    /**
     * The block decrement adjustment type.
     */
    public static final int BLOCK_DECREMENT     = 3;

    /**
     * The block increment adjustment type.
     */
    public static final int BLOCK_INCREMENT     = 4;

    /**
     * The absolute tracking adjustment type.
     */
    public static final int TRACK	        = 5;

    Adjustable adjustable;
    int value;
    int adjustmentType;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 5700290645205279921L;

    /**
     * Constructs a AdjustmentEvent object with the specified Adjustable source,
     * type, and value.
     * @param source the Adjustable object where the event originated
     * @id the event type
     * @type the adjustment type 
     * @value the current value of the adjustment
     */
    public AdjustmentEvent(Adjustable source, int id, int type, int value) {
        super(source, id);
	adjustable = source;
        this.adjustmentType = type;
	this.value = value;
    }

    /**
     * Returns the Adjustable object where this event originated.
     */
    public Adjustable getAdjustable() {
        return adjustable;
    }

    /**
     * Returns the current value in the adjustment event.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the type of adjustment which caused the value changed
     * event.
     * @see UNIT_INCREMENT
     * @see UNIT_DECREMENT
     * @see BLOCK_INCREMENT
     * @see BLOCK_DECREMENT
     * @see TRACK
     */
    public int getAdjustmentType() {
        return adjustmentType;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case ADJUSTMENT_VALUE_CHANGED:
              typeStr = "ADJUSTMENT_VALUE_CHANGED";
              break;
          default:
              typeStr = "unknown type";
        }
        String adjTypeStr;
        switch(adjustmentType) {
          case UNIT_INCREMENT:
              adjTypeStr = "UNIT_INCREMENT";
              break;
          case UNIT_DECREMENT:
              adjTypeStr = "UNIT_DECREMENT";
              break;
          case BLOCK_INCREMENT:
              adjTypeStr = "BLOCK_INCREMENT";
              break;
          case BLOCK_DECREMENT:
              adjTypeStr = "BLOCK_DECREMENT";
              break;
          case TRACK:
              adjTypeStr = "TRACK";
              break;
          default:
              adjTypeStr = "unknown type";
        }
        return typeStr + ",adjType="+adjTypeStr + ",value="+value;
    }
}
