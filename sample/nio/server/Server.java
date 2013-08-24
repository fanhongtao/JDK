/*
 * @(#)Server.java	1.3 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.security.*;
import javax.net.ssl.*;

/**
 * The main server base class.
 * <P>
 * This class is responsible for setting up most of the server state
 * before the actual server subclasses take over.
 *
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
public abstract class Server {

    ServerSocketChannel ssc;
    SSLContext sslContext = null;

    static private int PORT = 8000;
    static private int BACKLOG = 1024;
    static private boolean SECURE = false;

    Server(int port, int backlog,
	    boolean secure) throws Exception {

	if (secure) {
	    createSSLContext();
	}

	ssc = ServerSocketChannel.open();
	ssc.socket().setReuseAddress(true);
	ssc.socket().bind(new InetSocketAddress(port), backlog);
    }

    /*
     * If this is a secure server, we now setup the SSLContext we'll
     * use for creating the SSLEngines throughout the lifetime of
     * this process.
     */
    private void createSSLContext() throws Exception {

	char[] passphrase = "passphrase".toCharArray();

	KeyStore ks = KeyStore.getInstance("JKS");
	ks.load(new FileInputStream("testkeys"), passphrase);

	KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	kmf.init(ks, passphrase);

	TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
	tmf.init(ks);

	sslContext = SSLContext.getInstance("TLS");
	sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }

    abstract void runServer() throws Exception;

    static private void usage() {
	System.out.println(
	    "Usage:  Server <type> [options]\n"
		+ "	type:\n"
		+ "		B1	Blocking/Single-threaded Server\n"
		+ "		BN	Blocking/Multi-threaded Server\n"
		+ "		BP	Blocking/Pooled-Thread Server\n"
		+ "		N1	Nonblocking/Single-threaded Server\n"
		+ "		N2	Nonblocking/Dual-threaded Server\n"
		+ "\n"
		+ "	options:\n"
		+ "		-port port		port number\n"
		+ "		    default:  " + PORT + "\n"
		+ "		-backlog backlog	backlog\n"
		+ "		    default:  " + BACKLOG + "\n"
		+ "		-secure			encrypt with SSL/TLS");
	System.exit(1);
    }

    /*
     * Parse the arguments, decide what type of server to run,
     * see if there are any defaults to change.
     */
    static private Server createServer(String args[]) throws Exception {
	if (args.length < 1) {
	    usage();
	}

	int port = PORT;
	int backlog = BACKLOG;
	boolean secure = SECURE;

	for (int i = 1; i < args.length; i++) {
	    if (args[i].equals("-port")) {
		checkArgs(i, args.length);
		port = Integer.valueOf(args[++i]);
	    } else if (args[i].equals("-backlog")) {
		checkArgs(i, args.length);
		backlog = Integer.valueOf(args[++i]);
	    } else if (args[i].equals("-secure")) {
		secure = true;
	    } else {
		usage();
	    }
	}

	Server server = null;

	if (args[0].equals("B1")) {
	    server = new B1(port, backlog, secure);
	} else if (args[0].equals("BN")) {
	    server = new BN(port, backlog, secure);
	} else if (args[0].equals("BP")) {
	    server = new BP(port, backlog, secure);
	} else if (args[0].equals("N1")) {
	    server = new N1(port, backlog, secure);
	} else if (args[0].equals("N2")) {
	    server = new N2(port, backlog, secure);
	}

	return server;
    }

    static private void checkArgs(int i, int len) {
	if ((i + 1) >= len) {
	   usage();
	}
    }

    static public void main(String args[]) throws Exception {
	Server server = createServer(args);

	if (server == null) {
	    usage();
	}

	System.out.println("Server started.");
	server.runServer();
    }
}
