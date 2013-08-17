/*
 * @(#)DatagramSocket.java	1.26 98/07/01
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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * This class represents a socket for sending and receiving datagram packets.
 * <p>
 * A datagram socket is the sending or receiving point for a 
 * connectionless packet delivery service. Each packet sent or 
 * received on a datagram socket is individually addressed and 
 * routed. Multiple packets sent from one machine to another may be 
 * routed differently, and may arrive in any order. 
 *
 * @author  Pavani Diwanji
 * @version 1.26, 07/01/98
 * @see     java.net.DatagramPacket
 * @since   JDK1.0
*/
public
class DatagramSocket {
    /*
     * The implementation of this DatagramSocket.
     */
    DatagramSocketImpl impl;

    /*
     * The Class of DatagramSocketImpl we use for this runtime.  
     */

    static Class implClass;

    static {
	String prefix = "";
	try {

	    prefix = System.getProperty("impl.prefix", "Plain");

	    implClass = Class.forName("java.net."+prefix+"DatagramSocketImpl");
	} catch (Exception e) {
	    System.err.println("Can't find class: java.net." + prefix +
			       "DatagramSocketImpl: check impl.prefix property");
	}

	if (implClass == null) {
	    try {
		implClass = Class.forName("java.net.PlainDatagramSocketImpl");
	    } catch (Exception e) {
		throw new Error("System property impl.prefix incorrect");
	    }
	}
    }

    /**
     * Constructs a datagram socket and binds it to any available port 
     * on the local host machine. 
     *
     * @exception  SocketException  if the socket could not be opened,
     *               or the socket could not bind to the specified local port.
     * @since      JDK1.0
     */
    public DatagramSocket() throws SocketException {
	// create a datagram socket.
	create(0, null);
    }

    /**
     * Constructs a datagram socket and binds it to the specified port 
     * on the local host machine. 
     *
     * @param      local   port to use.
     * @exception  SocketException  if the socket could not be opened,
     *               or the socket could not bind to the specified local port.
     * @since      JDK1.0
     */
    public DatagramSocket(int port) throws SocketException {
	this(port, null);
    }

    /**
     * Creates a datagram socket, bound to the specified local
     * address.  The local port must be between 0 and 65535 inclusive.
     * @param port local port to use
     * @param laddr local address to bind
     * @since   JDK1.1
     */
    public DatagramSocket(int port, InetAddress laddr) throws SocketException {
	if (port < 0 || port > 0xFFFF)
	    throw new IllegalArgumentException("Port out of range:"+port);

	create(port, laddr);
    }

    /* do the work of creating a vanilla datagramsocket.  It is
     * important that the signature of this method not change,
     * even though it is package-private since it is overridden by
     * MulticastSocket, which must set SO_REUSEADDR.
     */
    void create(int port, InetAddress laddr) throws SocketException {
	SecurityManager sec = System.getSecurityManager();
	if (sec != null) {
	    sec.checkListen(port);
	}
	try {
	    impl = (DatagramSocketImpl) implClass.newInstance();
	} catch (Exception e) {
	    throw new SocketException("can't instantiate DatagramSocketImpl");
	}
	// creates a udp socket
	impl.create();
	// binds the udp socket to desired port + address
	if (laddr == null) {
	    laddr = InetAddress.anyLocalAddress;
	}
	impl.bind(port, laddr);
    }

    /**
     * Sends a datagram packet from this socket. The 
     * <code>DatagramPacket</code> includes information indicating the 
     * data to be sent, its length, the IP address of the remote host, 
     * and the port number on the remote host. 
     *
     * @param      p   the <code>DatagramPacket</code> to be sent.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.net.DatagramPacket
     * @since      JDK1.0
     */
    public void send(DatagramPacket p) throws IOException  {

	// check the address is ok wiht the security manager on every send.
	SecurityManager security = System.getSecurityManager();

	// The reason you want to synchronize on datagram packet
	// is because you dont want an applet to change the address 
	// while you are trying to send the packet for example 
	// after the security check but before the send.
	synchronized (p) {
	    if (security != null) {
		if (p.getAddress().isMulticastAddress()) {
		    security.checkMulticast(p.getAddress());
		} else {
		    security.checkConnect(p.getAddress().getHostAddress(), 
					  p.getPort());
		}
	    }
            // call the  method to send
            impl.send(p);
        }
    }

    /**
     * Receives a datagram packet from this socket. When this method 
     * returns, the <code>DatagramPacket</code>'s buffer is filled with 
     * the data received. The datagram packet also contains the sender's 
     * IP address, and the port number on the sender's machine. 
     * <p>
     * This method blocks until a datagram is received. The 
     * <code>length</code> field of the datagram packet object contains 
     * the length of the received message. If the message is longer than 
     * the buffer length, the message is truncated. 
     *
     * @param      p   the <code>DatagramPacket</code> into which to place
     *                 the incoming data.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.net.DatagramPacket
     * @see        java.net.DatagramSocket
     * @since      JDK1.0
     */
    public synchronized void receive(DatagramPacket p) throws IOException {
	// check the address is ok with the security manager before every recv.
	SecurityManager security = System.getSecurityManager();

      	synchronized (p) {
	    if (security != null) {

		while(true) {
		    // peek at the packet to see who it is from.
		    InetAddress peekAddress = new InetAddress();
		    int peekPort = impl.peek(peekAddress);

		    try {
			security.checkConnect(peekAddress.getHostAddress(), peekPort);
			// security check succeeded - so now break and recv the packet.
			break;
		    } catch (SecurityException se) {
			// Throw away the offending packet by consuming
			// it in a tmp buffer.
			DatagramPacket tmp = new DatagramPacket(new byte[1], 1);
			impl.receive(tmp);

		    	// silently discard the offending packet and continue:
			// unknown/malicious entities on nets should not make
			// runtime throw security exception and disrupt the applet
			// by sending random datagram packets.
			continue;
		    } 
		} // end of while
	    }
	    // If the security check succeeds, then receive the packet
 	    impl.receive(p);
	}	
    }

    /**
     * Gets the local address to which the socket is bound.
     *
     * @since   1.1
     */
    public InetAddress getLocalAddress() {
	InetAddress in = null;
	try {
	    in = (InetAddress) impl.getOption(SocketOptions.SO_BINDADDR);
	    SecurityManager s = System.getSecurityManager();
	    if (s != null) {
		s.checkConnect(in.getHostAddress(), -1);
	    }
	} catch (Exception e) {
	    in = InetAddress.anyLocalAddress; // "0.0.0.0"
	}
	return in;
    }    

    /**
     * Returns the port number on the local host to which this socket is bound.
     *
     * @return  the port number on the local host to which this socket is bound.
     * @since   JDK1.0
     */
    public int getLocalPort() {
	return impl.getLocalPort();
    }

    /** Enable/disable SO_TIMEOUT with the specified timeout, in
     *  milliseconds. With this option set to a non-zero timeout,
     *  a call to receive() for this DatagramSocket
     *  will block for only this amount of time.  If the timeout expires,
     *  a <B>java.io.InterruptedIOException</B> is raised, though the
     *  ServerSocket is still valid.  The option <B>must</B> be enabled
     *  prior to entering the blocking operation to have effect.  The 
     *  timeout must be > 0.
     *  A timeout of zero is interpreted as an infinite timeout.  
     *
     * @since   JDK1.1
     */
    public synchronized void setSoTimeout(int timeout) throws SocketException {
	impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    /**
     * Retrive setting for SO_TIMEOUT.  0 returns implies that the
     * option is disabled (i.e., timeout of infinity).
     *
     * @since   JDK1.1
     */
    public synchronized int getSoTimeout() throws SocketException {
	Object o = impl.getOption(SocketOptions.SO_TIMEOUT);
	/* extra type safety */
	if (o instanceof Integer) {
	    return ((Integer) o).intValue();
	} else {
	    return 0;
	}
    }

    /**
     * Closes this datagram socket. 
     *
     * @since   JDK1.0
     */
    public void close() {
	impl.close();
    }
}
