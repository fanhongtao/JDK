/*
 * @(#)URL.java	1.44 98/07/01
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
 * <ul><code>
 *     http://www.ncsa.uiuc.edu/demoweb/url-primer.html
 * </code></ul>
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
 * <ul><code>
 *     http://www.ncsa.uiuc.edu:8080/demoweb/url-primer.html
 * </code></ul>
 * <p>
 * A URL may have appended to it an "anchor", also known 
 * as a "ref" or a "reference". The anchor is 
 * indicated by the sharp sign character "#" followed by 
 * more characters. For example, 
 * <ul><code>
 *     http://java.sun.com/index.html#chapter1
 * </code></ul>
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
 * <ul><code>
 *     http://java.sun.com/index.html
 * </code></ul>
 * contained within it the relative URL:
 * <ul><code>
 *     FAQ.html
 * </code></ul>
 * it would be a shorthand for:
 * <ul><code>
 *     http://java.sun.com/FAQ.html
 * </code></ul>
 * <p>
 * The relative URL need not specify all the components of a URL. If 
 * the protocol, host name, or port number is missing, the value is 
 * inherited from the fully specified URL. The file component must be 
 * specified. The optional anchor is not inherited. 
 *
 * @author  James Gosling
 * @version 1.44, 07/01/98
 * @since   JDK1.0
 */
public final class URL implements java.io.Serializable {
    /**
     * The property which specifies the package prefix list to be scanned
     * for protocol handlers.  The value of this property (if any) should
     * be a vertical bar delimited list of package names to search through
     * for a protocol handler to load.  The policy of this class is that
     * all protocol handlers will be in a class called <protocolname>.Handler,
     * and each package in the list is examined in turn for a matching
     * handler.  If none are found (or the property is not specified), the
     * default package prefix, sun.net.www.protocol, is used.  The search
     * proceeds from the first package in the last to the last and stops
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
     * # reference. 
     */
    private String ref;

    /**
     * The URLStreamHandler for this URL.
     */
    transient URLStreamHandler handler;

    /* Our hash code */
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
     *     <ul><code>
     *         java.handler.protol.pkgs
     *     </code></ul>
     *     If the value of that system property is not <code>null</code>,
     *     it is interpreted as a list of packages separated by a vertical
     *     slash character '<code>|</code>'. The constructor tries to load 
     *     the class named:
     *     <ul><code>
     *         &lt;<i>package</i>&gt;.&lt;<i>protocol</i>&gt;.Handler
     *     </code></ul>
     *     where &lt;<i>package</i>&gt; is replaced by the name of the package
     *     and &lt;<i>protocol</i>&gt; is replaced by the name of the protocol.
     *     If this class does not exist, or if the class exists but it is not
     *     a subclass of <code>URLStreamHandler</code>, then the next package
     *     in the list is tried.
     * <li>If the previous step fails to find a protocol handler, then the
     *     constructor tries to load the class named:
     *     <ul><code>
     *         sun.net.www.protocol.&lt;<i>protocol</i>&gt;.Handler
     *     </code></ul>
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
     * @since      JDK1.0
     */
    public URL(String protocol, String host, int port, String file)
	throws MalformedURLException {
	this.protocol = protocol;
	this.host = host;
	this.port = port;
	int ind = file.indexOf('#');
	this.file = ind < 0 ? file: file.substring(0, ind);
	this.ref = ind < 0 ? null: file.substring(ind + 1);
	if ((handler = getURLStreamHandler(protocol)) == null) {
	    throw new MalformedURLException("unknown protocol: " + protocol);
	}
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
     * @since      JDK1.0
     */
    public URL(String protocol, String host, String file) throws MalformedURLException {
	this(protocol, host, -1, file);
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
     * @since      JDK1.0
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
     * <ul><code>
     *     java.net.URL#URL(java.lang.String, java.lang.String, int,
     *                      java.lang.String)
     * </code></ul>
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
     * @since   JDK1.0
     */
    public URL(URL context, String spec) throws MalformedURLException {
	String original = spec;
	int i, limit, c;
	int start = 0;
	String newProtocol = null;
	boolean aRef=false;

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
	    for (i = start ; !aRef && (i < limit) && ((c = spec.charAt(i)) != '/') ; i++) {
		if (c == ':') {
		    newProtocol = spec.substring(start, i).toLowerCase();
		    start = i + 1;
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
	    } else {
		protocol = newProtocol;
	    }

	    if (protocol == null) {
		throw new MalformedURLException("no protocol: "+original);
	    }

	    if ((handler = getURLStreamHandler(protocol)) == null) {
		throw new MalformedURLException("unknown protocol: "+protocol);
	    }

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

    /**
     * Sets the fields of the URL. This is not a public method so that 
     * only URLStreamHandlers can modify URL fields. URLs are 
     * otherwise constant.
     *
     * REMIND: this method will be moved to URLStreamHandler
     *
     * @param protocol the protocol to use
     * @param host the host name to connecto to
     * @param port the protocol port to connect to
     * @param file the specified file name on that host
     * @param ref the reference
     */
    protected void set(String protocol, String host, int port, String file, String ref) {
	this.protocol = protocol;
	this.host = host;
	this.port = port;
	this.file = file;
	this.ref = ref;
    }

    /**
     * Returns the port number of this <code>URL</code>.
     * Returns -1 if the port is not set.
     *
     * @return  the port number
     * @since   JDK1.0
     */
    public int getPort() {
	return port;
    }

    /**
     * Returns the protocol name this <code>URL</code>.
     *
     * @return  the protocol of this <code>URL</code>.
     * @since   JDK1.0
     */
    public String getProtocol() {
	return protocol;
    }

    /**
     * Returns the host name of this <code>URL</code>, if applicable.
     * For "<code>file</code>" protocol, this is an empty string.
     *
     * @return  the host name of this <code>URL</code>.
     * @since   JDK1.0
     */
    public String getHost() {
	return host;
    }

    /**
     * Returns the file name of this <code>URL</code>.
     *
     * @return  the file name of this <code>URL</code>.
     * @since   JDK1.0
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
     * @since   JDK1.0
     */
    public String getRef() {
	return ref;
    }

    /**
     * Compares two URLs.
     * The result is <code>true</code> if and only if the argument is 
     * not <code>null</code> and is a <code>URL</code> object that 
     * represents the same <code>URL</code> as this object. Two URL 
     * objects are equal if they have the same protocol and reference the 
     * same host, the same port number on the host, and the same file on 
     * the host. The anchors of the URL objects are not compared. 
     * <p>
     * This method is equivalent to:
     * <ul><code>
     *     (obj instanceof URL) &amp;&amp; sameFile((URL)obj)
     * </code></ul>
     *
     * @param   obj   the URL to compare against.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    public boolean equals(Object obj) {
	return (ref == null) ? (obj instanceof URL) && sameFile((URL)obj):
	    (obj instanceof URL) && sameFile((URL)obj) && ref.equals(((URL)obj).ref);
    }

    /** 
     * Creates an integer suitable for hash table indexing. 
     *
     * @return  a hash code for this <code>URL</code>.
     * @since   JDK1.0
     */
    public int hashCode() {

	if (hashCode == -1) {
	    hashCode = host.toLowerCase().hashCode() ^ file.hashCode() ^
		protocol.hashCode();
	}

	return hashCode;
    }
	
    /**
     * Compares the host components of two URLs.
     * @param h1 the URL of the first host to compare 
     * @param h2 the URL of the second host to compare 
     * @return	true if and only if they are equal, false otherwise.
     * @exception UnknownHostException If an unknown host is found.
     */
    boolean hostsEqual(String h1, String h2) {
	if (h1.equals(h2)) {
	    return true;
	}
	// Have to resolve addresses before comparing, otherwise
	// names like tachyon and tachyon.eng would compare different
	try {
	    InetAddress a1 = InetAddress.getByName(h1);
	    InetAddress a2 = InetAddress.getByName(h2);
	    return a1.equals(a2);
	} catch(UnknownHostException e) {
	} catch(SecurityException e) {
	}
	return false;
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
     * @since   JDK1.0
     */
    public boolean sameFile(URL other) {
	// AVH: should we not user getPort to compare ports?
	return protocol.equals(other.protocol) &&
	       hostsEqual(host, other.host) &&
	       (port == other.port) &&
	       file.equals(other.file);
    }

    /**
     * Constructs a string representation of this <code>URL</code>. The 
     * string is created by calling the <code>toExternalForm</code> 
     * method of the stream protocol handler for this object. 
     *
     * @return  a string representation of this object.
     * @see     java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see     java.net.URLStreamHandler#toExternalForm(java.net.URL)
     * @since   JDK1.0
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
     * @since   JDK1.0
     */
    public String toExternalForm() {
	return handler.toExternalForm(this);
    }

    /** 
     * Returns a <code>URLConnection</code> object that represents a 
     * connection to the remote object referred to by the <code>URL</code>.
     * <p>
     * If there is not already an open connection, the connection is 
     * opened by calling the <code>openConnection</code> method of the 
     * protocol handler for this URL. 
     *
     * @return     a <code>URLConnection</code> to the URL.
     * @exception  IOException  if an I/O exception occurs.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see        java.net.URLConnection
     * @see        java.net.URLStreamHandler#openConnection(java.net.URL)
     * @since      JDK1.0
     */
    public URLConnection openConnection()
	throws java.io.IOException
    {
	return handler.openConnection(this);
    }

    /**
     * Opens a connection to this <code>URL</code> and returns an 
     * <code>InputStream</code> for reading from that connection. This 
     * method is a shorthand for:
     * <ul><code>
     *     openConnection().getInputStream()
     * </code></ul>
     *
     * @return     an input stream for reading from the URL connection.
     * @exception  IOException  if an I/O exception occurs.
     * @since      JDK1.0
     */
    public final InputStream openStream() 			// REMIND: drop final
	throws java.io.IOException
    {
	return openConnection().getInputStream();
    }

    /**
     * Returns the contents of this URL. This method is a shorthand for:
     * <ul><code>
     *     openConnection().getContent()
     * </code></ul>
     *
     * @return     the contents of this URL.
     * @exception  IOException  if an I/O exception occurs.
     * @see        java.net.URLConnection#getContent()
     * @since      JDK1.0
     */
    public final Object getContent() 				// REMIND: drop final
	throws java.io.IOException
    {
	return openConnection().getContent();
    }

    /**
     * The URLStreamHandler factory.
     */
    static URLStreamHandlerFactory factory;

    /**
     * Sets an application's <code>URLStreamHandlerFactory</code>.
     * This method can be called at most once by an application. 
     * <p>
     * The <code>URLStreamHandlerFactory</code> instance is used to 
     * construct a stream protocol handler from a protocol name. 
     *
     * @param      fac   the desired factory.
     * @exception  Error  if the application has already set a factory.
     * @see        java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String)
     * @see        java.net.URLStreamHandlerFactory
     * @since      JDK1.0
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
		String packagePrefixList =
		    System.getProperty(protocolPathProp, "");
		if (packagePrefixList != "") {
		    packagePrefixList += "|";
		}

		// REMIND: decide whether to allow the "null" class prefix
		// or not.
		packagePrefixList += "sun.net.www.protocol";

		StringTokenizer packagePrefixIter =
		    new StringTokenizer(packagePrefixList, "|");

		while (handler == null && packagePrefixIter.hasMoreTokens()) {
		    String packagePrefix = packagePrefixIter.nextToken().trim();
		    try {
			String clname = packagePrefix + "." + protocol
			    + ".Handler";
			handler = (URLStreamHandler)Class.forName(clname).newInstance();
		    } catch (Exception e) {
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
     * WriteObject is called to save the state of the URL to an ObjectOutputStream
     * The handler is not saved since it is specific to this system.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	s.defaultWriteObject();	// write the fields
    }

    /**
     * readObject is called to restore the state of the URL from the stream.
     * It reads the compoents of the URL and finds the local stream handler.
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
