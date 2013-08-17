/*
 * @(#)BeanContextServiceRevokedListener.java	1.3 98/03/18
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
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

import java.beans.beancontext.BeanContextServiceRevokedEvent;

import java.util.EventListener;

public interface BeanContextServiceRevokedListener extends EventListener {

    /**
     * The service named has been revoked. getService requests for
     * this service will no longer be satisifed.
     */

    void serviceRevoked(BeanContextServiceRevokedEvent bcsre);
}
