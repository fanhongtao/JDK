/*
 * @(#)SocketImplFactory.java	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
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
 * @version 1.10, 12/10/01
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
     * @see     java.io.SocketImpl
     * @since   JDK1.0
     */
    SocketImpl createSocketImpl();
}
