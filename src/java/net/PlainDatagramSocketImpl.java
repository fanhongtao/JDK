/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Concrete datagram and multicast socket implementation base class.
 * Note: This is not a public class, so that applets cannot call
 * into the implementation directly and hence cannot bypass the
 * security checks present in the DatagramSocket and MulticastSocket
 * classes.
 *
 * @author Pavani Diwanji
 */

class PlainDatagramSocketImpl extends DatagramSocketImpl
{

    /* timeout value for receive() */
    private int timeout = 0;

    /**
     * Load net library into runtime.
     */
    static {
	java.security.AccessController.doPrivileged(
		  new sun.security.action.LoadLibraryAction("net"));
	init();
    }

    /**
     * Creates a datagram socket
     */
    protected synchronized void create() throws SocketException {
	fd = new FileDescriptor();
	datagramSocketCreate();
    }

    /**
     * Binds a datagram socket to a local port.
     */
    protected synchronized native void bind(int lport, InetAddress laddr)
        throws SocketException;

    /**
     * Sends a datagram packet. The packet contains the data and the
     * destination address to send the packet to.
     * @param packet to be sent.
     */
    protected native void send(DatagramPacket p) throws IOException;

    /**
     * Peek at the packet to see who it is from.
     * @param return the address which the packet came from.
     */
    protected synchronized native int peek(InetAddress i) throws IOException;

    /**
     * Receive the datagram packet.
     * @param Packet Received.
     */
    protected synchronized native void receive(DatagramPacket p)
        throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param TTL to be set.
     */
    protected native void setTimeToLive(int ttl) throws IOException;

    /**
     * Get the TTL (time-to-live) option.
     */
    protected native int getTimeToLive() throws IOException;

    /**
     * Set the TTL (time-to-live) option.
     * @param TTL to be set.
     */
    protected native void setTTL(byte ttl) throws IOException;

    /**
     * Get the TTL (time-to-live) option.
     */
    protected native byte getTTL() throws IOException;

    /**
     * Join the multicast group.
     * @param multicast address to join.
     */
    protected native void join(InetAddress inetaddr) throws IOException;

    /**
     * Leave the multicast group.
     * @param multicast address to leave.
     */
    protected native void leave(InetAddress inetaddr) throws IOException;

    /**
     * Close the socket.
     */
    protected void close() {
	if (fd != null) {
	    datagramSocketClose();
	    fd = null;
	}
    }

    protected void finalize() {
	close();
    }

    /**
     * set a value - since we only support (setting) binary options
     * here, o must be a Boolean
     */

     public void setOption(int optID, Object o) throws SocketException {
	 switch (optID) {
	    /* check type safety b4 going native.  These should never
	     * fail, since only java.Socket* has access to
	     * PlainSocketImpl.setOption().
	     */
	 case SO_TIMEOUT:
	     if (o == null || !(o instanceof Integer)) {
		 throw new SocketException("bad argument for SO_TIMEOUT");
	     }
	     int tmp = ((Integer) o).intValue();
	     if (tmp < 0)
		 throw new IllegalArgumentException("timeout < 0");
	     timeout = tmp;
	     return;
	 case SO_BINDADDR:
	     throw new SocketException("Cannot re-bind Socket");
	 case SO_REUSEADDR:
	     if (o == null || !(o instanceof Integer)) {
		 throw new SocketException("bad argument for SO_REUSEADDR");
	     }
	     break;
	 case SO_RCVBUF:
	 case SO_SNDBUF:
	     if (o == null || !(o instanceof Integer) ||
		 ((Integer)o).intValue() < 0) {
		 throw new SocketException("bad argument for SO_SNDBUF or " +
					   "SO_RCVBUF");
	     }
	     break;
	 case IP_MULTICAST_IF:
	     if (o == null || !(o instanceof InetAddress))
		 throw new SocketException("bad argument for IP_MULTICAST_IF");
	     break;
	 default:
	     throw new SocketException("invalid option: " + optID);
	 }
	 socketSetOption(optID, o);
     }

    /*
     * get option's state - set or not
     */

    public Object getOption(int optID) throws SocketException {
	Integer result = null;
	if (optID == SO_TIMEOUT) {
	    result = new Integer(timeout);
	} else {
	    int ret = socketGetOption(optID);

	    if (optID == SO_BINDADDR || optID == IP_MULTICAST_IF) {
		InetAddress in = new InetAddress();
		in.address = ret;
		return in;
	    } else if (optID == SO_RCVBUF || optID == SO_SNDBUF) {
		result = new Integer(ret);
	    }
	}
	return result;
    }

    private native void datagramSocketCreate() throws SocketException;
    private native void datagramSocketClose();
    private native void socketSetOption(int opt, Object val)
        throws SocketException;
    private native int socketGetOption(int opt) throws SocketException;

    /**
     * Perform class load-time initializations.
     */
    private native static void init();

}

