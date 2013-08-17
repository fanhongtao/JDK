/*
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

    /**
     * The local port number.
     */   
    protected int localPort;

    /**
     * The file descriptor object.
     */
    protected FileDescriptor fd;


    /**
     * Creates a datagram socket.
     * @exception SocketException if there is an error in the 
     * underlying protocol, such as a TCP error. 
     */
    protected abstract void create() throws SocketException;

    /**
     * Binds a datagram socket to a local port and address.
     * @param lport the local port
     * @param laddr the local address
     * @exception SocketException if there is an error in the
     * underlying protocol, such as a TCP error.
     */
    protected abstract void bind(int lport, InetAddress laddr) throws SocketException;

    /**
     * Sends a datagram packet. The packet contains the data and the
     * destination address to send the packet to.
     * @param p the packet to be sent.
     * @exception IOException if an I/O exception occurs while sending the 
     * datagram packet.
     */
    protected abstract void send(DatagramPacket p) throws IOException;

    /**
     * Peek at the packet to see who it is from.
     * @param i an InetAddress object 
     * @return the address which the packet came from.
     * @exception IOException if an I/O exception occurs
     */
    protected abstract int peek(InetAddress i) throws IOException;

    /**
     * Receive the datagram packet.
     * @param p the Packet Received.
     * @exception IOException if an I/O exception occurs
     * while receiving the datagram packet.
     */
    protected abstract void receive(DatagramPacket p) throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param ttl a byte specifying the TTL value
     *
     * @deprecated use setTimeToLive instead.
     * @exception IOException if an I/O exception occurs while setting
     * the time-to-live option.
     * @see #getTTL()
     */
    protected abstract void setTTL(byte ttl) throws IOException;

    /**
     * Retrieve the TTL (time-to-live) option.
     *
     * @exception IOException if an I/O exception occurs
     * while retrieving the time-to-live option
     * @deprecated use getTimeToLive instead.
     * @return a byte representing the TTL value
     * @see #setTTL(byte)
     */
    protected abstract byte getTTL() throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param ttl an <tt>int</tt> specifying the time-to-live value
     * @exception IOException if an I/O exception occurs
     * while setting the time-to-live option.
     * @see #getTimeToLive()
     */
    protected abstract void setTimeToLive(int ttl) throws IOException;

    /**
     * Retrieve the TTL (time-to-live) option.
     * @exception IOException if an I/O exception occurs
     * while retrieving the time-to-live option
     * @return an <tt>int</tt> representing the time-to-live value
     * @see #setTimeToLive(int)
     */
    protected abstract int getTimeToLive() throws IOException;

    /**
     * Join the multicast group.
     * @param inetaddr multicast address to join.
     * @exception IOException if an I/O exception occurs
     * while joining the multicast group.
     */
    protected abstract void join(InetAddress inetaddr) throws IOException;

    /**
     * Leave the multicast group.
     * @param inetaddr multicast address to leave.
     * @exception IOException if an I/O exception occurs
     * while leaving the multicast group.
     */
    protected abstract void leave(InetAddress inetaddr) throws IOException;

    /**
     * Close the socket.
     */
    protected abstract void close();

    /**
     * Gets the local port.
     * @return an <tt>int</tt> representing the local port value
     */
    protected int getLocalPort() {
	return localPort;
    }

    /**
     * Gets the datagram socket file descriptor.
     * @return a <tt>FileDescriptor</tt> object representing the datagram socket
     * file descriptor
     */
    protected FileDescriptor getFileDescriptor() {
	return fd;
    }
}
