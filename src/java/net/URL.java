/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * <code>http</code> (HyperText Transfer Protocol) and that the 
 * information resides on a host machine named 
 * <code>www.ncsa.uiuc.edu</code>. The information on that host 
 * machine is named <code>/demoweb/url-primer.html</code>. The exact 
 * meaning of this name on the host machine is both protocol 
 * dependent and host dependent. The information normally resides in 
 * a file, but it could be generated on the fly. This component of 
 * the URL is called the <i>path</i> component.
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
 * A URL may have appended to it a "fragment", also known 
 * as a "ref" or a "reference". The fragment is indicated by the sharp
 * sign character "#" followed by more characters. For example, 
 * <blockquote><pre>
 *     http://java.sun.com/index.html#chapter1
 * </pre></blockquote>
 * <p>
 * This fragment is not technically part of the URL. Rather, it 
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
 * specified. The optional fragment is not inherited. 
 *
 * @author  James Gosling
 * @version 1.94, 02/02/00
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
     * The protocol port to connect to. 
     */
    private int port = -1;

    /** 
     * The specified file name on that host. 
     */
    private String file;

    /** 
     * The query part of this URL. 
     */
    private transient String query;

    /** 
     * The authority part of this URL. 
     */
    private String authority;

    /** 
     * The path part of this URL. 
     */
    private transient String path;

    /** 
     * The userinfo part of this URL. 
     */
    private transient String userInfo;

    /** 
     * # reference. 
     */
    private String ref;

    /**
     * The host's IP address, used in equals and hashCode.
     * Computed on demand. An uninitialized or unknown hostAddress is null.
     */
    transient InetAddress hostAddress;

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
     * @param      protocol   the name of the protocol to use.
     * @param      host       the name of the host.
     * @param      port       the port number on the host.
     * @param      file       the file on the host
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
     * Creates a URL from the specified <code>protocol</code> 
     * name, <code>host</code> name, and <code>file</code> name. The 
     * default port for the specified protocol is used. 
     * <p>
     * This method is equivalent to calling the four-argument 
     * constructor with the arguments being <code>protocol</code>, 
     * <code>host</code>, <code>-1</code>, and <code>file</code>. 
     *
     * @param      protocol   the name of the protocol to use.
     * @param      host       the name of the host.
     * @param      file       the file on the host.
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
     *     java.net.URL#URL(java.lang.String, java.lang.String, int,
     *                      java.lang.String)
     * 
     * <p>If the handler is not null and there is a security manager, 
     * the security manager's <code>checkPermission</code> 
     * method is called with a 
     * <code>NetPermission("specifyStreamHandler")</code> permission.
     * This may result in a SecurityException.
     *
     * @param      protocol   the name of the protocol to use.
     * @param      host       the name of the host.
     * @param      port       the port number on the host.
     * @param      file       the file on the host
     * @param	   handler    the stream handler for the URL.
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
	int ind = file.indexOf('#');
	this.file = ind < 0 ? file: file.substring(0, ind);
	this.ref = ind < 0 ? null: file.substring(ind + 1);
        int q = file.lastIndexOf('?');
        if (q != -1) {
            query = file.substring(q+1);
            path = file.substring(0, q);
        } else
            path = file;
	// Note: we don't do validation of the URL here. Too risky to change
	// right now, but worth considering for future reference. -br
	if (handler == null &&
	    (handler = getURLStreamHandler(protocol)) == null) {
	    throw new MalformedURLException("unknown protocol: " + protocol);
	}
	this.handler = handler;
        this.port = port;
        if (host != null && host.length() > 0) {
            authority = (port == -1) ? host : host + ":" + port;
        }

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
     * Creates a URL by parsing the given spec within a specified context. 
     *
     * The new URL is created from the given context URL and the spec
     * argument as described in RFC2396 &quot;Uniform Resource Identifiers : Generic
     * Syntax&quot; :
     * <blockquote><pre>
     *          &lt;scheme&gt;://&lt;authority&gt;&lt;path&gt;?&lt;query&gt;#&lt;fragment&gt;
     * </pre></blockquote>
     * The reference is parsed into the scheme, authority, path, query and
     * fragment parts. If the path component is empty and the scheme,
     * authority, and query components are undefined, then the new URL is a
     * reference to the current document. Otherwise the any fragment and query
     * parts present in the spec are used in the new URL.
     * 
     * If the scheme component is defined in the given spec and does not match
     * the scheme of the context, then the new URL is created as an absolute
     * URL based on the spec alone. Otherwise the scheme component is inherited
     * from the context URL.
     *
     * If the authority component is present in the spec then the spec is
     * treated as absolute and the spec authority and path will replace the
     * context authority and path. If the authority component is absent in the
     * spec then the authority of the new URL will be inherited from the
     * context.
     *
     * If the spec's path component begins with a slash character &quot;/&quot; then the
     * path is treated as absolute and the spec path replaces the context path.
     * Otherwise the path is treated as a relative path and is appended to the
     * context path. The path is canonicalized through the removal of directory
     * changes made by occurences of &quot;..&quot; and &quot;.&quot;.
     * 
     * For a more detailed description of URL parsing, refer to RFC2396.
     *
     * @param      context   the context in which to parse the specification.
     * @param      spec      the <code>String</code> to parse as a URL.
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
     * Creates a URL by parsing the given spec with the specified handler
     * within a specified context. If the handler is null, the parsing
     * occurs as with the two argument constructor.
     *
     * @param      context   the context in which to parse the specification.
     * @param      spec      the <code>String</code> to parse as a URL.
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
	    protocol = newProtocol;
	    if ((context != null) && ((newProtocol == null) ||
			    newProtocol.equalsIgnoreCase(context.protocol))) {
                // inherit the protocol handler from the context
		// if not specified to the contructor
		if (handler == null) {
		    handler = context.handler;
		}

                // If the context is a hierarchical URL scheme and the spec
		// contains a matching scheme then maintain backwards
		// compatibility and treat it as if the spec didn't contain
		// the scheme; see 5.2.3 of RFC2396
		if (context.path != null && context.path.startsWith("/"))
		    newProtocol = null;

                if (newProtocol == null) {
                    protocol = context.protocol;
		    authority = context.authority;
		    userInfo = context.userInfo;
                    host = context.host;
                    port = context.port;
                    file = context.file;
                }
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
	    throw new MalformedURLException(e.toString());
	}
    }

    /*
     * Returns true if specified string is a valid protocol name.
     */
    private boolean isValidProtocol(String protocol) {
	int len = protocol.length();
        if (len < 1)
            return false;
        char c = protocol.charAt(0);
        if (!Character.isLetter(c))
            return false;
	for (int i = 1; i < len; i++) {
	    c = protocol.charAt(i);
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
     * @param protocol the name of the protocol to use
     * @param host the name of the host
       @param port the port number on the host
     * @param file the file on the host
     * @param ref the internal reference in the URL
     */
    protected void set(String protocol, String host, 
		       int port, String file, String ref) {
	synchronized (this) {
	    this.protocol = protocol;
	    this.host = host;
            authority = port == -1 ? host : host + ":" + port;
	    this.port = port;
	    this.file = file;
	    this.ref = ref;
	    /* This is very important. We must recompute this after the
	     * URL has been changed. */
	    hashCode = -1;
            hostAddress = null;
            int q = file.lastIndexOf('?');
            if (q != -1) {
                query = file.substring(q+1);
                path = file.substring(0, q);
            } else
                path = file;
	}
    }

    /**
     * Sets the specified 8 fields of the URL. This is not a public method so
     * that only URLStreamHandlers can modify URL fields. URLs are otherwise
     * constant.
     *
     * @param protocol the name of the protocol to use
     * @param host the name of the host 
     * @param port the port number on the host
     * @param authority the authority part for the url
     * @param userInfo the username and password
     * @param path the file on the host
     * @param ref the internal reference in the URL
     * @param query the query part of this URL
     */
    protected void set(String protocol, String host, int port,
                       String authority, String userInfo, String path,
                       String query, String ref) {
	synchronized (this) {
	    this.protocol = protocol;
	    this.host = host;
	    this.port = port;
	    this.file = query == null ? path : path + "?" + query;
            this.userInfo = userInfo;
            this.path = path;
	    this.ref = ref;
	    /* This is very important. We must recompute this after the
	     * URL has been changed. */
	    hashCode = -1;
            hostAddress = null;
            this.query = query;
            this.authority = authority;
	}
    }

    /**
     * Returns the query part of this <code>URL</code>.
     *
     * @return  the query part of this <code>URL</code>.
     */
    public String getQuery() {
	return query;
    }

    /**
     * Returns the path part of this <code>URL</code>.
     *
     * @return  the path part of this <code>URL</code>.
     */
    public String getPath() {
	return path;
    }

    /**
     * Returns the userInfo part of this <code>URL</code>.
     *
     * @return  the userInfo part of this <code>URL</code>.
     */
    public String getUserInfo() {
	return userInfo;
    }

    /**
     * Returns the authority part of this <code>URL</code>.
     *
     * @return  the authority part of this <code>URL</code>.
     */
    public String getAuthority() {
	return authority;
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
     * Returns the protocol name of this <code>URL</code>.
     *
     * @return  the protocol of this <code>URL</code>.
     */
    public String getProtocol() {
	return protocol;
    }

    /**
     * Returns the host name of this <code>URL</code>, if applicable.
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

        return handler.equals(this, u2);
    }

    /** 
     * Creates an integer suitable for hash table indexing.
     *
     * @return  a hash code for this <code>URL</code>.
     */
    public synchronized int hashCode() {
	if (hashCode != -1)
            return hashCode;

        hashCode = handler.hashCode(this);
	return hashCode;
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
        return handler.sameFile(this, other);
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
     * Returns the contents of this URL. This method is a shorthand for:
     * <blockquote><pre>
     *     openConnection().getContent(Class[])
     * </pre></blockquote>
     *
     * @return     the content object of this URL that is the first match of
     *               the types specified in the classes array.
     *               null if none of the requested types are supported.
     * @exception  IOException  if an I/O exception occurs.
     * @see        java.net.URLConnection#getContent(Class[])
     */
    public final Object getContent(Class[] classes) 
    throws java.io.IOException {
	return openConnection().getContent(classes);
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
    public static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
	synchronized (streamHandlerLock) {
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
    }

    /**
     * A table of protocol handlers.
     */
    static Hashtable handlers = new Hashtable();
    private static Object streamHandlerLock = new Object();

    /**
     * Returns the Stream Handler.
     * @param protocol the protocol to use
     */
    static URLStreamHandler getURLStreamHandler(String protocol) {
	URLStreamHandler handler = (URLStreamHandler)handlers.get(protocol);
	if (handler == null) {

	    boolean checkedWithFactory = false;

	    // Use the factory (if any)
	    if (factory != null) {
		handler = factory.createURLStreamHandler(protocol);
		checkedWithFactory = true;
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

	    synchronized (streamHandlerLock) {

		URLStreamHandler handler2 = null;

		// Check again with hashtable just in case another
		// thread created a handler since we last checked
		handler2 = (URLStreamHandler)handlers.get(protocol);

		if (handler2 != null) {
		    return handler2;
		} 

		// Check with factory if another thread set a
		// factory since our last check
		if (!checkedWithFactory && factory != null) {
		    handler2 = factory.createURLStreamHandler(protocol);
		}

		if (handler2 != null) {
		    // The handler from the factory must be given more 
		    // importance. Discard the default handler that
		    // this thread created.
		    handler = handler2;
		}

		// Insert this handler into the hashtable
		if (handler != null) {
		    handlers.put(protocol, handler);
		}
		
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

        // Construct authority part
        if (authority == null &&
	    ((host != null && host.length() > 0) || port != -1)) {
	    if (host == null)
		host = "";
            authority = (port == -1) ? host : host + ":" + port;

            // Handle hosts with userInfo in them
            int at = host.lastIndexOf('@');
            if (at != -1) {
                userInfo = host.substring(0, at);
                host = host.substring(at+1);
	    }
        } else if (authority != null) {
            // Construct user info part
            int ind = authority.indexOf('@');
            if (ind != -1)
                userInfo = authority.substring(0, ind);
	}

        // Construct path and query part
        path = null;
        query = null;
        if (file != null) {
	    // Fix: only do this if hierarchical?
            int q = file.lastIndexOf('?');
            if (q != -1) {
                query = file.substring(q+1);
                path = file.substring(0, q);
            } else
                path = file;
        }
    }
}
