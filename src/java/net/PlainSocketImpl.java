/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.42, 09/19/01
 */
class PlainSocketImpl extends SocketImpl
{
    /* timeout value for connection */
    static int preferredConnectionTimeout = 0;

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

    private boolean shut_rd = false;
    private boolean shut_wr = false;
    
    private SocketInputStream socketInputStream = null;
    /* number of threads using the FileDescriptor */
    private int fdUseCount = 0;

    /* lock when increment/decrementing fdUseCount */
    private Object fdLock = new Object();
	
    /* indicates a close is pending on the file descriptor */
    private boolean closePending = false;    

    /**
     * Load net library into runtime.
     */
    static {
	java.security.AccessController.doPrivileged(
		  new sun.security.action.LoadLibraryAction("net"));
	String s = (String)java.security.AccessController.doPrivileged(
		  new sun.security.action.GetPropertyAction("java.net.connectiontimeout"));
	if (s != null) {
	    preferredConnectionTimeout = Integer.parseInt(s);
	}
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
	case SO_SNDBUF:
	case SO_RCVBUF:
	    if (val == null || !(val instanceof Integer) ||
		!(((Integer)val).intValue() > 0)) {
		throw new SocketException("bad parameter for SO_SNDBUF " +
					  "or SO_RCVBUF");
	    }
	    break;
	case SO_KEEPALIVE:
	    if (val == null || !(val instanceof Boolean))
		throw new SocketException("bad parameter for SO_KEEPALIVE");
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
	case SO_SNDBUF:
        case SO_RCVBUF:
	    return new Integer(ret);
	case SO_KEEPALIVE:
  	    return (ret == -1) ? new Boolean(false): new Boolean(true);
	// should never get here
	default:
	    return null;
	}
    }

    /**
     * Connect to the SOCKS server using the SOCKS connection protocol.
     */
    private void doSOCKSConnect(InetAddress address, int port)
    throws IOException {
	connectToSocksServer();

	sendSOCKSCommandPacket(COMMAND_CONNECT, address, port);

	int protoStatus = getSOCKSReply();

	switch (protoStatus) {
	  case REQUEST_GRANTED:
	    // connection set up, return control to the socket client
	    return;

	  case REQUEST_REJECTED:
	  case REQUEST_REJECTED_NO_IDENTD:
		throw new SocketException("SOCKS server cannot connect to identd");

	  case REQUEST_REJECTED_DIFF_IDENTS:
	    throw new SocketException("User name does not match identd name");
	}
    }


    /**
     * Read the response from the socks server.  Return the result code.
     */
    private int getSOCKSReply() throws IOException {
     InputStream in = null;
	byte response[] = new byte[8];
        int bytesReceived = 0;
        int len = response.length;



try {
 	    in = (InputStream) java.security.AccessController.doPrivileged(
 		       new java.security.PrivilegedExceptionAction() {
 			       public Object run() throws IOException {
 				   return getInputStream();
 			       }
 			   });
 	} catch(java.security.PrivilegedActionException pae) {
 	    throw (IOException) pae.getException();
 	}





	for (int attempts = 0; bytesReceived<len &&  attempts<3; attempts++) {
	    int count = in.read(response, bytesReceived, len - bytesReceived);
	    if (count < 0)
		throw new SocketException("Malformed reply from SOCKS server");
	    bytesReceived += count;
	}

 	if (bytesReceived != len) {
 	    throw new SocketException("Reply from SOCKS server has bad length: " + bytesReceived);
  	}

	if (response[0] != 0) { // should be version0 
	    throw new SocketException("Reply from SOCKS server has bad version " + response[0]);
	}

	return response[1];	// the response code
    }

    /**
     * Just set up a connection to the SOCKS server and return.  The caller
     * needs to handle the SOCKS initiation protocol with the server after
     * the connection is established.
     */
    private void connectToSocksServer() throws IOException {

	String socksPortString = null;

	final String socksServerString = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction(socksServerProp));
	socksPortString = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction(socksPortProp,
							 socksDefaultPortStr));

	if (socksServerString == null) {
	    // REMIND: this is too trusting of its (internal) callers --
	    // needs to robustly assert that SOCKS are in fact being used,
	    // and signal an error (in some manner) if SOCKS are not being
	    // used.
	    return;
	}

	InetAddress socksServer = null; 
                // InetAddress.getByName(socksServerString);
try {	
       socksServer = (InetAddress)java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction() {
      public Object run() throws UnknownHostException {
      return InetAddress.getByName(socksServerString);
       }
 			});
 	} catch(java.security.PrivilegedActionException pae) {
 	    throw (UnknownHostException) pae.getException();
 	}

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


       OutputStream out = null;
 	try {
 	    out = (OutputStream) java.security.AccessController.doPrivileged(
 		       new java.security.PrivilegedExceptionAction() {
 			       public Object run() throws IOException {
 				   return getOutputStream();
 			       }
 			   });
 	} catch(java.security.PrivilegedActionException pae) {
 	    throw (IOException) pae.getException();
 	}

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

	String userName = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("user.name"));

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
	String ssp = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction(socksServerProp));
	return (ssp != null);
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
	if (isClosedOrPending()) {
	    throw new IOException("Socket Closed");
	}
	if (shut_rd) {
	    throw new IOException("Socket input is shutdown");
	}
	if (socketInputStream == null) {
	    socketInputStream = new SocketInputStream(this);
	}
	return socketInputStream;
    }

    /**
     * Gets an OutputStream for this socket.
     */
    protected synchronized OutputStream getOutputStream() throws IOException {
	if (isClosedOrPending()) {
	    throw new IOException("Socket Closed");
	}
        if (shut_wr) {
	    throw new IOException("Socket output is shutdown");
	}
	return new SocketOutputStream(this);
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     */
    protected synchronized int available() throws IOException {
        if (isClosedOrPending())
            throw new IOException("Stream closed.");
	return socketAvailable();
    }

    /**
     * Closes the socket.
     */
    protected void close() throws IOException {
        synchronized(fdLock) {
            if (fd != null) {
                if (fdUseCount == 0) {
                   if (closePending) {
                       return;
                   }
                   closePending = true;
                   socketClose(false);
                   fd = null;
                   return;
                } else {
		    /*
		     * If a thread has acquired the fd and a close
		     * isn't pending then use a deferred close.
		     * Also decrement fdUseCount to signal the last
		     * thread that releases the fd to close it.
		     */
                    if (!closePending) {
                        closePending = true;
                        fdUseCount--;
                        socketClose(true);
                    }
                }
            }
        }
    }
  
    /*
     * "Acquires" and returns the FileDescriptor for this impl
     *
     * A corresponding releaseFD is required to "release" the
     * FileDescriptor.
     */
    public final FileDescriptor acquireFD() {
        synchronized (fdLock) {
            fdUseCount++;
            return fd;
        }
    }

    /*
     * "Release" the FileDescriptor for this impl.
     *
     * If the use count goes to -1 then the socket is closed.
     */
    public final void releaseFD() {
        synchronized (fdLock) {
            fdUseCount--;
            if (fdUseCount == -1) {
                if (fd != null) {
                    try {
                        socketClose(false);
                    } catch (IOException e) {
                    } finally {
                       fd = null;
                    }
                }
            }
        }
    }
	
    public boolean isClosedOrPending() {
        /*
         * Lock on fdLock to ensure that we wait if a
         * close is in progress.
         */
        synchronized (fdLock) {
            if (closePending || fd == null) {
                return true;
            } else {
                return false;
            }
        }
    }
 

    /**
     * Shutdown read-half of the socket connection;
     */
    protected void shutdownInput() throws IOException {
      if (fd != null) {
	  socketShutdown(SHUT_RD);
	  if (socketInputStream != null) {
	      socketInputStream.setEOF(true);
	  }
	  shut_rd = true;
      }
    } 

    /**
     * Shutdown write-half of the socket connection;
     */
    protected void shutdownOutput() throws IOException {
      if (fd != null) {
	  socketShutdown(SHUT_WR);
	  shut_wr = true;
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
    private native void socketClose(boolean useDeferredClose)
	throws IOException;
    private native void socketShutdown(int howto)
	throws IOException;
    private static native void initProto();
    private native void socketSetOption(int cmd, boolean on, Object value)
	throws SocketException;
    private native int socketGetOption(int opt) throws SocketException;

    public final static int SHUT_RD = 0;
    public final static int SHUT_WR = 1;
}
