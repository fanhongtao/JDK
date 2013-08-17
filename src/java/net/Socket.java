/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

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
 * @version 1.56, 02/06/02
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
     * @param impl an instance of a <B>SocketImpl</B>
     * the subclass wishes to use on the Socket.
     *
     * @exception SocketException if there is an error in the underlying protocol,     
     * such as a TCP error. 
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
     * <p>
     * If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the host address and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     *
     * @param      host   the host name.
     * @param      port   the port number.
     *
     * @exception  UnknownHostException if the IP address of 
     * the host could not be determined.
     *
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkConnect</code> method doesn't allow the operation.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @see        SecurityManager#checkConnect
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
     * <p>
     * If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the host address and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     * 
     * @param      address   the IP address.
     * @param      port      the port number.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkConnect</code> method doesn't allow the operation.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @see        SecurityManager#checkConnect
     */
    public Socket(InetAddress address, int port) throws IOException {
	this(address, port, null, 0, true);
    }

    /**
     * Creates a socket and connects it to the specified remote host on
     * the specified remote port. The Socket will also bind() to the local
     * address and port supplied.
     * <p>
     * If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the host address and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     * 
     * @param host the name of the remote host
     * @param port the remote port
     * @param localAddr the local address the socket is bound to
     * @param localPort the local port the socket is bound to
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkConnect</code> method doesn't allow the operation.
     * @see        SecurityManager#checkConnect
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
     * <p>
     * If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the host address and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     * 
     * @param address the remote address
     * @param port the remote port
     * @param localAddr the local address the socket is bound to
     * @param localPort the local port the socket is bound to
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkConnect</code> method doesn't allow the operation.
     * @see        SecurityManager#checkConnect
     * @since   JDK1.1
     */
    public Socket(InetAddress address, int port, InetAddress localAddr,
		  int localPort) throws IOException {
		      this(address, port, localAddr, localPort, true);
    }

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
     * <p>
     * If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with the host address and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     *
     * @param      host     the host name.
     * @param      port     the port number.
     * @param      stream   a <code>boolean</code> indicating whether this is
     *                      a stream socket or a datagram socket.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkConnect</code> method doesn't allow the operation.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @see        SecurityManager#checkConnect
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
     * <p>If there is a security manager, its
     * <code>checkConnect</code> method is called
     * with <code>host.getHostAddress()</code> and <code>port</code> 
     * as its arguments. This could result in a SecurityException.
     *
     * @param      host     the IP address.
     * @param      port      the port number.
     * @param      stream    if <code>true</code>, create a stream socket;
     *                       otherwise, create a datagram socket.
     * @exception  IOException  if an I/O error occurs when creating the socket.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkConnect</code> method doesn't allow the operation.
     * @see        java.net.Socket#setSocketImplFactory(java.net.SocketImplFactory)
     * @see        java.net.SocketImpl
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @see        SecurityManager#checkConnect
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
     */
    public InetAddress getInetAddress() {
	return impl.getInetAddress();
    }

    /**
     * Gets the local address to which the socket is bound.
     *
     * @return the local address to which the socket is bound.
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
     */
    public int getPort() {
	return impl.getPort();
    }

    /**
     * Returns the local port to which this socket is bound.
     *
     * @return  the local port number to which this socket is connected.
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
     */
    public InputStream getInputStream() throws IOException {
	try {
	    return (InputStream)
		AccessController.doPrivileged(new PrivilegedExceptionAction() {
		    public Object run() throws IOException {
			return impl.getInputStream();
		    }
		});
	} catch (java.security.PrivilegedActionException e) {
	    throw (IOException) e.getException();
	}
    }

    /**
     * Returns an output stream for this socket.
     *
     * @return     an output stream for writing bytes to this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               output stream.
     */
    public OutputStream getOutputStream() throws IOException {
	try {
	    return (OutputStream)
		AccessController.doPrivileged(new PrivilegedExceptionAction() {
		    public Object run() throws IOException {
			return impl.getOutputStream();
		    }
		});
	} catch (java.security.PrivilegedActionException e) {
	    throw (IOException) e.getException();
	}
    }

    /**
     * Enable/disable TCP_NODELAY (disable/enable Nagle's algorithm).
     *
     * @param on <code>true</code> to enable TCP_NODELAY, 
     * <code>false</coder> to disable.
     *
     * @exception SocketException if there is an error 
     * in the underlying protocol, such as a TCP error.
     * 
     * @since   JDK1.1
     *
     * @see #getTcpNoDelay()
     */
    public void setTcpNoDelay(boolean on) throws SocketException {
	impl.setOption(SocketOptions.TCP_NODELAY, new Boolean(on));
    }

    /**
     * Tests if TCP_NODELAY is enabled.
     *
     * @return a <code>boolean</code> indicating whether or not TCP_NODELAY is enabled.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @since   JDK1.1
     * @see #setTcpNoDelay(boolean)
     */
    public boolean getTcpNoDelay() throws SocketException {
	return ((Boolean) impl.getOption(SocketOptions.TCP_NODELAY)).booleanValue();
    }

    /**
     * Enable/disable SO_LINGER with the specified linger time in seconds. 
     * The maximum timeout value is platform specific.
     *
     * The setting only affects socket close.
     * 
     * @param on     whether or not to linger on.
     * @param linger how to linger for, if on is true.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @exception IllegalArgumentException if the linger value is negative.
     * @since JDK1.1
     * @see #getSoLinger()
     */
    public void setSoLinger(boolean on, int linger) throws SocketException {
	if (!on) {
	    impl.setOption(SocketOptions.SO_LINGER, new Boolean(on));
	} else {
	    if (linger < 0) {
		throw new IllegalArgumentException("invalid value for SO_LINGER");
	    }
            if (linger > 65535)
                linger = 65535;
	    impl.setOption(SocketOptions.SO_LINGER, new Integer(linger));
	}
    }

    /**
     * Returns setting for SO_LINGER. -1 returns implies that the
     * option is disabled.
     *
     * The setting only affects socket close.
     *
     * @return the setting for SO_LINGER.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @since   JDK1.1
     * @see #setSoLinger(boolean, int)
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
     * @param timeout the specified timeout, in milliseconds.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @since   JDK 1.1
     * @see #getSoTimeout()
     */
    public synchronized void setSoTimeout(int timeout) throws SocketException {
	impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    /**
     * Returns setting for SO_TIMEOUT.  0 returns implies that the
     * option is disabled (i.e., timeout of infinity).
     * @return the setting for SO_TIMEOUT
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
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
     * <tt>Socket</tt>. The SO_SNDBUF option is used by the platform's
     * networking code as a hint for the size to set
     * the underlying network I/O buffers.
     *
     * <p>Increasing buffer size can increase the performance of
     * network I/O for high-volume connection, while decreasing it can
     * help reduce the backlog of incoming data. For UDP, this sets
     * the maximum size of a packet that may be sent on this <tt>Socket</tt>.
     *
     * <p>Because SO_SNDBUF is a hint, applications that want to
     * verify what size the buffers were set to should call
     * {@link #getSendBufferSize()}.
     *
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     *
     * @param size the size to which to set the send buffer
     * size. This value must be greater than 0.
     *
     * @exception IllegalArgumentException if the 
     * value is 0 or is negative.
     *
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
     * Get value of the SO_SNDBUF option for this <tt>Socket</tt>, 
     * that is the buffer size used by the platform 
     * for output on this <tt>Socket</tt>.
     * @return the value of the SO_SNDBUF option for this <tt>Socket</tt>.
     *
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     *
     * @see #setSendBufferSize(int)
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
     * <tt>Socket</tt>. The SO_RCVBUF option is used by the platform's
     * networking code as a hint for the size to set
     * the underlying network I/O buffers.
     *
     * <p>Increasing buffer size can increase the performance of
     * network I/O for high-volume connection, while decreasing it can
     * help reduce the backlog of incoming data. For UDP, this sets
     * the maximum size of a packet that may be sent on this <tt>Socket</tt>.
     *
     * <p>Because SO_RCVBUF is a hint, applications that want to
     * verify what size the buffers were set to should call
     * {@link #getReceiveBufferSize()}.
     *
     * @param size the size to which to set the receive buffer
     * size. This value must be greater than 0.
     *
     * @exception IllegalArgumentException if the value is 0 or is
     * negative.
     *
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error.
     * 
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
     * Gets the value of the SO_RCVBUF option for this <tt>Socket</tt>, 
     * that is the buffer size used by the platform for 
     * input on this <tt>Socket</tt>.
     *
     * @return the value of the SO_RCVBUF option for this <tt>Socket</tt>.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
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
     * Enable/disable SO_KEEPALIVE.
     * 
     * @param on     whether or not to have socket keep alive turned on.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @since 1.3 
     * @see #getKeepAlive()
     */
    public void setKeepAlive(boolean on) throws SocketException {
        impl.setOption(SocketOptions.SO_KEEPALIVE, new Boolean(on));
    }

    /**
     * Tests if SO_KEEPALIVE is enabled.
     *
     * @return a <code>boolean</code> indicating whether or not SO_KEEPALIVE is enabled.
     * @exception SocketException if there is an error
     * in the underlying protocol, such as a TCP error. 
     * @since   1.3
     * @see #setKeepAlive(boolean)
     */
    public boolean getKeepAlive() throws SocketException {
	return ((Boolean) impl.getOption(SocketOptions.SO_KEEPALIVE)).booleanValue();
    }

    /**
     * Closes this socket.
     *
     * @exception  IOException  if an I/O error occurs when closing this socket.
     */
    public synchronized void close() throws IOException {
	impl.close();
    }

    /**
     * Places the input stream for this socket at "end of stream".
     * Any data sent to the input stream side of the socket is acknowledged
     * and then silently discarded.
     *
     * If you read from a socket input stream after invoking 
     * shutdownInput() on the socket, the stream will return EOF.
     *
     * @exception IOException if an I/O error occurs when shutting down this
     * socket.
     * @see java.net.Socket#shutdownOutput()
     * @see java.net.Socket#close()
     * @see java.net.Socket#setSoLinger(boolean, int)
     */
    public void shutdownInput() throws IOException
    {
	impl.shutdownInput();
    }
    
    /**
     * Disables the output stream for this socket.
     * For a TCP socket, any previously written data will be sent
     * followed by TCP's normal connection termination sequence.
     *
     * If you write to a socket output stream after invoking 
     * shutdownOutput() on the socket, the stream will throw 
     * an IOException.
     *
     * @exception IOException if an I/O error occurs when shutting down this
     * socket.
     * @see java.net.Socket#shutdownInput()
     * @see java.net.Socket#close()
     * @see java.net.Socket#setSoLinger(boolean, int)
     */
    public void shutdownOutput() throws IOException
    {
	impl.shutdownOutput();
    }

    /**
     * Converts this socket to a <code>String</code>.
     *
     * @return  a string representation of this socket.
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
     * <p>If there is a security manager, this method first calls
     * the security manager's <code>checkSetFactory</code> method 
     * to ensure the operation is allowed. 
     * This could result in a SecurityException.
     *
     * @param      fac   the desired factory.
     * @exception  IOException  if an I/O error occurs when setting the
     *               socket factory.
     * @exception  SocketException  if the factory is already defined.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkSetFactory</code> method doesn't allow the operation.
     * @see        java.net.SocketImplFactory#createSocketImpl()
     * @see        SecurityManager#checkSetFactory
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
