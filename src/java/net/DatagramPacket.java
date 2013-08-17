/*
 * @(#)DatagramPacket.java	1.13 98/07/01
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
 * This class represents a datagram packet. 
 * <p>
 * Datagram packets are used to implement a connectionless packet 
 * delivery service. Each message is routed from one machine to 
 * another based solely on information contained within that packet. 
 * Multiple packets sent from one machine to another might be routed 
 * differently, and might arrive in any order. 
 *
 * @author  Pavani Diwanji
 * @version 1.13, 07/01/98
 * @since   JDK1.0
 */
public final 
class DatagramPacket {
    /*
     * The fields of this class are package-private since DatagramSocketImpl 
     * classes needs to access them.
     */
    byte[] buf;
    int length;
    InetAddress address;
    int port;

    /**
     * Constructs a <code>DatagramPacket</code> for receiving packets of 
     * length <code>ilength</code>. 
     * <p>
     * The <code>length</code> argument must be less than or equal to 
     * <code>ibuf.length</code>. 
     *
     * @param   ibuf      buffer for holding the incoming datagram.
     * @param   ilength   the number of bytes to read.
     * @since   JDK1.0
     */
    public DatagramPacket(byte ibuf[], int ilength) {
	if (ilength > ibuf.length) {
	    throw new IllegalArgumentException("illegal length");
	}
	buf = ibuf;
	length = ilength;
	address = null;
	port = -1;
    }
    
    /**
     * Constructs a datagram packet for sending packets of length 
     * <code>ilength</code> to the specified port number on the specified 
     * host. The <code>length</code> argument must be less than or equal 
     * to <code>ibuf.length</code>. 
     *
     * @param   ibuf      the packet data.
     * @param   ilength   the packet length.
     * @param   iaddr     the destination address.
     * @param   iport     the destination port number.
     * @see     java.net.InetAddress
     * @since   JDK1.0
     */
    public DatagramPacket(byte ibuf[], int ilength,
			  InetAddress iaddr, int iport) {
	if (ilength > ibuf.length) {
	    throw new IllegalArgumentException("illegal length");
	}
	if (iport < 0 || iport > 0xFFFF) {
	    throw new IllegalArgumentException("Port out of range:"+ iport);
	}
	buf = ibuf;
	length = ilength;
	address = iaddr;
	port = iport;
    }
    
    /**
     * Returns the IP address of the machine to which this datagram is being
     * sent or from which the datagram was received.
     *
     * @return  the IP address of the machine to which this datagram is being
     *          sent or from which the datagram was received.
     * @see     java.net.InetAddress
     * @since   JDK1.0
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
     * @since   JDK1.0
     */
    public synchronized int getPort() {
	return port;
    }
    
    /**
     * Returns the data received or the data to be sent.
     *
     * @return  the data received or the data to be sent.
     * @since   JDK1.0
     */
    public synchronized byte[] getData() {
	return buf;
    }
    
    /**
     * Returns the length of the data to be sent or the length of the
     * data received.
     *
     * @return  the length of the data to be sent or the length of the
     *          data received.
     * @since   JDK1.0
     */
    public synchronized int getLength() {
	return length;
    }

    /**
     * @since   JDK1.1
     */
    public synchronized void setAddress(InetAddress iaddr) {
	address = iaddr;
    }

    /**
     * @since   JDK1.1
     */
    public synchronized void setPort(int iport) {
	if (iport < 0 || iport > 0xFFFF) {
	    throw new IllegalArgumentException("Port out of range:"+ iport);
	}
	port = iport;
    }

    /**
     * @since   JDK1.1
     */
    public synchronized void setData(byte[] ibuf) {
	buf = ibuf;
    }

    /**
     * @since   JDK1.1
     */
    public synchronized void setLength(int ilength) {
	if (ilength > buf.length) {
	    throw new IllegalArgumentException("illegal length");
	}
	length = ilength;
    }
}
