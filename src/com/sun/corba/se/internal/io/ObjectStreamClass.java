/*
 * @(#)ObjectStreamClass.java	1.4 05/11/17
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.io;

public class ObjectStreamClass {

    /* Find out if the class has a static class initializer <clinit> */
    private static native boolean hasStaticInitializer(Class cl);

}
