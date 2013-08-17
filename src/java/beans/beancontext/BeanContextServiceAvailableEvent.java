/*
 * @(#)BeanContextServiceAvailableEvent.java	1.3 98/03/18
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
