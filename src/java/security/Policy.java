/*
 * @(#)Policy.java	1.62 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 

package java.security;

import java.io.*;
import java.lang.RuntimePermission;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.PropertyPermission;

import java.lang.reflect.*;

/**
 * This is an abstract class for representing the system security
 * policy for a Java application environment (specifying 
 * which permissions are available for code from various sources).
 * That is, the security policy is represented by a Policy subclass
 * providing an implementation of the abstract methods
 * in this Policy class.
 * 
 * <p>There is only one Policy object in effect at any given time.
 * It is consulted by
 * a ProtectionDomain when the protection domain initializes its set of
 * permissions. <p>
 *
 * <p>The source location for the policy information utilized by the
 * Policy object is up to the Policy implementation.
 * The policy configuration may be stored, for example, as a
 * flat ASCII file, as a serialized binary file of
 * the Policy class, or as a database. <p>
 *
 * <p>The currently-installed Policy object can be obtained by 
 * calling the <code>getPolicy</code> method, and it can be 
 * changed by a call to the <code>setPolicy</code> method (by
 * code with permission to reset the Policy).
 * 
 * <p>The <code>refresh</code> method causes the policy
 * object to refresh/reload its current configuration. This is
 * implementation-dependent. For example, if the policy object stores
 * its policy in configuration files, calling <code>refresh</code> will
 * cause it to re-read the configuration policy files.
 * 
 * <p>When a protection domain needs to initialize its set of
 * permissions, it executes code such as the following
 * to ask the currently installed Policy object to populate a
 * PermissionCollection object with the appropriate permissions:
 * <pre>
 *   policy = Policy.getPolicy();
 *   PermissionCollection perms = policy.getPermissions(MyCodeSource)
 * </pre>
 * 
 * <p>The protection domain passes in a CodeSource
 * object, which encapsulates its codebase (URL) and public key attributes.
 * The Policy object evaluates the global policy and 
 * returns an appropriate Permissions object specifying 
 * the permissions allowed for code from the specified code source.
 *
 * <p>The default Policy implementation can be changed by setting the 
 * value of the "policy.provider" security property (in the Java 
 * security properties file) to the fully qualified name of
 * the desired Policy implementation class.
 * The Java security properties file is located in the file named
 * &lt;JAVA_HOME&gt;/lib/security/java.security, where &lt;JAVA_HOME&gt;
 * refers to the directory where the JDK was installed.
 * 
 * @author Roland Schemers
 * @version 1.59, 09/24/98
 * @see java.security.CodeSource
 * @see java.security.PermissionCollection
 */

public abstract class Policy {

    /** the system-wide policy. */
    private static Policy policy; // package private for AccessControlContext

    
    /** package private for AccessControlContext */
    static boolean isSet()
    {
	return policy != null;
    }

    /**
     * Returns the installed Policy object. This value should not be cached,
     * as it may be changed by a call to <code>setPolicy</code>. 
     * This method first calls
     * <code>SecurityManager.checkPermission</code> with a
     * <code>SecurityPermission("getPolicy")</code> permission
     * to ensure it's ok to get the Policy object..
     *
     * @return the installed Policy.
     *
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        getting the Policy object.
     *
     * @see SecurityManager#checkPermission
     */
    public static Policy getPolicy() 
    {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(new SecurityPermission
						("getPolicy"));
	return getPolicyNoCheck();
    }

    /**
     * Returns the installed Policy object, skipping the security check.
     * Used by ProtectionDomain and getPolicy.
     *
     * @return the installed Policy.
     *
     */
    static Policy getPolicyNoCheck() 
    {
	if (policy == null) {

	    synchronized(Policy.class) {

		if (policy == null) {
		    String policy_class = null;
		    policy_class = (String)AccessController.doPrivileged(
						       new PrivilegedAction() {
			public Object run() {
			    return Security.getProperty("policy.provider");
			}
		    });
		    if (policy_class == null) {
			policy_class = "sun.security.provider.PolicyFile";
		    }

		    try {
			policy = (Policy)
				Class.forName(policy_class).newInstance();
		    } catch (Exception e) {
			policy = new sun.security.provider.PolicyFile();
		    }
		}
	    }
	}

	return policy;
    }

    /**
     * Sets the system-wide Policy object. This method first calls 
     * <code>SecurityManager.checkPermission</code> with a
     * <code>SecurityPermission("setPolicy")</code>
     * permission to ensure it's ok to set the Policy.
     *
     * @param policy the new system Policy object.
     *
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow 
     *        setting the Policy.
     *
     * @see SecurityManager#checkPermission
     *
     */
    public static void setPolicy(Policy policy)
    {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(
				 new SecurityPermission("setPolicy"));
	Policy.policy = policy;
    }

    /**
     * Evaluates the global policy and returns a
     * PermissionCollection object specifying the set of
     * permissions allowed for code from the specified 
     * code source.
     *
     * @param codesource the CodeSource associated with the caller.
     * This encapsulates the original location of the code (where the code
     * came from) and the public key(s) of its signer.
     *
     * @return the set of permissions allowed for code from <i>codesource</i>
     * according to the policy.  
     *
     * @exception java.lang.SecurityException if the current thread does not 
     *            have permission to call <code>getPermissions</code> on the policy object.

     */
    public abstract PermissionCollection getPermissions(CodeSource codesource);

    /**
     * Refreshes/reloads the policy configuration. The behavior of this method
     * depends on the implementation. For example, calling <code>refresh</code> 
     * on a file-based policy will cause the file to be re-read.
      *
     * @exception java.lang.SecurityException if the current thread does not 
     *            have permission to refresh this Policy object.
     */
    public abstract void refresh();
}
