/*
 * @(#)NTLoginModule.java	1.6 02/02/26
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.security.auth.module;

import java.util.*;
import java.io.IOException;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
import com.sun.security.auth.NTUserPrincipal;
import com.sun.security.auth.NTDomainPrincipal;
import com.sun.security.auth.NTSidDomainPrincipal;
import com.sun.security.auth.NTSidUserPrincipal;
import com.sun.security.auth.NTSidGroupPrincipal;
import com.sun.security.auth.NTSidPrimaryGroupPrincipal;
import com.sun.security.auth.NTNumericCredential;

/**
 * <p> This <code>LoginModule</code>
 * renders a user's NT security information as some number of
 * <code>Principal</code>s
 * and associates them with a <code>Subject</code>.
 *
 * <p> This LoginModule recognizes the debug option.
 * If set to true in the login Configuration,
 * debug messages will be output to the output stream, System.out.
 *
 * @version 1.15, 01/11/00
 * @see javax.security.auth.spi.LoginModule
 */
public class NTLoginModule implements LoginModule {

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;

    // configurable option
    private boolean debug = false;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // NTPrincipal
    private NTUserPrincipal ntUserPrincipal;
    //NTDomainPrincipal
    private NTDomainPrincipal ntDomainPrincipal;
    //NTSidDomainPrincipal
    private NTSidDomainPrincipal ntSidDomainPrincipal;
    //NTSidPrincipal
    private NTSidUserPrincipal ntSidUserPrincipal;
    //NTSidGroupPrincipal
    private NTSidGroupPrincipal ntSidGroupPrincipals[];
    //NTSidPrimaryGroupPrincipal
    private NTSidPrimaryGroupPrincipal ntSidPrimaryGroupPrincipal;
    //NTNumericCredential
    private NTNumericCredential ntNumericCredential;
    // NTSystem
    private NTSystem ntSystem;

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *			with the end user (prompting for usernames and
     *			passwords, for example). This particular LoginModule only
     *          extracts the underlying NT system information, so this
     *          parameter is ignored.<p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *			<code>Configuration</code> for this particular
     *			<code>LoginModule</code>.
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
 
	this.subject = subject;
	this.callbackHandler = callbackHandler;
	this.sharedState = sharedState;
	this.options = options;

	// initialize any configured options
	debug = "true".equalsIgnoreCase((String)options.get("debug"));
    }

    /**
     * Import underlying NT system identity information.
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
        
	ntSystem = null;
	
	succeeded = false; // Indicate not yet successful
	
	ntSystem = new NTSystem();
	if (ntSystem == null) {
	    if (debug) {
		System.out.println("\t\t[NTLoginModule] " +
				   "Failed in NT login");
	    }
	    throw new FailedLoginException
		("Failed in attempt to import the " +
		 "underlying NT system identity information");
	}
	
	ntUserPrincipal = new NTUserPrincipal(ntSystem.getName());
	ntDomainPrincipal = new NTDomainPrincipal(ntSystem.getDomain());
	{
	    String temp = ntSystem.getDomainSID();
	    if (!temp.equals(new String(""))) {
		ntSidDomainPrincipal = new NTSidDomainPrincipal(temp);
		temp = null;
	    }
	}
	ntSidUserPrincipal = new NTSidUserPrincipal(ntSystem.getUserSID());
	String groups[] = ntSystem.getGroupIDs();
	ntSidGroupPrincipals = new NTSidGroupPrincipal[groups.length];
	for (int i = 0; i < groups.length; i++)
	    ntSidGroupPrincipals[i]
		= new NTSidGroupPrincipal(groups[i]);
	ntSidPrimaryGroupPrincipal = 
	    new NTSidPrimaryGroupPrincipal(ntSystem.getPrimaryGroupID());
	ntNumericCredential = 
	    new NTNumericCredential(ntSystem.getImpersonationToken());
	
	// authentication succeeded!!!
	if (debug) {
	    System.out.println("\t\t[NTLoginModule] " +
			       "succeeded importing info: ");
	    System.out.println("\t\t\tuserID = " + ntUserPrincipal.getName());
	    System.out.println("\t\t\tdomain = " + ntDomainPrincipal.getName());
	    if (ntSidDomainPrincipal == null) 
		System.out.println("\t\t\tdomainSID = null");
	    else
		System.out.println("\t\t\tdomainSID = "
				   + ntSidDomainPrincipal.getName());
	    System.out.println("\t\t\tuserSID = " 
			       + ntSidUserPrincipal.getName());
	    System.out.println("\t\t\tprimary group ID = " +
			       ntSidPrimaryGroupPrincipal.getName());
	    if (ntSidGroupPrincipals != null) {
		for (int i = 0; i < ntSidGroupPrincipals.length; i++) {
		    System.out.println("\t\t\tgroup ID = "
				       + ntSidGroupPrincipals[i].getName());
		}
	    }
	    System.out.println("\t\t\timpersonationToken = "
			       + Long.toString
			       (ntNumericCredential.getToken()));
	}
	succeeded = true;
	return succeeded;
    }
    
    /**
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates some
     * number of various <code>Principal</code>s
     * with the <code>Subject</code> located in the
     * <code>LoginModuleContext</code>.  If this LoginModule's own
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
	if (succeeded == false) {
	    if (debug) {
		System.out.println("\t\t[NTLoginModule]: " +
		    "did not add any Principals to Subject " +
		    "because own authentication failed.");
	    }
	    return false;
	} 
	if (subject.isReadOnly()) {
	    throw new LoginException ("Subject is ReadOnly");
	}
	Set principals = subject.getPrincipals();
	if (!principals.contains(ntUserPrincipal))
	    principals.add(ntUserPrincipal);
	if (!principals.contains(ntDomainPrincipal))
	    principals.add(ntDomainPrincipal);
	
	// don't always have a SidDomainPrincipal
	if (ntSidDomainPrincipal != null &&
		!principals.contains(ntSidDomainPrincipal)) {
	    principals.add(ntSidDomainPrincipal);
	}

	if (!principals.contains(ntSidUserPrincipal))
	    principals.add(ntSidUserPrincipal);
	if (!principals.contains(ntSidPrimaryGroupPrincipal))
	    principals.add(ntSidPrimaryGroupPrincipal);
	for (int i = 0; i < ntSidGroupPrincipals.length; i++) {
	    if (!principals.contains(ntSidGroupPrincipals[i]))
		principals.add(ntSidGroupPrincipals[i]);
	}
	if (!subject.getPublicCredentials().contains(ntNumericCredential)) {
	    subject.getPublicCredentials().add(ntNumericCredential);
	}
	
	if (debug) {
	    System.out.println("\t\t[NTLoginModule] " +
			       "added NTPrincipal to Subject");
	}
	
	commitSucceeded = true;
	return true;
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
	if (debug) {
	    System.out.println("\t\t[NTLoginModule]: " +
		"aborted authentication attempt");
	}

	if (succeeded == false) {
	    return false;
	} else if (succeeded == true && commitSucceeded == false) {
	    ntUserPrincipal = null;
	    ntDomainPrincipal = null;
	    ntSidUserPrincipal = null;
	    ntSidDomainPrincipal = null;
	    ntSidGroupPrincipals = null;
	    ntSidPrimaryGroupPrincipal = null;
	    ntNumericCredential = null;
	    ntSystem = null;
	    succeeded = false;
	} else {
	    // overall authentication succeeded and commit succeeded,
	    // but someone else's commit failed
	    logout();
	}
	return succeeded;
    }

    /**
     * Logout the user.
     *
     * <p> This method removes the <code>NTUserPrincipal</code>,
     * <code>NTDomainPrincipal</code>, <code>NTSidUserPrincipal</code>,
     * <code>NTSidDomainPrincipal</code>, <code>NTSidGroupPrincipal</code>s,
     * <code>NTSidPrimaryGroupPrincipal</code> and <code>NTNumericCredential</code>
     * that may have been added by the <code>commit</code> method.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *          should not be ignored.
     */
    public boolean logout() throws LoginException {

	if (subject.isReadOnly()) {
	    throw new LoginException ("Subject is ReadOnly");
	}
	Set principals = subject.getPrincipals();
	if (principals.contains(ntUserPrincipal))
	    principals.remove(ntUserPrincipal);
	if (principals.contains(ntDomainPrincipal))
	    principals.remove(ntDomainPrincipal);
	if (principals.contains(ntSidDomainPrincipal))
	    principals.remove(ntSidDomainPrincipal);
	if (principals.contains(ntSidUserPrincipal))
	    principals.remove(ntSidUserPrincipal);
	for (int i = 0; i < ntSidGroupPrincipals.length; i++) {
	    if (principals.contains(ntSidGroupPrincipals[i]))
		principals.remove(ntSidGroupPrincipals[i]);
	}
	if (principals.contains(ntSidPrimaryGroupPrincipal))
	    principals.remove(ntSidPrimaryGroupPrincipal);
	if (principals.contains(ntNumericCredential))
	    principals.remove(ntNumericCredential);
	
	succeeded = false;
	commitSucceeded = false;
	ntSystem.logoff(); // Clean up NT system resources
	ntUserPrincipal = null;
	ntDomainPrincipal = null;
	ntSidUserPrincipal = null;
	ntSidDomainPrincipal = null;
	ntSidGroupPrincipals = null;
	ntSidPrimaryGroupPrincipal = null;
	ntNumericCredential = null;
	ntSystem = null;
		
	if (debug) {
		System.out.println("\t\t[NTLoginModule] " +
				"completed logout processing");
	}
	return true;
    }
}
