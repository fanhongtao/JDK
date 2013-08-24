/*
 * @(#)GetPropertyAction.java	1.10 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import java.security.PrivilegedAction;

/**
 * Utility class to be used by the method <tt>AccessControler.doPrivileged</tt>
 * to get a system property.
 *
 * @since 1.5
 */
public class GetPropertyAction implements PrivilegedAction<String> {
    private final String key;

    public GetPropertyAction(String key) {
	this.key = key;
    }

    public String run() {
	return System.getProperty(key);
    }
}
