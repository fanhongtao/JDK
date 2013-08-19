/*
 * @(#)BeanContextServiceProviderBeanInfo.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans.beancontext;

import java.beans.BeanInfo;

/**
 * A BeanContextServiceProvider implementor who wishes to provide explicit
 * information about the services their bean may provide shall implement a
 * BeanInfo class that implements this BeanInfo subinterface and provides
 * explicit information about the methods, properties, events, etc, of their
 * services.
 */

public interface BeanContextServiceProviderBeanInfo extends BeanInfo {

    /**
     * Gets a <code>BeanInfo</code> array, one for each 
     * service class or interface statically available 
     * from this ServiceProvider. 
     * @return the <code>BeanInfo</code> array
     */
    BeanInfo[] getServicesBeanInfo();
}
