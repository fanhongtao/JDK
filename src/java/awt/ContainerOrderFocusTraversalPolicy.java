/*
 * @(#)ContainerOrderFocusTraversalPolicy.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

/**
 * A FocusTraversalPolicy that determines traversal order based on the order
 * of child Components in a Container. From a particular focus cycle root, the
 * policy makes a pre-order traversal of the Component hierarchy, and traverses
 * a Container's children according to the ordering of the array returned by
 * <code>Container.getComponents()</code>. Portions of the hierarchy that are
 * not visible and displayable will not be searched.
 * <p>
 * By default, ContainerOrderFocusTraversalPolicy implicitly transfers focus
 * down-cycle. That is, during normal forward focus traversal, the Component
 * traversed after a focus cycle root will be the focus-cycle-root's default
 * Component to focus. This behavior can be disabled using the
 * <code>setImplicitDownCycleTraversal</code> method.
 * <p>
 * By default, methods of this class with return a Component only if it is
 * visible, displayable, enabled, and focusable. Subclasses can modify this
 * behavior by overriding the <code>accept</code> method.
 *
 * @author David Mendenhall
 * @version 1.3, 01/23/03
 *
 * @see Container#getComponents
 * @since 1.4
 */
public class ContainerOrderFocusTraversalPolicy extends FocusTraversalPolicy
    implements java.io.Serializable
{
    private static class MutableBoolean {
        boolean value = false;
    }
    private static final MutableBoolean found = new MutableBoolean();

    private boolean implicitDownCycleTraversal = true;

    /**
     * Returns the Component that should receive the focus after aComponent.
     * focusCycleRoot must be a focus cycle root of aComponent.
     * <p>
     * By default, ContainerOrderFocusTraversalPolicy implicitly transfers
     * focus down-cycle. That is, during normal forward focus traversal, the
     * Component traversed after a focus cycle root will be the focus-cycle-
     * root's default Component to focus. This behavior can be disabled using
     * the <code>setImplicitDownCycleTraversal</code> method.
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

        synchronized(focusCycleRoot.getTreeLock()) {
            found.value = false;
            Component retval = getComponentAfter(focusCycleRoot, aComponent,
                                                 found);
            if (retval != null) {
                return retval;
            } else if (found.value) {
                return getFirstComponent(focusCycleRoot);
            } else {
                return null;
            }
        }
    }

    private Component getComponentAfter(Container aContainer,
                                        Component aComponent,
                                        MutableBoolean found) {
        if (!(aContainer.isVisible() && aContainer.isDisplayable())) {
	    return null;
	}

        if (found.value) {
            if (accept(aContainer)) {
                return aContainer;
            }
        } else if (aContainer == aComponent) {
            found.value = true;
        }

        for (int i = 0; i < aContainer.ncomponents; i++) {
            Component comp = aContainer.component[i];
            if ((comp instanceof Container) &&
                !((Container)comp).isFocusCycleRoot()) {
                Component retval = getComponentAfter((Container)comp,
                                                     aComponent,
                                                     found);
                if (retval != null) {
                    return retval;
                }
            } else if (found.value) {
                if (accept(comp)) {
                    return comp;
                }
            } else if (comp == aComponent) {
                found.value = true;
            }

	    if (found.value &&
		getImplicitDownCycleTraversal() &&
		(comp instanceof Container) &&
		((Container)comp).isFocusCycleRoot())
	    {
		Container cont = (Container)comp;
		Component retval = cont.getFocusTraversalPolicy().
		    getDefaultComponent(cont);
		if (retval != null) {
		    return retval;
		}
	    }
        }

        return null;
    }

    /**
     * Returns the Component that should receive the focus before aComponent.
     * focusCycleRoot must be a focus cycle root of aComponent.
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

        synchronized(focusCycleRoot.getTreeLock()) {
            found.value = false;
            Component retval = getComponentBefore(focusCycleRoot, aComponent,
                                                  found);
            if (retval != null) {
                return retval;
            } else if (found.value) {
                return getLastComponent(focusCycleRoot);
            } else {
                return null;
            }
        }
    }

    private Component getComponentBefore(Container aContainer,
                                         Component aComponent,
                                         MutableBoolean found) {
        if (!(aContainer.isVisible() && aContainer.isDisplayable())) {
	    return null;
	}

        for (int i = aContainer.ncomponents - 1; i >= 0; i--) {
            Component comp = aContainer.component[i];
	    if (comp == aComponent) {
		found.value = true;
	    } else if ((comp instanceof Container) &&
                !((Container)comp).isFocusCycleRoot()) {
                Component retval = getComponentBefore((Container)comp,
                                                      aComponent,
                                                      found);
                if (retval != null) {
                    return retval;
                }
            } else if (found.value) {
                if (accept(comp)) {
                    return comp;
                }
            }
        }

        if (found.value) {
            if (accept(aContainer)) {
                return aContainer;
            }
        } else if (aContainer == aComponent) {
            found.value = true;
        }

        return null;
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
        if (focusCycleRoot == null) {
            throw new IllegalArgumentException("focusCycleRoot cannot be null");
        }

        synchronized(focusCycleRoot.getTreeLock()) {
            if (!(focusCycleRoot.isVisible() &&
		  focusCycleRoot.isDisplayable()))
	    {
                return null;
            }

            if (accept(focusCycleRoot)) {
                return focusCycleRoot;
            }

            for (int i = 0; i < focusCycleRoot.ncomponents; i++) {
                Component comp = focusCycleRoot.component[i];
		if (comp instanceof Container &&
		    !((Container)comp).isFocusCycleRoot())
		{
		    Component retval = getFirstComponent((Container)comp);
		    if (retval != null) {
		        return retval;
		    }
		} else if (accept(comp)) {
		    return comp;
		}
            }
        }

        return null;
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
        if (focusCycleRoot == null) {
            throw new IllegalArgumentException("focusCycleRoot cannot be null");
        }

        synchronized(focusCycleRoot.getTreeLock()) {
            if (!(focusCycleRoot.isVisible() &&
		  focusCycleRoot.isDisplayable()))
	    {
                return null;
            }

            for (int i = focusCycleRoot.ncomponents - 1; i >= 0; i--) {
                Component comp = focusCycleRoot.component[i];
		if (comp instanceof Container &&
		    !((Container)comp).isFocusCycleRoot())
		{
		    Component retval = getLastComponent((Container)comp);
		    if (retval != null) {
		        return retval;
		    }
		} else if (accept(comp)) {
		    return comp;
		}
            }

            if (accept(focusCycleRoot)) {
                return focusCycleRoot;
            }
        }

        return null;
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
     * Sets whether this ContainerOrderFocusTraversalPolicy transfers focus
     * down-cycle implicitly. If <code>true</code>, during normal forward focus
     * traversal, the Component traversed after a focus cycle root will be the
     * focus-cycle-root's default Component to focus. If <code>false</code>,
     * the next Component in the focus traversal cycle rooted at the specified
     * focus cycle root will be traversed instead. The default value for this
     * property is <code>true</code>.
     *
     * @param implicitDownCycleTraversal whether this
     *        ContainerOrderFocusTraversalPolicy transfers focus down-cycle
     *        implicitly
     * @see #getImplicitDownCycleTraversal
     * @see #getFirstComponent
     */
    public void setImplicitDownCycleTraversal(boolean
					      implicitDownCycleTraversal) {
	this.implicitDownCycleTraversal = implicitDownCycleTraversal;
    }

    /**
     * Returns whether this ContainerOrderFocusTraversalPolicy transfers focus
     * down-cycle implicitly. If <code>true</code>, during normal forward focus
     * traversal, the Component traversed after a focus cycle root will be the
     * focus-cycle-root's default Component to focus. If <code>false</code>,
     * the next Component in the focus traversal cycle rooted at the specified
     * focus cycle root will be traversed instead.
     *
     * @return whether this ContainerOrderFocusTraversalPolicy transfers focus
     *         down-cycle implicitly
     * @see #setImplicitDownCycleTraversal
     * @see #getFirstComponent
     */
    public boolean getImplicitDownCycleTraversal() {
	return implicitDownCycleTraversal;
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
	if (!(aComponent.isVisible() && aComponent.isDisplayable() &&
	      aComponent.isFocusable() && aComponent.isEnabled())) {
	    return false;
	}

	// Verify that the Component is recursively enabled. Disabling a
	// heavyweight Container disables its children, whereas disabling
	// a lightweight Container does not.
	if (!(aComponent instanceof Window)) {
	    for (Container enableTest = aComponent.getParent();
		 enableTest != null;
		 enableTest = enableTest.getParent())
	    {
		if (!(enableTest.isEnabled() || enableTest.isLightweight())) {
		    return false;
		}
		if (enableTest instanceof Window) {
		    break;
		}
	    }
	}

	return true;
    }
}
