/*
 * @(#)BeanContextServiceRevokedListener.java	1.6 00/02/02
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

/**
 *  The listener interface for receiving 
 * <code>BeanContextServiceRevokedEvent</code> objects. A class that is 
 * interested in processing a <code>BeanContextServiceRevokedEvent</code>
 * implements this interface. 
 */
public interface BeanContextServiceRevokedListener extends EventListener {

    /**
     * The service named has been revoked. getService requests for
     * this service will no longer be satisifed.
     * @param bcsre the <code>BeanContextServiceRevokedEvent</code> received 
     * by this listener. 
     */
    void serviceRevoked(BeanContextServiceRevokedEvent bcsre);
}



