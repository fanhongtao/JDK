/*
 * @(#)DatagramSocketImpl.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
     */
    protected FileDescriptor fd;


    /**
     * Creates a datagram socket
     */
    protected abstract void create() throws SocketException;

    /**
     * Binds a datagram socket to a local port and address.
     */
    protected abstract void bind(int lport, InetAddress laddr) throws SocketException;

    /**
     * Sends a datagram packet. The packet contains the data and the
     * destination address to send the packet to.
     * @param packet to be sent.
     */
    protected abstract void send(DatagramPacket p) throws IOException;

    /**
     * Peek at the packet to see who it is from.
     * @param return the address which the packet came from.
     */
    protected abstract int peek(InetAddress i) throws IOException;

    /**
     * Receive the datagram packet.
     * @param Packet Received.
     */
    protected abstract void receive(DatagramPacket p) throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param TTL to be set.
     *
     * @deprecated use setTimeToLive instead.
     */
    protected abstract void setTTL(byte ttl) throws IOException;

    /**
     * Retrieve the TTL (time-to-live) option.
     *
     * @deprecated use getTimeToLive instead.
     */
    protected abstract byte getTTL() throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param TTL to be set.
     */
    protected abstract void setTimeToLive(int ttl) throws IOException;

    /**
     * Retrieve the TTL (time-to-live) option.
     */
    protected abstract int getTimeToLive() throws IOException;

    /**
     * Join the multicast group.
     * @param multicast address to join.
     */
    protected abstract void join(InetAddress inetaddr) throws IOException;

    /**
     * Leave the multicast group.
     * @param multicast address to leave.
     */
    protected abstract void leave(InetAddress inetaddr) throws IOException;

    /**
     * Close the socket.
     */
    protected abstract void close();

    /**
     * Get the local port.
     */
    protected int getLocalPort() {
	return localPort;
    }

    /**
     * Get the datagram socket file descriptor
     */
    protected FileDescriptor getFileDescriptor() {
	return fd;
    }
}
