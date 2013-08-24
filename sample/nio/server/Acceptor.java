/*
 * @(#)Acceptor.java	1.3 05/11/17
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
import java.nio.channels.*;
import javax.net.ssl.*;

/**
 * A Runnable class which sits in a loop accepting SocketChannels,
 * then registers the Channels with the read/write Selector.
 *
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class Acceptor implements Runnable {

    private ServerSocketChannel ssc;
    private Dispatcher d;

    private SSLContext sslContext;

    Acceptor(ServerSocketChannel ssc, Dispatcher d, SSLContext sslContext) {
	this.ssc = ssc;
	this.d = d;
	this.sslContext = sslContext;
    }

    public void run() {
	for (;;) {
	    try {
		SocketChannel sc = ssc.accept();

		ChannelIO cio = (sslContext != null ?
		    ChannelIOSecure.getInstance(
			sc, false /* non-blocking */, sslContext) :
		    ChannelIO.getInstance(
			sc, false /* non-blocking */));

		RequestHandler rh = new RequestHandler(cio);

		d.register(cio.getSocketChannel(), SelectionKey.OP_READ, rh);

	    } catch (IOException x) {
		x.printStackTrace();
		break;
	    }
	}
    }
}
