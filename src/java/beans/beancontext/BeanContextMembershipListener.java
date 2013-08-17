/*
 * @(#)BeanContextMembershipListener.java	1.9 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * @version	1.9, 02/02/00
 * @since	1.2
 * @see		java.beans.beancontext.BeanContext
 */

public interface BeanContextMembershipListener extends EventListener {

    /**
     * Called when a child or list of children is added to a 
     * <code>BeanContext</code> that this listener is registered with.
     * @param bcme The <code>BeanContextMembershipEvent</code> 
     * describing the change that occurred.
     */
    void childrenAdded(BeanContextMembershipEvent bcme);

    /**
     * Called when a child or list of children is removed 
     * from a <code>BeanContext</code> that this listener 
     * is registered with.
     * @param bcme The <code>BeanContextMembershipEvent</code> 
     * describing the change that occurred.
     */
    void childrenRemoved(BeanContextMembershipEvent bcme);
}
