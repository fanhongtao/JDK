/*
 * @(#)ClassLoaderWithRepository.java	1.7 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.remote.util;

import javax.management.loading.ClassLoaderRepository;

public class ClassLoaderWithRepository extends ClassLoader {
    public ClassLoaderWithRepository(ClassLoaderRepository clr, 
				     ClassLoader cl2) {

	if (clr == null) throw new 
	    IllegalArgumentException("Null ClassLoaderRepository object.");

	repository = clr;
	this.cl2 = cl2;
   }

    protected Class findClass(String name) throws ClassNotFoundException {
	try {
	    return repository.loadClass(name);
	} catch (ClassNotFoundException cne) {
	    if (cl2 != null) {
		return cl2.loadClass(name);
	    } else {
		throw cne;
	    }
	}
    }

    private ClassLoaderRepository repository;
    private ClassLoader cl2;
}
