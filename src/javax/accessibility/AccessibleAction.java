/*
 * @(#)AccessibleAction.java	1.17 04/04/15
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.accessibility;

/**
 * The AccessibleAction interface should be supported by any object 
 * that can perform one or more actions.  This interface
 * provides the standard mechanism for an assistive technology to determine 
 * what those actions are as well as tell the object to perform them.
 * Any object that can be manipulated should support this
 * interface.  Applications can determine if an object supports the 
 * AccessibleAction interface by first obtaining its AccessibleContext (see
 * {@link Accessible}) and then calling the {@link AccessibleContext#getAccessibleAction}
 * method.  If the return value is not null, the object supports this interface.
 *
 * @see Accessible
 * @see Accessible#getAccessibleContext
 * @see AccessibleContext
 * @see AccessibleContext#getAccessibleAction
 *
 * @version     1.17 04/15/04
 * @author	Peter Korn
 * @author      Hans Muller
 * @author      Willie Walker
 * @author      Lynn Monsanto
 */
public interface AccessibleAction {

    /**
     * An action which causes a tree node to
     * collapse if expanded and expand if collapsed.
     * @since 1.5
     */
    public static final String TOGGLE_EXPAND =
        new String ("toggle expand"); 

    /**
     * An action which increments a value.
     * @since 1.5
     */
    public static final String INCREMENT =
        new String ("increment"); 


    /**
     * An action which decrements a value.
     * @since 1.5
     */
    public static final String DECREMENT =
        new String ("decrement"); 

    /**
     * Returns the number of accessible actions available in this object
     * If there are more than one, the first one is considered the "default"
     * action of the object.
     *
     * @return the zero-based number of Actions in this object
     */
    public int getAccessibleActionCount();

    /**
     * Returns a description of the specified action of the object.
     *
     * @param i zero-based index of the actions
     * @return a String description of the action
     * @see #getAccessibleActionCount
     */
    public String getAccessibleActionDescription(int i);

    /**
     * Performs the specified Action on the object
     *
     * @param i zero-based index of actions
     * @return true if the action was performed; otherwise false.
     * @see #getAccessibleActionCount
     */
    public boolean doAccessibleAction(int i);
}
