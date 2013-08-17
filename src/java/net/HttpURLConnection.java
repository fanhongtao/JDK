/*
 * @(#)HttpURLConnection.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.net;

import java.io.InputStream;
import java.io.IOException;
import java.security.Permission;

/**
 * A URLConnection with support for HTTP-specific features. See
 * <A HREF="http://www.w3.org/pub/WWW/Protocols/"> the spec </A> for
 * details.  
 * @since JDK1.1
 */
abstract public class HttpURLConnection extends URLConnection {
    /* instance variables */

    /**
     */
    protected String method = "GET";

    /**
     */
    protected int responseCode = -1;

    /**
     */
    protected String responseMessage = null;

    /* static variables */

    /* do we automatically follow redirects? The default is true. */
    private static boolean followRedirects = true;

    /* valid HTTP methods */
    private static final String[] methods = {
	"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
    };

    /**
     * Constructor for the URLStreamHandler.
     */
    protected HttpURLConnection (URL u) {
	super(u);
    }
    
    /**
     * Sets whether HTTP redirects  (requests with response code 3xx) should 
     * be automatically followed by this class.  True by default.  Applets
     * cannot change this variable.
     * <p>
     * If there is a security manager, this method first calls
     * the security manager's <code>checkSetFactory</code> method 
     * to ensure the operation is allowed. 
     * This could result in a SecurityException.
     * 
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkSetFactory</code> method doesn't allow the operation.
     * @see        SecurityManager#checkSetFactory
     */
    public static void setFollowRedirects(boolean set) {
	SecurityManager sec = System.getSecurityManager();
	if (sec != null) {
	    // seems to be the best check here...
	    sec.checkSetFactory();
	}
	followRedirects = set;
    }

    /**
     */
    public static boolean getFollowRedirects() {
	return followRedirects;
    }

    /**
     * Set the method for the URL request, one of:
     * <UL>
     *  <LI>GET
     *  <LI>POST
     *  <LI>HEAD
     *  <LI>OPTIONS
     *  <LI>PUT
     *  <LI>DELETE
     *  <LI>TRACE
     * </UL> are legal, subject to protocol restrictions.  The default
     * method is GET.
     * 
     * @exception ProtocolException if the method cannot be reset or if
     *              the requested method isn't valid for HTTP.
     */
    public void setRequestMethod(String method) throws ProtocolException {
	if (connected) {
	    throw new ProtocolException("Can't reset method: already connected");
	}
	// This restriction will prevent people from using this class to 
	// experiment w/ new HTTP methods using java.  But it should 
	// be placed for security - the request String could be
	// arbitrarily long.

	for (int i = 0; i < methods.length; i++) {
	    if (methods[i].equals(method)) {
		this.method = method;
		return;
	    }
	}
	throw new ProtocolException("Invalid HTTP method: " + method);
    }

    /**
     * Get the request method.
     */
    public String getRequestMethod() {
	return method;
    }
    
    /**
     * Gets HTTP response status.  From responses like:
     * <PRE>
     * HTTP/1.0 200 OK
     * HTTP/1.0 401 Unauthorized
     * </PRE>
     * Extracts the ints 200 and 401 respectively.
     * Returns -1 if none can be discerned
     * from the response (i.e., the response is not valid HTTP).
     * @throws IOException if an error occurred connecting to the server.
     */
    public int getResponseCode() throws IOException {
	if (responseCode != -1) {
	    return responseCode;
	}
	// make sure we've gotten the headers
	getInputStream();

	String resp = getHeaderField(0);
	/* should have no leading/trailing LWS
	 * expedite the typical case by assuming it has
	 * form "HTTP/1.x <WS> 2XX <mumble>"
	 */
	int ind;
	try {	
	    ind = resp.indexOf(' ');
	    while(resp.charAt(ind) == ' ')
		ind++;
	    responseCode = Integer.parseInt(resp.substring(ind, ind + 3));
	    responseMessage = resp.substring(ind + 4).trim();
	    return responseCode;
	} catch (Exception e) { 
	    return responseCode;
	}
    }

    /**
     * Gets the HTTP response message, if any, returned along with the
     * response code from a server.  From responses like:
     * <PRE>
     * HTTP/1.0 200 OK
     * HTTP/1.0 404 Not Found
     * </PRE>
     * Extracts the Strings "OK" and "Not Found" respectively.
     * Returns null if none could be discerned from the responses 
     * (the result was not valid HTTP).
     * @throws IOException if an error occurred connecting to the server.
     */
    public String getResponseMessage() throws IOException {
	getResponseCode();
	return responseMessage;
    }

    /**
     * Close the connection to the server.
     */
    public abstract void disconnect();

    /**
     * Indicates if the connection is going through a proxy.
     */
    public abstract boolean usingProxy();

    public Permission getPermission() throws IOException {
	int port = url.getPort();
	port = port < 0 ? 80 : port;
	String host = url.getHost() + ":" + port;
	Permission permission = new SocketPermission(host, "connect");
	return permission;
    }

   /**
    * Returns the error stream if the connection failed
    * but the server sent useful data nonetheless. The
    * typical example is when an HTTP server responds
    * with a 404, which will cause a FileNotFoundException 
    * to be thrown in connect, but the server sent an HTML
    * help page with suggestions as to what to do.
    *
    * <p>This method will not cause a connection to be initiated.
    * If there the connection was not connected, or if the server
    * did not have an error while connecting or if the server did
    * have an error but there no error data was sent, this method
    * will return null. This is the default.
    *
    * @return an error stream if any, null if there have been
    * no errors, the connection is not connected or the server
    * sent no useful data.
    */
    public InputStream getErrorStream() {
	return null;
    }

    /**
     * The response codes for HTTP, as of version 1.1.
     */

    // REMIND: do we want all these??
    // Others not here that we do want??

    /** 2XX: generally "OK" */
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_NOT_AUTHORITATIVE = 203; 
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_RESET = 205;
    public static final int HTTP_PARTIAL = 206;

    /** 3XX: relocation/redirect */
    public static final int HTTP_MULT_CHOICE = 300;
    public static final int HTTP_MOVED_PERM = 301;
    public static final int HTTP_MOVED_TEMP = 302;
    public static final int HTTP_SEE_OTHER = 303;
    public static final int HTTP_NOT_MODIFIED = 304;
    public static final int HTTP_USE_PROXY = 305;

    /** 4XX: client error */
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_BAD_METHOD = 405;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_PROXY_AUTH = 407;
    public static final int HTTP_CLIENT_TIMEOUT = 408;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_GONE = 410;
    public static final int HTTP_LENGTH_REQUIRED = 411;
    public static final int HTTP_PRECON_FAILED = 412;
    public static final int HTTP_ENTITY_TOO_LARGE = 413;
    public static final int HTTP_REQ_TOO_LONG = 414;
    public static final int HTTP_UNSUPPORTED_TYPE = 415;
    
    /** 5XX: server error */
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_INTERNAL_ERROR = 501;
    public static final int HTTP_BAD_GATEWAY = 502;
    public static final int HTTP_UNAVAILABLE = 503;
    public static final int HTTP_GATEWAY_TIMEOUT = 504;
    public static final int HTTP_VERSION = 505;

}
