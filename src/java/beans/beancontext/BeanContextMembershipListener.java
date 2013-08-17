/*
 * @(#)BeanContextMembershipListener.java	1.2 00/01/12
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
 * @version	1.4
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
