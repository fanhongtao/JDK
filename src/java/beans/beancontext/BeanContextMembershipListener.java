/*
 * @(#)BeanContextMembershipListener.java	1.5 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans.beancontext;

import java.beans.beancontext.BeanContextMembershipEvent;

import java.util.EventListener;

/**
 * <p>
 * Compliant BeanContexts fire events on this interface when the state of
 * the membership of the BeanContext changes.
 * </p>
 *
 * @author	Laurence P. G. Cable
 * @version	1.5
 * @since	JDK1.2
 * @see		java.beans.beancontext.BeanContext
 */

public interface BeanContextMembershipListener extends EventListener {

    /**
     * @param bcme The BeanContextMembershipEvent describing the change that occurred.
     */

    void childrenAdded(BeanContextMembershipEvent bcme);

    /**
     * @param bcme The BeanContextMembershipEvent describing the change that occurred.
     */

    void childrenRemoved(BeanContextMembershipEvent bcme);
}
