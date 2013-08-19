/*
 * @(#)SocksSocketImpl.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.net;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.prefs.Preferences;
/* import org.ietf.jgss.*; */

/**
 * SOCKS (V4 & V5) TCP socket implementation (RFC 1928).
 * This is a subclass of PlainSocketImpl.
 * Note this class should <b>NOT</b> be public.
 */

class SocksSocketImpl extends PlainSocketImpl implements SocksConsts {
    private String server = null;
    private int port = DEFAULT_PORT;
    private InetSocketAddress external_address;
    private boolean useV4 = false;
    private Socket cmdsock = null;
    private InputStream cmdIn = null;
    private OutputStream cmdOut = null;

    SocksSocketImpl(String server, int port) {
	this.server = server;
	this.port = (port == -1 ? DEFAULT_PORT : port);
    }

    void setV4() {
	useV4 = true;
    }

    private synchronized void privilegedConnect(final String host,
					      final int port,
					      final int timeout)
 	 throws IOException
    {
 	try {
 	    AccessController.doPrivileged(
		  new java.security.PrivilegedExceptionAction() {
			  public Object run() throws IOException {
			      superConnectServer(host, port, timeout);
			      cmdIn = getInputStream();
			      cmdOut = getOutputStream();
			      return null;
			  }
		      });
 	} catch (java.security.PrivilegedActionException pae) {
 	    throw (IOException) pae.getException();
 	}
    }

    private void superConnectServer(String host, int port,
				    int timeout) throws IOException {
	super.connect(new InetSocketAddress(host, port), timeout);
    }

    private int readSocksReply(InputStream in, byte[] data) throws IOException {
	int len = data.length;
	int received = 0;
	for (int attempts = 0; received < len && attempts < 3; attempts++) {
	    int count = in.read(data, received, len - received);
	    if (count < 0)
		throw new SocketException("Malformed reply from SOCKS server");
	    received += count;
	}
	return received;
    }

    /**
     * Provides the authentication machanism required by the proxy.
     */
    private boolean authenticate(byte method, InputStream in, 
				 DataOutputStream out) throws IOException {
	byte[] data = null;
	int i;
	// No Authentication required. We're done then!
	if (method == NO_AUTH)
	    return true;
	/**
	 * User/Password authentication. Try, in that order :
	 * - The application provided Authenticator, if any
	 * - The user preferences java.net.socks.username & 
	 *   java.net.socks.password
	 * - the user.name & no password (backward compatibility behavior).
	 */
	if (method == USER_PASSW) {
	    String userName;
	    String password = null;
	    final InetAddress addr = InetAddress.getByName(server);
	    PasswordAuthentication pw = (PasswordAuthentication)
		java.security.AccessController.doPrivileged(
		    new java.security.PrivilegedAction() {
			    public Object run() {
				return Authenticator.requestPasswordAuthentication(
                                       server, addr, port, "SOCKS5", "SOCKS authentication", null);
			    }
			});
	    if (pw != null) {
		userName = pw.getUserName();
		password = new String(pw.getPassword());
	    } else {
		final Preferences prefs = Preferences.userRoot().node("/java/net/socks");
		try {
		    userName = 
			(String) AccessController.doPrivileged(
			       new java.security.PrivilegedExceptionAction() {
				       public Object run() throws IOException {
					   return prefs.get("username", null);
				       }
				   });
		} catch (java.security.PrivilegedActionException pae) {
		    throw (IOException) pae.getException();
		}

		if (userName != null) {
		    try {
			password = 
			    (String) AccessController.doPrivileged(
				   new java.security.PrivilegedExceptionAction() {
					   public Object run() throws IOException {
					       return prefs.get("password", null);
					   }
				       });
		    } catch (java.security.PrivilegedActionException pae) {
			throw (IOException) pae.getException();
		    }
		} else {
		    userName = 
			(String) java.security.AccessController.doPrivileged(
									     new sun.security.action.GetPropertyAction("user.name"));
		}
	    }
	    if (userName == null)
		return false;
	    out.write(1);
	    out.write(userName.length());
	    out.write(userName.getBytes());
	    if (password != null) {
		out.write(password.length());
		out.write(password.getBytes());
	    } else
		out.write(0);
	    out.flush();
	    data = new byte[2];
	    i = readSocksReply(in, data);
	    if (i != 2 || data[1] != 0) {
		/* RFC 1929 specifies that the connection MUST be closed if
		   authentication fails */
		out.close();
		in.close();
		return false;
	    }
	    /* Authentication succeeded */
	    return true;
	}
	/**
	 * GSSAPI authentication mechanism.
	 * Unfortunately the RFC seems out of sync with the Reference
	 * implementation. I'll leave this in for future completion.
	 */
// 	if (method == GSSAPI) {
// 	    try {
// 		GSSManager manager = GSSManager.getInstance();
// 		GSSName name = manager.createName("SERVICE:socks@"+server,
// 						     null);
// 		GSSContext context = manager.createContext(name, null, null,
// 							   GSSContext.DEFAULT_LIFETIME);
// 		context.requestMutualAuth(true);
// 		context.requestReplayDet(true);
// 		context.requestSequenceDet(true);
// 		context.requestCredDeleg(true);
// 		byte []inToken = new byte[0];
// 		while (!context.isEstablished()) {
// 		    byte[] outToken 
// 			= context.initSecContext(inToken, 0, inToken.length);
// 		    // send the output token if generated
// 		    if (outToken != null) {
// 			out.write(1);
// 			out.write(1);
// 			out.writeShort(outToken.length);
// 			out.write(outToken);
// 			out.flush();
// 			data = new byte[2];
// 			i = readSocksReply(in, data);
// 			if (i != 2 || data[1] == 0xff) {
// 			    in.close();
// 			    out.close();
// 			    return false;
// 			}
// 			i = readSocksReply(in, data);
// 			int len = 0;
// 			len = ((int)data[0] & 0xff) << 8;
// 			len += data[1];
// 			data = new byte[len];
// 			i = readSocksReply(in, data);
// 			if (i == len)
// 			    return true;
// 			in.close();
// 			out.close();
// 		    }
// 		}
// 	    } catch (GSSException e) {
// 		/* RFC 1961 states that if Context initialisation fails the connection
// 		   MUST be closed */
// 		e.printStackTrace();
// 		in.close();
// 		out.close();
// 	    }
// 	}
	return false;
    }

    private void connectV4(InputStream in, OutputStream out,
			   InetSocketAddress endpoint) throws IOException {
	out.write(PROTO_VERS4);
	out.write(CONNECT);
	out.write((endpoint.getPort() >> 8) & 0xff);
	out.write((endpoint.getPort() >> 0) & 0xff);
	out.write(endpoint.getAddress().getAddress());
	String userName = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("user.name"));
	out.write(userName.getBytes());
	out.write(0);
	out.flush();
	byte[] data = new byte[8];
	int n = readSocksReply(in, data);
	if (n != 8)
	    throw new SocketException("Reply from SOCKS server has bad length: " + n);
	if (data[0] != 0 && data[0] != 4) 
	    throw new SocketException("Reply from SOCKS server has bad version");
	SocketException ex = null;
	switch (data[1]) {
	case 90:
	    // Success!
	    external_address = endpoint;
	    break;
	case 91:
	    ex = new SocketException("SOCKS request rejected");
	    break;
	case 92:
	    ex = new SocketException("SOCKS server couldn't reach destination");
	    break;
	case 93:
	    ex = new SocketException("SOCKS authentication failed");
	    break;
	default:
	    ex = new SocketException("Replay from SOCKS server contains bad status");
	    break;
	}
	if (ex != null) {
	    in.close();
	    out.close();
	    throw ex;
	}
    }

    /**
     * Connects the Socks Socket to the specified endpoint. It will first
     * connect to the SOCKS proxy and negotiate the access. If the proxy
     * grants the connections, then the connect is successful and all
     * further traffic will go to the "real" endpoint.
     *
     * @param	endpoint	the <code>SocketAddress</code> to connect to.
     * @param	timeout		the timeout value in milliseconds
     * @throws	IOException	if the connection can't be established.
     * @throws	SecurityException if there is a security manager and it
     *				doesn't allow the connection
     * @throws  IllegalArgumentException if endpoint is null or a
     *          SocketAddress subclass not supported by this socket
     */
    protected void connect(SocketAddress endpoint, int timeout) throws IOException {
	SecurityManager security = System.getSecurityManager();
	if (endpoint == null || !(endpoint instanceof InetSocketAddress))
	    throw new IllegalArgumentException("Unsupported address type");
	InetSocketAddress epoint = (InetSocketAddress) endpoint;
	if (security != null) {
	    if (epoint.isUnresolved())
		security.checkConnect(epoint.getHostName(),
				      epoint.getPort());
	    else
		security.checkConnect(epoint.getAddress().getHostAddress(),
				      epoint.getPort());
	}

	// Connects to the SOCKS server
	
	try {
	    privilegedConnect(server, port, timeout);
	} catch (Exception e) {
	    throw new SocketException(e.getMessage());
	}
	// cmdIn & cmdOut were intialized during the privilegedConnect() call
	DataOutputStream out = new DataOutputStream(cmdOut);
	InputStream in = cmdIn;
	    
	if (useV4) {
	    // SOCKS Protocol version 4 doesn't know how to deal with 
	    // DOMAIN type of addresses (unresolved addresses here)
	    if (epoint.isUnresolved())
		throw new UnknownHostException(epoint.toString());
	    connectV4(in, out, epoint);
	    return;
	}

	// This is SOCKS V5
	out.write(PROTO_VERS);
	out.write(2);
	out.write(NO_AUTH);
	out.write(USER_PASSW);
	out.flush();
	byte[] data = new byte[2];
	int i = readSocksReply(in, data);
	if (i != 2 || ((int)data[1]) == NO_METHODS)
	    throw new SocketException("SOCKS : No acceptable methods");
	if (!authenticate(data[1], in, out)) {
	    throw new SocketException("SOCKS : authentication failed");
	}
	out.write(PROTO_VERS);
	out.write(CONNECT);
	out.write(0);
	/* Test for IPV4/IPV6/Unresolved */
	if (epoint.isUnresolved()) {
	    out.write(DOMAIN_NAME);
	    out.write(epoint.getHostName().length());
	    out.write(epoint.getHostName().getBytes());
	    out.write((epoint.getPort() >> 8) & 0xff);
	    out.write((epoint.getPort() >> 0) & 0xff);
	} else if (epoint.getAddress() instanceof Inet6Address) {
	    out.write(IPV6);
	    out.write(epoint.getAddress().getAddress());
	    out.write((epoint.getPort() >> 8) & 0xff);
	    out.write((epoint.getPort() >> 0) & 0xff);
	} else {
	    out.write(IPV4);
	    out.write(epoint.getAddress().getAddress());
	    out.write((epoint.getPort() >> 8) & 0xff);
	    out.write((epoint.getPort() >> 0) & 0xff);
	}
	out.flush();
	data = new byte[4];
	i = readSocksReply(in, data);
	if (i != 4)
	    throw new SocketException("Reply from SOCKS server has bad length");
	SocketException ex = null;
	int nport, len;
	byte[] addr;
	switch (data[1]) {
	case REQUEST_OK:
	    // success!
	    switch(data[3]) {
	    case IPV4:
		addr = new byte[4];
		i = readSocksReply(in, addr);
		if (i != 4)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		data = new byte[2];
		i = readSocksReply(in, data);
		if (i != 2)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		nport = ((int)data[0] & 0xff) << 8;
		nport += ((int)data[1] & 0xff);
		break;
	    case DOMAIN_NAME:
		len = data[1];
		byte[] host = new byte[len];
		i = readSocksReply(in, host);
		if (i != len)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		data = new byte[2];
		i = readSocksReply(in, data);
		if (i != 2)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		nport = ((int)data[0] & 0xff) << 8;
		nport += ((int)data[1] & 0xff);
		break;
	    case IPV6:
		len = data[1];
		addr = new byte[len];
		i = readSocksReply(in, addr);
		if (i != len)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		data = new byte[2];
		i = readSocksReply(in, data);
		if (i != 2)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		nport = ((int)data[0] & 0xff) << 8;
		nport += ((int)data[1] & 0xff);
		break;
	    default:
		ex = new SocketException("Reply from SOCKS server contains wrong code");
		break;
	    }
	    break;
	case GENERAL_FAILURE:
	    ex = new SocketException("SOCKS server general failure");
	    break;
	case NOT_ALLOWED:
	    ex = new SocketException("SOCKS: Connection not allowed by ruleset");
	    break;
	case NET_UNREACHABLE:
	    ex = new SocketException("SOCKS: Network unreachable");
	    break;
	case HOST_UNREACHABLE:
	    ex = new SocketException("SOCKS: Host unreachable");
	    break;
	case CONN_REFUSED:
	    ex = new SocketException("SOCKS: Connection refused");
	    break;
	case TTL_EXPIRED:
	    ex =  new SocketException("SOCKS: TTL expired");
	    break;
	case CMD_NOT_SUPPORTED:
	    ex = new SocketException("SOCKS: Command not supported");
	    break;
	case ADDR_TYPE_NOT_SUP:
	    ex = new SocketException("SOCKS: address type not supported");
	    break;
	}
	if (ex != null) {
	    in.close();
	    out.close();
	    throw ex;
	}
	external_address = epoint;
    }

    private void bindV4(InputStream in, OutputStream out,
			InetAddress baddr,
			int lport) throws IOException {
	super.bind(baddr, lport);
	/* FIXME Test for IPV4/IPV6 */
	byte[] addr1 = baddr.getAddress();
	/* Test for AnyLocal */
	InetAddress naddr = baddr;
	if (naddr.isAnyLocalAddress()) {
	    naddr = cmdsock.getLocalAddress();
	    addr1 = naddr.getAddress();
	}
	out.write(PROTO_VERS4);
	out.write(BIND);
	out.write((super.getLocalPort() >> 8) & 0xff);
	out.write((super.getLocalPort() >> 0) & 0xff);
	out.write(addr1);
	String userName = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("user.name"));
	out.write(userName.getBytes());
	out.write(0);
	out.flush();
	byte[] data = new byte[8];
	int n = readSocksReply(in, data);
	if (n != 8)
	    throw new SocketException("Reply from SOCKS server has bad length: " + n);
	if (data[0] != 0 && data[0] != 4)
	    throw new SocketException("Reply from SOCKS server has bad version");
	SocketException ex = null;
	switch (data[1]) {
	case 90:
	    // Success!
	    external_address = new InetSocketAddress(baddr, lport);
	    break;
	case 91:
	    ex = new SocketException("SOCKS request rejected");
	    break;
	case 92:
	    ex = new SocketException("SOCKS server couldn't reach destination");
	    break;
	case 93:
	    ex = new SocketException("SOCKS authentication failed");
	    break;
	default:
	    ex = new SocketException("Replay from SOCKS server contains bad status");
	    break;
	}
	if (ex != null) {
	    in.close();
	    out.close();
	    throw ex;
	}
	
    }

    /**
     * Binds this socket to the specified port number on the specified host. 
     *
     * @param      baddr   the IP address of the remote host.
     * @param      lport   the port number.
     * @exception  IOException  if an I/O error occurs when binding this socket.
     */
    protected synchronized void bind(InetAddress baddr, int lport) throws IOException {
	if (socket != null) {
	    // this is a client socket, not a server socket, don't
	    // call the SOCKS proxy for a bind!
	    super.bind(baddr, lport);
	    return;
	}

	// Connects to the SOCKS server
	
	try {
	    AccessController.doPrivileged(new PrivilegedExceptionAction() {
		    public Object run() throws Exception {
			cmdsock = new Socket(new PlainSocketImpl());
			cmdsock.connect(new InetSocketAddress(server, port));
			cmdIn = cmdsock.getInputStream();
			cmdOut = cmdsock.getOutputStream();
			return null;
		    }
		});
	} catch (Exception e) {
	    throw new SocketException(e.getMessage());
	}
	DataOutputStream out = new DataOutputStream(cmdOut);
	InputStream in = cmdIn;
	if (useV4) {
	    bindV4(in, out, baddr, lport);
	    return;
	}
	out.write(PROTO_VERS);
	out.write(2);
	out.write(NO_AUTH);
	out.write(USER_PASSW);
	out.flush();
	byte[] data = new byte[2];
	int i = readSocksReply(in, data);
	if (i != 2 || ((int)data[1]) == NO_METHODS)
	    throw new SocketException("SOCKS : No acceptable methods");
	if (!authenticate(data[1], in, out)) {
	    throw new SocketException("SOCKS : authentication failed");
	}
	// We're OK. Let's issue the BIND command after we've bound ourself localy
	super.bind(baddr, lport);
	out.write(PROTO_VERS);
	out.write(BIND);
	out.write(0);
	InetAddress naddr = baddr;
	if (naddr.isAnyLocalAddress())
	    naddr = cmdsock.getLocalAddress();
	byte[] addr1 = naddr.getAddress();
	if (naddr.family == InetAddress.IPv4) {
	    out.write(IPV4);
	    out.write(addr1);
	    out.write((super.getLocalPort() >> 8) & 0xff);
	    out.write((super.getLocalPort() >> 0) & 0xff);
	    out.flush();
	} else if (naddr.family == InetAddress.IPv6) {
	    /* Test for AnyLocal */
	    out.write(IPV6);
	    out.write(addr1);
	    out.write((super.getLocalPort() >> 8) & 0xff);
	    out.write((super.getLocalPort() >> 0) & 0xff);
	    out.flush();
	} else {
	    cmdsock.close();
	    throw new SocketException("unsupported address type : " + naddr);
	}
	data = new byte[4];
	i = readSocksReply(in, data);
	SocketException ex = null;
	int len, nport;
	byte[] addr;
	switch (data[1]) {
	case REQUEST_OK:
	    // success!
	    InetSocketAddress real_end = null;
	    switch(data[3]) {
	    case IPV4:
		addr = new byte[4];
		i = readSocksReply(in, addr);
		if (i != 4)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		data = new byte[2];
		i = readSocksReply(in, data);
		if (i != 2)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		nport = ((int)data[0] & 0xff) << 8;
		nport += ((int)data[1] & 0xff);
		external_address =
		    new InetSocketAddress(new Inet4Address("", addr) , nport);
		break;
	    case DOMAIN_NAME:
		len = data[1];
		byte[] host = new byte[len];
		i = readSocksReply(in, host);
		if (i != len)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		data = new byte[2];
		i = readSocksReply(in, data);
		if (i != 2)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		nport = ((int)data[0] & 0xff) << 8;
		nport += ((int)data[1] & 0xff);
		external_address = new InetSocketAddress(new String(host), nport);
		break;
	    case IPV6:
		len = data[1];
		addr = new byte[len];
		i = readSocksReply(in, addr);
		if (i != len)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		data = new byte[2];
		i = readSocksReply(in, data);
		if (i != 2)
		    throw new SocketException("Reply from SOCKS server badly formatted");
		nport = ((int)data[0] & 0xff) << 8;
		nport += ((int)data[1] & 0xff);
		external_address = 
		    new InetSocketAddress(new Inet6Address("", addr), nport);
		break;
	    }
	    break;
	case GENERAL_FAILURE:
	    ex = new SocketException("SOCKS server general failure");
	    break;
	case NOT_ALLOWED:
	    ex = new SocketException("SOCKS: Bind not allowed by ruleset");
	    break;
	case NET_UNREACHABLE:
	    ex = new SocketException("SOCKS: Network unreachable");
	    break;
	case HOST_UNREACHABLE:
	    ex = new SocketException("SOCKS: Host unreachable");
	    break;
	case CONN_REFUSED:
	    ex = new SocketException("SOCKS: Connection refused");
	    break;
	case TTL_EXPIRED:
	    ex =  new SocketException("SOCKS: TTL expired");
	    break;
	case CMD_NOT_SUPPORTED:
	    ex = new SocketException("SOCKS: Command not supported");
	    break;
	case ADDR_TYPE_NOT_SUP:
	    ex = new SocketException("SOCKS: address type not supported");
	    break;
	}
	if (ex != null) {
	    in.close();
	    out.close();
	    cmdsock.close();
	    cmdsock = null;
	    throw ex;
	}
	cmdIn = in;
	cmdOut = out;
    }

    /**
     * Accepts a connection. 
     *
     * @param      s   the accepted connection.
     * @exception  IOException  if an I/O error occurs when accepting the
     *               connection.
     */
    protected void accept(SocketImpl s) throws IOException {
	if (cmdsock == null)
	    throw new SocketException("Socks channel closed");
	InputStream in = cmdIn;
	in.read();
	int i = in.read();
	in.read();
	SocketException ex = null;
	int nport;
	byte[] addr;
	InetSocketAddress real_end = null;
	switch (i) {
	case REQUEST_OK:
	    // success!
	    i = in.read();
	    switch(i) {
	    case IPV4:
		addr = new byte[4];
		readSocksReply(in, addr);
		nport = in.read() << 8;
		nport += in.read();
		real_end = 
		    new InetSocketAddress(new Inet4Address("", addr) , nport);
		break;
	    case DOMAIN_NAME:
		int len = in.read();
		addr = new byte[len];
		readSocksReply(in, addr);
		nport = in.read() << 8;
		nport += in.read();
		real_end = new InetSocketAddress(new String(addr), nport);
		break;
	    case IPV6:
		addr = new byte[16];
		readSocksReply(in, addr);
		nport = in.read() << 8;
		nport += in.read();
		real_end = 
		    new InetSocketAddress(new Inet6Address("", addr), nport);
		break;
	    }
	    break;
	case GENERAL_FAILURE:
	    ex = new SocketException("SOCKS server general failure");
	    break;
	case NOT_ALLOWED:
	    ex = new SocketException("SOCKS: Accept not allowed by ruleset");
	    break;
	case NET_UNREACHABLE:
	    ex = new SocketException("SOCKS: Network unreachable");
	    break;
	case HOST_UNREACHABLE:
	    ex = new SocketException("SOCKS: Host unreachable");
	    break;
	case CONN_REFUSED:
	    ex = new SocketException("SOCKS: Connection refused");
	    break;
	case TTL_EXPIRED:
	    ex =  new SocketException("SOCKS: TTL expired");
	    break;
	case CMD_NOT_SUPPORTED:
	    ex = new SocketException("SOCKS: Command not supported");
	    break;
	case ADDR_TYPE_NOT_SUP:
	    ex = new SocketException("SOCKS: address type not supported");
	    break;
	}
	if (ex != null) {
	    cmdIn.close();
	    cmdOut.close();
	    cmdsock.close();
	    cmdsock = null;
	    throw ex;
	}
	
	/**
	 * This is where we have to do some fancy stuff.
	 * The datastream from the socket "accepted" by the proxy will
	 * come through the cmdSocket. So we have to swap the socketImpls
	 */
	if (s instanceof SocksSocketImpl) {
	    ((SocksSocketImpl)s).external_address = real_end;
	}
	if (s instanceof PlainSocketImpl) {
	    ((PlainSocketImpl)s).setInputStream((SocketInputStream) in);
	}
	s.fd = cmdsock.getImpl().fd;
	s.address = cmdsock.getImpl().address;
	s.port = cmdsock.getImpl().port;
	s.localport = cmdsock.getImpl().localport;
	// Need to do that so that the socket won't be closed
	// when the ServerSocket is closed by the user.
	// It kinds of detaches the Socket because it is now
	// used elsewhere.
	cmdsock = null;
    }

    
    /**
     * Returns the value of this socket's <code>address</code> field.
     *
     * @return  the value of this socket's <code>address</code> field.
     * @see     java.net.SocketImpl#address
     */
    protected InetAddress getInetAddress() {
	if (external_address != null)
	    return external_address.getAddress();
	else
	    return super.getInetAddress();
    }

    /**
     * Returns the value of this socket's <code>port</code> field.
     *
     * @return  the value of this socket's <code>port</code> field.
     * @see     java.net.SocketImpl#port
     */
    protected int getPort() {
	if (external_address != null)
	    return external_address.getPort();
	else
	    return super.getPort();
    }

    protected int getLocalPort() {
	if (socket != null)
	    return super.getLocalPort();
	if (external_address != null)
	    return external_address.getPort();
	else
	    return super.getLocalPort();
    }

    protected void close() throws IOException {
	if (cmdsock != null)
	    cmdsock.close();
	cmdsock = null;
	super.close();
    }

}
