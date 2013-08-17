/*
 * @(#)SocketOptions.java	1.6 98/07/01
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
 * Interface of methods to get/set socket options.  This interface is
 * implemented by: <B>SocketImpl</B> and  <B>DatagramSocketImpl</B>.  
 * Subclasses of these should override the methods
 * of this interface in order to support their own options.
 * <P>
 * The methods and constants which specify options in this interface are
 * for implementation only.  If you're not subclassing SocketImpl or
 * DatagramSocketImpl, <B>you won't use these directly.</B> There are
 * type-safe methods to get/set each of these options in Socket, ServerSocket, 
 * DatagramSocket and MulticastSocket.
 * <P>
 * A subset of the standard BSD-style socket options are supported in the
 * JDK base classes, <B>PlainSocketImpl</B> and <B>PlainDatagramSocketImpl</B>.  
 * A brief description of each and their use is provided.
 * <P>
 * @version 1.6, 07/01/98
 * @author David Brown
 */


interface SocketOptions {

    /**
     * Enable/disable the option specified by <I>optID</I>.  If the option
     * is to be enabled, and it takes an option-specific "value",  this is 
     * passed in <I>value</I>.  The actual type of value is option-specific,
     * and it is an error to pass something that isn't of the expected type:
     * <BR><PRE>
     * SocketImpl s;
     * ...
     * s.setOption(SO_LINGER, new Integer(10)); 
     *    // OK - set SO_LINGER w/ timeout of 10 sec.
     * s.setOption(SO_LINGER, new Double(10)); 
     *    // ERROR - expects java.lang.Integer
     *</PRE>
     * If the requested option is binary, it can be set using this method by
     * a java.lang.Boolean:
     * <BR><PRE>
     * s.setOption(TCP_NODELAY, new Boolean(true)); 
     *    // OK - enables TCP_NODELAY, a binary option
     * </PRE>
     * <BR>
     * Any option can be disabled using this method with a Boolean(false):
     * <BR><PRE>
     * s.setOption(TCP_NODELAY, new Boolean(false)); 
     *    // OK - disables TCP_NODELAY
     * s.setOption(SO_LINGER, new Boolean(false)); 
     *    // OK - disables SO_LINGER
     * </PRE>
     * <BR>
     * For an option that requires a particular parameter, 
     * setting its value to anything other than 
     * <I>Boolean(false)</I> implicitly enables it.
     * <BR>
     * Throws SocketException if the option is unrecognized, 
     * the socket is closed, or some low-level error occurred 
     * <BR>
     * @param optID identifies the option 
     * @param value the parameter of the socket option
     * @throws SocketException if the option is unrecognized, 
     * the socket is closed, or some low-level error occurred 
     */

    public void 
	setOption(int optID, Object value) throws SocketException;

    /**
     * Fetch the value of an option.  
     * Binary options will return java.lang.Boolean(true) 
     * if enabled, java.lang.Boolean(false) if disabled, e.g.:
     * <BR><PRE>
     * SocketImpl s;
     * ...
     * Boolean noDelay = (Boolean)(s.getOption(TCP_NODELAY));
     * if (noDelay.booleanValue()) {
     *     // true if TCP_NODELAY is enabled...
     * ...
     * }
     * </PRE>
     * <P>
     * For options that take a particular type as a parameter,
     * getOption(int) will return the paramter's value, else
     * it will return java.lang.Boolean(false):
     * <PRE>
     * Object o = s.getOption(SO_LINGER);
     * if (o instanceof Integer) {
     *     System.out.print("Linger time is " + ((Integer)o).intValue());
     * } else {
     *   // the true type of o is java.lang.Boolean(false);
     * }
     * </PRE>
     *
     * @throws SocketException if the socket is closed
     * @throws SocketException if <I>optID</I> is unknown along the
     *         protocol stack (including the SocketImpl)
     */
      
    public Object getOption(int optID) throws SocketException;

    /**
     * The java-supported BSD-style options.
     */

    /**
     * Disable Nagle's algorithm for this connection.  Written data
     * to the network is not buffered pending acknowledgement of
     * previously written data.  
     *<P>
     * Valid for TCP only: SocketImpl.
     * <P>
     * @see Socket#setTcpNoDelay
     * @see Socket#getTcpNoDelay
     */

    public final static int TCP_NODELAY = 0x0001;

    /**
     * Fetch the local address binding of a socket (this option cannot
     * be "set" only "gotten", since sockets are bound at creation time,
     * and so the locally bound address cannot be changed).  The default local
     * address of a socket is INADDR_ANY, meaning any local address on a
     * multi-homed host.  A multi-homed host can use this option to accept
     * connections to only one of its addresses (in the case of a 
     * ServerSocket or DatagramSocket), or to specify its return address 
     * to the peer (for a Socket or DatagramSocket).  The parameter of 
     * this option is an InetAddress.
     * <P>
     * This option <B>must</B> be specified in the constructor.
     * <P>
     * Valid for: SocketImpl, DatagramSocketImpl
     * <P>
     * @see Socket#getLocalAddress
     * @see Server#getLocalAddress
     * @see DatagramSocket#getLocalAddress
     */

    public final static int SO_BINDADDR = 0x000F;

    /** Sets SO_REUSEADDR for a socket.  This is used only for MulticastSockets
     * in java, and it is set by default for MulticastSockets.
     * <P>
     * Valid for: DatagramSocketImpl
     */

    public final static int SO_REUSEADDR = 0x04;

    /** Set which outgoing interface on which to send multicast packets.  
     * Useful on hosts with multiple network interfaces, where applications
     * want to use other than the system default.  Takes/returns an InetAddress.
     * <P>
     * Valid for Multicast: DatagramSocketImpl
     * <P>
     * @see MulticastSocket#setInterface
     * @see MulitcastSocket#getInterface
     */
     
    public final static int IP_MULTICAST_IF = 0x10;

    /**
     * Specify a linger-on-close timeout.  This option disables/enables 
     * immediate return from a <B>close()</B> of a TCP Socket.  Enabling 
     * this option with a non-zero Integer <I>timeout</I> means that a 
     * <B>close()</B> will block pending the transmission and acknowledgement
     * of all data written to the peer, at which point the socket is closed
     * <I>gracefully</I>.  Upon reaching the linger timeout, the socket is
     * closed <I>forcefully</I>, with a TCP RST. Enabling the option with a 
     * timeout of zero does a forceful close immediately.
     * <P>
     * <B>Note:</B>The actual implementation of SO_LINGER in the OS varies 
     * across platforms.
     * <P>
     * Valid only for TCP: SocketImpl
     * <P>
     * @see Socket#setSoLinger
     * @see Socket#getSoLinger
     */

    public final static int SO_LINGER = 0x0080;

    /** Set a timeout on blocking Socket operations:
     * <PRE>
     * ServerSocket.accept();
     * SocketInputStream.read();
     * DatagramSocket.receive();
     * </PRE>
     * <P>
     * The option must be set prior to entering a blocking operation to take effect.
     * If the timeout expires and the operation would continue to block,
     * <B>java.io.InterruptedIOException</B> is raised.  The Socket is not closed
     * in this case.
     * <P>
     * Valid for all sockets: SocketImpl, DatagramSocketImpl
     * <P>
     * @see Socket#setSoTimeout
     * @see ServerSocket#setSoTimeout
     * @see DatagramSocket#setSoTimeout
     */

    public final static int SO_TIMEOUT = 0x1006;
}






