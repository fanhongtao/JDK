/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.security;

import java.util.*;

/**
 * A UnresolvedPermissionCollection stores a collection
 * of UnresolvedPermission permissions.
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.UnresolvedPermission
 *
 * @version 1.10 02/02/06
 *
 * @author Roland Schemers
 *
 * @serial include
 */

final class UnresolvedPermissionCollection
extends PermissionCollection
implements java.io.Serializable
{

    private Hashtable permissions; // keyed on type

    /**
     * Create an empty BasicPermissions object.
     *
     */

    public UnresolvedPermissionCollection() {
	permissions = new Hashtable(11);
    }

    /**
     * Adds a permission to the BasicPermissions. The key for the hash is
     * permission.path.
     *
     * @param permission the Permission object to add.
     */

    public void add(Permission permission)
    {
	if (! (permission instanceof UnresolvedPermission))
	    throw new IllegalArgumentException("invalid permission: "+
					       permission);
	UnresolvedPermission up = (UnresolvedPermission) permission;

	synchronized(permissions) {
	    Vector v = (Vector) permissions.get(up.getName());
	    if (v == null) {
		v = new Vector();
		permissions.put(up.getName(), v);
	    }
	    v.addElement(up);
	}
    }

    /**
     * get any unresolved permissions of the same type as p,
     * and return the Vector containing them.
     */
    synchronized Vector getUnresolvedPermissions(Permission p) {
	return (Vector) permissions.get(p.getClass().getName());
    }

    /**
     * always returns false for unresolved permissions
     *
     */
    public boolean implies(Permission permission)
    {
	return false;
    }

    /**
     * Returns an enumeration of all the UnresolvedPermission vectors in the
     * container.
     *
     * @return an enumeration of all the UnresolvedPermission objects.
     */

    public synchronized Enumeration elements()
    {
	Vector perms = new Vector();

	Enumeration enum = permissions.elements();

	while (enum.hasMoreElements()) {
	    try {
		Vector urp = (Vector) enum.nextElement();
		Enumeration ue = urp.elements();
		while (ue.hasMoreElements()) {
		    perms.addElement(ue.nextElement());
		}
	    } catch (NoSuchElementException e){
		// ignore
	    }
	}
	return perms.elements();
    }
}
