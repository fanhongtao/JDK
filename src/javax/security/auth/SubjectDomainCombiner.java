/*
 * @(#)SubjectDomainCombiner.java	1.40 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.AllPermission;
import java.security.Permission;
import java.security.Permissions;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.lang.ClassLoader;
import java.security.Security;
import java.util.Set;
import java.util.Iterator;

/**
 * A <code>SubjectDomainCombiner</code> updates ProtectionDomains
 * with Principals from the <code>Subject</code> associated with this
 * <code>SubjectDomainCombiner</code>.
 *
 * @version 1.40, 01/23/03 
 */
public class SubjectDomainCombiner implements java.security.DomainCombiner {

    private Subject subject;
    private java.util.Map cachedPDs = new java.util.WeakHashMap();
    private Set principalSet;
    private Principal[] principals;
    private static boolean checkedCacheProperty = false;
    private static boolean allowCaching = true;

    private static final sun.security.util.Debug debug =
	sun.security.util.Debug.getInstance("combiner",
					"\t[SubjectDomainCombiner]");

    /**
     * Associate the provided <code>Subject</code> with this
     * <code>SubjectDomainCombiner</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code> to be associated with
     *		with this <code>SubjectDomainCombiner</code>.
     */
    public SubjectDomainCombiner(Subject subject) {
	this.subject = subject;

	if (subject.isReadOnly()) {
	    principalSet = subject.getPrincipals();
	    principals = (Principal[])principalSet.toArray
			(new Principal[principalSet.size()]);
	}

	// see if we allow caching of the permissions
	if (!checkedCacheProperty) {
	    allowCaching = cachePolicy();
	    checkedCacheProperty = true;
	}
    }

    /**
     * Get the <code>Subject</code> associated with this
     * <code>SubjectDomainCombiner</code>.
     *
     * <p>
     *
     * @return the <code>Subject</code> associated with this
     *		<code>SubjectDomainCombiner</code>, or <code>null</code>
     *		if no <code>Subject</code> is associated with this
     *		<code>SubjectDomainCombiner</code>.
     *
     * @exception SecurityException if the caller does not have permission
     *		to get the <code>Subject</code> associated with this
     *		<code>SubjectDomainCombiner</code>.
     */
    public Subject getSubject() {
	java.lang.SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPermission(new AuthPermission
		("getSubjectFromDomainCombiner"));
	}
	return subject;
    }

    /**
     * Update the relevant ProtectionDomains with the Principals
     * from the <code>Subject</code> associated with this
     * <code>SubjectDomainCombiner</code>.
     *
     * <p> A new <code>ProtectionDomain</code> instance is created
     * for each <code>ProtectionDomain</code> in the
     * <i>currentDomains</i> array.  Each new <code>ProtectionDomain</code>
     * instance is created using the <code>CodeSource</code>,
     * <code>Permission</code>s and <code>ClassLoader</code>
     * from the corresponding <code>ProtectionDomain</code> in
     * <i>currentDomains</i>, as well as with the Principals from
     * the <code>Subject</code> associated with this
     * <code>SubjectDomainCombiner</code>.
     * 
     * <p> All of the newly instantiated ProtectionDomains are
     * combined into a new array.  The ProtectionDomains from the
     * <i>assignedDomains</i> array are appended to this new array,
     * and the result is returned.
     *
     * <p> Note that optimizations such as the removal of duplicate
     * ProtectionDomains may have occurred.
     * In addition, caching of ProtectionDomains may be permitted.
     *
     * <p>
     *
     * @param currentDomains the ProtectionDomains associated with the
     *		current execution Thread, up to the most recent
     *		privileged <code>ProtectionDomain</code>.
     *		The ProtectionDomains are are listed in order of execution,
     *		with the most recently executing <code>ProtectionDomain</code>
     *		residing at the beginning of the array. This parameter may
     *		be <code>null</code> if the current execution Thread
     *		has no associated ProtectionDomains.<p>
     *
     * @param assignedDomains the ProtectionDomains inherited from the
     *		parent Thread, or the ProtectionDomains from the
     *		privileged <i>context</i>, if a call to
     *		AccessController.doPrivileged(..., <i>context</i>)
     *		had occurred  This parameter may be <code>null</code>
     *		if there were no ProtectionDomains inherited from the
     *		parent Thread, or from the privileged <i>context</i>.
     *
     * @return a new array consisting of the updated ProtectionDomains,
     *		or <code>null</code>.
     */
    public ProtectionDomain[] combine(ProtectionDomain[] currentDomains,
				ProtectionDomain[] assignedDomains) {
	if (currentDomains == null || currentDomains.length == 0)
	    return optimize(assignedDomains, null);

	if (debug != null) {
	    if (subject == null) {
		debug.println("null subject");
	    } else {
		final Subject s = subject;
		AccessController.doPrivileged
		    (new java.security.PrivilegedAction() {
		    public Object run() {
			debug.println(s.toString());
			return null;
		    }
		});
	    }
	    printInputDomains(currentDomains, assignedDomains);
	}

	// optimize the inputs
	assignedDomains = optimize(assignedDomains, null);
	currentDomains = optimize(currentDomains, assignedDomains);
	if (debug != null) {
	    debug.println("after optimize");
	    printInputDomains(currentDomains, assignedDomains);
	}

	if (currentDomains == null && assignedDomains == null)
	    return null;

	// maintain backwards compatibility for people who provide
	// their own javax.security.auth.Policy implementations
	javax.security.auth.Policy javaxPolicy =
	    (javax.security.auth.Policy)AccessController.doPrivileged
	    (new PrivilegedAction() {
	    public Object run() {
		return javax.security.auth.Policy.getPolicy();
	    }
	});
	if (!(javaxPolicy instanceof com.sun.security.auth.PolicyFile)) {
	    if (debug != null) {
		debug.println("Providing backwards compatibility for " +
			"javax.security.auth.policy implementation: " +
			javaxPolicy.toString());
	    }
	    // use the javax.security.auth.Policy implementation
	    return combineJavaxPolicy(currentDomains, assignedDomains);
	}
	
	int cLen = (currentDomains == null ? 0 : currentDomains.length);
	int aLen = (assignedDomains == null ? 0 : assignedDomains.length);

	// the ProtectionDomains for the new AccessControlContext
	// that we will return
	ProtectionDomain[] newDomains = new ProtectionDomain[cLen + aLen];

	synchronized(cachedPDs) {
	    if (!subject.isReadOnly() &&
		!subject.getPrincipals().equals(principalSet)) {

		// if the Subject was mutated, clear the PD cache
		principalSet = new java.util.HashSet(subject.getPrincipals());
		principals = (Principal[])principalSet.toArray
			(new Principal[principalSet.size()]);
		cachedPDs.clear();

		if (debug != null) {
		    debug.println("Subject mutated - clearing cache");
		}
	    }
	    for (int i = 0; i < cLen; i++) {
		ProtectionDomain pd = currentDomains[i];
		ProtectionDomain subjectPd =
			(ProtectionDomain)cachedPDs.get(pd);
		if (subjectPd == null) {
		    subjectPd = new ProtectionDomain(pd.getCodeSource(),
						pd.getPermissions(), 
						pd.getClassLoader(),
						principals);
		    cachedPDs.put(pd, subjectPd);
		}
		newDomains[i] = subjectPd;
	    }
        }

	if (debug != null) {
	    debug.println("updated current: "); 
	    for (int i = 0; i < cLen; i++) {
		debug.println("\tupdated[" + i + "] = " +
				printDomain(newDomains[i]));
	    }
	}
		
	// now add on the assigned domains
	if (aLen > 0) {
	    System.arraycopy(assignedDomains, 0, newDomains, cLen, aLen);
	}

	// optimize the result
	newDomains = optimize(newDomains, null);
	
	if (debug != null) {
	    if (newDomains == null || newDomains.length == 0) {
		debug.println("returning null");
	    } else {
		debug.println("combinedDomains: ");
		for (int i = 0; i < newDomains.length; i++) {
		    debug.println("newDomain " + i + ": " +
				  printDomain(newDomains[i]));
		}
	    }
	}
	
	// return the new ProtectionDomains
	if (newDomains == null || newDomains.length == 0) {
	    return null;
	} else {
	    return newDomains;
	}
    }

    /**
     * Use the javax.security.auth.Policy implementation
     */
    ProtectionDomain[] combineJavaxPolicy(ProtectionDomain[] currentDomains,
				       ProtectionDomain[] assignedDomains) {
	java.security.AccessController.doPrivileged
	    (new PrivilegedAction() {
		public Object run() {
		    // Call refresh only caching is disallowed
		    if (!allowCaching)
			javax.security.auth.Policy.getPolicy().refresh();
		    return null;
		}
	    });
	
	int cLen = (currentDomains == null ? 0 : currentDomains.length);
	int aLen = (assignedDomains == null ? 0 : assignedDomains.length);

	// the ProtectionDomains for the new AccessControlContext
	// that we will return
	ProtectionDomain[] newDomains = new ProtectionDomain[cLen + aLen];
	int newDomainIndex = 0;

	synchronized(cachedPDs) {
	    if (!subject.isReadOnly() &&
		!subject.getPrincipals().equals(principalSet)) {

		// if the Subject was mutated, clear the PD cache
		principalSet = new java.util.HashSet(subject.getPrincipals());
		principals = (Principal[])principalSet.toArray
			(new Principal[principalSet.size()]);
		cachedPDs.clear();

		if (debug != null) {
		    debug.println("Subject mutated - clearing cache");
		}
	    }
	    for (int i = 0; i < cLen; i++) {
		ProtectionDomain pd = currentDomains[i];
		ProtectionDomain subjectPd =
			(ProtectionDomain)cachedPDs.get(pd);

		if (subjectPd == null) {

		    // XXX 
		    // we must first add the original permissions.
		    // that way when we later add the new JAAS permissions,
		    // any unresolved JAAS-related permissions will
		    // automatically get resolved.

		    // get the original perms
		    Permissions perms = new Permissions();
		    java.util.Enumeration e =
			currentDomains[i].getPermissions().elements();
		    while (e.hasMoreElements()) {
			Permission newPerm = (Permission)e.nextElement();
			perms.add(newPerm);
		    }

		    // get perms from the policy
		    PermissionCollection newPerms = null;

		    final java.security.CodeSource finalCs =
			currentDomains[i].getCodeSource();
		    final Subject finalS = subject;
		    newPerms = (PermissionCollection)
			java.security.AccessController.doPrivileged
			(new PrivilegedAction() {
			public Object run() {
			  return
			  javax.security.auth.Policy.getPolicy().getPermissions
				(finalS, finalCs);
			}
		    });
			
		    // add the newly granted perms,
		    // avoiding duplicates
		    e = newPerms.elements();
		    while (e.hasMoreElements()) {
			Permission newPerm = (Permission)e.nextElement();
			if (!perms.implies(newPerm)) {
			    perms.add(newPerm);
			    if (debug != null) 
				debug.println ("Adding perm " + newPerm + "\n");
			}
		    }
		    subjectPd = new ProtectionDomain(finalCs, perms);

		    if (allowCaching)
			cachedPDs.put(pd, subjectPd);
		}
		newDomains[i] = subjectPd;
	    }
	}

	if (debug != null) {
	    debug.println("updated current: ");
	    for (int i = 0; i < cLen; i++) {
		debug.println("\tupdated[" + i + "] = " + newDomains[i]);
	    }
	}

	// now add on the assigned domains
	if (aLen > 0) {
	    System.arraycopy(assignedDomains, 0, newDomains, cLen, aLen);
	}

	if (debug != null) {
	    if (newDomains == null || newDomains.length == 0) {
		debug.println("returning null");
	    } else {
		debug.println("combinedDomains: ");
		for (int i = 0; i < newDomains.length; i++) {
		    debug.println("newDomain " + i + ": " +
			newDomains[i].toString());
		}
	    }
	}

	// return the new ProtectionDomains
	if (newDomains == null || newDomains.length == 0) {
	    return null;
	} else {
	    return newDomains;
	}
    }
	
    /**
     * Remove System Domains and duplicate domains.
     *
     * Also remove domains from currentDomains if they already
     * exist in assignedDomains.  Optimization for bug 4308161.
     * In this case, 'domains' will be currentDomains,
     * and 'otherDomains' will be assignedDomains.
     */
    ProtectionDomain[] optimize(ProtectionDomain[] domains,
			ProtectionDomain[] otherDomains) {
	if (domains == null)
	    return null;

	ProtectionDomain[] optimized = new ProtectionDomain[domains.length];
	int num = 0;
	for (int i = 0; i < domains.length; i++) {

	    // skip System Domains
	    if (domains[i] == null)
		continue;

	    // skip domains with AllPermission 
	    // XXX
	    //
	    //	if (domains[i].implies(ALL_PERMISSION))
	    //	continue;

	    // remove duplicates
	    boolean foundIt = false;
	    for (int j = 0; j < num; j++) {
		if (optimized[j] == domains[i]) {
		    foundIt = true;
		    break;
		}
	    }
	    if (foundIt == false) {

		if (otherDomains == null) {

		    // keep the domain
		    optimized[num++] = domains[i];

		} else {

		    // remove domain if it exists in otherDomains
		    // in this case, domains == currentDomains,
		    // otherDomains == assignedDomains

		    boolean foundInOtherDomains = false;
		    for (int j = 0; j < otherDomains.length; j++) {
			if (otherDomains[j] == domains[i]) {
			    foundInOtherDomains = true;
			    break;
			}
		    }

		    if (foundInOtherDomains == false) {
			// keep the domain
			optimized[num++] = domains[i];
		    }
		}
	    }
	}

	// resize the array if necessary
	if (num < domains.length) {
	    ProtectionDomain[] downSize = new ProtectionDomain[num];
	    System.arraycopy(optimized, 0, downSize, 0, downSize.length);
	    optimized = downSize;
	}

	return (optimized.length == 0 ? null : optimized);
    }

    private boolean cachePolicy() {
	String s = (String)AccessController.doPrivileged
	    (new PrivilegedAction() {
	    public Object run() {
		return java.security.Security.getProperty
					("cache.auth.policy");
	    }
	});
	if (s != null) {
	    Boolean b = new Boolean(s);
	    return b.booleanValue();
	}

	// cache by default
	return true;
    }

    private void printInputDomains(ProtectionDomain[] currentDomains,
				ProtectionDomain[] assignedDomains) {
	if (currentDomains == null || currentDomains.length == 0) {
	    debug.println("currentDomains null or 0 length");
	} else {
	    for (int i = 0; currentDomains != null &&
			i < currentDomains.length; i++) {
		if (currentDomains[i] == null) {
		    debug.println("currentDomain " + i + ": SystemDomain");
		} else {
		    debug.println("currentDomain " + i + ": " +
				printDomain(currentDomains[i]));
		}
	    }
	}

	if (assignedDomains == null || assignedDomains.length == 0) {
	    debug.println("assignedDomains null or 0 length");
	} else {
	    debug.println("assignedDomains = ");
	    for (int i = 0; assignedDomains != null &&
			i < assignedDomains.length; i++) {
		if (assignedDomains[i] == null) {
		    debug.println("assignedDomain " + i + ": SystemDomain");
		} else {
		    debug.println("assignedDomain " + i + ": " +
				printDomain(assignedDomains[i]));
		}
	    }
	}
    }

    private String printDomain(final ProtectionDomain pd) {
	if (pd == null) {
	    return "null";
	}
	return (String)AccessController.doPrivileged(new PrivilegedAction() {
	    public Object run() {
		return pd.toString();
	    }
	});
    }
}
