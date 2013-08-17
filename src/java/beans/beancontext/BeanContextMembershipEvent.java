/*
 * @(#)BeanContextMembershipEvent.java	1.6 98/05/02
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

package java.beans.beancontext;

import java.util.EventObject;

import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * Compliant BeanContexts fire events on this interface when state maintained
 * by the BeanContext, for some or all of its "children", changes, to all
 * BeanContextListeners that register themselves with a particular BeanContext.
 * </p>
 *
 * @author	Laurence P. G. Cable
 * @version	1.6
 * @since	1.2
 * @see		java.beans.beancontext.BeanContext
 * @see		java.beans.beancontext.BeanContextEvent
 * @see		java.beans.beancontext.BeanContextListener
 */

public class BeanContextMembershipEvent extends BeanContextEvent {

    /**
     * Contruct a BeanContextMembershipEvent
     *
     * @param bc	The BeanContext source
     * @param changes	The Children effected
     */

    public BeanContextMembershipEvent(BeanContext bc, Collection changes) {
	super(bc);

	children = changes;
    }

    /**
     * Contruct a BeanContextMembershipEvent
     *
     * @param bc	The BeanContext source
     * @param changes	The Children effected
     */

    public BeanContextMembershipEvent(BeanContext bc, Object[] changes) {
	super(bc);

	children = Arrays.asList(changes);
    }

    /**
     * how many children are effected by the notification
     */

    public int size() { return children.size(); }

    /**
     * @return is the child specified effected by the event?
     */

    public boolean contains(Object child) {
	return children.contains(child);
    }

    /**
     * @return the array of children effected
     */

    public Object[] toArray() { return children.toArray(); }

    /**
     * @return the array of children effected
     */

    public Iterator iterator() { return children.iterator(); }

    /*
     * fields
     */

    protected Collection children;
}
