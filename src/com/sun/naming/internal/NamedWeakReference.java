/*
 * @(#)NamedWeakReference.java	1.6 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.naming.internal;


/**
 * A NamedWeakReference is a WeakReference with an immutable string name.
 *
 * @author Scott Seligman
 * @version 1.6 10/03/23
 */


class NamedWeakReference extends java.lang.ref.WeakReference {

    private final String name;

    NamedWeakReference(Object referent, String name) {
	super(referent);
	this.name = name;
    }

    String getName() {
	return name;
    }
}
