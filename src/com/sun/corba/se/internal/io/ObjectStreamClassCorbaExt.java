/*
 * @(#)ObjectStreamClassCorbaExt.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.io;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedAction;

import java.lang.reflect.Modifier;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;


// This file contains some utility methods that
// originally were in the OSC in the RMI-IIOP
// code delivered by IBM.  They don't make
// sense there, and hence have been put
// here so that they can be factored out in
// an attempt to eliminate redundant code from
// ObjectStreamClass.  Eventually the goal is
// to move to java.io.ObjectStreamClass, and
// java.io.ObjectStreamField.

// class is package private for security reasons

class ObjectStreamClassCorbaExt {

    static final boolean isAbstractInterface(Class cl) {

        Method[] method = ObjectStreamClassCorbaExt.getDeclaredMethods(cl);
        // Test for abstractness (used under rmi/iiop when determining whether
        // to call read/write_Abstract

        if (!cl.isInterface()) {
            return false;
        }

        if (method.length == 0) {
            return false;
        }

        boolean isAbstractInterface = false;

        for (int im = method.length -1 ; (im > -1); im--) {
            Class exceptions[] = method[im].getExceptionTypes();
            if (exceptions.length == 0) {
                return false;
            }
               
            // Set abstractness to false and flip to true only if the 
	    // method has at least one good exception

            for (int ime = exceptions.length -1; (ime > -1) && (isAbstractInterface == false); ime--) {
                if ((java.rmi.RemoteException.class == exceptions[ime]) ||
                    (java.lang.Throwable.class == exceptions[ime]) ||
                    (java.lang.Exception.class == exceptions[ime]) ||
                    (java.io.IOException.class == exceptions[ime])) {
                    isAbstractInterface = true;
                }
            }
        }

	Class superclass = cl.getSuperclass();

        isAbstractInterface = (isAbstractInterface && ((superclass == null) || ( ObjectStreamClassCorbaExt.isAbstractInterface(superclass))));
	return isAbstractInterface;

    }

    /*
     *  Returns TRUE if type is 'any'.
     */
    static final boolean isAny(String typeString) {

	int isAny = 0;

	if ( (typeString != null) &&
	    (typeString.equals("Ljava/lang/Object;") ||
	     typeString.equals("Ljava/io/Serializable;") ||
	     typeString.equals("Ljava/io/Externalizable;")) )
                isAny = 1;

        return (isAny==1);
    }

    private static final Method[] getDeclaredMethods(final Class clz) {
        return (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return clz.getDeclaredMethods();
            }
        });
    }

}
