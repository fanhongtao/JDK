/*
 * @(#)SkeletonNotFoundException.java	1.16 04/05/18
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

import java.rmi.RemoteException;

/**
 * A <code>SkeletonNotFoundException</code> is thrown if the
 * <code>Skeleton</code> corresponding to the remote object being exported is
 * not found.  Skeletons are not required in the Java 2 platform, so this exception is
 * never thrown.
 * 
 * @version 1.16, 05/18/04
 * @since   JDK1.1
 * @deprecated no replacement.  Skeletons are no longer required for remote
 * method calls in the Java 2 platform v1.2 and greater.
 */
@Deprecated
public class SkeletonNotFoundException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -7860299673822761231L;

    /**
     * Constructs a <code>SkeletonNotFoundException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @since JDK1.1
     */
    public SkeletonNotFoundException(String s) {
	super(s);
    }

    /**
     * Constructs a <code>SkeletonNotFoundException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message.
     * @param ex the nested exception
     * @since JDK1.1
     */
    public SkeletonNotFoundException(String s, Exception ex) {
	super(s, ex);
    }
}
