/*
 * @(#)FileContent.java	1.3 05/11/17
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
import java.nio.charset.*;

/**
 * A Content type that provides for transferring files.
 *
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class FileContent implements Content {

    private static File ROOT = new File("root");

    private File fn;

    FileContent(URI uri) {
	fn = new File(ROOT,
		      uri.getPath()
		      .replace('/',
			       File.separatorChar));
    }

    private String type = null;

    public String type() {
	if (type != null)
	    return type;
	String nm = fn.getName();
	if (nm.endsWith(".html"))
	    type = "text/html; charset=iso-8859-1";
	else if ((nm.indexOf('.') < 0) || nm.endsWith(".txt"))
	    type = "text/plain; charset=iso-8859-1";
	else
	    type = "application/octet-stream";
	return type;
    }

    private FileChannel fc = null;
    private long length = -1;
    private long position = -1;		// NB only; >= 0 if transferring

    public long length() {
	return length;
    }

    public void prepare() throws IOException {
	if (fc == null)
	    fc = new RandomAccessFile(fn, "r").getChannel();
	length = fc.size();
	position = 0;			// NB only
    }

    public boolean send(ChannelIO cio) throws IOException {
	if (fc == null)
	    throw new IllegalStateException();
	if (position < 0)		// NB only
	    throw new IllegalStateException();

	/*
	 * Short-circuit if we're already done.
	 */
	if (position >= length) {
	    return false;
	}

	position += cio.transferTo(fc, position, length - position);
	return (position < length);
    }

    public void release() throws IOException {
	if (fc != null) {
	    fc.close();
	    fc = null;
	}
    }
}
