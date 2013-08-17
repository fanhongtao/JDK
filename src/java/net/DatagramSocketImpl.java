/*
 * @(#)DatagramSocketImpl.java	1.12 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Abstract datagram and multicast socket implementation base class.
 * @author Pavani Diwanji
 * @since  JDK1.1
 */

public abstract class DatagramSocketImpl implements SocketOptions {
    protected int localPort;

    /**
     * The file descriptor object
     * @since   JDK1.1
     */
    protected FileDescriptor fd;


    /**
     * Creates a datagram socket
     * @since   JDK1.1
     */
    protected abstract void create() throws SocketException;

    /**
     * Binds a datagram socket to a local port and address.
     * @since   JDK1.1
     */
    protected abstract void bind(int lport, InetAddress laddr) throws SocketException;
 
    /**
     * Sends a datagram packet. The packet contains the data and the
     * destination address to send the packet to.
     * @param packet to be sent.
     * @since   JDK1.1
     */
    protected abstract void send(DatagramPacket p) throws IOException;

    /**
     * Peek at the packet to see who it is from.
     * @param return the address which the packet came from.
     * @since   JDK1.1
     */
    protected abstract int peek(InetAddress i) throws IOException;

    /**
     * Receive the datagram packet.
     * @param Packet Received.
     * @since   JDK1.1
     */
    protected abstract void receive(DatagramPacket p) throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param TTL to be set.
     * @since   JDK1.1
     */
    protected abstract void setTTL(byte ttl) throws IOException;

    /**
     * Retrieve the TTL (time-to-live) option.
     * @since   JDK1.1
     */
    protected abstract byte getTTL() throws IOException;

    /**
     * Join the multicast group.
     * @param multicast address to join.
     * @since   JDK1.1
     */
    protected abstract void join(InetAddress inetaddr) throws IOException;

    /**
     * Leave the multicast group.
     * @param multicast address to leave.
     * @since   JDK1.1
     */
    protected abstract void leave(InetAddress inetaddr) throws IOException;

    /**
     * Close the socket.
     * @since   JDK1.1
     */
    protected abstract void close();

    /**
     * Get the local port.
     * @since   JDK1.1
     */
    protected int getLocalPort() {
	return localPort;
    }

    /**
     * Get the datagram socket file descriptor
     * @since   JDK1.1
     */
    protected FileDescriptor getFileDescriptor() {
	return fd;
    }
}
