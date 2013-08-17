/*
 * @(#)AccessibleAction.java	1.7 98/08/26
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

package javax.accessibility;

/**
 * The AccessibleAction interface should be supported by any object 
 * that can perform one or more actions.  This interface
 * provides the standard mechanism for an assistive technology to determine 
 * what those actions are as well as tell the object to perform those
 * actions.  Any object that can be manipulated should support this
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
 * @version     1.3 02/04/98 11:12:57
 * @author	Peter Korn
 * @author      Hans Muller
 * @author      Willie Walker
 */
public interface AccessibleAction {

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
     * Perform the specified Action on the object
     *
     * @param i zero-based index of actions
     * @return true if the the action was performed; else false.
     * @see #getAccessibleActionCount
     */
    public boolean doAccessibleAction(int i);
}
