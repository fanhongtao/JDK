/*
 * @(#)URL.java	1.79 98/10/07
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
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Class <code>URL</code> represents a Uniform Resource 
 * Locator, a pointer to a "resource" on the World 
 * Wide Web. A resource can be something as simple as a file or a 
 * directory, or it can be a reference to a more complicated object, 
 * such as a query to a database or to a search engine. More 
 * information on the types of URLs and their formats can be found at:
 * <blockquote><pre>
 *     http://www.ncsa.uiuc.edu/demoweb/url-primer.html
 * </pre></blockquote>
 * <p>
 * In general, a URL can be broken into several parts. The previous 
 * example of a URL indicates that the protocol to use is 
 * <code>http</code> (HyperText Transport Protocol) and that the 
 * information resides on a host machine named 
 * <code>www.ncsa.uiuc.edu</code>. The information on that host 
 * machine is named <code>demoweb/url-primer.html</code>. The exact 
 * meaning of this name on the host machine is both protocol 
 * dependent and host dependent. The information normally resides in 
 * a file, but it could be generated on the fly. This component of 
 * the URL is called the <i>file</i> component, even though the 
 * information is not necessarily in a file. 
 * <p>
 * A URL can optionally specify a "port", which is the 
 * port number to which the TCP connection is made on the remote host 
 * machine. If the port is not specified, the default port for 
 * the protocol is used instead. For example, the default port for 
 * <code>http</code> is <code>80</code>. An alternative port could be 
 * specified as: 
 * <blockquote><pre>
 *     http://www.ncsa.uiuc.edu:8080/demoweb/url-primer.html
 * </pre></blockquote>
 * <p>
 * A URL may have appended to it an "anchor", also known 
 * as a "ref" or a "reference". The anchor is 
 * indicated by the sharp sign character "#" followed by 
 * more characters. For example, 
 * <blockquote><pre>
 *     http://java.sun.com/index.html#chapter1
 * </pre></blockquote>
 * <p>
 * This anchor is not technically part of the URL. Rather, it 
 * indicates that after the specified resource is retrieved, the 
 * application is specifically interested in that part of the 
 * document that has the tag <code>chapter1</code> attached to it. The 
 * meaning of a tag is resource specific. 
 * <p>
 * An application can also specify a "relative URL", 
 * which contains only enough information to reach the resource 
 * relative to another URL. Relative URLs are frequently used within 
 * HTML pages. For example, if the contents of the URL:
 * <blockquote><pre>
 *     http://java.sun.com/index.html
 * </pre></blockquote>
 * contained within it the relative URL:
 * <blockquote><pre>
 *     FAQ.html
 * </pre></blockquote>
 * it would be a shorthand for:
 * <blockquote><pre>
 *     http://java.sun.com/FAQ.html
 * </pre></blockquote>
 * <p>
 * The relative URL need not specify all the components of a URL. If 
 * the protocol, host name, or port number is missing, the value is 
 * inherited from the fully specified URL. The file component must be 
 * specified. The optional anchor is not inherited. 
 *
 * @author  James Gosling
 * @version 1.61, 04/16/98
 * @since   JDK1.0
 */
public final class URL implements java.io.Serializable {

    static final long serialVersionUID = -7627629688361524110L;

    /**
     * The property which specifies the package prefix list to be scanned
     * for protocol handlers.  The value of this property (if any) should
     * be a vertical bar delimited list of package names to search through
     * for a protocol handler to load.  The policy of this class is that
     * all protocol handlers will be in a class called <protocolname>.Handler,
     * and each package in the list is examined in turn for a matching
     * handler.  If none are found (or the property is not specified), the
     * default package prefix, sun.net.www.protocol, is used.  The search
     * proceeds from the first package in the list to the last and stops
     * when a match is found.
     */
    private static final String protocolPathProp = "java.protocol.handler.pkgs";

    /** 
     * The protocol to use (ftp, http, nntp, ... etc.) . 
     */
    private String protocol;

    /** 
     * The host name in which to connect to. 
     */
    private String host;

    /**
     * The host's IP address, used in equals and hashCode.
     * Computed on demand. An uninitialized or unknown hostAddress is null.
     */
    private transient InetAddress hostAddress;

    /** 
     * The protocol port to connect to. 
     */
    private int port = -1;

    /** 
     * The specified file name on that host. 
     */
    private String file;

    /** 
     * # reference. 
     */
    private String ref;

    /**
     * The URLStreamHandler for this URL.
     */
    transient URLStreamHandler handler;

    /* Our hash code. */
    private int hashCode = -1;

    /** 
     * Creates a <code>URL</code> object from the specified 
     * <code>protocol</code>, <code>host</code>, <code>port</code> 
     * number, and <code>file</code>. Specifying a <code>port</code> 
     * number of <code>-1</code> indicates that the URL should use 
     * the default port for the protocol. 
     * <p>
     * If this is the first URL object being created with the specified 
     * protocol, a <i>stream protocol handler</i> object, an instance of 
     * class <code>URLStreamHandler</code>, is created for that protocol:
     * <ol>
     * <li>If the application has previously set up an instance of
     *     <code>URLStreamHandlerFactory</code> as the stream handler factory,
     *     then the <code>createURLStreamHandler</code> method of that instance
     *     is called with the protocol string as an argument to create the
     *     stream protocol handler.
     * <li>If no <code>URLStreamHandlerFactory</code> has yet been set up,
     *     or if the factory's <code>createURLStreamHandler</code> method
     *     returns <code>null</code>, then the constructor finds the 
     *     value of the system property:
     *     <blockquote><pre>
     *         java.protocol.handler.pkgs
     *     </pre></blockquote>
     *     If the value of that system property is not <code>null</code>,
     *     it is interpreted as a list of packages separated by a vertical
     *     slash character '<code>|</code>'. The constructor tries to load 
     *     the class named:
     *     <blockquote><pre>
     *         &lt;<i>package</i>&gt;.&lt;<i>protocol</i>&gt;.Handler
     *     </pre></blockquote>
     *     where &lt;<i>package</i>&gt; is replaced by the name of the package
     *     and &lt;<i>protocol</i>&gt; is replaced by the name of the protocol.
     *     If this class does not exist, or if the class exists but it is not
     *     a subclass of <code>URLStreamHandler</code>, then the next package
     *     in the list is tried.
     * <li>If the previous step fails to find a protocol handler, then the
     *     constructor tries to load the class named:
     *     <blockquote><pre>
     *         sun.net.www.protocol.&lt;<i>protocol</i>&gt;.Handler
     *     </pre></blockquote>
     *     If this class does not exist, or if the class exists but it is not a
     *     subclass of <code>URLStreamHandler</code>, then a
     *     <code>MalformedURLException</code> is thrown.
     * </ol>
     *
     * @param      protocol   the name of the protocol.
     * @param      host       the name of the host.
     * @param      port       the port number.
     * @param      file       the host file.
     * @exception  MalformedURLException  if an unknown protocol is specified.
     * @see        java.lang.System#getProperty(java.lang.String)
     * @see        java.net.URL#setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory)
     * @see        java.net.URLStreamHandler
     * @see        java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     */
    public URL(String protocol, String host, int port, String file)
	throws MalformedURLException
    {
	this(protocol, host, port, file, null);
    }

    /** 
     * Creates an absolute URL from the specified <code>protocol</code> 
     * name, <code>host</code> name, and <code>file</code> name. The 
     * default port for the specified protocol is used. 
     * <p>
     * This method is equivalent to calling the four-argument 
     * constructor with the arguments being <code>protocol</code>, 
     * <code>host</code>, <code>-1</code>, and <code>file</code>. 
     *
     * @param      protocol   the protocol to use.
     * @param      host       the host to connect to.
     * @param      file       the file on that host.
     * @exception  MalformedURLException  if an unknown protocol is specified.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     */
    public URL(String protocol, String host, String file) throws MalformedURLException {
	this(protocol, host, -1, file);
    }

    /** 
     * Creates a <code>URL</code> object from the specified 
     * <code>protocol</code>, <code>host</code>, <code>port</code> 
     * number, <code>file</code>, and <code>handler</code>. Specifying
     * a <code>port</code> number of <code>-1</code> indicates that
     * the URL should use the default port for the protocol. Specifying
     * a <code>handler</code> of <code>null</code> indicates that the URL
     * should use a default stream handler for the protocol, as outlined
     * for:
     * <blockquote><pre>
     *     java.net.URL#URL(java.lang.String, java.lang.String, int,
     *                      java.lang.String)
     * </pre></blockquote>
     * 
     * <p>If the handler is not null and there is a security manager, 
     * the security manager's <code>checkPermission</code> 
     * method is called with a 
     * <code>NetPermission("specifyStreamHandler")</code> permission.
     * This may result in a SecurityException.
     *
     * @param      protocol   the name of the protocol.
     * @param      host       the name of the host.
     * @param      port       the port number.
     * @param      file       the host file.
     * @param	   handler    the stream handler.
     * @exception  MalformedURLException  if an unknown protocol is specified.
     * @exception  SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        specifying a stream handler explicitly.
     * @see        java.lang.System#getProperty(java.lang.String)
     * @see        java.net.URL#setURLStreamHandlerFactory(java.net.URLStreamHandlerFactory)
     * @see        java.net.URLStreamHandler
     * @see        java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
     * @see        SecurityManager#checkPermission
     * @see        java.net.NetPermission
     */
    public URL(String protocol, String host, int port, String file,
	       URLStreamHandler handler)
	throws MalformedURLException
    {
	if (handler != null) {
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null) {
		// check for permission to specify a handler
		checkSpecifyHandler(sm);
	    }
	}
	this.protocol = protocol;
	this.host = host;
	this.port = port;
	int ind = file.indexOf('#');
	this.file = ind < 0 ? file: file.substring(0, ind);
	this.ref = ind < 0 ? null: file.substring(ind + 1);
	// Note: we don't do validation of the URL here. Too risky to change
	// right now, but worth considering for future reference. -br
	if (handler == null &&
	    (handler = getURLStreamHandler(protocol)) == null) {
	    throw new MalformedURLException("unknown protocol: " + protocol);
	}
	this.handler = handler;
    }

    /**
     * Creates a <code>URL</code> object from the <code>String</code> 
     * representation. 
     * <p>
     * This constructor is equivalent to a call to the two-argument 
     * constructor with a <code>null</code> first argument. 
     *
     * @param      spec   the <code>String</code> to parse as a URL.
     * @exception  MalformedURLException  If the string specifies an
     *               unknown protocol.
     * @see        java.net.URL#URL(java.net.URL, java.lang.String)
     */
    public URL(String spec) throws MalformedURLException {
	this(null, spec);
    }

    /** 
     * Creates a URL by parsing the specification <code>spec</code> 
     * within a specified context. If the <code>context</code> argument 
     * is not <code>null</code> and the <code>spec</code> argument is a 
     * partial URL specification, then any of the strings missing 
     * components are inherited from the <code>context</code> argument. 
     * <p>
     * The specification given by the <code>String</code> argument is 
     * parsed to determine if it specifies a protocol. If the 
     * <code>String</code> contains an ASCII colon '<code>:</code>'
     * character before the first occurrence of an ASCII slash character 
     * '<code>/</code>', then the characters before the colon comprise 
     * the protocol. 
     * <ul>
     * <li>If the <code>spec</code> argument does not specify a protocol:
     *     <ul>
     *     <li>If the context argument is not <code>null</code>, then the
     *         protocol is copied from the context argument.
     *     <li>If the context argument is <code>null</code>, then a
     *         <code>MalformedURLException</code> is thrown.
     *     </ul>
     * <li>If the <code>spec</code> argument does specify a protocol:
     *     <ul>
     *     <li>If the context argument is <code>null</code>, or specifies a
     *         different protocol than the specification argument, the context
     *         argument is ignored.
     *     <li>If the context argument is not <code>null</code> and specifies
     *         the same protocol as the specification, the <code>host</code>,
     *         <code>port</code> number, and <code>file</code> are copied from
     *         the context argument into the newly created <code>URL</code>.
     *     </ul>
     * </ul>
     * <p>
     * The constructor then searches for an appropriate stream protocol 
     * handler of type <code>URLStreamHandler</code> as outlined for:
     * <blockquote><pre>
     *     java.net.URL#URL(java.lang.String, java.lang.String, int,
     *                      java.lang.String)
     * </pre></blockquote>
     * The stream protocol handler's 
     * <code>parseURL</code> method is called to parse the remaining 
     * fields of the specification that override any defaults set by the 
     * context argument. 

     * @param      context   the context in which to parse the specification.
     * @param      spec      a <code>String</code> representation of a URL.
     * @exception  MalformedURLException  if no protocol is specified, or an
     *               unknown protocol is found.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see        java.net.URLStreamHandler
     * @see        java.net.URLStreamHandler#parseURL(java.net.URL, java.lang.String, int, int)
     */
    public URL(URL context, String spec) throws MalformedURLException {
	this(context, spec, null);
    }

    /** 
     * Creates a URL by parsing the specification <code>spec</code> 
     * within a specified context. If the <code>context</code> argument 
     * is not <code>null</code> and the <code>spec</code> argument is a 
     * partial URL specification, then any of the strings missing 
     * components are inherited from the <code>context</code> argument. 
     * <p>
     * The specification given by the <code>String</code> argument is 
     * parsed to determine if it specifies a protocol. If the 
     * <code>String</code> contains an ASCII colon '<code>:</code>'
     * character before the first occurrence of an ASCII slash character 
     * '<code>/</code>', then the characters before the colon comprise 
     * the protocol. 
     * <ul>
     * <li>If the <code>spec</code> argument does not specify a protocol:
     *     <ul>
     *     <li>If the context argument is not <code>null</code>, then the
     *         protocol is copied from the context argument.
     *     <li>If the context argument is <code>null</code>, then a
     *         <code>MalformedURLException</code> is thrown.
     *     </ul>
     * <li>If the <code>spec</code> argument does specify a protocol:
     *     <ul>
     *     <li>If the context argument is <code>null</code>, or specifies a
     *         different protocol than the specification argument, the context
     *         argument is ignored.
     *     <li>If the context argument is not <code>null</code> and specifies
     *         the same protocol as the specification, the <code>host</code>,
     *         <code>port</code> number, and <code>file</code> are copied from
     *         the context argument into the newly created <code>URL</code>.
     *     </ul>
     * </ul>
     * <p>
     * If the argument <code>handler</code> is specified then it will be
     * used as the stream handler for the URL and will override that of
     * the context. Specifying a stream handler requires the NetPermission
     * <code>"specifyStreamHandler"</code> or a <code>SecurityException</code>
     * will be thrown.
     * <p>Otherwise, if <code>handler</code> is null and the context is
     * valid then the protocol handler of the context will be inherited.
     * The stream protocol handler's 
     * <code>parseURL</code> method is called to parse the remaining 
     * fields of the specification that override any defaults set by the 
     * context argument. 
     *
     * @param      context   the context in which to parse the specification.
     * @param      spec      a <code>String</code> representation of a URL.
     * @param	   handler   the stream handler for the URL.
     * @exception  MalformedURLException  if no protocol is specified, or an
     *               unknown protocol is found.
     * @exception  SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        specifying a stream handler.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see        java.net.URLStreamHandler
     * @see        java.net.URLStreamHandler#parseURL(java.net.URL, java.lang.String, int, int)
     */
    public URL(URL context, String spec, URLStreamHandler handler)
	throws MalformedURLException
    {
	String original = spec;
	int i, limit, c;
	int start = 0;
	String newProtocol = null;
	boolean aRef=false;

	// Check for permission to specify a handler
	if (handler != null) {
	    SecurityManager sm = System.getSecurityManager();
	    if (sm != null) {
		checkSpecifyHandler(sm);
	    }
	}

	try {
	    limit = spec.length();
	    while ((limit > 0) && (spec.charAt(limit - 1) <= ' ')) {
		limit--;	//eliminate trailing whitespace
	    }
	    while ((start < limit) && (spec.charAt(start) <= ' ')) {
		start++;	// eliminate leading whitespace
	    }

	    if (spec.regionMatches(true, start, "url:", 0, 4)) {
		start += 4;
	    }
	    if (start < spec.length() && spec.charAt(start) == '#') {
		/* we're assuming this is a ref relative to the context URL.
		 * This means protocols cannot start w/ '#', but we must parse
		 * ref URL's like: "hello:there" w/ a ':' in them.
		 */
		aRef=true;
	    }
	    for (i = start ; !aRef && (i < limit) && 
		     ((c = spec.charAt(i)) != '/') ; i++) {
		if (c == ':') {
		    String s = spec.substring(start, i).toLowerCase();
		    if (isValidProtocol(s)) {
			newProtocol = s;
			start = i + 1;
		    }
		    break;
		}
	    }

	    // Only use our context if the protocols match.
	    if ((context != null) && ((newProtocol == null) ||
				    newProtocol.equals(context.protocol))) {
		protocol = context.protocol;
		host = context.host;
		port = context.port;
		file = context.file;
		// inherit the protocol handler as well from the context
		// if not specified to the contructor
		if (handler == null) {
		    handler = context.handler;
		}
	    } else {
		protocol = newProtocol;
	    }

	    if (protocol == null) {
		throw new MalformedURLException("no protocol: "+original);
	    }

	    // Get the protocol handler if not specified or the protocol
	    // of the context could not be used
	    if (handler == null &&
	        (handler = getURLStreamHandler(protocol)) == null) {
		throw new MalformedURLException("unknown protocol: "+protocol);
	    }

	    this.handler = handler;

	    i = spec.indexOf('#', start);
	    if (i >= 0) {
		ref = spec.substring(i + 1, limit);
		limit = i;
	    }
	    handler.parseURL(this, spec, start, limit);

	} catch(MalformedURLException e) {
	    throw e;
	} catch(Exception e) {
	    throw new MalformedURLException(original + ": " + e);
	}
    }

    /*
     * Returns true if specified string is a valid protocol name.
     */
    private boolean isValidProtocol(String protocol) {
	int len = protocol.length();
        if (len < 2)
            return false;
	for (int i = 0; i < len; i++) {
	    char c = protocol.charAt(i);
	    if (!Character.isLetterOrDigit(c) && c != '.' && c != '+' &&
		c != '-') {
		return false;
	    }
	}
	return true;
    }

    /*
     * Checks for permission to specify a stream handler.
     */
    private void checkSpecifyHandler(SecurityManager sm) {
	if (specifyHandlerPerm == null) {
	    specifyHandlerPerm = new NetPermission("specifyStreamHandler");
	}
	sm.checkPermission(specifyHandlerPerm);
    }

    private static NetPermission specifyHandlerPerm;

    /**
     * Sets the fields of the URL. This is not a public method so that 
     * only URLStreamHandlers can modify URL fields. URLs are 
     * otherwise constant.
     *
     * @param protocol the protocol to use
     * @param host the host name to connecto to
       @param port the protocol port to connect to
     * @param file the specified file name on that host
     * @param ref the reference
     */
    protected void set(String protocol, String host, 
		       int port, String file, String ref) {
	synchronized (this) {
	    this.protocol = protocol;
	    this.host = host;
	    this.port = port;
	    this.file = file;
	    this.ref = ref;
	    /* This is very important. We must recompute this after the
	     * URL has been changed. */
	    hashCode = -1;
            hostAddress = null;
	}
    }

    /**
     * Returns the port number of this <code>URL</code>.
     * Returns -1 if the port is not set.
     *
     * @return  the port number
     */
    public int getPort() {
	return port;
    }

    /**
     * Returns the protocol name this <code>URL</code>.
     *
     * @return  the protocol of this <code>URL</code>.
     */
    public String getProtocol() {
	return protocol;
    }

    /**
     * Returns the host name of this <code>URL</code>, if applicable.
     * For "<code>file</code>" protocol, this is an empty string.
     *
     * @return  the host name of this <code>URL</code>.
     */
    public String getHost() {
	return host;
    }

    /**
     * Returns the file name of this <code>URL</code>.
     *
     * @return  the file name of this <code>URL</code>.
     */
    public String getFile() {
	return file;
    }

    /**
     * Returns the anchor (also known as the "reference") of this
     * <code>URL</code>.
     *
     * @return  the anchor (also known as the "reference") of this
     *          <code>URL</code>.
     */
    public String getRef() {
	return ref;
    }

    /** 
     * Compares two URLs.  The result is <code>true</code> if and
     * only if the argument is not <code>null</code> and is a
     * <code>URL</code> object that represents the same
     * <code>URL</code> as this object. Two URL objects are equal if
     * they have the same protocol and reference the same host, the
     * same port number on the host, and the same file and anchor on
     * the host.
     *
     * @param   obj   the URL to compare against.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof URL))
            return false;
	URL u2 = (URL)obj;

        return sameFile(u2) && 
            (ref == u2.ref || (ref != null && ref.equals(u2.ref)));
    }

    /** 
     * Creates an integer suitable for hash table indexing.
     *
     * @return  a hash code for this <code>URL</code>.
     */
    public synchronized int hashCode() {
	if (hashCode != -1)
            return hashCode;
        int h = 0;

        // Generate the protocol part.
        if (protocol != null)
	    h += protocol.hashCode();

        // Generate the host part.
	InetAddress addr = getHostAddress();
	if (addr != null)
	    h += addr.hashCode();
	else if (host != null)
	    h += host.toLowerCase().hashCode();

        // Generate the file part.
	if (file != null)
	    h += file.hashCode();

        // Generate the port part.
        h += port;

        // Generate the ref part.
	if (ref != null)
            h += ref.hashCode();

        hashCode = h;
	return h;
    }

    /**
     * Get the IP address of our host. An empty host field or a DNS failure
     * will result in a null return.
     */
    private synchronized InetAddress getHostAddress() {
	if (hostAddress != null)
            return hostAddress;
    
        if (host == null || host.equals("")) {
            return null;
        } else {
            try {
                hostAddress = InetAddress.getByName(host);
            } catch (UnknownHostException ex) {
                return null;
            } catch (SecurityException se) {
                return null;
            }
        }
	return hostAddress;
    }

    /**
     * Compares the host components of two URLs.
     * @param h1 the URL of the first host to compare 
     * @param h2 the URL of the second host to compare 
     * @return	true if and only if they are equal, false otherwise.
     * @exception UnknownHostException If an unknown host is found.
     */
    private static boolean hostsEqual(URL u1, URL u2) {
	InetAddress a1 = u1.getHostAddress();
        InetAddress a2 = u2.getHostAddress();
	// if we have internet address for both, compare them
	if (a1 != null && a2 != null) {
	    return a1.equals(a2);
        // else, if both have host names, compare them
	} else if (u1.host != null && u2.host != null) 
            return u1.host.equalsIgnoreCase(u2.host);
	 else
            return u1.host == null && u2.host == null;
    }

    /**
     * Compares two URLs, excluding the "ref" fields.
     * Returns <code>true</code> if this <code>URL</code> and the 
     * <code>other</code> argument both refer to the same resource.
     * The two <code>URL</code>s might not both contain the same anchor. 
     *
     * @param   other   the <code>URL</code> to compare against.
     * @return  <code>true</code> if they reference the same remote object;
     *          <code>false</code> otherwise.
     */
    public boolean sameFile(URL other) {
        // Compare the protocols.
        if (!((other.protocol == protocol) ||
              (protocol != null && protocol.equalsIgnoreCase(other.protocol))))
            return false;

	// Compare the hosts.
	if (!hostsEqual(this, other))
            return false;

	// Compare the files.
	if (!(file == other.file || (file != null && file.equals(other.file))))
	    return false;

	// Compare the ports.
	if (port != other.port)
	    return false;

        return true;
    }

    /**
     * Constructs a string representation of this <code>URL</code>. The 
     * string is created by calling the <code>toExternalForm</code> 
     * method of the stream protocol handler for this object. 
     *
     * @return  a string representation of this object.
     * @see     java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see     java.net.URLStreamHandler#toExternalForm(java.net.URL)
     */
    public String toString() {
	return toExternalForm();
    }

    /**
     * Constructs a string representation of this <code>URL</code>. The 
     * string is created by calling the <code>toExternalForm</code> 
     * method of the stream protocol handler for this object. 
     *
     * @return  a string representation of this object.
     * @see     java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see     java.net.URLStreamHandler#toExternalForm(java.net.URL)
     */
    public String toExternalForm() {
	return handler.toExternalForm(this);
    }

    /** 
     * Returns a <code>URLConnection</code> object that represents a 
     * connection to the remote object referred to by the <code>URL</code>.
     *
     * <p>A new connection is opened every time by calling the
     * <code>openConnection</code> method of the protocol handler for
     * this URL.
     *
     * <p>If for the URL's protocol (such as HTTP or JAR), there
     * exists a public, specialized URLConnection subclass belonging
     * to one of the following packages or one of their subpackages:
     * java.lang, java.io, java.util, java.net, the connection
     * returned will be of that subclass. For example, for HTTP an
     * HttpURLConnection will be returned, and for JAR a
     * JarURLConnection will be returned.
     *
     * @return     a <code>URLConnection</code> to the URL.
     * @exception  IOException  if an I/O exception occurs.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, 
     *             int, java.lang.String)
     * @see        java.net.URLConnection
     * @see java.net.URLStreamHandler#openConnection(java.net.URL) 
     */
    public URLConnection openConnection() throws java.io.IOException {
	return handler.openConnection(this);
    }

    /**
     * Opens a connection to this <code>URL</code> and returns an 
     * <code>InputStream</code> for reading from that connection. This 
     * method is a shorthand for:
     * <blockquote><pre>
     *     openConnection().getInputStream()
     * </pre></blockquote>
     *
     * @return     an input stream for reading from the URL connection.
     * @exception  IOException  if an I/O exception occurs.
     * @see        java.net.URL#openConnection()
     * @see        java.net.URLConnection#getInputStream()
     */
    public final InputStream openStream() throws java.io.IOException {
	return openConnection().getInputStream();
    }

    /**
     * Returns the contents of this URL. This method is a shorthand for:
     * <blockquote><pre>
     *     openConnection().getContent()
     * </pre></blockquote>
     *
     * @return     the contents of this URL.
     * @exception  IOException  if an I/O exception occurs.
     * @see        java.net.URLConnection#getContent()
     */
    public final Object getContent() throws java.io.IOException {
	return openConnection().getContent();
    }

    /**
     * The URLStreamHandler factory.
     */
    static URLStreamHandlerFactory factory;

    /**
     * Sets an application's <code>URLStreamHandlerFactory</code>.
     * This method can be called at most once in a given Java Virtual
     * Machine.
     *
     *<p> The <code>URLStreamHandlerFactory</code> instance is used to
     *construct a stream protocol handler from a protocol name.
     * 
     * <p> If there is a security manager, this method first calls
     * the security manager's <code>checkSetFactory</code> method 
     * to ensure the operation is allowed. 
     * This could result in a SecurityException.
     *
     * @param      fac   the desired factory.
     * @exception  Error  if the application has already set a factory.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkSetFactory</code> method doesn't allow the operation.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, 
     *             int, java.lang.String)
     * @see        java.net.URLStreamHandlerFactory
     * @see        SecurityManager#checkSetFactory
     */
    public static synchronized void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
	if (factory != null) {
	    throw new Error("factory already defined");
	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSetFactory();
	}
	handlers.clear();
	factory = fac;
    }

    /**
     * A table of protocol handlers.
     */
    static Hashtable handlers = new Hashtable();


    /**
     * Returns the Stream Handler.
     * @param protocol the protocol to use
     */
    static synchronized URLStreamHandler getURLStreamHandler(String protocol) {
	URLStreamHandler handler = (URLStreamHandler)handlers.get(protocol);
	if (handler == null) {
	    // Use the factory (if any)
	    if (factory != null) {
		handler = factory.createURLStreamHandler(protocol);
	    }

	    // Try java protocol handler
	    if (handler == null) {
		String packagePrefixList = null;

		packagePrefixList
		    = (String) java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction(protocolPathProp,""));
		if (packagePrefixList != "") {
		    packagePrefixList += "|";
		}

		// REMIND: decide whether to allow the "null" class prefix
		// or not.
		packagePrefixList += "sun.net.www.protocol";

		StringTokenizer packagePrefixIter =
		    new StringTokenizer(packagePrefixList, "|");

		while (handler == null && 
		       packagePrefixIter.hasMoreTokens()) {

		    String packagePrefix = 
		      packagePrefixIter.nextToken().trim();
		    try {
		        String clsName = packagePrefix + "." + protocol +
			  ".Handler";
			Class cls = null;
			try {
                            cls = Class.forName(clsName);
                        } catch (ClassNotFoundException e) {
			    ClassLoader cl = ClassLoader.getSystemClassLoader();
			    if (cl != null) {
			        cls = cl.loadClass(clsName);
			    }
			}
			if (cls != null) {
			    handler  = 
			      (URLStreamHandler)cls.newInstance();
			}
		    } catch (Exception e) {
			// any number of exceptions can get thrown here
		    }
		}
	    }
	    if (handler != null) {
		handlers.put(protocol, handler);
	    }
	}
	return handler;
    }

    /** 
     * WriteObject is called to save the state of the URL to an
     * ObjectOutputStream. The handler is not saved since it is
     * specific to this system.  
     *
     * @serialData the default write object value. When read back in,
     * the reader must ensure that calling getURLStreamHandler with
     * the protocol variable returns a valid URLStreamHandler and 
     * throw an IOException if it does not.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	s.defaultWriteObject();	// write the fields
    }

    /** 
     * readObject is called to restore the state of the URL from the
     * stream.  It reads the components of the URL and finds the local
     * stream handler.  
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	s.defaultReadObject();	// read the fields
	if ((handler = getURLStreamHandler(protocol)) == null) {
	    throw new IOException("unknown protocol: " + protocol);
	}
    }
}
