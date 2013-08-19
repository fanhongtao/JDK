/*
 * @(#)Container.java	1.239 03/02/26
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.awt.peer.ContainerPeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.ContainerListener;
import java.util.EventListener;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.dnd.DropTarget;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;
import javax.accessibility.*;
import java.beans.PropertyChangeListener;
import javax.swing.JRootPane;

import sun.awt.AppContext;
import sun.awt.DebugHelper;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;

/**
 * A generic Abstract Window Toolkit(AWT) container object is a component 
 * that can contain other AWT components.
 * <p>
 * Components added to a container are tracked in a list.  The order
 * of the list will define the components' front-to-back stacking order 
 * within the container.  If no index is specified when adding a
 * component to a container, it will be added to the end of the list
 * (and hence to the bottom of the stacking order).
 * @version 	1.239, 02/26/03
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 * @see       #add(java.awt.Component, int)
 * @see       #getComponent(int)
 * @see       LayoutManager
 * @since     JDK1.0
 */
public class Container extends Component {

    /**
     * The number of components in this container.
     * This value can be null.
     * @serial
     * @see #getComponent
     * @see #getComponents
     * @see #getComponentCount
     */
    int ncomponents;

    /** 
     * The components in this container.
     * @serial
     * @see #add
     * @see #getComponents
     */
    Component component[] = new Component[0];

    /** 
     * Layout manager for this container.
     * @serial
     * @see #doLayout
     * @see #setLayout
     * @see #getLayout
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
     * @see #getMaximumSize
     * @see #getPreferredSize
     */
    private Dimension maxSize;

    /**
     * The focus traversal policy that will manage keyboard traversal of this
     * Container's children, if this Container is a focus cycle root. If the
     * value is null, this Container inherits its policy from its focus-cycle-
     * root ancestor. If all such ancestors of this Container have null
     * policies, then the current KeyboardFocusManager's default policy is
     * used. If the value is non-null, this policy will be inherited by all
     * focus-cycle-root children that have no keyboard-traversal policy of
     * their own (as will, recursively, their focus-cycle-root children).
     * <p>
     * If this Container is not a focus cycle root, the value will be
     * remembered, but will not be used or inherited by this or any other
     * Containers until this Container is made a focus cycle root.
     *
     * @serial
     * @see #setFocusTraversalPolicy
     * @see #getFocusTraversalPolicy
     * @since 1.4
     */
    private transient FocusTraversalPolicy focusTraversalPolicy;
 
    /**
     * Indicates whether this Component is the root of a focus traversal cycle.
     * Once focus enters a traversal cycle, typically it cannot leave it via
     * focus traversal unless one of the up- or down-cycle keys is pressed.
     * Normal traversal is limited to this Container, and all of this
     * Container's descendants that are not descendants of inferior focus cycle
     * roots.
     *
     * @serial
     * @see #setFocusCycleRoot
     * @see #isFocusCycleRoot
     * @since 1.4
     */
    private boolean focusCycleRoot = false;
 
    // keeps track of the threads that are printing this component
    private transient Set printingThreads;
    // True if there is at least one thread that's printing this component
    private transient boolean printing = false;

    transient ContainerListener containerListener;

    /* HierarchyListener and HierarchyBoundsListener support */
    transient int listeningChildren;
    transient int listeningBoundsChildren;
    transient int descendantsCount;

    /**
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 4613797578919906343L;

    private static final DebugHelper dbg = DebugHelper.create(Container.class);

    /**
     * A constant which toggles one of the controllable behaviors 
     * of <code>getMouseEventTarget</code>. It is used to specify whether 
     * the method can return the Container on which it is originally called 
     * in case if none of its children are the current mouse event targets.
     * 
     * @see #getMouseEventTarget(int, int, boolean, boolean, boolean)
     */
    static final boolean INCLUDE_SELF = true;

    /**
     * A constant which toggles one of the controllable behaviors 
     * of <code>getMouseEventTarget</code>. It is used to specify whether 
     * the method should search only lightweight components.
     * 
     * @see #getMouseEventTarget(int, int, boolean, boolean, boolean)
     */
    static final boolean SEARCH_HEAVYWEIGHTS = true;

    static {
        /* ensure that the necessary native libraries are loaded */
	Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
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

    void initializeFocusTraversalKeys() {
	focusTraversalKeys = new Set[4];
    }
 
    /** 
     * Gets the number of components in this panel.
     * @return    the number of components in this panel.
     * @see       #getComponent
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
     * @see       Insets
     * @see       LayoutManager
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
	    return (Insets)peer.insets().clone();
	}
	return new Insets(0, 0, 0, 0);
    }

    /** 
     * Appends the specified component to the end of this container. 
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     *
     * @param     comp   the component to be added
     * @see #addImpl
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     * @return    the component argument
     */
    public Component add(Component comp) {
        addImpl(comp, null, -1);
	return comp;
    }

    /**
     * Adds the specified component to this container.
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * This method is obsolete as of 1.1.  Please use the
     * method <code>add(Component, Object)</code> instead.
     * @see #add(Component, Object)
     */
    public Component add(String name, Component comp) {
	addImpl(comp, name, -1);
	return comp;
    }

    /** 
     * Adds the specified component to this container at the given 
     * position. 
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     *
     * @param     comp   the component to be added
     * @param     index    the position at which to insert the component, 
     *                   or <code>-1</code> to append the component to the end
     * @return    the component <code>comp</code>
     * @see #addImpl
     * @see #remove
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     */
    public Component add(Component comp, int index) {
	addImpl(comp, null, index);
	return comp;
    }

    void setZOrder(Component comp, int index) {
	synchronized (getTreeLock()) {
	    /* Check for correct arguments:  index in bounds,
	     * comp cannot be one of this container's parents,
	     * and comp cannot be a window.
	     * comp and container must be on the same GraphicsDevice.
	     * if comp is container, all sub-components must be on
	     * same GraphicsDevice.
	     */
	    GraphicsConfiguration thisGC = this.getGraphicsConfiguration();

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
                if (comp instanceof Window) {
                    throw new IllegalArgumentException(
                                  "adding a window to a container");
                }
            }
            if (!(comp.peer instanceof LightweightPeer)) {
                throw new IllegalArgumentException("should be lightweight component");
            }

            Container nativeContainer = 
                (this.peer instanceof LightweightPeer) ? getNativeContainer() : this;
            if (nativeContainer != comp.getNativeContainer()) {
                throw new IllegalArgumentException("component should be in the same heavyweight container");
            }
            if (thisGC != null) {
                comp.checkGD(thisGC.getDevice().getIDstring());
            }

	    /* Reparent the component and tidy up the tree's state. */
	    if (comp.parent != null) {
                Container oldParent = comp.parent;
                /* Search backwards, expect that more recent additions
                 * are more likely to be removed.
                 */
                Component component[] = oldParent.component;
                int ncomponents = oldParent.ncomponents;
                for (int i = ncomponents; --i >= 0; ) {
                    if (component[i] == comp) {

                        if (oldParent.layoutMgr != null) {
                            oldParent.layoutMgr.removeLayoutComponent(comp);
                        }

                        oldParent.adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK, 
       	                    -comp.numListening(AWTEvent.HIERARCHY_EVENT_MASK));
                        oldParent.adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
                            -comp.numListening(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
                        oldParent.adjustDescendants(-(comp.countHierarchyMembers()));

                        comp.parent = null;
                        System.arraycopy(oldParent.component, i + 1,
                                         oldParent.component, i,
                                         oldParent.ncomponents - i - 1);
                        component[--oldParent.ncomponents] = null;

                        if (oldParent.valid) {
                            oldParent.invalidate();
                        }
                        if (oldParent.containerListener != null ||
                            (oldParent.eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                            Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
                            ContainerEvent e = new ContainerEvent(oldParent, 
                                                       ContainerEvent.COMPONENT_REMOVED,
                                                       comp);
                            oldParent.dispatchEvent(e);
                        }

                        comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED, comp,
				 oldParent, HierarchyEvent.PARENT_CHANGED,
                                 Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
                        if (oldParent.peer != null && oldParent.layoutMgr == null && oldParent.isVisible()) {
                            oldParent.updateCursorImmediately();
                        }
                    }
                }
                if (index > ncomponents) {
                    throw new IllegalArgumentException("illegal component position");
                }
            }

	    /* Add component to list; allocate new array if necessary. */
	    if (ncomponents == component.length) {
		Component newcomponents[] = new Component[ncomponents * 2 + 1];
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

	    adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK, 
	        comp.numListening(AWTEvent.HIERARCHY_EVENT_MASK));
	    adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
		comp.numListening(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
            adjustDescendants(comp.countHierarchyMembers());

	    if (valid) {
		invalidate();
	    }
	    if (peer != null) {
		comp.addNotify();
	    }
	    
	    /* Notify the layout manager of the added component. */
	    if (layoutMgr != null) {
		if (layoutMgr instanceof LayoutManager2) {
		    ((LayoutManager2)layoutMgr).addLayoutComponent(comp, null);
		}
	    }
            if (containerListener != null || 
                (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
                ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_ADDED,
                                     comp);
                dispatchEvent(e);
            }

	    comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED, comp,
				       this, HierarchyEvent.PARENT_CHANGED,
                                       Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
	    if (peer != null && layoutMgr == null && isVisible()) {
                updateCursorImmediately();
	    }
        }
    }

    /**
     * Adds the specified component to the end of this container.
     * Also notifies the layout manager to add the component to 
     * this container's layout using the specified constraints object.
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     *
     * @param     comp the component to be added
     * @param     constraints an object expressing 
     *                  layout contraints for this component
     * @see #addImpl
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
     * @see       LayoutManager
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
     * This is a convenience method for {@link #addImpl}.
     * <p>
     * Note: If a component has been added to a container that
     * has been displayed, <code>validate</code> must be
     * called on that container to display the new component.
     * If multiple components are being added, you can improve
     * efficiency by calling <code>validate</code> only once,
     * after all the components have been added.
     *
     * @param comp the component to be added
     * @param constraints an object expressing layout contraints for this
     * @param index the position in the container's list at which to insert
     * the component; <code>-1</code> means insert at the end
     * component
     * @see #addImpl
     * @see #validate
     * @see javax.swing.JComponent#revalidate()
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
     * constraints object via the <code>addLayoutComponent</code>
     * method.  The constraints are
     * defined by the particular layout manager being used.  For 
     * example, the <code>BorderLayout</code> class defines five
     * constraints: <code>BorderLayout.NORTH</code>,
     * <code>BorderLayout.SOUTH</code>, <code>BorderLayout.EAST</code>,
     * <code>BorderLayout.WEST</code>, and <code>BorderLayout.CENTER</code>.
     *
     * <p>Note that if the component already exists
     * in this container or a child of this container, 
     * it is removed from that container before
     * being added to this container. 
     * <p>
     * This is the method to override if a program needs to track 
     * every add request to a container as all other add methods defer
     * to this one. An overriding method should 
     * usually include a call to the superclass's version of the method:
     * <p>
     * <blockquote>
     * <code>super.addImpl(comp, constraints, index)</code>
     * </blockquote>
     * <p>
     * @param     comp       the component to be added
     * @param     constraints an object expressing layout constraints 
     *                 for this component
     * @param     index the position in the container's list at which to
     *                 insert the component, where <code>-1</code> 
     *                 means append to the end
     * @exception IllegalArgumentException if <code>index</code> is invalid
     * @exception IllegalArgumentException if adding the container's parent
     *			to itself
     * @exception IllegalArgumentException if adding a window to a container
     * @see       #add(Component)       
     * @see       #add(Component, int)       
     * @see       #add(Component, java.lang.Object)       
     * @see       LayoutManager
     * @see       LayoutManager2
     * @since     JDK1.1
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	synchronized (getTreeLock()) {
	    /* Check for correct arguments:  index in bounds,
	     * comp cannot be one of this container's parents,
	     * and comp cannot be a window.
	     * comp and container must be on the same GraphicsDevice.
	     * if comp is container, all sub-components must be on
	     * same GraphicsDevice.
	     */
	    GraphicsConfiguration thisGC = this.getGraphicsConfiguration();

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
            if (comp instanceof Window) {
                throw new IllegalArgumentException(
                       "adding a window to a container");
            }
        }
        if (thisGC != null) {
            comp.checkGD(thisGC.getDevice().getIDstring());
        }

	    /* Reparent the component and tidy up the tree's state. */
	    if (comp.parent != null) {
		comp.parent.remove(comp);
                    if (index > ncomponents) {
                        throw new IllegalArgumentException("illegal component position");
                    }
            }

	    /* Add component to list; allocate new array if necessary. */
	    if (ncomponents == component.length) {
		Component newcomponents[] = new Component[ncomponents * 2 + 1];
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

	    adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK, 
	        comp.numListening(AWTEvent.HIERARCHY_EVENT_MASK));
	    adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
		comp.numListening(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
            adjustDescendants(comp.countHierarchyMembers());

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
                (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
                ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_ADDED,
                                     comp);
                dispatchEvent(e);
            }

	    comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED, comp,
				       this, HierarchyEvent.PARENT_CHANGED,
                                       Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
	    if (peer != null && layoutMgr == null && isVisible()) {
                updateCursorImmediately();
	    }
	}
    }

    /**
     * Checks that all Components that this Container contains are on
     * the same GraphicsDevice as this Container.  If not, throws an
     * IllegalArgumentException.
     */
    void checkGD(String stringID) {
		Component tempComp;
		for (int i = 0; i < component.length; i++) {
			tempComp= component[i];
			if (tempComp != null) {
				tempComp.checkGD(stringID);
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
            if (index < 0  || index >= ncomponents) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
    	    Component comp = component[index];
	    if (peer != null) {
		comp.removeNotify();
	    }
	    if (layoutMgr != null) {
		layoutMgr.removeLayoutComponent(comp);
	    }

	    adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK, 
	        -comp.numListening(AWTEvent.HIERARCHY_EVENT_MASK));
	    adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
		-comp.numListening(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
            adjustDescendants(-(comp.countHierarchyMembers()));

	    comp.parent = null;
	    System.arraycopy(component, index + 1,
			     component, index,
			     ncomponents - index - 1);
	    component[--ncomponents] = null;

	    if (valid) {
		invalidate();
	    }
            if (containerListener != null ||
                (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
                ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_REMOVED,
                                     comp);
                dispatchEvent(e);
            }

	    comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED, comp,
				       this, HierarchyEvent.PARENT_CHANGED,
                                       Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
	    if (peer != null && layoutMgr == null && isVisible()) {
                updateCursorImmediately();
	    }
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
	    adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK,
                                    -listeningChildren);
	    adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
		                    -listeningBoundsChildren);
            adjustDescendants(-descendantsCount);

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
                   (eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 ||
                    Toolkit.enabledOnToolkit(AWTEvent.CONTAINER_EVENT_MASK)) {
                    ContainerEvent e = new ContainerEvent(this, 
                                     ContainerEvent.COMPONENT_REMOVED,
                                     comp);
                    dispatchEvent(e);
                }

		comp.createHierarchyEvents(HierarchyEvent.HIERARCHY_CHANGED,
					   comp, this,
					   HierarchyEvent.PARENT_CHANGED,
                                           Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_EVENT_MASK));
	    }
	    if (peer != null && layoutMgr == null && isVisible()) {
                updateCursorImmediately();
	    }
	    if (valid) {
		invalidate();
	    }
	}
    }

    // Should only be called while holding tree lock
    int numListening(long mask) {
        int superListening = super.numListening(mask);

        if (mask == AWTEvent.HIERARCHY_EVENT_MASK) {
	    if (dbg.on) {
	        // Verify listeningChildren is correct
	        int sum = 0;
		for (int i = 0; i < ncomponents; i++) {
		    sum += component[i].numListening(mask);
		}
		dbg.assertion(listeningChildren == sum);
	    }
	    return listeningChildren + superListening;
	} else if (mask == AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK) {
	    if (dbg.on) {
	        // Verify listeningBoundsChildren is correct
	        int sum = 0;
		for (int i = 0; i < ncomponents; i++) {
		    sum += component[i].numListening(mask);
		}
		dbg.assertion(listeningBoundsChildren == sum);
	    }
	    return listeningBoundsChildren + superListening;
	} else {
	    if (dbg.on) {
	        dbg.assertion(false);
	    }
	    return superListening;
	}
    }

    // Should only be called while holding tree lock
    void adjustListeningChildren(long mask, int num) {
        if (dbg.on) {
	    dbg.assertion(mask == AWTEvent.HIERARCHY_EVENT_MASK ||
		       mask == AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK ||
		       mask == (AWTEvent.HIERARCHY_EVENT_MASK |
				AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
	}

        if (num == 0)
	    return;

	if ((mask & AWTEvent.HIERARCHY_EVENT_MASK) != 0) {
	    listeningChildren += num;
	}
	if ((mask & AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK) != 0) {
	    listeningBoundsChildren += num;
	}

        adjustListeningChildrenOnParent(mask, num);
    }

    // Should only be called while holding tree lock
    void adjustDescendants(int num) {
        if (num == 0) 
            return;

        descendantsCount += num;
        adjustDecendantsOnParent(num);
    }

    // Should only be called while holding tree lock
    void adjustDecendantsOnParent(int num) {
        if (parent != null) {
            parent.adjustDescendants(num);
        }
    }

    // Should only be called while holding tree lock
    int countHierarchyMembers() {
        if (dbg.on) {
            // Verify descendantsCount is correct
            int sum = 0;
            for (int i = 0; i < ncomponents; i++) {
                sum += component[i].countHierarchyMembers();
            }
            dbg.assertion(descendantsCount == sum);
        }
        return descendantsCount;
    }

    // Should only be called while holding tree lock
    int createHierarchyEvents(int id, Component changed,
			      Container changedParent, long changeFlags,
                              boolean enabledOnToolkit) {
        int listeners = 0;
	switch (id) {
	  case HierarchyEvent.HIERARCHY_CHANGED:
	    listeners = listeningChildren;
	    break;
	  case HierarchyEvent.ANCESTOR_MOVED:
	  case HierarchyEvent.ANCESTOR_RESIZED:
	    if (dbg.on) {
	        dbg.assertion(changeFlags == 0);
	    }
	    listeners = listeningBoundsChildren;
	    break;
	  default:
	    if (dbg.on) {
	        dbg.assertion(false);
	    }
	    break;
	}

        if (enabledOnToolkit) {
            listeners = descendantsCount;
        }

        for (int count = listeners, i = 0; count > 0; i++) {
	    count -= component[i].createHierarchyEvents(id, changed,
							changedParent,
							changeFlags,
                                                        enabledOnToolkit);
	}
	return listeners + 
	    super.createHierarchyEvents(id, changed, changedParent,
					changeFlags, enabledOnToolkit);
    }

    void createChildHierarchyEvents(int id, long changeFlags, 
                                    boolean enabledOnToolkit) {
        synchronized (getTreeLock()) {
	    int listeners = 0;
	    switch (id) {
	      case HierarchyEvent.HIERARCHY_CHANGED:
		listeners = listeningChildren;
	        break;
	      case HierarchyEvent.ANCESTOR_MOVED:
	      case HierarchyEvent.ANCESTOR_RESIZED:
		if (dbg.on) {
		    dbg.assertion(changeFlags == 0);
		}
		listeners = listeningBoundsChildren;
	        break;
	      default:
		if (dbg.on) {
		    dbg.assertion(false);
		}
	        break;
	    }

            if (enabledOnToolkit) {
                listeners = descendantsCount;
            }
	  
	    for (int count = listeners, i = 0; count > 0; i++) {
	        count -= component[i].createHierarchyEvents(id, this, parent,
							    changeFlags,
                                                            enabledOnToolkit);
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
     * @see LayoutManager#layoutContainer
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
        LayoutManager layoutMgr = this.layoutMgr;
	if (layoutMgr instanceof LayoutManager2) {
	    LayoutManager2 lm = (LayoutManager2) layoutMgr;
	    lm.invalidateLayout(this);
	}
	super.invalidate();
    }

    /** 
     * Validates this container and all of its subcomponents.
     * <p>
     * The <code>validate</code> method is used to cause a container
     * to lay out its subcomponents again. It should be invoked when
     * this container's subcomponents are modified (added to or
     * removed from the container, or layout-related information
     * changed) after the container has been displayed.
     *
     * @see #add(java.awt.Component)
     * @see Component#invalidate
     * @see javax.swing.JComponent#revalidate()
     */
    public void validate() {
        /* Avoid grabbing lock unless really necessary. */
	if (!valid) {
	    boolean updateCur = false;
	    synchronized (getTreeLock()) {
		if (!valid && peer != null) {
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
			updateCur = isVisible();
		    }
		}
	    }	    
	    if (updateCur) {
                updateCursorImmediately();
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
	    if (peer instanceof ContainerPeer) {
		((ContainerPeer)peer).beginLayout();
	    }
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
	    if (peer instanceof ContainerPeer) {
		((ContainerPeer)peer).endLayout();
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

	Font oldfont = getFont();
	super.setFont(f);
	Font newfont = getFont();
	if (newfont != oldfont && (oldfont == null ||
				   !oldfont.equals(newfont))) {
	    invalidateTree();
	}
    }

    /** 
     * Returns the preferred size of this container.  
     * @return    an instance of <code>Dimension</code> that represents 
     *                the preferred size of this container.
     * @see       #getMinimumSize       
     * @see       #getLayout
     * @see       LayoutManager#preferredLayoutSize(Container)
     * @see       Component#getPreferredSize
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
     * @see       #getPreferredSize       
     * @see       #getLayout
     * @see       LayoutManager#minimumLayoutSize(Container)
     * @see       Component#getMinimumSize
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
     * Paints the container. This forwards the paint to any lightweight
     * components that are children of this container. If this method is
     * reimplemented, super.paint(g) should be called so that lightweight
     * components are properly rendered. If a child component is entirely
     * clipped by the current clipping setting in g, paint() will not be
     * forwarded to that child.
     *
     * @param g the specified Graphics window
     * @see   Component#update(Graphics)
     */
    public void paint(Graphics g) {
	if (isShowing()) {
	    if (printing) {
	        synchronized (this) {
		    if (printing) {
		        if (printingThreads.contains(Thread.currentThread())) {
			    return;
			}
		    }
		}
	    }

	    // The container is showing on screen and
	    // this paint() is not called from print().
	    // Paint self and forward the paint to lightweight subcomponents.

	    // super.paint(); -- Don't bother, since it's a NOP.

	    GraphicsCallback.PaintCallback.getInstance().
	        runComponents(component, g, GraphicsCallback.LIGHTWEIGHTS);
	}
    }

    /** 
     * Updates the container.  This forwards the update to any lightweight
     * components that are children of this container.  If this method is
     * reimplemented, super.update(g) should be called so that lightweight
     * components are properly rendered.  If a child component is entirely
     * clipped by the current clipping setting in g, update() will not be
     * forwarded to that child.
     *
     * @param g the specified Graphics window
     * @see   Component#update(Graphics)
     */
    public void update(Graphics g) {
        if (isShowing()) {
            if (! (peer instanceof LightweightPeer)) {
                g.clearRect(0, 0, width, height);
            }
            paint(g);
        }
    }

    /** 
     * Prints the container. This forwards the print to any lightweight
     * components that are children of this container. If this method is
     * reimplemented, super.print(g) should be called so that lightweight
     * components are properly rendered. If a child component is entirely
     * clipped by the current clipping setting in g, print() will not be
     * forwarded to that child.
     *
     * @param g the specified Graphics window
     * @see   Component#update(Graphics)
     */
    public void print(Graphics g) {
        if (isShowing()) {
	    Thread t = Thread.currentThread();
	    try {
	        synchronized (this) {
		    if (printingThreads == null) {
		        printingThreads = new HashSet();
		    }
		    printingThreads.add(t);
		    printing = true;
		}
		super.print(g);  // By default, Component.print() calls paint()
	    } finally {
	        synchronized (this) {
		    printingThreads.remove(t);
		    printing = !printingThreads.isEmpty();
		}
	    }

	    GraphicsCallback.PrintCallback.getInstance().
	        runComponents(component, g, GraphicsCallback.LIGHTWEIGHTS);
	}
    }

    /** 
     * Paints each of the components in this container.
     * @param     g   the graphics context.
     * @see       Component#paint
     * @see       Component#paintAll
     */
    public void paintComponents(Graphics g) {
        if (isShowing()) {
	    GraphicsCallback.PaintAllCallback.getInstance().
	        runComponents(component, g, GraphicsCallback.TWO_PASSES);
	}
    }

    /**
     * Simulates the peer callbacks into java.awt for printing of
     * lightweight Containers.
     * @param     g   the graphics context to use for printing.
     * @see       Component#printAll
     * @see       #printComponents
     */
    void lightweightPaint(Graphics g) {
        super.lightweightPaint(g);
        paintHeavyweightComponents(g);
    }

    /**
     * Prints all the heavyweight subcomponents.
     */
    void paintHeavyweightComponents(Graphics g) {
        if (isShowing()) {
	    GraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().
	        runComponents(component, g, GraphicsCallback.LIGHTWEIGHTS |
                                            GraphicsCallback.HEAVYWEIGHTS);
	}
    }

    /** 
     * Prints each of the components in this container. 
     * @param     g   the graphics context.
     * @see       Component#print
     * @see       Component#printAll
     */
    public void printComponents(Graphics g) {
        if (isShowing()) {
	    GraphicsCallback.PrintAllCallback.getInstance().
	        runComponents(component, g, GraphicsCallback.TWO_PASSES);
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
        if (isShowing()) {
	    GraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().
	        runComponents(component, g, GraphicsCallback.LIGHTWEIGHTS |
                                            GraphicsCallback.HEAVYWEIGHTS);
	}
    }

    /**
     * Adds the specified container listener to receive container events
     * from this container.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l the container listener
     *
     * @see #removeContainerListener
     * @see #getContainerListeners
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
     *
     * @see #addContainerListener
     * @see #getContainerListeners
     */ 
    public synchronized void removeContainerListener(ContainerListener l) {
	if (l == null) {
	    return;
	}
	containerListener = AWTEventMulticaster.remove(containerListener, l);
    }

    /**
     * Returns an array of all the container listeners
     * registered on this container.
     *
     * @return all of this container's <code>ContainerListener</code>s
     *         or an empty array if no container
     *         listeners are currently registered
     *
     * @see #addContainerListener
     * @see #removeContainerListener
     * @since 1.4
     */
    public synchronized ContainerListener[] getContainerListeners() {
        return (ContainerListener[]) (getListeners(ContainerListener.class));
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>Container</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>Container</code> <code>c</code>
     * for its container listeners with the following code:
     *
     * <pre>ContainerListener[] cls = (ContainerListener[])(c.getListeners(ContainerListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this container,
     *          or an empty array if no such listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getContainerListeners
     *
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	EventListener l = null; 
	if  (listenerType == ContainerListener.class) { 
	    l = containerListener;
	} else {
	    return super.getListeners(listenerType);
	}
	return AWTEventMulticaster.getListeners(l, listenerType);
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
     * Processes events on this container. If the event is a
     * <code>ContainerEvent</code>, it invokes the
     * <code>processContainerEvent</code> method, else it invokes 
     * its superclass's <code>processEvent</code>.
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
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
     * <ul>
     * <li>A ContainerListener object is registered via
     *     <code>addContainerListener</code>
     * <li>Container events are enabled via <code>enableEvents</code>
     * </ul>
     * <p>Note that if the event parameter is <code>null</code>
     * the behavior is unspecified and may result in an
     * exception.
     *
     * @param e the container event
     * @see Component#enableEvents
     */  
    protected void processContainerEvent(ContainerEvent e) {
        ContainerListener listener = containerListener;
        if (listener != null) {
            switch(e.getID()) {
              case ContainerEvent.COMPONENT_ADDED:
                listener.componentAdded(e);
                break;
              case ContainerEvent.COMPONENT_REMOVED:
                listener.componentRemoved(e);
                break;
            }
        }
    }

    /*
     * Dispatches an event to this component or one of its sub components.
     * Create ANCESTOR_RESIZED and ANCESTOR_MOVED events in response to
     * COMPONENT_RESIZED and COMPONENT_MOVED events. We have to do this
     * here instead of in processComponentEvent because ComponentEvents
     * may not be enabled for this Container.
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

	switch (e.getID()) {
	  case ComponentEvent.COMPONENT_RESIZED:
	    createChildHierarchyEvents(HierarchyEvent.ANCESTOR_RESIZED, 0,
                                       Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
	    break;
	  case ComponentEvent.COMPONENT_MOVED:
	    createChildHierarchyEvents(HierarchyEvent.ANCESTOR_MOVED, 0,
                                       Toolkit.enabledOnToolkit(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
	    break;
	  default:
	    break;
	}
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
        return getMouseEventTarget(x, y, includeSelf, 
                                   MouseEventTargetFilter.FILTER, 
                                   !SEARCH_HEAVYWEIGHTS);
    }

    /**
     * Fetches the top-most (deepest) component to receive SunDropTargetEvents.
     */
    Component getDropTargetEventTarget(int x, int y, boolean includeSelf) {
        return getMouseEventTarget(x, y, includeSelf, 
                                   DropTargetEventTargetFilter.FILTER, 
                                   SEARCH_HEAVYWEIGHTS);
    }

    /**
     * A private version of getMouseEventTarget which has two additional 
     * controllable behaviors. This method searches for the top-most 
     * descendant of this container that contains the given coordinates   
     * and is accepted by the given filter. The search will be constrained to
     * lightweight descendants if the last argument is <code>false</code>.
     *
     * @param filter EventTargetFilter instance to determine whether the
     *        given component is a valid target for this event. 
     * @param searchHeavyweights if <code>false</false>, the method 
     *        will bypass heavyweight components during the search. 
     */
    private Component getMouseEventTarget(int x, int y, boolean includeSelf,
                                          EventTargetFilter filter,
                                          boolean searchHeavyweights) {
        Component comp = null;
        if (searchHeavyweights) {
            comp = getMouseEventTargetImpl(x, y, includeSelf, filter,
                                           SEARCH_HEAVYWEIGHTS,
                                           searchHeavyweights);
        }
        
        if (comp == null || comp == this) {
            comp = getMouseEventTargetImpl(x, y, includeSelf, filter,
                                           !SEARCH_HEAVYWEIGHTS,
                                           searchHeavyweights);            
        }

        return comp;
    }

    /**
     * A private version of getMouseEventTarget which has three additional 
     * controllable behaviors. This method searches for the top-most 
     * descendant of this container that contains the given coordinates   
     * and is accepted by the given filter. The search will be constrained to
     * descendants of only lightweight children or only heavyweight children 
     * of this container depending on searchHeavyweightChildren. The search will
     * be constrained to only lightweight descendants of the searched children
     * of this container if searchHeavyweightDescendants is <code>false</code>. 
     *
     * @param filter EventTargetFilter instance to determine whether the
     *        selected component is a valid target for this event. 
     * @param searchHeavyweightChildren if <code>true</false>, the method 
     *        will bypass immediate lightweight children during the search.
     *        If <code>false</code>, the methods will bypass immediate
     *        heavyweight children during the search. 
     * @param searchHeavyweightDescendants if <code>false</false>, the method 
     *        will bypass heavyweight descendants which are not immediate
     *        children during the search. If <code>true</code>, the method
     *        will traverse both lightweight and heavyweight descendants during
     *        the search.
     */
    private Component getMouseEventTargetImpl(int x, int y, boolean includeSelf,
                                         EventTargetFilter filter,
                                         boolean searchHeavyweightChildren,
                                         boolean searchHeavyweightDescendants) {
        int ncomponents = this.ncomponents;
        Component component[] = this.component;

        for (int i = 0 ; i < ncomponents ; i++) {
            Component comp = component[i];
            if (comp != null && comp.visible &&
                ((!searchHeavyweightChildren && 
                  comp.peer instanceof LightweightPeer) ||
                 (searchHeavyweightChildren &&
                  !(comp.peer instanceof LightweightPeer))) &&
                comp.contains(x - comp.x, y - comp.y)) {

                // found a component that intersects the point, see if there is 
                // a deeper possibility.
                if (comp instanceof Container) {
                    Container child = (Container) comp;
		    Component deeper = child.getMouseEventTarget(x - child.x,
                                                                 y - child.y,
                                                                 includeSelf,
                                                                 filter,
                                                                 searchHeavyweightDescendants);
                    if (deeper != null) {
                        return deeper;
                    }
                } else {
                    if (filter.accept(comp)) {
                        // there isn't a deeper target, but this component is a
                        // target
                        return comp;
                    }
                }
            }
        }
	
        boolean isPeerOK;
        boolean	isMouseOverMe;
	
        isPeerOK = (peer instanceof LightweightPeer) || includeSelf;
        isMouseOverMe = contains(x,y);

        // didn't find a child target, return this component if it's a possible
        // target
        if (isMouseOverMe && isPeerOK && filter.accept(this)) {
            return this;
        }
        // no possible target
        return null;
    }

    static interface EventTargetFilter {
        boolean accept(final Component comp);
    }

    static class MouseEventTargetFilter implements EventTargetFilter {
        static final EventTargetFilter FILTER = new MouseEventTargetFilter();
        
        private MouseEventTargetFilter() {}

        public boolean accept(final Component comp) {
            return (comp.eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0
                || (comp.eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0
                || (comp.eventMask & AWTEvent.MOUSE_WHEEL_EVENT_MASK) != 0
                || comp.mouseListener != null
                || comp.mouseMotionListener != null 
                || comp.mouseWheelListener != null;
        }
    }

    static class DropTargetEventTargetFilter implements EventTargetFilter {
        static final EventTargetFilter FILTER = new DropTargetEventTargetFilter();
        
        private DropTargetEventTargetFilter() {}

        public boolean accept(final Component comp) {
            DropTarget dt = comp.getDropTarget();
            return dt != null && dt.isActive();
        }
    }

    /**
     * This is called by lightweight components that want the containing
     * windowed parent to enable some kind of events on their behalf.
     * This is needed for events that are normally only dispatched to 
     * windows to be accepted so that they can be forwarded downward to 
     * the lightweight component that has enabled them.
     */
    void proxyEnableEvents(long events) {
	if (peer instanceof LightweightPeer) {
	    // this container is lightweight.... continue sending it
	    // upward.
	    if (parent != null) {
		parent.proxyEnableEvents(events);
	    }
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
     * the given point via Component.contains(), except that Components
     * which have native peers take precedence over those which do not
     * (i.e., lightweight Components).
     *
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
	synchronized (getTreeLock()) {
	    // Two passes: see comment in sun.awt.SunGraphicsCallback
	    for (int i = 0 ; i < ncomponents ; i++) {
	        Component comp = component[i];
		if (comp != null &&
		    !(comp.peer instanceof LightweightPeer)) {
		    if (comp.contains(x - comp.x, y - comp.y)) {
		        return comp;
		    }
		}
	    }
	    for (int i = 0 ; i < ncomponents ; i++) {
	        Component comp = component[i];
		if (comp != null &&
		    comp.peer instanceof LightweightPeer) {
		    if (comp.contains(x - comp.x, y - comp.y)) {
		        return comp;
		    }
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
     * @see        Component#contains 
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
     * @see #getComponentAt
     * @since 1.2
     */
    public Component findComponentAt(int x, int y) {
	synchronized (getTreeLock()) {
            return findComponentAt(x, y, true);
	}
    }

    /**
     * Private version of findComponentAt which has a controllable
     * behavior. Setting 'ignoreEnabled' to 'false' bypasses disabled
     * Components during the search. This behavior is used by the
     * lightweight cursor support in sun.awt.GlobalCursorManager.
     * The cursor code calls this function directly via native code.
     *
     * The addition of this feature is temporary, pending the
     * adoption of new, public API which exports this feature.
     */
    final Component findComponentAt(int x, int y, boolean ignoreEnabled)
    {
        if (!(contains(x, y) && visible && (ignoreEnabled || enabled))) {
	    return null;
	}
	int ncomponents = this.ncomponents;
	Component component[] = this.component;

	// Two passes: see comment in sun.awt.SunGraphicsCallback
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
            if (comp != null &&
		!(comp.peer instanceof LightweightPeer)) {
		if (comp instanceof Container) {
		    comp = ((Container)comp).findComponentAt(x - comp.x,
							     y - comp.y,
                                                             ignoreEnabled);
		} else {
		    comp = comp.locate(x - comp.x, y - comp.y);
		}
		if (comp != null && comp.visible &&
		    (ignoreEnabled || comp.enabled))
		{
		    return comp;
		}
	    }
	}
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
            if (comp != null &&
		comp.peer instanceof LightweightPeer) {
		if (comp instanceof Container) {
		    comp = ((Container)comp).findComponentAt(x - comp.x,
							     y - comp.y,
                                                             ignoreEnabled);
		} else {
		    comp = comp.locate(x - comp.x, y - comp.y);
		}
		if (comp != null && comp.visible &&
		    (ignoreEnabled || comp.enabled))
		{
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
     * @see #getComponentAt
     * @since 1.2
     */
    public Component findComponentAt(Point p) {
        return findComponentAt(p.x, p.y);
    }

    /** 
     * Makes this Container displayable by connecting it to
     * a native screen resource.  Making a container displayable will
     * cause all of its children to be made displayable.
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
	    if (! (peer instanceof LightweightPeer)) {
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
     * to its native screen resource.  Making a container undisplayable
     * will cause all of its children to be made undisplayable. 
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
	    if ( dispatcher != null ) {
		dispatcher.dispose();
        dispatcher = null;
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
     * Returns a string representing the state of this <code>Container</code>.
     * This method is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between 
     * implementations. The returned string may be empty but may not be 
     * <code>null</code>.
     *
     * @return    the parameter string of this container
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
     * @see      Component#list(java.io.PrintStream, int)
     * @since    JDK1.0
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

    /**
     * Sets the focus traversal keys for a given traversal operation for this
     * Container.
     * <p>
     * The default values for a Container's focus traversal keys are
     * implementation-dependent. Sun recommends that all implementations for a
     * particular native platform use the same default values. The
     * recommendations for Windows and Unix are listed below. These
     * recommendations are used in the Sun AWT implementations.
     *
     * <table border=1 summary="Recommended default values for a Container's focus traversal keys">
     * <tr>
     *    <th>Identifier</th>
     *    <th>Meaning</th>
     *    <th>Default</th>
     * </tr>
     * <tr>
     *    <td>KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS</td>
     *    <td>Normal forward keyboard traversal</td>
     *    <td>TAB on KEY_PRESSED, CTRL-TAB on KEY_PRESSED</td>
     * </tr>
     * <tr>
     *    <td>KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS</td>
     *    <td>Normal reverse keyboard traversal</td>
     *    <td>SHIFT-TAB on KEY_PRESSED, CTRL-SHIFT-TAB on KEY_PRESSED</td>
     * </tr>
     * <tr>
     *    <td>KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS</td>
     *    <td>Go up one focus traversal cycle</td>
     *    <td>none</td>
     * </tr>
     * <tr>
     *    <td>KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS<td>
     *    <td>Go down one focus traversal cycle</td>
     *    <td>none</td>
     * </tr>
     * </table>
     *
     * To disable a traversal key, use an empty Set; Collections.EMPTY_SET is
     * recommended.
     * <p>
     * Using the AWTKeyStroke API, client code can specify on which of two
     * specific KeyEvents, KEY_PRESSED or KEY_RELEASED, the focus traversal
     * operation will occur. Regardless of which KeyEvent is specified,
     * however, all KeyEvents related to the focus traversal key, including the
     * associated KEY_TYPED event, will be consumed, and will not be dispatched
     * to any Container. It is a runtime error to specify a KEY_TYPED event as
     * mapping to a focus traversal operation, or to map the same event to
     * multiple default focus traversal operations.
     * <p>
     * If a value of null is specified for the Set, this Container inherits the
     * Set from its parent. If all ancestors of this Container have null
     * specified for the Set, then the current KeyboardFocusManager's default
     * Set is used.
     *
     * @param id one of KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *        KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @param keystrokes the Set of AWTKeyStroke for the specified operation
     * @see #getFocusTraversalKeys
     * @see KeyboardFocusManager#FORWARD_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#BACKWARD_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#UP_CYCLE_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#DOWN_CYCLE_TRAVERSAL_KEYS
     * @throws IllegalArgumentException if id is not one of
     *         KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *         KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, or if keystrokes
     *         contains null, or if any Object in keystrokes is not an
     *         AWTKeyStroke, or if any keystroke represents a KEY_TYPED event,
     *         or if any keystroke already maps to another focus traversal
     *         operation for this Container
     * @since 1.4
     * @beaninfo
     *       bound: true
     */
    public void setFocusTraversalKeys(int id, Set keystrokes) {
        if (id < 0 || id >= KeyboardFocusManager.TRAVERSAL_KEY_LENGTH) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
          
        // Don't call super.setFocusTraversalKey. The Component parameter check
	// does not allow DOWN_CYCLE_TRAVERSAL_KEYS, but we do.
        setFocusTraversalKeys_NoIDCheck(id, keystrokes);
    }

    /**
     * Returns the Set of focus traversal keys for a given traversal operation
     * for this Container. (See
     * <code>setFocusTraversalKeys</code> for a full description of each key.)
     * <p>
     * If a Set of traversal keys has not been explicitly defined for this
     * Container, then this Container's parent's Set is returned. If no Set
     * has been explicitly defined for any of this Container's ancestors, then
     * the current KeyboardFocusManager's default Set is returned.
     *
     * @param id one of KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *        KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @return the Set of AWTKeyStrokes for the specified operation. The Set
     *         will be unmodifiable, and may be empty. null will never be
     *         returned.
     * @see #setFocusTraversalKeys
     * @see KeyboardFocusManager#FORWARD_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#BACKWARD_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#UP_CYCLE_TRAVERSAL_KEYS
     * @see KeyboardFocusManager#DOWN_CYCLE_TRAVERSAL_KEYS
     * @throws IllegalArgumentException if id is not one of
     *         KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *         KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *         KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @since 1.4
     */
    public Set getFocusTraversalKeys(int id) {
        if (id < 0 || id >= KeyboardFocusManager.TRAVERSAL_KEY_LENGTH) {
	    throw new IllegalArgumentException("invalid focus traversal key identifier");
	}
 
	// Don't call super.getFocusTraversalKey. The Component parameter check
	// does not allow DOWN_CYCLE_TRAVERSAL_KEY, but we do.
	return getFocusTraversalKeys_NoIDCheck(id);
    }

    /**
     * Returns whether the Set of focus traversal keys for the given focus
     * traversal operation has been explicitly defined for this Container. If
     * this method returns <code>false</code>, this Container is inheriting the
     * Set from an ancestor, or from the current KeyboardFocusManager.
     *
     * @param id one of KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *        KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @return <code>true</code> if the the Set of focus traversal keys for the
     *         given focus traversal operation has been explicitly defined for
     *         this Component; <code>false</code> otherwise.
     * @throws IllegalArgumentException if id is not one of
     *         KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
     *        KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
     *        KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
     * @since 1.4
     */
    public boolean areFocusTraversalKeysSet(int id) {
        if (id < 0 || id >= KeyboardFocusManager.TRAVERSAL_KEY_LENGTH) {
	    throw new IllegalArgumentException("invalid focus traversal key identifier");
	}
 
	return (focusTraversalKeys != null && focusTraversalKeys[id] != null);
    }

    /**
     * Returns whether the specified Container is the focus cycle root of this
     * Container's focus traversal cycle. Each focus traversal cycle has only
     * a single focus cycle root and each Container which is not a focus cycle
     * root belongs to only a single focus traversal cycle. Containers which
     * are focus cycle roots belong to two cycles: one rooted at the Container
     * itself, and one rooted at the Container's nearest focus-cycle-root
     * ancestor. This method will return <code>true</code> for both such
     * Containers in this case.
     *
     * @param container the Container to be tested
     * @return <code>true</code> if the specified Container is a focus-cycle-
     *         root of this Container; <code>false</code> otherwise
     * @see #isFocusCycleRoot()
     * @since 1.4
     */
    public boolean isFocusCycleRoot(Container container) {
        if (isFocusCycleRoot() && container == this) {
	    return true;
	} else {
	    return super.isFocusCycleRoot(container);
	}
    }
 
    private Container findTraversalRoot() {
	// I potentially have two roots, myself and my root parent
	// If I am the current root, then use me
	// If none of my parents are roots, then use me
	// If my root parent is the current root, then use my root parent
	// If neither I nor my root parent is the current root, then
	// use my root parent (a guess)
 
	Container currentFocusCycleRoot = KeyboardFocusManager.
	    getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
	Container root;
	
	if (currentFocusCycleRoot == this) {
	    root = this;
	} else {
	    root = getFocusCycleRootAncestor();
	    if (root == null) {
		root = this;
	    }
	}

	if (root != currentFocusCycleRoot) {
	    KeyboardFocusManager.getCurrentKeyboardFocusManager().
		setGlobalCurrentFocusCycleRoot(root);
	}
	return root;
    }

    final boolean containsFocus() {
        synchronized (getTreeLock()) {
            Component comp = KeyboardFocusManager.
                getCurrentKeyboardFocusManager().getFocusOwner();
            while (comp != null && !(comp instanceof Window) && comp != this) 
            {
                comp = (Component) comp.getParent();
            }
            return (comp == this);
        }
    }

    /**
     * Check if this component is the child of this container or its children.
     * Note: this function acquires treeLock
     * Note: this function traverses children tree only in one Window.
     * @param comp a component in test, must not be null
     */
    boolean isParentOf(Component comp) {
        synchronized(getTreeLock()) {
            while (comp != null && comp != this && !(comp instanceof Window)) {
                comp = comp.getParent();
            }
            return (comp == this);
        }
    }

    void clearMostRecentFocusOwnerOnHide() {
        Component comp = null;
        Container window = this;

        synchronized (getTreeLock()) {
            while (window != null && !(window instanceof Window)) {
                window = window.getParent();
            }
            if (window != null) {
                comp = KeyboardFocusManager.
                    getMostRecentFocusOwner((Window)window);
                while ((comp != null) && (comp != this) && !(comp instanceof Window)) {
                    comp = comp.getParent();
                }
            }            
        }

        if (comp == this) {
            KeyboardFocusManager.setMostRecentFocusOwner((Window)window, null);
        }

        if (window != null) {
            Window myWindow = (Window)window;
            synchronized(getTreeLock()) {
                // This synchronized should always be the second in a pair (tree lock, KeyboardFocusManager.class)
                synchronized(KeyboardFocusManager.class) {
                    Component storedComp = myWindow.getTemporaryLostComponent();
                    if (isParentOf(storedComp) || storedComp == this) {
                        myWindow.setTemporaryLostComponent(null);
                    }
                }
            }
        }
    }

    void clearCurrentFocusCycleRootOnHide() {
        KeyboardFocusManager kfm = 
            KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Container cont = kfm.getCurrentFocusCycleRoot();

        synchronized (getTreeLock()) {
            while (this != cont && !(cont instanceof Window) && (cont != null)) {
                cont = cont.getParent();
            }
        }

        if (cont == this) {
            kfm.setGlobalCurrentFocusCycleRoot(null);
        }
    }

    boolean nextFocusHelper() {
        if (isFocusCycleRoot()) {
            Container root = findTraversalRoot();
            Component comp = this;
            Container anc;
            while (root != null && 
                   (anc = root.getFocusCycleRootAncestor()) != null &&
                   !(root.isShowing() && 
                     root.isFocusable() && 
                     root.isEnabled())) 
            {
                comp = root;
                root = anc;
            }
            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component toFocus = policy.getComponentAfter(root, comp);
                if (toFocus == null) {
                    toFocus = policy.getDefaultComponent(root);
                }
                if (toFocus != null) {
                    return toFocus.requestFocus(false);
                }
            }
            return false;
        } else {
            // I only have one root, so the general case will suffice
            return super.nextFocusHelper();
        }
    }
 
    public void transferFocusBackward() {
        if (isFocusCycleRoot()) {
            Container root = findTraversalRoot();
            Component comp = this;
            while (root != null && 
                   !(root.isShowing() && 
                     root.isFocusable() && 
                     root.isEnabled())) 
            {
                comp = root;
                root = comp.getFocusCycleRootAncestor();
            }
            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component toFocus = policy.getComponentBefore(root, comp);
                if (toFocus == null) {
                    toFocus = policy.getDefaultComponent(root);
                }
                if (toFocus != null) {
                    toFocus.requestFocus();
                }
            }
        } else {
            // I only have one root, so the general case will suffice
            super.transferFocusBackward();
        }
    }

    /**
     * Sets the focus traversal policy that will manage keyboard traversal of
     * this Container's children, if this Container is a focus cycle root. If
     * the argument is null, this Container inherits its policy from its focus-
     * cycle-root ancestor. If the argument is non-null, this policy will be 
     * inherited by all focus-cycle-root children that have no keyboard-
     * traversal policy of their own (as will, recursively, their focus-cycle-
     * root children).
     * <p>
     * If this Container is not a focus cycle root, the policy will be
     * remembered, but will not be used or inherited by this or any other
     * Containers until this Container is made a focus cycle root.
     *
     * @param policy the new focus traversal policy for this Container
     * @see #getFocusTraversalPolicy
     * @see #setFocusCycleRoot
     * @see #isFocusCycleRoot
     * @since 1.4
     * @beaninfo
     *       bound: true
     */
    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
        FocusTraversalPolicy oldPolicy;
	synchronized (this) {
	    oldPolicy = this.focusTraversalPolicy;
	    this.focusTraversalPolicy = policy;
	}
	firePropertyChange("focusTraversalPolicy", oldPolicy, policy);
    }

    /**
     * Returns the focus traversal policy that will manage keyboard traversal
     * of this Container's children, or null if this Container is not a focus
     * cycle root. If no traversal policy has been explicitly set for this
     * Container, then this Container's focus-cycle-root ancestor's policy is
     * returned. 
     *
     * @return this Container's focus traversal policy, or null if this
     *         Container is not a focus cycle root.
     * @see #setFocusTraversalPolicy
     * @see #setFocusCycleRoot
     * @see #isFocusCycleRoot
     * @since 1.4
     */
    public FocusTraversalPolicy getFocusTraversalPolicy() {
        if (!isFocusCycleRoot()) {
	    return null;
	}
 
	FocusTraversalPolicy policy = this.focusTraversalPolicy;
	if (policy != null) {
	    return policy;
	}
 
	Container rootAncestor = getFocusCycleRootAncestor();
	if (rootAncestor != null) {
	    return rootAncestor.getFocusTraversalPolicy();
	} else {
	    return KeyboardFocusManager.getCurrentKeyboardFocusManager().
	        getDefaultFocusTraversalPolicy();
	}
    }

    /**
     * Returns whether the focus traversal policy has been explicitly set for
     * this Container. If this method returns <code>false</code>, this
     * Container will inherit its focus traversal policy from an ancestor.
     *
     * @return <code>true</code> if the focus traversal policy has been
     *         explicitly set for this Container; <code>false</code> otherwise.
     * @since 1.4
     */
    public boolean isFocusTraversalPolicySet() {
        return (focusTraversalPolicy != null);
    }

    /**
     * Sets whether this Container is the root of a focus traversal cycle. Once
     * focus enters a traversal cycle, typically it cannot leave it via focus
     * traversal unless one of the up- or down-cycle keys is pressed. Normal
     * traversal is limited to this Container, and all of this Container's
     * descendants that are not descendants of inferior focus cycle roots. Note
     * that a FocusTraversalPolicy may bend these restrictions, however. For
     * example, ContainerOrderFocusTraversalPolicy supports implicit down-cycle
     * traversal.
     *
     * @param focusCycleRoot indicates whether this Container is the root of a
     *        focus traversal cycle
     * @see #isFocusCycleRoot()
     * @see #setFocusTraversalPolicy
     * @see #getFocusTraversalPolicy
     * @see ContainerOrderFocusTraversalPolicy
     * @since 1.4
     * @beaninfo
     *       bound: true
     */
    public void setFocusCycleRoot(boolean focusCycleRoot) {
        boolean oldFocusCycleRoot;
	synchronized (this) {
	    oldFocusCycleRoot = this.focusCycleRoot;
	    this.focusCycleRoot = focusCycleRoot;
	}
	firePropertyChange("focusCycleRoot", oldFocusCycleRoot,
			   focusCycleRoot);
    }

    /**
     * Returns whether this Container is the root of a focus traversal cycle.
     * Once focus enters a traversal cycle, typically it cannot leave it via
     * focus traversal unless one of the up- or down-cycle keys is pressed.
     * Normal traversal is limited to this Container, and all of this
     * Container's descendants that are not descendants of inferior focus
     * cycle roots. Note that a FocusTraversalPolicy may bend these
     * restrictions, however. For example, ContainerOrderFocusTraversalPolicy
     * supports implicit down-cycle traversal.
     *
     * @return whether this Container is the root of a focus traversal cycle
     * @see #setFocusCycleRoot
     * @see #setFocusTraversalPolicy
     * @see #getFocusTraversalPolicy
     * @see ContainerOrderFocusTraversalPolicy
     * @since 1.4
     */
    public boolean isFocusCycleRoot() {
        return focusCycleRoot;
    }

    /**
     * Transfers the focus down one focus traversal cycle. If this Container is
     * a focus cycle root, then the focus owner is set to this Container's
     * default Component to focus, and the current focus cycle root is set to
     * this Container. If this Container is not a focus cycle root, then no
     * focus traversal operation occurs.
     *
     * @see       Component#requestFocus()
     * @see       #isFocusCycleRoot
     * @see       #setFocusCycleRoot
     * @since     1.4
     */
    public void transferFocusDownCycle() {
        if (isFocusCycleRoot()) {
	    KeyboardFocusManager.getCurrentKeyboardFocusManager().
	        setGlobalCurrentFocusCycleRoot(this);
	    Component toFocus = getFocusTraversalPolicy().
	        getDefaultComponent(this);
	    if (toFocus != null) {
	        toFocus.requestFocus();
	    }
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

    boolean postsOldMouseEvents() {
        return true;
    }

    /**
     * Sets the <code>ComponentOrientation</code> property of this container
     * and all components contained within it.
     *
     * @param o the new component orientation of this container and
     *        the components contained within it.
     * @exception NullPointerException if <code>orientation</code> is null.
     * @see Component#setComponentOrientation
     * @see Component#getComponentOrientation
     * @since 1.4
     */
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        
        for (int i = 0 ; i < ncomponents ; ++i) {
             component[i].applyComponentOrientation(o);
        }
    }
    
    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     *    <li>this Container's font ("font")</li>
     *    <li>this Container's background color ("background")</li>
     *    <li>this Container's foreground color ("foreground")</li>
     *    <li>this Container's focusability ("focusable")</li>
     *    <li>this Container's focus traversal keys enabled state
     *        ("focusTraversalKeysEnabled")</li>
     *    <li>this Container's Set of FORWARD_TRAVERSAL_KEYS
     *        ("forwardFocusTraversalKeys")</li>
     *    <li>this Container's Set of BACKWARD_TRAVERSAL_KEYS
     *        ("backwardFocusTraversalKeys")</li>
     *    <li>this Container's Set of UP_CYCLE_TRAVERSAL_KEYS
     *        ("upCycleFocusTraversalKeys")</li>
     *    <li>this Container's Set of DOWN_CYCLE_TRAVERSAL_KEYS
     *        ("downCycleFocusTraversalKeys")</li>
     *    <li>this Container's focus traversal policy ("focusTraversalPolicy")
     *        </li>
     *    <li>this Container's focus-cycle-root state ("focusCycleRoot")</li>
     * </ul>
     * Note that if this Container is inheriting a bound property, then no
     * event will be fired in response to a change in the inherited property.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param    listener  the PropertyChangeListener to be added
     *
     * @see Component#removePropertyChangeListener
     * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	super.addPropertyChangeListener(listener);
    }
  
    /**
     * Adds a PropertyChangeListener to the listener list for a specific
     * property. The specified property may be user-defined, or one of the
     * following defaults:
     * <ul>
     *    <li>this Container's font ("font")</li>
     *    <li>this Container's background color ("background")</li>
     *    <li>this Container's foreground color ("foreground")</li>
     *    <li>this Container's focusability ("focusable")</li>
     *    <li>this Container's focus traversal keys enabled state
     *        ("focusTraversalKeysEnabled")</li>
     *    <li>this Container's Set of FORWARD_TRAVERSAL_KEYS
     *        ("forwardFocusTraversalKeys")</li>
     *    <li>this Container's Set of BACKWARD_TRAVERSAL_KEYS
     *        ("backwardFocusTraversalKeys")</li>
     *    <li>this Container's Set of UP_CYCLE_TRAVERSAL_KEYS
     *        ("upCycleFocusTraversalKeys")</li>
     *    <li>this Container's Set of DOWN_CYCLE_TRAVERSAL_KEYS
     *        ("downCycleFocusTraversalKeys")</li>
     *    <li>this Container's focus traversal policy ("focusTraversalPolicy")
     *        </li>
     *    <li>this Container's focus-cycle-root state ("focusCycleRoot")</li>
     * </ul>
     * Note that if this Container is inheriting a bound property, then no
     * event will be fired in response to a change in the inherited property.
     * <p>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param propertyName one of the property names listed above
     * @param listener the PropertyChangeListener to be added
     *
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @see Component#removePropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName,
					  PropertyChangeListener listener) {
	super.addPropertyChangeListener(propertyName, listener);
    }
  
    // Serialization support. A Container is responsible for restoring the
    // parent fields of its component children. 
   
    /**
     * Container Serial Data Version.
     * @serial
     */
    private int containerSerializedDataVersion = 1;
  
    /**
     * Serializes this <code>Container</code> to the specified
     * <code>ObjectOutputStream</code>.
     * <ul>
     *    <li>Writes default serializable fields to the stream.</li>
     *    <li>Writes a list of serializable ContainerListener(s) as optional
     *        data. The non-serializable ContainerListner(s) are detected and
     *        no attempt is made to serialize them.</li>
     *    <li>Write this Container's FocusTraversalPolicy if and only if it
     *        is Serializable; otherwise, <code>null</code> is written.</li>
     * </ul>
     *
     * @param s the <code>ObjectOutputStream</code> to write
     * @serialData <code>null</code> terminated sequence of 0 or more pairs;
     *   the pair consists of a <code>String</code> and <code>Object</code>;
     *   the <code>String</code> indicates the type of object and
     *   is one of the following:
     *   <code>containerListenerK</code> indicating an
     *     <code>ContainerListener</code> object;
     *   the <code>Container</code>'s <code>FocusTraversalPolicy</code>,
     *     or <code>null</code>
     *
     * @see AWTEventMulticaster#save(java.io.ObjectOutputStream, java.lang.String, java.util.EventListener)
     * @see Container#containerListenerK
     * @see #readObject(ObjectInputStream)
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
	
	AWTEventMulticaster.save(s, containerListenerK, containerListener);
	s.writeObject(null);
	
	if (focusTraversalPolicy instanceof java.io.Serializable) {
	    s.writeObject(focusTraversalPolicy);
	} else {
	    s.writeObject(null);
	}
    }
   
    /**
     * Deserializes this <code>Container</code> from the specified
     * <code>ObjectInputStream</code>.
     * <ul>
     *    <li>Reads default serializable fields from the stream.</li>
     *    <li>Reads a list of serializable ContainerListener(s) as optional
     *        data. If the list is null, no Listeners are installed.</li>
     *    <li>Reads this Container's FocusTraversalPolicy, which may be null,
     *        as optional data.</li>
     * </ul>
     *
     * @param s the <code>ObjectInputStream</code> to read
     * @serial
     * @see #addContainerListener
     * @see #writeObject(ObjectOutputStream)
     */
    private void readObject(ObjectInputStream s)
	throws ClassNotFoundException, IOException
    {
	s.defaultReadObject();
  
	Component component[] = this.component;
	for(int i = 0; i < ncomponents; i++) {
	    component[i].parent = this;
	    adjustListeningChildren(AWTEvent.HIERARCHY_EVENT_MASK, 
	        component[i].numListening(AWTEvent.HIERARCHY_EVENT_MASK));
	    adjustListeningChildren(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK,
                component[i].numListening(
                    AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK));
            adjustDescendants(component[i].countHierarchyMembers());
	}
   
	Object keyOrNull;
	while(null != (keyOrNull = s.readObject())) {
	    String key = ((String)keyOrNull).intern();
	    
	    if (containerListenerK == key) {
		addContainerListener((ContainerListener)(s.readObject()));
	    } else {
		// skip value for unrecognized key
		s.readObject();
	    }
	}
  
	try {
	    Object policy = s.readObject();
	    if (policy instanceof FocusTraversalPolicy) {
		focusTraversalPolicy = (FocusTraversalPolicy)policy;
	    }
	} catch (java.io.OptionalDataException e) {
	    // JDK 1.1/1.2/1.3 instances will not have this optional data.
	    // e.eof will be true to indicate that there is no more data
	    // available for this object. If e.eof is not true, throw the
	    // exception as it might have been caused by reasons unrelated to 
	    // focusTraversalPolicy.
  
	    if (!e.eof) {
		throw e;
	    }
	}
    }

    /*
     * --- Accessibility Support ---
     */

    /**
     * Inner class of Container used to provide default support for
     * accessibility.  This class is not meant to be used directly by
     * application developers, but is instead meant only to be
     * subclassed by container developers.
     * <p>
     * The class used to obtain the accessible role for this object,
     * as well as implementing many of the methods in the
     * AccessibleContainer interface.
     */
    protected class AccessibleAWTContainer extends AccessibleAWTComponent {

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement <code>Accessible</code>,
         * then this method should return the number of children of this object.
         *
         * @return the number of accessible children in the object
         */
        public int getAccessibleChildrenCount() {
	    return Container.this.getAccessibleChildrenCount();
        }

        /**
         * Returns the nth <code>Accessible</code> child of the object.
         *
         * @param i zero-based index of child
         * @return the nth <code>Accessible</code> child of the object
         */
        public Accessible getAccessibleChild(int i) {
            return Container.this.getAccessibleChild(i);
        }

        /**
         * Returns the <code>Accessible</code> child, if one exists,
         * contained at the local coordinate <code>Point</code>.
         *
         * @param p the point defining the top-left corner of the 
         *    <code>Accessible</code>, given in the coordinate space
         *    of the object's parent
         * @return the <code>Accessible</code>, if it exists,
         *    at the specified location; else <code>null</code>
         */
        public Accessible getAccessibleAt(Point p) {
            return Container.this.getAccessibleAt(p);
        }

	protected ContainerListener accessibleContainerHandler = null;

	/**
	 * Fire <code>PropertyChange</code> listener, if one is registered,
	 * when children are added or removed.
	 */
	protected class AccessibleContainerHandler 
	    implements ContainerListener {
	    public void componentAdded(ContainerEvent e) {
		Component c = e.getChild();
		if (c != null && c instanceof Accessible) {
		    AccessibleAWTContainer.this.firePropertyChange(
			AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, 
			null, ((Accessible) c).getAccessibleContext());
		}
	    }
	    public void componentRemoved(ContainerEvent e) {
		Component c = e.getChild();
		if (c != null && c instanceof Accessible) {
		    AccessibleAWTContainer.this.firePropertyChange(
			AccessibleContext.ACCESSIBLE_CHILD_PROPERTY, 
			((Accessible) c).getAccessibleContext(), null); 
		}
	    }
	}
    } // inner class AccessibleAWTContainer

    /**
     * Returns the <code>Accessible</code> child contained at the local
     * coordinate <code>Point</code>, if one exists.  Otherwise
     * returns <code>null</code>.
     *
     * @param p the point defining the top-left corner of the 
     *    <code>Accessible</code>, given in the coordinate space
     *    of the object's parent
     * @return the <code>Accessible</code> at the specified location,
     *    if it exists; otherwise <code>null</code>
     */
    Accessible getAccessibleAt(Point p) {
        synchronized (getTreeLock()) {
            if (this instanceof Accessible) {
                Accessible a = (Accessible)this;
                AccessibleContext ac = a.getAccessibleContext();
                if (ac != null) {
                    AccessibleComponent acmp;
                    Point location;
                    int nchildren = ac.getAccessibleChildrenCount();
                    for (int i=0; i < nchildren; i++) {
                        a = ac.getAccessibleChild(i);
                        if ((a != null)) {
                            ac = a.getAccessibleContext();
                            if (ac != null) {
                                acmp = ac.getAccessibleComponent();
                                if ((acmp != null) && (acmp.isShowing())) {
                                    location = acmp.getLocation();
                                    Point np = new Point(p.x-location.x,
                                                         p.y-location.y);
                                    if (acmp.contains(np)){
                                        return a;
                                    }
                                }
                            }
                        }
                    }
                }
                return (Accessible)this;
            } else {
                Component ret = this;
                if (!this.contains(p.x,p.y)) {
                    ret = null;
                } else {
                    int ncomponents = this.getComponentCount();
                    for (int i=0; i < ncomponents; i++) {
                        Component comp = this.getComponent(i);
                        if ((comp != null) && comp.isShowing()) {
                            Point location = comp.getLocation();
                            if (comp.contains(p.x-location.x,p.y-location.y)) {
                                ret = comp;
                            }
                        }
                    }
                }
                if (ret instanceof Accessible) {
                    return (Accessible) ret;
                }
            }
            return null;
        }
    }

    /**
     * Returns the number of accessible children in the object.  If all
     * of the children of this object implement <code>Accessible</code>,
     * then this method should return the number of children of this object.
     *
     * @return the number of accessible children in the object
     */
    int getAccessibleChildrenCount() {
        synchronized (getTreeLock()) {
            int count = 0;
            Component[] children = this.getComponents();
            for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof Accessible) {
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * Returns the nth <code>Accessible</code> child of the object.
     *
     * @param i zero-based index of child
     * @return the nth <code>Accessible</code> child of the object
     */
    Accessible getAccessibleChild(int i) {
        synchronized (getTreeLock()) {
            Component[] children = this.getComponents();
            int count = 0;
            for (int j = 0; j < children.length; j++) {
                if (children[j] instanceof Accessible) {
                    if (count == i) {
                        return (Accessible) children[j];
                    } else {
                        count++;
                    }
                }
            }
            return null;
        }
    }

}


/**
 * Class to manage the dispatching of MouseEvents to the lightweight descendants
 * and SunDropTargetEvents to both lightweight and heavyweight descendants
 * contained by a native container.
 *
 * NOTE: the class name is not appropriate anymore, but we cannot change it
 * because we must keep serialization compatibility.
 * 
 * @author Timothy Prinzing
 */
class LightweightDispatcher implements java.io.Serializable, AWTEventListener {

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 5184291520170872969L;
    /*
     * Our own mouse event for when we're dragged over from another hw
     * container
     */
    private static final int  LWD_MOUSE_DRAGGED_OVER = 1500;

    private static final DebugHelper dbg = DebugHelper.create(LightweightDispatcher.class);

    LightweightDispatcher(Container nativeContainer) {
	this.nativeContainer = nativeContainer;
	mouseEventTarget = null;
	eventMask = 0;
    }

    /*
     * Clean up any resources allocated when dispatcher was created;
     * should be called from Container.removeNotify
     */
    void dispose() {
	//System.out.println("Disposing lw dispatcher");
	stopListeningForOtherDrags();
    }

    /**
     * Enables events to subcomponents.
     */
    void enableEvents(long events) {
	eventMask |= events;
    }

    /**
     * Dispatches an event to a sub-component if necessary, and
     * returns whether or not the event was forwarded to a 
     * sub-component.
     *
     * @param e the event
     */
    boolean dispatchEvent(AWTEvent e) {
	boolean ret = false;

        /*
         * Fix for BugTraq Id 4389284.
         * Dispatch SunDropTargetEvents regardless of eventMask value.
         * Do not update cursor on dispatching SunDropTargetEvents.
         */
        if (e instanceof SunDropTargetEvent) {

            SunDropTargetEvent sdde = (SunDropTargetEvent) e;
            ret = processDropTargetEvent(sdde);

        } else {
            if (e instanceof MouseEvent && (eventMask & MOUSE_MASK) != 0) {
                MouseEvent me = (MouseEvent) e;
                ret = processMouseEvent(me);
            }

            if (e.getID() == MouseEvent.MOUSE_MOVED) {
                nativeContainer.updateCursorImmediately();
            }
        }

	return ret;
    }

    /* This method effectively returns whether or not a mouse button was down
     * just BEFORE the event happened.  A better method name might be
     * wasAMouseButtonDownBeforeThisEvent().
     */
    private boolean isMouseGrab(MouseEvent e) {
        int modifiers = e.getModifiersEx();
        
        if(e.getID() == MouseEvent.MOUSE_PRESSED 
            || e.getID() == MouseEvent.MOUSE_RELEASED) 
        {
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
		modifiers ^= InputEvent.BUTTON1_DOWN_MASK;
                break;
            case MouseEvent.BUTTON2:
		modifiers ^= InputEvent.BUTTON2_DOWN_MASK;
                break;
            case MouseEvent.BUTTON3:
		modifiers ^= InputEvent.BUTTON3_DOWN_MASK;
                break;
            }
        }
        /* modifiers now as just before event */ 
        return ((modifiers & (InputEvent.BUTTON1_DOWN_MASK
                              | InputEvent.BUTTON2_DOWN_MASK
                              | InputEvent.BUTTON3_DOWN_MASK)) != 0);
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
	Component mouseOver =	// sensitive to mouse events
            nativeContainer.getMouseEventTarget(e.getX(), e.getY(), 
                                                Container.INCLUDE_SELF);

	trackMouseEnterExit(mouseOver, e);

    // 4508327 : MOUSE_CLICKED should only go to the recipient of 
    // the accompanying MOUSE_PRESSED, so don't reset mouseEventTarget on a
    // MOUSE_CLICKED.
    if (!isMouseGrab(e) && id != MouseEvent.MOUSE_CLICKED) {
	    mouseEventTarget = (mouseOver != nativeContainer) ? mouseOver: null;
	} 

	if (mouseEventTarget != null) {
	    switch (id) {
	    case MouseEvent.MOUSE_ENTERED:
	    case MouseEvent.MOUSE_EXITED:
		break;
	    case MouseEvent.MOUSE_PRESSED:
		retargetMouseEvent(mouseEventTarget, id, e);
		break;
        case MouseEvent.MOUSE_RELEASED:
            retargetMouseEvent(mouseEventTarget, id, e);
        break;
        case MouseEvent.MOUSE_CLICKED:
        // 4508327: MOUSE_CLICKED should never be dispatched to a Component
        // other than that which received the MOUSE_PRESSED event.  If the
        // mouse is now over a different Component, don't dispatch the event.
        // The previous fix for a similar problem was associated with bug
        // 4155217.
        if (mouseOver == mouseEventTarget) {
            retargetMouseEvent(mouseOver, id, e);
        }
        break;
	    case MouseEvent.MOUSE_MOVED:
		retargetMouseEvent(mouseEventTarget, id, e);
		break;
        case MouseEvent.MOUSE_DRAGGED:
            if (isMouseGrab(e)) {
                retargetMouseEvent(mouseEventTarget, id, e);
            }
		break;
        case MouseEvent.MOUSE_WHEEL:
            // This may send it somewhere that doesn't have MouseWheelEvents
            // enabled.  In this case, Component.dispatchEventImpl() will
            // retarget the event to a parent that DOES have the events enabled.
            if (dbg.on && mouseOver != null) {
                dbg.println("LD retargeting mouse wheel to " +
                                mouseOver.getName() + ", " + 
                                mouseOver.getClass());
            }
            retargetMouseEvent(mouseOver, id, e);
        break;
	    }
	    e.consume();
    }
    return e.isConsumed();
    }

    private boolean processDropTargetEvent(SunDropTargetEvent e) {
        int id = e.getID();
        int x = e.getX(); 
        int y = e.getY();

        /*
         * Fix for BugTraq ID 4395290.
         * It is possible that SunDropTargetEvent's Point is outside of the
         * native container bounds. In this case we truncate coordinates.
         */
        if (!nativeContainer.contains(x, y)) {
            final Dimension d = nativeContainer.getSize();
            if (d.width <= x) { 
                x = d.width - 1;
            } else if (x < 0) {
                x = 0;
            }
            if (d.height <= y) { 
                y = d.height - 1;
            } else if (y < 0) {
                y = 0;
            }
        }
        Component mouseOver =   // not necessarily sensitive to mouse events
            nativeContainer.getDropTargetEventTarget(x, y,
                                                     Container.INCLUDE_SELF); 
        trackMouseEnterExit(mouseOver, e);

        if (mouseOver != nativeContainer && mouseOver != null) {
            switch (id) {
            case SunDropTargetEvent.MOUSE_ENTERED:
            case SunDropTargetEvent.MOUSE_EXITED:
                break;
            default:
                retargetMouseEvent(mouseOver, id, e);
                e.consume();
                break;
            }
        }
        return e.isConsumed();
    }

    /*
     * Generates enter/exit events as mouse moves over lw components
     * @param targetOver	Target mouse is over (including native container)
     * @param e			Mouse event in native container
     */
    private void trackMouseEnterExit(Component targetOver, MouseEvent e) {
	Component	targetEnter = null;
	int		id = e.getID();

        if (e instanceof SunDropTargetEvent &&
            id == MouseEvent.MOUSE_ENTERED &&
            isMouseInNativeContainer == true) {
            // This can happen if a lightweight component which initiated the
            // drag has an associated drop target. MOUSE_ENTERED comes when the
            // mouse is in the native container already. To propagate this event
            // properly we should null out targetLastEntered.
            targetLastEntered = null;
        } else if ( id != MouseEvent.MOUSE_EXITED &&
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
	
        if (targetLastEntered == targetEnter) {
	        return;
        }

        if (targetLastEntered != null) {
            retargetMouseEvent(targetLastEntered, MouseEvent.MOUSE_EXITED, e);
        }
        if (id == MouseEvent.MOUSE_EXITED) {
            // consume native exit event if we generate one
            e.consume();
        }

        if (targetEnter != null) {
            retargetMouseEvent(targetEnter, MouseEvent.MOUSE_ENTERED, e);
        }
        if (id == MouseEvent.MOUSE_ENTERED) {
            // consume native enter event if we generate one
            e.consume();
        }

	targetLastEntered = targetEnter;
    }

    /*
     * Listens to global mouse drag events so even drags originating
     * from other heavyweight containers will generate enter/exit
     * events in this container
     */
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
                                !(e instanceof SunDropTargetEvent) &&
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
			       srcEvent.getModifiersEx() | srcEvent.getModifiers(),
			       srcEvent.getX(),
			       srcEvent.getY(),
			       srcEvent.getClickCount(),
			       srcEvent.isPopupTrigger(),
                               srcEvent.getButton());
	    ((AWTEvent)srcEvent).copyPrivateDataInto(me);
	    // translate coordinates to this native container
	    Point	ptSrcOrigin = srcComponent.getLocationOnScreen();
	    Point	ptDstOrigin = nativeContainer.getLocationOnScreen();
	    me.translatePoint( ptSrcOrigin.x - ptDstOrigin.x, ptSrcOrigin.y - ptDstOrigin.y );
	}
	//System.out.println("Track event: " + me);
	// feed the 'dragged-over' event directly to the enter/exit
	// code (not a real event so don't pass it to dispatchEvent)
	Component targetOver = 
            nativeContainer.getMouseEventTarget(me.getX(), me.getY(), 
                                                Container.INCLUDE_SELF);
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
	    return; // mouse is over another hw component or target is disabled
	}

        int x = e.getX(), y = e.getY();
        Component component;

        for(component = target;
            component != null && component != nativeContainer;
            component = component.getParent()) {
            x -= component.x;
            y -= component.y;
        }
        MouseEvent retargeted;
        if (component != null) {
            if (e instanceof SunDropTargetEvent) {
                retargeted = new SunDropTargetEvent(target,
                                                    id,
                                                    x,
                                                    y,
                                                    ((SunDropTargetEvent)e).getDispatcher());
            } else if (id == MouseEvent.MOUSE_WHEEL) {
                retargeted = new MouseWheelEvent(target,
                                      id,
                                       e.getWhen(),
                                       e.getModifiersEx() | e.getModifiers(),
                                       x,
                                       y,
                                       e.getClickCount(),
                                       e.isPopupTrigger(),
                                       ((MouseWheelEvent)e).getScrollType(),
                                       ((MouseWheelEvent)e).getScrollAmount(),
                                       ((MouseWheelEvent)e).getWheelRotation());
            }
            else {
                retargeted = new MouseEvent(target,
                                            id, 
                                            e.getWhen(), 
                                            e.getModifiersEx() | e.getModifiers(),
                                            x, 
                                            y, 
                                            e.getClickCount(), 
                                            e.isPopupTrigger(),
                                            e.getButton());
            }

	    ((AWTEvent)e).copyPrivateDataInto(retargeted);

	    if (target == nativeContainer) {
		// avoid recursively calling LightweightDispatcher...
		((Container)target).dispatchEventToSelf(retargeted);
	    } else {
		target.dispatchEvent(retargeted);
	    }
        }
    }
	
    // --- member variables -------------------------------

    /**
     * The windowed container that might be hosting events for 
     * subcomponents.
     */
    private Container nativeContainer;

    /**
     * This variable is not used, but kept for serialization compatibility
     */
    private Component focus;

    /**
     * The current subcomponent being hosted by this windowed
     * component that has events being forwarded to it.  If this
     * is null, there are currently no events being forwarded to 
     * a subcomponent.
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
     * This variable is not used, but kept for serialization compatibility
     */
    private Cursor nativeCursor;

    /**
     * The event mask for contained lightweight components.  Lightweight
     * components need a windowed container to host window-related 
     * events.  This separate mask indicates events that have been 
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
        AWTEvent.MOUSE_MOTION_EVENT_MASK |
        AWTEvent.MOUSE_WHEEL_EVENT_MASK;

    private static final long MOUSE_MASK = 
        AWTEvent.MOUSE_EVENT_MASK |
        AWTEvent.MOUSE_MOTION_EVENT_MASK |
        AWTEvent.MOUSE_WHEEL_EVENT_MASK;
}
