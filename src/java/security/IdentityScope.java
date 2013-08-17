/*
 * @(#)IdentityScope.java	1.34 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

/** 
 * <p>This class represents a scope for identities. It is an Identity 
 * itself, and therefore has a name and can have a scope. It can also 
 * optionally have a public key and associated certificates.
 *
 * <p>An IdentityScope can contain Identity objects of all kinds, including
 * Signers. All types of Identity objects can be retrieved, added, and 
 * removed using the same methods. Note that it is possible, and in fact
 * expected, that different types of identity scopes will
 * apply different policies for their various operations on the
 * various types of Identities.
 *
 * <p>There is a one-to-one mapping between keys and identities, and 
 * there can only be one copy of one key per scope. For example, suppose
 * <b>Acme Software, Inc</b> is a software publisher known to a user.
 * Suppose it is an Identity, that is, it has a public key, and a set of
 * associated certificates. It is named in the scope using the name 
 * "Acme Software". No other named Identity in the scope has the same 
 * public  key. Of course, none has the same name as well.
 *
 * @see Identity
 * @see Signer
 * @see Principal
 * @see Key
 *
 * @version 	1.31, 01/27/97
 * @author Benjamin Renaud */
public abstract 
class IdentityScope extends Identity {

    /* The system's scope */
    private static IdentityScope scope;

    // initialize the system scope
    private static void initializeSystemScope() {

	String classname = Security.getProperty("system.scope");

	if (classname == null) {
	    return;

        } else {

	    try {
		Class.forName(classname);
	    } catch (ClassNotFoundException e) {
		Security.error("unable to establish a system scope from " +
			       classname);
		e.printStackTrace();
	    }
	}
    }

    /**
     * This constructor is used for serialization only and should not
     * be used by subclasses.
     */
    protected IdentityScope() {
	this("restoring...");
    }

    /**
     * Constructs a new identity scope with the specified name.
     *
     * @param name the scope name.
     */
    public IdentityScope(String name) {
	super(name);
    }

    /**
     * Constructs a new identity scope with the specified name and scope.
     * 
     * @param name the scope name.
     * @param scope the scope for the new identity scope.
     * 
     * @exception KeyManagementException if there is already an identity 
     * with the same name in the scope.
     */
    public IdentityScope(String name, IdentityScope scope) 
    throws KeyManagementException {
	super(name, scope);
    }

    /**
     * Returns the system's identity scope. See the
     * "System Identity Scope" section in the <a href=
     * "../guide/security/CryptoSpec.html#SysIdScope">
     * Java Cryptography Architecture API Specification &amp; Reference </a>.
     * 
     * @return the system's identity scope.
     */
    public static IdentityScope getSystemScope() {
	if (scope == null) {
	    initializeSystemScope();
	}
	return scope;
    }


    /**
     * Sets the system's identity scope. See the
     * "System Identity Scope" section in the <a href=
     * "../guide/security/CryptoSpec.html#SysIdScope">
     * Java Cryptography Architecture API Specification &amp; Reference </a>.
     *
     * @param scope the scope to set.
     */
    protected static void setSystemScope(IdentityScope scope) {
	staticCheck("set.system.scope");
	IdentityScope.scope = scope;
    }

    /**
     * Returns the number of identities within this identity scope.
     * 
     * @return the number of identities within this identity scope.
     */
    public abstract int size();

    /**
     * Returns the identity in this scope with the specified name (if any).
     * 
     * @param name the name of the identity to be retrieved.
     * 
     * @return the identity named <code>name</code>, or null if there are
     * no identities named <code>name</code> in this scope.
     */
    public abstract Identity getIdentity(String name);

    /**
     * Retrieves the identity whose name is the same as that of the 
     * specified principal. (Note: Identity implements Principal.)
     *
     * @param principal the principal corresponding to the identity
     * to be retrieved.
     * 
     * @return the identity whose name is the same as that of the 
     * principal, or null if there are no identities of the same name 
     * in this scope.
     */
    public Identity getIdentity(Principal principal) {
	return getIdentity(principal.getName());
    }

    /**
     * Retrieves the identity with the specified public key.
     *
     * @param key the public key for the identity to be returned.
     *
     * @return the identity with the given key, or null if there are
     * no identities in this scope with that key.
     */
    public abstract Identity getIdentity(PublicKey key);

    /**
     * Adds an identity to this identity scope.
     *
     * @param identity the identity to be added.
     *
     * @exception KeyManagementException if the identity is not
     * valid, a name conflict occurs, another identity has the same
     * public key as the identity being added, or another exception
     * occurs. */
    public abstract void addIdentity(Identity identity) 
    throws KeyManagementException;

    /**
     * Removes an identity from this identity scope.
     *
     * @param identity the identity to be removed.
     *
     * @exception KeyManagementException if the identity is missing,
     * or another exception occurs.
     */
    public abstract void removeIdentity(Identity identity) 
    throws KeyManagementException;

    /**
     * Returns an enumeration of all identities in this identity scope.
     * 
     * @return an enumeration of all identities in this identity scope.
     */
    public abstract Enumeration identities();

    /**
     * Returns a string representation of this identity scope, including
     * its name, its scope name, and the number of identities in this
     * identity scope.
     *
     * @return a string representation of this identity scope.
     */
    public String toString() {
	return super.toString() + "[" + size() + "]";
    }
}
