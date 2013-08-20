/*
 * @(#)GetPropertyAction.java	1.8 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
public class GetPropertyAction implements PrivilegedAction {
    private final String key;

    public GetPropertyAction(String key) {
	this.key = key;
    }

    public Object run() {
	return System.getProperty(key);
    }
}
