/*
 * @(#)ProtectionDomain.java	1.41 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

import java.util.Enumeration;
import java.util.Vector;
import sun.security.util.SecurityConstants;

/** 
 *
 *<p>
 * This ProtectionDomain class encapsulates the characteristics of a domain,
 * which encloses a set of classes whose instances are granted a set 
 * of permissions when being executed on behalf of a given set of Principals.
 * <p>
 * A static set of permissions can be bound to a ProtectionDomain when it is
 * constructed; such permissions are granted to the domain regardless of the
 * Policy in force. However, to support dynamic security policies, a
 * ProtectionDomain can also be constructed such that it is dynamically
 * mapped to a set of permissions by the current Policy whenever a permission
 * is checked.
 * <p>
 * 
 * @version 	1.41, 01/23/03
 * @author Li Gong 
 * @author Roland Schemers
 * @author Gary Ellison
 */

public class ProtectionDomain {

    /* CodeSource */
    private CodeSource codesource ;

    /* ClassLoader the protection domain was consed from */
    private ClassLoader classloader;

    /* Principals running-as within this protection domain */
    private Principal[] principals;

    /* the rights this protection domain is granted */
    private PermissionCollection permissions;

    /* the PermissionCollection is static (pre 1.4 constructor)
       or dynamic (via a policy refresh) */
    private boolean staticPermissions;

    /**
     * Creates a new ProtectionDomain with the given CodeSource and
     * Permissions. If the permissions object is not null, then
     *  <code>setReadOnly())</code> will be called on the passed in
     * Permissions object. The only permissions granted to this domain
     * are the ones specified; the current Policy will not be consulted.
     *
     * @param codesource the codesource associated with this domain
     * @param permissions the permissions granted to this domain
     */
    public ProtectionDomain(CodeSource codesource,
			    PermissionCollection permissions) {
	this.codesource = codesource;
	if (permissions != null) {
	    this.permissions = permissions;
	    this.permissions.setReadOnly();
	}
	this.classloader = null;
	this.principals = new Principal[0];
	staticPermissions = true;
    }

    /**
     * Creates a new ProtectionDomain qualified by the given CodeSource,
     * Permissions, ClassLoader and array of Principals. If the
     * permissions object is not null, then <code>setReadOnly()</code>
     * will be called on the passed in Permissions object.
     * The permissions granted to this domain are dynamic; they include
     * both the static permissions passed to this constructor, and any
     * permissions granted to this domain by the current Policy at the
     * time a permission is checked.
     * <p>
     * This constructor is typically used by
     * {@link SecureClassLoader ClassLoaders}
     * and {@link DomainCombiner DomainCombiners} which delegate to 
     * <code>Policy</code> to actively associate the permissions granted to
     * this domain. This constructor affords the
     * Policy provider the opportunity to augment the supplied
     * PermissionCollection to reflect policy changes.
     * <p>
     *
     * @param codesource the CodeSource associated with this domain
     * @param permissions the permissions granted to this domain
     * @param classloader the ClassLoader associated with this domain
     * @param principals the array of Principals associated with this domain
     * @see Policy#refresh
     * @see Policy#getPermissions(ProtectionDomain)
     * @since 1.4
     */
    public ProtectionDomain(CodeSource codesource,
			    PermissionCollection permissions,
			    ClassLoader classloader,
			    Principal[] principals) {
	this.codesource = codesource;
	if (permissions != null) {
	    this.permissions = permissions;
	    this.permissions.setReadOnly();
	}
	this.classloader = classloader;
	this.principals = (principals != null ?
			   (Principal[])principals.clone():
			   new Principal[0]);
	staticPermissions = false;
    }

    /**
     * Returns the CodeSource of this domain.
     * @return the CodeSource of this domain which may be null.
     * @since 1.2
     */
    public final CodeSource getCodeSource() {
	return this.codesource;
    }


    /**
     * Returns the ClassLoader of this domain.
     * @return the ClassLoader of this domain which may be null.
     *
     * @since 1.4
     */
    public final ClassLoader getClassLoader() {
	return this.classloader;
    }


    /**
     * Returns an array of principals for this domain.
     * @return returns a non-null array of principals for this domain.
     * Changes to this array will have no impact on the ProtectionDomain.
     *
     * @since 1.4
     */
    public final Principal[] getPrincipals() {
	return (Principal[])this.principals.clone();
    }

    /** 
     * Returns the static permissions granted to this domain. 
     * 
     * @return the static set of permissions for this domain which may be null.
     * @see Policy#refresh
     * @see Policy#getPermissions(ProtectionDomain)
     */
    public final PermissionCollection getPermissions() {
	return permissions;
    }

    /**
     * Check and see if this ProtectionDomain implies the permissions 
     * expressed in the Permission object. 
     * <p>
     * The set of permissions evaluated is a function of whether the
     * ProtectionDomain was constructed with a static set of permissions
     * or it was bound to a dynamically mapped set of permissions.
     * <p>
     * If the ProtectionDomain was constructed to a 
     * {@link #ProtectionDomain(CodeSource, PermissionCollection)
     * statically bound} PermissionCollection then the permission will
     * only be checked against the PermissionCollection supplied at
     * construction.
     * <p>
     * However, if the ProtectionDomain was constructed with
     * the constructor variant which supports 
     * {@link #ProtectionDomain(CodeSource, PermissionCollection,
     * ClassLoader, java.security.Principal[]) dynamically binding}
     * permissions, then the permission will be checked against the
     * combination of the PermissionCollection supplied at construction and
     * the current Policy binding.
     * <p>
     *
     * @param permission the Permission object to check.
     *
     * @return true if "permission" is implicit to this ProtectionDomain.
     */
    public boolean implies(Permission permission) {
	if (!staticPermissions && 
	    Policy.getPolicyNoCheck().implies(this, permission))
	    return true;
	if (permissions != null) 
	    return permissions.implies(permission);

	return false;
    }

    /**
     * Convert a ProtectionDomain to a String.
     */
    public String toString() {
	String pals = "<no principals>";
	if (principals != null && principals.length > 0) {
	    StringBuffer palBuf = new StringBuffer("(principals ");
	    
	    for (int i = 0; i < principals.length; i++) {
		palBuf.append(principals[i].getClass().getName() +
			    " \"" + principals[i].getName() +
			    "\"");
		if (i < principals.length-1)
		    palBuf.append(",\n");
		else
		    palBuf.append(")\n");
	    }
	    pals = palBuf.toString();
	}

	// Check if policy is set; we don't want to load
	// the policy prematurely here	
	PermissionCollection pc = Policy.isSet() && seeAllp() ?
				      mergePermissions():
	                              getPermissions();

 	return "ProtectionDomain "+
 	    " "+codesource+"\n"+
 	    " "+classloader+"\n"+
 	    " "+pals+"\n"+
	    " "+pc+"\n"; 
    }

    private boolean seeAllp() {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    String debug =
		(String) java.security.AccessController.doPrivileged(
	     new sun.security.action.GetPropertyAction("java.security.debug"));
	    if (debug != null) {
		return true;
	    }
	    try {
		sm.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
	    } catch (SecurityException se) {
		return false;
	    }
	}

	return true;
    }

    private PermissionCollection mergePermissions() {
	if (staticPermissions)
	    return permissions;
	
        PermissionCollection perms = (PermissionCollection)
            java.security.AccessController.doPrivileged
            (new java.security.PrivilegedAction() {
                    public Object run() {
                        Policy p = Policy.getPolicyNoCheck();
                        return p.getPermissions(ProtectionDomain.this);
                    }
                });
	

	Permissions mergedPerms = new Permissions();
	int swag = 32;
	int vcap = 8;       
	Enumeration e;
	Vector pdVector = new Vector(vcap);
	Vector plVector = new Vector(swag);
            
	//
	// Build a vector of domain permissions for subsequent merge
	if (permissions != null) {
	    e = permissions.elements();
	    while (e.hasMoreElements()) {
		Permission p = (Permission)e.nextElement();
		pdVector.add(p);
	    }
	}

	//
	// Build a vector of Policy permissions for subsequent merge
	if (perms != null) {
	    e = perms.elements();
	    while (e.hasMoreElements()) {
		plVector.add(e.nextElement());
		vcap++;
	    }
	}

	if (perms != null && permissions != null) {
	    //
	    // Weed out the duplicates from the policy. Unless a refresh
	    // has occured since the pd was consed this should result in
	    // an empty vector.
	    e = permissions.elements();   // domain vs policy
	    while (e.hasMoreElements()) {
		Permission pdp = (Permission)e.nextElement();
		for (int i = 0; i < plVector.size(); i++) {
		    Permission pp = (Permission) plVector.elementAt(i);
		    if (pdp.getClass().isInstance(pp)) {
			// The equals() method on some permissions
			// have some side effects so this manual 
			// comparison is sufficient.
			if (pdp.getName().equals(pp.getName()) &&
			    pdp.getActions().equals(pp.getActions())) {
			    plVector.remove(i);
			    break;
			} 
		    }
		}
	    }
	}
                
	if (perms !=null) {
	    // the order of adding to merged perms and permissions
	    // needs to preserve the bugfix 4301064
                
	    for (int i = plVector.size()-1; i >= 0; i--) {
		mergedPerms.add((Permission)plVector.elementAt(i));
	    }
	}
	if (permissions != null) {
	    for (int i = pdVector.size()-1; i >= 0; i--) {
		mergedPerms.add((Permission)pdVector.elementAt(i));
	    }
	}

	return mergedPerms;
    }
    
}
