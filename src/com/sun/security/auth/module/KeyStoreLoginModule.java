/*
 * @(#)KeyStoreLoginModule.java	1.13 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.security.auth.module;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Subject;
import javax.security.auth.x500.*;
import javax.security.auth.Subject;
import javax.security.auth.x500.*;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import sun.security.util.AuthResources;

/**
 * Provides a JAAS login module that prompts for a key store alias and
 * populates the subject with the alias's principal and credentials. Stores
 * an <code>X500Principal</code> for the subject distinguished name of the 
 * first certificate in the alias's credentials in the subject's principals,
 * the alias's certificate path in the subject's public credentials, and a
 * <code>X500PrivateCredential</code> whose certificate is the first
 * certificate in the alias's certificate path and whose private key is the
 * alias's private key in the subject's private credentials. <p>
 *
 * Recognizes the following options in the JAAS authentication policy file:
 * <dl>
 *
 * <dt> <code>keyStoreURL</code> </dt>
 * <dd> A URL that specifies the location of the key store file.  Defaults to
 *	the .keystore file in the directory specified by the
 *	<code>java.home</code> system property. </dd>
 *
 * <dt> <code>keyStoreType</code> </dt>
 * <dd> The key store type.  If not specified, defaults to the result of
 *      calling <code>KeyStore.getDefaultType()</code>. </dd>
 *
 * <dt> <code>keyStoreProvider</code> </dt>
 * <dd> The key store provider.  If not specified, uses the standard search
 *      order to find the provider. </dd>
 *
 * <dt> <code>keyStoreAlias</code> </dt>
 * <dd> The alias in the key store to login as.  Required when no callback
 *	handler is provided.  No default value. </dd>
 *
 * <dt> <code>keyStorePasswordURL</code> </dt>
 * <dd> A URL that specifies the location of the key store password.  Required
 *	when no callback handler is provided.  No default value. </dd>
 *
 * <dt> <code>privateKeyPasswordURL</code> </dt>
 * <dd> A URL that specifies the location of the specific private key password
 *	needed to access the private key for this alias.  
 *      The keystore password
 *	is used if this value is not specified. </dd>
 * </dl>
 */
public class KeyStoreLoginModule implements LoginModule {

   static final java.util.ResourceBundle rb =
        java.util.ResourceBundle.getBundle("sun.security.util.AuthResources");

    /* -- Fields -- */

    private static final int UNINITIALIZED = 0;
    private static final int INITIALIZED = 1;
    private static final int AUTHENTICATED = 2;
    private static final int LOGGED_IN = 3;

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;

    private char[] keyStorePassword;
    private char[] privateKeyPassword;

    private String keyStoreURL;
    private String keyStoreType;
    private String keyStoreProvider;
    private String keyStoreAlias;
    private String keyStorePasswordURL;
    private String privateKeyPasswordURL;
    private boolean debug;
    private javax.security.auth.x500.X500Principal principal;
    private Certificate[] fromKeyStore;
    private java.security.cert.CertPath certP = null;
    private X500PrivateCredential privateCredential;
    private int status = UNINITIALIZED;

    /* -- Methods -- */
  
    /**
     * Initialize this <code>LoginModule</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *			with the end user (prompting for usernames and
     *			passwords, for example). <p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *			<code>Configuration</code> for this particular
     *			<code>LoginModule</code>.
     */

    public void initialize(Subject subject,
			   CallbackHandler callbackHandler,
			   Map sharedState,
			   Map options)
    {
 	this.subject = subject;
	this.callbackHandler = callbackHandler;
	this.sharedState = sharedState;
	this.options = options;

	processOptions();
	status = INITIALIZED;
    }

    private void processOptions() {
	keyStoreURL = (String) options.get("keyStoreURL");
	if (keyStoreURL == null) {
	    keyStoreURL =
		"file:" +
		System.getProperty("user.home").replace(
		    File.separatorChar, '/') +
		'/' + ".keystore";
	}
	keyStoreType = (String) options.get("keyStoreType");
	if (keyStoreType == null) {
	    keyStoreType = KeyStore.getDefaultType();
	}

	keyStoreProvider = (String) options.get("keyStoreProvider");

	keyStoreAlias = (String) options.get("keyStoreAlias");

	keyStorePasswordURL = (String) options.get("keyStorePasswordURL");

	privateKeyPasswordURL = (String) options.get("privateKeyPasswordURL");

	debug = "true".equalsIgnoreCase((String) options.get("debug"));
	if (debug)
	    debugPrint("keyStoreURL=" + keyStoreURL +
		   " keyStoreAlias=" + keyStoreAlias +
		   " keyStorePasswordURL=" + keyStorePasswordURL +
		   " privateKeyPasswordURL=" + privateKeyPasswordURL);
	
    }
    
    /**
     * Authenticate the user .
     *
     * <p> Prompt the user for the Keystore alias and the password. Retrieve
     * the alias's principal and credentials from the Keystore.
     *
     * <p>
     *
     * @exception FailedLoginException if the authentication fails. <p>
     *
     * @return true in all cases (this <code>LoginModule</code>
     *		should not be ignored).
     */

    public boolean login() throws LoginException {
	switch (status) {
	case UNINITIALIZED:
	default:
	    throw new LoginException("The login module is not initialized");
	case INITIALIZED:
	case AUTHENTICATED:
	    getAliasAndPassword();
	    getKeyStoreInfo();
	    status = AUTHENTICATED;
	    return true;
	case LOGGED_IN:
	    return true;
	}
    }

    /** Get the alias and passwords to use for looking up in the KeyStore. */
    private void getAliasAndPassword() throws LoginException {
	if (callbackHandler == null) {

	    /*
	     * No callback handler.  Check for alias and password files
	     * specified in the options.
	     */
	    if (keyStoreAlias == null) {
		throw new LoginException(
		    "Need to specify an alias option to use " +
		    "KeyStoreLoginModule non-interactively.");
	    }
	    if (keyStorePasswordURL == null) {
		throw new LoginException(
		    "Need to specify passwordFile option to use " +
		    "KeyStoreLoginModule non-interactively.");
	    }
	    try {
		InputStream in = new URL(keyStorePasswordURL).openStream();
		keyStorePassword = readPassword(in);
		in.close();
	    } catch (IOException e) {
		throw new LoginException(
		    "Problem accessing keystore password \"" +
		    keyStorePasswordURL + "\": " + e);
	    }

	    if (privateKeyPasswordURL == null) {
		privateKeyPassword = keyStorePassword;
	    } else {
		try {
		    InputStream in =
			new URL(privateKeyPasswordURL).openStream();
		    privateKeyPassword = readPassword(in);
		    in.close();
		} catch (IOException e) {
		    throw new LoginException(
			"Problem accessing private key password \"" +
			privateKeyPasswordURL + "\": " + e);
		}
	    }


	} else {
	    TextOutputCallback bannerCallback =
		new TextOutputCallback(
		    TextOutputCallback.INFORMATION,
		    rb.getString("Please login to keystore"));

	    NameCallback aliasCallback;
	    if (keyStoreAlias == null || keyStoreAlias.length() == 0) {
		aliasCallback = new NameCallback(
				        rb.getString("Keystore alias: "));
	    } else {
		aliasCallback =
		    new NameCallback(rb.getString("Keystore alias: "), 
				     keyStoreAlias);
	    }
	    PasswordCallback keyStorePasswordCallback =
		new PasswordCallback(rb.getString("Keystore password: "),
						 false);

	    PasswordCallback privateKeyPasswordCallback =
		new PasswordCallback(
		    rb.getString("Private key password (optional): "), false);

	    ConfirmationCallback confirmationCallback =
		new ConfirmationCallback(
		    ConfirmationCallback.INFORMATION,
		    ConfirmationCallback.OK_CANCEL_OPTION,
		    ConfirmationCallback.OK);

	    try {
		callbackHandler.handle(
		    new Callback[] {
			bannerCallback, aliasCallback,
			keyStorePasswordCallback, privateKeyPasswordCallback,
		        confirmationCallback
		    });
	    } catch (IOException e) {
		throw new LoginException(
		    "Exception while getting keystore alias and password: " +
		    e);
	    } catch (UnsupportedCallbackException e) {
		throw new LoginException(
		    "Error: " + e.getCallback().toString() +
		    " is not available to retrieve authentication " +
		    " information from the user");
	    }

	    int confirmationResult = confirmationCallback.getSelectedIndex();

	    if (confirmationResult == ConfirmationCallback.CANCEL) {
		throw new LoginException("Login cancelled");
	    }

	    keyStoreAlias = aliasCallback.getName();

	    char[] tmpPassword = keyStorePasswordCallback.getPassword();
	    if (tmpPassword == null) {
		/* Treat a NULL password as an empty password */
		tmpPassword = new char[0];
	    }
	    keyStorePassword = new char[tmpPassword.length];
	    System.arraycopy(tmpPassword, 0,
			     keyStorePassword, 0, tmpPassword.length);
	    keyStorePasswordCallback.clearPassword();

	    tmpPassword = privateKeyPasswordCallback.getPassword();
	    if (tmpPassword == null
		|| tmpPassword.length == 0)
	    {
		/*
		 * Use keystore password if no private key password is
		 * specified.
		 */
		privateKeyPassword = keyStorePassword;
	    } else {
		privateKeyPassword = new char[tmpPassword.length];
		System.arraycopy(tmpPassword, 0,
				 privateKeyPassword, 0, tmpPassword.length);
		for (int i=0; i <tmpPassword.length ; i++)
		    tmpPassword[0] = ' ';
		tmpPassword=null;
		privateKeyPasswordCallback.clearPassword();
	    }
	    if (debug)
		debugPrint("alias=" + keyStoreAlias);
	}
    }

    /** Get the credentials from the KeyStore. */
    private void getKeyStoreInfo() throws LoginException {

	/* Get KeyStore instance */
	KeyStore keyStore;
	try {
	    if (keyStoreProvider == null) {
		keyStore = KeyStore.getInstance(keyStoreType);
	    } else {
		keyStore =
		    KeyStore.getInstance(keyStoreType, keyStoreProvider);
	    }
	} catch (KeyStoreException e) {
	    throw new LoginException(
		"The specified keystore type was not available: " + e);
	} catch (NoSuchProviderException e) {
	    throw new LoginException(
		"The specified keystore provider was not available: " + e);
	}

	/* Load KeyStore contents from file */
	try {
	    InputStream in = new URL(keyStoreURL).openStream();
	    keyStore.load(in, keyStorePassword);
	    in.close();
	} catch (MalformedURLException e) {
	    throw new LoginException("Incorrect keyStoreURL option: " + e);
	} catch (GeneralSecurityException e) {
	    throw new LoginException("Error initializing keystore: " + e);
	} catch (IOException e) {
	    throw new LoginException("Error initializing keystore: " + e);
	}

	/* Get certificate chain and create a certificate path */
	try {
	    fromKeyStore =
		keyStore.getCertificateChain(keyStoreAlias);
	    if (fromKeyStore == null
		|| fromKeyStore.length == 0
		|| !(fromKeyStore[0] instanceof X509Certificate))
	    {
		throw new FailedLoginException(
		    "Unable to find X.509 certificate chain in keystore");
	    } else {
		LinkedList certList = new LinkedList();
		for (int i=0; i < fromKeyStore.length; i++) {
		    certList.add(fromKeyStore[i]);
		}
		CertificateFactory certF= 
		    CertificateFactory.getInstance("X.509");
		certP = 
		    certF.generateCertPath(certList);	
	    }
	} catch (KeyStoreException e) {
	    throw new LoginException("Error using keystore: " + e);
	} catch (CertificateException ce) {
	    throw new LoginException("Error: X.509 Certificate type unavailable: " + ce);
	}

	/* Get principal and keys */
	try {
	    X509Certificate certificate = (X509Certificate)fromKeyStore[0];
	    principal = new javax.security.auth.x500.X500Principal
		(certificate.getSubjectDN().getName());
	    Key privateKey =
		keyStore.getKey(keyStoreAlias, privateKeyPassword);
	    if (privateKey == null
		|| !(privateKey instanceof PrivateKey))
	    {
		throw new FailedLoginException(
		    "Unable to recover key from keystore");
	    }

	    privateCredential = new X500PrivateCredential(
		certificate, (PrivateKey) privateKey, keyStoreAlias);
	} catch (KeyStoreException e) {
	    throw new LoginException("Error using keystore: " + e);
	} catch (NoSuchAlgorithmException e) {
	    throw new LoginException("Error using keystore: " + e);
	} catch (UnrecoverableKeyException e) {
	    throw new FailedLoginException(
					   "Unable to recover key from " +
					   "keystore: " + e);
	}
	if (debug) {
	    debugPrint("principal=" + principal +
		       "\n certificate="
		       + privateCredential.getCertificate() +
		       "\n alias =" + privateCredential.getAlias());
	}
    }

    /**
     * Abstract method to commit the authentication process (phase 2).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates a
     * <code>X500Principal</code> for the subject distinguished name of the 
     * first certificate in the alias's credentials in the subject's
     * principals,the alias's certificate path in the subject's public 
     * credentials, and a<code>X500PrivateCredential</code> whose certificate
     * is the first  certificate in the alias's certificate path and whose
     * private key is the alias's private key in the subject's private
     * credentials.  If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the commit fails
     *
     * @return true if this LoginModule's own login and commit
     *		attempts succeeded, or false otherwise.
     */

    public boolean commit() throws LoginException {
	switch (status) {
	case UNINITIALIZED:
	default:
	    throw new LoginException("The login module is not initialized");
	case INITIALIZED:
	    logoutInternal();
	    throw new LoginException("Authentication failed");
	case AUTHENTICATED:
	    if (commitInternal()) {
		return true;
	    } else {
		logoutInternal();
		throw new LoginException("Unable to retrieve certificates");
	    }
	case LOGGED_IN:
	    return true;
	}
    }

    private boolean commitInternal() throws LoginException {
	/* If the subject is not readonly add to the principal and credentials
	 * set; otherwise just return true
	 */
	if (subject.isReadOnly()) {
	    throw new LoginException ("Subject is set readonly");
	} else {
	    subject.getPrincipals().add(principal);
	    subject.getPublicCredentials().add(certP);
	    subject.getPrivateCredentials().add(privateCredential);
	    status = LOGGED_IN;
	    return true;
	}
    }

    /**
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
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
	switch (status) {
	case UNINITIALIZED:
	default:
	    return false;
	case INITIALIZED:
	    return false;
	case AUTHENTICATED:
	    logoutInternal();
	    return true;
	case LOGGED_IN:
	    logoutInternal();
	    return true;
	}
    }
    /**
     * Logout a user.
     *
     * <p> This method removes the Principals, public credentials and the
     * private credentials that were added by the <code>commit</code> method.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *		should not be ignored.
     */

    public boolean logout() throws LoginException {
	if (debug)
	    debugPrint("Entering logout " + status);
	switch (status) {
	case UNINITIALIZED:
	    throw new LoginException
		("The login module is not initialized");
	case INITIALIZED:
	case AUTHENTICATED:
	default:
	   // impossible for LoginModule to be in AUTHENTICATED 
	   // state
	   // assert status != AUTHENTICATED;
	    return false;
	case LOGGED_IN:
	    logoutInternal();
	    return true;
	}
    }

    private void logoutInternal() throws LoginException {
	if (debug)
	    debugPrint("Entering logoutInternal");
	Arrays.fill(keyStorePassword, '\0');
	keyStorePassword = null;
	Arrays.fill(privateKeyPassword, '\0');
	privateKeyPassword = null;
	if (subject.isReadOnly()) {
	    // attempt to destroy the private credential
	    // even if the Subject is read-only
	    principal = null;
	    certP = null;
	    status = INITIALIZED;
	    // destroy the private credential
	    Iterator it = subject.getPrivateCredentials().iterator();
	    while (it.hasNext()) {
		Object obj = it.next();
		if (privateCredential.equals(obj)) {
		    privateCredential = null;
		    try {
			((Destroyable)obj).destroy();
			if (debug)
			    debugPrint("Destroyed private credential, " +
				       obj.getClass().getName());
			break;
		    } catch (DestroyFailedException dfe) {
			throw new LoginException
			    ("Unable to destroy private credential, " 
			     + obj.getClass().getName()
			     + ": " + dfe.getMessage());
		    }
		}
	    }
	    
	    // throw an exception because we can not remove
	    // the principal and public credential from this
	    // read-only Subject
	    throw new LoginException
		("Unable to remove Principal (" 
		 + "X500Principal "
		 + ") and public credential (certificatepath) "
		 + "from read-only Subject");
	}
	if (principal != null) {
	    subject.getPrincipals().remove(principal);
	    principal = null;
	}
	if (certP != null) {
	    subject.getPublicCredentials().remove(certP);
	    certP = null;
	}
	if (privateCredential != null) {
	    subject.getPrivateCredentials().remove(privateCredential);
	    privateCredential = null;
	}
	status = INITIALIZED;
    }

    /** Reads user password from given input stream. */
    private char[] readPassword(InputStream in) throws IOException {
	char[] lineBuffer;
	char[] buf;
	int i;

	buf = lineBuffer = new char[128];

	int room = buf.length;
	int offset = 0;
	int c;

	boolean done = false;
	while (!done) {
	    switch (c = in.read()) {
	      case -1: 
	      case '\n':
		  done = true;
		  break;

	      case '\r':
		int c2 = in.read();
		if ((c2 != '\n') && (c2 != -1)) {
		    if (!(in instanceof PushbackInputStream)) {
			in = new PushbackInputStream(in);
		    }
		    ((PushbackInputStream)in).unread(c2);
		} else {
		    done = true;
		    break;
		}

	      default:
		if (--room < 0) {
		    buf = new char[offset + 128];
		    room = buf.length - offset - 1;
		    System.arraycopy(lineBuffer, 0, buf, 0, offset);
		    Arrays.fill(lineBuffer, ' ');
		    lineBuffer = buf;
		}
		buf[offset++] = (char) c;
		break;
	    }
	}

	if (offset == 0) {
	    return null;
	}

	char[] ret = new char[offset];
	System.arraycopy(buf, 0, ret, 0, offset);
	Arrays.fill(buf, ' ');

	return ret;
    }

    private void debugPrint(String message) {
	// we should switch to logging API
	    System.err.println("Debug KeyStoreLoginModule: " + message);
    }
}
