/*
 * @(#)HttpURLConnection.java	1.10 98/07/01
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

/**
 * A URLConnection with support for HTTP-specific features. See
 * <A HREF="http://www.w3.org/pub/WWW/Protocols/"> the spec </A> for
 * details.  
 * @since JDK1.1
 */
abstract public class HttpURLConnection extends URLConnection {
    /* instance variables */

    /**
     * @since   JDK1.1
     */
    protected String method = "GET";

    /**
     * @since   JDK1.1
     */
    protected int responseCode = -1;

    /**
     * @since   JDK1.1
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
     * @since   JDK1.1
     */
    protected HttpURLConnection (URL u) {
	super(u);
    }
    
    /**
     * Sets whether HTTP redirects  (requests with response code 3xx) should 
     * be automatically followed by this class.  True by default.  Applets
     * cannot change this variable.
     * @since   JDK1.1
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
     * @since   JDK1.1
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
     * @since     JDK1.1
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
     * @since   JDK1.1
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
     * @since   JDK1.1
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
     * @since  JDK1.1
     */
    public String getResponseMessage() throws IOException {
	getResponseCode();
	return responseMessage;
    }

    /**
     * Close the connection to the server.
     * @since JDK1.1
     */
    public abstract void disconnect();

    /**
     * Indicates if the connection is going through a proxy.
     * @since   JDK1.1
     */
    public abstract boolean usingProxy();

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
