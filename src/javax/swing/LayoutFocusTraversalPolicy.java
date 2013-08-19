/*
 * @(#)LayoutFocusTraversalPolicy.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.ComponentOrientation;
import java.util.Comparator;
import java.io.*;


/**
 * A SortingFocusTraversalPolicy which sorts Components based on their size,
 * position, and orientation. Based on their size and position, Components are
 * roughly categorized into rows and columns. For a Container with horizontal
 * orientation, columns run left-to-right or right-to-left, and rows run top-
 * to-bottom. For a Container with vertical orientation, columns run top-to-
 * bottom and rows run left-to-right or right-to-left. See
 * <code>ComponentOrientation</code> for more information. All columns in a
 * row are fully traversed before proceeding to the next row.
 *
 * @version 1.8, 01/23/03
 * @author David Mendenhall
 *
 * @see java.awt.ComponentOrientation
 * @since 1.4
 */
public class LayoutFocusTraversalPolicy extends SortingFocusTraversalPolicy
    implements Serializable
{
    // Delegate most of our fitness test to Default so that we only have to
    // code the algorithm once.
    private static final SwingDefaultFocusTraversalPolicy fitnessTestPolicy =
	new SwingDefaultFocusTraversalPolicy();

    /**
     * Constructs a LayoutFocusTraversalPolicy.
     */
    public LayoutFocusTraversalPolicy() {
	super(new LayoutComparator());
    }

    /**
     * Constructs a LayoutFocusTraversalPolicy with the passed in
     * <code>Comparator</code>.
     */
    LayoutFocusTraversalPolicy(Comparator c) {
	super(c);
    }

    /**
     * Returns the Component that should receive the focus after aComponent.
     * focusCycleRoot must be a focus cycle root of aComponent.
     * <p>
     * By default, LayoutFocusTraversalPolicy implicitly transfers focus down-
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
	Comparator comparator = getComparator();
	if (comparator instanceof LayoutComparator) {
	    ((LayoutComparator)comparator).
		setComponentOrientation(focusCycleRoot.
					getComponentOrientation());
	}
	return super.getComponentAfter(focusCycleRoot, aComponent);
    }

    /**
     * Returns the Component that should receive the focus before aComponent.
     * focusCycleRoot must be a focus cycle root of aComponent.
     * <p>
     * By default, LayoutFocusTraversalPolicy implicitly transfers focus down-
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
	Comparator comparator = getComparator();
	if (comparator instanceof LayoutComparator) {
	    ((LayoutComparator)comparator).
		setComponentOrientation(focusCycleRoot.
					getComponentOrientation());
	}
	return super.getComponentBefore(focusCycleRoot, aComponent);
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
	Comparator comparator = getComparator();
	if (comparator instanceof LayoutComparator) {
	    ((LayoutComparator)comparator).
		setComponentOrientation(focusCycleRoot.
					getComponentOrientation());
	}
	return super.getFirstComponent(focusCycleRoot);
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
	Comparator comparator = getComparator();
	if (comparator instanceof LayoutComparator) {
	    ((LayoutComparator)comparator).
		setComponentOrientation(focusCycleRoot.
					getComponentOrientation());
	}
	return super.getLastComponent(focusCycleRoot);
    }

    /**  
     * Determines whether the specified <code>Component</code>
     * is an acceptable choice as the new focus owner.
     * This method performs the following sequence of operations: 
     * <ol>
     * <li>Checks whether <code>aComponent</code> is visible, displayable,
     *     enabled, and focusable.  If any of these properties is
     *     <code>false</code>, this method returns <code>false</code>.
     * <li>If <code>aComponent</code> is an instance of <code>JTable</code>, 
     *     returns <code>true</code>.  
     * <li>If <code>aComponent</code> is an instance of <code>JComboBox</code>,
     *     then returns the value of
     *     <code>aComponent.getUI().isFocusTraversable(aComponent)</code>.
     * <li>If <code>aComponent</code> is a <code>JComponent</code>
     *     with a <code>JComponent.WHEN_FOCUSED</code>
     *     <code>InputMap</code> that is neither <code>null</code>
     *     nor empty, returns <code>true</code>.
     * <li>Returns the value of 
     *     <code>DefaultFocusTraversalPolicy.accept(aComponent)</code>.
     * </ol>
     *   
     * @param aComponent the <code>Component</code> whose fitness
     *                   as a focus owner is to be tested
     * @see java.awt.Component#isVisible
     * @see java.awt.Component#isDisplayable
     * @see java.awt.Component#isEnabled 
     * @see java.awt.Component#isFocusable
     * @see javax.swing.plaf.ComboBoxUI#isFocusTraversable 
     * @see javax.swing.JComponent#getInputMap
     * @see java.awt.DefaultFocusTraversalPolicy#accept
     * @return <code>true</code> if <code>aComponent</code> is a valid choice
     *         for a focus owner;
     *         otherwise <code>false</code>
     */  
     protected boolean accept(Component aComponent) {
	if (!super.accept(aComponent)) {
	    return false;
	} else if (aComponent instanceof JTable) {
            // JTable only has ancestor focus bindings, we thus force it
            // to be focusable by returning true here.
	    return true;
	} else if (aComponent instanceof JComboBox) {
	    JComboBox box = (JComboBox)aComponent;
	    return box.getUI().isFocusTraversable(box);
	} else if (aComponent instanceof JComponent) {
	    JComponent jComponent = (JComponent)aComponent;
	    InputMap inputMap = jComponent.getInputMap(JComponent.WHEN_FOCUSED,
						       false);
	    while (inputMap != null && inputMap.size() == 0) {
		inputMap = inputMap.getParent();
	    }
            if (inputMap != null) {
                return true;
            }
            // Delegate to the fitnessTestPolicy, this will test for the
            // case where the developer has overriden isFocusTraversable to
            // return true.
        }
        return fitnessTestPolicy.accept(aComponent);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
	out.writeObject(getComparator());
	out.writeBoolean(getImplicitDownCycleTraversal());
    }
    private void readObject(ObjectInputStream in)
	throws IOException, ClassNotFoundException
    {
	setComparator((Comparator)in.readObject());
	setImplicitDownCycleTraversal(in.readBoolean());
    }
}

// Create our own subclass and change accept to public so that we can call
// accept.
class SwingDefaultFocusTraversalPolicy
    extends java.awt.DefaultFocusTraversalPolicy
{
    public boolean accept(Component aComponent) {
	return super.accept(aComponent);
    }
}
