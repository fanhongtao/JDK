/*
 * @(#)Dispatcher1.java	1.2 04/07/26
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
import java.nio.channels.*;
import java.util.*;

/**
 * A single-threaded dispatcher.
 * <P>
 * When a SelectionKey is ready, it dispatches the job in this
 * thread.
 *
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.2, 04/07/26
 */
class Dispatcher1 implements Dispatcher {

    private Selector sel;

    Dispatcher1() throws IOException {
	sel = Selector.open();
    }

    // Doesn't really need to be runnable
    public void run() {
	for (;;) {
	    try {
		dispatch();
	    } catch (IOException x) {
		x.printStackTrace();
	    }
	}
    }

    private void dispatch() throws IOException {
	sel.select();
	for (Iterator i = sel.selectedKeys().iterator(); i.hasNext(); ) {
	    SelectionKey sk = (SelectionKey)i.next();
	    i.remove();
	    Handler h = (Handler)sk.attachment();
	    h.handle(sk);
	}
    }

    public void register(SelectableChannel ch, int ops, Handler h)
	    throws IOException {
	ch.register(sel, ops, h);
    }
}
