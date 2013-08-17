/*
 * @(#)ServerNotActiveException.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

public class ServerNotActiveException extends java.lang.Exception {

    private static final long serialVersionUID = 4687940720827538231L;

    /**
     * Create a new exception.
     */
    public ServerNotActiveException() {}

    public ServerNotActiveException(String s) 
    {
	super(s);
    }
}
