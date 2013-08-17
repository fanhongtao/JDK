/*
 * @(#)SocketImpl.java	1.24 00/08/09
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;

/**
 * The abstract class <code>SocketImpl</code> is a common superclass 
 * of all classes that actually implement sockets. It is used to 
 * create both client and server sockets. 
 * <p>
 * A "plain" socket implements these methods exactly as 
 * described, without attempting to go through a firewall or proxy. 
 *
 * @author  unascribed
 * @version 1.24, 08/09/00
 * @since   JDK1.0
 */
public abstract class SocketImpl implements SocketOptions {
    /**
     * The file descriptor object for this socket. 
     *
     * @since   JDK1.0
     */
    protected FileDescriptor fd;
    
    /**
     * The IP address of the remote end of this socket. 
     *
     * @since   JDK1.0
     */
    protected InetAddress address;
   
    /**
     * The port number on the remote host to which this socket is connected. 
     *
     * @since   JDK1.0
     */
    protected int port;

    /**
     * The local port number to which this socket is connected. 
     *
     * @since   JDK1.0
     */
    protected int localport;   

    /**
     * Creates either a stream or a datagram socket. 
     *
     * @param      stream   if <code>true</code>, create a stream socket;
     *                      otherwise, create a datagram socket.
     * @exception  IOException  if an I/O error occurs while creating the
     *               socket.
     * @since      JDK1.0
     */
    protected abstract void create(boolean stream) throws IOException;

    /**
     * Connects this socket to the specified port on the named host. 
     *
     * @param      host   the name of the remote host.
     * @param      port   the port number.
     * @exception  IOException  if an I/O error occurs when connecting to the
     *               remote host.
     * @since      JDK1.0
     */
    protected abstract void connect(String host, int port) throws IOException;

    /**
     * Connects this socket to the specified port number on the specified host.
     *
     * @param      address   the IP address of the remote host.
     * @param      port      the port number.
     * @exception  IOException  if an I/O error occurs when attempting a
     *               connection.
     * @since      JDK1.0
     */
    protected abstract void connect(InetAddress address, int port) throws IOException;

    /**
     * Binds this socket to the specified port number on the specified host. 
     *
     * @param      host   the IP address of the remote host.
     * @param      port   the port number.
     * @exception  IOException  if an I/O error occurs when binding this socket.
     * @since      JDK1.0
     */
    protected abstract void bind(InetAddress host, int port) throws IOException;

    /**
     * Sets the maximum queue length for incoming connection indications 
     * (a request to connect) to the <code>count</code> argument. If a 
     * connection indication arrives when the queue is full, the 
     * connection is refused. 
     *
     * @param      backlog   the maximum length of the queue.
     * @exception  IOException  if an I/O error occurs when creating the queue.
     * @since      JDK1.0
     */
    protected abstract void listen(int backlog) throws IOException;

    /**
     * Accepts a connection. 
     *
     * @param      s   the accepted connection.
     * @exception  IOException  if an I/O error occurs when accepting the
     *               connection.
     * @since   JDK1.0
     */
    protected abstract void accept(SocketImpl s) throws IOException;

    /**
     * Returns an input stream for this socket.
     *
     * @return     a stream for reading from this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               input stream.
     * @since      JDK1.0
    */
    protected abstract InputStream getInputStream() throws IOException;

    /**
     * Returns an output stream for this socket.
     *
     * @return     an output stream for writing to this socket.
     * @exception  IOException  if an I/O error occurs when creating the
     *               output stream.
     * @since      JDK1.0
     */
    protected abstract OutputStream getOutputStream() throws IOException;

    /**
     * Returns the number of bytes that can be read from this socket
     * without blocking.
     *
     * @return     the number of bytes that can be read from this socket
     *             without blocking.
     * @exception  IOException  if an I/O error occurs when determining the
     *               number of bytes available.
     * @since      JDK1.0
     */
    protected abstract int available() throws IOException;

    /**
     * Closes this socket. 
     *
     * @exception  IOException  if an I/O error occurs when closing this socket.
     * @since      JDK1.0
     */
    protected abstract void close() throws IOException;

    /**
     * Returns the value of this socket's <code>fd</code> field.
     *
     * @return  the value of this socket's <code>fd</code> field.
     * @see     java.net.SocketImpl#fd
     * @since   JDK1.0
     */
    protected FileDescriptor getFileDescriptor() {
	return fd;
    }

    /**
     * Returns the value of this socket's <code>address</code> field.
     *
     * @return  the value of this socket's <code>address</code> field.
     * @see     java.net.SocketImpl#address
     * @since   JDK1.0
     */
    protected InetAddress getInetAddress() {
	return address;
    }

    /**
     * Returns the value of this socket's <code>port</code> field.
     *
     * @return  the value of this socket's <code>port</code> field.
     * @see     java.net.SocketImpl#port
     * @since   JDK1.0
     */
    protected int getPort() {
	return port;
    }

    /**
     * Returns the value of this socket's <code>localport</code> field.
     *
     * @return  the value of this socket's <code>localport</code> field.
     * @see     java.net.SocketImpl#localport
     * @since   JDK1.0
     */
    protected int getLocalPort() {
	return localport;
    }
    
    /**
     * Returns the address and port of this socket as a <code>String</code>.
     *
     * @return  a string representation of this socket.
     * @since   JDK1.0
     */
    public String toString() {
	return "Socket[addr=" + getInetAddress() +
	    ",port=" + getPort() + ",localport=" + getLocalPort()  + "]";
    }

    void reset() throws IOException {
	address = null;
	port = 0;
	localport = 0;
	close();
    }
}
