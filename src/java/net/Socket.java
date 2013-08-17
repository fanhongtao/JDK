/*
 * @(#)Socket.java	1.30 98/07/01
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * This class implements client sockets (also called just 
 * "sockets"). A socket is an endpoint for communication 
 * between two machines. 
 * <p>
 * The actual work of the socket is performed by an instance of the 
 * <code>SocketImpl</code> class. An application, by changing 
 * the socket factory that creates the socket implementation, 
 * can configure itself to create sockets appropriate to the local 
 * firewall. 
 *
 * @author  unascribed
 * @version 1.30, 07/01/98
 * @see     java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
 * @see     java.net.SocketImpl
 * @since   JDK1.0
 */
public  
class Socket {
    /**
     * The implementation of this Socket.
     */
    SocketImpl impl;

    /**
     * Creates an unconnected socket, with the
     * system-default type of SocketImpl.
     *
     * @since   JDK1.1
     */
    protected Socket() {
	impl = (factory != null) ? factory.createSocketImpl() : 
	    new PlainSocketImpl();
    }

    /**
     * Creates an unconnected Socket with a user-specified
     * SocketImpl.
     * <P>
     * The <i>impl</i> parameter is an instance of a <B>SocketImpl</B> 
     * the subclass wishes to use on the Socket. 
     *
     * @since   JDK1.1
     */
    protected Socket(SocketImpl impl) throws SocketException {
	this.impl = impl;
    }

    /** 
     * Creates a stream socket and connects it to the specified port 
     * number on the named host. 
     * <p>
     * If the application has specified a server socket factory, that 
     * factory's <code>createSocketImpl</code> method is called to create 
     * the actual socket implementation. Otherwise a "plain" socket is created.
     *
     * @param      host   the host name.
     * @param      port   the port number.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @since      JDK1.0
     */
    public Socket(String host, int port)
	throws UnknownHostException, IOException
    {
	this(InetAddress.getByName(host), port, null, 0, true);
    }

    /** 
     * Creates a stream socket and connects it to the specified port 
     * number at the specified IP address. 
     * <p>
     * If the application has specified a socket factory, that factory's 
     * <code>createSocketImpl</code> method is called to create the 
     * actual socket implementation. Otherwise a "plain" socket is created.
     *
     * @param      address   the IP address.
     * @param      port      the port number.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @since      JDK1.0
     */
    public Socket(InetAddress address, int port) throws IOException {
	this(address, port, null, 0, true);
    }

    /** 
     * Creates a socket and connects it to the specified remote host on
     * the specified remote port. The Socket will also bind() to the local
     * address and port supplied.
     * @param host the name of the remote host
     * @param port the remote port
     * @param localAddr the local address the socket is bound to
     * @param localPort the local port the socket is bound to
     * @since   JDK1.1
     */
    public Socket(String host, int port, InetAddress localAddr, 
		  int localPort) throws IOException {
	this(InetAddress.getByName(host), port, localAddr, localPort, true);
    }

    /** 
     * Creates a socket and connects it to the specified remote address on
     * the specified remote port. The Socket will also bind() to the local
     * address and port supplied.
     * @param address the remote address
     * @param port the remote port
     * @param localAddr the local address the socket is bound to
     * @param localPort the local port the socket is bound to
     * @since   JDK1.1
     */
    public Socket(InetAddress address, int port, InetAddress localAddr, 
		  int localPort) throws IOException {
		      this(address, port, localAddr, localPort, true);
    };		     

    /**
     * Creates a stream socket and connects it to the specified port 
     * number on the named host. 
     * <p>
     * If the stream argument is <code>true</code>, this creates a 
     * stream socket. If the stream argument is <code>false</code>, it 
     * creates a datagram socket. 
     * <p>
     * If the application has specified a server socket factory, that 
     * factory's <code>createSocketImpl</code> method is called to create 
     * the actual socket implementation. Otherwise a "plain" socket is created.
     *
     * @param      host     the host name.
     * @param      port     the port number.
     * @param      stream   a <code>boolean</code> indicating whether this is
     *                      a stream socket or a datagram socket.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @since      JDK1.0
     * @deprecated Use DatagramSocket instead for UDP transport.
     */
    public Socket(String host, int port, boolean stream) throws IOException {
	this(InetAddress.getByName(host), port, null, 0, stream);
    }

    /**
     * Creates a socket and connects it to the specified port number at 
     * the specified IP address. 
     * <p>
     * If the stream argument is <code>true</code>, this creates a 
     * stream socket. If the stream argument is <code>false</code>, it 
     * creates a datagram socket. 
     * <p>
     * If the application has specified a server socket factory, that 
     * factory's <code>createSocketImpl</code> method is called to create 
     * the actual socket implementation. Otherwise a "plain" socket is created.
     *
     * @param      address   the IP address.
     * @param      port      the port number.
     * @param      stream    if <code>true</code>, create a stream socket;
     *                       otherwise, create a datagram socket.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @since      JDK1.0
     * @deprecated Use DatagramSocket instead for UDP transport.
     */
    public Socket(InetAddress host, int port, boolean stream) throws IOException {
	this(host, port, null, 0, stream);
    }
    
    private Socket(InetAddress address, int port, InetAddress localAddr, 
		  int localPort, boolean stream) throws IOException {
	this();

	if (port < 0 || port > 0xFFFF) {
	    throw new IllegalArgumentException("port out range:"+port);
	}

	if (localPort < 0 || localPort > 0xFFFF) {
	    throw new IllegalArgumentException("port out range:"+localPort);
	}

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkConnect(address.getHostAddress(), port);
	}

	try {
	    impl.create(stream); 
	    if (localAddr != null || localPort > 0) {
		if (localAddr == null) {
		    localAddr = InetAddress.anyLocalAddress;
		}
		impl.bind(localAddr, localPort);
	    }
	    impl.connect(address, port);
	} catch (SocketException e) {
	    impl.close();
	    throw e;
	}
    }

    /**
     * Returns the address to which the socket is connected.
     *
     * @return  the remote IP address to which this socket is connected.
     * @since   JDK1.0
     */
    public InetAddress getInetAddress() {
	return impl.getInetAddress();
    }

    /**
     * Gets the local address to which the socket is bound.
     *
     * @since   JDK1.1
     */
    public InetAddress getLocalAddress() {
	InetAddress in = null;
	try {
	    in = (InetAddress) impl.getOption(SocketOptions.SO_BINDADDR);
	} catch (Exception e) {
	    in = InetAddress.anyLocalAddress; // "0.0.0.0"
	}
	return in;
    }

    /**
     * Returns the remote port to which this socket is connected.
     *
     * @return  the remote port number to which this socket is connected.
     * @since   JDK1.0
     */
    public int getPort() {
	return impl.getPort();
    }

    /**
     * Returns the local port to which this socket is bound.
     *
     * @return  the local port number to which this socket is connected.
     * @since   JDK1.0
     */
    public int getLocalPort() {
	return impl.getLocalPort();
    }

    /**
     * Returns an input stream for this socket.
     *
     * @return     an input stream for reading bytes from this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               input stream.
     * @since      JDK1.0
     */
    public InputStream getInputStream() throws IOException {
	return impl.getInputStream();
    }

    /**
     * Returns an output stream for this socket.
     *
     * @return     an output stream for writing bytes to this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               output stream.
     * @since      JDK1.0
     */
    public OutputStream getOutputStream() throws IOException {
	return impl.getOutputStream();
    }

    /**
     * Enable/disable TCP_NODELAY (disable/enable Nagle's algorithm).
     *
     * @since   JDK1.1
     */
    public void setTcpNoDelay(boolean on) throws SocketException {
	impl.setOption(SocketOptions.TCP_NODELAY, new Boolean(on));
    }

    /**
     * Tests if TCP_NODELAY is enabled.
     *
     * @since   JDK1.1
     */
    public boolean getTcpNoDelay() throws SocketException {
	return ((Boolean) impl.getOption(SocketOptions.TCP_NODELAY)).booleanValue();
    }

    /**
     * Enable/disable SO_LINGER with the specified linger time.  
     *
     * @since   JDK1.1
     */
    public void setSoLinger(boolean on, int val) throws SocketException {
	if (!on) {
	    impl.setOption(SocketOptions.SO_LINGER, new Boolean(on));
	} else {
	    impl.setOption(SocketOptions.SO_LINGER, new Integer(val));
	}
    }

    /**
     * Returns setting for SO_LINGER. -1 returns implies that the
     * option is disabled.
     *
     * @since   JDK1.1
     */
    public int getSoLinger() throws SocketException {
	Object o = impl.getOption(SocketOptions.SO_LINGER);
	if (o instanceof Integer) {
	    return ((Integer) o).intValue();
	} else {
	    return -1;
	}
    }

    /**
     *  Enable/disable SO_TIMEOUT with the specified timeout, in
     *  milliseconds.  With this option set to a non-zero timeout,
     *  a read() call on the InputStream associated with this Socket
     *  will block for only this amount of time.  If the timeout expires,
     *  a <B>java.io.InterruptedIOException</B> is raised, though the
     *  Socket is still valid. The option <B>must</B> be enabled
     *  prior to entering the blocking operation to have effect. The 
     *  timeout must be > 0.
     *  A timeout of zero is interpreted as an infinite timeout.
     *
     * @since   JDK 1.1
     */
    public synchronized void setSoTimeout(int timeout) throws SocketException {
	impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    /**
     * Returns setting for SO_TIMEOUT.  0 returns implies that the
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
     * Closes this socket. 
     *
     * @exception  IOException  if an I/O error occurs when closing this socket.
     * @since      JDK1.0
     */
    public synchronized void close() throws IOException {
	impl.close();
    }

    /**
     * Converts this socket to a <code>String</code>.
     *
     * @return  a string representation of this socket.
     * @since   JDK1.0
     */
    public String toString() {
	return "Socket[addr=" + impl.getInetAddress() +
	    ",port=" + impl.getPort() + 
	    ",localport=" + impl.getLocalPort() + "]";
    }

    /**
     * The factory for all client sockets.
     */
    private static SocketImplFactory factory;

    /**
     * Sets the client socket implementation factory for the 
     * application. The factory can be specified only once. 
     * <p>
     * When an application creates a new client socket, the socket 
     * implementation factory's <code>createSocketImpl</code> method is 
     * called to create the actual socket implementation. 
     *
     * @param      fac   the desired factory.
     * @exception  IOException  if an I/O error occurs when setting the
     *               socket factory.
     * @exception  SocketException  if the factory is already defined.
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @since      JDK1.0
     */
    public static synchronized void setSocketImplFactory(SocketImplFactory fac)
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
