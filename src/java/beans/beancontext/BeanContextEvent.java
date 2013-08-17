/*
 * @(#)BeanContextEvent.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
