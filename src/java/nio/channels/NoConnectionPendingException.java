/*
 * @(#)NoConnectionPendingException.java	1.9 01/11/19
 *
 * Copyright 2000 by Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio.channels;


/**
 * Unchecked exception thrown when the {@link SocketChannel#finishConnect
 * finishConnect} method of a {@link SocketChannel} is invoked without first
 * successfully invoking its {@link SocketChannel#connect connect} method.
 *
 * @version 1.9, 01/11/19
 * @since 1.4
 */

public class NoConnectionPendingException
    extends IllegalStateException
{

    /**
     * Constructs an instance of this class.
     */
    public NoConnectionPendingException() { }

}
