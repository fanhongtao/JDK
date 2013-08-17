/*
 * @(#)URLStreamHandler.java	1.23 98/07/01
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
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * The abstract class <code>URLStreamHandler</code> is the common
 * superclass for all stream protocol handlers. A stream protocol
 * handler knows how to make a connection for a particular protocol
 * type, such as <code>http</code>, <code>ftp</code>, or
 * <code>gopher</code>.
 *
 * <p>In most cases, an instance of a <code>URLStreamHandler</code>
 * subclass is not created directly by an application. Rather, the
 * first time a protocol name is encountered when constructing a
 * <code>URL</code>, the appropriate stream protocol handler is
 * automatically loaded.
 *
 * @author  James Gosling
 *
 * @version 1.23, 07/01/98
 *
 * @see java.net.URL#URL(java.lang.String, java.lang.String, int,
 * java.lang.String)
 *
 * @since JDK1.0 
 */
public abstract class URLStreamHandler {
    /**
     * Opens a connection to the object referenced by the 
     * <code>URL</code> argument. 
     * This method should be overridden by a subclass.
     *
     * @param      u   the URL that this connects to.
     *
     * @return a <code>URLConnection</code> object for the
     * <code>URL</code>.
     *
     * @exception  IOException  if an I/O error occurs while opening the
     *               connection.
     * @since JDK1.0 
     */
    abstract protected URLConnection openConnection(URL u) throws IOException;

    /** 
     * Parses the string representation of a <code>URL</code> into a 
     * <code>URL</code> object. 
     *
     * <p>If there is any inherited context, then it has already been
     * copied into the <code>URL</code> argument.
     *
     * <p> The <code>parseURL</code> method of
     * <code>URLStreamHandler</code> parses the string representation
     * as if it were an <code>http</code> specification. Most URL
     * protocol families have a similar parsing. A stream protocol
     * handler for a protocol that has a different syntax must
     * override this routine.
     *
     * <p>If the file component of the URL argument contains a
     * question mark (as with CGI HTTP URLs), the context is
     * considered to be the URL's file component up to the first /
     * before the question mark, not including the question mark or
     * the directory before it. For example, if the URL was:
     *
     * <br><pre>    http://www.foo.com/dir/cgi-bin?foo=bar/baz</pre>
     *
     * and the spec argument was
     *
     * <br><pre>    quux.html</pre>
     *
     * the resulting URL would be:
     *
     * <br><pre>    http://www.foo.com/dir/quux.html</pre>.
     * 
     *
     * @param u the <code>URL</code> to receive the result of parsing
     * the spec.
     *
     * @param spec the <code>String</code> representing the URL that
     * must be parsed.
     *
     * @param start the character index at which to begin
     * parsing. This is just past the '<code>:</code>' (if there is
     * one) that specifies the determination of the protocol name.
     *
     * @param limit the character position to stop parsing at. This is
     * the end of the string or the position of the "<code>#</code>"
     * character, if present. All information after the sharp sign
     * indicates an anchor.
     *
     * @since JDK1.0 
     */
    protected void parseURL(URL u, String spec, int start, int limit) {
	String protocol = u.getProtocol();
	String host = u.getHost();
	int port = u.getPort();
	String file = u.getFile();
	String ref = u.getRef();

	int i;
	if ((start <= limit - 2) && (spec.charAt(start) == '/') &&
	    (spec.charAt(start + 1) == '/')) {
	    start += 2;
	    i = spec.indexOf('/', start);
	    if (i < 0) {
		i = limit;
	    }
	    int prn = spec.indexOf(':', start);
	    port = -1;
	    if ((prn < i) && (prn >= 0)) {
		try {
		    port = Integer.parseInt(spec.substring(prn + 1, i));
		} catch(Exception e) {
		    // ignore bogus port numbers
		}
		if (prn > start) {
		    host = spec.substring(start, prn);
		}
	    } else {
		host = spec.substring(start, i);
	    }
	    start = i;
	    file = null;
	} else if (host == null) {
	    host = "";
	}
	if (start < limit) {
	    /* 
	     * If the context URL is a CGI URL, the context to be the
	     * URL's file up to the / before ? character.
	     */
	    if (file != null) {
		int questionMarkIndex = file.indexOf('?');
		if (questionMarkIndex > -1) {
		    int lastSlashIndex = 
			file.lastIndexOf('?', questionMarkIndex);
		    file = file.substring(0, ++lastSlashIndex);
		}
	    }
	    if (spec.charAt(start) == '/') {
		file = spec.substring(start, limit);
	    } else if (file != null && file.length() > 0) {
		/* relative to the context file - use either 
		 * Unix separators || platform separators */
		int ind = Math.max(file.lastIndexOf('/'), 
				   file.lastIndexOf(File.separatorChar));

		file = file.substring(0, ind) + "/" + spec.substring(start, 
								     limit);
	    } else {
		file = "/" + spec.substring(start, limit);
	    }
	}
	if ((file == null) || (file.length() == 0)) {
	    file = "/"; 
	}
	while ((i = file.indexOf("/./")) >= 0) {
	    file = file.substring(0, i) + file.substring(i + 2);
	}
	while ((i = file.indexOf("/../")) >= 0) {
	    if ((limit = file.lastIndexOf('/', i - 1)) >= 0) {
		file = file.substring(0, limit) + file.substring(i + 3);
	    } else {
		file = file.substring(i + 3);
	    }
	}

	setURL(u, protocol, host, port, file, ref);
    }

    /**
     * Converts a <code>URL</code> of a specific protocol to a 
     * <code>String</code>. 
     *
     * @param   u   the URL.
     * @return  a string representation of the <code>URL</code> argument.
     * @since   JDK1.0
     */
    protected String toExternalForm(URL u) {
	String result = u.getProtocol() + ":";
	if ((u.getHost() != null) && (u.getHost().length() > 0)) {
	    result = result + "//" + u.getHost();
	    if (u.getPort() != -1) {
		result += ":" + u.getPort();
	    }
	}
	result += u.getFile();
	if (u.getRef() != null) {
	    result += "#" + u.getRef();
	}
	return result;
    }

    /**
     * Sets the fields of the <code>URL</code> argument to the
     * indicated values.  Only classes derived from URLStreamHandler
     * are supposed to be able to call the set method on a URL.
     *
     * @param   u         the URL to modify.
     * @param   protocol  the protocol name.
     * @param   host      the remote host value for the URL.
     * @param   port      the port on the remote machine.
     * @param   file      the file.
     * @param   ref       the reference.
     *
     * @see java.net.URL#set(java.lang.String, java.lang.String, int,
     * java.lang.String, java.lang.String)
     *
     * @since JDK1.0 
     */
    protected void setURL(URL u, String protocol, String host, int port,
			  String file, String ref) {
	if (this != u.handler) {
	    throw new SecurityException("handler for url different from " +
					"this handler");
	}
	// ensure that no one can reset the protocol on a given URL.
        u.set(u.getProtocol(), host, port, file, ref);
    }
}
