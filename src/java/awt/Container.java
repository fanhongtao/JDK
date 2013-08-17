/*
 * @(#)Container.java	1.150 00/04/19
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
import java.awt.event.AWTEventListener;

/**
 * A generic Abstract Window Toolkit(AWT) container object is a component 
 * that can contain other AWT components.
 * <p>
 * Components added to a container are tracked in a list.  The order
 * of the list will define the components' front-to-back stacking order 
 * within the container.  If no index is specified when adding a
 * component to a container, it will be added to the end of the list
 * (and hence to the bottom of the stacking order).
 * @version 	1.150 04/19/00
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 * @see       java.awt.Container#add(java.awt.Component, int)
 * @see       java.awt.Container#getComponent(int)
 * @see       java.awt.LayoutManager
 * @since     JDK1.0
 */
public class Container extends Component {

    /**
     * The number of components in this container.
	 * This value can be null.
	 * @serial
     * @see getComponent()
     * @see getComponents()
     * @see getComponentCount()
     */
    int ncomponents;

    /** 
     * The components in this container.
	 * @serial
     * @see add()
     * @see getComponents()
     */
    Component component[] = new Component[4];

    /** 
     * Layout manager for this container.
	 * @serial
     * @see doLayout()
     * @see setLayout()
     * @see getLayout()
     */
    LayoutManager layoutMgr;

    /**
     * Event router for lightweight components.  If this container
     * is native, this dispatcher takes care of forwarding and 
     * retargeting the events to lightweight components contained
     * (if any).
	 * @serial
     */
    private LightweightDispatcher dispatcher;

    /*
	 * Internal, cached size information.
	 * @serial
     * @see getMaximumSize()
	 * @see getPreferredSize()
	 */
    private Dimension maxSize;

    transient ContainerListener containerListener;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 4613797578919906343L;

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
	initIDs();
    }

    /**
     * Initialize JNI field and method IDs for fields that may be
       called from C.
     */
    private static native void initIDs();

    /**
     * Constructs a new Container. Containers can be extended directly, 
     * but are lightweight in this case and must be contained by a parent
     * somewhere higher up in the component tree that is native.
     * (such as Frame for example).
     */
    public Container() {
    }

    /** 
     * Gets the number of components in this panel.
     * @return    the number of components in this panel.
     * @see       java.awt.Container#getComponent
     * @since     JDK1.1
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
     * @param      n   the index of the component to get.
     * @return     the n<sup>th</sup> component in this container.
     * @exception  ArrayIndexOutOfBoundsException  
     *                 if the n<sup>th</sup> value does not exist.     
     */
    public Component getComponent(int n) {
	synchronized (getTreeLock()) {
	    if ((n < 0) || (n >= ncomponents)) {
		throw new ArrayIndexOutOfBoundsException("No such child: " + n);
	    }
	    return component[n];
	}
    }

    /**
     * Gets all the components in this container.
     * @return    an array of all the components in this container.     
     */
    public Component[] getComponents() {
	return getComponents_NoClientCode();
    }
    // NOTE: This method may be called by privileged threads.
    //       This functionality is implemented in a package-private method 
    //       to insure that it cannot be overridden by client subclasses. 
    //       DO NOT INVOKE CLIENT CODE ON THIS THREAD!
    final Component[] getComponents_NoClientCode() {
	synchronized (getTreeLock()) {
	    Component list[] = new Component[ncomponents];
	    System.arraycopy(component, 0, list, 0, ncomponents);
	    return list;
	}
    } // getComponents_NoClientCode()

    /**
     * Determines the insets of this container, which indicate the size 
     * of the container's border. 
     * <p>
     * A <code>Frame</code> object, for example, has a top inset that 
     * corresponds to the height of the frame's title bar. 
     * @return    the insets of this container.
     * @see       java.awt.Insets
     * @see       java.awt.LayoutManager
     * @since     JDK1.1
     */
    public Insets getInsets() {
    	return insets();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getInsets()</code>.
     */
    public Insets insets() {
	if (this.peer != null && this.peer instanceof ContainerPeer) {
	    ContainerPeer peer = (ContainerPeer)this.peer;
	    return peer.insets();
	}
	return new Insets(0, 0, 0, 0);
    }

    /** 
     * Adds the specified component to the end of this container. 
     * @param     comp   the component to be added.
     * @return    the component argument.     
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
     * position. 
     * @param     comp   the component to be added.
     * @param     index    the position at which to insert the component, 
     *                   or <code>-1</code> to insert the component at the end.
     * @return    the component <code>comp</code>
     * @see	  #remove
     */
    public Component add(Component comp, int index) {
	addImpl(comp, null, index);
	return comp;
    }

    /**
     * Adds the specified component to the end of this container.
     * Also notifies the layout manager to add the component to 
     * this container's layout using the specified constraints object.
     * @param     comp the component to be added
     * @param     constraints an object expressing 
     *                  layout contraints for this component
     * @see       java.awt.LayoutManager
     * @since     JDK1.1
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
     * index. This method also notifies the layout manager to add 
     * the component to this container's layout using the specified 
     * constraints object.
     * <p>
     * This is the method to override if a program needs to track 
     * every add request to a container. An overriding method should 
     * usually include a call to the superclass's version of the method:
     * <p>
     * <blockquote>
     * <code>super.addImpl(comp, constraints, index)</code>
     * </blockquote>
     * <p>
     * @param     comp       the component to be added.
     * @param     constraints an object expressing layout contraints 
     *                 for this component.
     * @param     index the position in the container's list at which to
     *                 insert the component, where <code>-1</code> 
     *                 means insert at the end.
     * @see       java.awt.Container#add(java.awt.Component)       
     * @see       java.awt.Container#add(java.awt.Component, int)       
     * @see       java.awt.Container#add(java.awt.Component, java.lang.Object)       
     * @see       java.awt.LayoutManager
     * @since     JDK1.1
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	synchronized (getTreeLock()) {

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
     * Removes the component, specified by <code>index</code>, 
     * from this container. 
     * @param     index   the index of the component to be removed.
     * @see #add
     * @since JDK1.1
     */
    public void remove(int index) {
	synchronized (getTreeLock()) {
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
	synchronized (getTreeLock()) {
	    if (comp.parent == this)  {
    		/* Search backwards, expect that more recent additions
		 * are more likely to be removed.
    	    	 */
                Component component[] = this.component;
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
	synchronized (getTreeLock()) {
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
     * Causes this container to lay out its components.  Most programs 
     * should not call this method directly, but should invoke 
     * the <code>validate</code> method instead.
     * @see java.awt.LayoutManager#layoutContainer
     * @see #setLayout
     * @see #validate
     * @since JDK1.1
     */
    public void doLayout() {
	layout();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by <code>doLayout()</code>.
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
     * Validates this container and all of its subcomponents.
     * <p>
     * AWT uses <code>validate</code> to cause a container to lay out   
     * its subcomponents again after the components it contains
     * have been added to or modified.
     * @see #validate
     * @see Component#invalidate
     */
    public void validate() {
        /* Avoid grabbing lock unless really necessary. */
	if (!valid) {
	    synchronized (getTreeLock()) {
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
     * that calls this one:  <code>validate</code>.
     */
    protected void validateTree() {
	if (!valid) {
	    doLayout();
            Component component[] = this.component;
	    for (int i = 0 ; i < ncomponents ; ++i) {
		Component comp = component[i];
		if (   (comp instanceof Container) 
	            && !(comp instanceof Window)
		    && !comp.valid) {
		    ((Container)comp).validateTree();
		} else {
		    comp.validate();
		}
	    }
	}
	valid = true;
    }

    /**
     * Recursively descends the container tree and invalidates all
     * contained components.
     */
    void invalidateTree() {
        synchronized (getTreeLock()) {
	    for (int i = 0; i < ncomponents; ++i) {
	        Component comp = component[i];
		if (comp instanceof Container) {
		    ((Container)comp).invalidateTree();
		}
		else {
		    if (comp.valid) {
		        comp.invalidate();
		    }
		}
	    }
	    if (valid) {
	        invalidate();
	    }
	}
    }

    /**
     * Sets the font of this container.
     * @param f The font to become this container's font.
     * @see Component#getFont
     * @since JDK1.0
     */
    public void setFont(Font f) {
        boolean shouldinvalidate = false;

	synchronized (this) {
	    Font oldfont = getFont();
	    super.setFont(f);
	    Font newfont = getFont();
	    if (newfont != oldfont && (oldfont == null ||
				       !oldfont.equals(newfont))) {
	        shouldinvalidate = true;
	    }
	}

	if (shouldinvalidate) {
	    invalidateTree();
	}
    }

    /** 
     * Returns the preferred size of this container.  
     * @return    an instance of <code>Dimension</code> that represents 
     *                the preferred size of this container.
     * @see       java.awt.Container#getMinimumSize       
     * @see       java.awt.Container#getLayout
     * @see       java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     * @see       java.awt.Component#getPreferredSize
     */
    public Dimension getPreferredSize() {
	return preferredSize();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize()</code>.
     */
    public Dimension preferredSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
	 * is available.
	 */ 
    	Dimension dim = prefSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	synchronized (getTreeLock()) {
	    prefSize = (layoutMgr != null) ?
			       layoutMgr.preferredLayoutSize(this) :
			       super.preferredSize();
	    
	    return prefSize;
	}
    }

    /** 
     * Returns the minimum size of this container.  
     * @return    an instance of <code>Dimension</code> that represents 
     *                the minimum size of this container.
     * @see       java.awt.Container#getPreferredSize       
     * @see       java.awt.Container#getLayout
     * @see       java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     * @see       java.awt.Component#getMinimumSize
     * @since     JDK1.1
     */
    public Dimension getMinimumSize() {
	return minimumSize();
    }

    /** 
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize()</code>.
     */
    public Dimension minimumSize() {
	/* Avoid grabbing the lock if a reasonable cached size value
	 * is available.
	 */ 
    	Dimension dim = minSize;
    	if (dim != null && isValid()) {
	    return dim;
	}
	synchronized (getTreeLock()) {
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
	    synchronized (getTreeLock()) {
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
	    synchronized (getTreeLock()) {
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
	    synchronized (getTreeLock()) {
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
            Component component[] = this.component;
	    Rectangle clip = g.getClipRect();
	    for (int i = ncomponents - 1 ; i >= 0 ; i--) {
		Component comp = component[i];
		if (comp != null && 
		    comp.peer instanceof java.awt.peer.LightweightPeer &&
		    comp.visible == true) {

		    Rectangle cr = comp.getBounds();
		    if (( clip == null ) || cr.intersects(clip)) {
			Graphics cg = g.create(cr.x, cr.y, cr.width, cr.height);
			cg.setFont(comp.getFont());
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
     * Updates the container.  This forwards the update to any lightweight components 
     * that are children of this container.  If this method is reimplemented, 
     * super.update(g) should be called so that lightweight components are properly
     * rendered.  If a child component is entirely clipped by the current clipping
     * setting in g, update() will not be forwarded to that child.
     *
     * @param g the specified Graphics window
     * @see   java.awt.Component#update(java.awt.Graphics)
     */
    public void update(Graphics g) {
        if (isShowing()) {
            if (! (peer instanceof java.awt.peer.LightweightPeer)) {
                g.clearRect(0, 0, width, height);
            }
            paint(g);
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
        Component component[] = this.component;
	Rectangle clip = g.getClipRect();
	for (int i = ncomponents - 1 ; i >= 0 ; i--) {
	    Component comp = component[i];
	    if (comp != null &&
		   comp.peer instanceof java.awt.peer.LightweightPeer) {
		Rectangle cr = comp.getBounds();
		if ((clip == null) || cr.intersects(clip)) {
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
     * Paints each of the components in this container. 
     * @param     g   the graphics context.
     * @see       java.awt.Component#paint
     * @see       java.awt.Component#paintAll
     */
    public void paintComponents(Graphics g) {
    	int ncomponents = this.ncomponents;
        Component component[] = this.component;
	for (int i = ncomponents - 1 ; i >= 0 ; i--) {
	    Component comp = component[i];
	    if (comp != null) {
                Graphics cg = comp.getGraphics();
		if (cg != null) {
		    Rectangle parentRect = g.getClipRect();

		    // Calculate the clipping region of the child's graphics
		    // context, by taking the intersection of the parent's
		    // clipRect (if any) and the child's bounds, and then 
		    // translating it's coordinates to be relative to the
		    // child.
		    if (parentRect != null) {
		        Rectangle childRect = comp.getBounds();
			if (childRect.intersects(parentRect) == false) {
			    // Child component is completely clipped out:
			    // ignore.
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
    }

    /** 
     * Prints each of the components in this container. 
     * @param     g   the graphics context.
     * @see       java.awt.Component#print
     * @see       java.awt.Component#printAll
     */
    public void printComponents(Graphics g) {
        int ncomponents = this.ncomponents;
        Component component[] = this.component;

        // A seriously sad hack--
        // Lightweight components always paint behind peered components,
        // even if they are at the top of the Z order. We emulate this
        // behavior by making two printing passes: the first for lightweights;
        // the second for heavyweights.

        for (int i = ncomponents - 1 ; i >= 0 ; i--) {
            Component comp = component[i];
            if (comp != null && 
                    comp.peer instanceof java.awt.peer.LightweightPeer) {
                printOneComponent(g, comp);
            }
        }
        for (int i = ncomponents - 1 ; i >= 0 ; i--) {
            Component comp = component[i];
            if (comp != null && 
                    !(comp.peer instanceof java.awt.peer.LightweightPeer)) {
                printOneComponent(g, comp);
            }
        }
    }

    private void printOneComponent(Graphics g, Component comp) {
        Graphics cg = g.create(comp.x, comp.y, comp.width,
                               comp.height);
        cg.setFont(comp.getFont());
        try {
            comp.printAll(cg);
        } finally {
            cg.dispose();
        }
    }

    /**
     * Simulates the peer callbacks into java.awt for printing of
     * lightweight Containers.
     * @param     g   the graphics context to use for printing.
     * @see       Component#printAll
     * @see       #printComponents
     */
    void lightweightPrint(Graphics g) {
        super.lightweightPrint(g);
        printHeavyweightComponents(g);
    }

    /**
     * Prints all the heavyweight subcomponents.
     */
    void printHeavyweightComponents(Graphics g) {
        int ncomponents = this.ncomponents;
        Component component[] = this.component;
        for (int i = ncomponents - 1 ; i >= 0 ; i--) {
            Component comp = component[i];
            if (comp != null) {
                if (!(comp.peer instanceof java.awt.peer.LightweightPeer)) {
                    printOneComponent(g, comp);
                } else {
                    Graphics cg = g.create(comp.x, comp.y,
                                           comp.width, comp.height);
                    cg.setFont(comp.getFont());
                    try {
                        comp.printHeavyweightComponents(cg);
                    } finally {
                        cg.dispose();
                    }
                }
            }
        }
    }

    /**
     * Adds the specified container listener to receive container events
     * from this container.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l the container listener
     */ 
    public synchronized void addContainerListener(ContainerListener l) {
	if (l == null) {
	    return;
	}
	containerListener = AWTEventMulticaster.add(containerListener, l);
        newEventsOnly = true;	
    }

    /**
     * Removes the specified container listener so it no longer receives
     * container events from this container.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param 	l the container listener
     */ 
    public synchronized void removeContainerListener(ContainerListener l) {
	if (l == null) {
	    return;
	}
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

    /*
     * Dispatches an event to this component, without trying to forward
     * it to any sub components
     * @param e the event
     */
    void dispatchEventToSelf(AWTEvent e) {
	super.dispatchEventImpl(e);
    }

    /**
     * Fetchs the top-most (deepest) lightweight component that is interested
     * in receiving mouse events.
     */
    Component getMouseEventTarget(int x, int y, boolean includeSelf) {
	int ncomponents = this.ncomponents;
        Component component[] = this.component;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if ((comp != null) && (comp.contains(x - comp.x, y - comp.y)) &&
		(comp.peer instanceof java.awt.peer.LightweightPeer) &&
		(comp.visible == true)) {
		// found a component that intersects the point, see if there is 
		// a deeper possibility.
		if (comp instanceof Container) {
		    Container child = (Container) comp;
		    Component deeper = child.getMouseEventTarget(x - child.x, y - child.y, includeSelf);
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
	
	boolean isPeerOK;
	boolean	isMouseOverMe;
	boolean	isMouseListener;
	boolean	isMotionListener;
	
	isPeerOK = (peer instanceof java.awt.peer.LightweightPeer) || includeSelf;
	isMouseOverMe = contains(x,y);
	isMouseListener = (mouseListener != null) ||
			  ((eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0);
	isMotionListener = (mouseMotionListener != null) ||
			   ((eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0);

	// didn't find a child target, return this component if it's a possible target
	if ( isMouseOverMe && isPeerOK && (isMouseListener || isMotionListener) ) {
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
                // If the focus is currently somewhere within this Window,
                // call the peer to set focus to this Container.  This way,
                // we avoid activating this Window if it's not currently
                // active.  -fredx, 2-19-98, bug #4111098
                Component window = this;
                while (!(window instanceof Window))
                    window = window.getParent();
                if (((Window)window).isActive()) {
                    peer.requestFocus();
		}
                Toolkit.getEventQueue().changeKeyEventFocus(this);
	    }
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>dispatchEvent(AWTEvent e)</code>
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
     * @param x the <i>x</i> coordinate
     * @param y the <i>y</i> coordinate
     * @return null if the component does not contain the position.
     * If there is no child component at the requested point and the 
     * point is within the bounds of the container the container itself 
     * is returned; otherwise the top-most child is returned.
     * @see Component#contains
     * @since JDK1.1
     */
    public Component getComponentAt(int x, int y) {
	return locate(x, y);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getComponentAt(int, int)</code>.
     */
    public Component locate(int x, int y) {
	if (!contains(x, y)) {
	    return null;
	}
	int ncomponents = this.ncomponents;
        Component component[] = this.component;
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
     * Gets the component that contains the specified point.
     * @param      p   the point.
     * @return     returns the component that contains the point,
     *                 or <code>null</code> if the component does 
     *                 not contain the point. 
     * @see        java.awt.Component#contains 
     * @since      JDK1.1 
     */
    public Component getComponentAt(Point p) {
	return getComponentAt(p.x, p.y);
    }

    /**
     * Locates the visible child component that contains the specified
     * position.  The top-most child component is returned in the case 
     * where there is overlap in the components.  If the containing child 
     * component is a Container, this method will continue searching for 
     * the deepest nested child component.  Components which are not
     * visible are ignored during the search.<p>
     *
     * The findComponentAt method is different from getComponentAt in
     * that getComponentAt only searches the Container's immediate
     * children; if the containing component is a Container, 
     * findComponentAt will search that child to find a nested component.
     *
     * @param x the <i>x</i> coordinate
     * @param y the <i>y</i> coordinate
     * @return null if the component does not contain the position.
     * If there is no child component at the requested point and the 
     * point is within the bounds of the container the container itself 
     * is returned.
     * @see Component#contains
     * @see getComponentAt
     * @since JDK1.2
     */
    public Component findComponentAt(int x, int y) {
	if (!contains(x, y)) {
	    return null;
	}
        int ncomponents = this.ncomponents;
        Component component[] = this.component;
        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            if (comp != null) {
                if (comp instanceof Container) {
                    comp = ((Container)comp).findComponentAt(x - comp.x,
                                                             y - comp.y);
                } else {
                    comp = comp.locate(x - comp.x, y - comp.y);
                }
                if (comp != null && comp.isVisible()) {
                    return comp;
                }
            }
        }
	return this;
    }

    /**
     * Locates the visible child component that contains the specified
     * point.  The top-most child component is returned in the case 
     * where there is overlap in the components.  If the containing child 
     * component is a Container, this method will continue searching for 
     * the deepest nested child component.  Components which are not
     * visible are ignored during the search.<p>
     *
     * The findComponentAt method is different from getComponentAt in
     * that getComponentAt only searches the Container's immediate
     * children; if the containing component is a Container, 
     * findComponentAt will search that child to find a nested component.
     *
     * @param      p   the point.
     * @return null if the component does not contain the position.
     * If there is no child component at the requested point and the 
     * point is within the bounds of the container the container itself 
     * is returned.
     * @see Component#contains
     * @see getComponentAt
     * @since JDK1.2
     */
    public Component findComponentAt(Point p) {
        return findComponentAt(p.x, p.y);
    }

    /** 
     * Makes this Container displayable by connecting it to
     * a native screen resource.  Making a container displayable will
     * cause any of its children to be made displayable.
     * This method is called internally by the toolkit and should
     * not be called directly by programs.
     * @see Component#isDisplayable
     * @see #removeNotify
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
	    // addNotify() on the children may cause proxy event enabling
	    // on this instance, so we first call super.addNotify() and
	    // possibly create an lightweight event dispatcher before calling
	    // addNotify() on the children which may be lightweight.
	    super.addNotify();
	    if (! (peer instanceof java.awt.peer.LightweightPeer)) {
	        dispatcher = new LightweightDispatcher(this);
	    }
	    int ncomponents = this.ncomponents;
            Component component[] = this.component;
	    for (int i = 0 ; i < ncomponents ; i++) {
	        component[i].addNotify();
	    }
        }
    }

    /** 
     * Makes this Container undisplayable by removing its connection
     * to its native screen resource.  Make a container undisplayable
     * will cause any of its children to be made undisplayable. 
     * This method is called by the toolkit internally and should
     * not be called directly by programs.
     * @see Component#isDisplayable
     * @see #addNotify
     */
    public void removeNotify() {
        synchronized (getTreeLock()) {
	    int ncomponents = this.ncomponents;
            Component component[] = this.component;
	    for (int i = 0 ; i < ncomponents ; i++) {
	        component[i].removeNotify();
	    }
	    super.removeNotify();
        }
    }

    /**
     * Checks if the component is contained in the component hierarchy of
     * this container.
     * @param c the component
     * @return     <code>true</code> if it is an ancestor; 
     *             <code>false</code> otherwise.
     * @since      JDK1.1
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
     * Returns the parameter string representing the state of this 
     * container. This string is useful for debugging. 
     * @return    the parameter string of this container.
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
     * Prints a listing of this container to the specified output 
     * stream. The listing starts at the specified indentation. 
     * @param    out      a print stream.
     * @param    indent   the number of spaces to indent.
     * @see      java.awt.Component#list(java.io.PrintStream, int)
     * @since    JDK
     */
    public void list(PrintStream out, int indent) {
	super.list(out, indent);
	int ncomponents = this.ncomponents;
        Component component[] = this.component;
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
        Component component[] = this.component;
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

    /**
     * Package-visible utility to set the orientation of this container
     * and all components contained within it.
     * @since JDK1.2
     * @see java.awt.Window#applyResourceBundle(java.util.ResourceBundle)
     */
    void applyOrientation(ComponentOrientation o) {
        setComponentOrientation(o);
        
	    for (int i = 0 ; i < ncomponents ; ++i) {
    		Component comp = component[i];
    		if (comp instanceof Container) {
    		    ((Container)comp).applyOrientation(o);
    		} else {
    		    comp.setComponentOrientation(o);
    		}
        }
    }
    
    /* Serialization support.  A Container is responsible for
     * restoring the parent fields of its component children. 
     */
	/*
	 * Container Serial Data Version.
	 * @serial
	 */
    private int containerSerializedDataVersion = 1;

	/**
    * Writes default serializable fields to stream.  Writes
    * a list of serializable ItemListener(s) as optional data.
    * The non-serializable ItemListner(s) are detected and
    * no attempt is made to serialize them.
    *
    * @serialData Null terminated sequence of 0 or more pairs.
    *             The pair consists of a String and Object.
    *             The String indicates the type of object and
    *             is one of the following :
    *             itemListenerK indicating and ItemListener object.
    *
    * @see AWTEventMulticaster.save(ObjectOutputStream, String, EventListener)
    * @see java.awt.Component.itemListenerK
    */
	
    private void writeObject(ObjectOutputStream s)
      throws IOException 
    {
      s.defaultWriteObject();

      AWTEventMulticaster.save(s, containerListenerK, containerListener);
      s.writeObject(null);
    }

	/*
    * Read the ObjectInputStream and if it isnt null
    * add a listener to receive item events fired
    * by the component in the container.
    * Unrecognised keys or values will be Ignored.
    * @serial
    * @see removeActionListener()
    * @see addActionListener()
    */
    private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException 
    {
      s.defaultReadObject();

      Component component[] = this.component;
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
class LightweightDispatcher implements java.io.Serializable, AWTEventListener {

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 5184291520170872969L;
    /*
     * Our own mouse event for when we're dragged over from another hw container
     */
    private static final int  LWD_MOUSE_DRAGGED_OVER = AWTEvent.RESERVED_ID_MAX + 1;

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
            if (focusOwner == null) {

                // No focus in this component
                focus = c;

            } else if (focusOwner == nativeContainer) {
		// This container already has focus, so just 
		// send a FOCUS_LOST event to the container
                // and FOCUS_GAINED event to lightweight component

		nativeContainer.dispatchEvent(new FocusEvent(nativeContainer, FocusEvent.FOCUS_LOST, false));
		focus = c ;
		c.dispatchEvent(new FocusEvent(c, FocusEvent.FOCUS_GAINED, false));
		
		peerNeedsRequest = false;
	    } else if (focusOwner == c) {
		// lightweight already has the focus
		focus = c ;
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
		focus = c ;
                c.dispatchEvent(new FocusEvent(c, FocusEvent.FOCUS_GAINED, false));
		peerNeedsRequest = false;
	     } else {
		//Fix for bug 4095214
		//Paul Sheehan
		focus = c;
	     }
   	}
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
	    // Don't duplicate the event here.
	    // The KeyEvent for LightWeightComponent is also passed to 
	    // input methods and their native handlers. The native handlers
	    // require the original native event data to be attached to
	    // the KeyEvent.
	    Component source = e.getComponent();
	    ((AWTEvent)e).setSource(focus);
	    focus.dispatchEvent(e);
	    ((AWTEvent)e).setSource(source);
	    return e.isConsumed();
	}
	return false;
    }
	
    private boolean processFocusEvent(FocusEvent e) {
	if (focus != null) {
	    int id = e.getID();
	    FocusEvent retargeted = new FocusEvent(focus, id, e.isTemporary());
	    ((AWTEvent)e).copyPrivateDataInto(retargeted);
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
	Component targetOver;
	Component lwOver;

	targetOver = nativeContainer.getMouseEventTarget(e.getX(), e.getY(),true);
	trackMouseEnterExit(targetOver, e);

	if (mouseEventTarget == null) {
	    if ( id == MouseEvent.MOUSE_MOVED ||
	    	 id == MouseEvent.MOUSE_PRESSED ) {
		lwOver = (targetOver != nativeContainer) ? targetOver : null;
		setMouseTarget(lwOver,e);
	    }
	}

	if (mouseEventTarget != null) {
	    // we are currently forwarding to some component, check
	    // to see if we should continue to forward.
	    switch(id) {
	    case MouseEvent.MOUSE_DRAGGED:
		if(dragging) {
		    retargetMouseEvent(mouseEventTarget, id, e);
		}
		break;
	    case MouseEvent.MOUSE_PRESSED:
		dragging = true;
		retargetMouseEvent(mouseEventTarget, id, e);
		break;
	    case MouseEvent.MOUSE_RELEASED:
	    {
		Component releasedTarget = mouseEventTarget;
		dragging = false;
		retargetMouseEvent(mouseEventTarget, id, e);
		lwOver = nativeContainer.getMouseEventTarget(e.getX(), e.getY(),false);
		setMouseTarget(lwOver, e);
		if (lwOver != releasedTarget) {
		// fix 4155217
		// component was hidden or moved in user code MOUSE_RELEASED handling
		    isClickOrphaned = true;
		}
		break;
	    }
	    
	    case MouseEvent.MOUSE_CLICKED:
		if (!isClickOrphaned) {
		// fix 4155217
		// click event should not be redirected since component has moved or hidden
		    retargetMouseEvent(mouseEventTarget, id, e);
		}
		isClickOrphaned = false;
		break;
	    case MouseEvent.MOUSE_ENTERED:
		break;
	    case MouseEvent.MOUSE_EXITED:
		if (!dragging) {
		    setMouseTarget(null, e);
		}
		break;
	    case MouseEvent.MOUSE_MOVED:
		lwOver = nativeContainer.getMouseEventTarget(e.getX(), e.getY(),false);
		setMouseTarget(lwOver, e);
		retargetMouseEvent(mouseEventTarget, id, e);
		break;
	    }
	    e.consume();
	}

	return e.isConsumed();
    }

    /**
     * Change the current target of mouse events.
     */
    private void setMouseTarget(Component target, MouseEvent e) {
	if (target != mouseEventTarget) {
	    //System.out.println("setMouseTarget: " + target);
	    mouseEventTarget = target;
	}
    }

    /*
     * Generates enter/exit events as mouse moves over lw components
     * @param targetOver	Target mouse is over (including native container)
     * @param e			Mouse event in native container
     */
    private void trackMouseEnterExit(Component targetOver, MouseEvent e) {
	Component	targetEnter = null;
	int		id = e.getID();

	if ( id != MouseEvent.MOUSE_EXITED &&
	     id != MouseEvent.MOUSE_DRAGGED &&
	     id != LWD_MOUSE_DRAGGED_OVER &&
	     isMouseInNativeContainer == false ) {
	    // any event but an exit or drag means we're in the native container
	    isMouseInNativeContainer = true;
	    startListeningForOtherDrags();
	} else if ( id == MouseEvent.MOUSE_EXITED ) {
	    isMouseInNativeContainer = false;
	    stopListeningForOtherDrags();
	}

	if (isMouseInNativeContainer) {
	    targetEnter = targetOver;
	}
	//System.out.println("targetEnter = " + targetEnter);
	//System.out.println("targetLastEntered = " + targetLastEntered);

	if (targetLastEntered == targetEnter) {
	    return;
	}

	retargetMouseEvent(targetLastEntered, MouseEvent.MOUSE_EXITED, e);
	if (id == MouseEvent.MOUSE_EXITED) {
	    // consume native exit event if we generate one
	    e.consume();
	}

	retargetMouseEvent(targetEnter, MouseEvent.MOUSE_ENTERED, e);
	if (id == MouseEvent.MOUSE_ENTERED) {
	    // consume native enter event if we generate one
	    e.consume();
	}

	//System.out.println("targetLastEntered: " + targetLastEntered);
	targetLastEntered = targetEnter;
    }

    private void startListeningForOtherDrags() {
	//System.out.println("Adding AWTEventListener");
	java.security.AccessController.doPrivileged(
	    new java.security.PrivilegedAction() {
		public Object run() {
		    nativeContainer.getToolkit().addAWTEventListener(
		    	LightweightDispatcher.this,
			AWTEvent.MOUSE_EVENT_MASK |
			AWTEvent.MOUSE_MOTION_EVENT_MASK);
		    return null;
		}
	    }
	);
    }

    private void stopListeningForOtherDrags() {
	//System.out.println("Removing AWTEventListener");
	java.security.AccessController.doPrivileged(
	    new java.security.PrivilegedAction() {
		public Object run() {
		    nativeContainer.getToolkit().removeAWTEventListener(LightweightDispatcher.this);
		    return null;
		}
	    }
	);
    }

    /*
     * (Implementation of AWTEventListener)
     * Listen for drag events posted in other hw components so we can
     * track enter/exit regardless of where a drag originated
     */
    public void eventDispatched(AWTEvent e) {
	boolean isForeignDrag = (e instanceof MouseEvent) &&
				(e.id == MouseEvent.MOUSE_DRAGGED) &&
				(e.getSource() != nativeContainer);
	
	if (!isForeignDrag) {
	    // only interested in drags from other hw components
	    return;
	}

	MouseEvent	srcEvent = (MouseEvent)e;
	MouseEvent	me;

	synchronized (nativeContainer.getTreeLock()) {
	    Component srcComponent = srcEvent.getComponent();

	    // component may have disappeared since drag event posted
	    // (i.e. Swing hierarchical menus)
	    if ( !srcComponent.isShowing() ||
		 !nativeContainer.isShowing() ) {
		return;
	    }

	    //
	    // create an internal 'dragged-over' event indicating
	    // we are being dragged over from another hw component
	    //
	    me = new MouseEvent(nativeContainer,
			       LWD_MOUSE_DRAGGED_OVER,
			       srcEvent.getWhen(),
			       srcEvent.getModifiers(),
			       srcEvent.getX(),
			       srcEvent.getY(),
			       srcEvent.getClickCount(),
			       srcEvent.isPopupTrigger());
	    ((AWTEvent)srcEvent).copyPrivateDataInto(me);
	    // translate coordinates to this native container
	    Point	ptSrcOrigin = srcComponent.getLocationOnScreen();
	    Point	ptDstOrigin = nativeContainer.getLocationOnScreen();
	    me.translatePoint( ptSrcOrigin.x - ptDstOrigin.x, ptSrcOrigin.y - ptDstOrigin.y );
	}
	//System.out.println("Track event: " + me);
	// feed the 'dragged-over' event directly to the enter/exit
	// code (not a real event so don't pass it to dispatchEvent)
	Component targetOver = nativeContainer.getMouseEventTarget(me.getX(), me.getY(), true);
	trackMouseEnterExit(targetOver, me);
    }

    /**
     * Sends a mouse event to the current mouse event recipient using
     * the given event (sent to the windowed host) as a srcEvent.  If
     * the mouse event target is still in the component tree, the 
     * coordinates of the event are translated to those of the target.
     * If the target has been removed, we don't bother to send the
     * message.
     */
    void retargetMouseEvent(Component target, int id, MouseEvent e) {
	if (target == null) {
	    return; // mouse is over another hw component
	}

        int x = e.getX(), y = e.getY();
        Component component;

        for(component = target;
            component != null && component != nativeContainer;
            component = component.getParent()) {
            x -= component.x;
            y -= component.y;
        }
        if (component != null) {
            MouseEvent retargeted = new MouseEvent(target,
                                                   id, 
                                                   e.getWhen(), 
                                                   e.getModifiers(),
                                                   x, 
                                                   y, 
                                                   e.getClickCount(), 
                                                   e.isPopupTrigger());
	    ((AWTEvent)e).copyPrivateDataInto(retargeted);

	    if (target == nativeContainer) {
		// avoid recursively calling LightweightDispatcher...
		((Container)target).dispatchEventToSelf(retargeted);
	    } else {
		target.dispatchEvent(retargeted);
	    }

            // update cursor if needed.  This is done after the event has
            // been sent to the target so the target has a chance to change
            // the cursor after reacting to the event.
	    Cursor c = target.getCursor();
	    if (nativeContainer.getCursor() != c) {
		//System.out.println("Setting cursor to: " + c.getType());
		nativeContainer.setCursor(c);
	    }
        }
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
     * The last component entered
     */
    private transient Component targetLastEntered;

    /**
     * Is the mouse over the native container
     */
    private transient boolean isMouseInNativeContainer = false;

    /**
     * Is the next click event orphaned because the component hid/moved
     */
    private transient boolean isClickOrphaned = false;

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
