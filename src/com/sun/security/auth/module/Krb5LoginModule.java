/*
 * @(#)Krb5LoginModule.java	1.21 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.security.auth.module;

import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;

import javax.security.auth.*;
import javax.security.auth.kerberos.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;

import sun.security.krb5.*;
import sun.security.krb5.Config;
import sun.security.krb5.RealmException;
import sun.security.util.AuthResources;

/**
 * <p> This <code>LoginModule</code> authenticates users using 
 * Kerberos protocols. 
 * <p> Configuration entry for <code>Krb5LoginModule</code> has 
 * several options that control the authentication process and 
 * additions to the <code>Subject</code>'s private credential
 * set.
 * Irrespective of the options, only when <code>commit</code> 
 * is called the Subject's principal set and private credentials 
 * set are updated.
 * When <code>commit</code> is called the <code>KerberosPrincipal</code>
 * is added to the  <code>Subject</code>'s
 * principal set and <code>KerberosTicket</code> will be
 * added to the <code>Subject</code>'s private credentials.
 * 
 * <p> If the configuration entry for
 * KerberosLoginModule has the option <code>storeKey</code> set to true , 
 * then   
 * <code>KerberosKey</code> will also be added to the 
 * subject's private credentials. <code>KerberosKey</code>, the principal's
 * key will be  obtained either from the keytab or
 * derived from user's password
 * 
 * <p> This LoginModule recogonizes the <code>doNotPrompt</code> option.
 * If set to true the user will not be prompted for the password.
 *
 * <p> The user can  specify the location of the ticket cache by using 
 * the option <code>ticketCache</code> in the configuration entry. 
 * 
 * <p>The user can specify the keytab location by using 
 * the option <code>keyTab</code>
 * in the configuraion entry.
 *
 * <p> The principal name can be specified in the configuration entry 
 * by 
 * using the option <code>principal</code> The principal name 
 * can either be a simple 
 * user name or a service name such as
 * <code>host/mission.eng.sun.com</code>
 *
 * <p> The following are the list of configuration options supported 
 * for <code>Krb5LoginModule</code>
 *<p><code>useTicketCache</code>: Set this to true, if you want the 
 * TGT to be obtained
 * from the ticket cache. Set this option 
 * to false if you do not want this module to use the ticket cache.
 * (Default is False).
 * This module will 
 * search for the tickect 
 * cache in the following locations:
 * For Windows 2000, it will use Local Security Authority (LSA) API 
 * to get the TGT. On Solaris and Linux 
 * it will look for the ticket cache in /tmp/krb5cc_<code>uid</code>
 * where the uid is numeric user
 * identifier. If the ticket cache is 
 * not available in either of the above locations, or if we are on a
 * different WIndows platform,  it will look for the cache as 
 * {user.home}{file.separator}krb5cc_{user.name}.
 * You can override the ticket cache location by using
 * <code>ticketCache</code> 
 *
 * <p><code>ticketCache</code>: Set this to the name of the ticket 
 * cache that  contains user's TGT. 
 * If this is set,  <code>useTicketcache</code> 
 * must also be set to true; Otherwise a configuration error will 
 * be returned.
 *   
 * <p> <code>doNotPrompt</code>: Set this to true if you do not want to be
 * prompted for the password 
 * if credentials can 
 * not be obtained from the cache or keytab.(Default is false)  
 * If set to true authentication will fail if credentials can 
 * not be obtained from the cache or keytab. 
 *
 *<p><code>useKeyTab</code>:Set this to true if you 
 * want the module to get the principal's key from the
 * the keytab.(default value is False) 
 * If <code>keyatb</code> 
 * is not set then
 * the module will locate the keytab from the 
 * Kerberos configuration file. 
 * If it is not specifed in the Kerberos configuration file 
 * then it will look for the file
 * <code>{user.home}{file.separator}</code>krb5.keytab.
 *
 * <p><code>keyTab</code>: Set this to the file name of the 
 * keytab to get principal's secret key.
 *
 * <p> <code>storeKey</code>: Set  this to True to if you want the
 * principal's key to be stored in the Subject's private credentials. 
 * 
 * <p> <code>principal</code>: The name of the principal that should 
 * be used. It could be
 * simple username such as <code>"testuser"</code> or a service name 
 * such as
 * <code>"host/testhost.eng.sun.com" </code>. You can use 
 * <code>principal</code>  option to set the principal when there are
 * credentials for multiple principals in the
 * <code>keyTab</code> or when you want a specific ticket cache only.  
 *
 * <p> This LoginModule also recognizes the following additional 
 * <code>Configuration</code>
 * options that enable you to share username and passwords across different 
 * authentication modules:
 * <pre>
 *
 *    useFirstPass   if, true, this LoginModule retrieves the
 *                   username and password from the module's shared state,
 *                   using "javax.security.auth.login.name" and
 *                   "javax.security.auth.login.password" as the respective
 *                   keys. The retrieved values are used for authentication.
 *                   If authentication fails, no attempt for a retry
 *                   is made, and the failure is reported back to the
 *                   calling application.
 *
 *    tryFirstPass   if, true, this LoginModule retrieves the
 *                   the username and password from the module's shared
 *                   state using "javax.security.auth.login.name" and
 *                   "javax.security.auth.login.password" as the respective
 *                   keys.  The retrieved values are used for
 *                   authentication.
 *                   If authentication fails, the module uses the
 *                   CallbackHandler to retrieve a new username
 *                   and password, and another attempt to authenticate
 *                   is made. If the authentication fails, 
 *                   the failure is reported back to the calling application
 *
 *    storePass      if, true, this LoginModule stores the username and
 *                   password obtained from the CallbackHandler in the
 *                   modules shared state, using 
 *                   "javax.security.auth.login.name" and 
 *                   "javax.security.auth.login.password" as the respective
 *                   keys.  This is not performed if existing values already
 *                   exist for the username and password in the shared
 *                   state, or if authentication fails.
 *
 *    clearPass     if, true, this <code>LoginModule</code> clears the
 *                  username and password stored in the module's shared
 *                  state  after both phases of authentication
 *                  (login and commit)  have completed.
 * </pre>
 * <p>Examples of some configuration values for Krb5LoginModule in 
 * JAAS config file and the results are:
 * <ul>
 * <p> <code>doNotPrompt</code>=true;
 * </ul>
 * <p> This is an illegal combination since <code>useTicketCache</code>
 * is not set and the user can not be prompted for the password.
 *<ul>
 * <p> <code>ticketCache</code> = < filename >;
 *</ul>
 * <p> This is an illegal combination since useTicketCache is not set to 
 * true and the ticketCache is set. A configuratin error will occur.
 * <ul>
 * <p> <code>storeKey</code>=true
 * <code>useTicketCache</code> = true
 * <code>doNotPrompt</code>=true;;
 *</ul>
 * <p> This is an illegal combination since  <code>storeKey</code> is set to
 * true but the key can not be obtained either by prompting the user or from
 * the keytab.A configuratin error will occur.
 * <ul>
 * <p>  <code>keyTab</code> = < filename > <code>doNotPrompt</code>=true ;
 * </ul>
 * <p>This is an illegal combination since useKeyTab is not set to true and
 * the keyTab is set. A configuration error will occur.
 * <ul>
 * <p> <code>debug=true </code>
 *</ul>
 * <p> Prompt the user for the principal name and the password.
 * Use the authentication exchange to get TGT from the KDC and 
 * populate the <code>Subject</code> with the principal and TGT. 
 * Output debug messages.
 * <ul>
 * <p> <code>useTicketCache</code> = true <code>doNotPrompt</code>=true;
 *</ul>
 * <p>Check the default cache for TGT and populate the <code>Subject</code>
 * with the principal and TGT. If the TGT is not available, 
 * do not prompt the user, instead fail the authentication.
 * <ul>
 * <p><code>principal</code>=< name ><code>useTicketCache</code> = true 
 * <code>doNotPrompt</code>=true;
 *</ul>
 * <p> Get the TGT from the default cache for the principal and populate the
 * Subject's principal and private creds set. If ticket cache is
 * not available or does not contain the principal's TGT 
 * authentication will fail.
 * <ul>
 * <p> <code>useTicketCache</code> = true 
 * <code>ticketCache</code>=< file name ><code>useKeyTab</code> = true 
 * <code> keyTab</code>=< keytab filename >
 * <code>principal</code> = < principal name >
 * <code>doNotPrompt</code>=true;
 *</ul>
 * <p>  Search the cache for the principal's TGT. If it is not available 
 * use the key in the keytab to perform authentication exchange with the 
 * KDC and acquire the TGT.
 * The Subject will be populated with the principal and the TGT.
 * If the key is not available or valid then authentication will fail.
 * <ul>
 * <p><code>useTicketCache</code> = true 
 * <code>ticketCache</code>=< file name >
 *</ul>
 * <p> The TGT will be obtained from the cache specified. 
 * The Kerberos principal name used will be the principal name in
 * the Ticket cache. If the TGT is not available in the
 * ticket cache the user will be prompted for the principal name 
 * and the password. The TGT will be obtained using the authentication 
 * exchange with the KDC.
 * The Subject will be populated with the TGT.
 *<ul>
 * <p> <code>useKeyTab</code> = true 
 * <code>keyTab</code>=< keytab filename >
 * <code>principal</code>= < principal name > 
 * <code>storeKey</code>=true;
 *</ul>
 * <p>  The key for the principal will be retrieved from the keytab.
 * If the key is not available in the keytab the user will be prompted
 * for the principal's password. The Subject will be populated
 * with the principal's key either from the keytab or derived from the
 * password entered.
 * <ul>
 * <p> <code>useKeyTab</code> = true 
 * <code>keyTab</code>=< keytabname >
 * <code>storeKey</code>=true</code>
 * <code>doNotPrompt</code>=true;
 *</ul>
 * <p>The user will be prompted for the service principal name. 
 * If the principal's
 * longterm key is available in the keytab , it will be added to the
 * Subject's private credentials. An authentication exchange will be 
 * attempted with the principal name and the key from the Keytab. 
 * If successful the TGT will be added to the
 * Subject's private credentials set. Otherwise the authentication will
 * fail.
 *<ul>
 * <p><code>useKeyTab</code> = true
 * <code>keyTab</code>=< file name > <code>storeKey</code>=true
 * <code>principal</code>= < principal name > 
 * <code>useTicketCache</code>=true
 * <code>ticketCache</code>=< file name >;
 *</ul>
 * <p>The principal's key will be retrieved from the keytab and added
 * to the <code>Subject</code>'s private credentials. If the key 
 * is not available, the
 * user will be prompted for the password; the key derived from the password
 * will be added to the Subject's private credentials set. The
 * client's TGT will be retrieved from the ticket cache and added to the
 * <code>Subject</code>'s private credentials. If the TGT is not available  
 * in the ticket cache, it will be obtained using the authentication
 * exchange and added to the Subject's private credentials.
 *
 *
 * @version 1.18, 01/11/00
 * @author Ram Marti
 */

public class Krb5LoginModule implements LoginModule {

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;

    // configurable option
    private boolean debug = false;
    private boolean storeKey = false;
    private boolean doNotPrompt = false;
    private boolean useTicketCache = false;
    private boolean useKeyTab = false;
    private String ticketCacheName = null;
    private String keyTabName = null;
    private String princName = null;

    private boolean useFirstPass = false;
    private boolean tryFirstPass = false;
    private boolean storePass = false;
    private boolean clearPass = false;
    private boolean refreshKrb5Config = false;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;
    private String username;
    private EncryptionKey encKey;
    private sun.security.krb5.Credentials cred = null;

    private PrincipalName principal = null;
    private KerberosPrincipal kerbClientPrinc = null;
    private KerberosTicket kerbTicket = null;
    private KerberosKey kerbKey = null;
    private StringBuffer krb5PrincName = null;
    private char[] password = null;

    private static final String NAME = "javax.security.auth.login.name";
    private static final String PWD = "javax.security.auth.login.password";
    static final java.util.ResourceBundle rb =
        java.util.ResourceBundle.getBundle("sun.security.util.AuthResources");

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * <p>
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for 
     *                  communication with the end user (prompting for
     *                  usernames and passwords, for example). <p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *			<code>Configuration</code> for this particular
     *			<code>LoginModule</code>.
     */

    public void initialize(Subject subject, 
			   CallbackHandler callbackHandler,
			   Map sharedState, Map options) {
 
	this.subject = subject;
	this.callbackHandler = callbackHandler;
	this.sharedState = sharedState;
	this.options = options;

	// initialize any configured options

	debug = "true".equalsIgnoreCase((String)options.get("debug"));
	storeKey = "true".equalsIgnoreCase((String)options.get("storeKey"));
	doNotPrompt = "true".equalsIgnoreCase((String)options.get
					      ("doNotPrompt"));
	useTicketCache = "true".equalsIgnoreCase((String)options.get
						 ("useTicketCache"));
	useKeyTab = "true".equalsIgnoreCase((String)options.get("useKeyTab"));
	ticketCacheName = (String)options.get("ticketCache");
	keyTabName = (String)options.get("keyTab");
	princName = (String)options.get("principal");
	refreshKrb5Config =
	    "true".equalsIgnoreCase((String)options.get("refreshKrb5Config"));
	tryFirstPass =
	    "true".equalsIgnoreCase
	    ((String)options.get("tryFirstPass"));
	useFirstPass =
	    "true".equalsIgnoreCase
	    ((String)options.get("useFirstPass"));
	storePass =
	    "true".equalsIgnoreCase((String)options.get("storePass"));
	clearPass =
	    "true".equalsIgnoreCase((String)options.get("clearPass"));
	if (debug) {
	    System.out.print("Debug is  " + debug  
			     + " storeKey " + storeKey 
			     + " useTicketCache " + useTicketCache
			     + " useKeyTab " + useKeyTab
			     + " doNotPrompt " + doNotPrompt
			     + " ticketCache is " + ticketCacheName
			     + " KeyTab is " + keyTabName
			     + " refreshKrb5Config is " + refreshKrb5Config
		     	     + " principal is " + princName
			     + " tryFirstPass is " + tryFirstPass 
			     + " useFirstPass is " + useFirstPass
			     + " storePass is " + storePass
			     + " clearPass is " + clearPass + "\n");
	}
    }
    

    /**
     * Authenticate the user 
     *
     * <p>
     *
     * @return true in all cases since this <code>LoginModule</code>
     *		should not be ignored.
     *
     * @exception FailedLoginException if the authentication fails. <p>
     *
     * @exception LoginException if this <code>LoginModule</code>
     *		is unable to perform the authentication.
     */
    public boolean login() throws LoginException {

	int len;	
	validateConfiguration();
	if (refreshKrb5Config) {
	    try {
		if (debug) {
		    System.out.println("Refreshing Kerberos configuration");
		}
	        sun.security.krb5.Config.refresh();
	    } catch (KrbException ke) {
	        LoginException le = new LoginException(ke.getMessage());
	        le.initCause(ke);
	        throw le;
	    }
	}
	String principalProperty = System.getProperty
	    ("sun.security.krb5.principal"); 
	if (principalProperty != null) {
	    krb5PrincName = new StringBuffer(principalProperty);
	} else {
	    if (princName != null) {
		krb5PrincName = new StringBuffer(princName);
	    }
    	}
    
	if (tryFirstPass) {
	    try {
		attemptAuthentication(true);    
		if (debug)
		    System.out.println("\t\t[Krb5LoginModule] " +
				       "authentication succeeded");
		succeeded = true;
		cleanState();
		return true;
	    } catch (LoginException le) {
		// authentication failed -- try again below by prompting
		cleanState();
		if (debug) {
		    System.out.println("\t\t[Krb5LoginModule] " +
				       "tryFirstPass failed with:" +
				       le.getMessage());
		}
	    } 
	} else if (useFirstPass) {
	    try {
		attemptAuthentication(true);
		succeeded = true;
		cleanState();
		return true;
	    } catch (LoginException e) {
		// authentication failed -- clean out state
		if (debug) {
		    System.out.println("\t\t[Krb5LoginModule] " +
				       "authentication failed \n" +
				       e.getMessage());
		}
		succeeded = false;
		cleanState();
		throw e;
	    } 
	}
    
	// attempt the authentication by getting the username and pwd 
	// by prompting or configuration i.e. not from shared state
	
	try {
	    attemptAuthentication(false);
	    succeeded = true;
	    cleanState();
	    return true;
	} catch (LoginException e) {
	    // authentication failed -- clean out state
	    if (debug) {
		System.out.println("\t\t[Krb5LoginModule] " +
				   "authentication failed \n" +
				   e.getMessage());
	    }
	    succeeded = false;
	    cleanState();
	    throw e;
	}
    }
    /** 
     * process the configuration options
     * Get the TGT either out of
     * cache or from the KDC using the password entered
     * Check the  permission before getting the TGT
     */

    private void attemptAuthentication(boolean getPasswdFromSharedState)
	throws LoginException {
	
	/* 
	 * Check the creds cache to see whether 
	 * we have TGT for this client principal
	 */
	if (krb5PrincName != null) {
	    try {
	        principal = new PrincipalName
		    (krb5PrincName.toString(),
		     PrincipalName.KRB_NT_PRINCIPAL);
	    } catch (KrbException e) {
		LoginException le = new LoginException(e.getMessage());
		le.initCause(e);
		throw le;
	    }
	}
	
	try { 
	    if (useTicketCache) {
		// ticketCacheName == null implies the default cache
		cred  = Credentials.acquireTGTFromCache
		    (principal, ticketCacheName);
		if (cred != null) {
		    // get the principal name from the ticket cache
		    if (principal == null) { 
			principal = cred.getClient();
		    }
		}
		if (debug) {
		    System.out.println("Principal is " + principal);
		    if (cred ==  null) {
			System.out.println
			    ("null credentials from Ticket Cache");
		    }
		}
	    }		     

	    // cred = null indicates that we did n't get the creds
	    // from the cache or useTicketCache was false
		
	    if (cred == null) {
		// We need the principal name whether we use keytab
		// or AS Exchange
		if (principal == null) {
		    promptForName(getPasswdFromSharedState);
		    principal = new PrincipalName
			(krb5PrincName.toString(),
			 PrincipalName.KRB_NT_PRINCIPAL);
		}
		if (useKeyTab) {
		    encKey = EncryptionKey.acquireSecretKey
			(principal, keyTabName);
		    if (debug) {
			if (encKey != null)
			    System.out.println
				("principal's key obtained from the keytab");
			else
			    System.out.println
				("Key for the principal " + 
				 principal  + 
				 " not available in " + 
				 ((keyTabName == null) ? 
				  "default key tab" : keyTabName));
		    }
		    
		}   
		// We can't get the key from the keytab so prompt    
		if (encKey == null) {	
		    promptForPass(getPasswdFromSharedState);
		    encKey = new EncryptionKey(
				     new StringBuffer().append(password),
				     principal.getSalt());
		}
		// Get the TGT using AS Exchange
		if (debug)
		    System.out.println("principal is " + principal);
		cred = Credentials.acquireTGT(principal, encKey);

		// we should hava a  non-null cred 
		if (cred == null) {
		    throw new LoginException 
			("TGT Can not be obtained from the KDC ");
		}
	    }
	} catch (KrbException e) {
	    LoginException le = new LoginException(e.getMessage());
	    le.initCause(e);
	    throw le;
	} catch (IOException ioe) {
	    LoginException ie = new LoginException(ioe.getMessage());
	    ie.initCause(ioe);
	    throw ie;
	}
    }
    
    private void promptForName(boolean getPasswdFromSharedState)
	throws LoginException {
	krb5PrincName = new StringBuffer("");
	if (getPasswdFromSharedState) {
	    // use the name saved by the first module in the stack
	    username = (String)sharedState.get(NAME);
	    if (debug) {
		System.out.println
		    ("username from shared state is " + username + "\n");
	    }
	    if (username == null) {
		System.out.println
		    ("username from shared state is null\n");
		throw new LoginException
		    ("Username can not be obtained from sharedstate ");
	    }
	    if (debug) {
		System.out.println
		    ("username from shared state is " + username + "\n");
	    }
	    if (username != null && username.length() > 0) {
		krb5PrincName.insert(0, username);
		return;
	    }
	}
   
	if (doNotPrompt) {
	    throw new LoginException
		("Unable to obtain Princpal Name for authentication ");
	} else {
	    if (callbackHandler == null)
		throw new LoginException("No CallbackHandler "
					 + "available "
					 + "to garner authentication " 
					 + "information from the user");
	    try {
		String defUsername = System.getProperty("user.name");
		
		Callback[] callbacks = new Callback[1];
		MessageFormat form = new MessageFormat(
				       rb.getString(
				       "Kerberos username [[defUsername]]: "));
	        Object[] source =  {defUsername};
		callbacks[0] = new NameCallback(form.format(source));
		callbackHandler.handle(callbacks);
		username = ((NameCallback)callbacks[0]).getName();
		if (username == null || username.length() == 0)
		    username = defUsername;
		krb5PrincName.insert(0, username);
		
	    } catch (java.io.IOException ioe) {
		throw new LoginException(ioe.getMessage());
	    } catch (UnsupportedCallbackException uce) {
		throw new LoginException
		    (uce.getMessage()
		     +" not available to garner " 
		     +" authentication information " 
		     +" from the user");
	    }
	}
    }
    
    private void promptForPass(boolean getPasswdFromSharedState) 
	throws LoginException {

	if (getPasswdFromSharedState) {
	    // use the password saved by the first module in the stack
	    password = (char[])sharedState.get(PWD);
	    if (password == null) {
		if (debug) {
		    System.out.println
			("Password from shared state is null");
		}
		throw new LoginException
		    ("Password can not be obtained from sharedstate ");
	    }
	    if (debug) {
		System.out.println
		    ("password is " + new String(password));
	    }
	    return;
	}
	if (doNotPrompt) {
	    throw new LoginException
		("Unable to obtain password from user\n");
	} else {
	    try {
		Callback[] callbacks = new Callback[1];
		String userName = krb5PrincName.toString();
		MessageFormat form = new MessageFormat(
					 rb.getString(
					 "Kerberos password for [username]: "));
	        Object[] source = {userName};
		callbacks[0] = new PasswordCallback(
						    form.format(source),
						    false);
		callbackHandler.handle(callbacks);
		char[] tmpPassword = ((PasswordCallback)
				      callbacks[0]).getPassword();
		if (tmpPassword == null) {
		    // treat a NULL password as an empty password
		    tmpPassword = new char[0];
		}
		password = new char[tmpPassword.length];
		System.arraycopy(tmpPassword, 0,
				 password, 0, tmpPassword.length);
		((PasswordCallback)callbacks[0]).clearPassword();
		

		// clear tmpPassword
		for (int i = 0; i < tmpPassword.length; i++)
		    tmpPassword[i] = ' ';
		tmpPassword = null;
		if (debug) {
		    System.out.println("\t\t[Krb5LoginModule] " +
				       "user entered username: " +
				       krb5PrincName);
		    System.out.println();
		}
	    } catch (java.io.IOException ioe) {
		throw new LoginException(ioe.getMessage());
	    } catch (UnsupportedCallbackException uce) {
		throw new LoginException(uce.getMessage()
					 +" not available to garner " 
					 +" authentication information " 
					 + "from the user");
	    }
	}	
    }

    private void validateConfiguration() throws LoginException {
	if (doNotPrompt && !useTicketCache && !useKeyTab)
	    throw new LoginException
		("Configuration Error" 
		 + " - either doNotPrompt should be "
		 + " false or useTicketCache/useKeyTab "
		 + " should be true");
	if (ticketCacheName != null && !useTicketCache)
	    throw new LoginException
		("Configuration Error " 
		 + " - useTicketCache should be set "
		 + "to true to use the ticket cache" 
		 + ticketCacheName);
	if (keyTabName != null & !useKeyTab)
	    throw new LoginException
		("Configuration Error - useKeyTab should be set to true "
		 + "to use the keytab" + keyTabName);
	if (storeKey && doNotPrompt && !useKeyTab) 
	    throw new LoginException
		("Configuration Error - either doNotPrompt "
		 + "should be set to false or "
		 + "useKeyTab must be set to true for storeKey option");
    }
    
    
    /**
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
     * LoginModules succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates a
     * <code>Krb5Principal</code>
     * with the <code>Subject</code> located in the
     * <code>LoginModule</code>. It adds Kerberos Credentials to the
     *  the Subject's private credentials set. If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the commit fails.
     *
     * @return true if this LoginModule's own login and commit
     *		attempts succeeded, or false otherwise.
     */

    public boolean commit() throws LoginException {

	/*
	 * Let us add the Krb5 Creds to the Subject's 
	 * private credentials. The credentials are of type
	 * KerberosKey or KerberosTicket
	 */
	if (succeeded == false) {
	    return false;
	} else {
	    /*
	     * Add the Principal (authenticated identity)
	     * to the Subject's principal set and
	     * add the credentials (TGT or Service key) to the
	     * Subject's private credentials
	     */

	    Set privCredSet =  subject.getPrivateCredentials();
	    Set princSet  = subject.getPrincipals();
	    kerbClientPrinc = new KerberosPrincipal(principal.getName());
	    
	    if (cred == null) {
		succeeded = false;
		throw new LoginException("Null Client Credential");
	    }
	    EncryptionKey sessionKey = cred.getSessionKey();
	    kerbTicket  = new KerberosTicket
		(cred.getEncoded(),
		 new KerberosPrincipal(cred.getClient().getName()),
		 new KerberosPrincipal(cred.getServer().getName()),
		 sessionKey.getBytes(), 
		 sessionKey.getEType(), 
		 cred.getFlags(), 
		 cred.getAuthTime(), 
		 cred.getStartTime(), 
		 cred.getEndTime(), 
		 cred.getRenewTill(), 
		 cred.getClientAddresses());
	    
	    if (storeKey) {
		if (encKey == null) {
		    succeeded = false;
		    throw new LoginException("Null Server Key ");
		}		
		Integer temp = encKey.getKeyVersionNumber();
		kerbKey = new KerberosKey(kerbClientPrinc,
					  encKey.getBytes(),
					  encKey.getEType(),
					  (temp == null?
					  0: temp.intValue()));
		
	    }
	    // Let us add the kerbClientPrinc,kerbTicket and kerbKey (if
	    // storeKey is true)
	    if (!princSet.contains(princSet))
		princSet.add(kerbClientPrinc);
	    if (!privCredSet.contains(kerbTicket)) 	
		privCredSet.add(kerbTicket);
	    if (storeKey) {
		if (!privCredSet.contains(kerbKey)) {	
		    privCredSet.add(kerbKey);
		}
		encKey.destroy();
		encKey = null;
		if (debug) {
		    System.out.println("Added server's key"
					+ kerbKey);		    
		    System.out.println("\t\t[Krb5LoginModule] " +
				       "added Krb5Principal  " + 
				       kerbClientPrinc.toString()
				       + " to Subject");
		}			
	    }
	}
	commitSucceeded = true;
	if (debug)
	    System.out.println("Commit Succeeded \n");
	return true;
    }
    
    /**
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL 
     * LoginModules did not succeed).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> and <code>commit</code> methods),
     * then this method cleans up any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the abort fails.
     *
     * @return false if this LoginModule's own login and/or commit attempts
     *		failed, and true otherwise.
     */

    public boolean abort() throws LoginException {
	if (succeeded == false) {
	    return false;
	} else if (succeeded == true && commitSucceeded == false) {
	    // login succeeded but overall authentication failed
	    succeeded = false;
	    username = null;
	    try {
		if (kerbTicket != null)
		    kerbTicket.destroy();
		if (kerbKey != null)
		    kerbKey.destroy();
	    } catch (DestroyFailedException e) {
		throw new LoginException
		    ("Destroy Failed on Kerberos Private Credentials");
	    }
	    kerbTicket = null;
	    kerbKey = null;
	    kerbClientPrinc = null;
	} else {
	    // overall authentication succeeded and commit succeeded,
	    // but someone else's commit failed
	    logout();
	}
	return true;
    }
    
    /**
     * Logout the user.
     *
     * <p> This method removes the <code>Krb5Principal</code>
     * that was added by the <code>commit</code> method.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *          should not be ignored.
     */
    public boolean logout() throws LoginException {
	
	subject.getPrincipals().remove(kerbClientPrinc);
	   // Let us remove all Kerberos credentials stored in the Subject 
	Iterator it = subject.getPrivateCredentials().iterator();
	while (it.hasNext()) {
	   Object o = it.next();
	   if (o instanceof KerberosTicket ||
	       o instanceof KerberosKey) {
	       it.remove();
	   }
	}
	// Clean the slate
	try {
	    if (kerbTicket != null)
		kerbTicket.destroy();
	    if (kerbKey != null)
		kerbKey.destroy();
	} catch (DestroyFailedException e) {
	    throw new LoginException
		("Destroy Failed on Kerberos Private Credentials");
	}
	kerbTicket = null;
	kerbKey = null;
	kerbClientPrinc = null;
	succeeded = false;
	commitSucceeded = false;
	username = null;
	if (debug) {
            System.out.println("\t\t[Krb5LoginModule]: " +
			       "logged out Subject");
        }
	return true;
    }

    /**
     * Clean out the state 
     */
    private void cleanState() {
       
	// save input as shared state only if
	// authentication succeeded
	if (succeeded) {
	    if (storePass &&
		!sharedState.containsKey(NAME) &&
		!sharedState.containsKey(PWD)) {
		sharedState.put(NAME, username);
		sharedState.put(PWD, password);
	    }
	}
	username = null;
	password = null;
	if (krb5PrincName != null && krb5PrincName.length() != 0)
	    krb5PrincName.delete(0, krb5PrincName.length());
	krb5PrincName = null;
	if (clearPass) {
	    sharedState.remove(NAME);
	    sharedState.remove(PWD);
	}
    }
}
