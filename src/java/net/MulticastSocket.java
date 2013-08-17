/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * The multicast datagram socket class is useful for sending
 * and receiving IP multicast packets.  A MulticastSocket is
 * a (UDP) DatagramSocket, with additional capabilities for
 * joining "groups" of other multicast hosts on the internet.
 * <P>
 * A multicast group is specified by a class D IP address
 * and by a standard UDP port number. Class D IP addresses
 * are in the range <CODE>224.0.0.0</CODE> to <CODE>239.255.255.255</CODE>,
 * inclusive. The address 224.0.0.0 is reserved and should not be used.
 * <P>
 * One would join a multicast group by first creating a MulticastSocket
 * with the desired port, then invoking the
 * <CODE>joinGroup(InetAddress groupAddr)</CODE>
 * method:
 * <PRE>
 * // join a Multicast group and send the group salutations
 * ...
 * String msg = "Hello";
 * InetAddress group = InetAddress.getByName("228.5.6.7");
 * MulticastSocket s = new MulticastSocket(6789);
 * s.joinGroup(group);
 * DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
 *                             group, 6789);
 * s.send(hi);
 * // get their responses!
 * byte[] buf = new byte[1000];
 * DatagramPacket recv = new DatagramPacket(buf, buf.length);
 * s.receive(recv);
 * ...
 * // OK, I'm done talking - leave the group...
 * s.leaveGroup(group);
 * </PRE>
 *
 * When one sends a message to a multicast group, <B>all</B> subscribing
 * recipients to that host and port receive the message (within the
 * time-to-live range of the packet, see below).  The socket needn't
 * be a member of the multicast group to send messages to it.
 * <P>
 * When a socket subscribes to a multicast group/port, it receives
 * datagrams sent by other hosts to the group/port, as do all other
 * members of the group and port.  A socket relinquishes membership
 * in a group by the leaveGroup(InetAddress addr) method.  <B>
 * Multiple MulticastSocket's</B> may subscribe to a multicast group
 * and port concurrently, and they will all receive group datagrams.
 * <P>
 * Currently applets are not allowed to use multicast sockets.
 *
 * @author Pavani Diwanji
 * @since  JDK1.1
 */
public
class MulticastSocket extends DatagramSocket {
    /**
     * Create a multicast socket.
     * 
     * <p>If there is a security manager, 
     * its <code>checkListen</code> method is first called
     * with 0 as its argument to ensure the operation is allowed. 
     * This could result in a SecurityException.
     * 
     * @exception IOException if an I/O exception occurs
     * while creating the MulticastSocket
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkListen</code> method doesn't allow the operation.
     * @see SecurityManager#checkListen
     */
    public MulticastSocket() throws IOException {
	super();
    }

    /**
     * Create a multicast socket and bind it to a specific port.
     * 
     * <p>If there is a security manager, 
     * its <code>checkListen</code> method is first called
     * with the <code>port</code> argument
     * as its argument to ensure the operation is allowed. 
     * This could result in a SecurityException.
     * 
     * @param port port to use
     * @exception IOException if an I/O exception occurs
     * while creating the MulticastSocket
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkListen</code> method doesn't allow the operation.
     * @see SecurityManager#checkListen
     */
    public MulticastSocket(int port) throws IOException {
	super(port);
    }

    /* do the work of creating a vanilla multicast socket.  It is
     * important that the signature of this method not change,
     * even though it is package-private, since it is overrides a
     * method from DatagramSocket, which must not set SO_REUSEADDR.
     */
    void create(int port, InetAddress ignore) throws SocketException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkListen(port);
	}
	if (factory != null) {
	    impl = factory.createDatagramSocketImpl();
	} else {
	    try {
	        this.impl = (DatagramSocketImpl) implClass.newInstance();
	    } catch (Exception e) {
	        throw new SocketException("can't instantiate DatagramSocketImpl" + e.toString());
	    }
	}
	impl.create();
	impl.setOption(SocketOptions.SO_REUSEADDR, new Integer(-1));
	impl.bind(port, InetAddress.anyLocalAddress);
    }

    /**
     * The lock on the socket's TTL. This is for set/getTTL and
     * send(packet,ttl).
     */
    private Object ttlLock = new Object();

    /**
     * Set the default time-to-live for multicast packets sent out
     * on this socket.  The TTL sets the IP time-to-live for
     * <code>DatagramPackets</code> sent to a MulticastGroup, which
     * specifies how many "hops" that the packet will be forwarded
     * on the network before it expires.
     *
     * <p>The ttl is an <b>unsigned</b> 8-bit quantity, and so <B>must</B> be
     * in the range <code> 0 <= ttl <= 0xFF </code>.
     *
     * @param ttl the time-to-live
     * @exception IOException if an I/O exception occurs
     * while setting the default time-to-live value
     * @deprecated use the setTimeToLive method instead, which uses
     * <b>int</b> instead of <b>byte</b> as the type for ttl.
     * @see #getTTL()
     */
    public void setTTL(byte ttl) throws IOException {
	impl.setTTL(ttl);
    }

    /**
     * Set the default time-to-live for multicast packets sent out
     * on this socket.  The TTL sets the IP time-to-live for
     * <code>DatagramPackets</code> sent to a MulticastGroup, which
     * specifies how many "hops" that the packet will be forwarded
     * on the network before it expires.
     *
     * <P> The ttl <B>must</B> be in the range <code> 0 <= ttl <=
     * 255</code> or an IllegalArgumentException will be thrown.
     * @exception IOException if an I/O exception occurs
     * while setting the default time-to-live value
     * @param ttl the time-to-live
     * @see #getTimeToLive()
     */
    public void setTimeToLive(int ttl) throws IOException {
	if (ttl < 0 || ttl > 255) {
	    throw new IllegalArgumentException("ttl out of range");
	}
	impl.setTimeToLive(ttl);
    }

    /**
     * Get the default time-to-live for multicast packets sent out on
     * the socket.
     *
     * @exception IOException if an I/O exception occurs
     * while getting the default time-to-live value
     * @deprecated use the getTimeToLive method instead, which returns
     * an <b>int</b> instead of a <b>byte</b>.
     * @see #setTTL(byte)
     */
    public byte getTTL() throws IOException {
	return impl.getTTL();
    }

    /**
     * Get the default time-to-live for multicast packets sent out on
     * the socket.
     * @exception IOException if an I/O exception occurs while
     * getting the default time-to-live value
     * @return the default time-to-live value
     * @see #setTimeToLive(int)
     */
    public int getTimeToLive() throws IOException {
	return impl.getTimeToLive();
    }

    /**
     * Joins a multicast group.Its behavior may be affected
     * by <code>setInterface</code>.
     * 
     * <p>If there is a security manager, this method first
     * calls its <code>checkMulticast</code> method
     * with the <code>mcastaddr</code> argument
     * as its argument.
     * 
     * @param mcastaddr is the multicast address to join
     * 
     * @exception IOException if there is an error joining
     * or when the address is not a multicast address.
     * @exception  SecurityException  if a security manager exists and its  
     * <code>checkMulticast</code> method doesn't allow the join.
     * 
     * @see SecurityManager#checkMulticast(InetAddress)
     */
    public void joinGroup(InetAddress mcastaddr) throws IOException {

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkMulticast(mcastaddr);
	}
	impl.join(mcastaddr);
    }

    /**
     * Leave a multicast group. Its behavior may be affected
     * by <code>setInterface</code>.
     * 
     * <p>If there is a security manager, this method first
     * calls its <code>checkMulticast</code> method
     * with the <code>mcastaddr</code> argument
     * as its argument.
     * 
     * @param mcastaddr is the multicast address to leave
     * @exception IOException if there is an error leaving
     * or when the address is not a multicast address.
     * @exception  SecurityException  if a security manager exists and its  
     * <code>checkMulticast</code> method doesn't allow the operation.
     * 
     * @see SecurityManager#checkMulticast(InetAddress)
     */
    public void leaveGroup(InetAddress mcastaddr) throws IOException {

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkMulticast(mcastaddr);
	}
	impl.leave(mcastaddr);
    }

    /**
     * Set the multicast network interface used by methods
     * whose behavior would be affected by the value of the
     * network interface. Useful for multihomed hosts.
     * @param inf the InetAddress
     * @exception SocketException if there is an error in 
     * the underlying protocol, such as a TCP error. 
     * @see #getInterface()
     */
    public void setInterface(InetAddress inf) throws SocketException {
	impl.setOption(SocketOptions.IP_MULTICAST_IF, inf);
    }

    /**
     * Retrieve the address of the network interface used for
     * multicast packets.
     * 
     * @return An <code>InetAddress</code> representing
     *  the address of the network interface used for 
     *  multicast packets.
     *
     * @exception SocketException if there is an error in 
     * the underlying protocol, such as a TCP error.
     * 
     * @see #setInterface(java.net.InetAddress)
     */
    public InetAddress getInterface() throws SocketException {
	return (InetAddress) impl.getOption(SocketOptions.IP_MULTICAST_IF);
    }

    /**
     * Sends a datagram packet to the destination, with a TTL (time-
     * to-live) other than the default for the socket.  This method
     * need only be used in instances where a particular TTL is desired;
     * otherwise it is preferable to set a TTL once on the socket, and
     * use that default TTL for all packets.  This method does <B>not
     * </B> alter the default TTL for the socket. Its behavior may be
     * affected by <code>setInterface</code>.
     *
     * <p>If there is a security manager, this method first performs some
     * security checks. First, if <code>p.getAddress().isMulticastAddress()</code>
     * is true, this method calls the
     * security manager's <code>checkMulticast</code> method
     * with <code>p.getAddress()</code> and <code>ttl</code> as its arguments.
     * If the evaluation of that expression is false,
     * this method instead calls the security manager's 
     * <code>checkConnect</code> method with arguments
     * <code>p.getAddress().getHostAddress()</code> and
     * <code>p.getPort()</code>. Each call to a security manager method
     * could result in a SecurityException if the operation is not allowed.
     * 
     * @param p	is the packet to be sent. The packet should contain
     * the destination multicast ip address and the data to be sent.
     * One does not need to be the member of the group to send
     * packets to a destination multicast address.
     * @param ttl optional time to live for multicast packet.
     * default ttl is 1.
     * 
     * @exception IOException is raised if an error occurs i.e
     * error while setting ttl.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkMulticast</code> or <code>checkConnect</code> 
     *             method doesn't allow the send.
     * 
     * @see DatagramSocket#send
     * @see DatagramSocket#receive
     * @see SecurityManager#checkMulticast(java.net.InetAddress, byte)
     * @see SecurityManager#checkConnect
     */
    public void send(DatagramPacket p, byte ttl)
        throws IOException {
            synchronized(ttlLock) {
                synchronized(p) {
		    if (!connected) {
                        // Security manager makes sure that the multicast address
                        // is allowed one and that the ttl used is less
                        // than the allowed maxttl.
                        SecurityManager security = System.getSecurityManager();
                        if (security != null) {
                            if (p.getAddress().isMulticastAddress()) {
                                security.checkMulticast(p.getAddress(), ttl);
                            } else {
                                security.checkConnect(p.getAddress().getHostAddress(),
                                                      p.getPort());
                            }
                        }
		    } else {
			// we're connected
			InetAddress packetAddress = null;
			packetAddress = p.getAddress();
			if (packetAddress == null) {
			    p.setAddress(connectedAddress);
			    p.setPort(connectedPort);
			} else if ((!packetAddress.equals(connectedAddress)) ||
				   p.getPort() != connectedPort) {
			    throw new SecurityException("connected address and packet address" +
							" differ");
			}
		    }
                    byte dttl = getTTL();
                    try {
                        if (ttl != dttl) {
                            // set the ttl
                            impl.setTTL(ttl);
                        }
                        // call the datagram method to send
                        impl.send(p);
                    } finally {
                        // set it back to default
                        if (ttl != dttl) {
                            impl.setTTL(dttl);
                        }
                    }
                } // synch p
            }  //synch ttl
    } //method
}
