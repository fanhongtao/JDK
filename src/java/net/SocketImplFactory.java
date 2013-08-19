/*
 * @(#)SocketImplFactory.java	1.17 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * This interface defines a factory for socket implementations. It
 * is used by the classes <code>Socket</code> and
 * <code>ServerSocket</code> to create actual socket
 * implementations.
 *
 * @author  Arthur van Hoff
 * @version 1.17, 01/23/03
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
