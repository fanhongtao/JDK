/*
 * @(#)BeanContextEvent.java	1.4 98/03/18
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

/**
 * <p>
 * BeanContextEvent is the abstract root event class for all events emitted
 * from, and pertaining to the semantics of, a BeanContext.
 * </p>
 *
 * @author	Laurence P. G. Cable
 * @version	1.2
 * @since	JDK1.2
 * @see		java.beans.beancontext.BeanContext
 */

public abstract class BeanContextEvent extends EventObject {

    /**
     * Contruct a BeanContextEvent
     *
     * @param bc	The BeanContext source
     */

    protected BeanContextEvent(BeanContext bc) {
	super(bc);
    }

    /**
     *
     */

    public BeanContext getBeanContext() { return (BeanContext)getSource(); }

    /**
     * @param bc Set the BeanContext that last propagated this BeanContextEvent
     */

    public synchronized void setPropagatedFrom(BeanContext bc) {
	propagatedFrom = bc;
    }

    /**
     * @param bc The BeanContext that last propagated this BeanContextEvent
     */

    public synchronized BeanContext getPropagatedFrom() {
	return propagatedFrom;
    }

    /**
     * @return is the BeanContextEvent propagated?
     */

    public synchronized boolean isPropagated() {
	return propagatedFrom != null;
    }

    /*
     * fields
     */

    protected BeanContext propagatedFrom;
}
