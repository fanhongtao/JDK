/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * This class represents a socket for sending and receiving datagram packets.
 *
 * <p>A datagram socket is the sending or receiving point for a packet
 * delivery service. Each packet sent or received on a datagram socket
 * is individually addressed and routed. Multiple packets sent from
 * one machine to another may be routed differently, and may arrive in
 * any order.
 *
 * <p>UDP broadcasts sends and receives are always enabled on a
 * DatagramSocket.
 *
 * @author  Pavani Diwanji
 * @version 1.50, 02/06/02
 * @see     java.net.DatagramPacket
 * @since JDK1.0
 */
public
class DatagramSocket {
    /*
     * The implementation of this DatagramSocket.
     */
    DatagramSocketImpl impl;

    boolean connected = false;
    InetAddress connectedAddress = null;
    int connectedPort = -1;

    /*
     * The Class of DatagramSocketImpl we use for this runtime.
     */

    static Class implClass;

    static {
	String prefix = "";
	try {
	    prefix = (String) java.security.AccessController.doPrivileged(
	            new sun.security.action.GetPropertyAction("impl.prefix",
							      "Plain"));
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
     * <p>If there is a security manager, 
     * its <code>checkListen</code> method is first called
     * with 0 as its argument to ensure the operation is allowed. 
     * This could result in a SecurityException.
     *
     * @exception  SocketException  if the socket could not be opened,
     *               or the socket could not bind to the specified local port.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkListen</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkListen
     */
    public DatagramSocket() throws SocketException {
	// create a datagram socket.
	create(0, null);
    }

    /**
     * Constructs a datagram socket and binds it to the specified port
     * on the local host machine.
     * 
     * <p>If there is a security manager, 
     * its <code>checkListen</code> method is first called
     * with the <code>port</code> argument
     * as its argument to ensure the operation is allowed. 
     * This could result in a SecurityException.
     *
     * @param      port port to use.
     * @exception  SocketException  if the socket could not be opened,
     *               or the socket could not bind to the specified local port.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkListen</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkListen
     */
    public DatagramSocket(int port) throws SocketException {
	this(port, null);
    }

    /**
     * Creates a datagram socket, bound to the specified local
     * address.  The local port must be between 0 and 65535 inclusive.
     * 
     * <p>If there is a security manager, 
     * its <code>checkListen</code> method is first called
     * with the <code>port</code> argument
     * as its argument to ensure the operation is allowed. 
     * This could result in a SecurityException.
     * 
     * @param port local port to use
     * @param laddr local address to bind
     * 
     * @exception  SocketException  if the socket could not be opened,
     *               or the socket could not bind to the specified local port.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkListen</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkListen
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
	if (factory != null) {
	    impl = factory.createDatagramSocketImpl();
	} else {
	    try {
	        impl = (DatagramSocketImpl) implClass.newInstance();
	    } catch (Exception e) {
	        throw new SocketException("can't instantiate DatagramSocketImpl");
	    }
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
     * Connects the socket to a remote address for this socket. When a
     * socket is connected to a remote address, packets may only be
     * sent to or received from that address. By default a datagram
     * socket is not connected.
     *
     * <p>A caller's permission to send and receive datagrams to a
     * given host and port are checked at connect time. When a socket
     * is connected, receive and send <b>will not
     * perform any security checks</b> on incoming and outgoing
     * packets, other than matching the packet's and the socket's
     * address and port. On a send operation, if the packet's address
     * is set and the packet's address and the socket's address do not
     * match, an IllegalArgumentException will be thrown. A socket
     * connected to a multicast address may only be used to send packets.
     *
     * @param address the remote address for the socket
     *
     * @param port the remote port for the socket.
     *
     * @exception IllegalArgumentException if the address is invalid
     * or the port is out of range.
     *
     * @exception SecurityException if the caller is not allowed to
     * send datagrams to and receive datagrams from the address and port.
     *
     * @see #disconnect
     * @see #send
     * @see #receive 
     */
    public void connect(InetAddress address, int port) {
	synchronized (this) {
	    if (port < 0 || port > 0xFFFF) {
		throw new IllegalArgumentException("connect: " + port);
	    }
	    if (address == null) {
		throw new IllegalArgumentException("connect: null address");
	    }
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                if (address.isMulticastAddress()) {
                    security.checkMulticast(address);
                } else {
                    security.checkConnect(address.getHostAddress(), port);
		    security.checkAccept(address.getHostAddress(), port);
                }
	    }
	    connectedAddress = address;
	    connectedPort = port;
	    connected = true;
	}
    }

    /** 
     * Disconnects the socket. This does nothing if the socket is not
     * connected.
     *
     * @see #connect
     */
    public void disconnect() {
	synchronized (this) {
	    connectedAddress = null;
	    connectedPort = -1;
	    connected = false;
	}
    }

    /**
     * Returns the address to which this socket is connected. Returns null
     * if the socket is not connected.
     *
     * @return the address to which this socket is connected.
     */
    public InetAddress getInetAddress() {
	return connectedAddress;
    }

    /**
     * Returns the port for this socket. Returns -1 if the socket is not
     * connected.
     *
     * @return the port to which this socket is connected.
     */
    public int getPort() {
	return connectedPort;
    }

    /**
     * Sends a datagram packet from this socket. The
     * <code>DatagramPacket</code> includes information indicating the
     * data to be sent, its length, the IP address of the remote host,
     * and the port number on the remote host.
     *
     * <p>If there is a security manager, and the socket is not currently
     * connected to a remote address, this method first performs some
     * security checks. First, if <code>p.getAddress().isMulticastAddress()</code>
     * is true, this method calls the
     * security manager's <code>checkMulticast</code> method
     * with <code>p.getAddress()</code> as its argument.
     * If the evaluation of that expression is false,
     * this method instead calls the security manager's 
     * <code>checkConnect</code> method with arguments
     * <code>p.getAddress().getHostAddress()</code> and
     * <code>p.getPort()</code>. Each call to a security manager method
     * could result in a SecurityException if the operation is not allowed.
     * 
     * @param      p   the <code>DatagramPacket</code> to be sent.
     * 
     * @exception  IOException  if an I/O error occurs.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkMulticast</code> or <code>checkConnect</code> 
     *             method doesn't allow the send.
     * 
     * @see        java.net.DatagramPacket
     * @see        SecurityManager#checkMulticast(InetAddress)
     * @see        SecurityManager#checkConnect
     */
    public void send(DatagramPacket p) throws IOException  {
	InetAddress packetAddress = null;
	synchronized (p) {
	    if (!connected) {
		// check the address is ok wiht the security manager on every send.
		SecurityManager security = System.getSecurityManager();

		// The reason you want to synchronize on datagram packet
		// is because you dont want an applet to change the address 
		// while you are trying to send the packet for example 
		// after the security check but before the send.
		if (security != null) {
		    if (p.getAddress().isMulticastAddress()) {
			security.checkMulticast(p.getAddress());
		    } else {
			security.checkConnect(p.getAddress().getHostAddress(), 
					      p.getPort());
		    }
		}
	    } else {
		// we're connected
		packetAddress = p.getAddress();
		if (packetAddress == null) {
		    p.setAddress(connectedAddress);
		    p.setPort(connectedPort);
		} else if ((!packetAddress.equals(connectedAddress)) ||
			   p.getPort() != connectedPort) {
		    throw new IllegalArgumentException("connected address " +
						       "and packet address" +
						       " differ");
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
     * the packet's length, the message is truncated.
     * <p>
     * If there is a security manager, a packet cannot be received if the
     * security manager's <code>checkAccept</code> method
     * does not allow it.
     * 
     * @param      p   the <code>DatagramPacket</code> into which to place
     *                 the incoming data.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.net.DatagramPacket
     * @see        java.net.DatagramSocket
     */
    public synchronized void receive(DatagramPacket p) throws IOException {
	// check the address is ok with the security manager before every recv.
	SecurityManager security = null;
      	synchronized (p) {
	    if (connected || ((security = System.getSecurityManager()) != null)) {
		while(true) {
		    // peek at the packet to see who it is from.
		    InetAddress peekAddress = new InetAddress();
		    int peekPort = impl.peek(peekAddress);
		    
		    if (connected) {
			if (!connectedAddress.equals(peekAddress) ||
			    (connectedPort != peekPort)) {
			    // throw the packet away and silently continue
			    DatagramPacket tmp = new DatagramPacket(new byte[1], 1);
			    impl.receive(tmp);
			    continue;
			} else {
			    break;
			}
		    } else if (security != null) {
			try {
			    security.checkAccept(peekAddress.getHostAddress(), 
						 peekPort);
			    // security check succeeded - so now break
			    // and recv the packet.
			    break;
			} catch (SecurityException se) {
			    // Throw away the offending packet by consuming
			    // it in a tmp buffer.
			    DatagramPacket tmp = new DatagramPacket(new byte[1], 1);
			    impl.receive(tmp);
			
			    // silently discard the offending packet
			    // and continue: unknown/malicious
			    // entities on nets should not make
			    // runtime throw security exception and
			    // disrupt the applet by sending random
			    // datagram packets.
			    continue;
			} 
		    }
		} // end of while
	    }
	    // If the security check succeeds, or the datagram is
	    // connected then receive the packet
	    impl.receive(p);
	}
    }

    /**
     * Gets the local address to which the socket is bound.
     *
     * <p>If there is a security manager, its
     * <code>checkConnect</code> method is first called
     * with the host address and <code>-1</code> 
     * as its arguments to see if the operation is allowed.
     * 
     * @exception  SecurityException  if a security manager exists and its  
     *  <code>checkConnect</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkConnect
     * @return an <tt>InetAddress</tt> representing the local
     * address to which the socket is bound
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
     * @param timeout the specified timeout in milliseconds.
     * @throws SocketException if there is an error in the underlying protocol, such as a TCP error. 
     * @since   JDK1.1
     * @see #getSoTimeout()
     */
    public synchronized void setSoTimeout(int timeout) throws SocketException {
	impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    /**
     * Retrive setting for SO_TIMEOUT.  0 returns implies that the
     * option is disabled (i.e., timeout of infinity).
     *
     * @return the setting for SO_TIMEOUT
     * @throws SocketException if there is an error in the underlying protocol, such as a TCP error.
     * @since   JDK1.1
     * @see #setSoTimeout(int)
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
     * Sets the SO_SNDBUF option to the specified value for this
     * <tt>DatagramSocket</tt>. The SO_SNDBUF option is used by the platform's
     * networking code as a hint for the size to set
     * the underlying network I/O buffers.
     *
     * <p>Increasing buffer size can increase the performance of
     * network I/O for high-volume connection, while decreasing it can
     * help reduce the backlog of incoming data. For UDP, this sets
     * the maximum size of a packet that may be sent on this socket.
     *
     * <p>Because SO_SNDBUF is a hint, applications that want to
     * verify what size the buffers were set to should call
     * {@link #getSendBufferSize()}.
     *
     * @param size the size to which to set the send buffer
     * size. This value must be greater than 0.
     *
     * @exception <tt>SocketException</tt> if there is an error 
     * in the underlying protocol, such as a TCP error.
     * @exception <tt>IllegalArgumentException</tt> if the value is 0 or is
     * negative.
     * @see #getSendBufferSize()
     */
    public synchronized void setSendBufferSize(int size)
    throws SocketException{
	if (!(size > 0)) {
	    throw new IllegalArgumentException("negative send size");
	}
	impl.setOption(SocketOptions.SO_SNDBUF, new Integer(size));
    }

    /**
     * Get value of the SO_SNDBUF option for this <tt>DatagramSocket</tt>, that is the
     * buffer size used by the platform for output on this <tt>DatagramSocket</tt>.
     *
     * @return the value of the SO_SNDBUF option for this <tt>DatagramSocket</tt>
     * @exception <tt>SocketException</tt> if there is an error in 
     * the underlying protocol, such as a TCP error.
     * @see #setSendBufferSize
     */
    public synchronized int getSendBufferSize() throws SocketException {
	int result = 0;
	Object o = impl.getOption(SocketOptions.SO_SNDBUF);
	if (o instanceof Integer) {
	    result = ((Integer)o).intValue();
	}
	return result;
    }

    /**
     * Sets the SO_RCVBUF option to the specified value for this
     * <tt>DatagramSocket</tt>. The SO_RCVBUF option is used by the platform's
     * networking code as a hint for the size to set
     * the underlying network I/O buffers.
     *
     * <p>Increasing buffer size can increase the performance of
     * network I/O for high-volume connection, while decreasing it can
     * help reduce the backlog of incoming data. For UDP, this sets
     * the maximum size of a packet that may be sent on this <tt>DatagramSocket</tt>.
     *
     * <p>Because SO_RCVBUF is a hint, applications that want to
     * verify what size the buffers were set to should call
     * {@link #getReceiveBufferSize()}.
     *
     * @param size the size to which to set the receive buffer
     * size. This value must be greater than 0.
     *
     * @exception <tt>SocketException</tt> if there is an error in 
     * the underlying protocol, such as a TCP error.
     * @exception IllegalArgumentException if the value is 0 or is
     * negative.
     * @see #getReceiveBufferSize()
     */
    public synchronized void setReceiveBufferSize(int size)
    throws SocketException{
	if (size <= 0) {
	    throw new IllegalArgumentException("invalid receive size");
	}
	impl.setOption(SocketOptions.SO_RCVBUF, new Integer(size));
    }

    /**
     * Get value of the SO_RCVBUF option for this <tt>DatagramSocket</tt>, that is the
     * buffer size used by the platform for input on this <tt>DatagramSocket</tt>.
     *
     * @return the value of the SO_RCVBUF option for this <tt>DatagramSocket</tt>
     * @exception SocketException if there is an error in the underlying protocol, such as a TCP error.
     * @see #setReceiveBufferSize(int)
     */
    public synchronized int getReceiveBufferSize()
    throws SocketException{
	int result = 0;
	Object o = impl.getOption(SocketOptions.SO_RCVBUF);
	if (o instanceof Integer) {
	    result = ((Integer)o).intValue();
	}
	return result;
    }

    /**
     * Closes this datagram socket.
     */
    public void close() {
	impl.close();
    }
 
    /**
     * The factory for all datagram sockets.
     */
    static DatagramSocketImplFactory factory;
 
    /**
     * Sets the datagram socket implementation factory for the
     * application. The factory can be specified only once.
     * <p>
     * When an application creates a new datagram socket, the socket
     * implementation factory's <code>createDatagramSocketImpl</code> method 
is
     * called to create the actual datagram socket implementation.
     * 
     * <p>If there is a security manager, this method first calls
     * the security manager's <code>checkSetFactory</code> method 
     * to ensure the operation is allowed. 
     * This could result in a SecurityException.
     *
     * @param      fac   the desired factory.
     * @exception  IOException  if an I/O error occurs when setting the
     *              datagram socket factory.
     * @exception  SocketException  if the factory is already defined.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkSetFactory</code> method doesn't allow the 
     operation.
     * @see        
     java.net.DatagramSocketImplFactory#createDatagramSocketImpl()
     * @see       SecurityManager#checkSetFactory
     */
    public static synchronized void 
    setDatagramSocketImplFactory(DatagramSocketImplFactory fac)
       throws IOException
    {
        if (factory != null) {
	    throw new SocketException("factory already defined");
	}
 	SecurityManager security = System.getSecurityManager();
 	if (security != null) {
	    security.checkSetFactory();
 	}
 	factory = fac;
    }
}
