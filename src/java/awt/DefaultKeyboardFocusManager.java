/*
 * @(#)DefaultKeyboardFocusManager.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import sun.awt.AppContext;
import sun.awt.SunToolkit;

/**
 * The default KeyboardFocusManager for AWT applications. Focus traversal is
 * done in response to a Component's focus traversal keys, and using a
 * Container's FocusTraversalPolicy.
 *
 * @author David Mendenhall
 * @version 1.21, 01/23/03
 *
 * @see FocusTraversalPolicy
 * @see Component#setFocusTraversalKeys
 * @see Component#getFocusTraversalKeys
 * @since 1.4
 */
public class DefaultKeyboardFocusManager extends KeyboardFocusManager {

    private Window realOppositeWindow;
    private Component realOppositeComponent;
    private int inSendMessage;
    private LinkedList enqueuedKeyEvents = new LinkedList(),
	typeAheadMarkers = new LinkedList();
    private boolean consumeNextKeyTyped;

    private static class TypeAheadMarker {
	long after;
	Component untilFocused;

	TypeAheadMarker(long after, Component untilFocused) {
	    this.after = after;
	    this.untilFocused = untilFocused;
	}
    }

    private Window getOwningFrameDialog(Window window) {
        while (window != null && !(window instanceof Frame ||
				   window instanceof Dialog)) {
	    window = (Window)window.getParent();
	}
	return window;
    }

    /*
     * This series of restoreFocus methods is used for recovering from a
     * rejected focus or activation change. Rejections typically occur when
     * the user attempts to focus a non-focusable Component or Window.
     */
    private void restoreFocus(FocusEvent fe, Window newFocusedWindow) {
        Component realOppositeComponent = this.realOppositeComponent;
        Component vetoedComponent = fe.getComponent();
        if (newFocusedWindow != null && restoreFocus(newFocusedWindow, 
                                                     vetoedComponent, false))
        {
        } else if (realOppositeComponent != null &&
                   restoreFocus(realOppositeComponent, false)) {
        } else if (fe.getOppositeComponent() != null &&
                   restoreFocus(fe.getOppositeComponent(), false)) {
        } else {
            clearGlobalFocusOwner();
        }
    }
    private void restoreFocus(WindowEvent we) {
        Window realOppositeWindow = this.realOppositeWindow;
        if (realOppositeWindow != null && restoreFocus(realOppositeWindow,
                                                       null, false)) {
        } else if (we.getOppositeWindow() != null &&
                   restoreFocus(we.getOppositeWindow(), null, false)) {
        } else {
            clearGlobalFocusOwner();
        }
    }
    private boolean restoreFocus(Window aWindow, Component vetoedComponent, 
                                 boolean clearOnFailure) {
        Component toFocus =
            KeyboardFocusManager.getMostRecentFocusOwner(aWindow);
        if (toFocus != null && toFocus != vetoedComponent && restoreFocus(toFocus, false)) {
            return true;
        } else if (clearOnFailure) {
            clearGlobalFocusOwner();
            return true;
        } else {
            return false;
        }
    }
    private boolean restoreFocus(Component toFocus, boolean clearOnFailure) {
        if (toFocus.isShowing() && toFocus.isFocusable() &&
            toFocus.requestFocus(false)) {
            return true;
        } else if (toFocus.nextFocusHelper()) {
            return true;
        } else if (clearOnFailure) {
            clearGlobalFocusOwner();
            return true;
        } else {
            return false;
        }
    }

    /**
     * A special type of SentEvent which updates a counter in the target
     * KeyboardFocusManager if it is an instance of
     * DefaultKeyboardFocusManager.
     */
    private static class DefaultKeyboardFocusManagerSentEvent
	extends SentEvent
    {
	public DefaultKeyboardFocusManagerSentEvent(AWTEvent nested,
						    AppContext toNotify) {
	    super(nested, toNotify);
	}
	public final void dispatch() {
	    KeyboardFocusManager manager =
		KeyboardFocusManager.getCurrentKeyboardFocusManager();
	    DefaultKeyboardFocusManager defaultManager =
		(manager instanceof DefaultKeyboardFocusManager)
		? (DefaultKeyboardFocusManager)manager
		: null;

	    if (defaultManager != null) {
		synchronized (defaultManager) {
		    defaultManager.inSendMessage++;
		}
	    }

	    super.dispatch();

	    if (defaultManager != null) {
		synchronized (defaultManager) {
		    defaultManager.inSendMessage--;
		}
	    }
	}
    }

    /**
     * Sends a synthetic AWTEvent to a Component. If the Component is in
     * the current AppContext, then the event is immediately dispatched.
     * If the Component is in a different AppContext, then the event is
     * posted to the other AppContext's EventQueue, and this method blocks
     * until the event is handled or target AppContext is disposed.
     * Returns true if successfuly dispatched event, false if failed 
     * to dispatch.
     */
    private boolean sendMessage(Component target, AWTEvent e) {
	AppContext myAppContext = AppContext.getAppContext();
        final AppContext targetAppContext = target.appContext;
	final SentEvent se =
	    new DefaultKeyboardFocusManagerSentEvent(e, myAppContext);
    
	if (myAppContext == targetAppContext) {
	    se.dispatch();
	} else {
            if (targetAppContext.isDisposed()) {
                return false;
            }
	    SunToolkit.postEvent(targetAppContext, se);
	    if (EventQueue.isDispatchThread()) {
		EventDispatchThread edt = (EventDispatchThread)
		    Thread.currentThread();
		edt.pumpEvents(SentEvent.ID, new Conditional() {
			public boolean evaluate() {
			    return !se.dispatched && !targetAppContext.isDisposed();
			}
		    });
	    } else {
		synchronized (se) {
		    while (!se.dispatched && !targetAppContext.isDisposed()) {
			try {
			    se.wait(1000);
			} catch (InterruptedException ie) {
			    break;
			}
		    }
		}
	    }
	}
        return se.dispatched;
    }

    /**
     * This method is called by the AWT event dispatcher requesting that the
     * current KeyboardFocusManager dispatch the specified event on its behalf.
     * DefaultKeyboardFocusManagers dispatch all FocusEvents, all WindowEvents
     * related to focus, and all KeyEvents. These events are dispatched based
     * on the KeyboardFocusManager's notion of the focus owner and the focused
     * and active Windows, sometimes overriding the source of the specified
     * AWTEvent. If this method returns <code>false</code>, then the AWT event
     * dispatcher will attempt to dispatch the event itself.
     *
     * @param e the AWTEvent to be dispatched
     * @return <code>true</code> if this method dispatched the event;
     *         <code>false</code> otherwise
     */
    public boolean dispatchEvent(AWTEvent e) {

        switch (e.getID()) {
            case WindowEvent.WINDOW_GAINED_FOCUS: {
	        WindowEvent we = (WindowEvent)e;
                Window oldFocusedWindow = getGlobalFocusedWindow();
                Window newFocusedWindow = we.getWindow();
		if (newFocusedWindow == oldFocusedWindow) {
		    break;
		}

		// If there exists a current focused window, then notify it
		// that it has lost focus.
		if (oldFocusedWindow != null) {
		    boolean isEventDispatched = 
                        sendMessage(oldFocusedWindow,
                                new WindowEvent(oldFocusedWindow,
                                                WindowEvent.WINDOW_LOST_FOCUS,
                                                newFocusedWindow));
                    // Failed to dispatch, clear by ourselfves
                    if (!isEventDispatched) {
                        setGlobalFocusOwner(null);
                        setGlobalFocusedWindow(null);
                    }
		}

		// Because the native libraries do not post WINDOW_ACTIVATED
		// events, we need to synthesize one if the active Window
		// changed.
		Window newActiveWindow =
		    getOwningFrameDialog(newFocusedWindow);
		Window currentActiveWindow = getGlobalActiveWindow();
		if (newActiveWindow != currentActiveWindow) {
		    sendMessage(newActiveWindow,
				new WindowEvent(newActiveWindow,
						WindowEvent.WINDOW_ACTIVATED,
						currentActiveWindow));
		    if (newActiveWindow != getGlobalActiveWindow()) {
		        // Activation change was rejected. Unlikely, but
		        // possible.
			restoreFocus(we);
			break;
		    }
		}

		setGlobalFocusedWindow(newFocusedWindow);

		if (newFocusedWindow != getGlobalFocusedWindow()) {
		    // Focus change was rejected. Will happen if
		    // newFocusedWindow is not a focusable Window.
		    restoreFocus(we);
		    break;
		}

		// Restore focus to the Component which last held it. We do
		// this here so that client code can override our choice in
		// a WINDOW_GAINED_FOCUS handler.
		//
		// Make sure that the focus change request doesn't change the
		// focused Window in case we are no longer the focused Window
		// when the request is handled.
                if (inSendMessage == 0) {
                    // Identify which Component should initially gain focus 
                    // in the Window.
                    //
                    // * If we're in SendMessage, then this is a synthetic
                    //   WINDOW_GAINED_FOCUS message which was generated by a
                    //   the FOCUS_GAINED handler. Allow the Component to 
                    //   which the FOCUS_GAINED message was targeted to 
                    //   receive the focus.
                    // * Otherwise, look up the correct Component here. 
                    //   We don't use Window.getMostRecentFocusOwner because
                    //   window is focused now and 'null' will be returned


                    // Calculating of most recent focus owner and focus 
                    // request should be synchronized on KeyboardFocusManager.class
                    // to prevent from thread race when user will request
                    // focus between calculation and our request.
                    // But if focus transfer is synchronous, this synchronization
                    // may cause deadlock, thus we don't synchronize this block.
                    Component toFocus = KeyboardFocusManager.
                        getMostRecentFocusOwner(newFocusedWindow);
                    if ((toFocus == null) && 
                        newFocusedWindow.isFocusableWindow()) 
                    {
                        toFocus = newFocusedWindow.getFocusTraversalPolicy().
                            getInitialComponent(newFocusedWindow);
                    }
                    Component tempLost = null;
                    synchronized(KeyboardFocusManager.class) {
                        tempLost = newFocusedWindow.setTemporaryLostComponent(null);
                    }

                    // The component which last has the focus when this window was focused
                    // should receive focus first
                    if (tempLost != null) {
                        tempLost.requestFocusInWindow();                        
                    }

                    if (toFocus != null && toFocus != tempLost) {
                        // If there is a component which requested focus when this window
                        // was inactive it expects to receive focus after activation.
                        toFocus.requestFocusInWindow();
                    }
                }

		Window realOppositeWindow = this.realOppositeWindow;
		if (realOppositeWindow != we.getOppositeWindow()) {
		    we = new WindowEvent(newFocusedWindow,
					 WindowEvent.WINDOW_GAINED_FOCUS,
					 realOppositeWindow);
		}
		return typeAheadAssertions(newFocusedWindow, we);
	    }

	    case WindowEvent.WINDOW_ACTIVATED: {
	        WindowEvent we = (WindowEvent)e;
	        Window oldActiveWindow = getGlobalActiveWindow();
	        Window newActiveWindow = we.getWindow();
		if (oldActiveWindow == newActiveWindow) {
		    break;
		}

		// If there exists a current active window, then notify it that
		// it has lost activation.
		if (oldActiveWindow != null) {
                    boolean isEventDispatched = 
                        sendMessage(oldActiveWindow,
                                new WindowEvent(oldActiveWindow,
                                                WindowEvent.WINDOW_DEACTIVATED,
                                                newActiveWindow));
                    // Failed to dispatch, clear by ourselfves
                    if (!isEventDispatched) {
                        setGlobalActiveWindow(null);
                    }
		    if (getGlobalActiveWindow() != null) {
			// Activation change was rejected. Unlikely, but
			// possible.
			break;
		    }
		}

		setGlobalActiveWindow(newActiveWindow);

		if (newActiveWindow != getGlobalActiveWindow()) {
		    // Activation change was rejected. Unlikely, but
		    // possible.
		    break;
		}

		return typeAheadAssertions(newActiveWindow, we);
	    }

            case FocusEvent.FOCUS_GAINED: {
	        FocusEvent fe = (FocusEvent)e;
                Component oldFocusOwner = getGlobalFocusOwner();
                Component newFocusOwner = fe.getComponent();
		if (oldFocusOwner == newFocusOwner) {
		    break;
		}

		// If there exists a current focus owner, then notify it that
		// it has lost focus.
		if (oldFocusOwner != null) {
                    boolean isEventDispatched =
                        sendMessage(oldFocusOwner,
                                    new FocusEvent(oldFocusOwner,
                                                   FocusEvent.FOCUS_LOST,
                                                   fe.isTemporary(),
                                                   newFocusOwner));
                    // Failed to dispatch, clear by ourselfves
                    if (!isEventDispatched) {
                        setGlobalFocusOwner(null);
                        if (!fe.isTemporary()) {
                            setGlobalPermanentFocusOwner(null);
                        }
                    }
		}
		    
		// Because the native windowing system has a different notion
		// of the current focus and activation states, it is possible
		// that a Component outside of the focused Window receives a
		// FOCUS_GAINED event. We synthesize a WINDOW_GAINED_FOCUS
		// event in that case.
		Component newFocusedWindow = newFocusOwner;
		while (newFocusedWindow != null &&
		       !(newFocusedWindow instanceof Window)) {
		    newFocusedWindow = newFocusedWindow.parent;
		}
		Window currentFocusedWindow = getGlobalFocusedWindow();
		if (newFocusedWindow != null &&
		    newFocusedWindow != currentFocusedWindow)
		{
		    sendMessage(newFocusedWindow,
				new WindowEvent((Window)newFocusedWindow,
					WindowEvent.WINDOW_GAINED_FOCUS,
						currentFocusedWindow));
		    if (newFocusedWindow != getGlobalFocusedWindow()) {
		        // Focus change was rejected. Will happen if
		        // newFocusedWindow is not a focusable Window.

			// Need to recover type-ahead, but don't bother
			// restoring focus. That was done by the
			// WINDOW_GAINED_FOCUS handler
			dequeueKeyEvents(-1, newFocusOwner);
		        break;
		    }
		}
		 
		setGlobalFocusOwner(newFocusOwner);

		if (newFocusOwner != getGlobalFocusOwner()) {
		    // Focus change was rejected. Will happen if
		    // newFocusOwner is not focus traversable.
		    dequeueKeyEvents(-1, newFocusOwner);
		    restoreFocus(fe, (Window)newFocusedWindow);
		    break;
		}

		if (!fe.isTemporary()) {
		    setGlobalPermanentFocusOwner(newFocusOwner);

		    if (newFocusOwner != getGlobalPermanentFocusOwner()) {
			// Focus change was rejected. Unlikely, but possible.
			dequeueKeyEvents(-1, newFocusOwner);
			restoreFocus(fe, (Window)newFocusedWindow);
			break;
		    }
		}

		Component realOppositeComponent = this.realOppositeComponent;
		if (realOppositeComponent != null &&
                    realOppositeComponent != fe.getOppositeComponent()) {
		    fe = new FocusEvent(newFocusOwner,
					FocusEvent.FOCUS_GAINED,
					fe.isTemporary(),
					realOppositeComponent); 
		}
		return typeAheadAssertions(newFocusOwner, fe);
	    }

	    case FocusEvent.FOCUS_LOST: {
	        FocusEvent fe = (FocusEvent)e;
		Component currentFocusOwner = getGlobalFocusOwner();
		if (currentFocusOwner == null) {
		    break;
		}
		// Ignore cases where a Component loses focus to itself.
		// If we make a mistake because of retargeting, then the
		// FOCUS_GAINED handler will correct it.
		if (currentFocusOwner == fe.getOppositeComponent()) {
		    break;
		}

		setGlobalFocusOwner(null);

		if (getGlobalFocusOwner() != null) {
		    // Focus change was rejected. Unlikely, but possible.
		    restoreFocus(currentFocusOwner, true);
		    break;
		}

		if (!fe.isTemporary()) {                    
		    setGlobalPermanentFocusOwner(null);

		    if (getGlobalPermanentFocusOwner() != null) {
			// Focus change was rejected. Unlikely, but possible.
			restoreFocus(currentFocusOwner, true);
			break;
		    }
		} else {
                    Window owningWindow = getContainingWindow(currentFocusOwner);
                    if (owningWindow != null) {
                        owningWindow.setTemporaryLostComponent(currentFocusOwner);
                    }
                }
		
		fe.setSource(currentFocusOwner);

                realOppositeComponent = (fe.getOppositeComponent() != null)
                    ? currentFocusOwner : null;

		return typeAheadAssertions(currentFocusOwner, fe);
	    }

	    case WindowEvent.WINDOW_DEACTIVATED: {
	        WindowEvent we = (WindowEvent)e;
		Window currentActiveWindow = getGlobalActiveWindow();
		if (currentActiveWindow == null) {
		    break;
		}

		setGlobalActiveWindow(null);
		if (getGlobalActiveWindow() != null) {
		    // Activation change was rejected. Unlikely, but possible.
		    break;
		}

		we.setSource(currentActiveWindow);
		return typeAheadAssertions(currentActiveWindow, we);
	    }

	    case WindowEvent.WINDOW_LOST_FOCUS: {
	        WindowEvent we = (WindowEvent)e;
		Window currentFocusedWindow = getGlobalFocusedWindow();
		if (currentFocusedWindow == null) {
		    break;
		}

	        // Special case -- if the native windowing system posts an
		// event claiming that the active Window has lost focus to the
		// focused Window, then discard the event. This is an artifact
		// of the native windowing system not knowing which Window is
		// really focused.
		Window losingFocusWindow = we.getWindow();
		Window activeWindow = getGlobalActiveWindow();
		Window oppositeWindow = we.getOppositeWindow();
		if (inSendMessage == 0 && losingFocusWindow == activeWindow &&
		    oppositeWindow == currentFocusedWindow)
		{
		    break;
		}

		Component currentFocusOwner = getGlobalFocusOwner();
		if (currentFocusOwner != null) {
		    // The focus owner should always receive a FOCUS_LOST event
		    // before the Window is defocused.
                    Component oppositeComp = null;
                    if (oppositeWindow != null) {
                        oppositeComp = oppositeWindow.getTemporaryLostComponent();
                        if (oppositeComp == null) {
                            oppositeComp = oppositeWindow.getMostRecentFocusOwner();
                        }
                    }
                    if (oppositeComp == null) {
                        oppositeComp = oppositeWindow;
                    }
		    sendMessage(currentFocusOwner,
				new FocusEvent(currentFocusOwner,
					       FocusEvent.FOCUS_LOST,
					       true,
					       oppositeComp));
		}

		setGlobalFocusedWindow(null);
		if (getGlobalFocusedWindow() != null) {
		    // Focus change was rejected. Unlikely, but possible.
                    restoreFocus(currentFocusedWindow, null, true);
		    break;
		}

		we.setSource(currentFocusedWindow);
		realOppositeWindow = (oppositeWindow != null)
		    ? currentFocusedWindow
		    : null;
		typeAheadAssertions(currentFocusedWindow, we);

		if (oppositeWindow == null) {
		    // Then we need to deactive the active Window as well.
		    // No need to synthesize in other cases, because
		    // WINDOW_ACTIVATED will handle it if necessary.
		    sendMessage(activeWindow,
				new WindowEvent(activeWindow,
						WindowEvent.WINDOW_DEACTIVATED,
						null));
		    if (getGlobalActiveWindow() != null) {
		        // Activation change was rejected. Unlikely,
			// but possible.
                        restoreFocus(currentFocusedWindow, null, true);
		    }
		}
		break;
	    }

            case KeyEvent.KEY_TYPED:
            case KeyEvent.KEY_PRESSED:
            case KeyEvent.KEY_RELEASED:
		return typeAheadAssertions(null, e);

            default:
                return false;
        }

        return true;
    }

    /**
     * Called by <code>dispatchEvent</code> if no other
     * KeyEventDispatcher in the dispatcher chain dispatched the KeyEvent, or
     * if no other KeyEventDispatchers are registered. If the event has not
     * been consumed, its target is enabled, and the focus owner is not null,
     * this method dispatches the event to its target. This method will also
     * subsequently dispatch the event to all registered
     * KeyEventPostProcessors. After all this operations are finished, 
     * the event is passed to peers for processing.
     * <p>
     * In all cases, this method returns <code>true</code>, since
     * DefaultKeyboardFocusManager is designed so that neither
     * <code>dispatchEvent</code>, nor the AWT event dispatcher, should take
     * further action on the event in any situation.
     *
     * @param e the KeyEvent to be dispatched
     * @return <code>true</code>
     * @see Component#dispatchEvent
     */
    public boolean dispatchKeyEvent(KeyEvent e) {
        Component focusOwner = getFocusOwner();
        if (focusOwner != null && focusOwner.isShowing() &&
            focusOwner.isFocusable() && focusOwner.isEnabled()) {
            if (!e.isConsumed()) {
                Component comp = e.getComponent();
                if (comp != null && comp.isEnabled()) {
                    redispatchEvent(comp, e);
                }
            }
        }
        boolean stopPostProcessing = false;
        java.util.List processors = getKeyEventPostProcessors();
        if (processors != null) {
            for (java.util.Iterator iter = processors.iterator();
                 !stopPostProcessing && iter.hasNext(); )
            {
                stopPostProcessing = (((KeyEventPostProcessor)(iter.next())).
                            postProcessKeyEvent(e));
            }
        }
        if (!stopPostProcessing) {
            postProcessKeyEvent(e);
        }

        // Allow the peer to process KeyEvent
        Component source = e.getComponent();
        ComponentPeer peer = source.getPeer();

        if (peer == null || peer instanceof LightweightPeer) {
            // if focus owner is lightweight then its native container 
            // processes event
            Container target = source.getNativeContainer();
            if (target != null) {
                peer = target.getPeer();
            }
        }
        if (peer != null) {
            peer.handleEvent(e);
        }

        return true;
    }

    /**
     * This method will be called by <code>dispatchKeyEvent</code>. It will
     * handle any unconsumed KeyEvents that map to an AWT
     * <code>MenuShortcut</code> by consuming the event and activating the
     * shortcut.
     *
     * @param e the KeyEvent to post-process
     * @return <code>true</code>
     * @see #dispatchKeyEvent
     * @see MenuShortcut
     */
    public boolean postProcessKeyEvent(KeyEvent e) {
        if (!e.isConsumed()) {
            Component target = e.getComponent();
            Container p = (Container)
                (target instanceof Container ? target : target.getParent());
            if (p != null) {
                p.postProcessKeyEvent(e);
            }
        }
        return true;
    }

    private void pumpApprovedKeyEvents() {
	KeyEvent ke;
        if (requestCount() == 0) {
            synchronized(this) {
                typeAheadMarkers.clear();
            }
        }
	do {
	    ke = null;
	    synchronized (this) {
		if (enqueuedKeyEvents.size() != 0) {
		    ke = (KeyEvent)enqueuedKeyEvents.getFirst();
		    if (typeAheadMarkers.size() != 0) {
			TypeAheadMarker marker = (TypeAheadMarker)
			    typeAheadMarkers.getFirst();
			if (ke.getWhen() > marker.after) {
			    ke = null;
			}
		    }
		    if (ke != null) {
			enqueuedKeyEvents.removeFirst();
		    }
		}
	    }
	    if (ke != null) {
		preDispatchKeyEvent(ke);
	    }
	} while (ke != null);
    }
    private boolean typeAheadAssertions(Component target, AWTEvent e) {

	// Clear any pending events here as well as in the FOCUS_GAINED
	// handler. We need this call here in case a marker was removed in
	// response to a call to dequeueKeyEvents.
	pumpApprovedKeyEvents();

	switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
            case KeyEvent.KEY_PRESSED:
	    case KeyEvent.KEY_RELEASED: {
		KeyEvent ke = (KeyEvent)e;

		synchronized (this) {
		    if (typeAheadMarkers.size() != 0) {
			TypeAheadMarker marker = (TypeAheadMarker)
			    typeAheadMarkers.getFirst();
			if (ke.getWhen() > marker.after) {
			    enqueuedKeyEvents.addLast(ke);
			    return true;
			}
		    }
		}

		// KeyEvent was posted before focus change request
		return preDispatchKeyEvent(ke);
	    }

	    case FocusEvent.FOCUS_GAINED:
		// Search the marker list for the first marker tied to the
		// Component which just gained focus. Then remove that marker
		// and any markers which immediately follow and are tied to
		// the same Component. This handles the case where multiple
		// focus requests were made for the same Component in a row.
		// Since FOCUS_GAINED events will not be generated for these
		// additional requests, we need to clear those markers too.
		synchronized (this) {
		    boolean found = false;
		    for (Iterator iter = typeAheadMarkers.iterator();
			 iter.hasNext(); )
		    {
                        if (((TypeAheadMarker)iter.next()).untilFocused ==
			    target)
		        {
                            iter.remove();
			    found = true;
			} else if (found) {
			    break;
			}
		    }			
		}

		redispatchEvent(target, e);

		// Now, dispatch any pending KeyEvents which have been
		// released because of the FOCUS_GAINED event so that we don't
		// have to wait for another event to be posted to the queue.
		pumpApprovedKeyEvents();
		return true;

	    default:
		redispatchEvent(target, e);
		return true;
	}
    }
    private boolean preDispatchKeyEvent(KeyEvent ke) {
        Component focusOwner = getFocusOwner();
        ke.setSource(((focusOwner != null) ? focusOwner
                      : getFocusedWindow()));
        if (ke.getSource() == null) {
            return true;
        }

        // Explicitly set the current event and most recent timestamp here in
        // addition to the call in Component.dispatchEventImpl. Because
        // KeyEvents can be delivered in response to a FOCUS_GAINED event, the
        // current timestamp may be incorrect. We need to set it here so that
        // KeyEventDispatchers will use the correct time.
        EventQueue.setCurrentEventAndMostRecentTime(ke);

        /**
         * Fix for 4495473.
         * This fix allows to correctly dispatch events when native
         * event proxying mechanism is active.
         * If it is active we should redispatch key events after
         * we detected its correct target.
         */
        if (KeyboardFocusManager.isProxyActive(ke)) {
            Component source = (Component)ke.getSource();
            Container target = source.getNativeContainer();
            if (target != null) {
                ComponentPeer peer = target.getPeer();
                if (peer != null) {
                    peer.handleEvent(ke);
                    /**
                     * Fix for 4478780 - consume event after it was dispatched by peer.
                     */
                    ke.consume();
                }
            }
            return true;
        }

	java.util.List dispatchers = getKeyEventDispatchers();
	if (dispatchers != null) {
	    for (java.util.Iterator iter = dispatchers.iterator();
		 iter.hasNext(); )
	     {
		 if (((KeyEventDispatcher)(iter.next())).
		     dispatchKeyEvent(ke))
		 {
		     return true;
		 }
	     }
	}
	return dispatchKeyEvent(ke);
    }

    /**
     * This method initiates a focus traversal operation if and only if the
     * KeyEvent represents a focus traversal key for the specified
     * focusedComponent. It is expected that focusedComponent is the current
     * focus owner, although this need not be the case. If it is not,
     * focus traversal will nevertheless proceed as if focusedComponent
     * were the focus owner.
     *
     * @param focusedComponent the Component that is the basis for a focus
     *        traversal operation if the specified event represents a focus
     *        traversal key for the Component
     * @param e the event that may represent a focus traversal key
     */
    public void processKeyEvent(Component focusedComponent, KeyEvent e) {
	// KEY_TYPED events cannot be focus traversal keys
	if (e.getID() == KeyEvent.KEY_TYPED) {
	    if (consumeNextKeyTyped) {
		e.consume();
		consumeNextKeyTyped = false;
	    }

	    return;
	}

        if (focusedComponent.getFocusTraversalKeysEnabled() &&
            !e.isConsumed())
        {
	    AWTKeyStroke stroke = AWTKeyStroke.getAWTKeyStrokeForEvent(e),
		oppStroke = AWTKeyStroke.getAWTKeyStroke(stroke.getKeyCode(),
						 stroke.getModifiers(),
						 !stroke.isOnKeyRelease());
	    Set toTest;
	    boolean contains, containsOpp;

	    toTest = focusedComponent.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
	    contains = toTest.contains(stroke);
	    containsOpp = toTest.contains(oppStroke);

	    if (contains || containsOpp) {
		if (contains) {
		    focusNextComponent(focusedComponent);
		}
		e.consume();
		consumeNextKeyTyped = (e.getID() == KeyEvent.KEY_PRESSED);
		return;
	    }

	    toTest = focusedComponent.getFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
	    contains = toTest.contains(stroke);
	    containsOpp = toTest.contains(oppStroke);

	    if (contains || containsOpp) {
		if (contains) {
		    focusPreviousComponent(focusedComponent);
		}
		e.consume();
		consumeNextKeyTyped = (e.getID() == KeyEvent.KEY_PRESSED);
		return;
	    }

	    toTest = focusedComponent.getFocusTraversalKeys(
                KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS);
	    contains = toTest.contains(stroke);
	    containsOpp = toTest.contains(oppStroke);

	    if (contains || containsOpp) {
		if (contains) {
		    upFocusCycle(focusedComponent);
		}
		e.consume();
		consumeNextKeyTyped = (e.getID() == KeyEvent.KEY_PRESSED);
		return;
	    }

	    if (!((focusedComponent instanceof Container) &&
		  ((Container)focusedComponent).isFocusCycleRoot())) {
	        return;
	    }

	    toTest = focusedComponent.getFocusTraversalKeys(
                KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS);
	    contains = toTest.contains(stroke);
	    containsOpp = toTest.contains(oppStroke);

	    if (contains || containsOpp) {
		if (contains) {
		    downFocusCycle((Container)focusedComponent);
		}
		e.consume();
		consumeNextKeyTyped = (e.getID() == KeyEvent.KEY_PRESSED);
	    }
	}
    }

    /**
     * Delays dispatching of KeyEvents until the specified Component becomes
     * the focus owner. KeyEvents with timestamps later than the specified
     * timestamp will be enqueued until the specified Component receives a
     * FOCUS_GAINED event, or the AWT cancels the delay request by invoking
     * <code>dequeueKeyEvents</code> or <code>discardKeyEvents</code>.
     *
     * @param after timestamp of current event, or the current, system time if
     *        the current event has no timestamp, or the AWT cannot determine
     *        which event is currently being handled
     * @param untilFocused Component which will receive a FOCUS_GAINED event
     *        before any pending KeyEvents
     * @see #dequeueKeyEvents
     * @see #discardKeyEvents
     */
    protected synchronized void enqueueKeyEvents(long after,
						 Component untilFocused) {
        if (untilFocused == null) {
            return;
        }

	int insertionIndex = 0,
	    i = typeAheadMarkers.size();
	ListIterator iter = typeAheadMarkers.listIterator(i);

	for (; i > 0; i--) {
	    TypeAheadMarker marker = (TypeAheadMarker)iter.previous();
	    if (marker.after <= after) {
		insertionIndex = i;
		break;
	    }
	}

	typeAheadMarkers.add(insertionIndex,
			     new TypeAheadMarker(after, untilFocused));
    }

    /**
     * Releases for normal dispatching to the current focus owner all
     * KeyEvents which were enqueued because of a call to
     * <code>enqueueKeyEvents</code> with the same timestamp and Component.
     * If the given timestamp is less than zero, the outstanding enqueue
     * request for the given Component with the <b>oldest</b> timestamp (if
     * any) should be cancelled.
     *
     * @param after the timestamp specified in the call to
     *        <code>enqueueKeyEvents</code>, or any value < 0
     * @param untilFocused the Component specified in the call to
     *        <code>enqueueKeyEvents</code>
     * @see #enqueueKeyEvents
     * @see #discardKeyEvents
     */
    protected synchronized void dequeueKeyEvents(long after,
						 Component untilFocused) {
        if (untilFocused == null) {
            return;
        }

        TypeAheadMarker marker;
        ListIterator iter = typeAheadMarkers.listIterator
            ((after >= 0) ? typeAheadMarkers.size() : 0);

        if (after < 0) {
            while (iter.hasNext()) {
                marker = (TypeAheadMarker)iter.next();
                if (marker.untilFocused == untilFocused)
                {
                    iter.remove();
                    return;
                }
            }
        } else {
            while (iter.hasPrevious()) {
                marker = (TypeAheadMarker)iter.previous();
                if (marker.untilFocused == untilFocused &&
                    marker.after == after)
                {
                    iter.remove();
                    return;
                }
            }
        }
    }

    /**
     * Discards all KeyEvents which were enqueued because of one or more calls
     * to <code>enqueueKeyEvents</code> with the specified Component, or one of
     * its descendants.
     *
     * @param comp the Component specified in one or more calls to
     *        <code>enqueueKeyEvents</code>, or a parent of such a Component
     * @see #enqueueKeyEvents
     * @see #dequeueKeyEvents
     */
    protected synchronized void discardKeyEvents(Component comp) {
        if (comp == null) {
            return;
        }

	long start = -1;

	for (Iterator iter = typeAheadMarkers.iterator(); iter.hasNext(); ) {
	    TypeAheadMarker marker = (TypeAheadMarker)iter.next();
            Component toTest = marker.untilFocused;
            boolean match = (toTest == comp);
            while (!match && toTest != null && !(toTest instanceof Window)) {
                toTest = toTest.getParent();
                match = (toTest == comp);
            }
	    if (match) {
		if (start < 0) {
		    start = marker.after;
		}
                iter.remove();
	    } else if (start >= 0) {
		purgeStampedEvents(start, marker.after);
		start = -1;
	    }
	}

	purgeStampedEvents(start, -1);
    }

    // Notes:
    //   * must be called inside a synchronized block
    //   * if 'start' is < 0, then this function does nothing
    //   * if 'end' is < 0, then all KeyEvents from 'start' to the end of the
    //     queue will be removed
    private void purgeStampedEvents(long start, long end) {
	if (start < 0) {
	    return;
	}

	for (Iterator iter = enqueuedKeyEvents.iterator(); iter.hasNext(); ) {
	    KeyEvent ke = (KeyEvent)iter.next();
	    long time = ke.getWhen();

	    if (start < time && (end < 0 || time <= end)) {
		iter.remove();
	    }

	    if (end >= 0 && time > end) {
		break;
	    }
	}
    }

    /**
     * Focuses the Component before aComponent, typically based on a
     * FocusTraversalPolicy.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see FocusTraversalPolicy
     * @see Component#transferFocusBackward
     */
    public void focusPreviousComponent(Component aComponent) {
        if (aComponent != null) {
            aComponent.transferFocusBackward();
        }
    }

    /**
     * Focuses the Component after aComponent, typically based on a
     * FocusTraversalPolicy.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see FocusTraversalPolicy
     * @see Component#transferFocus
     */
    public void focusNextComponent(Component aComponent) {
        if (aComponent != null) {
            aComponent.transferFocus();
        }
    }

    /**
     * Moves the focus up one focus traversal cycle. Typically, the focus owner
     * is set to aComponent's focus cycle root, and the current focus cycle
     * root is set to the new focus owner's focus cycle root. If, however,
     * aComponent's focus cycle root is a Window, then the focus owner is set
     * to the focus cycle root's default Component to focus, and the current
     * focus cycle root is unchanged.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see Component#transferFocusUpCycle
     */
    public void upFocusCycle(Component aComponent) {
        if (aComponent != null) {
            aComponent.transferFocusUpCycle();
        }
    }

    /**
     * Moves the focus down one focus traversal cycle. If aContainer is a focus
     * cycle root, then the focus owner is set to aContainer's default
     * Component to focus, and the current focus cycle root is set to
     * aContainer. If aContainer is not a focus cycle root, then no focus
     * traversal operation occurs.
     *
     * @param aContainer the Container that is the basis for the focus
     *        traversal operation
     * @see Container#transferFocusDownCycle
     */
    public void downFocusCycle(Container aContainer) {
        if (aContainer != null && aContainer.isFocusCycleRoot()) {
            aContainer.transferFocusDownCycle();
        }
    }
}
