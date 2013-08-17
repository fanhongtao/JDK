/*
 * @(#)BeanContextServiceRevokedEvent.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.beans.beancontext.BeanContextEvent;

import java.beans.beancontext.BeanContextServices;

/**
 * <p>
 * This event type is used by the BeanContextServicesListener in order to
 * identify the service being revoked.
 * </p>
 */

public class BeanContextServiceRevokedEvent extends BeanContextEvent {

    /**
     * construct a BeanContextServiceEvent
     */

    public BeanContextServiceRevokedEvent(BeanContextServices bcs, Class sc, boolean invalidate) {
	super((BeanContext)bcs);

	serviceClass    = sc;
	invalidateRefs  = invalidate;
    }

    /**
     * get the source as a reference of type BeanContextServices
     */

    public BeanContextServices getSourceAsBeanContextServices() {
	return (BeanContextServices)getBeanContext();
    }

    /**
     * get the service class that is the subject of this notification
     */

    public Class getServiceClass() { return serviceClass; }

    /**
     * test service equality
     */

    public boolean isServiceClass(Class service) {
	return serviceClass.equals(service);
    }

    /**
     * true if current service references are now invalidated and unusable.
     */

    public boolean isCurrentServiceInvalidNow() { return invalidateRefs; }

    /**
     * fields
     */

    protected Class			 serviceClass;
    private   boolean			 invalidateRefs;
}
