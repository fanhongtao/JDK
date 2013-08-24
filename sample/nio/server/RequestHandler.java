/*
 * @(#)RequestHandler.java	1.3 05/11/17
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
import java.nio.*;
import java.nio.channels.*;

/**
 * Primary driver class used by non-blocking Servers to receive,
 * prepare, send, and shutdown requests.
 *
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class RequestHandler implements Handler {

    private ChannelIO cio;
    private ByteBuffer rbb = null;

    private boolean requestReceived = false;
    private Request request = null;
    private Reply reply = null;

    private static int created = 0;

    RequestHandler(ChannelIO cio) {
	this.cio = cio;

	// Simple heartbeat to let user know we're alive.
	synchronized (RequestHandler.class) {
	    created++;
	    if ((created % 50) == 0) {
		System.out.println(".");
		created = 0;
	    } else {
		System.out.print(".");
	    }
	}
    }

    // Returns true when request is complete
    // May expand rbb if more room required
    //
    private boolean receive(SelectionKey sk) throws IOException {
	ByteBuffer tmp = null;

	if (requestReceived) {
	    return true;
	}

	if (!cio.doHandshake(sk)) {
	    return false;
	}

	if ((cio.read() < 0) || Request.isComplete(cio.getReadBuf())) {
	    rbb = cio.getReadBuf();
	    return (requestReceived = true);
	}
	return false;
    }

    // When parse is successfull, saves request and returns true
    //
    private boolean parse() throws IOException {
	try {
	    request = Request.parse(rbb);
	    return true;
	} catch (MalformedRequestException x) {
	    reply = new Reply(Reply.Code.BAD_REQUEST,
			      new StringContent(x));
	}
	return false;
    }

    // Ensures that reply field is non-null
    //
    private void build() throws IOException {
	Request.Action action = request.action();
	if ((action != Request.Action.GET) &&
		(action != Request.Action.HEAD)) {
	    reply = new Reply(Reply.Code.METHOD_NOT_ALLOWED,
			      new StringContent(request.toString()));
	}
	reply = new Reply(Reply.Code.OK,
			  new FileContent(request.uri()), action);
    }

    public void handle(SelectionKey sk) throws IOException {
	try {

	    if (request == null) {
		if (!receive(sk))
		    return;
		rbb.flip();
		if (parse())
		    build();
		try {
		    reply.prepare();
		} catch (IOException x) {
		    reply.release();
		    reply = new Reply(Reply.Code.NOT_FOUND,
				      new StringContent(x));
		    reply.prepare();
		}
		if (send()) {
		    // More bytes remain to be written
		    sk.interestOps(SelectionKey.OP_WRITE);
		} else {
		    // Reply completely written; we're done
		    if (cio.shutdown()) {
			cio.close();
			reply.release();
		    }
		}
	    } else {
		if (!send()) {	// Should be rp.send()
		    if (cio.shutdown()) {
			cio.close();
			reply.release();
		    }
		}
	    }
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
	    if (reply !=  null) {
		reply.release();
	    }
	}

    }

    private boolean send() throws IOException {
	try {
	    return reply.send(cio);
	} catch (IOException x) {
	    if (x.getMessage().startsWith("Resource temporarily")) {
		System.err.println("## RTA");
		return true;
	    }
	    throw x;
	}
    }
}
