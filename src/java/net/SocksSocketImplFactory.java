/*
 * @(#)SocksSocketImplFactory.java	1.4 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.net;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * This factory creates an SocketImpl that implements the SOCKS protocol
 * (both V5 & V4). It implements RFC 1928.
 *
 * @see java.net.Socket#setSocketImplFactory(SocketImplFactory)
 * @see java.net.ServerSocket#setSocketImplFactory(SocketImplFactory)
 */

class SocksSocketImplFactory implements SocketImplFactory, SocksConsts {
    private String server;
    private int port = -1;
    private boolean useV4 = false;

    /**
     * Creates a SocksSocketImplFactory with a specific server & port.
     * This should point to a SOCKS v5 proxy server.
     *
     * @param	server	the server hostname
     * @param	port	the port number. -1 for the default SOCKS port.
     */
    SocksSocketImplFactory(String server, int port) {
	this.server = server;
	this.port = port == -1 ? DEFAULT_PORT : port;
	guessVersion();
    }

    /**
     * Creates a SocksSocketImplFactory with a specific server & port.
     *
     * @param	server	the server hostname
     * @param	port	the port number. -1 for the default SOCKS port.
     * @param	v4	<code>true</code> if the protocol should be version 4
     *			<code>false</code> for version 5.
     */
    SocksSocketImplFactory(String server, int port, boolean v4) {
	this.server = server;
	this.port = port == -1 ? DEFAULT_PORT : port;
	this.useV4 = v4;
    }

    /*
     * Checks whether the System properties changed.
     * If they did, we need to renegociate the protocol version
     */

    private synchronized void checkProps() {
	boolean changed = false;
	int newport = DEFAULT_PORT;
	String socksPort = null;
	String socksHost = 
	    (String) java.security.AccessController.doPrivileged(
		     new sun.security.action.GetPropertyAction("socksProxyHost"));
	socksPort =
	    (String) java.security.AccessController.doPrivileged(
		     new sun.security.action.GetPropertyAction("socksProxyPort"));
	if (socksPort != null) {
	    try {
		newport = Integer.parseInt(socksPort);
	    } catch (Exception e) {
		newport = DEFAULT_PORT;
	    }
	}
	if (socksHost != null && !socksHost.equals(this.server)) {
	    this.server = socksHost;
	    changed = true;
	}
	if (newport != this.port) {
	    this.port = newport;
	    changed = true;
	}
	if (changed) {
	    guessVersion();
	}
    }

    private void guessVersion() {
	Socket s;
	// Connects to the SOCKS server
	
	try {
	    s = (Socket) AccessController.doPrivileged(new PrivilegedExceptionAction() {
		    public Object run() throws Exception {
			Socket so = new Socket(new PlainSocketImpl());
			so.connect(new InetSocketAddress(server, port));
			return so;
		    }
		});
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
	InputStream in = null;
	OutputStream out = null;
	try {
	    // If it's taking too long to get an answer, then it's probably
	    // the wrong version
	    s.setSoTimeout(1000);
	    out = s.getOutputStream();
	    in = s.getInputStream();
	    // Try V5 first
	    out.write(PROTO_VERS);
	    out.write(2);
	    out.write(NO_AUTH);
	    out.write(USER_PASSW);
	    out.flush();
	    int i = in.read();
	    if (i == PROTO_VERS) {
		// All rigth it's V5
		useV4 = false;
		i = in.read();
	    } else {
		// V5 doesn't work, let's assume it's V4
		useV4 = true;
	    }
	    in.close();
	    out.close();
	    s.close();
	} catch (java.net.SocketTimeoutException te) {
	    // Timeout. Took too long let's assume it's V4 then
	    useV4 = true;
	    try {
		in.close();
		out.close();
		s.close();
	    } catch (Exception e2) {
	    }
	} catch (java.io.IOException ioe) {
	    // Nothing much we can do here, but we tried
	}
    }

    /**
     * Creates a new <code>SocketImpl</code> instance.
     *
     * @return  a new instance of <code>SocketImpl</code>.
     * @see     java.net.SocketImpl
     */
    public SocketImpl createSocketImpl() {
	checkProps();
	SocksSocketImpl impl = new SocksSocketImpl(server, port);
	if (useV4)
	    impl.setV4();
	return impl;
    }
}
