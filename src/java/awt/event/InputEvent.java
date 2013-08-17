/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @author Carl Quinn
 * @version 1.23 02/06/02
 *
 * @see KeyEvent
 * @see KeyAdapter
 * @see MouseEvent
 * @see MouseAdapter
 * @see MouseMotionAdapter
 *
 * @since 1.1
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
     * The alt-graph key modifier constant.
     */
    public static final int ALT_GRAPH_MASK = 1 << 5;

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

    /**
     * The input events Time stamp.  The time stamp is in
     * UTC format that indicates when the input event was
     * created.
     *
     * @serial
     * @see getWhen()
     */
    long when;
    /**
     * The state of the modifier key at the time the input
     * event was fired.
     *
     * @serial
     * @see getModifiers()
     * @see java.awt.event.MouseEvent
     */
    int modifiers;

    static {
        /* ensure that the necessary native libraries are loaded */
	NativeLibLoader.loadLibraries();
	initIDs();
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       accessed from C.
     */
    private static native void initIDs();

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
     * Returns whether or not the Alt-Graph modifier is down on this event.
     */
    public boolean isAltGraphDown() {
        return (modifiers & InputEvent.ALT_GRAPH_MASK) != 0;
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

    // state serialization compatibility with JDK 1.1
    static final long serialVersionUID = -2482525981698309786L;
}
