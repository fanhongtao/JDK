/*
 * @(#)SecureClassLoaderRepository.java	1.10 05/05/27
 * 
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.jmx.mbeanserver;

import javax.management.loading.ClassLoaderRepository;

/** 
 * Fix security hole in ClassLoaderRepository. This class wraps
 * the actual ClassLoaderRepository implementation so that
 * only the methods from {@link javax.management.loading.ClassLoaderRepository}
 * can be accessed (read-only).
 *
 * @since 1.5
 */
final class SecureClassLoaderRepository 
    implements ClassLoaderRepository {

    private final ClassLoaderRepository clr;
    /**
     * Creates a new secure ClassLoaderRepository wrapping an
     * unsecure implementation.
     * @param clr Unsecure {@link ClassLoaderRepository} implementation
     *            to wrap.
     **/
    public SecureClassLoaderRepository(ClassLoaderRepository clr) {
	this.clr=clr;
    }
    public final Class loadClass(String className) 
	throws ClassNotFoundException {
	return clr.loadClass(className);
    }
    public final Class loadClassWithout(ClassLoader loader,
				  String className) 
	throws ClassNotFoundException {
	return clr.loadClassWithout(loader,className);
    }
    public final Class loadClassBefore(ClassLoader loader,
				 String className) 
	throws ClassNotFoundException {
	return clr.loadClassBefore(loader,className);
    }
}
