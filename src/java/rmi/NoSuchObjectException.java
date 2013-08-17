/*
 * @(#)NoSuchObjectException.java	1.7 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.rmi;

/**
 * A <code>NoSuchObjectException</code> is thrown if an attempt is made to
 * invoke a method on an object that no longer exists in the remote virtual
 * machine.  If a <code>NoSuchObjectException</code> occurs attempting to
 * invoke a method on a remote object, the call may be retransmitted and still
 * preserve RMI's "at most once" call semantics.
 *
 * A <code>NoSuchObjectException</code> is also thrown by the method
 * <code>java.rmi.server.RemoteObject.toStub</code> and by the
 * <code>unexportObject</code> methods of
 * <code>java.rmi.server.UnicastRemoteObject</code> and
 * <code>java.rmi.activation.Activatable</code> and
 * 
 * @version 1.7, 09/21/98
 * @author  Ann Wollrath
 * @since   JDK1.1
 * @see     java.rmi.server.RemoteObject#toStub(Remote)
 * @see     java.rmi.server.UnicastRemoteObject#unexportObject(Remote,boolean)
 * @see     java.rmi.activation.Activatable#unexportObject(Remote,boolean)
 */
public class NoSuchObjectException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = 6619395951570472985L;

    /**
     * Constructs a <code>NoSuchObjectException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     * @since   JDK1.1
     */
    public NoSuchObjectException(String s) {
	super(s);
    }
}
