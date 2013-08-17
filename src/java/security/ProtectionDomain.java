/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

/** 
 *
 * <p> This ProtectionDomain class encapulates the characteristics of
 * a domain, which encloses a set of classes whose instances
 * are granted the same set of permissions.
 * 
 * <p>In addition to a set of permissions, a domain is comprised of a 
 * CodeSource, which is a set of PublicKeys together with a codebase (in 
 * the form of a URL). Thus, classes signed by the same keys and
 * from the same URL are placed in the same domain.
 * Classes that have the same permissions but are from different code
 * sources belong to different domains.
 *
 * <p> A class belongs to one and only one ProtectionDomain.
 * 
 * @version 	1.27, 02/06/02
 * @author Li Gong 
 * @author Roland Schemers
 */

public class ProtectionDomain {

    /* CodeSource */
    private CodeSource codesource ;

    /* the rights this protection domain is granted */
    private PermissionCollection permissions;

    /**
     * Creates a new ProtectionDomain with the given CodeSource and
     * Permissions. If the permissions object is not null, then
     * <code>setReadOnly()</code> will be called on the passed in 
     * Permissions object.
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
    }

    /**
     * Returns the CodeSource of this domain.
     * @return the CodeSource of this domain.
     */
    public final CodeSource getCodeSource() {
	return this.codesource;
    }


    /** 
     * Returns the permissions of this domain.
     * @return the permissions of this domain.
     */
    public final PermissionCollection getPermissions() {
	return this.permissions;
    }

    /**
     * Check and see if this ProtectionDomain implies the permissions 
     * expressed in the Permission object.
     *
     * @param permission the Permission object to check.
     *
     * @return true if "permission" is a proper subset of a permission in 
     * this ProtectionDomain, false if not.
     */

    public boolean implies(Permission permission) {
	if (permissions != null) {
	    return permissions.implies(permission);
	} else {
	    return false;
	}
    }

    /**
     * Convert a ProtectionDomain to a String.
     */
    public String toString() {
	return "ProtectionDomain "+codesource+"\n"+permissions+"\n";
    }
}
