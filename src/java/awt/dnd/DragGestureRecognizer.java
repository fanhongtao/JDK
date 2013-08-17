/*
 * @(#)DragGestureRecognizer.java	1.2 98/06/29
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

package java.awt.dnd;

import java.awt.event.InputEvent;
import java.awt.Component;
import java.awt.Point;

import java.util.TooManyListenersException;
import java.util.ArrayList;

/**
 * <p>
 * The DragGestureRecognizer is an abstract base class for the specification
 * of a platform-dependent listener that can be associated with a particular
 * Component in order to identify platform-dependent drag and drop action
 * initiating gestures.
 * </p>
 * <p>
 * The appropriate DragGestureRecognizer subclass is obtained from the
 * DragSource asssociated with a particular Component, or from the Toolkit
 * object via its createDragGestureRecognizer() method.
 * <p>
 * <p>
 * Once the DragGestureRecognizer is associated with a particular Component
 * it will register the appropriate Listener interfaces on that Component
 * in order to track the input Events delivered to the Component.
 * <p>
 * <p>
 * Once the DragGestureRecognizer identifies a sequence of Events on the
 * Component as a Drag and Drop action initiating gesture it will notify
 * its unicast DragGestureListener by invoking its gestureRecognized() method.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.2
 * @see java.awt.dnd.DragGestureListener
 * @see java.awt.dnd.DragGestureEvent
 * @see java.awt.dnd.DragSource
 */

public abstract class DragGestureRecognizer {

    /**
     * construct a new DragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     * @param c   The Component to observe
     * @param sa  The source drag actions
     * @param dgl The DragGestureRecognizer to notify when a gesture is detected
     *
     */

    protected DragGestureRecognizer(DragSource ds, Component c, int sa, DragGestureListener dgl) {
	super();

	if (ds == null) throw new IllegalArgumentException("null DragSource");

	dragSource    = ds;
	component     = c;
	sourceActions = sa & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK);

	try {
	    if (dgl != null) addDragGestureListener(dgl);
	} catch (TooManyListenersException tmle) {
	    // cant happen ...
	}
    }

    /**
     * construct a new DragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     * @param sa  The source drag actions
     * @param c   The Component to observe
     */

    protected DragGestureRecognizer(DragSource ds, Component c, int sa) {
	this(ds, c, sa, null);
    }

    /**
     * construct a new DragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     * @param c   The Component to observe
     */

    protected DragGestureRecognizer(DragSource ds, Component c) {
	this(ds, c, DnDConstants.ACTION_NONE);
    }

    /**
     * construct a new DragGestureRecognizer
     *
     * @param ds  The DragSource for the Component c
     */

    protected DragGestureRecognizer(DragSource ds) {
	this(ds, null);
    }

    /**
     * register this DragGestureRecognizer's Listeners with the Component
     *
     * subclasses must override this method
     */

    protected abstract void registerListeners();

    /**
     * unregister this DragGestureRecognizer's Listeners with the Component
     *
     * subclasses must override this method
     */

    protected abstract void unregisterListeners();

    /**
     * @return the DragSource
     */

    public DragSource getDragSource() { return dragSource; }

    /**
     * @return The Component this DragGestureRecognizer is associated with
     */

    public synchronized Component getComponent() { return component; }

    /**
     * set the Component that the DragGestureRecognizer is associated with
     *
     * registerListeners() and unregisterListeners() are called as a side
     * effect as appropriate.
     *
     * @param c The Component or null
     */

    public synchronized void setComponent(Component c) {
	if (component != null && dragGestureListener != null)
	    unregisterListeners();

	component = c;

	if (component != null && dragGestureListener != null)
	    registerListeners();
    }

    /**
     * @return the currently permitted source actions
     */

    public synchronized int getSourceActions() { return sourceActions; }

    /**
     * set the permitted source drag actions
     */

    public synchronized void setSourceActions(int actions) {
	sourceActions = actions & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK);
    }

    /**
     * @return the initial event that triggered the gesture
     */

    public InputEvent getTriggerEvent() { return events.isEmpty() ? null : (InputEvent)events.get(0); }

    /**
     * reset the Recognizer, if its currently recognizing a gesture, ignore
     * it.
     */

    public void resetRecognizer() { events.clear(); }

    /**
     * register a new DragGestureListener
     *
     * @throw java.util.TooManyListenersException
     */

    public synchronized void addDragGestureListener(DragGestureListener dgl) throws TooManyListenersException {
	if (dragGestureListener != null)
	    throw new TooManyListenersException();
	else {
	    dragGestureListener = dgl;

	    if (component != null) registerListeners();
	}
    }


    /**
     * unregister the current DragGestureListener
     *
     * @throw IllegalArgumentException
     */

    public synchronized void removeDragGestureListener(DragGestureListener dgl) {
	if (dragGestureListener == null || !dragGestureListener.equals(dgl))
	    throw new IllegalArgumentException();
	else {
	    dragGestureListener = null;

	    if (component != null) unregisterListeners();
	}
    }

    /**
     * notify the DragGestureListener that a Drag and Drop initiating
     * gesture has occurred. Then reset the state of the Recognizer.
     *
     * @param dragAction The action initially selected by the users gesture
     * @param p          The point (in Component coords) where the gesture originated
     */


    protected synchronized void fireDragGestureRecognized(int dragAction, Point p) {
	if (dragGestureListener != null) {
	    dragGestureListener.dragGestureRecognized(new DragGestureEvent(this, dragAction, p, events));
	}
	events.clear();
    }

    /**
     * Listeners registered on the Component by this Recognizer shall record
     * all Events that are recognized as part of the series of Events that go
     * to comprise a Drag and Drop initiating gesture via this API.
     *
     * this state is cleared when a the gesture is completed, after the
     * DragGestureListener is notified.
     */

    protected synchronized void appendEvent(InputEvent awtie) {
	events.add(awtie);
    }

    /*
     * fields
     */

    protected DragSource          dragSource;
    protected Component           component;
    protected DragGestureListener dragGestureListener;
    protected int		  sourceActions;

    protected ArrayList	          events = new ArrayList(1);
}
