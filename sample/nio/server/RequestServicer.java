/*
 * @(#)RequestServicer.java	1.2 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
import java.nio.*;

/**
 * Primary driver class used by blocking Servers to receive,
 * prepare, send, and shutdown requests.
 *
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.2, 04/07/26
 */
class RequestServicer implements Runnable {

    private ChannelIO cio;

    private static int created = 0;

    RequestServicer(ChannelIO cio) {
	this.cio = cio;

	// Simple heartbeat to let user know we're alive.
	synchronized (RequestServicer.class) {
	    created++;
	    if ((created % 50) == 0) {
		System.out.println(".");
		created = 0;
	    } else {
		System.out.print(".");
	    }
	}
    }

    private void service() throws IOException {
	Reply rp = null;
	try {
	    ByteBuffer rbb = receive();		// Receive
	    Request rq = null;
	    try {				// Parse
		rq = Request.parse(rbb);
	    } catch (MalformedRequestException x) {
		rp = new Reply(Reply.Code.BAD_REQUEST,
			       new StringContent(x));
	    }
	    if (rp == null) rp = build(rq);	// Build
	    do {} while (rp.send(cio));		// Send
	    do {} while (!cio.shutdown());
	    cio.close();
	    rp.release();
	} catch (IOException x) {
	    String m = x.getMessage();
	    if (!m.equals("Broken pipe") &&
		    !m.equals("Connection reset by peer")) {
		System.err.println("RequestHandler: " + x.toString());
	    }

	    try {
		/*
		 * We had a failure here, so we'll try to be nice
		 * before closing down and send off a close_notify,
		 * but if we can't get the message off with one try,
		 * we'll just shutdown.
		 */
		cio.shutdown();
	    } catch (IOException e) {
		// ignore
	    }

	    cio.close();
	    if (rp != null) {
		rp.release();
	    }
	}
    }

    public void run() {
	try {
	    service();
	} catch (IOException x) {
	    x.printStackTrace();
	}
    }

    ByteBuffer receive() throws IOException {

	do {} while (!cio.doHandshake());

	for (;;) {
	    int read = cio.read();
	    ByteBuffer bb = cio.getReadBuf();
	    if ((read < 0) || (Request.isComplete(bb))) {
		bb.flip();
		return bb;
	    }
	}
    }

    Reply build(Request rq) throws IOException {

	Reply rp = null;
	Request.Action action = rq.action();
	if ((action != Request.Action.GET) &&
		(action != Request.Action.HEAD))
	    rp = new Reply(Reply.Code.METHOD_NOT_ALLOWED,
			   new StringContent(rq.toString()));
	else
	    rp = new Reply(Reply.Code.OK,
			   new FileContent(rq.uri()), action);
	try {
	    rp.prepare();
	} catch (IOException x) {
	    rp.release();
	    rp = new Reply(Reply.Code.NOT_FOUND,
			   new StringContent(x));
	    rp.prepare();
	}
	return rp;
    }
}
