/*
 * @(#)MulticastSocket.java	1.19 98/07/01
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
import java.io.InterruptedIOException;

/**
 * The multicast datagram socket class is useful for sending
 * and receiving IP multicast packets.  A MulticastSocket is
 * a (UDP) DatagramSocket, with additional capabilities for
 * joining "groups" of other multicast hosts on the internet.
 * <P>
 * A multicast group is specified by a class D IP address, those
 * in the range <CODE>224.0.0.1</CODE> to <CODE>239.255.255.255</CODE>, 
 * inclusive, and by a standard UDP port number.  One would join a 
 * multicast group by first creating a MulticastSocket with the desired
 * port, then invoking the <CODE>joinGroup(InetAddress groupAddr)</CODE>
 * method:
 * <PRE>
 * // join a Multicast group and send the group salutations
 * ...
 * byte[] msg = {'H', 'e', 'l', 'l', 'o'};
 * InetAddress group = InetAddress.getByName("228.5.6.7");
 * MulticastSocket s = new MulticastSocket(6789);
 * s.joinGroup(group);
 * DatagramPacket hi = new DatagramPacket(msg, msg.length,
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
 * Currently applets are not allowed ot use multicast sockets.
 *
 * @author Pavani Diwanji
 * @since  JDK1.1
 */
public
class MulticastSocket extends DatagramSocket {
    /**
     * Create a multicast socket.
     * @since   JDK1.1
     */
    public MulticastSocket() throws IOException {
	super();
    }

    /**
     * Create a multicast socket and bind it to a specific port.
     * @param local port to use
     * @since   JDK1.1
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
	try {
	    this.impl = (DatagramSocketImpl) implClass.newInstance();
	} catch (Exception e) {
	    throw new SocketException("can't instantiate DatagramSocketImpl" + e.toString());
	}
	impl.create();
	impl.setOption(SocketOptions.SO_REUSEADDR, new Integer(-1));
	impl.bind(port, InetAddress.anyLocalAddress);
    }
    
    /**
     * Set the default time-to-live for multicast packets sent out
     * on this socket.  The TTL sets the IP time-to-live for
     * <code>DatagramPackets</code> sent to a MulticastGroup, which
     * specifies how many "hops" that the packet will be forwarded
     * on the network before it expires.
     * <P>
     * The ttl is an <b>unsigned</b> 8-bit quantity, and so <B>must</B> be
     * in the range <code> 0 < ttl <= 0xFF </code>.
     * @param ttl the time-to-live
     * @since   JDK1.1
     */
    public void setTTL(byte ttl) throws IOException {
	impl.setTTL(ttl);
    }

    /**
     * Get the default time-to-live for multicast packets sent out
     * on the socket.
     * @since   JDK1.1
     */
    public byte getTTL() throws IOException {
	return impl.getTTL();
    }

    /**
     * Joins a multicast group.
     * @param mcastaddr is the multicast address to join 
     * @exception IOException is raised if there is an error joining
     * or when address is not a multicast address.
     * @since   JDK1.1
     */
    public void joinGroup(InetAddress mcastaddr) throws IOException {

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkMulticast(mcastaddr);
	}
	impl.join(mcastaddr);
    }

    /**
     * Leave a multicast group.
     * @param mcastaddr is the multicast address to leave
     * @exception IOException is raised if there is an error leaving
     * or when address is not a multicast address.
     * @since   JDK1.1
     */
    public void leaveGroup(InetAddress mcastaddr) throws IOException {

	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkMulticast(mcastaddr);
	}
	impl.leave(mcastaddr);
    }

    /**
     * Set the outgoing network interface for multicast packets on this
     * socket, to other than the system default.  Useful for multihomed
     * hosts.
     * @since   JDK1.1
     */
    public void setInterface(InetAddress inf) throws SocketException {
	impl.setOption(SocketOptions.IP_MULTICAST_IF, inf);
    }
    
    /**
     * Retrieve the address of the network interface used for
     * multicast packets.
     * @since   JDK1.1
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
     * </B> alter the default TTL for the socket.
     * @param p	is the packet to be sent. The packet should contain
     * the destination multicast ip address and the data to be sent.
     * One does not need to be the member of the group to send
     * packets to a destination multicast address.
     * @param ttl optional time to live for multicast packet.
     * default ttl is 1.
     * @exception IOException is raised if an error occurs i.e
     * error while setting ttl.
     * @see DatagramSocket#send
     * @see DatagramSocket#receive
     * @since   JDK1.1
     */
    public synchronized void send(DatagramPacket p, byte ttl)
	 throws IOException {

        // Security manager makes sure that the multicast address is
	// is allowed one and that the ttl used is less
	// than the allowed maxttl.
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    if (p.getAddress().isMulticastAddress()) {
		security.checkMulticast(p.getAddress(), ttl);
	    } else {
		security.checkConnect(p.getAddress().getHostAddress(), p.getPort());
	    }
	}

	byte dttl = getTTL();
	
	if (ttl != dttl) {
	// set the ttl
	    impl.setTTL(ttl);
	}
	// call the datagram method to send
	impl.send(p);
	// set it back to default
	if (ttl != dttl) {
	    impl.setTTL(dttl);
	} 
    }  
}
