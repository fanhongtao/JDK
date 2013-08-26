/*
 * @(#)SocketSecurityException.java	1.15 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * An obsolete subclass of {@link ExportException}.
 *
 * @version 1.15, 03/23/10
 * @author  Ann Wollrath
 * @since   JDK1.1
 **/
public class SocketSecurityException extends ExportException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -7622072999407781979L;

    /**
     * Constructs an <code>SocketSecurityException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     * @since JDK1.1
     */
    public SocketSecurityException(String s) {
	super(s);
    }

    /**
     * Constructs an <code>SocketSecurityException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message.
     * @param ex the nested exception
     * @since JDK1.1
     */
    public SocketSecurityException(String s, Exception ex) {
	super(s, ex);
    }

}
