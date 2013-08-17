/*
 * @(#)DelegationPermission.java	1.4 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth.kerberos;

import java.util.*;
import java.security.Permission;
import java.security.BasicPermission;
import java.security.PermissionCollection;
import java.io.IOException;


/**
 * This class is used to restrict the usage of the Kerberos
 * delegation model, ie: forwardable and proxiable tickets.
 * <p>
 * The target name of this <code>Permission</code> specifies a pair of 
 * kerberos service principals. The first is the subordinate service principal 
 * being entrusted to use the TGT. The second service principal designates
 * the target service the subordinate service principal is to
 * interact with on behalf of the initiating KerberosPrincipal. This
 * latter service principal is specified to restrict the use of a
 * proxiable ticket.
 * <p>
 * For example, to specify the "host" service use of a forwardable TGT the
 * target permission is specified as follows:
 * <p> 
 * <pre>
 *  DelegationPermission("\"host/foo.example.com@EXAMPLE.COM\" \"krbtgt/EXAMPLE.COM@EXAMPLE.COM\"");
 * </pre>
 * <p>
 * To give the "backup" service a proxiable nfs service ticket the target permission
 * might be specified:
 * <p>
 * <pre>
 *  DelegationPermission("\"backup/bar.example.com@EXAMPLE.COM\" \"nfs/home.EXAMPLE.COM@EXAMPLE.COM\"");
 * </pre>
 *
 * @since JDK1.4
 */

public final class DelegationPermission extends BasicPermission 
    implements java.io.Serializable {

    private transient String subordinate, service;

    /**
     * Create a new <code>DelegationPermission</code>
     * with the specified subordinate and target principals.
     *
     * <p>
     *
     * @param principals the name of the subordinate and target principals
     */
    public DelegationPermission(String principals) {
	super(principals);
	init(principals);
    }

    /**
     * Create a new <code>DelegationPermission</code>
     * with the specified subordinate and target principals.
     * <p>
     *
     * @param principals the name of the subordinate and target principals 
     * <p>
     * @param actions should be null.
     */
    public DelegationPermission(String principals, String actions) {
	super(principals, actions);
	init(principals);
    }


    /**
     * Initialize the DelegationPermission object.
     */
    private void init(String target) {

	StringTokenizer t = null;
	if (!target.startsWith("\"")) {
	    throw new IllegalArgumentException
		("service principal [" + target +
		 "] syntax invalid: " +
		 "improperly quoted");
	} else {
	    t = new StringTokenizer(target, "\"", false);
	    subordinate = t.nextToken();
	    if (t.countTokens() == 2) {
		t.nextToken();	// bypass whitespace
		service = t.nextToken();
	    } else if (t.countTokens() > 0) {
		throw new IllegalArgumentException
		    ("service principal [" + t.nextToken() +
		     "] syntax invalid: " +
		     "improperly quoted");
	    }
	}
    }
	    
    /**
     * Checks if this Kerberos delegation permission object "implies" the 
     * specified permission.
     * <P>
     * If none of the above are true, <code>implies</code> returns false.
     * @param p the permission to check against.
     *
     * @return true if the specified permission is implied by this object,
     * false if not.  
     */
    public boolean implies(Permission p) {
	if (!(p instanceof DelegationPermission))
	    return false;

	DelegationPermission that = (DelegationPermission) p;
	if (this.subordinate.equals(that.subordinate) &&
	    this.service.equals(that.service))
	    return true;

	return false;
    }
    
    
    /**
     * Checks two DelegationPermission objects for equality. 
     * <P>
     * @param obj the object to test for equality with this object.
     * 
     * @return true if <i>obj</i> is a DelegationPermission, and
     *  has the same subordinate and service principal as this.
     *  DelegationPermission object.
     */
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;

	if (! (obj instanceof DelegationPermission))
	    return false;

	DelegationPermission that = (DelegationPermission) obj;
	return implies(that);
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return a hash code value for this object.
     */

    public int hashCode() {
	return getName().hashCode();
    }
    

    /**
     * Returns a PermissionCollection object for storing
     * DelegationPermission objects.
     * <br>
     * DelegationPermission objects must be stored in a manner that
     * allows them to be inserted into the collection in any order, but
     * that also enables the PermissionCollection implies method to
     * be implemented in an efficient (and consistent) manner.
     *
     * @return a new PermissionCollection object suitable for storing
     * DelegationPermissions.
     */

    public PermissionCollection newPermissionCollection() {
	return new KrbDelegationPermissionCollection();
    }

    /**
     * WriteObject is called to save the state of the DelegationPermission 
     * to a stream. The actions are serialized, and the superclass
     * takes care of the name.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	s.defaultWriteObject();
    }

    /**
     * readObject is called to restore the state of the
     * DelegationPermission from a stream.
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	// Read in the action, then initialize the rest
	s.defaultReadObject();
	init(getName());
    }

    /*
      public static void main(String args[]) throws Exception {
      DelegationPermission this_ =
      new DelegationPermission(args[0]);
      DelegationPermission that_ =
      new DelegationPermission(args[1]);
      System.out.println("-----\n");
      System.out.println("this.implies(that) = " + this_.implies(that_));
      System.out.println("-----\n");
      System.out.println("this = "+this_);
      System.out.println("-----\n");
      System.out.println("that = "+that_);
      System.out.println("-----\n");
      
      KrbDelegationPermissionCollection nps =
      new KrbDelegationPermissionCollection();
      nps.add(this_);
      nps.add(new DelegationPermission("\"host/foo.example.com@EXAMPLE.COM\" \"CN=Gary Ellison/OU=JSN/O=SUNW/L=Palo Alto/ST=CA/C=US\""));
      try {
      nps.add(new DelegationPermission("host/foo.example.com@EXAMPLE.COM \"CN=Gary Ellison/OU=JSN/O=SUNW/L=Palo Alto/ST=CA/C=US\""));
      } catch (Exception e) {
      System.err.println(e);
      }
      
      System.out.println("nps.implies(that) = " + nps.implies(that_));
      System.out.println("-----\n");
      
      Enumeration e = nps.elements();
      
      while (e.hasMoreElements()) {
      DelegationPermission x =
      (DelegationPermission) e.nextElement();
      System.out.println("nps.e = " + x);
      }
      }
    */    
}


final class KrbDelegationPermissionCollection extends PermissionCollection 
    implements java.io.Serializable {

    private Vector permissions;

    public KrbDelegationPermissionCollection() {
	permissions = new Vector();
    }

    
    /**
     * Check and see if this collection of permissions implies the permissions 
     * expressed in "permission".
     *
     * @param p the Permission object to compare
     *
     * @return true if "permission" is a proper subset of a permission in 
     * the collection, false if not.
     */

    public boolean implies(Permission permission) {
	if (! (permission instanceof DelegationPermission))
   		return false;

	DelegationPermission np = (DelegationPermission) permission;
	DelegationPermission x = null;
	Enumeration e = elements();
	while (e.hasMoreElements()) {
	    x = (DelegationPermission)e.nextElement();
	    if (x.implies(np))
		return true;
	}
	return false;

    }

    /**
     * Adds a permission to the DelegationPermissions. The key for
     * the hash is the name.
     *
     * @param permission the Permission object to add.
     *
     * @exception IllegalArgumentException - if the permission is not a
     *                                       DelegationPermission
     *
     * @exception SecurityException - if this PermissionCollection object
     *                                has been marked readonly
     */

    public void add(Permission permission) {
	if (! (permission instanceof DelegationPermission))
	    throw new IllegalArgumentException("invalid permission: "+
					       permission);
	if (isReadOnly())
	    throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");

	permissions.add(0, permission);
    }

    /**
     * Returns an enumeration of all the DelegationPermission objects
     * in the container.
     *
     * @return an enumeration of all the DelegationPermission objects.
     */

    public Enumeration elements() {
	return permissions.elements();
    }

}


