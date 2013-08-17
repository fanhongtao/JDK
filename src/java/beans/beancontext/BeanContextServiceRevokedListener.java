/*
 * @(#)BeanContextServiceRevokedListener.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
