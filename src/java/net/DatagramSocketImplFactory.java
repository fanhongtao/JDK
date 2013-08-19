/*
 * @(#)DatagramSocketImplFactory.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * This interface defines a factory for datagram socket implementations. It
 * is used by the classes <code>DatagramSocket</code> to create actual socket
 * implementations.
 *
 * @author  Yingxian Wang
 * @version %I %E
 * @see     java.net.DatagramSocket
 * @since   1.3
 */
public
interface DatagramSocketImplFactory {
    /**
     * Creates a new <code>DatagramSocketImpl</code> instance.
     *
     * @return  a new instance of <code>DatagramSocketImpl</code>.
     * @see     java.net.DatagramSocketImpl
     */
    DatagramSocketImpl createDatagramSocketImpl();
}
