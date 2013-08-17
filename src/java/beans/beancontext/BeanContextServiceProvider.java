/*
 * @(#)BeanContextServiceProvider.java	1.3 98/03/18
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

import java.util.Iterator;

/**
 * <p>
 * One of the primary functions of a BeanContext is to act a as rendezvous 
 * between JavaBeans, and BeanContextServiceProviders.
 * </p>
 * <p>
 * A JavaBean nested within a BeanContext, may ask that BeanContext to 
 * provide an instance of a "service", based upon a reference to a Java
 * Class object that represents that service.
 * </p>
 * <p>
 * If such a service has been registered with the context, or one of its 
 * nesting context's, in the case where a context delegate to its context
 * to satisfy a service request, then the BeanContextServiceProvider associated with 
 * the service is asked to provide an instance of that service.
 * </p>
 * <p>
 * The ServcieProvider may always return the same instance, or it may
 * construct a new instance for each request.
 * </p>
 */

public interface BeanContextServiceProvider {

   /**
    * request an instance of a service, 
    *
    * @param requestor	 	The object requesting the service
    * @param serviceClass	The service requested
    * @param serviceSelector	Additional parameterisation of the service 
    */

    Object getService(BeanContextServices bcs, Object requestor, Class serviceClass, Object serviceSelector);

    /**
     * release the service
     */

    public void releaseService(BeanContextServices bcs, Object requestor, Object service);

    /**
     * @return the current service selectors for the specified serviceClass
     */

    Iterator getCurrentServiceSelectors(BeanContextServices bcs, Class serviceClass);
}
