/*
 * @(#)InputEvent.java	1.12 98/07/01
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

import java.awt.Event;
import java.awt.Component;

/**
 * The root event class for all component-level input events.
 *
 * Input events are delivered to listeners before they are
 * processed normally by the source where they originated.
 * This allows listeners and component subclasses to "consume"
 * the event so that the source will not process them in their
 * default manner.  For example, consuming mousePressed events
 * on a Button component will prevent the Button from being
 * activated.
 *
 * @version 1.12 07/01/98
 * @author Carl Quinn
 */
public abstract class InputEvent extends ComponentEvent {

    /**
     * The shift key modifier constant.
     */
    public static final int SHIFT_MASK = Event.SHIFT_MASK;

    /**
     * The control key modifier constant.
     */
    public static final int CTRL_MASK = Event.CTRL_MASK;

    /** 
     * The meta key modifier constant.
     */
    public static final int META_MASK = Event.META_MASK;

    /** 
     * The alt key modifier constant.
     */
    public static final int ALT_MASK = Event.ALT_MASK;

    /**
     * The mouse button1 modifier constant.
     */
    public static final int BUTTON1_MASK = 1 << 4;

    /**
     * The mouse button2 modifier constant.
     */
    public static final int BUTTON2_MASK = Event.ALT_MASK;

    /** 
     * The mouse button3 modifier constant.
     */
    public static final int BUTTON3_MASK = Event.META_MASK;

    long when;
    int modifiers;

    /**
     * Constructs an InputEvent object with the specified source component,
     * modifiers, and type.
     * @param source the object where the event originated
     * @id the event type
     * @when the time the event occurred
     * @modifiers the modifier keys down while event occurred
     */
    InputEvent(Component source, int id, long when, int modifiers) {
        super(source, id);
        this.when = when;
        this.modifiers = modifiers;
    }

    /**
     * Returns whether or not the Shift modifier is down on this event.
     */
    public boolean isShiftDown() {
        return (modifiers & Event.SHIFT_MASK) != 0;
    }

    /**
     * Returns whether or not the Control modifier is down on this event.
     */
    public boolean isControlDown() {
        return (modifiers & Event.CTRL_MASK) != 0;
    }

    /**
     * Returns whether or not the Meta modifier is down on this event.
     */ 
    public boolean isMetaDown() {
        return (modifiers & Event.META_MASK) != 0;
    }

    /**
     * Returns whether or not the Alt modifier is down on this event.
     */ 
    public boolean isAltDown() {
        return (modifiers & Event.ALT_MASK) != 0;
    }


    /**
     * Returns the timestamp of when this event occurred.
     */
    public long getWhen() {
        return when;
    }

    /**
     * Returns the modifiers flag for this event.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Consumes this event so that it will not be processed
     * in the default manner by the source which originated it.
     */
    public void consume() {
        consumed = true;
    }

    /**
     * Returns whether or not this event has been consumed.
     * @see #consume
     */
    public boolean isConsumed() {
        return consumed;
    }
 
}
