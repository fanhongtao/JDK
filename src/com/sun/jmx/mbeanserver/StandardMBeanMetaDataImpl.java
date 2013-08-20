/*
 * @(#)StandardMBeanMetaDataImpl.java	1.2 05/05/27
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import javax.management.MBeanInfo;
import javax.management.StandardMBean;

/**
 * Override StandardMetaDataImpl in order to redefine the caching
 * of MBean Information in StandardMBean.
 *
 * @since 1.6
 */
public final class StandardMBeanMetaDataImpl extends StandardMetaDataImpl {

    private final StandardMBean mbean;

    /**
     * Constructor.
     */
    public StandardMBeanMetaDataImpl(StandardMBean mbean) {
        super(false);
        this.mbean = mbean;
    }

    /**
     * We need to override this method because some methods
     * from BaseMetaDataImpl rely on MetaData#getMBeanInfo().
     * <p>
     * The default caching implemented in StandardMetaDataImpl
     * will not work if two instances of class <var>c</var>
     * can have different management interfaces, which is
     * made possible by {@link javax.management.StandardMBean}.
     *
     * @return mbean.getMBeanInfo();
     */
    MBeanInfo getCachedMBeanInfo(Class beanClass) {

        if (beanClass == null) return null;

        // Need the synchronized block as long as implementation
        // and mbeanInterface are not final.
        //
        synchronized (mbean) {
            // Consistency checking: beanClass must be equal
            // to mbean.getImplementationClass().
            //
            final Class implementationClass =
                mbean.getImplementationClass();
            if (implementationClass == null) return null;
            if (!beanClass.equals(implementationClass)) return null;

            // Should always come here (null cases excepted)...
            //
            return mbean.getMBeanInfo();
        }
    }

    /**
     * We need to override this method because some methods
     * from StandardMetaDataImpl rely on it.
     * <p>
     * The default caching implemented in StandardMetaDataImpl
     * will not work if two instances of class <var>c</var>
     * can have different management interfaces, which is
     * made possible by {@link javax.management.StandardMBean}.
     *
     * @return mbean.getMBeanInterface();
     */
    Class getCachedMBeanInterface(Class beanClass) {
        // Need the synchronized block as long as implementation
        // and mbeanInterface are not final.
        //
        synchronized (mbean) {
            // Consistency checking: beanClass must be equal
            // to mbean.getImplementationClass().
            //
            final Class implementationClass =
                mbean.getImplementationClass();
            if (implementationClass == null) return null;
            if (!beanClass.equals(implementationClass)) return null;

            // Should always come here (null cases excepted)...
            //
            return mbean.getMBeanInterface();
        }
    }

    /**
     * Need to override this method because default caching implemented
     * in StandardMetaDataImpl will not work if two instances of class
     * <var>c</var> can have different <var>mbeanInterface</var>.
     * <p>
     * The default caching mechanism in StandardMetaDataImpl uses
     * class static {@link java.util.WeakHashMap WeakHashMaps} - and
     * is common to all instance of StandardMetaData - hence to
     * all MBeanServer.
     * <p>
     * As this default mechanism might not always work for
     * StandardMBean objects (may have several instances of class
     * <var>c</var> with different MBean interfaces), we disable
     * this default caching by defining an empty
     * <code>cacheMBeanInfo()</code> method.
     * <p>
     * Caching in our case is no longer performed by the MetaData
     * object, but by the StandardMBean object.
     */
    void cacheMBeanInfo(Class c, Class mbeanInterface, MBeanInfo mbeanInfo) {
    }
}
