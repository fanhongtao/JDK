/*
 * @(#)PrivateCredentialPermission.java	1.27 03/01/27
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.security.auth;

import java.util.*;
import java.text.MessageFormat;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import sun.security.util.ResourcesMgr;

/**
 * This class is used to protect access to private Credentials
 * belonging to a particular <code>Subject</code>.  The <code>Subject</code>
 * is represented by a Set of Principals.
 *
 * <p> The target name of this <code>Permission</code> specifies
 * a Credential class name, and a Set of Principals.
 * The only valid value for this Permission's actions is, "read".
 * The target name must abide by the following syntax:
 *
 * <pre>
 *	CredentialClass {PrincipalClass "PrincipalName"}*
 * </pre>
 *
 * For example, the following permission grants access to the
 * com.sun.PrivateCredential owned by Subjects which have
 * a com.sun.Principal with the name, "duke".  Note that although
 * this example, as well as all the examples below, do not contain
 * Codebase, SignedBy, or Principal information in the grant statement
 * (for simplicity reasons), actual policy configurations should
 * specify that information when appropriate.
 *
 * <pre>
 *
 *    grant {
 *	permission javax.security.auth.PrivateCredentialPermission
 *		"com.sun.PrivateCredential com.sun.Principal \"duke\"",
 *		"read";
 *    };
 * </pre>
 *
 * If CredentialClass is "*", then access is granted to
 * all private Credentials belonging to the specified
 * <code>Subject</code>.
 * If "PrincipalName" is "*", then access is granted to the
 * specified Credential owned by any <code>Subject</code> that has the
 * specified <code>Principal</code> (the actual PrincipalName doesn't matter).
 * For example, the following grants access to the
 * a.b.Credential owned by any <code>Subject</code> that has
 * an a.b.Principal.
 * 
 * <pre>
 *    grant {
 *	permission javax.security.auth.PrivateCredentialPermission
 *		"a.b.Credential a.b.Principal "*"",
 *		"read";
 *    };
 * </pre>
 *
 * If both the PrincipalClass and "PrincipalName" are "*",
 * then access is granted to the specified Credential owned by
 * any <code>Subject</code>.
 * 
 * <p> In addition, the PrincipalClass/PrincipalName pairing may be repeated:
 *
 * <pre>
 *    grant {
 *	permission javax.security.auth.PrivateCredentialPermission
 *		"a.b.Credential a.b.Principal "duke" c.d.Principal "dukette"",
 *		"read";
 *    };
 * </pre>
 *
 * The above grants access to the private Credential, "a.b.Credential",
 * belonging to a <code>Subject</code> with at least two associated Principals:
 * "a.b.Principal" with the name, "duke", and "c.d.Principal", with the name,
 * "dukette".
 *
 * @version 1.27, 01/27/03
 */
public final class PrivateCredentialPermission extends Permission {

    private static final long serialVersionUID = 5284372143517237068L;

    /**
     * @serial
     */
    private String credentialClass;
    /**
     * @serial The Principals associated with this permission.
     *		The set contains elements of type,
     *		<code>PrivateCredentialPermission.CredOwner</code>.
     */
    private Set principals;
    /**
     * @serial
     */
    private boolean testing = false;

    /**
     * Convenience function to create a PrivateCredentialPermission
     * from a Credential class String and a Set of Permissions.
     */
    static String buildTarget(String credentialClass, Set principals) {
	if (credentialClass == null ||
	    principals == null ||
	    principals.size() == 0)
	    throw new IllegalArgumentException
		(ResourcesMgr.getString("invalid null input(s)"));

	String name = credentialClass;

	Iterator i = principals.iterator();
	while (i.hasNext()) {
	    Principal p = (Principal)i.next();
	    name += " " + p.getClass().getName() + " \"" + p.getName() + "\"";
	}
	return name;
    }

    Set getPrincipalSet() {
	return principals;
    }

    /**
     * Create a new <code>PrivateCredentialPermission</code>
     * with the specified <code>credentialClass</code>
     * and an empty set of Principals.
     */
    PrivateCredentialPermission(String credentialClass, Set principals) {
	super(credentialClass);
	this.credentialClass = credentialClass;
	this.principals = principals;
    }

    /**
     * Creates a new <code>PrivateCredentialPermission</code>
     * with the specified <code>name</code>.  The <code>name</code>
     * specifies both a Credential class and a <code>Principal</code> Set.
     *
     * <p>
     *
     * @param name the name specifying the Credential class and
     *		<code>Principal</code> Set. <p>
     *
     * @param actions the actions specifying that the Credential can be read.
     *
     * @throws IllegalArgumentException if <code>name</code> does not conform
     *		to the correct syntax or if <code>actions</code> is not "read".
     */
    public PrivateCredentialPermission(String name, String actions) {
	super(name);

	if (!"read".equalsIgnoreCase(actions))
	    throw new IllegalArgumentException
		(ResourcesMgr.getString("actions can only be 'read'"));
	init(name);
    }

    /**
     * Returns the Class name of the Credential associated with this
     * <code>PrivateCredentialPermission</code>.
     *
     * <p>
     *
     * @return the Class name of the Credential associated with this
     *		<code>PrivateCredentialPermission</code>.
     */
    public String getCredentialClass() {
	return credentialClass;
    }

    /**
     * Returns the <code>Principal</code> classes and names
     * associated with this <code>PrivateCredentialPermission</code>.
     * The information is returned as a two-dimensional array (array[x][y]).
     * The 'x' value corresponds to the number of <code>Principal</code>
     * class and name pairs.  When (y==0), it corresponds to
     * the <code>Principal</code> class value, and when (y==1),
     * it corresponds to the <code>Principal</code> name value.
     * For example, array[0][0] corresponds to the class name of
     * the first <code>Principal</code> in the array.  array[0][1]
     * corresponds to the <code>Principal</code> name of the
     * first <code>Principal</code> in the array.
     *
     * <p>
     *
     * @return the <code>Principal</code> class and names associated
     *		with this <code>PrivateCredentialPermission</code>.
     */
    public String[][] getPrincipals() {

	if (principals == null) {
	    // this should never happen
	    return new String[0][0];
	}

	String[][] pArray = new String[principals.size()][2];
	Iterator pIterator = principals.iterator();

	int i = 0;
	while (pIterator.hasNext()) {
	    CredOwner co = (CredOwner)pIterator.next();
	    pArray[i][0] = co.principalClass;
	    pArray[i][1] = co.principalName;
	    i++;
	}
	return pArray;
    }

    /**
     * Checks if this <code>PrivateCredentialPermission</code> implies
     * the specified <code>Permission</code>.
     *
     * <p>
     *
     * This method returns true if:
     * <p><ul>
     * <li> <i>p</i> is an instanceof PrivateCredentialPermission and <p>
     * <li> the target name for <i>p</i> is implied by this object's
     *		target name.  For example:
     * <pre>
     *	[* P1 "duke"] implies [a.b.Credential P1 "duke"].
     *	[C1 P1 "duke"] implies [C1 P1 "duke" P2 "dukette"].
     *	[C1 P2 "dukette"] implies [C1 P1 "duke" P2 "dukette"].
     * </pre>
     * </ul>		
     *
     * <p>
     *
     * @param p the <code>Permission</code> to check against.
     *
     * @return true if this <code>PrivateCredentialPermission</code> implies
     * the specified <code>Permission</code>, false if not.
     */
    public boolean implies(Permission p) {

	if (p == null || !(p instanceof PrivateCredentialPermission))
	    return false;

	PrivateCredentialPermission that = (PrivateCredentialPermission)p;

	if (!impliesCredentialClass(credentialClass, that.getCredentialClass()))
	    return false;

	return impliesPrincipalSet(principals, that.getPrincipalSet());
    }

    /**
     * Checks two <code>PrivateCredentialPermission</code> objects for
     * equality.  Checks that <i>obj</i> is a
     * <code>PrivateCredentialPermission</code>,
     * and has the same credential class as this object,
     * as well as the same Principals as this object.
     * The order of the Principals in the respective Permission's
     * target names is not relevant.
     *
     * <p>
     *
     * @param obj the object we are testing for equality with this object.
     *
     * @return true if obj is a <code>PrivateCredentialPermission</code>,
     *		has the same credential class as this object,
     *		and has the same Principals as this object.
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (! (obj instanceof PrivateCredentialPermission))
            return false;

        PrivateCredentialPermission that = (PrivateCredentialPermission)obj;

	return (this.implies(that) && that.implies(this));
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
        return this.getCredentialClass().hashCode();
    }

    /**
     * Returns the "canonical string representation" of the actions.
     * This method always returns the String, "read".
     *
     * <p>
     *
     * @return the actions (always returns "read").
     */
    public String getActions() {
	return "read";
    }

    /**
     * Return a homogeneous collection of PrivateCredentialPermissions
     * in a <code>PermissionCollection</code>.
     * No such <code>PermissionCollection</code> is defined,
     * so this method always returns <code>null</code>.
     *
     * <p>
     *
     * @return null in all cases.
     */
    public PermissionCollection newPermissionCollection() {
	return null;
    }

    private void init(String name) {
	principals = new HashSet();
	StringTokenizer tokenizer = new StringTokenizer(name, " ", true);
	String principalClass = null;
	String principalName = null;

	if (testing)
	    System.out.println("whole name = " + name);

	// get the Credential Class
	credentialClass = tokenizer.nextToken();
	if (testing)
	    System.out.println("Credential Class = " + credentialClass);

	if (tokenizer.hasMoreTokens() == false) {
	    MessageFormat form = new MessageFormat(ResourcesMgr.getString
		("permission name [name] syntax invalid: "));
	    Object[] source = {name};
	    throw new IllegalArgumentException
		(form.format(source) + ResourcesMgr.getString
			("Credential Class not followed by a " +
			"Principal Class and Name"));
	}

	while (tokenizer.hasMoreTokens()) {

	    // skip delimiter
	    tokenizer.nextToken();

	    // get the Principal Class
	    principalClass = tokenizer.nextToken();
	    if (testing)
		System.out.println("    Principal Class = " + principalClass);

	    if (tokenizer.hasMoreTokens() == false) {
		MessageFormat form = new MessageFormat(ResourcesMgr.getString
			("permission name [name] syntax invalid: "));
		Object[] source = {name};
		throw new IllegalArgumentException
			(form.format(source) + ResourcesMgr.getString
			("Principal Class not followed by a Principal Name"));
	    }

	    // skip delimiter
	    tokenizer.nextToken();

	    // get the Principal Name
	    principalName = tokenizer.nextToken();

	    if (!principalName.startsWith("\"")) {
		MessageFormat form = new MessageFormat(ResourcesMgr.getString
			("permission name [name] syntax invalid: "));
		Object[] source = {name};
		throw new IllegalArgumentException
			(form.format(source) + ResourcesMgr.getString
			("Principal Name must be surrounded by quotes"));
	    }

	    if (!principalName.endsWith("\"")) {

		// we have a name with spaces in it --
		// keep parsing until we find the end quote,
		// and keep the spaces in the name

		while (tokenizer.hasMoreTokens()) {
		    principalName = principalName + tokenizer.nextToken();
		    if (principalName.endsWith("\""))
			break;
		}

		if (!principalName.endsWith("\"")) {
		    MessageFormat form = new MessageFormat
			(ResourcesMgr.getString
			("permission name [name] syntax invalid: "));
		    Object[] source = {name};
		    throw new IllegalArgumentException
			(form.format(source) + ResourcesMgr.getString
				("Principal Name missing end quote"));
		}
	    }

	    if (testing)
		System.out.println("\tprincipalName = '" + principalName + "'");

	    principalName = principalName.substring
					(1, principalName.length() - 1);

	    if (principalClass.equals("*") &&
		!principalName.equals("*")) {
		    throw new IllegalArgumentException(ResourcesMgr.getString
			("PrivateCredentialPermission Principal Class " +
			"can not be a wildcard (*) value if Principal Name " +
			"is not a wildcard (*) value"));
	    }

	    if (testing)
		System.out.println("\tprincipalName = '" + principalName + "'");

	    CredOwner co = new CredOwner(principalClass, principalName);
	    principals.add(co);
	}
    }

    private boolean impliesCredentialClass(String thisC, String thatC) {

	// this should never happen
	if (thisC == null || thatC == null)
	    return false;

	if (testing)
	    System.out.println("credential class comparison: " +
				thisC + "/" + thatC);

	if (thisC.equals("*"))
	    return true;

	/**
	 * XXX let's not enable this for now --
	 *	if people want it, we'll enable it later
	 */
	/*
	if (thisC.endsWith("*")) {
	    String cClass = thisC.substring(0, thisC.length() - 2);
	    return thatC.startsWith(cClass);
	}
	*/

	return thisC.equals(thatC);
    }

    private boolean impliesPrincipalSet(Set thisP, Set thatP) {

	// this should never happen
	if (thisP == null || thatP == null)
	    return false;

	if (testing) {
	    Iterator i = thisP.iterator();
	    for (int j = 0; j < thisP.size(); j++) {
		CredOwner co = (CredOwner)i.next();
		System.out.println("this permission set [" + j + "]= " +
				co.toString());
	    }
	}

	if (thatP.size() == 0)
	    return true;

	if (thisP.size() == 0)
	    return false;

	// make sure thatP "contains all" of the principals in thisP
	//
	// XXX	we can not simply call containsAll on the sets
	//	because we're not doing an "equals" --
	//	we're doing an "implies"
	Iterator thisI = thisP.iterator();
	while (thisI.hasNext()) {
	    CredOwner thisOwner = (CredOwner)thisI.next();
	    Iterator thatI = thatP.iterator();
	    boolean foundMatch = false;
	    while (thatI.hasNext()) {
		CredOwner thatOwner = (CredOwner)thatI.next();
		if (thisOwner.implies(thatOwner)) {
		    foundMatch = true;
		    break;
		}
	    }
	    if (!foundMatch)
		return false;
	}
	return true;
    }

    /**
     * Reads this object from a stream (i.e., deserializes it)
     */
    private void readObject(java.io.ObjectInputStream s) throws
					java.io.IOException,
					ClassNotFoundException {

        s.defaultReadObject();

        // perform new initialization from the permission name

        if (getName().indexOf(" ") == -1 && getName().indexOf("\"") == -1) {

	    // name only has a credential class specified
	    credentialClass = getName();
	    principals = new HashSet();

        } else {

	    // perform regular initialization
	    init(getName());
        }
    }

    /**
     * @serial include
     */
    static class CredOwner implements java.io.Serializable {

	private static final long serialVersionUID = -5607449830436408266L;

	/**
	 * @serial
	 */
	String principalClass;
	/**
	 * @serial
	 */
	String principalName;

	CredOwner(String principalClass, String principalName) {
	    this.principalClass = principalClass;
	    this.principalName = principalName;
	}

	public boolean implies(Object obj) {
	    if (obj == null || !(obj instanceof CredOwner))
		return false;

	    CredOwner that = (CredOwner)obj;

	    if (principalClass.equals("*") ||
		principalClass.equals(that.principalClass)) {

		if (principalName.equals("*") ||
		    principalName.equals(that.principalName)) {
		    return true;
		}
	    }

	    /**
	     * XXX no code yet to support a.b.*
	     */

	    return false;
	}

	public String toString() {
	    MessageFormat form = new MessageFormat(ResourcesMgr.getString
		("CredOwner:\n\tPrincipal Class = class\n\t" +
			"Principal Name = name"));
	    Object[] source = {principalClass, principalName};
	    return (form.format(source));
	}
    }
}
