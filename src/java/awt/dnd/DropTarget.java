/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * The <code>DropTarget</code> is associated 
 * with a <code>Component</code> when that <code>Component</code> 
 * wishes
 * to accept drops during Drag and Drop operations. 
 * 
 * @version 	1.38, 04/12/04
 * @since 1.2
 */

public class DropTarget implements DropTargetListener, Serializable {

    static final long serialVersionUID = -6283860791671019047L;

    /*
     * default FlavorMap for the system
     */

    static private FlavorMap defaultFlavorMap;

    /**
     * Construct a new DropTarget given the <code>Component</code> 
     * to associate itself with, an <code>int</code> representing
     * the default acceptable action(s) to 
     * support, a <code>DropTargetListener</code>
     * to handle event processing, a <code>boolean</code> indicating 
     * if the <code>DropTarget</code> is currently accepting drops, and 
     * a <code>FlavorMap</code> to use (or null).
     * <P>
     * The Component will receive drops only if it is enabled.
     * @param c 	The <code>Component</code> with which this <code>DropTarget</code> is associated
     * @param ops	The default acceptable actions for this <code>DropTarget</code>
     * @param dtl	The <code>DropTargetListener</code> for this <code>DropTarget</code>
     * @param act	Is the <code>DropTarget</code> accepting drops.
     * @param fm	The <code>FlavorMap</code> to use or null 
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
     * Construct a <code>DropTarget</code> given the <code>Component</code> 
     * to associate itself with, an <code>int</code> representing
     * the default acceptable action(s) 
     * to support, a <code>DropTargetListener</code>
     * to handle event processing, and a <code>boolean</code> indicating 
     * if the <code>DropTarget</code> is currently accepting drops.
     * <P>
     * The Component will receive drops only if it is enabled.
     * @param c 	The <code>Component</code> with which this <code>DropTarget</code> is associated
     * @param ops	The default acceptable actions for this <code>DropTarget</code>
     * @param dtl	The <code>DropTargetListener</code> for this <code>DropTarget</code>
     * @param act	Is the <code>DropTarget</code> accepting drops.
     *
     */

    public DropTarget(Component c, int ops, DropTargetListener dtl, boolean act) {
	this(c, ops, dtl, act, null);
    }

    /**
     * Construct a <code>DropTarget</code>.
     */

    public DropTarget() {
	this(null, DnDConstants.ACTION_COPY_OR_MOVE, null, true, null);
    }

    /**
     * Construct a <code>DropTarget</code> given the <code>Component</code> 
     * to associate itself with, and the <code>DropTargetListener</code>
     * to handle event processing.
     * <P>
     * The Component will receive drops only if it is enabled.
     * @param c 	The <code>Component</code> with which this <code>DropTarget</code> is associated
     * @param dtl	The <code>DropTargetListener</code> for this <code>DropTarget</code>
     */

    public DropTarget(Component c, DropTargetListener dtl) {
	this(c, DnDConstants.ACTION_COPY_OR_MOVE, dtl, true, null);
    }

    /**
     * Construct a <code>DropTarget</code> given the <code>Component</code> 
     * to associate itself with, an <code>int</code> representing
     * the default acceptable action(s) to support, and a
     * <code>DropTargetListener</code> to handle event processing.
     * <P>
     * The Component will receive drops only if it is enabled.
     * @param c 	The <code>Component</code> with which this <code>DropTarget</code> is associated
     * @param ops	The default acceptable actions for this <code>DropTarget</code>
     * @param dtl	The <code>DropTargetListener</code> for this <code>DropTarget</code>
     */

    public DropTarget(Component c, int ops, DropTargetListener dtl) {
	this(c, ops, dtl, true);
    }

    /**
     * Note: this interface is required to permit the safe association
     * of a DropTarget with a Component in one of two ways, either:
     * <code> component.setDropTarget(droptarget); </code>
     * or <code> droptarget.setComponent(component); </code>
     * <P>
     * The Component will receive drops only if it is enabled.
     * @param c The new <code>Component</code> this <code>DropTarget</code> 
     * is to be associated with.<P>
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
     * This method returns the <code>Component</code> associated 
     * with this <code>DropTarget</code>.
     * <P>
     * @return the current </code>Component</code>
     */

    public synchronized Component getComponent() {
	return component;
    }

    /**
     * Sets the default acceptable actions for this <code>DropTarget</code>
     * <P>
     * @param ops the default actions
     * <P>
     * @see java.awt.dnd.DnDConstants
     */

    public synchronized void setDefaultActions(int ops) {
	actions = ops & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_REFERENCE);

	if (dropTargetContext != null) dropTargetContext.setTargetActions(actions);

    }

    /**
     * This method returns an <code>int</code> representing the
     * current action(s) supported by this <code>DropTarget</code>.
     * <P>
     * @return the current default actions
     */

    public synchronized int getDefaultActions() {
	return actions;
    }

    /**
     * Set the DropTarget active if <code>true</code>, 
     * inactive if <code>false</code>.
     * <P>
     * @param isActive sets the <code>DropTarget</code> (in)active.
     */

    public synchronized void setActive(boolean isActive) {
	if (isActive != active) {
	    active = isActive;
	}

	if (!active) clearAutoscroll();
    }

    /**
     * This method returns a <code>boolean</code> 
     * indicating whether or not this <code>DropTarget</code> 
     * is currently active (ready to accept drops).
     * <P>
     * @return is the <code>DropTarget</code> active?
     */

    public synchronized boolean isActive() {
	return active;
    }

    /**
     * Add a new <code>DropTargetListener</code> (UNICAST SOURCE)
     * <P>
     * @param dtl The new <code>DropTargetListener</code>
     * <P>
     * @throws <code>TooManyListenersException</code> if a 
     * <code>DropTargetListener</code> is already added to this
     * <code>DropTarget</code>.
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
     * Remove the current <code>DropTargetListener</code> (UNICAST SOURCE)
     * <P>
     * @param dtl the DropTargetListener to deregister.
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
     * The <code>DropTarget</code> intercepts 
     * dragEnter() notifications before the 
     * registered <code>DropTargetListener</code> gets them. 
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
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
     * The <code>DropTarget</code> 
     * intercepts dragOver() notifications before the
     * registered <code>DropTargetListener</code> gets them.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */

    public synchronized void dragOver(DropTargetDragEvent dtde) {
	if (!active) return;

	if (dtListener != null && active) dtListener.dragOver(dtde);

	updateAutoscroll(dtde.getLocation());
    }

    /**
     * The <code>DropTarget</code> intercepts 
     * dropActionChanged() notifications before the 
     * registered <code>DropTargetListener</code> gets them.
     * <P>
     * @param dtde the DropTargetDragEvent
     */

    public void dropActionChanged(DropTargetDragEvent dtde) {
	if (!active) return;

	if (dtListener != null) dtListener.dropActionChanged(dtde);

	updateAutoscroll(dtde.getLocation());
    }

    /**
     * The <code>DropTarget</code> intercepts 
     * dragExit() notifications before the 
     * registered <code>DropTargetListener</code> gets them.
     * <P>
     * @param dte the <code>DropTargetEvent</code>
     */

    public synchronized void dragExit(DropTargetEvent dte) {
	if (!active) return;

	if (dtListener != null && active) dtListener.dragExit(dte);

	clearAutoscroll();
    }

    /**
     * The <code>DropTarget</code> intercepts drop() notifications before the 
     * registered <code>DropTargetListener</code> gets them.
     * <P>
     * @param dtde the <code>DropTargetDropEvent</code>
     */

    public synchronized void drop(DropTargetDropEvent dtde) {
	if (dtListener != null && active)
	    dtListener.drop(dtde);
	else { // we should'nt get here ...
	    dtde.rejectDrop();
	}
    }

    /**
     * This method returns the <code>FlavorMap</code>
     * associated with this <code>DropTarget</code>
     * <P>
     * @return the FlavorMap for this DropTarget
     */

    public FlavorMap getFlavorMap() { return flavorMap; }

    /**
     * This method sets the <code>FlavorMap</code> associated
     * with this <code>DropTarget</code>.
     * <P>
     * @param fm set the new <code>FlavorMap</code>, or null for default
     */

    public void setFlavorMap(FlavorMap fm) {
	if (defaultFlavorMap == null) {
		defaultFlavorMap = SystemFlavorMap.getDefaultFlavorMap();
	}
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
     * <P>
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
     * <P>
     * @param peer The Peer of the Component we are being disassociated from!
     */

    public void removeNotify(ComponentPeer peer) {
	if (nativePeer != null)
	    ((DropTargetPeer)nativePeer).removeDropTarget(this);

	componentPeer = nativePeer = null;
    }

    /**
     * This method returns the <code>DropTargetContext</code> associated 
     * with this <code>DropTarget</code>.
     * <P>
     * @return the <code>DropTargetContext</code> associated with this <code>DropTarget</code>.
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
         * <P>
         * @param c the <code>Component</code>
         * @param p the <code>Point</code>
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
         * <P>
         * @param newLocn the <code>Point</code>
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
         * <P>
         * @param e the <code>ActionEvent</code>
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
     * <P>
     * @param c the <code>Component</code>
     * @param p the <code>Point</code>
     */

    protected DropTargetAutoScroller createDropTargetAutoScroller(Component c, Point p) {
	return new DropTargetAutoScroller(c, p);
    }

    /**
     * initialize autoscrolling
     * <P>
     * @param p the <code>Point</code>
     */

    protected void initializeAutoscrolling(Point p) {
	if (component == null || !(component instanceof Autoscroll)) return;

	autoScroller = createDropTargetAutoScroller(component, p);
    }

    /**
     * update autoscrolling with current cursor locn
     * <P>
     * @param dragCursorLocn the <code>Point</code>
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

    private transient FlavorMap flavorMap = (defaultFlavorMap == null ? defaultFlavorMap = SystemFlavorMap.getDefaultFlavorMap() : defaultFlavorMap);
}














