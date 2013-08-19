/*
 * @(#)BeanDescriptor.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
     *
     * @param beanClass  The Class object of the Java class that implements
     *		the bean.  For example sun.beans.OurButton.class.
     */
    public BeanDescriptor(Class beanClass) {
	this(beanClass, null);
    }

    /**
     * Create a BeanDescriptor for a bean that has a customizer.
     *
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
     * Gets the bean's Class object.
     *
     * @return The Class object for the bean.
     */
    public Class getBeanClass() {
	return beanClass;
    }

    /**
     * Gets the Class object for the bean's customizer.
     *
     * @return The Class object for the bean's customizer.  This may
     * be null if the bean doesn't have a customizer.
     */
    public Class getCustomizerClass() {
	return customizerClass;
    }

    /*
     * Package-private dup constructor
     * This must isolate the new object from any changes to the old object.
     */
    BeanDescriptor(BeanDescriptor old) {
	super(old);
	beanClass = old.beanClass;
	customizerClass = old.customizerClass;
    }

    private Class beanClass;
    private Class customizerClass;

}
