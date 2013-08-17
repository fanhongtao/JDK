/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * This class represents a datagram packet. 
 * <p>
 * Datagram packets are used to implement a connectionless packet 
 * delivery service. Each message is routed from one machine to 
 * another based solely on information contained within that packet. 
 * Multiple packets sent from one machine to another might be routed 
 * differently, and might arrive in any order. 
 *
 * @author  Pavani Diwanji
 * @author  Benjamin Renaud
 * @version 1.31, 02/06/02
 * @since   JDK1.0
 */
public final 
class DatagramPacket {

    /**
     * Perform class initialization
     */
    static {
	java.security.AccessController.doPrivileged(
		  new sun.security.action.LoadLibraryAction("net"));
	init();
    }

    /*
     * The fields of this class are package-private since DatagramSocketImpl 
     * classes needs to access them.
     */
    byte[] buf;
    int offset;
    int length;
    InetAddress address;
    int port;

    /**
     * Constructs a <code>DatagramPacket</code> for receiving packets of 
     * length <code>length</code>, specifying an offset into the buffer.
     * <p>
     * The <code>length</code> argument must be less than or equal to 
     * <code>buf.length</code>. 
     *
     * @param   buf      buffer for holding the incoming datagram.
     * @param   offset   the offset for the buffer
     * @param   length   the number of bytes to read.
     *
     * @since JDK1.2
     */
    public DatagramPacket(byte buf[], int offset, int length) {
	setData(buf, offset, length);
	this.address = null;
	this.port = -1;
    }

    /**
     * Constructs a <code>DatagramPacket</code> for receiving packets of 
     * length <code>length</code>. 
     * <p>
     * The <code>length</code> argument must be less than or equal to 
     * <code>buf.length</code>. 
     *
     * @param   buf      buffer for holding the incoming datagram.
     * @param   length   the number of bytes to read.
     */
    public DatagramPacket(byte buf[], int length) {
	this (buf, 0, length);
    }
    
    /**
     * Constructs a datagram packet for sending packets of length
     * <code>length</code> with offset <code>ioffset</code>to the
     * specified port number on the specified host. The
     * <code>length</code> argument must be less than or equal to
     * <code>buf.length</code>.
     *
     * @param   buf      the packet data.
     * @param   offset   the packet data offset.
     * @param   length   the packet data length.
     * @param   address  the destination address.
     * @param   port     the destination port number.
     * @see java.net.InetAddress
     *
     * @since JDK1.2
     */
    public DatagramPacket(byte buf[], int offset, int length,
			  InetAddress address, int port) {
	setData(buf, offset, length);
	setAddress(address);
	setPort(port);
    }

    /**
     * Constructs a datagram packet for sending packets of length 
     * <code>length</code> to the specified port number on the specified 
     * host. The <code>length</code> argument must be less than or equal 
     * to <code>buf.length</code>. 
     *
     * @param   buf      the packet data.
     * @param   length   the packet length.
     * @param   address  the destination address.
     * @param   port     the destination port number.
     * @see     java.net.InetAddress
     */
    public DatagramPacket(byte buf[], int length,
			  InetAddress address, int port) {
	this(buf, 0, length, address, port);
    }
    
    /**
     * Returns the IP address of the machine to which this datagram is being
     * sent or from which the datagram was received.
     *
     * @return  the IP address of the machine to which this datagram is being
     *          sent or from which the datagram was received.
     * @see     java.net.InetAddress
     * @see #setAddress(java.net.InetAddress)
     */
    public synchronized InetAddress getAddress() {
	return address;
    }
    
    /**
     * Returns the port number on the remote host to which this datagram is
     * being sent or from which the datagram was received.
     *
     * @return  the port number on the remote host to which this datagram is
     *          being sent or from which the datagram was received.
     * @see #setPort(int)
     */
    public synchronized int getPort() {
	return port;
    }
    
    /**
     * Returns the data received or the data to be sent.
     *
     * @return  the data received or the data to be sent.
     * @see #setData(byte[], int, int)
     */
    public synchronized byte[] getData() {
	return buf;
    }
    
    /**
     * Returns the offset of the data to be sent or the offset of the
     * data received.
     *
     * @return  the offset of the data to be sent or the offset of the
     *          data received.
     *
     * @since JDK1.2
     */
    public synchronized int getOffset() {
	return offset;
    }

    /**
     * Returns the length of the data to be sent or the length of the
     * data received.
     *
     * @return  the length of the data to be sent or the length of the
     *          data received.
     * @see #setLength(int)
     */
    public synchronized int getLength() {
	return length;
    }

    /** 
     * Set the data buffer for this packet. This sets the
     * data, length and offset of the packet.
     *
     * @param buf the buffer to set for this packet
     *
     * @param offset the offset into the data
     *
     * @param length the length of the data
     *
     * @exception NullPointerException if the argument is null
     *
     * @see #getData
     * @see #getOffset
     * @see #getLength
     *
     * @since JDK1.2 
     */
    public synchronized void setData(byte[] buf, int offset, int length) {
	/* this will check to see if buf is null */
	if (length < 0 || offset < 0 ||
	    ((length + offset) > buf.length)) {
	    throw new IllegalArgumentException("illegal length or offset");
	}
	this.buf = buf;
	this.length = length;
	this.offset = offset;
    }

    /**
     * Sets the IP address of the machine to which this datagram
     * is being sent.
     * @param iaddr the <code>InetAddress</code>
     * @since   JDK1.1
     * @see #getAddress()
     */
    public synchronized void setAddress(InetAddress iaddr) {
	address = iaddr;
    }

    /**
     * Sets the port number on the remote host to which this datagram
     * is being sent.
     * @param iport the port number
     * @since   JDK1.1
     * @see #setPort(int)
     */
    public synchronized void setPort(int iport) {
	if (iport < 0 || iport > 0xFFFF) {
	    throw new IllegalArgumentException("Port out of range:"+ iport);
	}
	port = iport;
    }

    /** 
     * Set the data buffer for this packet. If the length of the
     * packet length is greater than the length of argument to this
     * method, the length is reset to the the length of the argument.
     *
     * @param buf the buffer to set for this packet.
     *
     * @exception NullPointerException if the argument is null.
     *
     * @see #getLength
     * @see #getData
     *
     * @since JDK1.1 
     */
    public synchronized void setData(byte[] buf) {
	if (buf == null) {
	    throw new NullPointerException("null packet buffer");
	}
	this.buf = buf;

	if (length > buf.length) {
	    setLength(buf.length);
	}
    }

    /**  
     * Set the length for this packet. The length of the packet is
     * the number of bytes from the packet's data buffer that will be
     * sent, or the number of bytes of the packet's data buffer that
     * will be used for receiving data. The length must be lesser or
     * equal to the length of the packet's buffer.
     *
     * @param length the length to set for this packet.
     * 
     * @exception IllegalArgumentException if the length is negative
     * of if the length is greater than the packet's data buffer
     * length.
     *
     * @see #getLength
     * @see #setData
     *
     * @since JDK1.1 
     */
    public synchronized void setLength(int length) {
	if (length > buf.length || length < 0) {
	    throw new IllegalArgumentException("illegal length");
	}
	this.length = length;
    }

    /**
     * Perform class load-time initializations.  
     */
    private native static void init();
}



