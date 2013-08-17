/*
 * @(#)BeanContext.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.beans.beancontext;

import java.beans.DesignMode;
import java.beans.Visibility;

import java.io.InputStream;
import java.io.IOException;

import java.net.URL;

import java.util.Collection;
import java.util.Locale;

/**
 * <p>
 * The BeanContext acts a logical heirarchical container for JavaBeans.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.9
 * @since JDK1.2
 *
 * @seealso java.beans.Beans
 * @seealso java.beans.beancontext.BeanContextChild
 * @seealso java.beans.beancontext.BeanContextListener
 * @seealso java.beans.PropertyChangeEvent
 * @seealso java.beans.VetoableChangeEvent
 * @seealso java.beans.DesignMode
 * @seealso java.beans.Visibility
 * @seealso java.util.Collection
 */

public interface BeanContext extends BeanContextChild, Collection, DesignMode, Visibility {

    /**
     * Instantiate the javaBean named as a child of this BeanContext
     *
     * @param beanName The name of the JavaBean to instantiate as a child of this BeanContext
     */

    Object instantiateChild(String beanName) throws IOException, ClassNotFoundException;

    /**
     * @return an InputStream to the named resource for the specified child
     *
     * @throw IllegalArgumentException
     */

    InputStream getResourceAsStream(String name, BeanContextChild bcc) throws IllegalArgumentException;

    /**
     * @return a URL for the named resource for the specified child
     *
     * @throw IllegalArgumentException
     */

    URL getResource(String name, BeanContextChild bcc) throws IllegalArgumentException;

     /**
      * add a BeanContextMembershipListener
      */

    void addBeanContextMembershipListener(BeanContextMembershipListener bcml);

     /**
      * remove a BeanContextMembershipListener
      */

    void removeBeanContextMembershipListener(BeanContextMembershipListener bcml);

    /**
     * this global lock is used by both BeanContext and BeanContextServices
     * implementors to serialize changes in a BeanContext hierarchy and
     * any service requests etc.
     */

    public static final Object globalHierarchyLock = new Object();
}
