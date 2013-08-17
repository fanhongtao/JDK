/*
 * @(#)MarshalException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi;

public class MarshalException extends RemoteException {

    private static final long serialVersionUID = 6223554758134037936L;

    /**
     * Create a new marshal exception with a descriptive string.
     */
    public MarshalException(String s) {
	super(s);
    }

    /**
     * Create a new marshal exception with a descriptive string and an
     * exception.
     */
    public MarshalException(String s, Exception ex) {
	super(s, ex);
    }
}
