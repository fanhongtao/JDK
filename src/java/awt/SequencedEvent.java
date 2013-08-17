/*
 * @(#)SequencedEvent.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.util.LinkedList;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

/**
 * A mechanism for ensuring that a series of AWTEvents are executed in a
 * precise order, even across multiple AppContexts. The nested events will be
 * dispatched in the order in which their wrapping SequencedEvents were
 * constructed. The only exception to this rule is if the peer of the target of
 * the nested event was destroyed (with a call to Component.removeNotify)
 * before the wrapping SequencedEvent was able to be dispatched. In this case,
 * the nested event is never dispatched.
 *
 * @version 1.4, 12/03/01
 * @author David Mendenhall
 */
class SequencedEvent extends AWTEvent implements ActiveEvent {
    private static final int ID =
	java.awt.event.FocusEvent.FOCUS_LAST + 1;
    private static final LinkedList list = new LinkedList();

    private final AWTEvent nested;
    private AppContext appContext;
    private boolean disposed;

    /**
     * Constructs a new SequencedEvent which will dispatch the specified
     * nested event.
     *
     * @param nested the AWTEvent which this SequencedEvent's dispatch()
     *        method will dispatch
     */
    SequencedEvent(AWTEvent nested) {
	super(nested.getSource(), ID);
	this.nested = nested;
	synchronized (SequencedEvent.class) {
	    list.add(this);
	}
    }

    /**
     * Dispatches the nested event after all previous nested events have been
     * dispatched or disposed. If this method is invoked before all previous nested events
     * have been dispatched, then this method blocks until such a point is
     * reached.
     * While waiting disposes nested events to disposed AppContext
     */
    public final void dispatch() {
        try {
            boolean shouldBother;
            appContext = AppContext.getAppContext();

            synchronized (SequencedEvent.class) {
                shouldBother = (list.getFirst() != this);
            }

            if (shouldBother) {
                if (EventQueue.isDispatchThread()) {
                    EventDispatchThread edt = (EventDispatchThread)
                        Thread.currentThread();
                    edt.pumpEvents(SentEvent.ID, new Conditional() {
                            public boolean evaluate() {
                                synchronized (SequencedEvent.class) {
                                    SequencedEvent first = (SequencedEvent)list.getFirst();
                                    while(isOwnerAppContextDisposed(first)) {
                                        first.dispose();
                                        first = (SequencedEvent)list.getFirst();
                                    }
                                    return first != SequencedEvent.this
                                        && !disposed;

                                }
                            }
                        });
                } else {
                    synchronized (SequencedEvent.class) {
                        while (list.getFirst() != this && !disposed) {
                            SequencedEvent first = (SequencedEvent)list.getFirst();
                            while(isOwnerAppContextDisposed(first)) {
                                first.dispose();
                                first = (SequencedEvent)list.getFirst();
                            }
                            try {                             
                                SequencedEvent.class.wait(1000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
            }

            if (!disposed) {
                Toolkit.getEventQueue().dispatchEvent(nested);
            }
        } finally {
            dispose();
        }
    }
   
    /**
     * true only if event exists and nested source appContext is disposed.
     */
    private final static boolean isOwnerAppContextDisposed(SequencedEvent se) {
        if (se != null) {
            Object target = se.nested.getSource();
            if (target instanceof Component) {
                return ((Component)target).appContext.isDisposed();
            }
        }
        return false;
    }
         

    /**
     * Disposes of this instance. This method is invoked once the nested event
     * has been dispatched and handled, or when the peer of the target of the
     * nested event has been disposed with a call to Component.removeNotify.
     */
    final void dispose() {
	synchronized (SequencedEvent.class) {
	    disposed = true;

	    // Wake myself up
	    if (appContext != null) {
		SunToolkit.postEvent(appContext, new SentEvent());
	    }
	    SequencedEvent.class.notifyAll();

	    if (list.getFirst() == this) {
		list.removeFirst();

		// Wake up waiting threads
		if (!list.isEmpty()) {
		    SequencedEvent next = (SequencedEvent)list.getFirst();
		    if (next.appContext != null) {
			SunToolkit.postEvent(next.appContext, new SentEvent());
		    }
		}
	    } else {
		list.remove(this);
	    }
	}
    }
}
