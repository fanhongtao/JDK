/*
 * @(#)ORBClassLoader.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.impl.orbutil;

/**
 * Based on feedback from bug report 4452016, all class loading
 * in the ORB is isolated here.  It is acceptable to use
 * Class.forName only when one is certain that the desired class
 * should come from the core JDK.
 */
public class ORBClassLoader
{
    public static Class loadClass(String className) 
        throws ClassNotFoundException
    {
        return ORBClassLoader.getClassLoader().loadClass(className);
    }

    public static ClassLoader getClassLoader() {
        if (Thread.currentThread().getContextClassLoader() != null)
            return Thread.currentThread().getContextClassLoader();
        else
            return ClassLoader.getSystemClassLoader();
    }
}
