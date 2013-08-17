/*
 * @(#)BeanDescriptor.java	1.11 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

/**
 * A BeanDescriptor provides global information about a "bean",
 * including its Java class, its displayName, etc.
 * <p>
 * This is one of the kinds of descriptor returned by a BeanInfo object,
 * which also returns descriptors for properties, method, and events.
 */

public class BeanDescriptor extends FeatureDescriptor {

    /**
     * Create a BeanDescriptor for a bean that doesn't have a customizer.
     * @param beanClass  The Class object of the Java class that implements
     *		the bean.  For example sun.beans.OurButton.class.
     */
    public BeanDescriptor(Class beanClass) {
	this(beanClass, null);
    }

    /**
     * Create a BeanDescriptor for a bean that has a customizer.
     * @param beanClass  The Class object of the Java class that implements
     *		the bean.  For example sun.beans.OurButton.class.
     * @param customizerClass  The Class object of the Java class that implements
     *		the bean's Customizer.  For example sun.beans.OurButtonCustomizer.class.
     */
    public BeanDescriptor(Class beanClass, Class customizerClass) {
	this.beanClass = beanClass;
	this.customizerClass = customizerClass;
	String name = beanClass.getName();
	while (name.indexOf('.') >= 0) {
	    name = name.substring(name.indexOf('.')+1);
	}
	setName(name);
    }

    /**
     * @return The Class object for the bean.
     */
    public Class getBeanClass() {
	return beanClass;
    }

    /**
     * @return The Class object for the bean's customizer.  This may
     * be null if the bean doesn't have a customizer.
     */
    public Class getCustomizerClass() {
	return customizerClass;
    }

    private Class beanClass;
    private Class customizerClass;

}
