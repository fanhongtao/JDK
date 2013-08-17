/*
 * @(#)BeanContextServiceAvailableEvent.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextEvent;

import java.beans.beancontext.BeanContextServices;

import java.util.Iterator;

/**
 * <p>
 * This event type is used by the BeanContextServicesListener in order to
 * identify the service being registered.
 * </p>
 */

public class BeanContextServiceAvailableEvent extends BeanContextEvent {

    /**
     * construct a BeanContextServiceEvent
     */

    public BeanContextServiceAvailableEvent(BeanContextServices bcs, Class sc) {
	super((BeanContext)bcs);

	serviceClass = sc;
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
     * @return the current selectors available from the service
     */

    public Iterator getCurrentServiceSelectors() {
    	return ((BeanContextServices)getSource()).getCurrentServiceSelectors(serviceClass);
    }

    /*
     * fields
     */

    protected Class			 serviceClass;
}
