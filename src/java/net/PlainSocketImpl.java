/*
 * @(#)PlainSocketImpl.java	1.26 98/07/01
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
import java.io.InterruptedIOException;
import java.io.FileDescriptor;
import java.io.ByteArrayOutputStream;

/**
 * Default Socket Implementation. This implementation does
 * not implement any security checks.  It does support SOCKS version 4.
 * Note this class should <b>NOT</b> be public.
 *
 * @author  Steven B. Byrne
 * @version 1.26, 07/01/98
 */
class PlainSocketImpl extends SocketImpl
{
    /* instance variable for SO_TIMEOUT */
    int timeout;   // timeout in millisec

    /* SOCKS related constants */

    private static final int SOCKS_PROTO_VERS		= 4;
    private static final int SOCKS_REPLY_VERS		= 4;

    private static final int COMMAND_CONNECT		= 1;
    private static final int COMMAND_BIND		= 2;

    private static final int REQUEST_GRANTED		= 90;
    private static final int REQUEST_REJECTED		= 91;
    private static final int REQUEST_REJECTED_NO_IDENTD  = 92;
    private static final int REQUEST_REJECTED_DIFF_IDENTS = 93;

    public static final String socksServerProp		= "socksProxyHost";
    public static final String socksPortProp		= "socksProxyPort";

    public static final String socksDefaultPortStr	= "1080";

    /**
     * Load net library into runtime.
     */
    static {
	System.loadLibrary("net");
	initProto();
    }

    /**
     * Creates a socket with a boolean that specifies whether this
     * is a stream socket (true) or an unconnected UDP socket (false).
     */
    protected synchronized void create(boolean stream) throws IOException {
	fd = new FileDescriptor();
	socketCreate(stream);
    }

    /** 
     * Creates a socket and connects it to the specified port on
     * the specified host.
     * @param host the specified host
     * @param port the specified port 
     */
    protected void connect(String host, int port)
        throws UnknownHostException, IOException
    {
	IOException pending = null;
	try {
	    InetAddress address = InetAddress.getByName(host);

	    try {
		connectToAddress(address, port);
		return;
	    } catch (IOException e) {
		pending = e;
	    }
	} catch (UnknownHostException e) {
	    pending = e;
	}

	// everything failed
	close();
	throw pending;
    }

    /** 
     * Creates a socket and connects it to the specified address on
     * the specified port.
     * @param address the address
     * @param port the specified port
     */
    protected void connect(InetAddress address, int port) throws IOException {
	this.port = port;
	this.address = address;

	try {
	    connectToAddress(address, port);
	    return;
	} catch (IOException e) {
	    // everything failed
	    close();
	    throw e;
	}
    }

    private void connectToAddress(InetAddress address, int port) throws IOException {
	if (usingSocks()) {
	    doSOCKSConnect(address, port);
	} else {
	    doConnect(address, port);
	}
    }

    public void setOption(int opt, Object val) throws SocketException {
	boolean on = true;
	switch (opt) {
	    /* check type safety b4 going native.  These should never
	     * fail, since only java.Socket* has access to 
	     * PlainSocketImpl.setOption().
	     */
	    case SO_LINGER:
		if (val == null || (!(val instanceof Integer) && !(val instanceof Boolean)))
		    throw new SocketException("Bad parameter for option");
		if (val instanceof Boolean) { 
		    /* true only if disabling - enabling should be Integer */
		    on = false;
		}
		break;
	    case SO_TIMEOUT:
		if (val == null || (!(val instanceof Integer)))
		    throw new SocketException("Bad parameter for SO_TIMEOUT");
		int tmp = ((Integer) val).intValue();
		if (tmp < 0)
		    throw new IllegalArgumentException("timeout < 0");
		timeout = tmp;
		return;
	    case SO_BINDADDR:
		throw new SocketException("Cannot re-bind socket");
	    case TCP_NODELAY:
		if (val == null || !(val instanceof Boolean))
		    throw new SocketException("bad parameter for TCP_NODELAY");
		on = ((Boolean)val).booleanValue();
		break;
	    default:
		throw new SocketException("unrecognized TCP option: " + opt);
	}
	socketSetOption(opt, on, val);
    }

    public Object getOption(int opt) throws SocketException {
	if (opt == SO_TIMEOUT) {
	    return new Integer(timeout);
	}
	int ret = socketGetOption(opt);
	/*
	 * The native socketGetOption() knows about 3 options.
	 * The 32 bit value it returns will be interpreted according
	 * to what we're asking.  A return of -1 means it understands
	 * the option but its turned off.  It will raise a SocketException
	 * if "opt" isn't one it understands.
	 */

	switch (opt) {
	case TCP_NODELAY:
	    return (ret == -1) ? new Boolean(false): new Boolean(true);
	case SO_LINGER:
	    return (ret == -1) ? new Boolean(false): (Object)(new Integer(ret));
	case SO_BINDADDR:
	    InetAddress in = new InetAddress();
	    in.address = ret;
	    return in;
	}
	// should never get here
	return null;
    }

    /**
     * Connect to the SOCKS server using the SOCKS connection protocol.
     */
    private void doSOCKSConnect(InetAddress address, int port) throws IOException {
	connectToSocksServer();

	sendSOCKSCommandPacket(COMMAND_CONNECT, address, port);

	int protoStatus = getSOCKSReply();

	switch (protoStatus) {
	  case REQUEST_GRANTED:
	    // connection set up, return control to the socket client
	    return;

	  case REQUEST_REJECTED:
	  case REQUEST_REJECTED_NO_IDENTD:
		throw new SocketException("SOCKS server cannot conect to identd");

	  case REQUEST_REJECTED_DIFF_IDENTS:
	    throw new SocketException("User name does not match identd name");
	}
    }
    

    /**
     * Read the response from the socks server.  Return the result code.
     */
    private int getSOCKSReply() throws IOException {
	InputStream in = getInputStream();

	// REMIND: this could deal with reading < 8 bytes and buffering
	// them up.

	byte response[] = new byte[8];

	int code;
	if ((code = in.read(response)) != response.length) {
	    throw new SocketException("Malformed reply from SOCKS server");
	}

	if (response[0] != 0) { // should be version 0
	    throw new SocketException("Malformed reply from SOCKS server");
	}

	return response[1];	// the response code
    }

    /**
     * Just set up a connection to the SOCKS server and return.  The caller
     * needs to handle the SOCKS initiation protocol with the server after
     * the connection is established.
     */
    private void connectToSocksServer() throws IOException {

	String socksServerString = System.getProperty(socksServerProp);
	if (socksServerString == null) {
	    // REMIND: this is too trusting of its (internal) callers --
	    // needs to robustly assert that SOCKS are in fact being used,
	    // and signal an error (in some manner) if SOCKS are not being
	    // used.
	    return;
	}

	InetAddress socksServer = InetAddress.getByName(socksServerString);

	String socksPortString = System.getProperty(socksPortProp,
						    socksDefaultPortStr);

	int socksServerPort;
	try {
	    socksServerPort = Integer.parseInt(socksPortString);
	} catch (Exception e) {
	    throw new SocketException("Bad port number format");
	}
	
	doConnect(socksServer, socksServerPort);
    }


    /**
     * The workhorse of the connection operation.  Tries several times to
     * establish a connection to the given <host, port>.  If unsuccessful,
     * throws an IOException indicating what went wrong.
     */

    private void doConnect(InetAddress address, int port) throws IOException {
	IOException pending = null;

	for (int i = 0 ; i < 3 ; i++) {
	    try {
		socketConnect(address, port);
		return;
	    } catch (ProtocolException e) {
		// Try again in case of a protocol exception
		close();
		fd = new FileDescriptor();
		socketCreate(true);
		pending = e;
	    } catch (IOException e) {
		// Let someone else deal with this exception
		close();
		throw e;
	    }
	}

	// failed to connect -- tell our client the bad news
	close();
	throw pending;
    }


    /**
     * Just creates and sends out to the connected socket a SOCKS command
     * packet.
     */
    private void sendSOCKSCommandPacket(int command, InetAddress address,
					int port) throws IOException {
	
        byte commandPacket[] = makeCommandPacket(command, address, port);
	OutputStream out = getOutputStream();

	out.write(commandPacket);
    }

    /**
     * Create and return a SOCKS V4 command packet.
     */
    private byte[] makeCommandPacket(int command, InetAddress address,
					int port) { 

	// base packet size = 8, + 1 null byte 
	ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8 + 1);

	byteStream.write(SOCKS_PROTO_VERS);
	byteStream.write(command);


	byteStream.write((port >> 8) & 0xff);
	byteStream.write((port >> 0) & 0xff);

	byte addressBytes[] = address.getAddress();
	byteStream.write(addressBytes, 0, addressBytes.length);

	String userName = System.getProperty("user.name");
	byte userNameBytes[] = new byte[userName.length()];
	userName.getBytes(0, userName.length(), userNameBytes, 0);

	byteStream.write(userNameBytes, 0, userNameBytes.length);
	byteStream.write(0);	// null termination for user name

	return byteStream.toByteArray();
    }

    /**
     * Returns true if implementation should use the SOCKS protocol
     * (i.e. the user has set the required properties to enable SOCKS to
     * be used).
     */
    private boolean usingSocks() {
	return (System.getProperty(socksServerProp) != null);
    }
    

    /**
     * Binds the socket to the specified address of the specified local port.
     * @param address the address
     * @param port the port
     */
    protected synchronized void bind(InetAddress address, int lport) 
	throws IOException
    {
	socketBind(address, lport);
    }

    /**
     * Listens, for a specified amount of time, for connections.
     * @param count the amount of time to listen for connections
     */
    protected synchronized void listen(int count) throws IOException {
	socketListen(count);
    }

    /**
     * Accepts connections.
     * @param s the connection
     */
    protected synchronized void accept(SocketImpl s) throws IOException {
	socketAccept(s);
    }

    /**
     * Gets an InputStream for this socket.
     */
    protected synchronized InputStream getInputStream() throws IOException {
	return new SocketInputStream(this);
    }

    /**
     * Gets an OutputStream for this socket.
     */
    protected synchronized OutputStream getOutputStream() throws IOException {
	return new SocketOutputStream(this);
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     */
    protected synchronized int available() throws IOException {
	return socketAvailable();
    }

    /**
     * Closes the socket.
     */
    protected void close() throws IOException {
	if (fd != null) {
	    socketClose();
	    fd = null;
	}
    }

    /**
     * Cleans up if the user forgets to close it.
     */
    protected void finalize() throws IOException {
	close();
    }

    private native void socketCreate(boolean isServer) throws IOException;
    private native void socketConnect(InetAddress address, int port)
	throws IOException;
    private native void socketBind(InetAddress address, int port)
	throws IOException;
    private native void socketListen(int count)
	throws IOException;
    private native void socketAccept(SocketImpl s)
	throws IOException;
    private native int socketAvailable()
	throws IOException;
    private native void socketClose()
	throws IOException;
    private static native void initProto();
    private native void socketSetOption(int cmd, boolean on, Object value) 
	throws SocketException;
    private native int socketGetOption(int opt) throws SocketException;
}
