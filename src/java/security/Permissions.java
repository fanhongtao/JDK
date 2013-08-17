/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package java.security;

import java.util.Enumeration; 
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.io.Serializable;
import java.util.ArrayList;

/**  
 * This class represents a heterogeneous collection of Permissions. That is,
 * it contains different types of Permission objects, organized into
 * PermissionCollections. For example, if any <code>java.io.FilePermission</code>
 * objects are added to an instance of this class, they are all stored in a single
 * PermissionCollection. It is the PermissionCollection returned by a call to
 * the <code>newPermissionCollection</code> method in the FilePermission class.
 * Similarly, any <code>java.lang.RuntimePermission</code> objects are stored in 
 * the PermissionCollection returned by a call to the 
 * <code>newPermissionCollection</code> method in the
 * RuntimePermission class. Thus, this class represents a collection of
 * PermissionCollections.
 * 
 * <p>When the <code>add</code> method is called to add a Permission, the 
 * Permission is stored in the appropriate PermissionCollection. If no such 
 * collection exists yet, the Permission object's class is determined and the
 * <code>newPermissionCollection</code> method is called on that class to create
 * the PermissionCollection and add it to the Permissions object. If
 * <code>newPermissionCollection</code> returns null, then a default 
 * PermissionCollection that uses a hashtable will be created and used. Each 
 * hashtable entry stores a Permission object as both the key and the value.
 * 
 * @see Permission
 * @see PermissionCollection
 * @see AllPermission
 * 
 * @version 1.47, 02/02/06
 *
 * @author Marianne Mueller
 * @author Roland Schemers
 *
 * @serial exclude
 */

public final class Permissions extends PermissionCollection 
implements Serializable 
{

    private Hashtable perms;

    // optimization. keep track of the AllPermission collection
    private PermissionCollection allPermission;

    /**
     * Creates a new Permissions object containing no PermissionCollections.
     */
    public Permissions() {
	perms = new Hashtable(11);
	allPermission = null;
    }

    /**
     * Adds a permission object to the PermissionCollection for the class the
     * permission belongs to. For example, if <i>permission</i> is a FilePermission,
     * it is added to the FilePermissionCollection stored in this
     * Permissions object. 
     * 
     * This method creates
     * a new PermissionCollection object (and adds the permission to it)
     * if an appropriate collection does not yet exist. <p>
     *
     * @param permission the Permission object to add.
     * 
     * @exception SecurityException if this Permissions object is
     * marked as readonly.
     * 
     * @see PermissionCollection#isReadOnly()
     */

    public void add(Permission permission) {
	if (isReadOnly())
	    throw new SecurityException(
              "attempt to add a Permission to a readonly Permissions object");
	PermissionCollection pc = getPermissionCollection(permission);
	pc.add(permission);
	if (permission instanceof AllPermission) {
	    allPermission = pc;
	}
    }

    /**
     * Checks to see if this object's PermissionCollection for permissions of the
     * specified permission's type implies the permissions 
     * expressed in the <i>permission</i> object. Returns true if the combination
     * of permissions in the appropriate PermissionCollection (e.g., a
     * FilePermissionCollection for a FilePermission) together imply the
     * specified permission.
     * 
     * <p>For example, suppose there is a FilePermissionCollection in this Permissions
     * object, and it contains one FilePermission that specifies "read" access for
     * all files in all subdirectories of the "/tmp" directory, and another
     * FilePermission that specifies "write" access for all files in the
     * "/tmp/scratch/foo" directory. Then if the <code>implies</code> method
     * is called with a permission specifying both "read" and "write" access
     * to files in the "/tmp/scratch/foo" directory, <code>true</code> is returned.
     * <p>Additionally, if this PermissionCollection contains the
     * AllPermission, this method will always return true.
     * <p>
     * @param permission the Permission object to check.
     *
     * @return true if "permission" is implied by the permissions in the
     * PermissionCollection it
     * belongs to, false if not.
     */

    public boolean implies(Permission permission) {
	PermissionCollection pc = getPermissionCollection(permission);

	if (allPermission != null && allPermission.implies(permission))
	    return true;
	else 
	    return pc.implies(permission);
    }

    /**
     * Returns an enumeration of all the Permission objects in all the
     * PermissionCollections in this Permissions object.
     *
     * @return an enumeration of all the Permissions.
     */

    public Enumeration elements() {
	// go through each Permissions in the hash table 
	// and call their elements() function.
	return new PermissionsEnumerator(perms.elements());
    }

    /**
     * Returns an enumeration of all the Permission objects with the same
     * type as <i>p</i>.
     *
     * @param p the prototype Permission object.
     *
     * @return an enumeration of all the Permissions with the same type as <i>p</i>.
     */

    // XXX this could be public. Question is, do we want to make it public?
    // it is currently trivial to implement, but that might change...

    private Enumeration elements(Permission p) {
	PermissionCollection pc = getPermissionCollection(p);
	return pc.elements();
    }

    /** 
     * Gets the PermissionCollection in this Permissions object for
     * permissions whose type is the same as that of <i>p</i>.
     * For example, if <i>p</i> is a FilePermission, the FilePermissionCollection
     * stored in this Permissions object will be returned. 
     * 
     * This method creates a new PermissionCollection object for the specified 
     * type of permission objects if one does not yet exist. 
     * To do so, it first calls the <code>newPermissionCollection</code> method
     * on <i>p</i>.  Subclasses of class Permission 
     * override that method if they need to store their permissions in a particular
     * PermissionCollection object in order to provide the correct semantics
     * when the <code>PermissionCollection.implies</code> method is called.
     * If the call returns a PermissionCollection, that collection is stored
     * in this Permissions object. If the call returns null, then
     * this method instantiates and stores a default PermissionCollection 
     * that uses a hashtable to store its permission objects.
     */

    private PermissionCollection getPermissionCollection(Permission p) {
	Class c = p.getClass();
	PermissionCollection pc = (PermissionCollection) perms.get(c);
	if (pc == null) {
	    synchronized (perms) {
		// check again, in case someone else created one
		// between the time we checked and the time we
		// got the lock. We do this here to avoid 
		// making this whole method synchronized, because
		// it is called by every public method.
		pc = (PermissionCollection) perms.get(c);

		//check for unresolved permissions
		if (pc == null) {

		    pc = getUnresolvedPermissions(p);

		    // if still null, create a new collection
		    if (pc == null) {

			pc = p.newPermissionCollection();

			// still no PermissionCollection? 
			// We'll give them a PermissionsHash.
			if (pc == null)
			    pc = new PermissionsHash();
		    }
		}
		perms.put(c, pc);
	    }
	}
	return pc;
    }

    /**
     * Resolves any unresolved permissions of type p.
     *
     * @param p the type of unresolved permission to resolve
     *
     * @return PermissionCollection containing the unresolved permissions,
     *  or null if there were no unresolved permissions of type p.
     *
     */
    private PermissionCollection getUnresolvedPermissions(Permission p)
    {
	UnresolvedPermissionCollection uc = 
	(UnresolvedPermissionCollection) perms.get(UnresolvedPermission.class);

	// we have no unresolved permissions if uc is null
	if (uc == null) 
	    return null;

	java.util.Vector v = uc.getUnresolvedPermissions(p);
	
	// we have no unresolved permissions of this type if v is null
	if (v == null)
	    return null;

	java.security.cert.Certificate certs[] = null;

	Object signers[] = p.getClass().getSigners();

	int n = 0;
	if (signers != null) {
	    for (int j=0; j < signers.length; j++) {
		if (signers[j] instanceof java.security.cert.Certificate) {
		    n++;
		}
	    }
	    certs = new java.security.cert.Certificate[n];
	    n = 0;
	    for (int j=0; j < signers.length; j++) {
		if (signers[j] instanceof java.security.cert.Certificate) {
		    certs[n++] = (java.security.cert.Certificate)signers[j];
		}
	    }
	}

	PermissionCollection pc = null;
	Enumeration e = v.elements();

	while(e.hasMoreElements()) {
	    UnresolvedPermission up = (UnresolvedPermission) e.nextElement();
	    Permission perm = up.resolve(p, certs);
	    if (perm != null) {
		if (pc == null) {
		    pc = p.newPermissionCollection();
		    if (pc == null) 
			pc = new PermissionsHash();
		}
		pc.add(perm);
	    }

	}
	return pc;
    }
}

final class PermissionsEnumerator implements Enumeration {

    // all the perms
    private Enumeration perms;
    // the current set
    private Enumeration permset;
   
    PermissionsEnumerator(Enumeration e) {
	perms = e;
	permset = getNextEnumWithMore();
    }

    public synchronized boolean hasMoreElements() {
	// if we enter with permissionimpl null, we know
	// there are no more left.

	if (permset == null) 
	    return  false;

	// try to see if there are any left in the current one

	if (permset.hasMoreElements())
	    return true;

	// get the next one that has something in it...
	permset = getNextEnumWithMore();

	// if it is null, we are done!
	return (permset != null);
    }

    public synchronized Object nextElement() {

	// hasMoreElements will update permset to the next permset
	// with something in it...

	if (hasMoreElements()) {
	    return permset.nextElement();
	} else {
	    throw new NoSuchElementException("PermissionsEnumerator");
	}

    }

    private Enumeration getNextEnumWithMore() {
	while (perms.hasMoreElements()) {
	    PermissionCollection pc = (PermissionCollection) perms.nextElement();
	    Enumeration next = (Enumeration) pc.elements();
	    if (next.hasMoreElements())
		return next;
	}
	return null;
    }
}

/**
 * A PermissionsHash stores a homogeneous set of permissions in a hashtable.
 *
 * @see Permission
 * @see Permissions
 *
 * @version 1.47, 02/06/02
 *
 * @author Roland Schemers
 *
 * @serial include
 */

final class PermissionsHash extends PermissionCollection
implements Serializable
{

    private Hashtable perms;

    /**
     * Create an empty PermissionsHash object.
     */

    PermissionsHash() {
	perms = new Hashtable(11);
    }

    /**
     * Adds a permission to the PermissionsHash.
     *
     * @param permission the Permission object to add.
     */

    public void add(Permission permission)
    {
	perms.put(permission, permission);
    }

    /**
     * Check and see if this set of permissions implies the permissions 
     * expressed in "permission".
     *
     * @param permission the Permission object to compare
     *
     * @return true if "permission" is a proper subset of a permission in 
     * the set, false if not.
     */

    public boolean implies(Permission permission) 
    {
	// attempt a fast lookup and implies. If that fails
	// then enumerate through all the permissions.
	Permission p = (Permission) perms.get(permission);
	if ((p == null) || (!p.implies(permission))) {
	    Enumeration enum = elements();
	    try {
		while (enum.hasMoreElements()) {
		    p = (Permission) enum.nextElement();
		    if (p.implies(permission))
			return true;
		}
	    } catch (NoSuchElementException e){
		// ignore
	    }
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Returns an enumeration of all the Permission objects in the container.
     *
     * @return an enumeration of all the Permissions.
     */

    public Enumeration elements()
    {
	return perms.elements();
    }
}

