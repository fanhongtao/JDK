/*
 * @(#)BeanContextServices.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.util.Iterator;

import java.util.TooManyListenersException;

import java.beans.beancontext.BeanContext;

import java.beans.beancontext.BeanContextServiceProvider;

import java.beans.beancontext.BeanContextServicesListener;


/**
 * <p>
 * The BeanContextServices interface provides a mechanism for a BeanContext
 * to expose generic "services" to the BeanContextChild objects within.
 * </p>
 */

public interface BeanContextServices extends BeanContext, BeanContextServicesListener {

    /**
     * add a service to this BeanContext
     */
   
    boolean addService(Class serviceClass, BeanContextServiceProvider serviceProvider);

    /**
     * remove a service from this BeanContext
     */
 
    void revokeService(Class serviceClass, BeanContextServiceProvider serviceProvider, boolean revokeCurrentServicesNow);

    /**
     * @return true iff the service is available.
     */

    boolean hasService(Class serviceClass);

    /**
     * @return a reference to this context's named Service as requested or null
     */

    Object getService(BeanContextChild child, Object requestor, Class serviceClass, Object serviceSelector, BeanContextServiceRevokedListener bcsrl) throws TooManyListenersException;

    /**
     * release the service reference
     */

    void releaseService(BeanContextChild child, Object requestor, Object service); 

    /**
     * return the currently available services
     */

    Iterator getCurrentServiceClasses();

    /**
     * @return the currently available service selectors for the named serviceClass
     */

    Iterator getCurrentServiceSelectors(Class serviceClass);

    /**
     * add a BeanContextServicesListener to this BeanContext
     */

    void addBeanContextServicesListener(BeanContextServicesListener bcsl);

    /**
     * remove a BeanContextServicesListener from this BeanContext
     */

    void removeBeanContextServicesListener(BeanContextServicesListener bcsl);
}
