/*
 * @(#)SocketImplFactory.java	1.15 00/02/02
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.net;

/**
 * This interface defines a factory for socket implementations. It
 * is used by the classes <code>Socket</code> and
 * <code>ServerSocket</code> to create actual socket
 * implementations.
 *
 * @author  Arthur van Hoff
 * @version 1.15, 02/02/00
 * @see     java.net.Socket
 * @see     java.net.ServerSocket
 * @since   JDK1.0
 */
public
interface SocketImplFactory {
    /**
     * Creates a new <code>SocketImpl</code> instance.
     *
     * @return  a new instance of <code>SocketImpl</code>.
     * @see     java.net.SocketImpl
     */
    SocketImpl createSocketImpl();
}
