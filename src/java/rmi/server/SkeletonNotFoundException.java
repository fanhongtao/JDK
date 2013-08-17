/*
 * @(#)SkeletonNotFoundException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

import java.rmi.RemoteException;

public class SkeletonNotFoundException extends RemoteException {

    private static final long serialVersionUID = -7860299673822761231L;

    /**
     * Create a new SkeletonNotFoundException exception with a descriptive string.
     */
    public SkeletonNotFoundException(String s) {
	super(s);
    }

    /**
     * Create a new SkeletonNotFoundException with a descriptive string and an exception.
     */
    public SkeletonNotFoundException(String s, Exception ex) {
	super(s, ex);
    }
}
