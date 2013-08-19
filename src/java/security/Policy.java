/*
 * @(#)Policy.java	1.89 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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

import java.util.WeakHashMap;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;


/**
 * This is an abstract class for representing the system security
 * policy for a Java application environment (specifying
 * which permissions are available for code from various sources).
 * That is, the security policy is represented by a Policy subclass
 * providing an implementation of the abstract methods
 * in this Policy class.
 *
 * <p>There is only one Policy object in effect at any given time.
 *
 * <p>The source location for the policy information utilized by the
 * Policy object is up to the Policy implementation.
 * The policy configuration may be stored, for example, as a
 * flat ASCII file, as a serialized binary file of
 * the Policy class, or as a database.
 *
 * <p>The currently-installed Policy object can be obtained by
 * calling the <code>getPolicy</code> method, and it can be
 * changed by a call to the <code>setPolicy</code> method (by
 * code with permission to reset the Policy).
 *
 * <p>The <code>refresh</code> method causes the policy
 * object to refresh/reload its current configuration.
 *
 * <p>This is implementation-dependent. For example, if the policy
 * object stores its policy in configuration files, calling
 * <code>refresh</code> will cause it to re-read the configuration 
 * policy files. The refreshed policy may not have an effect on classes
 * in a particular ProtectionDomain. This is dependent on the Policy
 * provider's implementation of the 
 * {@link #implies(ProtectionDomain,Permission) implies}
 * method and the PermissionCollection caching strategy.
 *
 * <p>The default Policy implementation can be changed by setting the
 * value of the "policy.provider" security property (in the Java
 * security properties file) to the fully qualified name of
 * the desired Policy implementation class.
 * The Java security properties file is located in the file named
 * &lt;JAVA_HOME&gt;/lib/security/java.security, where &lt;JAVA_HOME&gt;
 * refers to the directory where the SDK was installed.
 *
 * @author Roland Schemers
 * @author Gary Ellison
 * @version 1.89, 01/23/03
 * @see java.security.CodeSource
 * @see java.security.PermissionCollection
 * @see java.security.SecureClassLoader
 */

public abstract class Policy {

    /** the system-wide policy. */
    private static Policy policy; // package private for AccessControlContext
    private static final Debug debug = Debug.getInstance("policy");

    // Cache mapping  ProtectionDomain to PermissionCollection
    private WeakHashMap pdMapping;

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
     * @see SecurityManager#checkPermission(Permission)
     * @see #setPolicy(java.security.Policy)
     */
    public static Policy getPolicy()
    {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
	    sm.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
	return getPolicyNoCheck();
    }

    /**
     * Returns the installed Policy object, skipping the security check.
     * Used by SecureClassLoader and getPolicy.
     *
     * @return the installed Policy.
     *
     */
    static synchronized Policy getPolicyNoCheck()
    {
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
		/*
		 * The policy_class seems to be an extension
		 * so we have to bootstrap loading it via a policy
		 * provider that is on the bootclasspath
		 * If it loads then shift gears to using the configured
		 * provider. 
		 */

		// install the bootstrap provider to avoid recursion
		policy = new sun.security.provider.PolicyFile();
			
		final String pc = policy_class;
		Policy p = (Policy)
		    AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
			    try {
				ClassLoader cl =
					ClassLoader.getSystemClassLoader();
				// we want the extension loader 
				ClassLoader extcl = null;
				while (cl != null) {
				    extcl = cl;
				    cl = cl.getParent();
				} 
				return (extcl != null? Class.forName
					(pc, true, extcl).newInstance():
					null);
			    } catch (Exception e) {
				return null;
			    }
			}
		    });
		/*
		 * if it loaded install it as the policy provider. Otherwise
		 * continue to use the system default implementation
		 */
		if (p != null) 
		    policy = p;
			
		if (p == null && debug != null) {
		    debug.println("policy provider " + 
				  policy_class + " not available;using " +
				  "sun.security.provider.PolicyFile");
		    e.printStackTrace();
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
     * @see SecurityManager#checkPermission(Permission)
     * @see #getPolicy()
     *
     */
    public static void setPolicy(Policy policy)
    {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(
				 new SecurityPermission("setPolicy"));
	if (policy != null) {
	    initPolicy(policy);
	}
	Policy.policy = policy;
    }

    /**
     * Initialize superclass state such that a legacy provider can
     * handle queries for itself.
     *
     * @since 1.4
     */
    private static void initPolicy (final Policy p) {
	/*
	 * A policy provider not on the bootclasspath could trigger
	 * security checks fulfilling a call to either Policy.implies
	 * or Policy.getPermissions. If this does occur the provider
	 * must be able to answer for it's own ProtectionDomain
	 * without triggering additional security checks, otherwise
	 * the policy implementation will end up in an infinite
	 * recursion.
	 * 
	 * To mitigate this, the provider can collect it's own
	 * ProtectionDomain and associate a PermissionCollection while
	 * it is being installed. The currently installed policy
	 * provider (if there is one) will handle calls to
	 * Policy.implies or Policy.getPermissions during this
	 * process.
	 * 
	 * This Policy superclass caches away the ProtectionDomain and
	 * statically binds permissions so that legacy Policy 
	 * implementations will continue to function.
	 */

	ProtectionDomain policyDomain = (ProtectionDomain)
		AccessController.doPrivileged(new PrivilegedAction() {
		    public Object run() {
			return p.getClass().getProtectionDomain();
		    }
		});

	/*
	 * Collect the permissions granted to this protection domain
	 * so that the provider can be security checked while processing
	 * calls to Policy.implies or Policy.getPermissions.
	 */
	PermissionCollection policyPerms = null;
	synchronized (p) {
	   if (p.pdMapping == null) {
		    p.pdMapping = new WeakHashMap();
	   }
	}

	if (policyDomain.getCodeSource() != null) {
	    if (Policy.isSet()) {
		    policyPerms = policy.getPermissions(policyDomain);
	    }

	    if (policyPerms == null) { // assume it has all
		policyPerms = new Permissions();
		policyPerms.add(SecurityConstants.ALL_PERMISSION);
	    }

	    synchronized (p) {
		    // cache of pd to permissions
		    p.pdMapping.put(policyDomain, policyPerms);
	    }
	}
	return;
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
     * according to the policy.The returned set of permissions must be 
     * a new mutable instance and it must support heterogeneous 
     * Permission types.
     *
     */
    public abstract PermissionCollection getPermissions(CodeSource codesource);

    /**
     * Evaluates the global policy and returns a
     * PermissionCollection object specifying the set of
     * permissions allowed given the characteristics of the 
     * protection domain.
     *
     * @param domain the ProtectionDomain associated with the caller.
     *
     * @return the set of permissions allowed for the <i>domain</i>
     * according to the policy.The returned set of permissions must be 
     * a new mutable instance and it must support heterogeneous 
     * Permission types.
     *
     * @see java.security.ProtectionDomain
     * @see java.security.SecureClassLoader
     * @since 1.4
     */
    public PermissionCollection getPermissions(ProtectionDomain domain) {
	PermissionCollection pc = null;

	if (domain == null)
	    return new Permissions();

	if (pdMapping == null) {
	    initPolicy(this);
	}

	synchronized (pdMapping) {
	    pc = (PermissionCollection)pdMapping.get(domain);
	}

	if (pc != null) {
	    Permissions perms = new Permissions();
	    for (Enumeration e = pc.elements() ; e.hasMoreElements() ;) {
		perms.add((Permission)e.nextElement());
	    }
	    return perms;
	}

	pc = getPermissions(domain.getCodeSource());
	if (pc == null) {
	    pc = new Permissions();
	}

	addStaticPerms(pc, domain.getPermissions());
	return pc;
    }

    /**
     * add static permissions to provided permission collection
     */
    private void addStaticPerms(PermissionCollection perms,
				PermissionCollection statics) {
	if (statics != null) {
	    Enumeration e = statics.elements();
	    while (e.hasMoreElements()) {
		perms.add((Permission)e.nextElement());
	    }
	}
    }

    /**
     * Evaluates the global policy for the permissions granted to
     * the ProtectionDomain and tests whether the permission is 
     * granted.
     *
     * @param domain the ProtectionDomain to test
     * @param permission the Permission object to be tested for implication.
     *
     * @return true if "permission" is a proper subset of a permission
     * granted to this ProtectionDomain.
     *
     * @see java.security.ProtectionDomain
     * @since 1.4
     */
    public boolean implies(ProtectionDomain domain, Permission permission) {
	PermissionCollection pc;
	WeakHashMap policyCache;

	if (pdMapping == null) {
	    initPolicy(this);
	}

	policyCache = pdMapping;

	synchronized (policyCache) {
	    pc = (PermissionCollection)policyCache.get(domain);
	}

	if (pc != null) {
	    return pc.implies(permission);
	} 
	
	pc = getPermissions(domain);
	if (pc == null) {
	    return false;
	}

	synchronized (policyCache) {
	    // cache it 
	    policyCache.put(domain, pc);
	}
	
	return pc.implies(permission);
    }

    /**
     * Refreshes/reloads the policy configuration. The behavior of this method
     * depends on the implementation. For example, calling <code>refresh</code>
     * on a file-based policy will cause the file to be re-read.
     *
     */
    public abstract void refresh();
}
