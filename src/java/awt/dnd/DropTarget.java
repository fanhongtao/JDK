/*
 * @(#)DropTarget.java	1.21 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
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

import java.util.TooManyListenersException;

import java.io.Serializable;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.dnd.Autoscroll;

import javax.swing.Timer;

import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;

import java.awt.dnd.peer.DropTargetPeer;

/**
 * <p>
 * The DropTarget is associated with a Component, when that Component wishes
 * to accept Drops during Drag and Drop operations. 
 * </p>
 *
 * @version 1.21
 * @since JDK1.2
 *
 */

public class DropTarget implements DropTargetListener, Serializable {

    static final long serialVersionUID = -6283860791671019047L;

    /*
     * default FlavorMap for the system
     */

    static private final FlavorMap defaultFlavorMap = SystemFlavorMap.getDefaultFlavorMap();

    /**
     * Construct a DropTarget
     *
     * @param c 	The Component with which this DropTarget is associated
     * @param ops	The default acceptable actions for this DropTarget
     * @param dtl	The DropTargetListener for this DropTarget
     * @param act	Is the DropTarget accepting drops.
     * @param fm	The flavorMap to use or null 
     *
     */

    public DropTarget(Component c, int ops, DropTargetListener dtl, boolean act, FlavorMap fm) {
	super();

	component = c;

	setDefaultActions(ops);

	if (dtl != null) try {
	    addDropTargetListener(dtl);
	} catch (TooManyListenersException tmle) {
	    // do nothing!
	}

	if (c != null) {
	    c.setDropTarget(this);
	    setActive(act);
	}

        if (fm != null) flavorMap = fm;
    }

    /**
     * Construct a DropTarget
     *
     * @param c 	The Component with which this DropTarget is associated
     * @param ops	The default acceptable actions for this DropTarget
     * @param dtl	The DropTargetListener for this DropTarget
     * @param act	Is the DropTarget accepting drops.
     *
     */

    public DropTarget(Component c, int ops, DropTargetListener dtl, boolean act) {
	this(c, ops, dtl, act, null);
    }

    /**
     * Construct a DropTarget
     */

    public DropTarget() {
	this(null, DnDConstants.ACTION_COPY_OR_MOVE, null, true, null);
    }

    /**
     * Construct a DropTarget
     *
     * @param c 	The Component with which this DropTarget is associated
     * @param dtl	The DropTargetListener for this DropTarget
     */

    public DropTarget(Component c, DropTargetListener dtl) {
	this(c, DnDConstants.ACTION_COPY_OR_MOVE, dtl, true, null);
    }

    /**
     * Construct a DropTarget
     *
     * @param c 	The Component with which this DropTarget is associated
     * @param ops	The default acceptable actions for this DropTarget
     * @param dtl	The DropTargetListener for this DropTarget
     */

    public DropTarget(Component c, int ops, DropTargetListener dtl) {
	this(c, ops, dtl, true);
    }

    /**
     * Note: this interface is required to permit the safe association
     * of a DropTarget with a Component in one of two ways, either:
     * <code> component.setDropTarget(droptarget); </code>
     * or <code> droptarget.setComponent(component); </code>
     *
     * The caller must have AWTPermission.setDropTarget to succeed.
     *
     * @param c The new Component this DropTarget is to be associated with.
     *
     * @throw IllegalArgumentException
     * @throw UnsupportedOperationException
     */

    public synchronized void setComponent(Component c) {
	if (component == c || component != null && component.equals(c))
	    return;
	
	Component     old;
	ComponentPeer oldPeer = null;

	if ((old = component) != null) {
	    clearAutoscroll();

	    component = null;

	    if (componentPeer != null) {
		oldPeer = componentPeer;
		removeNotify(componentPeer);
	    }

	    old.setDropTarget(null); 

	}

	if ((component = c) != null) try {
	    c.setDropTarget(this);
	} catch (Exception e) { // undo the change
	    if (old != null) {
		old.setDropTarget(this);
		addNotify(oldPeer);
	    }
	}
    }

    /**
     * @return the current Component
     */

    public synchronized Component getComponent() {
	return component;
    }

    /**
     * Sets the default acceptable actions for this DropTarget
     *
     * @param ops the default actions.
     *
     * @see java.awt.dnd.DnDConstants
     */

    public synchronized void setDefaultActions(int ops) {
	actions = ops & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_REFERENCE);

	if (dropTargetContext != null) dropTargetContext.setTargetActions(actions);

    }

    /**
     * @return the current default actions
     */

    public synchronized int getDefaultActions() {
	return actions;
    }

    /**
     * set the DropTarget (in)active.
     *
     * @param isActive
     */

    public synchronized void setActive(boolean isActive) {
	if (isActive != active) {
	    active = isActive;
	}

	if (!active) clearAutoscroll();
    }

    /**
     * @return is the DropTarget active?
     */

    public synchronized boolean isActive() {
	return active;
    }

    /**
     * Add a new DropTargetListener (UNICAST SOURCE)
     *
     * @param dtl The new DropTargetListener
     *
     * @throw TooManyListenersExceptio
     */

    public synchronized void addDropTargetListener(DropTargetListener dtl) throws TooManyListenersException {
	if (dtl == null) return;

	if (equals(dtl)) throw new IllegalArgumentException("DropTarget may not be its own Listener");

	if (dtListener == null)
	    dtListener = dtl;
	else
	    throw new TooManyListenersException();
    }

    /**
     * Remove the current DropTargetListener (UNICAST SOURCE)
     *
     * @param dtl the DropTargetListener to deregister.
     *
     * @throw IllegalArgumentException
     */

    public synchronized void removeDropTargetListener(DropTargetListener dtl) {
	if (dtl != null && dtListener != null) {
	    if(dtListener.equals(dtl))
		dtListener = null;
	    else
		throw new IllegalArgumentException("listener mismatch");
	}
    }


    /**
     * The DropTarget intercepts dragEnter() notifications before the 
     * registered DropTargetListener gets them. 
     */

    public synchronized void dragEnter(DropTargetDragEvent dtde) {
	if (!active) return;

	if (dtListener != null) {
	    dtListener.dragEnter(dtde);
	} else
            dtde.getDropTargetContext().setTargetActions(DnDConstants.ACTION_NONE);

	initializeAutoscrolling(dtde.getLocation());
    }

    /**
     * The DropTarget intercepts dragOver() notifications before the
     * registered DropTargetListener gets them.
     */

    public synchronized void dragOver(DropTargetDragEvent dtde) {
	if (!active) return;

	if (dtListener != null && active) dtListener.dragOver(dtde);

	updateAutoscroll(dtde.getLocation());
    }

    /**
     * The DropTarget intercepts dropActionChanged() notifications before the 
     * registered DropTargetListener gets them.
     */

    public void dropActionChanged(DropTargetDragEvent dtde) {
	if (!active) return;

	if (dtListener != null) dtListener.dropActionChanged(dtde);

	updateAutoscroll(dtde.getLocation());
    }

    /**
     * The DropTarget intercepts dragExit() notifications before the 
     * registered DropTargetListener gets them.
     */

    public synchronized void dragExit(DropTargetEvent dte) {
	if (!active) return;

	if (dtListener != null && active) dtListener.dragExit(dte);

	clearAutoscroll();
    }

    /**
     * The DropTarget intercepts drop() notifications before the 
     * registered DropTargetListener gets them.
     */

    public synchronized void drop(DropTargetDropEvent dtde) {
	if (dtListener != null && active)
	    dtListener.drop(dtde);
	else { // we should'nt get here ...
	    dtde.rejectDrop();
	}
    }

    /**
     * @return the FlavorMap for this DropTarget
     */

    public FlavorMap getFlavorMap() { return flavorMap; }

    /**
     * @param set the new flavormap, or null for default
     */

    public void setFlavorMap(FlavorMap fm) {
	flavorMap = fm == null ? defaultFlavorMap : fm;
    }

    /**
     * Notify the DropTarget that it has been associated with a Component
     *
     **********************************************************************
     * This method is usually called from java.awt.Component.addNotify() of
     * the Component associated with this DropTarget to notify the DropTarget
     * that a ComponentPeer has been associated with that Component.
     *
     * Calling this method, other than to notify this DropTarget of the
     * association of the ComponentPeer with the Component may result in
     * a malfunction of the DnD system.
     **********************************************************************
     *
     * @param peer The Peer of the Component we are associated with!
     *
     */

    public void addNotify(ComponentPeer peer) {
	/*
	 * FIX THIS FOR BETA4
	 */

	// java.security.AccessController.checkPermission(new AWTPermission("setDTarget"));
	if (peer == componentPeer) return;

	componentPeer = peer;

	for (Component c = component; peer instanceof LightweightPeer; c = c.getParent())
	    peer = c.getPeer();
	     
	try {
	    ((DropTargetPeer)(nativePeer = peer)).addDropTarget(this);
	} catch (ClassCastException cce) {
	    nativePeer = null;
	    // throw new InvalidDnDOperationException("No Native Peer support");
	}
    }

    /**
     * Notify the DropTarget that it has been disassociated from a Component
     *
     **********************************************************************
     * This method is usually called from java.awt.Component.removeNotify() of
     * the Component associated with this DropTarget to notify the DropTarget
     * that a ComponentPeer has been disassociated with that Component.
     *
     * Calling this method, other than to notify this DropTarget of the
     * disassociation of the ComponentPeer from the Component may result in
     * a malfunction of the DnD system.
     **********************************************************************
     *
     * @param peer The Peer of the Component we are being disassociated froe!
     */

    public void removeNotify(ComponentPeer peer) {
	if (nativePeer != null)
	    ((DropTargetPeer)nativePeer).removeDropTarget(this);

	componentPeer = nativePeer = null;
    }

    /**
     * @return the DropTargetContext associated with this DropTarget.
     */

    public DropTargetContext getDropTargetContext() {
	if (dropTargetContext == null) dropTargetContext = createDropTargetContext();

	return dropTargetContext;
    }

    /**
     * Create the DropTargetContext associated with this DropTarget.
     * Subclasses may override this method to instantiate their own
     * DropTargetContext subclass.
     *
     * This call is typically *only* called by the platform's
     * DropTargetContextPeer as a drag operation encounters this
     * DropTarget. Accessing the Context while no Drag is current
     * has undefined results.
     */

    protected DropTargetContext createDropTargetContext() {
	return new DropTargetContext(this);
    }

    /*********************************************************************/

    /**
     * this protected nested class implements autoscrolling
     */

    protected static class DropTargetAutoScroller implements ActionListener {

	/**
         * construct a DropTargetAutoScroller
	 */

	protected DropTargetAutoScroller(Component c, Point p) {
	    super();

	    component  = c;
	    autoScroll = (Autoscroll)component;

	    Toolkit t  = Toolkit.getDefaultToolkit();

	    Integer    initial  = new Integer(100);
	    Integer    interval = new Integer(100);

	    try {
		initial = (Integer)t.getDesktopProperty("DnD.Autoscroll.initialDelay");
	    } catch (Exception e) {
		// ignore
	    }

	    try {
	        interval = (Integer)t.getDesktopProperty("DnD.Autoscroll.interval");
	    } catch (Exception e) {
		// ignore
	    }

	    timer  = new Timer(interval.intValue(), this);

	    timer.setCoalesce(true);
	    timer.setInitialDelay(initial.intValue());

	    locn = p;
	    prev = p;

	    try {
		hysteresis = ((Integer)t.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
	    } catch (Exception e) {
		// ignore
	    }

	    timer.start();
	}

	/**
	 * update the geometry of the autoscroll region
	 */

	private void updateRegion() {
	   Insets    i    = autoScroll.getAutoscrollInsets();
	   Dimension size = component.getSize();

	   if (size.width != outer.width || size.height != outer.height)
		outer.reshape(0, 0, size.width, size.height);

	   if (inner.x != i.left || inner.y != i.top)
		inner.setLocation(i.left, i.top);
		
	   int newWidth  = size.width -  (i.left + i.right);
	   int newHeight = size.height - (i.top  + i.bottom);

	   if (newWidth != inner.width || newHeight != inner.height)
		inner.setSize(newWidth, newHeight);
	
	}

	/**
	 * cause autoscroll to occur
	 */

	protected synchronized void updateLocation(Point newLocn) {
	    prev = locn;
	    locn = newLocn;

	    if (Math.abs(locn.x - prev.x) > hysteresis ||
	   	Math.abs(locn.y - prev.y) > hysteresis) {
		if (timer.isRunning()) timer.stop();
	    } else {
		if (!timer.isRunning()) timer.start();
	    }
	}

	/**
	 * cause autoscrolling to stop
	 */

	protected void stop() { timer.stop(); }

	/**
	 * cause autoscroll to occur
	 */

	public synchronized void actionPerformed(ActionEvent e) {
	    updateRegion();

	    if (outer.contains(locn) && !inner.contains(locn))
	        autoScroll.autoscroll(locn);
	}

	/*
	 * fields
	 */

	private Component  component;
	private Autoscroll autoScroll;

	private Timer      timer;

	private Point	   locn;
	private Point	   prev;

	private Rectangle  outer = new Rectangle();
	private Rectangle  inner = new Rectangle();

	private int	   hysteresis = 10;
    }

    /*********************************************************************/

    /**
     * create an embedded autoscroller
     */

    protected DropTargetAutoScroller createDropTargetAutoScroller(Component c, Point p) {
	return new DropTargetAutoScroller(c, p);
    }

    /**
     * initialize autoscrolling
     */

    protected void initializeAutoscrolling(Point p) {
	if (component == null || !(component instanceof Autoscroll)) return;

	autoScroller = createDropTargetAutoScroller(component, p);
    }

    /**
     * update autoscrolling with current cursor locn
     */

    protected void updateAutoscroll(Point dragCursorLocn) {
	if (autoScroller != null) autoScroller.updateLocation(dragCursorLocn);
    }

    /**
     * clear autoscrolling
     */

    protected void clearAutoscroll() {
	if (autoScroller != null) {
	    autoScroller.stop();
	    autoScroller = null;
	}
    }

    /*
     * The DropTargetContext associated with this DropTarget
     */

    private transient DropTargetContext dropTargetContext;

    /*
     * The Component associated with this DropTarget
     */

    private Component component;
 
    /*
     * That Component's  Peer
     */

    private transient ComponentPeer componentPeer;

    /*
     * That Component's "native" Peer
     */

    private transient ComponentPeer nativePeer;
 

    /*
     * Default permissable actions supported by this DropTarget
     *
     * @see #setDefaultActions
     * @see #getDefaultActions
     */

    int	    actions = DnDConstants.ACTION_COPY_OR_MOVE;

    /*
     * Is the Target accepting DND ops ...
     */

    boolean active = true;

    /*
     * the auto scrolling object
     */

    private transient DropTargetAutoScroller autoScroller;

    /*
     * The delegate
     */

    private DropTargetListener dtListener;

    /*
     * The FlavorMap
     */

    private transient FlavorMap flavorMap = defaultFlavorMap;
}
