/*
 * @(#)SocketImplFactory.java	1.9 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.net;

/**
 * This interface defines a factory for socket implementations. It 
 * is used by the classes <code>Socket</code> and 
 * <code>ServerSocket</code> to create actual socket 
 * implementations. 
 *
 * @author  Arthur van Hoff
 * @version 1.9, 07/01/98
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
