/*
 * @(#)Container.java	1.107 97/05/05
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.awt.peer.ContainerPeer;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * A generic Abstract Window Toolkit(AWT) container object is a component 
 * that can contain other AWT components.
 * <p>
 * Components added to a container are tracked in a list.  The order
 * of the list will define the components' front-to-back stacking order 
 * within the container.  If no index is specified when adding a
 * component to a container, it will be added to the end of the list
 * (and hence to the bottom of the stacking order).
 * @version 	1.107, 05/05/97
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 */
public abstract class Container extends Component {

    /**
     * The number of components in this container.
     */
    int ncomponents;

    /** 
     * The components in this container.
     */
    Component component[] = new Component[4];

    /** 
     * Layout manager for this container.
     */
    LayoutManager layoutMgr;

    /**
     * Event router for lightweight components.  If this container
     * is native, this dispatcher takes care of forwarding and 
     * retargeting the events to lightweight components contained
     * (if any).
     */
    private LightweightDispatcher dispatcher;

    /** Internal, cached size information */
    private Dimension maxSize;

    transient ContainerListener containerListener;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 4613797578919906343L;  

    /**
     * Constructs a new Container. Containers can be extended directly, 
     * but are lightweight in this case and must be contained by a parent
     * somewhere higher up in the component tree that is native.
     * (such as Frame for example).
     */
    protected Container() {
    }

    /** 
     * Returns the number of components in this panel.
     * @see #getComponent
     */
    public int getComponentCount() {
	return countComponents();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by getComponentCount().
     */
    public int countComponents() {
	return ncomponents;
    }

    /** 
     * Gets the nth component in this container.
     * @param n the number of the component to get
     * @exception ArrayIndexOutOfBoundsException If the nth value does not 
     * exist.
     */
    public Component getComponent(int n) {
	synchronized (Component.LOCK) {
	    if ((n < 0) || (n >= ncomponents)) {
		throw new ArrayIndexOutOfBoundsException("No such child: " + n);
	    }
	    return component[n];
	}
    }

    /**
     * Gets all the components in this container.
     */
    public Component[] getComponents() {
	synchronized (Component.LOCK) {
	    Component list[] = new Component[ncomponents];
	    System.arraycopy(component, 0, list, 0, ncomponents);
	    return list;
	}
    }

    /**
     * Returns the insets of the container. The insets indicate the size of
     * the border of the container. A Frame, for example, will have a top inset
     * that corresponds to the height of the Frame's title bar. 
     * @see LayoutManager
     */
    public Insets getInsets() {
    	return insets();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getInsets().
     */
    public Insets insets() {
	if (this.peer != null && this.peer instanceof ContainerPeer) {
	    ContainerPeer peer = (ContainerPeer)this.peer;
	    return peer.insets();
	}
	return new Insets(0, 0, 0, 0);
    }

    /** 
     * Adds the specified component to this container.
     * @param comp the component to be added
     */
    public Component add(Component comp) {
        addImpl(comp, null, -1);
	return comp;
    }

    /**
     * Adds the specified component to this container.
     * It is strongly advised to use the 1.1 method, add(Component, Object),
     * in place of this method.
     */
    public Component add(String name, Component comp) {
	addImpl(comp, name, -1);
	return comp;
    }

    /** 
     * Adds the specified component to this container at the given
     * position in the container's component list.
     * @param comp the component to be added 
     * @param index the position in the container's list at which to
     * insert the component.  -1 means insert at the end.
     * @see #remove
     */
    public Component add(Component comp, int index) {
	addImpl(comp, null, index);
	return comp;
    }

    /**
     * Adds the specified component to this container at the specified
     * index.  Also notifies the layout manager to add the component to
     * the this container's layout using the specified constraints object.
     * @param comp the component to be added
     * @param constraints an object expressing layout contraints for this
     * component
     * @see #remove
     * @see LayoutManager
     */
    public void add(Component comp, Object constraints) {
	addImpl(comp, constraints, -1);
    }

    /**
     * Adds the specified component to this container with the specified
     * constraints at the specified index.  Also notifies the layout 
     * manager to add the component to the this container's layout using 
     * the specified constraints object.
     * @param comp the component to be added
     * @param constraints an object expressing layout contraints for this
     * @param index the position in the container's list at which to insert
     * the component. -1 means insert at the end.
     * component
     * @see #remove
     * @see LayoutManager
     */
    public void add(Component comp, Object constraints, int index) {
       addImpl(comp, constraints, index);
    }

    /**
     * Adds the specified component to this container at the specified
     * index.  Also notifies the layout manager to add the component to
     * the this container's layout using the specified constraints object.
     * <p>
     * This is the method to override if you want to track every add
     * request to a container.  An overriding method should usually
     * include a call to super.addImpl(comp, constraints, index).
     * @param comp the component to be added
     * @param constraints an object expressing layout contraints for this
     * component
     * @param index the position in the container's list at which to
     * insert the component.  -1 means insert at the end.
     * @see #remove
     * @see LayoutManager
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	synchronized (Component.LOCK) {

	    /* Check for correct arguments:  index in bounds,
	     * comp cannot be one of this container's parents,
	     * and comp cannot be a window.
	     */
	    if (index > ncomponents || (index < 0 && index != -1)) {
		throw new IllegalArgumentException(
			  "illegal component position");
	    }
	    if (comp instanceof Container) {
		for (Container cn = this; cn != null; cn=cn.parent) {
		    if (cn == comp) {
			throw new IllegalArgumentException(
				  "adding container's parent to itself");
		    }
		}
	    }
	    if (comp instanceof Window) {
	        throw new IllegalArgumentException(
			     "adding a window to a container");
	    }

	    /* Reparent the component and tidy up the tree's state. */
	    if (comp.parent != null) {
		comp.parent.remove(comp);
	    }

	    /* Add component to list; allocate new array if necessary. */
	    if (ncomponents == component.length) {
		Component newcomponents[] = new Component[ncomponents * 2];
		System.arraycopy(component, 0, newcomponents, 0, ncomponents);
		component = newcomponents;
	    }
	    if (index == -1 || index == ncomponents) {
		component[ncomponents++] = comp;
	    } else {
		System.arraycopy(component, index, component,
				 index + 1, ncomponents - index);
		component[index] = comp;
		ncomponents++;
	    }
	    comp.parent = this;
	    if (valid) {
		invalidate();
	    }
	    if (peer != null) {
		comp.addNotify();
	    }
	    
	    /* Notify the layout manager of the added component. */
	    if (layoutMgr != null) {
		if (layoutMgr instanceof LayoutManager2) {
		    ((LayoutManager2)layoutMgr).addLayoutComponent(comp, constraints);
		} else if (constraints instanceof String) {
		    layoutMgr.addLayoutComponent((String)constraints, comp);
		}
	    }
            if (containerListener != null || 
                (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0) {
                ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_ADDED,
                                     comp);
                processEvent(e);
            }
	}
    }

    /** 
     * Removes the component at the specified index from this container.
     * @param index the index of the component to be removed
     * @see #add
     */
    public void remove(int index) {
	synchronized (Component.LOCK) {
    	    Component comp = component[index];
	    if (peer != null) {
		comp.removeNotify();
	    }
	    if (layoutMgr != null) {
		layoutMgr.removeLayoutComponent(comp);
	    }
	    comp.parent = null;
	    System.arraycopy(component, index + 1,
			     component, index,
			     ncomponents - index - 1);
	    component[--ncomponents] = null;
	    if (valid) {
		invalidate();
	    }
            if (containerListener != null ||
                (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0) {
                ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_REMOVED,
                                     comp);
                processEvent(e);
            }
	    return;
	}
    }

    /** 
     * Removes the specified component from this container.
     * @param comp the component to be removed
     * @see #add
     */
    public void remove(Component comp) {
	synchronized (Component.LOCK) {
	    if (comp.parent == this)  {
    		/* Search backwards, expect that more recent additions
		 * are more likely to be removed.
    	    	 */
		for (int i = ncomponents; --i >= 0; ) {
		    if (component[i] == comp) {
    	    	    	remove(i);
		    }
		}
	    }
	}
    }

    /** 
     * Removes all the components from this container.
     * @see #add
     * @see #remove
     */
    public void removeAll() {
	synchronized (Component.LOCK) {
	    while (ncomponents > 0) {
		Component comp = component[--ncomponents];
		component[ncomponents] = null;

		if (peer != null) {
		    comp.removeNotify();
		}
		if (layoutMgr != null) {
		    layoutMgr.removeLayoutComponent(comp);
		}
		comp.parent = null;
                if (containerListener != null ||
                   (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0) {
                    ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_REMOVED,
                                     comp);
                    processEvent(e);
                }
	    }
	    if (valid) {
		invalidate();
	}
	}
    }

    /** 
     * Gets the layout manager for this container.  
     * @see #doLayout
     * @see #setLayout
     */
    public LayoutManager getLayout() {
	return layoutMgr;
    }

    /** 
     * Sets the layout manager for this container.
     * @param mgr the specified layout manager
     * @see #doLayout
     * @see #getLayout
     */
    public void setLayout(LayoutManager mgr) {
	layoutMgr = mgr;
	if (valid) {
	    invalidate();
	}
    }

    /** 
     * Does a layout on this Container.  Most programs should not call
     * this directly, but should invoke validate instead.
     * @see #setLayout
     * @see #validate
     */
    public void doLayout() {
	layout();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by doLayout().
     */
    public void layout() {
	LayoutManager layoutMgr = this.layoutMgr;
	if (layoutMgr != null) {
	    layoutMgr.layoutContainer(this);
	}
    }

    /** 
     * Invalidates the container.  The container and all parents
     * above it are marked as needing to be laid out.  This method can
     * be called often, so it needs to execute quickly.
     * @see #validate
     * @see #layout
     * @see LayoutManager
     */
    public void invalidate() {
	if (layoutMgr instanceof LayoutManager2) {
	    LayoutManager2 lm = (LayoutManager2) layoutMgr;
	    lm.invalidateLayout(this);
	}
	super.invalidate();
    }

    /** 
     * Validates this Container and all of the components contained
     * within it. 
     * @see #validate
     * @see Component#invalidate
     */
    public void validate() {
        /* Avoid grabbing lock unless really necessary. */
	if (!valid) {
	    synchronized (Component.LOCK) {
		if (!valid && peer != null) {
                    Cursor oldCursor = getCursor();
		    ContainerPeer p = null;
		    if (peer instanceof ContainerPeer) {
			p = (ContainerPeer) peer;
		    }
		    if (p != null) {
			p.beginValidate();
		    }
		    validateTree();
		    valid = true;
		    if (p != null) {
			p.endValidate();
		    }
		}
	    }
	}
    }

    /**
     * Recursively descends the container tree and recomputes the
     * layout for any subtrees marked as needing it (those marked as
     * invalid).  Synchronization should be provided by the method
     * that calls this:  validate.
     */
    protected void validateTree() {
	if (!valid) {
	    doLayout();
	    for (int i = 0 ; i < ncomponents ; ++i) {
		Component comp = component[i];
		if (   (comp instanceof Container) 
	            && !(comp instanceof Window)
		    && !comp.valid) {
		    ((Container)comp).validateTree();
		} else {
		    comp.valid = true;
		}
	    }
	}
	valid = true;
    }

    /** 
     * Returns the preferred size of this container.  
     * @see #getMinimumSize
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by getPreferredSize().
     */
    public Dimension preferredSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
	 * is available.
	 */ 
    	Dimension dim = prefSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	synchronized (Component.LOCK) {
	    prefSize = (layoutMgr != null) ?
			       layoutMgr.preferredLayoutSize(this) :
			       super.preferredSize();
	    
	    return prefSize;
	}
    }

    /** 
     * Returns the minimum size of this container.  
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize() {
	return minimumSize();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by getMinimumSize().
     */
    public Dimension minimumSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
	 * is available.
	 */ 
    	Dimension dim = minSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	synchronized (Component.LOCK) {
	    minSize = (layoutMgr != null) ?
		   layoutMgr.minimumLayoutSize(this) :
		   super.minimumSize();
	    return minSize;
	}
    }

    /** 
     * Returns the maximum size of this container.  
     * @see #getPreferredSize
     */
    public Dimension getMaximumSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
	 * is available.
	 */ 
    	Dimension dim = maxSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	if (layoutMgr instanceof LayoutManager2) {
	    synchronized (Component.LOCK) {
		LayoutManager2 lm = (LayoutManager2) layoutMgr;
		maxSize = lm.maximumLayoutSize(this);
	    }
	} else {
	    maxSize = super.getMaximumSize();
	}
	return maxSize;
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getAlignmentX() {
	float xAlign;
	if (layoutMgr instanceof LayoutManager2) {
	    synchronized (Component.LOCK) {
		LayoutManager2 lm = (LayoutManager2) layoutMgr;
		xAlign = lm.getLayoutAlignmentX(this);
	    }
	} else {
	    xAlign = super.getAlignmentX();
	}
	return xAlign;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getAlignmentY() {
	float yAlign;
	if (layoutMgr instanceof LayoutManager2) {
	    synchronized (Component.LOCK) {
		LayoutManager2 lm = (LayoutManager2) layoutMgr;
		yAlign = lm.getLayoutAlignmentY(this);
	    }
	} else {
	    yAlign = super.getAlignmentY();
	}
	return yAlign;
    }

    /** 
     * Paints the container.  This forwards the paint to any lightweight components 
     * that are children of this container.  If this method is reimplemented, 
     * super.paint(g) should be called so that lightweight components are properly
     * rendered.  If a child component is entirely clipped by the current clipping
     * setting in g, paint() will not be forwarded to that child.
     *
     * @param g the specified Graphics window
     * @see   java.awt.Component#update(java.awt.Graphics)
     */
    public void paint(Graphics g) {
	if (isShowing()) {
	    int ncomponents = this.ncomponents;
	    Rectangle clip = g.getClipRect();
	    for (int i = ncomponents - 1 ; i >= 0 ; i--) {
		Component comp = component[i];
		if (comp != null && 
		    comp.peer instanceof java.awt.peer.LightweightPeer &&
		    comp.visible == true) {

		    Rectangle cr = comp.getBounds();
		    if ((clip == null) || cr.intersects(clip)) {
			Graphics cg = g.create(cr.x, cr.y, cr.width, cr.height);
			try {
			    comp.paint(cg);
			} finally {
			    cg.dispose();
			}
		    }
		}
	    }
	}
    }

    /** 
     * Prints the container.  This forwards the print to any lightweight components 
     * that are children of this container.  If this method is reimplemented, 
     * super.print(g) should be called so that lightweight components are properly
     * rendered.  If a child component is entirely clipped by the current clipping
     * setting in g, print() will not be forwarded to that child.
     *
     * @param g the specified Graphics window
     * @see   java.awt.Component#update(java.awt.Graphics)
     */
    public void print(Graphics g) {
        super.print(g);  // By default, Component.print() calls paint()

    	int ncomponents = this.ncomponents;
	Rectangle clip = g.getClipRect();
	for (int i = ncomponents - 1 ; i >= 0 ; i--) {
	    Component comp = component[i];
	    if (comp != null && comp.peer instanceof java.awt.peer.LightweightPeer) {
		Rectangle cr = comp.getBounds();
		if (cr.intersects(clip)) {
		    Graphics cg = g.create(cr.x, cr.y, cr.width, cr.height);
		    try {
			comp.print(cg);
		    } finally {
			cg.dispose();
		    }
		}
	    }
	}
    }

    /** 
     * Paints the components in this container.
     * @param g the specified Graphics window
     * @see Component#paint
     * @see Component#paintAll
     */
    public void paintComponents(Graphics g) {
    	int ncomponents = this.ncomponents;
	for (int i = ncomponents - 1 ; i >= 0 ; i--) {
	    Component comp = component[i];
	    if (comp != null) {
                Graphics cg = comp.getGraphics();
                Rectangle parentRect = g.getClipRect();

                // Calculate the clipping region of the child's graphics
                // context, by taking the intersection of the parent's
                // clipRect (if any) and the child's bounds, and then 
                // translating it's coordinates to be relative to the child.
                if (parentRect != null) {
                    Rectangle childRect = comp.getBounds();
                    if (childRect.intersects(parentRect) == false) {
                        // Child component is completely clipped out: ignore.
                        continue;
                    }
                    Rectangle childClipRect = 
                        childRect.intersection(parentRect);
                    childClipRect.translate(-childRect.x, -childRect.y);
                    cg.clipRect(childClipRect.x, childClipRect.y,
                                childClipRect.width, childClipRect.height);
                }

		try {
		    comp.paintAll(cg);
		} finally {
		    cg.dispose();
		}
	    }
	}
    }

    /** 
     * Prints the components in this container.
     * @param g the specified Graphics window
     * @see Component#print
     * @see Component#printAll
     */
    public void printComponents(Graphics g) {
    	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		Graphics cg = g.create(comp.x, comp.y, comp.width, comp.height);
		try {
		    comp.printAll(cg);
		} finally {
		    cg.dispose();
		}
	    }
	}
    }

    /**
     * Adds the specified container listener to receive container events
     * from this container.
     * @param l the container listener
     */ 
    public synchronized void addContainerListener(ContainerListener l) {
	containerListener = AWTEventMulticaster.add(containerListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified container listener so it no longer receives
     * container events from this container.
     * @param l the container listener
     */ 
    public void removeContainerListener(ContainerListener l) {
	containerListener = AWTEventMulticaster.remove(containerListener, l);
    }

    // REMIND: remove when filtering is done at lower level
    boolean eventEnabled(AWTEvent e) {
        int id = e.getID();

        if (id == ContainerEvent.COMPONENT_ADDED ||
            id == ContainerEvent.COMPONENT_REMOVED) {
            if ((eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                containerListener != null) {
                return true;
            }
            return false;
        }
        return super.eventEnabled(e);
    }          

    /**
     * Processes events on this container. If the event is a ContainerEvent,
     * it invokes the processContainerEvent method, else it invokes its
     * superclass's processEvent.
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {
        if (e instanceof ContainerEvent) {
            processContainerEvent((ContainerEvent)e);     
            return;
        }
	super.processEvent(e);
    }

    /** 
     * Processes container events occurring on this container by
     * dispatching them to any registered ContainerListener objects.
     * NOTE: This method will not be called unless container events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A ContainerListener object is registered via addContainerListener()
     * b) Container events are enabled via enableEvents()
     * @see Component#enableEvents
     * @param e the container event
     */  
    protected void processContainerEvent(ContainerEvent e) {
        if (containerListener != null) {
            switch(e.getID()) {
              case ContainerEvent.COMPONENT_ADDED:
                containerListener.componentAdded(e);
                break;
              case ContainerEvent.COMPONENT_REMOVED:
                containerListener.componentRemoved(e);
                break;
            }
        }
    }

    /*
     * Dispatches an event to this component or one of its sub components.
     * @param e the event
     */
    void dispatchEventImpl(AWTEvent e) {
	if ((dispatcher != null) && dispatcher.dispatchEvent(e)) {
	    // event was sent to a lightweight component.  The
	    // native-produced event sent to the native container
	    // must be properly disposed of by the peer, so it 
	    // gets forwarded.  If the native host has been removed
	    // as a result of the sending the lightweight event, 
	    // the peer reference will be null.
	    e.consume();
	    if (peer != null) {
		peer.handleEvent(e);
	    }
	    return;
	}
	super.dispatchEventImpl(e);
    }

    /**
     * Fetchs the top-most (deepest) lightweight component that is interested
     * in receiving mouse events.
     */
    Component getMouseEventTarget(int x, int y) {
	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if ((comp != null) && (comp.contains(x - comp.x, y - comp.y)) &&
		(comp.peer instanceof java.awt.peer.LightweightPeer) &&
		(comp.visible == true)) {
		// found a component that intersects the point, see if there is 
		// a deeper possibility.
		if (comp instanceof Container) {
		    Container child = (Container) comp;
		    Component deeper = child.getMouseEventTarget(x - child.x, y - child.y);
		    if (deeper != null) {
			return deeper;
		    }
		} else {
		    if ((comp.mouseListener != null) || 
			((comp.eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0) ||
			(comp.mouseMotionListener != null) ||
			((comp.eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0)) {
			// there isn't a deeper target, but this component is a target
			return comp;
		    }
		}
	    }
	}
	// didn't find a child target, return this component if it's a possible target
	if (((mouseListener != null) || 
	     ((eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0) ||
	     (mouseMotionListener != null) ||
	     ((eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0)) &&
	    (peer instanceof java.awt.peer.LightweightPeer)) {
	    
	    return this;
	}
	// no possible target
	return null;
    }

    /**
     * This is called by lightweight components that want the containing
     * windowed parent to enable some kind of events on their behalf.
     * This is needed for events that are normally only dispatched to 
     * windows to be accepted so that they can be forwarded downward to 
     * the lightweight component that has enabled them.
     */
    void proxyEnableEvents(long events) {
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    // this container is lightweight.... continue sending it
	    // upward.
	    parent.proxyEnableEvents(events);
	} else {
	    // This is a native container, so it needs to host
	    // one of it's children.  If this function is called before
	    // a peer has been created we don't yet have a dispatcher
	    // because it has not yet been determined if this instance
	    // is lightweight.
	    if (dispatcher != null) {
		dispatcher.enableEvents(events);
	    }
	}
    }

    Window getWindow() {
        Container w = this;
        while(!(w instanceof Window)) {
            w = w.getParent();
        }
        return (Window)w;
    }

    /**
     * This is called by lightweight components that have requested focus.
     * The focus request is propagated upward until a native container is
     * found, at which point the native container requests focus and records
     * the component the host is requesting focus for.
     */
    void proxyRequestFocus(Component c) {
	if (peer instanceof java.awt.peer.LightweightPeer) {
	    // this container is lightweight... continue sending it
	    // upward.
	    parent.proxyRequestFocus(c);
	} else {
	    // This is a windowed container, so record true focus
	    // component and request focus from the native window
	    // if needed.
	    if (dispatcher.setFocusRequest(c)) {
		peer.requestFocus();
                Toolkit.getEventQueue().changeKeyEventFocus(this);
	    }
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by dispatchEvent(AWTEvent e)
     */
    public void deliverEvent(Event e) {
	Component comp = getComponentAt(e.x, e.y);
	if ((comp != null) && (comp != this)) {
	    e.translate(-comp.x, -comp.y);
	    comp.deliverEvent(e);
	} else {
	    postEvent(e);
	}
    }

    /**
     * Locates the component that contains the x,y position.  The
     * top-most child component is returned in the case where there
     * is overlap in the components.  This is determined by finding
     * the component closest to the index 0 that claims to contain
     * the given point via Component.contains().
     * @param x the x coordinate
     * @param y the y coordinate
     * @return null if the component does not contain the position.
     * If there is no child component at the requested point and the 
     * point is within the bounds of the container the container itself 
     * is returned, otherwise the top-most child is returned.
     * @see Component#contains 
     */
    public Component getComponentAt(int x, int y) {
	return locate(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by getComponentAt(int, int).
     */
    public Component locate(int x, int y) {
	if (!contains(x, y)) {
	    return null;
	}
	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		if (comp.contains(x - comp.x, y - comp.y)) {
		    return comp;
		}
	    }
	}
	return this;
    }

    /**
     * Locates the component that contains the specified point.
     * @param p the point
     * @return null if the component does not contain the point;
     * returns the component otherwise. 
     * @see Component#contains 
     */
    public Component getComponentAt(Point p) {
	return getComponentAt(p.x, p.y);
    }

    /** 
     * Notifies the container to create a peer. It will also
     * notify the components contained in this container.
     * This method should be called by Container.add, and not by user
     * code directly.
     * @see #removeNotify
     */
    public void addNotify() {
	// addNotify() on the children may cause proxy event enabling
	// on this instance, so we first call super.addNotify() and
	// possibly create an lightweight event dispatcher before calling
	// addNotify() on the children which may be lightweight.
	super.addNotify();
	if (! (peer instanceof java.awt.peer.LightweightPeer)) {
	    dispatcher = new LightweightDispatcher(this);
	}

	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    component[i].addNotify();
	}
    }

    /** 
     * Notifies the container to remove its peer. It will
     * also notify the components contained in this container.
     * This method should be called by Container.remove, and not by user
     * code directly.
     * @see #addNotify
     */
    public void removeNotify() {
	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    component[i].removeNotify();
	}
	super.removeNotify();
    }

    /**
     * Checks if the component is contained in the component hierarchy of
     * this container.
     * @param c the component
     */
    public boolean isAncestorOf(Component c) {
	Container p;
	if (c == null || ((p = c.getParent()) == null)) {
	    return false;
	}
	while (p != null) {
	    if (p == this) {
		return true;
	    }
	    p = p.getParent();
	}
	return false;
    }

    /**
     * Returns the parameter String of this Container.
     */
    protected String paramString() {
	String str = super.paramString();
	LayoutManager layoutMgr = this.layoutMgr;
	if (layoutMgr != null) {
	    str += ",layout=" + layoutMgr.getClass().getName();
	}
	return str;
    }

    /**
     * Prints out a list, starting at the specified indention, to the specified
     * out stream. 
     * @param out the Stream name
     * @param indent the start of the list
     */
    public void list(PrintStream out, int indent) {
	super.list(out, indent);
	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		comp.list(out, indent+1);
	    }
	}
    }

    /**
     * Prints out a list, starting at the specified indention, to the specified
     * print writer.
     */
    public void list(PrintWriter out, int indent) {
	super.list(out, indent);
	int ncomponents = this.ncomponents;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		comp.list(out, indent+1);
	    }
	}
    }

    void setFocusOwner(Component c) {
	Container parent = this.parent;
	if (parent != null) {
	    parent.setFocusOwner(c);
	}
    }

    void preProcessKeyEvent(KeyEvent e) {
        Container parent = this.parent;
        if (parent != null) {
            parent.preProcessKeyEvent(e);
        }
    }

    void postProcessKeyEvent(KeyEvent e) {
        Container parent = this.parent;
        if (parent != null) {
            parent.postProcessKeyEvent(e);
        }
    }

    void transferFocus(Component base) {
	nextFocus(base);
    }

    boolean postsOldMouseEvents() {
        return true;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by transferFocus(Component).
     */
    void nextFocus(Component base) {
	Container parent = this.parent;
	if (parent != null) {
	    parent.transferFocus(base);
	}
    }

    /* Serialization support.  A Container is responsible for
     * restoring the parent fields of its component children. 
     */

    private int containerSerializedDataVersion = 1;


    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, containerListenerK, containerListener);
      s.writeObject(null);
    }


    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      for(int i = 0; i < ncomponents; i++)
	component[i].parent = this;

      Object keyOrNull;
      while(null != (keyOrNull = s.readObject())) {
	String key = ((String)keyOrNull).intern();

	if (containerListenerK == key) 
	  addContainerListener((ContainerListener)(s.readObject()));

	else // skip value for unrecognized key
	  s.readObject();
      }
    }
}


/**
 * Class to manage the dispatching of events to the lightweight
 * components contained by a native container.
 * 
 * @author Timothy Prinzing
 */
class LightweightDispatcher implements java.io.Serializable {

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 5184291520170872969L;

    LightweightDispatcher(Container nativeContainer) {
	this.nativeContainer = nativeContainer;
	focus = null;
	mouseEventTarget = null;
	eventMask = 0;
    }

    /**
     * Enables events to lightweight components.
     */
    void enableEvents(long events) {
	eventMask |= events;
    }

    /**
     * This is called by the hosting native container on behalf of lightweight 
     * components that have requested focus.  The focus request is propagated 
     * upward from the requesting lightweight component until a windowed host 
     * is found, at which point the windowed host calls this method.  This method 
     * returns whether or not the peer associated with the native component needs 
     * to request focus from the native window system. 
     *
     * If a lightweight component already has focus the focus events are synthesized 
     * since there will be no native events to drive the focus.  If the native host 
     * already has focus, the focus gained is synthesized for the lightweight component 
     * requesting focus since it will receive no native focus requests.
     */
    boolean setFocusRequest(Component c) {
	boolean peerNeedsRequest = true;
	Window w = nativeContainer.getWindow();
	if (w != null && c != null) {
	    Component focusOwner = w.getFocusOwner();
	    if (focusOwner == nativeContainer) {
		// This container already has focus, so just 
		// send FOCUS_GAINED event to lightweight component
		c.dispatchEvent(new FocusEvent(c, FocusEvent.FOCUS_GAINED, false));
		peerNeedsRequest = false;
	    } else if (focusOwner == c) {
		// lightweight already has the focus
		peerNeedsRequest = false;
	    } else if (focusOwner == focus) {
		// a lightweight component has focus currently and a new one has been
		// requested.  There won't be any window-system events associated with
		// this so we go ahead and send FOCUS_LOST for the old and FOCUS_GAINED
		// for the new.
		if (focus != null) {
		    focus.dispatchEvent(new FocusEvent(focus, 
						       FocusEvent.FOCUS_LOST, 
						       false));
		}
		c.dispatchEvent(new FocusEvent(c, FocusEvent.FOCUS_GAINED, false));
		peerNeedsRequest = false;
	    }
   	}
	focus = c;
	return peerNeedsRequest;
    }

    /**
     * Dispatches an event to a lightweight sub-component if necessary, and
     * returns whether or not the event was forwarded to a lightweight
     * sub-component.
     *
     * @param e the event
     */
    boolean dispatchEvent(AWTEvent e) {
	if ((eventMask & PROXY_EVENT_MASK) != 0) {
	    if ((e instanceof MouseEvent) && 
		((eventMask & MOUSE_MASK) != 0)) {
		
		MouseEvent me = (MouseEvent) e;
		return processMouseEvent(me);

	    } else if (e instanceof FocusEvent) {
		
		FocusEvent fe = (FocusEvent) e;
		return processFocusEvent(fe);

	    } else if (e instanceof KeyEvent) {

		KeyEvent ke = (KeyEvent) e;
		return processKeyEvent(ke);

	    }
	}
	return false;
    }

    private boolean processKeyEvent(KeyEvent e) {
	if (focus != null) {
	    KeyEvent retargeted = new KeyEvent(focus, 
					       e.getID(), e.getWhen(),
					       e.getModifiers(),
					       e.getKeyCode(),
                                               e.getKeyChar());
	    focus.dispatchEvent(retargeted);
	    return true;
	}
	return false;
    }
	
    private boolean processFocusEvent(FocusEvent e) {
	if (focus != null) {
	    int id = e.getID();
	    FocusEvent retargeted = new FocusEvent(focus, id, e.isTemporary());
	    focus.dispatchEvent(retargeted);
	    if ((id == FocusEvent.FOCUS_LOST) && (e.isTemporary() == false)) {
		focus = null;
	    }
	    return true;
	}
	return false;
    }

    /**
     * This method attempts to distribute a mouse event to a lightweight
     * component.  It tries to avoid doing any unnecessary probes down
     * into the component tree to minimize the overhead of determining
     * where to route the event, since mouse movement events tend to
     * come in large and frequent amounts.
     */
    private boolean processMouseEvent(MouseEvent e) {
	int id = e.getID();
	if (mouseEventTarget != null) {
	    // we are currently forwarding to some component, check
	    // to see if we should continue to forward.
	    switch(id) {
	    case MouseEvent.MOUSE_DRAGGED:
		retargetMouseEvent(id, e);
		break;
	    case MouseEvent.MOUSE_PRESSED:
		dragging = true;
		retargetMouseEvent(id, e);
		break;
	    case MouseEvent.MOUSE_RELEASED:
		dragging = false;
		retargetMouseEvent(id, e);
		Component tr = nativeContainer.getMouseEventTarget(e.getX(), e.getY());
		if (tr != mouseEventTarget) {
		    setMouseTarget(tr, e);
		}
		break;
	    case MouseEvent.MOUSE_CLICKED:
		retargetMouseEvent(id, e);
		break;
	    case MouseEvent.MOUSE_ENTERED:
		retargetMouseEvent(id, e);
		break;
	    case MouseEvent.MOUSE_EXITED:
		if (dragging) {
		    retargetMouseEvent(id, e);
		} else {
		    setMouseTarget(null, e);
		}
		break;
	    case MouseEvent.MOUSE_MOVED:
		Component t = nativeContainer.getMouseEventTarget(e.getX(), e.getY());
		if (t != mouseEventTarget) {
		    setMouseTarget(t, e);
		}
		if (mouseEventTarget != null) {
		    retargetMouseEvent(id, e);
		}
		break;
	    }
	    e.consume();
	} else {
	    // we are not forwarding, see if there is anything we might
	    // start forwarding to.
	    Component t = nativeContainer.getMouseEventTarget(e.getX(), e.getY());
	    if (t != null) {
		setMouseTarget(t, e);
		if (id != MouseEvent.MOUSE_ENTERED) {
		    retargetMouseEvent(id, e);
		}
		e.consume();
	    }
	}

	return e.isConsumed();
    }

    /**
     * Change the current target of mouse events.  This sends 
     * the appropriate MOUSE_EXITED and MOUSE_ENTERED events.
     */
    void setMouseTarget(Component target, MouseEvent e) {
	if (mouseEventTarget != null) {
	    retargetMouseEvent(MouseEvent.MOUSE_EXITED, e);
	} else {
	    nativeCursor = nativeContainer.getCursor();
	}
	mouseEventTarget = target;
	if (mouseEventTarget != null) {
	    retargetMouseEvent(MouseEvent.MOUSE_ENTERED, e);
	} else {
	    nativeContainer.setCursor(nativeCursor);
	}
    }

    /**
     * Sends a mouse event to the current mouse event recipient using
     * the given event (sent to the windowed host) as a prototype.  If
     * the mouse event target is still in the component tree, the 
     * coordinates of the event are translated to those of the target.
     * If the target has been removed, we don't bother to send the
     * message.
     */
    void retargetMouseEvent(int id, MouseEvent e) {
	Point pt = getTranslatedPoint(e.getX(), e.getY(), 
				      nativeContainer, mouseEventTarget);
	if (pt != null) {
	    MouseEvent retargeted = new MouseEvent(mouseEventTarget, 
						   id,
						   e.getWhen(),
						   e.getModifiers(),
						   pt.x, pt.y, e.getClickCount(),
						   e.isPopupTrigger());
	    mouseEventTarget.dispatchEvent(retargeted);

	    // update cursor if needed.  This is done after the event has
	    // been sent to the target so the target has a chance to change
	    // the cursor after reacting to the event.
	    Cursor c = mouseEventTarget.getCursor();
	    if (nativeContainer.getCursor() != c) {
		nativeContainer.setCursor(c);
	    }
	}
    }

    /**
     * Returns the given point in the <i>from</i> components coordinate space
     * translated into the <i>to</i> components coordinate space.  The 
     * <i>from</i> component is expected to parent the <i>to</i> component.
     */
    static Point getTranslatedPoint(int x, int y, Component from, Component to) {
	Component c;
	for(c = to; c != null && c != from; c = c.getParent()) {
	    Rectangle r = c.getBounds();
	    x -= r.x;
	    y -= r.y;
	}
	if (c != null) {
	    return new Point(x, y);
	}
	return null;
    }
	
	
    // --- member variables -------------------------------

    /**
     * The windowed container that might be hosting events for 
     * lightweight components.
     */
    private Container nativeContainer;

    /**
     * The current lightweight component that has focus that is being
     * hosted by this container.  If this is a null reference then 
     * there is currently no focus on a lightweight component being 
     * hosted by this container 
     */
    private Component focus;

    /**
     * The current lightweight component being hosted by this windowed
     * component that has mouse events being forwarded to it.  If this
     * is null, there are currently no mouse events being forwarded to 
     * a lightweight component.
     */
    private transient Component mouseEventTarget;

    /**
     * Indicates if the mouse pointer is currently being dragged...
     * this is needed because we may receive exit events while dragging
     * and need to keep the current mouse target in this case.
     */
    private boolean dragging;

    /**
     * The cursor used by the native container that is hosting the
     * lightweight components.  Since the Cursor used by the lightweight
     * components overwrites the Cursor set in the native container
     * we need to stash the native cursor so we can restore it after
     * the lightweight components are done having their cursor shown.
     */
    private Cursor nativeCursor;

    /**
     * The event mask for contained lightweight components.  Lightweight
     * components need a windowed container to host window-related 
     * events.  This seperate mask indicates events that have been 
     * requested by contained lightweight components without effecting
     * the mask of the windowed component itself.
     */
    private long eventMask;

    /**
     * The kind of events routed to lightweight components from windowed
     * hosts.
     */
    private static final long PROXY_EVENT_MASK =
        AWTEvent.FOCUS_EVENT_MASK | 
        AWTEvent.KEY_EVENT_MASK |
        AWTEvent.MOUSE_EVENT_MASK | 
        AWTEvent.MOUSE_MOTION_EVENT_MASK;

    private static final long MOUSE_MASK = 
        AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK;
}
