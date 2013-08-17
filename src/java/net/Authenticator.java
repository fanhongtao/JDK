/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

/**
 * The class Authenticator represents an object that knows how to obtain
 * authentication for a network connection.  Usually, it will do this
 * by prompting the user for information.
 * <p>
 * Applications use this class by creating a subclass, and registering
 * an instance of that subclass with the system with setDefault().
 * When authentication is required, the system will invoke a method
 * on the subclass (like getPasswordAuthentication).  The subclass's
 * method can query about the authentication being requested with a
 * number of inherited methods (getRequestingXXX()), and form an
 * appropriate message for the user.
 * <p>
 * All methods that request authentication have a default implementation
 * that fails.
 *
 * @see java.net.Authenticator#setDefault(java.net.Authenticator)
 * @see java.net.Authenticator#getPasswordAuthentication()
 *
 * @author  Bill Foote
 * @version 1.21, 02/06/02
 * @since   1.2
 */

// There are no abstract methods, but to be useful the user must
// subclass.
public abstract 
class Authenticator {

    // The system-wide authenticator object.  See setDefault().
    private static Authenticator theAuthenticator;

    private InetAddress requestingSite;
    private int requestingPort;
    private String requestingProtocol;
    private String requestingPrompt;
    private String requestingScheme;

    private void reset() {
	requestingSite = null;
	requestingPort = -1;
	requestingProtocol = null;
	requestingPrompt = null;
	requestingScheme = null;
    }


    /**
     * Sets the authenticator that will be used by the networking code
     * when a proxy or an HTTP server asks for authenticator.
     * If an Authenticator has already been established
     * as the current authenticator, no action will be taken.
     * If the argument is <code>null</code> and no
     * authenticator has been established, then no action is taken
     * and the method simply returns.
     * <p>
     * First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with a 
     * <code>NetPermission("setDefaultAuthenticator")</code> permission.
     * This may result in a java.lang.SecurityException. 
     * <p>
     * Typically, this method will be called exactly once, at system startup.
     *
     * @param	a	The authenticator
     *
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        setting the default authenticator.
     *
     * @see SecurityManager#checkPermission
     * @see java.net.NetPermission
     */
    public synchronized static void setDefault(Authenticator a) {
	NetPermission setDefaultPermission
	    = new NetPermission("setDefaultAuthenticator");
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(setDefaultPermission);
	if (theAuthenticator != null) {
	    return;
	}
	theAuthenticator = a;
    }

    /**
     * Ask the authenticator that has been registered with the system
     * for a password.
     * <p>
     * First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with a 
     * <code>NetPermission("requestPasswordAuthentication")</code> permission.
     * This may result in a java.lang.SecurityException. 
     *
     * @param addr The InetAddress of the site requesting authorization,
     *             or null if not known.
     * @param port the port for the requested connection
     * @param protocol The protocol that's requesting the connection
     *          ({@link java.net.Authenticator#getProtocol()})
     * @param prompt A prompt string for the user
     * @param scheme The authentication scheme
     *
     * @return The username/password, or null if one can't be gotten.
     *
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        the password authentication request.
     *
     * @see SecurityManager#checkPermission
     * @see java.net.NetPermission
     */
    public static PasswordAuthentication requestPasswordAuthentication(
					    InetAddress addr,
					    int port,
					    String protocol,
					    String prompt,
					    String scheme) {

	NetPermission requestPermission
	    = new NetPermission("requestPasswordAuthentication");
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(requestPermission);

	Authenticator a = theAuthenticator;
	if (a == null) {
	    return null;
	} else {
	    synchronized(a) {
		a.reset();
		a.requestingSite = addr;
		a.requestingPort = port;
		a.requestingProtocol = protocol;
		a.requestingPrompt = prompt;
		a.requestingScheme = scheme;
		return a.getPasswordAuthentication();
	    }
	}
    }

    /**
     * Gets the <code>InetAddress</code> of the
     * site requesting authorization, or <code>null</code>
     * if not available.
     * 
     * @return the InetAddress of the site requesting authorization, or null
     *		if it's not available.
     */
    protected final InetAddress getRequestingSite() {
	return requestingSite;
    }

    /**
     * Gets the port number for the requested connection.
     * @return an <code>int</code> indicating the 
     * port for the requested connection.
     */
    protected final int getRequestingPort() {
	return requestingPort;
    }

    /**
     * Give the protocol that's requesting the connection.  Often this
     * will be based on a URL, but in a future SDK it could be, for
     * example, "SOCKS" for a password-protected SOCKS5 firewall.
     *
     * @return the protcol, optionally followed by "/version", where
     *		version is a version number.
     *
     * @see java.net.URL#getProtocol()
     */
    protected final String getRequestingProtocol() {
	return requestingProtocol;
    }

    /**
     * Gets the prompt string given by the requestor.
     *
     * @return the prompt string given by the requestor (realm for
     *		http requests)
     */
    protected final String getRequestingPrompt() {
	return requestingPrompt;
    }

    /**
     * Gets the scheme of the requestor (the HTTP scheme
     * for an HTTP firewall, for example).
     *
     * @return the scheme of the requestor
     *	      
     */
    protected final String getRequestingScheme() {
	return requestingScheme;
    }

    /**
     * Called when password authorization is needed.  Subclasses should
     * override the default implementation, which returns null.
     * @return The PasswordAuthentication collected from the
     *		user, or null if none is provided.
     */
    protected PasswordAuthentication getPasswordAuthentication() {
	return null;
    }

}






