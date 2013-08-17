/*
 * @(#)BeanContextMembershipListener.java	1.4 98/03/18
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
