/*
 * @(#)SimpleBeanInfo.java	1.18 98/07/01
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
 * This is a support class to make it easier for people to provide
 * BeanInfo classes.
 * <p>
 * It defaults to providing "noop" information, and can be selectively
 * overriden to provide more explicit information on chosen topics.
 * When the introspector sees the "noop" values, it will apply low
 * level introspection and design patterns to automatically analyze
 * the target bean.
 */

public class SimpleBeanInfo implements BeanInfo {

    /**
     * Deny knowledge about the class and customizer of the bean.
     * You can override this if you wish to provide explicit info.
     */
    public BeanDescriptor getBeanDescriptor() {
	return null;
    }

    /**
     * Deny knowledge of properties. You can override this
     * if you wish to provide explicit property info.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
	return null;
    }

    /**
     * Deny knowledge of a default property. You can override this
     * if you wish to define a default property for the bean.
     */
    public int getDefaultPropertyIndex() {
	return -1;
    }

    /**
     * Deny knowledge of event sets. You can override this
     * if you wish to provide explicit event set info.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
	return null;
    }

    /**
     * Deny knowledge of a default event. You can override this
     * if you wish to define a default event for the bean.
     */
    public int getDefaultEventIndex() {
	return -1;
    }

    /**
     * Deny knowledge of methods. You can override this
     * if you wish to provide explicit method info.
     */
    public MethodDescriptor[] getMethodDescriptors() {
	return null;
    }

    /**
     * Claim there are no other relevant BeanInfo objects.  You
     * may override this if you want to (for example) return a
     * BeanInfo for a base class.
     */
    public BeanInfo[] getAdditionalBeanInfo() {
	return null;
    }

    /**
     * Claim there are no icons available.  You can override
     * this if you want to provide icons for your bean.
     */
    public java.awt.Image getIcon(int iconKind) {
	return null;
    }

    /**
     * This is a utility method to help in loading icon images.
     * It takes the name of a resource file associated with the
     * current object's class file and loads an image object
     * from that file.  Typically images will be GIFs.
     * <p>
     * @param resourceName  A pathname relative to the directory
     *		holding the class file of the current class.  For example,
     *		"wombat.gif".
     * @return  an image object.  May be null if the load failed.
     */
    public java.awt.Image loadImage(String resourceName) {
	try {
	    java.net.URL url = getClass().getResource(resourceName);
	    java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
	    return tk.createImage((java.awt.image.ImageProducer) url.getContent());
	} catch (Exception ex) {
	    return null;
	}
    }

}
