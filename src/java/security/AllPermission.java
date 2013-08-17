/*
 * @(#)AllPermission.java	1.5 98/12/03
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
 
package java.security;

import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * The AllPermission is a permission that implies all other permissions.
 * <p>
 * <b>Note:</b> Granting AllPermission should be done with extreme care,
 * as it implies all other permissions. Thus, it grants code the ability 
 * to run with security
 * disabled.  Extreme caution should be taken before granting such
 * a permission to code.  This permission should be used only during testing,
 * or in extremely rare cases where an application or applet is
 * completely trusted and adding the necessary permissions to the policy 
 * is prohibitively cumbersome.
 * 
 * @see java.security.Permission
 * @see java.security.AccessController
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 *
 * @version 1.5 98/12/03
 *
 * @author Roland Schemers
 */

public final class AllPermission extends Permission {

    /**
     * Creates a new AllPermission object.
     */

    public AllPermission()
    {
	super("<all permissions>");
    }


    /**
     * Creates a new AllPermission object. This
     * constructor exists for use by the <code>Policy</code> object
     * to instantiate new Permission objects.
     *
     * @param name ignored
     * @param actions ignored.
     */
    public AllPermission(String name, String actions) 
    {
	this();
    }

    /**
     * Checks if the specified permission is "implied" by 
     * this object. This method always returns true.
     *
     * @param p the permission to check against.
     *
     * @return return
     */
    public boolean implies(Permission p) {
	 return true;
    }

    /**
     * Checks two AllPermission objects for equality. Two AllPermission
     * objects are always equal.
     *
     * @param obj the object we are testing for equality with this object.
     * @return true if <i>obj</i> is an AllPermission, false otherwise.
     */
    public boolean equals(Object obj) {
	return (obj instanceof AllPermission);
    }

    /**
     * Returns the hash code value for this object.
     * 
     * @return a hash code value for this object.
     */

    public int hashCode() {
	return 1;
    }

    /**
     * Returns the canonical string representation of the actions.
     *
     * @return the actions.
     */
    public String getActions()
    {
	return "<all actions>";
    }

    /**
     * Returns a new PermissionCollection object for storing AllPermission 
     * objects.
     * <p>
     * 
     * @return a new PermissionCollection object suitable for 
     * storing AllPermissions.
     */

    public PermissionCollection newPermissionCollection() {
	return new AllPermissionCollection();
    }

}

/**
 * A AllPermissionCollection stores a collection
 * of AllPermission permissions. AllPermission objects
 * must be stored in a manner that allows them to be inserted in any
 * order, but enable the implies function to evaluate the implies
 * method in an efficient (and consistent) manner. 
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 *
 * @version 1.5 01/03/25
 *
 * @author Roland Schemers
 */

final class AllPermissionCollection
extends PermissionCollection 
implements java.io.Serializable 
{

    private boolean all_allowed; // true if any all permissions have been added

    /**
     * Create an empty AllPermissions object.
     *
     */

    public AllPermissionCollection() {
	all_allowed = false;
    }

    /**
     * Adds a permission to the AllPermissions. The key for the hash is
     * permission.path.
     *
     * @param permission the Permission object to add.
     */

    public void add(Permission permission)
    {
	if (! (permission instanceof AllPermission))
	    throw new IllegalArgumentException("invalid permission: "+
					       permission);
	all_allowed = true;
    }

    /**
     * Check and see if this set of permissions implies the permissions 
     * expressed in "permission".
     *
     * @param p the Permission object to compare
     *
     * @return always returns true.
     */

    public boolean implies(Permission permission) 
    {
	return all_allowed;
    }

    /**
     * Returns an enumeration of all the AllPermission objects in the 
     * container.
     *
     * @return an enumeration of all the AllPermission objects.
     */

    public Enumeration elements()
    {
	return new Enumeration() {
	    private boolean done = false;

	    public boolean hasMoreElements() {
		return !done;
	    }

	    public Object nextElement() {
		done = true;
		return new AllPermission();
	    }
	};
    }
}

