/*
 * @(#)SortingFocusTraversalPolicy.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.*;


/**
 * A FocusTraversalPolicy that determines traversal order by sorting the
 * Components of a focus traversal cycle based on a given Comparator. Portions
 * of the Component hierarchy that are not visible and displayable will not be
 * included.
 * <p>
 * By default, SortingFocusTraversalPolicy implicitly transfers focus down-
 * cycle. That is, during normal focus traversal, the Component
 * traversed after a focus cycle root will be the focus-cycle-root's default
 * Component to focus. This behavior can be disabled using the
 * <code>setImplicitDownCycleTraversal</code> method.
 * <p>
 * By default, methods of this class with return a Component only if it is
 * visible, displayable, enabled, and focusable. Subclasses can modify this
 * behavior by overriding the <code>accept</code> method.
 *
 * @author David Mendenhall
 * @version 1.4, 01/23/03
 *
 * @see java.util.Comparator
 * @since 1.4
 */
public class SortingFocusTraversalPolicy
    extends InternalFrameFocusTraversalPolicy
{
    private Comparator comparator;
    private boolean implicitDownCycleTraversal = true;

    /**
     * Used by getComponentAfter and getComponentBefore for efficiency. In
     * order to maintain compliance with the specification of
     * FocusTraversalPolicy, if traversal wraps, we should invoke
     * getFirstComponent or getLastComponent. These methods may be overriden in
     * subclasses to behave in a non-generic way. However, in the generic case,
     * these methods will simply return the first or last Components of the
     * sorted list, respectively. Since getComponentAfter and
     * getComponentBefore have already built the sorted list before determining
     * that they need to invoke getFirstComponent or getLastComponent, the
     * sorted list should be reused if possible.
     */
    private Container cachedRoot;
    private List cachedCycle;

    // Delegate our fitness test to ContainerOrder so that we only have to
    // code the algorithm once.
    private static final SwingContainerOrderFocusTraversalPolicy
	fitnessTestPolicy = new SwingContainerOrderFocusTraversalPolicy();

    /**
     * Constructs a SortingFocusTraversalPolicy without a Comparator.
     * Subclasses must set the Comparator using <code>setComparator</code>
     * before installing this FocusTraversalPolicy on a focus cycle root or
     * KeyboardFocusManager.
     */
    protected SortingFocusTraversalPolicy() {
    }

    /**
     * Constructs a SortingFocusTraversalPolicy with the specified Comparator.
     */
    public SortingFocusTraversalPolicy(Comparator comparator) {
	this.comparator = comparator;
    }

    private void enumerateAndSortCycle(Container focusCycleRoot,
				       List cycle, Map defaults) {
	List defaultRoots = null;

	if (!focusCycleRoot.isShowing()) {
	    return;
	}

	enumerateCycle(focusCycleRoot, cycle);

	boolean addDefaultComponents =
	    (defaults != null && getImplicitDownCycleTraversal());

	// Create a list of all default Components which should be added
	// to the list
	if (addDefaultComponents) {
	    defaultRoots = new ArrayList();
	    for (Iterator iter = cycle.iterator(); iter.hasNext(); ) {
		Component comp = (Component)iter.next();
		if ((comp instanceof Container) &&
		    ((Container)comp).isFocusCycleRoot())
		{
		    defaultRoots.add(comp);
		}
	    }
	    Collections.sort(defaultRoots, comparator);
	}

	// Test all Components in the cycle for fitness. Remove unfit
	// Components. Do not test default Components of other cycles.
	for (Iterator iter = cycle.iterator(); iter.hasNext(); ) {
	    Component comp = (Component)iter.next();
	    if (!accept(comp)) {
		iter.remove();
	    }
	}

	// Sort the Components in the cycle
	Collections.sort(cycle, comparator);

	// Find all of the roots in the cycle and place their default
	// Components after them. Note that the roots may have been removed
	// from the list because they were unfit. In that case, insert the
	// default Components as though the roots were still in the list.
	if (addDefaultComponents) {
	    for (ListIterator defaultRootsIter = 
		     defaultRoots.listIterator(defaultRoots.size());
		 defaultRootsIter.hasPrevious(); )
	    {
		Container root = (Container)defaultRootsIter.previous();
		Component defComp =
		    root.getFocusTraversalPolicy().getDefaultComponent(root);

		if (defComp != null && defComp.isShowing()) {
		    int index = Collections.binarySearch(cycle, root,
							 comparator);
		    if (index < 0) {
			// If root is not in the list, then binarySearch
			// returns (-(insertion point) - 1). defComp follows
			// the index one less than the insertion point.
			
			index = -index - 2;
		    }
		    
		    defaults.put(new Integer(index), defComp);
		}
	    }
	}
    }

    private void enumerateCycle(Container container, List cycle) {
	if (!(container.isVisible() && container.isDisplayable())) {
	    return;
	}

	cycle.add(container);

	Component[] components = container.getComponents();
	for (int i = 0; i < components.length; i++) {
	    Component comp = components[i];
	    if ((comp instanceof Container) &&
		!((Container)comp).isFocusCycleRoot() &&
		!((comp instanceof JComponent) &&
		  ((JComponent)comp).isManagingFocus())) {
		enumerateCycle((Container)comp, cycle);
	    } else {
		cycle.add(comp);
	    }
	}
    }

    /**
     * Returns the Component that should receive the focus after aComponent.
     * focusCycleRoot must be a focus cycle root of aComponent.
     * <p>
     * By default, SortingFocusTraversalPolicy implicitly transfers focus down-
     * cycle. That is, during normal focus traversal, the Component
     * traversed after a focus cycle root will be the focus-cycle-root's
     * default Component to focus. This behavior can be disabled using the
     * <code>setImplicitDownCycleTraversal</code> method.
     *
     * @param focusCycleRoot a focus cycle root of aComponent
     * @param aComponent a (possibly indirect) child of focusCycleRoot, or
     *        focusCycleRoot itself
     * @return the Component that should receive the focus after aComponent, or
     *         null if no suitable Component can be found
     * @throws IllegalArgumentException if focusCycleRoot is not a focus cycle
     *         root of aComponent, or if either focusCycleRoot or aComponent is
     *         null
     */
    public Component getComponentAfter(Container focusCycleRoot,
				       Component aComponent) {
        if (focusCycleRoot == null || aComponent == null) {
            throw new IllegalArgumentException("focusCycleRoot and aComponent cannot be null");
        }
        if (!aComponent.isFocusCycleRoot(focusCycleRoot)) {
            throw new IllegalArgumentException("focusCycleRoot is not a focus cyle root of aComponent");
        }

	List cycle = new ArrayList();
	Map defaults = new HashMap();
	enumerateAndSortCycle(focusCycleRoot, cycle, defaults);

	int index;
	try {
	    index = Collections.binarySearch(cycle, aComponent, comparator);
	} catch (ClassCastException e) {
	    return getFirstComponent(focusCycleRoot);
	}

	if (index < 0) {
	    // If we're not in the cycle, then binarySearch returns
	    // (-(insertion point) - 1). The next element is our insertion
	    // point.

	    index = -index - 2;
	}

	Component defComp = (Component)defaults.get(new Integer(index));
	if (defComp != null) {
	    return defComp;
	}

	index++;

	if (index >= cycle.size()) {
	    this.cachedRoot = focusCycleRoot;
	    this.cachedCycle = cycle;

	    Component retval = getFirstComponent(focusCycleRoot);

	    this.cachedRoot = null;
	    this.cachedCycle = null;

	    return retval;
	} else {
	    return (Component)cycle.get(index);
	}
    }

    /**
     * Returns the Component that should receive the focus before aComponent.
     * focusCycleRoot must be a focus cycle root of aComponent.
     * <p>
     * By default, SortingFocusTraversalPolicy implicitly transfers focus down-
     * cycle. That is, during normal focus traversal, the Component
     * traversed after a focus cycle root will be the focus-cycle-root's
     * default Component to focus. This behavior can be disabled using the
     * <code>setImplicitDownCycleTraversal</code> method.
     *
     * @param focusCycleRoot a focus cycle root of aComponent
     * @param aComponent a (possibly indirect) child of focusCycleRoot, or
     *        focusCycleRoot itself
     * @return the Component that should receive the focus before aComponent,
     *         or null if no suitable Component can be found
     * @throws IllegalArgumentException if focusCycleRoot is not a focus cycle
     *         root of aComponent, or if either focusCycleRoot or aComponent is
     *         null
     */
    public Component getComponentBefore(Container focusCycleRoot,
					Component aComponent) {
        if (focusCycleRoot == null || aComponent == null) {
            throw new IllegalArgumentException("focusCycleRoot and aComponent cannot be null");
        }
        if (!aComponent.isFocusCycleRoot(focusCycleRoot)) {
            throw new IllegalArgumentException("focusCycleRoot is not a focus cyle root of aComponent");
        }

	List cycle = new ArrayList();
	Map defaults = new HashMap();
	enumerateAndSortCycle(focusCycleRoot, cycle, defaults);

	int index;
	try {
	    index = Collections.binarySearch(cycle, aComponent, comparator);
	} catch (ClassCastException e) {
	    return getLastComponent(focusCycleRoot);
	}

	if (index < 0) {
	    // If we're not in the cycle, then binarySearch returns
	    // (-(insertion point) - 1). The previous element is our insertion
	    // point - 1.
	    
            index = -index - 2;
	} else {
            index--;
        }


	if (index < 0) {
	    this.cachedRoot = focusCycleRoot;
	    this.cachedCycle = cycle;

	    Component retval = getLastComponent(focusCycleRoot);

	    this.cachedRoot = null;
	    this.cachedCycle = null;

	    return retval;
	} else {
	    Component defComp = (Component)defaults.get(new Integer(index));
	    if (defComp != null) {
	        return defComp;
	    }
	    return (Component)cycle.get(index);
	}
    }

    /**
     * Returns the first Component in the traversal cycle. This method is used
     * to determine the next Component to focus when traversal wraps in the
     * forward direction.
     *
     * @param focusCycleRoot the focus cycle root whose first Component is to
     *         be returned
     * @return the first Component in the traversal cycle when focusCycleRoot
     *         is the focus cycle root, or null if no suitable Component can be
     *         found
     * @throws IllegalArgumentException if focusCycleRoot is null
     */
    public Component getFirstComponent(Container focusCycleRoot) {
	List cycle;

	if (focusCycleRoot == null) {
	    throw new IllegalArgumentException("focusCycleRoot cannot be null");
	}

	if (this.cachedRoot == focusCycleRoot) {
	    cycle = this.cachedCycle;
	} else {
	    cycle = new ArrayList();
	    enumerateAndSortCycle(focusCycleRoot, cycle, null);
	}

	int size = cycle.size();
	if (size == 0) {
	    return null;
	}

	return (Component)cycle.get(0);
    }

    /**
     * Returns the last Component in the traversal cycle. This method is used
     * to determine the next Component to focus when traversal wraps in the
     * reverse direction.
     *
     * @param focusCycleRoot the focus cycle root whose last Component is to be
     *        returned
     * @return the last Component in the traversal cycle when focusCycleRoot is
     *         the focus cycle root, or null if no suitable Component can be
     *         found
     * @throws IllegalArgumentException if focusCycleRoot is null
     */
    public Component getLastComponent(Container focusCycleRoot) {
	List cycle;

	if (focusCycleRoot == null) {
	    throw new IllegalArgumentException("focusCycleRoot cannot be null");
	}

	if (this.cachedRoot == focusCycleRoot) {
	    cycle = this.cachedCycle;
	} else {
	    cycle = new ArrayList();
	    enumerateAndSortCycle(focusCycleRoot, cycle, null);
	}

	int size = cycle.size();
	if (size == 0) {
	    return null;
	}

	return (Component)cycle.get(size - 1);
    }

    /**
     * Returns the default Component to focus. This Component will be the first
     * to receive focus when traversing down into a new focus traversal cycle
     * rooted at focusCycleRoot. The default implementation of this method
     * returns the same Component as <code>getFirstComponent</code>.
     *
     * @param focusCycleRoot the focus cycle root whose default Component is to
     *        be returned
     * @return the default Component in the traversal cycle when focusCycleRoot
     *         is the focus cycle root, or null if no suitable Component can be
     *         found
     * @see #getFirstComponent
     * @throws IllegalArgumentException if focusCycleRoot is null
     */
    public Component getDefaultComponent(Container focusCycleRoot) {
	return getFirstComponent(focusCycleRoot);
    }

    /**
     * Sets whether this SortingFocusTraversalPolicy transfers focus down-cycle
     * implicitly. If <code>true</code>, during normal focus traversal,
     * the Component traversed after a focus cycle root will be the focus-
     * cycle-root's default Component to focus. If <code>false</code>, the
     * next Component in the focus traversal cycle rooted at the specified
     * focus cycle root will be traversed instead. The default value for this
     * property is <code>true</code>.
     *
     * @param implicitDownCycleTraversal whether this
     *        SortingFocusTraversalPolicy transfers focus down-cycle implicitly
     * @see #getImplicitDownCycleTraversal
     * @see #getFirstComponent
     */
    public void setImplicitDownCycleTraversal(boolean
					      implicitDownCycleTraversal) {
	this.implicitDownCycleTraversal = implicitDownCycleTraversal;
    }

    /**
     * Returns whether this SortingFocusTraversalPolicy transfers focus down-
     * cycle implicitly. If <code>true</code>, during normal focus
     * traversal, the Component traversed after a focus cycle root will be the
     * focus-cycle-root's default Component to focus. If <code>false</code>,
     * the next Component in the focus traversal cycle rooted at the specified
     * focus cycle root will be traversed instead.
     *
     * @return whether this SortingFocusTraversalPolicy transfers focus down-
     *         cycle implicitly
     * @see #setImplicitDownCycleTraversal
     * @see #getFirstComponent
     */
    public boolean getImplicitDownCycleTraversal() {
	return implicitDownCycleTraversal;
    }

    /**
     * Sets the Comparator which will be used to sort the Components in a
     * focus traversal cycle.
     *
     * @param comparator the Comparator which will be used for sorting
     */
    protected void setComparator(Comparator comparator) {
	this.comparator = comparator;
    }

    /**
     * Returns the Comparator which will be used to sort the Components in a
     * focus traversal cycle.
     *
     * @return the Comparator which will be used for sorting
     */
    protected Comparator getComparator() {
	return comparator;
    }

    /**
     * Determines whether a Component is an acceptable choice as the new
     * focus owner. By default, this method will accept a Component if and
     * only if it is visible, displayable, enabled, and focusable.
     *
     * @param aComponent the Component whose fitness as a focus owner is to
     *        be tested
     * @return <code>true</code> if aComponent is visible, displayable,
     *         enabled, and focusable; <code>false</code> otherwise
     */
    protected boolean accept(Component aComponent) {
	return fitnessTestPolicy.accept(aComponent);
    }
}

// Create our own subclass and change accept to public so that we can call
// accept.
class SwingContainerOrderFocusTraversalPolicy
    extends java.awt.ContainerOrderFocusTraversalPolicy
{
    public boolean accept(Component aComponent) {
	return super.accept(aComponent);
    }
}
